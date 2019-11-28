/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.libs.json.{JsArray, JsValue, Json}
import v1.models.domain.{Status, TypeOfBusiness}
import v1.models.request.AccountingPeriod
import v1.models.response.ListBsasResponse
import v1.models.response.listBsas.{BsasEntries, BusinessSourceSummary}


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
      | "selfEmploymentId": "000000000000210",
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

  val summaryFromDesJSON: JsValue = Json.parse(
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

  val summariesJSON: JsValue = Json.parse(
    """
      | {
      |   "businessSourceSummaries": [
      |     {
      |       "typeOfBusiness": "self-employment",
      |       "selfEmploymentId": "000000000000210",
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

  val summariesFromDesJSON = JsArray(Seq(summaryFromDesJSON))

  val summaryModel =
    ListBsasResponse(
      Seq(BusinessSourceSummary(
        typeOfBusiness = TypeOfBusiness.`self-employment`,
        selfEmploymentId = Some("000000000000210"),
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
