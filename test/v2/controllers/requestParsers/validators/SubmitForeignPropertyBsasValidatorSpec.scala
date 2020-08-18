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

package v2.controllers.requestParsers.validators

import play.api.libs.json.Json
import support.UnitSpec
import v2.models.errors.{BsasIdFormatError, FormatAdjustmentValueError, NinoFormatError, RuleAdjustmentRangeInvalid, RuleBothExpensesError, RuleIncorrectOrEmptyBodyError}
import v2.models.request.submitBsas.foreignProperty.SubmitForeignPropertyRawData

class SubmitForeignPropertyBsasValidatorSpec extends UnitSpec {

  private val validNino = "AA123456A"
  private val validBsasId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  private val requestBodyJson = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "travelCosts": 123.12,
      |            "costOfServices": 123.12,
      |            "residentialFinancialCost": 123.12,
      |            "other": 123.12
      |        }
      |    },
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "costOfServices": 123.12,
      |            "travelCosts": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonConsolidatedExpenses = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        },
      |        "expenses": {
      |            "residentialFinancialCost": 123.12,
      |            "consolidatedExpenses": 123.12
      |        }
      |    },
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        },
      |        "expenses": {
      |            "consolidatedExpenses": 123.12
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonNoDecimals = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123,
      |            "premiumsOfLeaseGrant": 123,
      |            "foreignTaxTakenOff": 123,
      |            "otherPropertyIncome": 123
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123,
      |            "repairsAndMaintenance": 123,
      |            "financialCosts": 123,
      |            "professionalFees": 123,
      |            "travelCosts": 123,
      |            "costOfServices": 123,
      |            "residentialFinancialCost": 123,
      |            "other": 123
      |        }
      |    },
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123,
      |            "repairsAndMaintenance": 123,
      |            "financialCosts": 123,
      |            "professionalFees": 123,
      |            "costOfServices": 123,
      |            "travelCosts": 123,
      |            "other": 123
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonNoFhlEea = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "travelCosts": 123.12,
      |            "costOfServices": 123.12,
      |            "residentialFinancialCost": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonNoForeignProperty = Json.parse(
    """
      |{
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "costOfServices": 123.12,
      |            "travelCosts": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonNoForeignPropertyIncome = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "travelCosts": 123.12,
      |            "costOfServices": 123.12,
      |            "residentialFinancialCost": 123.12,
      |            "other": 123.12
      |        }
      |    },
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "costOfServices": 123.12,
      |            "travelCosts": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonNoForeignPropertyExpenses = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        }
      |    },
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "costOfServices": 123.12,
      |            "travelCosts": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonNoFhlEeaIncome = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "travelCosts": 123.12,
      |            "costOfServices": 123.12,
      |            "residentialFinancialCost": 123.12,
      |            "other": 123.12
      |        }
      |    },
      |    "fhlEea": {
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "costOfServices": 123.12,
      |            "travelCosts": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonNoFhlEeaExpenses = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "travelCosts": 123.12,
      |            "costOfServices": 123.12,
      |            "residentialFinancialCost": 123.12,
      |            "other": 123.12
      |        }
      |    },
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonEmptyForeignProperty = Json.parse(
    """
      |{
      |    "foreignProperty": {},
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "costOfServices": 123.12,
      |            "travelCosts": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin
  )

  private val requestBodyJsonEmptyFhlEea = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "travelCosts": 123.12,
      |            "costOfServices": 123.12,
      |            "residentialFinancialCost": 123.12,
      |            "other": 123.12
      |        }
      |    },
      |    "fhlEea": {}
      |}
      |""".stripMargin
  )

  private val requestBodyJsonEmptyForeignPropertyAndFhlEea = Json.parse(
    """
      |{
      |    "foreignProperty": {},
      |    "fhlEea": {}
      |}
      |""".stripMargin
  )

  private val requestBodyJsonEmptyForeignPropertyIncome = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {},
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "travelCosts": 123.12,
      |            "costOfServices": 123.12,
      |            "residentialFinancialCost": 123.12,
      |            "other": 123.12
      |        }
      |    },
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "costOfServices": 123.12,
      |            "travelCosts": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin)

  private val requestBodyJsonEmptyForeignPropertyExpenses = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        },
      |        "expenses": {}
      |    },
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "costOfServices": 123.12,
      |            "travelCosts": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin
  )

  private val requestBodyJsonEmptyFhlEeaIncome = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "travelCosts": 123.12,
      |            "costOfServices": 123.12,
      |            "residentialFinancialCost": 123.12,
      |            "other": 123.12
      |        }
      |    },
      |    "fhlEea": {
      |        "income": {},
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "costOfServices": 123.12,
      |            "travelCosts": 123.12,
      |            "other": 123.12
      |        }
      |    }
      |}
      |""".stripMargin
  )

  private val requestBodyJsonEmptyFhlEeaExpenses = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "income": {
      |            "rentIncome": 123.12,
      |            "premiumsOfLeaseGrant": 123.12,
      |            "foreignTaxTakenOff": 123.12,
      |            "otherPropertyIncome": 123.12
      |        },
      |        "expenses": {
      |            "premisesRunningCosts": 123.12,
      |            "repairsAndMaintenance": 123.12,
      |            "financialCosts": 123.12,
      |            "professionalFees": 123.12,
      |            "travelCosts": 123.12,
      |            "costOfServices": 123.12,
      |            "residentialFinancialCost": 123.12,
      |            "other": 123.12
      |        }
      |    },
      |    "fhlEea": {
      |        "income": {
      |            "rentIncome": 123.12
      |        },
      |        "expenses": {}
      |    }
      |}
      |""".stripMargin
  )


  private val emptyJson = Json.parse(
    """
      |{}
      |""".stripMargin
  )

  val validator = new SubmitForeignPropertyBsasValidator

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJson)) shouldBe Nil
      }
      "a valid request is supplied without decimal places in the JSON" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonNoDecimals)) shouldBe Nil
      }
      "a valid request is supplied without an fhlEea object" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonNoFhlEea)) shouldBe Nil
      }
      "a valid request is supplied without a foreignProperty object" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonNoForeignProperty)) shouldBe Nil
      }
      "a valid request is supplied without a foreignProperty.income object" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonNoForeignPropertyIncome)) shouldBe Nil
      }
      "a valid request is supplied without a foreignProperty.expenses" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonNoForeignPropertyExpenses)) shouldBe Nil
      }
      "a valid request is supplied without an fhlEea.income object" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonNoFhlEeaIncome)) shouldBe Nil
      }
      "a valid request is supplied without an fhlEea.expenses object" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonNoFhlEeaExpenses)) shouldBe Nil
      }
    }

    "return a path parameter error" when {
      "the nino is invalid" in {
        validator.validate(SubmitForeignPropertyRawData("A12344A", validBsasId, requestBodyJson)) shouldBe List(NinoFormatError)
      }
      "the bsasId format is invalid" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, "Walrus", requestBodyJson)) shouldBe List(BsasIdFormatError)
      }
      "both path parameters are invalid" in {
        validator.validate(SubmitForeignPropertyRawData("A12344A", "Walrus", requestBodyJson)) shouldBe List(NinoFormatError, BsasIdFormatError)
      }
    }
    "return RuleIncorrectOrEmptyBodyError error" when {
      "an empty JSON body is submitted" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, emptyJson)) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty foreignProperty object is submitted" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonEmptyForeignProperty)) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty foreignProperty.income object is submitted" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonEmptyForeignPropertyIncome)) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty foreignProperty.expenses is submitted" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonEmptyForeignPropertyExpenses)) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty fhlEea object is submitted" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonEmptyFhlEea)) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty fhlEea.income object is submitted" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonEmptyFhlEeaIncome)) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty fhlEea.expenses object is submitted" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonEmptyFhlEeaExpenses)) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty foreignProperty and fhlEea object is submitted" in {
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, requestBodyJsonEmptyForeignPropertyAndFhlEea)) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
    }
    "return a FORMAT_ADJUSTMENT_VALUE error" when {
      "a value field has more than 2 decimal points" in {
        val badJson = Json.parse(
          """
            |{
            |    "foreignProperty": {
            |        "income": {
            |            "rentIncome": 123.456,
            |            "premiumsOfLeaseGrant": 123.12,
            |            "foreignTaxTakenOff": 123.12,
            |            "otherPropertyIncome": 123.12
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.12,
            |            "repairsAndMaintenance": 123.12,
            |            "financialCosts": 123.12,
            |            "professionalFees": 123.12,
            |            "travelCosts": 123.12,
            |            "costOfServices": 123.12,
            |            "residentialFinancialCost": 123.12,
            |            "other": 123.12
            |        }
            |    },
            |    "fhlEea": {
            |        "income": {
            |            "rentIncome": 123.12
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.12,
            |            "repairsAndMaintenance": 123.12,
            |            "financialCosts": 123.12,
            |            "professionalFees": 123.12,
            |            "costOfServices": 123.12,
            |            "travelCosts": 123.12,
            |            "other": 123.12
            |        }
            |    }
            |}
            |""".stripMargin)
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, badJson)) shouldBe List(
          FormatAdjustmentValueError.copy(paths = Some(Seq("/foreignProperty/income/rentIncome")))
        )
      }
      "multiple fields have more than 2 decimal points" in {
        val badjson = Json.parse(
          """
            |{
            |    "foreignProperty": {
            |        "income": {
            |            "rentIncome": 123.456,
            |            "premiumsOfLeaseGrant": 123.456,
            |            "foreignTaxTakenOff": 123.456,
            |            "otherPropertyIncome": 123.456
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.456,
            |            "repairsAndMaintenance": 123.456,
            |            "financialCosts": 123.456,
            |            "professionalFees": 123.456,
            |            "travelCosts": 123.456,
            |            "costOfServices": 123.456,
            |            "residentialFinancialCost": 123.456,
            |            "other": 123.456
            |        }
            |    },
            |    "fhlEea": {
            |        "income": {
            |            "rentIncome": 123.456
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.456,
            |            "repairsAndMaintenance": 123.456,
            |            "financialCosts": 123.456,
            |            "professionalFees": 123.456,
            |            "costOfServices": 123.456,
            |            "travelCosts": 123.456,
            |            "other": 123.456
            |        }
            |    }
            |}
            |""".stripMargin)
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, badjson)) shouldBe List(
          FormatAdjustmentValueError.copy(paths = Some(Seq(
            "/foreignProperty/income/rentIncome",
            "/foreignProperty/income/premiumsOfLeaseGrant",
            "/foreignProperty/income/foreignTaxTakenOff",
            "/foreignProperty/income/otherPropertyIncome",
            "/foreignProperty/expenses/premisesRunningCosts",
            "/foreignProperty/expenses/repairsAndMaintenance",
            "/foreignProperty/expenses/financialCosts",
            "/foreignProperty/expenses/professionalFees",
            "/foreignProperty/expenses/travelCosts",
            "/foreignProperty/expenses/costOfServices",
            "/foreignProperty/expenses/residentialFinancialCost",
            "/foreignProperty/expenses/other",
            "/fhlEea/income/rentIncome",
            "/fhlEea/expenses/premisesRunningCosts",
            "/fhlEea/expenses/repairsAndMaintenance",
            "/fhlEea/expenses/financialCosts",
            "/fhlEea/expenses/professionalFees",
            "/fhlEea/expenses/costOfServices",
            "/fhlEea/expenses/travelCosts",
            "/fhlEea/expenses/other"
          )))
        )
      }
    }
    "return a RULE_RANGE_INVALID error" when {
      "a value field is above 99999999999.99" in {
        val badJson = Json.parse(
          """
            |{
            |    "foreignProperty": {
            |        "income": {
            |            "rentIncome": 100000000000.99,
            |            "premiumsOfLeaseGrant": 123.12,
            |            "foreignTaxTakenOff": 123.12,
            |            "otherPropertyIncome": 123.12
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.12,
            |            "repairsAndMaintenance": 123.12,
            |            "financialCosts": 123.12,
            |            "professionalFees": 123.12,
            |            "travelCosts": 123.12,
            |            "costOfServices": 123.12,
            |            "residentialFinancialCost": 123.12,
            |            "other": 123.12
            |        }
            |    },
            |    "fhlEea": {
            |        "income": {
            |            "rentIncome": 123.12
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.12,
            |            "repairsAndMaintenance": 123.12,
            |            "financialCosts": 123.12,
            |            "professionalFees": 123.12,
            |            "costOfServices": 123.12,
            |            "travelCosts": 123.12,
            |            "other": 123.12
            |        }
            |    }
            |}
            |""".stripMargin)
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, badJson)) shouldBe List(
          RuleAdjustmentRangeInvalid.copy(paths = Some(Seq("/foreignProperty/income/rentIncome")))
        )
      }
      "multiple fields are below 0" in {
        val badjson = Json.parse(
          """
            |{
            |    "foreignProperty": {
            |        "income": {
            |            "rentIncome": 100000000000.99,
            |            "premiumsOfLeaseGrant": 100000000000.99,
            |            "foreignTaxTakenOff": 100000000000.99,
            |            "otherPropertyIncome": 100000000000.99
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 100000000000.99,
            |            "repairsAndMaintenance": 100000000000.99,
            |            "financialCosts": 100000000000.99,
            |            "professionalFees": 100000000000.99,
            |            "travelCosts": 100000000000.99,
            |            "costOfServices": 100000000000.99,
            |            "residentialFinancialCost": 100000000000.99,
            |            "other": 100000000000.99
            |        }
            |    },
            |    "fhlEea": {
            |        "income": {
            |            "rentIncome": 100000000000.99
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 100000000000.99,
            |            "repairsAndMaintenance": 100000000000.99,
            |            "financialCosts": 100000000000.99,
            |            "professionalFees": 100000000000.99,
            |            "costOfServices": 100000000000.99,
            |            "travelCosts": 100000000000.99,
            |            "other": 100000000000.99
            |        }
            |    }
            |}
            |""".stripMargin)
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, badjson)) shouldBe List(
          RuleAdjustmentRangeInvalid.copy(paths = Some(Seq(
            "/foreignProperty/income/rentIncome",
            "/foreignProperty/income/premiumsOfLeaseGrant",
            "/foreignProperty/income/foreignTaxTakenOff",
            "/foreignProperty/income/otherPropertyIncome",
            "/foreignProperty/expenses/premisesRunningCosts",
            "/foreignProperty/expenses/repairsAndMaintenance",
            "/foreignProperty/expenses/financialCosts",
            "/foreignProperty/expenses/professionalFees",
            "/foreignProperty/expenses/travelCosts",
            "/foreignProperty/expenses/costOfServices",
            "/foreignProperty/expenses/residentialFinancialCost",
            "/foreignProperty/expenses/other",
            "/fhlEea/income/rentIncome",
            "/fhlEea/expenses/premisesRunningCosts",
            "/fhlEea/expenses/repairsAndMaintenance",
            "/fhlEea/expenses/financialCosts",
            "/fhlEea/expenses/professionalFees",
            "/fhlEea/expenses/costOfServices",
            "/fhlEea/expenses/travelCosts",
            "/fhlEea/expenses/other"
          )))
        )
      }
    }
    "return a RULE_BOTH_EXPENSES_SUPPLIED error" when {
      "both expenses and consolidated expenses are supplied" in {
        val badJson = Json.parse(
          """
            |{
            |    "foreignProperty": {
            |        "income": {
            |            "rentIncome": 123.12,
            |            "premiumsOfLeaseGrant": 123.12,
            |            "foreignTaxTakenOff": 123.12,
            |            "otherPropertyIncome": 123.12
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.12,
            |            "repairsAndMaintenance": 123.12,
            |            "financialCosts": 123.12,
            |            "professionalFees": 123.12,
            |            "travelCosts": 123.12,
            |            "costOfServices": 123.12,
            |            "residentialFinancialCost": 123.12,
            |            "other": 123.12,
            |            "consolidatedExpenses": 123.12
            |        }
            |    },
            |    "fhlEea": {
            |        "income": {
            |            "rentIncome": 123.12
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.12,
            |            "repairsAndMaintenance": 123.12,
            |            "financialCosts": 123.12,
            |            "professionalFees": 123.12,
            |            "costOfServices": 123.12,
            |            "travelCosts": 123.12,
            |            "other": 123.12
            |        }
            |    }
            |}
            |""".stripMargin)
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, badJson)) shouldBe List(RuleBothExpensesError)
      }
      "both objects have expenses and consolidated expenses" in {
        val badjson = Json.parse(
          """
            |{
            |    "foreignProperty": {
            |        "income": {
            |            "rentIncome": 123.12,
            |            "premiumsOfLeaseGrant": 123.12,
            |            "foreignTaxTakenOff": 123.12,
            |            "otherPropertyIncome": 123.12
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.12,
            |            "repairsAndMaintenance": 123.12,
            |            "financialCosts": 123.12,
            |            "professionalFees": 123.12,
            |            "travelCosts": 123.12,
            |            "costOfServices": 123.12,
            |            "residentialFinancialCost": 123.12,
            |            "other": 123.12,
            |            "consolidatedExpenses": 123.12
            |        }
            |    },
            |    "fhlEea": {
            |        "income": {
            |            "rentIncome": 123.12
            |        },
            |        "expenses": {
            |            "premisesRunningCosts": 123.12,
            |            "repairsAndMaintenance": 123.12,
            |            "financialCosts": 123.12,
            |            "professionalFees": 123.12,
            |            "costOfServices": 123.12,
            |            "travelCosts": 123.12,
            |            "other": 123.12,
            |            "consolidatedExpenses": 123.12
            |        }
            |    }
            |}
            |""".stripMargin)
        validator.validate(SubmitForeignPropertyRawData(validNino, validBsasId, badjson)) shouldBe List(RuleBothExpensesError)
      }
    }
  }
}

