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

package v5.common.model

import play.api.libs.json
import play.api.libs.json.Format
import shared.utils.enums.Enums

enum IncomeSourceType(val toTypeOfBusiness: TypeOfBusiness) {
  case `01` extends IncomeSourceType(TypeOfBusiness.`self-employment`)
  case `02` extends IncomeSourceType(TypeOfBusiness.`uk-property-non-fhl`)
  case `03` extends IncomeSourceType(TypeOfBusiness.`foreign-property-fhl-eea`)
  case `04` extends IncomeSourceType(TypeOfBusiness.`uk-property-fhl`)
  case `15` extends IncomeSourceType(TypeOfBusiness.`foreign-property`)
}

object IncomeSourceType {

  given Format[IncomeSourceType] = Enums.format(values)
}
