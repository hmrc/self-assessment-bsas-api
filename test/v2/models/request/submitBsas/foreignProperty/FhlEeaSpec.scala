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

package v2.models.request.submitBsas.foreignProperty

import play.api.libs.json.Json
import support.UnitSpec

class FhlEeaSpec extends UnitSpec {

  val validReadsJson = Json.parse(
    """
      |{
      |  "income": {
      |    "rentIncome": 123.12
      |  },
      |  "expenses": {
      |    "premisesRunningCosts": 123.12,
      |    "repairsAndMaintenance": 123.12,
      |    "financialCosts": 123.12,
      |    "professionalFees": 123.12,
      |    "costOfServices": 123.12,
      |    "travelCosts": 123.12,
      |    "other": 123.12,
      |    "consolidatedExpenses": 123.12
      |  }
      |}
      |""".stripMargin)

  val validWritesJson = Json.parse(
    """
      |{
      |  "income": {
      |    "rentAmount": 123.12
      |  },
      |  "expenses": {
      |    "premisesRunningCosts": 123.12,
      |    "repairsAndMaintenance": 123.12,
      |    "financialCosts": 123.12,
      |    "professionalFees": 123.12,
      |    "costOfServices": 123.12,
      |    "travelCosts": 123.12,
      |    "other": 123.12,
      |    "consolidatedExpenses": 123.12
      |  }
      |}
      |""".stripMargin)

  val emptyJson = Json.parse("""{}""")

  val validModel = FhlEea(
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
      Some(123.12)
    ))
  )

  val emptyModel = FhlEea(None, None)

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        validModel shouldBe validReadsJson.as[FhlEea]
      }
    }
  }
  "reads from an empty JSON" when{
    "passed an empty JSON" should {
      "return an empty model" in {
        emptyModel shouldBe emptyJson.as[FhlEea]
      }
    }
  }
  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(validModel) shouldBe validWritesJson
      }
    }
  }
  "write from an empty body" when {
    "passed an empty model" should {
      "return an empty JSON" in {
        Json.toJson(emptyModel) shouldBe emptyJson
      }
    }
  }

  "isEmpty" when {
    "passed a non empty model" should {
      "return false" in {
        validModel.isEmpty shouldBe false
      }
    }
    "passed an empty model" should {
      "return true" in {
        emptyModel.isEmpty shouldBe true
      }
    }
  }
}
