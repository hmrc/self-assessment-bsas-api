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

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import shared.config.AppConfig
import shared.controllers.{AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import shared.hateoas.HateoasFactory
import shared.services.{EnrolmentsAuthService, MtdIdLookupService}
import shared.utils.{IdGenerator, Logging}
import v4.controllers.validators.RetrieveSelfEmploymentBsasValidatorFactory
import v4.models.response.retrieveBsas.selfEmployment.RetrieveSelfAssessmentBsasHateoasData
import v4.services.RetrieveSelfEmploymentBsasService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RetrieveSelfEmploymentBsasController @Inject() (
    val authService: EnrolmentsAuthService,
    val lookupService: MtdIdLookupService,
    validatorFactory: RetrieveSelfEmploymentBsasValidatorFactory,
    service: RetrieveSelfEmploymentBsasService,
    hateoasFactory: HateoasFactory,
    cc: ControllerComponents,
    val idGenerator: IdGenerator
)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with Logging {

  val endpointName = "retrieve-self-employment-bsas"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "RetrieveSelfEmploymentBsasController", endpointName = "retrieve")

  def handleRequest(nino: String, calculationId: String, taxYear: Option[String] = None): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, calculationId, taxYear)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.retrieveSelfEmploymentBsas)
          .withHateoasResultFrom(hateoasFactory)((parsedRequest, responseData) =>
            RetrieveSelfAssessmentBsasHateoasData(nino, responseData.metadata.calculationId, parsedRequest.taxYear))

      requestHandler.handleRequest()
    }

}
