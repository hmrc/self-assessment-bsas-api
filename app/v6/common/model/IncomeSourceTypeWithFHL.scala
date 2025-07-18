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

package v6.common.model

import play.api.libs.json.Format
import shared.utils.enums.Enums

enum IncomeSourceTypeWithFHL(val toTypeOfBusiness: TypeOfBusinessWithFHL) {
  case `01` extends IncomeSourceTypeWithFHL(TypeOfBusinessWithFHL.`self-employment`)
  case `02` extends IncomeSourceTypeWithFHL(TypeOfBusinessWithFHL.`uk-property`)
  case `03` extends IncomeSourceTypeWithFHL(TypeOfBusinessWithFHL.`foreign-property-fhl-eea`)
  case `04` extends IncomeSourceTypeWithFHL(TypeOfBusinessWithFHL.`uk-property-fhl`)
  case `15` extends IncomeSourceTypeWithFHL(TypeOfBusinessWithFHL.`foreign-property`)
}

object IncomeSourceTypeWithFHL {

  given Format[IncomeSourceTypeWithFHL] = Enums.format(values)
}
