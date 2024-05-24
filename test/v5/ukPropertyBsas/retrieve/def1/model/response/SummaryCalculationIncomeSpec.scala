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

package v5.ukPropertyBsas.retrieve.def1.model.response

import shared.UnitSpec
import shared.models.utils.JsonErrorValidators
import v5.models.RoundTripTest
import v5.ukPropertyBsas.retrieve.def1.model.response.RetrieveUkPropertyBsasFixtures._

class SummaryCalculationIncomeSpec extends UnitSpec with JsonErrorValidators with RoundTripTest {

  import SummaryCalculationIncome._

  testRoundTrip(
    "Summary Calculation Income FHL",
    downstreamSummaryCalculationIncomeJson,
    summaryCalculationIncomeFhlModel,
    mtdSummaryCalculationIncomeFhlJson
  )(readsFhl)

  testRoundTrip(
    "Summary Calculation Income Non-FHL",
    downstreamSummaryCalculationIncomeJson,
    summaryCalculationIncomeNonFhlModel,
    mtdSummaryCalculationIncomeNonFhlJson
  )(readsNonFhl)

}
