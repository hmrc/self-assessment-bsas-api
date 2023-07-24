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

package api.controllers.validators.resolvers

import api.models.errors.BadRequestError
import support.UnitSpec

class ResolveBooleanSpec extends UnitSpec {

  "ResolveBoolean" should {

    "return no errors" when {
      "given the string 'true'" in {
        val result = ResolveBoolean("true", BadRequestError)
        result shouldBe Right(true)
      }

      "given the upper case string 'TRUE'" in {
        val result = ResolveBoolean("TRUE", BadRequestError)
        result shouldBe Right(true)
      }

      "given the string 'false'" in {
        val result = ResolveBoolean("false", BadRequestError)
        result shouldBe Right(false)
      }

      "given the optional string 'true'" in {
        val result = ResolveBoolean(Some("true"), BadRequestError)
        result shouldBe Right(Some(true))
      }

      "given None with a default value" in {
        val result = ResolveBoolean(None, defaultValue = true, BadRequestError)
        result shouldBe Right(true)
      }
    }

    "return an error" when {
      "given an invalid string" in {
        val result = ResolveBoolean("invalid", BadRequestError)
        result shouldBe Left(List(BadRequestError))
      }

      "given an invalid optional string" in {
        val result = ResolveBoolean(Some("invalid"), BadRequestError)
        result shouldBe Left(List(BadRequestError))
      }

      "given an invalid optional string with a default value" in {
        val result = ResolveBoolean(Some("invalid"), defaultValue = true, BadRequestError)
        result shouldBe Left(List(BadRequestError))
      }
    }
  }

}
