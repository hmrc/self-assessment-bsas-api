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

package v5.foreignPropertyBsas.retrieve.def1.model.response

import common.model.RoundTripTest
import shared.utils.UnitSpec
import v5.foreignPropertyBsas.retrieve.def1.model.response.RetrieveForeignPropertyBsasBodyFixtures._

class Def1_RetrieveForeignPropertyBsasResponseSpec extends UnitSpec with RoundTripTest {

  import Def1_RetrieveForeignPropertyBsasResponse._

  testRoundTrip(
    "Retrieve Foreign Property Bsas Response FHL",
    retrieveForeignPropertyBsasDesFhlJson,
    parsedFhlRetrieveForeignPropertyBsasResponse,
    retrieveForeignPropertyBsasMtdFhlJson
  )(reads)

  testRoundTrip(
    "Retrieve Foreign Property Bsas Response Non-FHL",
    retrieveForeignPropertyBsasDesNonFhlJson,
    parsedNonFhlRetrieveForeignPropertyBsasResponse,
    retrieveForeignPropertyBsasMtdNonFhlJson
  )(reads)

}