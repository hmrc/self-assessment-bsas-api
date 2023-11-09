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

package shared.config

import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait ConnectorConfigs {

  protected val config: ServicesConfig
  protected val configuration: Configuration

  def desBaseUrl: String                         = config.baseUrl("des")
  def desEnv: String                             = config.getString("microservice.services.des.env")
  def desToken: String                           = config.getString("microservice.services.des.token")
  def desEnvironmentHeaders: Option[Seq[String]] = configuration.getOptional[Seq[String]]("microservice.services.des.environmentHeaders")

  def desDownstreamConfig: DownstreamConfig =
    DownstreamConfig(baseUrl = desBaseUrl, env = desEnv, token = desToken, environmentHeaders = desEnvironmentHeaders)

}
