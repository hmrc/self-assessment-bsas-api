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

sealed trait IncomeSourceType {
  def toTypeOfBusiness: TypeOfBusiness
}

trait HasIncomeSourceType {
  def incomeSourceType: String
}

//noinspection ScalaStyle
object IncomeSourceType {

  case object `01` extends IncomeSourceType {
    override def toTypeOfBusiness: TypeOfBusiness = TypeOfBusiness.`self-employment`
  }

  case object `02` extends IncomeSourceType {
    override def toTypeOfBusiness: TypeOfBusiness = TypeOfBusiness.`uk-property`
  }

  case object `15` extends IncomeSourceType {
    override def toTypeOfBusiness: TypeOfBusiness = TypeOfBusiness.`foreign-property`
  }

  implicit val format: json.Format[IncomeSourceType] = Enums.format[IncomeSourceType]
}

sealed trait IncomeSourceTypeWithFHL extends IncomeSourceType {
  def toTypeOfBusiness: TypeOfBusinessWithFHL
}

trait HasIncomeSourceTypeWithFHL {
  def incomeSourceType: String
}

//noinspection ScalaStyle
object IncomeSourceTypeWithFHL {

  case object `02` extends IncomeSourceTypeWithFHL {
    override def toTypeOfBusiness: TypeOfBusinessWithFHL = TypeOfBusinessWithFHL.`uk-property-non-fhl`
  }

  case object `03` extends IncomeSourceTypeWithFHL {
    override def toTypeOfBusiness: TypeOfBusinessWithFHL = TypeOfBusinessWithFHL.`foreign-property-fhl-eea`
  }

  case object `04` extends IncomeSourceTypeWithFHL {
    override def toTypeOfBusiness: TypeOfBusinessWithFHL = TypeOfBusinessWithFHL.`uk-property-fhl`
  }

  implicit val format: json.Format[IncomeSourceTypeWithFHL] = Enums.format[IncomeSourceTypeWithFHL]
}
