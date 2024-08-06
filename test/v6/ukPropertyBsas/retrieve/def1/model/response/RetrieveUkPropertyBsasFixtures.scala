/*
 * Copyright 2023 HM Revenue & Customs
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

package v6.ukPropertyBsas.retrieve.def1.model.response

import play.api.libs.json.{JsValue, Json}
import v6.common.model.IncomeSourceType

object RetrieveUkPropertyBsasFixtures {

  private val now          = "2019-04-06"
  private val aYearFromNow = "2020-04-05"

  val downstreamMetadataJson: JsValue = Json.parse(
    """
      |{
      |  "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |  "requestedDateTime": "2000-01-01T10:12:10Z",
      |  "adjustedDateTime": "2000-01-01T10:12:10Z",
      |  "taxableEntityId": "AA999999A",
      |  "taxYear": 2021,
      |  "status": "valid"
      |}
      |""".stripMargin
  )

  val downstreamSubmissionPeriodWithPeriodIdRegexJson: JsValue = Json.parse(
    s"""
      |{
      |  "periodId": "1234567890123456",
      |  "startDate": "$now",
      |  "endDate": "$aYearFromNow",
      |  "receivedDateTime": "2000-01-01T10:12:10Z"
      |}
      |""".stripMargin
  )

  val downstreamSubmissionPeriodWithInvalidPeriodIdRegexJson: JsValue = Json.parse(
    s"""
       |{
       |  "periodId": "7038926d-d7a1-4399-8641-f278b438259c",
       |  "startDate": "$now",
       |  "endDate": "$aYearFromNow",
       |  "receivedDateTime": "2000-01-01T10:12:10Z"
       |}
       |""".stripMargin
  )

  val downstreamInputsFhlJson: JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceType": "04",
      |  "incomeSourceId": "XAIS12345678910",
      |  "incomeSourceName": "Business Name",
      |  "accountingPeriodStartDate": "$now",
      |  "accountingPeriodEndDate": "$aYearFromNow",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [$downstreamSubmissionPeriodWithPeriodIdRegexJson, $downstreamSubmissionPeriodWithInvalidPeriodIdRegexJson]
      |}
      |""".stripMargin
  )

  val downstreamInputsNonFhlJson: JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceType": "02",
      |  "incomeSourceId": "XAIS12345678910",
      |  "incomeSourceName": "Business Name",
      |  "accountingPeriodStartDate": "$now",
      |  "accountingPeriodEndDate": "$aYearFromNow",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [$downstreamSubmissionPeriodWithPeriodIdRegexJson, $downstreamSubmissionPeriodWithInvalidPeriodIdRegexJson]
      |}
      |""".stripMargin
  )

  val downstreamInputsInvalidSourceJson: JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceType": "01",
      |  "incomeSourceId": "XAIS12345678910",
      |  "incomeSourceName": "Business Name",
      |  "accountingPeriodStartDate": "$now",
      |  "accountingPeriodEndDate": "$aYearFromNow",
      |  "source": "MTD-VAT",
      |  "submissionPeriods": [$downstreamSubmissionPeriodWithPeriodIdRegexJson]
      |}
      |""".stripMargin
  )

  def downstreamInputsInvalidIncomeSourceTypeJson(incomeSourceType: IncomeSourceType): JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceType": "$incomeSourceType",
      |  "incomeSourceId": "XAIS12345678910",
      |  "incomeSourceName": "Business Name",
      |  "accountingPeriodStartDate": "$now",
      |  "accountingPeriodEndDate": "$aYearFromNow",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [$downstreamSubmissionPeriodWithPeriodIdRegexJson]
      |}
      |""".stripMargin
  )

  val downstreamSummaryCalculationIncomeJson: JsValue = Json.parse(
    """
      |{
      |  "totalRentsReceived": 1.01,
      |  "premiumsOfLeaseGrant": 1.02,
      |  "reversePremiums": 1.03,
      |  "otherPropertyIncome": 1.04,
      |  "rentReceived": 1.05,
      |  "rarRentReceived": 1.06
      |}
      |""".stripMargin
  )

  val downstreamSummaryCalculationExpensesJson: JsValue = Json.parse(
    """
       |{
       |  "consolidatedExpenses": 2.01,
       |  "premisesRunningCosts": 2.02,
       |  "repairsAndMaintenance": 2.03,
       |  "financialCosts": 2.04,
       |  "professionalFees": 2.05,
       |  "costOfServices": 2.06,
       |  "residentialFinancialCost": 2.07,
       |  "broughtFwdResidentialFinancialCost": 2.08,
       |  "other": 2.09,
       |  "travelCosts": 2.10
       |}
       |""".stripMargin
  )

  val downstreamSummaryCalculationAdditionsJson: JsValue = Json.parse(
    """
      |{
      |  "privateUseAdjustment": 5.01,
      |  "balancingCharge": 5.02,
      |  "bpraBalancingCharge": 5.03
      |}
      |""".stripMargin
  )

  val downstreamSummaryCalculationDeductionsJson: JsValue = Json.parse(
    """
      |{
      |  "zeroEmissionsGoodsVehicleAllowance": 6.01,
      |  "annualInvestmentAllowance": 6.02,
      |  "costOfReplacingDomesticItems": 6.03,
      |  "businessPremisesRenovationAllowance": 6.04,
      |  "propertyAllowance": 6.05,
      |  "otherCapitalAllowance": 6.06,
      |  "rarReliefClaimed": 6.07,
      |  "electricChargePointAllowance": 6.08,
      |  "structuredBuildingAllowance": 6.09,
      |  "enhancedStructuredBuildingAllowance": 6.10,
      |  "zeroEmissionsCarAllowance": 6.11
      |}
      |""".stripMargin
  )

  val downstreamSummaryCalculationJson: JsValue = Json.parse(
    s"""
      |{
      |  "totalIncome": 1.00,
      |  "income": $downstreamSummaryCalculationIncomeJson,
      |  "totalExpenses": 2.00,
      |  "expenses": $downstreamSummaryCalculationExpensesJson,
      |  "netProfit": 3.00,
      |  "netLoss": 4.00,
      |  "totalAdditions": 5.00,
      |  "additions": $downstreamSummaryCalculationAdditionsJson,
      |  "totalDeductions": 6.00,
      |  "deductions": $downstreamSummaryCalculationDeductionsJson,
      |  "taxableProfit": 7.00,
      |  "adjustedIncomeTaxLoss": 8.00
      |}
      |""".stripMargin
  )

  val downstreamAdjustmentsIncomeJson: JsValue = Json.parse(
    """
      |{
      |  "totalRentsReceived": 1.01,
      |  "premiumsOfLeaseGrant": 1.02,
      |  "reversePremiums": 1.03,
      |  "otherPropertyIncome": 1.04,
      |  "rentReceived": 1.05
      |}
      |""".stripMargin
  )

  val downstreamAdjustmentsExpensesJson: JsValue = Json.parse(
    """
      |{
      |    "consolidatedExpenses": 2.01,
      |    "premisesRunningCosts": 2.02,
      |    "repairsAndMaintenance": 2.03,
      |    "financialCosts": 2.04,
      |    "professionalFees": 2.05,
      |    "costOfServices": 2.06,
      |    "residentialFinancialCost": 2.07,
      |    "other": 2.08,
      |    "travelCosts": 2.09
      |}
      |""".stripMargin
  )

  val downstreamAdjustmentsJson: JsValue = Json.parse(
    s"""
      |{
      |  "income": $downstreamAdjustmentsIncomeJson,
      |  "expenses": $downstreamAdjustmentsExpensesJson
      |}
      |""".stripMargin
  )

  val downstreamRetrieveBsasFhlResponseJson: JsValue = Json.parse(s"""
       |{
       |  "metadata": $downstreamMetadataJson,
       |  "inputs": $downstreamInputsFhlJson,
       |  "adjustableSummaryCalculation": $downstreamSummaryCalculationJson,
       |  "adjustments": $downstreamAdjustmentsJson,
       |  "adjustedSummaryCalculation": $downstreamSummaryCalculationJson
       |}
       |""".stripMargin)

  val downstreamRetrieveBsasNonFhlResponseJson: JsValue = Json.parse(s"""
       |{
       |  "metadata": $downstreamMetadataJson,
       |  "inputs": $downstreamInputsNonFhlJson,
       |  "adjustableSummaryCalculation": $downstreamSummaryCalculationJson,
       |  "adjustments": $downstreamAdjustmentsJson,
       |  "adjustedSummaryCalculation": $downstreamSummaryCalculationJson
       |}
       |""".stripMargin)

  def downstreamRetrieveBsasResponseJsonInvalidIncomeSourceType(incomeSourceType: IncomeSourceType): JsValue = Json.parse(s"""
       |{
       |  "metadata": $downstreamMetadataJson,
       |  "inputs": ${downstreamInputsInvalidIncomeSourceTypeJson(incomeSourceType)},
       |  "adjustableSummaryCalculation": $downstreamSummaryCalculationJson,
       |  "adjustments": $downstreamAdjustmentsJson,
       |  "adjustedSummaryCalculation": $downstreamSummaryCalculationJson
       |}
       |""".stripMargin)

  val mtdMetadataJson: JsValue = Json.parse(
    """
      |{
      |  "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |  "requestedDateTime": "2000-01-01T10:12:10Z",
      |  "adjustedDateTime": "2000-01-01T10:12:10Z",
      |  "nino": "AA999999A",
      |  "taxYear": "2020-21",
      |  "summaryStatus": "valid"
      |}
      |""".stripMargin
  )

  val mtdSubmissionPeriodWithPeriodIdJson: JsValue = Json.parse(
    s"""
       |{
       |  "periodId": "1234567890123456",
       |  "startDate": "$now",
       |  "endDate": "$aYearFromNow",
       |  "receivedDateTime": "2000-01-01T10:12:10Z"
       |}
       |""".stripMargin
  )

  val mtdSubmissionPeriodWithSubmissionIdJson: JsValue = Json.parse(
    s"""
       |{
       |  "submissionId": "${now}_$aYearFromNow",
       |  "startDate": "$now",
       |  "endDate": "$aYearFromNow",
       |  "receivedDateTime": "2000-01-01T10:12:10Z"
       |}
       |""".stripMargin
  )

  val mtdInputsFhlJson: JsValue = Json.parse(
    s"""
       |{
       |  "typeOfBusiness": "uk-property-fhl",
       |  "businessId": "XAIS12345678910",
       |  "businessName": "Business Name",
       |  "accountingPeriodStartDate": "$now",
       |  "accountingPeriodEndDate": "$aYearFromNow",
       |  "source": "MTD-SA",
       |  "submissionPeriods": [$mtdSubmissionPeriodWithPeriodIdJson, $mtdSubmissionPeriodWithSubmissionIdJson]
       |}
       |""".stripMargin
  )

  val mtdInputsNonFhlJson: JsValue = Json.parse(
    s"""
       |{
       |  "typeOfBusiness": "uk-property-non-fhl",
       |  "businessId": "XAIS12345678910",
       |  "businessName": "Business Name",
       |  "accountingPeriodStartDate": "$now",
       |  "accountingPeriodEndDate": "$aYearFromNow",
       |  "source": "MTD-SA",
       |  "submissionPeriods": [$mtdSubmissionPeriodWithPeriodIdJson, $mtdSubmissionPeriodWithSubmissionIdJson]
       |}
       |""".stripMargin
  )

  val mtdSummaryCalculationIncomeFhlJson: JsValue = Json.parse(
    """
      |{
      |  "totalRentsReceived": 1.05,
      |  "rarRentReceived": 1.06
      |}
      |""".stripMargin
  )

  val mtdSummaryCalculationIncomeNonFhlJson: JsValue = Json.parse(
    """
      |{
      |  "totalRentsReceived": 1.01,
      |  "premiumsOfLeaseGrant": 1.02,
      |  "reversePremiums": 1.03,
      |  "otherPropertyIncome": 1.04,
      |  "rarRentReceived": 1.06
      |}
      |""".stripMargin
  )

  val mtdSummaryCalculationExpensesFhlJson: JsValue = Json.parse(
    """
       |{
       |  "consolidatedExpenses": 2.01,
       |  "premisesRunningCosts": 2.02,
       |  "repairsAndMaintenance": 2.03,
       |  "financialCosts": 2.04,
       |  "professionalFees": 2.05,
       |  "costOfServices": 2.06,
       |  "other": 2.09,
       |  "travelCosts": 2.10
       |}
       |""".stripMargin
  )

  val mtdSummaryCalculationExpensesNonFhlJson: JsValue = Json.parse(
    """
       |{
       |  "consolidatedExpenses": 2.01,
       |  "premisesRunningCosts": 2.02,
       |  "repairsAndMaintenance": 2.03,
       |  "financialCosts": 2.04,
       |  "professionalFees": 2.05,
       |  "costOfServices": 2.06,
       |  "residentialFinancialCost": 2.07,
       |  "broughtFwdResidentialFinancialCost": 2.08,
       |  "other": 2.09,
       |  "travelCosts": 2.10
       |}
       |""".stripMargin
  )

  val mtdSummaryCalculationAdditionsJson: JsValue = Json.parse(
    """
      |{
      |  "privateUseAdjustment": 5.01,
      |  "balancingCharge": 5.02,
      |  "bpraBalancingCharge": 5.03
      |}
      |""".stripMargin
  )

  val mtdSummaryCalculationDeductionsFhlJson: JsValue = Json.parse(
    """
      |{
      |  "annualInvestmentAllowance": 6.02,
      |  "businessPremisesRenovationAllowance": 6.04,
      |  "propertyAllowance": 6.05,
      |  "otherCapitalAllowance": 6.06,
      |  "rarReliefClaimed": 6.07,
      |  "electricChargePointAllowance": 6.08,
      |  "zeroEmissionsCarAllowance": 6.11
      |}
      |""".stripMargin
  )

  val mtdSummaryCalculationDeductionsNonFhlJson: JsValue = Json.parse(
    """
      |{
      |  "zeroEmissionGoods": 6.01,
      |  "annualInvestmentAllowance": 6.02,
      |  "costOfReplacingDomesticItems": 6.03,
      |  "businessPremisesRenovationAllowance": 6.04,
      |  "propertyAllowance": 6.05,
      |  "otherCapitalAllowance": 6.06,
      |  "rarReliefClaimed": 6.07,
      |  "electricChargePointAllowance": 6.08,
      |  "structuredBuildingAllowance": 6.09,
      |  "enhancedStructuredBuildingAllowance": 6.10,
      |  "zeroEmissionsCarAllowance": 6.11
      |}
      |""".stripMargin
  )

  val mtdSummaryCalculationFhlJson: JsValue = Json.parse(
    s"""
       |{
       |  "totalIncome": 1,
       |  "income": $mtdSummaryCalculationIncomeFhlJson,
       |  "totalExpenses": 2,
       |  "expenses": $mtdSummaryCalculationExpensesFhlJson,
       |  "netProfit": 3,
       |  "netLoss": 4,
       |  "totalAdditions": 5,
       |  "additions": $mtdSummaryCalculationAdditionsJson,
       |  "totalDeductions": 6,
       |  "deductions": $mtdSummaryCalculationDeductionsFhlJson,
       |  "taxableProfit": 7,
       |  "adjustedIncomeTaxLoss": 8
       |}
       |""".stripMargin
  )

  val mtdSummaryCalculationNonFhlJson: JsValue = Json.parse(
    s"""
       |{
       |  "totalIncome": 1,
       |  "income": $mtdSummaryCalculationIncomeNonFhlJson,
       |  "totalExpenses": 2,
       |  "expenses": $mtdSummaryCalculationExpensesNonFhlJson,
       |  "netProfit": 3,
       |  "netLoss": 4,
       |  "totalAdditions": 5,
       |  "additions": $mtdSummaryCalculationAdditionsJson,
       |  "totalDeductions": 6,
       |  "deductions": $mtdSummaryCalculationDeductionsNonFhlJson,
       |  "taxableProfit": 7,
       |  "adjustedIncomeTaxLoss": 8
       |}
       |""".stripMargin
  )

  val mtdAdjustmentsIncomeFhlJson: JsValue = Json.parse(
    """
      |{
      |  "totalRentsReceived": 1.05
      |}
      |""".stripMargin
  )

  val mtdAdjustmentsIncomeNonFhlJson: JsValue = Json.parse(
    """
      |{
      |  "totalRentsReceived": 1.01,
      |  "premiumsOfLeaseGrant": 1.02,
      |  "reversePremiums": 1.03,
      |  "otherPropertyIncome": 1.04
      |}
      |""".stripMargin
  )

  val mtdAdjustmentsExpensesFhlJson: JsValue = Json.parse(
    """
      |{
      |    "consolidatedExpenses": 2.01,
      |    "premisesRunningCosts": 2.02,
      |    "repairsAndMaintenance": 2.03,
      |    "financialCosts": 2.04,
      |    "professionalFees": 2.05,
      |    "costOfServices": 2.06,
      |    "other": 2.08,
      |    "travelCosts": 2.09
      |}
      |""".stripMargin
  )

  val mtdAdjustmentsExpensesNonFhlJson: JsValue = Json.parse(
    """
      |{
      |    "consolidatedExpenses": 2.01,
      |    "premisesRunningCosts": 2.02,
      |    "repairsAndMaintenance": 2.03,
      |    "financialCosts": 2.04,
      |    "professionalFees": 2.05,
      |    "costOfServices": 2.06,
      |    "residentialFinancialCost": 2.07,
      |    "other": 2.08,
      |    "travelCosts": 2.09
      |}
      |""".stripMargin
  )

  val mtdAdjustmentsFhlJson: JsValue = Json.parse(
    s"""
       |{
       |  "income": $mtdAdjustmentsIncomeFhlJson,
       |  "expenses": $mtdAdjustmentsExpensesFhlJson
       |}
       |""".stripMargin
  )

  val mtdAdjustmentsNonFhlJson: JsValue = Json.parse(
    s"""
       |{
       |  "income": $mtdAdjustmentsIncomeNonFhlJson,
       |  "expenses": $mtdAdjustmentsExpensesNonFhlJson
       |}
       |""".stripMargin
  )

  val mtdRetrieveBsasResponseFhlJson: JsValue = Json.parse(
    s"""
       |{
       |  "metadata": $mtdMetadataJson,
       |  "inputs": $mtdInputsFhlJson,
       |  "adjustableSummaryCalculation": $mtdSummaryCalculationFhlJson,
       |  "adjustments": $mtdAdjustmentsFhlJson,
       |  "adjustedSummaryCalculation": $mtdSummaryCalculationFhlJson
       |}
       |""".stripMargin
  )

  val mtdRetrieveBsasResponseNonFhlJson: JsValue = Json.parse(
    s"""
       |{
       |  "metadata": $mtdMetadataJson,
       |  "inputs": $mtdInputsNonFhlJson,
       |  "adjustableSummaryCalculation": $mtdSummaryCalculationNonFhlJson,
       |  "adjustments": $mtdAdjustmentsNonFhlJson,
       |  "adjustedSummaryCalculation": $mtdSummaryCalculationNonFhlJson
       |}
       |""".stripMargin
  )

  /*val parsedMetadata: Metadata = Metadata(
    calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    requestedDateTime = "2000-01-01T10:12:10Z",
    adjustedDateTime = Some("2000-01-01T10:12:10Z"),
    nino = "AA999999A",
    taxYear = "2020-21",
    summaryStatus = Status.`valid`
  )

  val submissionPeriodWithPeriodId: SubmissionPeriod = SubmissionPeriod(
    periodId = Some("1234567890123456"),
    submissionId = None,
    startDate = now,
    endDate = aYearFromNow,
    receivedDateTime = "2000-01-01T10:12:10Z"
  )

  val submissionPeriodWithSubmissionId: SubmissionPeriod = SubmissionPeriod(
    periodId = None,
    submissionId = Some(s"${now}_$aYearFromNow"),
    startDate = now,
    endDate = aYearFromNow,
    receivedDateTime = "2000-01-01T10:12:10Z"
  )

  val inputsFhl: Inputs = Inputs(
    typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
    businessId = "XAIS12345678910",
    businessName = Some("Business Name"),
    accountingPeriodStartDate = now,
    accountingPeriodEndDate = aYearFromNow,
    source = Source.`MTD-SA`,
    submissionPeriods = List(submissionPeriodWithPeriodId, submissionPeriodWithSubmissionId)
  )

  val inputsNonFhl: Inputs = Inputs(
    typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`,
    businessId = "XAIS12345678910",
    businessName = Some("Business Name"),
    accountingPeriodStartDate = now,
    accountingPeriodEndDate = aYearFromNow,
    source = Source.`MTD-SA`,
    submissionPeriods = List(submissionPeriodWithPeriodId, submissionPeriodWithSubmissionId)
  )

  val summaryCalculationIncomeFhl: SummaryCalculationIncome = SummaryCalculationIncome(
    totalRentsReceived = Some(1.05),
    premiumsOfLeaseGrant = None,
    reversePremiums = None,
    otherPropertyIncome = None,
    rarRentReceived = Some(1.06)
  )

  val summaryCalculationIncomeNonFhl: SummaryCalculationIncome = SummaryCalculationIncome(
    totalRentsReceived = Some(1.01),
    premiumsOfLeaseGrant = Some(1.02),
    reversePremiums = Some(1.03),
    otherPropertyIncome = Some(1.04),
    rarRentReceived = Some(1.06)
  )

  val summaryCalculationExpensesFhl: SummaryCalculationExpenses = SummaryCalculationExpenses(
    consolidatedExpenses = Some(2.01),
    premisesRunningCosts = Some(2.02),
    repairsAndMaintenance = Some(2.03),
    financialCosts = Some(2.04),
    professionalFees = Some(2.05),
    costOfServices = Some(2.06),
    residentialFinancialCost = None,
    broughtFwdResidentialFinancialCost = None,
    other = Some(2.09),
    travelCosts = Some(2.10)
  )

  val summaryCalculationExpensesNonFhl: SummaryCalculationExpenses = SummaryCalculationExpenses(
    consolidatedExpenses = Some(2.01),
    premisesRunningCosts = Some(2.02),
    repairsAndMaintenance = Some(2.03),
    financialCosts = Some(2.04),
    professionalFees = Some(2.05),
    costOfServices = Some(2.06),
    residentialFinancialCost = Some(2.07),
    broughtFwdResidentialFinancialCost = Some(2.08),
    other = Some(2.09),
    travelCosts = Some(2.10)
  )

  val summaryCalculationAdditions: SummaryCalculationAdditions = SummaryCalculationAdditions(
    privateUseAdjustment = Some(5.01),
    balancingCharge = Some(5.02),
    bpraBalancingCharge = Some(5.03)
  )

  val summaryCalculationDeductionsFhl: SummaryCalculationDeductions = SummaryCalculationDeductions(
    zeroEmissionGoods = None,
    annualInvestmentAllowance = Some(6.02),
    costOfReplacingDomesticItems = None,
    businessPremisesRenovationAllowance = Some(6.04),
    propertyAllowance = Some(6.05),
    otherCapitalAllowance = Some(6.06),
    rarReliefClaimed = Some(6.07),
    electricChargePointAllowance = Some(6.08),
    structuredBuildingAllowance = None,
    enhancedStructuredBuildingAllowance = None,
    zeroEmissionsCarAllowance = Some(6.11)
  )

  val summaryCalculationDeductionsNonFhl: SummaryCalculationDeductions = SummaryCalculationDeductions(
    zeroEmissionGoods = Some(6.01),
    annualInvestmentAllowance = Some(6.02),
    costOfReplacingDomesticItems = Some(6.03),
    businessPremisesRenovationAllowance = Some(6.04),
    propertyAllowance = Some(6.05),
    otherCapitalAllowance = Some(6.06),
    rarReliefClaimed = Some(6.07),
    electricChargePointAllowance = Some(6.08),
    structuredBuildingAllowance = Some(6.09),
    enhancedStructuredBuildingAllowance = Some(6.10),
    zeroEmissionsCarAllowance = Some(6.11)
  )

  val adjustableSummaryCalculationFhl: AdjustableSummaryCalculation = AdjustableSummaryCalculation(
    totalIncome = Some(1),
    income = Some(summaryCalculationIncomeFhl),
    totalExpenses = Some(2),
    expenses = Some(summaryCalculationExpensesFhl),
    netProfit = Some(3),
    netLoss = Some(4),
    totalAdditions = Some(5),
    additions = Some(summaryCalculationAdditions),
    totalDeductions = Some(6),
    deductions = Some(summaryCalculationDeductionsFhl),
    taxableProfit = Some(7),
    adjustedIncomeTaxLoss = Some(8)
  )

  val adjustableSummaryCalculationNonFhl: AdjustableSummaryCalculation = AdjustableSummaryCalculation(
    totalIncome = Some(1),
    income = Some(summaryCalculationIncomeNonFhl),
    totalExpenses = Some(2),
    expenses = Some(summaryCalculationExpensesNonFhl),
    netProfit = Some(3),
    netLoss = Some(4),
    totalAdditions = Some(5),
    additions = Some(summaryCalculationAdditions),
    totalDeductions = Some(6),
    deductions = Some(summaryCalculationDeductionsNonFhl),
    taxableProfit = Some(7),
    adjustedIncomeTaxLoss = Some(8)
  )

  val adjustmentsIncomeFhl: AdjustmentsIncome = AdjustmentsIncome(
    totalRentsReceived = Some(1.05),
    premiumsOfLeaseGrant = None,
    reversePremiums = None,
    otherPropertyIncome = None
  )

  val adjustmentsIncomeNonFhl: AdjustmentsIncome = AdjustmentsIncome(
    totalRentsReceived = Some(1.01),
    premiumsOfLeaseGrant = Some(1.02),
    reversePremiums = Some(1.03),
    otherPropertyIncome = Some(1.04)
  )

  val adjustmentsExpensesFhl: AdjustmentsExpenses = AdjustmentsExpenses(
    consolidatedExpenses = Some(2.01),
    premisesRunningCosts = Some(2.02),
    repairsAndMaintenance = Some(2.03),
    financialCosts = Some(2.04),
    professionalFees = Some(2.05),
    costOfServices = Some(2.06),
    residentialFinancialCost = None,
    other = Some(2.08),
    travelCosts = Some(2.09)
  )

  val adjustmentsExpensesNonFhl: AdjustmentsExpenses = AdjustmentsExpenses(
    consolidatedExpenses = Some(2.01),
    premisesRunningCosts = Some(2.02),
    repairsAndMaintenance = Some(2.03),
    financialCosts = Some(2.04),
    professionalFees = Some(2.05),
    costOfServices = Some(2.06),
    residentialFinancialCost = Some(2.07),
    other = Some(2.08),
    travelCosts = Some(2.09)
  )

  val adjustmentsFhl: Adjustments = Adjustments(
    income = Some(adjustmentsIncomeFhl),
    expenses = Some(adjustmentsExpensesFhl)
  )

  val adjustmentsNonFhl: Adjustments = Adjustments(
    income = Some(adjustmentsIncomeNonFhl),
    expenses = Some(adjustmentsExpensesNonFhl)
  )

  val adjustedSummaryCalculationFhl: AdjustedSummaryCalculation = AdjustedSummaryCalculation(
    totalIncome = Some(1),
    income = Some(summaryCalculationIncomeFhl),
    totalExpenses = Some(2),
    expenses = Some(summaryCalculationExpensesFhl),
    netProfit = Some(3),
    netLoss = Some(4),
    totalAdditions = Some(5),
    additions = Some(summaryCalculationAdditions),
    totalDeductions = Some(6),
    deductions = Some(summaryCalculationDeductionsFhl),
    taxableProfit = Some(7),
    adjustedIncomeTaxLoss = Some(8)
  )

  val adjustedSummaryCalculationNonFhl: AdjustedSummaryCalculation = AdjustedSummaryCalculation(
    totalIncome = Some(1),
    income = Some(summaryCalculationIncomeNonFhl),
    totalExpenses = Some(2),
    expenses = Some(summaryCalculationExpensesNonFhl),
    netProfit = Some(3),
    netLoss = Some(4),
    totalAdditions = Some(5),
    additions = Some(summaryCalculationAdditions),
    totalDeductions = Some(6),
    deductions = Some(summaryCalculationDeductionsNonFhl),
    taxableProfit = Some(7),
    adjustedIncomeTaxLoss = Some(8)
  )

  val retrieveBsasResponseFhl: Def1_RetrieveUkPropertyBsasResponse = Def1_RetrieveUkPropertyBsasResponse(
    metadata = parsedMetadata,
    inputs = inputsFhl,
    adjustableSummaryCalculation = adjustableSummaryCalculationFhl,
    adjustments = Some(adjustmentsFhl),
    adjustedSummaryCalculation = Some(adjustedSummaryCalculationFhl)
  )

  val retrieveBsasResponseNonFhl: Def1_RetrieveUkPropertyBsasResponse = Def1_RetrieveUkPropertyBsasResponse(
    metadata = parsedMetadata,
    inputs = inputsNonFhl,
    adjustableSummaryCalculation = adjustableSummaryCalculationNonFhl,
    adjustments = Some(adjustmentsNonFhl),
    adjustedSummaryCalculation = Some(adjustedSummaryCalculationNonFhl)
  )

  def retrieveBsasResponseInvalidTypeOfBusiness(typeOfBusiness: TypeOfBusiness): Def1_RetrieveUkPropertyBsasResponse =
    Def1_RetrieveUkPropertyBsasResponse(
      metadata = parsedMetadata,
      inputs = inputsFhl
        /** EndMarker */
        .copy(typeOfBusiness = typeOfBusiness)
        .copy(typeOfBusiness = typeOfBusiness),
      adjustableSummaryCalculation = adjustableSummaryCalculationFhl,
      adjustments = Some(adjustmentsFhl),
      adjustedSummaryCalculation = Some(adjustedSummaryCalculationFhl)
    )*/

}
