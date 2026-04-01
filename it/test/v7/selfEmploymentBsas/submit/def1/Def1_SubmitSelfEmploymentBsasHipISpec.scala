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

package v7.selfEmploymentBsas.submit.def1

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import common.errors.*
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v7.selfEmploymentBsas.submit.def1.model.request.fixtures.SubmitSelfEmploymentBsasFixtures.*

class Def1_SubmitSelfEmploymentBsasHipISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino          = "AA123456A"
    val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

    def setupStubs(): StubMapping

    def mtdUri: String

    def downstreamUrl: String
    def taxYear: String

    def request(): WSRequest = {
      setupStubs()
      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.7.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(code: String): String =
      s"""
         |{
         |  "response": {
         |    "failures": [
         |      {
         |        "type": "$code",
         |        "reason": "message"
         |      }
         |    ]
         |  }
         |}
    """.stripMargin

  }

  private trait HipTest extends Test {
    override def taxYear: String = "2023-24"
    def mtdUri: String           = s"/$nino/self-employment/$calculationId/adjust/$taxYear"
    def downstreamUrl: String    = s"/itsa/income-tax/v1/23-24/adjustable-summary-calculation/$nino/$calculationId"
  }

  val requestBody: JsValue = mtdRequestJson

  "Calling the Submit Adjustments endpoint for self-employment" should {
    "return a 200 status code" when {
      "any valid TYS request is made" in new HipTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUrl, OK)
        }

        val result: WSResponse = await(request().post(requestBody))
        result.status shouldBe OK
        result.header("Content-Type") shouldBe None
      }

    }

    "return error according to spec" when {

      "validation error" when {
        def validationErrorTest(requestNino: String, expectedStatus: Int, expectedBody: MtdError, requestBodyJson: JsValue): Unit = {
          s"validation fails with ${expectedBody.code} error" in new HipTest {

            override val nino: String = requestNino

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().post(requestBodyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = List(
          ("AA1234A", BAD_REQUEST, NinoFormatError, requestBody),
          ("AA123456A", BAD_REQUEST, RuleBothExpensesError.copy(paths = Some(List("/expenses"))), mtdRequestWithBothExpenses)
        )
        input.foreach(validationErrorTest.tupled)
      }
    }
  }

}
