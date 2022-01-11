/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.models.response.retrieveBsas.foreignProperty

import v2.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import play.api.libs.json.Json
import support.UnitSpec
import v2.models.utils.JsonErrorValidators

class MetadataSpec extends UnitSpec with JsonErrorValidators{

  val nonFhlMtdJson = Json.parse(
    """{
      |  "typeOfBusiness": "foreign-property",
      |  "accountingPeriod": {
      |    "startDate": "2020-10-11",
      |    "endDate": "2021-10-10"
      |  },
      |  "taxYear": "2021-22",
      |  "requestedDateTime": "2019-10-14T11:33:27Z",
      |  "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "summaryStatus": "valid",
      |  "adjustedSummary": true
      |}""".stripMargin
  )

  val fhlMtdJson = Json.parse(
    """{
      |  "typeOfBusiness": "foreign-property-fhl-eea",
      |  "accountingPeriod": {
      |    "startDate": "2020-10-11",
      |    "endDate": "2021-10-10"
      |  },
      |  "taxYear": "2021-22",
      |  "requestedDateTime": "2019-10-14T11:33:27Z",
      |  "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "summaryStatus": "valid",
      |  "adjustedSummary": true
      |}""".stripMargin
  )

  val nonFhlDesJson = Json.parse(
    """{
      |  "inputs": {
      |    "incomeSourceType": "15",
      |    "incomeSourceId": "XAIS12345678910",
      |    "accountingPeriodStartDate": "2020-10-11",
      |    "accountingPeriodEndDate": "2021-10-10"
      |  },
      |  "metadata": {
      |    "taxYear": 2022,
      |    "requestedDateTime": "2019-10-14T11:33:27Z",
      |    "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "status": "valid"
      |  },
      |  "adjustedSummaryCalculation": {
      |    "totalIncome": 123.12,
      |    "totalExpenses": 123.12,
      |    "totalAdditions": 123.12,
      |    "totalDeductions": 123.12,
      |    "accountingAdjustments": 123.12,
      |    "netProfit": 123.12,
      |    "taxableProfit": 123.12,
      |    "netLoss": 123.12,
      |    "adjustedIncomeTaxLoss": 123.12,
      |    "income": {
      |      "rentIncome": 123.12,
      |      "premiumsOfLeaseGrant": 123.12,
      |      "otherPropertyIncome": 123.12,
      |      "foreignTaxTakenOff": 123.12,
      |      "specialWithholdingTaxOrUKTaxPaid": 123.12
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 123.12,
      |      "repairsAndMaintenance": 123.12,
      |      "financialCosts": 123.12,
      |      "professionalFees": 123.12,
      |      "travelCosts": 123.12,
      |      "costOfServices": 123.12,
      |      "residentialFinancialCost": 123.12,
      |      "broughtFwdResidentialFinancialCost": 123.12,
      |      "other": 123.12
      |    }
      |  }
      |}""".stripMargin
  )

  val fhlDesJson = Json.parse(
    """{
      |  "inputs": {
      |    "incomeSourceType": "03",
      |    "incomeSourceId": "XAIS12345678910",
      |    "accountingPeriodStartDate": "2020-10-11",
      |    "accountingPeriodEndDate": "2021-10-10"
      |  },
      |  "metadata": {
      |    "taxYear": 2022,
      |    "requestedDateTime": "2019-10-14T11:33:27Z",
      |    "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "status": "valid"
      |  },
      |  "adjustedSummaryCalculation": {
      |    "totalIncome": 123.12,
      |    "totalExpenses": 123.12,
      |    "totalAdditions": 123.12,
      |    "totalDeductions": 123.12,
      |    "accountingAdjustments": 123.12,
      |    "netProfit": 123.12,
      |    "taxableProfit": 123.12,
      |    "netLoss": 123.12,
      |    "adjustedIncomeTaxLoss": 123.12,
      |    "income": {
      |      "rentIncome": 123.12
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 123.12,
      |      "repairsAndMaintenance": 123.12,
      |      "financialCosts": 123.12,
      |      "professionalFees": 123.12,
      |      "travelCosts": 123.12,
      |      "costOfServices": 123.12,
      |      "other": 123.12
      |    }
      |  }
      |}""".stripMargin
  )

  val adjustedSummaryFalseDesJson = Json.parse(
    """{
      |  "inputs": {
      |    "incomeSourceType": "03",
      |    "incomeSourceId": "XAIS12345678910",
      |    "accountingPeriodStartDate": "2020-10-11",
      |    "accountingPeriodEndDate": "2021-10-10"
      |  },
      |  "metadata": {
      |    "taxYear": 2022,
      |    "requestedDateTime": "2019-10-14T11:33:27Z",
      |    "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "status": "valid"
      |  }
      |}""".stripMargin
  )

  "reads" should {
    "return a valid model" when {
      "a valid non-fhl json with all fields are supplied" in {
        nonFhlDesJson.as[Metadata] shouldBe nonFhlMetaDataModel
      }

      "a valid fhl json with all fields are supplied" in {
        fhlDesJson.as[Metadata] shouldBe fhlMetaDataModel
      }

      "a valid fhl json with no adjustedSummary is supplied" in {
        adjustedSummaryFalseDesJson.as[Metadata] shouldBe fhlMetaDataModel.copy(adjustedSummary = false)
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid non-fhl model is supplied" in {
        nonFhlMetaDataModel.toJson shouldBe nonFhlMtdJson
      }

      "a valid fhl model is supplied" in {
        fhlMetaDataModel.toJson shouldBe fhlMtdJson
      }
    }
  }
}
