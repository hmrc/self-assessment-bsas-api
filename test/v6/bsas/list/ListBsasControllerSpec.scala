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
import shared.config.MockAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.domain.{BusinessId, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v6.bsas.list.def1.model.Def1_ListBsasFixtures
import v6.bsas.list.def1.model.request.Def1_ListBsasRequestData
import v6.bsas.list.def1.model.response.Def1_ListBsasResponse
import v6.bsas.list.def2.model.Def2_ListBsasFixtures
import v6.bsas.list.def2.model.request.Def2_ListBsasRequestData
import v6.bsas.list.def2.model.response.Def2_ListBsasResponse
import v6.bsas.list.model.request.ListBsasRequestData
import v6.bsas.list.model.response.ListBsasResponse
import v6.common.model.TypeOfBusinessWithFHL

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListBsasValidatorFactory
    with MockListBsasService
    with MockAppConfig
    with MockIdGenerator
    with Def1_ListBsasFixtures {

  private val typeOfBusiness = "self-employment"
  private val businessId     = "XAIS12345678901"

  "list bsas" should {
    "return OK" when {
      "the request is valid with FHL" in new Def1_Test {
        MockAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes()
        MockAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()

        willUseValidator(returningSuccess(requestData))

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(summariesJs)
        )
      }

      "the request is valid" in new Def2_Test {
        MockAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes()
        MockAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()

        willUseValidator(returningSuccess(requestData))

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(summariesJs)
        )
      }

      "valid request with no taxYear path parameter" in new Def1_Test {
        MockAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes()
        MockAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()

        override def maybeTaxYear: Option[String] = None

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

      "the parser validation fails" in new Def1_Test {
        willUseValidator(returning(NinoFormatError))
        runErrorTest(expectedError = NinoFormatError)
      }

      "the service returns an error" in new Def1_Test {
        willUseValidator(returningSuccess(requestData))

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, BusinessIdFormatError))))

        runErrorTest(expectedError = BusinessIdFormatError)
      }
    }
  }

  private trait Def1_Test extends ControllerTest {
    def maybeTaxYear: Option[String] = Some("2019-20")

    val controller = new ListBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockListBsasValidatorFactory,
      service = mockListBsasService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    val requestData: ListBsasRequestData = Def1_ListBsasRequestData(
      nino = parsedNino,
      taxYear = TaxYear.currentTaxYear(),
      incomeSourceId = Some(BusinessId(businessId)),
      incomeSourceType = Some(typeOfBusiness)
    )

    val response: ListBsasResponse = Def1_ListBsasResponse(
      List(
        businessSourceSummary(),
        businessSourceSummary().copy(
          typeOfBusiness = TypeOfBusinessWithFHL.`uk-property-fhl`,
          summaries = List(
            bsasSummary.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5")
          )),
        businessSourceSummary().copy(
          typeOfBusiness = TypeOfBusinessWithFHL.`uk-property-non-fhl`,
          summaries = List(
            bsasSummary.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6")
          ))
      ))

    protected def callController(): Future[Result] =
      controller.listBsas(validNino, maybeTaxYear, Some(typeOfBusiness), Some(businessId))(fakeGetRequest)

  }

  private trait Def2_Test extends Def1_Test with Def2_ListBsasFixtures {
    override def maybeTaxYear: Option[String] = Some("2025-26")

    override val requestData: ListBsasRequestData = Def2_ListBsasRequestData(
      nino = parsedNino,
      taxYear = TaxYear.fromMtd("2025-26"),
      incomeSourceId = Some(BusinessId(businessId)),
      incomeSourceType = Some(typeOfBusiness)
    )

    override val response: ListBsasResponse = Def2_ListBsasResponse(
      List(businessSourceSummary()))
  }

}
