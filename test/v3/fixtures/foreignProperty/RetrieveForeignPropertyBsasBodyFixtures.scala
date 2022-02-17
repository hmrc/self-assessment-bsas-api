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

package v3.fixtures.foreignProperty

import play.api.libs.json.{JsObject, JsValue, Json}
import v3.models.domain.TypeOfBusiness
import v3.models.response.retrieveBsas.foreignProperty._

object RetrieveForeignPropertyBsasBodyFixtures {

  val metadataMtdJson: JsValue = Json.parse(
    """{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "adjustedDateTime": "2020-12-05T16:19:44Z",
      |  "nino": "AA999999A",
      |  "taxYear": "2019-20",
      |  "summaryStatus": "valid"
      |  }""".stripMargin
  )

  val metadataDesJson: JsValue = Json.parse(
    """{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "adjustedDateTime": "2020-12-05T16:19:44Z",
      |  "taxableEntityId": "AA999999A",
      |  "taxYear": 2020,
      |  "status": "valid"
      |}""".stripMargin
  )

  val metadataDesJsonWithoutADT: JsValue = Json.parse(
    """{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "taxableEntityId": "AA999999A",
      |  "taxYear": 2020,
      |  "status": "valid"
      |}""".stripMargin
  )

  val inputsMtdJson: JsValue = Json.parse(
    """{
      |  "businessId": "000000000000210",
      |  "typeOfBusiness": "foreign-property-fhl-eea",
      |  "businessName": "Business Name",
      |  "accountingPeriodStartDate": "2019-04-06",
      |  "accountingPeriodEndDate": "2020-04-05",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [
      |   {
      |     "submissionId": "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
      |     "startDate": "2019-04-06",
      |     "endDate": "2020-04-05",
      |     "receivedDateTime": "2019-02-15T09:35:04.843Z"
      |   }]
      |}""".stripMargin
  )

  val inputsDesJson: JsValue = Json.parse(
    """{
      |  "incomeSourceId": "000000000000210",
      |  "incomeSourceType": "03",
      |  "incomeSourceName": "Business Name",
      |  "accountingPeriodStartDate": "2019-04-06",
      |  "accountingPeriodEndDate": "2020-04-05",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [
      |    {
      |      "periodId": "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
      |      "startDate": "2019-04-06",
      |      "endDate": "2020-04-05",
      |      "receivedDateTime": "2019-02-15T09:35:04.843Z"
      |    }
      |  ]
      |}""".stripMargin
  )

  val inputsDesJsonWithoutBusinessName: JsValue = Json.parse(
    """{
      |  "incomeSourceId": "000000000000210",
      |  "incomeSourceType": "03",
      |  "accountingPeriodStartDate": "2019-04-06",
      |  "accountingPeriodEndDate": "2020-04-05",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [
      |    {
      |      "periodId": "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
      |      "startDate": "2019-04-06",
      |      "endDate": "2020-04-05",
      |      "receivedDateTime": "2019-02-15T09:35:04.843Z"
      |    }
      |  ]
      |}""".stripMargin
  )

  val incomeMtdJson: JsValue = Json.parse(
    """{
      |  "totalRentsReceived": 0.12,
      |  "premiumsOfLeaseGrant": 0.12,
      |  "otherPropertyIncome": 0.12
      |}""".stripMargin
  )

  val incomeDesJson: JsValue = Json.parse(
    """{
      |  "rent": 0.12,
      |  "premiumsOfLeaseGrant": 0.12,
      |  "otherPropertyIncome": 0.12
      |}""".stripMargin
  )

  val expensesMtdJson: JsValue = Json.parse(
    """{
      | "consolidatedExpenses": 0.12,
      |  "premisesRunningCosts": 0.12,
      |  "repairsAndMaintenance": 0.12,
      |  "financialCosts": 0.12,
      |  "professionalFees": 0.12,
      |  "travelCosts": 0.12,
      |  "costOfServices": 0.12,
      |  "residentialFinancialCost": 0.12,
      |  "broughtFwdResidentialFinancialCost": 0.12,
      |  "other": 0.12
      |}""".stripMargin
  )

