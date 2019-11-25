/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.libs.json.{JsObject, Json, OFormat, OWrites, Reads}
import v1.models.domain.TypeOfBusiness

case class TriggerBsasRequestBody(accountingPeriod: AccountingPeriodRequest,
                                  typeOfBusiness: TypeOfBusiness,
                                  selfEmploymentId: Option[String])

object TriggerBsasRequestBody {

  implicit val reads: Reads[TriggerBsasRequestBody] = Json.reads[TriggerBsasRequestBody]

  implicit val writes: OWrites[TriggerBsasRequestBody] = new OWrites[TriggerBsasRequestBody] {
    def writes(triggerBsasRequestBody: TriggerBsasRequestBody): JsObject = {
      if (triggerBsasRequestBody.typeOfBusiness == TypeOfBusiness.`self-employment`) {
        Json.obj(
          "incomeSourceIdentifier" -> "incomeSourceId",
          "identifierValue" -> triggerBsasRequestBody.selfEmploymentId,
          "accountingPeriodStartDate" -> triggerBsasRequestBody.accountingPeriod.startDate,
          "accountingPeriodEndDate" -> triggerBsasRequestBody.accountingPeriod.endDate
        )
      } else {
        Json.obj(
          "incomeSourceIdentifier" -> "incomeSourceType",
          "identifierValue" -> triggerBsasRequestBody.typeOfBusiness.toIdentifierValue,
          "accountingPeriodStartDate" -> triggerBsasRequestBody.accountingPeriod.startDate,
          "accountingPeriodEndDate" -> triggerBsasRequestBody.accountingPeriod.endDate
        )
      }
    }
  }
}
