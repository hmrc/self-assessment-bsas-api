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

package v1.models.response.retrieveBsas.ukProperty

import support.UnitSpec
import v1.fixtures.RetrieveUkPropertyBsasFixtures._
import v1.models.utils.JsonErrorValidators

class RetrieveUkPropertyBsasResponseSpec extends UnitSpec with JsonErrorValidators{

  "reads" should {
    "return a valid model" when {
      "a valid json is supplied" in {
        desRetrieveBsasResponse.as[RetrieveUkPropertyBsasResponse] shouldBe retrieveUkPropertyBsasResponseModel
      }

      "a valid json with out adjusted summary is supplied" in {
        desRetrieveBsasResponseWithAdjustableSummary.as[RetrieveUkPropertyBsasResponse] shouldBe
          retrieveUkPropertyBsasResponseModel.copy(metadata = metadataModelWithAdjustableSummary)
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        retrieveUkPropertyBsasResponseModel.toJson shouldBe mtdResponse
      }
    }
  }
}
