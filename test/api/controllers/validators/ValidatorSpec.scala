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

package api.controllers.validators

import api.controllers.validators.resolvers.{ ResolveJsonObject, ResolveNino, ResolveTaxYear }
import api.models.domain.{ Nino, TaxYear }
import api.models.errors._
import org.scalamock.scalatest.MockFactory
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.{ JsValue, Json, Reads }
import support.UnitSpec

class ValidatorSpec extends UnitSpec with MockFactory {

  private implicit val correlationId: String = "1234"

  private val validNino    = Nino("AA123456A")
  private val validTaxYear = TaxYear.fromMtd("2023-24")

  private val validBody = Json.parse("""
                                       | {
                                       |   "value1": "value 1",
                                       |   "value2": true
                                       | }
                                       |""".stripMargin)

  private val parsedRequestBody = TestParsedRequestBody("value 1", value2 = true)
  private val parsedRequest     = TestParsedRequest(validNino, validTaxYear, parsedRequestBody)

  case class TestParsedRequest(nino: Nino, taxYear: TaxYear, body: TestParsedRequestBody)
  case class TestParsedRequestBody(value1: String, value2: Boolean)
  implicit val testParsedRequestBodyReads: Reads[TestParsedRequestBody] = Json.reads[TestParsedRequestBody]

  /** The main/outermost validator.
    */
  private class TestParsedRequestValidator(nino: String = "AA123456A", taxYear: String = "2023-24", jsonBody: JsValue = validBody)
      extends Validator[TestParsedRequest] {

    private val jsonResolver = new ResolveJsonObject[TestParsedRequestBody]

    def validate: Either[Seq[MtdError], TestParsedRequest] = {
      val resolvedNino    = ResolveNino(nino)
      val resolvedTaxYear = ResolveTaxYear(taxYear)

      val result = (for {
        nino    <- resolvedNino
        taxYear <- resolvedTaxYear
        body    <- jsonResolver(jsonBody, RuleIncorrectOrEmptyBodyError)
        _       <- new TestParsedRequestBodyValidator(body).validate
      } yield {
        TestParsedRequest(nino, taxYear, body)
      })

      mapResult(result, possibleErrors = resolvedNino, resolvedTaxYear)
    }
  }

  /** Perform additional business-rules validation on the correctly parsed request.
    */
  private class TestParsedRequestBodyValidator(parsedBody: TestParsedRequestBody) extends Validator[TestParsedRequestBody] {

    def validate: Either[Seq[MtdError], TestParsedRequestBody] = {
      val resolvedValue1 = if (parsedBody.value1 == "value 1") Right(parsedBody) else Left(List(RuleValue1Invalid))
      val resolvedValue2 = if (parsedBody.value2) Right(parsedBody) else Left(List(RuleValue2Invalid))

      val result = for {
        _ <- resolvedValue1
        _ <- resolvedValue2
      } yield parsedBody

      mapResult(result, possibleErrors = resolvedValue1, resolvedValue2)
    }

  }

  private object RuleValue1Invalid extends MtdError("RULE_VALUE_1_INVALID", "value1 can only be 'value 1'", BAD_REQUEST)
  private object RuleValue2Invalid extends MtdError("RULE_VALUE_2_INVALID", "value2 can only be true", BAD_REQUEST)

  "validateAndWrapResult()" should {

    "return the parsed domain object" when {
      "given valid input" in {
        val validator = new TestParsedRequestValidator()
        val result    = validator.validateAndWrapResult()
        result shouldBe Right(parsedRequest)
      }
    }

    "return an error from the request params" when {
      "given a single invalid request param" in {
        val validator = new TestParsedRequestValidator(nino = "not-a-nino")
        val result    = validator.validateAndWrapResult()
        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "given two invalid request params" in {
        val validator = new TestParsedRequestValidator(nino = "not-a-nino", taxYear = "not-a-tax-year")
        val result    = validator.validateAndWrapResult()
        result shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(List(NinoFormatError, TaxYearFormatError))))
      }

    }

    "return an error from the Json body" when {
      "given a request with valid params and an invalid body" in {
        val jsonRequestBody = Json.parse("""
                                           | {
                                           |   "value1": "value 1",
                                           |   "value2": "not-a-boolean"
                                           | }
                                           |""".stripMargin)

        val validator = new TestParsedRequestValidator(jsonBody = jsonRequestBody)
        val result    = validator.validateAndWrapResult()
        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      }
    }
  }

}
