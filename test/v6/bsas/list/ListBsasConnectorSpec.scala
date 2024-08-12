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

package v6.bsas.list

import org.scalamock.handlers.CallHandler
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors.{DownstreamErrorCode, DownstreamErrors}
import shared.models.outcomes.ResponseWrapper
import v6.bsas.list.def1.model.Def1_ListBsasFixtures
import v6.bsas.list.def1.model.request.Def1_ListBsasRequestData
import v6.bsas.list.model.request.ListBsasRequestData
import v6.bsas.list.model.response.ListBsasResponse

import scala.concurrent.Future

class ListBsasConnectorSpec extends ConnectorSpec {

  private val nino             = Nino("AA123456A")
  private val incomeSourceId   = "XAIS12345678910"
  private val incomeSourceType = "02"

  private val preTysTaxYear         = TaxYear.fromMtd("2018-19")
  private val tysTaxYear            = TaxYear.fromMtd("2023-24")

  private val additionalQueryParams: Seq[(String, String)] = List(
    ("taxYear", preTysTaxYear.asDownstream)
  )

  private val commonQueryParams: Seq[(String, String)] = List(
    ("incomeSourceId", incomeSourceId),
    ("incomeSourceType", incomeSourceType)
  )

  "listBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new IfsTest with Test with Def1_ListBsasFixtures {
        def taxYear: TaxYear                             = preTysTaxYear
        def downstreamQueryParams: Seq[(String, String)] = commonQueryParams ++ additionalQueryParams

        val outcome = Right(ResponseWrapper(correlationId, listBsasResponse))
        stubHttpResponse(outcome)

        val result: DownstreamOutcome[ListBsasResponse] = await(connector.listBsas(request))
        result shouldBe outcome
      }
    }
  }

  "a valid request with Tax Year Specific tax year is supplied" in new TysIfsTest with Test with Def1_ListBsasFixtures {
    def taxYear: TaxYear                             = tysTaxYear
    def downstreamQueryParams: Seq[(String, String)] = commonQueryParams
    val outcome                                      = Right(ResponseWrapper(correlationId, listBsasResponse))

    stubTysHttpResponse(outcome)

    await(connector.listBsas(request)) shouldBe outcome
  }

  "response is an error" must {
    val downstreamErrorResponse: DownstreamErrors =
      DownstreamErrors.single(DownstreamErrorCode("SOME_ERROR"))
    val outcome = Left(ResponseWrapper(correlationId, downstreamErrorResponse))

    "return the error" in new IfsTest with Test with Def1_ListBsasFixtures {
      def taxYear: TaxYear                             = preTysTaxYear
      def downstreamQueryParams: Seq[(String, String)] = commonQueryParams ++ additionalQueryParams

      stubHttpResponse(outcome)

      val result: DownstreamOutcome[ListBsasResponse] =
        await(connector.listBsas(request))
      result shouldBe outcome
    }

    "return the error given a TYS tax year request" in new TysIfsTest with Test with Def1_ListBsasFixtures {
      def taxYear: TaxYear                             = tysTaxYear
      def downstreamQueryParams: Seq[(String, String)] = commonQueryParams

      stubTysHttpResponse(outcome)

      val result: DownstreamOutcome[ListBsasResponse] =
        await(connector.listBsas(request))
      result shouldBe outcome
    }
  }

  trait Test { _: ConnectorTest =>
    protected def taxYear: TaxYear
    protected def downstreamQueryParams: Seq[(String, String)]

    protected val request: ListBsasRequestData =
      Def1_ListBsasRequestData(nino, taxYear, Some(BusinessId(incomeSourceId)), Some(incomeSourceType))

    protected val connector: ListBsasConnector =
      new ListBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    protected def stubHttpResponse(
        outcome: DownstreamOutcome[ListBsasResponse]
    ): CallHandler[Future[DownstreamOutcome[ListBsasResponse]]]#Derived = {
      willGet(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino",
        parameters = downstreamQueryParams
      ).returns(Future.successful(outcome))
    }

    protected def stubTysHttpResponse(
        outcome: DownstreamOutcome[ListBsasResponse]
    ): CallHandler[Future[DownstreamOutcome[ListBsasResponse]]]#Derived = {
      willGet(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino",
        parameters = downstreamQueryParams
      ).returns(Future.successful(outcome))
    }

  }

}
