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

package v6.bsas.list

import cats.data.Validated.{Invalid, Valid}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport, TaxYearTestSupport}
import shared.models.errors.{RuleTaxYearRangeInvalidError, TaxYearFormatError}
import shared.utils.UnitSpec

class ListBsasSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport with TaxYearTestSupport {

  "schema lookup" when {
    "a tax year is present" must {
      "use Def1 for tax years before 2025-26" in {
        forTaxYearsBefore(TaxYear.fromMtd("2025-26")) { taxYear =>
          ListBsasSchema.schemaFor(Some(taxYear.asMtd)) shouldBe Valid(ListBsasSchema.Def1)
        }
      }

      "use Def2 for tax years from 2025-26" in {
        forTaxYearsFrom(TaxYear.fromMtd("2025-26")) { taxYear =>
          ListBsasSchema.schemaFor(Some(taxYear.asMtd)) shouldBe Valid(ListBsasSchema.Def2)
        }
      }
    }

    "no tax year is present" must {
      "use the same schema as for the current tax year" in {
        ListBsasSchema.schemaFor(None)(clockAtTimeInTaxYear(TaxYear.fromMtd("2024-25"))) shouldBe Valid(ListBsasSchema.Def1)
        ListBsasSchema.schemaFor(None)(clockAtTimeInTaxYear(TaxYear.fromMtd("2025-26"))) shouldBe Valid(ListBsasSchema.Def2)
      }

    }

    "the tax year is present but not valid" when {
      "the tax year format is invalid" must {
        "return a TaxYearFormatError" in {
          ListBsasSchema.schemaFor(Some("NotATaxYear")) shouldBe Invalid(Seq(TaxYearFormatError))
        }
      }

      "the tax year range is invalid" must {
        "return a RuleTaxYearRangeInvalidError" in {
          ListBsasSchema.schemaFor(Some("2020-99")) shouldBe Invalid(Seq(RuleTaxYearRangeInvalidError))
        }
      }
    }
  }

}
