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

class RetrieveForeignPropertyAdjustmentsResponseSpec extends UnitSpec with JsonErrorValidators {

  val fhlDesJson: JsValue = Json.parse(
    """
      |{
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
      | "adjustments" : {
      |    "income": {
      |      "rentReceived": 100.49
      |    },
      |    "expenses" : {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "other": 100.49,
      |      "consolidatedExpenses": 100.49
      |    }
      | }
      |}
    """.stripMargin)

  val nonFhlDesJson: JsValue = Json.parse(
    """
      |{
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
      | },
      | "adjustments" : {
      |    "income": {
      |      "totalRentsReceived": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "otherPropertyIncome": 100.49,
      |      "foreignPropertyTaxTakenOff": 100.49
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

  val fhlMinimalDesJson: JsValue = Json.parse(
    """
      |{
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
      |  "adjustedSummaryCalculation" : {
      |
      | },
      | "adjustments" : {
      |
      | }
      |}
    """.stripMargin)

  val nonFhlMinimalDesJson: JsValue = Json.parse(
    """
      |{
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
      |  "adjustedSummaryCalculation" : {
      |
      | },
      | "adjustments" : {
      |
      | }
      |}
    """.stripMargin)

  val foreignPropertyFullMtdJson: JsValue = Json.parse(
    """
      |{
      | "metadata": {
      |      "typeOfBusiness": "foreign-property",
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
      |         "otherPropertyIncome": 100.49,
      |         "foreignTaxTakenOff": 100.49
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

  val foreignPropertyFhlEeaFullMtdJson: JsValue = Json.parse(
    """
      |{
      | "metadata": {
      |      "typeOfBusiness": "foreign-property-fhl-eea",
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
      |         "rentIncome": 100.49
      |      },
      |      "expenses": {
      |         "premisesRunningCosts": 100.49,
      |         "repairsAndMaintenance": 100.49,
      |         "financialCosts": 100.49,
      |         "professionalFees": 100.49,
      |         "travelCosts": 100.49,
      |         "costOfServices": 100.49,
      |         "other": 100.49,
      |         "consolidatedExpenses": 100.49
      |      }
      | }
      |}
    """.stripMargin)

  val foreignPropertyMinimalMtdJson: JsValue = Json.parse(
    """
      |{
      | "metadata": {
      |      "typeOfBusiness": "foreign-property",
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

  val foreignPropertyFhlEeaMinimalMtdJson: JsValue = Json.parse(
    """
      |{
      | "metadata": {
      |      "typeOfBusiness": "foreign-property-fhl-eea",
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
      "return the appropriate non-FHL model when the most data has been provided" in {
        nonFhlDesJson.as[RetrieveForeignPropertyAdjustmentsResponse] shouldBe foreignPropertyRetrieveForeignPropertyAdjustmentResponseModel
      }

      "return the appropriate FHL model when the most data has been provided" in {
        fhlDesJson.as[RetrieveForeignPropertyAdjustmentsResponse] shouldBe foreignPropertyFhlEeaRetrieveForeignPropertyAdjustmentResponseModel
      }

      "return the appropriate non-FHL model when the minimal data has been provided" in {
        nonFhlMinimalDesJson.as[RetrieveForeignPropertyAdjustmentsResponse] shouldBe foreignPropertyMinimalRetrieveForeignPropertyAdjustmentResponseModel
      }

      "return the appropriate FHL model when the minimal data has been provided" in {
        fhlMinimalDesJson.as[RetrieveForeignPropertyAdjustmentsResponse] shouldBe foreignPropertyFhlEeaMinimalRetrieveForeignPropertyAdjustmentResponseModel
      }
    }

    "writing to valid json" should {
      "return valid foreign property json with most data provided" in {
        foreignPropertyRetrieveForeignPropertyAdjustmentResponseModel.toJson shouldBe foreignPropertyFullMtdJson
      }

      "return valid foreign property fhl eea json with most data provided" in {
        foreignPropertyFhlEeaRetrieveForeignPropertyAdjustmentResponseModel.toJson shouldBe foreignPropertyFhlEeaFullMtdJson
      }

      "return valid foreign property Json when the minimal data has been provided" in {
        foreignPropertyMinimalRetrieveForeignPropertyAdjustmentResponseModel.toJson shouldBe foreignPropertyMinimalMtdJson
      }

      "return valid foreign property fhl eea Json when the minimal data has been provided" in {
        foreignPropertyFhlEeaMinimalRetrieveForeignPropertyAdjustmentResponseModel.toJson shouldBe foreignPropertyFhlEeaMinimalMtdJson
      }
    }
  }
}
