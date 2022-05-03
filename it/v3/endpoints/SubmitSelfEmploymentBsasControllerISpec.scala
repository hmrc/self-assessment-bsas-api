/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v3.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v3.models.errors._
import v3.stubs._

class SubmitSelfEmploymentBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino          = "AA123456A"
    val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

    val nrsSuccess: JsValue = Json.parse(
      s"""
         |{
         |  "nrSubmissionId":"2dd537bc-4244-4ebf-bac9-96321be13cdc",
         |  "cadesTSignature":"30820b4f06092a864886f70111111111c0445c464",
         |  "timestamp":""
         |}
         """.stripMargin
    )

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/self-employment/$calculationId/adjust"

    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$calculationId"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
      )
    }

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "des message"
         |      }
    """.stripMargin
  }

  import v3.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._

  val requestBody: JsValue = mtdRequest

  "Calling the Submit Adjustments endpoint for self-employment" should {

    "return a 200 status code" when {

      "any valid request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          NrsStub.onSuccess(NrsStub.PUT, s"/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", ACCEPTED, nrsSuccess)
          DownstreamStub.onSuccess(DownstreamStub.PUT, desUrl, OK)
        }

        val result: WSResponse = await(request().post(requestBody))
        result.status shouldBe OK
        result.json shouldBe Json.parse(hateoasResponse(nino, calculationId))
        result.header("Content-Type") shouldBe Some("application/json")
      }

      "a valid request is made with a failed nrs call" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          NrsStub.onError(NrsStub.PUT, s"/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", INTERNAL_SERVER_ERROR, "An internal server error occurred")
          DownstreamStub.onSuccess(DownstreamStub.PUT, desUrl, OK)
        }

        val result: WSResponse = await(request().post(requestBody))
        result.status shouldBe OK
        result.json shouldBe Json.parse(hateoasResponse(nino, calculationId))
        result.header("Content-Type") shouldBe Some("application/json")
      }
    }

    "return error according to spec" when {

      "validation error" when {
        def validationErrorTest(requestNino: String, expectedStatus: Int, expectedBody: MtdError, requestBodyJson: JsValue): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String = requestNino

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().post(requestBodyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          ("AA1234A", BAD_REQUEST, NinoFormatError, requestBody),
          ("AA123456A", BAD_REQUEST, RuleBothExpensesError.copy(paths = Some(Seq("/expenses"))), mtdRequestWithBothExpenses)
        )
        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "des service error" when {
        def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"des returns an $desCode error and status $desStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.PUT, desUrl, desStatus, errorBody(desCode))
            }

            val response: WSResponse = await(request().post(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
          (UNPROCESSABLE_ENTITY, "ASC_ID_INVALID", FORBIDDEN, RuleSummaryStatusInvalid),
          (CONFLICT, "ASC_ALREADY_SUPERSEDED", FORBIDDEN, RuleSummaryStatusSuperseded),
          (CONFLICT, "ASC_ALREADY_ADJUSTED", FORBIDDEN, RuleAlreadyAdjusted),
          (UNPROCESSABLE_ENTITY, "UNALLOWABLE_VALUE", FORBIDDEN, RuleResultingValueNotPermitted),
          (FORBIDDEN, "BVR_FAILURE_C55316", FORBIDDEN, RuleOverConsolidatedExpensesThreshold),
          (FORBIDDEN, "BVR_FAILURE_C15320", FORBIDDEN, RuleTradingIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C55503", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C55508", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C55509", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C559107", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C559103", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C559099", INTERNAL_SERVER_ERROR, DownstreamError),
          (UNPROCESSABLE_ENTITY, "INCOMESOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, DownstreamError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
        )
        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}