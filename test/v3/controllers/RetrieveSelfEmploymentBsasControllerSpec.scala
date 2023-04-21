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
import api.hateoas.Method.GET
import api.hateoas.{ HateoasWrapper, Link, MockHateoasFactory }
import api.mocks.MockIdGenerator
import api.models.ResponseWrapper
import api.models.domain.Nino
import api.models.errors._
import api.services.{ MockEnrolmentsAuthService, MockMtdIdLookupService }
import config.MockAppConfig
import play.api.libs.json.{ JsObject, Json }
import play.api.mvc.Result
import v3.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures._
import v3.mocks.requestParsers.MockRetrieveSelfEmploymentRequestParser
import v3.mocks.services.MockRetrieveSelfEmploymentBsasService
import v3.models.errors._
import v3.models.request.retrieveBsas.selfEmployment.{ RetrieveSelfEmploymentBsasRawData, RetrieveSelfEmploymentBsasRequestData }
import v3.models.response.retrieveBsas.selfEmployment.RetrieveSelfAssessmentBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSelfEmploymentBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveSelfEmploymentRequestParser
    with MockRetrieveSelfEmploymentBsasService
    with MockHateoasFactory
    with MockIdGenerator
    with MockAppConfig {

  private val calculationId = "03e3bc8b-910d-4f5b-88d7-b627c84f2ed7"

  private val request        = RetrieveSelfEmploymentBsasRequestData(Nino(nino), calculationId, None)
  private val requestRawData = RetrieveSelfEmploymentBsasRawData(nino, calculationId, None)

  private val testHateoasLinks =
    Seq(Link(href = "/some/link", method = GET, rel = "someRel"))

  private val hateoasResponse = mtdRetrieveBsasResponseJson
    .as[JsObject] ++ Json.parse("""{
      |  "links": [ { "href":"/some/link", "method":"GET", "rel":"someRel" } ]
      |}
      |""".stripMargin).as[JsObject]

  "retrieve" should {
    "return OK" when {
      "the request is valid" in new Test {
        MockRetrieveSelfEmploymentRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveSelfEmploymentBsasService
          .retrieveBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseModel))))

        MockHateoasFactory
          .wrap(retrieveBsasResponseModel, RetrieveSelfAssessmentBsasHateoasData(nino, calculationId, None))
          .returns(HateoasWrapper(retrieveBsasResponseModel, testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(hateoasResponse)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockRetrieveSelfEmploymentRequestParser
          .parse(requestRawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        runErrorTest(expectedError = NinoFormatError)
      }

      "the service returns an error" in new Test {
        MockRetrieveSelfEmploymentRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveSelfEmploymentBsasService
          .retrieveBsas(request)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError))))

        runErrorTest(expectedError = RuleTypeOfBusinessIncorrectError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller = new RetrieveSelfEmploymentBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRequestParser,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, calculationId)(fakeGetRequest)
  }
}
