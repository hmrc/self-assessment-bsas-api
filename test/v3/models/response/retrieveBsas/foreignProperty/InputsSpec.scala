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

import support.UnitSpec
import v3.models.utils.JsonErrorValidators
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._


class InputsSpec extends UnitSpec with JsonErrorValidators{

  "reads" should {
    "return a valid inputs model" when {
      "a valid json with all fields are supplied" in {
        inputsDesJson.as[Inputs] shouldBe inputsModel
      }

      "a valid json with no businessName is supplied" in {
        inputsDesJsonWithoutBusinessName.as[Inputs] shouldBe inputsModel.copy(businessName = None)
      }
    }
  }

  "writes" should {
    "return a valid metadata json" when {
      "a valid model is supplied" in {
        inputsModel.toJson shouldBe inputsMtdJson
      }
    }
  }
}