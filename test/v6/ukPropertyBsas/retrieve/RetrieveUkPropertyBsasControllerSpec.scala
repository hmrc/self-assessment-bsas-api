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

package v6.ukPropertyBsas.retrieve

import common.errors._
import play.api.mvc.Result
import shared.config.MockAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.domain.CalculationId
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v6.ukPropertyBsas.retrieve.def1.model.request.Def1_RetrieveUkPropertyBsasRequestData
import v6.ukPropertyBsas.retrieve.def1.model.response.RetrieveUkPropertyBsasFixtures._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveUkPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveUkPropertyBsasValidatorFactory
    with MockRetrieveUkPropertyBsasService
    with MockIdGenerator
    with MockAppConfig {

  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")
  private val requestData   = Def1_RetrieveUkPropertyBsasRequestData(parsedNino, calculationId, taxYear = None)

  "retrieve" should {
    "return OK for FHL" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveUkPropertyBsasService
          .retrieveBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseFhl))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdRetrieveBsasResponseFhlJson)
        )
      }
    }
    "return OK for non-FHL" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveUkPropertyBsasService
          .retrieveBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseNonFhl))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdRetrieveBsasResponseNonFhlJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))
        runErrorTest(expectedError = NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveUkPropertyBsasService
          .retrieveBsas(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError))))

        runErrorTest(expectedError = RuleTypeOfBusinessIncorrectError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller = new RetrieveUkPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrieveUkPropertyBsasValidatorFactory,
      service = mockService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.retrieve(validNino, calculationId.calculationId, taxYear = None)(fakeGetRequest)
  }

}
