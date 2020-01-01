/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.response

import mocks.MockAppConfig
import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec
import v1.hateoas.HateoasFactory
import v1.models.domain.TypeOfBusiness
import v1.models.hateoas.Method.{GET, POST}
import v1.models.hateoas.{HateoasWrapper, Link}

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
      |   "id" : "anId"
      |}
  """.stripMargin)

  val invalidDesJson: JsValue = Json.parse("""
      |{
      |   "id" : 3
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

  "HateoasFactory" must {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val bsasId         = "anId"
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes
    }

    "expose the correct links for triggering a self employment BSAS" in new Test {
      hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, TypeOfBusiness.`self-employment`, bsasId)) shouldBe
        HateoasWrapper(
          triggerBsasResponse,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId", GET, "self")
          )
        )
    }
    "expose the correct links for triggering an FHL property BSAS" in new Test {
      hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, TypeOfBusiness.`uk-property-fhl`, bsasId)) shouldBe
        HateoasWrapper(
          triggerBsasResponse,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId", GET, "self")
          )
        )
    }
    "expose the correct links for triggering a non-FHL property BSAS" in new Test {
      hateoasFactory.wrap(triggerBsasResponse, TriggerBsasHateoasData(nino, TypeOfBusiness.`uk-property-non-fhl`, bsasId)) shouldBe
        HateoasWrapper(
          triggerBsasResponse,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId", GET, "self")
          )
        )
    }

  }
}
