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

package v3.hateoas

import api.hateoas.Link
import api.hateoas.Method._
import api.models.domain.TaxYear
import config.AppConfig
import v3.hateoas.RelType._

trait HateoasLinks {

  // API resource links
  // L1
  def triggerBsas(appConfig: AppConfig, nino: String): Link =
    Link(href = bsasBasUri(appConfig, nino) + "/trigger", method = POST, rel = TRIGGER)

  // L2
  def getSelfEmploymentBsas(appConfig: AppConfig, nino: String, calcId: String, taxYear: Option[TaxYear]): Link = {
    val href = withTaxYearParameter(bsasBasUri(appConfig, nino) + s"/self-employment/$calcId", taxYear)
    Link(href = href, method = GET, rel = SELF)
  }

  // L3
  def getUkPropertyBsas(appConfig: AppConfig, nino: String, calcId: String, taxYear: Option[TaxYear]): Link = {
    val href = withTaxYearParameter(bsasBasUri(appConfig, nino) + s"/uk-property/$calcId", taxYear)
    Link(href = href, method = GET, rel = SELF)
  }

  private def withTaxYearParameter(uri: String, maybeTaxYear: Option[TaxYear]): String = {
    maybeTaxYear match {
      case Some(taxYear) if taxYear.useTaxYearSpecificApi => s"$uri?taxYear=${taxYear.asMtd}"
      case _                                              => uri
    }
  }

  private def bsasBasUri(appConfig: AppConfig, nino: String) =
    s"/${appConfig.apiGatewayContext}/$nino"

  // L4
  def getForeignPropertyBsas(appConfig: AppConfig, nino: String, calcId: String, taxYear: Option[TaxYear]): Link = {
    val href = withTaxYearParameter(bsasBasUri(appConfig, nino) + s"/foreign-property/$calcId", taxYear)
    Link(href = href, method = GET, rel = SELF)
  }

  // L5
  def listBsas(appConfig: AppConfig, nino: String, taxYear: Option[TaxYear]): Link = {
    val href = withTaxYearParameter(bsasBasUri(appConfig, nino), taxYear)
    Link(href = href, method = GET, rel = SELF)
  }

  // L6
  def adjustSelfEmploymentBsas(appConfig: AppConfig, nino: String, calcId: String, taxYear: Option[TaxYear]): Link = {
    val href = withTaxYearParameter(bsasBasUri(appConfig, nino) + s"/self-employment/$calcId/adjust", taxYear)
    Link(href = href, method = POST, rel = SUBMIT_SE_ADJUSTMENTS)
  }

  // L7
  def adjustUkPropertyBsas(appConfig: AppConfig, nino: String, calcId: String, taxYear: Option[TaxYear]): Link = {
    val href = withTaxYearParameter(bsasBasUri(appConfig, nino) + s"/uk-property/$calcId/adjust", taxYear)
    Link(href = href, method = POST, rel = SUBMIT_UK_PROPERTY_ADJUSTMENTS)
  }

  // L8
  def adjustForeignPropertyBsas(appConfig: AppConfig, nino: String, calcId: String, taxYear: Option[TaxYear]): Link = {
    val href = withTaxYearParameter(bsasBasUri(appConfig, nino) + s"/foreign-property/$calcId/adjust", taxYear)
    Link(href = href, method = POST, rel = SUBMIT_FOREIGN_PROPERTY_ADJUSTMENTS)
  }

}
