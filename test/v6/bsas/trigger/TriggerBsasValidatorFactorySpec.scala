/*
 * Copyright 2024 HM Revenue & Customs
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

package v6.bsas.trigger

import config.MockBsasConfig
import play.api.libs.json.Json
import shared.controllers.validators.AlwaysErrorsValidator
import shared.utils.UnitSpec
import v6.bsas.trigger.def1.Def1_TriggerBsasValidator
import v6.bsas.trigger.def2.Def2_TriggerBsasValidator

class TriggerBsasValidatorFactorySpec extends UnitSpec with MockBsasConfig {

  private def validatorFor(endDate: String) = {
    val body = Json.parse(s"""
                          |{
                          |  "accountingPeriod": {
                          |    "endDate": "$endDate"
                          |  }
                          |}
          """.stripMargin)

    new TriggerBsasValidatorFactory().validator(nino = "ignoredNino", body)
  }

  "TriggerBsasValidatorFactory" when {
    "given a request corresponding to a Def1 schema" should {
      "return a Def1 validator" in {
        validatorFor("2024-04-06") shouldBe a[Def1_TriggerBsasValidator]
      }
    }

    "given a request corresponding to a Def2 schema" should {
      "return a Def2 validator" in {
        validatorFor("2025-04-06") shouldBe a[Def2_TriggerBsasValidator]
      }
    }

    "given a request where no valid schema could be determined" should {
      "return a validator returning the errors" in {
        validatorFor("BAD_DATE") shouldBe an[AlwaysErrorsValidator]
      }
    }
  }

}
