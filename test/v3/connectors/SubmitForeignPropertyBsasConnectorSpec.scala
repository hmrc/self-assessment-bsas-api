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

package v3.connectors

import domain.Nino
import v3.models.domain.TaxYear
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.foreignProperty._

import scala.concurrent.Future

class SubmitForeignPropertyBsasConnectorSpec extends ConnectorSpec {

  private val nino   = Nino("AA123456A")
  private val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val submitForeignPropertyBsasRequestBodyModel: SubmitForeignPropertyBsasRequestBody =
    SubmitForeignPropertyBsasRequestBody(None, None)

  trait Test { _: ConnectorTest =>
    val connector: SubmitForeignPropertyBsasConnector = new SubmitForeignPropertyBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    def requestWith(taxYear: Option[TaxYear]): SubmitForeignPropertyBsasRequestData =
      SubmitForeignPropertyBsasRequestData(nino, bsasId, taxYear, submitForeignPropertyBsasRequestBodyModel)
  }

  "submitBsas" must {

    "post a SubmitBsasRequest body and return the result for a request without a tax year" in new IfsTest with Test {
      val request = requestWith(taxYear = None)
      val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId", body = submitForeignPropertyBsasRequestBodyModel)
        .returns(Future.successful(outcome))

      await(connector.submitForeignPropertyBsas(request)) shouldBe outcome
    }

    "post a SubmitBsasRequest body and return the result for a pre-TYS tax year request" in new IfsTest with Test {
      val request = requestWith(Some(TaxYear.fromMtd("2022-23")))
      val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId", body = submitForeignPropertyBsasRequestBodyModel)
        .returns(Future.successful(outcome))

      await(connector.submitForeignPropertyBsas(request)) shouldBe outcome
    }

    "post a SubmitBsasRequest body and return the result for a post-TYS tax year request" in new TysIfsTest with Test {
      val request = requestWith(Some(TaxYear.fromMtd("2023-24")))
      val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(url = s"$baseUrl/income-tax/adjustable-summary-calculation/23-24/${nino.nino}/$bsasId",
              body = submitForeignPropertyBsasRequestBodyModel)
        .returns(Future.successful(outcome))

      await(connector.submitForeignPropertyBsas(request)) shouldBe outcome
    }
  }
}
