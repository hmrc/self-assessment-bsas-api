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

package v3.models.response.listBsas

import mocks.MockAppConfig
import play.api.libs.json.{JsError, JsObject, Json}
import support.UnitSpec
import v3.fixtures.ListBsasFixture
import v3.models.domain.TypeOfBusiness
import v3.models.hateoas.Link
import v3.models.hateoas.Method.{GET, POST}

class ListBsasResponseSpec extends UnitSpec with MockAppConfig with ListBsasFixture {

  val selfEmploymentBsasModel: ListBsasResponse[BsasSummary] = listBsasResponseModel

  val ukPropertyBsasModel: ListBsasResponse[BsasSummary] = ListBsasResponse(Seq(
    businessSourceSummaryModel.copy(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`)
  ))

  val foreignPropertyBsasModel: ListBsasResponse[BsasSummary] = ListBsasResponse(Seq(
    businessSourceSummaryModel.copy(typeOfBusiness = TypeOfBusiness.`foreign-property`)
  ))

  "ListBsasResponse" when {
    "read from valid JSON" should {
      "return the expected object" in {
        listBsasResponseDownstreamJson.as[ListBsasResponse[BsasSummary]] shouldBe listBsasResponseModel
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        JsObject.empty.validate[ListBsasResponse[BsasSummary]] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JSON" in {
        Json.toJson(listBsasResponseModel) shouldBe listBsasResponseJson
      }
    }
  }

  "Links Factory" should {
    val nino = "someNino"
    val selfEmployment = "self-employment"
    val ukProperty = "property"
    val foreignProperty = "foreign-property"
    val bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"

    "expose the correct top level links for a self employment list" in {
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes

      ListBsasResponse.LinksFactory.links(mockAppConfig, ListBsasHateoasData(nino, selfEmploymentBsasModel)) shouldBe
        Seq(
          Link(s"/individuals/self-assessment/adjustable-summary/$nino/trigger", POST, "trigger-business-source-adjustable-summary"),
          Link(s"/individuals/self-assessment/adjustable-summary/$nino", GET, "self")
        )
    }

    "expose the correct top level links for a uk property list" in {
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes

      ListBsasResponse.LinksFactory.links(mockAppConfig, ListBsasHateoasData(nino, ukPropertyBsasModel)) shouldBe
        Seq(
          Link(s"/individuals/self-assessment/adjustable-summary/$nino/trigger", POST, "trigger-business-source-adjustable-summary"),
          Link(s"/individuals/self-assessment/adjustable-summary/$nino", GET, "self")
        )
    }

    "expose the correct top level links for a foreign property list" in {
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes

      ListBsasResponse.LinksFactory.links(mockAppConfig, ListBsasHateoasData(nino, foreignPropertyBsasModel)) shouldBe
        Seq(
          Link(s"/individuals/self-assessment/adjustable-summary/$nino/trigger", POST, "trigger-business-source-adjustable-summary"),
          Link(s"/individuals/self-assessment/adjustable-summary/$nino", GET, "self")
        )
    }

    "expose the correct item level links for a self employment list" in {
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes

      ListBsasResponse.LinksFactory.itemLinks(
        mockAppConfig,
        ListBsasHateoasData(nino, selfEmploymentBsasModel),
        selfEmploymentBsasModel.businessSources.head.summaries.head
      ) shouldBe Seq(Link(s"/individuals/self-assessment/adjustable-summary/$nino/$selfEmployment/$bsasId", GET, "self"))
    }

    "expose the correct item level links for a uk property list" in {
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes

      ListBsasResponse.LinksFactory.itemLinks(
        mockAppConfig,
        ListBsasHateoasData(nino, ukPropertyBsasModel),
        ukPropertyBsasModel.businessSources.head.summaries.head
      ) shouldBe Seq(Link(s"/individuals/self-assessment/adjustable-summary/$nino/$ukProperty/$bsasId", GET, "self"))
    }

    "expose the correct item level links for a foreign property list" in {
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes

      ListBsasResponse.LinksFactory.itemLinks(
        mockAppConfig,
        ListBsasHateoasData(nino, foreignPropertyBsasModel),
        foreignPropertyBsasModel.businessSources.head.summaries.head
      ) shouldBe Seq(Link(s"/individuals/self-assessment/adjustable-summary/$nino/$foreignProperty/$bsasId", GET, "self"))
    }
  }
}
