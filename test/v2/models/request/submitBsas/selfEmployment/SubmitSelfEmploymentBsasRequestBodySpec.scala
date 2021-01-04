/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.models.request.submitBsas.selfEmployment

import play.api.libs.json.Json
import support.UnitSpec
import v2.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._
import v2.models.domain.EmptyJsonBody

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
        Json.toJson(submitSelfEmploymentBsasRequestBodyModel) shouldBe requestToDes
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        submitSelfEmploymentBsasRequestBodyDesJson(submitSelfEmploymentBsasRequestBodyModelWithoutIncome).as[SubmitSelfEmploymentBsasRequestBody] shouldBe
          submitSelfEmploymentBsasRequestBodyModelWithoutIncome
      }

      "not write those fields to JSON" in {
        Json.toJson(submitSelfEmploymentBsasRequestBodyModel) shouldBe requestToDes
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

    "isEmpty is called" should {
      "return true when all empty fields are supplied" in {
        submitSelfEmploymentBsasRequestBodyDesJson(emptySubmitSelfEmploymentBsasRequestBodyModel).as[SubmitSelfEmploymentBsasRequestBody]
          .isEmpty shouldBe true
      }

      "return false when non-empty fields is supplied" in {
        submitSelfEmploymentBsasRequestBodyDesJson(submitSelfEmploymentBsasRequestBodyModelWithoutIncome).as[SubmitSelfEmploymentBsasRequestBody]
          .isEmpty shouldBe false
      }
    }

    "isIncorrectOrEmptyBodyError is called" should {
      "return true when all fields are empty is supplied" in {
        submitSelfEmploymentBsasRequestBodyDesJson(emptySubmitSelfEmploymentBsasRequestBodyModel).as[SubmitSelfEmploymentBsasRequestBody]
          .isIncorrectOrEmptyBodyError shouldBe true
      }

      "return true when empty income fields are supplied" in {
        submitSelfEmploymentBsasRequestBodyDesJson(emptySubmitSelfEmploymentBsasRequestBodyModel.
          copy(Some(Income(None, None)))).as[SubmitSelfEmploymentBsasRequestBody]
          .isIncorrectOrEmptyBodyError shouldBe true
      }

      "return false when non-empty fields is supplied" in {
        submitSelfEmploymentBsasRequestBodyDesJson(submitSelfEmploymentBsasRequestBodyModelWithoutIncome).as[SubmitSelfEmploymentBsasRequestBody]
          .isIncorrectOrEmptyBodyError shouldBe false
      }
    }
  }
}
