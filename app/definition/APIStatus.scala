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

package definition

import play.api.libs.json.Format
import utils.enums.Enums

sealed trait APIStatus

object APIStatus extends Enumeration {
  case object ALPHA extends APIStatus
  case object BETA extends APIStatus
  case object STABLE extends APIStatus
  case object DEPRECATED extends APIStatus
  case object RETIRED extends APIStatus

  implicit val formatApiVersion: Format[APIStatus] = Enums.format[APIStatus]
  val parser: PartialFunction[String, APIStatus] = Enums.parser[APIStatus]
}
