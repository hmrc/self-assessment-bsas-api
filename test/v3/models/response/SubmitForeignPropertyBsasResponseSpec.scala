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

package v3.models.response

import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.models.hateoas.Link
import v3.models.hateoas.Method.GET
import v3.models.domain.TypeOfBusiness

class SubmitForeignPropertyBsasResponseSpec extends UnitSpec with MockAppConfig {

  "LinksFactory" should {
    "return the correct links" in {
      val nino = "mynino"
      val bsasId = "mybsasid"

      MockedAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes
      SubmitForeignPropertyBsasResponse.SubmitForeignPropertyAdjustmentHateoasFactory.links(
        mockAppConfig, SubmitForeignPropertyBsasHateoasData(nino, bsasId)) shouldBe
        Seq(
          Link(s"/my/context/$nino/foreign-property/$bsasId", GET, "self")
        )
    }
  }

}
