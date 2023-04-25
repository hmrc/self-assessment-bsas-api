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

package v2.endpoints

import api.models.errors._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{ JsValue, Json }
import play.api.libs.ws.{ WSRequest, WSResponse }
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v2.models.errors._
import v2.stubs._

class SubmitUkPropertyBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino   = "AA123456A"
    val bsasId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

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

    def uri: String = s"/$nino/property/$bsasId/adjust"

    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$bsasId"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
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

  import v2.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._

  val requestBody: JsValue = validfhlInputJson

  "Calling the Submit Adjustments to your UK Property Summary endpoint" should {
    "return a 200 status code" when {
      "any valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          NrsStub.onSuccess(NrsStub.PUT, s"/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", ACCEPTED, nrsSuccess)
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(fhlDesResponse(bsasId, "04")))
        }

        val result: WSResponse = await(request().post(requestBody))
        result.status shouldBe OK
        result.json shouldBe Json.parse(hateoasResponse(nino, bsasId))
        result.header("Content-Type") shouldBe Some("application/json")
        result.header("Deprecation") shouldBe Some(
          "This endpoint is deprecated. See the service guide: https://developer.service.hmrc.gov.uk/guides/income-tax-mtd-end-to-end-service-guide/")
      }

      "a valid request is made with a failed nrs call" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          NrsStub.onError(NrsStub.PUT, s"/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", INTERNAL_SERVER_ERROR, "An internal server error occurred")
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(fhlDesResponse(bsasId, "04")))
        }

        val result: WSResponse = await(request().post(requestBody))
        result.status shouldBe OK
        result.json shouldBe Json.parse(hateoasResponse(nino, bsasId))
        result.header("Content-Type") shouldBe Some("application/json")
        result.header("Deprecation") shouldBe Some(
          "This endpoint is deprecated. See the service guide: https://developer.service.hmrc.gov.uk/guides/income-tax-mtd-end-to-end-service-guide/")
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String = requestNino

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().post(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          ("AA1234A", BAD_REQUEST, NinoFormatError)
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
              DesStub.onError(DesStub.PUT, desUrl, desStatus, errorBody(desCode))
            }

            val response: WSResponse = await(request().post(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, BsasIdFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "ASC_ID_INVALID", FORBIDDEN, RuleSummaryStatusInvalid),
          (CONFLICT, "ASC_ALREADY_SUPERSEDED", FORBIDDEN, RuleSummaryStatusSuperseded),
          (CONFLICT, "ASC_ALREADY_ADJUSTED", FORBIDDEN, RuleBsasAlreadyAdjusted),
          (UNPROCESSABLE_ENTITY, "UNALLOWABLE_VALUE", FORBIDDEN, RuleResultingValueNotPermitted),
          (UNPROCESSABLE_ENTITY, "INCOMESOURCE_TYPE_NOT_MATCHED", FORBIDDEN, RuleTypeOfBusinessError),
          (FORBIDDEN, "BVR_FAILURE_C55316", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C15320", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55503", FORBIDDEN, RuleOverConsolidatedExpensesThreshold),
          (FORBIDDEN, "BVR_FAILURE_C55509", FORBIDDEN, RulePropertyIncomeAllowanceClaimed),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )
        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
