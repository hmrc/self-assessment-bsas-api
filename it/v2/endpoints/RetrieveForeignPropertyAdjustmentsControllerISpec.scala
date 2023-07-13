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

package v2.endpoints

import api.models.errors._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v2.fixtures.foreignProperty.RetrieveForeignPropertyAdjustmentsFixtures._
import v2.models.errors.{RuleNoAdjustmentsMade, RuleNotForeignProperty}
import v2.stubs.{AuditStub, AuthStub, DesStub, MtdIdLookupStub}

class RetrieveForeignPropertyAdjustmentsControllerISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino = "AA123456B"
    val bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5"

    val desResponse: String => String = (typeOfBusiness: String) =>
      s"""
         |{
         |    "metadata": {
         |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5",
         |        "requestedDateTime": "2020-10-14T11:33:27Z",
         |        "taxableEntityId": "AA1234567A",
         |        "taxYear": 2020,
         |        "status": "valid"
         |    },
         |    "inputs": {
         |        "incomeSourceId": "111111111111111",
         |        "incomeSourceType": "$typeOfBusiness",
         |        "accountingPeriodStartDate": "2020-10-11",
         |        "accountingPeriodEndDate": "2020-01-01",
         |        "source": "MTD-SA",
         |        "submissionPeriods": [
         |            {
         |                "periodId": "0000000000000000",
         |                "startDate": "2020-01-01",
         |                "endDate": "2021-10-10",
         |                "receivedDateTime": "2020-01-01T10:12:10Z"
         |            }
         |        ]
         |    },
         |    "adjustments": {
         |        "income": {
         |            "rentReceived": 100.49,
         |            "premiumsOfLeaseGrant": 100.49,
         |            "otherPropertyIncome": 100.49
         |        },
         |        "expenses": {
         |            "consolidatedExpenses": 100.49,
         |            "repairsAndMaintenance": 100.49,
         |            "financialCosts": 100.49,
         |            "professionalFees": 100.49,
         |            "costOfServices": 100.49,
         |            "travelCosts": 100.49,
         |            "other": 100.49,
         |            "premisesRunningCosts": 100.49
         |        }
         |    }
         |}
         |""".stripMargin

    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$bsasId"

    def setupStubs(): StubMapping

    def request: WSRequest = {

      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    def uri: String = s"/$nino/foreign-property/$bsasId/adjust"
  }

  "Calling the retrieve Foreign Property Adjustments endpoint" should {

    val desQueryParams = Map("return" -> "2")

    "return a valid response with status OK" when {
      "a valid response is received from des" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.GET, desUrl, desQueryParams, OK, desJson)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.header("Deprecation") shouldBe Some(
          "This endpoint is deprecated. See the API documentation: https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/self-assessment-bsas-api")
        response.json shouldBe Json.parse(hateoasResponseForForeignPropertyAdjustments(nino, bsasId))
      }
    }

    "return error according to spec" when {
      "request made is for an invalid type of business" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.GET, desUrl, OK, Json.parse(desResponse("01")))
        }

        val response: WSResponse = await(request.get())
        response.status shouldBe FORBIDDEN
        response.json shouldBe Json.toJson(RuleNotForeignProperty)
      }

      def validationErrorTest(requestNino: String, requestBsasId: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new Test {

          override val nino: String = requestNino
          override val bsasId: String = requestBsasId

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request.get())
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        ("AA1123A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, NinoFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-beans", BAD_REQUEST, BsasIdFormatError)
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
        s"des returns an $desCode error and status $desStatus" in new Test {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DesStub.onError(DesStub.GET, desUrl, desStatus, errorBody(desCode))
          }

          val response: WSResponse = await(request.get())
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, BsasIdFormatError),
        (BAD_REQUEST, "INVALID_RETURN", INTERNAL_SERVER_ERROR, InternalError),
        (UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", FORBIDDEN, RuleNoAdjustmentsMade),
        (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
      )
      input.foreach(args => (serviceErrorTest _).tupled(args))
    }
  }
}
