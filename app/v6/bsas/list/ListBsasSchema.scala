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

import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.schema.DownstreamReadable
import v6.bsas.list.def1.model.response.{Def1_BsasSummary, Def1_ListBsasResponse}
import v6.bsas.list.def2.model.response.{Def2_BsasSummary, Def2_ListBsasResponse}
import v6.bsas.list.model.response.{BsasSummary, ListBsasResponse}

import scala.math.Ordered.orderingToOrdered

sealed trait ListBsasSchema extends DownstreamReadable[ListBsasResponse[BsasSummary]]

object ListBsasSchema {

  case object Def1 extends ListBsasSchema {
    type DownstreamResp = Def1_ListBsasResponse[Def1_BsasSummary]
    val connectorReads: Reads[DownstreamResp] = Def1_ListBsasResponse.reads
  }

  case object Def2 extends ListBsasSchema {
    type DownstreamResp = Def2_ListBsasResponse[Def2_BsasSummary]
    val connectorReads: Reads[DownstreamResp] = Def2_ListBsasResponse.reads
  }

  def schemaFor(maybeTaxYear: Option[String]): ListBsasSchema = {
    maybeTaxYear
      .map(ResolveTaxYear.resolver)
      .flatMap(_.toOption.map(schemaFor))
      .getOrElse(schemaFor(TaxYear.currentTaxYear))
  }

  def schemaFor(taxYear: TaxYear): ListBsasSchema = {
    if (taxYear <= TaxYear.starting(2023)) {
      Def1
    } else {
      Def2
    }
  }

}
