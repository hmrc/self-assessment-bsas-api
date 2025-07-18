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

import play.api.libs.json
import shared.utils.enums.Enums


trait HasIncomeSourceType {
  def incomeSourceType: String
}

enum IncomeSourceType {
  case `01`, `02`, `03`, `04`, `15`

  def toTypeOfBusiness: TypeOfBusiness = this match{
    case `01` => TypeOfBusiness.`self-employment`
    case `02` => TypeOfBusiness.`uk-property`
    case `15` => TypeOfBusiness.`foreign-property`

  }
}


object IncomeSourceType {
  given json.Format[IncomeSourceType] = Enums.format(values)
}