  val expensesDesJson: JsValue = Json.parse(
    """{
      | "consolidatedExpenses": 0.12,
      |  "premisesRunningCosts": 0.12,
      |  "repairsAndMaintenance": 0.12,
      |  "financialCosts": 0.12,
      |  "professionalFees": 0.12,
      |  "travelCosts": 0.12,
      |  "costOfServices": 0.12,
      |  "residentialFinancialCost": 0.12,
      |  "broughtFwdResidentialFinancialCost": 0.12,
      |  "other": 0.12
      |}""".stripMargin
  )

  val deductionsMtdJson: JsValue = Json.parse(
    """{
      |   "annualInvestmentAllowance": 0.12,
      |   "costOfReplacingDomesticItems": 0.12,
      |   "zeroEmissionGoods": 0.12,
      |   "propertyAllowance": 0.12,
      |   "otherCapitalAllowance": 0.12,
      |   "electricChargePointAllowance": 0.12,
      |   "structuredBuildingAllowance": 0.12,
      |   "zeroEmissionsCarAllowance": 0.12
      |}""".stripMargin
  )

  val deductionsDesJson: JsValue = Json.parse(
    """{
      |   "annualInvestmentAllowance": 0.12,
      |   "costOfReplacingDomesticItems": 0.12,
      |   "zeroEmissionsGoodsVehicleAllowance": 0.12,
      |   "propertyAllowance": 0.12,
      |   "otherCapitalAllowance": 0.12,
      |   "electricChargePointAllowance": 0.12,
      |   "structuredBuildingAllowance": 0.12,
      |   "zeroEmissionsCarAllowance": 0.12
      |}""".stripMargin
  )

  val additionsMtdJson: JsValue = Json.parse(
    """{
      |  "privateUseAdjustment": 0.12,
      |  "balancingCharge": 0.12
      |}""".stripMargin
  )

  val additionsDesJson: JsValue = Json.parse(
    """{
      |  "privateUseAdjustment": 0.12,
      |  "balancingCharge": 0.12
      |}""".stripMargin
  )

  val countryLevelDetailsMtdJson: JsValue = Json.parse(
    s"""{
      |  "countryCode": "CYM",
      |  "totalIncome": 0.12,
      |  "income": $incomeMtdJson,
      |  "totalExpenses": 0.12,
      |  "expenses": $expensesMtdJson,
      |   "netProfit": 0.12,
      |   "netLoss": 0.12,
      |   "totalAdditions": 0.12,
      |   "additions": $additionsMtdJson,
      |   "totalDeductions": 0.12,
      |   "deductions": $deductionsMtdJson,
      |    "taxableProfit": 1,
      |    "adjustedIncomeTaxLoss": 1
      |}""".stripMargin
  )

  val countryLevelDetailDesJson: JsValue = Json.parse(
    s"""{
      |  "countryCode": "CYM",
      |  "totalIncome": 0.12,
      |  "income": $incomeDesJson,
      |  "totalExpenses": 0.12,
      |  "expenses": $expensesDesJson,
      |   "netProfit": 0.12,
      |   "netLoss": 0.12,
      |   "totalAdditions": 0.12,
      |   "additions": $additionsDesJson,
      |   "totalDeductions": 0.12,
      |   "deductions": $deductionsDesJson,
      |    "taxableProfit": 1,
      |    "adjustedIncomeTaxLoss": 1
      |}""".stripMargin
  )

