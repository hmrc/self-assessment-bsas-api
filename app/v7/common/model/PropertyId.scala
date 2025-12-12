/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.common.model

import play.api.libs.json.{Reads, Writes, JsString}

case class PropertyId(propertyId: String) {

  override def toString: String = propertyId
}

object PropertyId {

  implicit val reads: Reads[PropertyId]   = Reads.of[String].map(PropertyId(_))
  implicit val writes: Writes[PropertyId] = propertyId => JsString(propertyId.propertyId)
}
