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
import cats.data.Validated
import cats.data.Validated.{ Invalid, Valid }
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import v3.models.request.retrieveBsas.RetrieveForeignPropertyBsasRequestData

trait MockRetrieveForeignPropertyBsasValidatorFactory extends MockFactory {

  val mockRetrieveForeignPropertyBsasValidatorFactory: RetrieveForeignPropertyBsasValidatorFactory = mock[RetrieveForeignPropertyBsasValidatorFactory]

  object MockedRetrieveForeignPropertyBsasValidatorFactory {

    def validator(): CallHandler[Validator[RetrieveForeignPropertyBsasRequestData]] =
      (mockRetrieveForeignPropertyBsasValidatorFactory.validator(_: String, _: String, _: Option[String])).expects(*, *, *)
  }

  def willUseValidator(use: Validator[RetrieveForeignPropertyBsasRequestData]): CallHandler[Validator[RetrieveForeignPropertyBsasRequestData]] = {
    MockedRetrieveForeignPropertyBsasValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: RetrieveForeignPropertyBsasRequestData): Validator[RetrieveForeignPropertyBsasRequestData] =
    new Validator[RetrieveForeignPropertyBsasRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveForeignPropertyBsasRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[RetrieveForeignPropertyBsasRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[RetrieveForeignPropertyBsasRequestData] =
    new Validator[RetrieveForeignPropertyBsasRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveForeignPropertyBsasRequestData] = Invalid(result)
    }

}
