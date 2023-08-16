/*
 * Copyright 2023 HM Revenue & Customs
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

package v4.models.response.retrieveBsas.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class SummaryCalculationAccountingAdjustments(
                                                    basisAdjustment: Option[BigDecimal],
                                                    overlapReliefUsed: Option[BigDecimal],
                                                    accountingAdjustment: Option[BigDecimal],
                                                    averagingAdjustment: Option[BigDecimal],
                                                  )

object SummaryCalculationAccountingAdjustments {
  implicit val reads: Reads[SummaryCalculationAccountingAdjustments] = (
    (JsPath \ "basisAdjustment").readNullable[BigDecimal] and
      (JsPath \ "overlapReliefUsed").readNullable[BigDecimal] and
      (JsPath \ "accountingAdjustment").readNullable[BigDecimal] and
      (JsPath \ "averagingAdjustment").readNullable[BigDecimal]
    ) (SummaryCalculationAccountingAdjustments.apply _)

  implicit val writes: OWrites[SummaryCalculationAccountingAdjustments] = Json.writes[SummaryCalculationAccountingAdjustments]
}
