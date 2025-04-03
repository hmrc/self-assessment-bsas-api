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

package shared.models.errors

import play.api.libs.json.{Reads, __}

case class DownstreamErrorCode(code: String) {
  def toMtd(httpStatus: Int): MtdError = MtdError(code = code, message = "", httpStatus = httpStatus)
}

object DownstreamErrorCode {
  implicit val reads: Reads[DownstreamErrorCode] = (__ \ "code").read[String].map(DownstreamErrorCode(_))
}

case class HipDownstreamErrorCode(code: String) {
  def toMtd(httpStatus: Int): MtdError           = MtdError(code = code, message = "", httpStatus = httpStatus)
  def toDownstreamErrorCode: DownstreamErrorCode = DownstreamErrorCode(code)
}

object HipDownstreamErrorCode {
  implicit val reads: Reads[HipDownstreamErrorCode] = (__ \ "type").read[String].map(HipDownstreamErrorCode(_))
}

sealed trait DownstreamError

case class DownstreamErrors(errors: Seq[DownstreamErrorCode]) extends DownstreamError

object DownstreamErrors {
  def single(error: DownstreamErrorCode): DownstreamErrors = DownstreamErrors(List(error))
}

case class OutboundError(error: MtdError, errors: Option[Seq[MtdError]] = None) extends DownstreamError
