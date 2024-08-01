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

sealed trait TypeOfBusiness {
  def asDownstreamValue: String
}

object TypeOfBusiness {

  def parser: PartialFunction[String, TypeOfBusiness] = Enums.parser[TypeOfBusiness]

  case object `self-employment` extends TypeOfBusiness {
    val asDownstreamValue: String = "01"
  }

  case object `uk-property` extends TypeOfBusiness {
    val asDownstreamValue: String = "02"
  }

  case object `foreign-property` extends TypeOfBusiness {
    val asDownstreamValue: String = "15"
  }

  implicit val format: Format[TypeOfBusiness] = Enums.format[TypeOfBusiness]
}

sealed trait TypeOfBusinessWithFHL extends TypeOfBusiness

object TypeOfBusinessWithFHL {

  case object `uk-property-fhl` extends TypeOfBusinessWithFHL {
    val asDownstreamValue: String = "04"
  }

  case object `uk-property-non-fhl` extends TypeOfBusinessWithFHL {
    val asDownstreamValue: String = "02"
  }

  case object `foreign-property-fhl-eea` extends TypeOfBusinessWithFHL {
    val asDownstreamValue: String = "03"
  }

  implicit val format: Format[TypeOfBusinessWithFHL] = Enums.format[TypeOfBusinessWithFHL]
}

trait HasTypeOfBusiness {
  def typeOfBusiness: TypeOfBusiness
}

trait HasTypeOfBusinessWithFHL {
  def typeOfBusiness: TypeOfBusinessWithFHL
}
