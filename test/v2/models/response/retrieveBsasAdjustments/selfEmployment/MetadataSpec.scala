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

package v2.models.response.retrieveBsasAdjustments.selfEmployment

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.fixtures.selfEmployment.RetrieveSelfEmploymentAdjustmentsFixtures._
import v2.models.utils.JsonErrorValidators

class MetadataSpec extends UnitSpec with JsonErrorValidators {

  val desJson: JsValue = Json.parse(
    """{
      | "inputs": {
      |   "incomeSourceType" : "01",
      |   "incomeSourceId" : "000000000000210",
      |   "accountingPeriodStartDate" : "2018-10-11",
      |   "accountingPeriodEndDate" : "2019-10-10"
      | },
      | "metadata": {
      |   "taxYear" : 2020,
      |   "calculationId" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |   "requestedDateTime" : "2019-10-14T11:33:27Z",
      |   "status" : "superseded"
      | }
      |}
    """.stripMargin)

  val mtdJson: JsValue = Json.parse(
    """{
      | "typeOfBusiness": "self-employment",
      |   "businessId": "000000000000210",
      |   "accountingPeriod": {
      |     "startDate": "2018-10-11",
      |     "endDate": "2019-10-10"
      |   },
      |   "taxYear": "2019-20",
      |   "requestedDateTime": "2019-10-14T11:33:27Z",
      |   "bsasId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
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
