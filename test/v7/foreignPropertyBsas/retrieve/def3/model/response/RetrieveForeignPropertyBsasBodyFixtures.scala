/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.foreignPropertyBsas.retrieve.def3.model.response

import play.api.libs.json.{JsValue, Json}

object RetrieveForeignPropertyBsasBodyFixtures {

  /* Downstream JSON */

  lazy val metadataHipJson: JsValue = Json.parse(
    """{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "adjustedDateTime": "2020-12-05T16:19:44Z",
      |  "taxableEntityId": "AA999999A",
      |  "taxYear": 2024,
      |  "status": "valid"
      |}""".stripMargin
  )

  lazy val submissionPeriodDesJson: JsValue = Json.parse(
    """{
      |      "submissionId": "64cc4efc-d8a9-4c4d-96a8-2be26eb169f6",
      |      "startDate": "2019-04-06",
      |      "endDate": "2020-04-05",
      |      "receivedDateTime": "2019-02-15T09:35:04.843Z"
      |}""".stripMargin
  )

  lazy val inputsDesJson: JsValue = Json.parse(
    s"""{
       |  "incomeSourceId": "000000000000210",
       |  "incomeSourceType": "15",
       |  "incomeSourceName": "Business Name",
       |  "accountingPeriodStartDate": "2019-04-06",
       |  "accountingPeriodEndDate": "2020-04-05",
       |  "source": "MTD-SA",
       |  "submissionPeriod": $submissionPeriodDesJson
       |}""".stripMargin
  )

  lazy val summaryCalculationIncomeDesJson: JsValue = Json.parse(
    """{
      |  "rent": 0.02,
      |  "premiumsOfLeaseGrant": 0.03,
      |  "otherPropertyIncome": 0.04
      |}""".stripMargin
  )

  lazy val summaryCalculationExpensesDesJson: JsValue = Json.parse(
    """{
      |  "consolidatedExpenses": 0.06,
      |  "premisesRunningCosts": 0.07,
      |  "repairsAndMaintenance": 0.08,
      |  "financialCosts": 0.09,
      |  "professionalFees": 0.10,
      |  "travelCosts": 0.11,
      |  "costOfServices": 0.12,
      |  "residentialFinancialCost": 0.13,
      |  "broughtFwdResidentialFinancialCost": 0.14,
      |  "other": 0.15
      |}""".stripMargin
  )

  lazy val summaryCalculationAdditionsDesJson: JsValue = Json.parse(
    """{
      |  "privateUseAdjustment": 0.19,
      |  "balancingCharge": 0.20
      |}""".stripMargin
  )

  lazy val summaryCalculationDeductionsDesJson: JsValue = Json.parse(
    """{
      |  "annualInvestmentAllowance": 0.22,
      |  "costOfReplacingDomesticItems": 0.23,
      |  "zeroEmissionsGoodsVehicleAllowance": 0.24,
      |  "propertyAllowance": 0.25,
      |  "otherCapitalAllowance": 0.26,
      |  "structuredBuildingAllowance": 0.28,
      |  "zeroEmissionsCarAllowance": 0.29
      |}""".stripMargin
  )

  lazy val summaryCalculationCountryLevelDetailDesJson: JsValue = Json.parse(
    s"""{
       |  "countryCode": "AFG",
       |  "totalIncome": 0.01,
       |  "income": $summaryCalculationIncomeDesJson,
       |  "totalExpenses": 0.02,
       |  "expenses": $summaryCalculationExpensesDesJson,
       |  "netProfit": 0.03,
       |  "netLoss": 0.04,
       |  "totalAdditions": 0.05,
       |  "additions": $summaryCalculationAdditionsDesJson,
       |  "totalDeductions": 0.06,
       |  "deductions": $summaryCalculationDeductionsDesJson,
       |  "taxableProfit": 1.12,
       |  "adjustedIncomeTaxLoss": 1.13,
       |  "propertyLevelDetail": [$summaryCalculationPropertyLevelDetailHipJson]
       |}""".stripMargin
  )

