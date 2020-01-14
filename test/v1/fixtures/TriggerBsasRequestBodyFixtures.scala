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

package v1.fixtures

import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.AnyContentAsJson
import v1.models.domain.TypeOfBusiness
import v1.models.request.AccountingPeriod
import v1.models.request.triggerBsas.TriggerBsasRequestBody
import v1.models.response.TriggerBsasResponse

object TriggerBsasRequestBodyFixtures {

  val seRequestBodyMtd: JsValue = Json.parse("""
      |{
      |  "accountingPeriod" : {
      |     "startDate" : "2018-11-25",
      |     "endDate" : "2018-11-26"
      |  },
      |  "typeOfBusiness" : "self-employment",
      |  "selfEmploymentId" : "anId"
      |}
  """.stripMargin)

  val seRequestBodyDes: JsValue = Json.parse("""
      |{
      |   "incomeSourceIdentifier" : "incomeSourceId",
      |   "identifierValue" : "anId",
      |   "accountingPeriodStartDate" : "2018-11-25",
      |   "accountingPeriodEndDate" : "2018-11-26"
      |}
  """.stripMargin)

  val fhlRequestBodyDes: JsValue = Json.parse("""
      |{
      |   "incomeSourceIdentifier" : "incomeSourceType",
      |   "identifierValue" : "04",
      |   "accountingPeriodStartDate" : "2018-11-25",
      |   "accountingPeriodEndDate" : "2018-11-26"
      |}
  """.stripMargin)

  val nonFhlRequestBodyDes: JsValue = Json.parse("""
      |{
      |   "incomeSourceIdentifier" : "incomeSourceType",
      |   "identifierValue" : "02",
      |   "accountingPeriodStartDate" : "2018-11-25",
      |   "accountingPeriodEndDate" : "2018-11-26"
      |}
  """.stripMargin)

  val invalidJson: JsValue = Json.parse("""
      |{
      |  "startDate" : 4,
      |  "endDate" : true
      |}
  """.stripMargin)

  val seBody: TriggerBsasRequestBody = TriggerBsasRequestBody(
    AccountingPeriod("2018-11-25", "2018-11-26"),
    TypeOfBusiness.`self-employment`,
    Some("anId")
  )

  val fhlBody: TriggerBsasRequestBody = TriggerBsasRequestBody(
    AccountingPeriod("2018-11-25", "2018-11-26"),
    TypeOfBusiness.`uk-property-fhl`,
    None
  )

  val nonFhlBody: TriggerBsasRequestBody = TriggerBsasRequestBody(
    AccountingPeriod("2018-11-25", "2018-11-26"),
    TypeOfBusiness.`uk-property-non-fhl`,
    None
  )

  val responseObj = TriggerBsasResponse("c75f40a6-a3df-4429-a697-471eeec46435")

  val response: JsValue = Json.parse("""{
      |"id" : "c75f40a6-a3df-4429-a697-471eeec46435"
      |}""".stripMargin)

  val hateoasResponseForSE: String => String = (nino: String) => s"""
      |{
      |  "id": "c75f40a6-a3df-4429-a697-471eeec46435",
      |  "links":[
      |    {
      |      "href":"/individuals/self-assessment/adjustable-summary/$nino/self-employment/c75f40a6-a3df-4429-a697-471eeec46435",
      |      "rel":"self",
      |      "method":"GET"
      |    }
      |  ]
      |}
    """.stripMargin

  val hateoasResponseForProperty: String => String = (nino: String) => s"""
      |{
      |  "id": "c75f40a6-a3df-4429-a697-471eeec46435",
      |  "links":[
      |    {
      |      "href":"/individuals/self-assessment/adjustable-summary/$nino/property/c75f40a6-a3df-4429-a697-471eeec46435",
      |      "rel":"self",
      |      "method":"GET"
      |    }
      |  ]
      |}
    """.stripMargin

  val requestBody: JsObject =
    Json.obj(
      "accountingPeriod" -> Json.obj("startDate" -> "2019-05-05", "endDate" -> "2020-05-06"),
      "typeOfBusiness"   -> TypeOfBusiness.`self-employment`.toString,
      "selfEmploymentId" -> "XAIS12345678901"
    )

  val requestBodyForProperty: JsObject =
    Json.obj("accountingPeriod" -> Json.obj("startDate" -> "2019-05-05", "endDate" -> "2020-05-06"),
             "typeOfBusiness"   -> TypeOfBusiness.`uk-property-fhl`.toString)

