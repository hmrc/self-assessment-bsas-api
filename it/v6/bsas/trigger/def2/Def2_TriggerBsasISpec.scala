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

package v6.bsas.trigger.def2

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import common.errors._
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec
import v6.bsas.trigger.def2.model.Def2_TriggerBsasFixtures._

class Def2_TriggerBsasISpec extends IntegrationBaseSpec {

  "Calling the triggerBsas" should {
    "return a 200 status code" when {

      List(
        "self-employment",
        "uk-property",
        "foreign-property"
      ).foreach { typeOfBusiness =>
        s"any valid request is made with typeOfBusiness: $typeOfBusiness (TYS)" in new TysIfsTest {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, OK, Json.parse(downstreamResponse))
          }

          val result: WSResponse = await(request().post(makeRequestBody(typeOfBusiness)))
          result.status shouldBe OK
          result.json shouldBe Json.parse(responseBody)
          result.header("Content-Type") shouldBe Some("application/json")
        }
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, json: JsObject, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new TysIfsTest {

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

        val validRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2019-05-05", "endDate" -> "2020-05-06"),
          "typeOfBusiness"   -> "self-employment",
          "businessId"       -> "XAIS12345678901"
        )

        val missingFieldsRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2019-05-05")
        )

        val missingFieldsError: MtdError = RuleIncorrectOrEmptyBodyError.copy(
          paths = Some(
            List(
              "/accountingPeriod/endDate",
              "/businessId",
              "/typeOfBusiness"
            )))

        val startDateErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "20180202", "endDate" -> "2019-05-06"),
          "typeOfBusiness"   -> "self-employment",
          "businessId"       -> "XAIS12345678901"
        )

        val endDateErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "20190506"),
          "typeOfBusiness"   -> "self-employment",
          "businessId"       -> "XAIS12345678901"
        )

        val typeOfBusinessErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "2019-05-06"),
          "typeOfBusiness"   -> "selfemployment",
          "businessId"       -> "XAIS12345678901"
        )

        val businessIdErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "2019-05-06"),
          "typeOfBusiness"   -> "self-employment",
          "businessId"       -> "XAIS12345678901234"
        )

        val DateOrderErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2020-02-02", "endDate" -> "2019-05-06"),
          "typeOfBusiness"   -> "self-employment",
          "businessId"       -> "XAIS12345678901"
        )

        val accountingPeriodNotSupportRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "2018-05-06"),
          "typeOfBusiness"   -> "self-employment",
          "businessId"       -> "XAIS12345678901"
        )

        val input = List(
          ("AA1123A", validRequestJson, BAD_REQUEST, NinoFormatError),
          ("AA123456A", missingFieldsRequestJson, BAD_REQUEST, missingFieldsError),
          ("AA123456A", startDateErrorRequestJson, BAD_REQUEST, StartDateFormatError),
          ("AA123456A", endDateErrorRequestJson, BAD_REQUEST, EndDateFormatError),
          ("AA123456A", typeOfBusinessErrorRequestJson, BAD_REQUEST, TypeOfBusinessFormatError),
          ("AA123456A", businessIdErrorRequestJson, BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", DateOrderErrorRequestJson, BAD_REQUEST, RuleEndBeforeStartDateError),
          ("AA123456A", accountingPeriodNotSupportRequestJson, BAD_REQUEST, RuleAccountingPeriodNotSupportedError)
        )
        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new TysIfsTest {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.POST, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().post(makeRequestBody("self-employment")))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = List(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (UNPROCESSABLE_ENTITY, "ACCOUNTING_PERIOD_NOT_ENDED", BAD_REQUEST, RuleAccountingPeriodNotEndedError),
          (UNPROCESSABLE_ENTITY, "OBLIGATIONS_NOT_MET", BAD_REQUEST, RulePeriodicDataIncompleteError),
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
        (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  private trait Test {

    val nino = "AA123456A"

    def downstreamTaxYear: String

    def downstreamUri: String

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.6.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def uri: String = s"/$nino/trigger"

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "message"
         |      }
    """.stripMargin

    def makeRequestBody(typeOfBusiness: String): JsObject = {

      val startDate = "2025-05-01"

      val endDate = "2025-05-02"

      Json.obj(
        "accountingPeriod" -> Json.obj("startDate" -> startDate, "endDate" -> endDate),
        "typeOfBusiness"   -> typeOfBusiness,
        "businessId"       -> "XAIS12345678901"
      )
    }

    val responseBody: String =
      """
         |{
         |  "calculationId": "c75f40a6-a3df-4429-a697-471eeec46435"
         |}
    """.stripMargin

  }

  private trait TysIfsTest extends Test {
    def downstreamTaxYear: String = "25-26"

    override def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/25-26/$nino"

  }

}
