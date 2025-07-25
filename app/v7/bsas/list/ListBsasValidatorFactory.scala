/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.bsas.list

import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.Validator
import v7.bsas.list.ListBsasSchema.*
import v7.bsas.list.def1.Def1_ListBsasValidator
import v7.bsas.list.def2.Def2_ListBsasValidator
import v7.bsas.list.model.request.ListBsasRequestData

import javax.inject.Singleton

@Singleton
class ListBsasValidatorFactory {

  def validator(
      nino: String,
      taxYear: String,
      typeOfBusiness: Option[String],
      businessId: Option[String]
  ): Validator[ListBsasRequestData] = {

    ListBsasSchema.schemaFor(taxYear) match {
      case Valid(Def1)     => new Def1_ListBsasValidator(nino, taxYear, typeOfBusiness, businessId)
      case Valid(Def2)     => new Def2_ListBsasValidator(nino, taxYear, typeOfBusiness, businessId)
      case Invalid(errors) => Validator.returningErrors(errors)
    }
  }

}
