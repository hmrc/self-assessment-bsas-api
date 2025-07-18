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

package v7.common.model

import play.api.libs.json
import shared.utils.enums.Enums


enum IncomeSourceTypeWithFHL {
  case `01`, `02`, `03`, `04`, `15`

  def toTypeOfBusiness: TypeOfBusinessWithFHL = this match{
    case `01` => TypeOfBusinessWithFHL.`self-employment`
    case `02` => TypeOfBusinessWithFHL.`uk-property`
    case `03` => TypeOfBusinessWithFHL.`foreign-property-fhl-eea`
    case `04` => TypeOfBusinessWithFHL.`uk-property-fhl`
    case `15` => TypeOfBusinessWithFHL.`foreign-property`

  }
}


object IncomeSourceTypeWithFHL {
  given json.Format[IncomeSourceTypeWithFHL] = Enums.format(values)
}