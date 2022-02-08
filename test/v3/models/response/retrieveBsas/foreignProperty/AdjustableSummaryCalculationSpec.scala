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

package v3.models.response.retrieveBsas.foreignProperty

import play.api.libs.json.Json
import support.UnitSpec
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures.adjustableSummaryCalculationModel
import v3.models.utils.JsonErrorValidators

class AdjustableSummaryCalculationSpec extends UnitSpec with JsonErrorValidators{

  val mtdJson = Json.parse(
    """{
      |		"totalIncome": 0.12,
      |		"income": {
      |			"totalRentsReceived": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"totalExpenses": 0.12,
      |		"expenses": {
      |     "consolidatedExpenses": 0.12,
      |			"premisesRunningCosts": 0.12,
      |			"repairsAndMaintenance": 0.12,
      |			"financialCosts": 0.12,
      |			"professionalFees": 0.12,
      |			"travelCosts": 0.12,
      |			"costOfServices": 0.12,
      |			"residentialFinancialCost": 0.12,
      |			"broughtFwdResidentialFinancialCost": 0.12,
      |			"other": 0.12
      |		},
      |		"netProfit": 0.12,
      |   "netLoss": 0.12,
      |		"totalAdditions": 0.12,
      |		"additions": {
      |			"privateUseAdjustment": 0.12,
      |			"balancingCharge": 0.12
      |		},
      |		"totalDeductions": 0.12,
      |		"deductions": {
      |			"annualInvestmentAllowance": 0.12,
      |			"costOfReplacingDomesticItems": 0.12,
      |			"zeroEmissionGoods": 0.12,
      |     "propertyAllowance": 0.12,
      |			"otherCapitalAllowance": 0.12,
      |			"electricChargePointAllowance": 0.12,
      |			"structuredBuildingAllowance": 0.12,
      |			"zeroEmissionsCarAllowance": 0.12
      |		},
      |		"taxableProfit": 1,
      |   "adjustedIncomeTaxLoss": 1,
      |		"countryLevelDetail": [{
      |			"countryCode": "CYM",
      |			"totalIncome": 0.12,
      |			"income": {
      |				"totalRentsReceived": 0.12,
      |				"premiumsOfLeaseGrant": 0.12,
      |				"otherPropertyIncome": 0.12
      |			},
      |			"totalExpenses": 0.12,
      |			"expenses": {
      |				"consolidatedExpenses": 0.12,
      |				"premisesRunningCosts": 0.12,
      |				"repairsAndMaintenance": 0.12,
      |				"financialCosts": 0.12,
      |				"professionalFees": 0.12,
      |				"travelCosts": 0.12,
      |				"costOfServices": 0.12,
      |				"residentialFinancialCost": 0.12,
      |				"broughtFwdResidentialFinancialCost": 0.12,
      |				"other": 0.12
      |			},
      |			"netProfit": 0.12,
      |			"netLoss": 0.12,
      |			"totalAdditions": 0.12,
      |			"additions": {
      |				"privateUseAdjustment": 0.12,
      |				"balancingCharge": 0.12
      |			},
      |			"totalDeductions": 0.12,
      |			"deductions": {
      |				"annualInvestmentAllowance": 0.12,
      |				"costOfReplacingDomesticItems": 0.12,
      |				"zeroEmissionGoods": 0.12,
      |				"propertyAllowance": 0.12,
      |				"otherCapitalAllowance": 0.12,
      |				"electricChargePointAllowance": 0.12,
      |				"structuredBuildingAllowance": 0.12,
      |				"zeroEmissionsCarAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |			"adjustedIncomeTaxLoss": 1
      |		}]
      |}""".stripMargin
  )

  val desJson = Json.parse(
    """{
      |		"totalIncome": 0.12,
      |		"income": {
      |			"rent": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"totalExpenses": 0.12,
      |		"expenses": {
      |     "consolidatedExpenses": 0.12,
      |			"premisesRunningCosts": 0.12,
      |			"repairsAndMaintenance": 0.12,
      |			"financialCosts": 0.12,
      |			"professionalFees": 0.12,
      |			"travelCosts": 0.12,
      |			"costOfServices": 0.12,
      |			"residentialFinancialCost": 0.12,
      |			"broughtFwdResidentialFinancialCost": 0.12,
      |			"other": 0.12
      |		},
      |		"netProfit": 0.12,
      |   "netLoss": 0.12,
      |		"totalAdditions": 0.12,
      |		"additions": {
      |			"privateUseAdjustment": 0.12,
      |			"balancingCharge": 0.12
      |		},
      |		"totalDeductions": 0.12,
      |		"deductions": {
      |			"annualInvestmentAllowance": 0.12,
      |			"costOfReplacingDomesticItems": 0.12,
      |			"zeroEmissionsGoodsVehicleAllowance": 0.12,
      |     "propertyAllowance": 0.12,
      |			"otherCapitalAllowance": 0.12,
      |			"electricChargePointAllowance": 0.12,
      |			"structuredBuildingAllowance": 0.12,
      |			"zeroEmissionsCarAllowance": 0.12
      |		},
      |		"taxableProfit": 1,
      |   "adjustedIncomeTaxLoss": 1,
      |		"countryLevelDetail": [{
      |			"countryCode": "CYM",
      |			"totalIncome": 0.12,
      |			"income": {
      |				"rent": 0.12,
      |				"premiumsOfLeaseGrant": 0.12,
      |				"otherPropertyIncome": 0.12
      |			},
      |			"totalExpenses": 0.12,
      |			"expenses": {
      |				"consolidatedExpenses": 0.12,
      |				"premisesRunningCosts": 0.12,
      |				"repairsAndMaintenance": 0.12,
      |				"financialCosts": 0.12,
      |				"professionalFees": 0.12,
      |				"travelCosts": 0.12,
      |				"costOfServices": 0.12,
      |				"residentialFinancialCost": 0.12,
      |				"broughtFwdResidentialFinancialCost": 0.12,
      |				"other": 0.12
      |			},
      |			"netProfit": 0.12,
      |			"netLoss": 0.12,
      |			"totalAdditions": 0.12,
      |			"additions": {
      |				"privateUseAdjustment": 0.12,
      |				"balancingCharge": 0.12
      |			},
      |			"totalDeductions": 0.12,
      |			"deductions": {
      |				"annualInvestmentAllowance": 0.12,
      |				"costOfReplacingDomesticItems": 0.12,
      |				"zeroEmissionsGoodsVehicleAllowance": 0.12,
      |				"propertyAllowance": 0.12,
      |				"otherCapitalAllowance": 0.12,
      |				"electricChargePointAllowance": 0.12,
      |				"structuredBuildingAllowance": 0.12,
      |				"zeroEmissionsCarAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |			"adjustedIncomeTaxLoss": 1
      |		}]
      |}""".stripMargin
  )

  "reads" should {
    "return a valid adjustableSummaryCalculation model" when {
      "a valid json with all fields are supplied" in {
        desJson.as[AdjustableSummaryCalculation] shouldBe adjustableSummaryCalculationModel
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        adjustableSummaryCalculationModel.toJson shouldBe mtdJson
      }
    }
  }
}
