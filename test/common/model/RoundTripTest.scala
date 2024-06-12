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

package common.model

import play.api.libs.json.{JsValue, Json, Reads, Writes}
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec

trait RoundTripTest extends UnitSpec with JsonErrorValidators {

  def testRoundTrip[A](
      testName: String,
      downstreamJson: JsValue,
      dataObject: A,
      mtdJson: JsValue
  )(reads: Reads[A])(implicit writes: Writes[A]): Unit = {

    s"$testName JSON reads/writes tests" when {
      "reads" should {
        "return the parsed item" when {
          "given valid JSON" in {
            downstreamJson.as[A](reads) shouldBe dataObject
          }
        }
      }

      "writes" should {
        "return valid JSON" when {
          "given a valid data object" in {
            Json.toJson(dataObject) shouldBe mtdJson
          }
        }
      }
    }
  }

}
