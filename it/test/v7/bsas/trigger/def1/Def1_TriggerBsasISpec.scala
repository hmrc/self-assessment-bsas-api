/*
 * Copyright 2026 HM Revenue & Customs
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
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.*
import api.models.errors.*
import api.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import api.support.IntegrationBaseSpec
import v7.bsas.trigger.def1.model.Def1_TriggerBsasFixtures.*

class Def1_TriggerBsasISpec extends IntegrationBaseSpec {

  private def requestBody(typeOfBusiness: String = "self-employment",
                          startDate: String = "2023-04-06",
                          endDate: String = "2024-04-05",
                          businessId: String = "XAIS12345678901"): JsObject = {
    Json.obj(
      "accountingPeriod" -> Json.obj("startDate" -> startDate, "endDate" -> endDate),
      "typeOfBusiness"   -> typeOfBusiness,
      "businessId"       -> businessId
    )
  }

  "Calling the triggerBsas" should {
    "return a 200 status code" when {
      List(
        "self-employment",
        "uk-property-fhl",
        "uk-property",
        "foreign-property-fhl-eea",
        "foreign-property"
      ).foreach { typeOfBusiness =>
        s"any valid request is made for a non-TYS tax year with typeOfBusiness: $typeOfBusiness" in new NonTysTest {
          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, OK, Json.parse(downstreamResponse))
          }

          val response: WSResponse = await(request().post(requestBody(typeOfBusiness, "2021-04-06", "2022-04-05")))
          response.status shouldBe OK
          response.json shouldBe Json.parse(responseBody)
          response.header("Content-Type") shouldBe Some("application/json")
          response.header("X-CorrelationId").nonEmpty shouldBe true
        }

        s"any valid request is made for a TYS tax year with typeOfBusiness: $typeOfBusiness" in new TysTest {
          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, OK, Json.parse(downstreamResponse))
          }

          val response: WSResponse = await(request().post(requestBody(typeOfBusiness)))
          response.status shouldBe OK
          response.json shouldBe Json.parse(responseBody)
          response.header("Content-Type") shouldBe Some("application/json")
          response.header("X-CorrelationId").nonEmpty shouldBe true
        }
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, json: JsObject, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new TysTest {
            override val nino: String = requestNino

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().post(json))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = List(
          ("AA1123A", requestBody(), BAD_REQUEST, NinoFormatError),
          (
            "AA123456A",
            Json.obj("accountingPeriod" -> Json.obj("startDate" -> "2023-04-06", "endDate" -> "2024-04-05")),
            BAD_REQUEST,
            RuleIncorrectOrEmptyBodyError.withPaths(List("/businessId", "/typeOfBusiness"))
          ),
          ("AA123456A", requestBody(startDate = "20180202"), BAD_REQUEST, StartDateFormatError),
          ("AA123456A", requestBody(endDate = "20190506"), BAD_REQUEST, EndDateFormatError),
          ("AA123456A", requestBody(typeOfBusiness = "badTypeOfBusiness"), BAD_REQUEST, TypeOfBusinessFormatError),
          ("AA123456A", requestBody(businessId = "badBusinessId"), BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", requestBody(startDate = "2024-04-06"), BAD_REQUEST, RuleEndBeforeStartDateError),
          ("AA123456A", requestBody(startDate = "2018-04-06", endDate = "2019-04-05"), BAD_REQUEST, RuleAccountingPeriodNotSupportedError),
          ("AA123456A", requestBody(startDate = "2023-04-07"), BAD_REQUEST, RuleAccountingPeriodNotAlignedError)
        )

        input.foreach(validationErrorTest.tupled)
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns a code $downstreamCode error and status $downstreamStatus" in new TysTest {
            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.POST, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().post(requestBody()))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("X-CorrelationId").nonEmpty shouldBe true
            response.header("Content-Type") shouldBe Some("application/json")
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
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (UNPROCESSABLE_ENTITY, "ACCOUNTING_PERIOD_NOT_ALIGNED", INTERNAL_SERVER_ERROR, InternalError)
        )

        (errors ++ extraTysErrors).foreach(serviceErrorTest.tupled)
      }
    }
  }

  private trait Test {
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

    def errorBody(`type`: String): String =
      s"""
        |{
        |  "origin": "HIP",
        |  "response": {
        |    "failures": [
        |      {
        |        "type": "${`type`}",
        |        "reason": "downstream message"
        |      }
        |    ]
        |  }
        |}
      """.stripMargin

    val responseBody: String =
      """
        |{
        |  "calculationId": "c75f40a6-a3df-4429-a697-471eeec46435"
        |}
      """.stripMargin

  }

  private trait NonTysTest extends Test {
    override def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/$nino"
  }

  private trait TysTest extends Test {
    override def downstreamUri: String = s"/itsa/income-tax/v1/23-24/adjustable-summary-calculation/$nino"
  }

}