  val adjustableSCMtdJson: JsValue = Json.parse(
    s"""{
      |		"totalIncome": 0.12,
      |		"income": $incomeMtdJson,
      |		"totalExpenses": 0.12,
      |		"expenses": $expensesMtdJson,
      |		"netProfit": 0.12,
      |   "netLoss": 0.12,
      |		"totalAdditions": 0.12,
      |		"additions": $additionsMtdJson,
      |		"totalDeductions": 0.12,
      |		"deductions": $deductionsMtdJson,
      |		"taxableProfit": 1,
      |   "adjustedIncomeTaxLoss": 1,
      |		"countryLevelDetail": [$countryLevelDetailsMtdJson]
      |}""".stripMargin
  )

  val adjustableSCDesJson: JsValue = Json.parse(
    s"""{
      |		"totalIncome": 0.12,
      |		"income": $incomeDesJson,
      |		"totalExpenses": 0.12,
      |		"expenses": $expensesDesJson,
      |		"netProfit": 0.12,
      |   "netLoss": 0.12,
      |		"totalAdditions": 0.12,
      |		"additions": $additionsDesJson,
      |		"totalDeductions": 0.12,
      |		"deductions": $deductionsDesJson,
      |		"taxableProfit": 1,
      |   "adjustedIncomeTaxLoss": 1,
      |		"countryLevelDetail": [$countryLevelDetailDesJson]
      |}""".stripMargin
  )

  val adjustmentsMtdNonFhlJson: JsValue = Json.parse(
    s"""{
      |	"countryLevelDetail": [{
      |		"countryCode": "CYM",
      |		"income": $incomeMtdJson,
      |		"expenses": $expensesMtdJson
      |	}]
      |}""".stripMargin
  )

  val adjustmentsDesNonFhlJson: JsValue = Json.parse(
    s"""{
       |  "countryCode": "CYM",
       |	"income": $incomeDesJson,
       |	"expenses": $expensesDesJson
       |}""".stripMargin
  )

  val adjustmentsMtdFhlEeaJson: JsValue = Json.parse(
    s"""{
      |		"income": $incomeMtdJson,
      |		"expenses": $expensesMtdJson
      |}""".stripMargin
  )

  val adjustmentsDesFhlEeaJson: JsValue = Json.parse(
    s"""{
      |		"income": $incomeDesJson,
      |		"expenses": $expensesDesJson
      |}""".stripMargin
  )

  val metaDataModel: Metadata = Metadata(
    "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    "2020-12-05T16:19:44Z",
    Some("2020-12-05T16:19:44Z"),
    "AA999999A",
    "2019-20",
    "valid"
  )

  val inputsModel: Inputs = Inputs(
    "000000000000210",
    TypeOfBusiness.`foreign-property-fhl-eea`,
    Some("Business Name"),
    "2019-04-06",
    "2020-04-05",
    "MTD-SA",
    Seq(SubmissionPeriods("617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4","2019-04-06","2020-04-05","2019-02-15T09:35:04.843Z"))
  )

  val submissionPeriodModel: SubmissionPeriods = SubmissionPeriods(
    "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
    "2019-04-06",
    "2020-04-05",
    "2019-02-15T09:35:04.843Z"
  )

  val retrieveForeignPropertyBsasMtdJsonNonFhl: JsValue = Json.parse(
    s"""{
       |	"metadata": $metadataMtdJson,
       |	"inputs": $inputsMtdJson,
       |	"adjustableSummaryCalculation": $adjustableSCMtdJson,
       |	"adjustments": $adjustmentsMtdNonFhlJson,
       |	"adjustedSummaryCalculation": $adjustableSCMtdJson
       |}""".stripMargin
  )

  val retrieveForeignPropertyBsasMtdJsonFhlEea: JsValue = Json.parse(
    s"""{
      |	"metadata": $metadataMtdJson,
      |	"inputs": $inputsMtdJson,
      |	"adjustableSummaryCalculation": $adjustableSCMtdJson,
      |	"adjustments": $adjustmentsMtdFhlEeaJson,
      |	"adjustedSummaryCalculation": $adjustableSCMtdJson
      |}""".stripMargin
  )

