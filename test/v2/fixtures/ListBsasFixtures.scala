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

package v2.fixtures

import play.api.libs.json.{JsArray, JsValue, Json}
import v2.models.domain.{Status, TypeOfBusiness}
import v2.models.request.AccountingPeriod
import v2.models.response.listBsas.{BsasEntries, BusinessSourceSummary, ListBsasResponse}


object ListBsasFixtures {

  val accountingJSON: JsValue = Json.parse(
    """
      |{
      | "startDate": "2018-10-11",
      | "endDate": "2019-10-10"
      | }
      |""".stripMargin
  )

  val accountingFromDesJSON: JsValue = Json.parse (
    """
      |{
      | "accountingStartDate": "2018-10-11",
      | "accountingEndDate": "2019-10-10"
      | }
      |""".stripMargin)


  val invalidAccountingJson: JsValue = Json.parse(
    """
      |{
      |  "startDate" : 4,
      |  "endDate" : true
      | }
      | """.stripMargin)

  val bsasEntriesJSON: JsValue = Json.parse(
    """
      |{
      | "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      | "requestedDateTime": "2019-10-14T11:33:27Z",
      | "summaryStatus": "valid",
      | "adjustedSummary": false
      | }
      |""".stripMargin
  )

  val bsasEntriesFromDesJSON: JsValue = Json.parse (
    """
      |{
      | "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      | "requestedDateTime": "2019-10-14T11:33:27Z",
      | "status": "valid",
      | "adjusted": false
      | }
      |""".stripMargin
  )

  val summaryJSON: JsValue = Json.parse(
    """
      |{
      | "typeOfBusiness": "self-employment",
      | "businessId": "000000000000210",
      | "accountingPeriod": {
      |     "startDate": "2018-10-11",
      |     "endDate": "2019-10-10"
      | },
      | "bsasEntries": [
      |     {
      |       "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |       "requestedDateTime": "2019-10-14T11:33:27Z",
      |       "summaryStatus": "valid",
      |       "adjustedSummary": false
      |     }
      |   ]
      | }
      |""".stripMargin
  )

  val summaryFromDesJSONSE: JsValue = Json.parse(
    """
      |{
      | "incomeSourceType": "01",
      | "incomeSourceId": "000000000000210",
      | "accountingStartDate": "2018-10-11",
      | "accountingEndDate": "2019-10-10",
      | "ascCalculations": [
      |     {
      |       "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |       "requestedDateTime": "2019-10-14T11:33:27Z",
      |       "status": "valid",
      |       "adjusted": false
      |     }
      |   ]
      | }
      |""".stripMargin
  )

  val summaryFromDesJSONUkFhl: JsValue = Json.parse(
    """
      |{
      | "incomeSourceType": "04",
      | "accountingStartDate": "2018-10-11",
      | "accountingEndDate": "2019-10-10",
      | "ascCalculations": [
      |     {
      |       "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce3",
      |       "requestedDateTime": "2019-10-14T11:33:27Z",
      |       "status": "valid",
      |       "adjusted": false
      |     }
      |   ]
      | }
      |""".stripMargin
  )

  val summaryFromDesJSONUkNonFhl: JsValue = Json.parse(
    """
      |{
      | "incomeSourceType": "02",
      | "accountingStartDate": "2018-10-11",
      | "accountingEndDate": "2019-10-10",
      | "ascCalculations": [
      |     {
      |       "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
      |       "requestedDateTime": "2019-10-14T11:33:27Z",
      |       "status": "valid",
      |       "adjusted": false
      |     }
      |   ]
      | }
      |""".stripMargin
  )

  val summaryFromDesJSONForeign: JsValue = Json.parse(
    """
      |{
      | "incomeSourceType": "15",
      | "accountingStartDate": "2018-10-11",
      | "accountingEndDate": "2019-10-10",
      | "ascCalculations": [
      |     {
      |       "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
      |       "requestedDateTime": "2019-10-14T11:33:27Z",
      |       "status": "valid",
      |       "adjusted": false
      |     }
      |   ]
      | }
      |""".stripMargin
  )

  val summaryFromDesJSONFhlEea: JsValue = Json.parse(
    """
      |{
      | "incomeSourceType": "03",
      | "accountingStartDate": "2018-10-11",
      | "accountingEndDate": "2019-10-10",
      | "ascCalculations": [
      |     {
      |       "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
      |       "requestedDateTime": "2019-10-14T11:33:27Z",
      |       "status": "valid",
      |       "adjusted": false
      |     }
      |   ]
      | }
      |""".stripMargin
  )

