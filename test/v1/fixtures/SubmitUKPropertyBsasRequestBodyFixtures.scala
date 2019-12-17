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

package v1.fixtures

import v1.models.request.submitBsas._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.AnyContentAsJson
import v1.models.errors.MtdError

object SubmitUKPropertyBsasRequestBodyFixtures {

  val invalidBsasId = "a54ba782-5ef4-/(0)*f4-ab72-4954066%%%%%%%%%%"

  val nonFHLInputJson: JsValue = Json.parse(
    """
      |{
      | "nonFurnishedHolidayLet": {
      |   "income": {
      |     "rentIncome": 1000.45,
      |     "premiumsOfLeaseGrant": 1000.45,
      |     "reversePremiums": 1000.45,
      |     "otherPropertyIncome": 1000.45
      |   },
      |   "expenses": {
      |     "premisesRunningCosts": 1000.45,
      |     "repairsAndMaintenance": 1000.45,
      |     "financialCosts": 1000.45,
      |     "professionalFees": 1000.45,
      |     "travelCosts": 1000.45,
      |     "costOfServices": 1000.45,
      |     "residentialFinancialCost": 1000.45,
      |     "other": 1000.45,
      |     "consolidatedExpenses": 1000.45
      |   }
      | }
      |}
      |""".stripMargin)

  val validNonFHLInputJson: JsValue = Json.parse(
    """
      |{
      | "nonFurnishedHolidayLet": {
      |   "income": {
      |     "rentIncome": 1000.45,
      |     "premiumsOfLeaseGrant": 1000.45,
      |     "reversePremiums": 1000.45,
      |     "otherPropertyIncome": 1000.45
      |   },
      |   "expenses": {
      |     "premisesRunningCosts": 1000.45,
      |     "repairsAndMaintenance": 1000.45,
      |     "financialCosts": 1000.45,
      |     "professionalFees": 1000.45,
      |     "travelCosts": 1000.45,
      |     "costOfServices": 1000.45,
      |     "residentialFinancialCost": 1000.45,
      |     "other": 1000.45
      |   }
      | }
      |}
      |""".stripMargin)

  val nonFHLRequestJson: JsValue = Json.parse(
    """
      |{
      |  "nonFurnishedHolidayLet": {
      |     "income": {
      |       "totalRentsReceived": "1000.45",
      |       "premiumsOfLeaseGrant": "1000.45",
      |       "reversePremiums": "1000.45",
      |       "otherPropertyIncome": "1000.45"
      |     },
      |     "expenses": {
      |       "premisesRunningCosts": "1000.45",
      |       "repairsAndMaintenance": "1000.45",
      |       "financialCosts": "1000.45",
      |       "professionalFees": "1000.45",
      |       "travelCosts": "1000.45",
      |       "costOfServices": "1000.45",
      |       "residentialFinancialCost": "1000.45",
      |       "other": "1000.45",
      |       "consolidatedExpenses": "1000.45"
      |     }
      |   }
      |}
      |""".stripMargin)


  val nonFHLInvalidJson: JsValue = Json.parse(
    """
      |{
      |   "nonFurnishedHolidayLet": {
      |     "income": {
      |       "totalRentsReceived": 1000.45,
      |       "premiumsOfLeaseGrant": true,
      |       "reversePremiums": 1000.45,
      |       "otherPropertyIncome": 1000.45
      |     },
      |     "expenses": {
      |       "premisesRunningCosts": 1000.45,
      |       "repairsAndMaintenance": 1000.45,
      |       "financialCosts": true,
      |       "professionalFees": 1000.45,
      |       "travelCosts": 1000.45,
      |       "costOfServices": false,
      |       "residentialFinancialCost": 1000.45,
      |       "other": 1000.45,
      |       "consolidatedExpenses": 1000.45
      |     }
      |   }
      |}
      |""".stripMargin)

