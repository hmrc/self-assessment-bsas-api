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
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import v3.models.utils.JsonErrorValidators

class RetrieveForeignPropertyBsasResponseSpec extends UnitSpec with JsonErrorValidators{

  val mtdJsonNonFhl = Json.parse(
    """{
      |	"metadata": {
      |		"calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |		"requestedDateTime": "2020-12-05T16:19:44Z",
      |		"adjustedDateTime": "2020-12-05T16:19:44Z",
      |		"nino": "AA999999A",
      |		"taxYear": "2019-20",
      |		"summaryStatus": "valid"
      |	},
      |	"inputs": {
      |		"businessId": "000000000000210",
      |		"typeOfBusiness": "foreign-property-fhl-eea",
      |		"businessName": "Business Name",
      |		"accountingPeriodStartDate": "2019-04-06",
      |		"accountingPeriodEndDate": "2020-04-05",
      |		"source": "MTD-SA",
      |		"submissionPeriods": [{
      |			"submissionId": "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
      |			"startDate": "2019-04-06",
      |			"endDate": "2020-04-05",
      |			"receivedDateTime": "2019-02-15T09:35:04.843Z"
      |		}]
      |	},
      |	"adjustableSummaryCalculation": {
      |		"totalIncome": 0.12,
      |		"income": {
      |			"totalRentsReceived": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"totalExpenses": 0.12,
      |		"expenses": {
      |			"consolidatedExpenses": 0.12,
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
      |		"netLoss": 0.12,
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
      |			"propertyAllowance": 0.12,
      |			"otherCapitalAllowance": 0.12,
      |			"electricChargePointAllowance": 0.12,
      |			"structuredBuildingAllowance": 0.12,
      |			"zeroEmissionsCarAllowance": 0.12
      |		},
      |		"taxableProfit": 1,
      |		"adjustedIncomeTaxLoss": 1,
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
      |	},
      |	"adjustments": [{
      |		"countryCode": "CYM",
      |		"income": {
      |			"rentReceived": 99999999999.99,
      |			"premiumsOfLeaseGrant": 99999999999.99,
      |			"otherPropertyIncome": 99999999999.99
      |		},
      |		"expenses": {
      |			"premisesRunningCosts": 99999999999.99,
      |			"repairsAndMaintenance": 99999999999.99,
      |			"financialCosts": 99999999999.99,
      |			"professionalFees": 99999999999.99,
      |			"travelCosts": 99999999999.99,
      |			"costOfServices": 99999999999.99,
      |			"residentialFinancialCost": 99999999999.99,
      |			"other": 99999999999.99,
      |			"consolidatedExpenses": 99999999999.99
      |		}
      |	}],
      |	"adjustedSummaryCalculation": {
      |		"totalIncome": 0.12,
      |		"income": {
      |			"totalRentsReceived": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"totalExpenses": 0.12,
      |		"expenses": {
      |			"consolidatedExpenses": 0.12,
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
      |		"netLoss": 0.12,
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
      |			"propertyAllowance": 0.12,
      |			"otherCapitalAllowance": 0.12,
      |			"electricChargePointAllowance": 0.12
      |		},
      |		"taxableProfit": 1,
      |		"adjustedIncomeTaxLoss": 1,
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
      |				"electricChargePointAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |			"adjustedIncomeTaxLoss": 1
      |		}]
      |	}
      |}""".stripMargin
  )

