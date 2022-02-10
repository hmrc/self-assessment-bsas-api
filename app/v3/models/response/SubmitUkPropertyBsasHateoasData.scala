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

package v3.models.response

import config.AppConfig
import v3.hateoas.{HateoasLinks, HateoasLinksFactory}
import v3.models.hateoas.{HateoasData, Link}

object SubmitUkPropertyBsasHateoasData extends HateoasLinks {

  implicit object SubmitPropertyAdjustmentHateoasFactory
    extends HateoasLinksFactory[Unit, SubmitUkPropertyBsasHateoasData] {
    override def links(appConfig: AppConfig, data: SubmitUkPropertyBsasHateoasData): Seq[Link] = {
      import data._

      Seq(
        getUkPropertyBsas(appConfig, nino, calculationId)
      )
    }
  }
}

case class SubmitUkPropertyBsasHateoasData(nino: String, calculationId: String) extends HateoasData