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

package v1.models.response.retrieveBsas

import java.time.LocalDate

import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec
import v1.fixtures.RetrieveUkPropertyBsasFixtures._

class AccountingPeriodSpec extends UnitSpec {

  val desJson: JsValue = Json.parse("""
      |{
      |  "inputs": {
      |  "incomeSourceId": "111111111111111",
      |  "incomeSourceType": "04",
      |  "accountingPeriodStartDate": "2019-04-06",
      |  "accountingPeriodEndDate": "2020-04-05",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [
      |   {
      |    "periodId": "2222222222222222",
      |    "startDate": "2019-04-06",
      |    "endDate": "2020-04-05",
      |    "receivedDateTime": "a"
      |   }
      |  ]
      | }
      |}
  """.stripMargin)

  val invalidJson: JsValue = Json.parse("""
      |{
      |  "startDate" : 4,
      |  "endDate" : true
      |}
  """.stripMargin)

  val mtdJson = Json.parse(
    """{
      |    "startDate": "2019-04-06",
      |    "endDate": "2020-04-05"
      |}""".stripMargin)

  "AccountingPeriod" when {
    "read from valid JSON" should {
      "return the expected AccountingPeriod object" in {
        desJson.as[AccountingPeriod] shouldBe accountingPeriodModel
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidJson.validate[AccountingPeriod] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(accountingPeriodModel) shouldBe mtdJson
      }
    }
  }
}
