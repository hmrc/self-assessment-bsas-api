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

package v6.bsas.list

import play.api.Configuration
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.domain.{BusinessId, TaxYear}
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v6.bsas.list.def2.model.Def2_ListBsasFixtures
import v6.bsas.list.def2.model.request.Def2_ListBsasRequestData
import v6.bsas.list.def2.model.response.Def2_ListBsasResponse
import v6.bsas.list.model.request.ListBsasRequestData
import v6.bsas.list.model.response.ListBsasResponse
import v6.common.model.TypeOfBusiness

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListBsasValidatorFactory
    with MockListBsasService
    with MockSharedAppConfig
    with MockIdGenerator
    with Def2_ListBsasFixtures {

  private val typeOfBusiness = "self-employment"
  private val businessId     = "XAIS12345678901"

  "list bsas" should {
    "return OK" when {
      "the request is valid" in new Test {
        MockedSharedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes()
        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()

        willUseValidator(returningSuccess(requestData))

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(summariesJs)
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

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, BusinessIdFormatError))))

        runErrorTest(expectedError = BusinessIdFormatError)
      }
    }
  }

  private trait Test extends ControllerTest {
    def taxYear: String = "2019-20"

    val controller = new ListBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockListBsasValidatorFactory,
      service = mockListBsasService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    val requestData: ListBsasRequestData = Def2_ListBsasRequestData(
      nino = parsedNino,
      taxYear = TaxYear.currentTaxYear(),
      incomeSourceId = Some(BusinessId(businessId)),
      incomeSourceType = Some(typeOfBusiness)
    )

    val response: ListBsasResponse = Def2_ListBsasResponse(
      List(
        businessSourceSummary(),
        businessSourceSummary().copy(
          typeOfBusiness = TypeOfBusiness.`uk-property`,
          summaries = List(
            bsasSummary.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5")
          )),
        businessSourceSummary().copy(
          typeOfBusiness = TypeOfBusiness.`foreign-property`,
          summaries = List(
            bsasSummary.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6")
          ))
      ))

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] =
      controller.listBsas(validNino, taxYear, Some(typeOfBusiness), Some(businessId))(fakeGetRequest)

  }

}
