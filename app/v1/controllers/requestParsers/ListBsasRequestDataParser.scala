/*
 * Copyright 2020 HM Revenue & Customs
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
import utils.{CurrentDateProvider, DateUtils}
import v1.controllers.requestParsers.validators.ListBsasValidator
import v1.controllers.requestParsers.validators.validations.TypeOfBusinessValidation
import v1.models.domain.TypeOfBusiness
import v1.models.request.{ListBsasRawData, ListBsasRequest}

class ListBsasRequestDataParser @Inject()(val validator: ListBsasValidator,
                                          val currentDateProvider: CurrentDateProvider)
  extends RequestParser[ListBsasRawData, ListBsasRequest] {

  override protected def requestFor(data: ListBsasRawData): ListBsasRequest = {

    val incomeSourceIdentifier = (data.selfEmploymentId, data.typeOfBusiness) match {
      case (Some(_), _) => Some("incomeSourceId")
      case (None, Some(_)) => Some("incomeSourceType")
      case (None, None) => None
    }

    val identifierValue: Option[String] = if(data.selfEmploymentId.isDefined) data.selfEmploymentId else data.typeOfBusiness.map(TypeOfBusiness.parser).map {
      case TypeOfBusiness.`self-employment` => TypeOfBusiness.`self-employment`.toIdentifierValue
      case TypeOfBusiness.`uk-property-fhl` => TypeOfBusiness.`uk-property-fhl`.toIdentifierValue
      case TypeOfBusiness.`uk-property-non-fhl` => TypeOfBusiness.`uk-property-non-fhl`.toIdentifierValue
    }

    ListBsasRequest(
      nino = Nino(data.nino),
      taxYear = data.taxYear.fold(DateUtils.getDesTaxYear(currentDateProvider.getCurrentDate()))(DateUtils.getDesTaxYear),
      incomeSourceIdentifier = incomeSourceIdentifier,
      identifierValue = identifierValue
    )
  }
}
