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

import play.api.libs.json.Format
import shared.utils.enums.Enums

//sealed trait TypeOfBusiness {
//  def asDownstreamValue: String
//}

trait HasTypeOfBusiness {
  def typeOfBusiness: TypeOfBusiness
}

enum TypeOfBusiness(val valasDownstreamValue: String) {
  case `self-employment` extends TypeOfBusiness("01")
  case `uk-property-fhl` extends TypeOfBusiness("04")
  case `uk-property-non-fhl` extends TypeOfBusiness("02")
  case `foreign-property-fhl-eea` extends TypeOfBusiness("03")
  case `foreign-property` extends TypeOfBusiness("15")
}

object TypeOfBusiness {
  val parser: PartialFunction[String, TypeOfBusiness] = Enums.parser(values)

  given Format[TypeOfBusiness] = Enums.format(values)
}

//noinspection ScalaStyle
//object TypeOfBusiness {
//  val parser: PartialFunction[String, TypeOfBusiness] = Enums.parser[TypeOfBusiness](Array())
//
//  case object `self-employment` extends TypeOfBusiness {
//    val asDownstreamValue: String = "01"
//  }
//
//  case object `uk-property-fhl` extends TypeOfBusiness {
//    val asDownstreamValue: String = "04"
//  }
//
//  case object `uk-property-non-fhl` extends TypeOfBusiness {
//    val asDownstreamValue: String = "02"
//  }
//
//  case object `foreign-property-fhl-eea` extends TypeOfBusiness {
//    val asDownstreamValue: String = "03"
//  }
//
//  given format: Format[TypeOfBusiness] = Enums.format[TypeOfBusiness](Array())
//
//  case object `foreign-property` extends TypeOfBusiness {
//    val asDownstreamValue: String = "15"
//  }
//
//}
