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

package v5.listBsas.validators

import org.scalamock.handlers.CallHandler
import shared.controllers.validators.{MockValidatorFactory, Validator}
import v5.listBsas.models.ListBsasRequestData
import v5.listBsas.schema.ListBsasSchema

trait MockListBsasValidatorFactory extends MockValidatorFactory[ListBsasRequestData] {

  val mockListBsasValidatorFactory: ListBsasValidatorFactory = mock[ListBsasValidatorFactory]

  def validator(): CallHandler[Validator[ListBsasRequestData]] =
    (mockListBsasValidatorFactory
      .validator(_: String, _: Option[String], _: Option[String], _: Option[String], _: ListBsasSchema))
      .expects(*, *, *, *, *)

}
