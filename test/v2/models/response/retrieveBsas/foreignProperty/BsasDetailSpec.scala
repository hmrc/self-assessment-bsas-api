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

class BsasDetailSpec extends UnitSpec with JsonErrorValidators {

  val nonFhlMtdJson = Json.parse(
    """{
      |  "total": {
      |    "income": 100.49,
      |    "expenses": 100.49,
      |    "additions": 100.49,
      |    "deductions": 100.49
      |  },
      |  "profit": {
      |    "net": 100.49,
      |    "taxable": 100
      |  },
      |  "loss": {
      |    "net": 100.49,
      |    "adjustedIncomeTax": 100
      |  },
      |  "incomeBreakdown": {
      |    "rentIncome": 100.49,
      |    "premiumsOfLeaseGrant": 100.49,
      |    "otherPropertyIncome": 100.49
      |  },
      |  "expensesBreakdown": {
      |    "premisesRunningCosts": 100.49,
      |    "repairsAndMaintenance": 100.49,
      |    "financialCosts": 100.49,
      |    "professionalFees": 100.49,
      |    "travelCosts": 100.49,
      |    "costOfServices": 100.49,
      |    "residentialFinancialCost": 100.49,
      |    "broughtFwdResidentialFinancialCost": 100.49,
      |    "other": 100.49
      |  },
      |  "countryLevelDetail":[
      |  {
      |    "countryCode": "FRA",
      |    "total": {
      |      "income": 100.49,
      |      "expenses": 100.49,
      |      "additions": 100.49,
      |      "deductions": 100.49
      |    },
      |    "incomeBreakdown": {
      |      "rentIncome": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "otherPropertyIncome": 100.49
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
      |      }
      |   }
      |  ]
      |}""".stripMargin
  )

  val fhlMtdJson = Json.parse(
    """{
      |  "total": {
      |    "income": 100.49,
      |    "expenses": 100.49,
      |    "additions": 100.49,
      |    "deductions": 100.49
      |  },
      |  "profit": {
      |    "net": 100.49,
      |    "taxable": 100
      |  },
      |  "loss": {
      |    "net": 100.49,
      |    "adjustedIncomeTax": 100
      |  },
      |  "incomeBreakdown": {
      |    "rentIncome": 100.49
      |  },
      |  "expensesBreakdown": {
      |    "premisesRunningCosts": 100.49,
      |    "repairsAndMaintenance": 100.49,
      |    "financialCosts": 100.49,
      |    "professionalFees": 100.49,
      |    "travelCosts": 100.49,
      |    "costOfServices": 100.49,
      |    "other": 100.49
      |  },
      |  "countryLevelDetail": [
      |  {
      |    "countryCode":"FRA",
      |    "total": {
      |      "income":100.49,
      |      "expenses":100.49,
      |      "additions":100.49,
      |      "deductions":100.49
      |      },
      |    "incomeBreakdown": {
      |      "rentIncome":100.49
      |      },
      |    "expensesBreakdown": {
      |      "premisesRunningCosts":100.49,
      |      "repairsAndMaintenance":100.49,
      |      "financialCosts":100.49,
      |      "professionalFees":100.49,
      |      "travelCosts":100.49,
      |      "costOfServices":100.49,
      |      "other":100.49
      |    }
      |   }
      | ]
      |}""".stripMargin
  )

  val nonFhlDesJson = Json.parse(
    """{
      |  "totalIncome": 100.49,
      |  "totalExpenses": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "accountingAdjustments": 100.49,
      |  "netProfit": 100.49,
      |  "taxableProfit": 100,
      |  "netLoss": 100.49,
      |  "adjustedIncomeTaxLoss": 100,
      |  "income": {
      |    "rent": 100.49,
      |    "premiumsOfLeaseGrant": 100.49,
      |    "otherPropertyIncome": 100.49,
      |    "foreignTaxTakenOff": 100.49,
      |    "specialWithholdingTaxOrUKTaxPaid": 100.49
      |  },
      |  "expenses": {
      |    "premisesRunningCosts": 100.49,
      |    "repairsAndMaintenance": 100.49,
      |    "financialCosts": 100.49,
      |    "professionalFees": 100.49,
      |    "travelCosts": 100.49,
      |    "costOfServices": 100.49,
      |    "residentialFinancialCost": 100.49,
      |    "broughtFwdResidentialFinancialCost": 100.49,
      |    "other": 100.49
      |  },
      |  "countryLevelDetail":[
      |  {
      |    "countryCode": "FRA",
      |    "total": {
      |      "totalIncome": 100.49,
      |      "totalExpenses": 100.49,
      |      "totalAdditions": 100.49,
      |      "totalDeductions": 100.49
      |    },
      |    "income": {
      |      "rent": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "otherPropertyIncome": 100.49
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
      | ]
      |}""".stripMargin
  )

