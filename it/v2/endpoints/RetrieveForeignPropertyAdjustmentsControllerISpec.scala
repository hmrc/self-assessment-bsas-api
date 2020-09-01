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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v2.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v2.fixtures.foreignProperty.RetrieveForeignPropertyAdjustmentsFixtures.hateoasResponseForForeignPropertyAdjustments
import v2.fixtures.selfEmployment.RetrieveSelfEmploymentAdjustmentsFixtures.desJson
import v2.stubs.{AuditStub, AuthStub, DesStub, MtdIdLookupStub}

class RetrieveForeignPropertyAdjustmentsControllerISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino = "AA123456B"
    val correlationId = "X-123"
    val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

    def uri: String = s"/$nino/foreign-property/$bsasId/adjust"

    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$bsasId"

    def setupStubs(): StubMapping

    def request: WSRequest = {

      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.2.0+json"))
    }
  }


  "Calling the retrieve Foreign Property Adjustments endpoint" should {

    val desQueryParams = Map("return" -> "2")

    "return a valid response with status OK" when {

      "a valid response is recieved from des" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.GET, desUrl, desQueryParams, OK, desJson)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.parse(hateoasResponseForForeignPropertyAdjustments(nino, bsasId))
      }
    }

    "return error response with status BAD_REQUEST" when {

      "valid requesy"
    }
  }
}
