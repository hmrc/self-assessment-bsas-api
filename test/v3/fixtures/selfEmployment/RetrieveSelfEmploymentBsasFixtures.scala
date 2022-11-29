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

package v3.fixtures.selfEmployment

import play.api.libs.json.{ JsObject, JsValue, Json }
import v3.models.domain.{ IncomeSourceType, Source, Status, TypeOfBusiness }
import v3.models.response.retrieveBsas.selfEmployment._

object RetrieveSelfEmploymentBsasFixtures {

  private val now          = "2019-04-06"
  private val aYearFromNow = "2020-04-05"

  val downstreamMetadataJson: JsValue = Json.parse(
    """
      |{
      |  "calculationId": "03e3bc8b-910d-4f5b-88d7-b627c84f2ed7",
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

  val downstreamInputsJson: JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceType": "01",
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
      |  "turnover": 1.01,
      |  "other": 1.02
      |}
      |""".stripMargin
  )

  val downstreamSummaryCalculationExpensesJson: JsValue = Json.parse(
    """
       |{
       |  "consolidatedExpenses": 2.01,
       |  "costOfGoodsAllowable": 2.02,
       |  "paymentsToSubcontractorsAllowable": 2.03,
       |  "wagesAndStaffCostsAllowable": 2.04,
       |  "carVanTravelExpensesAllowable": 2.05,
       |  "premisesRunningCostsAllowable": 2.06,
       |  "maintenanceCostsAllowable": 2.07,
       |  "adminCostsAllowable": 2.08,
       |  "interestOnBankOtherLoansAllowable": 2.09,
       |  "financeChargesAllowable": 2.10,
       |  "irrecoverableDebtsAllowable": 2.11,
       |  "professionalFeesAllowable": 2.12,
       |  "depreciationAllowable": 2.13,
       |  "otherExpensesAllowable": 2.14,
       |  "advertisingCostsAllowable": 2.15,
       |  "businessEntertainmentCostsAllowable": 2.16
       |}
       |""".stripMargin
  )

  val downstreamSummaryCalculationAdditionsJson: JsValue = Json.parse(
    """
      |{
      |  "costOfGoodsDisallowable": 5.01,
      |  "paymentsToSubcontractorsDisallowable": 5.02,
      |  "wagesAndStaffCostsDisallowable": 5.03,
      |  "carVanTravelExpensesDisallowable": 5.04,
      |  "premisesRunningCostsDisallowable": 5.05,
      |  "maintenanceCostsDisallowable": 5.06,
      |  "adminCostsDisallowable": 5.07,
      |  "interestOnBankOtherLoansDisallowable": 5.08,
      |  "financeChargesDisallowable": 5.09,
      |  "irrecoverableDebtsDisallowable": 5.10,
      |  "professionalFeesDisallowable": 5.11,
      |  "depreciationDisallowable": 5.12,
      |  "otherExpensesDisallowable": 5.13,
      |  "advertisingCostsDisallowable": 5.14,
      |  "businessEntertainmentCostsDisallowable": 5.15,
      |  "outstandingBusinessIncome": 5.16,
      |  "balancingChargeOther": 5.17,
      |  "balancingChargeBpra": 5.18,
      |  "goodAndServicesOwnUse": 5.19
      |}
      |""".stripMargin
  )

  val downstreamSummaryCalculationDeductionsJson: JsValue = Json.parse(
    """
      |{
      |  "tradingAllowance": 6.01,
      |  "annualInvestmentAllowance": 6.02,
      |  "capitalAllowanceMainPool": 6.03,
      |  "capitalAllowanceSpecialRatePool": 6.04,
      |  "zeroEmissionGoods": 6.05,
      |  "businessPremisesRenovationAllowance": 6.06,
      |  "enhancedCapitalAllowance": 6.07,
      |  "allowanceOnSales": 6.08,
      |  "capitalAllowanceSingleAssetPool": 6.09,
      |  "includedNonTaxableProfits": 6.10,
      |  "electricChargePointAllowance": 6.11,
      |  "structuredBuildingAllowance": 6.12,
      |  "enhancedStructuredBuildingAllowance": 6.13,
      |  "zeroEmissionsCarAllowance": 6.14
      |}
      |""".stripMargin
  )

