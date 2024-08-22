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

package v6.bsas.trigger

import cats.data.Validated
import cats.data.Validated.Invalid
import play.api.libs.json.{JsValue, Reads}
import shared.controllers.validators.resolvers.ResolveIsoDate
import shared.models.domain.TaxYear
import shared.models.errors.{EndDateFormatError, MtdError, RuleIncorrectOrEmptyBodyError}
import shared.schema.DownstreamReadable
import v6.bsas.trigger.def1.model.response.Def1_TriggerBsasResponse
import v6.bsas.trigger.def2.model.response.Def2_TriggerBsasResponse
import v6.bsas.trigger.model.TriggerBsasResponse

import scala.math.Ordered.orderingToOrdered

sealed trait TriggerSchema extends DownstreamReadable[TriggerBsasResponse]

object TriggerSchema {

  case object Def1 extends TriggerSchema {
    type DownstreamResp = Def1_TriggerBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def1_TriggerBsasResponse.reads
  }

  case object Def2 extends TriggerSchema {
    type DownstreamResp = Def2_TriggerBsasResponse
    val connectorReads: Reads[DownstreamResp] = Def2_TriggerBsasResponse.reads
  }

  def schemaFor(request: JsValue): Validated[Seq[MtdError], TriggerSchema] = {
    val maybeDateString = (request \ "accountingPeriod" \ "endDate").asOpt[String]

    maybeDateString match {
      case None             => Invalid(Seq(RuleIncorrectOrEmptyBodyError.withPath("/accountingPeriod/endDate")))
      case Some(dateString) => ResolveIsoDate(EndDateFormatError)(dateString).map(date => schemaFor(TaxYear.containing(date)))
    }
  }

  def schemaFor(taxYear: TaxYear): TriggerSchema = {
    if (taxYear <= TaxYear.starting(2024)) Def1
    else Def2
  }

}
