/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.foreignPropertyBsas.retrieve.def3.model.response

import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v7.foreignPropertyBsas.retrieve.def3.model.response.RetrieveForeignPropertyBsasBodyFixtures._

class MetadataSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return the expected parsed object" when {
      "given a valid json object with all fields" in {
        metadataHipJson.as[Metadata] shouldBe parsedMetadata
      }
    }
  }

  "writes" should {
    "return the expected json" when {
      "given a valid data object" in {
        parsedMetadata.toJson shouldBe metadataMtdJson
      }
    }
  }

}
