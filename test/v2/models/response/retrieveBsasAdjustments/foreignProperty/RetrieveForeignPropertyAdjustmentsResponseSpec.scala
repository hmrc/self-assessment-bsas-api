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
    """{
      |    "metadata": {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |        "requestedDateTime": "2019-10-14T11:33:27Z",
      |        "taxableEntityId": "AA1234567A",
      |        "taxYear": 2020,
      |        "status": "superseded"
      |    },
      |    "inputs": {
      |        "incomeSourceId": "XAIS123456789012",
      |        "incomeSourceType": "03",
      |        "accountingPeriodStartDate": "2018-10-11",
      |        "accountingPeriodEndDate": "2019-10-10",
      |        "source": "MTD-SA",
      |        "submissionPeriods": [
      |            {
      |                "periodId": "0000000000000000",
      |                "startDate": "2020-01-01",
      |                "endDate": "2021-10-10",
      |                "receivedDateTime": "2020-01-01T10:12:10Z"
      |            }
      |        ]
      |    },
      |    "adjustments": {
      |        "income": {
      |            "rent": 100.49
      |        },
      |        "expenses": {
      |            "repairsAndMaintenance": 100.49,
      |            "financialCosts": 100.49,
      |            "professionalFees": 100.49,
      |            "costOfServices": 100.49,
      |            "travelCosts": 100.49,
      |            "residentialFinancialCost": 100.49,
      |            "other": 100.49,
      |            "premisesRunningCosts": 100.49,
      |            "consolidatedExpenses":100.49
      |        }
      |    }
      |}
    """.stripMargin)

  val nonFhlDesJson: JsValue = Json.parse(
    """
      |{
      |  "metadata":{
      |    "calculationId":"717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "requestedDateTime":"2019-10-14T12:00:22Z",
      |    "adjustedDateTime":"2021-01-21T12:00:22Z",
      |    "taxableEntityId": "AA1234567A",
      |    "taxYear":2020,
      |    "status":"superseded"
      |  },
      |  "inputs":{
      |    "incomeSourceId":"XAIS123456789012",
      |    "incomeSourceType":"15",
      |    "incomeSourceName":"string",
      |    "accountingPeriodStartDate":"2018-10-11",
      |    "accountingPeriodEndDate":"2019-10-10",
      |    "source":"MTD-SA",
      |    "submissionPeriods":[
      |      {
      |        "periodId":"2019040620190405",
      |        "startDate":"2019-04-06",
      |        "endDate":"2020-04-05",
      |        "receivedDateTime":"2019-02-15T09:35:04.843Z"
      |      }
      |    ]
      |  },
      |  "adjustments":[
      |    {
      |      "countryCode": "FRA",
      |      "income":{
      |        "rent":100.49,
      |        "premiumsOfLeaseGrant":100.49,
      |        "otherPropertyIncome":100.49
      |      },
      |      "expenses":{
      |        "premisesRunningCosts":100.49,
      |        "repairsAndMaintenance":100.49,
      |        "financialCosts":100.49,
      |        "professionalFees":100.49,
      |        "travelCosts":100.49,
      |        "costOfServices":100.49,
      |        "residentialFinancialCost":100.49,
      |        "other":100.49,
      |        "consolidatedExpenses":100.49
      |      }
      |    }
      |  ]
      |}
    """.stripMargin)

  val fhlMinimalDesJson: JsValue = Json.parse(
    """
      |{
      | "inputs": {
      |   "incomeSourceId":"XAIS123456789012",
      |   "incomeSourceType" : "03",
      |   "accountingPeriodStartDate" : "2018-10-11",
      |   "accountingPeriodEndDate" : "2019-10-10"
      | },
      | "metadata": {
      |   "taxYear" : 2020,
      |   "calculationId" : "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |   "requestedDateTime" : "2019-10-14T11:33:27Z",
      |   "taxableEntityId": "AA1234567A",
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
      |   "incomeSourceId":"XAIS123456789012",
      |   "incomeSourceType" : "15",
      |   "accountingPeriodStartDate" : "2018-10-11",
      |   "accountingPeriodEndDate" : "2019-10-10"
      | },
      | "metadata": {
      |   "taxYear" : 2020,
      |   "calculationId" : "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |   "requestedDateTime" : "2019-10-14T12:00:22Z",
      |   "taxableEntityId": "AA1234567A",
      |   "status" : "superseded"
      | },
      |  "adjustedSummaryCalculation" : {
      |
      | },
      | "adjustments" : [{
      |     "countryCode": "FRA"
      |   }
      | ]
      |}
    """.stripMargin)

  val foreignPropertyFullMtdJson: JsValue = Json.parse(
    """
      |{
      |   "metadata": {
      |      "typeOfBusiness": "foreign-property",
      |      "businessId": "XAIS123456789012",
      |      "accountingPeriod": {
      |         "startDate": "2018-10-11",
      |         "endDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2019-10-14T12:00:22Z",
      |      "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "summaryStatus": "superseded",
      |      "adjustedSummary": true
      |   },
      |   "adjustments": [
      |      {
      |         "countryCode": "FRA",
      |         "incomes": {
      |            "rentIncome": 100.49,
      |            "premiumsOfLeaseGrant": 100.49,
      |            "otherPropertyIncome": 100.49
      |         },
      |         "expenses": {
      |            "premisesRunningCosts": 100.49,
      |            "repairsAndMaintenance": 100.49,
      |            "financialCosts": 100.49,
      |            "professionalFees": 100.49,
      |            "travelCosts": 100.49,
      |            "costOfServices": 100.49,
      |            "residentialFinancialCost": 100.49,
      |            "other": 100.49,
      |            "consolidatedExpenses":100.49
      |         }
      |      }]
      |}
    """.stripMargin)

  val foreignPropertyFhlEeaFullMtdJson: JsValue = Json.parse(
    """
      |{
      |   "metadata": {
      |      "typeOfBusiness": "foreign-property-fhl-eea",
      |      "businessId": "XAIS123456789012",
      |      "accountingPeriod": {
      |         "startDate": "2018-10-11",
      |         "endDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2019-10-14T11:33:27Z",
      |      "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "summaryStatus": "superseded",
      |      "adjustedSummary": true
      |   },
      |   "adjustments": [{
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
      |         "consolidatedExpenses":100.49
      |      }
      |   }]
      |}
    """.stripMargin)

  val foreignPropertyMinimalMtdJson: JsValue = Json.parse(
    """
      |{
      | "metadata": {
      |      "typeOfBusiness": "foreign-property",
      |      "businessId": "XAIS123456789012",
      |      "accountingPeriod": {
      |         "startDate": "2018-10-11",
      |         "endDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2019-10-14T12:00:22Z",
      |      "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "summaryStatus": "superseded",
      |      "adjustedSummary": true
      | },
      | "adjustments": [{
      |   "countryCode":"FRA"
      | }]
      |}
    """.stripMargin)

  val foreignPropertyFhlEeaMinimalMtdJson: JsValue = Json.parse(
    """
      |{
      | "metadata": {
      |      "typeOfBusiness": "foreign-property-fhl-eea",
      |      "businessId": "XAIS123456789012",
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
      | "adjustments": [{}]
      |}
    """.stripMargin)

  "RetrieveForeignPropertyAdjustmentResponse" when {
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
