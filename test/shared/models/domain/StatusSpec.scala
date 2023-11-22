package shared.models.domain

import play.api.libs.json.{JsString, JsValue, Json}
import shared.UnitSpec

class StatusSpec extends UnitSpec {

  "reading a status from json" should {
    "produce the 'valid' status type" in {
      val result: Status = JsString("valid").as[Status]
      result shouldBe Status.valid
    }

    "produce the 'invalid' status type" in {
      val result: Status = JsString("invalid").as[Status]
      result shouldBe Status.invalid
    }

    "produce the 'superseded' status type" in {
      val result: Status = JsString("superseded").as[Status]
      result shouldBe Status.superseded
    }

    "produce a json parse error" in {
      val result: Option[Status] = JsString("not-a-status").validate[Status].asOpt
      result shouldBe None
    }
  }

  "writing a status to json" should {
    "produce the expected json string" in {
      val status: Status  = Status.valid
      val result: JsValue = Json.toJson(status)
      result shouldBe JsString("valid")
    }
  }

}
