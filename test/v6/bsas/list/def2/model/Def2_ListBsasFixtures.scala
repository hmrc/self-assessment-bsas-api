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

package v6.bsas.list.def2.model

import play.api.libs.json.{JsArray, JsValue, Json}
import shared.models.domain.{Status, TaxYear}
import v6.bsas.list.def2.model.response.{AccountingPeriod, BusinessSource, Def2_BsasSummary, Def2_ListBsasResponse}
import v6.bsas.list.model.response.ListBsasResponse
import v6.common.model.TypeOfBusiness

trait Def2_ListBsasFixtures {

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

  val bsasSummary: Def2_BsasSummary = Def2_BsasSummary(
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

  val businessSourceSummaryDownstreamJson: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceId": "000000000000210",
      |  "incomeSourceType": "01",
      |  "accountingStartDate": "2018-10-11",
      |  "accountingEndDate": "2019-10-10",
      |  "taxYear": 2020,
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

  val businessSourceSummaryJson: JsValue = Json.parse(
    """
      |{
      |  "businessId": "000000000000210",
      |  "typeOfBusiness": "self-employment",
      |  "accountingPeriod": {
      |    "startDate": "2018-10-11",
      |    "endDate": "2019-10-10"
      |  },
      |  "taxYear": "2019-20",
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

  val listBsasResponseDownstreamJson: JsValue = Json.parse(
    """
      |[
      |  {
      |    "incomeSourceId": "000000000000210",
      |    "incomeSourceType": "01",
      |    "accountingStartDate": "2018-10-11",
      |    "accountingEndDate": "2019-10-10",
      |    "taxYear": 2020,
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

  val listBsasResponseDownstreamJsonForeign: JsValue = Json.parse(
    """
      |[
      |  {
      |    "incomeSourceId": "000000000000210",
      |    "incomeSourceType": "15",
      |    "accountingStartDate": "2018-10-11",
      |    "accountingEndDate": "2019-10-10",
      |    "taxYear": 2020,
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

  val listBsasResponse: ListBsasResponse = Def2_ListBsasResponse(List(businessSourceSummary()))

  val listBsasResponseJson: JsValue = Json.parse(
    """
      |{
      |  "businessSources": [
      |    {
      |      "businessId": "000000000000210",
      |      "typeOfBusiness": "self-employment",
      |      "accountingPeriod": {
      |        "startDate": "2018-10-11",
      |        "endDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
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

  val listBsasResponseDownstreamJsonSE: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "01",
      |  "incomeSourceId": "000000000000210",
      |  "taxYear": 2020,
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

  val listBsasDownstreamJsonMultiple: JsArray = JsArray(
    List(
      listBsasResponseDownstreamJsonSE
    ))

  def businessSourceSummary(taxYear: String = "2019-20"): BusinessSource = BusinessSource(
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
         |        "startDate": "2018-10-11",
         |        "endDate": "2019-10-10"
         |      },
         |      "taxYear": "2019-20",
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

  val summariesForeignJs: JsValue =
    Json.parse(
      s"""
         |{
         |  "businessSources": [
         |    {
         |      "typeOfBusiness": "foreign-property",
         |      "businessId": "000000000000210",
         |      "taxYear": "2019-20",
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
