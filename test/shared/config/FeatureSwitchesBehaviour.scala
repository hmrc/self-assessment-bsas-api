/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package shared.config

import play.api.Configuration
import shared.UnitSpec

trait FeatureSwitchesBehaviour[FS <: FeatureSwitches] {
  _: UnitSpec =>

  def featureSwitches(configuration: Configuration): FS

  def aFeatureSwitchWithKey(key: String, evaluateSwitch: FS => Boolean): Unit =
    s"a feature switch with key $key" should {
      def config(value: Boolean) = Configuration(key -> value)

      "be true" when {
        "absent from the config" in {
          val fs = featureSwitches(Configuration.empty)
          evaluateSwitch(fs) shouldBe true
        }

        "enabled" in {
          val fs = featureSwitches(config(true))
          evaluateSwitch(fs) shouldBe true
        }
      }

      "be false" when {
        "disabled" in {
          val fs = featureSwitches(config(false))
          evaluateSwitch(fs) shouldBe false
        }
      }
    }

}
