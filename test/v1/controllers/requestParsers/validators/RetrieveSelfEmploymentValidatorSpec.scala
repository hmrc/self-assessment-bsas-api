/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import support.UnitSpec
import v1.models.errors.{AdjustedStatusFormatError, BsasIdFormatError, NinoFormatError}
import v1.models.request.RetrieveSelfEmploymentBsasRawData

class RetrieveSelfEmploymentValidatorSpec extends UnitSpec {

  val validNino = "AA123456A"
  val validBsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val validAdjustedStatus = Some("true")
  val invalidNino = "BEANS"
  val invalidBsasId = "f2fb30e5-4ab6-4a29-b3c1-beans"
  val invalidAdjustedStatus = Some("beans")

  val validator = new RetrieveSelfEmploymentValidator()

  "validator" should {
    "return no errors" when {
      "passed valid raw data with all fields" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, validBsasId, validAdjustedStatus)
        validator.validate(input) shouldBe List()
      }
      "passed valid raw data with only mandatory fields" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, validBsasId, None)
        validator.validate(input) shouldBe List()
      }
    }
    "return a single error" when {
      "passed raw data with an invalid nino" in {
        val input = RetrieveSelfEmploymentBsasRawData(invalidNino, validBsasId, validAdjustedStatus)
        validator.validate(input) shouldBe List(NinoFormatError)
      }
      "passed raw data with an invalid bsas id" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, invalidBsasId, validAdjustedStatus)
        validator.validate(input) shouldBe List(BsasIdFormatError)
      }
      "passed raw data with an invalid adjusted status" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, validBsasId, invalidAdjustedStatus)
        validator.validate(input) shouldBe List(AdjustedStatusFormatError)
      }
    }
    "return multiple errors" when {
      "passed raw data with multiple invalid fields" in {
        val input = RetrieveSelfEmploymentBsasRawData(invalidNino, invalidBsasId, invalidAdjustedStatus)
        validator.validate(input) shouldBe List(NinoFormatError, BsasIdFormatError, AdjustedStatusFormatError)
      }
    }
  }

}
