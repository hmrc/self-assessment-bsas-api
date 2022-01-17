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

package v3.fixtures.selfEmployment

import play.api.libs.json.{JsValue, Json}
import v2.models.request.submitBsas.selfEmployment.{Income, queryMap}

object IncomeFixture {

  val incomeModel: Income =
    Income(
      turnover = Some(1000.25),
      other = Some(1000.50)
    )

  def incomeJson(model: Income): JsValue = {
    import model._

    val fields: Map[String, Option[BigDecimal]] =
      Map(
        "turnover" -> turnover,
        "other" -> other
      )

    Json.toJsObject(queryMap(fields))
  }
}
