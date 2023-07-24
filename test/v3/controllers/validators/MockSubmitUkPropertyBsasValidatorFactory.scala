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
import api.models.errors.MtdError
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.JsValue
import v3.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRequestData

trait MockSubmitUkPropertyBsasValidatorFactory extends MockFactory {

  val mockSubmitUkPropertyBsasValidatorFactory: SubmitUkPropertyBsasValidatorFactory = mock[SubmitUkPropertyBsasValidatorFactory]

  object MockedSubmitUkPropertyBsasValidatorFactory {

    def validator(): CallHandler[Validator[SubmitUkPropertyBsasRequestData]] =
      (mockSubmitUkPropertyBsasValidatorFactory.validator(_: String, _: String, _: Option[String], _: JsValue)).expects(*, *, *, *)
  }

  def willUseValidator(use: Validator[SubmitUkPropertyBsasRequestData]): CallHandler[Validator[SubmitUkPropertyBsasRequestData]] = {
    MockedSubmitUkPropertyBsasValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: SubmitUkPropertyBsasRequestData): Validator[SubmitUkPropertyBsasRequestData] =
    new Validator[SubmitUkPropertyBsasRequestData] {
      def validate: Either[Seq[MtdError], SubmitUkPropertyBsasRequestData] = Right(result)
    }

  def returning(result: MtdError): Validator[SubmitUkPropertyBsasRequestData] = returningErrors(List(result))

  def returningErrors(result: Seq[MtdError]): Validator[SubmitUkPropertyBsasRequestData] = new Validator[SubmitUkPropertyBsasRequestData] {
    def validate: Either[Seq[MtdError], SubmitUkPropertyBsasRequestData] = Left(result)
  }

}
