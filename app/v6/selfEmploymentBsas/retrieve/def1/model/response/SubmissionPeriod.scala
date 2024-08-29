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

package v6.selfEmploymentBsas.retrieve.def1.model.response

import play.api.libs.json._

case class SubmissionPeriod(
    periodId: Option[String],
    submissionId: Option[String],
    startDate: String,
    endDate: String,
    receivedDateTime: String
)

object SubmissionPeriod {

  private val periodIdRegex = "^[0-9]{16}$"

  implicit val reads: Reads[SubmissionPeriod] = (json: JsValue) => {
    for {
      startDate        <- (json \ "startDate").validate[String]
      endDate          <- (json \ "endDate").validate[String]
      receivedDateTime <- (json \ "receivedDateTime").validate[String]
      id               <- (json \ "periodId").validate[String]
      (periodId, submissionId) = {
        if (id.matches(periodIdRegex)) {
          (Some(id), None)
        } else {
          (None, Some(s"${startDate}_$endDate"))
        }
      }
    } yield {
      SubmissionPeriod(
        periodId = periodId,
        submissionId = submissionId,
        startDate = startDate,
        endDate = endDate,
        receivedDateTime = receivedDateTime)
    }
  }

  implicit val writes: OWrites[SubmissionPeriod] = Json.writes[SubmissionPeriod]
}