  lazy val summaryCalculationPropertyLevelDetailHipJson: JsValue = Json.parse(
    s"""{
       |  "propertyId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
       |  "totalIncome": 0.01,
       |  "income": $summaryCalculationPropertyLevelIncomeHipJson,
       |  "totalExpenses": 0.02,
       |  "expenses": $summaryCalculationPropertyLevelExpensesHipJson,
       |  "netProfit": 0.03,
       |  "netLoss": 0.04,
       |  "totalAdditions": 0.05,
       |  "additions": $summaryCalculationPropertyLevelAdditionsHipJson,
       |  "totalDeductions": 0.06,
       |  "deductions": $summaryCalculationPropertyLevelDeductionsHipJson,
       |  "taxableProfit": 1.12,
       |  "adjustedIncomeTaxLoss": 1.13
       |}""".stripMargin
  )

  lazy val summaryCalculationPropertyLevelIncomeHipJson: JsValue = Json.parse(
    """{
      |  "rentAmount": 0.02,
      |  "premiumsOfLeaseGrantAmount": 0.03,
      |  "otherPropertyIncomeAmount": 0.04
      |}""".stripMargin
  )

  lazy val summaryCalculationPropertyLevelExpensesHipJson: JsValue = Json.parse(
    """{
      |  "premisesRunningCostsAmount": 0.07,
      |  "repairsAndMaintenanceAmount": 0.08,
      |  "financialCostsAmount": 0.09,
      |  "professionalFeesAmount": 0.10,
      |  "travelCostsAmount": 0.11,
      |  "costOfServicesAmount": 0.12,
      |  "residentialFinancialCostAmount": 0.13,
      |  "broughtFwdResidentialFinancialCostAmount": 0.14,
      |  "otherAmount": 0.15,
      |  "consolidatedExpenseAmount": 0.06
      |}""".stripMargin
  )

  lazy val summaryCalculationPropertyLevelAdditionsHipJson: JsValue = Json.parse(
    """{
      |  "privateUseAdjustment": 0.19,
      |  "balancingCharge": 0.20
      |}""".stripMargin
  )

  lazy val summaryCalculationPropertyLevelDeductionsHipJson: JsValue = Json.parse(
    """{
      |  "annualInvestmentAllowance": 0.22,
      |  "costOfReplacingDomesticItems": 0.23,
      |  "zeroEmissionsGoodsVehicleAllowance": 0.24,
      |  "propertyAllowance": 0.25,
      |  "otherCapitalAllowance": 0.26,
      |  "structuredBuildingAllowance": 0.28,
      |  "zeroEmissionsCarAllowance": 0.29
      |}""".stripMargin
  )

  lazy val summaryCalculationDesJson: JsValue = Json.parse(
    s"""{
       |  "totalIncome": 0.01,
       |  "income": $summaryCalculationIncomeDesJson,
       |  "totalExpenses": 0.05,
       |  "expenses": $summaryCalculationExpensesDesJson,
       |  "netProfit": 0.16,
       |  "netLoss": 0.17,
       |  "totalAdditions": 0.18,
       |  "additions": $summaryCalculationAdditionsDesJson,
       |  "totalDeductions": 0.21,
       |  "deductions": $summaryCalculationDeductionsDesJson,
       |  "taxableProfit": 0.30,
       |  "adjustedIncomeTaxLoss": 0.31,
       |  "countryLevelDetail": [$summaryCalculationCountryLevelDetailDesJson]
       |}""".stripMargin
  )

  lazy val adjustmentsIncomeHipJson: JsValue = Json.parse(
    """{
      |  "rentReceived": 0.12,
      |  "premiumsOfLeaseGrant": 0.13,
      |  "otherPropertyIncome": 0.14
      |}""".stripMargin
  )

  lazy val adjustmentsExpensesHipJson: JsValue = Json.parse(
    """{
      |  "consolidatedExpenses": 0.01,
      |  "repairsAndMaintenance": 0.02,
      |  "financialCosts": 0.03,
      |  "professionalFees": 0.04,
      |  "costOfServices": 0.05,
      |  "travelCosts": 0.06,
      |  "other": 0.07,
      |  "premisesRunningCosts": 0.08,
      |  "residentialFinancialCost": 0.09
      |}""".stripMargin
  )

