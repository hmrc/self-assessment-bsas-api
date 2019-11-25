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

package v1.models.response.common

import play.api.libs.json.{JsSuccess, JsValue, Json}
import support.UnitSpec

class BSASEntriesSpec extends UnitSpec{

  val json: JsValue = Json.parse(
    """
      |{
      | "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      | "requestedDateTime": "2019-10-14T11:33:27Z",
      | "summaryStatus": "valid",
      | "adjustedSummary": false
      | }
      |""".stripMargin
  )

  val desJson: JsValue = Json.parse (
    """
      |{
      | "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      | "requestedDateTime": "2019-10-14T11:33:27Z",
      | "status": "valid",
      | "adjusted": false
      | }
      |""".stripMargin
  )

  val model =
    BSASEntries(
      bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      requestedDateTime = "2019-10-14T11:33:27Z",
      summaryStatus = "valid",
      adjustedSummary = false
      )

  "BSAS Entries" should {

    "write correctly to json" in {
      Json.toJson(model) shouldBe json
    }

    "read correctly to json" in {
      desJson.validate[BSASEntries] shouldBe JsSuccess(model)
    }
  }
}
