/*
 * Copyright 2022 HM Revenue & Customs
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

import domain.Nino
import mocks.{ MockAppConfig, MockIdGenerator }
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v3.fixtures.ListBsasFixture
import v3.hateoas.HateoasLinks
import v3.mocks.MockCurrentDateProvider
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockListBsasRequestParser
import v3.mocks.services.{ MockEnrolmentsAuthService, MockListBsasService, MockMtdIdLookupService }
import v3.models.domain.TaxYear
import v3.models.domain.TypeOfBusiness._
import v3.models.errors._
import v3.models.hateoas.HateoasWrapper
import v3.models.outcomes.ResponseWrapper
import v3.models.request.{ ListBsasRawData, ListBsasRequest }
import v3.models.response.listBsas.{ BsasSummary, BusinessSourceSummary, ListBsasHateoasData, ListBsasResponse }

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListBsasControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListBsasRequestParser
    with MockListBsasService
    with MockHateoasFactory
    with MockAppConfig
    with HateoasLinks
    with MockCurrentDateProvider
    with MockIdGenerator
    with ListBsasFixture {

  private val correlationId  = "X-123"
  private val nino           = "AA123456A"
  private val taxYear        = Some("2019-20")
  private val typeOfBusiness = Some("self-employment")
  private val businessId     = Some("XAIS12345678901")

  private val rawData = ListBsasRawData(
    nino = nino,
    taxYear = taxYear,
    typeOfBusiness = typeOfBusiness,
    businessId = businessId
  )

  private val requestData = ListBsasRequest(
    nino = Nino(nino),
    taxYear = TaxYear("2019"),
    incomeSourceId = Some("self-employment"),
    incomeSourceType = Some(`self-employment`.toIdentifierValue)
  )

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new ListBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator,
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)

    val date: LocalDate = LocalDate.of(2019, 6, 18)
    MockCurrentDateProvider.getCurrentDate().returns(date).anyNumberOfTimes()
  }

  val response: ListBsasResponse[BsasSummary] = ListBsasResponse(
    Seq(
      businessSourceSummaryModel,
      businessSourceSummaryModel.copy(
        typeOfBusiness = `uk-property-fhl`,
        summaries = Seq(bsasSummaryModel.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5"))
      ),
      businessSourceSummaryModel.copy(
        typeOfBusiness = `uk-property-non-fhl`,
        summaries = Seq(bsasSummaryModel.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6"))
      )
    ))

  "list bsas" should {
    "return successful response with status OK" when {
      "valid request" in new Test {
        MockedAppConfig.apiGatewayContext returns "individuals/self-assessment/adjustable-summary" anyNumberOfTimes ()
        MockedAppConfig.featureSwitches returns Configuration("tys-api.enabled" -> false) anyNumberOfTimes ()

        MockListBsasRequestDataParser
          .parse(rawData)
          .returns(Right(requestData))

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val responseWithHateoas: HateoasWrapper[ListBsasResponse[HateoasWrapper[BsasSummary]]] = HateoasWrapper(
          ListBsasResponse(
            Seq(
              BusinessSourceSummary(
                businessId = "000000000000210",
                typeOfBusiness = `self-employment`,
                accountingPeriodModel,
                taxYear = TaxYear.fromMtd("2019-20"),
                Seq(
                  HateoasWrapper(
                    bsasSummaryModel,
                    Seq(getSelfEmploymentBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4", None))
                  ))
              ),
              BusinessSourceSummary(
                businessId = "000000000000210",
                typeOfBusiness = `uk-property-fhl`,
                accountingPeriodModel,
                taxYear = TaxYear.fromMtd("2019-20"),
                Seq(HateoasWrapper(
                  bsasSummaryModel.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5"),
                  Seq(getUkPropertyBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5", None))
                ))
              ),
              BusinessSourceSummary(
                businessId = "000000000000210",
                typeOfBusiness = `uk-property-non-fhl`,
                accountingPeriodModel,
                taxYear = TaxYear.fromMtd("2019-20"),
                Seq(HateoasWrapper(
                  bsasSummaryModel.copy(calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6"),
                  Seq(getUkPropertyBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce6", None))
                ))
              )
            )
          ),
          Seq(triggerBsas(mockAppConfig, nino), listBsas(mockAppConfig, nino, None))
        )

        MockHateoasFactory
          .wrapList(response, ListBsasHateoasData(nino, response, None))
          .returns(responseWithHateoas)

        val result: Future[Result] = controller.listBsas(nino, taxYear, typeOfBusiness, businessId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe summariesJSONWithHateoas(nino)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockListBsasRequestDataParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.listBsas(nino, taxYear, typeOfBusiness, businessId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (TypeOfBusinessFormatError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST),
          (InternalError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockListBsasRequestDataParser
              .parse(rawData)
              .returns(Right(requestData))

            MockListBsasService
              .listBsas(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.listBsas(nino, taxYear, typeOfBusiness, businessId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (InternalError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
