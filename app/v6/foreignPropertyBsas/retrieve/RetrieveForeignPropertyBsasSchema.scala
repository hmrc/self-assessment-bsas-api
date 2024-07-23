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

package v6.foreignPropertyBsas.retrieve

import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.schema.DownstreamReadable
import v6.foreignPropertyBsas.retrieve.def1.model.response.Def1_RetrieveForeignPropertyBsasResponse
import v6.foreignPropertyBsas.retrieve.model.response.RetrieveForeignPropertyBsasResponse

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveForeignPropertyBsasSchema extends DownstreamReadable[RetrieveForeignPropertyBsasResponse]

object RetrieveForeignPropertyBsasSchema {

  case object Def1 extends RetrieveForeignPropertyBsasSchema {
    type DownstreamResp = Def1_RetrieveForeignPropertyBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveForeignPropertyBsasResponse.reads
  }

  private val defaultSchema = Def1

  def schemaFor(maybeTaxYear: Option[String]): RetrieveForeignPropertyBsasSchema = {
    maybeTaxYear
      .map(ResolveTaxYear.resolver)
      .flatMap(_.toOption.map(schemaFor))
      .getOrElse(defaultSchema)
  }

  def schemaFor(taxYear: TaxYear): RetrieveForeignPropertyBsasSchema = {
    if (TaxYear.starting(2023) <= taxYear) {
      Def1
    } else {
      defaultSchema
    }
  }

}
