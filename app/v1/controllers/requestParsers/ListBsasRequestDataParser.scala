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
import v1.controllers.requestParsers.validators.ListBsasValidator
import v1.controllers.requestParsers.validators.validations.TypeOfBusinessValidation
import v1.models.request.{DesTaxYear, ListBsasRawData, ListBsasRequest}

class ListBsasRequestDataParser @Inject()(val validator: ListBsasValidator)
  extends RequestParser[ListBsasRawData, ListBsasRequest] {

  override protected def requestFor(data: ListBsasRawData): ListBsasRequest = {

    val incomeSourceIdentifier = (data.selfEmploymentId, data.typeOfBusiness) match {
      case (Some(_), _) => Some("incomeSourceId")
      case (None, Some(_)) => Some("incomeSourceType")
      case (None, None) => None
    }
    val identifierValue: Option[String] = if(data.selfEmploymentId.isDefined) data.selfEmploymentId else data.typeOfBusiness match {
      case Some(TypeOfBusinessValidation.selfEmployed) => Some("01")
      case Some(TypeOfBusinessValidation.ukPropertyFHL) => Some("04")
      case Some(TypeOfBusinessValidation.ukPropertyNonFHL) => Some("02")
      case _ => None
    }



    ListBsasRequest(Nino(data.nino), DesTaxYear.fromMtd(data.taxYear), incomeSourceIdentifier, identifierValue)
  }
}
