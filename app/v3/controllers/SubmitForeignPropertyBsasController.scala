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

import api.controllers._
import api.hateoas.HateoasFactory
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import config.AppConfig
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import routing.{Version, Version3}
import utils.{IdGenerator, Logging}
import v3.controllers.validators.SubmitForeignPropertyBsasValidatorFactory
import v3.models.response.SubmitForeignPropertyBsasHateoasData
import v3.models.response.SubmitForeignPropertyBsasResponse.SubmitForeignPropertyAdjustmentHateoasFactory
import v3.services._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class SubmitForeignPropertyBsasController @Inject() (val authService: EnrolmentsAuthService,
                                                     val lookupService: MtdIdLookupService,
                                                     nrsService: SubmitForeignPropertyBsasNrsProxyService,
                                                     validatorFactory: SubmitForeignPropertyBsasValidatorFactory,
                                                     service: SubmitForeignPropertyBsasService,
                                                     hateoasFactory: HateoasFactory,
                                                     auditService: AuditService,
                                                     cc: ControllerComponents,
                                                     val idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitForeignPropertyBsasController", endpointName = "SubmitForeignPropertyBsas")

  def handleRequest(nino: String, calculationId: String, taxYear: Option[String]): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val apiVersion: Version = Version.from(request, orElse = Version3)
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, calculationId, taxYear, request.body)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService { parsedRequest =>
            nrsService.submit(nino, parsedRequest.body) // Submit asynchronously to NRS
            service.submitForeignPropertyBsas(parsedRequest)
          }
          .withHateoasResultFrom(hateoasFactory) { (parsedRequest, _) =>
            SubmitForeignPropertyBsasHateoasData(nino, calculationId, parsedRequest.taxYear)
          }
          .withAuditing(AuditHandler(
            auditService,
            auditType = "SubmitForeignPropertyAccountingAdjustments",
            transactionName = "submit-foreign-property-accounting-adjustments",
            apiVersion = apiVersion,
            params = Map("nino" -> nino, "calculationId" -> calculationId),
            requestBody = Some(request.body),
            includeResponse = true
          ))

      requestHandler.handleRequest()
    }

}
