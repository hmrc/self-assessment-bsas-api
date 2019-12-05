/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.domain

import play.api.libs.json.Format
import utils.enums.Enums

sealed trait TypeOfBusiness {
  def toIdentifierValue: String
}

//noinspection ScalaStyle
object TypeOfBusiness {

  val ukPropertyFHL: String = "uk-property-fhl"
  val ukPropertyNonFHL: String = "uk-property-non-fhl"
  val selfEmployment: String = "self-employment"

  case object `self-employment` extends TypeOfBusiness {
    override def toIdentifierValue: String = "01"
  }

  case object `uk-property-fhl` extends TypeOfBusiness {
    override def toIdentifierValue: String = "04"
  }

  case object `uk-property-non-fhl` extends TypeOfBusiness {
    override def toIdentifierValue: String = "02"
  }

  implicit val format: Format[TypeOfBusiness] = Enums.format[TypeOfBusiness]
  val parser: PartialFunction[String, TypeOfBusiness] = Enums.parser[TypeOfBusiness]
}