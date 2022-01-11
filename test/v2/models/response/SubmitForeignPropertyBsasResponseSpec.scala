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

package v2.models.response

import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.models.hateoas.Link
import v2.models.hateoas.Method.GET
import v2.models.domain.TypeOfBusiness

class SubmitForeignPropertyBsasResponseSpec extends UnitSpec with MockAppConfig {

  val mtdJson = Json.parse(
    """
      |{
      | "id": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"
      |}
      |""".stripMargin)

  val desJson: JsValue = Json.parse(
    """
      |{
      |   "metadata" : {
      |       "calculationId" : "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"
      |   },
      |   "inputs": {
      |   "incomeSourceType":"03"
      |   }
      |}
  """.stripMargin)

  val responseModel = SubmitForeignPropertyBsasResponse("717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4", TypeOfBusiness.`foreign-property-fhl-eea`)

  "SubmitForeignPropertyBsasResponseSpec" when {
    "read from valid JSON" should {
      "return the expected SubmitForeignPropertyBsasResponse object" in {
        desJson.as[SubmitForeignPropertyBsasResponse] shouldBe responseModel
      }
    }
    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(responseModel) shouldBe mtdJson
      }
    }
  }

  "LinksFactory" should {
    "return the correct links" in {
      val nino = "mynino"
      val bsasId = "mybsasid"

      MockedAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes
      SubmitForeignPropertyBsasResponse.SubmitForeignPropertyAdjustmentHateoasFactory.links(
        mockAppConfig, SubmitForeignPropertyBsasHateoasData(nino, bsasId)) shouldBe
        Seq(
          Link(s"/my/context/$nino/foreign-property/$bsasId/adjust", GET, "self"),
          Link(s"/my/context/$nino/foreign-property/$bsasId?adjustedStatus=true", GET, "retrieve-adjustable-summary")
        )
    }
  }

}
