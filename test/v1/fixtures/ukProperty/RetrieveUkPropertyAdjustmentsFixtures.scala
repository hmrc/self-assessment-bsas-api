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

package v1.fixtures.ukProperty

import java.time.LocalDate

import play.api.libs.json.{JsValue, Json}
import v1.models.domain.TypeOfBusiness
import v1.models.response.retrieveBsas.AccountingPeriod
import v1.models.response.retrieveBsasAdjustments.ukProperty.{BsasDetail, ExpensesBreakdown, IncomeBreakdown, Metadata, RetrieveUkPropertyAdjustmentsResponse}

object RetrieveUkPropertyAdjustmentsFixtures {

  val metaDataModel: Metadata =
    Metadata(
      typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
      accountingPeriod = AccountingPeriod(
        LocalDate.parse("2018-10-11"),
        LocalDate.parse("2019-10-10")
      ),
      taxYear = "2019-20",
      requestedDateTime = "2019-10-14T11:33:27Z",
      bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      summaryStatus = "superseded",
      adjustedSummary = true
    )

  val incomeModel = IncomeBreakdown(Some(100.49), Some(100.49), Some(100.49), Some(100.49))

  val expenseBreakdownModel = ExpensesBreakdown(Some(100.49), Some(100.49), Some(100.49), Some(100.49),
    Some(100.49), Some(100.49), Some(100.49), Some(100.49), Some(100.49))

  val bsasDetailModel = BsasDetail(Some(incomeModel), Some(expenseBreakdownModel))

  val retrieveUKPropertyAdjustmentResponseModel = RetrieveUkPropertyAdjustmentsResponse(metaDataModel, bsasDetailModel)

  val minimalRetrieveUKPropertyAdjustmentResponseModel = RetrieveUkPropertyAdjustmentsResponse(metaDataModel, BsasDetail(None, None))

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |   "metadata": {
      |      "typeOfBusiness": "self-employment",
      |      "selfEmploymentId": "000000000000210",
      |      "accountingPeriod": {
      |         "StartDate": "2018-10-11",
      |         "EndDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2019-10-14T11:33:27Z",
      |      "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "summaryStatus": "superseded",
      |      "adjustedSummary": "true"
      |   },
      |   "adjustments": {
      |      "incomes": {
      |         "rentIncome": 100.49,
      |         "premiumsOfLeaseGrant": 100.49,
      |         "reversePremiums": 100.49,
      |         "otherPropertyIncome": 100.49
      |      },
      |      "expenses": {
      |         "premisesRunningCosts": 100.49,
      |         "repairsAndMaintenance": 100.49,
      |         "financialCosts": 100.49,
      |         "professionalFees": 100.49,
      |         "travelCosts": 100.49,
      |         "costOfServices": 100.49,
      |         "residentialFinancialCost": 100.49,
      |         "other": 100.49
      |      }
      |   }
      |}
    """.stripMargin
  )
}
