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

package v6.bsas.list.def1.model

import play.api.libs.json.{JsArray, JsValue, Json}
import shared.models.domain.{Status, TaxYear}
import v6.bsas.list.def1.model.response.{AccountingPeriod, BusinessSource, Def1_ListBsasResponse}
import v6.bsas.list.model.response.{BsasSummary, ListBsasResponse}
import v6.common.model.TypeOfBusinessWithFHL

trait Def1_ListBsasFixtures {

  val bsasSummaryDownstreamJson: JsValue = Json.parse(
    """
      |{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2019-10-14T11:33:27Z",
      |  "status": "valid",
      |  "adjusted": false
      |}
    """.stripMargin
  )

  val bsasSummary: BsasSummary = BsasSummary(
    calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    requestedDateTime = "2019-10-14T11:33:27Z",
    summaryStatus = Status.`valid`,
    adjustedSummary = false,
    adjustedDateTime = None
  )

  val bsasSummaryJson: JsValue = Json.parse(
    """
      |{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2019-10-14T11:33:27Z",
      |  "summaryStatus": "valid",
      |  "adjustedSummary": false
      |}
    """.stripMargin
  )

  val accountingPeriodDownstreamJson: JsValue = Json.parse(
    """
      |{
      |  "accountingStartDate": "2018-10-11",
      |  "accountingEndDate": "2019-10-10"
      |}
    """.stripMargin
  )

  val accountingPeriod: AccountingPeriod = AccountingPeriod(
    startDate = "2018-10-11",
    endDate = "2019-10-10"
  )

  val accountingPeriodJson: JsValue = Json.parse(
    """
      |{
      |  "startDate": "2018-10-11",
      |  "endDate": "2019-10-10"
      |}
    """.stripMargin
  )

