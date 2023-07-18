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

package v2.models.response

import api.hateoas.Method.GET
import api.hateoas.{HateoasFactory, HateoasWrapper, Link}
import mocks.MockAppConfig
import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec
import v2.models.domain.TypeOfBusiness

class SubmitSelfEmploymentBsasResponseSpec extends UnitSpec {

  val desJson: JsValue = Json.parse(
    """
      |{
      |   "metadata" : {
      |       "calculationId" : "anId"
      |   },
      |   "inputs" : {
      |     "incomeSourceType" : "01"
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

  val submitSelfEmploymentBsasResponseModel: SubmitSelfEmploymentBsasResponse =
    SubmitSelfEmploymentBsasResponse(
      id = "anId",
      typeOfBusiness = TypeOfBusiness.`self-employment`
    )

  "SubmitSelfEmploymentBsasResponse" when {
    "read from valid JSON" should {
      "return the expected SubmitSelfEmploymentBsasResponse object" in {
        desJson.as[SubmitSelfEmploymentBsasResponse] shouldBe submitSelfEmploymentBsasResponseModel
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidDesJson.validate[SubmitSelfEmploymentBsasResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(submitSelfEmploymentBsasResponseModel) shouldBe mtdJson
      }
    }
  }

  "HateoasFactory" must {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      val bsasId = "anId"
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes()
    }

    "expose the correct links for a response from Submit a Self Employment Summary Adjustment" in new Test {
      hateoasFactory.wrap(submitSelfEmploymentBsasResponseModel, SubmitSelfEmploymentBsasHateoasData(nino, bsasId)) shouldBe
        HateoasWrapper(
          submitSelfEmploymentBsasResponseModel,
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId/adjust", GET, "self"),
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId?adjustedStatus=true",
              GET,
              "retrieve-adjustable-summary")
          )
        )
    }
  }
}
