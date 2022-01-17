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

package v3.support

import java.time.LocalDate

import support.UnitSpec
import utils.Logging
import v2.controllers.EndpointLogContext
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.response.retrieveBsas.AccountingPeriod
import v2.models.response.{retrieveBsas, retrieveBsasAdjustments}

class DesResponseMappingSupportSpec extends UnitSpec {

  implicit val logContext: EndpointLogContext         = EndpointLogContext("ctrl", "ep")
  val mapping: DesResponseMappingSupport with Logging = new DesResponseMappingSupport with Logging {}

  val correlationId = "someCorrelationId"

  object Error1 extends MtdError("msg", "code1")

  object Error2 extends MtdError("msg", "code2")

  object ErrorBvrMain extends MtdError("msg", "bvrMain")

  object ErrorBvr extends MtdError("msg", "bvr")

  val errorCodeMap: PartialFunction[String, MtdError] = {
    case "ERR1" => Error1
    case "ERR2" => Error2
    case "DS"   => DownstreamError
  }

  lazy val date: LocalDate = LocalDate.now()

  "mapping Des errors" when {
    "single error" when {
      "the error code is in the map provided" must {
        "use the mapping and wrap" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode("ERR1")))) shouldBe
            ErrorWrapper(correlationId, Error1)
        }
      }

      "the error code is not in the map provided" must {
        "default to DownstreamError and wrap" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode("UNKNOWN")))) shouldBe
            ErrorWrapper(correlationId, DownstreamError)
        }
      }
    }

    "multiple errors" when {
      "the error codes is in the map provided" must {
        "use the mapping and wrap with main error type of BadRequest" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors(List(DesErrorCode("ERR1"), DesErrorCode("ERR2"))))) shouldBe
            ErrorWrapper(correlationId, BadRequestError, Some(Seq(Error1, Error2)))
        }
      }

      "the error code is not in the map provided" must {
        "default main error to DownstreamError ignore other errors" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors(List(DesErrorCode("ERR1"), DesErrorCode("UNKNOWN"))))) shouldBe
            ErrorWrapper(correlationId, DownstreamError)
        }
      }

      "one of the mapped errors is DownstreamError" must {
        "wrap the errors with main error type of DownstreamError" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors(List(DesErrorCode("ERR1"), DesErrorCode("DS"))))) shouldBe
            ErrorWrapper(correlationId, DownstreamError)
        }
      }
    }

    "the error code is an OutboundError" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, OutboundError(ErrorBvrMain))) shouldBe
          ErrorWrapper(correlationId, ErrorBvrMain)
      }
    }

    "the error code is an OutboundError with multiple errors" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, OutboundError(ErrorBvrMain, Some(Seq(ErrorBvr))))) shouldBe
          ErrorWrapper(correlationId, ErrorBvrMain, Some(Seq(ErrorBvr)))
      }
    }
  }

  "validateRetrieveUkPropertyAdjustmentsSuccessResponse" should {
    def generateResponseWrapper(
        typeOfBusiness: TypeOfBusiness): ResponseWrapper[retrieveBsasAdjustments.ukProperty.RetrieveUkPropertyAdjustmentsResponse] =
      ResponseWrapper(
        correlationId = "",
        responseData = retrieveBsasAdjustments.ukProperty.RetrieveUkPropertyAdjustmentsResponse(
          retrieveBsasAdjustments.ukProperty.Metadata(typeOfBusiness, Some("XAIS00000000210"), AccountingPeriod(date, date), "", "", "", "", adjustedSummary = true),
          retrieveBsasAdjustments.ukProperty.BsasDetail(None, None)
        )
      )
    "return Left" when {
      List(TypeOfBusiness.`self-employment`, TypeOfBusiness.`foreign-property`, TypeOfBusiness.`foreign-property-fhl-eea`).foreach { typeOfBusiness =>
        s"provided a model with $typeOfBusiness" in {
          val input = generateResponseWrapper(typeOfBusiness)
          mapping.validateRetrieveUkPropertyAdjustmentsSuccessResponse(input) shouldBe {
            Left(ErrorWrapper("", RuleNotUkProperty, None))
          }
        }
      }
    }
    "return Right" when {
      List(TypeOfBusiness.`uk-property-fhl`, TypeOfBusiness.`uk-property-non-fhl`).foreach { typeOfBusiness =>
        s"provided a model with $typeOfBusiness" in {
          val input = generateResponseWrapper(typeOfBusiness)
          mapping.validateRetrieveUkPropertyAdjustmentsSuccessResponse(input) shouldBe {
            Right(input)
          }
        }
      }
    }
  }

  "validateRetrieveSelfEmploymentAdjustmentsSuccessResponse" should {
    def generateResponseWrapper(
        typeOfBusiness: TypeOfBusiness): ResponseWrapper[retrieveBsasAdjustments.selfEmployment.RetrieveSelfEmploymentAdjustmentsResponse] =
      ResponseWrapper(
        correlationId = "",
        responseData = retrieveBsasAdjustments.selfEmployment.RetrieveSelfEmploymentAdjustmentsResponse(
          retrieveBsasAdjustments.selfEmployment.Metadata(typeOfBusiness, None, AccountingPeriod(date, date), "", "", "", "", adjustedSummary = true),
          retrieveBsasAdjustments.selfEmployment.BsasDetail(None, None, None)
        )
      )
    "return Left" when {
      List(
        TypeOfBusiness.`uk-property-fhl`,
        TypeOfBusiness.`uk-property-non-fhl`,
        TypeOfBusiness.`foreign-property`,
        TypeOfBusiness.`foreign-property-fhl-eea`
      ).foreach { typeOfBusiness =>
        s"provided a model with $typeOfBusiness" in {
          val input = generateResponseWrapper(typeOfBusiness)
          mapping.validateRetrieveSelfEmploymentAdjustmentsSuccessResponse(input) shouldBe {
            Left(ErrorWrapper("", RuleNotSelfEmployment, None))
          }
        }
      }
    }
    "return Right" when {
      List(TypeOfBusiness.`self-employment`).foreach { typeOfBusiness =>
        s"provided a model with $typeOfBusiness" in {
          val input = generateResponseWrapper(typeOfBusiness)
          mapping.validateRetrieveSelfEmploymentAdjustmentsSuccessResponse(input) shouldBe {
            Right(input)
          }
        }
      }
    }
  }

  "validateRetrieveSelfEmploymentBsasSuccessResponse" should {
    def generateResponseWrapper(typeOfBusiness: TypeOfBusiness): ResponseWrapper[retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasResponse] =
      ResponseWrapper(
        correlationId = "",
        responseData = retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasResponse(
          retrieveBsas.selfEmployment.Metadata(typeOfBusiness, None, AccountingPeriod(date, date), "", "", "", "", adjustedSummary = true),
          None
        )
      )
    "return Left" when {
      List(
        TypeOfBusiness.`uk-property-fhl`,
        TypeOfBusiness.`uk-property-non-fhl`,
        TypeOfBusiness.`foreign-property`,
        TypeOfBusiness.`foreign-property-fhl-eea`
      ).foreach { typeOfBusiness =>
        s"provided a model with $typeOfBusiness" in {
          val input = generateResponseWrapper(typeOfBusiness)
          mapping.validateRetrieveSelfEmploymentBsasSuccessResponse(input) shouldBe {
            Left(ErrorWrapper("", RuleNotSelfEmployment, None))
          }
        }
      }
    }
    "return Right" when {
      List(TypeOfBusiness.`self-employment`).foreach { typeOfBusiness =>
        s"provided a model with $typeOfBusiness" in {
          val input = generateResponseWrapper(typeOfBusiness)
          mapping.validateRetrieveSelfEmploymentBsasSuccessResponse(input) shouldBe {
            Right(input)
          }
        }
      }
    }
  }


  "validateRetrieveUkPropertyBsasSuccessResponse" should {
    def generateResponseWrapper(typeOfBusiness: TypeOfBusiness): ResponseWrapper[retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse] =
      ResponseWrapper(
        correlationId = "",
        responseData = retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse(
          retrieveBsas.ukProperty.Metadata(
            typeOfBusiness,
            None,
            AccountingPeriod(date, date),
            "",
            "",
            "",
            "",
            adjustedSummary = true
          ),
          None
        )
      )
    "return Left" when {
      List(
        TypeOfBusiness.`self-employment`,
        TypeOfBusiness.`foreign-property`,
        TypeOfBusiness.`foreign-property-fhl-eea`
      ).foreach { typeOfBusiness =>
        s"provided a model with $typeOfBusiness" in {
          val input = generateResponseWrapper(typeOfBusiness)
          mapping.validateRetrieveUkPropertyBsasSuccessResponse(input) shouldBe {
            Left(ErrorWrapper("", RuleNotUkProperty, None))
          }
        }
      }
    }
    "return Right" when {
      List(
        TypeOfBusiness.`uk-property-fhl`,
        TypeOfBusiness.`uk-property-non-fhl`
      ).foreach { typeOfBusiness =>
        s"provided a model with $typeOfBusiness" in {
          val input = generateResponseWrapper(typeOfBusiness)
          mapping.validateRetrieveUkPropertyBsasSuccessResponse(input) shouldBe {
            Right(input)
          }
        }
      }
    }
  }


}
