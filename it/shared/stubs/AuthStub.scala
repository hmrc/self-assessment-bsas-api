package shared.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.{JsObject, Json}
import support.WireMockMethods

object AuthStub extends WireMockMethods {

  private val authoriseUri: String = "/auth/authorise"

  private val mtdEnrolment =  List(Json.obj(
    "key" -> "HMRC-MTD-IT",
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "MTDITID",
        "value" -> "1234567890"
      )
    )
  ))

  private val secondaryAgentEnrolments = List(
    Json.obj("key" -> "HMRC-AS-AGENT",
      "identifiers" -> Json.arr(
        Json.obj(
          "key" -> "AgentReferenceNumber",
          "value" -> "123567890"
        )
      )
    ))

  def authorised(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = OK, body = successfulAuthResponse(mtdEnrolment))
  }

  def authorisedAsSecondaryAgent(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = OK, body = successfulAuthResponse(secondaryAgentEnrolments, "Agent"))
  }

  private def successfulAuthResponse(enrolments: Seq[JsObject], affinityGroup : String = "Individual" ): JsObject = {
    Json.obj("authorisedEnrolments" -> enrolments, "affinityGroup" -> affinityGroup)
  }

  def unauthorisedNotLoggedIn(): StubMapping = {
    // Note that MissingBearerToken extends NoActiveSession
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="MissingBearerToken""""))
  }

  def unauthorisedOther(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="InvalidBearerToken""""))
  }

  def unauthorisedAsSecondaryAgent(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="FailedRelationship""""))
  }
}
