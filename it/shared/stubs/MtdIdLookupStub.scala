package shared.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import support.WireMockMethods

object MtdIdLookupStub extends WireMockMethods {

  def ninoFound(nino: String): StubMapping = {
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status = OK, body = Json.obj("mtdbsa" -> "12345678"))
  }

  def unauthorised(nino: String): StubMapping = {
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status = FORBIDDEN, body = Json.obj())
  }

  def badRequest(nino: String): StubMapping = {
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status = BAD_REQUEST, body = Json.obj())
  }

  private def lookupUrl(nino: String): String = s"/mtd-identifier-lookup/nino/$nino"

  def internalServerError(nino: String): StubMapping = {
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status = INTERNAL_SERVER_ERROR, body = Json.obj())
  }

}
