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

package v3.fixtures.ukProperty

import play.api.libs.json.{ JsObject, JsValue, Json }
import v3.models.request.submitBsas.ukProperty._

object SubmitUKPropertyBsasRequestBodyFixtures {

  val mtdRequestNonFhlFull: JsValue = Json.parse("""
      |{
      |  "nonFurnishedHolidayLet": {
      |    "income": {
      |      "totalRentsReceived": 1.45,
      |      "premiumsOfLeaseGrant": 2.45,
      |      "reversePremiums": 3.45,
      |      "otherPropertyIncome": 4.45
      |    },
      |    "expenses": {
      |      "consolidatedExpenses": 5.45,
      |      "premisesRunningCosts": 6.45,
      |      "repairsAndMaintenance": 7.45,
      |      "financialCosts": 8.45,
      |      "professionalFees": 9.45,
      |      "costOfServices": 10.45,
      |      "residentialFinancialCost": 11.45,
      |      "other": 12.45,
      |      "travelCosts": 13.45
      |    }
      |  }
      |}
      |""".stripMargin)

  val validNonFHLInputJson: JsValue = Json.parse("""
     |{
     |  "nonFurnishedHolidayLet": {
     |    "income": {
     |      "totalRentsReceived": 1.45,
     |      "premiumsOfLeaseGrant": 2.45,
     |      "reversePremiums": 3.45,
     |      "otherPropertyIncome": 4.45
     |    },
     |    "expenses": {
     |      "premisesRunningCosts": 6.45,
     |      "repairsAndMaintenance": 7.45,
     |      "financialCosts": 8.45,
     |      "professionalFees": 9.45,
     |      "costOfServices": 10.45,
     |      "residentialFinancialCost": 11.45,
     |      "other": 12.45,
     |      "travelCosts": 13.45
     |    }
     |  }
     |}
     |""".stripMargin)

  val requestNonFhlFullModel: SubmitUKPropertyBsasRequestBody = SubmitUKPropertyBsasRequestBody(
    nonFurnishedHolidayLet = Some(
      NonFurnishedHolidayLet(
        income = Some(
          NonFHLIncome(
            totalRentsReceived = Some(1.45),
            premiumsOfLeaseGrant = Some(2.45),
            reversePremiums = Some(3.45),
            otherPropertyIncome = Some(4.45)
          )),
        expenses = Some(NonFHLExpenses(
          consolidatedExpenses = Some(5.45),
          premisesRunningCosts = Some(6.45),
          repairsAndMaintenance = Some(7.45),
          financialCosts = Some(8.45),
          professionalFees = Some(9.45),
          costOfServices = Some(10.45),
          residentialFinancialCost = Some(11.45),
          other = Some(12.45),
          travelCosts = Some(13.45)
        ))
      )),
    furnishedHolidayLet = None
  )

  val nonFHLBody: SubmitUKPropertyBsasRequestBody = requestNonFhlFullModel

  val downstreamRequestNonFhlFull: JsValue = Json.parse("""
    |{
    |  "incomeSourceType": "02",
    |  "adjustments": {
    |    "income": {
    |      "totalRentsReceived": 1.45,
    |      "premiumsOfLeaseGrant": 2.45,
    |      "reversePremiums": 3.45,
    |      "otherPropertyIncome": 4.45
    |    },
    |    "expenses": {
    |      "consolidatedExpenses": 5.45,
    |      "premisesRunningCosts": 6.45,
    |      "repairsAndMaintenance": 7.45,
    |      "financialCosts": 8.45,
    |      "professionalFees": 9.45,
    |      "costOfServices": 10.45,
    |      "residentialFinancialCost": 11.45,
    |      "other": 12.45,
    |      "travelCosts": 13.45
    |    }
    |  }
    |}
    |""".stripMargin)

  val nonFHLExpensesAllFields: Option[JsObject] = Some(
    Json.obj(
      "premisesRunningCosts"     -> 6.45,
      "repairsAndMaintenance"    -> 7.45,
      "financialCosts"           -> 8.45,
      "professionalFees"         -> 9.45,
      "costOfServices"           -> 10.45,
      "residentialFinancialCost" -> 11.45,
      "other"                    -> 12.45,
      "travelCosts"              -> 13.45
    ))

  val mtdRequestFhlFull: JsValue = Json.parse("""
      |{
      |  "furnishedHolidayLet": {
      |    "income": {
      |      "totalRentsReceived": 1.45
      |    },
      |    "expenses": {
      |      "consolidatedExpenses": 2.45,
      |      "premisesRunningCosts": 3.45,
      |      "repairsAndMaintenance": 4.45,
      |      "financialCosts": 5.45,
      |      "professionalFees": 6.45,
      |      "costOfServices": 7.45,
      |      "other": 8.45,
      |      "travelCosts": 9.45
      |    }
      |  }
      |}
      |""".stripMargin)

