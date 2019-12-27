/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.fixtures.selfEmployment

import play.api.libs.json.{JsObject, JsValue, Json}
import AdditionsFixture.{additionsDesJson, additionsModel, additionsMtdJson}
import ExpensesFixture.{expensesDesJson, expensesModel, expensesMtdJson}
import IncomeFixture.{incomeJson, incomeModel}
import v1.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestBody

import scala.collection.mutable.ListBuffer

object SubmitSelfEmploymentBsasFixtures {

  val submitSelfEmploymentBsasRequestBodyModel: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(
      income = Some(incomeModel),
      additions = Some(additionsModel),
      expenses = Some(expensesModel)
    )

  val submitSelfEmploymentBsasRequestBodyModelWithoutIncome: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(
      income = None,
      additions = Some(additionsModel),
      expenses = Some(expensesModel)
    )

  val emptySubmitSelfEmploymentBsasRequestBodyModel: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(
      income = None,
      additions = None,
      expenses = None
    )

  def submitSelfEmploymentBsasRequestBodyDesJson(model: SubmitSelfEmploymentBsasRequestBody): JsValue = {
    import model._

    val jsObjects : ListBuffer[JsObject] = ListBuffer.empty[JsObject]

    if (income.nonEmpty) {
      jsObjects += Json.obj("income" -> incomeJson(income.get))
    }

    if (additions.nonEmpty) {
      jsObjects += Json.obj("additions" -> additionsDesJson(additions.get))
    }

    if (expenses.nonEmpty) {
      jsObjects += Json.obj("expenses" -> expensesDesJson(expenses.get))
    }

    val json = jsObjects.fold(Json.parse("""{}""").as[JsObject])((a: JsObject, b: JsObject) => a ++ b)
    json
  }

  def submitSelfEmploymentBsasRequestBodyMtdJson(model: SubmitSelfEmploymentBsasRequestBody): JsValue = {
    import model._

    val jsObjects : ListBuffer[JsObject] = ListBuffer.empty[JsObject]

    if (income.nonEmpty) {
      jsObjects += Json.obj("income" -> incomeJson(income.get))
    }

    if (additions.nonEmpty) {
      jsObjects += Json.obj("additions" -> additionsMtdJson(additions.get))
    }

    if (expenses.nonEmpty) {
      jsObjects += Json.obj("expenses" -> expensesMtdJson(expenses.get))
    }

    val json = jsObjects.fold(Json.parse("""{}""").as[JsObject])((a: JsObject, b: JsObject) => a ++ b)
    json
  }

  val mtdRequest = Json.parse("""{
                      |	"income": {
                      |		"turnover": 1000.25,
                      |		"other": 1000.5
                      |	},
                      |	"additions": {
                      |		"costOfGoodsBoughtDisallowable": 3000.1,
                      |		"cisPaymentsToSubcontractorsDisallowable": 3000.2,
                      |		"staffCostsDisallowable": 3000.3,
                      |		"travelCostsDisallowable": 3000.4,
                      |		"premisesRunningCostsDisallowable": 3000.5,
                      |		"maintenanceCostsDisallowable": -3000.1,
                      |		"adminCostsDisallowable": -3000.2,
                      |		"advertisingCostsDisallowable": -3000.3,
                      |		"businessEntertainmentCostsDisallowable": -3000.4,
                      |		"interestDisallowable": -3000.5,
                      |		"financialChargesDisallowable": 3000.6,
                      |		"badDebtDisallowable": -3000.6,
                      |		"professionalFeesDisallowable": 3000.7,
                      |		"depreciationDisallowable": -3000.7,
                      |		"otherDisallowable": 3000.8
                      |	},
                      |	"expenses": {
                      |		"costOfGoodsBought": 2000.25,
                      |		"cisPaymentsToSubcontractors": 2000.5,
                      |		"staffCosts": 2000.75,
                      |		"travelCosts": -2000.25,
                      |		"premisesRunningCosts": -2000.5,
                      |		"maintenanceCosts": -2000.75,
                      |		"adminCosts": 2001.25,
                      |		"advertisingCosts": 2001.5,
                      |		"businessEntertainmentCosts": 2001.75,
                      |		"interest": -2001.25,
                      |		"financialCharges": -2001.5,
                      |		"badDebt": -2001.75,
                      |		"professionalFees": 2002.25,
                      |		"depreciation": 2002.5,
                      |		"other": 2002.75,
                      |		"consolidatedExpenses": -2002.25
                      |	}
                      |}""".stripMargin)
}
