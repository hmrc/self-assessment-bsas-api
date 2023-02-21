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

package v3.controllers.requestParsers

import api.controllers.RequestParser
import api.models.domain.{Nino, TaxYear}
import v3.controllers.requestParsers.validators.SubmitUkPropertyBsasValidator
import v3.models.request.submitBsas.ukProperty.{SubmitUKPropertyBsasRequestBody, SubmitUkPropertyBsasRawData, SubmitUkPropertyBsasRequestData}

import javax.inject.Inject

class SubmitUkPropertyBsasRequestParser @Inject()(val validator: SubmitUkPropertyBsasValidator)
    extends RequestParser[SubmitUkPropertyBsasRawData, SubmitUkPropertyBsasRequestData] {

  override protected def requestFor(data: SubmitUkPropertyBsasRawData): SubmitUkPropertyBsasRequestData = {

    val taxYear     = data.taxYear.map(TaxYear.fromMtd)
    val requestBody = data.body.as[SubmitUKPropertyBsasRequestBody]

    SubmitUkPropertyBsasRequestData(Nino(data.nino), data.calculationId, requestBody, taxYear)
  }
}
