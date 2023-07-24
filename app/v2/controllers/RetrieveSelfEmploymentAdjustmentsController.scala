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
import play.api.mvc.{ Action, AnyContent, ControllerComponents }
import utils.{ IdGenerator, Logging }
import v2.controllers.requestParsers.RetrieveAdjustmentsRequestParser
import v2.models.request.RetrieveAdjustmentsRawData
import v2.models.response.retrieveBsasAdjustments.selfEmployment.RetrieveSelfEmploymentAdjustmentsHateoasData
import v2.services.RetrieveSelfEmploymentAdjustmentsService

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

@Singleton
class RetrieveSelfEmploymentAdjustmentsController @Inject()(val authService: EnrolmentsAuthService,
                                                            val lookupService: MtdIdLookupService,
                                                            parser: RetrieveAdjustmentsRequestParser,
                                                            service: RetrieveSelfEmploymentAdjustmentsService,
                                                            hateoasFactory: HateoasFactory,
                                                            auditService: AuditService,
                                                            cc: ControllerComponents,
                                                            val idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with V2Controller
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "RetrieveSelfEmploymentAdjustmentsController", endpointName = "retrieve")

  def retrieve(nino: String, bsasId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = RetrieveAdjustmentsRawData(nino, bsasId)

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService(service.retrieveSelfEmploymentsAdjustments)
          .withHateoasResultFrom(hateoasFactory)(
            (_, responseData) => RetrieveSelfEmploymentAdjustmentsHateoasData(nino, responseData.metadata.bsasId))
          .withAuditing(AuditHandler(
            auditService,
            auditType = "retrieveBusinessSourceAccountingAdjustments",
            transactionName = "retrieve-a-self-employment-business-accounting-adjustments",
            apiVersion = apiVersion,
            params = Map("nino" -> nino, "bsasId" -> bsasId),
            requestBody = None,
            includeResponse = true
          ))

      requestHandler.handleRequest(rawData)
    }
}
