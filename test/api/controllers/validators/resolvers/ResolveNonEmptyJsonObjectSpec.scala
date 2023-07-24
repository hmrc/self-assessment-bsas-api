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

import api.models.errors.RuleIncorrectOrEmptyBodyError
import api.models.utils.JsonErrorValidators
import play.api.libs.json.{ Json, OFormat }
import shapeless.HNil
import support.UnitSpec
import utils.EmptinessChecker

import scala.annotation.nowarn

class ResolveNonEmptyJsonObjectSpec extends UnitSpec with JsonErrorValidators {

  case class TestDataObject(fieldOne: String, fieldTwo: String, oneOf1: Option[String] = None, oneOf2: Option[String] = None)
  case class TestDataWrapper(arrayField: Seq[TestDataObject])

  implicit val testDataObjectFormat: OFormat[TestDataObject]   = Json.format[TestDataObject]
  implicit val testDataWrapperFormat: OFormat[TestDataWrapper] = Json.format[TestDataWrapper]

  // at least one of oneOf1 and oneOf2 must be included:
  @nowarn("cat=lint-byname-implicit")
  implicit val emptinessChecker: EmptinessChecker[TestDataObject] = EmptinessChecker.use { o =>
    "oneOf1" -> o.oneOf1 :: "oneOf2" -> o.oneOf2 :: HNil
  }

  private val resolveTestDataObject = new ResolveNonEmptyJsonObject[TestDataObject]()

  @nowarn("cat=lint-byname-implicit")
  private val resolveTestDataWrapper = new ResolveNonEmptyJsonObject[TestDataWrapper]()

  "ResolveNonEmptyJsonObject" should {
    "return no errors" when {
      "given a valid JSON object" in {
        val json = Json.parse("""{ "fieldOne" : "field one", "fieldTwo" : "field two" }""")

        val result = resolveTestDataObject(json)
        result shouldBe Right(TestDataObject("field one", "field two"))
      }
    }

    "return an error " when {
      "a required field is missing" in {
        val json = Json.parse("""{ "fieldOne" : "field one" }""")

        val result = resolveTestDataObject(json)
        result shouldBe Left(
          List(
            RuleIncorrectOrEmptyBodyError.withPath("/fieldTwo")
          ))
      }

      "a required field is missing in an array object" in {
        val json = Json.parse("""{ "arrayField" : [{ "fieldOne" : "Something" }]}""")

        val result = resolveTestDataWrapper(json)
        result shouldBe Left(
          List(
            RuleIncorrectOrEmptyBodyError.withPath("/arrayField/0/fieldTwo")
          ))
      }

      "a required field is missing in multiple array objects" in {
        val json = Json.parse("""
            |{
            |  "arrayField" : [
            |    { "fieldOne" : "Something" },
            |    { "fieldOne" : "Something" }
            |  ]
            |}
            |""".stripMargin)

        val result = resolveTestDataWrapper(json)
        result shouldBe Left(
          List(
            RuleIncorrectOrEmptyBodyError.withPaths(
              List(
                "/arrayField/0/fieldTwo",
                "/arrayField/1/fieldTwo"
              ))
          ))
      }

      "an empty body is submitted" in {
        val json = Json.parse("""{}""")

        val result = resolveTestDataObject(json)
        result shouldBe Left(List(RuleIncorrectOrEmptyBodyError))
      }

      "a non-empty body is supplied without any expected fields" in {
        val json = Json.parse("""{"field": "value"}""")

        val result = resolveTestDataObject(json)
        result shouldBe Left(
          List(
            RuleIncorrectOrEmptyBodyError.withPaths(List("/fieldOne", "/fieldTwo"))
          ))
      }

      "a field is supplied with the wrong data type" in {
        val json = Json.parse("""{"fieldOne": true, "fieldTwo": "value"}""")

        val result = resolveTestDataObject(json)
        result shouldBe Left(
          List(
            RuleIncorrectOrEmptyBodyError.withPath("/fieldOne")
          ))
      }

      "detect empty objects" in {
        val json = Json.parse("""{ "fieldOne" : "Something", "fieldTwo" : "SomethingElse" }""")

        val result = resolveTestDataObject(json)
        result shouldBe Left(List(RuleIncorrectOrEmptyBodyError))
      }

      "detect empty arrays" in {
        val json   = Json.parse("""{ "arrayField": [] }""")
        val result = resolveTestDataWrapper(json)

        result shouldBe Left(
          List(
            RuleIncorrectOrEmptyBodyError.withPath("/arrayField")
          ))
      }

      "return no error when all objects are non-empty" in {
        val json = Json.parse("""{ "fieldOne" : "field one", "fieldTwo" : "field two", "oneOf1": "value" }""")

        val result = resolveTestDataObject(json)
        result shouldBe Right(
          TestDataObject("field one", "field two")
        )
      }
    }
  }
}
