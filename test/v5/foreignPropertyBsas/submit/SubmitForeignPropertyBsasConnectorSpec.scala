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

package v5.foreignPropertyBsas.submit

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v5.foreignPropertyBsas.submit.def1.model.request.{Def1_SubmitForeignPropertyBsasRequestBody, Def1_SubmitForeignPropertyBsasRequestData}
import v5.foreignPropertyBsas.submit.model.request.SubmitForeignPropertyBsasRequestData

import scala.concurrent.Future

class SubmitForeignPropertyBsasConnectorSpec extends ConnectorSpec {

  private val nino          = Nino("AA123456A")
  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  private val parsedSubmitForeignPropertyBsasRequestBody = Def1_SubmitForeignPropertyBsasRequestBody(None, None)

  trait Test {
    _: ConnectorTest =>
    val connector: SubmitForeignPropertyBsasConnector = new SubmitForeignPropertyBsasConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)

    def requestWith(taxYear: Option[TaxYear]): SubmitForeignPropertyBsasRequestData =
      Def1_SubmitForeignPropertyBsasRequestData(nino, calculationId, taxYear, parsedSubmitForeignPropertyBsasRequestBody)

  }

  "submitBsas" must {

    "post a SubmitBsasRequest body and return the result for a request without a tax year" in new IfsTest with Test {
      private val request = requestWith(taxYear = None)
      private val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(url = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino/$calculationId", body = parsedSubmitForeignPropertyBsasRequestBody)
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitForeignPropertyBsas(request))
      result shouldBe outcome
    }

    "post a SubmitBsasRequest body and return the result for a pre-TYS tax year request" in new IfsTest with Test {
      private val request = requestWith(Some(TaxYear.fromMtd("2022-23")))
      private val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(url = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino/$calculationId", body = parsedSubmitForeignPropertyBsasRequestBody)
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitForeignPropertyBsas(request))
      result shouldBe outcome
    }

    "post a SubmitBsasRequest body and return the result for a post-TYS tax year request" in new TysIfsTest with Test {
      private val request = requestWith(Some(TaxYear.fromMtd("2023-24")))
      private val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/23-24/$nino/$calculationId",
        body = parsedSubmitForeignPropertyBsasRequestBody)
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitForeignPropertyBsas(request))
      result shouldBe outcome
    }

    "post a SubmitBsasRequest body and return the result for a post-TYS tax year request on HIP" in new HipTest with Test {
      private val request = requestWith(Some(TaxYear.fromMtd("2023-24")))
      private val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(
        url = s"$baseUrl/itsa/income-tax/v1/23-24/adjustable-summary-calculation/$nino/$calculationId",
        body = parsedSubmitForeignPropertyBsasRequestBody)
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitForeignPropertyBsas(request))
      result shouldBe outcome
    }
  }

}
