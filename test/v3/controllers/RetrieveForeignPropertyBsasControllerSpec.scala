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

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.Method.GET
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import api.mocks.MockIdGenerator
import api.models.ResponseWrapper
import api.models.domain.Nino
import api.models.errors._
import api.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import v3.mocks.requestParsers.MockRetrieveForeignPropertyRequestParser
import v3.mocks.services.MockRetrieveForeignPropertyBsasService
import v3.models.errors._
import v3.models.request.retrieveBsas.foreignProperty.{RetrieveForeignPropertyBsasRawData, RetrieveForeignPropertyBsasRequestData}
import v3.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveForeignPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveForeignPropertyRequestParser
    with MockRetrieveForeignPropertyBsasService
    with MockHateoasFactory
    with MockIdGenerator {

  private val calcId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val request        = RetrieveForeignPropertyBsasRequestData(Nino(nino), calcId, taxYear = None)
  private val requestRawData = RetrieveForeignPropertyBsasRawData(nino, calcId, taxYear = None)

  private val testHateoasLinks =
    Seq(Link(href = "/some/link", method = GET, rel = "someRel"))

  private val hateoasResponse = retrieveForeignPropertyBsasMtdNonFhlJson
    .as[JsObject] ++ Json.parse("""{
      |  "links": [ { "href":"/some/link", "method":"GET", "rel":"someRel" } ]
      |}
      |""".stripMargin).as[JsObject]

  "retrieve" should {
    "return OK" when {
      "the request is valid" in new Test {
        MockRetrieveForeignPropertyRequestParser.parse(requestRawData) returns Right(request)

        MockRetrieveForeignPropertyBsasService.retrieveBsas(request) returns
          Future.successful(Right(ResponseWrapper(correlationId, retrieveForeignPropertyBsasResponseNonFhlModel)))

        MockHateoasFactory
          .wrap(retrieveForeignPropertyBsasResponseNonFhlModel, RetrieveForeignPropertyHateoasData(nino, calcId, None)) returns
          HateoasWrapper(retrieveForeignPropertyBsasResponseNonFhlModel, testHateoasLinks)

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(hateoasResponse)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockRetrieveForeignPropertyRequestParser.parse(requestRawData) returns Left(ErrorWrapper(correlationId, NinoFormatError, None))
        runErrorTest(expectedError = NinoFormatError)
      }

      "the service returns an error" in new Test {
        MockRetrieveForeignPropertyRequestParser.parse(requestRawData) returns Right(request)

        MockRetrieveForeignPropertyBsasService.retrieveBsas(request) returns Future.successful(
          Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError)))

        runErrorTest(expectedError = RuleTypeOfBusinessIncorrectError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller = new RetrieveForeignPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRequestParser,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.retrieve(nino, calcId, taxYear = None)(fakeGetRequest)
  }
}
