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

  def isTypeOfBusiness(typeOfBusiness: String): Boolean = typeOfBusiness match {
    case "self-employment" => true
    case "uk-property-fhl" => true
    case "uk-property-non-fhl" => true
    case _ => false
  }

  def apply(typeOfBusiness: String): TypeOfBusiness = typeOfBusiness match {
    case "self-employment" => TypeOfBusiness.SelfEmployment
    case "uk-property-fhl" => TypeOfBusiness.UkPropertyFhl
    case "uk-property-non-fhl" => TypeOfBusiness.UkPropertyNonFhl
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





