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

package v6.ukPropertyBsas.submit.def3.model.request

import play.api.libs.json.{JsValue, Json}

object SubmitUKPropertyBsasRequestBodyFixtures {

  val fullRequestJson: JsValue = Json.parse("""
      |{
      |"ukProperty": {
      |   "income": {
      |     "totalRentsReceived": 1000.45,
      |     "premiumsOfLeaseGrant": 1000.45,
      |     "reversePremiums": 1000.45,
      |     "otherPropertyIncome": 1000.45
      | },
      |   "expenses": {
      |     "premisesRunningCosts": -10.25,
      |     "repairsAndMaintenance": 888.78,
      |     "financialCosts": 10.25,
      |     "professionalFees": 54.45,
      |     "costOfServices": -10.50,
      |     "residentialFinancialCost": 130.32,
      |     "other": 10.78,
      |     "travelCosts": 20.45
      |    }
      |  }
      |}
      |""".stripMargin)

  val fullDownStreamRequest: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "02",
      |  "adjustments":{
      |   "income": {
      |      "totalRentsReceived": 1000.45,
      |      "premiumsOfLeaseGrant": 1000.45,
      |      "reversePremiums": 1000.45,
      |      "otherPropertyIncome": 1000.45
      |   },
      |   "expenses": {
      |      "premisesRunningCosts": -10.25,
      |      "repairsAndMaintenance": 888.78,
      |      "financialCosts": 10.25,
      |      "professionalFees": 54.45,
      |      "costOfServices": -10.50,
      |      "residentialFinancialCost": 130.32,
      |      "other": 10.78,
      |      "travelCosts": 20.45
      |     }
      |  }
      |}
      |""".stripMargin
  )

  val requestFullParsed: Def3_SubmitUkPropertyBsasRequestBody = Def3_SubmitUkPropertyBsasRequestBody(
    ukProperty = Some(
      UkProperty(
        income = Some(
          Income(
            totalRentsReceived = Some(1000.45),
            premiumsOfLeaseGrant = Some(1000.45),
            reversePremiums = Some(1000.45),
            otherPropertyIncome = Some(1000.45)
          )
        ),
        expenses = Some(Expenses(
          premisesRunningCosts = Some(-10.25),
          repairsAndMaintenance = Some(888.78),
          financialCosts = Some(10.25),
          professionalFees = Some(54.45),
          costOfServices = Some(-10.50),
          residentialFinancialCost = Some(130.32),
          other = Some(10.78),
          travelCosts = Some(20.45),
          consolidatedExpenses = None
        ))
      ))
  )

}
