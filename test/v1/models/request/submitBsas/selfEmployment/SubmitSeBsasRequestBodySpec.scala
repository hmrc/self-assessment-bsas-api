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

package v1.models.request.submitBsas.selfEmployment

import play.api.libs.json.{JsObject, JsValue, Json}
import support.UnitSpec
import v1.fixtures.request.submitBsas.selfEmployment.SeAdditionsFixture._
import v1.fixtures.request.submitBsas.selfEmployment.SeExpensesFixture._
import v1.fixtures.request.submitBsas.selfEmployment.SeIncomeFixture._
import v1.models.domain.EmptyJsonBody

import scala.collection.mutable.ListBuffer

class SubmitSeBsasRequestBodySpec extends UnitSpec {

  val submitSeBsasRequestBodyModel: SubmitSeBsasRequestBody =
    SubmitSeBsasRequestBody(
      income = Some(seIncomeModel),
      additions = Some(seAdditionsModel),
      expenses = Some(seExpensesModel)
    )

  val submitSeBsasRequestBodyModelWithoutIncome: SubmitSeBsasRequestBody =
    submitSeBsasRequestBodyModel.copy(
      income = None
    )

  val empySubmitSeBsasRequestBodyModel: SubmitSeBsasRequestBody =
    SubmitSeBsasRequestBody(
      income = None,
      additions = None,
      expenses = None
    )

  def submitSeBsasRequestBodyDesJson(model: SubmitSeBsasRequestBody): JsValue = {
    import model._

    val jsObjects : ListBuffer[JsObject] = ListBuffer.empty[JsObject]

    if (income.nonEmpty) {
      jsObjects += Json.obj("income" -> seIncomeJson(income.get))
    }

    if (additions.nonEmpty) {
      jsObjects += Json.obj("additions" -> seAdditionsDesJson(additions.get))
    }

    if (expenses.nonEmpty) {
      jsObjects += Json.obj("expenses" -> seExpensesDesJson(expenses.get))
    }

    val json = jsObjects.fold(Json.parse("""{}""").as[JsObject])((a: JsObject, b: JsObject) => a ++ b)
    json
  }

  def submitSeBsasRequestBodyMtdJson(model: SubmitSeBsasRequestBody): JsValue = {
    import model._

    val jsObjects : ListBuffer[JsObject] = ListBuffer.empty[JsObject]

    if (income.nonEmpty) {
      jsObjects += Json.obj("income" -> seIncomeJson(income.get))
    }

    if (additions.nonEmpty) {
      jsObjects += Json.obj("additions" -> seAdditionsMtdJson(additions.get))
    }

    if (expenses.nonEmpty) {
      jsObjects += Json.obj("expenses" -> seExpensesMtdJson(expenses.get))
    }

    val json = jsObjects.fold(Json.parse("""{}""").as[JsObject])((a: JsObject, b: JsObject) => a ++ b)
    json
  }

  "SubmitSeBsasRequestBody" when {
    "read from valid JSON" should {
      "produce the expected object" in {
        submitSeBsasRequestBodyDesJson(submitSeBsasRequestBodyModel).as[SubmitSeBsasRequestBody] shouldBe
          submitSeBsasRequestBodyModel
      }
    }

    "written to JSON" should {
      "produce the expected SubmitSeBsasRequestBody JsObject" in {
        Json.toJson(submitSeBsasRequestBodyModel) shouldBe submitSeBsasRequestBodyMtdJson(submitSeBsasRequestBodyModel)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        submitSeBsasRequestBodyDesJson(submitSeBsasRequestBodyModelWithoutIncome).as[SubmitSeBsasRequestBody] shouldBe
          submitSeBsasRequestBodyModelWithoutIncome
      }

      "not write those fields to JSON" in {
        Json.toJson(submitSeBsasRequestBodyModel) shouldBe submitSeBsasRequestBodyMtdJson(submitSeBsasRequestBodyModel)
      }
    }


    "no fields as supplied" should {
      "read to an empty SubmitSeBsasRequestBody object" in {
        submitSeBsasRequestBodyDesJson(empySubmitSeBsasRequestBodyModel).as[SubmitSeBsasRequestBody] shouldBe
          empySubmitSeBsasRequestBodyModel
      }

      "write to empty JSON" in {
        Json.toJson(empySubmitSeBsasRequestBodyModel) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }
}
