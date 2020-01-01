/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.hateoas

import config.AppConfig
import v1.models.hateoas.Link
import v1.models.hateoas.Method._
import v1.models.hateoas.RelType._

trait HateoasLinks {

  //Domain URIs
  private def bsasBasUri(appConfig: AppConfig, nino: String) =
    s"/${appConfig.apiGatewayContext}/$nino"

  private def listUri(appConfig: AppConfig, nino: String): String = bsasBasUri(appConfig, nino)

  private def triggerUri(appConfig: AppConfig, nino: String): String =
    bsasBasUri(appConfig, nino) + "/trigger"

  private def selfEmploymentBsasUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/self-employment/$bsasId"

  private def propertyBsasUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/property/$bsasId"

  private def selfEmploymentAdjustmentUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/self-employment/$bsasId/adjust"

  private def propertyAdjustmentUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/property/$bsasId/adjust"

  //API resource links

  //L1
  def triggerBsas(appConfig: AppConfig, nino: String): Link =
    Link(href = triggerUri(appConfig, nino), method = POST, rel = TRIGGER)

  //L2
  def getSelfEmploymentBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = selfEmploymentBsasUri(appConfig, nino, bsasId), method = GET, rel = SELF)

  //L2 with adjusted flag
  def getAdjustedSelfEmploymentBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = selfEmploymentBsasUri(appConfig, nino, bsasId) + "?adjustedStatus=true" , method = GET, rel = RETRIEVE_BSAS)

  //L3
  def getPropertyBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = propertyBsasUri(appConfig, nino, bsasId), method = GET, rel = SELF)

  //L3 with adjusted flag
  def getAdjustedPropertyBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = propertyBsasUri(appConfig, nino, bsasId) + "?adjustedStatus=true", method = GET, rel = RETRIEVE_BSAS)

  //L4
  def adjustSelfEmploymentBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(
      href = selfEmploymentAdjustmentUri(appConfig, nino, bsasId),
      method = POST,
      rel = SUBMIT_ADJUSTMENTS
    )

  //L5
  def adjustPropertyBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(
      href = propertyAdjustmentUri(appConfig, nino, bsasId),
      method = POST,
      rel = SUBMIT_ADJUSTMENTS
    )

  //L6
  def getSelfEmploymentBsasAdjustments(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(
      href = selfEmploymentAdjustmentUri(appConfig, nino, bsasId),
      method = GET,
      rel = SELF
    )

  //L7
  def getPropertyBsasAdjustments(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(
      href = propertyAdjustmentUri(appConfig, nino, bsasId),
      method = GET,
      rel = SELF
    )

  //L8
  def listBsas(appConfig: AppConfig, nino: String): Link =
    Link(href = listUri(appConfig, nino), method = GET, rel = SELF)
}
