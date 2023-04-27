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

package v2.models.response.listBsas

import api.hateoas.Link
import api.hateoas.Method.{ GET, POST }
import api.models.domain.Status
import mocks.MockAppConfig
import play.api.libs.json.{ JsSuccess, Json }
import support.UnitSpec
import v2.fixtures.ListBsasFixtures._
import v2.models.domain.TypeOfBusiness
import v2.models.request.AccountingPeriod

class ListBsasResponseSpec extends UnitSpec with MockAppConfig {

  val selfEmploymentBsasModel =
    ListBsasResponse(
      Seq(
        BusinessSourceSummary(
          typeOfBusiness = TypeOfBusiness.`self-employment`,
          businessId = Some("000000000000210"),
          AccountingPeriod(
            startDate = "2018-10-11",
            endDate = "2019-10-10"
          ),
          Seq(
            BsasEntries(
              bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
              requestedDateTime = "2019-10-14T11:33:27Z",
              summaryStatus = Status.`valid`,
              adjustedSummary = false
            )
          )
        ))
    )

  val ukPropertyBsasModel =
    ListBsasResponse(
      Seq(
        BusinessSourceSummary(
          typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
          businessId = Some("000000000000210"),
          AccountingPeriod(
            startDate = "2018-10-11",
            endDate = "2019-10-10"
          ),
          Seq(
            BsasEntries(
              bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
              requestedDateTime = "2019-10-14T11:33:27Z",
              summaryStatus = Status.`valid`,
              adjustedSummary = false
            )
          )
        ))
    )

  val foreignPropertyBsasModel =
    ListBsasResponse(
      Seq(
        BusinessSourceSummary(
          typeOfBusiness = TypeOfBusiness.`foreign-property`,
          businessId = Some("000000000000210"),
          AccountingPeriod(
            startDate = "2018-10-11",
            endDate = "2019-10-10"
          ),
          Seq(
            BsasEntries(
              bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
              requestedDateTime = "2019-10-14T11:33:27Z",
              summaryStatus = Status.`valid`,
              adjustedSummary = false
            )
          )
        ))
    )

  val bsasSummariesModel = Seq(
    BusinessSourceSummary(
      typeOfBusiness = TypeOfBusiness.`foreign-property`,
      businessId = Some("000000000000210"),
      AccountingPeriod(
        startDate = "2018-10-11",
        endDate = "2019-10-10"
      ),
      Seq(
        BsasEntries(
          bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
          requestedDateTime = "2019-10-14T11:33:27Z",
          summaryStatus = Status.`valid`,
          adjustedSummary = false
        )
      )
    ))

  "BusinessSourceSummaries" should {

    "write correctly to json" in {

      Json.toJson(summaryModel) shouldBe summariesJSON
    }

    "read correctly to json" in {
      summariesFromDesJSONSingle.validate[ListBsasResponse[BsasEntries]] shouldBe JsSuccess(summaryModel)
    }

    "Links Factory" should {
      val nino            = "someNino"
      val selfEmployment  = "self-employment"
      val ukProperty      = "property"
      val foreignProperty = "foreign-property"
      val bsasId          = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"

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
          BsasEntries(bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
                      requestedDateTime = "2019-10-14T11:33:27Z",
                      summaryStatus = Status.`valid`,
                      adjustedSummary = false)
        ) shouldBe
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/$selfEmployment/$bsasId", GET, "self")
          )
      }

      "expose the correct item level links for a uk property list" in {
        MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes
        ListBsasResponse.LinksFactory.itemLinks(
          mockAppConfig,
          ListBsasHateoasData(nino, ukPropertyBsasModel),
          BsasEntries(bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
                      requestedDateTime = "2019-10-14T11:33:27Z",
                      summaryStatus = Status.`valid`,
                      adjustedSummary = false)
        ) shouldBe
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/$ukProperty/$bsasId", GET, "self")
          )
      }

      "expose the correct item level links for a foreign property list" in {
        MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes
        ListBsasResponse.LinksFactory.itemLinks(
          mockAppConfig,
          ListBsasHateoasData(nino, foreignPropertyBsasModel),
          BsasEntries(bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
                      requestedDateTime = "2019-10-14T11:33:27Z",
                      summaryStatus = Status.`valid`,
                      adjustedSummary = false)
        ) shouldBe
          Seq(
            Link(s"/individuals/self-assessment/adjustable-summary/$nino/$foreignProperty/$bsasId", GET, "self")
          )
      }
    }
  }
}
