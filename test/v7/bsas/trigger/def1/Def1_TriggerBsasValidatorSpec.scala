/*
 * Copyright 2026 HM Revenue & Customs
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

package v7.bsas.trigger.def1

import api.models.domain.Nino
import api.models.errors.*
import api.utils.UnitSpec
import common.errors.*
import config.MockBsasConfig
import play.api.libs.json.{JsObject, JsValue, Json}
import v7.bsas.trigger.def1.model.request.{Def1_TriggerBsasRequestBody, Def1_TriggerBsasRequestData}
import v7.bsas.trigger.model.TriggerBsasRequestData

class Def1_TriggerBsasValidatorSpec extends UnitSpec with MockBsasConfig {

  private implicit val correlationId: String = "1234"

  private val validNino  = "AA123456A"
  private val parsedNino = Nino(validNino)

  private def validator(nino: String, body: JsValue) = new Def1_TriggerBsasValidator(nino, body)

  private def triggerBsasRequestJson(startDate: String = "2021-04-06",
                                     endDate: String = "2022-04-05",
                                     typeOfBusiness: String = "self-employment",
                                     businessId: String = "XAIS12345678901"): JsObject = {
    Json.obj(
      "accountingPeriod" -> Json.obj("startDate" -> startDate, "endDate" -> endDate),
      "typeOfBusiness"   -> typeOfBusiness,
      "businessId"       -> businessId
    )
  }

  private trait Test {
    MockedBsasConfig.v3TriggerForeignBsasMinimumTaxYear.returns("2021-22").anyNumberOfTimes()
    MockedBsasConfig.v3TriggerNonForeignBsasMinimumTaxYear.returns("2019-20").anyNumberOfTimes()
  }

  "running validation" should {
    "return the parsed domain object" when {
      List(
        ("self-employment", "2019-04-06", "2020-04-05"),
        ("uk-property-fhl", "2019-04-06", "2020-04-05"),
        ("uk-property", "2019-04-06", "2020-04-05"),
        ("foreign-property-fhl-eea", "2021-04-06", "2022-04-05"),
        ("foreign-property", "2021-04-06", "2022-04-05")
      ).foreach { case (typeOfBusiness, startDate, endDate) =>
        s"$typeOfBusiness is supplied" in new Test {
          val body: JsObject = triggerBsasRequestJson(
            startDate = startDate,
            endDate = endDate,
            typeOfBusiness = typeOfBusiness
          )

          val expectedBody: Def1_TriggerBsasRequestBody = body.as[Def1_TriggerBsasRequestBody]

          val result: Either[ErrorWrapper, TriggerBsasRequestData] = validator(validNino, body).validateAndWrapResult()

          result shouldBe Right(Def1_TriggerBsasRequestData(parsedNino, expectedBody))
        }
      }
    }

    "return NinoFormatError" when {
      "the nino is invalid" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] = validator("not-a-nino", triggerBsasRequestJson()).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return a StartDateFormatError" when {
      "the start date format is incorrect" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(startDate = "06-05-2019")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, StartDateFormatError)
        )
      }

      "the start date is before the min start date" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(startDate = "1890-05-23")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, StartDateFormatError)
        )
      }
    }

    "return an EndDateFormatError" when {
      "the end date format is incorrect" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(endDate = "06-05-2020")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, EndDateFormatError)
        )
      }

      "the end date is after the max end date" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(endDate = "2101-05-20")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, EndDateFormatError)
        )
      }
    }

    "return a TypeOfBusinessFormatError" when {
      "an incorrect business type is given" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(typeOfBusiness = "not-a-type-of-business")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, TypeOfBusinessFormatError)
        )
      }
    }

    "return a BusinessIdFormatError" when {
      "a business id is provided with wrong formatting" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(businessId = "not-a-business-id")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, BusinessIdFormatError)
        )
      }
    }

    "return a RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] = validator(validNino, JsObject.empty).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "mandatory fields are missing" in new Test {
        val requestJs: JsObject                                  = Json.obj("accountingPeriod" -> Json.obj("endDate" -> "2022-04-05"))
        val result: Either[ErrorWrapper, TriggerBsasRequestData] = validator(validNino, requestJs).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPaths(List("/accountingPeriod/startDate", "/businessId", "/typeOfBusiness")))
        )
      }
    }

    "return a RuleEndBeforeStartDateError" when {
      "the end date is before the start date" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(startDate = "2022-05-07")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleEndBeforeStartDateError)
        )
      }
    }

    "return a RuleAccountingPeriodNotSupportedError" when {
      List(
        ("self-employment", "2018-04-06", "2019-04-05"),
        ("uk-property-fhl", "2018-04-06", "2019-04-05"),
        ("uk-property", "2018-04-06", "2019-04-05"),
        ("foreign-property-fhl-eea", "2020-04-06", "2021-04-05"),
        ("foreign-property", "2020-04-06", "2021-04-05")
      ).foreach { case (typeOfBusiness, startDate, endDate) =>
        s"the accounting period is before the minimum tax year and typeOfBusiness is $typeOfBusiness" in new Test {
          val body: JsObject = triggerBsasRequestJson(
            startDate = startDate,
            endDate = endDate,
            typeOfBusiness = typeOfBusiness
          )

          val result: Either[ErrorWrapper, TriggerBsasRequestData] = validator(validNino, body).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, RuleAccountingPeriodNotSupportedError))
        }
      }
    }

    "return a RuleAccountingPeriodNotAlignedError" when {
      "the accounting period does not align to a complete tax year" in new Test {
        val result: Either[ErrorWrapper, TriggerBsasRequestData] =
          validator(validNino, triggerBsasRequestJson(startDate = "2021-04-07")).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleAccountingPeriodNotAlignedError))
      }
    }

    "return multiple errors" when {
      "the request body has multiple issues" in new Test {
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

}
