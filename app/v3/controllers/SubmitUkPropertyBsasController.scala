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

import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import shared.config.AppConfig
import shared.controllers._
import shared.hateoas.HateoasFactory
import shared.routing.{Version, Version3}
import shared.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import shared.utils.IdGenerator
import v3.controllers.validators.SubmitUkPropertyBsasValidatorFactory
import v3.models.response.SubmitUkPropertyBsasHateoasData
import v3.services._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class SubmitUkPropertyBsasController @Inject() (val authService: EnrolmentsAuthService,
                                                val lookupService: MtdIdLookupService,
                                                validatorFactory: SubmitUkPropertyBsasValidatorFactory,
                                                service: SubmitUkPropertyBsasService,
                                                hateoasFactory: HateoasFactory,
                                                auditService: AuditService,
                                                cc: ControllerComponents,
                                                val idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitUkPropertyBsasController", endpointName = "submitUkPropertyBsas")

  def handleRequest(nino: String, calculationId: String, taxYear: Option[String]): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val apiVersion: Version = Version.from(request, orElse = Version3)
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, calculationId, taxYear, request.body)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.submitPropertyBsas)
          .withHateoasResultFrom(hateoasFactory) { (parsedRequest, _) =>
            SubmitUkPropertyBsasHateoasData(nino, calculationId, parsedRequest.taxYear)
          }
          .withAuditing(AuditHandler(
            auditService,
            auditType = "SubmitUKPropertyAccountingAdjustments",
            transactionName = "submit-uk-property-accounting-adjustments",
            apiVersion = apiVersion,
            params = Map("nino" -> nino, "calculationId" -> calculationId),
            requestBody = Some(request.body),
            includeResponse = true
          ))

      requestHandler.handleRequest()
    }

}
