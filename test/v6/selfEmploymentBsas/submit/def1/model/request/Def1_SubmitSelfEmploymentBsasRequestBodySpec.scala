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

package v6.selfEmploymentBsas.submit.def1.model.request

import play.api.libs.json.Json
import shared.models.domain.EmptyJsonBody
import shared.utils.UnitSpec
import v6.selfEmploymentBsas.submit.def1.model.request.fixtures.SubmitSelfEmploymentBsasFixtures._

class Def1_SubmitSelfEmploymentBsasRequestBodySpec extends UnitSpec {

  "SubmitSelfEmploymentBsasRequestBody" when {
    "read from valid JSON" should {
      "produce the expected SubmitSelfEmploymentBsasRequestBody object" in {
        submitSelfEmploymentBsasRequestBodyDesJson(submitSelfEmploymentBsasRequestBody).as[Def1_SubmitSelfEmploymentBsasRequestBody] shouldBe
          submitSelfEmploymentBsasRequestBody
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(submitSelfEmploymentBsasRequestBody) shouldBe requestToIfs
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        submitSelfEmploymentBsasRequestBodyDesJson(submitSelfEmploymentBsasRequestBodyWithoutIncome)
          .as[Def1_SubmitSelfEmploymentBsasRequestBody] shouldBe
          submitSelfEmploymentBsasRequestBodyWithoutIncome
      }

      "not write those fields to JSON" in {
        Json.toJson(submitSelfEmploymentBsasRequestBody) shouldBe requestToIfs
      }
    }

    "no fields as supplied" should {
      "read to an empty SubmitSelfEmploymentBsasRequestBody object" in {
        submitSelfEmploymentBsasRequestBodyDesJson(emptySubmitSelfEmploymentBsasRequestBody)
          .as[Def1_SubmitSelfEmploymentBsasRequestBody] shouldBe
          emptySubmitSelfEmploymentBsasRequestBody
      }

      "write to empty JSON" in {
        Json.toJson(emptySubmitSelfEmploymentBsasRequestBody) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }

}
