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

import api.models.errors.MtdError

import scala.annotation.tailrec

object ResolverHelpers {

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
  @tailrec
  def flatten[PARSED](either: Either[Seq[MtdError], _]): Either[Seq[MtdError], PARSED] = {
    either match {
      case Left(errors) =>
        Left(errors)
      case Right(inner) =>
        inner match {
          case nestedEither: Either[_, _] =>
            flatten(nestedEither.asInstanceOf[Either[Seq[MtdError], PARSED]])
          case parsed =>
            Right(parsed.asInstanceOf[PARSED])
        }
    }
  }
}