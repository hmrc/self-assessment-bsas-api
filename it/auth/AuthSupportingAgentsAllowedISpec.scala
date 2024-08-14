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

package auth

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.OK
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec
import v6.bsas.trigger.def1.model.Def1_TriggerBsasFixtures
import v6.common.model.TypeOfBusiness

class AuthSupportingAgentsAllowedISpec extends IntegrationBaseSpec {

  private val callingApiVersion = "6.0"

  private val supportingAgentsAllowedEndpoint = "trigger-bsas"

  /** One endpoint where supporting agents are allowed.
    */
  override def servicesConfig: Map[String, Any] =
    Map(
      s"api.supporting-agent-endpoints.$supportingAgentsAllowedEndpoint" -> "true"
    ) ++ super.servicesConfig

  private val nino = "AA123456A"

  private val mtdUrl = s"/$nino/trigger"

  private val requestJson = Json.obj(
    "accountingPeriod" -> Json.obj("startDate" -> "2019-01-01", "endDate" -> "2019-10-31"),
    "typeOfBusiness"   -> TypeOfBusiness.`self-employment`.toString,
    "businessId"       -> "XAIS12345678901"
  )

  private val downstreamResponse: JsValue = Json.parse(Def1_TriggerBsasFixtures.downstreamResponse)

  "Calling an endpoint that allows supporting agents" when {
    "the client is the primary agent" should {
      "return a success response" in new Test {
        def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)

          AuthStub.authorisedWithAgentAffinityGroup()
          AuthStub.authorisedWithPrimaryAgentEnrolment()

          DownstreamStub
            .when(DownstreamStub.POST, downstreamUri)
            .thenReturn(OK, downstreamResponse)
        }

        val response: WSResponse = sendMtdRequest()
        response.status shouldBe OK
      }
    }

    "the client is a supporting agent" should {
      "return a success response" in new Test {
        def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)

          AuthStub.authorisedWithAgentAffinityGroup()
          AuthStub.unauthorisedForPrimaryAgentEnrolment()
          AuthStub.authorisedWithSupportingAgentEnrolment()

          DownstreamStub
            .when(DownstreamStub.POST, downstreamUri)
            .thenReturn(OK, downstreamResponse)
        }

        val response: WSResponse = sendMtdRequest()
        response.status shouldBe OK
      }
    }
  }

  private trait Test {

    def setupStubs(): StubMapping

    def sendMtdRequest(): WSResponse = await(request.post(requestJson))

    private def request: WSRequest = {
      setupStubs()
      buildRequest(mtdUrl)
        .withHttpHeaders(
          (ACCEPT, s"application/vnd.hmrc.$callingApiVersion+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/$nino"
  }

}
