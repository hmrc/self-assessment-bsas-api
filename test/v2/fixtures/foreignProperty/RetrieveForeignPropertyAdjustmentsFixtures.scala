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

import v2.models.domain.TypeOfBusiness
import v2.models.response.retrieveBsas.AccountingPeriod
import v2.models.response.retrieveBsasAdjustments.foreignProperty.{BsasDetail, ExpensesBreakdown, IncomeBreakdown, Metadata, RetrieveForeignPropertyAdjustmentsResponse}

object RetrieveForeignPropertyAdjustmentsFixtures {

  val foreignPropertyMetaDataModel = Metadata(TypeOfBusiness.`foreign-property`,
    AccountingPeriod(LocalDate.parse("2018-10-11"), LocalDate.parse("2019-10-10")), "2019-20",
    "2019-10-14T11:33:27Z", "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4", "superseded", adjustedSummary = true)

  val foreignPropertyFhlEeaMetaDataModel = Metadata(TypeOfBusiness.`foreign-property-fhl-eea`,
    AccountingPeriod(LocalDate.parse("2018-10-11"), LocalDate.parse("2019-10-10")), "2019-20",
    "2019-10-14T11:33:27Z", "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4", "superseded", adjustedSummary = true)

  val nonFhlIncomeModel = IncomeBreakdown(Some(100.49), Some(100.49), Some(100.49), Some(100.49))

  val fhlIncomeModel = IncomeBreakdown(Some(100.49), None, None, None)

  val nonFhlExpenseBreakdownModel = ExpensesBreakdown(Some(100.49), Some(100.49), Some(100.49), Some(100.49),
    Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49))

  val fhlExpenseBreakdownModel = ExpensesBreakdown(Some(100.49), Some(100.49), Some(100.49), Some(100.49),
    Some(100.49), Some(100.49), None, Some(100.49), Some(100.49))

  val nonFhlBsasDetailModel = BsasDetail(Some(nonFhlIncomeModel), Some(nonFhlExpenseBreakdownModel))

  val fhlBsasDetailModel = BsasDetail(Some(fhlIncomeModel), Some(fhlExpenseBreakdownModel))

  val foreignPropertyRetrieveForeignPropertyAdjustmentResponseModel =
    RetrieveForeignPropertyAdjustmentsResponse(foreignPropertyMetaDataModel, nonFhlBsasDetailModel)

  val foreignPropertyFhlEeaRetrieveForeignPropertyAdjustmentResponseModel =
    RetrieveForeignPropertyAdjustmentsResponse(foreignPropertyFhlEeaMetaDataModel, fhlBsasDetailModel)

  val foreignPropertyMinimalRetrieveForeignPropertyAdjustmentResponseModel =
    RetrieveForeignPropertyAdjustmentsResponse(foreignPropertyMetaDataModel, BsasDetail(None, None))

  val foreignPropertyFhlEeaMinimalRetrieveForeignPropertyAdjustmentResponseModel =
    RetrieveForeignPropertyAdjustmentsResponse(foreignPropertyFhlEeaMetaDataModel, BsasDetail(None, None))

  val hateoasResponseForForeignPropertyAdjustments: (String, String) => String = (nino: String, bsasId: String) =>
    s"""
       |{
       |   "metadata": {
       |      "typeOfBusiness": "foreign-property",
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
       |      "incomes": {
       |         "rentIncome": 100.49,
       |         "premiumsOfLeaseGrant": 100.49,
       |         "otherPropertyIncome": 100.49,
       |         "foreignTaxTakenOff": 100.49
       |      },
       |      "expenses": {
       |         "premisesRunningCosts": 100.49,
       |         "repairsAndMaintenance": 100.49,
       |         "financialCosts": 100.49,
       |         "professionalFees": 100.49,
       |         "travelCosts": 100.49,
       |         "costOfServices": 100.49,
       |         "residentialFinancialCost": 100.49,
       |         "other": 100.49,
       |         "consolidatedExpenses": 100.49
       |      }
       |   },
       |	"links": [{
       |		"href": "/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId?adjustedStatus=true",
       |		"method": "GET",
       |		"rel": "retrieve-adjustable-summary"
       |	}, {
       |		"href": "/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust",
       |		"method": "GET",
       |		"rel": "self"
       |	}]
       |}
       |""".stripMargin
}
