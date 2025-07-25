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

package v7.foreignPropertyBsas.retrieve.def1.model.response

import common.model.RoundTripTest
import shared.utils.UnitSpec
import v7.foreignPropertyBsas.retrieve.def1.model.response.RetrieveForeignPropertyBsasBodyFixtures.*

class Def1_RetrieveForeignPropertyBsasResponseSpec extends UnitSpec with RoundTripTest {

  import Def1_RetrieveForeignPropertyBsasResponse.*

  testRoundTrip(
    "Retrieve Foreign Property Bsas Response FHL",
    retrieveForeignPropertyBsasDesFhlJson(),
    parsedFhlRetrieveForeignPropertyBsasResponse,
    retrieveForeignPropertyBsasMtdFhlJson()
  )(reads)

  testRoundTrip(
    "Retrieve Foreign Property Bsas Response Non-FHL",
    retrieveForeignPropertyBsasDesJson(),
    parsedRetrieveForeignPropertyBsasResponse,
    retrieveForeignPropertyBsasMtdJson()
  )(reads)

  testRoundTrip(
    "Retrieve Foreign Property Bsas Zero Adjustments Response FHL",
    retrieveForeignPropertyBsasDesZeroAdjustmentsFhlJson(),
    parsedFhlRetrieveForeignPropertyBsasZeroAdjustmentsResponse,
    retrieveForeignPropertyBsasMtdFhlZeroAdjustmentsJson()
  )(reads)

  testRoundTrip(
    "Retrieve Foreign Property Bsas Zero Adjustments Response Non-FHL",
    retrieveForeignPropertyBsasDesZeroAdjustmentsJson(),
    parsedRetrieveForeignPropertyBsasZeroAdjustmentsResponse,
    retrieveForeignPropertyBsasMtdZeroAdjustmentsJson()
  )(reads)

}
