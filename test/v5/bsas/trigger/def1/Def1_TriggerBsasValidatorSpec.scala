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

package v5.bsas.trigger.def1

import cats.data.Validated
import cats.data.Validated.Valid
import common.errors.{RuleAccountingPeriodNotSupportedError, TypeOfBusinessFormatError}
import config.MockBsasConfig
import play.api.libs.json.{JsObject, JsValue, Json}
import shared.models.domain.Nino
import shared.models.errors._
import shared.utils.UnitSpec
import v5.bsas.trigger.def1.model.request.{Def1_TriggerBsasRequestBody, Def1_TriggerBsasRequestData}
import v5.bsas.trigger.model.TriggerBsasRequestData
import v5.common.model.TypeOfBusiness

class Def1_TriggerBsasValidatorSpec extends UnitSpec with MockBsasConfig {

  private implicit val correlationId: String = "1234"

  private val validNino  = "AA123456A"
  private val parsedNino = Nino(validNino)

  private def validator(nino: String, body: JsValue) = new Def1_TriggerBsasValidator(nino, body)

  private def triggerBsasRequestJson(startDate: String = "2021-05-05",
                                     endDate: String = "2022-05-06",
                                     typeOfBusiness: String = "self-employment",
                                     businessId: String = "XAIS12345678901"): JsObject = {
    Json.obj(
      "accountingPeriod" -> Json.obj("startDate" -> startDate, "endDate" -> endDate),
      "typeOfBusiness"   -> typeOfBusiness,
      "businessId"       -> businessId
    )
  }

  class SetUp {
    MockedBsasConfig.v3TriggerForeignBsasMinimumTaxYear.returns("2021-22").anyNumberOfTimes()
    MockedBsasConfig.v3TriggerNonForeignBsasMinimumTaxYear.returns("2019-20").anyNumberOfTimes()
  }

