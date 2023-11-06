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

package v3.models.response.retrieveBsas.foreignProperty

import shared.hateoas.HateoasFactory
import shared.models.domain.TaxYear
import shared.hateoas.Method.{GET, POST}
import shared.hateoas.{HateoasWrapper, Link}
import play.api.Configuration
import shared.UnitSpec
import shared.config.MockAppConfig
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._

class RetrieveForeignPropertyBsasResponseSpec extends UnitSpec with RoundTripTest {

  import RetrieveForeignPropertyBsasResponse._

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

  "HateoasFactory" should {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val calculationId  = "anId"
      val context        = "individuals/self-assessment/adjustable-summary"
      val taxYear        = Some(TaxYear.fromMtd("2023-24"))
      val rawResponse    = parsedFhlRetrieveForeignPropertyBsasResponse

      MockedAppConfig.apiGatewayContext.returns(context).anyNumberOfTimes()
    }

    class TysDisabledTest extends Test {
      MockedAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()
    }

    class TysEnabledTest extends Test {
      MockedAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> true)).anyNumberOfTimes()
    }

    "return the correct links without tax year" in new TysDisabledTest {
      private val result = hateoasFactory.wrap(rawResponse, RetrieveForeignPropertyHateoasData(nino, calculationId, None))
      private val expectedLinks = Seq(
        Link(s"/$context/$nino/foreign-property/$calculationId", GET, "self"),
        Link(s"/$context/$nino/foreign-property/$calculationId/adjust", POST, "submit-foreign-property-accounting-adjustments")
      )

      result shouldBe HateoasWrapper(rawResponse, expectedLinks)
    }

    "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
      private val result = hateoasFactory.wrap(rawResponse, RetrieveForeignPropertyHateoasData(nino, calculationId, taxYear))
      private val expectedLinks = Seq(
        Link(s"/$context/$nino/foreign-property/$calculationId?taxYear=2023-24", GET, "self"),
        Link(s"/$context/$nino/foreign-property/$calculationId/adjust?taxYear=2023-24", POST, "submit-foreign-property-accounting-adjustments")
      )

      result shouldBe HateoasWrapper(rawResponse, expectedLinks)
    }
  }

}
