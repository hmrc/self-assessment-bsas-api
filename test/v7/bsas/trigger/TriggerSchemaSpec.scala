/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.bsas.trigger

import cats.data.Validated.{Invalid, Valid}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.libs.json.{JsObject, Json}
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.models.errors.{EndDateFormatError, RuleIncorrectOrEmptyBodyError}
import shared.utils.UnitSpec

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

      "use Def1 for tax years before 2025-26" in {
        forTaxYearsBefore(TaxYear.fromMtd("2025-26")) { taxYear =>
          TriggerSchema.schemaFor(body(taxYear.endDate)) shouldBe Valid(TriggerSchema.Def1)
        }
      }

      "use Def2 for tax years from 2025-26" in {
        forTaxYearsFrom(TaxYear.fromMtd("2025-26")) { taxYear =>
          TriggerSchema.schemaFor(body(taxYear.endDate)) shouldBe Valid(TriggerSchema.Def2)
        }
      }
    }

    "An endDate is not in the body" must {
      "return a RuleIncorrectOrEmptyBodyError" in {
        TriggerSchema.schemaFor(JsObject.empty) shouldBe Invalid(Seq(RuleIncorrectOrEmptyBodyError.withPath("/accountingPeriod/endDate")))
      }
    }

    "An invalid endDate is in the body" must {
      "return a EndDateFormatError" in {
        val body = Json.parse(s"""
                                                     |{
                                                     |  "accountingPeriod": {
                                                     |    "endDate": "NotADate"
                                                     |  }
                                                     |}
          """.stripMargin)

        TriggerSchema.schemaFor(body) shouldBe Invalid(Seq(EndDateFormatError))
      }
    }

  }

}
