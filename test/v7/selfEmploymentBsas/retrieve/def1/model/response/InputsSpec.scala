/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.selfEmploymentBsas.retrieve.def1.model.response

import play.api.libs.json.{JsResultException, Json}
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v7.selfEmploymentBsas.retrieve.def1.model.Def1_RetrieveSelfEmploymentBsasFixtures._

class InputsSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid model" when {
      "passed valid JSON" in {
        downstreamInputsJson.as[Inputs] shouldBe parsedInputs
      }
    }
    "return an error" when {
      "passed JSON with an invalid source" in {
        a[JsResultException] should be thrownBy downstreamInputsInvalidSourceJson.as[Inputs]

        val thrown: JsResultException = the[JsResultException] thrownBy downstreamInputsInvalidSourceJson.as[Inputs]
        thrown.errors
          .map { case (path, errors) =>
            (path, errors.map(_.messages))
          }
          .map { case (path, errors) =>
            path.toString() shouldBe "/source"
            errors.flatten.contains("error.expected.Source") shouldBe true
          }
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid model" in {
        Json.toJson(parsedInputs) shouldBe mtdInputsJson
      }
    }
  }

}
