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

package v3.controllers

import api.controllers.{ ControllerBaseSpec, ControllerTestRunner }
import api.hateoas.MockHateoasFactory
import api.mocks.services.{ MockEnrolmentsAuthService, MockMtdIdLookupService }
import api.mocks.{ MockCurrentDate, MockIdGenerator }
import api.models.domain.{ BusinessId, Nino, TaxYear }
import api.models.errors._
import api.models.hateoas.HateoasWrapper
import api.models.outcomes.ResponseWrapper
import mocks.MockAppConfig
import play.api.Configuration
import play.api.mvc.Result
import routing.Version3
import v3.controllers.validators.MockListBsasValidatorFactory
import v3.fixtures.ListBsasFixture
import v3.hateoas.HateoasLinks
import v3.mocks.services.MockListBsasService
import v3.models.domain.TypeOfBusiness
import v3.models.request.ListBsasRequestData
import v3.models.response.listBsas.{ BsasSummary, BusinessSourceSummary, ListBsasHateoasData, ListBsasResponse }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListBsasValidatorFactory
    with MockListBsasService
    with MockHateoasFactory
    with MockAppConfig
    with HateoasLinks
    with MockCurrentDate
    with MockIdGenerator
    with ListBsasFixture {

  private val typeOfBusiness = "self-employment"
  private val businessId     = "XAIS12345678901"
  private val version        = Version3

  "list bsas" should {
    "return OK" when {
      "the request is valid" in new Test {
        MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes()
        MockedAppConfig.featureSwitches.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()

        willUseValidator(returningSuccess(requestData))

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrapList(response, ListBsasHateoasData(nino, response, Some(requestData.taxYear)))
          .returns(responseWithHateoas)

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(summariesJSONWithHateoas(nino))
        )
      }

      "valid request with no taxYear path parameter" in new Test {
        MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes()
        MockedAppConfig.featureSwitches.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()

        override def maybeTaxYear: Option[String] = None

        willUseValidator(returningSuccess(requestData))

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrapList(response, ListBsasHateoasData(nino, response, Some(requestData.taxYear)))
          .returns(responseWithHateoas)

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(summariesJSONWithHateoas(nino))
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
    def maybeTaxYear: Option[String] = Some("2019-20")

    val controller = new ListBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockListBsasValidatorFactory,
      service = mockListBsasService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator,
    )

    val requestData: ListBsasRequestData = ListBsasRequestData(
      nino = Nino(nino),
      taxYear = TaxYear.currentTaxYear(),
      incomeSourceId = Some(BusinessId(businessId)),
      incomeSourceType = Some(typeOfBusiness)
    )

    val response: ListBsasResponse[BsasSummary] = ListBsasResponse(
      List(
        businessSourceSummaryModel(),
        businessSourceSummaryModel().copy(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
                                          summaries = List(
                                            bsasSummaryModel.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5")
                                          )),
        businessSourceSummaryModel().copy(typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`,
                                          summaries = List(
                                            bsasSummaryModel.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6")
                                          ))
      ))

    def responseWithHateoas: HateoasWrapper[ListBsasResponse[HateoasWrapper[BsasSummary]]] = HateoasWrapper(
      ListBsasResponse(
        List(
          BusinessSourceSummary(
            businessId = "000000000000210",
            typeOfBusiness = TypeOfBusiness.`self-employment`,
            accountingPeriodModel,
            taxYear = TaxYear.fromMtd("2019-20"),
            List(
              HateoasWrapper(
                bsasSummaryModel,
                List(getSelfEmploymentBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4", None))
              ))
          ),
          BusinessSourceSummary(
            businessId = "000000000000210",
            typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
            accountingPeriodModel,
            taxYear = TaxYear.fromMtd("2019-20"),
            List(
              HateoasWrapper(
                bsasSummaryModel.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5"),
                List(getUkPropertyBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5", None))
              ))
          ),
          BusinessSourceSummary(
            businessId = "000000000000210",
            typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`,
            accountingPeriodModel,
            taxYear = TaxYear.fromMtd("2019-20"),
            List(
              HateoasWrapper(
                bsasSummaryModel.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6"),
                List(getUkPropertyBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6", None))
              ))
          )
        )
      ),
      List(triggerBsas(mockAppConfig, nino), listBsas(mockAppConfig, nino, None))
    )

    protected def callController(): Future[Result] = controller.listBsas(nino, maybeTaxYear, Some(typeOfBusiness), Some(businessId))(fakeGetRequest)

    MockedAppConfig.isApiDeprecated(version) returns false
  }
}
