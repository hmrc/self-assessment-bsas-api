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

package v2.models.response.retrieveBsas.foreignProperty

import play.api.libs.json.Json
import support.UnitSpec
import v2.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import v2.models.utils.JsonErrorValidators

class RetrieveForeignPropertyBsasResponseSpec extends UnitSpec with JsonErrorValidators{

  val nonFhlMtdJson = Json.parse(
    """{
      |  "metadata": {
      |    "typeOfBusiness": "foreign-property",
      |    "accountingPeriod": {
      |      "startDate": "2020-10-11",
      |      "endDate": "2021-10-10"
      |    },
      |    "taxYear": "2021-22",
      |    "requestedDateTime": "2019-10-14T11:33:27Z",
      |    "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "summaryStatus": "valid",
      |    "adjustedSummary": true
      |  },
      |  "bsas": {
      |    "total": {
      |      "income": 100.49,
      |      "expenses": 100.49,
      |      "additions": 100.49,
      |      "deductions": 100.49
      |    },
      |    "profit": {
      |      "net": 100.49,
      |      "taxable": 100.49
      |    },
      |    "loss": {
      |      "net": 100.49,
      |      "adjustedIncomeTax": 100.49
      |    },
      |    "incomeBreakdown": {
      |      "rentIncome": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "otherPropertyIncome": 100.49,
      |      "foreignTaxTakenOff": 100.49,
      |      "specialWithholdingTaxOrUKTaxPaid": 100.49
      |    },
      |    "expensesBreakdown": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "residentialFinancialCost": 100.49,
      |      "broughtFwdResidentialFinancialCost": 100.49,
      |      "other": 100.49
      |    }
      |  }
      |}""".stripMargin
  )

  val fhlMtdJson = Json.parse(
    """{
      |  "metadata": {
      |    "typeOfBusiness": "foreign-property-fhl-eea",
      |    "accountingPeriod": {
      |      "startDate": "2020-10-11",
      |      "endDate": "2021-10-10"
      |    },
      |    "taxYear": "2021-22",
      |    "requestedDateTime": "2019-10-14T11:33:27Z",
      |    "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "summaryStatus": "valid",
      |    "adjustedSummary": true
      |  },
      |  "bsas": {
      |    "total": {
      |      "income": 100.49,
      |      "expenses": 100.49,
      |      "additions": 100.49,
      |      "deductions": 100.49
      |    },
      |    "profit": {
      |      "net": 100.49,
      |      "taxable": 100.49
      |    },
      |    "loss": {
      |      "net": 100.49,
      |      "adjustedIncomeTax": 100.49
      |    },
      |    "incomeBreakdown": {
      |      "rentIncome": 100.49
      |    },
      |    "expensesBreakdown": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "other": 100.49
      |    }
      |  }
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
      |  "adjustableSummaryCalculation": {
      |    "totalIncome": 100.49,
      |    "totalExpenses": 100.49,
      |    "totalAdditions": 100.49,
      |    "totalDeductions": 100.49,
      |    "accountingAdjustments": 100.49,
      |    "netProfit": 100.49,
      |    "taxableProfit": 100.49,
      |    "netLoss": 100.49,
      |    "adjustedIncomeTaxLoss": 100.49,
      |    "income": {
      |      "rentIncome": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "otherPropertyIncome": 100.49,
      |      "foreignTaxTakenOff": 100.49,
      |      "specialWithholdingTaxOrUKTaxPaid": 100.49
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "residentialFinancialCost": 100.49,
      |      "broughtFwdResidentialFinancialCost": 100.49,
      |      "other": 100.49
      |    }
      |  },
      |  "adjustedSummaryCalculation": {
      |    "totalIncome": 100.49,
      |    "totalExpenses": 100.49,
      |    "totalAdditions": 100.49,
      |    "totalDeductions": 100.49,
      |    "accountingAdjustments": 100.49,
      |    "netProfit": 100.49,
      |    "taxableProfit": 100.49,
      |    "netLoss": 100.49,
      |    "adjustedIncomeTaxLoss": 100.49,
      |    "income": {
      |      "rentIncome": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "otherPropertyIncome": 100.49,
      |      "foreignTaxTakenOff": 100.49,
      |      "specialWithholdingTaxOrUKTaxPaid": 100.49
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "residentialFinancialCost": 100.49,
      |      "broughtFwdResidentialFinancialCost": 100.49,
      |      "other": 100.49
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
      |  "adjustableSummaryCalculation": {
      |    "totalIncome": 100.49,
      |    "totalExpenses": 100.49,
      |    "totalAdditions": 100.49,
      |    "totalDeductions": 100.49,
      |    "accountingAdjustments": 100.49,
      |    "netProfit": 100.49,
      |    "taxableProfit": 100.49,
      |    "netLoss": 100.49,
      |    "adjustedIncomeTaxLoss": 100.49,
      |    "income": {
      |      "rentIncome": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "otherPropertyIncome": 100.49,
      |      "foreignTaxTakenOff": 100.49,
      |      "specialWithholdingTaxOrUKTaxPaid": 100.49
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "residentialFinancialCost": 100.49,
      |      "broughtFwdResidentialFinancialCost": 100.49,
      |      "other": 100.49
      |    }
      |  },
      |  "adjustedSummaryCalculation": {
      |    "totalIncome": 100.49,
      |    "totalExpenses": 100.49,
      |    "totalAdditions": 100.49,
      |    "totalDeductions": 100.49,
      |    "accountingAdjustments": 100.49,
      |    "netProfit": 100.49,
      |    "taxableProfit": 100.49,
      |    "netLoss": 100.49,
      |    "adjustedIncomeTaxLoss": 100.49,
      |    "income": {
      |      "rentIncome": 100.49
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "other": 100.49
      |    }
      |  }
      |}""".stripMargin
  )