  lazy val adjustmentsHipJson: JsValue = Json.parse(
    s"""{
       |  "propertyId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
       |  "income": $adjustmentsIncomeHipJson,
       |  "expenses": $adjustmentsExpensesHipJson
       |}""".stripMargin
  )

  lazy val zeroAdjustmentsDesJson: JsValue = Json.parse(
    """{
       |  "zeroAdjustments": true
       |}""".stripMargin
  )

  lazy val retrieveForeignPropertyBsasHipJson: JsValue = Json.parse(
    s"""{
       |  "metadata": $metadataHipJson,
       |  "inputs": $inputsDesJson,
       |  "adjustableSummaryCalculation": $summaryCalculationDesJson,
       |  "adjustments": [$adjustmentsHipJson],
       |  "adjustedSummaryCalculation": $summaryCalculationDesJson
       |}""".stripMargin
  )

  lazy val retrieveForeignPropertyBsasDesZeroAdjustmentJson: JsValue = Json.parse(
    s"""{
       |  "metadata": $metadataHipJson,
       |  "inputs": $inputsDesJson,
       |  "adjustableSummaryCalculation": $summaryCalculationDesJson,
       |  "adjustments": $zeroAdjustmentsDesJson,
       |  "adjustedSummaryCalculation": $summaryCalculationDesJson
       |}""".stripMargin
  )

  /* MTD JSON */

  lazy val metadataMtdJson: JsValue = Json.parse(
    """{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "adjustedDateTime": "2020-12-05T16:19:44Z",
      |  "nino": "AA999999A",
      |  "taxYear": "2023-24",
      |  "summaryStatus": "valid"
      |}""".stripMargin
  )

  lazy val submissionPeriodMtdJson: JsValue = Json.parse(
    """{
      |  "submissionId": "64cc4efc-d8a9-4c4d-96a8-2be26eb169f6",
      |  "startDate": "2019-04-06",
      |  "endDate": "2020-04-05",
      |  "receivedDateTime": "2019-02-15T09:35:04.843Z"
      |}""".stripMargin
  )

  lazy val inputsMtdJson: JsValue = Json.parse(
    s"""{
       |  "businessId": "000000000000210",
       |  "businessName": "Business Name",
       |  "accountingPeriodStartDate": "2019-04-06",
       |  "accountingPeriodEndDate": "2020-04-05",
       |  "source": "MTD-SA",
       |  "submissionPeriod": $submissionPeriodMtdJson
       |}""".stripMargin
  )

  lazy val summaryCalculationIncomeMtdJson: JsValue = Json.parse(
    """{
      |  "totalRentsReceived": 0.02,
      |  "premiumsOfLeaseGrant": 0.03,
      |  "otherPropertyIncome": 0.04
      |}""".stripMargin
  )

  lazy val summaryCalculationExpensesMtdJson: JsValue = Json.parse(
    """{
      |  "consolidatedExpenses": 0.06,
      |  "premisesRunningCosts": 0.07,
      |  "repairsAndMaintenance": 0.08,
      |  "financialCosts": 0.09,
      |  "professionalFees": 0.10,
      |  "travelCosts": 0.11,
      |  "costOfServices": 0.12,
      |  "residentialFinancialCost": 0.13,
      |  "broughtFwdResidentialFinancialCost": 0.14,
      |  "other": 0.15
      |}""".stripMargin
  )

  lazy val summaryCalculationAdditionsMtdJson: JsValue = Json.parse(
    """{
      |  "privateUseAdjustment": 0.19,
      |  "balancingCharge": 0.20
      |}""".stripMargin
  )

  lazy val summaryCalculationDeductionsMtdJson: JsValue = Json.parse(
    """{
      |  "annualInvestmentAllowance": 0.22,
      |  "costOfReplacingDomesticItems": 0.23,
      |  "zeroEmissionGoods": 0.24,
      |  "propertyAllowance": 0.25,
      |  "otherCapitalAllowance": 0.26,
      |  "structuredBuildingAllowance": 0.28,
      |  "zeroEmissionsCarAllowance": 0.29
      |}""".stripMargin
  )