  val downstreamSummaryCalculationAccountingAdjustmentsJson: JsValue = Json.parse(
    """
      |{
      |  "basisAdjustment": 7.01,
      |  "overlapReliefUsed": 7.02,
      |  "accountingAdjustment": 7.03,
      |  "averagingAdjustment": 7.04
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
      |  "accountingAdjustments": 7.00,
      |  "selfEmploymentAccountingAdjustments": $downstreamSummaryCalculationAccountingAdjustmentsJson,
      |  "taxableProfit": 8.00,
      |  "adjustedIncomeTaxLoss": 9.00
      |}
      |""".stripMargin
  )

  val downstreamAdjustmentsIncomeJson: JsValue = Json.parse(
    """
      |{
      |  "turnover": 1.01,
      |  "other": 1.02
      |}
      |""".stripMargin
  )

  val downstreamAdjustmentsExpensesJson: JsValue = Json.parse(
    """
      |{
      |    "consolidatedExpenses": 2.01,
      |    "costOfGoodsAllowable": 2.02,
      |    "paymentsToSubcontractorsAllowable": 2.03,
      |    "wagesAndStaffCostsAllowable": 2.04,
      |    "carVanTravelExpensesAllowable": 2.05,
      |    "premisesRunningCostsAllowable": 2.06,
      |    "maintenanceCostsAllowable": 2.07,
      |    "adminCostsAllowable": 2.08,
      |    "interestOnBankOtherLoansAllowable": 2.09,
      |    "financeChargesAllowable": 2.10,
      |    "irrecoverableDebtsAllowable": 2.11,
      |    "professionalFeesAllowable": 2.12,
      |    "depreciationAllowable": 2.13,
      |    "otherExpensesAllowable": 2.14,
      |    "advertisingCostsAllowable": 2.15,
      |    "businessEntertainmentCostsAllowable": 2.16
      |}
      |""".stripMargin
  )

  val downstreamAdjustmentsAdditionsJson: JsValue = Json.parse(
    """
      |{
      |  "costOfGoodsDisallowable": 3.01,
      |  "paymentsToSubcontractorsDisallowable": 3.02,
      |  "wagesAndStaffCostsDisallowable": 3.03,
      |  "carVanTravelExpensesDisallowable": 3.04,
      |  "premisesRunningCostsDisallowable": 3.05,
      |  "maintenanceCostsDisallowable": 3.06,
      |  "adminCostsDisallowable": 3.07,
      |  "interestOnBankOtherLoansDisallowable": 3.08,
      |  "financeChargesDisallowable": 3.09,
      |  "irrecoverableDebtsDisallowable": 3.10,
      |  "professionalFeesDisallowable": 3.11,
      |  "depreciationDisallowable": 3.12,
      |  "otherExpensesDisallowable": 3.13,
      |  "advertisingCostsDisallowable": 3.14,
      |  "businessEntertainmentCostsDisallowable": 3.15
      |}
      |""".stripMargin
  )

  val downstreamAdjustmentsJson: JsValue = Json.parse(
    s"""
      |{
      |  "income": $downstreamAdjustmentsIncomeJson,
      |  "expenses": $downstreamAdjustmentsExpensesJson,
      |  "additions": $downstreamAdjustmentsAdditionsJson
      |}
      |""".stripMargin
  )

