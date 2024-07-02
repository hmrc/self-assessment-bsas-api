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

package shared.controllers.validators.resolvers

import play.api.libs.json.{JsObject, Json}
import shared.models.errors.RuleIncorrectOrEmptyBodyError
import shared.utils.UnitSpec

class UnexpectedJsonFieldsValidatorSpec extends UnitSpec {

  sealed trait SomeEnum

  object SumEnum {
    object X extends SomeEnum
    object Y extends SomeEnum
  }

  case class Bar(a: Option[String] = None, b: Option[String] = None, e: Option[SomeEnum] = None)

  case class Foo(bar: Bar, bars: Option[Seq[Bar]] = None, bar2: Option[Bar] = None)

  implicit val someEnumChecker: ExtraPathChecker[SomeEnum] = ExtraPathChecker.primitive

  val validator = new UnexpectedJsonFieldsValidator[Foo]

  "UnexpectedJsonFieldsValidator" when {
    "there are no extra fields" must {
      "validate successfully" in {
        val json = Json.parse("""{ "bar": {"a" : "v1", "b" : "v2" }, "bars": []}""").as[JsObject]
        val data = Foo(bar = Bar(Some("v1"), Some("v2")), Some(Nil))

        validator.validator((json, data)) shouldBe None
      }

      "validate successfully when object fields are in a different order" in {
        val json = Json.parse("""{ "bars": [], "bar2": {"b" : "v2" }, "bar": {"a" : "v1" }}""").as[JsObject]
        val data = Foo(bar = Bar(Some("v1"), None), Some(Nil), bar2 = Some(Bar(None, Some("v2"))))

        validator.validator((json, data)) shouldBe None
      }
    }

    "optional fields are missing" must {
      "validate successfully" in {
        val json = Json.parse("""{ "bar": {"a" : "v1", "b" : "v2" }}""").as[JsObject]
        val data = Foo(bar = Bar(Some("v1"), Some("v2")))

        validator.validator((json, data)) shouldBe None
      }
    }

    "an additional field is present" when {
      "a top level extra field is present" when {
        def bazWithValue(bazValue: String) =
          Json
            .parse(s"""{ "baz": $bazValue, "bar": {"a" : "v1",  "b" : "v2" }}""".stripMargin)
            .as[JsObject]

        val data = Foo(bar = Bar(Some("v1"), Some("v2")), bars = None)

        "the field is a string" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue(""""extra""""), data)) shouldBe Some(Seq(RuleIncorrectOrEmptyBodyError.withPath("/baz")))
          }
        }

        "the field is a number" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue("123"), data)) shouldBe Some(Seq(RuleIncorrectOrEmptyBodyError.withPath("/baz")))
          }
        }

        "the field is a boolean" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue("true"), data)) shouldBe Some(Seq(RuleIncorrectOrEmptyBodyError.withPath("/baz")))
          }
        }

        "the field is a object" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue("""{"bazField": "value"}"""), data)) shouldBe Some(Seq(RuleIncorrectOrEmptyBodyError.withPath("/baz")))
          }
        }

        "the field is a array" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue("""["value"]"""), data)) shouldBe Some(Seq(RuleIncorrectOrEmptyBodyError.withPath("/baz")))
          }
        }
      }

      "locate extra fields when object fields are in a different order" in {
        val json = Json.parse("""{ "bar2": {"b" : "v2", "baz": 123 }, "bar": {"a" : "v1" }}""").as[JsObject]
        val data = Foo(bar = Bar(Some("v1"), None), bar2 = Some(Bar(None, Some("v2"))))

        validator.validator((json, data)) shouldBe Some(Seq(RuleIncorrectOrEmptyBodyError.withPath("/bar2/baz")))
      }

      "a nested extra field is present" when {
        val data = Foo(bar = Bar(Some("v1"), Some("v2")), bars = Some(Seq(Bar(Some("v1"), Some("v2")), Bar(Some("v1"), Some("v2")))))

        "the field is nested in an object" must {
          "return an error with path to the extra field" in {
            val json = Json
              .parse(s"""{ "bar": {"a" : "v1", "baz": "extra", "b" : "v2" }, 
                   |  "bars": [
                   |    {"a" : "v1",  "b" : "v2" }, 
                   |    {"a" : "v1", "b" : "v2" }
                   |  ]
                   |}""".stripMargin)
              .as[JsObject]

            validator.validator((json, data)) shouldBe Some(Seq(RuleIncorrectOrEmptyBodyError.withPath("/bar/baz")))
          }
        }

        "the field is nested in an object in an array" must {
          "return an error with path to the extra field" in {
            val json = Json
              .parse(s"""{
                   |  "bar": {"a" : "v1", "b" : "v2" },
                   |  "bars": [
                   |    {"a" : "v1",  "b" : "v2" }, 
                   |    {"a" : "v1", "baz": "extra", "b" : "v2" }
                   |  ]
                   |}""".stripMargin)
              .as[JsObject]

            validator.validator((json, data)) shouldBe Some(Seq(RuleIncorrectOrEmptyBodyError.withPath("/bars/1/baz")))
          }
        }

        "multiple additional fields are present" must {
          "return an error with paths to the extra fields" in {
            val json = Json
              .parse(s"""{
                 |  "bar": {"a" : "v1", "b" : "v2" , "baz": "extra"},
                 |  "baz": "extra",
                 |  "bars": [
                 |    {"a" : "v1", "baz": "extra0", "b" : "v2" }, 
                 |    {"a" : "v1", "baz": "extra1", "b" : "v2" }
                 |  ]
                 |}""".stripMargin)
              .as[JsObject]

            validator.validator((json, data)) shouldBe Some(
              Seq(RuleIncorrectOrEmptyBodyError.withPaths(Seq("/baz", "/bar/baz", "/bars/0/baz", "/bars/1/baz"))))
          }
        }
      }
    }
  }

}
