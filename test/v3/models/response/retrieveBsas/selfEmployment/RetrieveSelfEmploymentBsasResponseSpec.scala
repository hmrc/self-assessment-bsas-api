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

package v3.models.response.retrieveBsas.selfEmployment

import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v3.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures.{downstreamRetrieveBsasResponseJson, mtdRetrieveBsasResponseJson, retrieveBsasResponseModel}
import v3.hateoas.HateoasFactory
import v3.models.hateoas.Method._
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.utils.JsonErrorValidators

class RetrieveSelfEmploymentBsasResponseSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid model" when {
      "passed valid JSON" in {
        downstreamRetrieveBsasResponseJson.as[RetrieveSelfEmploymentBsasResponse] shouldBe retrieveBsasResponseModel
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid model" in {
        Json.toJson(retrieveBsasResponseModel) shouldBe mtdRetrieveBsasResponseJson
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
      hateoasFactory.wrap(retrieveBsasResponseModel, RetrieveSelfAssessmentBsasHateoasData(nino, calculationId)) shouldBe
        HateoasWrapper(
          retrieveBsasResponseModel,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$calculationId", GET, "self"),
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$calculationId/adjust", POST, "submit-summary-adjustments")
          )
        )
    }
  }

}
