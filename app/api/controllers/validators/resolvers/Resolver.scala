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

import api.models.errors.{ InternalError, MtdError }

/** Parses a raw value (e.g. String or JsValue) to a target type, validating in the process.
  *
  * @tparam S
  *   The source data type
  * @tparam T
  *   The target data type
  */
trait Resolver[S, T] {

  def apply(value: S, error: Option[MtdError], path: Option[String]): Either[Seq[MtdError], T]

  def apply(value: S): Either[Seq[MtdError], T] = apply(value, None, None)

  def apply(value: S, path: Option[String]): Either[Seq[MtdError], T] = apply(value, None, path)

  def apply(value: S, path: String): Either[Seq[MtdError], T] = apply(value, None, Some(path))

  def apply(value: S, error: MtdError): Either[Seq[MtdError], T] =
    apply(value, Option(error), path = None)

  def apply(maybeValue: Option[S], error: MtdError): Either[Seq[MtdError], Option[T]] =
    apply(maybeValue, Option(error))

  def apply(maybeValue: Option[S], defaultValue: T): Either[Seq[MtdError], T] =
    apply(maybeValue, defaultValue, error = None)

  def apply(maybeValue: Option[S], defaultValue: T, error: MtdError): Either[Seq[MtdError], T] =
    apply(maybeValue, defaultValue, Option(error))

  def apply(maybeValue: Option[S], defaultValue: T, error: Option[MtdError]): Either[Seq[MtdError], T] =
    apply(maybeValue, error)
      .map(maybeResolvedValue => maybeResolvedValue.getOrElse(defaultValue))

  def apply(maybeValue: Option[S], error: Option[MtdError] = None, path: Option[String] = None): Either[Seq[MtdError], Option[T]] =
    maybeValue match {
      case Some(value) => apply(value, error, path).map(Option(_))
      case None        => Right(None)
    }

  protected def requirePath(path: Option[String]): String = {
    path.getOrElse(throw new IllegalArgumentException(s"${getClass.getSimpleName} requires the path"))
  }

  protected def requireError(maybeError: Option[MtdError], path: Option[String]): MtdError =
    withError(maybeError, orDefault = InternalError, path)

  protected def withError(maybeError: Option[MtdError], orDefault: MtdError, extraPath: Option[String]): MtdError =
    maybeError match {
      case Some(error) => error.maybeWithExtraPath(extraPath)
      case None        => orDefault
    }

  protected def withErrors(maybeError: Option[MtdError], additional: Seq[MtdError], extraPath: Option[String]): Seq[MtdError] =
    maybeError match {
      case Some(error) => List(error.maybeWithExtraPath(extraPath))
      case None        => additional
    }
}
