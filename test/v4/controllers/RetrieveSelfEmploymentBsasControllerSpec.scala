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

package v4.controllers

import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.GET
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.mocks.MockIdGenerator
import shared.models.domain.CalculationId
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import shared.config.MockAppConfig
import shared.routing.Version4
import v4.controllers.validators.MockRetrieveSelfEmploymentBsasValidatorFactory
import v4.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures._
import v4.mocks.services.MockRetrieveSelfEmploymentBsasService
import v4.models.errors._
import v4.models.request.retrieveBsas
import v4.models.response.retrieveBsas.selfEmployment.RetrieveSelfAssessmentBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSelfEmploymentBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveSelfEmploymentBsasValidatorFactory
    with MockRetrieveSelfEmploymentBsasService
    with MockHateoasFactory
    with MockIdGenerator
    with MockAppConfig {

  private val calculationId    = CalculationId("03e3bc8b-910d-4f5b-88d7-b627c84f2ed7")
  private val requestData      = retrieveBsas.RetrieveSelfEmploymentBsasRequestData(parsedNino, calculationId, None)
  private val testHateoasLinks = List(Link(href = "/some/link", method = GET, rel = "someRel"))

  private val hateoasResponse = mtdRetrieveBsasResponseJson
    .as[JsObject] ++ Json
    .parse("""{
      |  "links": [ { "href":"/some/link", "method":"GET", "rel":"someRel" } ]
      |}
      |""".stripMargin)
    .as[JsObject]

  "retrieve" should {
    "return OK" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveSelfEmploymentBsasService
          .retrieveBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseModel))))

        MockHateoasFactory
          .wrap(retrieveBsasResponseModel, RetrieveSelfAssessmentBsasHateoasData(validNino, calculationId.calculationId, None))
          .returns(HateoasWrapper(retrieveBsasResponseModel, testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(hateoasResponse)
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

        MockRetrieveSelfEmploymentBsasService
          .retrieveBsas(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError))))

        runErrorTest(expectedError = RuleTypeOfBusinessIncorrectError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller = new RetrieveSelfEmploymentBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrieveSelfEmploymentBsasValidatorFactory,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(validNino, calculationId.calculationId)(fakeGetRequest)

    MockedAppConfig.isApiDeprecated(Version4) returns false
  }

}
