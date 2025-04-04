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

package v7.foreignPropertyBsas.submit.def2.model.request

import play.api.libs.json.{JsValue, Json}

object SubmitForeignPropertyBsasFixtures {

  val mtdRequestFull: JsValue = Json.parse(
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

  val mtdRequestForeignPropertyInvalid: JsValue = Json.parse(
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
      |                    "residentialFinancialCost": -3000.93,
      |                    "other": 11.12,
      |                    "travelCosts": 12.12
      |                }
      |            }
      |        ]
      |    }
      |}
    """.stripMargin
  )

  val downstreamRequestFull: JsValue = Json.parse(
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

  val requestFull: Def2_SubmitForeignPropertyBsasRequestBody = Def2_SubmitForeignPropertyBsasRequestBody(
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
    ),
    foreignFhlEea = None
  )

  val mtdRequestFhlFull: JsValue = Json.parse(
    """
      |{
      |    "foreignFhlEea": {
      |        "income": {
      |            "totalRentsReceived": 1.12
      |        },
      |        "expenses": {
      |            "consolidatedExpenses": 2.12,
      |            "premisesRunningCosts": 3.12,
      |            "repairsAndMaintenance": 4.12,
      |            "financialCosts": 5.12,
      |            "professionalFees": 6.12,
      |            "costOfServices": 7.12,
      |            "other": 8.12,
      |            "travelCosts": 9.12
      |        }
      |    }
      |}
    """.stripMargin
  )

  val downstreamRequestFhlFull: JsValue = Json.parse(
    """
      |{
      |    "incomeSourceType": "03",
      |    "adjustments": {
      |        "income": {
      |            "rentAmount": 1.12
      |        },
      |        "expenses": {
      |            "consolidatedExpenses": 2.12,
      |            "premisesRunningCosts": 3.12,
      |            "repairsAndMaintenance": 4.12,
      |            "financialCosts": 5.12,
      |            "professionalFees": 6.12,
      |            "costOfServices": 7.12,
      |            "other": 8.12,
      |            "travelCosts": 9.12
      |        }
      |    }
      |}
    """.stripMargin
  )

  val requestFhlFull: Def2_SubmitForeignPropertyBsasRequestBody = Def2_SubmitForeignPropertyBsasRequestBody(
    foreignProperty = None,
    foreignFhlEea = Some(
      FhlEea(
        income = Some(
          FhlIncome(
            totalRentsReceived = Some(1.12)
          )),
        expenses = Some(
          FhlEeaExpenses(
            consolidatedExpenses = Some(2.12),
            premisesRunningCosts = Some(3.12),
            repairsAndMaintenance = Some(4.12),
            financialCosts = Some(5.12),
            professionalFees = Some(6.12),
            costOfServices = Some(7.12),
            other = Some(8.12),
            travelCosts = Some(9.12)
          )),
        zeroAdjustments = None
      )
    )
  )

  val mtdRequestValid: JsValue = Json.parse(
    """
      |{
      |    "foreignFhlEea": {
      |        "income": {
      |            "totalRentsReceived": 1.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 3.12,
      |            "repairsAndMaintenance": 4.12,
      |            "financialCosts": 5.12,
      |            "professionalFees": 6.12,
      |            "costOfServices": 7.12,
      |            "other": 8.12,
      |            "travelCosts": 9.12
      |        }
      |    }
      |}
    """.stripMargin
  )

  val downstreamRequestValid: JsValue = Json.parse(
    """
      |{
      |    "incomeSourceType": "03",
      |    "adjustments": {
      |        "income": {
      |            "rentAmount": 1.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 3.12,
      |            "repairsAndMaintenance": 4.12,
      |            "financialCosts": 5.12,
      |            "professionalFees": 6.12,
      |            "costOfServices": 7.12,
      |            "other": 8.12,
      |            "travelCosts": 9.12
      |        }
      |    }
      |}
    """.stripMargin
  )

  def downstreamRequestWithOnlyZeroAdjustments(incomeSourceType: String): JsValue =
    Json.parse(
      s"""
        |{
        |    "incomeSourceType": "$incomeSourceType",
        |    "adjustments": {
        |        "zeroAdjustments": true
        |    }
        |}
      """.stripMargin
    )

  def mtdRequestWithOnlyZeroAdjustments(propertyType: String, zeroAdjustments: Boolean): JsValue =
    Json.parse(
      s"""
        |{
        |    "$propertyType": {
        |        "zeroAdjustments": $zeroAdjustments
        |    }
        |}
      """.stripMargin
    )

  def mtdRequestWithZeroAndOtherAdjustments(propertyType: String, zeroAdjustments: Boolean): JsValue =
    if (propertyType == "foreignProperty") {
      Json.parse(
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
    } else {
      Json.parse(
        s"""
          |{
          |    "foreignFhlEea": {
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

}
