/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package config

import play.api.Configuration
import shared.UnitSpec
import shared.config.FeatureSwitchesBehaviour

class BsasFeatureSwitchesSpec extends UnitSpec with FeatureSwitchesBehaviour[BsasFeatureSwitches] {
  override def featureSwitches(configuration: Configuration): BsasFeatureSwitches = BsasFeatureSwitches(configuration)

  "isIfsEnabled" should {
    behave like aFeatureSwitchWithKey("ifs.enabled", _.isIfsEnabled)
  }

  "isIfsInProduction" should {
    behave like aFeatureSwitchWithKey("ifs.released-in-production", _.isIfsInProduction)
  }

}