  val validfhlInputJson: JsValue = Json.parse("""
      |{
      |  "furnishedHolidayLet": {
      |    "income": {
      |      "totalRentsReceived": 1.45
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 3.45,
      |      "repairsAndMaintenance": 4.45,
      |      "financialCosts": 5.45,
      |      "professionalFees": 6.45,
      |      "costOfServices": 7.45,
      |      "other": 8.45,
      |      "travelCosts": 9.45
      |    }
      |  }
      |}
      |""".stripMargin)

  val fhlIncomeAllFields: Option[JsObject] = Some(Json.obj("totalRentsReceived" -> 1.45))

  val fhlExpensesAllFields: Option[JsObject] = Some(
    Json.obj(
      "premisesRunningCosts"  -> 3.45,
      "repairsAndMaintenance" -> 4.45,
      "financialCosts"        -> 5.45,
      "professionalFees"      -> 6.45,
      "costOfServices"        -> 7.45,
      "other"                 -> 8.45,
      "travelCosts"           -> 9.45
    ))

  val nonFHLIncomeAllFields: Option[JsObject] = Some(
    Json.obj(
      "totalRentsReceived"   -> 1.45,
      "premiumsOfLeaseGrant" -> 2.45,
      "reversePremiums"      -> 3.45,
      "otherPropertyIncome"  -> 4.45
    ))

  val requestFhlFullModel: SubmitUKPropertyBsasRequestBody = SubmitUKPropertyBsasRequestBody(
    nonFurnishedHolidayLet = None,
    furnishedHolidayLet = Some(
      FurnishedHolidayLet(
        income = Some(
          FHLIncome(
            totalRentsReceived = Some(1.45),
          )),
        expenses = Some(FHLExpenses(
          consolidatedExpenses = Some(2.45),
          premisesRunningCosts = Some(3.45),
          repairsAndMaintenance = Some(4.45),
          financialCosts = Some(5.45),
          professionalFees = Some(6.45),
          costOfServices = Some(7.45),
          other = Some(8.45),
          travelCosts = Some(9.45)
        ))
      ))
  )

  val fhlBody: SubmitUKPropertyBsasRequestBody = requestFhlFullModel

  val downstreamRequestFhlFull: JsValue = Json.parse("""
     |{
     |  "incomeSourceType": "04",
     |  "adjustments": {
     |    "income": {
     |      "rentReceived": 1.45
     |    },
     |    "expenses": {
     |      "consolidatedExpenses": 2.45,
     |      "premisesRunningCosts": 3.45,
     |      "repairsAndMaintenance": 4.45,
     |      "financialCosts": 5.45,
     |      "professionalFees": 6.45,
     |      "costOfServices": 7.45,
     |      "other": 8.45,
     |      "travelCosts": 9.45
     |    }
     |  }
     |}
     |""".stripMargin)

  def submitBsasRawDataBodyFHL(income: Option[JsObject] = None, expenses: Option[JsObject] = None): JsValue = {
    Json.obj(
      "furnishedHolidayLet" ->
        (income.fold(Json.obj())(income => Json.obj("income"        -> income)) ++
          expenses.fold(Json.obj())(expenses => Json.obj("expenses" -> expenses))))
  }

  def submitBsasRawDataBodyNonFHL(income: Option[JsObject] = None, expenses: Option[JsObject] = None): JsValue = {
    Json.obj(
      "nonFurnishedHolidayLet" ->
        (income.fold(Json.obj())(income => Json.obj("income"        -> income)) ++
          expenses.fold(Json.obj())(expenses => Json.obj("expenses" -> expenses))))
  }

  def hateoasResponse(nino: String, calcId: String): String = s"""
       |{
       |  "links":[
       |    {
       |      "href":"/individuals/self-assessment/adjustable-summary/$nino/uk-property/$calcId",
       |      "rel":"self",
       |      "method":"GET"
       |    }
       |  ]
       |}
    """.stripMargin

  def hateoasResponseWithTaxYearParam(nino: String, calcId: String, taxYear: String): String =
    s"""
       |{
       |  "links":[
       |    {
       |      "href":"/individuals/self-assessment/adjustable-summary/$nino/uk-property/$calcId?taxYear=$taxYear",
       |      "rel":"self",
       |      "method":"GET"
       |    }
       |  ]
       |}
    """.stripMargin
}
