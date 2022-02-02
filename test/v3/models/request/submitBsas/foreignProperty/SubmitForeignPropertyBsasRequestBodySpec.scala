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

package v3.models.request.submitBsas.foreignProperty

import play.api.libs.json.{JsSuccess, JsValue, Json}
import support.UnitSpec

class SubmitForeignPropertyBsasRequestBodySpec extends UnitSpec {

  val foreignPropertyJson: JsValue = Json.parse(
    """
      |{
      |  "foreignProperty": [
      |    {
      |      "countryCode": "FRA",
      |      "income": {
      |        "rentIncome": 123.12,
      |        "premiumsOfLeaseGrant": 123.12,
      |        "otherPropertyIncome": 123.12
      |      },
      |      "expenses": {
      |        "premisesRunningCosts": 123.12,
      |        "repairsAndMaintenance": 123.12,
      |        "financialCosts": 123.12,
      |        "professionalFees": 123.12,
      |        "travelCosts": 123.12,
      |        "costOfServices": 123.12,
      |        "residentialFinancialCost": 123.12,
      |        "other": 123.12
      |      }
      |    }
      |  ]
      |}
      |""".stripMargin)

  val foreignPropertyJsonWithType: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "15",
      |  "adjustments": [
      |    {
      |      "countryCode": "FRA",
      |      "income": {
      |        "rent": 123.12,
      |        "premiumsOfLeaseGrant": 123.12,
      |        "otherPropertyIncome": 123.12
      |      },
      |      "expenses": {
      |        "premisesRunningCosts": 123.12,
      |        "repairsAndMaintenance": 123.12,
      |        "financialCosts": 123.12,
      |        "professionalFees": 123.12,
      |        "travelCosts": 123.12,
      |        "costOfServices": 123.12,
      |        "residentialFinancialCost": 123.12,
      |        "other": 123.12
      |      }
      |    }
      |  ]
      |}
      |""".stripMargin)

  val foreignFhlEeaJson: JsValue = Json.parse(
    """
      |{
      |  "foreignFhlEea": {
      |    "income": {
      |      "rentIncome": 123.12
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 123.12,
      |      "repairsAndMaintenance": 123.12,
      |      "financialCosts": 123.12,
      |      "professionalFees": 123.12,
      |      "costOfServices": 123.12,
      |      "travelCosts": 123.12,
      |      "other": 123.12
      |    }
      |  }
      |}
      |""".stripMargin
  )

  val foreignFhlEeaJsonWithType: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "03",
      |  "adjustments": {
      |    "income": {
      |      "totalRentsReceived": 123.12
      |    },
      |    "expenses": {
      |      "premisesRunningCosts": 123.12,
      |      "repairsAndMaintenance": 123.12,
      |      "financialCosts": 123.12,
      |      "professionalFees": 123.12,
      |      "costOfServices": 123.12,
      |      "travelCosts": 123.12,
      |      "other": 123.12
      |    }
      |  }
      |}
      |""".stripMargin
  )

  val emptyJson: JsValue = Json.parse("""{}""")

  val emptyModel = SubmitForeignPropertyBsasRequestBody(None, None)

  val foreignPropertyModel = SubmitForeignPropertyBsasRequestBody(
    Some(Seq(ForeignProperty(
      "FRA",
      Some(ForeignPropertyIncome(
        Some(123.12),
        Some(123.12),
        Some(123.12)
      )),
      Some(ForeignPropertyExpenses(
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        consolidatedExpenses = None
      ))
    ))),
    None
  )

  val foreignFhlEeaModel = SubmitForeignPropertyBsasRequestBody(
    None,
    Some(FhlEea(
      Some(FhlIncome(
        Some(123.12)
      )),
      Some(FhlEeaExpenses(
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        consolidatedExpenses = None
      ))
    ))
  )

  "SubmitForeignPropertyBsasRequestBody" when {
    "read from valid JSON" should {
      "return the expected SubmitForeignPropertyBsasRequestBody with a foreignProperty" in {
        foreignPropertyJson.validate[SubmitForeignPropertyBsasRequestBody] shouldBe JsSuccess(foreignPropertyModel)
      }

      "return the expected SubmitForeignPropertyBsasRequestBody with a foreignFhlEea" in {
        foreignFhlEeaJson.validate[SubmitForeignPropertyBsasRequestBody] shouldBe JsSuccess(foreignFhlEeaModel)
      }

      "return the expected empty body" in {
        emptyJson.as[SubmitForeignPropertyBsasRequestBody] shouldBe emptyModel
      }
    }

    "written to JSON" should {
      "return the expected SubmitForeignPropertyBsasRequestBody with a foreignProperty" in {
        Json.toJson(foreignPropertyModel) shouldBe foreignPropertyJsonWithType
      }

      "return the expected SubmitForeignPropertyBsasRequestBody with a foreignFhlEea" in {
        Json.toJson(foreignFhlEeaModel) shouldBe foreignFhlEeaJsonWithType
      }

      "return an empty body when written from an empty model" in {
        Json.toJson(emptyModel) shouldBe emptyJson
      }
    }
  }
}
