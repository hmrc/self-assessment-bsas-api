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

package shared.controllers.validators.resolvers

import cats.data.Validated.{Invalid, Valid}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.UnitSpec
import shared.models.errors.ValueFormatError

class ResolveIntegerSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks {

  private val path = "/some/path"

  "validate" when {
    "min and max are specified" must {
      val min: Int = -100
      val max: Int = 100

      val error   = ValueFormatError.copy(paths = Some(List(path)), message = "The value must be between -100 and 100")
      val resolve = ResolveInteger(min, max)

      "return the error with the correct message if and only if the value is outside the inclusive range" when {

        "using validate" in forAll { money: Int =>
          val expected = if (min <= money && money <= max) Valid(money) else Invalid(List(error))
          val result   = resolve(money, path)
          result shouldBe expected
        }

        "using validateOptional" in forAll { money: Int =>
          val expected = if (min <= money && money <= max) Valid(Some(money)) else Invalid(List(error))
          val result   = resolve(Some(money), path)
          result shouldBe expected
        }
      }

      "no number is supplied to validateOptional" when {
        "return no error" in {
          val result = resolve(None, path)
          result shouldBe Valid(None)
        }
      }
    }

    "min and max are not specified" must {
      val resolve = ResolveInteger()
      val error   = ValueFormatError.copy(paths = Some(List(path)), message = "The value must be between 0 and 99")

      "allow 0" in {
        val result = resolve(0, path)
        result shouldBe Valid(0)
      }

      "disallow less than 0" in {
        val result = resolve(-1, path)
        result shouldBe Invalid(List(error))
      }

      "allow 99" in {
        val result = resolve(99, path)
        result shouldBe Valid(99)
      }

      "disallow more than 103" in {
        val result = resolve(103, path)
        result shouldBe Invalid(List(error))
      }
    }
  }

}
