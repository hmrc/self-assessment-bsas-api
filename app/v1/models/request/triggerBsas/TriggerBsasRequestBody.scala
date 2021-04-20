/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.models.request.triggerBsas

import play.api.libs.functional.syntax._
import play.api.libs.json._
import v1.models.domain.TypeOfBusiness
import v1.models.request.AccountingPeriod

case class TriggerBsasRequestBody(accountingPeriod: AccountingPeriod,
                                  typeOfBusiness: TypeOfBusiness,
                                  selfEmploymentId: Option[String])

object TriggerBsasRequestBody {

  implicit val reads: Reads[TriggerBsasRequestBody] = Json.reads[TriggerBsasRequestBody]

  implicit val writes: OWrites[TriggerBsasRequestBody] = (
    (__ \ "incomeSourceType").write[String] and
      (__ \ "incomeSourceId").writeNullable[String] and
      (__ \ "accountingPeriodStartDate").write[String] and
      (__ \ "accountingPeriodEndDate").write[String]
  )(unlift(TriggerBsasRequestBody.unapply(_: TriggerBsasRequestBody).map {
    case (accountingPeriod, typeOfBusiness, selfEmploymentId) =>
      (typeOfBusiness.toIdentifierValue, selfEmploymentId, accountingPeriod.startDate, accountingPeriod.endDate)
  }))
}
