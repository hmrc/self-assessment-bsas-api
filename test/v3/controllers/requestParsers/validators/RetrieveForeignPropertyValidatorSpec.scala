/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.controllers.requestParsers.validators

import support.UnitSpec
import v3.models.errors.{CalculationIdFormatError, NinoFormatError}
import v3.models.request.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasRawData

class RetrieveForeignPropertyValidatorSpec extends UnitSpec {

  val validNino = "AA123456A"
  val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val invalidNino = "WALRUS"
  val invalidCalculationId = "f2fb30e5-4ab6-4a29-b3c1-walrus"

  val validator = new RetrieveForeignPropertyValidator()

  "validator" should {
    "return no errors" when {
      "passed valid raw data with all fields" in {
        val input = RetrieveForeignPropertyBsasRawData(validNino, validCalculationId)
        validator.validate(input) shouldBe List()
      }
    }
    "return a single error" when {
      "passed raw data with an invalid nino" in {
        val input = RetrieveForeignPropertyBsasRawData(invalidNino, validCalculationId)
        validator.validate(input) shouldBe List(NinoFormatError)
      }
      "passed raw data with an invalid calculation id" in {
        val input = RetrieveForeignPropertyBsasRawData(validNino, invalidCalculationId)
        validator.validate(input) shouldBe List(CalculationIdFormatError)
      }
    }
    "return multiple errors" when {
      "passed raw data with multiple invalid fields" in {
        val input = RetrieveForeignPropertyBsasRawData(invalidNino, invalidCalculationId)
        validator.validate(input) shouldBe List(NinoFormatError, CalculationIdFormatError)
      }
    }
  }
}