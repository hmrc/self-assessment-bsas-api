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
import v1.controllers.requestParsers.validators.TriggerBSASValidator
import v1.models.domain.{BSAS, TypeOfBusiness}
import v1.models.request.AccountingPeriod
import v1.models.request.triggerBsas.{TriggerBsasRawData, TriggerBsasRequest, TriggerBsasRequestBody}

class TriggerBSASRequestParser @Inject()(val validator: TriggerBSASValidator) extends RequestParser[TriggerBsasRawData, TriggerBsasRequest] {

  override protected def requestFor(data: TriggerBsasRawData): TriggerBsasRequest = {
    val request = data.body.json.as[BSAS]

    val requestBody = TriggerBsasRequestBody(AccountingPeriod(request.startDate, request.endDate),
      TypeOfBusiness.parser(request.typeOfBusiness),
      request.selfEmploymentId)

    TriggerBsasRequest(Nino(data.nino), requestBody)
  }
}