/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.http.Status
import play.api.http.Status.OK
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v2.fixtures.TriggerBsasRequestBodyFixtures.desResponse
import v2.models.domain.TypeOfBusiness
import v2.stubs.{AuditStub, AuthStub, DesStub, MtdIdLookupStub}

class AuthISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino          = "AA123456A"
    val data        = "someData"
    val correlationId = "X-123"

    val requestJson: JsObject = Json.obj(
      "accountingPeriod" -> Json.obj("startDate" -> "2019-01-01", "endDate" -> "2019-10-31"),
      "typeOfBusiness"   -> TypeOfBusiness.`self-employment`.toString,
      "selfEmploymentId" -> "XAIS12345678901"
    )

    def uri: String = s"/$nino/trigger"

    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.2.0+json"))
    }
  }

  "Calling the sample endpoint" when {

    "the NINO cannot be converted to a MTD ID" should {

      "return 500" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.internalServerError(nino)
        }

        val response: WSResponse = await(request().post(requestJson))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "an MTD ID is successfully retrieve from the NINO and the user is authorised" should {

      "return 201" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.POST, desUrl, OK, Json.parse(desResponse))
        }

        val response: WSResponse = await(request().post(requestJson))
        response.status shouldBe Status.CREATED
        response.header("Content-Type") shouldBe Some("application/json")
      }
    }

    "an MTD ID is successfully retrieve from the NINO and the user is NOT logged in" should {

      "return 403" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.unauthorisedNotLoggedIn()
        }

        val response: WSResponse = await(request().post(requestJson))
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "an MTD ID is successfully retrieve from the NINO and the user is NOT authorised" should {

      "return 403" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.unauthorisedOther()
        }

        val response: WSResponse = await(request().post(requestJson))
        response.status shouldBe Status.FORBIDDEN
      }
    }

  }

}
