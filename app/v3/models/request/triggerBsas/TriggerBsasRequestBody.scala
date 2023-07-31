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

package v3.models.request.triggerBsas

import play.api.libs.json.{Json, OWrites, Reads}
import v3.models.domain.TypeOfBusiness
import v3.models.request.AccountingPeriod

/** @param typeOfBusiness
  *   reads "self-employment" etc from the vendor request, writes "01" etc to the downstream request.
  */
case class TriggerBsasRequestBody(accountingPeriod: AccountingPeriod, typeOfBusiness: String, businessId: String)

object TriggerBsasRequestBody {

  implicit val reads: Reads[TriggerBsasRequestBody] = Json.reads[TriggerBsasRequestBody]

  implicit val writes: OWrites[TriggerBsasRequestBody] = (requestBody: TriggerBsasRequestBody) => {
    val typeOfBusiness = TypeOfBusiness.parser(requestBody.typeOfBusiness)
    Json.obj(
      "incomeSourceType"          -> typeOfBusiness.asDownstreamValue,
      "incomeSourceId"            -> requestBody.businessId,
      "accountingPeriodStartDate" -> requestBody.accountingPeriod.startDate,
      "accountingPeriodEndDate"   -> requestBody.accountingPeriod.endDate
    )
  }

}
