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

package v7.foreignPropertyBsas.retrieve

import cats.data.Validated
import cats.data.Validated.Valid
import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import shared.schema.DownstreamReadable
import v7.foreignPropertyBsas.retrieve.def1.model.response.Def1_RetrieveForeignPropertyBsasResponse
import v7.foreignPropertyBsas.retrieve.def2.model.response.Def2_RetrieveForeignPropertyBsasResponse
import v7.foreignPropertyBsas.retrieve.def3.model.response.Def3_RetrieveForeignPropertyBsasResponse
import v7.foreignPropertyBsas.retrieve.model.response.RetrieveForeignPropertyBsasResponse

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveForeignPropertyBsasSchema extends DownstreamReadable[RetrieveForeignPropertyBsasResponse]

object RetrieveForeignPropertyBsasSchema {

  case object Def1 extends RetrieveForeignPropertyBsasSchema {
    type DownstreamResp = Def1_RetrieveForeignPropertyBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveForeignPropertyBsasResponse.reads
  }

  case object Def2 extends RetrieveForeignPropertyBsasSchema {
    type DownstreamResp = Def2_RetrieveForeignPropertyBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def2_RetrieveForeignPropertyBsasResponse.reads
  }

  case object Def3 extends RetrieveForeignPropertyBsasSchema {
    type DownstreamResp = Def3_RetrieveForeignPropertyBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def3_RetrieveForeignPropertyBsasResponse.reads
  }

  def schemaFor(taxYearString: String): Validated[Seq[MtdError], RetrieveForeignPropertyBsasSchema] = {
    ResolveTaxYear(taxYearString) andThen schemaFor
  }

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], RetrieveForeignPropertyBsasSchema] = {
    if (taxYear <= TaxYear.starting(2024)) Valid(Def1)
    else if (taxYear <= TaxYear.starting(2025)) Valid(Def2)
    else Valid(Def3)
  }

}
