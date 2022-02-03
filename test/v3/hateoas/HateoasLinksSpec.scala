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

package v3.hateoas

import mocks.MockAppConfig
import support.UnitSpec
import v3.models.hateoas.Link
import v3.models.hateoas.Method.{GET, POST}
import v3.models.hateoas.RelType._

class HateoasLinksSpec extends UnitSpec with MockAppConfig {

  private val nino   = "AA111111A"
  private val calcId = "1234567890"

  object Target extends HateoasLinks

  class Test {
    val context = "context"
    MockedAppConfig.apiGatewayContext.returns(context).anyNumberOfTimes
  }

  "HateoasLinks" when {

    "supplied config and valid nino" should {
      "return the Trigger BSAS link" in new Test {
        val link: Link = Link(href = s"/context/$nino/trigger", method = POST, rel = TRIGGER)

        Target.triggerBsas(mockAppConfig, nino) shouldBe link
      }

      "return the List BSAS link" in new Test {
        val link: Link = Link(href = s"/context/$nino", method = GET, rel = SELF)

        Target.listBsas(mockAppConfig, nino) shouldBe link
      }
    }

    "supplied config, a nino, and a calculation ID" should {

      "return the Retrieve SE BSAS link" in new Test {
        val link: Link = Link(href = s"/context/$nino/self-employment/$calcId", method = GET, rel = SELF)
        Target.getSelfEmploymentBsas(mockAppConfig, nino, calcId) shouldBe link
      }

      "return the UK Retrieve Property BSAS link" in new Test {
        val link: Link = Link(href = s"/context/$nino/property/$calcId", method = GET, rel = SELF)
        Target.getUkPropertyBsas(mockAppConfig, nino, calcId) shouldBe link
      }

      "return the Retrieve Foreign Property BSAS link" in new Test {
        val link: Link = Link(href = s"/context/$nino/foreign-property/$calcId", method = GET, rel = SELF)
        Target.getForeignPropertyBsas(mockAppConfig, nino, calcId) shouldBe link
      }

      "return the Submit SE BSAS Adjustments link" in new Test {
        val link: Link = Link(href = s"/context/$nino/self-employment/$calcId/adjust", method = POST, rel = SUBMIT_SE_ADJUSTMENTS)
        Target.adjustSelfEmploymentBsas(mockAppConfig, nino, calcId) shouldBe link
      }

      "return the Submit UK Property BSAS Adjustments link" in new Test {
        val link: Link = Link(href = s"/context/$nino/property/$calcId/adjust", method = POST, rel = SUBMIT_UK_PROPERTY_ADJUSTMENTS)
        Target.adjustPropertyBsas(mockAppConfig, nino, calcId) shouldBe link
      }

      "return the Submit Foreign Property BSAS Adjustments link" in new Test {
        val link: Link = Link(href = s"/context/$nino/foreign-property/$calcId/adjust", method = POST, rel = SUBMIT_FOREIGN_PROPERTY_ADJUSTMENTS)
        Target.adjustForeignPropertyBsas(mockAppConfig, nino, calcId) shouldBe link
      }
    }
  }
}
