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

package v3.models.response.retrieveBsas.selfEmployment

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures.{bsasDetailModel, desBsasDetailJson, mtdBsasDetailJson}
import v3.models.response.retrieveBsas.TotalBsas
import v3.models.utils.JsonErrorValidators

class BsasDetailSpec extends UnitSpec with JsonErrorValidators {

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        desBsasDetailJson.as[BsasDetail] shouldBe bsasDetailModel
      }

      "not return fields when all nested object optional fields are not present" in {

        val desBsasJson: JsValue = Json.parse(
          s"""{
             |  "totalIncome": 100.49,
             |  "totalExpenses": 100.49,
             |  "totalAdditions": 100.49,
             |  "totalDeductions": 100.49
             |}""".stripMargin
        )

        val totalBsasModel = TotalBsas(Some(100.49), Some(100.49), Some(100.49), Some(100.49))
        desBsasJson.as[BsasDetail] shouldBe BsasDetail(totalBsasModel, None, None, None, None, None, None)
      }
    }
  }

  "writes" when {
    "passed a valid model" should {
      "return valid JSON" in {
        bsasDetailModel.toJson shouldBe mtdBsasDetailJson
      }
    }
  }

}
