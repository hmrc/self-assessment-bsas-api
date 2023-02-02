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

package v3.models.response.retrieveBsas.foreignProperty

import support.UnitSpec
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import v3.models.utils.JsonErrorValidators

class SubmissionPeriodSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid submission period model" when {
      "a valid json with all fields are supplied" in {
        submissionPeriodDesJson.as[SubmissionPeriods] shouldBe submissionPeriodModel
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        submissionPeriodModel.toJson shouldBe submissionPeriodMtdJson
      }
    }
  }
}
