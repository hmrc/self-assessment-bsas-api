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

import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v2.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import v3.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures
import v3.models.errors._
import v3.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}

class RetrieveForeignPropertyBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino   = "AA123456B"
    val calcId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

    def downstreamUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$calcId"

    def request: WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      buildRequest(s"/$nino/foreign-property/$calcId")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
      )
    }

    def responseWithHateoas(response: JsValue, nino: String, calculationId: String): JsValue =
      response.as[JsObject] ++ Json.parse(
        s"""
           |{
           |  "links": [
           |    {
           |      "href": "/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$calculationId",
           |      "method": "GET",
           |      "rel": "self"
           |    }, {
           |      "href": "/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$calculationId/adjust",
           |      "method": "POST",
           |      "rel": "submit-foreign-property-accounting-adjustments"
           |    }
           |  ]
           |}
           |""".stripMargin).as[JsObject]
  }

  "Calling the retrieve Foreign Property Bsas endpoint" should {
    "return a valid response with status OK" when {
      "valid request is made and Non-fhl is returned" in new Test {
        DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, retrieveForeignPropertyBsasDesNonFhlJson)

        val response: WSResponse = await(request.get)

        response.json shouldBe responseWithHateoas(retrieveForeignPropertyBsasMtdNonFhlJson, nino, calcId)
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
      }

      "valid request is made and fhl is returned" in new Test {
        DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, retrieveForeignPropertyBsasDesFhlJson)

        val response: WSResponse = await(request.get)

        response.json shouldBe responseWithHateoas(retrieveForeignPropertyBsasMtdFhlJson, nino, calcId)
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
      }
    }

    "return error response with status BAD_REQUEST" when {
      "Downstream response is UK property" in {
        checkTypeOfBusinessIncorrectWith(RetrieveUkPropertyBsasFixtures.downstreamRetrieveBsasResponse)
      }

      "Downstream response is self employment" in {
        checkTypeOfBusinessIncorrectWith(RetrieveSelfEmploymentBsasFixtures.downstreamRetrieveBsasResponseJson)
      }

      def checkTypeOfBusinessIncorrectWith(downstreamResponse: JsValue): Unit =
        new Test {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, downstreamResponse)

          val response: WSResponse = await(request.get)

          response.json shouldBe Json.toJson(RuleTypeOfBusinessIncorrectError)
          response.status shouldBe BAD_REQUEST
          response.header("Content-Type") shouldBe Some("application/json")
        }
    }

    "return error according to spec" when {
      def validationErrorTest(requestNino: String, requestBsasId: String, expectedStatus: Int, expectedBody: MtdError): Unit =
        s"validation fails with ${expectedBody.code} error" in new Test {
          override val nino: String   = requestNino
          override val calcId: String = requestBsasId

          val response: WSResponse = await(request.get)

          response.json shouldBe Json.toJson(expectedBody)
          response.status shouldBe expectedStatus
          response.header("Content-Type") shouldBe Some("application/json")
        }

      val input = Seq(
        ("BAD_NINO", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, NinoFormatError),
        ("AA123456A", "bad_calc_id", BAD_REQUEST, CalculationIdFormatError)
      )
      input.foreach(args => (validationErrorTest _).tupled(args))
    }

    "downstream service error" when {
      def errorBody(code: String): String =
        s"""{
           |  "code": "$code",
           |  "reason": "message"
           |}""".stripMargin

      def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit =
        s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {
          DownstreamStub.onError(DownstreamStub.GET, downstreamUrl, downstreamStatus, errorBody(downstreamCode))

          val response: WSResponse = await(request.get)

          response.json shouldBe Json.toJson(expectedBody)
          response.status shouldBe expectedStatus
          response.header("Content-Type") shouldBe Some("application/json")
        }

      val input = Seq(
        (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
        (BAD_REQUEST, "INVALID_RETURN", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
        (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
        (UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", INTERNAL_SERVER_ERROR, InternalError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
      )
      input.foreach(args => (serviceErrorTest _).tupled(args))
    }
  }
}