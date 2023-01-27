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

package v3.services

import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v3.controllers.EndpointLogContext
import v3.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v3.mocks.connectors.MockRetrieveUkPropertyBsasConnector
import v3.models.domain.TypeOfBusiness
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.retrieveBsas.ukProperty.RetrieveUkPropertyBsasRequestData
import v3.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse

import scala.concurrent.Future

class RetrieveUkPropertyBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  val id           = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val request: RetrieveUkPropertyBsasRequestData = RetrieveUkPropertyBsasRequestData(nino, id, taxYear = None)

  trait Test extends MockRetrieveUkPropertyBsasConnector {
    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("RetrieveUkPropertyBsasConnector", "retrieve")

    val service = new RetrieveUkPropertyBsasService(mockConnector)
  }

  "retrieve" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockRetrievePropertyBsasConnector
          .retrievePropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseFhlModel))))

        await(service.retrieve(request)) shouldBe Right(ResponseWrapper(correlationId, retrieveBsasResponseFhlModel))
      }
    }

    "return error response" when {
      "downstream returns a success response with invalid type of business" should {
        import TypeOfBusiness._
        Seq(`self-employment`, `foreign-property`, `foreign-property-fhl-eea`).foreach(typeOfBusiness =>
          s"return an error for $typeOfBusiness" in new Test {
            val response: RetrieveUkPropertyBsasResponse = retrieveBsasResponseInvalidTypeOfBusinessModel(typeOfBusiness = typeOfBusiness)

            MockRetrievePropertyBsasConnector
              .retrievePropertyBsas(request)
              .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

            await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError))
        })
      }

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrievePropertyBsasConnector
            .retrievePropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(desErrorCode))))))

          await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = Seq(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", CalculationIdFormatError),
        ("INVAlID_CORRELATIONID", InternalError),
        ("INVALID_RETURN", InternalError),
        ("UNPROCESSABLE_ENTITY", InternalError),
        ("NO_DATA_FOUND", NotFoundError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      val extraTysErrors = Seq(
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("NOT_FOUND", NotFoundError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }
}
