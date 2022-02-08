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
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures.retrieveBsasResponseModel
import v3.models.utils.JsonErrorValidators

class RetrieveForeignPropertyBsasResponseSpec extends UnitSpec with JsonErrorValidators{

  val mtdJson = Json.parse(
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
      |     "consolidatedExpenses": 99999999999.99
      |		},
      |   "income": {
      |     "totalRentsReceived": 99999999999.99
      |   },
      |   "expenses": {
      |     "premisesRunningCosts": 99999999999.99,
      |     "repairsAndMaintenance": 99999999999.99,
      |			"financialCosts": 99999999999.99,
      |			"professionalFees": 99999999999.99,
      |			"travelCosts": 99999999999.99,
      |			"costOfServices": 99999999999.99,
      |			"residentialFinancialCost": 99999999999.99,
      |			"other": 99999999999.99,
      |     "consolidatedExpenses": 99999999999.99
      |   }
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
      |			"electricChargePointAllowance": 0.12
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
      |				"electricChargePointAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |     "adjustedIncomeTaxLoss": 1
      |		}]
      |	}
      |}""".stripMargin
  )

/*  val desJson = Json.parse(
    """{
      |	"metadata": {
      |		"calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |		"requestedDateTime": "2020-12-05T16:19:44Z",
      |		"adjustedDateTime": "2020-12-05T16:19:44Z",
      |		"taxableEntityId": "AA111111A",
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
      |				"zeroEmissionsGoodsVehicleAllowance": 0.12,
      |				"otherCapitalAllowance": 0.12,
      |				"electricChargePointAllowance": 0.12,
      |				"structuredBuildingAllowance": 0.12,
      |				"zeroEmissionsCarAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |     "adjustedIncomeTaxLoss": 1
      |		}]
      |	},
      |	"adjustments": [{
      |   [
      |		"countryCode": "CYM",
      |		"income": {
      |			"rentReceived": 99999999999.99,
      |			"premiumsOfLeaseGrant": 99999999999.99,
      |			"otherPropertyIncome": 99999999999.99
      |		},
      |		"expenses": {
      |     "consolidatedExpenses": 99999999999.99,
      |			"premisesRunningCosts": 99999999999.99,
      |			"repairsAndMaintenance": 99999999999.99,
      |			"financialCosts": 99999999999.99,
      |			"professionalFees": 99999999999.99,
      |			"travelCosts": 99999999999.99,
      |			"costOfServices": 99999999999.99,
      |			"residentialFinancialCost": 99999999999.99,
      |			"other": 99999999999.99
      |		}],
      |  "income": {
      |   "rent": 99999999999.99
      |  },
      |  "expenses": {
      |   "premisesRunningCosts": 99999999999.99,
      |   "repairsAndMaintenance": 99999999999.99,
      |   "financialCosts": 99999999999.99,
      |   "professionalFees": 99999999999.99,
      |   "costOfServices": 99999999999.99,
      |   "travelCosts": 99999999999.99,
      |   "other": 99999999999.99,
      |   "consolidatedExpenses": 99999999999.99
      |  }
      |	}],
      |	"adjustedSummaryCalculation": {
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
      |     "structuredBuildingAllowance": 0.12,
      |     "zeroEmissionsCarAllowance": 0.12
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
      |       "consolidatedExpenses": 0.12,
      |				"premisesRunningCosts": 0.12,
      |				"repairsAndMaintenance": 0.12,
      |				"financialCosts": 0.12,
      |				"professionalFees": 0.12,
      |				"travelCosts": 0.12,
      |				"costOfServices": 0.12,
      |				"residentialFinancialCost": 0.12,
      |				"broughtFwdResidentialFinancialCost": 0.12,
      |       "structuredBuildingAllowance": 0.12,
      |       "zeroEmissionsCarAllowance": 0.12,
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
      |				"zeroEmissionsGoodsVehicleAllowance": 0.12,
      |       "propertyAllowance": 0.12,
      |				"otherCapitalAllowance": 0.12,
      |				"electricChargePointAllowance": 0.12
      |			},
      |			"taxableProfit": 1,
      |     "adjustedIncomeTaxLoss": 1
      |		}]
      |	}
      |}""".stripMargin
  )*/

/*  "reads" should {
    "return a valid retrieve BSAS model" when {
      "a valid json with all fields are supplied" in {
        desJson.as[RetrieveForeignPropertyBSASResponse] shouldBe retrieveBsasResponseModel
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        retrieveBsasResponseModel.toJson shouldBe mtdJson
      }
    }
<<<<<<< HEAD
  }

  "HateoasFactory" should {
    class Test extends MockAppConfig{
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      val bsasId = "anId"
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes
    }

    "expose the correct links for a response from Submit a Property Summary Adjustment" in new Test {
      hateoasFactory.wrap(retrieveForeignPropertyBsasResponseModel, RetrieveForeignPropertyHateoasData(nino, bsasId)) shouldBe
        HateoasWrapper(
          retrieveForeignPropertyBsasResponseModel,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId", GET, "self"),
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust", POST, "submit-foreign-property-accounting-adjustments")
          )
        )
    }
  }

=======
  }*/
}
