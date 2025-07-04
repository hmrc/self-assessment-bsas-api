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

package v6.foreignPropertyBsas.submit

import play.api.Configuration
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v6.foreignPropertyBsas.submit.def3.model.request.{Def3_SubmitForeignPropertyBsasRequestBody, Def3_SubmitForeignPropertyBsasRequestData}
import v6.foreignPropertyBsas.submit.model.request.SubmitForeignPropertyBsasRequestData
import uk.gov.hmrc.http.StringContextOps
import scala.concurrent.Future

class SubmitForeignPropertyBsasConnectorSpec extends ConnectorSpec {

  private val nino          = Nino("AA123456A")
  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  private val parsedSubmitForeignPropertyBsasRequestBody = Def3_SubmitForeignPropertyBsasRequestBody(None)

  trait Test {
    _: ConnectorTest =>
    val connector: SubmitForeignPropertyBsasConnector = new SubmitForeignPropertyBsasConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)

    def requestWith(taxYear: TaxYear): SubmitForeignPropertyBsasRequestData =
      Def3_SubmitForeignPropertyBsasRequestData(nino, calculationId, taxYear, parsedSubmitForeignPropertyBsasRequestBody)

  }

  "submitBsas" must {

    "post a SubmitBsasRequest body and return the result for a pre-TYS tax year request" in new IfsTest with Test {
      private val request = requestWith(TaxYear.fromMtd("2022-23"))
      private val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(url = url"$baseUrl/income-tax/adjustable-summary-calculation/$nino/$calculationId", body = parsedSubmitForeignPropertyBsasRequestBody)
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitForeignPropertyBsas(request))
      result shouldBe outcome
    }

    "post a SubmitBsasRequest body and return the result for a post-TYS tax year request" in new IfsTest with Test {
      MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1874.enabled" -> false))
      private val request = requestWith(TaxYear.fromMtd("2023-24"))
      private val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(
        url = url"$baseUrl/income-tax/adjustable-summary-calculation/23-24/$nino/$calculationId",
        body = parsedSubmitForeignPropertyBsasRequestBody)
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitForeignPropertyBsas(request))
      result shouldBe outcome
    }

    "post a SubmitBsasRequest body and return the result for a post-TYS tax year request on HIP" in new HipTest with Test {
      MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1874.enabled" -> true))
      private val request = requestWith(TaxYear.fromMtd("2023-24"))
      private val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(
        url = url"$baseUrl/itsa/income-tax/v1/23-24/adjustable-summary-calculation/$nino/$calculationId",
        body = parsedSubmitForeignPropertyBsasRequestBody)
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitForeignPropertyBsas(request))
      result shouldBe outcome
    }
  }

}
