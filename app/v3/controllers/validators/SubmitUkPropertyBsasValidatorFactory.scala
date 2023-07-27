/*
 * Copyright 2023 HM Revenue & Customs
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

package v3.controllers.validators

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ ResolveCalculationId, ResolveNino, ResolveNonEmptyJsonObject, ResolveTysTaxYear }
import api.models.errors.MtdError
import play.api.libs.json.JsValue
import v3.models.errors.RuleBothPropertiesSuppliedError
import v3.models.request.submitBsas.ukProperty.{ SubmitUKPropertyBsasRequestBody, SubmitUkPropertyBsasRequestData }

import javax.inject.{ Inject, Singleton }
import scala.annotation.nowarn

@Singleton
class SubmitUkPropertyBsasValidatorFactory @Inject()(rulesValidatorFactory: SubmitUkPropertyBsasRulesValidatorFactory) {

  @nowarn("cat=lint-byname-implicit")
  private val resolveJson = new ResolveNonEmptyJsonObject[SubmitUKPropertyBsasRequestBody]()

  def validator(nino: String, calculationId: String, taxYear: Option[String], body: JsValue): Validator[SubmitUkPropertyBsasRequestData] =
    new Validator[SubmitUkPropertyBsasRequestData] {

      def validate: Either[Seq[MtdError], SubmitUkPropertyBsasRequestData] = {
        val resolvedNino          = ResolveNino(nino)
        val resolvedCalculationId = ResolveCalculationId(calculationId)
        val resolvedTaxYear       = ResolveTysTaxYear(taxYear)
        val resolvedBody          = resolveJson(body)

        val validatedOneProperty = validateOnePropertyOnly(body)

        val result: Either[Seq[MtdError], SubmitUkPropertyBsasRequestData] = for {
          nino          <- resolvedNino
          calculationId <- resolvedCalculationId
          maybeTaxYear  <- resolvedTaxYear
          body          <- resolvedBody
          _             <- validatedOneProperty
          parsed = SubmitUkPropertyBsasRequestData(nino, calculationId, maybeTaxYear, body)
          _ <- rulesValidatorFactory.validator(parsed).validate
        } yield {
          parsed
        }

        mapResult(
          result,
          possibleErrors = resolvedNino,
          resolvedCalculationId,
          resolvedTaxYear,
          resolvedBody,
          validatedOneProperty
        )
      }
    }

  private def validateOnePropertyOnly(jsBody: JsValue): Either[List[RuleBothPropertiesSuppliedError.type], Unit] =
    if ((jsBody \ "furnishedHolidayLet").isDefined && (jsBody \ "nonFurnishedHolidayLet").isDefined)
      Left(List(RuleBothPropertiesSuppliedError))
    else
      Right(())

}
