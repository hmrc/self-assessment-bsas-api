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

package v2.fixtures.ukProperty

import java.time.LocalDate

import play.api.libs.json.Json
import v2.models.domain.TypeOfBusiness
import v2.models.response.retrieveBsas.AccountingPeriod
import v2.models.response.retrieveBsasAdjustments.ukProperty._

object RetrieveBsasUkPropertyAdjustmentsFixtures {

  val desJsonNonFhl = Json.parse("""{
      |  "inputs": {
      |    "incomeSourceType" : "02",
      |    "incomeSourceId": "000000000000210",
      |    "accountingPeriodStartDate" : "2018-10-11",
      |    "accountingPeriodEndDate" : "2019-10-10"
      |  },
      |  "metadata": {
      |    "taxYear" : 2020,
      |    "calculationId" : "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "requestedDateTime" : "2019-10-14T11:33:27Z",
      |    "status" : "valid"
      |  },
      |  "adjustments" : {
      |     "income": {
      |        "totalRentsReceived": 100.49,
      |        "premiumsOfLeaseGrant": 100.49,
      |        "reversePremiums": 100.49,
      |        "otherPropertyIncome": 100.49
      |     },
      |     "expenses" : {
      |      "premisesRunningCosts" : 100.49,
      |      "repairsAndMaintenance" : 100.49,
      |      "financialCosts" : 100.49,
      |      "professionalFees" : 100.49,
      |      "travelCosts" : 100.49,
      |      "costOfServices" : 100.49,
      |      "residentialFinancialCost" : 100.49,
      |      "other" : 100.49
      |    }
      |  }
      |}
    """.stripMargin)

  val desJsonFhl = Json.parse("""{
      |  "inputs": {
      |    "incomeSourceType" : "04",
      |    "incomeSourceId": "000000000000210",
      |    "accountingPeriodStartDate" : "2018-10-11",
      |    "accountingPeriodEndDate" : "2019-10-10"
      |  },
      |  "metadata": {
      |    "taxYear" : 2020,
      |    "calculationId" : "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "requestedDateTime" : "2019-10-14T11:33:27Z",
      |    "status" : "valid"
      |  },
      |  "adjustments" : {
      |     "income": {
      |        "rentReceived": 100.49
      |     },
      |     "expenses" : {
      |      "premisesRunningCosts" : 100.49,
      |      "repairsAndMaintenance" : 100.49,
      |      "financialCosts" : 100.49,
      |      "professionalFees" : 100.49,
      |      "travelCosts" : 100.49,
      |      "costOfServices" : 100.49,
      |      "other" : 100.49
      |    }
      |  }
      |}
    """.stripMargin)

  val desJsonWithWrongTypeOfBusiness = Json.parse("""{
      |  "inputs": {
      |    "incomeSourceType" : "01",
      |    "incomeSourceId": "000000000000210",
      |    "accountingPeriodStartDate" : "2018-10-11",
      |    "accountingPeriodEndDate" : "2019-10-10"
      |  },
      |  "metadata": {
      |    "taxYear" : 2020,
      |    "calculationId" : "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "requestedDateTime" : "2019-10-14T11:33:27Z",
      |    "status" : "valid"
      |  },
      |  "adjustments" : {
      |     "income": {
      |        "rentReceived": 100.49,
      |        "premiumsOfLeaseGrant": 100.49,
      |        "reversePremiums": 100.49,
      |        "otherPropertyIncome": 100.49
      |     },
      |     "expenses" : {
      |      "premisesRunningCosts" : 100.49,
      |      "repairsAndMaintenance" : 100.49,
      |      "financialCosts" : 100.49,
      |      "professionalFees" : 100.49,
      |      "travelCosts" : 100.49,
      |      "costOfServices" : 100.49,
      |      "residentialFinancialCost" : 100.49,
      |      "other" : 100.49
      |    }
      |  }
      |}
    """.stripMargin)

  val expenseBreakdownModel: ExpensesBreakdown = ExpensesBreakdown(
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    None
  )

  val incomeModel: IncomeBreakdown = IncomeBreakdown(Some(100.49), Some(100.49), Some(100.49), Some(100.49))

  val bsasDetailModelNonFhl: BsasDetail = BsasDetail(Some(incomeModel), Some(expenseBreakdownModel))

  val bsasDetailModelFhl: BsasDetail =
    BsasDetail(Some(IncomeBreakdown(Some(100.49), None, None, None)), Some(expenseBreakdownModel.copy(residentialFinancialCost = None)))

  val metaDataModel: Metadata = Metadata(
    TypeOfBusiness.`uk-property-non-fhl`,
    Some("000000000000210"),
    AccountingPeriod(LocalDate.parse("2018-10-11"), LocalDate.parse("2019-10-10")),
    "2019-20",
    "2019-10-14T11:33:27Z",
    "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    "valid",
    true
  )

  val retrieveUkPropertyNonFhlAdjustmentsResponseModel: RetrieveUkPropertyAdjustmentsResponse =
    RetrieveUkPropertyAdjustmentsResponse(metaDataModel, bsasDetailModelNonFhl)

  val retrieveUkPropertyFhlAdjustmentsResponseModel: RetrieveUkPropertyAdjustmentsResponse =
    RetrieveUkPropertyAdjustmentsResponse(metaDataModel.copy(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`), bsasDetailModelFhl)

  val hateoasResponseForUkPropertyNonFhlAdjustments: (String, String) => String = (nino, bsasId) => s"""
      |{
      |  "metadata": {
      |     "typeOfBusiness": "uk-property-non-fhl",
      |     "businessId": "000000000000210",
      |     "accountingPeriod": {
      |       "startDate": "2018-10-11",
      |       "endDate": "2019-10-10"
      |     },
      |     "taxYear": "2019-20",
      |     "requestedDateTime": "2019-10-14T11:33:27Z",
      |     "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |     "summaryStatus": "valid",
      |     "adjustedSummary": true
      |  },
      |  "adjustments": {
      |    "incomes": {
      |      "rentIncome": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "reversePremiums": 100.49,
      |      "otherPropertyIncome": 100.49
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "residentialFinancialCost": 100.49,
      |      "other": 100.49
      |    }
      |  },
      |  "links": [
      |    {
      |      "href": "/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId?adjustedStatus=true",
      |      "rel": "retrieve-adjustable-summary",
      |      "method": "GET"
      |    },
      |    {
      |      "href": "/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId/adjust",
      |      "rel": "self",
      |      "method": "GET"
      |    }
      |  ]
      |}
      |""".stripMargin

  val hateoasResponseForUkPropertyFhlAdjustments: (String, String) => String = (nino, bsasId) => s"""
      |{
      |  "metadata": {
      |     "typeOfBusiness": "uk-property-fhl",
      |     "businessId": "000000000000210",
      |     "accountingPeriod": {
      |       "startDate": "2018-10-11",
      |       "endDate": "2019-10-10"
      |     },
      |     "taxYear": "2019-20",
      |     "requestedDateTime": "2019-10-14T11:33:27Z",
      |     "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |     "summaryStatus": "valid",
      |     "adjustedSummary": true
      |  },
      |  "adjustments": {
      |    "incomes": {
      |      "rentIncome": 100.49
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "other": 100.49
      |    }
      |  },
      |  "links": [
      |    {
      |      "href": "/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId?adjustedStatus=true",
      |      "rel": "retrieve-adjustable-summary",
      |      "method": "GET"
      |    },
      |    {
      |      "href": "/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId/adjust",
      |      "rel": "self",
      |      "method": "GET"
      |    }
      |  ]
      |}
      |""".stripMargin

}
