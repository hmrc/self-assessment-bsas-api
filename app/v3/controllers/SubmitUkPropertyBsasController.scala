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
import api.services.{ AuditService, EnrolmentsAuthService, MtdIdLookupService }
import play.api.libs.json.JsValue
import play.api.mvc.{ Action, ControllerComponents }
import utils.{ IdGenerator, Logging }
import v3.controllers.validators.SubmitUkPropertyBsasValidatorFactory
import v3.models.response.SubmitUkPropertyBsasHateoasData
import v3.services._

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

@Singleton
class SubmitUkPropertyBsasController @Inject()(val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               nrsService: SubmitUKPropertyBsasNrsProxyService,
                                               validatorFactory: SubmitUkPropertyBsasValidatorFactory,
                                               service: SubmitUkPropertyBsasService,
                                               hateoasFactory: HateoasFactory,
                                               auditService: AuditService,
                                               cc: ControllerComponents,
                                               val idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with V3Controller
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitUkPropertyBsasController", endpointName = "submitUkPropertyBsas")

  def handleRequest(nino: String, calculationId: String, taxYear: Option[String]): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, calculationId, taxYear, request.body)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService { parsedRequest =>
            nrsService.submit(nino, parsedRequest.body) //Submit asynchronously to NRS
            service.submitPropertyBsas(parsedRequest)
          }
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
