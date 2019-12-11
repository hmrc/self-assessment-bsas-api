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

package v1.models.response.retrieveBsas.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class RetrieveSelfEmploymentBsasResponse (metadata: MetadataSelfEmployment,
                                               bsas: Option[BsasDetailSelfEmployment])

object RetrieveSelfEmploymentBsasResponse {

  implicit val reads: Reads[RetrieveSelfEmploymentBsasResponse] = (
    JsPath.read[MetadataSelfEmployment] and
      (JsPath \ "adjustedSummaryCalculation").readNullable[JsObject].flatMap{
        case Some(_) => (JsPath \ "adjustedSummaryCalculation").readNullable[BsasDetailSelfEmployment]
        case _ => (JsPath \ "adjustableSummaryCalculation").readNullable[BsasDetailSelfEmployment]
      }
    )(RetrieveSelfEmploymentBsasResponse.apply _)

  implicit val writes: OWrites[RetrieveSelfEmploymentBsasResponse] = Json.writes[RetrieveSelfEmploymentBsasResponse]
}