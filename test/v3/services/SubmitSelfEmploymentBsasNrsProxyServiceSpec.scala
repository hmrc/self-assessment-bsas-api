/*
 * Copyright 2023 HM Revenue & Customs
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

package v3.services

import api.models.domain.Nino
import api.services.ServiceSpec
import v3.mocks.connectors.MockSubmitSelfEmploymentBsasNrsProxyConnector
import v3.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestBody

import scala.concurrent.Future

class SubmitSelfEmploymentBsasNrsProxyServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")

  trait Test extends MockSubmitSelfEmploymentBsasNrsProxyConnector {
    lazy val service = new SubmitSelfEmploymentBsasNrsProxyService(mockNrsProxyConnector)
  }


  "NrsProxyService" should {
    "call the Nrs Proxy connector" when {
      "the connector is valid" in new Test {
        MockNrsProxyConnector.submit(nino.toString())
          .returns(Future.successful((): Unit))

        await(service.submit(nino.toString(), SubmitSelfEmploymentBsasRequestBody(None, None, None))) shouldBe (())
      }
      "the connector fails" in new Test {
        MockNrsProxyConnector.submit(nino.toString())
          .returns(Future.failed(new Exception()))

        assertThrows[Exception](
          await(service.submit(nino.toString(), SubmitSelfEmploymentBsasRequestBody(None, None, None)))
        )
      }
    }
  }
}
