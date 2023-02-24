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

import api.controllers.{AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import api.hateoas.HateoasFactory
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.RetrieveUkPropertyRequestParser
import v3.models.request.retrieveBsas.ukProperty.RetrieveUkPropertyBsasRawData
import v3.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyHateoasData
import v3.services.RetrieveUkPropertyBsasService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RetrieveUkPropertyBsasController @Inject()(
    val authService: EnrolmentsAuthService,
    val lookupService: MtdIdLookupService,
    parser: RetrieveUkPropertyRequestParser,
    service: RetrieveUkPropertyBsasService,
    hateoasFactory: HateoasFactory,
    cc: ControllerComponents,
    val idGenerator: IdGenerator
)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with V3Controller
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "RetrievePropertyBsasController",
      endpointName = "retrieve"
    )

  def retrieve(nino: String, calculationId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = RetrieveUkPropertyBsasRawData(nino, calculationId, taxYear)

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService(service.retrieve)
          .withHateoasResultFrom(hateoasFactory)((parsedRequest, responseData) =>
            RetrieveUkPropertyHateoasData(nino, responseData.metadata.calculationId, parsedRequest.taxYear))

      requestHandler.handleRequest(rawData)
    }

}
