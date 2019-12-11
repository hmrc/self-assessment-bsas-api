package v1.mocks.services

import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.EndpointLogContext
import v1.models.errors.ErrorWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.request.ListBsasRequest
import v1.models.response.listBsas.{BsasEntries, ListBsasResponse}
import v1.services.ListBsasService

import scala.concurrent.{ExecutionContext, Future}

trait MockListBsasService extends MockFactory{

  val mockService: ListBsasService = mock[ListBsasService]

  object MockListBsasService{

    def listBsas(requestData: ListBsasRequest): CallHandler4[ListBsasRequest, HeaderCarrier, ExecutionContext, EndpointLogContext, Future[Either[ErrorWrapper, ResponseWrapper[ListBsasResponse[BsasEntries]]]]] = {
      (mockService
        .listBsas(_: ListBsasRequest)(_: HeaderCarrier, _: ExecutionContext, _: EndpointLogContext))
        .expects(requestData, *, *, *)
    }
  }
}
