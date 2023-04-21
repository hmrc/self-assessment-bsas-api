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

package v3.endpoints

import api.models.errors._
import api.stubs.{ AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub }
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{ WSRequest, WSResponse }
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v3.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v3.models.domain.IncomeSourceType
import v3.models.errors._

class RetrieveUkPropertyBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino                    = "AA123456B"
    val calculationId           = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
    def taxYear: Option[String] = None

    def uri: String = s"/$nino/uk-property/$calculationId"

    def downstreamUri: String

    def setupStubs(): Unit

    def request: WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      buildRequest(uri)
        .withQueryStringParameters(taxYear.map(ty => Seq("taxYear" -> ty)).getOrElse(Nil): _*)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }
  }

  private trait NonTysTest extends Test {

    def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/$nino/$calculationId"
  }

  private trait TysIfsTest extends Test {

    override def taxYear: Option[String] = Some("2023-24")

    def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/23-24/$nino/$calculationId"
  }

  "Calling the retrieve UK Property Bsas endpoint" should {
    "return a valid response with status OK" when {
      "valid request is made and FHL is returned" in new NonTysTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamRetrieveBsasFhlResponseJson)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdRetrieveBsasReponseFhlJsonWithHateoas(nino, calculationId)
        response.header("Deprecation") shouldBe None
      }

      "valid request is made and Non-FHL is returned" in new NonTysTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamRetrieveBsasNonFhlResponseJson)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdRetrieveBsasReponseNonFhlJsonWithHateoas(nino, calculationId)
        response.header("Deprecation") shouldBe None
      }

      "any valid Tax Year Specific request is made and FHL is returned" in new TysIfsTest {
        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamRetrieveBsasFhlResponseJson)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdRetrieveBsasReponseFhlJsonWithHateoas(nino, calculationId, taxYear)
        response.header("Deprecation") shouldBe None
      }

      "any valid Tax Year Specific request is made and Non-FHL is returned" in new TysIfsTest {
        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamRetrieveBsasNonFhlResponseJson)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.header("Deprecation") shouldBe None
        response.json shouldBe mtdRetrieveBsasReponseNonFhlJsonWithHateoas(nino, calculationId, taxYear)
      }
    }

    "return error response with status BAD_REQUEST" when {
      "valid request is made but DES response has invalid type of business" in new NonTysTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET,
                                   downstreamUri,
                                   OK,
                                   downstreamRetrieveBsasResponseJsonInvalidIncomeSourceType(IncomeSourceType.`01`))
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe BAD_REQUEST
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe RuleTypeOfBusinessIncorrectError.asJson
      }
    }

    "return error according to spec" when {
      def validationErrorTest(requestNino: String,
                              requestCalculationId: String,
                              taxYearString: Option[String],
                              expectedStatus: Int,
                              expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new TysIfsTest {

          override val nino: String            = requestNino
          override val calculationId: String   = requestCalculationId
          override def taxYear: Option[String] = taxYearString

          override def setupStubs(): Unit = {}

          val response: WSResponse = await(request.get)
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        ("AA1123A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2023-24"), BAD_REQUEST, NinoFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-beans", Some("2023-24"), BAD_REQUEST, CalculationIdFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2023-2024"), BAD_REQUEST, TaxYearFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2019-20"), BAD_REQUEST, InvalidTaxYearParameterError)
      )
      input.foreach(args => (validationErrorTest _).tupled(args))
    }

    "des service error" when {

      def errorBody(code: String): String =
        s"""{
             |  "code": "$code",
             |  "reason": "des message"
             |}""".stripMargin

      def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"des returns an $desCode error and status $desStatus" in new NonTysTest {

          override def setupStubs(): Unit = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onError(DownstreamStub.GET, downstreamUri, desStatus, errorBody(desCode))
          }

          val response: WSResponse = await(request.get)
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val errors = Seq(
        (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
        (BAD_REQUEST, "INVALID_RETURN", INTERNAL_SERVER_ERROR, InternalError),
        (UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", INTERNAL_SERVER_ERROR, InternalError),
        (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
      )
      val extraTysErrors = Seq(
        (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
        (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
        (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
    }
  }
}
