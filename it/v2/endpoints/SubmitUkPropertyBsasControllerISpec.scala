/*
 * Copyright 2020 HM Revenue & Customs
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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v2.models.errors._
import v2.stubs.{AuditStub, AuthStub, DesStub, MtdIdLookupStub}

class SubmitUkPropertyBsasControllerISpec extends IntegrationBaseSpec {


  private trait Test {

    val nino             = "AA123456A"
    val bsasId           = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"
    val correlationId    = "X-123"

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/property/$bsasId/adjust"

    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$bsasId"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.2.0+json"))
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
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(fhlDesResponse(bsasId, "04")))
        }

        val result: WSResponse = await(request().post(requestBody))
        result.status shouldBe OK
        result.json shouldBe Json.parse(hateoasResponse(nino, bsasId))
        result.header("Content-Type") shouldBe Some("application/json")
      }
    }

    "return error according to spec" when {

      "request made is for the invalid type of business `SELF-EMPLOYMENT`" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(fhlDesResponse(bsasId, "01")))
        }

        val result: WSResponse = await(request().post(requestBody))
        result.status shouldBe FORBIDDEN
        result.json shouldBe Json.toJson(RuleSelfEmploymentAdjustedError)
      }

      "request made is for the invalid type of business `uk-property-non-fhl` where fhl is expected" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(fhlDesResponse(bsasId, "02")))
        }

        val result: WSResponse = await(request().post(requestBody))
        result.status shouldBe FORBIDDEN
        result.json shouldBe Json.toJson(RuleIncorrectPropertyAdjusted)
      }

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

        val input = Seq(("AA1234A", BAD_REQUEST, NinoFormatError))

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
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "INVALID_PAYLOAD_REMOTE", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "INVALID_FIELD", FORBIDDEN, RuleTypeOfBusinessError),
          (BAD_REQUEST, "INVALID_MONETARY_FORMAT", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "ASC_ID_INVALID", FORBIDDEN, RuleSummaryStatusInvalid),
          (FORBIDDEN, "ASC_ALREADY_SUPERSEDED", FORBIDDEN, RuleSummaryStatusSuperseded),
          (FORBIDDEN, "ASC_ALREADY_ADJUSTED", FORBIDDEN, RuleBsasAlreadyAdjusted),
          (FORBIDDEN, "UNALLOWABLE_VALUE", FORBIDDEN, RuleResultingValueNotPermitted),
          (FORBIDDEN, "BVR_FAILURE_C55316", FORBIDDEN, RuleTypeOfBusinessError),
          (FORBIDDEN, "BVR_FAILURE_C15320", FORBIDDEN, RuleTypeOfBusinessError),
          (FORBIDDEN, "BVR_FAILURE_C55503", FORBIDDEN, RuleOverConsolidatedExpensesThreshold),
          (FORBIDDEN, "BVR_FAILURE_C55509", FORBIDDEN, RulePropertyIncomeAllowanceClaimed),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
