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

package v7.foreignPropertyBsas.submit

import play.api.libs.json.JsObject
import shared.controllers.validators.AlwaysErrorsValidator
import shared.utils.UnitSpec
import v7.foreignPropertyBsas.submit.def1.Def1_SubmitForeignPropertyBsasValidator
import v7.foreignPropertyBsas.submit.def2.Def2_SubmitForeignPropertyBsasValidator
import v7.foreignPropertyBsas.submit.def3.Def3_SubmitForeignPropertyBsasValidator

class SubmitForeignPropertyBsasValidatorFactorySpec extends UnitSpec {

  private def validatorFor(taxYear: String) =
    new SubmitForeignPropertyBsasValidatorFactory().validator(
      nino = "ignoredNino",
      calculationId = "ignored",
      taxYear = taxYear,
      body = JsObject.empty)

  "SubmitForeignPropertyBsasValidatorFactory" when {
    "given a request corresponding to a Def1 schema" should {
      "return a Def1 validator" in {
        validatorFor("2023-24") shouldBe a[Def1_SubmitForeignPropertyBsasValidator]
      }
    }

    "given a request corresponding to a Def2 schema" should {
      "return a Def2 validator" in {
        validatorFor("2024-25") shouldBe a[Def2_SubmitForeignPropertyBsasValidator]
      }
    }

    "given a request corresponding to a Def3 schema" should {
      "return a Def3 validator" in {
        validatorFor("2025-26") shouldBe a[Def3_SubmitForeignPropertyBsasValidator]
      }
    }

    "given a request where no valid schema could be determined" should {
      "return a validator returning the errors" in {
        validatorFor("BAD_TAX_YEAR") shouldBe an[AlwaysErrorsValidator]
      }
    }
  }

}
