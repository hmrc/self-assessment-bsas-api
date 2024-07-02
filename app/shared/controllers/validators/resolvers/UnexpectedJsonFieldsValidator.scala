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

import play.api.libs.json.{JsArray, JsObject, JsValue}
import shapeless.labelled.FieldType
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}
import shared.controllers.validators.resolvers.ExtraPathChecker.Structure
import shared.models.errors.RuleIncorrectOrEmptyBodyError
import shared.utils.Logging

class UnexpectedJsonFieldsValidator[A](implicit extraPathChecker: ExtraPathChecker[A]) extends ResolverSupport with Logging {

  def validator: Validator[(JsObject, A)] = { case (inputJson, data) =>
    val expectedJson = extraPathChecker.structureOf(data)

    findExtraPaths(acc = Nil, path = "", in = inputJson, expected = expectedJson) match {
      case Nil => None
      case paths =>
        logger.warn(s"Request body failed validation with errors - Unexpected fields: $paths")
        Some(Seq(RuleIncorrectOrEmptyBodyError.withPaths(paths)))
    }
  }

  private def findExtraPaths(acc: List[String], path: String, in: JsValue, expected: Structure): List[String] =
    (in, expected) match {
      case (in: JsObject, expected: Structure.Obj) => handleObjects(acc, path, in, expected)
      case (in: JsArray, expected: Structure.Arr)  => handleArrays(acc, path, in, expected)
      case _                                       => acc
    }

  private def handleObjects(acc: List[String], path: String, in: JsObject, expected: Structure.Obj) = {
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

  private def handleArrays(acc: List[String], path: String, in: JsArray, expected: Structure.Arr) = {
    val zippedArrayItems = (in.value.zipWithIndex zip expected.items).toList

    zippedArrayItems.foldLeft(acc) { case (acc, ((inItem, index), expectedItem)) =>
      findExtraPaths(acc, s"$path/$index", inItem, expectedItem)
    }
  }

}

object UnexpectedJsonFieldsValidator extends ResolverSupport {
  def validator[A](implicit extraPathChecker: ExtraPathChecker[A]): Validator[(JsObject, A)] = new UnexpectedJsonFieldsValidator[A].validator
}

trait ExtraPathChecker[A] {
  import ExtraPathChecker._

  def structureOf(value: A): Structure
}

// Internal specialization of ExtraPathChecker for object instances so we can directly access its fields
private[resolvers] trait ObjExtraPathChecker[A] extends ExtraPathChecker[A] {
  import ExtraPathChecker._

  def structureOf(value: A): Structure.Obj
}

object ExtraPathChecker {

  private[resolvers] sealed abstract class Structure

  private[resolvers] object Structure {

    case class Obj(fields: List[(String, Structure)]) extends Structure {
      def keyedChildren: Seq[(String, Structure)] = fields.filter(_._2 != Structure.Null)
      def keys: Set[String]                       = keyedChildren.map(_._1).toSet
    }

    case class Arr(items: Seq[Structure]) extends Structure

    case object Primitive extends Structure
    case object Null      extends Structure
  }

  def apply[A](implicit aInstance: ExtraPathChecker[A]): ExtraPathChecker[A] = aInstance

  def instance[A](func: A => Structure): ExtraPathChecker[A] = (value: A) => func(value)

  def instanceObj[A](func: A => Structure.Obj): ObjExtraPathChecker[A] = (value: A) => func(value)

  def primitive[A]: ExtraPathChecker[A] = ExtraPathChecker.instance(_ => Structure.Primitive)

  implicit val stringInstance: ExtraPathChecker[String]   = instance(_ => Structure.Primitive)
  implicit val intInstance: ExtraPathChecker[Int]         = instance(_ => Structure.Primitive)
  implicit val doubleInstance: ExtraPathChecker[Double]   = instance(_ => Structure.Primitive)
  implicit val booleanInstance: ExtraPathChecker[Boolean] = instance(_ => Structure.Primitive)

  implicit val bigIntInstance: ExtraPathChecker[BigInt]         = instance(_ => Structure.Primitive)
  implicit val bigDecimalInstance: ExtraPathChecker[BigDecimal] = instance(_ => Structure.Primitive)

  implicit def optionInstance[A](implicit aInstance: ExtraPathChecker[A]): ExtraPathChecker[Option[A]] =
    instance(opt => opt.map(aInstance.structureOf).getOrElse(Structure.Null))

  implicit def seqInstance[A, I](implicit aInstance: ExtraPathChecker[A]): ExtraPathChecker[Seq[A]] =
    instance(list => Structure.Arr(list.map(aInstance.structureOf)))

  implicit def listInstance[A](implicit aInstance: ExtraPathChecker[A]): ExtraPathChecker[List[A]] =
    instance(list => Structure.Arr(list.map(aInstance.structureOf)))

  implicit val hnilInstance: ObjExtraPathChecker[HNil] = instanceObj(_ => Structure.Obj(Nil))

  implicit def hlistInstance[K <: Symbol, H, T <: HList](implicit
      witness: Witness.Aux[K],
      hInstance: Lazy[ExtraPathChecker[H]],
      tInstance: ObjExtraPathChecker[T]
  ): ObjExtraPathChecker[FieldType[K, H] :: T] =
    instanceObj { case h :: t =>
      val hField  = witness.value.name -> hInstance.value.structureOf(h)
      val tFields = tInstance.structureOf(t).fields
      Structure.Obj(hField :: tFields)
    }

  implicit def genericInstance[A, R](implicit
      gen: LabelledGeneric.Aux[A, R],
      enc: Lazy[ExtraPathChecker[R]]
  ): ExtraPathChecker[A] =
    instance(a => enc.value.structureOf(gen.to(a)))

}
