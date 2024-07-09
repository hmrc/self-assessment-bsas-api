/*
 * Copyright 2024 HM Revenue & Customs
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

package shared.models.audit

import play.api.libs.json.{JsValue, Json}
import shared.models.audit.AuditResponseFixture.{auditResponseModelWithBody, auditResponseModelWithErrors}
import shared.models.auth.UserDetails

object FlattenedGenericAuditDetailFixture {

  val nino: String                         = "ZG903729C"
  val calculationId: String                = "calcId"
  val userType: String                     = "Agent"
  val agentReferenceNumber: Option[String] = Some("012345678")
  val userDetails: UserDetails = UserDetails(calculationId, userType, agentReferenceNumber)
  val pathParams: Map[String, String]      = Map("nino" -> nino, "calculationId" -> calculationId)
  val itsaStatuses: Option[JsValue]         = Some(Json.obj("field1" -> "value1"))
  val xCorrId                              = "a1e8057e-fbbc-47a8-a8b478d9f015c253"
  val versionNumber: String                = "3.0"


  val flattenedGenericAuditDetailSuccess: FlattenedGenericAuditDetail =
    FlattenedGenericAuditDetail(
      versionNumber = Some(versionNumber),
      userDetails = userDetails,
      params = pathParams,
      futureYears = None,
      history = None,
      itsaStatuses = itsaStatuses,
      `X-CorrelationId` = xCorrId,
      auditResponse = auditResponseModelWithBody)


  val flattenedGenericAuditDetailJsonSuccess: JsValue = Json.parse(
    s"""
       |{
       |   "versionNumber": "$versionNumber",
       |   "userType" : "$userType",
       |   "agentReferenceNumber" : "${agentReferenceNumber.get}",
       |   "nino": "$nino",
       |   "calculationId" : "$calculationId",
       |   "field1":"value1",
       |   "X-CorrelationId": "$xCorrId",
       |   "outcome": "success",
       |   "httpStatusCode": 200
       |}
     """.stripMargin
  )

  val flattenedGenericAuditDetailErrors: FlattenedGenericAuditDetail =
    FlattenedGenericAuditDetail(
      versionNumber = Some(versionNumber),
      userDetails = userDetails,
      params = pathParams,
      futureYears = None,
      history = None,
      itsaStatuses = itsaStatuses,
      `X-CorrelationId` = xCorrId,
      auditResponse = auditResponseModelWithErrors)


  val flattenedGenericAuditDetailJsonErrors: JsValue = Json.parse(
    s"""
       |{
       |   "versionNumber": "$versionNumber",
       |   "userType" : "$userType",
       |   "agentReferenceNumber" : "${agentReferenceNumber.get}",
       |   "nino": "$nino",
       |   "calculationId" : "$calculationId",
       |   "field1":"value1",
       |   "X-CorrelationId": "$xCorrId",
       |   "outcome": "error",
       |   "httpStatusCode": 400,
       |   "errorCodes": [
       |      "FORMAT_NINO",
       |      "FORMAT_TAX_YEAR"
       |   ]
       |}
     """.stripMargin
  )

}
