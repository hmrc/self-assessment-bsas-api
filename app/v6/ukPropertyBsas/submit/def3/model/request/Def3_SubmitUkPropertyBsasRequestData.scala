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

package v6.ukPropertyBsas.submit.def3.model.request

import shared.models.domain.{CalculationId, Nino, TaxYear}
import v6.ukPropertyBsas.submit.SubmitUkPropertyBsasSchema
import v6.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestData

case class Def3_SubmitUkPropertyBsasRequestData(
    nino: Nino,
    calculationId: CalculationId,
    taxYear: Option[TaxYear],
    body: Def3_SubmitUkPropertyBsasRequestBody
) extends SubmitUkPropertyBsasRequestData {

  override val schema: SubmitUkPropertyBsasSchema = SubmitUkPropertyBsasSchema.Def3

}
