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

package v1.controllers.requestParsers

import javax.inject.Inject
import uk.gov.hmrc.domain.Nino
import v1.controllers.requestParsers.validators.RetrieveUkPropertyValidator
import v1.models.request.{RetrievePropertyBsasRawData, RetrievePropertyBsasRequestData}

class RetrieveUkPropertyRequestParser @Inject()(val validator: RetrieveUkPropertyValidator)
  extends RequestParser[RetrievePropertyBsasRawData, RetrievePropertyBsasRequestData] {

  val ADJUSTED_SUMMARY = "03"
  val ADJUSTABLE_SUMMARY = "01"

  def toDesAdjustedStatus(s: String): String = if(s.toBoolean) ADJUSTED_SUMMARY else ADJUSTABLE_SUMMARY

  override protected def requestFor(data: RetrievePropertyBsasRawData): RetrievePropertyBsasRequestData = {
    RetrievePropertyBsasRequestData(Nino(data.nino), data.bsasId, data.adjustedStatus.map(toDesAdjustedStatus))
  }

}