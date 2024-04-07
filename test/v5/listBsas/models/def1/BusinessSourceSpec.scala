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

package v5.listBsas.models.def1

import play.api.libs.json.{JsError, JsObject, Json}
import shared.UnitSpec
import v5.listBsas.fixtures.def1.ListBsasFixture

class BusinessSourceSpec extends UnitSpec with ListBsasFixture {

  "BusinessSourceSummary" when {
    "read from valid JSON" should {
      "return the expected model" in {
        businessSourceSummaryDownstreamJson.as[BusinessSource[Def1_BsasSummary]] shouldBe businessSourceSummaryModel()
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        JsObject.empty.validate[BusinessSource[Def1_BsasSummary]] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JSON" in {
        Json.toJson(businessSourceSummaryModel()) shouldBe businessSourceSummaryJson
      }
    }
  }

}
