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

import play.api.libs.json.Format
import shared.utils.enums.Enums

trait HasTypeOfBusiness {
  def typeOfBusiness: TypeOfBusiness
}

enum TypeOfBusiness(val asDownstreamValue: String) {
  case `self-employment`  extends TypeOfBusiness("01")
  case `uk-property`      extends TypeOfBusiness("02")
  case `foreign-property` extends TypeOfBusiness("15")
}

object TypeOfBusiness {
  val parser: PartialFunction[String, TypeOfBusiness] = Enums.parser(values)

  given Format[TypeOfBusiness] = Enums.format(values)
}
