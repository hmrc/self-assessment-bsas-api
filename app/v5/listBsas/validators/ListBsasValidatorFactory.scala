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

package v5.listBsas.validators

import shared.controllers.validators.Validator
import v5.listBsas.models.ListBsasRequestData
import v5.listBsas.schema.ListBsasSchema
import v5.listBsas.validators.def1.Def1_ListBsasValidator

import javax.inject.Singleton

@Singleton
class ListBsasValidatorFactory {

  def validator(nino: String,
                taxYear: Option[String],
                typeOfBusiness: Option[String],
                businessId: Option[String],
                schema: ListBsasSchema): Validator[ListBsasRequestData] =
    schema match {
      case ListBsasSchema.Def1 => new Def1_ListBsasValidator(nino, taxYear, typeOfBusiness, businessId)
    }

}


