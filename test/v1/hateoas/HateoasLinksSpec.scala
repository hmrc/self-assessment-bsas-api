/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.hateoas

import mocks.MockAppConfig
import support.UnitSpec
import v1.models.hateoas.Link
import v1.models.hateoas.Method.{GET, POST}
import v1.models.hateoas.RelType._

class HateoasLinksSpec extends UnitSpec with MockAppConfig {

  private val nino   = "AA111111A"
  private val bsasId = "1234567890"

  object Target extends HateoasLinks

  class Test {
    val context = "context"
    MockedAppConfig.apiGatewayContext.returns(context).anyNumberOfTimes
  }

  "HateoasLinks" when {

    "supplied config and valid nino" should {
      "return the Trigger BSAS link" in new Test {
        val link = Link(href = s"/context/$nino/trigger", method = POST, rel = TRIGGER)

        Target.triggerBsas(mockAppConfig, nino) shouldBe link
      }

      "return the List BSAS link" in new Test {
        val link = Link(href = s"/context/$nino", method = GET, rel = SELF)

        Target.listBsas(mockAppConfig, nino) shouldBe link
      }
    }

    "supplied config, a nino, and a bsas ID" should {

      "return the Retrieve SE BSAS link" in new Test {
        val link = Link(href = s"/context/$nino/self-employment/$bsasId", method = GET, rel = SELF)
        Target.getSelfEmploymentBsas(mockAppConfig, nino, bsasId) shouldBe link
      }

      "return the Retrieve Property BSAS link" in new Test {
        val link = Link(href = s"/context/$nino/property/$bsasId", method = GET, rel = SELF)
        Target.getPropertyBsas(mockAppConfig, nino, bsasId) shouldBe link
      }

      "return the Retrieve Property BSAS link with adjustedStatus parameter set" in new Test {
        val link = Link(href = s"/context/$nino/property/$bsasId?adjustedStatus=true", method = GET, rel = RETRIEVE_BSAS)
        Target.getAdjustedPropertyBsas(mockAppConfig, nino, bsasId) shouldBe link
      }

      "return the Retrieve SE BSAS link with adjustedStatus parameter set" in new Test {
        val link = Link(href = s"/context/$nino/self-employment/$bsasId?adjustedStatus=true", method = GET, rel = RETRIEVE_BSAS)
        Target.getAdjustedSelfEmploymentBsas(mockAppConfig, nino, bsasId) shouldBe link
      }

      "return the Submit SE BSAS Adjustments link" in new Test {
        val link = Link(href = s"/context/$nino/self-employment/$bsasId/adjust", method = POST, rel = SUBMIT_ADJUSTMENTS)
        Target.adjustSelfEmploymentBsas(mockAppConfig, nino, bsasId) shouldBe link
      }

      "return the Submit Property BSAS Adjustments link" in new Test {
        val link = Link(href = s"/context/$nino/property/$bsasId/adjust", method = POST, rel = SUBMIT_ADJUSTMENTS)
        Target.adjustPropertyBsas(mockAppConfig, nino, bsasId) shouldBe link
      }

      "return the Retrieve SE BSAS Adjustments link" in new Test {
        val link = Link(href = s"/context/$nino/self-employment/$bsasId/adjust", method = GET, rel = SELF)
        Target.getSelfEmploymentBsasAdjustments(mockAppConfig, nino, bsasId) shouldBe link
      }

      "return the Retrieve Property BSAS Adjustments link" in new Test {
        val link = Link(href = s"/context/$nino/property/$bsasId/adjust", method = GET, rel = SELF)
        Target.getPropertyBsasAdjustments(mockAppConfig, nino, bsasId) shouldBe link
      }
    }
  }
}
