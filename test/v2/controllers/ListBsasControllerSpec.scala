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

package v2.controllers

import java.time.LocalDate

import mocks.{MockAppConfig, MockIdGenerator}
import play.api.libs.json.Json
import play.api.mvc.Result
import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import utils.DateUtils
import v2.fixtures.ListBsasFixtures._
import v2.hateoas.HateoasLinks
import v2.mocks.MockCurrentDateProvider
import v2.mocks.hateoas.MockHateoasFactory
import v2.mocks.requestParsers.MockListBsasRequestParser
import v2.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockListBsasService, MockMtdIdLookupService}
import v2.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v2.models.domain.{Status, TypeOfBusiness}
import v2.models.errors._
import v2.models.hateoas.HateoasWrapper
import v2.models.outcomes.ResponseWrapper
import v2.models.request.{AccountingPeriod, ListBsasRawData, ListBsasRequest}
import v2.models.response.listBsas.{BsasEntries, BusinessSourceSummary, ListBsasHateoasData, ListBsasResponse}
import v3.models.domain.DownstreamTaxYear

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
    with MockAuditService
    with MockCurrentDateProvider
    with MockIdGenerator {

  private val correlationId = "X-123"
  private val nino = "AA123456A"
  private val taxYear = Some("2019-20")
  private val typeOfBusiness = Some("uk-property-fhl")
  private val businessId = Some("XAIS12345678901")

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new ListBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator,
      currentDateProvider = mockCurrentDateProvider
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)

    val date: LocalDate = LocalDate.of(2019, 6, 18)
    MockCurrentDateProvider.getCurrentDate().returns(date).anyNumberOfTimes()
  }

  val response: ListBsasResponse[BsasEntries] =
    ListBsasResponse(
      Seq(
        BusinessSourceSummary(
          typeOfBusiness = TypeOfBusiness.`self-employment`,
          businessId = Some("000000000000210"),
          AccountingPeriod(
            startDate = "2018-10-11",
            endDate = "2019-10-10"
          ),
          Seq(BsasEntries(
            bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
            requestedDateTime = "2019-10-14T11:33:27Z",
            summaryStatus = Status.`valid`,
            adjustedSummary = false
          ))
        ),
        BusinessSourceSummary(
          typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
          businessId = None,
          AccountingPeriod(
            startDate = "2018-10-11",
            endDate = "2019-10-10"
          ),
          Seq(BsasEntries(
            bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce3",
            requestedDateTime = "2019-10-14T11:33:27Z",
            summaryStatus = Status.`valid`,
            adjustedSummary = false
          ))
        ),
        BusinessSourceSummary(
          typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`,
          businessId = None,
          AccountingPeriod(
            startDate = "2018-10-11",
            endDate = "2019-10-10"
          ),
          Seq(BsasEntries(
            bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
            requestedDateTime = "2019-10-14T11:33:27Z",
            summaryStatus = Status.`valid`,
            adjustedSummary = false
          ))
        )
      )
    )

  private val rawData = ListBsasRawData(nino, taxYear, typeOfBusiness, businessId)
  private val requestData = ListBsasRequest(Nino(nino), DownstreamTaxYear("2019"), Some("self-employment"), Some(TypeOfBusiness.`self-employment`.toIdentifierValue))

  def event(auditResponse: AuditResponse, taxYear: String = "2019-20"): AuditEvent[GenericAuditDetail] =
    AuditEvent(
      auditType = "listBusinessSourceAdjustableSummaries",
      transactionName = "list-business-source-adjustable-summaries",
      detail = GenericAuditDetail(
        userType = "Individual",
        agentReferenceNumber = None,
        params = Map("nino" -> nino, "taxYear" -> taxYear),
        requestBody = None,
        `X-CorrelationId` = correlationId,
        auditResponse = auditResponse
      )
    )

  "list bsas" should {
    "return successful response with status OK" when {
      "valid request" in new Test {

        MockedAppConfig.apiGatewayContext returns "individuals/self-assessment/adjustable-summary" anyNumberOfTimes()

        MockListBsasRequestDataParser
          .parse(rawData)
          .returns(Right(requestData))

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val responseWithHateoas: HateoasWrapper[ListBsasResponse[HateoasWrapper[BsasEntries]]] = HateoasWrapper(
          ListBsasResponse(
            Seq(
              BusinessSourceSummary(
                typeOfBusiness = TypeOfBusiness.`self-employment`,
                businessId = Some("000000000000210"),
                AccountingPeriod(
                  startDate = "2018-10-11",
                  endDate = "2019-10-10"
                ),
                Seq(HateoasWrapper(
                  BsasEntries(
                    bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
                    requestedDateTime = "2019-10-14T11:33:27Z",
                    summaryStatus = Status.`valid`,
                    adjustedSummary = false
                  ),
                  Seq(getSelfEmploymentBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"))
                ))
              ),
              BusinessSourceSummary(
                typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
                businessId = None,
                AccountingPeriod(
                  startDate = "2018-10-11",
                  endDate = "2019-10-10"
                ),
                Seq(HateoasWrapper(
                  BsasEntries(
                    bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce3",
                    requestedDateTime = "2019-10-14T11:33:27Z",
                    summaryStatus = Status.`valid`,
                    adjustedSummary = false
                  ),
                  Seq(getUkPropertyBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce3"))
                ))
              ),
              BusinessSourceSummary(
                typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`,
                businessId = None,
                AccountingPeriod(
                  startDate = "2018-10-11",
                  endDate = "2019-10-10"
                ),
                Seq(HateoasWrapper(
                  BsasEntries(
                    bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
                    requestedDateTime = "2019-10-14T11:33:27Z",
                    summaryStatus = Status.`valid`,
                    adjustedSummary = false
                  ),
                  Seq(getUkPropertyBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2"))
                ))
              )
            )
          ),
          Seq(triggerBsas(mockAppConfig, nino), listBsas(mockAppConfig, nino))
        )

        MockHateoasFactory
          .wrapList(response, ListBsasHateoasData(nino, response))
          .returns(responseWithHateoas)

        val result: Future[Result] = controller.listBsas(nino, taxYear, typeOfBusiness, businessId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe summariesJSONWithHateoas(nino)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(OK, None, Some(summariesJSONWithHateoas(nino)))
        MockedAuditService.verifyAuditEvent(event(auditResponse)).once
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

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(error.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse)).once
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (TypeOfBusinessFormatError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR)
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

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(mtdError.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse)).once
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (TypeOfBusinessFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }

    "audit correctly with taxYear" when {
      "the taxYear parameter is not provided (success)" in new Test {

        val desTaxYear: DownstreamTaxYear = DateUtils.getDownstreamTaxYear(mockCurrentDateProvider.getCurrentDate())
        val mtdTaxYear: String = DownstreamTaxYear.fromDownstream(desTaxYear.toString)

        MockedAppConfig.apiGatewayContext returns "individuals/self-assessment/adjustable-summary" anyNumberOfTimes()

        MockListBsasRequestDataParser
          .parse(rawData.copy(taxYear = None))
          .returns(Right(requestData.copy(taxYear = desTaxYear)))

        MockListBsasService
          .listBsas(requestData.copy(taxYear = desTaxYear))
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val responseWithHateoas: HateoasWrapper[ListBsasResponse[HateoasWrapper[BsasEntries]]] = HateoasWrapper(
          ListBsasResponse(
            Seq(
              BusinessSourceSummary(
                typeOfBusiness = TypeOfBusiness.`self-employment`,
                businessId = Some("000000000000210"),
                AccountingPeriod(
                  startDate = "2018-10-11",
                  endDate = "2019-10-10"
                ),
                Seq(HateoasWrapper(
                  BsasEntries(
                    bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
                    requestedDateTime = "2019-10-14T11:33:27Z",
                    summaryStatus = Status.`valid`,
                    adjustedSummary = false
                  ),
                  Seq(getSelfEmploymentBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"))
                ))
              ),
              BusinessSourceSummary(
                typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
                businessId = None,
                AccountingPeriod(
                  startDate = "2018-10-11",
                  endDate = "2019-10-10"
                ),
                Seq(HateoasWrapper(
                  BsasEntries(
                    bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce3",
                    requestedDateTime = "2019-10-14T11:33:27Z",
                    summaryStatus = Status.`valid`,
                    adjustedSummary = false
                  ),
                  Seq(getUkPropertyBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce3"))
                ))
              ),
              BusinessSourceSummary(
                typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`,
                businessId = None,
                AccountingPeriod(
                  startDate = "2018-10-11",
                  endDate = "2019-10-10"
                ),
                Seq(HateoasWrapper(
                  BsasEntries(
                    bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2",
                    requestedDateTime = "2019-10-14T11:33:27Z",
                    summaryStatus = Status.`valid`,
                    adjustedSummary = false
                  ),
                  Seq(getUkPropertyBsas(mockAppConfig, nino, "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce2"))
                ))
              )
            )
          ),
          Seq(triggerBsas(mockAppConfig, nino), listBsas(mockAppConfig, nino))
        )

        MockHateoasFactory
          .wrapList(response, ListBsasHateoasData(nino, response))
          .returns(responseWithHateoas)

        val result: Future[Result] = controller.listBsas(nino, None, typeOfBusiness, businessId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe summariesJSONWithHateoas(nino)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(OK, None, Some(summariesJSONWithHateoas(nino)))
        MockedAuditService.verifyAuditEvent(event(auditResponse, mtdTaxYear)).once
      }

      "the taxYear parameter is not provided (failure)" in new Test {

        val desTaxYear: DownstreamTaxYear = DateUtils.getDownstreamTaxYear(mockCurrentDateProvider.getCurrentDate())
        val mtdTaxYear: String = DownstreamTaxYear.fromDownstream(desTaxYear.toString)

        MockListBsasRequestDataParser
          .parse(rawData.copy(taxYear = None))
          .returns(Right(requestData.copy(taxYear = desTaxYear)))

        MockListBsasService
          .listBsas(requestData.copy(taxYear = desTaxYear))
          .returns(Future.successful(Left(ErrorWrapper(correlationId, NinoFormatError))))

        val result: Future[Result] = controller.listBsas(nino, None, typeOfBusiness, businessId)(fakeGetRequest)

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(NinoFormatError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(BAD_REQUEST, Some(Seq(AuditError(NinoFormatError.code))), None)
        MockedAuditService.verifyAuditEvent(event(auditResponse, mtdTaxYear)).once
      }
    }

  }
}
