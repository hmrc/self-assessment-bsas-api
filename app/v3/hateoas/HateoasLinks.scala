/*
 * Copyright 2022 HM Revenue & Customs
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

import config.AppConfig
import v3.models.hateoas.Link
import v3.models.hateoas.Method._
import v3.models.hateoas.RelType._

trait HateoasLinks {

  private def bsasBasUri(appConfig: AppConfig, nino: String) =
    s"/${appConfig.apiGatewayContext}/$nino"

<<<<<<< HEAD
=======
  private def listUri(appConfig: AppConfig, nino: String): String = bsasBasUri(appConfig, nino)

  private def triggerUri(appConfig: AppConfig, nino: String): String =
    bsasBasUri(appConfig, nino) + "/trigger"

  private def selfEmploymentBsasUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/self-employment/$bsasId"

  private def ukPropertyBsasUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/property/$bsasId"

  private def foreignPropertyBsasUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/foreign-property/$bsasId"

  private def selfEmploymentAdjustmentUri(appConfig: AppConfig, nino: String, calculationId: String): String =
    bsasBasUri(appConfig, nino) + s"/self-employment/$calculationId/adjust"

  private def propertyAdjustmentUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/property/$bsasId/adjust"

  private def foreignPropertyAdjustmentUri(appConfig: AppConfig, nino: String, bsasId: String): String =
    bsasBasUri(appConfig, nino) + s"/foreign-property/$bsasId/adjust"

  //API resource links

>>>>>>> c738ca6 (Further additions to correct the model errors)
  //L1
  def triggerBsas(appConfig: AppConfig, nino: String): Link =
    Link(href = bsasBasUri(appConfig, nino) + "/trigger", method = POST, rel = TRIGGER)

  //L2
  def getSelfEmploymentBsas(appConfig: AppConfig, nino: String, calcId: String): Link =
    Link(href = bsasBasUri(appConfig, nino) + s"/self-employment/$calcId", method = GET, rel = SELF)

  //L3
  def getUkPropertyBsas(appConfig: AppConfig, nino: String, calcId: String): Link =
    Link(href = bsasBasUri(appConfig, nino) + s"/property/$calcId", method = GET, rel = SELF)

  //L4
  def getForeignPropertyBsas(appConfig: AppConfig, nino: String, calcId: String): Link =
    Link(href = bsasBasUri(appConfig, nino) + s"/foreign-property/$calcId", method = GET, rel = SELF)

  //L5
  def listBsas(appConfig: AppConfig, nino: String): Link =
    Link(href = bsasBasUri(appConfig, nino), method = GET, rel = SELF)

  // L6
  def adjustSelfEmploymentBsas(appConfig: AppConfig, nino: String, calcId: String): Link =
    Link(
      href = bsasBasUri(appConfig, nino) + s"/self-employment/$calcId/adjust",
      method = POST,
      rel = SUBMIT_SE_ADJUSTMENTS
    )

  //L7
  def adjustPropertyBsas(appConfig: AppConfig, nino: String, calcId: String): Link =
    Link(
      href = bsasBasUri(appConfig, nino) + s"/property/$calcId/adjust",
      method = POST,
      rel = SUBMIT_UK_PROPERTY_ADJUSTMENTS
    )

  //L8
  def adjustForeignPropertyBsas(appConfig: AppConfig, nino: String, calcId: String): Link =
    Link(
      href = bsasBasUri(appConfig, nino) + s"/foreign-property/$calcId/adjust",
      method = POST,
      rel = SUBMIT_FOREIGN_PROPERTY_ADJUSTMENTS
    )
}
