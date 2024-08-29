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

import shared.config.AppConfig
import shared.definition._
import shared.routing.{Version4, Version5, Version6}

import javax.inject.{Inject, Singleton}

@Singleton
class BsasApiDefinitionFactory @Inject() (protected val appConfig: AppConfig) extends ApiDefinitionFactory {

  lazy val definition: Definition =
    Definition(
      scopes = scopes,
      api = APIDefinition(
        name = "Business Source Adjustable Summary (MTD)",
        description = "An API for providing business source adjustable summary data",
        context = appConfig.apiGatewayContext,
        categories = List(mtdCategory),
        versions = List(
          APIVersion(
            version = Version4,
            status = buildAPIStatus(Version4),
            endpointsEnabled = appConfig.endpointsEnabled(Version4)
          ),
          APIVersion(
            version = Version5,
            status = buildAPIStatus(Version5),
            endpointsEnabled = appConfig.endpointsEnabled(Version5)
          ),
          APIVersion(
            version = Version6,
            status = buildAPIStatus(Version6),
            endpointsEnabled = appConfig.endpointsEnabled(Version6)
          )
        ),
        requiresTrust = None
      )
    )

}
