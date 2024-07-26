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

package v4.hateoas

object RelType {
  val TRIGGER                             = "trigger-business-source-adjustable-summary"
  val SUBMIT_SE_ADJUSTMENTS               = "submit-self-employment-accounting-adjustments"
  val SUBMIT_UK_PROPERTY_ADJUSTMENTS      = "submit-uk-property-accounting-adjustments"
  val SUBMIT_FOREIGN_PROPERTY_ADJUSTMENTS = "submit-foreign-property-accounting-adjustments"

  val SELF = "self"
}
