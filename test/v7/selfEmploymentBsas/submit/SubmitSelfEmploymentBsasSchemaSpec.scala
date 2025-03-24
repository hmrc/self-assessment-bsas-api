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

package v7.selfEmploymentBsas.submit

import cats.data.Validated.{Invalid, Valid}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.models.errors.{RuleTaxYearRangeInvalidError, TaxYearFormatError}
import shared.utils.UnitSpec

class SubmitSelfEmploymentBsasSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "schema lookup" when {
    "a valid tax year is present" must {
      "use Def1 for pre-TYS tax years" in {
        forPreTysTaxYears { taxYear =>
          SubmitSelfEmploymentBsasSchema.schemaFor(taxYear.asMtd) shouldBe Valid(SubmitSelfEmploymentBsasSchema.Def1)
        }
      }

      "use Def1 for tax year 2023-24" in {
        SubmitSelfEmploymentBsasSchema.schemaFor(TaxYear.fromMtd("2023-24")) shouldBe Valid(SubmitSelfEmploymentBsasSchema.Def1)
      }

      "use Def2 for tax years 2024-25 onwards" in {
        forTaxYearsFrom(TaxYear.fromMtd("2024-25")) { taxYear =>
          SubmitSelfEmploymentBsasSchema.schemaFor(taxYear.asMtd) shouldBe Valid(SubmitSelfEmploymentBsasSchema.Def2)
        }
      }
    }

    "the tax year is present but not valid" must {
      "return a TaxYearFormatError for a badly formatted tax year" in {
        SubmitSelfEmploymentBsasSchema.schemaFor("NotATaxYear") shouldBe Invalid(Seq(TaxYearFormatError))
      }

      "return a RuleTaxYearRangeInvalidError for an invalid tax year range" in {
        SubmitSelfEmploymentBsasSchema.schemaFor("2020-99") shouldBe Invalid(Seq(RuleTaxYearRangeInvalidError))
      }
    }
  }

}
