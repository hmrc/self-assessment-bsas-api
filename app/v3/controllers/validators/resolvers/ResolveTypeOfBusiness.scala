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

package v3.controllers.validators.resolvers

import shared.controllers.validators.resolvers.Resolver
import shared.models.errors.{ MtdError, TypeOfBusinessFormatError }
import cats.data.Validated
import cats.data.Validated.{ Invalid, Valid }
import v3.models.domain.TypeOfBusiness

import scala.util.{ Failure, Success, Try }

object ResolveTypeOfBusiness extends Resolver[String, TypeOfBusiness] {

  def apply(value: String, error: Option[MtdError], path: Option[String]): Validated[Seq[MtdError], TypeOfBusiness] =
    Try {
      TypeOfBusiness.parser(value)
    } match {
      case Failure(_)              => Invalid(List(error.getOrElse(TypeOfBusinessFormatError)))
      case Success(typeOfBusiness) => Valid(typeOfBusiness)
    }

}
