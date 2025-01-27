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

package v7.bsas.list.def2.model

import play.api.libs.json.{JsArray, JsValue, Json}
import shared.models.domain.{Status, TaxYear}
import v7.bsas.list.def2.model.response.{AccountingPeriod, BsasSummary, BusinessSource, Def2_ListBsasResponse}
import v7.bsas.list.model.response.ListBsasResponse
import v7.common.model.TypeOfBusiness

trait Def2_ListBsasFixtures {

  val bsasSummaryDownstreamJson: JsValue = Json.parse(
    """
      |{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2025-01-02T12:00:00Z",
      |  "status": "valid",
      |  "adjusted": false
      |}
    """.stripMargin
  )

  val bsasSummary: BsasSummary = BsasSummary(
    calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    requestedDateTime = "2025-01-02T12:00:00Z",
    summaryStatus = Status.`valid`,
    adjustedSummary = false,
    adjustedDateTime = None
  )

  val bsasSummaryJson: JsValue = Json.parse(
    """
      |{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2025-01-02T12:00:00Z",
      |  "summaryStatus": "valid",
      |  "adjustedSummary": false
      |}
    """.stripMargin
  )

  val accountingPeriodDownstreamJson: JsValue = Json.parse(
    """
      |{
      |  "accountingStartDate": "2025-01-01",
      |  "accountingEndDate": "2026-01-01"
      |}
    """.stripMargin
  )

  val accountingPeriod: AccountingPeriod = AccountingPeriod(
    startDate = "2025-01-01",
    endDate = "2026-01-01"
  )

  val accountingPeriodJson: JsValue = Json.parse(
    """
      |{
      |  "startDate": "2025-01-01",
      |  "endDate": "2026-01-01"
      |}
    """.stripMargin
  )

