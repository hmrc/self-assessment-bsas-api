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

package v2.controllers.requestParsers

import javax.inject.Inject

import domain.Nino
import v2.controllers.requestParsers.validators.TriggerBSASValidator
import v2.models.request.triggerBsas.{TriggerBsasRawData, TriggerBsasRequest, TriggerBsasRequestBody}

class TriggerBsasRequestParser @Inject()(val validator: TriggerBSASValidator) extends RequestParser[TriggerBsasRawData, TriggerBsasRequest] {

  override protected def requestFor(data: TriggerBsasRawData): TriggerBsasRequest = {
    val requestBody = data.body.json.as[TriggerBsasRequestBody]

    TriggerBsasRequest(Nino(data.nino), requestBody)
  }
}
