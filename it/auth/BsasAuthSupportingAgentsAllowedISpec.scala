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
 * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package auth

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import shared.auth.AuthSupportingAgentsAllowedISpec
import v6.bsas.trigger.def1.model.Def1_TriggerBsasFixtures
import v6.common.model.TypeOfBusiness

class BsasAuthSupportingAgentsAllowedISpec extends AuthSupportingAgentsAllowedISpec {

  val callingApiVersion = "6.0"

  val supportingAgentsAllowedEndpoint = "trigger-bsas"

  val mtdUrl = s"/$nino/trigger"

  def sendMtdRequest(request: WSRequest): WSResponse = await(request.post(requestJson))

  val downstreamUri: String = s"/income-tax/adjustable-summary-calculation/$nino"

  val maybeDownstreamResponseJson: Option[JsValue] = Some(
    Json.parse(Def1_TriggerBsasFixtures.downstreamResponse)
  )

  private val requestJson =
    Json.obj(
      "accountingPeriod" -> Json.obj("startDate" -> "2019-01-01", "endDate" -> "2019-10-31"),
      "typeOfBusiness"   -> TypeOfBusiness.`self-employment`.toString,
      "businessId"       -> "XAIS12345678901"
    )

}
