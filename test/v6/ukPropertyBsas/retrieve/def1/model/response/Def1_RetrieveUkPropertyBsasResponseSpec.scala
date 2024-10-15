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

package v6.ukPropertyBsas.retrieve.def1.model.response

import common.model.RoundTripTest
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v6.ukPropertyBsas.retrieve.def1.model.response.RetrieveUkPropertyBsasFixtures._

class Def1_RetrieveUkPropertyBsasResponseSpec extends UnitSpec with JsonErrorValidators with RoundTripTest {

  import Def1_RetrieveUkPropertyBsasResponse._

  testRoundTrip(
    testName = "Retrieve UK Property FHL",
    downstreamJson = downstreamRetrieveBsasFhlResponseJson(2024),
    dataObject = retrieveBsasResponseFhl,
    mtdJson = mtdRetrieveBsasResponseFhlJson(taxYear = "2023-24")
  )(reads)

  testRoundTrip(
    testName = "Retrieve UK Property Uk Property",
    downstreamJson = downstreamRetrieveBsasUkPropertyResponseJson(2024),
    dataObject = retrieveBsasResponseUkProperty,
    mtdJson = mtdRetrieveBsasResponseUkPropertyJson(taxYear = "2023-24")
  )(reads)

}
