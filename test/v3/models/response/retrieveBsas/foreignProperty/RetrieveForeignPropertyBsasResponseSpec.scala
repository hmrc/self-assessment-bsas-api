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

package v3.models.response.retrieveBsas.foreignProperty

import mocks.MockAppConfig
import support.UnitSpec
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import v3.hateoas.HateoasFactory
import v3.models.hateoas.Method.{ GET, POST }
import v3.models.hateoas.{ HateoasWrapper, Link }
import v3.models.utils.JsonErrorValidators

class RetrieveForeignPropertyBsasResponseSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid retrieve BSAS model" when {
      "a valid FhlEea json with all fields are supplied" in {
        retrieveForeignPropertyBsasDesJsonFhlEea.as[RetrieveForeignPropertyBsasResponse] shouldBe retrieveBsasResponseFhlEeaModel
      }

      "a valid NonFhl json with all fields are supplied" in {
        retrieveForeignPropertyBsasDesJsonNonFhl.as[RetrieveForeignPropertyBsasResponse] shouldBe retrieveBsasResponseNonFhlModel
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid fhlEea model is supplied" in {
        retrieveBsasResponseFhlEeaModel.toJson shouldBe retrieveForeignPropertyBsasMtdJsonFhlEea
      }

      "a valid nonFhl model is supplied" in {
        retrieveBsasResponseNonFhlModel.toJson shouldBe retrieveForeignPropertyBsasMtdJsonNonFhl
      }
    }
  }

  "HateoasFactory" should {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val calculationId  = "anId"
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes
    }

    "expose the correct links for a response from Submit a Property Summary Adjustment" in new Test {
      val rawResponse: RetrieveForeignPropertyBsasResponse = retrieveBsasResponseFhlEeaModel

      hateoasFactory.wrap(rawResponse, RetrieveForeignPropertyHateoasData(nino, calculationId)) shouldBe
        HateoasWrapper(
          rawResponse,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$calculationId", GET, "self"),
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$calculationId/adjust",
                 POST,
                 "submit-foreign-property-accounting-adjustments")
          )
        )
    }
  }
}
