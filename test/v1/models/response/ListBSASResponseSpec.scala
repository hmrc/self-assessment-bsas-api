/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.libs.json.{JsSuccess, Json}
import support.UnitSpec
import v1.fixtures.ListBSASFixtures._
import v1.models.domain.TypeOfBusiness
import v1.models.response.common.{AccountingPeriodResponse, BSASEntries, BusinessSourceSummary}

class ListBSASResponseSpec extends UnitSpec {

  val model =
    ListBSASResponse(
      Seq(BusinessSourceSummary(
        typeOfBusiness = TypeOfBusiness.`self-employment`,
        selfEmploymentId = Some("000000000000210"),
        AccountingPeriodResponse(
          startDate = "2018-10-11",
          endDate = "2019-10-10"
        ),
        Seq(
          BSASEntries(
            bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
            requestedDateTime = "2019-10-14T11:33:27Z",
            summaryStatus = "valid",
            adjustedSummary = false
          )
        )
      ))
    )

  "BusinessSourceSummaries" should {

    "write correctly to json" in {
      Json.toJson(model) shouldBe summariesJSON
    }

    "read correctly to json" in {
      summariesFromDesJSON.validate[ListBSASResponse] shouldBe JsSuccess(model)
    }
  }
}
