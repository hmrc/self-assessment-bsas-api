/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.fixtures.foreignProperty

import v3.models.domain.TypeOfBusiness
import v3.models.response.retrieveBsas.foreignProperty._

object RetrieveForeignPropertyBsasBodyFixtures {

  val metaDataModel: Metadata = Metadata(
    "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    "2020-12-05T16:19:44Z",
    Some("2020-12-05T16:19:44Z"),
    "AA999999A",
    "2019-20",
    "valid"
  )

  val inputsModel: Inputs = Inputs(
    "000000000000210",
    TypeOfBusiness.`foreign-property-fhl-eea`,
    Some("Business Name"),
    "2019-04-06",
    "2020-04-05",
    "MTD-SA",
    Seq(SubmissionPeriods("617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4","2019-04-06","2020-04-05","2019-02-15T09:35:04.843Z"))
  )

  val submissionPeriodModel: SubmissionPeriods = SubmissionPeriods(
    "617f3a7a-db8e-11e9-8a34-2a2ae2dbeed4",
    "2019-04-06",
    "2020-04-05",
    "2019-02-15T09:35:04.843Z"
  )

  val incomeModel = Income(
    Some(0.12),
    Some(0.12),
    Some(0.12)
  )


  val expensesModel = Expenses(
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12)
  )

  val additionsModel = Additions(
    Some(0.12),
    Some(0.12)
  )

  val deductionsModel = Deductions(
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(0.12)
  )

  val countryLevelDetailModel = CountryLevelDetail(
    Some("CYM"),
    Some(0.12),
    Some(Income(Some(0.12), Some(0.12), Some(0.12))),
    Some(0.12),
    Some(Expenses(Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12))),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(Additions(Some(0.12), Some(0.12))),
    Some(0.12),
    Some(Deductions(Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12))),
    Some(1),
    Some(1)
  )

  val adjustableSummaryCalculationModel = AdjustableSummaryCalculation(
    Some(0.12),
    Some(Income(Some(0.12), Some(0.12), Some(0.12))),
    Some(0.12),
    Some(Expenses(Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12))),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(Additions(Some(0.12), Some(0.12))),
    Some(0.12),
    Some(Deductions(Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12))),
    Some(1),
    Some(1),
    Some(Seq(CountryLevelDetail(
    Some("CYM"),
    Some(0.12),
    Some(Income(Some(0.12), Some(0.12), Some(0.12))),
    Some(0.12),
    Some(Expenses(Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12))),
    Some(0.12),
    Some(0.12),
    Some(0.12),
    Some(Additions(Some(0.12), Some(0.12))),
    Some(0.12),
    Some(Deductions(Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12))),
    Some(1),
    Some(1)
  ))))

  val adjustmentsModel = Adjustments(
    None,
    Some(Income(Some(0.12), Some(0.12), Some(0.12))),
    Some(Expenses(Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12))))

  val adjustmentsArrayModel = Adjustments(
    Some(Seq(CountryLevelDetail(
      Some("CYM"),
      None,
      Some(Income(Some(0.12), Some(0.12), Some(0.12))),
      None,
      Some(Expenses(Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12), Some(0.12))),
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None
    ))),
    None,
    None)

  val retrieveBsasResponseModel = RetrieveForeignPropertyBSASResponse(
    metadata = metaDataModel,
    inputs = inputsModel,
    adjustableSummaryCalculation = adjustableSummaryCalculationModel,
    adjustments = Some(adjustmentsModel),
    adjustedSummaryCalculation = adjustableSummaryCalculationModel
  )
}