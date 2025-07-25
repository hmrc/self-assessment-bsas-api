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

package config

import play.api.Configuration
import shared.config.{AppConfigBase, FeatureSwitches, SharedAppConfig}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

/** Put API-specific config here...
  */
@Singleton
class BsasConfig @Inject() (val config: ServicesConfig, val configuration: Configuration) extends AppConfigBase {

  def featureSwitchConfig: Configuration = configuration.getOptional[Configuration](s"feature-switch").getOrElse(Configuration.empty)

  def featureSwitches(implicit appConfig: SharedAppConfig): FeatureSwitches = BsasFeatureSwitches()

  // V3 Trigger BSAS minimum dates
  def v3TriggerForeignBsasMinimumTaxYear: String    = config.getString("v3TriggerForeignBsasMinimumTaxYear")
  def v3TriggerNonForeignBsasMinimumTaxYear: String = config.getString("v3TriggerNonForeignBsasMinimumTaxYear")

}
