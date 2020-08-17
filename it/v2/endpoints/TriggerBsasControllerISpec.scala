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
import play.api.libs.json.Json
import play.api.libs.ws.{ WSRequest, WSResponse }
import support.IntegrationBaseSpec
import v2.fixtures.TriggerBsasRequestBodyFixtures._
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.stubs.{ AuditStub, AuthStub, DesStub, MtdIdLookupStub }

class TriggerBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino             = "AA123456A"
    val selfEmploymentId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"
    val correlationId    = "X-123"

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/trigger"

    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino"

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

  val requestBody =
    Json.obj(
      "accountingPeriod" -> Json.obj("startDate" -> "2019-01-01", "endDate" -> "2019-10-31"),
      "typeOfBusiness"   -> TypeOfBusiness.`self-employment`.toString,
      "selfEmploymentId" -> "XAIS12345678901"
    )

  "Calling the triggerBsas" should {

    "return a 201 status code" when {

      "any valid request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.POST, desUrl, OK, Json.parse(desResponse))
        }

        val result: WSResponse = await(request().post(requestBody))
        result.status shouldBe CREATED
        result.json shouldBe Json.parse(hateoasResponseForSE(nino))
        result.header("Content-Type") shouldBe Some("application/json")
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

        val input = Seq(("AA1123A", BAD_REQUEST, NinoFormatError))

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "des service error" when {
        def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"des returns an $desCode error and status $desStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DesStub.onError(DesStub.POST, desUrl, desStatus, errorBody(desCode))
            }

            val response: WSResponse = await(request().post(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (FORBIDDEN, "ACCOUNTING_PERIOD_NOT_ENDED", FORBIDDEN, RuleAccountingPeriodNotEndedError),
          (FORBIDDEN, "OBLIGATIONS_NOT_MET", FORBIDDEN, RulePeriodicDataIncompleteError),
          (FORBIDDEN, "NO_ACCOUNTING_PERIOD", FORBIDDEN, RuleNoAccountingPeriodError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
