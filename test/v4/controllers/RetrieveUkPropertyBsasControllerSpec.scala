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

import common.errors._
import play.api.Configuration
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.GET
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.domain.CalculationId
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v4.controllers.validators.MockRetrieveUkPropertyBsasValidatorFactory
import v4.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures.{
  mtdRetrieveBsasResponseFhlJson,
  mtdRetrieveBsasResponseNonFhlJson,
  retrieveBsasResponseFhlModel,
  retrieveBsasResponseNonFhlModel
}
import v4.mocks.services.MockRetrieveUkPropertyBsasService
import v4.models.request.retrieveBsas.RetrieveUkPropertyBsasRequestData
import v4.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveUkPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveUkPropertyBsasValidatorFactory
    with MockRetrieveUkPropertyBsasService
    with MockHateoasFactory
    with MockIdGenerator
    with MockSharedAppConfig {

  private val calculationId    = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")
  private val requestData      = RetrieveUkPropertyBsasRequestData(parsedNino, calculationId, taxYear = None)
  private val testHateoasLinks = List(Link(href = "/some/link", method = GET, rel = "someRel"))

  private val hateoasFhlResponse = mtdRetrieveBsasResponseFhlJson
    .as[JsObject] ++ Json
    .parse("""{
      |  "links": [ { "href":"/some/link", "method":"GET", "rel":"someRel" } ]
      |}
      |""".stripMargin)
    .as[JsObject]

  private val hateoasNonFhlResponse = mtdRetrieveBsasResponseNonFhlJson
    .as[JsObject] ++ Json
    .parse("""{
      |  "links": [ { "href":"/some/link", "method":"GET", "rel":"someRel" } ]
      |}
      |""".stripMargin)
    .as[JsObject]

  "retrieve" should {
    "return successful hateoas response for fhl with status OK" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveUkPropertyBsasService
          .retrieveBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseFhlModel))))

        MockHateoasFactory
          .wrap(retrieveBsasResponseFhlModel, RetrieveUkPropertyHateoasData(validNino, calculationId.calculationId, None))
          .returns(HateoasWrapper(retrieveBsasResponseFhlModel, testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(hateoasFhlResponse)
        )
      }
    }
    "return OK" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveUkPropertyBsasService
          .retrieveBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseNonFhlModel))))

        MockHateoasFactory
          .wrap(retrieveBsasResponseNonFhlModel, RetrieveUkPropertyHateoasData(validNino, calculationId.calculationId, None))
          .returns(HateoasWrapper(retrieveBsasResponseNonFhlModel, testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(hateoasNonFhlResponse)
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

        MockRetrieveUkPropertyBsasService
          .retrieveBsas(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError))))

        runErrorTest(expectedError = RuleTypeOfBusinessIncorrectError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller = new RetrieveUkPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrieveUkPropertyBsasValidatorFactory,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.retrieve(validNino, calculationId.calculationId, taxYear = None)(fakeGetRequest)
  }

}