  lazy val summaryCalculationCountryLevelDetailMtdJson: JsValue = Json.parse(
    s"""{
       |  "countryCode": "AFG",
       |  "totalIncome": 0.01,
       |  "income": $summaryCalculationIncomeMtdJson,
       |  "totalExpenses": 0.02,
       |  "expenses": $summaryCalculationExpensesMtdJson,
       |  "netProfit": 0.03,
       |  "netLoss": 0.04,
       |  "totalAdditions": 0.05,
       |  "additions": $summaryCalculationAdditionsMtdJson,
       |  "totalDeductions": 0.06,
       |  "deductions": $summaryCalculationDeductionsMtdJson,
       |  "taxableProfit": 1.12,
       |  "adjustedIncomeTaxLoss": 1.13,
       |  "propertyLevelDetail": [$summaryCalculationPropertyLevelDetailMtdJson]
       |}""".stripMargin
  )

  lazy val summaryCalcPropLevelIncomeMtdJson: JsValue = Json.parse("""
      |{
      | "totalRentsReceived": 0.02,
      | "premiumsOfLeaseGrant": 0.03,
      | "otherPropertyIncome": 0.04
      |}
      |""".stripMargin)

  lazy val summaryCalcPropLevelExpensesMtdJson: JsValue = Json.parse("""
      |{
      | "premisesRunningCosts": 0.07,
      | "repairsAndMaintenance": 0.08,
      | "financialCosts": 0.09,
      | "professionalFees": 0.10,
      | "travelCosts": 0.11,
      | "costOfServices": 0.12,
      | "residentialFinancialCost": 0.13,
      | "broughtFwdResidentialFinancialCost": 0.14,
      | "other": 0.15,
      | "consolidatedExpenses": 0.06
      |}
      |""".stripMargin)

  lazy val summaryCalculationPropertyLevelDetailMtdJson: JsValue = Json.parse(s"""
       |{
       | "propertyId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
       | "totalIncome": 0.01,
       | "income": $summaryCalcPropLevelIncomeMtdJson,
       | "totalExpenses": 0.02,
       | "expenses": $summaryCalcPropLevelExpensesMtdJson,
       | "netProfit": 0.03,
       | "netLoss": 0.04,
       | "totalAdditions": 0.05,
       | "additions": $summaryCalculationAdditionsMtdJson,
       | "totalDeductions": 0.06,
       | "deductions":$summaryCalculationDeductionsMtdJson,
       | "taxableProfit": 1.12,
       | "adjustedIncomeTaxLoss": 1.13
       |}
       |""".stripMargin)

  lazy val summaryCalculationMtdJson: JsValue = Json.parse(
    s"""{
       |  "totalIncome": 0.01,
       |  "income": $summaryCalculationIncomeMtdJson,
       |  "totalExpenses": 0.05,
       |  "expenses": $summaryCalculationExpensesMtdJson,
       |  "netProfit": 0.16,
       |  "netLoss": 0.17,
       |  "totalAdditions": 0.18,
       |  "additions": $summaryCalculationAdditionsMtdJson,
       |  "totalDeductions": 0.21,
       |  "deductions": $summaryCalculationDeductionsMtdJson,
       |  "taxableProfit": 0.30,
       |  "adjustedIncomeTaxLoss": 0.31,
       |  "countryLevelDetail": [$summaryCalculationCountryLevelDetailMtdJson]
       |}""".stripMargin
  )

  lazy val adjustmentsIncomeMtdJson: JsValue = Json.parse(
    """{
      |  "totalRentsReceived": 0.12,
      |  "premiumsOfLeaseGrant": 0.13,
      |  "otherPropertyIncome": 0.14
      |}""".stripMargin
  )

