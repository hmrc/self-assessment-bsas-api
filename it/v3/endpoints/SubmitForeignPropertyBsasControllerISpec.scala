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

import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{ JsObject, JsValue, Json }
import play.api.libs.ws.{ WSRequest, WSResponse }
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v3.models.errors._
import v3.stubs._
import v3.fixtures.foreignProperty.SubmitForeignPropertyBsasFixtures._

class SubmitForeignPropertyBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino: String            = "AA123456A"
    val calculationId: String   = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"
    def taxYear: Option[String] = None

    // Downstream returns the adjustments and adjusted calculation - we ignore whatever we get back...
    val ignoredDownstreamResponse: JsValue = Json.parse("""{"ignored": "doesn't matter"}""")

    val nrsSuccess: JsValue = Json.parse(
      s"""
         |{
         |  "nrSubmissionId":"2dd537bc-4244-4ebf-bac9-96321be13cdc",
         |  "cadesTSignature":"30820b4f06092a864886f70111111111c0445c464",
         |  "timestamp":""
         |}
         """.stripMargin
    )

    val responseBody: JsValue = Json.parse(s"""
         |{
         |  "links":[
         |    {
         |      "href":"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$calculationId",
         |      "rel":"self",
         |      "method":"GET"
         |    }
         |  ]
         |}
         |""".stripMargin)

    def setupStubs(): Unit = ()

    def downstreamUrl: String

    def stubDownstreamSuccess(): Unit = {
      DownstreamStub
        .when(DownstreamStub.PUT, downstreamUrl)
        .withRequestBody(downstreamRequestValid)
        .thenReturn(OK, ignoredDownstreamResponse)
    }

    def stubNrsSuccess(): Unit = {
      NrsStub.onSuccess(NrsStub.PUT, s"/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", ACCEPTED, nrsSuccess)
    }

    def request(): WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      buildRequest(s"/$nino/foreign-property/$calculationId/adjust")
        .withQueryStringParameters(taxYear.map(ty => Seq("taxYear" -> ty)).getOrElse(Nil): _*)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "message"
         |      }
    """.stripMargin
  }

  private trait NonTysTest extends Test {
    def downstreamUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$calculationId"
  }

  private trait TysIfsTest extends Test {
    override def taxYear: Option[String] = Some("2023-24")

    def downstreamUrl: String = s"/income-tax/adjustable-summary-calculation/23-24/$nino/$calculationId"
  }

  "Calling the submit foreign property bsas endpoint" should {

    "return a 200 status code" when {

      "any valid foreignProperty request is made" in new NonTysTest {
        override def setupStubs(): Unit = {
          stubNrsSuccess()
          stubDownstreamSuccess()
        }

        val response: WSResponse = await(request().post(mtdRequestValid))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId") should not be empty
      }

      "a valid request is made for TYS" in new TysIfsTest {
        override def setupStubs(): Unit = {
          stubNrsSuccess()
          stubDownstreamSuccess()
        }

        val response: WSResponse = await(request().post(mtdRequestValid))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId") should not be empty
      }

      "any valid foreignProperty request is made despite a failed nrs call" in new NonTysTest {
        override def setupStubs(): Unit = {
          NrsStub.onError(NrsStub.PUT, s"/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", INTERNAL_SERVER_ERROR, "An internal server error occurred")
          stubDownstreamSuccess()
        }

        val response: WSResponse = await(request().post(mtdRequestValid))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId") should not be empty
      }
    }

    "return error according to spec" when {

      def requestBodyWithCountryCode(code: String): JsValue = Json.parse(s"""
           |{
           |  "nonFurnishedHolidayLet": [
           |    {
           |      "countryCode": "$code",
           |      "income": {
           |        "totalRentsReceived": 123.12
           |      }
           |    }
           |  ]
           |}
           |""".stripMargin)

      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestCalculationId: String,
                                requestTaxYear: Option[String],
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new TysIfsTest {

            override val nino: String            = requestNino
            override val calculationId: String   = requestCalculationId
            override val taxYear: Option[String] = requestTaxYear

            val response: WSResponse = await(request().post(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          ("Walrus", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", None, mtdRequestValid, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "BAD_CALC_ID", None, mtdRequestValid, BAD_REQUEST, CalculationIdFormatError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2022-23"), mtdRequestValid, BAD_REQUEST, InvalidTaxYearParameterError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("BAD_TAX_YEAR"), mtdRequestValid, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2022-24"), mtdRequestValid, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", None, JsObject.empty, BAD_REQUEST, RuleIncorrectOrEmptyBodyError),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           None,
           mtdRequestNonFhlFull,
           BAD_REQUEST,
           RuleBothExpensesError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/expenses")))),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           None,
           requestBodyWithCountryCode("XXX"),
           BAD_REQUEST,
           RuleCountryCodeError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/countryCode")))),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           None,
           requestBodyWithCountryCode("FRANCE"),
           BAD_REQUEST,
           CountryCodeFormatError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/countryCode"))))
        )
        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new TysIfsTest {

            override def setupStubs(): Unit =
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUrl, downstreamStatus, errorBody(downstreamCode))

            val response: WSResponse = await(request().post(mtdRequestValid))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C15320", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55508", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55509", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C559107", FORBIDDEN, RulePropertyIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C559103", FORBIDDEN, RulePropertyIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C559099", FORBIDDEN, RuleOverConsolidatedExpensesThreshold),
          (FORBIDDEN, "BVR_FAILURE_C55503", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55316", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (CONFLICT, "ASC_ALREADY_SUPERSEDED", FORBIDDEN, RuleSummaryStatusSuperseded),
          (CONFLICT, "ASC_ALREADY_ADJUSTED", FORBIDDEN, RuleAlreadyAdjusted),
          (UNPROCESSABLE_ENTITY, "UNALLOWABLE_VALUE", FORBIDDEN, RuleResultingValueNotPermitted),
          (UNPROCESSABLE_ENTITY, "ASC_ID_INVALID", FORBIDDEN, RuleSummaryStatusInvalid),
          (UNPROCESSABLE_ENTITY, "INCOMESOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
        )
        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
