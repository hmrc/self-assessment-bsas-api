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

package v7.bsas.trigger.def1.model.request

import play.api.libs.json.{Json, OWrites, Reads}
import v7.bsas.trigger.model.TriggerBsasRequestBody
import v7.common.model.TypeOfBusinessWithFHL

/** @param typeOfBusiness
  *   reads "self-employment" etc from the vendor request, writes "01" etc to the downstream request.
  */
case class Def1_TriggerBsasRequestBody(
    accountingPeriod: AccountingPeriod,
    typeOfBusiness: String,
    businessId: String
) extends TriggerBsasRequestBody

object Def1_TriggerBsasRequestBody {

  implicit val reads: Reads[Def1_TriggerBsasRequestBody] = Json.reads[Def1_TriggerBsasRequestBody]

  implicit val writes: OWrites[Def1_TriggerBsasRequestBody] = (requestBody: Def1_TriggerBsasRequestBody) => {
    val typeOfBusiness = TypeOfBusinessWithFHL.parser(requestBody.typeOfBusiness)
    Json.obj(
      "incomeSourceType"          -> typeOfBusiness.asDownstreamValue,
      "incomeSourceId"            -> requestBody.businessId,
      "accountingPeriodStartDate" -> requestBody.accountingPeriod.startDate,
      "accountingPeriodEndDate"   -> requestBody.accountingPeriod.endDate
    )
  }

}
