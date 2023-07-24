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

import api.controllers.{AuditHandler, AuthorisedController, EndpointLogContext, RequestContext}
import api.hateoas.HateoasFactory
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import config.AppConfig
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{CurrentDate, DateUtils, IdGenerator, Logging}
import v2.controllers.requestParsers.ListBsasRequestParser
import v2.models.domain.DownstreamTaxYear
import v2.models.request.ListBsasRawData
import v2.models.response.listBsas.ListBsasHateoasData
import v2.services.ListBsasService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListBsasController @Inject()(val authService: EnrolmentsAuthService,
                                   val lookupService: MtdIdLookupService,
                                   parser: ListBsasRequestParser,
                                   service: ListBsasService,
                                   hateoasFactory: HateoasFactory,
                                   auditService: AuditService,
                                   cc: ControllerComponents,
                                   val currentDateProvider: CurrentDate,
                                   val idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with V2Controller
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "ListBsasController", endpointName = "listBsas")

  def listBsas(nino: String, taxYear: Option[String], typeOfBusiness: Option[String], businessId: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      lazy val currentMtdTaxYear = DownstreamTaxYear.fromDownstream(DateUtils.getDownstreamTaxYear(currentDateProvider.getCurrentDate()).toString)

      val rawData = ListBsasRawData(nino, taxYear, typeOfBusiness, businessId)

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService(service.listBsas)
          .withAuditing(AuditHandler(
            auditService,
            auditType = "listBusinessSourceAdjustableSummaries",
            transactionName = "list-business-source-adjustable-summaries",
            apiVersion = apiVersion,
            params = Map("nino" -> nino, "taxYear" -> taxYear.getOrElse(currentMtdTaxYear)),
            requestBody = None,
            includeResponse = true
          ))
          .withResultCreator(ResultCreator.hateoasListWrapping(hateoasFactory)((_, responseData) => ListBsasHateoasData(nino, responseData)))

      requestHandler.handleRequest(rawData)
    }
}
