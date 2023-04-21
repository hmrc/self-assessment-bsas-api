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

import api.controllers._
import api.hateoas.HateoasFactory
import api.services.{ AuditService, EnrolmentsAuthService, MtdIdLookupService }
import config.AppConfig
import play.api.libs.json.JsValue
import play.api.mvc.{ Action, AnyContentAsJson, ControllerComponents }
import utils.{ IdGenerator, Logging }
import v2.controllers.requestParsers.SubmitUkPropertyBsasDataParser
import v2.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRawData
import v2.models.response.SubmitUkPropertyBsasHateoasData
import v2.services._

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

@Singleton
class SubmitUkPropertyBsasController @Inject()(val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               nrsService: SubmitUKPropertyBsasNrsProxyService,
                                               parser: SubmitUkPropertyBsasDataParser,
                                               service: SubmitUkPropertyBsasService,
                                               hateoasFactory: HateoasFactory,
                                               auditService: AuditService,
                                               cc: ControllerComponents,
                                               val idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with V2Controller
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitUkPropertyBsasController", endpointName = "submitUkPropertyBsas")

  def submitUkPropertyBsas(nino: String, bsasId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = SubmitUkPropertyBsasRawData(nino, bsasId, AnyContentAsJson(request.body))

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService { parsedRequest =>
            nrsService.submit(nino, parsedRequest.body) //Submit asynchronously to NRS
            service.submitPropertyBsas(parsedRequest)
          }
          .withHateoasResultFrom(hateoasFactory)((_, response) => SubmitUkPropertyBsasHateoasData(nino, response.id))
          .withAuditing(AuditHandler(
            auditService,
            auditType = "submitBusinessSourceAccountingAdjustments",
            transactionName = "submit-uk-property-accounting-adjustments",
            apiVersion = apiVersion,
            params = Map("nino" -> nino, "bsasId" -> bsasId),
            requestBody = Some(request.body),
            includeResponse = true
          ))

      requestHandler.handleRequest(rawData)
    }

}
