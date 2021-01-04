/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.models.response.retrieveBsasAdjustments.ukProperty

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.fixtures.ukProperty.RetrieveUkPropertyAdjustmentsFixtures._
import v2.models.utils.JsonErrorValidators

class MetadataSpec extends UnitSpec with JsonErrorValidators {

  val desJson: JsValue = Json.parse(
    """{
      | "inputs": {
      |   "incomeSourceType" : "04",
      |   "accountingPeriodStartDate" : "2018-10-11",
      |   "accountingPeriodEndDate" : "2019-10-10"
      | },
      | "metadata": {
      |   "taxYear" : 2020,
      |   "calculationId" : "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |   "requestedDateTime" : "2019-10-14T11:33:27Z",
      |   "status" : "superseded"
      | },
      | "adjustedSummaryCalculation" : {
      |
      | }
      |}
    """.stripMargin)

  val mtdJson: JsValue = Json.parse(
    """{
      | "typeOfBusiness": "uk-property-fhl",
      |   "accountingPeriod": {
      |     "startDate": "2018-10-11",
      |     "endDate": "2019-10-10"
      |   },
      |   "taxYear": "2019-20",
      |   "requestedDateTime": "2019-10-14T11:33:27Z",
      |   "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |   "summaryStatus": "superseded",
      |   "adjustedSummary": true
      |}
    """.stripMargin)

  "Metadata" when {
    "reading from valid JSON" should {
      "return the appropriate model" in {
        desJson.as[Metadata] shouldBe metaDataModel
      }
    }

    "writing to valid json" should {
      "return valid json" in {
        metaDataModel.toJson shouldBe mtdJson
      }
    }
  }
}
