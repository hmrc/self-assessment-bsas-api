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

package v2.models.response.listBsas

import play.api.libs.functional.syntax._
import play.api.libs.json._
import v2.models.domain.{IncomeSourceType, TypeOfBusiness}
import v2.models.request.AccountingPeriod

case class BusinessSourceSummary[I](typeOfBusiness: TypeOfBusiness,
                                    businessId: Option[String],
                                    accountingPeriod: AccountingPeriod,
                                    bsasEntries: Seq[I])

object BusinessSourceSummary {

  implicit def reads[I: Reads]: Reads[BusinessSourceSummary[I]] =
    (
      (JsPath \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness) and
        (JsPath \ "incomeSourceId").readNullable[String] and
        JsPath.read[AccountingPeriod](AccountingPeriod.desReads) and
        (JsPath \ "ascCalculations").read[Seq[I]]
    )(BusinessSourceSummary.apply(_, _, _, _))

  implicit def writes[I: Writes]: OWrites[BusinessSourceSummary[I]] = Json.writes[BusinessSourceSummary[I]]
}
