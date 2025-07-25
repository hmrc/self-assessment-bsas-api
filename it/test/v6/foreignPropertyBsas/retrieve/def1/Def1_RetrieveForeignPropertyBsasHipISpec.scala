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

package v6.foreignPropertyBsas.retrieve.def1

import common.errors.RuleTypeOfBusinessIncorrectError
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v6.foreignPropertyBsas.retrieve.def1.model.response.RetrieveForeignPropertyBsasBodyFixtures.{
  retrieveForeignPropertyBsasDesFhlJson,
  retrieveForeignPropertyBsasDesJson,
  retrieveForeignPropertyBsasMtdFhlJson,
  retrieveForeignPropertyBsasMtdJson
}
import v6.selfEmploymentBsas.retrieve.def1.model.Def1_RetrieveSelfEmploymentBsasFixtures
import v6.ukPropertyBsas.retrieve.def1.model.response.RetrieveUkPropertyBsasFixtures

class Def1_RetrieveForeignPropertyBsasHipISpec extends IntegrationBaseSpec {

  "Calling the retrieve Foreign Property Bsas endpoint" should {
    "return a valid response with status OK" when {
      "valid request is made and Non-fhl is returned" in new NonTysTest {
        DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, retrieveForeignPropertyBsasDesJson(2020))

        val response: WSResponse = await(request.get())

        response.json shouldBe retrieveForeignPropertyBsasMtdJson(taxYear)
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")

      }

      "valid request is made and fhl is returned" in new NonTysTest {
        DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, retrieveForeignPropertyBsasDesFhlJson(2020))

        val response: WSResponse = await(request.get())

        response.json shouldBe retrieveForeignPropertyBsasMtdFhlJson(taxYear)
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")

      }

      "valid request is made for a Tax Year Specific (TYS) tax year" in new TysTest {
        DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, retrieveForeignPropertyBsasDesJson())

        val response: WSResponse = await(request.get())

        response.json shouldBe retrieveForeignPropertyBsasMtdJson(taxYear)
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")

      }
    }

    "return error response with status BAD_REQUEST" when {
      "Downstream response is UK property" in {
        checkTypeOfBusinessIncorrectWith(RetrieveUkPropertyBsasFixtures.downstreamRetrieveBsasFhlResponseJson())
      }

      "Downstream response is self employment" in {
        checkTypeOfBusinessIncorrectWith(Def1_RetrieveSelfEmploymentBsasFixtures.downstreamRetrieveBsasResponseJson())
      }

      def checkTypeOfBusinessIncorrectWith(downstreamResponse: JsValue): Unit =
        new NonTysTest {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, downstreamResponse)

          val response: WSResponse = await(request.get())

          response.json shouldBe RuleTypeOfBusinessIncorrectError.asJson
          response.status shouldBe BAD_REQUEST
          response.header("Content-Type") shouldBe Some("application/json")
        }
    }

    "return error according to spec" when {
      def validationErrorTest(requestNino: String, requestBsasId: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit =
        s"validation fails with ${expectedBody.code} error" in new TysTest {
          override val nino: String          = requestNino
          override val calculationId: String = requestBsasId
          override def taxYear: String       = requestTaxYear
          val response: WSResponse           = await(request.get())
          response.json shouldBe Json.toJson(expectedBody)
          response.status shouldBe expectedStatus
          response.header("Content-Type") shouldBe Some("application/json")
        }

      val input = List(
        ("BAD_NINO", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2023-24", BAD_REQUEST, NinoFormatError),
        ("AA123456A", "bad_calc_id", "2023-24", BAD_REQUEST, CalculationIdFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2023-2024", BAD_REQUEST, TaxYearFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2023-25", BAD_REQUEST, RuleTaxYearRangeInvalidError)
      )
      input.foreach(validationErrorTest.tupled)
    }

    "downstream service error" when {
      def errorBody(code: String): String =
        s"""{
           |  "code": "$code",
           |  "reason": "message"
           |}""".stripMargin

      def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit =
        s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {
          DownstreamStub.onError(DownstreamStub.GET, downstreamUrl, downstreamStatus, errorBody(downstreamCode))

          val response: WSResponse = await(request.get())

          response.json shouldBe Json.toJson(expectedBody)
          response.status shouldBe expectedStatus
          response.header("Content-Type") shouldBe Some("application/json")
        }

      val errors = List(
        (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
        (BAD_REQUEST, "INVALID_RETURN", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
        (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
        (UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", INTERNAL_SERVER_ERROR, InternalError),
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

    def taxYear: String
    def downstreamUrl: String

    def request: WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      buildRequest(s"/$nino/foreign-property/$calculationId/$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.6.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

  }

  private trait NonTysTest extends Test {
    def taxYear: String       = "2019-20"
    def downstreamUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$calculationId"

  }

  private trait TysTest extends Test {
    def taxYear: String       = "2023-24"
    def downstreamUrl: String = s"/itsa/income-tax/v1/23-24/adjustable-summary-calculation/$nino/$calculationId"

  }

}
