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

package v5.retrieveSelfEmploymentBsas.schema

import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import v5.retrieveSelfEmploymentBsas.models.RetrieveSelfEmploymentBsasResponse
import v5.retrieveSelfEmploymentBsas.models.def1.Def1_RetrieveSelfEmploymentBsasResponse
import v5.schema.DownstreamReadable

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveSelfEmploymentBsasSchema extends DownstreamReadable[RetrieveSelfEmploymentBsasResponse]

object RetrieveSelfEmploymentBsasSchema {

  case object Def1 extends RetrieveSelfEmploymentBsasSchema {
    type DownstreamResp = Def1_RetrieveSelfEmploymentBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveSelfEmploymentBsasResponse.reads
  }

  private val defaultSchema = Def1

  def schemaFor(maybeTaxYear: Option[String]): RetrieveSelfEmploymentBsasSchema = {
    maybeTaxYear
      .map(ResolveTaxYear.resolver)
      .flatMap(_.toOption.map(schemaFor))
      .getOrElse(defaultSchema)
  }

  def schemaFor(taxYear: TaxYear): RetrieveSelfEmploymentBsasSchema = {
    if (TaxYear.starting(2023) <= taxYear) {
      Def1
    } else {
      defaultSchema
    }
  }

}
