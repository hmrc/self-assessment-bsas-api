package shared.models.domain

import shared.UnitSpec

import java.time.LocalDate

class DateRangeSpec extends UnitSpec {

  private val startDate = LocalDate.parse("2023-06-01")
  private val endDate   = LocalDate.parse("2024-05-07")

  private val dateRange = DateRange(startDate, endDate)

  "apply(LocalDate,LocalDate)" should {
    "return a DateRange" in {
      val result = DateRange(startDate -> endDate)
      result shouldBe dateRange
    }

    "asTaxYear" should {
      "return a TaxYear based on the date range" in {
        val result = dateRange.asTaxYearMtdString
        result shouldBe "2023-24"
      }
    }
  }

}