  lazy val adjustmentsExpensesMtdJson: JsValue = Json.parse(
    """{
      |  "consolidatedExpenses": 0.01,
      |  "repairsAndMaintenance": 0.02,
      |  "financialCosts": 0.03,
      |  "professionalFees": 0.04,
      |  "costOfServices": 0.05,
      |  "travelCosts": 0.06,
      |  "other": 0.07,
      |  "premisesRunningCosts": 0.08,
      |  "residentialFinancialCost": 0.09
      |}""".stripMargin
  )

  lazy val adjustmentsIncomeMtdFhlJson: JsValue = Json.parse(
    """{
      |  "totalRentsReceived": 0.12
      |}""".stripMargin
  )

  lazy val adjustmentsExpensesMtdFhlJson: JsValue = Json.parse(
    """{
      |  "consolidatedExpenses": 0.01,
      |  "repairsAndMaintenance": 0.02,
      |  "financialCosts": 0.03,
      |  "professionalFees": 0.04,
      |  "costOfServices": 0.05,
      |  "travelCosts": 0.06,
      |  "other": 0.07,
      |  "premisesRunningCosts": 0.08
      |}""".stripMargin
  )

  lazy val adjustmentsMtdFhlJson: JsValue = Json.parse(
    s"""{
       |  "income": $adjustmentsIncomeMtdFhlJson,
       |  "expenses": $adjustmentsExpensesMtdFhlJson
       |}""".stripMargin
  )

  lazy val adjustmentsPropertyLevelDetailMtdJson: JsValue = Json.parse(
    s"""{
       |  "propertyId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
       |  "income": $adjustmentsIncomeMtdJson,
       |  "expenses": $adjustmentsExpensesMtdJson
       |}""".stripMargin
  )

  lazy val zeroAdjustmentsMtdJson: JsValue = Json.parse(
    """{
       |  "zeroAdjustments": true
       |}""".stripMargin
  )

  lazy val adjustmentsMtdJson: JsValue = Json.parse(
    s"""{
       |  "propertyLevelDetail": [$adjustmentsPropertyLevelDetailMtdJson]
       |}""".stripMargin
  )

  lazy val retrieveForeignPropertyBsasMtdJson: JsValue = Json.parse(
    s"""{
       |  "metadata": $metadataMtdJson,
       |  "inputs": $inputsMtdJson,
       |  "adjustableSummaryCalculation": $summaryCalculationMtdJson,
       |  "adjustments": $adjustmentsMtdJson,
       |  "adjustedSummaryCalculation": $summaryCalculationMtdJson
       |}""".stripMargin
  )

  lazy val retrieveForeignPropertyBsasMtdZeroAdjustmentJson: JsValue = Json.parse(
    s"""{
       |  "metadata": $metadataMtdJson,
       |  "inputs": $inputsMtdJson,
       |  "adjustableSummaryCalculation": $summaryCalculationMtdJson,
       |  "adjustments": $zeroAdjustmentsMtdJson,
       |  "adjustedSummaryCalculation": $summaryCalculationMtdJson
       |}""".stripMargin
  )

  /* Parsed items */

  lazy val parsedMetadata: Metadata = Metadata(
    calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    requestedDateTime = "2020-12-05T16:19:44Z",
    adjustedDateTime = Some("2020-12-05T16:19:44Z"),
    nino = "AA999999A",
    taxYear = "2023-24",
    summaryStatus = "valid"
  )

  lazy val parsedSubmissionPeriod: SubmissionPeriod = SubmissionPeriod(
    submissionId = "64cc4efc-d8a9-4c4d-96a8-2be26eb169f6",
    startDate = "2019-04-06",
    endDate = "2020-04-05",
    receivedDateTime = "2019-02-15T09:35:04.843Z"
  )

  lazy val parsedInputs: Inputs = Inputs(
    businessId = "000000000000210",
    incomeSourceType = "15",
    businessName = Some("Business Name"),
    accountingPeriodStartDate = "2019-04-06",
    accountingPeriodEndDate = "2020-04-05",
    source = "MTD-SA",
    submissionPeriod = parsedSubmissionPeriod
  )

