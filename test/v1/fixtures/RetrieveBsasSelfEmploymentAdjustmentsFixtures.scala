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

package v1.fixtures

import java.time.LocalDate

import play.api.libs.json.Json
import v1.models.domain.TypeOfBusiness
import v1.models.response.retrieveBsas.AccountingPeriod
import v1.models.response.retrieveBsasAdjustments._

object RetrieveBsasSelfEmploymentAdjustmentsFixtures {

  val additionsBreakdownModel = AdditionsBreakdown(
    Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49),
    Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49),
    Some(100.49), Some(100.49), Some(100.49))

  val expenseBreakdownModel = ExpensesBreakdown(
    Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49),
    Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49)
  )

  val incomeModel = IncomeBreakdown(Some(100.49), Some(100.49))

  val bsasDetailModel = BsasDetail(Some(incomeModel), Some(expenseBreakdownModel), Some(additionsBreakdownModel))

  val metaDataModel = Metadata(TypeOfBusiness.`self-employment`, Some("000000000000210"),
    AccountingPeriod(LocalDate.parse("2018-10-11"), LocalDate.parse("2019-10-10")), "2019-20",
    "2019-10-14T11:33:27Z", "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4", "superseded", true)

  val mtdJson = Json.parse(
    """
      |{
      |   "metadata": {
      |      "typeOfBusiness": "self-employment",
      |      "selfEmploymentId": "000000000000210",
      |      "accountingPeriod": {
      |         "startDate": "2018-10-11",
      |         "endDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2019-10-14T11:33:27Z",
      |      "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "summaryStatus": "superseded",
      |      "adjustedSummary": true
      |   },
      |   "adjustments": {
      |      "income": {
      |         "turnover": 100.49,
      |         "other": 100.49
      |      },
      |      "expenses": {
      |         "costOfGoodsBought": 100.49,
      |         "cisPaymentsToSubcontractors": 100.49,
      |         "staffCosts": 100.49,
      |         "travelCosts": 100.49,
      |         "premisesRunningCosts": 100.49,
      |         "maintenanceCosts": 100.49,
      |         "adminCosts": 100.49,
      |         "advertisingCosts": 100.49,
      |         "businessEntertainmentCosts": 100.49,
      |         "interest": 100.49,
      |         "financialCharges": 100.49,
      |         "badDebt": 100.49,
      |         "professionalFees": 100.49,
      |         "depreciation": 100.49,
      |         "other": 100.49,
      |         "consolidatedExpenses" :100.49
      |      },
      |      "additions": {
      |         "costOfGoodsBoughtDisallowable": 100.49,
      |         "cisPaymentsToSubcontractorsDisallowable": 100.49,
      |         "staffCostsDisallowable": 100.49,
      |         "travelCostsDisallowable": 100.49,
      |         "premisesRunningCostsDisallowable": 100.49,
      |         "maintenanceCostsDisallowable": 100.49,
      |         "adminCostsDisallowable": 100.49,
      |         "advertisingCostsDisallowable": 100.49,
      |         "businessEntertainmentCostsDisallowable": 100.49,
      |         "interestDisallowable": 100.49,
      |         "financialChargesDisallowable": 100.49,
      |         "badDebtDisallowable": 100.49,
      |         "professionalFeesDisallowable": 100.49,
      |         "depreciationDisallowable": 100.49,
      |         "otherDisallowable": 100.49
      |      }
      |   }
      |}
    """.stripMargin
  )
}