  val adjustableSummaryCalculationNonFhlDesJson = Json.parse(
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
      |  "adjustableSummaryCalculation": {
      |    "totalIncome": 100.49,
      |    "totalExpenses": 100.49,
      |    "totalAdditions": 100.49,
      |    "totalDeductions": 100.49,
      |    "accountingAdjustments": 100.49,
      |    "netProfit": 100.49,
      |    "taxableProfit": 100.49,
      |    "netLoss": 100.49,
      |    "adjustedIncomeTaxLoss": 100.49,
      |    "income": {
      |      "rentIncome": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "otherPropertyIncome": 100.49,
      |      "foreignTaxTakenOff": 100.49,
      |      "specialWithholdingTaxOrUKTaxPaid": 100.49
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "residentialFinancialCost": 100.49,
      |      "broughtFwdResidentialFinancialCost": 100.49,
      |      "other": 100.49
      |    }
      |  }
      |}""".stripMargin
  )

  val adjustableSummaryCalculationFhlDesJson = Json.parse(
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
      |  "adjustableSummaryCalculation": {
      |    "totalIncome": 100.49,
      |    "totalExpenses": 100.49,
      |    "totalAdditions": 100.49,
      |    "totalDeductions": 100.49,
      |    "accountingAdjustments": 100.49,
      |    "netProfit": 100.49,
      |    "taxableProfit": 100.49,
      |    "netLoss": 100.49,
      |    "adjustedIncomeTaxLoss": 100.49,
      |    "income": {
      |      "rentIncome": 100.49
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "other": 100.49
      |    }
      |  }
      |}""".stripMargin
  )

  "reads" should {
    "return a valid model" when {
      "a valid non-fhl json with all fields are supplied" in {
        nonFhlDesJson.as[RetrieveForeignPropertyBsasResponse] shouldBe retrieveForeignPropertyBsasResponseModel
      }

      "a valid fhl json with all fields are supplied with" in {
        fhlDesJson.as[RetrieveForeignPropertyBsasResponse] shouldBe retrieveForeignPropertyFhlEeaBsasResponseModel
      }

      "a valid non-fhl json with all fields are supplied and no adjustedSummaryCalculation" in {
        adjustableSummaryCalculationNonFhlDesJson.as[RetrieveForeignPropertyBsasResponse] shouldBe
          retrieveForeignPropertyBsasResponseModel.copy(metadata = nonFhlMetaDataModel.copy(adjustedSummary = false))
      }

      "a valid fhl json with all fields are supplied and no adjustedSummaryCalculation" in {
        adjustableSummaryCalculationFhlDesJson.as[RetrieveForeignPropertyBsasResponse] shouldBe
          retrieveForeignPropertyFhlEeaBsasResponseModel.copy(metadata = fhlMetaDataModel.copy(adjustedSummary = false))
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid non-fhl model is supplied" in {
        retrieveForeignPropertyBsasResponseModel.toJson shouldBe nonFhlMtdJson
      }

      "a valid fhl model is supplied" in {
        retrieveForeignPropertyFhlEeaBsasResponseModel.toJson shouldBe fhlMtdJson
      }
    }
  }
}