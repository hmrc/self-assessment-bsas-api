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
import v1.fixtures.request.submitBsas.selfEmployment.AdditionsFixture._
import v1.fixtures.request.submitBsas.selfEmployment.ExpensesFixture._
import v1.fixtures.request.submitBsas.selfEmployment.IncomeFixture._
import v1.models.domain.EmptyJsonBody

import scala.collection.mutable.ListBuffer

class SubmitSelfEmploymentBsasRequestBodySpec extends UnitSpec {

  val submitSelfEmploymentBsasRequestBodyModel: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(
      income = Some(incomeModel),
      additions = Some(additionsModel),
      expenses = Some(expensesModel)
    )

  val submitSelfEmploymentBsasRequestBodyModelWithoutIncome: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(
      income = None,
      additions = Some(additionsModel),
      expenses = Some(expensesModel)
    )

  val emptySubmitSelfEmploymentBsasRequestBodyModel: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(
      income = None,
      additions = None,
      expenses = None
    )

  def submitSelfEmploymentBsasRequestBodyDesJson(model: SubmitSelfEmploymentBsasRequestBody): JsValue = {
    import model._

    val jsObjects : ListBuffer[JsObject] = ListBuffer.empty[JsObject]

    if (income.nonEmpty) {
      jsObjects += Json.obj("income" -> incomeJson(income.get))
    }

    if (additions.nonEmpty) {
      jsObjects += Json.obj("additions" -> additionsDesJson(additions.get))
    }

    if (expenses.nonEmpty) {
      jsObjects += Json.obj("expenses" -> expensesDesJson(expenses.get))
    }

    val json = jsObjects.fold(Json.parse("""{}""").as[JsObject])((a: JsObject, b: JsObject) => a ++ b)
    json
  }

  def submitSelfEmploymentBsasRequestBodyMtdJson(model: SubmitSelfEmploymentBsasRequestBody): JsValue = {
    import model._

    val jsObjects : ListBuffer[JsObject] = ListBuffer.empty[JsObject]

    if (income.nonEmpty) {
      jsObjects += Json.obj("income" -> incomeJson(income.get))
    }

    if (additions.nonEmpty) {
      jsObjects += Json.obj("additions" -> additionsMtdJson(additions.get))
    }

    if (expenses.nonEmpty) {
      jsObjects += Json.obj("expenses" -> expensesMtdJson(expenses.get))
    }

    val json = jsObjects.fold(Json.parse("""{}""").as[JsObject])((a: JsObject, b: JsObject) => a ++ b)
    json
  }

  "SubmitSelfEmploymentBsasRequestBody" when {
    "read from valid JSON" should {
      "produce the expected SubmitSelfEmploymentBsasRequestBody object" in {
        submitSelfEmploymentBsasRequestBodyDesJson(submitSelfEmploymentBsasRequestBodyModel).as[SubmitSelfEmploymentBsasRequestBody] shouldBe
          submitSelfEmploymentBsasRequestBodyModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(submitSelfEmploymentBsasRequestBodyModel) shouldBe submitSelfEmploymentBsasRequestBodyMtdJson(submitSelfEmploymentBsasRequestBodyModel)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        submitSelfEmploymentBsasRequestBodyDesJson(submitSelfEmploymentBsasRequestBodyModelWithoutIncome).as[SubmitSelfEmploymentBsasRequestBody] shouldBe
          submitSelfEmploymentBsasRequestBodyModelWithoutIncome
      }

      "not write those fields to JSON" in {
        Json.toJson(submitSelfEmploymentBsasRequestBodyModel) shouldBe submitSelfEmploymentBsasRequestBodyMtdJson(submitSelfEmploymentBsasRequestBodyModel)
      }
    }


    "no fields as supplied" should {
      "read to an empty SubmitSelfEmploymentBsasRequestBody object" in {
        submitSelfEmploymentBsasRequestBodyDesJson(emptySubmitSelfEmploymentBsasRequestBodyModel).as[SubmitSelfEmploymentBsasRequestBody] shouldBe
          emptySubmitSelfEmploymentBsasRequestBodyModel
      }

      "write to empty JSON" in {
        Json.toJson(emptySubmitSelfEmploymentBsasRequestBodyModel) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }
}
