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

package v2.fixtures.foreignProperty

import java.time.LocalDate

import play.api.libs.json.{JsValue, Json}
import v2.models.domain.TypeOfBusiness
import v2.models.response.retrieveBsas.{AccountingPeriod, Loss, Profit, TotalBsas}
import v2.models.response.retrieveBsas.foreignProperty.{BsasDetail, Metadata, ExpensesBreakdown, IncomeBreakdown}

object RetrieveForeignPropertyBsasFixtures {

  private val now = "2020-04-06"
  private val aYearFromNow = "2021-04-05"

  val mtdRetrieveBsasResponseJson: Boolean => JsValue = adjustedSummary =>
    Json.parse(
      s"""{
         |  "metadata": ${mtdMetadataJson(adjustedSummary)},
         |  "bsas": $mtdBsasDetailJson
         |}""".stripMargin
    )

  val mtdMetadataJson: Boolean => JsValue = adjustedSummary =>
    Json.parse(
      s"""{
         |  "typeOfBusiness": "foreign-property",
         |  "businessId": "X0IS12345678901",
         |  "accountingPeriod": {
         |    "startDate": "$now",
         |    "endDate": "$aYearFromNow"
         |  },
         |  "taxYear": "2019-20",
         |  "bsasId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
         |  "requestedDateTime": "2020-04-07T23:59:59.000Z",
         |  "summaryStatus": "valid",
         |  "adjustedSummary": $adjustedSummary
         |}""".stripMargin
    )

  val mtdBsasDetailJson: JsValue = Json.parse(
    s"""{
       |    "bsas": {
       |        "total": {
       |            "income": 123.12,
       |            "expenses": 123.12,
       |            "additions": 123.12,
       |            "deductions": 123.12
       |        },
       |        "profit": {
       |            "net": 123.12,
       |            "taxable": 123.12
       |        },
       |        "loss": {
       |            "net": 123.12,
       |            "adjustedIncomeTax": 123.12
       |        },
       |        "incomeBreakdown": {
       |            "totalRentsReceived": 123.12,
       |            "premiumsOfLeaseGrant": 123.12,
       |            "otherPropertyIncome": 123.12,
       |            "foreignTaxTakenOff": 123.12,
       |            "specialWithholdingTaxOrUKTaxPaid": 123.12
       |        },
       |        "expensesBreakdown": {
       |            "premisesRunningCosts": 123.12,
       |            "repairsAndMaintenance": 123.12,
       |            "financialCosts": 123.12,
       |            "professionalFees": 123.12,
       |            "travelCosts": 123.12,
       |            "costOfServices": 123.12,
       |            "residentialFinancialCost": 123.12,
       |            "broughtFwdResidentialFinancialCost": 123.12,
       |            "other": 123.12,
       |            "consolidatedExpenses": 123.12
       |        }
       |     }
       |}""".stripMargin
  )

  val totalBsasModel = TotalBsas(Some(100.49),Some(100.49),Some(100.49),Some(100.49))
  val profitModel = Profit(Some(100.49),Some(100.49))
  val lossModel = Loss(Some(100.49),Some(100.49))
  val incomeBreakdownModel = IncomeBreakdown(Some(100.49),Some(100.49),Some(100.49),Some(100.49), Some(100.49))
  val expensesBreakdownModel = ExpensesBreakdown(Some(100.49),Some(100.49),Some(100.49), Some(100.49),
    Some(100.49),Some(100.49),Some(100.49), Some(100.49), Some(100.49),None)
  val accountingPeriodModel = AccountingPeriod(LocalDate.parse("2019-04-06"), LocalDate.parse("2020-04-05"))

  val metadataModel = Metadata(typeOfBusiness = TypeOfBusiness.`foreign-property`,
    accountingPeriod = accountingPeriodModel,
    taxYear = "2019-20",
    requestedDateTime = "2020-10-14T11:33:27.111Z",
    bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    summaryStatus = "valid",
    adjustedSummary = true
  )

  val bsasDetailModel = BsasDetail(total = totalBsasModel,
    profit = Some(profitModel),
    loss = Some(lossModel),
    incomeBreakdown = Some(incomeBreakdownModel),
    expensesBreakdown = Some(expensesBreakdownModel))

}
