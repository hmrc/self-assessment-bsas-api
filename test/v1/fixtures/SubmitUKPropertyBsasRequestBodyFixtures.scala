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

import v1.models.request.submitBsas._
import play.api.libs.json.{JsValue, Json}

object SubmitUKPropertyBsasRequestBodyFixtures {

  val nonFHLRequestBodyMtd: JsValue = Json.parse(
    """
      |{
      | "income": {
      |   "rentIncome": 1000.45,
      |   "premiumsOfLeaseGrant": 1000.45,
      |   "reversePremiums": 1000.45,
      |   "otherPropertyIncome": 1000.45
      | },
      | "expenses": {
      |   "premisesRunningCosts": 1000.45,
      |   "repairsAndMaintenance": 1000.45,
      |   "financialCosts": 1000.45,
      |   "professionalFees": 1000.45,
      |   "travelCosts": 1000.45,
      |   "costOfServices": 1000.45,
      |   "residentialFinancialCost": 1000.45,
      |   "other": 1000.45,
      |   "consolidatedExpenses": 1000.45
      | }
      |}
      |""".stripMargin)

  val nonFHLBody: SubmitUKPropertyBsasRequestBody =
    SubmitUKPropertyBsasRequestBody(
      NonFurnishedHolidayLet(
        None,
        //Some(NonFHLIncome(Some("1000.45"), Some("1000.45"), Some("1000.45"), Some("1000.45"))),
        Some(NonFHLExpenses(Some("1000.45"), Some("1000.45"), Some("1000.45"), Some("1000.45"), Some("1000.45"), Some("1000.45"), Some("1000.45")))
      )
    )
}
