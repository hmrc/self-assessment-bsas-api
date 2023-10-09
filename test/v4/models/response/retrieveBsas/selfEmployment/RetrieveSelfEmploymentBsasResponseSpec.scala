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

package v4.models.response.retrieveBsas.selfEmployment

import api.hateoas.Method._
import api.hateoas.{HateoasFactory, HateoasWrapper, Link}
import api.models.domain.TaxYear
import api.models.utils.JsonErrorValidators
import config.MockAppConfig
import play.api.Configuration
import play.api.libs.json.Json
import support.UnitSpec
import v4.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures.{
  downstreamRetrieveBsasResponseJson,
  mtdRetrieveBsasResponseJson,
  retrieveBsasResponseModel
}

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
      val context        = "individuals/self-assessment/adjustable-summary"
      val taxYear        = Some(TaxYear.fromMtd("2023-24"))

      MockedAppConfig.apiGatewayContext.returns(context).anyNumberOfTimes()
    }

    class TysDisabledTest extends Test {
      MockedAppConfig.featureSwitches.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()
    }

    class TysEnabledTest extends Test {
      MockedAppConfig.featureSwitches.returns(Configuration("tys-api.enabled" -> true)).anyNumberOfTimes()
    }

    "return the correct links without tax year" in new TysDisabledTest {
      private val result = hateoasFactory.wrap(retrieveBsasResponseModel, RetrieveSelfAssessmentBsasHateoasData(nino, calculationId, None))
      private val expectedLinks = Seq(
        Link(s"/$context/$nino/self-employment/$calculationId", GET, "self"),
        Link(s"/$context/$nino/self-employment/$calculationId/adjust", POST, "submit-self-employment-accounting-adjustments")
      )

      result shouldBe HateoasWrapper(retrieveBsasResponseModel, expectedLinks)
    }

    "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
      private val result = hateoasFactory.wrap(retrieveBsasResponseModel, RetrieveSelfAssessmentBsasHateoasData(nino, calculationId, taxYear))
      private val expectedLinks = Seq(
        Link(s"/$context/$nino/self-employment/$calculationId?taxYear=2023-24", GET, "self"),
        Link(s"/$context/$nino/self-employment/$calculationId/adjust?taxYear=2023-24", POST, "submit-self-employment-accounting-adjustments")
      )

      result shouldBe HateoasWrapper(retrieveBsasResponseModel, expectedLinks)
    }
  }

}
