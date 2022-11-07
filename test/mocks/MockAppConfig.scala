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

package mocks

import config.{AppConfig, ConfidenceLevelConfig}
import org.scalamock.handlers.{CallHandler, CallHandler0}
import org.scalamock.scalatest.MockFactory
import play.api.Configuration

trait MockAppConfig extends MockFactory {

  val mockAppConfig: AppConfig = mock[AppConfig]

  object MockedAppConfig {

    // DES Config
    def desBaseUrl: CallHandler[String] = (mockAppConfig.desBaseUrl _: () => String).expects()
    def desToken: CallHandler[String] = (mockAppConfig.desToken _).expects()
    def desEnv: CallHandler[String] = (mockAppConfig.desEnv _).expects()
    def desEnvironmentHeaders: CallHandler[Option[Seq[String]]] = (mockAppConfig.desEnvironmentHeaders _).expects()

    // IFS Config
    def ifsBaseUrl: CallHandler[String] = (mockAppConfig.ifsBaseUrl _: () => String).expects()
    def ifsToken: CallHandler[String] = (mockAppConfig.ifsToken _).expects()
    def ifsEnv: CallHandler[String] = (mockAppConfig.ifsEnv _).expects()
    def ifsEnabled: CallHandler[Boolean] = (mockAppConfig.ifsEnabled _).expects()
    def ifsEnvironmentHeaders: CallHandler[Option[Seq[String]]] = (mockAppConfig.ifsEnvironmentHeaders _).expects()

    // TYS IFS Config
    def tysIfsBaseUrl: CallHandler[String] = (mockAppConfig.tysIfsBaseUrl _: () => String).expects()
    def tysIfsToken: CallHandler[String] = (mockAppConfig.tysIfsToken _).expects()
    def tysIfsEnv: CallHandler[String] = (mockAppConfig.tysIfsEnv _).expects()
    def tysIfsEnvironment: CallHandler[String] = (mockAppConfig.tysIfsEnv _).expects()
    def tysIfsEnvironmentHeaders: CallHandler[Option[Seq[String]]] = (mockAppConfig.tysIfsEnvironmentHeaders _).expects()

    // MTD ID Loopup Config
    def mtdIdBaseUrl: CallHandler[String] = (mockAppConfig.mtdIdBaseUrl _: () => String).expects()

    // API Config
    def apiGatewayContext: CallHandler[String] = (mockAppConfig.apiGatewayContext _: () => String).expects()
    def apiStatus1: CallHandler[String] = (mockAppConfig.apiStatus: String => String).expects("1.0")
    def apiStatus2: CallHandler[String] = (mockAppConfig.apiStatus: String => String).expects("2.0")
    def apiStatus3: CallHandler[String] = (mockAppConfig.apiStatus: String => String).expects("3.0")

    def featureSwitches: CallHandler[Configuration] = (mockAppConfig.featureSwitches _: () => Configuration).expects()
    def endpointsEnabled: CallHandler[Boolean] = (mockAppConfig.endpointsEnabled: String => Boolean).expects(*)
    def confidenceLevelCheckEnabled: CallHandler[ConfidenceLevelConfig] = (mockAppConfig.confidenceLevelConfig _: () => ConfidenceLevelConfig).expects()
    def mtdNrsProxyBaseUrl: CallHandler[String] = (mockAppConfig.mtdNrsProxyBaseUrl _).expects()

    // Trigger BSAS minimum tax years
    def v3TriggerForeignBsasMinimumTaxYear: CallHandler[String] = (mockAppConfig.v3TriggerForeignBsasMinimumTaxYear _: () => String).expects()
    def v3TriggerNonForeignBsasMinimumTaxYear: CallHandler0[String] = (mockAppConfig.v3TriggerNonForeignBsasMinimumTaxYear _: () => String).expects()
  }
}