  val retrieveForeignPropertyBsasDesJsonNonFhl: JsValue = Json.parse(
    s"""{
      |	"metadata": $metadataDesJson,
      |	"inputs": $inputsDesJson,
      |	"adjustableSummaryCalculation": $adjustableSCDesJson,
      |	"adjustments": [$adjustmentsDesNonFhlJson],
      |	"adjustedSummaryCalculation": $adjustableSCDesJson
      |}""".stripMargin
  )

  val retrieveForeignPropertyBsasDesJsonFhlEea: JsValue = Json.parse(
    s"""{
       |	"metadata": $metadataDesJson,
       |	"inputs": $inputsDesJson,
       |	"adjustableSummaryCalculation": $adjustableSCDesJson,
       |	"adjustments": $adjustmentsDesFhlEeaJson,
       |	"adjustedSummaryCalculation": $adjustableSCDesJson
       |}""".stripMargin
  )

  val incomeModel: Income = Income(
    Some(0.12),
    Some(0.12),
    Some(0.12)
  )

  val expensesModel: Expenses = Expenses(
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12)
  )

  val additionsModel: Additions = Additions(
    Some(0.12),
    Some(0.12)
  )

  val deductionsModel: Deductions = Deductions(
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12)
  )

  val countryLevelDetailModel: CountryLevelDetail = CountryLevelDetail(
    Some("CYM"),
    Some(0.12),
    Some(incomeModel),
    Some(0.12),
    Some(expensesModel),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(additionsModel),
    Some(0.12),
    Some(deductionsModel),
    Some(1),
    Some(1)
  )

  val adjustableSummaryCalculationModel: AdjustableSummaryCalculation = AdjustableSummaryCalculation(
    Some(0.12),
    Some(incomeModel),
    Some(0.12),
    Some(expensesModel),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(additionsModel),
    Some(0.12),
    Some(deductionsModel),
    Some(1),
    Some(1),
    Some(Seq(countryLevelDetailModel)))

  val adjustmentsFhlEeaModel: Adjustments = Adjustments(
    None,
    Some(incomeModel),
    Some(expensesModel))

  val adjustmentsNonFhlModel: Adjustments = Adjustments(
    Some(Seq(CountryLevelDetail(
      Some("CYM"),
      None,
      Some(incomeModel),
      None,
      Some(expensesModel),
      None, None, None, None, None, None, None, None))),
    None,
    None
  )

  val retrieveBsasResponseFhlEeaModel: RetrieveForeignPropertyBsasResponse = RetrieveForeignPropertyBsasResponse(
    metadata = metaDataModel,
    inputs = inputsModel,
    adjustableSummaryCalculation = adjustableSummaryCalculationModel,
    adjustments = Some(adjustmentsFhlEeaModel),
    adjustedSummaryCalculation = Some(adjustableSummaryCalculationModel)
  )

  val retrieveBsasResponseNonFhlModel: RetrieveForeignPropertyBsasResponse = RetrieveForeignPropertyBsasResponse(
    metadata = metaDataModel,
    inputs = inputsModel,
    adjustableSummaryCalculation = adjustableSummaryCalculationModel,
    adjustments = Some(adjustmentsNonFhlModel),
    adjustedSummaryCalculation = Some(adjustableSummaryCalculationModel)
  )

  def retrieveBsasResponseNonFhlModelWith(typeOfBusiness: TypeOfBusiness): RetrieveForeignPropertyBsasResponse = RetrieveForeignPropertyBsasResponse(
    metadata = metaDataModel,
    inputs = inputsModel.copy(typeOfBusiness = typeOfBusiness),
    adjustableSummaryCalculation = adjustableSummaryCalculationModel,
    adjustments = Some(adjustmentsNonFhlModel),
    adjustedSummaryCalculation = Some(adjustableSummaryCalculationModel)
  )
}