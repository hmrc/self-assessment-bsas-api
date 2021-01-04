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

package v2.models.response.retrieveBsasAdjustments.foreignProperty

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.fixtures.foreignProperty.RetrieveForeignPropertyAdjustmentsFixtures._
import v2.models.utils.JsonErrorValidators

class MetadataSpec extends UnitSpec with JsonErrorValidators {

  val foreignPropertyFhlEeaDesJson: JsValue = Json.parse(
    """{
      | "inputs": {
      |   "incomeSourceType" : "03",
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

  val foreignPropertyDesJson: JsValue = Json.parse(
    """{
      | "inputs": {
      |   "incomeSourceType" : "15",
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

  val foreignPropertyFhlEeaMtdJson: JsValue = Json.parse(
    """{
      | "typeOfBusiness": "foreign-property-fhl-eea",
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

  val foreignPropertyMtdJson: JsValue = Json.parse(
    """{
      | "typeOfBusiness": "foreign-property",
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
      "return the appropriate foreign property fhl eea meta data model" in {
        foreignPropertyFhlEeaDesJson.as[Metadata] shouldBe foreignPropertyFhlEeaMetaDataModel
      }

      "return the appropriate foreign property meta data model" in {
        foreignPropertyDesJson.as[Metadata] shouldBe foreignPropertyMetaDataModel
      }
    }

    "writing to valid json" should {
      "return valid foreign property fhl eea meta data json" in {
        foreignPropertyFhlEeaMetaDataModel.toJson shouldBe foreignPropertyFhlEeaMtdJson
      }

      "return valid foreign property meta data json" in {
        foreignPropertyMetaDataModel.toJson shouldBe foreignPropertyMtdJson
      }
    }
  }
}
