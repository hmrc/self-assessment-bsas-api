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
import api.controllers.validators.resolvers._
import api.models.domain.Nino
import api.models.errors.MtdError
import config.AppConfig
import play.api.libs.json.JsValue
import v3.controllers.validators.resolvers.ResolveTypeOfBusiness
import v3.models.domain.TypeOfBusiness
import v3.models.errors.RuleAccountingPeriodNotSupportedError
import v3.models.request.triggerBsas.{ TriggerBsasRequestBody, TriggerBsasRequestData }

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.{ Inject, Singleton }
import scala.annotation.nowarn

@Singleton
class TriggerBsasValidatorFactory @Inject()(appConfig: AppConfig) {

  @nowarn("cat=lint-byname-implicit")
  private val resolveJson = new ResolveNonEmptyJsonObject[TriggerBsasRequestBody]()

  private lazy val foreignPropertyEarliestEndDate: LocalDate = LocalDate.parse(
    s"${appConfig.v3TriggerForeignBsasMinimumTaxYear.dropRight(3)}-04-06",
    DateTimeFormatter.ISO_LOCAL_DATE
  )

  private lazy val selfEmploymentAndUkPropertyEarliestEndDate: LocalDate = LocalDate.parse(
    s"${appConfig.v3TriggerNonForeignBsasMinimumTaxYear.dropRight(3)}-04-06",
    DateTimeFormatter.ISO_LOCAL_DATE
  )

  def validator(nino: String, body: JsValue): Validator[TriggerBsasRequestData] =
    new Validator[TriggerBsasRequestData] {

      def validate: Either[Seq[MtdError], TriggerBsasRequestData] = {
        val resolvedNino = ResolveNino(nino)
        val resolvedBody = resolveJson(body)

        val result: Either[Seq[MtdError], TriggerBsasRequestData] = flatten(for {
          nino <- resolvedNino
          body <- resolvedBody
        } yield validateParsedRequestBody(nino, body))

        mapResult(result, possibleErrors = resolvedNino, resolvedBody)
      }

      private def validateParsedRequestBody(nino: Nino, body: TriggerBsasRequestBody): Either[Seq[MtdError], TriggerBsasRequestData] = {
        val parsed = TriggerBsasRequestData(nino, body)
        import parsed.body.accountingPeriod._

        val resolvedBusinessId     = ResolveBusinessId(body.businessId)
        val resolvedTypeOfBusiness = ResolveTypeOfBusiness(body.typeOfBusiness)
        val resolvedDateRange      = ResolveDateRange(startDate -> endDate)

        val bodyValidationResult = for {
          _              <- resolvedBusinessId
          typeOfBusiness <- resolvedTypeOfBusiness
          dateRange      <- resolvedDateRange
          _              <- validateAccountingPeriodNotSupported(typeOfBusiness, dateRange.endDate)
        } yield parsed

        mapResult(bodyValidationResult, possibleErrors = resolvedBusinessId, resolvedTypeOfBusiness, resolvedDateRange)
      }

      private def validateAccountingPeriodNotSupported(typeOfBusiness: TypeOfBusiness, endDate: LocalDate): Either[Seq[MtdError], Unit] = {
        val earliestDate: LocalDate = typeOfBusiness match {
          case TypeOfBusiness.`self-employment` | TypeOfBusiness.`uk-property-fhl` | TypeOfBusiness.`uk-property-non-fhl` =>
            selfEmploymentAndUkPropertyEarliestEndDate
          case TypeOfBusiness.`foreign-property-fhl-eea` | TypeOfBusiness.`foreign-property` =>
            foreignPropertyEarliestEndDate
        }

        if (endDate.isBefore(earliestDate)) {
          Left(List(RuleAccountingPeriodNotSupportedError))
        } else {
          Right(())
        }
      }
    }

}
