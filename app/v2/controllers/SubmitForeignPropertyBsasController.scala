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

import api.controllers.{AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import api.hateoas.HateoasFactory
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import config.AppConfig
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import utils.{IdGenerator, Logging}
import v2.controllers.requestParsers.SubmitForeignPropertyBsasRequestParser
import v2.models.request.submitBsas.foreignProperty.SubmitForeignPropertyRawData
import v2.models.response.SubmitForeignPropertyBsasHateoasData
import v2.services.{SubmitForeignPropertyBsasNrsProxyService, SubmitForeignPropertyBsasService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class SubmitForeignPropertyBsasController @Inject()(val authService: EnrolmentsAuthService,
                                                    val lookupService: MtdIdLookupService,
                                                    nrsService: SubmitForeignPropertyBsasNrsProxyService,
                                                    parser: SubmitForeignPropertyBsasRequestParser,
                                                    service: SubmitForeignPropertyBsasService,
                                                    hateoasFactory: HateoasFactory,
                                                    cc: ControllerComponents,
                                                    val idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
  extends AuthorisedController(cc)
    with V2Controller
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitForeignPropertyBsasController", endpointName = "SubmitForeignPropertyBsas")

  def handleRequest(nino: String, bsasId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = SubmitForeignPropertyRawData(nino, bsasId, request.body)

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService { parsedRequest =>
            nrsService.submit(nino, parsedRequest.body) //Submit asynchronously to NRS
            service.submitForeignPropertyBsas(parsedRequest)
          }
          .withHateoasResult(hateoasFactory)(SubmitForeignPropertyBsasHateoasData(nino, bsasId))

      requestHandler.handleRequest(rawData)
    }

}
