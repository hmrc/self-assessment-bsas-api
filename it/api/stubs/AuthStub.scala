package api.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{ OK, UNAUTHORIZED }
import play.api.libs.json.{ JsObject, Json }
import support.WireMockMethods

object AuthStub extends WireMockMethods {

  private val authoriseUri: String = "/auth/authorise"

  private val mtdEnrolment: JsObject = Json.obj(
    "key" -> "HMRC-MTD-IT",
    "identifiers" -> Json.arr(
      Json.obj(
        "key"   -> "MTDITID",
        "value" -> "1234567890"
      )
    )
  )

  def authorised(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = OK, body = successfulAuthResponse(mtdEnrolment))
  }

  def unauthorisedNotLoggedIn(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="MissingBearerToken""""))
  }

  def unauthorisedOther(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="InvalidBearerToken""""))
  }

  private def successfulAuthResponse(enrolments: JsObject*): JsObject = {
    Json.obj("authorisedEnrolments" -> enrolments, "affinityGroup" -> "Individual")
  }
}
