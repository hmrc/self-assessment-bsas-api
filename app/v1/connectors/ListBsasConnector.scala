/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.connectors

import config.AppConfig
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import v1.models.request.ListBsasRequest
import v1.models.response.listBsas.{BsasEntries, ListBsasResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBsasConnector @Inject()(val http: HttpClient,
                                  val appConfig: AppConfig) extends BaseDesConnector {

  def listBsas(request: ListBsasRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext): Future[DesOutcome[ListBsasResponse[BsasEntries]]] = {

    import v1.connectors.httpparsers.StandardDesHttpParser._

    val nino = request.nino.nino

    val queryParams = Map(
      "taxYear" -> request.taxYear.toString,
      "incomeSourceIdentifier" -> request.incomeSourceIdentifier,
      "identifierValue" -> request.identifierValue
    ).filter { // filter for ...
      case (_, v) =>
        v.isInstanceOf[String] || // ... if value is String or ...
          (v.isInstanceOf[Some[Any]] && // ... not None and ...
            v.asInstanceOf[Some[Any]].exists(_.isInstanceOf[String])) // ... is Some[String] (need to split up due to type erasure)
    }

    def queryMap[A](as: Map[String, A]): Map[String, String] = as.map {
      case (k: String, Some(v: String)) => (k, v)
      case (k: String, v: String) => (k, v)
    }

    val mappedQueryParams: Map[String, String] = queryMap(queryParams)

    get(
      DesUri[ListBsasResponse[BsasEntries]](s"income-tax/adjustable-summary-calculation/$nino"), mappedQueryParams.toSeq
    )
  }
}
