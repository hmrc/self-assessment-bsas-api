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

package v5.listBsas.controllers

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import shared.config.AppConfig
import shared.controllers._
import shared.hateoas.HateoasFactory
import shared.services.{EnrolmentsAuthService, MtdIdLookupService}
import shared.utils._
import v5.listBsas.models.{BsasSummary, ListBsasHateoasData, ListBsasRequestData, ListBsasResponse}
import v5.listBsas.schema.ListBsasSchema
import v5.listBsas.services.ListBsasService
import v5.listBsas.validators.ListBsasValidatorFactory

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListBsasController @Inject() (val authService: EnrolmentsAuthService,
                                    val lookupService: MtdIdLookupService,
                                    validatorFactory: ListBsasValidatorFactory,
                                    service: ListBsasService,
                                    hateoasFactory: HateoasFactory,
                                    cc: ControllerComponents,
                                    val idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "ListBsasController", endpointName = "listBsas")

  def listBsas(nino: String, taxYear: Option[String], typeOfBusiness: Option[String], businessId: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, taxYear, typeOfBusiness, businessId, ListBsasSchema.schemaFor(taxYear))

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.listBsas)
          .withResultCreator(
            ResultCreator.hateoasListWrapping(hateoasFactory)((parsedRequest: ListBsasRequestData, response: ListBsasResponse[BsasSummary]) =>
              ListBsasHateoasData(nino, response, Some(parsedRequest.taxYear))))

      requestHandler.handleRequest()
    }

}
