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

package v3.models.response.listBsas

import play.api.Configuration
import play.api.libs.json.{JsError, JsObject, Json}
import shared.UnitSpec
import shared.config.MockAppConfig
import shared.hateoas.Link
import shared.hateoas.Method.{GET, POST}
import shared.models.domain.TaxYear
import v3.fixtures.ListBsasFixture
import v3.models.domain.TypeOfBusiness
import v3.models.domain.TypeOfBusiness._
import v3.models.response.listBsas.ListBsasResponse.LinksFactory._

class ListBsasResponseSpec extends UnitSpec with MockAppConfig with ListBsasFixture {

  val selfEmploymentBsasResponse: ListBsasResponse[BsasSummary]        = makeResponse(`self-employment`)
  val ukPropertyFhlBsasResponse: ListBsasResponse[BsasSummary]         = makeResponse(`uk-property-fhl`)
  val ukPropertyNonFhlBsasResponse: ListBsasResponse[BsasSummary]      = makeResponse(`uk-property-non-fhl`)
  val foreignPropertyFhlEeaBsasResponse: ListBsasResponse[BsasSummary] = makeResponse(`foreign-property-fhl-eea`)
  val foreignPropertyBsasResponse: ListBsasResponse[BsasSummary]       = makeResponse(`foreign-property`)

  def makeResponse(typeOfBusiness: TypeOfBusiness): ListBsasResponse[BsasSummary] = ListBsasResponse(
    Seq(businessSourceSummaryModel().copy(typeOfBusiness = typeOfBusiness))
  )

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
    class Test extends MockAppConfig {
      val nino    = "someNino"
      val bsasId  = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"
      val context = "individuals/self-assessment/adjustable-summary"
      val taxYear = Some(TaxYear.fromMtd("2023-24"))

      MockAppConfig.apiGatewayContext.returns(context).anyNumberOfTimes()
    }

    class TysDisabledTest extends Test {
      MockAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()
    }

    class TysEnabledTest extends Test {
      MockAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> true)).anyNumberOfTimes()
    }

    "top level links" should {
      "return the correct links without tax year" in new TysDisabledTest {
        private val result = links(mockAppConfig, ListBsasHateoasData(nino, listBsasResponseModel, None))
        private val expectedLinks = Seq(
          Link(s"/$context/$nino/trigger", POST, "trigger-business-source-adjustable-summary"),
          Link(s"/$context/$nino", GET, "self")
        )

        result shouldBe expectedLinks
      }

      "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
        private val result = links(mockAppConfig, ListBsasHateoasData(nino, listBsasResponseModel, taxYear))
        private val expectedLinks = Seq(
          Link(s"/$context/$nino/trigger", POST, "trigger-business-source-adjustable-summary"),
          Link(s"/$context/$nino?taxYear=2023-24", GET, "self")
        )

        result shouldBe expectedLinks
      }
    }

    "item level links" when {
      "given self-employment business type" should {
        "return correct links without tax year" in new TysDisabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, selfEmploymentBsasResponse, None), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/self-employment/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, selfEmploymentBsasResponse, taxYear), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/self-employment/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }

      "given uk-property-fhl business type" should {
        "return correct links without tax year" in new TysDisabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, ukPropertyFhlBsasResponse, None), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/uk-property/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, ukPropertyFhlBsasResponse, taxYear), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/uk-property/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }

      "given uk-property-non-fhl business type" should {
        "return correct links without tax year" in new TysDisabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, ukPropertyNonFhlBsasResponse, None), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/uk-property/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, ukPropertyNonFhlBsasResponse, taxYear), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/uk-property/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }

      "given foreign-property-fhl-eea business type" should {
        "return correct links without tax year" in new TysDisabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, foreignPropertyFhlEeaBsasResponse, None), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/foreign-property/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, foreignPropertyFhlEeaBsasResponse, taxYear), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/foreign-property/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }

      "given foreign-property business type" should {
        "return correct links without tax year" in new TysDisabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, foreignPropertyBsasResponse, None), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/foreign-property/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, foreignPropertyBsasResponse, taxYear), bsasSummaryModel)
          result shouldBe Seq(Link(s"/$context/$nino/foreign-property/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }
    }
  }

}