  val fhlDesJson = Json.parse(
    """{
      |  "totalIncome": 100.49,
      |  "totalExpenses": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "accountingAdjustments": 100.49,
      |  "netProfit": 100.49,
      |  "taxableProfit": 100,
      |  "netLoss": 100.49,
      |  "adjustedIncomeTaxLoss": 100,
      |  "income": {
      |    "rent": 100.49
      |  },
      |  "expenses": {
      |    "premisesRunningCosts": 100.49,
      |    "repairsAndMaintenance": 100.49,
      |    "financialCosts": 100.49,
      |    "professionalFees": 100.49,
      |    "travelCosts": 100.49,
      |    "costOfServices": 100.49,
      |    "other": 100.49
      |  },
      |  "total": {
      |    "income": 100.49,
      |    "expenses": 100.49,
      |    "additions": 100.49,
      |    "deductions": 100.49
      |  },
      |  "profit": {
      |    "net": 100.49,
      |    "taxable": 100.49
      |  },
      |  "loss": {
      |    "net": 100.49,
      |    "adjustedIncomeTax": 100.49
      |  },
      |  "income": {
      |    "rent": 100.49
      |  },
      |  "expenses": {
      |    "premisesRunningCosts": 100.49,
      |    "repairsAndMaintenance": 100.49,
      |    "financialCosts": 100.49,
      |    "professionalFees": 100.49,
      |    "travelCosts": 100.49,
      |    "costOfServices": 100.49,
      |    "other": 100.49
      |  },
      |  "countryLevelDetail": [
      |  {
      |    "countryCode":"FRA",
      |    "total": {
      |      "totalIncome": 100.49,
      |      "totalExpenses": 100.49,
      |      "totalAdditions": 100.49,
      |      "totalDeductions": 100.49
      |    },
      |    "income": {
      |      "rent":100.49
      |      },
      |    "expenses": {
      |      "premisesRunningCosts":100.49,
      |      "repairsAndMaintenance":100.49,
      |      "financialCosts":100.49,
      |      "professionalFees":100.49,
      |      "travelCosts":100.49,
      |      "costOfServices":100.49,
      |      "other":100.49
      |    }
      |   }
      | ]
      |}""".stripMargin
  )

  val emptyObjectsDesJson = Json.parse(
    """{
      |  "totalIncome": 100.49,
      |  "totalExpenses": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "accountingAdjustments": 100.49,
      |  "income": {},
      |  "expenses": {}
      |}""".stripMargin
  )

  "reads" should {
    "return a valid model" when {
      "a valid non-fhl json with all fields are supplied" in {
        nonFhlDesJson.as[BsasDetail](BsasDetail.nonFhlReads) shouldBe nonFhlBsasDetailModel
      }

      "a valid fhl json with all fields are supplied" in {
        fhlDesJson.as[BsasDetail](BsasDetail.fhlReads) shouldBe fhlBsasDetailModel
      }

      "a valid non-fhl json with all fields empty are supplied" in {
        emptyObjectsDesJson.as[BsasDetail](BsasDetail.nonFhlReads) shouldBe BsasDetail(total, None, None, None, None, None)
      }

      "a valid fhl json with all fields empty are supplied" in {
        emptyObjectsDesJson.as[BsasDetail](BsasDetail.fhlReads) shouldBe BsasDetail(total, None, None, None, None, None)
      }

    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid non-fhl model is supplied" in {
        nonFhlBsasDetailModel.toJson shouldBe nonFhlMtdJson
      }

      "a valid fhl model is supplied" in {
        fhlBsasDetailModel.toJson shouldBe fhlMtdJson
      }
    }
  }
}
