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

package v6.bsas.list

import cats.data.Validated
import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import shared.schema.DownstreamReadable
import v6.bsas.list.def1.model.response.Def1_ListBsasResponse
import v6.bsas.list.def2.model.response.Def2_ListBsasResponse
import v6.bsas.list.model.response.ListBsasResponse

import scala.math.Ordered.orderingToOrdered

sealed trait ListBsasSchema extends DownstreamReadable[ListBsasResponse]

object ListBsasSchema {

  case object Def1 extends ListBsasSchema {
    type DownstreamResp = Def1_ListBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def1_ListBsasResponse.reads
  }

  case object Def2 extends ListBsasSchema {
    type DownstreamResp = Def2_ListBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def2_ListBsasResponse.reads
  }

  def schemaFor(taxYearString: String): Validated[Seq[MtdError], ListBsasSchema] =
    ResolveTaxYear(taxYearString).map(schemaFor)

  def schemaFor(taxYear: TaxYear): ListBsasSchema = {
    if (taxYear <= TaxYear.fromMtd("2024-25")) {
      Def1
    } else {
      Def2
    }
  }

}
