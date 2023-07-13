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

package v2.hateoas

import api.hateoas.Link
import api.hateoas.Method._
import config.AppConfig
import v2.hateoas.RelType._

trait HateoasLinks {

  //L1
  def triggerBsas(appConfig: AppConfig, nino: String): Link =
    Link(href = triggerUri(appConfig, nino), method = POST, rel = TRIGGER)

  private def triggerUri(appConfig: AppConfig, nino: String): String =
    bsasBasUri(appConfig, nino) + "/trigger"

  //L2
  def getSelfEmploymentBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = selfEmploymentBsasUri(appConfig, nino, bsasId), method = GET, rel = SELF)

  private def selfEmploymentBsasUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/self-employment/$bsasId"

  //L2 with adjusted flag
  def getAdjustedSelfEmploymentBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = selfEmploymentBsasUri(appConfig, nino, bsasId) + "?adjustedStatus=true", method = GET, rel = RETRIEVE_BSAS)

  //L3
  def getUkPropertyBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = ukPropertyBsasUri(appConfig, nino, bsasId), method = GET, rel = SELF)

  private def ukPropertyBsasUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/property/$bsasId"

  //L3 with adjusted flag
  def getAdjustedPropertyBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = ukPropertyBsasUri(appConfig, nino, bsasId) + "?adjustedStatus=true", method = GET, rel = RETRIEVE_BSAS)

  //L4
  def adjustSelfEmploymentBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(
      href = selfEmploymentAdjustmentUri(appConfig, nino, bsasId),
      method = POST,
      rel = SUBMIT_ADJUSTMENTS
    )

  //API resource links

  private def selfEmploymentAdjustmentUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/self-employment/$bsasId/adjust"

  //L5
  def adjustPropertyBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(
      href = propertyAdjustmentUri(appConfig, nino, bsasId),
      method = POST,
      rel = SUBMIT_ADJUSTMENTS
    )

  private def propertyAdjustmentUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/property/$bsasId/adjust"

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

  private def listUri(appConfig: AppConfig, nino: String): String = bsasBasUri(appConfig, nino)

  //L9
  def getForeignPropertyBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = foreignPropertyBsasUri(appConfig, nino, bsasId), method = GET, rel = SELF)

  private def foreignPropertyBsasUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/foreign-property/$bsasId"

  //Domain URIs
  private def bsasBasUri(appConfig: AppConfig, nino: String) =
    s"/${appConfig.apiGatewayContext}/$nino"

  def getAdjustedForeignPropertyBsasNoStat(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = foreignPropertyBsasUri(appConfig, nino, bsasId), method = GET, rel = RETRIEVE_BSAS)

  //L9 with adjusted flag
  def getAdjustedForeignPropertyBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = foreignPropertyBsasUri(appConfig, nino, bsasId) + "?adjustedStatus=true", method = GET, rel = RETRIEVE_BSAS)

  //L10
  def adjustForeignPropertyBsas(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(href = foreignPropertyAdjustmentUri(appConfig, nino, bsasId), method = POST, rel = SUBMIT_ADJUSTMENTS)

  //L11
  def getForeignPropertyBsasAdjustments(appConfig: AppConfig, nino: String, bsasId: String): Link =
    Link(
      href = foreignPropertyAdjustmentUri(appConfig, nino, bsasId),
      method = GET,
      rel = SELF
    )

  private def foreignPropertyAdjustmentUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/foreign-property/$bsasId/adjust"

}