  val desResponse: String =
    """
      |{
      | "metadata": {
      |  "calculationId": "c75f40a6-a3df-4429-a697-471eeec46435",
      |  "requestedDateTime": "0000-01-01",
      |  "taxableEntityId": "0",
      |  "taxYear": 2020,
      |  "status": "valid"
      | },
      | "inputs": {
      |  "incomeSourceId": "111111111111111",
      |  "incomeSourceType": "01",
      |  "accountingPeriodStartDate": "0000-01-01",
      |  "accountingPeriodEndDate": "0000-01-01",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [
      |   {
      |    "periodId": "2222222222222222",
      |    "startDate": "0000-01-01",
      |    "endDate": "0000-01-01",
      |    "receivedDateTime": "0000-01-01"
      |   }
      |  ]
      | },
      | "adjustableSummaryCalculation": {
      |  "totalIncome": 0.02,
      |  "income": {
      |   "turnover": 0.02,
      |   "other": 0.02
      |  },
      |  "totalExpenses": 0.02,
      |  "expenses": {
      |   "consolidatedExpenses": 0.02,
      |   "costOfGoodsAllowable": 0.02,
      |   "paymentsToSubcontractorsAllowable": 0.02,
      |   "wagesAndStaffCostsAllowable": 0.02,
      |   "carVanTravelExpensesAllowable": 0.02,
      |   "premisesRunningCostsAllowable": 0.02,
      |   "maintenanceCostsAllowable": 0.02,
      |   "adminCostsAllowable": 0.02,
      |   "interestOnBankOtherLoansAllowable": 0.02,
      |   "financeChargesAllowable": 0.02,
      |   "irrecoverableDebtsAllowable": 0.02,
      |   "professionalFeesAllowable": 0.02,
      |   "depreciationAllowable": 0.02,
      |   "otherExpensesAllowable": 0.02,
      |   "advertisingCostsAllowable": 0.02,
      |   "businessEntertainmentCostsAllowable": 0.02
      |  },
      |  "netProfit": 0.02,
      |  "netLoss": 0.02,
      |  "additions": {
      |   "costOfGoodsDisallowable": 0.02,
      |   "paymentsToSubcontractorsDisallowable": 0.02,
      |   "wagesAndStaffCostsDisallowable": 0.02,
      |   "carVanTravelExpensesDisallowable": 0.02,
      |   "premisesRunningCostsDisallowable": 0.02,
      |   "maintenanceCostsDisallowable": 0.02,
      |   "adminCostsDisallowable": 0.02,
      |   "interestOnBankOtherLoansDisallowable": 0.02,
      |   "financeChargesDisallowable": 0.02,
      |   "irrecoverableDebtsDisallowable": 0.02,
      |   "professionalFeesDisallowable": 0.02,
      |   "depreciationDisallowable": 0.02,
      |   "otherExpensesDisallowable": 0.02,
      |   "advertisingCostsDisallowable": 0.02,
      |   "businessEntertainmentCostsDisallowable": 0.02,
      |   "outstandingBusinessIncome": 0.02,
      |   "balancingChargeOther": 0.02,
      |   "balancingChargeBpra": 0.02,
      |   "goodAndServicesOwnUse": 0.02
      |  },
      |  "deductions": {
      |   "tradingAllowance": 0.02,
      |   "annualInvestmentAllowance": 0.02,
      |   "capitalAllowanceMainPool": 0.02,
      |   "capitalAllowanceSpecialRatePool": 0.02,
      |   "zeroEmissionGoods": 0.02,
      |   "businessPremisesRenovationAllowance": 0.02,
      |   "enhancedCapitalAllowance": 0.02,
      |   "allowanceOnSales": 0.02,
      |   "capitalAllowanceSingleAssetPool": 0.02,
      |   "includedNonTaxableProfits": 0.02
      |  },
      |  "accountingAdjustments": 0.02,
      |  "selfEmploymentAccountingAdjustments": {
      |   "basisAdjustment": 0.02,
      |   "overlapReliefUsed": 0.02,
      |   "accountingAdjustment": 0.02,
      |   "averagingAdjustment": 0.02
      |  },
      |  "taxableProfit": 0.02,
      |  "adjustedIncomeTaxLoss": 0.02
      | }
      |}
      |
    """.stripMargin

  def triggerBsasRawDataBody(startDate: String = "2019-05-05",
                             endDate: String = "2020-05-06",
                             typeOfBusiness: String = TypeOfBusiness.`self-employment`.toString,
                             selfEmploymentId: Option[String] = Some("XAIS12345678901")): AnyContentAsJson = {

    AnyContentAsJson(
      Json.obj(
        "accountingPeriod" -> Json.obj("startDate" -> startDate, "endDate" -> endDate),
        "typeOfBusiness"   -> typeOfBusiness
      ) ++ selfEmploymentId.fold(Json.obj())(selfEmploymentId => Json.obj("selfEmploymentId" -> selfEmploymentId)))
  }

  def triggerBsasRequestDataBody(accountingPeriod: String = "2019-20",
                                 startDate: String = "2019-05-05",
                                 endDate: String = "2020-05-06",
                                 typeOfBusiness: TypeOfBusiness = TypeOfBusiness.`self-employment`,
                                 selfEmploymentId: Option[String] = Some("XAIS12345678901")): TriggerBsasRequestBody = {
    TriggerBsasRequestBody(AccountingPeriod(startDate, endDate), typeOfBusiness = typeOfBusiness, selfEmploymentId = selfEmploymentId)

  }

}
