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

package v1.models.response.retrieveBsasAdjustments.ukProperty

import play.api.libs.json.Json
import support.UnitSpec
import v1.models.utils.JsonErrorValidators
import v1.fixtures.RetrieveBsasUKPropertyAdjustmentsFixtures._

class RetrieveUKPropertyAdjustmentsSpec extends UnitSpec with JsonErrorValidators {

  val fullDesJson = Json.parse(
    """
      |{
      | "inputs": {
      |   "incomeSourceType" : "04",
      |   "accountingPeriodStartDate" : "2018-10-11",
      |   "accountingPeriodEndDate" : "2019-10-10"
      | },
      | "metadata": {
      |   "taxYear" : "2020",
      |   "calculationId" : "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |   "requestedDateTime" : "2019-10-14T11:33:27Z",
      |   "status" : "superseded"
      | },
      | "adjustedSummaryCalculation" : {
      |
      | },
      | "adjustments" : {
      |    "incomes": {
      |      "rentReceived": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "reversePremums": 100.49,
      |      "otherPropertyIncome": 100.49
      |    },
      |    "expenses" : {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "residentialFinancialCost" : 100.49,
      |      "other": 100.49,
      |      "consolidatedExpenses": 100.49
      |    }
      | }
      |}
    """.stripMargin)

  val minimalDesJson = Json.parse(
    """
      |{
      | "inputs": {
      |   "incomeSourceType" : "04",
      |   "accountingPeriodStartDate" : "2018-10-11",
      |   "accountingPeriodEndDate" : "2019-10-10"
      | },
      | "metadata": {
      |   "taxYear" : "2020",
      |   "calculationId" : "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |   "requestedDateTime" : "2019-10-14T11:33:27Z",
      |   "status" : "superseded"
      | },
      |  "adjustedSummaryCalculation" : {
      |
      | },
      | "adjustments" : {
      |
      | }
      |}
    """.stripMargin)

  val fullMtdJson = Json.parse(
    """
      |{
      | "metadata": {
      |      "typeOfBusiness": "uk-property-fhl",
      |      "accountingPeriod": {
      |         "startDate": "2018-10-11",
      |         "endDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2019-10-14T11:33:27Z",
      |      "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "summaryStatus": "superseded",
      |      "adjustedSummary": true
      | },
      | "adjustments": {
      |      "incomes": {
      |         "rentIncome": 100.49,
      |         "premiumsOfLeaseGrant": 100.49,
      |         "reversePremiums": 100.49,
      |         "otherPropertyIncome": 100.49
      |      },
      |      "expenses": {
      |         "premisesRunningCosts": 100.49,
      |         "repairsAndMaintenance": 100.49,
      |         "financialCosts": 100.49,
      |         "professionalFees": 100.49,
      |         "travelCosts": 100.49,
      |         "costOfServices": 100.49,
      |         "residentialFinancialCost": 100.49,
      |         "other": 100.49,
      |         "consolidatedExpenses": 100.49
      |      }
      | }
      |}
    """.stripMargin)

  val minimalMtdJson = Json.parse(
    """
      |{
      | "metadata": {
      |      "typeOfBusiness": "uk-property-fhl",
      |      "accountingPeriod": {
      |         "startDate": "2018-10-11",
      |         "endDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2019-10-14T11:33:27Z",
      |      "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "summaryStatus": "superseded",
      |      "adjustedSummary": true
      | },
      | "adjustments": {}
      |}
    """.stripMargin)

  "RetrieveSelfEmploymentAdjustmentResponse" when {
    "reading from valid JSON" should {
      "return the appropriate model when the most data has been provided" in {

        fullDesJson.as[RetrieveUKPropertyAdjustments] shouldBe retrieveUKPropertyAdjustmentResponseModel
      }

      "return the appropriate model when the minimal data has been provided" in {

        minimalDesJson.as[RetrieveUKPropertyAdjustments] shouldBe minimalRetrieveUKPropertyAdjustmentResponseModel
      }
    }

    "writing to valid json" should {
      "return valid json with most data provided" in {

        retrieveUKPropertyAdjustmentResponseModel.toJson shouldBe fullMtdJson
      }

      "return valid Json when the minimal data has been provided" in {

        minimalRetrieveUKPropertyAdjustmentResponseModel.toJson shouldBe minimalMtdJson
      }
    }
  }

}
