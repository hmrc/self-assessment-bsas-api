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

package v3.models.response.retrieveBsas.ukProperty

import common.model.RoundTripTest
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v3.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._

class SummaryCalculationSpec extends UnitSpec with JsonErrorValidators with RoundTripTest {

  import AdjustableSummaryCalculation.{readsFhl => readsFhlAdjustable, readsNonFhl => readsNonFhlAdjustable, writes => writesAdjustable}

  testRoundTrip(
    "Adjustable Summary Calculation FHL",
    downstreamSummaryCalculationJson,
    adjustableSummaryCalculationFhlModel,
    mtdSummaryCalculationFhlJson)(readsFhlAdjustable)

  testRoundTrip(
    "Adjustable Summary Calculation Non-FHL",
    downstreamSummaryCalculationJson,
    adjustableSummaryCalculationNonFhlModel,
    mtdSummaryCalculationNonFhlJson)(readsNonFhlAdjustable)

  import AdjustedSummaryCalculation.{readsFhl => readsFhlAdjusted, readsNonFhl => readsNonFhlAdjusted, writes => writesAdjusted}

  testRoundTrip(
    "Adjusted Summary Calculation FHL",
    downstreamSummaryCalculationJson,
    adjustedSummaryCalculationFhlModel,
    mtdSummaryCalculationFhlJson)(readsFhlAdjusted)

  testRoundTrip(
    "Adjusted Summary Calculation Non-FHL",
    downstreamSummaryCalculationJson,
    adjustedSummaryCalculationNonFhlModel,
    mtdSummaryCalculationNonFhlJson)(readsNonFhlAdjusted)

}
