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

package v5.bsas.list.model.response

import play.api.Configuration
import shared.UnitSpec
import shared.config.MockAppConfig
import shared.hateoas.Link
import shared.hateoas.Method.{GET, POST}
import shared.models.domain.TaxYear
import v5.bsas.list.def1.model.Def1_ListBsasFixtures
import v5.bsas.list.def1.model.response.Def1_ListBsasResponse
import v5.bsas.list.model.response.ListBsasResponse.LinksFactory.{itemLinks, links}
import v5.models.domain.TypeOfBusiness
import v5.models.domain.TypeOfBusiness._

class ListBsasResponseHateoasLinksSpec extends UnitSpec with MockAppConfig with Def1_ListBsasFixtures {

  val selfEmploymentBsasResponse: ListBsasResponse[BsasSummary]        = makeResponse(`self-employment`)
  val ukPropertyFhlBsasResponse: ListBsasResponse[BsasSummary]         = makeResponse(`uk-property-fhl`)
  val ukPropertyNonFhlBsasResponse: ListBsasResponse[BsasSummary]      = makeResponse(`uk-property-non-fhl`)
  val foreignPropertyFhlEeaBsasResponse: ListBsasResponse[BsasSummary] = makeResponse(`foreign-property-fhl-eea`)
  val foreignPropertyBsasResponse: ListBsasResponse[BsasSummary]       = makeResponse(`foreign-property`)

  def makeResponse(typeOfBusiness: TypeOfBusiness): ListBsasResponse[BsasSummary] = Def1_ListBsasResponse(
    Seq(businessSourceSummary().copy(typeOfBusiness = typeOfBusiness))
  )

  "Links Factory" should {
    class Test extends MockAppConfig {
      val nino    = "someNino"
      val bsasId  = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"
      val context = "individuals/self-assessment/adjustable-summary"

      val taxYear: Option[TaxYear] = Some(TaxYear.fromMtd("2023-24"))

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
        private val result = links(mockAppConfig, ListBsasHateoasData(nino, listBsasResponse, None))
        private val expectedLinks = Seq(
          Link(s"/$context/$nino/trigger", POST, "trigger-business-source-adjustable-summary"),
          Link(s"/$context/$nino", GET, "self")
        )

        result shouldBe expectedLinks
      }

      "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
        private val result = links(mockAppConfig, ListBsasHateoasData(nino, listBsasResponse, taxYear))
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
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, selfEmploymentBsasResponse, None), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/self-employment/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, selfEmploymentBsasResponse, taxYear), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/self-employment/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }

      "given uk-property-fhl business type" should {
        "return correct links without tax year" in new TysDisabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, ukPropertyFhlBsasResponse, None), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/uk-property/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, ukPropertyFhlBsasResponse, taxYear), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/uk-property/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }

      "given uk-property-non-fhl business type" should {
        "return correct links without tax year" in new TysDisabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, ukPropertyNonFhlBsasResponse, None), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/uk-property/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, ukPropertyNonFhlBsasResponse, taxYear), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/uk-property/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }

      "given foreign-property-fhl-eea business type" should {
        "return correct links without tax year" in new TysDisabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, foreignPropertyFhlEeaBsasResponse, None), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/foreign-property/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, foreignPropertyFhlEeaBsasResponse, taxYear), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/foreign-property/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }

      "given foreign-property business type" should {
        "return correct links without tax year" in new TysDisabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, foreignPropertyBsasResponse, None), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/foreign-property/$bsasId", GET, "self"))
        }

        "return correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
          private val result = itemLinks(mockAppConfig, ListBsasHateoasData(nino, foreignPropertyBsasResponse, taxYear), bsasSummary)
          result shouldBe Seq(Link(s"/$context/$nino/foreign-property/$bsasId?taxYear=2023-24", GET, "self"))
        }
      }
    }
  }

}
