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

package v3.models.response.retrieveBsas.ukProperty

import mocks.MockAppConfig
import support.UnitSpec
import v3.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v3.hateoas.HateoasFactory
import v3.models.hateoas.Method._
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.utils.JsonErrorValidators

class RetrieveUkPropertyBsasResponseSpec extends UnitSpec with JsonErrorValidators with RoundTripTest {

  import RetrieveUkPropertyBsasResponse._

  testRoundTrip("Retrieve UK Property FHL", downstreamRetrieveBsasFhlResponseJson, retrieveBsasResponseFhlModel, mtdRetrieveBsasResponseFhlJson)(reads)
  testRoundTrip("Retrieve UK Property Non-FHL", downstreamRetrieveBsasNonFhlResponseJson, retrieveBsasResponseNonFhlModel, mtdRetrieveBsasResponseNonFhlJson)(reads)

  "HateoasFactory" should {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val calculationId  = "anId"
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes
    }

    "expose the correct links for a response from Submit a Property Summary Adjustment" in new Test {
      hateoasFactory.wrap(retrieveBsasResponseFhlModel, RetrieveUkPropertyHateoasData(nino, calculationId)) shouldBe
        HateoasWrapper(
          retrieveBsasResponseFhlModel,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/uk-property/$calculationId", GET, "self"),
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/uk-property/$calculationId/adjust", POST, "submit-uk-property-accounting-adjustments")
          )
        )
    }
  }

}
