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

package v4.models.response.retrieveBsas.foreignProperty

import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v4.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._

class SubmissionPeriodSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid submission period model" when {
      "a valid json with all fields are supplied" in {
        submissionPeriodDesJson.as[SubmissionPeriods] shouldBe parsedSubmissionPeriod
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        parsedSubmissionPeriod.toJson shouldBe submissionPeriodMtdJson
      }
    }
  }

}
