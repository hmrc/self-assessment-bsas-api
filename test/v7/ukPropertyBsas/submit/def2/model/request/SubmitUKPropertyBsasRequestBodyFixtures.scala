/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.ukPropertyBsas.submit.def2.model.request

import play.api.libs.json.{JsValue, Json}

object SubmitUKPropertyBsasRequestBodyFixtures {

  val mtdRequestUkPropertyFull: JsValue = Json.parse("""
      |{
      |  "ukProperty": {
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

  val validUkPropertyInputJson: JsValue = Json.parse("""
      |{
      |  "ukProperty": {
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

  val requestUkPropertyFull: Def2_SubmitUkPropertyBsasRequestBody = Def2_SubmitUkPropertyBsasRequestBody(
    ukProperty = Some(
      UkProperty(
        income = Some(
          Income(
            totalRentsReceived = Some(1.45),
            premiumsOfLeaseGrant = Some(2.45),
            reversePremiums = Some(3.45),
            otherPropertyIncome = Some(4.45)
          )),
        expenses = Some(Expenses(
          consolidatedExpenses = Some(5.45),
          premisesRunningCosts = Some(6.45),
          repairsAndMaintenance = Some(7.45),
          financialCosts = Some(8.45),
          professionalFees = Some(9.45),
          costOfServices = Some(10.45),
          residentialFinancialCost = Some(11.45),
          other = Some(12.45),
          travelCosts = Some(13.45)
        )),
        zeroAdjustments = None
      )),
    furnishedHolidayLet = None
  )

  val downstreamRequestUkPropertyFull: JsValue = Json.parse("""
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

  val requestFhlFull: Def2_SubmitUkPropertyBsasRequestBody = Def2_SubmitUkPropertyBsasRequestBody(
    ukProperty = None,
    furnishedHolidayLet = Some(
      FurnishedHolidayLet(
        income = Some(
          FHLIncome(
            totalRentsReceived = Some(1.45)
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
        )),
        zeroAdjustments = None
      ))
  )

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

  def downstreamRequestWithOnlyZeroAdjustments(incomeSourceType: String): JsValue = Json.parse(
    s"""
      |{
      |    "incomeSourceType": "$incomeSourceType",
      |    "adjustments": {
      |        "zeroAdjustments": true
      |    }
      |}
    """.stripMargin
  )

  def mtdRequestWithOnlyZeroAdjustments(propertyType: String, zeroAdjustments: Boolean): JsValue = Json.parse(
    s"""
      |{
      |    "$propertyType": {
      |        "zeroAdjustments": $zeroAdjustments
      |    }
      |}
    """.stripMargin
  )

  def mtdRequestWithZeroAndOtherAdjustments(propertyType: String, zeroAdjustments: Boolean): JsValue = Json.parse(
    s"""
      |{
      |   "$propertyType": {
      |        "zeroAdjustments": $zeroAdjustments,
      |        "income": {
      |            "totalRentsReceived": 1000.25
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 2000.25
      |        }
      |    }
      |}
    """.stripMargin
  )

}
