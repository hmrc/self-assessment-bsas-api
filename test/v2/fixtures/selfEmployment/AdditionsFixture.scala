/*
 * Copyright 2020 HM Revenue & Customs
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

package v2.fixtures.selfEmployment

import play.api.libs.json.{JsValue, Json}
import v2.models.request.submitBsas.selfEmployment.{Additions, queryMap}

object AdditionsFixture {

  val additionsModel: Additions =
    Additions(
      costOfGoodsBoughtDisallowable = Some(3000.1),
      cisPaymentsToSubcontractorsDisallowable = Some(3000.2),
      staffCostsDisallowable = Some(3000.3),
      travelCostsDisallowable = Some(3000.4),
      premisesRunningCostsDisallowable = Some(3000.5),
      maintenanceCostsDisallowable = Some(-3000.1),
      adminCostsDisallowable = Some(-3000.2),
      advertisingCostsDisallowable = Some(-3000.3),
      businessEntertainmentCostsDisallowable = Some(-3000.4),
      interestDisallowable = Some(-3000.5),
      financialChargesDisallowable = Some(3000.6),
      badDebtDisallowable = Some(-3000.6),
      professionalFeesDisallowable = Some(3000.7),
      depreciationDisallowable = Some(-3000.7),
      otherDisallowable = Some(3000.8)
    )

  def additionsToDesJson(model: Additions): JsValue = {
    import model._

    val desFields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoodsDisallowable" -> costOfGoodsBoughtDisallowable,
        "paymentsToSubcontractorsDisallowable" -> cisPaymentsToSubcontractorsDisallowable,
        "wagesAndStaffCostsDisallowable" -> staffCostsDisallowable,
        "carVanTravelExpensesDisallowable" -> travelCostsDisallowable,
        "premisesRunningCostsDisallowable" -> premisesRunningCostsDisallowable,
        "maintenanceCostsDisallowable" -> maintenanceCostsDisallowable,
        "adminCostsDisallowable" -> adminCostsDisallowable,
        "advertisingCostsDisallowable" -> advertisingCostsDisallowable,
        "businessEntertainmentCostsDisallowable" -> businessEntertainmentCostsDisallowable,
        "interestOnBankOtherLoansDisallowable" -> interestDisallowable,
        "financeChargesDisallowable" -> financialChargesDisallowable,
        "irrecoverableDebtsDisallowable" -> badDebtDisallowable,
        "professionalFeesDisallowable" -> professionalFeesDisallowable,
        "depreciationDisallowable" -> depreciationDisallowable,
        "otherExpensesDisallowable" -> otherDisallowable
      )

    Json.toJsObject(queryMap(desFields))
  }

  def additionsFromVendorJson(model: Additions): JsValue = {
    import model._

    val vendorSuppliedFields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoodsBoughtDisallowable" -> costOfGoodsBoughtDisallowable,
        "cisPaymentsToSubcontractorsDisallowable" -> cisPaymentsToSubcontractorsDisallowable,
        "staffCostsDisallowable" -> staffCostsDisallowable,
        "travelCostsDisallowable" -> travelCostsDisallowable,
        "premisesRunningCostsDisallowable" -> premisesRunningCostsDisallowable,
        "maintenanceCostsDisallowable" -> maintenanceCostsDisallowable,
        "adminCostsDisallowable" -> adminCostsDisallowable,
        "advertisingCostsDisallowable" -> advertisingCostsDisallowable,
        "businessEntertainmentCostsDisallowable" -> businessEntertainmentCostsDisallowable,
        "interestDisallowable" -> interestDisallowable,
        "financialChargesDisallowable" -> financialChargesDisallowable,
        "badDebtDisallowable" -> badDebtDisallowable,
        "professionalFeesDisallowable" -> professionalFeesDisallowable,
        "depreciationDisallowable" -> depreciationDisallowable,
        "otherDisallowable" -> otherDisallowable
      )

    Json.toJsObject(queryMap(vendorSuppliedFields))
  }
}
