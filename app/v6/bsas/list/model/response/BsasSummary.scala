
package v6.bsas.list.model.response

import play.api.libs.json.OWrites
import shared.utils.JsonWritesUtil
import v6.bsas.list.def1.model.response.Def1_BsasSummary
import v6.bsas.list.def2.model.response.Def2_BsasSummary

trait BsasSummary {
  def calculationId: String
}

object BsasSummary extends JsonWritesUtil {
  implicit val writes: OWrites[BsasSummary] = writesFrom{
    case def1: Def1_BsasSummary => implicitly[OWrites[Def1_BsasSummary]].writes(def1)
    case def2: Def2_BsasSummary => implicitly[OWrites[Def2_BsasSummary]].writes(def2)
  }
}
