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

sealed trait TypeOfBusiness {
  def toIdentifierValue: String
}

object TypeOfBusiness {

  private val selfEmployment: String = "self-employment"
  private val ukPropertyFhl: String = "uk-property-fhl"
  private val ukPropertyNonFhl: String = "uk-property-non-fhl"
  private val typesOfBusiness: Seq[String] = Seq(selfEmployment, ukPropertyFhl, ukPropertyNonFhl)

  def isTypeOfBusiness(typeOfBusiness: String): Boolean = typeOfBusiness match {
    case _ if typesOfBusiness.contains(typeOfBusiness) => true
    case _ => false
  }

  def apply(typeOfBusiness: String): TypeOfBusiness = typeOfBusiness match {
    case _ if typeOfBusiness == selfEmployment => TypeOfBusiness.SelfEmployment
    case _ if typeOfBusiness == ukPropertyFhl => TypeOfBusiness.UkPropertyFhl
    case _ if typeOfBusiness == ukPropertyNonFhl => TypeOfBusiness.UkPropertyNonFhl
  }

  case object SelfEmployment extends TypeOfBusiness {
    def toIdentifierValue: String = "N/A"
  }

  case object UkPropertyFhl extends TypeOfBusiness {
    def toIdentifierValue: String = "04"
  }

  case object UkPropertyNonFhl extends TypeOfBusiness {
    def toIdentifierValue: String = "02"
  }
}