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

package v6.foreignPropertyBsas.submit

import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import shared.config.SharedAppConfig
import shared.controllers.*
import shared.routing.Version
import shared.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import shared.utils.IdGenerator

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class SubmitForeignPropertyBsasController @Inject() (
    val authService: EnrolmentsAuthService,
    val lookupService: MtdIdLookupService,
    validatorFactory: SubmitForeignPropertyBsasValidatorFactory,
    service: SubmitForeignPropertyBsasService,
    auditService: AuditService,
    cc: ControllerComponents,
    val idGenerator: IdGenerator
)(implicit
    ec: ExecutionContext,
    appConfig: SharedAppConfig
) extends AuthorisedController(cc) {

  val endpointName = "submit-foreign-property-bsas"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitForeignPropertyBsasController", endpointName = "SubmitForeignPropertyBsas")

  def handleRequest(nino: String, calculationId: String, taxYear: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)
      val validator                    = validatorFactory.validator(nino, calculationId, taxYear, request.body)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.submitForeignPropertyBsas)
          .withAuditing(AuditHandler(
            auditService,
            auditType = "SubmitForeignPropertyAccountingAdjustments",
            transactionName = "submit-foreign-property-accounting-adjustments",
            apiVersion = Version(request),
            params = Map("nino" -> nino, "calculationId" -> calculationId),
            requestBody = Some(request.body),
            includeResponse = true
          ))
          .withNoContentResult(OK)

      requestHandler.handleRequest()
    }

}
