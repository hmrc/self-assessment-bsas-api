package v1.mocks.requestParsers

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import v1.controllers.requestParsers.ListBsasRequestDataParser
import v1.models.errors.ErrorWrapper
import v1.models.request.{ListBsasRawData, ListBsasRequest}

trait MockListBsasRequestDataParser extends MockFactory {

  val mockRequestParser = mock[ListBsasRequestDataParser]

  object MockListBsasRequestDataParser {
    def parse(data: ListBsasRawData): CallHandler[Either[ErrorWrapper, ListBsasRequest]] = {
      (mockRequestParser.parseRequest(_: ListBsasRawData)).expects(data)
    }
  }
}