  val mtdJsonFhlEea = Json.parse(
    """{
      |	"metadata": {
      |		"calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |		"requestedDateTime": "2020-12-05T16:19:44Z",
      |		"adjustedDateTime": "2020-12-05T16:19:44Z",
      |		"nino": "AA999999A",
      |		"taxYear": "2019-20",
      |		"summaryStatus": "valid"
      |	},
      |	"inputs": {
      |		"businessId": "000000000000210",
      |		"typeOfBusiness": "foreign-property-fhl-eea",
      |		"businessName": "Business Name",
      |		"accountingPeriodStartDate": "2019-04-06",
      |		"accountingPeriodEndDate": "2020-04-05",
      |		"source": "MTD-SA",
      |		"submissionPeriods": [{
      |			"submissionId": "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
      |			"startDate": "2019-04-06",
      |			"endDate": "2020-04-05",
      |			"receivedDateTime": "2019-02-15T09:35:04.843Z"
      |		}]
      |	},
      |	"adjustableSummaryCalculation": {
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
      |       "consolidatedExpenses": 0.12,
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
      |     "netLoss": 0.12,
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
      |       "propertyAllowance": 0.12,
      |				"otherCapitalAllowance": 0.12,
      |				"electricChargePointAllowance": 0.12,
      |				"structuredBuildingAllowance": 0.12,
      |				"zeroEmissionsCarAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |     "adjustedIncomeTaxLoss": 1
      |		}]
      |	},
      |	"adjustments": {
      |		  "income": {
      |		  	"totalRentsReceived": 99999999999.99,
      |		  	"premiumsOfLeaseGrant": 99999999999.99,
      |		  	"otherPropertyIncome": 99999999999.99
      |	  	},
      |	  	"expenses": {
      |	  		"premisesRunningCosts": 99999999999.99,
      |	  		"repairsAndMaintenance": 99999999999.99,
      |		  	"financialCosts": 99999999999.99,
      |		  	"professionalFees": 99999999999.99,
      |		  	"travelCosts": 99999999999.99,
      |		  	"costOfServices": 99999999999.99,
      |		  	"residentialFinancialCost": 99999999999.99,
      |       "broughtFwdResidentialFinancialCost":99999999999.99,
      |		  	"other": 99999999999.99,
      |      "consolidatedExpenses": 99999999999.99
      |		  }
      |	  },
      |	"adjustedSummaryCalculation": {
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
      |     "structuredBuildingAllowance":0.12,
      |     "zeroEmissionsCarAllowance":0.12
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
      |       "consolidatedExpenses": 0.12,
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
      |     "netLoss": 0.12,
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
      |       "propertyAllowance": 0.12,
      |				"otherCapitalAllowance": 0.12,
      |				"electricChargePointAllowance": 0.12,
      |       "structuredBuildingAllowance":0.12,
      |       "zeroEmissionsCarAllowance":0.12
      |			},
      |			"taxableProfit": 1,
      |     "adjustedIncomeTaxLoss": 1
      |		}]
      |	}
      |}""".stripMargin
  )

  val desJsonNonFhl = Json.parse(
    """{
      |	"metadata": {
      |		"calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |		"requestedDateTime": "2020-12-05T16:19:44Z",
      |		"adjustedDateTime": "2020-12-05T16:19:44Z",
      |		"taxableEntityId": "AA999999A",
      |		"taxYear": 2020,
      |		"status": "valid"
      |	},
      |	"inputs": {
      |		"incomeSourceId": "000000000000210",
      |		"incomeSourceType": "15",
      |		"incomeSourceName": "Business Name",
      |		"accountingPeriodStartDate": "2019-04-06",
      |		"accountingPeriodEndDate": "2020-04-05",
      |		"source": "MTD-SA",
      |		"submissionPeriods": [{
      |			"periodId": "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
      |			"startDate": "2019-04-06",
      |			"endDate": "2020-04-05",
      |			"receivedDateTime": "2019-02-15T09:35:04.843Z"
      |		}]
      |	},
      |	"adjustableSummaryCalculation": {
      |		"totalIncome": 0.12,
      |		"income": {
      |			"rent": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"totalExpenses": 0.12,
      |		"expenses": {
      |			"consolidatedExpenses": 0.12,
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
      |		"netLoss": 0.12,
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
      |			"propertyAllowance": 0.12,
      |			"otherCapitalAllowance": 0.12,
      |			"electricChargePointAllowance": 0.12,
      |			"structuredBuildingAllowance": 0.12,
      |			"zeroEmissionsCarAllowance": 0.12
      |		},
      |		"taxableProfit": 1,
      |		"adjustedIncomeTaxLoss": 1,
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
      |				"otherCapitalAllowance": 0.12,
      |				"electricChargePointAllowance": 0.12,
      |				"structuredBuildingAllowance": 0.12,
      |				"zeroEmissionsCarAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |			"adjustedIncomeTaxLoss": 1
      |		}]
      |	},
      |	"adjustments": [
      |    {
      |        "countryCode": "CYM",
      |        "income": {
      |          "rentReceived": 99999999999.99,
      |          "premiumsOfLeaseGrant": 99999999999.99,
      |          "otherPropertyIncome": 99999999999.99
      |        },
      |        "expenses": {
      |          "premisesRunningCosts": 99999999999.99,
      |          "repairsAndMaintenance": 99999999999.99,
      |          "financialCosts": 99999999999.99,
      |          "professionalFees": 99999999999.99,
      |          "travelCosts": 99999999999.99,
      |          "costOfServices": 99999999999.99,
      |          "residentialFinancialCost": 99999999999.99,
      |          "other": 99999999999.99
      |        }
      |    }
      |  ],
      |	"adjustedSummaryCalculation": {
      |		"totalIncome": 0.12,
      |		"income": {
      |			"rent": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"totalExpenses": 0.12,
      |		"expenses": {
      |			"consolidatedExpenses": 0.12,
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
      |			"propertyAllowance": 0.12,
      |			"otherCapitalAllowance": 0.12,
      |			"electricChargePointAllowance": 0.12,
      |			"structuredBuildingAllowance": 0.12,
      |			"zeroEmissionsCarAllowance": 0.12
      |		},
      |		"taxableProfit": 1,
      |		"adjustedIncomeTaxLoss": 1,
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
      |				"structuredBuildingAllowance": 0.12,
      |				"zeroEmissionsCarAllowance": 0.12,
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
      |				"electricChargePointAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |			"adjustedIncomeTaxLoss": 1
      |		}]
      |	}
      |}""".stripMargin
  )