  lazy val parsedSummaryCalculationIncome: SummaryCalculationIncome = SummaryCalculationIncome(
    totalRentsReceived = Some(0.02),
    premiumsOfLeaseGrant = Some(0.03),
    otherPropertyIncome = Some(0.04)
  )

  lazy val parsedSummaryCalculationPropertyLevelIncome: SummaryCalculationPropertyLevelIncome = SummaryCalculationPropertyLevelIncome(
    totalRentsReceived = Some(0.02),
    premiumsOfLeaseGrant = Some(0.03),
    otherPropertyIncome = Some(0.04)
  )

  lazy val parsedSummaryCalculationExpenses: SummaryCalculationExpenses = SummaryCalculationExpenses(
    consolidatedExpenses = Some(0.06),
    premisesRunningCosts = Some(0.07),
    repairsAndMaintenance = Some(0.08),
    financialCosts = Some(0.09),
    professionalFees = Some(0.10),
    costOfServices = Some(0.12),
    residentialFinancialCost = Some(0.13),
    broughtFwdResidentialFinancialCost = Some(0.14),
    other = Some(0.15),
    travelCosts = Some(0.11)
  )

  lazy val parsedSummaryCalculationPropertyLevelExpenses: SummaryCalculationPropertyLevelExpenses = SummaryCalculationPropertyLevelExpenses(
    consolidatedExpenses = Some(0.06),
    premisesRunningCosts = Some(0.07),
    repairsAndMaintenance = Some(0.08),
    financialCosts = Some(0.09),
    professionalFees = Some(0.10),
    costOfServices = Some(0.12),
    residentialFinancialCost = Some(0.13),
    broughtFwdResidentialFinancialCost = Some(0.14),
    other = Some(0.15),
    travelCosts = Some(0.11)
  )

  lazy val parsedSummaryCalculationAdditions: SummaryCalculationAdditions = SummaryCalculationAdditions(
    privateUseAdjustment = Some(0.19),
    balancingCharge = Some(0.20)
  )

  lazy val parsedSummaryCalculationDeductions: SummaryCalculationDeductions = SummaryCalculationDeductions(
    annualInvestmentAllowance = Some(0.22),
    costOfReplacingDomesticItems = Some(0.23),
    zeroEmissionGoods = Some(0.24),
    propertyAllowance = Some(0.25),
    otherCapitalAllowance = Some(0.26),
    structuredBuildingAllowance = Some(0.28),
    zeroEmissionsCarAllowance = Some(0.29)
  )

  lazy val parsedSummaryCalculationCountryLevelDetail: SummaryCalculationCountryLevelDetail = SummaryCalculationCountryLevelDetail(
    countryCode = "AFG",
    totalIncome = Some(0.01),
    income = Some(parsedSummaryCalculationIncome),
    totalExpenses = Some(0.02),
    expenses = Some(parsedSummaryCalculationExpenses),
    netProfit = Some(0.03),
    netLoss = Some(0.04),
    totalAdditions = Some(0.05),
    additions = Some(parsedSummaryCalculationAdditions),
    totalDeductions = Some(0.06),
    deductions = Some(parsedSummaryCalculationDeductions),
    taxableProfit = Some(1.12),
    adjustedIncomeTaxLoss = Some(1.13),
    propertyLevelDetail = Some(Seq(parsedSummaryCalculationPropertyLevelDetail))
  )

  lazy val parsedSummaryCalculationPropertyLevelDetail: SummaryCalculationPropertyLevelDetail = SummaryCalculationPropertyLevelDetail(
    propertyId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    totalIncome = Some(0.01),
    income = Some(parsedSummaryCalculationPropertyLevelIncome),
    totalExpenses = Some(0.02),
    expenses = Some(parsedSummaryCalculationPropertyLevelExpenses),
    netProfit = Some(0.03),
    netLoss = Some(0.04),
    totalAdditions = Some(0.05),
    additions = Some(parsedSummaryCalculationAdditions),
    totalDeductions = Some(0.06),
    deductions = Some(parsedSummaryCalculationDeductions),
    taxableProfit = Some(1.12),
    adjustedIncomeTaxLoss = Some(1.13)
  )

