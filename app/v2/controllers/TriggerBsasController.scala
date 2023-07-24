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

package v2.controllers

import api.controllers.{ RequestHandler => _, _ }
import api.hateoas.HateoasFactory
import api.services.{ AuditService, EnrolmentsAuthService, MtdIdLookupService }
import config.AppConfig
import play.api.libs.json.JsValue
import play.api.mvc.{ Action, AnyContentAsJson, ControllerComponents }
import utils.{ IdGenerator, Logging }
import v2.controllers.requestParsers.TriggerBsasRequestParser
import v2.models.domain.TypeOfBusiness
import v2.models.request.triggerBsas.TriggerBsasRawData
import v2.models.response.TriggerBsasHateoasData
import v2.services.TriggerBsasService

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

@Singleton
class TriggerBsasController @Inject()(val authService: EnrolmentsAuthService,
                                      val lookupService: MtdIdLookupService,
                                      parser: TriggerBsasRequestParser,
                                      service: TriggerBsasService,
                                      hateoasFactory: HateoasFactory,
                                      auditService: AuditService,
                                      cc: ControllerComponents,
                                      val idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with V2Controller
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "TriggerBsasController", endpointName = "triggerBsas")

  def triggerBsas(nino: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = TriggerBsasRawData(nino, AnyContentAsJson(request.body))

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService(service.triggerBsas)
          .withHateoasResultFrom(hateoasFactory)(
            (parsedRequest, responseData) => TriggerBsasHateoasData(nino, TypeOfBusiness.parser(parsedRequest.body.typeOfBusiness), responseData.id),
            successStatus = CREATED
          )
          .withAuditing(AuditHandler(
            auditService,
            auditType = "triggerABusinessSourceAdjustableSummary",
            transactionName = "trigger-a-business-source-adjustable-Summary",
            apiVersion = apiVersion,
            params = Map("nino" -> nino),
            requestBody = Some(request.body),
            includeResponse = true
          ))

      requestHandler.handleRequest(rawData)
    }
}
