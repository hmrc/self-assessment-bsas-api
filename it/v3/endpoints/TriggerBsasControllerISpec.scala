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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v3.fixtures.TriggerBsasRequestBodyFixtures._
import v3.models.errors._
import v3.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}

class TriggerBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino = "AA123456A"

    def setupStubs(): StubMapping
    def uri: String = s"/$nino/trigger"
    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
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


    def requestBody(typeOfBusiness: String): JsObject =
      Json.obj(
        "accountingPeriod" -> Json.obj("startDate" -> "2019-01-01", "endDate" -> "2022-10-31"),
        "typeOfBusiness"   -> typeOfBusiness,
        "businessId" -> "XAIS12345678901"
      )

    def responseBody(hateoasLinkPath: String): String = s"""
      |{
      |  "calculationId": "c75f40a6-a3df-4429-a697-471eeec46435",
      |  "links":[
      |    {
      |      "href":"/individuals/self-assessment/adjustable-summary/$nino/$hateoasLinkPath/c75f40a6-a3df-4429-a697-471eeec46435",
      |      "rel":"self",
      |      "method":"GET"
      |    }
      |  ]
      |}
    """.stripMargin
  }

  "Calling the triggerBsas" should {
    "return a 200 status code" when {

      List(
        ("self-employment", "self-employment"),
        ("uk-property-fhl", "uk-property"),
        ("uk-property-non-fhl", "uk-property"),
        ("foreign-property-fhl-eea", "foreign-property"),
        ("foreign-property", "foreign-property"),
      ).foreach {
        case (typeOfBusiness, hateoasLinkPath) =>
          s"any valid request is made with typeOfBusiness: $typeOfBusiness" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onSuccess(DownstreamStub.POST, desUrl, OK, Json.parse(desResponse))
            }

            val result: WSResponse = await(request().post(requestBody(typeOfBusiness)))
            result.status shouldBe OK
            result.json shouldBe Json.parse(responseBody(hateoasLinkPath))
            result.header("Content-Type") shouldBe Some("application/json")
          }
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, json: JsObject, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

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
          "typeOfBusiness" -> "self-employment",
          "businessId" -> "XAIS12345678901"
        )

        val missingFieldsRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2019-05-05")
        )

        val missingFieldsError: MtdError = RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq(
          "/accountingPeriod/endDate", "/typeOfBusiness", "/businessId"
        )))

        val startDateErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "20180202", "endDate" -> "2019-05-06"),
          "typeOfBusiness" -> "self-employment",
          "businessId" -> "XAIS12345678901"
        )

        val endDateErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "20190506"),
          "typeOfBusiness" -> "self-employment",
          "businessId" -> "XAIS12345678901"
        )

        val typeOfBusinessErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "2019-05-06"),
          "typeOfBusiness" -> "selfemployment",
          "businessId" -> "XAIS12345678901"
        )

        val businessIdErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "2019-05-06"),
          "typeOfBusiness" -> "self-employment",
          "businessId" -> "XAIS12345678901234"
        )

        val DateOrderErrorRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2020-02-02", "endDate" -> "2019-05-06"),
          "typeOfBusiness" -> "self-employment",
          "businessId" -> "XAIS12345678901"
        )

        val accountingPeriodNotSupportRequestJson: JsObject = Json.obj(
          "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "2018-05-06"),
          "typeOfBusiness" -> "self-employment",
          "businessId" -> "XAIS12345678901"
        )

        val input = Seq(
          ("AA1123A",  validRequestJson, BAD_REQUEST, NinoFormatError),
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

      "des service error" when {
        def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"des returns an $desCode error and status $desStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.POST, desUrl, desStatus, errorBody(desCode))
            }

            val response: WSResponse = await(request().post(requestBody("self-employment")))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (UNPROCESSABLE_ENTITY, "ACCOUNTING_PERIOD_NOT_ENDED", FORBIDDEN, RuleAccountingPeriodNotEndedError),
          (UNPROCESSABLE_ENTITY, "OBLIGATIONS_NOT_MET", FORBIDDEN, RulePeriodicDataIncompleteError),
          (UNPROCESSABLE_ENTITY, "NO_ACCOUNTING_PERIOD", FORBIDDEN, RuleNoAccountingPeriodError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError)
        )
        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
