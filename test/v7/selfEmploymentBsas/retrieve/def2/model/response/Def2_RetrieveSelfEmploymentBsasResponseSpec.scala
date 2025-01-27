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

package v7.selfEmploymentBsas.retrieve.def2.model.response

import common.model.RoundTripTest
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v7.selfEmploymentBsas.retrieve.def2.model.Def2_RetrieveSelfEmploymentBsasFixtures._

class Def2_RetrieveSelfEmploymentBsasResponseSpec extends UnitSpec with JsonErrorValidators with RoundTripTest {

  import Def2_RetrieveSelfEmploymentBsasResponse._

  testRoundTrip(
    "Retrieve Self Employment Bsas Response ",
    downstreamRetrieveBsasResponseJson(),
    retrieveBsasResponse,
    mtdRetrieveBsasResponseJson()
  )(reads)

}
