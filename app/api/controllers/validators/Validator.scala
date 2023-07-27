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

import api.models.errors.{ BadRequestError, ErrorWrapper, MtdError }
import utils.Logging

trait Validator[PARSED] extends Logging {

  def validate: Either[Seq[MtdError], PARSED]

  def validateAndWrapResult()(implicit correlationId: String): Either[ErrorWrapper, PARSED] = {
    validate match {
      case Right(parsed) =>
        logger.info(s"Validation successful for the request with CorrelationId: $correlationId")
        Right(parsed)

      case Left(errs) =>
        combineErrors(errs) match {
          case err :: Nil =>
            logger.warn(s"Validation failed with ${err.code} error for the request with CorrelationId: $correlationId")
            Left(ErrorWrapper(correlationId, err, None))

          case errs =>
            logger.warn(s"Validation failed with ${errs.map(_.code).mkString(",")} error for the request with CorrelationId: $correlationId")
            Left(ErrorWrapper(correlationId, BadRequestError, Some(errs)))
        }
    }
  }

  /** Assuming one or more validation errors - combines the possibleErrors with the Left error from result, then removes duplicates. The Left 'result'
    * is included as it may be the result being returned by a nested Validator.
    */
  protected def mapResult(result: Either[Seq[MtdError], PARSED], possibleErrors: Either[Seq[MtdError], _]*): Either[Seq[MtdError], PARSED] = {
    result match {
      case Left(_)       => combineLefts((possibleErrors :+ result): _*)
      case Right(parsed) => Right(parsed)
    }
  }

  /**
    * If all of the results are Rights, return the parsed value as a Right.
    * If any of the results are Lefts, return a combined Left containing all the separate errors.
    */
  protected def combine(parsed: PARSED, results: Either[Seq[MtdError], _]*): Either[Seq[MtdError], PARSED] = {
    val lefts = results.collect {
      case Left(errs) => errs
      case Right(_)   => Nil
    }.flatten

    if (lefts.isEmpty) {
      Right(parsed)
    } else {
      Left(lefts)
    }
  }

  /**
    * Always returns a Left.
    */
  protected def combineLefts(possibleErrors: Either[Seq[MtdError], _]*): Either[Seq[MtdError], PARSED] =
    Left(
      possibleErrors.distinct
        .collect { case Left(errs) => errs }
        .flatten
        .toList)

  private def combineErrors(errors: Seq[MtdError]): Seq[MtdError] = {
    errors
      .groupBy(_.message)
      .map {
        case (_, errors) =>
          val baseError = errors.head.copy(paths = Some(Seq.empty[String]))

          errors.fold(baseError)(
            (error1, error2) => {
              val paths: Option[Seq[String]] = for {
                error1Paths <- error1.paths
                error2Paths <- error2.paths
              } yield {
                error1Paths ++ error2Paths
              }
              error1.copy(paths = paths)
            }
          )
      }
      .toList
  }

}