  val nonFHLBody: SubmitUKPropertyBsasRequestBody =
    SubmitUKPropertyBsasRequestBody(
      Some(NonFurnishedHolidayLet(
        Some(NonFHLIncome(Some(1000.45), Some(1000.45), Some(1000.45), Some(1000.45))),
        Some(NonFHLExpenses(Some(1000.45), Some(1000.45), Some(1000.45),
          Some(1000.45), Some(1000.45), Some(1000.45),
          Some(1000.45), Some(1000.45), Some(1000.45)))
      )),
      None
    )

  val validNonFHLBody: SubmitUKPropertyBsasRequestBody =
    SubmitUKPropertyBsasRequestBody(
      Some(NonFurnishedHolidayLet(
        Some(NonFHLIncome(Some(1000.45), Some(1000.45), Some(1000.45), Some(1000.45))),
        Some(NonFHLExpenses(Some(1000.45), Some(1000.45), Some(1000.45),
          Some(1000.45), Some(1000.45), Some(1000.45),
          Some(1000.45), Some(1000.45), None))
      )),
      None
    )

  val nonFHLExpensesAllFields: Option[JsObject] = Some(Json.obj(
    "premisesRunningCosts" -> -1000.45,
    "repairsAndMaintenance" -> 1000.45,
    "financialCosts" -> 1000.45,
    "professionalFees" -> 1000.45,
    "travelCosts" -> 1000.45,
    "costOfServices" -> -1000.45,
    "residentialFinancialCost" -> 1000.45,
    "other" -> 1000.45
  ))

  val nonFHLIncomeZeroValue: Option[JsObject] = Some(Json.obj(
    "rentIncome" -> 1000.45,
    "premiumsOfLeaseGrant" -> 1000.45,
    "reversePremiums" -> 1000.45,
    "otherPropertyIncome" -> 0
  ))

  val nonFHLIncomeExceedRangeValue: Option[JsObject] = Some(Json.obj(
    "rentIncome" -> "100000000000.00",
    "premiumsOfLeaseGrant" -> 1000.45,
    "reversePremiums" -> 1000.45,
    "otherPropertyIncome" -> 1000.45
  ))

  val fhlInputJson: JsValue = Json.parse(
    """
      |{
      | "furnishedHolidayLet": {
      |   "income": {
      |     "rentIncome": 1000.45
      |   },
      |   "expenses": {
      |     "premisesRunningCosts": 1000.45,
      |     "repairsAndMaintenance": 1000.45,
      |     "financialCosts": 1000.45,
      |     "professionalFees": 1000.45,
      |     "costOfServices": 1000.45,
      |     "travelCosts": 1000.45,
      |     "other": 1000.45,
      |     "consolidatedExpenses": 1000.45
      |   }
      | }
      |}
      |""".stripMargin)

  val validfhlInputJson: JsValue = Json.parse(
    """
      |{
      | "furnishedHolidayLet": {
      |   "income": {
      |     "rentIncome": 1000.45
      |   },
      |   "expenses": {
      |     "premisesRunningCosts": 1000.45,
      |     "repairsAndMaintenance": 1000.45,
      |     "financialCosts": 1000.45,
      |     "professionalFees": 1000.45,
      |     "costOfServices": 1000.45,
      |     "travelCosts": 1000.45,
      |     "other": 1000.45
      |   }
      | }
      |}
      |""".stripMargin)

  val fhlRequestJson: JsValue = Json.parse(
    """
      |{
      | "furnishedHolidayLet": {
      |   "income": {
      |     "totalRentsReceived": "1000.45"
      |   },
      |   "expenses": {
      |     "premisesRunningCosts": "1000.45",
      |     "repairsAndMaintenance": "1000.45",
      |     "financialCosts": "1000.45",
      |     "professionalFees": "1000.45",
      |     "costOfServices": "1000.45",
      |     "travelCosts": "1000.45",
      |     "other": "1000.45",
      |     "consolidatedExpenses": "1000.45"
      |   }
      | }
      |}
      |""".stripMargin)

