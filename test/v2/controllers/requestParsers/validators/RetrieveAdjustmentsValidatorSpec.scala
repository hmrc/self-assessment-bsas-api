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

package v2.controllers.requestParsers.validators

import api.models.errors.{BsasIdFormatError, NinoFormatError}
import support.UnitSpec
import v2.models.request.RetrieveAdjustmentsRawData

class RetrieveAdjustmentsValidatorSpec extends UnitSpec {

  val validNino = "AA123456A"
  val validBsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val invalidNino = "BEANS"
  val invalidBsasId = "f2fb30e5-4ab6-4a29-b3c1-beans"

  val validator = new RetrieveAdjustmentsValidator()

  "validator" should {
    "return no errors" when {
      "passed valid raw data" in {
        val input = RetrieveAdjustmentsRawData(validNino, validBsasId)
        validator.validate(input) shouldBe List()
      }
    }
    "return a single error" when {
      "passed raw data with an invalid nino" in {
        val input = RetrieveAdjustmentsRawData(invalidNino, validBsasId)
        validator.validate(input) shouldBe List(NinoFormatError)
      }
      "passed raw data with an invalid bsas id" in {
        val input = RetrieveAdjustmentsRawData(validNino, invalidBsasId)
        validator.validate(input) shouldBe List(BsasIdFormatError)
      }
    }
    "return multiple errors" when {
      "passed raw data with multiple invalid fields" in {
        val input = RetrieveAdjustmentsRawData(invalidNino, invalidBsasId)
        validator.validate(input) shouldBe List(NinoFormatError, BsasIdFormatError)
      }
    }
  }

}
