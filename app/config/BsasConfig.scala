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

import com.typesafe.config.Config
import play.api.{ConfigLoader, Configuration}
import shared.config.FeatureSwitches
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.util.Try

/** Put API-specific config here...
  */
@Singleton
class BsasConfig @Inject() (config: ServicesConfig, configuration: Configuration) {

  def featureSwitchConfig: Configuration = configuration.getOptional[Configuration](s"feature-switch").getOrElse(Configuration.empty)

  def featureSwitches: FeatureSwitches = BsasFeatureSwitches(featureSwitchConfig)

  def secondaryAgentEndpointsAccessControlConfig: SecondaryAgentEndpointsAccessControlConfig =
    configuration.get[SecondaryAgentEndpointsAccessControlConfig](s"api.secondary-agent-endpoints-access-control")

  // V3 Trigger BSAS minimum dates
  def v3TriggerForeignBsasMinimumTaxYear: String    = config.getString("v3TriggerForeignBsasMinimumTaxYear")
  def v3TriggerNonForeignBsasMinimumTaxYear: String = config.getString("v3TriggerNonForeignBsasMinimumTaxYear")

}

case class SecondaryAgentEndpointsAccessControlConfig(listBsas: Boolean,
                                                      triggerBsas: Boolean,
                                                      retrieveSelfEmploymentBsas: Boolean,
                                                      submitSelfEmploymentBsas: Boolean,
                                                      retrieveUKPropertyBsas: Boolean,
                                                      submitUKPropertyBsas: Boolean,
                                                      retrieveForeignPropertyBsas: Boolean,
                                                      submitForeignPropertyBsas: Boolean)

object SecondaryAgentEndpointsAccessControlConfig {

  implicit val configLoader: ConfigLoader[SecondaryAgentEndpointsAccessControlConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)
    SecondaryAgentEndpointsAccessControlConfig(
      getBooleanOrDefaultToFalse(config, "listBsas"),
      getBooleanOrDefaultToFalse(config, "triggerBsas"),
      getBooleanOrDefaultToFalse(config, "retrieveSelfEmploymentBsas"),
      getBooleanOrDefaultToFalse(config, "submitSelfEmploymentBsas"),
      getBooleanOrDefaultToFalse(config, "retrieveUKPropertyBsas"),
      getBooleanOrDefaultToFalse(config, "submitUKPropertyBsas"),
      getBooleanOrDefaultToFalse(config, "retrieveForeignPropertyBsas"),
      getBooleanOrDefaultToFalse(config, "submitForeignPropertyBsas")
    )
  }

  private def getBooleanOrDefaultToFalse(config: Config, endpoint: String): Boolean = Try(config.getBoolean(endpoint)).getOrElse(false)

}
