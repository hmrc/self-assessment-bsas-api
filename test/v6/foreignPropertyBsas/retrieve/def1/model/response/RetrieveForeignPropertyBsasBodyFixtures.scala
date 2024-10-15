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

package v6.foreignPropertyBsas.retrieve.def1.model.response

import play.api.libs.json.{JsValue, Json}
import v6.common.model.TypeOfBusinessWithFHL

object RetrieveForeignPropertyBsasBodyFixtures {

  /* Downstream JSON */

  def metadataDesJson(taxYear: Int): JsValue = Json.parse(
    s"""{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "adjustedDateTime": "2020-12-05T16:19:44Z",
      |  "taxableEntityId": "AA999999A",
      |  "taxYear": $taxYear,
      |  "status": "valid"
      |}""".stripMargin
  )

  lazy val submissionPeriodDesJson: JsValue = Json.parse(
    """{
      |      "periodId": "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
      |      "startDate": "2019-04-06",
      |      "endDate": "2020-04-05",
      |      "receivedDateTime": "2019-02-15T09:35:04.843Z"
      |}""".stripMargin
  )

  lazy val inputsDesFhlJson: JsValue = Json.parse(
    s"""{
       |  "incomeSourceId": "000000000000210",
       |  "incomeSourceType": "03",
       |  "incomeSourceName": "Business Name",
       |  "accountingPeriodStartDate": "2019-04-06",
       |  "accountingPeriodEndDate": "2020-04-05",
       |  "source": "MTD-SA",
       |  "submissionPeriods": [$submissionPeriodDesJson]
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
       |  "submissionPeriods": [$submissionPeriodDesJson]
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
      |  "electricChargePointAllowance": 0.27,
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
       |  "adjustedIncomeTaxLoss": 1.13
       |}""".stripMargin
  )

