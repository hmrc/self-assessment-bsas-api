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

import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import shared.config.AppConfig
import shared.controllers._
import shared.hateoas.HateoasFactory
import shared.routing.Version
import shared.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import shared.utils.{IdGenerator, Logging}
import v4.controllers.validators.TriggerBsasValidatorFactory
import v4.models.domain.TypeOfBusiness
import v4.models.response.triggerBsas.TriggerBsasHateoasData
import v4.services.TriggerBsasService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TriggerBsasController @Inject() (val authService: EnrolmentsAuthService,
                                       val lookupService: MtdIdLookupService,
                                       validatorFactory: TriggerBsasValidatorFactory,
                                       service: TriggerBsasService,
                                       hateoasFactory: HateoasFactory,
                                       auditService: AuditService,
                                       cc: ControllerComponents,
                                       val idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "TriggerBsasController", endpointName = "triggerBsas")

  def triggerBsas(nino: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, request.body)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.triggerBsas)
          .withHateoasResultFrom(hateoasFactory) { (parsedRequest, responseData) =>
            val typeOfBusiness = TypeOfBusiness.parser(parsedRequest.body.typeOfBusiness)
            TriggerBsasHateoasData(nino, typeOfBusiness, responseData.calculationId, Some(parsedRequest.taxYear))
          }
          .withAuditing(AuditHandler(
            auditService,
            auditType = "TriggerBusinessSourceAdjustableSummary",
            transactionName = "trigger-business-source-adjustable-summary",
            apiVersion = Version(request),
            params = Map("nino" -> nino),
            requestBody = Some(request.body),
            includeResponse = true
          ))

      requestHandler.handleRequest()
    }

}
