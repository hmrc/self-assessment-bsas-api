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

package v1.models.response.retrieveBsas.selfEmployment

import mocks.MockAppConfig
import v1.models.hateoas.Method._
import support.UnitSpec
import v1.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures.{desRetrieveBsasResponseJsonAdjustable, desRetrieveBsasResponseJsonAdjusted, mtdRetrieveBsasResponseJson, retrieveBsasResponseModelAdjustable, retrieveBsasResponseModelAdjusted}
import v1.hateoas.HateoasFactory
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.utils.JsonErrorValidators

class RetrieveSelfEmploymentBsasResponseSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid model" when {
      "passed valid JSON with adjustedSummaryCalculation" in {
        desRetrieveBsasResponseJsonAdjusted.as[RetrieveSelfEmploymentBsasResponse] shouldBe retrieveBsasResponseModelAdjusted
      }
      "passed valid JSON with adjustableSummaryCalculation" in {
        desRetrieveBsasResponseJsonAdjustable.as[RetrieveSelfEmploymentBsasResponse] shouldBe retrieveBsasResponseModelAdjustable
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid model with adjustedSummary = true" in {
         retrieveBsasResponseModelAdjusted.toJson shouldBe mtdRetrieveBsasResponseJson(true)
      }
      "passed a valid model with adjustedSummary = false" in {
        retrieveBsasResponseModelAdjustable.toJson shouldBe mtdRetrieveBsasResponseJson(false)
      }
    }
  }

  "HateoasFactory" should {
    class Test extends MockAppConfig{
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      val bsasId = "anId"
      val adjustment = "03"
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes
    }

    "expose the correct links for a response from Submit a Property Summary Adjustment" in new Test {
      hateoasFactory.wrap(retrieveBsasResponseModelAdjustable, RetrieveSelfAssessmentBsasHateoasData(nino, bsasId)) shouldBe
        HateoasWrapper(
          retrieveBsasResponseModelAdjustable,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId", GET, "self"),
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId/adjust", POST, "submit-summary-adjustments")
          )
        )
    }
  }

}
