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

import play.api.libs.json.Json
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v7.selfEmploymentBsas.retrieve.def1.model.Def1_RetrieveSelfEmploymentBsasFixtures.*

class SubmissionPeriodSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid model" when {
      "passed valid JSON with periodId regex" in {
        downstreamSubmissionPeriodWithPeriodIdRegexJson.as[SubmissionPeriod] shouldBe submissionPeriodWithPeriodId
      }
      "passed valid JSON with invalid periodId regex" in {
        downstreamSubmissionPeriodWithInvalidPeriodIdRegexJson.as[SubmissionPeriod] shouldBe submissionPeriodWithSubmissionId
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid model with periodId" in {
        Json.toJson(submissionPeriodWithPeriodId) shouldBe mtdSubmissionPeriodWithPeriodIdJson
      }
      "passed a valid model with submissionId" in {
        Json.toJson(submissionPeriodWithSubmissionId) shouldBe mtdSubmissionPeriodWithSubmissionIdJson
      }
    }
  }

}
