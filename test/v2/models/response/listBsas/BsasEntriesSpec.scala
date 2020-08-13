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

package v2.models.response.listBsas

import play.api.libs.json.{JsSuccess, Json}
import support.UnitSpec
import v2.fixtures.ListBsasFixtures._
import v2.models.domain.Status

class BsasEntriesSpec extends UnitSpec{

  val model =
    BsasEntries(
      bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      requestedDateTime = "2019-10-14T11:33:27Z",
      summaryStatus = Status.`valid`,
      adjustedSummary = false
      )

  "BSAS Entries" should {

    "write correctly to json" in {
      Json.toJson(model) shouldBe bsasEntriesJSON
    }

    "read correctly to json" in {
      bsasEntriesFromDesJSON.validate[BsasEntries] shouldBe JsSuccess(model)
    }
  }
}
