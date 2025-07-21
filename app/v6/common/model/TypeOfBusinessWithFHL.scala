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

enum TypeOfBusinessWithFHL(val asDownstreamValue: String) {
  case `self-employment`          extends TypeOfBusinessWithFHL("01")
  case `uk-property-fhl`          extends TypeOfBusinessWithFHL("04")
  case `uk-property`              extends TypeOfBusinessWithFHL("02")
  case `foreign-property-fhl-eea` extends TypeOfBusinessWithFHL("03")
  case `foreign-property`         extends TypeOfBusinessWithFHL("15")
}

object TypeOfBusinessWithFHL {
  val parser: PartialFunction[String, TypeOfBusinessWithFHL] = Enums.parser(values)

  given Format[TypeOfBusinessWithFHL] = Enums.format(values)
}
