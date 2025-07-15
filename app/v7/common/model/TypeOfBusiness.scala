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

sealed trait TypeOfBusiness {
  def asDownstreamValue: String
}

trait HasTypeOfBusiness {
  def typeOfBusiness: TypeOfBusiness
}

//noinspection ScalaStyle
object TypeOfBusiness {
  val parser: PartialFunction[String, TypeOfBusiness] = Enums.parser[TypeOfBusiness](Array())

  case object `self-employment` extends TypeOfBusiness {
    val asDownstreamValue: String = "01"
  }

  case object `uk-property` extends TypeOfBusiness {
    val asDownstreamValue: String = "02"
  }

  case object `foreign-property` extends TypeOfBusiness {
    val asDownstreamValue: String = "15"
  }

  given format: Format[TypeOfBusiness] = Enums.format[TypeOfBusiness](Array())
  

}
