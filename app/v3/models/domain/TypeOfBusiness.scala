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

package v3.models.domain

import play.api.libs.json.Format
import utils.enums.Enums

sealed trait TypeOfBusiness {
  def toIdentifierValue: String
}

trait HasTypeOfBusiness{
  def typeOfBusiness: TypeOfBusiness
}

//noinspection ScalaStyle
object TypeOfBusiness {

  case object `self-employment` extends TypeOfBusiness {
    override def toIdentifierValue: String = "01"
  }

  case object `uk-property-fhl` extends TypeOfBusiness {
    override def toIdentifierValue: String = "04"
  }

  case object `uk-property-non-fhl` extends TypeOfBusiness {
    override def toIdentifierValue: String = "02"
  }

  case object `foreign-property-fhl-eea` extends TypeOfBusiness {
    override def toIdentifierValue: String = "03"
  }

  case object `foreign-property` extends TypeOfBusiness {
    override def toIdentifierValue: String = "15"
  }

  implicit val format: Format[TypeOfBusiness] = Enums.format[TypeOfBusiness]
  val parser: PartialFunction[String, TypeOfBusiness] = Enums.parser[TypeOfBusiness]
}