  val businessSourceSummaryDownstreamJson: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceId": "000000000000210",
      |  "incomeSourceType": "01",
      |  "accountingStartDate": "2025-01-01",
      |  "accountingEndDate": "2026-01-01",
      |  "taxYear": 2026,
      |  "ascCalculations": [
      |    {
      |      "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "requestedDateTime": "2025-01-02T12:00:00Z",
      |      "status": "valid",
      |      "adjusted": false
      |    }
      |  ]
      |}
    """.stripMargin
  )

  val listBsasResponseDownstreamJsonUkNonFhl: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "02",
      |  "incomeSourceId": "000000000000210",
      |  "accountingStartDate": "2025-01-01",
      |  "accountingEndDate": "2026-01-01",
      |  "taxYear": 2026,
      |  "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5",
      |        "requestedDateTime": "2025-01-02T12:00:00Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |}
    """.stripMargin
  )

  val listBsasResponseDownstreamJsonForeignNonFhl: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "15",
      |  "incomeSourceId": "000000000000210",
      |  "accountingStartDate": "2025-01-01",
      |  "accountingEndDate": "2026-01-01",
      |  "taxYear": 2026,
      |  "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6",
      |        "requestedDateTime": "2025-01-02T12:00:00Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |}
    """.stripMargin
  )

  val businessSourceSummaryJson: JsValue = Json.parse(
    """
      |{
      |  "businessId": "000000000000210",
      |  "typeOfBusiness": "self-employment",
      |  "accountingPeriod": {
      |    "startDate": "2025-01-01",
      |    "endDate": "2026-01-01"
      |  },
      |  "taxYear": "2025-26",
      |  "summaries": [
      |    {
      |      "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |      "requestedDateTime": "2025-01-02T12:00:00Z",
      |      "summaryStatus": "valid",
      |      "adjustedSummary": false
      |    }
      |  ]
      |}
    """.stripMargin
  )

  val listBsasResponseDownstreamJson: JsValue = Json.parse(
    """
      |[
      |  {
      |    "incomeSourceId": "000000000000210",
      |    "incomeSourceType": "01",
      |    "accountingStartDate": "2025-01-01",
      |    "accountingEndDate": "2026-01-01",
      |    "taxYear": 2026,
      |    "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |        "requestedDateTime": "2025-01-02T12:00:00Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |  }
      |]
    """.stripMargin
  )

  val listBsasResponseDownstreamJsonForeign: JsValue = Json.parse(
    """
      |[
      |  {
      |    "incomeSourceId": "000000000000210",
      |    "incomeSourceType": "15",
      |    "accountingStartDate": "2025-01-01",
      |    "accountingEndDate": "2026-01-01",
      |    "taxYear": 2026,
      |    "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |        "requestedDateTime": "2025-01-02T12:00:00Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |  }
      |]
    """.stripMargin
  )

  val listBsasResponse: ListBsasResponse = Def2_ListBsasResponse(List(businessSourceSummary()))

  val listBsasResponseJson: JsValue = Json.parse(
    """
      |{
      |  "businessSources": [
      |    {
      |      "businessId": "000000000000210",
      |      "typeOfBusiness": "self-employment",
      |      "accountingPeriod": {
      |        "startDate": "2025-01-01",
      |        "endDate": "2026-01-01"
      |      },
      |      "taxYear": "2025-26",
      |      "summaries": [
      |        {
      |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |          "requestedDateTime": "2025-01-02T12:00:00Z",
      |          "summaryStatus": "valid",
      |          "adjustedSummary": false
      |        }
      |      ]
      |    }
      |  ]
      |}
    """.stripMargin
  )

  val listBsasResponseDownstreamJsonSE: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "01",
      |  "incomeSourceId": "000000000000210",
      |  "taxYear": 2026,
      |  "accountingStartDate": "2025-01-01",
      |  "accountingEndDate": "2026-01-01",
      |  "ascCalculations": [
      |      {
      |        "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |        "requestedDateTime": "2025-01-02T12:00:00Z",
      |        "status": "valid",
      |        "adjusted": false
      |      }
      |    ]
      |}
    """.stripMargin
  )

  val listBsasDownstreamJsonMultiple: JsArray = JsArray(
    List(
      listBsasResponseDownstreamJsonSE,
      listBsasResponseDownstreamJsonUkNonFhl,
      listBsasResponseDownstreamJsonForeignNonFhl
    ))

  def businessSourceSummary(taxYear: String = "2025-26"): BusinessSource = BusinessSource(
    businessId = "000000000000210",
    typeOfBusiness = TypeOfBusiness.`self-employment`,
    accountingPeriod = accountingPeriod,
    taxYear = TaxYear.fromMtd(taxYear),
    summaries = List(bsasSummary)
  )

  val summariesJs: JsValue =
    Json.parse(
      s"""
         |{
         |  "businessSources": [
         |    {
         |      "businessId": "000000000000210",
         |      "typeOfBusiness": "self-employment",
         |      "accountingPeriod": {
         |        "startDate": "2025-01-01",
         |        "endDate": "2026-01-01"
         |      },
         |      "taxYear": "2025-26",
         |      "summaries": [
         |        {
         |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
         |          "requestedDateTime": "2025-01-02T12:00:00Z",
         |          "summaryStatus": "valid",
         |          "adjustedSummary": false
         |        }
         |      ]
         |    },
         |    {
         |  "businessId": "000000000000210",
         |  "typeOfBusiness": "uk-property",
         |  "accountingPeriod": {
         |    "startDate": "2025-01-01",
         |    "endDate": "2026-01-01"
         |  },
         |  "taxYear": "2025-26",
         |  "summaries": [
         |    {
         |      "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5",
         |      "requestedDateTime": "2025-01-02T12:00:00Z",
         |      "summaryStatus": "valid",
         |      "adjustedSummary": false
         |    }
         |  ]
         |},
         |{
         |  "businessId": "000000000000210",
         |  "typeOfBusiness": "foreign-property",
         |  "accountingPeriod": {
         |    "startDate": "2025-01-01",
         |    "endDate": "2026-01-01"
         |  },
         |  "taxYear": "2025-26",
         |  "summaries": [
         |    {
         |      "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6",
         |      "requestedDateTime": "2025-01-02T12:00:00Z",
         |      "summaryStatus": "valid",
         |      "adjustedSummary": false
         |    }
         |  ]
         |}
         |  ]
         |}
    """.stripMargin
    )

  val summariesForeignJs: JsValue =
    Json.parse(
      s"""
         |{
         |  "businessSources": [
         |    {
         |      "typeOfBusiness": "foreign-property",
         |      "businessId": "000000000000210",
         |      "taxYear": "2025-26",
         |      "accountingPeriod": {
         |        "startDate": "2025-01-01",
         |        "endDate": "2026-01-01"
         |      },
         |      "summaries": [
         |        {
         |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
         |          "requestedDateTime": "2025-01-02T12:00:00Z",
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
