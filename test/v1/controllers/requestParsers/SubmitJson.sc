import play.api.libs.json.Json
import v1.fixtures.selfEmployment.AdditionsFixture.additionsModel
import v1.fixtures.selfEmployment.ExpensesFixture.expensesModel
import v1.fixtures.selfEmployment.IncomeFixture.incomeModel
import v1.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestBody

val submitSelfEmploymentBsasRequestBodyModel: SubmitSelfEmploymentBsasRequestBody =
  SubmitSelfEmploymentBsasRequestBody(
    income = Some(incomeModel),
    additions = Some(additionsModel),
    expenses = Some(expensesModel)
  )

Json.toJson(submitSelfEmploymentBsasRequestBodyModel)
