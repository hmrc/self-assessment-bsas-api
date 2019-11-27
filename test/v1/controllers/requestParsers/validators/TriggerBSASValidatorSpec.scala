/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import java.time.LocalDate

import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v1.mocks.MockCurrentDateProvider
import v1.models.domain.TypeOfBusiness
import v1.models.errors._
import v1.models.request.triggerBsas.TriggerBsasRawData

class TriggerBSASValidatorSpec extends UnitSpec {

  val nino = "AA123456A"

  def triggerBsasRawDataBody(accountingPeriod: String = "2019-20",
                             startDate: String = "2019-05-05",
                             endDate: String = "2020-05-06",
                             typeOfBusiness: String = TypeOfBusiness.`self-employment`.toString,
                             selfEmploymentId: Option[String] = Some("XAIS12345678901")): AnyContentAsJson = {


    AnyContentAsJson(Json.obj(
      "accountingPeriod" -> accountingPeriod,
      "startDate" -> startDate,
      "endDate" -> endDate,
      "typeOfBusiness" -> typeOfBusiness
    ) ++ selfEmploymentId.fold(Json.obj())(selfEmploymentId => Json.obj("selfEmploymentId" -> selfEmploymentId)))
  }

  class SetUp(date:LocalDate = LocalDate.of(2020, 6, 18)) extends MockCurrentDateProvider {
    val validator = new TriggerBSASValidator(currentDateProvider = mockCurrentDateProvider)

    MockCurrentDateProvider.getCurrentDate().returns(date)
  }


  "running validation" should {
    "return no errors" when {

      "a valid self employment is supplied" in new SetUp {

        validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody())).isEmpty shouldBe true
      }

      "a valid property is supplied" in new SetUp {

        validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`.toString,
          selfEmploymentId = None))).isEmpty shouldBe true
      }

      "a valid fhl-property is supplied" in new SetUp {

        validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`.toString,
          selfEmploymentId = None))).isEmpty shouldBe true
      }

    }

    "return a EndBeforeStartDate error" when {
      "the end date is before the start date" in new SetUp {

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(startDate = "2020-05-07")))

        result.length shouldBe 1
        result shouldBe List(EndBeforeStartDateError)
      }
    }

    "return a EndDateFormat error" when {
      "the end date format is incorrect" in new SetUp(){

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(endDate = "06-05-2020")))

        result.length shouldBe 1
        result shouldBe List(EndDateFormatError)
      }

    }

    "return a JsonFormat error" when {
      "there is no body" in new SetUp() {

        val result = validator.validate(TriggerBsasRawData(nino, AnyContentAsJson(Json.obj())))

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
    }

    "return a MinTaxYear error" when {
      "the tax year is before the minmal tax year that can be applied" in new SetUp {

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(accountingPeriod = "2010-11")))

        result.length shouldBe 1
        result shouldBe List(RuleTaxYearNotSupportedError)
      }
    }

    "return a TaxYearFormat error" when {
      "the format of the tax year does not match YYYY-YY format" in new SetUp {

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(accountingPeriod = "1998-1999")))

        result.length shouldBe 1
        result shouldBe List(TaxYearFormatError)
      }

      "the tax year is longer then a year" in new SetUp {

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(accountingPeriod = "2018-20")))

        result.length shouldBe 1
        result shouldBe List(RuleTaxYearRangeExceededError)
      }
    }

    "return a NinoFormat error" when {
      "the nino is invalid" in new SetUp {

        val result = validator.validate(TriggerBsasRawData("123456789", triggerBsasRawDataBody()))

        result.length shouldBe 1
        result shouldBe List(NinoFormatError)
      }
    }

    "return a NotEndedBefore error" when {
      "the end date for the accounting period is in the future" in new SetUp(LocalDate.of(2020, 5, 4)) {

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody()))

        result.length shouldBe 1
        result shouldBe List(PeriodNotEndedError)
      }
    }

    "return a selfEmploymentIdRule error" when {
      "a valid self employment is supplied with no self employment ID value" in new SetUp() {

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(selfEmploymentId = None)))

        result.length shouldBe 1
        result shouldBe List(SelfEmploymentIdRuleError)
      }

      "a valid property is supplied with an self employed id" in new SetUp {

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`.toString)))

        result.length shouldBe 1
        result shouldBe List(SelfEmploymentIdRuleError)
      }
    }

    "return a SelfEmploymentIdValue error" when {
      "a self emploment id is provided with wrong formatting" in new SetUp() {

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(selfEmploymentId = Some("XISS2646382"))))

        result.length shouldBe 1
        result shouldBe List(SelfEmploymentIdFormatError)
      }

    }

    "return a StartDateFormat error" when {
      "the start date format is incorrect" in new SetUp(){

        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(startDate = "06-05-2019")))

        result.length shouldBe 1
        result shouldBe List(StartDateFormatError)
      }
    }

    "return a TypeOfBusinessFormat Error" when {
      "a incorrect business type is given" in new SetUp() {
        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(typeOfBusiness = "selfEmployed")))

        result.length shouldBe 1
        result shouldBe List(TypeOfBusinessFormatError)
      }
    }

    "return multiple errors" when {
      "a request has muliple issues with the data" in new SetUp(LocalDate.of(2020, 5, 4)) {
        val result = validator.validate(TriggerBsasRawData(nino, triggerBsasRawDataBody(selfEmploymentId = None)))

        result.length shouldBe 2
        result contains List(SelfEmploymentIdFormatError, PeriodNotEndedError)
      }
    }
  }
}
