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

package v7.bsas.trigger.def2.model.request

import shared.models.domain.{Nino, TaxYear}
import v7.bsas.trigger.TriggerSchema
import v7.bsas.trigger.model.TriggerBsasRequestData

case class Def2_TriggerBsasRequestData(
    nino: Nino,
    body: Def2_TriggerBsasRequestBody
) extends TriggerBsasRequestData {

  lazy val taxYear: TaxYear = TaxYear.fromIso(body.accountingPeriod.endDate)

  override val schema: TriggerSchema = TriggerSchema.Def2
}
