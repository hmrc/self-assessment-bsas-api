/*
 * Copyright 2024 HM Revenue & Customs
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

package v5.bsas.trigger

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.libs.json.{JsObject, Json}
import shared.UnitSpec
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}

import java.time.LocalDate

class TriggerSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "Getting a schema" when {
    "endDate is in body" must {
      def body(endDate: LocalDate) = Json.parse(s"""
          |{
          |  "accountingPeriod": {
          |    "endDate": "$endDate"
          |  }
          |}
          """.stripMargin)

      "use Def1 for tax years from 2023-24" in {
        forTaxYearsFrom(TaxYear.fromMtd("2023-24")) { taxYear =>
          TriggerSchema.schemaFor(body(taxYear.endDate)) shouldBe TriggerSchema.Def1
        }
      }

      "use Def1 for pre-TYS tax years" in {
        forPreTysTaxYears { taxYear =>
          TriggerSchema.schemaFor(body(taxYear.endDate)) shouldBe TriggerSchema.Def1
        }
      }
    }

    "An endDate is not in the body" must {
      "use a default of Def1" in {
        TriggerSchema.schemaFor(JsObject.empty) shouldBe TriggerSchema.Def1
      }
    }

    "An invalid endDate is in the body" must {
      "use a default of Def1" in {
        val body = Json.parse(s"""
                                                     |{
                                                     |  "accountingPeriod": {
                                                     |    "endDate": "NotADate"
                                                     |  }
                                                     |}
          """.stripMargin)

        TriggerSchema.schemaFor(body) shouldBe TriggerSchema.Def1
      }
    }

  }

}