  lazy val parsedSummaryCalculation: SummaryCalculation = SummaryCalculation(
    totalIncome = Some(0.01),
    income = Some(parsedSummaryCalculationIncome),
    totalExpenses = Some(0.05),
    expenses = Some(parsedSummaryCalculationExpenses),
    netProfit = Some(0.16),
    netLoss = Some(0.17),
    totalAdditions = Some(0.18),
    additions = Some(parsedSummaryCalculationAdditions),
    totalDeductions = Some(0.21),
    deductions = Some(parsedSummaryCalculationDeductions),
    taxableProfit = Some(0.30),
    adjustedIncomeTaxLoss = Some(0.31),
    countryLevelDetail = Some(List(parsedSummaryCalculationCountryLevelDetail))
  )

  lazy val parsedAdjustmentsIncome: AdjustmentsIncome = AdjustmentsIncome(
    totalRentsReceived = Some(0.12),
    premiumsOfLeaseGrant = Some(0.13),
    otherPropertyIncome = Some(0.14)
  )

  lazy val parsedAdjustmentsExpenses: AdjustmentsExpenses = AdjustmentsExpenses(
    consolidatedExpenses = Some(0.01),
    premisesRunningCosts = Some(0.08),
    repairsAndMaintenance = Some(0.02),
    financialCosts = Some(0.03),
    professionalFees = Some(0.04),
    costOfServices = Some(0.05),
    residentialFinancialCost = Some(0.09),
    other = Some(0.07),
    travelCosts = Some(0.06)
  )

  lazy val parsedAdjustments: Adjustments = Adjustments(
    propertyId = Some("717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"),
    income = Some(parsedAdjustmentsIncome),
    expenses = Some(parsedAdjustmentsExpenses),
    propertyLevelDetail = None,
    zeroAdjustments = None
  )

  lazy val zeroParsedAdjustments: Adjustments = Adjustments(
    propertyId = None,
    income = None,
    expenses = None,
    propertyLevelDetail = None,
    zeroAdjustments = Some(true)
  )

  lazy val parsedAdjustmentsSeq: Adjustments = Adjustments(
    propertyLevelDetail = Some(
      List(Adjustments(
        propertyLevelDetail = None,
        propertyId = parsedAdjustments.propertyId,
        income = parsedAdjustments.income,
        expenses = parsedAdjustments.expenses,
        zeroAdjustments = None
      ))),
    propertyId = None,
    income = None,
    expenses = None,
    zeroAdjustments = None
  )

  lazy val parsedRetrieveForeignPropertyBsasResponse: Def3_RetrieveForeignPropertyBsasResponse =
    Def3_RetrieveForeignPropertyBsasResponse(
      metadata = parsedMetadata,
      inputs = parsedInputs,
      adjustableSummaryCalculation = parsedSummaryCalculation,
      adjustments = Some(parsedAdjustmentsSeq),
      adjustedSummaryCalculation = Some(parsedSummaryCalculation)
    )

  lazy val parsedRetrieveForeignPropertyBsasZeroAdjustmentResponse: Def3_RetrieveForeignPropertyBsasResponse =
    Def3_RetrieveForeignPropertyBsasResponse(
      metadata = parsedMetadata,
      inputs = parsedInputs,
      adjustableSummaryCalculation = parsedSummaryCalculation,
      adjustments = Some(zeroParsedAdjustments),
      adjustedSummaryCalculation = Some(parsedSummaryCalculation)
    )

  def parsedRetrieveForeignPropertyBsasResponseWith(incomeSourceType: String): Def3_RetrieveForeignPropertyBsasResponse =
    Def3_RetrieveForeignPropertyBsasResponse(
      metadata = parsedMetadata,
      inputs = parsedInputs.copy(incomeSourceType = incomeSourceType),
      adjustableSummaryCalculation = parsedSummaryCalculation,
      adjustments = Some(parsedAdjustmentsSeq),
      adjustedSummaryCalculation = Some(parsedSummaryCalculation)
    )

}
