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

package v2.controllers.requestParsers.validators

import java.time.LocalDate

import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v2.mocks.MockCurrentDateProvider
import v2.models.errors._
import v2.models.request.triggerBsas.TriggerBsasRawData


class TriggerBSASValidatorSpec extends UnitSpec {

  val nino = "AA123456A"

  def triggerBsasRawDataBody(startDate: String = "2021-05-05",
                             endDate: String = "2022-05-06",
                             typeOfBusiness: String = "self-employment",
                             businessId: String = "XAIS12345678901"): AnyContentAsJson = {

    AnyContentAsJson(
      Json.obj("accountingPeriod" -> Json.obj("startDate" -> startDate, "endDate" -> endDate),
        "typeOfBusiness"   -> typeOfBusiness,
        "businessId"       -> businessId)
    )
  }

  class SetUp(date: LocalDate = LocalDate.of(2020, 6, 18)) extends MockCurrentDateProvider {
    val validator = new TriggerBSASValidator(currentDateProvider = mockCurrentDateProvider)

    MockCurrentDateProvider.getCurrentDate().returns(date)
  }

  "running validation" should {
    "return no errors" when {

      List(
        "self-employment",
        "uk-property-fhl",
        "uk-property-non-fhl",
        "foreign-property-fhl-eea",
        "foreign-property"
      ).foreach { typeOfBusiness =>
        s"$typeOfBusiness is supplied" in new SetUp {
          validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(typeOfBusiness = typeOfBusiness))) shouldBe Nil
        }
      }

    }

    "return a FORMAT_NINO error" when {
      "the nino is invalid" in new SetUp {
        val result = validator.validate(TriggerBsasRawData("123456789", triggerBsasRawDataBody()))

        result shouldBe List(NinoFormatError)
      }
    }

    "return a FORMAT_START_DATE error" when {
      "the start date format is incorrect" in new SetUp() {
        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(startDate = "06-05-2019")))

        result shouldBe List(StartDateFormatError)
      }
    }

    "return a FORMAT_END_DATE error" when {
      "the end date format is incorrect" in new SetUp() {
        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(endDate = "06-05-2020")))

        result shouldBe List(EndDateFormatError)
      }
    }

    "return a FORMAT_TYPE_OF_BUSINESS Error" when {
      "a incorrect business type is given" in new SetUp() {
        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(typeOfBusiness = "nonsense typeOfBusiness")))

        result shouldBe List(TypeOfBusinessFormatError)
      }
    }

    "return a FORMAT_BUSINESS_ID error" when {
      "a business id is provided with wrong formatting" in new SetUp() {
        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(businessId = "nonsense businessId")))

        result shouldBe List(BusinessIdFormatError)
      }
    }

    "return a RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED error" when {
      "an empty body is submitted" in new SetUp() {
        val result = validator.validate(TriggerBsasRawData(nino, AnyContentAsJson(Json.obj())))

        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "mandatory fields are missing" in new SetUp() {
        val result = validator.validate(
          TriggerBsasRawData(nino,
            AnyContentAsJson(
              Json.obj("accountingPeriod" -> Json.obj("endDate" -> "2020-05-06"))
            )))

        result shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/accountingPeriod/startDate", "/typeOfBusiness", "/businessId"))))
      }
    }

    "return a RULE_END_DATE_BEFORE_START_DATE error" when {
      "the end date is before the start date" in new SetUp {
        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(startDate = "2022-05-07")))

        result shouldBe List(RuleEndBeforeStartDateError)
      }
    }

    "return a RULE_ACCOUNTING_PERIOD_NOT_SUPPORTED error" when {
      "the accounting period is before the minimum tax year" in new SetUp() {
        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(startDate = "2015-05-05", endDate = "2016-05-06")))

        result shouldBe List(RuleAccountingPeriodNotSupportedError)
      }
    }

    "return multiple errors" when {
      "a request has muliple issues with the data" in new SetUp() {
        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(typeOfBusiness = "", businessId = "")))

        result shouldBe List(BusinessIdFormatError, TypeOfBusinessFormatError)
      }
    }
  }
}