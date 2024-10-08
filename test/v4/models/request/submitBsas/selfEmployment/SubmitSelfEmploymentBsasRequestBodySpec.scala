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

package v4.models.request.submitBsas.selfEmployment

import play.api.libs.json.Json
import shared.models.domain.EmptyJsonBody
import shared.utils.UnitSpec
import v4.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._
import v4.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestBody

class SubmitSelfEmploymentBsasRequestBodySpec extends UnitSpec {

  "SubmitSelfEmploymentBsasRequestBody" when {
    "read from valid JSON" should {
      "produce the expected SubmitSelfEmploymentBsasRequestBody object" in {
        submitSelfEmploymentBsasRequestBodyDesJson(submitSelfEmploymentBsasRequestBodyModel).as[SubmitSelfEmploymentBsasRequestBody] shouldBe
          submitSelfEmploymentBsasRequestBodyModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(submitSelfEmploymentBsasRequestBodyModel) shouldBe requestToIfs
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        submitSelfEmploymentBsasRequestBodyDesJson(submitSelfEmploymentBsasRequestBodyModelWithoutIncome)
          .as[SubmitSelfEmploymentBsasRequestBody] shouldBe
          submitSelfEmploymentBsasRequestBodyModelWithoutIncome
      }

      "not write those fields to JSON" in {
        Json.toJson(submitSelfEmploymentBsasRequestBodyModel) shouldBe requestToIfs
      }
    }

    "no fields as supplied" should {
      "read to an empty SubmitSelfEmploymentBsasRequestBody object" in {
        submitSelfEmploymentBsasRequestBodyDesJson(emptySubmitSelfEmploymentBsasRequestBodyModel).as[SubmitSelfEmploymentBsasRequestBody] shouldBe
          emptySubmitSelfEmploymentBsasRequestBodyModel
      }

      "write to empty JSON" in {
        Json.toJson(emptySubmitSelfEmploymentBsasRequestBodyModel) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }

}