  val fhlInvalidJson: JsValue = Json.parse(
    """
      |{
      | "furnishedHolidayLet": {
      |   "income": {
      |     "rentIncome": "1000.45"
      |   },
      |   "expenses": {
      |     "premisesRunningCosts": 1000.45,
      |     "repairsAndMaintenance": false,
      |     "financialCosts": 1000.45,
      |     "professionalFees": 1000.45,
      |     "costOfServices": true,
      |     "travelCosts": 1000.45,
      |     "other": 1000.45,
      |     "consolidatedExpenses": "1000.45"
      |   }
      | }
      |}
      |""".stripMargin)

  val fhlIncomeAllFields: Option[JsObject] = Some(Json.obj("rentIncome" -> 1000.45))

  val fhlInvalidConsolidatedExpenses: Option[JsObject] = Some(Json.obj(
    "consolidatedExpenses" -> 1000.45,
    "premisesRunningCosts" -> 1000.45
  ))

  val fhlMultipleInvalidExpenses: Option[JsObject] = Some(Json.obj(
    "premisesRunningCosts" -> "100000000000.00",
    "repairsAndMaintenance" -> "-100000000000.00",
    "financialCosts" -> "1000230800000.00",
    "professionalFees" -> "-2001200034500.00",
    "costOfServices" -> 1000.45,
    "travelCosts" -> 1000.45,
    "other" -> 1000.45
  ))

  val fhlExpensesAllFields: Option[JsObject] = Some(Json.obj(
    "premisesRunningCosts" -> 1000.45,
    "repairsAndMaintenance" -> 1000.45,
    "financialCosts" -> 1000.45,
    "professionalFees" -> 1000.45,
    "costOfServices" -> 1000.45,
    "travelCosts" -> 1000.45,
    "other" -> 1000.45
  ))

  val nonFHLIncomeAllFields: Option[JsObject] = Some(Json.obj(
    "rentIncome" -> 1000.45,
    "premiumsOfLeaseGrant" -> 1000.45,
    "reversePremiums" -> 1000.45,
    "otherPropertyIncome" -> 1000.45
  ))


  val fhlBody: SubmitUKPropertyBsasRequestBody =
    SubmitUKPropertyBsasRequestBody(
      None,
      Some(FurnishedHolidayLet(
        Some(FHLIncome(Some(1000.45))),
        Some(FHLExpenses(Some(1000.45), Some(1000.45), Some(1000.45),
          Some(1000.45), Some(1000.45), Some(1000.45),
          Some(1000.45), Some(1000.45)))
      ))
    )

  val validfhlBody: SubmitUKPropertyBsasRequestBody =
    SubmitUKPropertyBsasRequestBody(
      None,
      Some(FurnishedHolidayLet(
        Some(FHLIncome(Some(1000.45))),
        Some(FHLExpenses(Some(1000.45), Some(1000.45), Some(1000.45),
          Some(1000.45), Some(1000.45), Some(1000.45),
          Some(1000.45), None)
        ))
      )
    )

  val validMinimalBody: SubmitUKPropertyBsasRequestBody =
    SubmitUKPropertyBsasRequestBody(Some(NonFurnishedHolidayLet(None, None)), None)

  def rangeError(fieldName: String): MtdError =
    MtdError("RULE_RANGE_INVALID", s"Adjustment value $fieldName falls outside the accepted range")

  def zeroError(fieldName: String): MtdError =
    MtdError("FORMAT_ADJUSTMENT_VALUE", s"The format of $fieldName value is invalid")

  def submitBsasRawDataBodyFHL(income: Option[JsObject] = None,
                               expenses: Option[JsObject] = None): AnyContentAsJson = {
    AnyContentAsJson(
      Json.obj("furnishedHolidayLet" ->
        (income.fold(Json.obj())(income => Json.obj("income" -> income)) ++
          expenses.fold(Json.obj())(expenses => Json.obj("expenses" -> expenses)))
      )
    )
  }

  def submitBsasRawDataBodyNonFHL(income: Option[JsObject] = None,
                                  expenses: Option[JsObject] = None): AnyContentAsJson = {
    AnyContentAsJson(
      Json.obj("nonFurnishedHolidayLet" ->
        (income.fold(Json.obj())(income => Json.obj("income" -> income)) ++
          expenses.fold(Json.obj())(expenses => Json.obj("expenses" -> expenses)))
      )
    )
  }
}
