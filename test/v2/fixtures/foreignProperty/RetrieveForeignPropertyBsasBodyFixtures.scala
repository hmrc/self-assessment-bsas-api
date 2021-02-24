/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.fixtures.foreignProperty

import java.time.LocalDate

import v2.models.domain.TypeOfBusiness
import v2.models.response.retrieveBsas.foreignProperty._
import v2.models.response.retrieveBsas.{AccountingPeriod, Loss, Profit, TotalBsas}

object RetrieveForeignPropertyBsasBodyFixtures {

  val nonFhlExpensesModel: ExpensesBreakdown = ExpensesBreakdown(
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    None
  )

  val fhlExpensesModel: ExpensesBreakdown = ExpensesBreakdown(
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    Some(100.49),
    None,
    None,
    Some(100.49),
    None
  )

  val nonFhlIncomeModel: IncomeBreakdown = IncomeBreakdown(Some(100.49), Some(100.49), Some(100.49))

  val fhlIncomeModel: IncomeBreakdown = IncomeBreakdown(Some(100.49), None, None)

  val total: TotalBsas = TotalBsas(Some(100.49), Some(100.49), Some(100.49), Some(100.49))

  val profit: Profit = Profit(Some(100.49), Some(100))

  val loss: Loss = Loss(Some(100.49), Some(100))

  val nonFhlCountryLevelDetail: CountryLevelDetail = CountryLevelDetail("FRA", total, Some(nonFhlIncomeModel), Some(nonFhlExpensesModel))

  val fhlCountryLevelDetail: CountryLevelDetail = CountryLevelDetail("FRA", total, Some(fhlIncomeModel), Some(fhlExpensesModel))

  val nonFhlBsasDetailModel: BsasDetail =
    BsasDetail(total, Some(profit), Some(loss), Some(nonFhlIncomeModel), Some(nonFhlExpensesModel), Some(Seq(nonFhlCountryLevelDetail)))

  val fhlBsasDetailModel: BsasDetail = BsasDetail(total, Some(profit), Some(loss), Some(fhlIncomeModel), Some(fhlExpensesModel), Some(Seq(fhlCountryLevelDetail)))

  val nonFhlMetaDataModel: Metadata = Metadata(
    TypeOfBusiness.`foreign-property`,
    AccountingPeriod(LocalDate.parse("2020-10-11"), LocalDate.parse("2021-10-10")),
    "2021-22",
    "2019-10-14T11:33:27Z",
    "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    "valid",
    true
  )

  val fhlMetaDataModel: Metadata = Metadata(
    TypeOfBusiness.`foreign-property-fhl-eea`,
    AccountingPeriod(LocalDate.parse("2020-10-11"), LocalDate.parse("2021-10-10")),
    "2021-22",
    "2019-10-14T11:33:27Z",
    "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    "valid",
    true
  )

  val retrieveForeignPropertyBsasResponseModel: RetrieveForeignPropertyBsasResponse =
    RetrieveForeignPropertyBsasResponse(nonFhlMetaDataModel, Some(nonFhlBsasDetailModel))

  val retrieveForeignPropertyFhlEeaBsasResponseModel: RetrieveForeignPropertyBsasResponse =
    RetrieveForeignPropertyBsasResponse(fhlMetaDataModel.copy(typeOfBusiness = TypeOfBusiness.`foreign-property-fhl-eea`), Some(fhlBsasDetailModel))

}