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

package v3.models.response

import mocks.MockAppConfig
import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec
import v3.hateoas.HateoasFactory
import v3.models.domain.TypeOfBusiness
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.hateoas.Method.GET

class SubmitUkPropertyBsasResponseSpec extends UnitSpec{

  val desJson: JsValue = Json.parse(
    """
      |{
      |   "metadata" : {
      |       "calculationId" : "anId"
      |   },
      |   "inputs": {
      |   "incomeSourceType":"04"
      |   }
      |}
  """.stripMargin)

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |   "id" : "anId"
      |}
  """.stripMargin)

  val invalidDesJson: JsValue = Json.parse(
    """
      |{
      |   "id" : 3
      |}
  """.stripMargin)

  val submitBsasResponse: SubmitUkPropertyBsasResponse = SubmitUkPropertyBsasResponse("anId", TypeOfBusiness.`uk-property-fhl`)

  "SubmitUkBsasResponse" when {
    "read from valid JSON" should {
      "return the expected SubmitBsasResponse object" in {
        desJson.as[SubmitUkPropertyBsasResponse] shouldBe submitBsasResponse
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidDesJson.validate[SubmitUkPropertyBsasResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(submitBsasResponse) shouldBe mtdJson
      }
    }
  }

  "HateoasFactory" must {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      val bsasId = "anId"
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes
    }

    "expose the correct links for a response from Submit a Property Summary Adjustment" in new Test {
      hateoasFactory.wrap(submitBsasResponse, SubmitUkPropertyBsasHateoasData(nino, bsasId)) shouldBe
        HateoasWrapper(
          submitBsasResponse,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId", GET, "self")
          )
        )
    }
  }

}
