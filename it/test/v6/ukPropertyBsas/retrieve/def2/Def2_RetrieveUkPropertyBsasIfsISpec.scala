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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v6.ukPropertyBsas.retrieve.def2

import common.errors.*
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v6.common.model.IncomeSourceType
import v6.ukPropertyBsas.retrieve.def2.model.response.RetrieveUkPropertyBsasFixtures.*

class Def2_RetrieveUkPropertyBsasIfsISpec extends IntegrationBaseSpec {

  override def servicesConfig: Map[String, Any] =
    Map("feature-switch.ifs_hip_migration_1876.enabled" -> false) ++ super.servicesConfig

  "Calling the retrieve UK Property Bsas endpoint" should {
    "return a valid response with status OK" when {
      "valid request is made" in new TysIfsTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamRetrieveBsasResponseJson)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdRetrieveBsasResponseJson

      }

      "any valid Tax Year Specific request is made" in new TysIfsTest {
        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamRetrieveBsasResponseJson)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")

        response.json shouldBe mtdRetrieveBsasResponseJson
      }
    }

    "return error response with status BAD_REQUEST" when {
      "valid request is made but downstream response has invalid type of business" in new TysIfsTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(
            DownstreamStub.GET,
            downstreamUri,
            OK,
            downstreamRetrieveBsasResponseJsonInvalidIncomeSourceType(IncomeSourceType.`01`))
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe BAD_REQUEST
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe RuleTypeOfBusinessIncorrectError.asJson
      }
    }

    "return error according to spec" when {
      def validationErrorTest(requestNino: String,
                              requestCalculationId: String,
                              taxYearString: String,
                              expectedStatus: Int,
                              expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new TysIfsTest {

          override val nino: String          = requestNino
          override val calculationId: String = requestCalculationId

          override def taxYear: String = taxYearString

          override def setupStubs(): Unit = {}

          val response: WSResponse = await(request.get())
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = List(
        ("AA1123A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2023-24", BAD_REQUEST, NinoFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-beans", "2023-24", BAD_REQUEST, CalculationIdFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2023-2024", BAD_REQUEST, TaxYearFormatError)
      )
      input.foreach(validationErrorTest.tupled)
    }

    "downstream service error" when {

      def errorBody(code: String): String =
        s"""{
           |  "code": "$code",
           |  "reason": "error message"
           |}""".stripMargin

      def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"downstream returns an $downstreamCode error and status $downstreamStatus" in new TysIfsTest {

          override def setupStubs(): Unit = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onError(DownstreamStub.GET, downstreamUri, downstreamStatus, errorBody(downstreamCode))
          }

          val response: WSResponse = await(request.get())
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val errors = List(
        (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
        (BAD_REQUEST, "INVALID_RETURN", INTERNAL_SERVER_ERROR, InternalError),
        (UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", INTERNAL_SERVER_ERROR, InternalError),
        (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
      )
      val extraTysErrors = List(
        (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
        (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
        (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(serviceErrorTest.tupled)
    }
  }

  private trait Test {
    val nino          = "AA123456B"
    val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

    def downstreamUri: String

    def setupStubs(): Unit

    def request: WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.6.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def taxYear: String

    def uri: String = s"/$nino/uk-property/$calculationId/$taxYear"
  }

  private trait TysIfsTest extends Test {

    override def taxYear: String = "2025-26"

    def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/25-26/$nino/$calculationId"
  }

}
