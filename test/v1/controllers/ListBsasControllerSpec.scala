/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.controllers

import v1.fixtures.ListBsasFixtures._
import mocks.requestParsers.MockListBsasRequestDataParser
import mocks.services.MockListBsasService
import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import v1.models.domain.{Status, TypeOfBusiness}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{AccountingPeriod, DesTaxYear, ListBsasRawData, ListBsasRequest}
import v1.models.response.ListBsasResponse
import v1.models.response.listBsas.{BsasEntries, BusinessSourceSummary}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListBsasControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListBsasRequestDataParser
    with MockListBsasService {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new ListBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }


  private val nino = "AA123456A"
  private val taxYear = "2018-19"
  private val typeOfBusiness = Some("uk-property-fhl")
  private val selfEmploymentId = Some("XAIS12345678901")
  private val secondTypeOfBusiness = "uk-property-non-fhl"
  private val correlationId = "X-123"

  val response =
    ListBsasResponse(
      Seq(BusinessSourceSummary(
        typeOfBusiness = TypeOfBusiness.`self-employment`,
        selfEmploymentId = Some("000000000000210"),
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
      ))
    )

  private val rawData = ListBsasRawData(nino, taxYear, typeOfBusiness, selfEmploymentId)
  private val requestData = ListBsasRequest(Nino(nino), DesTaxYear("2019"), Some("self-employment"), Some(TypeOfBusiness.`self-employment`.toIdentifierValue))

  "list bsas" should {
    "return successful response with status OK" when {
      "valid request" in new Test {

        MockListBsasRequestDataParser
          .parse(rawData)
          .returns(Right(requestData))

        MockListBsasService
          .listBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: Future[Result] = controller.listBsas(nino, taxYear, typeOfBusiness, selfEmploymentId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe summariesJSON
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockListBsasRequestDataParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(Some(correlationId), error, None)))

            val result: Future[Result] = controller.listBsas(nino, taxYear, typeOfBusiness, selfEmploymentId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (SelfEmploymentIdFormatError, BAD_REQUEST),
          (TypeOfBusinessFormatError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (RuleTaxYearRangeExceededError, BAD_REQUEST),
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
              .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), mtdError))))

            val result: Future[Result] = controller.listBsas(nino, taxYear, typeOfBusiness, selfEmploymentId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
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
  }
}
