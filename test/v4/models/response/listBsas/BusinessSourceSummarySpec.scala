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

package v4.models.response.listBsas

import play.api.libs.json.{JsError, JsObject, Json}
import shared.utils.UnitSpec
import v4.fixtures.ListBsasFixture
import v4.models.response.listBsas.{BsasSummary, BusinessSourceSummary}

class BusinessSourceSummarySpec extends UnitSpec with ListBsasFixture {

  "BusinessSourceSummary" when {
    "read from valid JSON" should {
      "return the expected model" in {
        businessSourceSummaryDownstreamJson.as[BusinessSourceSummary[BsasSummary]] shouldBe businessSourceSummaryModel()
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        JsObject.empty.validate[BusinessSourceSummary[BsasSummary]] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JSON" in {
        Json.toJson(businessSourceSummaryModel()) shouldBe businessSourceSummaryJson
      }
    }
  }

}