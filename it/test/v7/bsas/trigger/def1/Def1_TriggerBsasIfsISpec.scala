/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.bsas.trigger.def1

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import common.errors.*
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v7.bsas.trigger.def1.model.Def1_TriggerBsasFixtures.*

class Def1_TriggerBsasIfsISpec extends IntegrationBaseSpec {

  override def servicesConfig: Map[String, Any] =
    Map("feature-switch.ifs_hip_migration_1873.enabled" -> false) ++ super.servicesConfig

  "Calling the triggerBsas" should {
    "return a 200 status code" when {

      List(
        "self-employment",
        "uk-property-fhl",
        "uk-property",
        "foreign-property-fhl-eea",
        "foreign-property"
      ).foreach { typeOfBusiness =>
        s"any valid request is made with typeOfBusiness: $typeOfBusiness" in new NonTysTest {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)

            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, OK, Json.parse(downstreamResponse))
          }

          val result: WSResponse = await(request().post(requestBody(typeOfBusiness)))
          result.status shouldBe OK
          result.json shouldBe Json.parse(responseBody)
          result.header("Content-Type") shouldBe Some("application/json")
        }

        s"any valid request is made with typeOfBusiness: $typeOfBusiness (TYS)" in new TysIfsTest {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, OK, Json.parse(downstreamResponse))
          }

          val result: WSResponse = await(request().post(requestBody(typeOfBusiness)))
          result.status shouldBe OK
          result.json shouldBe Json.parse(responseBody)
          result.header("Content-Type") shouldBe Some("application/json")
        }
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, json: JsObject, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new NonTysTest {

            override val nino: String = requestNino

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().post(json))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        import NonTysRequestBodyHelper.*

        val input = List(
          ("AA1123A", requestBody(), BAD_REQUEST, NinoFormatError),
          (
            "AA123456A",
            Json.obj("accountingPeriod" -> Json.obj("startDate" -> defaultStartDate, "endDate" -> defaultEndDate)),
            BAD_REQUEST,
            RuleIncorrectOrEmptyBodyError.copy(
              paths = Some(
                List(
                  "/businessId",
                  "/typeOfBusiness"
                )))),
          ("AA123456A", requestBody(startDate = "20180202"), BAD_REQUEST, StartDateFormatError),
          ("AA123456A", requestBody(endDate = "20190506"), BAD_REQUEST, EndDateFormatError),
          ("AA123456A", requestBody(typeOfBusiness = "badTypeOfBusiness"), BAD_REQUEST, TypeOfBusinessFormatError),
          ("AA123456A", requestBody(businessId = "badBusinessId"), BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", requestBody(startDate = "2080-02-02", endDate = defaultEndDate), BAD_REQUEST, RuleEndBeforeStartDateError),
          ("AA123456A", requestBody(startDate = "2018-02-02", endDate = "2018-05-06"), BAD_REQUEST, RuleAccountingPeriodNotSupportedError)
        )

        input.foreach(validationErrorTest.tupled)
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.POST, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().post(requestBody("self-employment")))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = List(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (UNPROCESSABLE_ENTITY, "ACCOUNTING_PERIOD_NOT_ENDED", BAD_REQUEST, RuleAccountingPeriodNotEndedError),
          (UNPROCESSABLE_ENTITY, "OBLIGATIONS_NOT_MET", BAD_REQUEST, RuleObligationsNotMet),
          (UNPROCESSABLE_ENTITY, "NO_ACCOUNTING_PERIOD", BAD_REQUEST, RuleNoAccountingPeriodError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, TriggerNotFoundError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError)
        )

        val extraTysErrors = List(
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(serviceErrorTest.tupled)
      }
    }
  }

  trait RequestBodyHelper {

    val defaultTypeOfBusiness = "self-employment"

    val defaultBusinessId = "XAIS12345678901"

    val defaultStartDate: String

    val defaultEndDate: String

    def requestBody(typeOfBusiness: String = defaultTypeOfBusiness,
                    startDate: String = defaultStartDate,
                    endDate: String = defaultEndDate,
                    businessId: String = defaultBusinessId): JsObject = {
      Json.obj(
        "accountingPeriod" -> Json.obj("startDate" -> startDate, "endDate" -> endDate),
        "typeOfBusiness"   -> typeOfBusiness,
        "businessId"       -> businessId
      )
    }

  }

  trait NonTysRequestBodyHelper extends RequestBodyHelper {
    val defaultStartDate = "2021-04-06"

    val defaultEndDate = "2022-04-05"
  }

  object NonTysRequestBodyHelper extends NonTysRequestBodyHelper

  trait TysRequestBodyHelper extends RequestBodyHelper {

    val defaultStartDate = "2023-04-06"

    val defaultEndDate = "2024-04-05"
  }

  private trait Test {
    self: RequestBodyHelper =>

    val nino = "AA123456A"

    def downstreamUri: String

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.7.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    private def uri: String = s"/$nino/trigger"

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "message"
         |      }
    """.stripMargin

    val responseBody: String =
      """
         |{
         |  "calculationId": "c75f40a6-a3df-4429-a697-471eeec46435"
         |}
    """.stripMargin

  }

  private trait NonTysTest extends Test with NonTysRequestBodyHelper {

    override def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/$nino"

  }

  private trait TysIfsTest extends Test with TysRequestBodyHelper {

    override def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/23-24/$nino"

  }

}
