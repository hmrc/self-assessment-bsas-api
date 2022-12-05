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

package v3.fixtures

import play.api.libs.json.{ JsArray, JsValue, Json }
import v3.models.domain.{ Status, TaxYear, TypeOfBusiness }
import v3.models.response.listBsas.{ AccountingPeriod, BsasSummary, BusinessSourceSummary, ListBsasResponse }

trait ListBsasFixture {

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

  val bsasSummaryModel: BsasSummary = BsasSummary(
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

  val accountingPeriodModel: AccountingPeriod = AccountingPeriod(
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

  def businessSourceSummaryModel(taxYear: String = "2019-20"): BusinessSourceSummary[BsasSummary] = BusinessSourceSummary(
    businessId = "000000000000210",
    typeOfBusiness = TypeOfBusiness.`self-employment`,
    accountingPeriod = accountingPeriodModel,
    taxYear = TaxYear.fromMtd(taxYear),
    summaries = Seq(bsasSummaryModel)
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

  val listBsasResponseModel: ListBsasResponse[BsasSummary] = ListBsasResponse(Seq(businessSourceSummaryModel()))

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

  val listBsasResponseDownstreamJsonUkFhl: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "04",
      |  "incomeSourceId": "000000000000210",
      |  "accountingStartDate": "2018-10-11",
      |  "accountingEndDate": "2019-10-10",
      |  "taxYear": 2020,
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

  val listBsasResponseDownstreamJsonUkNonFhl: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "02",
      |  "incomeSourceId": "000000000000210",
      |  "accountingStartDate": "2018-10-11",
      |  "accountingEndDate": "2019-10-10",
      |  "taxYear": 2020,
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

  def summariesJSONWithHateoas(nino: String, taxYear: Option[String] = None): JsValue = {
    val taxYearParam = taxYear.fold("")("?taxYear=" + _)

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
      |          "adjustedSummary": false,
      |          "links": [
      |            {
      |              "href": "/individuals/self-assessment/adjustable-summary/$nino/self-employment/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4$taxYearParam",
      |              "method": "GET",
      |              "rel": "self"
      |            }
      |          ]
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
      |      "taxYear": "2019-20",
      |      "summaries": [
      |        {
      |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5",
      |          "requestedDateTime": "2019-10-14T11:33:27Z",
      |          "summaryStatus": "valid",
      |          "adjustedSummary": false,
      |          "links": [
      |            {
      |              "href": "/individuals/self-assessment/adjustable-summary/$nino/uk-property/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5$taxYearParam",
      |              "method": "GET",
      |              "rel": "self"
      |            }
      |          ]
      |        }
      |      ]
      |    },
      |    {
      |      "businessId": "000000000000210",
      |      "typeOfBusiness": "uk-property-non-fhl",
      |      "accountingPeriod": {
      |        "startDate": "2018-10-11",
      |        "endDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
      |      "summaries": [
      |        {
      |          "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6",
      |          "requestedDateTime": "2019-10-14T11:33:27Z",
      |          "summaryStatus": "valid",
      |          "adjustedSummary": false,
      |          "links": [
      |            {
      |              "href": "/individuals/self-assessment/adjustable-summary/$nino/uk-property/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6$taxYearParam",
      |              "method": "GET",
      |              "rel": "self"
      |            }
      |          ]
      |        }
      |      ]
      |    }
      |  ],
      |  "links": [
      |    {
      |      "href": "/individuals/self-assessment/adjustable-summary/$nino/trigger",
      |      "method": "POST",
      |      "rel": "trigger-business-source-adjustable-summary"
      |    },
      |    {
      |      "href": "/individuals/self-assessment/adjustable-summary/$nino$taxYearParam",
      |      "method": "GET",
      |      "rel": "self"
      |    }
      |  ]
      |}
    """.stripMargin
    )
  }

  def summariesJSONForeignWithHateoas(nino: String, taxYear: Option[String] = None): JsValue = {
    val taxYearParam = taxYear.fold("")("?taxYear=" + _)

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
       |          "adjustedSummary": false,
       |          "links": [
       |            {
       |              "href": "/individuals/self-assessment/adjustable-summary/$nino/foreign-property/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4$taxYearParam",
       |              "method": "GET",
       |              "rel": "self"
       |            }
       |          ]
       |        }
       |      ]
       |    }
       |  ],
       |  "links": [
       |    {
       |      "href": "/individuals/self-assessment/adjustable-summary/$nino/trigger",
       |      "method": "POST",
       |      "rel": "trigger-business-source-adjustable-summary"
       |    },
       |    {
       |      "href": "/individuals/self-assessment/adjustable-summary/$nino$taxYearParam",
       |      "method": "GET",
       |      "rel": "self"
       |    }
       |  ]
       |}
    """.stripMargin
    )
  }

  val listBsasDownstreamJsonMultiple: JsArray = JsArray(
    Seq(
      listBsasResponseDownstreamJsonSE,
      listBsasResponseDownstreamJsonUkFhl,
      listBsasResponseDownstreamJsonUkNonFhl
    ))
}
