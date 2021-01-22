/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v2.fixtures.TriggerBsasRequestBodyFixtures._
import v2.models.errors._
import v2.stubs.{AuditStub, AuthStub, DesStub, MtdIdLookupStub}

class TriggerBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino             = "AA123456A"

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


    def requestBody(typeOfBusiness: String): JsObject =
      Json.obj(
        "accountingPeriod" -> Json.obj("startDate" -> "2019-01-01", "endDate" -> "2022-10-31"),
        "typeOfBusiness"   -> typeOfBusiness,
        "businessId" -> "XAIS12345678901"
      )

    def responseBody(hateoasLinkPath: String): String = s"""
      |{
      |  "id": "c75f40a6-a3df-4429-a697-471eeec46435",
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

    "return a 201 status code" when {

      List(
        ("self-employment", "self-employment"),
        ("uk-property-fhl", "property"),
        ("uk-property-non-fhl", "property"),
        ("foreign-property-fhl-eea", "foreign-property"),
        ("foreign-property", "foreign-property"),
      ).foreach {
        case (typeOfBusiness, hateoasLinkPath) =>
          s"any valid request is made with typeOfBusiness: $typeOfBusiness" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DesStub.onSuccess(DesStub.POST, desUrl, OK, Json.parse(desResponse))
            }

            val result: WSResponse = await(request().post(requestBody(typeOfBusiness)))
            result.status shouldBe CREATED
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

        val input = Seq(
          ("AA1123A", requestBody, BAD_REQUEST, NinoFormatError),
          ("AA123456A", Json.obj("accountingPeriod" -> Json.obj("startDate" -> "2018-02-02")),
            BAD_REQUEST, RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/accountingPeriod/endDate", "/typeOfBusiness", "/businessId")))),
          ("AA123456A", Json.obj(
            "accountingPeriod" -> Json.obj("startDate" -> "20180202", "endDate" -> "2019-05-06"),
            "typeOfBusiness" -> "self-employment",
            "businessId" -> "XAIS12345678901"
          ), BAD_REQUEST, StartDateFormatError),
          ("AA123456A", Json.obj(
            "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "20190506"),
            "typeOfBusiness" -> "self-employment",
            "businessId" -> "XAIS12345678901"
          ), BAD_REQUEST, EndDateFormatError),
          ("AA123456A", Json.obj(
            "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "2019-05-06"),
            "typeOfBusiness" -> "selfemployment",
            "businessId" -> "XAIS12345678901"
          ), BAD_REQUEST, TypeOfBusinessFormatError),
          ("AA123456A", Json.obj(
            "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "2019-05-06"),
            "typeOfBusiness" -> "self-employment",
            "businessId" -> "XAIS12345678901234"
          ), BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", Json.obj(
            "accountingPeriod" -> Json.obj("startDate" -> "2020-02-02", "endDate" -> "2019-05-06"),
            "typeOfBusiness" -> "self-employment",
            "businessId" -> "XAIS12345678901"
          ), BAD_REQUEST, RuleEndBeforeStartDateError),
          ("AA123456A", Json.obj(
            "accountingPeriod" -> Json.obj("startDate" -> "2018-02-02", "endDate" -> "2018-05-06"),
            "typeOfBusiness" -> "self-employment",
            "businessId" -> "XAIS12345678901"
          ), BAD_REQUEST, RuleAccountingPeriodNotSupportedError)
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
              DesStub.onError(DesStub.POST, desUrl, desStatus, errorBody(desCode))
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
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (UNPROCESSABLE_ENTITY, "INCOME_SOURCEID_NOT_PROVIDED", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
