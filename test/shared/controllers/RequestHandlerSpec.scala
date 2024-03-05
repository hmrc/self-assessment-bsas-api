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

package shared.controllers

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.catsSyntaxValidatedId
import org.scalamock.handlers.CallHandler
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsString, Json, OWrites}
import play.api.mvc.{AnyContent, AnyContentAsEmpty}
import play.api.test.{FakeRequest, ResultExtractors}
import shared.UnitSpec
import shared.config.Deprecation.{Deprecated, NotDeprecated}
import shared.config.{AppConfig, Deprecation, MockAppConfig}
import shared.controllers.validators.Validator
import shared.hateoas._
import shared.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.auth.UserDetails
import shared.models.errors.{ErrorWrapper, MtdError, NinoFormatError}
import shared.models.outcomes.ResponseWrapper
import shared.routing.{Version, Version3}
import shared.services.{MockAuditService, ServiceOutcome}
import shared.utils.MockIdGenerator
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class RequestHandlerSpec
    extends UnitSpec
    with MockAuditService
    with MockHateoasFactory
    with MockIdGenerator
    with Status
    with HeaderNames
    with ResultExtractors
    with ControllerSpecHateoasSupport
    with MockAppConfig {

  private val successResponseJson = Json.obj("result" -> "SUCCESS!")
  private val successCode         = ACCEPTED

  private val generatedCorrelationId = "generatedCorrelationId"
  private val serviceCorrelationId   = "serviceCorrelationId"

  private val userDetails = UserDetails("mtdId", "Individual", Some("agentReferenceNumber"))

  implicit val userRequest: UserRequest[AnyContent] = {
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(HeaderNames.ACCEPT -> "application/vnd.hmrc.3.0+json")
    UserRequest[AnyContent](userDetails, fakeRequest)
  }

  implicit val appConfig: AppConfig = mockAppConfig
  private val mockService           = mock[DummyService]

  private def service =
    (mockService.service(_: Input.type)(_: RequestContext, _: ExecutionContext)).expects(Input, *, *)

  case object Input
  case object Output { implicit val writes: OWrites[Output.type] = _ => successResponseJson }
  case object HData extends HateoasData

  implicit object HLinksFactory extends HateoasLinksFactory[Output.type, HData.type] {
    override def links(appConfig: AppConfig, data: HData.type): Seq[Link] = hateoaslinks
  }

  trait DummyService {
    def service(input: Input.type)(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[Output.type]]
  }

  def mockDeprecation(deprecationStatus: Deprecation): CallHandler[Validated[String, Deprecation]] =
    MockAppConfig
      .deprecationFor(Version(userRequest))
      .returns(deprecationStatus.valid)
      .anyNumberOfTimes()

  private val successValidatorForRequest = new Validator[Input.type] {
    def validate: Validated[Seq[MtdError], Input.type] = Valid(Input)
  }

  private val singleErrorValidatorForRequest = new Validator[Input.type] {
    def validate: Validated[Seq[MtdError], Input.type] = Invalid(List(NinoFormatError))
  }

  private def auditResult(headerCarrier: HeaderCarrier,
                          response: Boolean,
                          params: Map[String, String],
                          auditType: String,
                          txName: String,
                          requestBody: Some[JsString],
                          validator: Validator[Input.type],
                          responseCode: Int) = {
    val generatedCorrelationId = "generatedCorrelationId"
    MockIdGenerator.generateCorrelationId.returns(generatedCorrelationId).anyNumberOfTimes()

    implicit val hc: HeaderCarrier = headerCarrier

    implicit val endpointLogContext: EndpointLogContext =
      EndpointLogContext(controllerName = "SomeController", endpointName = "someEndpoint")

    implicit val ctx: RequestContext = RequestContext.from(mockIdGenerator, endpointLogContext)

    def auditHandler(includeResponse: Boolean = response): AuditHandler = AuditHandler(
      mockAuditService,
      auditType = auditType,
      transactionName = txName,
      apiVersion = Version3,
      params = params,
      requestBody = requestBody,
      includeResponse = includeResponse
    )

    val basicRequestHandler = RequestHandler
      .withValidator(validator)
      .withService(mockService.service)
      .withPlainJsonResult(responseCode)

    basicRequestHandler.withAuditing(auditHandler())
  }

  private def verifyAudit(correlationId: String,
                          auditResponse: AuditResponse,
                          params: Map[String, String],
                          auditType: String,
                          txName: String,
                          requestBody: Some[JsString]): CallHandler[Future[AuditResult]] =
    MockedAuditService.verifyAuditEvent(
      AuditEvent(
        auditType = auditType,
        transactionName = txName,
        GenericAuditDetail(
          userDetails,
          params = params,
          apiVersion = Version3.name,
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse)
      ))

  "RequestHandler" when {
    "a request is successful" must {
      "return the correct response" in new nonGTSTest {
        val requestHandler = RequestHandler
          .withValidator(successValidatorForRequest)
          .withService(mockService.service)
          .withPlainJsonResult(successCode)

        mockDeprecation(NotDeprecated)

        service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

        val result = requestHandler.handleRequest()

        contentAsJson(result) shouldBe successResponseJson
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe successCode
      }

      "return no content if required" in new nonGTSTest {
        val requestHandler = RequestHandler
          .withValidator(successValidatorForRequest)
          .withService(mockService.service)
          .withNoContentResult()

        mockDeprecation(NotDeprecated)
        service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

        val result = requestHandler.handleRequest()

        contentAsString(result) shouldBe ""
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe NO_CONTENT
      }

      "wrap the response with hateoas links if required§" in new nonGTSTest {
        val requestHandler = RequestHandler
          .withValidator(successValidatorForRequest)
          .withService(mockService.service)
          .withHateoasResult(mockHateoasFactory)(HData, successCode)

        mockDeprecation(NotDeprecated)
        service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

        MockHateoasFactory.wrap(Output, HData) returns HateoasWrapper(Output, hateoaslinks)

        val result = requestHandler.handleRequest()

        contentAsJson(result) shouldBe successResponseJson ++ hateoaslinksJson
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe successCode
      }

      "a request is made to a deprecated version" must {
        "return the correct response" when {
          "deprecatedOn and sunsetDate exists" in new nonGTSTest {

            val requestHandler = RequestHandler
              .withValidator(successValidatorForRequest)
              .withService(mockService.service)
              .withPlainJsonResult(successCode)

            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

            mockDeprecation(
              Deprecated(
                deprecatedOn = LocalDateTime.of(2023, 1, 17, 12, 0),
                sunsetDate = Some(LocalDateTime.of(2024, 1, 17, 12, 0))
              )
            )

            MockAppConfig.apiDocumentationUrl().returns("http://someUrl").anyNumberOfTimes()

            val result = requestHandler.handleRequest()

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            header("Deprecation", result) shouldBe Some("Tue, 17 Jan 2023 12:00:00 GMT")
            header("Sunset", result) shouldBe Some("Wed, 17 Jan 2024 12:00:00 GMT")
            header("Link", result) shouldBe Some("http://someUrl")

            status(result) shouldBe successCode
          }

          "only deprecatedOn exists" in new nonGTSTest {
            val requestHandler = RequestHandler
              .withValidator(successValidatorForRequest)
              .withService(mockService.service)
              .withPlainJsonResult(successCode)

            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

            mockDeprecation(
              Deprecated(
                deprecatedOn = LocalDateTime.of(2023, 1, 17, 12, 0),
                None
              )
            )
            MockAppConfig.apiDocumentationUrl().returns("http://someUrl").anyNumberOfTimes()

            val result = requestHandler.handleRequest()

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            header("Deprecation", result) shouldBe Some("Tue, 17 Jan 2023 12:00:00 GMT")
            header("Sunset", result) shouldBe None
            header("Link", result) shouldBe Some("http://someUrl")
            status(result) shouldBe successCode
          }
        }
      }
    }

    "a request fails with validation errors" must {
      "return the errors" in new nonGTSTest {
        val requestHandler = RequestHandler
          .withValidator(singleErrorValidatorForRequest)
          .withService(mockService.service)
          .withPlainJsonResult(successCode)

        mockDeprecation(NotDeprecated)

        val result = requestHandler.handleRequest()

        contentAsJson(result) shouldBe NinoFormatError.asJson
        header("X-CorrelationId", result) shouldBe Some(generatedCorrelationId)
        status(result) shouldBe NinoFormatError.httpStatus
      }
    }

    "a request fails with service errors" must {
      "return the errors" in new nonGTSTest {
        val requestHandler = RequestHandler
          .withValidator(successValidatorForRequest)
          .withService(mockService.service)
          .withPlainJsonResult(successCode)

        mockDeprecation(NotDeprecated)
        service returns Future.successful(Left(ErrorWrapper(serviceCorrelationId, NinoFormatError)))

        val result = requestHandler.handleRequest()

        contentAsJson(result) shouldBe NinoFormatError.asJson
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe NinoFormatError.httpStatus
      }
    }

    "a request has a REQUEST_CANNOT_BE_FULFILLED gov-test-scenario heading" must {
      "return RULE_REQUEST_CANNOT_BE_FULFILLED" in new GTSTest {
        val params    = Map("param" -> "value")
        val auditType = "type"
        val txName    = "txName"

        val requestBody = Some(JsString("REQUEST BODY"))

        val requestHandler = auditResult(
          HeaderCarrier(otherHeaders = Seq(("Gov-Test-Scenario", "REQUEST_CANNOT_BE_FULFILLED"))),
          response = true,
          params,
          auditType,
          txName,
          requestBody,
          successValidatorForRequest,
          successCode
        )

        mockDeprecation(NotDeprecated)

        val result = requestHandler.handleRequest()

        status(result) shouldBe 422
        contentAsJson(result) shouldBe Json.parse(
          """
            |{
            |  "code":"RULE_REQUEST_CANNOT_BE_FULFILLED",
            |  "message":"Custom (will vary in production depending on the actual error)"
            |}
            |""".stripMargin
        )
      }
    }

    "auditing is configured" when {

      "a request is successful" when {
        "no response is to be audited" must {
          "audit without the response" in new nonGTSTest {
            val params    = Map("param" -> "value")
            val auditType = "type"
            val txName    = "txName"

            val requestBody = Some(JsString("REQUEST BODY"))

            val requestHandler =
              auditResult(HeaderCarrier(), response = false, params, auditType, txName, requestBody, successValidatorForRequest, successCode)

            mockDeprecation(NotDeprecated)
            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

            val result = requestHandler.handleRequest()

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            status(result) shouldBe successCode

            verifyAudit(serviceCorrelationId, AuditResponse(successCode, Right(None)), params, auditType, txName, requestBody)
          }
        }

        "the response is to be audited" must {
          "audit with the response" in new nonGTSTest {
            val params    = Map("param" -> "value")
            val auditType = "type"
            val txName    = "txName"

            val requestBody = Some(JsString("REQUEST BODY"))

            val requestHandler =
              auditResult(HeaderCarrier(), response = true, params, auditType, txName, requestBody, successValidatorForRequest, successCode)

            mockDeprecation(NotDeprecated)
            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

            val result = requestHandler.handleRequest()

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            status(result) shouldBe successCode

            verifyAudit(serviceCorrelationId, AuditResponse(successCode, Right(Some(successResponseJson))), params, auditType, txName, requestBody)
          }
        }
      }

      "a request fails with validation errors" must {
        "audit the failure" in new nonGTSTest {
          val params    = Map("param" -> "value")
          val auditType = "type"
          val txName    = "txName"

          val requestBody = Some(JsString("REQUEST BODY"))

          val requestHandler =
            auditResult(HeaderCarrier(), response = false, params, auditType, txName, requestBody, singleErrorValidatorForRequest, BAD_REQUEST)

          mockDeprecation(NotDeprecated)

          val result = requestHandler.handleRequest()

          contentAsJson(result) shouldBe NinoFormatError.asJson
          header("X-CorrelationId", result) shouldBe Some(generatedCorrelationId)
          status(result) shouldBe NinoFormatError.httpStatus

          verifyAudit(
            generatedCorrelationId,
            AuditResponse(NinoFormatError.httpStatus, Left(List(AuditError(NinoFormatError.code)))),
            params,
            auditType,
            txName,
            requestBody)
        }
      }

      "a request fails with service errors" must {
        "audit the failure" in new nonGTSTest {
          val params = Map("param" -> "value")

          val auditType = "type"
          val txName    = "txName"

          val requestBody = Some(JsString("REQUEST BODY"))

          val requestHandler =
            auditResult(HeaderCarrier(), response = false, params, auditType, txName, requestBody, successValidatorForRequest, BAD_REQUEST)

          mockDeprecation(NotDeprecated)

          service returns Future.successful(Left(ErrorWrapper(serviceCorrelationId, NinoFormatError)))

          val result = requestHandler.handleRequest()

          contentAsJson(result) shouldBe NinoFormatError.asJson
          header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
          status(result) shouldBe NinoFormatError.httpStatus

          verifyAudit(
            serviceCorrelationId,
            AuditResponse(NinoFormatError.httpStatus, Left(List(AuditError(NinoFormatError.code)))),
            params,
            auditType,
            txName,
            requestBody)
        }
      }
    }
  }

}

trait nonGTSTest extends MockIdGenerator {
  private val generatedCorrelationId = "generatedCorrelationId"
  MockIdGenerator.generateCorrelationId.returns(generatedCorrelationId).anyNumberOfTimes()
  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SomeController", endpointName = "someEndpoint")

  implicit val ctx: RequestContext = RequestContext.from(mockIdGenerator, endpointLogContext)

}

trait GTSTest extends MockIdGenerator {
  private val generatedCorrelationId = "generatedCorrelationId"
  MockIdGenerator.generateCorrelationId.returns(generatedCorrelationId).anyNumberOfTimes()

  implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = Seq(("Gov-Test-Scenario", "REQUEST_CANNOT_BE_FULFILLED")))

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SomeController", endpointName = "someEndpoint")

  implicit val ctx: RequestContext = RequestContext.from(mockIdGenerator, endpointLogContext)

}
