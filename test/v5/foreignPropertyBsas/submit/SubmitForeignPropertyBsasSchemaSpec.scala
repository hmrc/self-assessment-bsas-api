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

package v5.foreignPropertyBsas.submit

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.utils.UnitSpec

class SubmitForeignPropertyBsasSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "schema lookup" when {
    "a tax year is present" must {
      "use Def2 for tax years from 2024-25" in {
        forTaxYearsFrom(TaxYear.fromMtd("2024-25")) { taxYear =>
          SubmitForeignPropertyBsasSchema.schemaFor(Some(taxYear.asMtd)) shouldBe SubmitForeignPropertyBsasSchema.Def2
        }
      }
      "use Def1 for tax years before 2023-24" in {
        forTaxYearsBefore(TaxYear.fromMtd("2023-24")) { taxYear =>
          SubmitForeignPropertyBsasSchema.schemaFor(Some(taxYear.asMtd)) shouldBe SubmitForeignPropertyBsasSchema.Def1
        }
      }

      "use Def1 for pre-TYS tax years" in {
        forPreTysTaxYears { taxYear =>
          SubmitForeignPropertyBsasSchema.schemaFor(Some(taxYear.asMtd)) shouldBe SubmitForeignPropertyBsasSchema.Def1
        }
      }
    }

    "the tax year is present but not valid" must {
      "use a default of Def1" in {
        SubmitForeignPropertyBsasSchema.schemaFor(Some("NotATaxYear")) shouldBe SubmitForeignPropertyBsasSchema.Def1
      }
    }

    "no tax year is present" must {
      "use a default of Def1" in {
        SubmitForeignPropertyBsasSchema.schemaFor(None) shouldBe SubmitForeignPropertyBsasSchema.Def1
      }
    }
  }

}
