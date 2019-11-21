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

import play.api.libs.json.{JsValue, Json}
import v1.models.request.triggerBsas.{AccountingPeriod, TriggerBsasRequestBody}

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

  val invalidJson: JsValue = Json.parse("""
      |{
      |  "startDate" : 4,
      |  "endDate" : true
      |}
  """.stripMargin)

  val seBody: TriggerBsasRequestBody = TriggerBsasRequestBody(
    AccountingPeriod("2018-11-25", "2018-11-26"),
    "self-employment",
    Some("anId")
  )

  val fhlBody: TriggerBsasRequestBody = TriggerBsasRequestBody(
    AccountingPeriod("2018-11-25", "2018-11-26"),
    "uk-property-fhl",
    None
  )

}
