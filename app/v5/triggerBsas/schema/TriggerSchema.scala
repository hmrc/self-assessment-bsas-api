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

package v5.triggerBsas.schema

import play.api.libs.json.{JsValue, Reads}
import shared.models.domain.TaxYear
import v5.schema.DownstreamReadable
import v5.triggerBsas.models.TriggerBsasResponse
import v5.triggerBsas.models.def1.Def1_TriggerBsasResponse

import java.time.LocalDate
import scala.math.Ordered.orderingToOrdered

sealed trait TriggerSchema extends DownstreamReadable[TriggerBsasResponse]

object TriggerSchema {

  case object Def1 extends TriggerSchema {
    type DownstreamResp = Def1_TriggerBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def1_TriggerBsasResponse.reads
  }

  private val defaultSchema = Def1

  def schemaFor(request: JsValue): TriggerSchema =
    (request \ "accountingPeriod" \ "endDate")
      .asOpt[LocalDate]
      .map(TaxYear.containing)
      .map(schemaFor)
      .getOrElse(defaultSchema)

  def schemaFor(taxYear: TaxYear): TriggerSchema = {
    if (TaxYear.starting(2023) <= taxYear) {
      Def1
    } else {
      defaultSchema
    }
  }

}