  "running validation" should {
    "return the parsed domain object" when {
      List(
        "self-employment",
        "uk-property-fhl",
        "uk-property-non-fhl",
        "foreign-property-fhl-eea",
        "foreign-property"
      ).foreach { typeOfBusiness =>
        s"$typeOfBusiness is supplied" in new SetUp {
          val body: JsObject                            = triggerBsasRequestJson(typeOfBusiness = typeOfBusiness)
          val expectedBody: Def1_TriggerBsasRequestBody = body.as[Def1_TriggerBsasRequestBody]

          val result: Validated[Seq[MtdError], TriggerBsasRequestData] = validator(validNino, body).validate
          result shouldBe Valid(Def1_TriggerBsasRequestData(parsedNino, expectedBody))
        }
      }
    }

    "return NinoFormatError" when {
      "the nino is invalid" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] = validator("not-a-nino", triggerBsasRequestJson()).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return a StartDateFormatError" when {
      "the start date format is incorrect" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(startDate = "06-05-2019")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, StartDateFormatError)
        )
      }

      "the start date is before the min start date" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(startDate = "1890-05-23")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, StartDateFormatError)
        )
      }
    }

    "return an EndDateFormatError" when {
      "the end date format is incorrect" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(endDate = "06-05-2020")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, EndDateFormatError)
        )
      }

      "the end date is after the max end date" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(endDate = "2101-05-20")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, EndDateFormatError)
        )
      }
    }

    "return a TypeOfBusinessFormatError" when {
      "an incorrect business type is given" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(typeOfBusiness = "not-a-type-of-business")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, TypeOfBusinessFormatError)
        )
      }
    }

    "return a BusinessIdFormatError" when {
      "a business id is provided with wrong formatting" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(businessId = "not-a-business-id")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, BusinessIdFormatError)
        )
      }
    }

    "return a RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] = validator(validNino, JsObject.empty).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "mandatory fields are missing" in new SetUp {
        val requestJs: JsObject                                  = Json.obj("accountingPeriod" -> Json.obj("endDate" -> "2020-05-06"))
        val result: Either[ErrorWrapper, TriggerBsasRequestData] = validator(validNino, requestJs).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPaths(List("/accountingPeriod/startDate", "/businessId", "/typeOfBusiness")))
        )
      }
    }

    "return a RuleEndBeforeStartDateError" when {
      "the end date is before the start date" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(startDate = "2022-05-07")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleEndBeforeStartDateError)
        )
      }
    }

    "return RuleTaxYearNotSupportedError" when {
      "given endDate is after 2025-04-05" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(endDate = "2025-05-07")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }

    "return a RuleEndBeforeStartDateError" when {
      "the end date is equal to start date" in new SetUp {
        val body: JsObject                            = triggerBsasRequestJson(startDate = "2022-05-07", endDate = "2022-05-07")
        val expectedBody: Def1_TriggerBsasRequestBody = body.as[Def1_TriggerBsasRequestBody]
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, body).validateAndWrapResult()

        result shouldBe Right(Def1_TriggerBsasRequestData(parsedNino, expectedBody))
      }
    }

    "return a RuleAccountingPeriodNotSupportedError" when {
      "the accounting period is before the minimum tax year" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(startDate = "2015-05-05", endDate = "2016-05-06")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleAccountingPeriodNotSupportedError)
        )
      }
    }

    "return multiple errors" when {
      "the request body has muliple issues" in new SetUp {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(typeOfBusiness = "", businessId = "")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(BusinessIdFormatError, TypeOfBusinessFormatError))
          )
        )
      }
    }
  }

  "the Accounting Period validation" should {
    "return no errors" when {
      "passed the correct Accounting Period dates for each Type of Business" when {
        List(
          (TypeOfBusiness.`self-employment`, "2019-04-06"),
          (TypeOfBusiness.`uk-property-fhl`, "2019-04-06"),
          (TypeOfBusiness.`uk-property-non-fhl`, "2019-04-06"),
          (TypeOfBusiness.`foreign-property-fhl-eea`, "2021-04-06"),
          (TypeOfBusiness.`foreign-property`, "2021-04-06")
        ).foreach { case (typeOfBusiness, endDate) =>
          s"typeOfBusiness is $typeOfBusiness and the endDate is after the allowed end date" in new SetUp {
            private val body         = triggerBsasRequestJson(typeOfBusiness = typeOfBusiness.toString, startDate = "2019-01-01", endDate = endDate)
            private val expectedBody = body.as[Def1_TriggerBsasRequestBody]

            val result: Validated[Seq[MtdError], TriggerBsasRequestData] = validator(validNino, body).validate
            result shouldBe Valid(Def1_TriggerBsasRequestData(parsedNino, expectedBody))
          }
        }
      }

      "return RuleAccountingPeriodNotSupported" when {
        "passed incorrect Accounting Period dates for each Type of Business" when {
          List(
            (TypeOfBusiness.`self-employment`, "2019-04-05"),
            (TypeOfBusiness.`uk-property-fhl`, "2019-04-05"),
            (TypeOfBusiness.`uk-property-non-fhl`, "2019-04-05"),
            (TypeOfBusiness.`foreign-property-fhl-eea`, "2021-04-05"),
            (TypeOfBusiness.`foreign-property`, "2021-04-05")
          ).foreach { case (typeOfBusiness, endDate) =>
            s"typeOfBusiness is $typeOfBusiness and endDate is before the earliest allowed end date" in new SetUp {
              private val body = triggerBsasRequestJson(typeOfBusiness = typeOfBusiness.toString, startDate = "2019-01-01", endDate = endDate)

              val result: Either[ErrorWrapper, TriggerBsasRequestData] = validator(validNino, body).validateAndWrapResult()

              result shouldBe Left(
                ErrorWrapper(correlationId, RuleAccountingPeriodNotSupportedError)
              )
            }
          }
        }
      }
    }
  }

}
