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

package v3.hateoas

import shared.config.MockAppConfig
import shared.hateoas.Method.{GET, POST}
import shared.hateoas.{Link, Method}
import shared.models.domain.TaxYear
import shared.utils.UnitSpec
import v3.hateoas.RelType._

class HateoasLinksSpec extends UnitSpec with MockAppConfig {

  private val nino        = "AA111111A"
  private val calcId      = "1234567890"
  private val taxYear2023 = TaxYear.fromMtd("2022-23")
  private val taxYear2024 = TaxYear.fromMtd("2023-24")

  def assertCorrectLink(makeLink: Option[TaxYear] => Link, baseHref: String, method: Method, rel: String): Unit = {
    "return the correct link" in new Test {
      makeLink(None) shouldBe Link(href = baseHref, method = method, rel = rel)
    }

    "not include tax year query parameter given a non-TYS tax year" in new Test {
      makeLink(Some(taxYear2023)) shouldBe Link(href = baseHref, method = method, rel = rel)
    }

    "include tax year query parameter given a TYS tax year" in new Test {
      makeLink(Some(taxYear2024)) shouldBe Link(href = s"$baseHref?taxYear=2023-24", method = method, rel = rel)
    }
  }

  class Test {
    MockedAppConfig.apiGatewayContext.returns("context").anyNumberOfTimes()
  }

  "HateoasLinks" when {
    "triggerBsas" should {
      "return the correct link" in new Test {
        val link: Link = Link(href = s"/context/$nino/trigger", method = POST, rel = TRIGGER)
        Target.triggerBsas(mockAppConfig, nino) shouldBe link
      }
    }

    "listBsas" should {
      assertCorrectLink(
        makeLink = Target.listBsas(mockAppConfig, nino, _),
        baseHref = s"/context/$nino",
        method = GET,
        rel = SELF
      )
    }

    "getSelfEmploymentBsas" should {
      assertCorrectLink(
        makeLink = Target.getSelfEmploymentBsas(mockAppConfig, nino, calcId, _),
        baseHref = s"/context/$nino/self-employment/$calcId",
        method = GET,
        rel = SELF
      )
    }

    "getUkPropertyBsas" should {
      assertCorrectLink(
        makeLink = Target.getUkPropertyBsas(mockAppConfig, nino, calcId, _),
        baseHref = s"/context/$nino/uk-property/$calcId",
        method = GET,
        rel = SELF
      )
    }

    "getForeignPropertyBsas" should {
      assertCorrectLink(
        makeLink = Target.getForeignPropertyBsas(mockAppConfig, nino, calcId, _),
        baseHref = s"/context/$nino/foreign-property/$calcId",
        method = GET,
        rel = SELF
      )
    }

    "adjustSelfEmploymentBsas" should {
      assertCorrectLink(
        makeLink = Target.adjustSelfEmploymentBsas(mockAppConfig, nino, calcId, _),
        baseHref = s"/context/$nino/self-employment/$calcId/adjust",
        method = POST,
        rel = SUBMIT_SE_ADJUSTMENTS
      )
    }

    "adjustUkPropertyBsas" should {
      assertCorrectLink(
        makeLink = Target.adjustUkPropertyBsas(mockAppConfig, nino, calcId, _),
        baseHref = s"/context/$nino/uk-property/$calcId/adjust",
        method = POST,
        rel = SUBMIT_UK_PROPERTY_ADJUSTMENTS
      )
    }

    "adjustForeignPropertyBsas" should {
      assertCorrectLink(
        makeLink = Target.adjustForeignPropertyBsas(mockAppConfig, nino, calcId, _),
        baseHref = s"/context/$nino/foreign-property/$calcId/adjust",
        method = POST,
        rel = SUBMIT_FOREIGN_PROPERTY_ADJUSTMENTS
      )
    }

  }

  object Target extends HateoasLinks
}
