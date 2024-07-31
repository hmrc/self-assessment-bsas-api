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

package v6.bsas.list.def2.model.response

import play.api.libs.json.{JsError, JsObject, Json}
import shared.utils.UnitSpec
import v6.bsas.list.def2.model.Def2_ListBsasFixtures

class Def2_BsasSummarySpec extends UnitSpec with Def2_ListBsasFixtures {

  "BsasSummary" when {
    "read from valid JSON" should {
      "return the expected data object" in {
        val result = bsasSummaryDownstreamJson.as[Def2_BsasSummary]
        result shouldBe bsasSummary
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        val result = JsObject.empty.validate[Def2_BsasSummary]
        result shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JSON" in {
        val result = Json.toJson(bsasSummary)
        result shouldBe bsasSummaryJson
      }
    }
  }

}
