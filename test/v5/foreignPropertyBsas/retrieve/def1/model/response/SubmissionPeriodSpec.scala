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

package v5.foreignPropertyBsas.retrieve.def1.model.response

import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v5.foreignPropertyBsas.retrieve.def1.model.response.RetrieveForeignPropertyBsasBodyFixtures.*

class SubmissionPeriodSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return the expected SubmissionPeriod" when {
      "given a valid json object with all fields" in {
        submissionPeriodDesJson.as[SubmissionPeriods] shouldBe parsedSubmissionPeriod
      }
    }
  }

  "writes" should {
    "return the expected json object" when {
      "given a valid SubmissionPeriod" in {
        parsedSubmissionPeriod.toJson shouldBe submissionPeriodMtdJson
      }
    }
  }

}
