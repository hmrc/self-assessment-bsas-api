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
import play.api.mvc.AnyContentAsJson
import v3.models.request.submitBsas.ukProperty._

object SubmitUKPropertyBsasRequestBodyFixtures {

  val invalidBsasId = "a54ba782-5ef4-/(0)*f4-ab72-4954066%%%%%%%%%%"

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

  val validNonFHLBody: SubmitUKPropertyBsasRequestBody = SubmitUKPropertyBsasRequestBody(
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
          consolidatedExpenses = None,
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

  val nonFHLIncomeZeroValue: Option[JsObject] = Some(
    Json.obj(
      "totalRentsReceived"           -> 1.45,
      "premiumsOfLeaseGrant" -> 2.45,
      "reversePremiums"      -> 3.45,
      "otherPropertyIncome"  -> 0
    ))

  val nonFHLIncomeExceedRangeValue: Option[JsObject] = Some(
    Json.obj(
      "totalRentsReceived"           -> "100000000000.00",
      "premiumsOfLeaseGrant" -> 2.45,
      "reversePremiums"      -> 3.45,
      "otherPropertyIncome"  -> 4.45
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

  val fhlInvalidConsolidatedExpenses: Option[JsObject] = Some(
    Json.obj(
      "consolidatedExpenses" -> 1000.45,
      "premisesRunningCosts" -> 1000.45
    ))

  val fhlMultipleInvalidExpenses: Option[JsObject] = Some(
    Json.obj(
      "premisesRunningCosts"  -> 100000000000.00,
      "repairsAndMaintenance" -> -100000000000.00,
      "financialCosts"        -> 1000230800000.00,
      "professionalFees"      -> -2001200034500.00,
      "costOfServices"        -> 1000.45,
      "travelCosts"           -> 1000.45,
      "other"                 -> 1000.45
    ))

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
      "totalRentsReceived"           -> 1.45,
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

  val validfhlBody: SubmitUKPropertyBsasRequestBody = SubmitUKPropertyBsasRequestBody(
    nonFurnishedHolidayLet = None,
    furnishedHolidayLet = Some(
      FurnishedHolidayLet(
        income = Some(
          FHLIncome(
            totalRentsReceived = Some(1.45),
          )),
        expenses = Some(FHLExpenses(
          consolidatedExpenses = None,
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

  val validMinimalBody: SubmitUKPropertyBsasRequestBody =
    SubmitUKPropertyBsasRequestBody(Some(NonFurnishedHolidayLet(None, None)), None)

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

  def submitBsasRawDataBodyFHL(income: Option[JsObject] = None, expenses: Option[JsObject] = None): AnyContentAsJson = {
    AnyContentAsJson(
      Json.obj(
        "furnishedHolidayLet" ->
          (income.fold(Json.obj())(income => Json.obj("income"        -> income)) ++
            expenses.fold(Json.obj())(expenses => Json.obj("expenses" -> expenses))))
    )
  }

  def submitBsasRawDataBodyNonFHL(income: Option[JsObject] = None, expenses: Option[JsObject] = None): AnyContentAsJson = {
    AnyContentAsJson(
      Json.obj(
        "nonFurnishedHolidayLet" ->
          (income.fold(Json.obj())(income => Json.obj("income"        -> income)) ++
            expenses.fold(Json.obj())(expenses => Json.obj("expenses" -> expenses))))
    )
  }

  val hateoasResponse: (String, String) => String = (nino: String, calcId: String) => s"""
       |{
       |  "id": "$calcId",
       |  "links":[
       |    {
       |      "href":"/individuals/self-assessment/adjustable-summary/$nino/property/$calcId",
       |      "rel":"self",
       |      "method":"GET"
       |    }
       |  ]
       |}
    """.stripMargin

  val fhlDesResponse: (String, String) => String = (calcId: String, typeOfBusiness: String) => s"""
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
       |          },
       |          {
       |            "periodId":"2019070620191005",
       |            "startDate":"2019-07-06",
       |            "endDate":"2019-10-05",
       |            "receivedDateTime":"2019-11-01T12:00:00.000Z"
       |          },
       |          {
       |            "periodId":"2019100620200105",
       |            "startDate":"2019-10-06",
       |            "endDate":"2020-01-05",
       |            "receivedDateTime":"2020-02-01T12:00:00.000Z"
       |          },
       |          {
       |            "periodId":"2020010620200405",
       |            "startDate":"2020-01-06",
       |            "endDate":"2020-04-05",
       |            "receivedDateTime":"2020-05-01T12:00:00.000Z"
       |          }
       |        ]
       |      },
       |      "adjustableSummaryCalculation":{
       |        "totalIncome":1.23,
       |        "income":{
       |          "rentReceived":1.23,
       |          "rarRentReceived":1.23
       |        },
       |        "totalExpenses":1.23,
       |        "expenses":{
       |          "consolidatedExpenses":-1.23,
       |          "repairsAndMaintenance":-1.23,
       |          "financialCosts":-1.23,
       |          "professionalFees":-1.23,
       |          "costOfServices":-1.23,
       |          "travelCosts":-1.23,
       |          "other":-1.23,
       |          "premisesRunningCosts":-1.23
       |        },
       |        "netProfit":1.23,
       |        "netLoss":1.23,
       |        "totalAdditions":1.23,
       |        "additions":{
       |          "privateUseAdjustment":1.23,
       |          "balancingCharge":1.23,
       |          "bpraBalancingCharge":1.23
       |        },
       |        "totalDeductions":1.23,
       |        "deductions":{
       |          "rarReliefClaimed":1.23,
       |          "annualInvestmentAllowance":1.23,
       |          "otherCapitalAllowance":1.23,
       |          "businessPremisesRenovationAllowance":1.23,
       |          "propertyAllowance":1.23
       |        },
       |        "taxableProfit":1.23,
       |        "adjustedIncomeTaxLoss":1.23
       |      },
       |      "adjustments":{
       |        "income":{
       |          "rentReceived":-1.23
       |        },
       |        "expenses":{
       |          "consolidatedExpenses":-1.23,
       |          "repairsAndMaintenance":-1.23,
       |          "financialCosts":-1.23,
       |          "professionalFees":-1.23,
       |          "costOfServices":-1.23,
       |          "travelCosts":-1.23,
       |          "other":-1.23,
       |          "premisesRunningCosts":-1.23
       |        }
       |      },
       |      "adjustedSummaryCalculation":{
       |        "totalIncome":1.23,
       |        "income":{
       |          "rentReceived":1.23,
       |          "rarRentReceived":1.23
       |        },
       |        "totalExpenses":1.23,
       |        "expenses":{
       |          "consolidatedExpenses":-1.23,
       |          "repairsAndMaintenance":-1.23,
       |          "financialCosts":-1.23,
       |          "professionalFees":-1.23,
       |          "costOfServices":-1.23,
       |          "travelCosts":-1.23,
       |          "other":-1.23,
       |          "premisesRunningCosts":-1.23
       |        },
       |        "netProfit":1.23,
       |        "netLoss":1.23,
       |        "totalAdditions":1.23,
       |        "additions":{
       |          "privateUseAdjustment":1.23,
       |          "balancingCharge":1.23,
       |          "bpraBalancingCharge":1.23
       |        },
       |        "totalDeductions":1.23,
       |        "deductions":{
       |          "rarReliefClaimed":1.23,
       |          "annualInvestmentAllowance":1.23,
       |          "otherCapitalAllowance":1.23,
       |          "businessPremisesRenovationAllowance":1.23,
       |          "propertyAllowance":1.23
       |        },
       |        "taxableProfit":1.23,
       |        "adjustedIncomeTaxLoss":1.23
       |      }
       |    }
       |""".stripMargin
}
