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

package v3.fixtures.selfEmployment

import play.api.libs.json.{JsObject, JsValue, Json}
import v3.fixtures.selfEmployment.AdditionsFixture.{additionsFromVendorJson, additionsModel, additionsToDesJson}
import v3.fixtures.selfEmployment.ExpensesFixture.{expensesFromMtdJson, expensesModel, expensesToDesJson}
import v3.fixtures.selfEmployment.IncomeFixture.{incomeJson, incomeModel}
import v3.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestBody

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
  val mtdRequestJson: JsValue = Json.parse(
    """
      |{
      |   "income":{
      |      "turnover":1000.25,
      |      "other":1000.5
      |   },
      |   "expenses":{
      |     "costOfGoodsAllowable":2000.25,
      |     "paymentsToSubcontractorsAllowable":2000.5,
      |     "wagesAndStaffCostsAllowable":2000.75,
      |     "carVanTravelExpensesAllowable":-2000.25,
      |     "premisesRunningCostsAllowable":-2000.5,
      |     "maintenanceCostsAllowable":-2000.75,
      |     "adminCostsAllowable":2001.25,
      |     "interestOnBankOtherLoansAllowable":-2001.25,
      |     "financeChargesAllowable":-2001.5,
      |     "irrecoverableDebtsAllowable":-2001.75,
      |     "professionalFeesAllowable":2002.25,
      |     "depreciationAllowable":2002.5,
      |     "otherExpensesAllowable":2002.75,
      |     "advertisingCostsAllowable":2001.5,
      |     "businessEntertainmentCostsAllowable":2001.75
      |   },
      |   "additions":{
      |     "costOfGoodsDisallowable":3000.1,
      |     "paymentsToSubcontractorsDisallowable":3000.2,
      |     "wagesAndStaffCostsDisallowable":3000.3,
      |     "carVanTravelExpensesDisallowable":3000.4,
      |     "premisesRunningCostsDisallowable":3000.5,
      |     "maintenanceCostsDisallowable":-3000.1,
      |     "adminCostsDisallowable":-3000.2,
      |     "interestOnBankOtherLoansDisallowable":-3000.5,
      |     "financeChargesDisallowable":3000.6,
      |     "irrecoverableDebtsDisallowable":-3000.6,
      |     "professionalFeesDisallowable":3000.7,
      |     "depreciationDisallowable":-3000.7,
      |     "otherExpensesDisallowable":3000.8,
      |     "advertisingCostsDisallowable":-3000.3,
      |     "businessEntertainmentCostsDisallowable":-3000.4
      |   }
      |}
      |""".stripMargin
  )
  val mtdRequestWithOnlyConsolidatedExpenses: JsValue = Json.parse(
    """
      |{
      |  "income": {
      |    "turnover": 1000.25,
      |    "other": 1000.5
      |  },
      |  "expenses": {
      |    "consolidatedExpenses": 2002.75
      |  }
      |}
      |""".stripMargin)
  val mtdRequestWithOnlyAdditionsExpenses: JsValue = Json.parse(
    """
      |{
      |  "income": {
      |    "turnover": 1000.25,
      |    "other": 1000.5
      |  },
      |  "additions":{
      |    "costOfGoodsDisallowable":3000.1,
      |    "paymentsToSubcontractorsDisallowable":3000.2,
      |    "wagesAndStaffCostsDisallowable":3000.3,
      |    "carVanTravelExpensesDisallowable":3000.4,
      |    "premisesRunningCostsDisallowable":3000.5,
      |    "maintenanceCostsDisallowable":-3000.1,
      |    "adminCostsDisallowable":-3000.2,
      |    "interestOnBankOtherLoansDisallowable":-3000.5,
      |    "financeChargesDisallowable":3000.6,
      |    "irrecoverableDebtsDisallowable":-3000.6,
      |    "professionalFeesDisallowable":3000.7,
      |    "depreciationDisallowable":-3000.7,
      |    "otherExpensesDisallowable":3000.8,
      |    "advertisingCostsDisallowable":-3000.3,
      |    "businessEntertainmentCostsDisallowable":-3000.4
      |  }
      |}
      |""".stripMargin)
  val mtdRequestWithAdditionsAndExpenses: JsValue = Json.parse(
    """
      |{
      |  "income": {
      |    "turnover": 1000.25,
      |    "other": 1000.5
      |  },
      |  "additions":{
      |    "costOfGoodsDisallowable":3000.1,
      |    "paymentsToSubcontractorsDisallowable":3000.2,
      |    "wagesAndStaffCostsDisallowable":3000.3,
      |    "carVanTravelExpensesDisallowable":3000.4,
      |    "premisesRunningCostsDisallowable":3000.5,
      |    "maintenanceCostsDisallowable":-3000.1,
      |    "adminCostsDisallowable":-3000.2,
      |    "interestOnBankOtherLoansDisallowable":-3000.5,
      |    "financeChargesDisallowable":3000.6,
      |    "irrecoverableDebtsDisallowable":-3000.6,
      |    "professionalFeesDisallowable":3000.7,
      |    "depreciationDisallowable":-3000.7,
      |    "otherExpensesDisallowable":3000.8,
      |    "advertisingCostsDisallowable":-3000.3,
      |    "businessEntertainmentCostsDisallowable":-3000.4
      |   },
      |  "expenses": {
      |    "consolidatedExpenses": 2002.75
      |  }
      |}
      |""".stripMargin)
  val mtdRequestWithBothExpenses: JsValue = Json.parse(
    """
      |{
      |  "income": {
      |    "turnover": 1000.25,
      |    "other": 1000.5
      |  },
      |  "additions":{
      |    "costOfGoodsDisallowable":3000.1,
      |    "paymentsToSubcontractorsDisallowable":3000.2,
      |    "wagesAndStaffCostsDisallowable":3000.3,
      |    "carVanTravelExpensesDisallowable":3000.4,
      |    "premisesRunningCostsDisallowable":3000.5,
      |    "maintenanceCostsDisallowable":-3000.1,
      |    "adminCostsDisallowable":-3000.2,
      |    "advertisingCostsDisallowable":-3000.3,
      |    "businessEntertainmentCostsDisallowable":-3000.4,
      |    "interestOnBankOtherLoansDisallowable":-3000.5,
      |    "financeChargesDisallowable":3000.6,
      |    "irrecoverableDebtsDisallowable":-3000.6,
      |    "professionalFeesDisallowable":3000.7,
      |    "depreciationDisallowable":-3000.7,
      |    "otherExpensesDisallowable":3000.8
      |  },
      |  "expenses":{
      |    "costOfGoodsAllowable":2000.25,
      |    "paymentsToSubcontractorsAllowable":2000.5,
      |    "wagesAndStaffCostsAllowable":2000.75,
      |    "carVanTravelExpensesAllowable":-2000.25,
      |    "premisesRunningCostsAllowable":-2000.5,
      |    "maintenanceCostsAllowable":-2000.75,
      |    "adminCostsAllowable":2001.25,
      |    "advertisingCostsAllowable":2001.5,
      |    "businessEntertainmentCostsAllowable":2001.75,
      |    "interestOnBankOtherLoansAllowable":-2001.25,
      |    "financeChargesAllowable":-2001.5,
      |    "irrecoverableDebtsAllowable":-2001.75,
      |    "professionalFeesAllowable":2002.25,
      |    "depreciationAllowable":2002.5,
      |    "otherExpensesAllowable":2002.75,
      |    "consolidatedExpenses": -2002.25
      |   }
      |}""".stripMargin)
  val requestToIfs: JsValue = Json.parse(
    """
      |{
      | "incomeSourceType": "01",
      | "adjustments": {
      |    "income": {
      |      "turnover": 1000.25,
      |      "other": 1000.5
      |    },
      |    "expenses": {
      |      "costOfGoodsAllowable": 2000.25,
      |      "paymentsToSubcontractorsAllowable": 2000.5,
      |      "wagesAndStaffCostsAllowable": 2000.75,
      |      "carVanTravelExpensesAllowable": -2000.25,
      |      "premisesRunningCostsAllowable": -2000.5,
      |      "maintenanceCostsAllowable": -2000.75,
      |      "adminCostsAllowable": 2001.25,
      |      "advertisingCostsAllowable": 2001.5,
      |      "businessEntertainmentCostsAllowable": 2001.75,
      |      "interestOnBankOtherLoansAllowable": -2001.25,
      |      "financeChargesAllowable": -2001.5,
      |      "irrecoverableDebtsAllowable": -2001.75,
      |      "professionalFeesAllowable": 2002.25,
      |      "depreciationAllowable": 2002.5,
      |      "otherExpensesAllowable": 2002.75
      |    },
      |    "additions": {
      |      "costOfGoodsDisallowable": 3000.1,
      |      "paymentsToSubcontractorsDisallowable": 3000.2,
      |      "wagesAndStaffCostsDisallowable": 3000.3,
      |      "carVanTravelExpensesDisallowable": 3000.4,
      |      "premisesRunningCostsDisallowable": 3000.5,
      |      "maintenanceCostsDisallowable": -3000.1,
      |      "adminCostsDisallowable": -3000.2,
      |      "advertisingCostsDisallowable": -3000.3,
      |      "businessEntertainmentCostsDisallowable": -3000.4,
      |      "interestOnBankOtherLoansDisallowable": -3000.5,
      |      "financeChargesDisallowable": 3000.6,
      |      "irrecoverableDebtsDisallowable": -3000.6,
      |      "professionalFeesDisallowable": 3000.7,
      |      "depreciationDisallowable": -3000.7,
      |      "otherExpensesDisallowable": 3000.8
      |    }
      |  }
      |}""".stripMargin)
  val ifsResponse: (String, String) => String = (calcId: String, typeOfBusiness: String) =>
    s"""
       |{
       |      "metadata":{
       |        "calculationId":"$calcId",
       |        "requestedDateTime":"2019-12-01T12:00:00.000Z",
       |        "taxableEntityId":"AB123456C",
       |        "taxYear":2020,
       |        "status":"valid"
       |      },
       |      "inputs":{
       |        "incomeSourceId":"X2IS01234512345",
       |        "incomeSourceType": "$typeOfBusiness",
       |        "accountingPeriodStartDate":"2019-04-06",
       |        "accountingPeriodEndDate":"2020-04-05",
       |        "source":"MTD-SA",
       |        "submissionPeriods":[
       |          {
       |            "periodId":"2019040620190705",
       |            "startDate":"2019-04-06",
       |            "endDate":"2019-07-05",
       |            "receivedDateTime":"2019-08-01T12:00:00.000Z"
       |          }
       |        ]
       |      }
       |}
       |""".stripMargin

  def submitSelfEmploymentBsasRequestBodyDesJson(model: SubmitSelfEmploymentBsasRequestBody): JsValue = {
    import model._

    val jsObjects: ListBuffer[JsObject] = ListBuffer.empty[JsObject]

    if (income.nonEmpty) {
      jsObjects += Json.obj("income" -> incomeJson(income.get))
    }

    if (additions.nonEmpty) {
      jsObjects += Json.obj("additions" -> additionsFromVendorJson(additions.get))
    }

    if (expenses.nonEmpty) {
      jsObjects += Json.obj("expenses" -> expensesFromMtdJson(expenses.get))
    }

    val json = jsObjects.fold(Json.parse("""{}""").as[JsObject])((a: JsObject, b: JsObject) => a ++ b)
    json
  }

  def submitSelfEmploymentBsasRequestBodyMtdJson(model: SubmitSelfEmploymentBsasRequestBody): JsValue = {
    import model._

    val jsObjects: ListBuffer[JsObject] = ListBuffer.empty[JsObject]

    if (income.nonEmpty) {
      jsObjects += Json.obj("income" -> incomeJson(income.get))
    }

    if (additions.nonEmpty) {
      jsObjects += Json.obj("additions" -> additionsToDesJson(additions.get))
    }

    if (expenses.nonEmpty) {
      jsObjects += Json.obj("expenses" -> expensesToDesJson(expenses.get))
    }

    val json = jsObjects.fold(Json.parse("""{}""").as[JsObject])((a: JsObject, b: JsObject) => a ++ b)
    json
  }

  def hateoasResponse(nino: String, calcId: String, taxYear: Option[String] = None): String = {
    val taxYearParam = taxYear.fold("")("?taxYear=" + _)

    s"""
       |{
       |  "links":[
       |    {
       |      "href":"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$calcId$taxYearParam",
       |      "rel":"self",
       |      "method":"GET"
       |    }
       |  ]
       |}
    """.stripMargin
  }
}