  def businessSourceSummaryDownstreamJson(taxYear: String = "2020"): JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceId": "000000000000210",
      |  "incomeSourceType": "01",
      |  "accountingStartDate": "2018-10-11",
      |  "accountingEndDate": "2019-10-10",
      |  "taxYear": $taxYear,
      |  "ascCalculations": [
      |    {
      |      "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "requestedDateTime": "2019-10-14T11:33:27Z",
      |      "status": "valid",
      |      "adjusted": false
      |    }
      |  ]
      |}
    """.stripMargin
  )

  def businessSourceSummaryJson(taxYear: String = "2019-20"): JsValue = Json.parse(
    s"""
      |{
      |  "businessId": "000000000000210",
      |  "typeOfBusiness": "self-employment",
      |  "accountingPeriod": {
      |    "startDate": "2018-10-11",
      |    "endDate": "2019-10-10"
      |  },
      |  "taxYear": "$taxYear",
      |  "summaries": [
      |    {
      |      "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "requestedDateTime": "2019-10-14T11:33:27Z",
      |      "summaryStatus": "valid",
      |      "adjustedSummary": false
      |    }
      |  ]
      |}
    """.stripMargin
  )

  def listBsasResponseDownstreamJson(taxYear: String = "2020"): JsValue = Json.parse(
    s"""
      |[
      |  {
      |    "incomeSourceId": "000000000000210",
      |    "incomeSourceType": "01",
      |    "accountingStartDate": "2018-10-11",
      |    "accountingEndDate": "2019-10-10",
      |    "taxYear": $taxYear,
      |    "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |        "requestedDateTime": "2019-10-14T11:33:27Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |  }
      |]
    """.stripMargin
  )

  def listBsasResponseDownstreamJsonForeign(taxYear: String = "2020"): JsValue = Json.parse(
    s"""
      |[
      |  {
      |    "incomeSourceId": "000000000000210",
      |    "incomeSourceType": "15",
      |    "accountingStartDate": "2018-10-11",
      |    "accountingEndDate": "2019-10-10",
      |    "taxYear": $taxYear,
      |    "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |        "requestedDateTime": "2019-10-14T11:33:27Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |  }
      |]
    """.stripMargin
  )

  val listBsasResponse: ListBsasResponse                = Def1_ListBsasResponse(List(businessSourceSummary()))
  val listBsasMultipleTaxYearResponse: ListBsasResponse = Def1_ListBsasResponse(Seq(businessSourceSummary(), businessSourceSummary("2020-21")))

  def listBsasResponseJson(taxYear: String = "2019-20"): JsValue = Json.parse(
    s"""
      |{
      |  "businessSources": [
      |    {
      |      "businessId": "000000000000210",
      |      "typeOfBusiness": "self-employment",
      |      "accountingPeriod": {
      |        "startDate": "2018-10-11",
      |        "endDate": "2019-10-10"
      |      },
      |      "taxYear": "$taxYear",
      |      "summaries": [
      |        {
      |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |          "requestedDateTime": "2019-10-14T11:33:27Z",
      |          "summaryStatus": "valid",
      |          "adjustedSummary": false
      |        }
      |      ]
      |    }
      |  ]
      |}
    """.stripMargin
  )

  def listBsasResponseDownstreamJsonSE(taxYear: String = "2020"): JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceType": "01",
      |  "incomeSourceId": "000000000000210",
      |  "taxYear": $taxYear,
      |  "accountingStartDate": "2018-10-11",
      |  "accountingEndDate": "2019-10-10",
      |  "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |        "requestedDateTime": "2019-10-14T11:33:27Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |}
    """.stripMargin
  )

  def listBsasResponseDownstreamJsonUkFhl(taxYear: String = "2020"): JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceType": "04",
      |  "incomeSourceId": "000000000000210",
      |  "accountingStartDate": "2018-10-11",
      |  "accountingEndDate": "2019-10-10",
      |  "taxYear": $taxYear,
      |  "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5",
      |        "requestedDateTime": "2019-10-14T11:33:27Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |}
    """.stripMargin
  )

  def listBsasResponseDownstreamJsonUkNonFhl(taxYear: String = "2020"): JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceType": "02",
      |  "incomeSourceId": "000000000000210",
      |  "accountingStartDate": "2018-10-11",
      |  "accountingEndDate": "2019-10-10",
      |  "taxYear": $taxYear,
      |  "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6",
      |        "requestedDateTime": "2019-10-14T11:33:27Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |}
    """.stripMargin
  )

  def listBsasDownstreamJsonMultiple(taxYear: String = "2020"): JsArray = JsArray(
    List(
      listBsasResponseDownstreamJsonSE(taxYear),
      listBsasResponseDownstreamJsonUkFhl(taxYear),
      listBsasResponseDownstreamJsonUkNonFhl(taxYear)
    ))

  def businessSourceSummary(taxYear: String = "2019-20"): BusinessSource = BusinessSource(
    businessId = "000000000000210",
    typeOfBusiness = TypeOfBusinessWithFHL.`self-employment`,
    accountingPeriod = accountingPeriod,
    taxYear = TaxYear.fromMtd(taxYear),
    summaries = List(bsasSummary)
  )

  def summariesJs(taxYear: String = "2019-20"): JsValue =
    Json.parse(
      s"""
         |{
         |  "businessSources": [
         |    {
         |      "businessId": "000000000000210",
         |      "typeOfBusiness": "self-employment",
         |      "accountingPeriod": {
         |        "startDate": "2018-10-11",
         |        "endDate": "2019-10-10"
         |      },
         |      "taxYear": "$taxYear",
         |      "summaries": [
         |        {
         |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
         |          "requestedDateTime": "2019-10-14T11:33:27Z",
         |          "summaryStatus": "valid",
         |          "adjustedSummary": false
         |        }
         |      ]
         |    },
         |    {
         |      "businessId": "000000000000210",
         |      "typeOfBusiness": "uk-property-fhl",
         |      "accountingPeriod": {
         |        "startDate": "2018-10-11",
         |        "endDate": "2019-10-10"
         |      },
         |      "taxYear": "$taxYear",
         |      "summaries": [
         |        {
         |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5",
         |          "requestedDateTime": "2019-10-14T11:33:27Z",
         |          "summaryStatus": "valid",
         |          "adjustedSummary": false
         |        }
         |      ]
         |    },
         |    {
         |      "businessId": "000000000000210",
         |      "typeOfBusiness": "uk-property",
         |      "accountingPeriod": {
         |        "startDate": "2018-10-11",
         |        "endDate": "2019-10-10"
         |      },
         |      "taxYear": "$taxYear",
         |      "summaries": [
         |        {
         |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6",
         |          "requestedDateTime": "2019-10-14T11:33:27Z",
         |          "summaryStatus": "valid",
         |          "adjustedSummary": false
         |        }
         |      ]
         |    }
         |  ]
         |}
    """.stripMargin
    )

  def summariesForeignJs(taxYear: String = "2019-20"): JsValue =
    Json.parse(
      s"""
         |{
         |  "businessSources": [
         |    {
         |      "typeOfBusiness": "foreign-property",
         |      "businessId": "000000000000210",
         |      "taxYear": "$taxYear",
         |      "accountingPeriod": {
         |        "startDate": "2018-10-11",
         |        "endDate": "2019-10-10"
         |      },
         |      "summaries": [
         |        {
         |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
         |          "requestedDateTime": "2019-10-14T11:33:27Z",
         |          "summaryStatus": "valid",
         |          "adjustedSummary": false
         |        }
         |      ]
         |    }
         |  ]
         |}
    """.stripMargin
    )

}
