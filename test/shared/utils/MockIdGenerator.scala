package shared.utils

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory

trait MockIdGenerator extends MockFactory {

  val mockIdGenerator: IdGenerator = mock[IdGenerator]

  object MockIdGenerator {
    def generateCorrelationId: CallHandler[String] = (() => mockIdGenerator.generateCorrelationId).expects()
  }

}
