/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.ukPropertyBsas.retrieve

import cats.data.Validated
import cats.data.Validated.Valid
import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import shared.schema.DownstreamReadable
import v7.ukPropertyBsas.retrieve.def1.model.response.Def1_RetrieveUkPropertyBsasResponse
import v7.ukPropertyBsas.retrieve.def2.model.response.Def2_RetrieveUkPropertyBsasResponse
import v7.ukPropertyBsas.retrieve.model.response.RetrieveUkPropertyBsasResponse

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveUkPropertyBsasSchema extends DownstreamReadable[RetrieveUkPropertyBsasResponse]

object RetrieveUkPropertyBsasSchema {

  case object Def1 extends RetrieveUkPropertyBsasSchema {
    type DownstreamResp = Def1_RetrieveUkPropertyBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveUkPropertyBsasResponse.reads
  }

  case object Def2 extends RetrieveUkPropertyBsasSchema {
    type DownstreamResp = Def2_RetrieveUkPropertyBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def2_RetrieveUkPropertyBsasResponse.reads
  }

  def schemaFor(taxYearString: String): Validated[Seq[MtdError], RetrieveUkPropertyBsasSchema] = {
    ResolveTaxYear(taxYearString) andThen schemaFor
  }

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], RetrieveUkPropertyBsasSchema] = {
    if (taxYear <= TaxYear.starting(2024)) Valid(Def1)
    else Valid(Def2)
  }

}
