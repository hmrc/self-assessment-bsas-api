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

package v3.models.response

import play.api.Configuration
import play.api.libs.json.{JsError, JsValue, Json}
import shared.config.MockAppConfig
import shared.hateoas.Method.GET
import shared.hateoas.{HateoasFactory, HateoasWrapper, Link}
import shared.models.domain.TaxYear
import shared.utils.UnitSpec
import v3.models.domain.TypeOfBusiness._

class TriggerBsasResponseSpec extends UnitSpec {

  val desJson: JsValue = Json.parse("""
      |{
      |   "metadata" : {
      |       "calculationId" : "anId"
      |   }
      |}
  """.stripMargin)

  val mtdJson: JsValue = Json.parse("""
      |{
      |   "calculationId" : "anId"
      |}
  """.stripMargin)

  val invalidDesJson: JsValue = Json.parse("""
      |{
      |   "calculationId" : 3
      |}
  """.stripMargin)

  val triggerBsasResponse: TriggerBsasResponse = TriggerBsasResponse("anId")

  "TriggerBsasResponse" when {
    "read from valid JSON" should {
      "return the expected TriggerBsasResponse object" in {
        desJson.as[TriggerBsasResponse] shouldBe triggerBsasResponse
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidDesJson.validate[TriggerBsasResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(triggerBsasResponse) shouldBe mtdJson
      }
    }
  }

  "HateoasFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val bsasId         = "anId"
      val taxYear        = Some(TaxYear.fromMtd("2023-24"))
      val context        = "individuals/self-assessment/adjustable-summary"

      MockAppConfig.apiGatewayContext.returns(context).anyNumberOfTimes()
    }

    class TysDisabledTest extends Test {
      MockAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()
    }

    class TysEnabledTest extends Test {
      MockAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> true)).anyNumberOfTimes()
    }

    "triggering a self employment BSAS" should {
      "return the correct links without tax year" in new TysDisabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `self-employment`, bsasId, None))
        private val expectedLink = Link(s"/$context/$nino/self-employment/$bsasId", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }

      "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `self-employment`, bsasId, taxYear))
        private val expectedLink = Link(s"/$context/$nino/self-employment/$bsasId?taxYear=2023-24", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }
    }

    "triggering an FHL property BSAS" should {
      "return the correct links without tax year" in new TysDisabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `uk-property-fhl`, bsasId, None))
        private val expectedLink = Link(s"/$context/$nino/uk-property/$bsasId", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }

      "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `uk-property-fhl`, bsasId, taxYear))
        private val expectedLink = Link(s"/$context/$nino/uk-property/$bsasId?taxYear=2023-24", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }
    }

    "triggering a non-FHL property BSAS" should {
      "return the correct links without tax year" in new TysDisabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `uk-property-non-fhl`, bsasId, None))
        private val expectedLink = Link(s"/$context/$nino/uk-property/$bsasId", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }

      "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `uk-property-non-fhl`, bsasId, taxYear))
        private val expectedLink = Link(s"/$context/$nino/uk-property/$bsasId?taxYear=2023-24", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }
    }

    "triggering a foreign property BSAS" should {
      "return the correct links without tax year" in new TysDisabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `foreign-property`, bsasId, None))
        private val expectedLink = Link(s"/$context/$nino/foreign-property/$bsasId", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }

      "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `foreign-property`, bsasId, taxYear))
        private val expectedLink = Link(s"/$context/$nino/foreign-property/$bsasId?taxYear=2023-24", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }
    }

    "triggering a foreign property fhl eea BSAS" should {
      "return the correct links without tax year" in new TysDisabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `foreign-property-fhl-eea`, bsasId, None))
        private val expectedLink = Link(s"/$context/$nino/foreign-property/$bsasId", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }

      "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
        private val result       = hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, `foreign-property-fhl-eea`, bsasId, taxYear))
        private val expectedLink = Link(s"/$context/$nino/foreign-property/$bsasId?taxYear=2023-24", GET, "self")

        result shouldBe HateoasWrapper(triggerBsasResponse, Seq(expectedLink))
      }
    }
  }

}
