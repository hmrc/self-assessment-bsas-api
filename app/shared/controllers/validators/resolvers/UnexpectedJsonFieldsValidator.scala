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

import play.api.libs.json.{JsArray, JsObject, JsValue, OWrites}
import shared.models.errors.RuleIncorrectOrEmptyBodyError
import shared.utils.Logging

class UnexpectedJsonFieldsValidator[A](writes: OWrites[A]) extends ResolverSupport with Logging {

  def validator: Validator[(JsObject, A)] = { case (inputJson, data) =>
    val expectedJson = writes.writes(data)

    findExtraPaths(acc = Nil, path = "", in = inputJson, expected = expectedJson) match {
      case Nil => None
      case paths =>
        logger.warn(s"Request body failed validation with errors - Unexpected fields: $paths")
        Some(Seq(RuleIncorrectOrEmptyBodyError.withPaths(paths)))
    }
  }

  private def findExtraPaths(acc: List[String], path: String, in: JsValue, expected: JsValue): List[String] =
    (in, expected) match {
      case (in: JsObject, expected: JsObject) => handleObjects(acc, path, in, expected)
      case (in: JsArray, expected: JsArray)   => handleArrays(acc, path, in, expected)
      case _                                  => acc
    }

  private def handleObjects(acc: List[String], path: String, in: JsObject, expected: JsObject) = {
    val extraPathsInThisObject = {
      val extraFields = in.value.toMap -- expected.keys
      extraFields.map { case (name, _) => s"$path/$name" }.toList
    }

    expected.fields.foldLeft(acc ++ extraPathsInThisObject) { case (acc, (fieldName, expectedField)) =>
      in.value.get(fieldName) match {
        case Some(inField) => findExtraPaths(acc, s"$path/$fieldName", inField, expectedField)
        case None          => acc
      }
    }
  }

  private def handleArrays(acc: List[String], path: String, in: JsArray, expected: JsArray) = {
    val zippedArrayItems = (in.value.zipWithIndex zip expected.value).toList

    zippedArrayItems.foldLeft(acc) { case (acc, ((inItem, index), expectedItem)) =>
      findExtraPaths(acc, s"$path/$index", inItem, expectedItem)
    }
  }

}

object UnexpectedJsonFieldsValidator extends ResolverSupport {
  def validator[A](writes: OWrites[A]): Validator[(JsObject, A)] = new UnexpectedJsonFieldsValidator(writes).validator
}
