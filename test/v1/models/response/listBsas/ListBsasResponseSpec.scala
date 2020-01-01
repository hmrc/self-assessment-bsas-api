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

package v1.models.response.listBsas

import play.api.libs.json.{JsSuccess, Json}
import support.UnitSpec
import v1.fixtures.ListBsasFixtures._

class ListBsasResponseSpec extends UnitSpec {

  "BusinessSourceSummaries" should {

    "write correctly to json" in {

      Json.toJson(summaryModel) shouldBe summariesJSON
    }

    "read correctly to json" in {
      summariesFromDesJSONSingle.validate[ListBsasResponse[BsasEntries]] shouldBe JsSuccess(summaryModel)
    }
  }
}