  val summariesJSON: JsValue = Json.parse(
    """
      | {
      |   "businessSourceSummaries": [
      |     {
      |       "typeOfBusiness": "self-employment",
      |       "businessId": "000000000000210",
      |       "accountingPeriod": {
      |         "startDate": "2018-10-11",
      |         "endDate": "2019-10-10"
      |     },
      |       "bsasEntries": [
      |         {
      |           "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |           "requestedDateTime": "2019-10-14T11:33:27Z",
      |           "summaryStatus": "valid",
      |           "adjustedSummary": false
      |         }
      |       ]
      |     }
      |   ]
      | }
      |""".stripMargin
  )

  val summariesJSONWithHateoas: String => JsValue = nino => Json.parse(
    s"""
      |{
      |  "businessSourceSummaries": [
      |    {
      |      "typeOfBusiness": "self-employment",
      |      "businessId": "000000000000210",
      |      "accountingPeriod": {
      |        "startDate": "2018-10-11",
      |        "endDate": "2019-10-10"
      |      },
      |      "bsasEntries": [
      |        {
      |          "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |          "requestedDateTime": "2019-10-14T11:33:27Z",
      |          "summaryStatus": "valid",
      |          "adjustedSummary": false,
      |          "links": [
      |            {
      |              "href": "/individuals/self-assessment/adjustable-summary/$nino/self-employment/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |              "method": "GET",
      |              "rel": "self"
      |            }
      |          ]
      |        }
      |      ]
      |    },
      |    {
      |      "typeOfBusiness": "uk-property-fhl",
      |      "accountingPeriod": {
      |        "startDate": "2018-10-11",
      |        "endDate": "2019-10-10"
      |      },
      |      "bsasEntries": [
      |        {
      |          "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce3",
      |          "requestedDateTime": "2019-10-14T11:33:27Z",
      |          "summaryStatus": "valid",
      |          "adjustedSummary": false,
      |          "links": [
      |            {
      |              "href": "/individuals/self-assessment/adjustable-summary/$nino/property/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce3",
      |              "method": "GET",
      |              "rel": "self"
      |            }
      |          ]
      |        }
      |      ]
      |    },
      |    {
      |      "typeOfBusiness": "uk-property-non-fhl",
      |      "accountingPeriod": {
      |        "startDate": "2018-10-11",
      |        "endDate": "2019-10-10"
      |      },
      |      "bsasEntries": [
      |        {
      |          "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
      |          "requestedDateTime": "2019-10-14T11:33:27Z",
      |          "summaryStatus": "valid",
      |          "adjustedSummary": false,
      |          "links": [
      |            {
      |              "href": "/individuals/self-assessment/adjustable-summary/$nino/property/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
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
      |      "href": "/individuals/self-assessment/adjustable-summary/$nino",
      |      "method": "GET",
      |      "rel": "self"
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  val summariesJSONForeignWithHateoas: String => JsValue = nino => Json.parse(
    s"""
       |{
       |  "businessSourceSummaries": [
       |    {
       |      "typeOfBusiness": "foreign-property",
       |      "accountingPeriod": {
       |        "startDate": "2018-10-11",
       |        "endDate": "2019-10-10"
       |      },
       |      "bsasEntries": [
       |        {
       |          "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
       |          "requestedDateTime": "2019-10-14T11:33:27Z",
       |          "summaryStatus": "valid",
       |          "adjustedSummary": false,
       |          "links": [
       |            {
       |              "href": "/individuals/self-assessment/adjustable-summary/$nino/foreign-property/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
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
       |      "href": "/individuals/self-assessment/adjustable-summary/$nino",
       |      "method": "GET",
       |      "rel": "self"
       |    }
       |  ]
       |}
       |""".stripMargin
  )

  val summariesFromDesJSONSingle = JsArray(Seq(summaryFromDesJSONSE))
  val summariesFromDesJSONMultiple = JsArray(Seq(summaryFromDesJSONSE, summaryFromDesJSONUkFhl, summaryFromDesJSONUkNonFhl))
  val summariesFromDesJSONForeign = JsArray(Seq(summaryFromDesJSONForeign))
  val summariesFromDesJSONFhlEea = JsArray(Seq(summaryFromDesJSONFhlEea))

  val summaryModel =
    ListBsasResponse(
      Seq(BusinessSourceSummary(
        typeOfBusiness = TypeOfBusiness.`self-employment`,
        businessId = Some("000000000000210"),
        AccountingPeriod(
          startDate = "2018-10-11",
          endDate = "2019-10-10"
        ),
        Seq(
          BsasEntries(
            bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
            requestedDateTime = "2019-10-14T11:33:27Z",
            summaryStatus = Status.`valid`,
            adjustedSummary = false
          )
        )
      ))
    )

}
