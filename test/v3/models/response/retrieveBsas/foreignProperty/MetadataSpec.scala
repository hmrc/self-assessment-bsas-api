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

package v3.models.response.retrieveBsas.foreignProperty

import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import play.api.libs.json.Json
import support.UnitSpec
import v3.models.utils.JsonErrorValidators

class MetadataSpec extends UnitSpec with JsonErrorValidators{

  val mtdJson = Json.parse(
    """{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "adjustedDateTime": "2020-12-05T16:19:44Z",
      |  "nino": "AA999999A",
      |  "taxYear": "2019-20",
      |  "summaryStatus": "valid"
      |  }""".stripMargin
  )

  val desJson = Json.parse(
    """{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "adjustedDateTime": "2020-12-05T16:19:44Z",
      |  "taxableEntityId": "AA999999A",
      |  "taxYear": 2020,
      |  "status": "valid"
      |}""".stripMargin
  )

  val desJsonWithoutADT = Json.parse(
    """{
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-12-05T16:19:44Z",
      |  "taxableEntityId": "AA999999A",
      |  "taxYear": 2020,
      |  "status": "valid"
      |}""".stripMargin
  )


  "reads" should {
    "return a valid metadata model" when {
      "a valid json with all fields are supplied" in {
        desJson.as[Metadata] shouldBe metaDataModel
      }

      "a valid json with no adjustedSummary is supplied" in {
        desJsonWithoutADT.as[Metadata] shouldBe metaDataModel.copy(adjustedDateTime = None)
      }
    }
  }

  "writes" should {
    "return a valid metadata json" when {
      "a valid model is supplied" in {
        metaDataModel.toJson shouldBe mtdJson
      }
    }
  }
}
