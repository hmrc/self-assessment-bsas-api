/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package shared.models.domain

import java.time.{Clock, ZoneOffset}

trait TaxYearTestSupport {

  def clockAtTimeInTaxYear(taxYear: TaxYear): Clock =
    Clock.fixed(taxYear.endDate.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC)

}
