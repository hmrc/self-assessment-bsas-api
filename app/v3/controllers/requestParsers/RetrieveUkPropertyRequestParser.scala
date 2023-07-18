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

import api.controllers.requestParsers.RequestParser
import api.models.domain.{Nino, TaxYear}
import v3.controllers.requestParsers.validators.RetrieveUkPropertyValidator
import v3.models.request.retrieveBsas.ukProperty.{RetrieveUkPropertyBsasRawData, RetrieveUkPropertyBsasRequestData}

import javax.inject.Inject

class RetrieveUkPropertyRequestParser @Inject()(val validator: RetrieveUkPropertyValidator)
  extends RequestParser[RetrieveUkPropertyBsasRawData, RetrieveUkPropertyBsasRequestData] {

  override protected def requestFor(data: RetrieveUkPropertyBsasRawData): RetrieveUkPropertyBsasRequestData = {
    val taxYear = data.taxYear.map(TaxYear.fromMtd)
    RetrieveUkPropertyBsasRequestData(Nino(data.nino), data.calculationId, taxYear)
  }
}