  val downstreamRetrieveBsasResponseJson: JsValue = Json.parse(s"""
       |{
       |  "metadata": $downstreamMetadataJson,
       |  "inputs": $downstreamInputsJson,
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
      |  "calculationId": "03e3bc8b-910d-4f5b-88d7-b627c84f2ed7",
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

  val mtdInputsJson: JsValue = Json.parse(
    s"""
       |{
       |  "typeOfBusiness": "self-employment",
       |  "businessId": "XAIS12345678910",
       |  "businessName": "Business Name",
       |  "accountingPeriodStartDate": "$now",
       |  "accountingPeriodEndDate": "$aYearFromNow",
       |  "source": "MTD-SA",
       |  "submissionPeriods": [$mtdSubmissionPeriodWithPeriodIdJson, $mtdSubmissionPeriodWithSubmissionIdJson]
       |}
       |""".stripMargin
  )

  val mtdSummaryCalculationIncomeJson: JsValue = Json.parse(
    """
      |{
      |  "turnover": 1.01,
      |  "other": 1.02
      |}
      |""".stripMargin
  )

  val mtdSummaryCalculationExpensesJson: JsValue = Json.parse(
    """
       |{
       |  "consolidatedExpenses": 2.01,
       |  "costOfGoodsAllowable": 2.02,
       |  "paymentsToSubcontractorsAllowable": 2.03,
       |  "wagesAndStaffCostsAllowable": 2.04,
       |  "carVanTravelExpensesAllowable": 2.05,
       |  "premisesRunningCostsAllowable": 2.06,
       |  "maintenanceCostsAllowable": 2.07,
       |  "adminCostsAllowable": 2.08,
       |  "interestOnBankOtherLoansAllowable": 2.09,
       |  "financeChargesAllowable": 2.10,
       |  "irrecoverableDebtsAllowable": 2.11,
       |  "professionalFeesAllowable": 2.12,
       |  "depreciationAllowable": 2.13,
       |  "otherExpensesAllowable": 2.14,
       |  "advertisingCostsAllowable": 2.15,
       |  "businessEntertainmentCostsAllowable": 2.16
       |}
       |""".stripMargin
  )

  val mtdSummaryCalculationAdditionsJson: JsValue = Json.parse(
    """
      |{
      |  "costOfGoodsDisallowable": 5.01,
      |  "paymentsToSubcontractorsDisallowable": 5.02,
      |  "wagesAndStaffCostsDisallowable": 5.03,
      |  "carVanTravelExpensesDisallowable": 5.04,
      |  "premisesRunningCostsDisallowable": 5.05,
      |  "maintenanceCostsDisallowable": 5.06,
      |  "adminCostsDisallowable": 5.07,
      |  "interestOnBankOtherLoansDisallowable": 5.08,
      |  "financeChargesDisallowable": 5.09,
      |  "irrecoverableDebtsDisallowable": 5.10,
      |  "professionalFeesDisallowable": 5.11,
      |  "depreciationDisallowable": 5.12,
      |  "otherExpensesDisallowable": 5.13,
      |  "advertisingCostsDisallowable": 5.14,
      |  "businessEntertainmentCostsDisallowable": 5.15,
      |  "outstandingBusinessIncome": 5.16,
      |  "balancingChargeOther": 5.17,
      |  "balancingChargeBpra": 5.18,
      |  "goodsAndServicesOwnUse": 5.19
      |}
      |""".stripMargin
  )

  val mtdSummaryCalculationDeductionsJson: JsValue = Json.parse(
    """
      |{
      |  "tradingAllowance": 6.01,
      |  "annualInvestmentAllowance": 6.02,
      |  "capitalAllowanceMainPool": 6.03,
      |  "capitalAllowanceSpecialRatePool": 6.04,
      |  "zeroEmissionGoods": 6.05,
      |  "businessPremisesRenovationAllowance": 6.06,
      |  "enhancedCapitalAllowance": 6.07,
      |  "allowanceOnSales": 6.08,
      |  "capitalAllowanceSingleAssetPool": 6.09,
      |  "includedNonTaxableProfits": 6.10,
      |  "electricChargePointAllowance": 6.11,
      |  "structuredBuildingAllowance": 6.12,
      |  "enhancedStructuredBuildingAllowance": 6.13,
      |  "zeroEmissionsCarAllowance": 6.14
      |}
      |""".stripMargin
  )

  val mtdSummaryCalculationAccountingAdjustmentsJson: JsValue = Json.parse(
    """
      |{
      |  "basisAdjustment": 7.01,
      |  "overlapReliefUsed": 7.02,
      |  "accountingAdjustment": 7.03,
      |  "averagingAdjustment": 7.04
      |}
      |""".stripMargin
  )

  val mtdSummaryCalculationJson: JsValue = Json.parse(
    s"""
       |{
       |  "totalIncome": 1,
       |  "income": $mtdSummaryCalculationIncomeJson,
       |  "totalExpenses": 2,
       |  "expenses": $mtdSummaryCalculationExpensesJson,
       |  "netProfit": 3,
       |  "netLoss": 4,
       |  "totalAdditions": 5,
       |  "additions": $mtdSummaryCalculationAdditionsJson,
       |  "totalDeductions": 6,
       |  "deductions": $mtdSummaryCalculationDeductionsJson,
       |  "totalAccountingAdjustments": 7,
       |  "accountingAdjustments": $mtdSummaryCalculationAccountingAdjustmentsJson,
       |  "taxableProfit": 8,
       |  "adjustedIncomeTaxLoss": 9
       |}
       |""".stripMargin
  )

  val mtdAdjustmentsIncomeJson: JsValue = Json.parse(
    """
      |{
      |  "turnover": 1.01,
      |  "other": 1.02
      |}
      |""".stripMargin
  )

  val mtdAdjustmentsExpensesJson: JsValue = Json.parse(
    """
      |{
      |    "consolidatedExpenses": 2.01,
      |    "costOfGoodsAllowable": 2.02,
      |    "paymentsToSubcontractorsAllowable": 2.03,
      |    "wagesAndStaffCostsAllowable": 2.04,
      |    "carVanTravelExpensesAllowable": 2.05,
      |    "premisesRunningCostsAllowable": 2.06,
      |    "maintenanceCostsAllowable": 2.07,
      |    "adminCostsAllowable": 2.08,
      |    "interestOnBankOtherLoansAllowable": 2.09,
      |    "financeChargesAllowable": 2.10,
      |    "irrecoverableDebtsAllowable": 2.11,
      |    "professionalFeesAllowable": 2.12,
      |    "depreciationAllowable": 2.13,
      |    "otherExpensesAllowable": 2.14,
      |    "advertisingCostsAllowable": 2.15,
      |    "businessEntertainmentCostsAllowable": 2.16
      |}
      |""".stripMargin
  )

  val mtdAdjustmentsAdditionsJson: JsValue = Json.parse(
    """
      |{
      |  "costOfGoodsDisallowable": 3.01,
      |  "paymentsToSubcontractorsDisallowable": 3.02,
      |  "wagesAndStaffCostsDisallowable": 3.03,
      |  "carVanTravelExpensesDisallowable": 3.04,
      |  "premisesRunningCostsDisallowable": 3.05,
      |  "maintenanceCostsDisallowable": 3.06,
      |  "adminCostsDisallowable": 3.07,
      |  "interestOnBankOtherLoansDisallowable": 3.08,
      |  "financeChargesDisallowable": 3.09,
      |  "irrecoverableDebtsDisallowable": 3.10,
      |  "professionalFeesDisallowable": 3.11,
      |  "depreciationDisallowable": 3.12,
      |  "otherExpensesDisallowable": 3.13,
      |  "advertisingCostsDisallowable": 3.14,
      |  "businessEntertainmentCostsDisallowable": 3.15
      |}
      |""".stripMargin
  )

  val mtdAdjustmentsJson: JsValue = Json.parse(
    s"""
       |{
       |  "income": $mtdAdjustmentsIncomeJson,
       |  "expenses": $mtdAdjustmentsExpensesJson,
       |  "additions": $mtdAdjustmentsAdditionsJson
       |}
       |""".stripMargin
  )

  val mtdRetrieveBsasResponseJson: JsValue = Json.parse(
    s"""
       |{
       |  "metadata": $mtdMetadataJson,
       |  "inputs": $mtdInputsJson,
       |  "adjustableSummaryCalculation": $mtdSummaryCalculationJson,
       |  "adjustments": $mtdAdjustmentsJson,
       |  "adjustedSummaryCalculation": $mtdSummaryCalculationJson
       |}
       |""".stripMargin
  )

  def mtdRetrieveBsasReponseJsonWithHateoas(nino: String, calculationId: String, taxYear: Option[String] = None): JsValue = {
    val taxYearParam = taxYear.fold("")("?taxYear=" + _)

    mtdRetrieveBsasResponseJson.as[JsObject] ++ Json.parse(s"""
      |{
      |  "links": [
      |    {
      |      "href": "/individuals/self-assessment/adjustable-summary/$nino/self-employment/$calculationId$taxYearParam",
      |      "method": "GET",
      |      "rel": "self"
      |    }, {
      |      "href": "/individuals/self-assessment/adjustable-summary/$nino/self-employment/$calculationId/adjust$taxYearParam",
      |      "method": "POST",
      |      "rel": "submit-self-employment-accounting-adjustments"
      |    }
      |  ]
      |}
      |""".stripMargin).as[JsObject]
  }

  val metadataModel: Metadata = Metadata(
    calculationId = "03e3bc8b-910d-4f5b-88d7-b627c84f2ed7",
    requestedDateTime = "2000-01-01T10:12:10Z",
    adjustedDateTime = Some("2000-01-01T10:12:10Z"),
    nino = "AA999999A",
    taxYear = "2020-21",
    summaryStatus = Status.`valid`
  )

  val submissionPeriodWithPeriodIdModel: SubmissionPeriod = SubmissionPeriod(
    periodId = Some("1234567890123456"),
    submissionId = None,
    startDate = now,
    endDate = aYearFromNow,
    receivedDateTime = "2000-01-01T10:12:10Z"
  )

  val submissionPeriodWithSubmissionIdModel: SubmissionPeriod = SubmissionPeriod(
    periodId = None,
    submissionId = Some(s"${now}_$aYearFromNow"),
    startDate = now,
    endDate = aYearFromNow,
    receivedDateTime = "2000-01-01T10:12:10Z"
  )

  val inputsModel: Inputs = Inputs(
    typeOfBusiness = TypeOfBusiness.`self-employment`,
    businessId = "XAIS12345678910",
    businessName = Some("Business Name"),
    accountingPeriodStartDate = now,
    accountingPeriodEndDate = aYearFromNow,
    source = Source.`MTD-SA`,
    submissionPeriods = Seq(submissionPeriodWithPeriodIdModel, submissionPeriodWithSubmissionIdModel)
  )

  val summaryCalculationIncomeModel: SummaryCalculationIncome = SummaryCalculationIncome(
    turnover = Some(1.01),
    other = Some(1.02)
  )

  val summaryCalculationExpensesModel: SummaryCalculationExpenses = SummaryCalculationExpenses(
    consolidatedExpenses = Some(2.01),
    costOfGoodsAllowable = Some(2.02),
    paymentsToSubcontractorsAllowable = Some(2.03),
    wagesAndStaffCostsAllowable = Some(2.04),
    carVanTravelExpensesAllowable = Some(2.05),
    premisesRunningCostsAllowable = Some(2.06),
    maintenanceCostsAllowable = Some(2.07),
    adminCostsAllowable = Some(2.08),
    interestOnBankOtherLoansAllowable = Some(2.09),
    financeChargesAllowable = Some(2.10),
    irrecoverableDebtsAllowable = Some(2.11),
    professionalFeesAllowable = Some(2.12),
    depreciationAllowable = Some(2.13),
    otherExpensesAllowable = Some(2.14),
    advertisingCostsAllowable = Some(2.15),
    businessEntertainmentCostsAllowable = Some(2.16)
  )

  val summaryCalculationAdditionsModel: SummaryCalculationAdditions = SummaryCalculationAdditions(
    costOfGoodsDisallowable = Some(5.01),
    paymentsToSubcontractorsDisallowable = Some(5.02),
    wagesAndStaffCostsDisallowable = Some(5.03),
    carVanTravelExpensesDisallowable = Some(5.04),
    premisesRunningCostsDisallowable = Some(5.05),
    maintenanceCostsDisallowable = Some(5.06),
    adminCostsDisallowable = Some(5.07),
    interestOnBankOtherLoansDisallowable = Some(5.08),
    financeChargesDisallowable = Some(5.09),
    irrecoverableDebtsDisallowable = Some(5.10),
    professionalFeesDisallowable = Some(5.11),
    depreciationDisallowable = Some(5.12),
    otherExpensesDisallowable = Some(5.13),
    advertisingCostsDisallowable = Some(5.14),
    businessEntertainmentCostsDisallowable = Some(5.15),
    outstandingBusinessIncome = Some(5.16),
    balancingChargeOther = Some(5.17),
    balancingChargeBpra = Some(5.18),
    goodsAndServicesOwnUse = Some(5.19)
  )

  val summaryCalculationDeductionsModel: SummaryCalculationDeductions = SummaryCalculationDeductions(
    tradingAllowance = Some(6.01),
    annualInvestmentAllowance = Some(6.02),
    capitalAllowanceMainPool = Some(6.03),
    capitalAllowanceSpecialRatePool = Some(6.04),
    zeroEmissionGoods = Some(6.05),
    businessPremisesRenovationAllowance = Some(6.06),
    enhancedCapitalAllowance = Some(6.07),
    allowanceOnSales = Some(6.08),
    capitalAllowanceSingleAssetPool = Some(6.09),
    includedNonTaxableProfits = Some(6.10),
    electricChargePointAllowance = Some(6.11),
    structuredBuildingAllowance = Some(6.12),
    enhancedStructuredBuildingAllowance = Some(6.13),
    zeroEmissionsCarAllowance = Some(6.14)
  )

  val summaryCalculationAccountingAdjustmentsModel: SummaryCalculationAccountingAdjustments = SummaryCalculationAccountingAdjustments(
    basisAdjustment = Some(7.01),
    overlapReliefUsed = Some(7.02),
    accountingAdjustment = Some(7.03),
    averagingAdjustment = Some(7.04)
  )

  val adjustableSummaryCalculationModel: AdjustableSummaryCalculation = AdjustableSummaryCalculation(
    totalIncome = Some(1),
    income = Some(summaryCalculationIncomeModel),
    totalExpenses = Some(2),
    expenses = Some(summaryCalculationExpensesModel),
    netProfit = Some(3),
    netLoss = Some(4),
    totalAdditions = Some(5),
    additions = Some(summaryCalculationAdditionsModel),
    totalDeductions = Some(6),
    deductions = Some(summaryCalculationDeductionsModel),
    totalAccountingAdjustments = Some(7),
    accountingAdjustments = Some(summaryCalculationAccountingAdjustmentsModel),
    taxableProfit = Some(8),
    adjustedIncomeTaxLoss = Some(9)
  )

  val adjustmentsIncomeModel: AdjustmentsIncome = AdjustmentsIncome(
    turnover = Some(1.01),
    other = Some(1.02)
  )

  val adjustmentsExpensesModel: AdjustmentsExpenses = AdjustmentsExpenses(
    consolidatedExpenses = Some(2.01),
    costOfGoodsAllowable = Some(2.02),
    paymentsToSubcontractorsAllowable = Some(2.03),
    wagesAndStaffCostsAllowable = Some(2.04),
    carVanTravelExpensesAllowable = Some(2.05),
    premisesRunningCostsAllowable = Some(2.06),
    maintenanceCostsAllowable = Some(2.07),
    adminCostsAllowable = Some(2.08),
    interestOnBankOtherLoansAllowable = Some(2.09),
    financeChargesAllowable = Some(2.10),
    irrecoverableDebtsAllowable = Some(2.11),
    professionalFeesAllowable = Some(2.12),
    depreciationAllowable = Some(2.13),
    otherExpensesAllowable = Some(2.14),
    advertisingCostsAllowable = Some(2.15),
    businessEntertainmentCostsAllowable = Some(2.16)
  )

  val adjustmentsAdditionsModel: AdjustmentsAdditions = AdjustmentsAdditions(
    costOfGoodsDisallowable = Some(3.01),
    paymentsToSubcontractorsDisallowable = Some(3.02),
    wagesAndStaffCostsDisallowable = Some(3.03),
    carVanTravelExpensesDisallowable = Some(3.04),
    premisesRunningCostsDisallowable = Some(3.05),
    maintenanceCostsDisallowable = Some(3.06),
    adminCostsDisallowable = Some(3.07),
    interestOnBankOtherLoansDisallowable = Some(3.08),
    financeChargesDisallowable = Some(3.09),
    irrecoverableDebtsDisallowable = Some(3.10),
    professionalFeesDisallowable = Some(3.11),
    depreciationDisallowable = Some(3.12),
    otherExpensesDisallowable = Some(3.13),
    advertisingCostsDisallowable = Some(3.14),
    businessEntertainmentCostsDisallowable = Some(3.15)
  )

  val adjustmentsModel: Adjustments = Adjustments(
    income = Some(adjustmentsIncomeModel),
    expenses = Some(adjustmentsExpensesModel),
    additions = Some(adjustmentsAdditionsModel)
  )

  val adjustedSummaryCalculationModel: AdjustedSummaryCalculation = AdjustedSummaryCalculation(
    totalIncome = Some(1),
    income = Some(summaryCalculationIncomeModel),
    totalExpenses = Some(2),
    expenses = Some(summaryCalculationExpensesModel),
    netProfit = Some(3),
    netLoss = Some(4),
    totalAdditions = Some(5),
    additions = Some(summaryCalculationAdditionsModel),
    totalDeductions = Some(6),
    deductions = Some(summaryCalculationDeductionsModel),
    totalAccountingAdjustments = Some(7),
    accountingAdjustments = Some(summaryCalculationAccountingAdjustmentsModel),
    taxableProfit = Some(8),
    adjustedIncomeTaxLoss = Some(9)
  )

  val retrieveBsasResponseModel: RetrieveSelfEmploymentBsasResponse = RetrieveSelfEmploymentBsasResponse(
    metadata = metadataModel,
    inputs = inputsModel,
    adjustableSummaryCalculation = adjustableSummaryCalculationModel,
    adjustments = Some(adjustmentsModel),
    adjustedSummaryCalculation = Some(adjustedSummaryCalculationModel)
  )

  def retrieveBsasResponseInvalidTypeOfBusinessModel(typeOfBusiness: TypeOfBusiness): RetrieveSelfEmploymentBsasResponse =
    RetrieveSelfEmploymentBsasResponse(
      metadata = metadataModel,
      inputs = inputsModel.copy(typeOfBusiness = typeOfBusiness),
      adjustableSummaryCalculation = adjustableSummaryCalculationModel,
      adjustments = Some(adjustmentsModel),
      adjustedSummaryCalculation = Some(adjustedSummaryCalculationModel)
    )
}
