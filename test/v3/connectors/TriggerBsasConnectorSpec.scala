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

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{DownstreamErrorCode, DownstreamErrors}
import shared.models.outcomes.ResponseWrapper
import org.scalamock.handlers.CallHandler
import v3.fixtures.TriggerBsasRequestBodyFixtures._
import v3.models.request.triggerBsas.TriggerBsasRequestData
import v3.models.response.TriggerBsasResponse

import scala.concurrent.Future

class TriggerBsasConnectorSpec extends ConnectorSpec {

  val nino: Nino    = Nino("AA123456A")
  val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val preTysTaxYear = TaxYear.fromIso("2019-05-05")
  private val tysTaxYear    = TaxYear.fromIso("2023-05-02")

  "triggerBsas" must {

    "post a TriggerBsasRequest body and return the result" in new IfsTest with Test {
      protected def taxYear: TaxYear = preTysTaxYear

      val outcome = Right(ResponseWrapper(correlationId, TriggerBsasResponse(calculationId)))
      stubHttpResponse(outcome)

      val result: DownstreamOutcome[TriggerBsasResponse] = await(connector.triggerBsas(request))
      result shouldBe outcome
    }

    "post a TriggerBsasRequest body and return the result given a TYS tax year" in new TysIfsTest with Test {
      override protected val request: TriggerBsasRequestData = TriggerBsasRequestData(nino, tysModel)
      protected def taxYear: TaxYear                         = tysTaxYear

      val outcome = Right(ResponseWrapper(correlationId, TriggerBsasResponse(calculationId)))
      stubTysHttpResponse(outcome)

      val result: DownstreamOutcome[TriggerBsasResponse] = await(connector.triggerBsas(request))
      result shouldBe outcome
    }

  }

  "response is an error" must {
    val downstreamErrorResponse = DownstreamErrors.single(DownstreamErrorCode("SOME_ERROR"))
    val outcome                 = Left(ResponseWrapper(correlationId, downstreamErrorResponse))

    "return the error" in new IfsTest with Test {
      protected def taxYear: TaxYear = preTysTaxYear

      stubHttpResponse(outcome)

      val result: DownstreamOutcome[TriggerBsasResponse] =
        await(connector.triggerBsas(request))
      result shouldBe outcome
    }

    "return the error given a TYS tax year request" in new TysIfsTest with Test {
      override protected val request: TriggerBsasRequestData = TriggerBsasRequestData(nino, tysModel)
      protected def taxYear: TaxYear                         = tysTaxYear

      stubTysHttpResponse(outcome)

      val result: DownstreamOutcome[TriggerBsasResponse] =
        await(connector.triggerBsas(request))
      result shouldBe outcome
    }
  }

  private trait Test {
    _: ConnectorTest =>

    protected def taxYear: TaxYear
    protected val request: TriggerBsasRequestData = TriggerBsasRequestData(nino, model)
    protected val connector: TriggerBsasConnector = new TriggerBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    protected def stubHttpResponse(
        outcome: DownstreamOutcome[TriggerBsasResponse]): CallHandler[Future[DownstreamOutcome[TriggerBsasResponse]]]#Derived = {
      willPost(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino",
        body = model
      ).returns(Future.successful(outcome))
    }

    protected def stubTysHttpResponse(
        outcome: DownstreamOutcome[TriggerBsasResponse]): CallHandler[Future[DownstreamOutcome[TriggerBsasResponse]]]#Derived = {
      willPost(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino",
        body = tysModel
      ).returns(Future.successful(outcome))
    }

  }

}
