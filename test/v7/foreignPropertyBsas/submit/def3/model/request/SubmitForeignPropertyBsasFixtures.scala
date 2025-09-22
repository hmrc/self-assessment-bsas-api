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

package v7.foreignPropertyBsas.submit.def3.model.request

import play.api.libs.json.{JsValue, Json}

object SubmitForeignPropertyBsasFixtures {

  val mtdRequestForeignPropertyFull: JsValue = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "countryLevelDetail": [
      |            {
      |                "countryCode": "FRA",
      |                "income": {
      |                    "totalRentsReceived": 1.12,
      |                    "premiumsOfLeaseGrant": 2.12,
      |                    "otherPropertyIncome": 3.12
      |                },
      |                "expenses": {
      |                    "consolidatedExpenses": 4.12,
      |                    "premisesRunningCosts": 5.12,
      |                    "repairsAndMaintenance": 6.12,
      |                    "financialCosts": 7.12,
      |                    "professionalFees": 8.12,
      |                    "costOfServices": 9.12,
      |                    "residentialFinancialCost": 10.12,
      |                    "other": 11.12,
      |                    "travelCosts": 12.12
      |                }
      |            }
      |        ]
      |    }
      |}
    """.stripMargin
  )

  val mtdRequestForeignPropertyValid: JsValue = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "countryLevelDetail": [
      |            {
      |                "countryCode": "FRA",
      |                "income": {
      |                    "totalRentsReceived": 1.12,
      |                    "premiumsOfLeaseGrant": 2.12,
      |                    "otherPropertyIncome": 3.12
      |                },
      |                "expenses": {
      |                    "premisesRunningCosts": 5.12,
      |                    "repairsAndMaintenance": 6.12,
      |                    "financialCosts": 7.12,
      |                    "professionalFees": 8.12,
      |                    "costOfServices": 9.12,
      |                    "residentialFinancialCost": 10.12,
      |                    "other": 11.12,
      |                    "travelCosts": 12.12
      |                }
      |            }
      |        ]
      |    }
      |}
    """.stripMargin
  )

  val downstreamRequestForeignPropertyFull: JsValue = Json.parse(
    """
      |{
      |    "incomeSourceType": "15",
      |    "adjustments": [
      |        {
      |            "countryCode": "FRA",
      |            "income": {
      |                "rent": 1.12,
      |                "premiumsOfLeaseGrant": 2.12,
      |                "otherPropertyIncome": 3.12
      |            },
      |            "expenses": {
      |                "consolidatedExpenses": 4.12,
      |                "premisesRunningCosts": 5.12,
      |                "repairsAndMaintenance": 6.12,
      |                "financialCosts": 7.12,
      |                "professionalFees": 8.12,
      |                "costOfServices": 9.12,
      |                "residentialFinancialCost": 10.12,
      |                "other": 11.12,
      |                "travelCosts": 12.12
      |            }
      |        }
      |    ]
      |}
    """.stripMargin
  )

  val requestForeignPropertyFull: Def3_SubmitForeignPropertyBsasRequestBody = Def3_SubmitForeignPropertyBsasRequestBody(
    foreignProperty = Some(
      ForeignProperty(
        countryLevelDetail = Some(
          Seq(
            CountryLevelDetail(
              countryCode = "FRA",
              income = Some(
                ForeignPropertyIncome(
                  totalRentsReceived = Some(1.12),
                  premiumsOfLeaseGrant = Some(2.12),
                  otherPropertyIncome = Some(3.12)
                )
              ),
              expenses = Some(
                ForeignPropertyExpenses(
                  consolidatedExpenses = Some(4.12),
                  premisesRunningCosts = Some(5.12),
                  repairsAndMaintenance = Some(6.12),
                  financialCosts = Some(7.12),
                  professionalFees = Some(8.12),
                  costOfServices = Some(9.12),
                  residentialFinancialCost = Some(10.12),
                  other = Some(11.12),
                  travelCosts = Some(12.12)
                )
              )
            )
          )
        ),
        zeroAdjustments = None
      )
    )
  )

  val downstreamRequestValid: JsValue = Json.parse(
    """
      |{
      |    "incomeSourceType": "15",
      |    "adjustments": [
      |        {
      |            "countryCode": "FRA",
      |            "income": {
      |                "rent": 1.12,
      |                "premiumsOfLeaseGrant": 2.12,
      |                "otherPropertyIncome": 3.12
      |            },
      |            "expenses": {
      |                "premisesRunningCosts": 5.12,
      |                "repairsAndMaintenance": 6.12,
      |                "financialCosts": 7.12,
      |                "professionalFees": 8.12,
      |                "travelCosts": 12.12,
      |                "costOfServices": 9.12,
      |                "residentialFinancialCost": 10.12,
      |                "other": 11.12
      |            }
      |        }
      |    ]
      |}
    """.stripMargin
  )

  val downstreamRequestWithOnlyZeroAdjustments: JsValue = Json.parse(
    """
      |{
      |    "incomeSourceType": "15",
      |    "adjustments": {
      |        "zeroAdjustments": true
      |    }
      |}
    """.stripMargin
  )

  def mtdRequestWithOnlyZeroAdjustments(zeroAdjustments: Boolean): JsValue = Json.parse(
    s"""
      |{
      |    "foreignProperty": {
      |        "zeroAdjustments": $zeroAdjustments
      |    }
      |}
    """.stripMargin
  )

  def mtdRequestWithZeroAndOtherAdjustments(zeroAdjustments: Boolean): JsValue = Json.parse(
    s"""
      |{
      |    "foreignProperty": {
      |        "countryLevelDetail": [
      |            {
      |                "countryCode": "FRA",
      |                "income": {
      |                    "totalRentsReceived": 1000.25
      |                },
      |                "expenses": {
      |                    "premisesRunningCosts": 2000.25
      |                }
      |            }
      |        ],
      |        "zeroAdjustments": $zeroAdjustments
      |    }
      |}
    """.stripMargin
  )

}
