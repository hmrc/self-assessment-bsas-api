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

package v6.selfEmploymentBsas.retrieve

import cats.data.Validated
import cats.data.Validated.Valid
import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.errors.MtdError
import shared.schema.DownstreamReadable
import v6.selfEmploymentBsas.retrieve.def1.model.response.Def1_RetrieveSelfEmploymentBsasResponse
import v6.selfEmploymentBsas.retrieve.model.response.RetrieveSelfEmploymentBsasResponse

sealed trait RetrieveSelfEmploymentBsasSchema extends DownstreamReadable[RetrieveSelfEmploymentBsasResponse]

object RetrieveSelfEmploymentBsasSchema {

  case object Def1 extends RetrieveSelfEmploymentBsasSchema {
    type DownstreamResp = Def1_RetrieveSelfEmploymentBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveSelfEmploymentBsasResponse.reads
  }

  def schemaFor(taxYearString: String): Validated[Seq[MtdError], RetrieveSelfEmploymentBsasSchema] = {
    ResolveTaxYear(taxYearString) andThen (_ => schemaFor())
  }

  def schemaFor(): Validated[Seq[MtdError], RetrieveSelfEmploymentBsasSchema] = {
    Valid(Def1)
  }

}
