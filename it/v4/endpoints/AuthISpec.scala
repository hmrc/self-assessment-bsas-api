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

package v4.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status
import play.api.http.Status.OK
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v4.fixtures.TriggerBsasRequestBodyFixtures.downstreamResponse
import v4.models.domain.TypeOfBusiness

class AuthISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino = "AA123456A"

    val requestJson: JsObject = Json.obj(
      "accountingPeriod" -> Json.obj("startDate" -> "2019-01-01", "endDate" -> "2019-10-31"),
      "typeOfBusiness"   -> TypeOfBusiness.`self-employment`.toString,
      "businessId"       -> "XAIS12345678901"
    )

    def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/$nino"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.4.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    def uri: String = s"/$nino/trigger"
  }

  "Calling the sample endpoint" when {

    "MTD ID lookup fails with a 500" should {

      "return 500" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.error(nino, Status.INTERNAL_SERVER_ERROR)
        }

        val response: WSResponse = await(request().post(requestJson))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "MTD ID lookup fails with a 403" should {

      "return 403" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.error(nino, Status.FORBIDDEN)
        }

        val response: WSResponse = await(request().post(requestJson))
        response.status shouldBe Status.FORBIDDEN
      }
    }
  }

  "MTD ID lookup succeeds and the user is authorised" should {

    "return 200" in new Test {
      override def setupStubs(): StubMapping = {
        AuditStub.audit()
        AuthStub.authorised()
        MtdIdLookupStub.ninoFound(nino)
        DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, OK, Json.parse(downstreamResponse))
      }

      val response: WSResponse = await(request().post(requestJson))
      response.status shouldBe Status.OK
      response.header("Content-Type") shouldBe Some("application/json")
    }
  }

  "MTD ID lookup succeeds but the user is NOT logged in" should {

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

  "MTD ID lookup succeeds but the user is NOT authorised" should {

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