  val desJsonFhlEea = Json.parse(
    """{
      |	"metadata": {
      |		"calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |		"requestedDateTime": "2020-12-05T16:19:44Z",
      |		"adjustedDateTime": "2020-12-05T16:19:44Z",
      |		"taxableEntityId": "AA999999A",
      |		"taxYear": 2020,
      |		"status": "valid"
      |	},
      |	"inputs": {
      |		"incomeSourceId": "000000000000210",
      |		"incomeSourceType": "03",
      |		"incomeSourceName": "Business Name",
      |		"accountingPeriodStartDate": "2019-04-06",
      |		"accountingPeriodEndDate": "2020-04-05",
      |		"source": "MTD-SA",
      |		"submissionPeriods": [{
      |			"periodId": "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
      |			"startDate": "2019-04-06",
      |			"endDate": "2020-04-05",
      |			"receivedDateTime": "2019-02-15T09:35:04.843Z"
      |		}]
      |	},
      |	"adjustableSummaryCalculation": {
      |		"totalIncome": 0.12,
      |		"income": {
      |			"rent": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"totalExpenses": 0.12,
      |		"expenses": {
      |			"consolidatedExpenses": 0.12,
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
      |		"netLoss": 0.12,
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
      |			"propertyAllowance": 0.12,
      |			"otherCapitalAllowance": 0.12,
      |			"electricChargePointAllowance": 0.12,
      |			"structuredBuildingAllowance": 0.12,
      |			"zeroEmissionsCarAllowance": 0.12
      |		},
      |		"taxableProfit": 1,
      |		"adjustedIncomeTaxLoss": 1,
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
      |       "propertyAllowance": 0.12,
      |				"otherCapitalAllowance": 0.12,
      |				"electricChargePointAllowance": 0.12,
      |				"structuredBuildingAllowance": 0.12,
      |				"zeroEmissionsCarAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |			"adjustedIncomeTaxLoss": 1
      |		}]
      |	},
      |	"adjustments": {
      |		"income": {
      |			"rent": 99999999999.99,
      |     "premiumsOfLeaseGrant": 99999999999.99,
      |     "otherPropertyIncome": 99999999999.99
      |		},
      |		"expenses": {
      |			"premisesRunningCosts": 99999999999.99,
      |			"repairsAndMaintenance": 99999999999.99,
      |			"financialCosts": 99999999999.99,
      |			"professionalFees": 99999999999.99,
      |			"costOfServices": 99999999999.99,
      |     "residentialFinancialCost": 99999999999.99,
      |     "broughtFwdResidentialFinancialCost": 99999999999.99,
      |			"travelCosts": 99999999999.99,
      |			"other": 99999999999.99,
      |			"consolidatedExpenses": 99999999999.99
      |		}
      |	},
      |	"adjustedSummaryCalculation": {
      |		"totalIncome": 0.12,
      |		"income": {
      |			"rent": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"totalExpenses": 0.12,
      |		"expenses": {
      |			"consolidatedExpenses": 0.12,
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
      |			"propertyAllowance": 0.12,
      |			"otherCapitalAllowance": 0.12,
      |			"electricChargePointAllowance": 0.12,
      |			"structuredBuildingAllowance": 0.12,
      |			"zeroEmissionsCarAllowance": 0.12
      |		},
      |		"taxableProfit": 1,
      |		"adjustedIncomeTaxLoss": 1,
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
      |				"structuredBuildingAllowance": 0.12,
      |				"zeroEmissionsCarAllowance": 0.12,
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
      |       "structuredBuildingAllowance": 0.12,
      |       "zeroEmissionsCarAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |			"adjustedIncomeTaxLoss": 1
      |		}]
      |	}
      |}""".stripMargin
  )
}