  lazy val summaryCalculationDesFhlJson: JsValue = Json.parse(
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
       |  "adjustedIncomeTaxLoss": 0.31
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

  lazy val adjustmentsIncomeDesFhlJson: JsValue = Json.parse(
    """{
      |  "rent": 0.12
      |}""".stripMargin
  )

  lazy val adjustmentsIncomeDesJson: JsValue = Json.parse(
    """{
      |  "rentReceived": 0.12,
      |  "premiumsOfLeaseGrant": 0.13,
      |  "otherPropertyIncome": 0.14
      |}""".stripMargin
  )

  lazy val adjustmentsExpensesDesJson: JsValue = Json.parse(
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

  lazy val adjustmentsDesFhlJson: JsValue = Json.parse(
    s"""{
       |  "income": $adjustmentsIncomeDesFhlJson,
       |  "expenses": $adjustmentsExpensesDesJson
       |}""".stripMargin
  )

  lazy val adjustmentsDesJson: JsValue = Json.parse(
    s"""{
       |  "countryCode": "AFG",
       |  "income": $adjustmentsIncomeDesJson,
       |  "expenses": $adjustmentsExpensesDesJson
       |}""".stripMargin
  )

  def retrieveForeignPropertyBsasDesFhlJson(taxYear: Int): JsValue = Json.parse(
    s"""{
       |  "metadata": ${metadataDesJson(taxYear)},
       |  "inputs": $inputsDesFhlJson,
       |  "adjustableSummaryCalculation": $summaryCalculationDesFhlJson,
       |  "adjustments": $adjustmentsDesFhlJson,
       |  "adjustedSummaryCalculation": $summaryCalculationDesFhlJson
       |}""".stripMargin
  )

  def retrieveForeignPropertyBsasDesJson(taxYear: Int): JsValue = Json.parse(
    s"""{
       |  "metadata": ${metadataDesJson(taxYear)},
       |  "inputs": $inputsDesJson,
       |  "adjustableSummaryCalculation": $summaryCalculationDesJson,
       |  "adjustments": [$adjustmentsDesJson],
       |  "adjustedSummaryCalculation": $summaryCalculationDesJson
       |}""".stripMargin
  )

  /* MTD JSON */

  def metadataMtdJson(taxYear: String): JsValue = Json.parse(
    s"""{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "adjustedDateTime": "2020-12-05T16:19:44Z",
      |  "nino": "AA999999A",
      |  "taxYear": "$taxYear",
      |  "summaryStatus": "valid"
      |}""".stripMargin
  )

  lazy val submissionPeriodMtdJson: JsValue = Json.parse(
    """{
      |  "submissionId": "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
      |  "startDate": "2019-04-06",
      |  "endDate": "2020-04-05",
      |  "receivedDateTime": "2019-02-15T09:35:04.843Z"
      |}""".stripMargin
  )

  lazy val inputsMtdFhlJson: JsValue = Json.parse(
    s"""{
       |  "businessId": "000000000000210",
       |  "typeOfBusiness": "foreign-property-fhl-eea",
       |  "businessName": "Business Name",
       |  "accountingPeriodStartDate": "2019-04-06",
       |  "accountingPeriodEndDate": "2020-04-05",
       |  "source": "MTD-SA",
       |  "submissionPeriods": [$submissionPeriodMtdJson]
       |}""".stripMargin
  )

  lazy val inputsMtdJson: JsValue = Json.parse(
    s"""{
       |  "businessId": "000000000000210",
       |  "typeOfBusiness": "foreign-property",
       |  "businessName": "Business Name",
       |  "accountingPeriodStartDate": "2019-04-06",
       |  "accountingPeriodEndDate": "2020-04-05",
       |  "source": "MTD-SA",
       |  "submissionPeriods": [$submissionPeriodMtdJson]
       |}""".stripMargin
  )

  lazy val summaryCalculationIncomeMtdFhlJson: JsValue = Json.parse(
    """{
      |  "totalRentsReceived": 0.02
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

  lazy val summaryCalculationDeductionsMtdFhlJson: JsValue = Json.parse(
    """{
      |  "annualInvestmentAllowance": 0.22,
      |  "propertyAllowance": 0.25,
      |  "otherCapitalAllowance": 0.26,
      |  "electricChargePointAllowance": 0.27,
      |  "zeroEmissionsCarAllowance": 0.29
      |}""".stripMargin
  )

  lazy val summaryCalculationDeductionsMtdJson: JsValue = Json.parse(
    """{
      |  "annualInvestmentAllowance": 0.22,
      |  "costOfReplacingDomesticItems": 0.23,
      |  "zeroEmissionGoods": 0.24,
      |  "propertyAllowance": 0.25,
      |  "otherCapitalAllowance": 0.26,
      |  "electricChargePointAllowance": 0.27,
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
       |  "adjustedIncomeTaxLoss": 1.13
       |}""".stripMargin
  )

  lazy val summaryCalculationMtdFhlJson: JsValue = Json.parse(
    s"""{
       |  "totalIncome": 0.01,
       |  "income": $summaryCalculationIncomeMtdFhlJson,
       |  "totalExpenses": 0.05,
       |  "expenses": $summaryCalculationExpensesMtdJson,
       |  "netProfit": 0.16,
       |  "netLoss": 0.17,
       |  "totalAdditions": 0.18,
       |  "additions": $summaryCalculationAdditionsMtdJson,
       |  "totalDeductions": 0.21,
       |  "deductions": $summaryCalculationDeductionsMtdFhlJson,
       |  "taxableProfit": 0.30,
       |  "adjustedIncomeTaxLoss": 0.31
       |}""".stripMargin
  )

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

  lazy val adjustmentsIncomeMtdFhlJson: JsValue = Json.parse(
    """{
      |  "totalRentsReceived": 0.12
      |}""".stripMargin
  )

  lazy val adjustmentsIncomeMtdJson: JsValue = Json.parse(
    """{
      |  "totalRentsReceived": 0.12,
      |  "premiumsOfLeaseGrant": 0.13,
      |  "otherPropertyIncome": 0.14
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

  lazy val adjustmentsMtdFhlJson: JsValue = Json.parse(
    s"""{
       |  "income": $adjustmentsIncomeMtdFhlJson,
       |  "expenses": $adjustmentsExpensesMtdFhlJson
       |}""".stripMargin
  )

  lazy val adjustmentsCountryLevelDetailMtdJson: JsValue = Json.parse(
    s"""{
       |  "countryCode": "AFG",
       |  "income": $adjustmentsIncomeMtdJson,
       |  "expenses": $adjustmentsExpensesMtdJson
       |}""".stripMargin
  )

  lazy val adjustmentsMtdJson: JsValue = Json.parse(
    s"""{
       |  "countryLevelDetail": [$adjustmentsCountryLevelDetailMtdJson]
       |}""".stripMargin
  )

  def retrieveForeignPropertyBsasMtdFhlJson(taxYear: String): JsValue = Json.parse(
    s"""{
       |  "metadata": ${metadataMtdJson(taxYear)},
       |  "inputs": $inputsMtdFhlJson,
       |  "adjustableSummaryCalculation": $summaryCalculationMtdFhlJson,
       |  "adjustments": $adjustmentsMtdFhlJson,
       |  "adjustedSummaryCalculation": $summaryCalculationMtdFhlJson
       |}""".stripMargin
  )

  def retrieveForeignPropertyBsasMtdJson(taxYear: String): JsValue = Json.parse(
    s"""{
       |  "metadata": ${metadataMtdJson(taxYear)},
       |  "inputs": $inputsMtdJson,
       |  "adjustableSummaryCalculation": $summaryCalculationMtdJson,
       |  "adjustments": $adjustmentsMtdJson,
       |  "adjustedSummaryCalculation": $summaryCalculationMtdJson
       |}""".stripMargin
  )

  /* Parsed items */

  lazy val parsedMetadata: Metadata = Metadata(
    calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    requestedDateTime = "2020-12-05T16:19:44Z",
    adjustedDateTime = Some("2020-12-05T16:19:44Z"),
    nino = "AA999999A",
    taxYear = "2019-20",
    summaryStatus = "valid"
  )

  lazy val parsedSubmissionPeriod: SubmissionPeriods = SubmissionPeriods(
    submissionId = "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
    startDate = "2019-04-06",
    endDate = "2020-04-05",
    receivedDateTime = "2019-02-15T09:35:04.843Z"
  )

  lazy val parsedFhlInputs: Inputs = Inputs(
    businessId = "000000000000210",
    incomeSourceType = "03",
    typeOfBusiness = TypeOfBusinessWithFHL.`foreign-property-fhl-eea`,
    businessName = Some("Business Name"),
    accountingPeriodStartDate = "2019-04-06",
    accountingPeriodEndDate = "2020-04-05",
    source = "MTD-SA",
    submissionPeriods = List(parsedSubmissionPeriod)
  )

  lazy val parsedInputs: Inputs = Inputs(
    businessId = "000000000000210",
    incomeSourceType = "15",
    typeOfBusiness = TypeOfBusinessWithFHL.`foreign-property`,
    businessName = Some("Business Name"),
    accountingPeriodStartDate = "2019-04-06",
    accountingPeriodEndDate = "2020-04-05",
    source = "MTD-SA",
    submissionPeriods = List(parsedSubmissionPeriod)
  )

  lazy val parsedFhlSummaryCalculationIncome: SummaryCalculationIncome = SummaryCalculationIncome(
    totalRentsReceived = Some(0.02),
    premiumsOfLeaseGrant = None,
    otherPropertyIncome = None
  )

  lazy val parsedSummaryCalculationIncome: SummaryCalculationIncome = SummaryCalculationIncome(
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

  lazy val parsedSummaryCalculationAdditions: SummaryCalculationAdditions = SummaryCalculationAdditions(
    privateUseAdjustment = Some(0.19),
    balancingCharge = Some(0.20)
  )

  lazy val parsedFhlSummaryCalculationDeductions: SummaryCalculationDeductions = SummaryCalculationDeductions(
    annualInvestmentAllowance = Some(0.22),
    costOfReplacingDomesticItems = None,
    zeroEmissionGoods = None,
    propertyAllowance = Some(0.25),
    otherCapitalAllowance = Some(0.26),
    electricChargePointAllowance = Some(0.27),
    structuredBuildingAllowance = None,
    zeroEmissionsCarAllowance = Some(0.29)
  )

  lazy val parsedSummaryCalculationDeductions: SummaryCalculationDeductions = SummaryCalculationDeductions(
    annualInvestmentAllowance = Some(0.22),
    costOfReplacingDomesticItems = Some(0.23),
    zeroEmissionGoods = Some(0.24),
    propertyAllowance = Some(0.25),
    otherCapitalAllowance = Some(0.26),
    electricChargePointAllowance = Some(0.27),
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
    adjustedIncomeTaxLoss = Some(1.13)
  )

  lazy val parsedFhlsummaryCalculation: SummaryCalculation = SummaryCalculation(
    totalIncome = Some(0.01),
    income = Some(parsedFhlSummaryCalculationIncome),
    totalExpenses = Some(0.05),
    expenses = Some(parsedSummaryCalculationExpenses),
    netProfit = Some(0.16),
    netLoss = Some(0.17),
    totalAdditions = Some(0.18),
    additions = Some(parsedSummaryCalculationAdditions),
    totalDeductions = Some(0.21),
    deductions = Some(parsedFhlSummaryCalculationDeductions),
    taxableProfit = Some(0.30),
    adjustedIncomeTaxLoss = Some(0.31),
    countryLevelDetail = None
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

  lazy val parsedFhlAdjustmentsIncome: AdjustmentsIncome = AdjustmentsIncome(
    totalRentsReceived = Some(0.12),
    premiumsOfLeaseGrant = None,
    otherPropertyIncome = None
  )

  lazy val parsedAdjustmentsIncome: AdjustmentsIncome = AdjustmentsIncome(
    totalRentsReceived = Some(0.12),
    premiumsOfLeaseGrant = Some(0.13),
    otherPropertyIncome = Some(0.14)
  )

  lazy val parsedFhlAdjustmentsExpenses: AdjustmentsExpenses = AdjustmentsExpenses(
    consolidatedExpenses = Some(0.01),
    premisesRunningCosts = Some(0.08),
    repairsAndMaintenance = Some(0.02),
    financialCosts = Some(0.03),
    professionalFees = Some(0.04),
    costOfServices = Some(0.05),
    residentialFinancialCost = None,
    other = Some(0.07),
    travelCosts = Some(0.06)
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

  lazy val parsedFhlAdjustments: Adjustments = Adjustments(
    countryLevelDetail = None,
    countryCode = None,
    income = Some(parsedFhlAdjustmentsIncome),
    expenses = Some(parsedFhlAdjustmentsExpenses)
  )

  lazy val parsedAdjustments: Adjustments = Adjustments(
    countryLevelDetail = None,
    countryCode = Some("AFG"),
    income = Some(parsedAdjustmentsIncome),
    expenses = Some(parsedAdjustmentsExpenses)
  )

  lazy val parsedAdjustmentsSeq: Adjustments = Adjustments(
    countryLevelDetail = Some(
      List(
        Adjustments(
          countryLevelDetail = None,
          countryCode = parsedAdjustments.countryCode,
          income = parsedAdjustments.income,
          expenses = parsedAdjustments.expenses
        ))),
    countryCode = None,
    income = None,
    expenses = None
  )

  lazy val parsedFhlRetrieveForeignPropertyBsasResponse: Def1_RetrieveForeignPropertyBsasResponse = Def1_RetrieveForeignPropertyBsasResponse(
    metadata = parsedMetadata,
    inputs = parsedFhlInputs,
    adjustableSummaryCalculation = parsedFhlsummaryCalculation,
    adjustments = Some(parsedFhlAdjustments),
    adjustedSummaryCalculation = Some(parsedFhlsummaryCalculation)
  )

  lazy val parsedRetrieveForeignPropertyBsasResponse: Def1_RetrieveForeignPropertyBsasResponse = Def1_RetrieveForeignPropertyBsasResponse(
    metadata = parsedMetadata,
    inputs = parsedInputs,
    adjustableSummaryCalculation = parsedSummaryCalculation,
    adjustments = Some(parsedAdjustmentsSeq),
    adjustedSummaryCalculation = Some(parsedSummaryCalculation)
  )

  def parsedRetrieveForeignPropertyBsasResponseWith(typeOfBusinessWithFHL: TypeOfBusinessWithFHL): Def1_RetrieveForeignPropertyBsasResponse =
    Def1_RetrieveForeignPropertyBsasResponse(
      metadata = parsedMetadata,
      inputs = parsedInputs.copy(typeOfBusiness = typeOfBusinessWithFHL),
      adjustableSummaryCalculation = parsedSummaryCalculation,
      adjustments = Some(parsedAdjustmentsSeq),
      adjustedSummaryCalculation = Some(parsedSummaryCalculation)
    )

}
