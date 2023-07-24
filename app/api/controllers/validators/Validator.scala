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

package api.controllers.validators

import api.controllers.validators.resolvers.ResolverHelpers
import api.models.errors.{ BadRequestError, ErrorWrapper, MtdError }
import utils.Logging

trait Validator[PARSED] extends Logging {

  def validate: Either[Seq[MtdError], PARSED]

  def validateAndWrapResult()(implicit correlationId: String): Either[ErrorWrapper, PARSED] = {
    validate match {
      case Right(parsed) =>
        logger.info(
          "[RequestParser][parseRequest] " +
            s"Validation successful for the request with CorrelationId: $correlationId")

        Right(parsed)

      case Left(err :: Nil) =>
        logger.warn(
          "[RequestParser][parseRequest] " +
            s"Validation failed with ${err.code} error for the request with CorrelationId: $correlationId")
        Left(ErrorWrapper(correlationId, err, None))

      case Left(errs) =>
        logger.warn(
          "[RequestParser][parseRequest] " +
            s"Validation failed with ${errs.map(_.code).mkString(",")} error for the request with CorrelationId: $correlationId")
        Left(ErrorWrapper(correlationId, BadRequestError, Some(errs)))
    }
  }

  /** Flatten an arbitrarily nested result. 'either' may be any of:
    * {{{
    *   Either[Seq[MtdError], PARSED]
    *   Either[Seq[MtdError], Either[Seq[MtdError], PARSED]]
    *   Either[Seq[MtdError], Either[Seq[MtdError], Either[Seq[MtdError], PARSED]]]
    *   etc
    * }}}
    *
    * to:
    *
    * {{{
    *   Either[Seq[MtdError], PARSED]]
    * }}}.
    *
    * The asInstanceOfs are a workaround for type erasure.
    */
  protected def flatten(either: Either[Seq[MtdError], _]): Either[Seq[MtdError], PARSED] =
    ResolverHelpers.flatten(either)

  /** Assuming one or more validation errors - combines the possibleErrors with the Left error from result, then removes duplicates. The Left 'result'
    * is included as it may be the result being returned by a nested Validator.
    */
  protected def mapResult(result: Either[Seq[MtdError], PARSED], possibleErrors: Either[Seq[MtdError], _]*): Either[Seq[MtdError], PARSED] = {
    result match {
      case Left(_)       => combineLefts((possibleErrors :+ result): _*)
      case Right(parsed) => Right(parsed)
    }
  }

  protected def combineLefts(possibleErrors: Either[Seq[MtdError], _]*): Either[Seq[MtdError], PARSED] =
    Left(
      possibleErrors.distinct
        .collect { case Left(errs) => errs }
        .flatten
        .toList)

}
