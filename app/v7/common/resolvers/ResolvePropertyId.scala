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

package v7.common.resolvers

import cats.data.Validated
import shared.controllers.validators.resolvers.{ResolveStringPattern, ResolverSupport}
import shared.models.errors.{MtdError, TransactionIdFormatError}
import v7.common.model.PropertyId

import scala.util.matching.Regex

object ResolvePropertyId extends ResolverSupport {

  private val uuidRegex: Regex = "^.*\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}.*$".r

  val resolver: Resolver[String, PropertyId] =
    ResolveStringPattern(uuidRegex, TransactionIdFormatError).resolver.map(PropertyId.apply)

  def apply(value: PropertyId): Validated[Seq[MtdError], PropertyId] = resolver(value.toString)
}
