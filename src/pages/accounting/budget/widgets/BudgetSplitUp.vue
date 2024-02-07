<template>
  <div class="pL30 pR30 d-flex flex-direction-column">
    <div class="d-flex flex-col mT30">
      <div class="d-flex flex-row">
        <div class="budget-header-text">BUDGET DETAILS</div>
        <div class="mL30 hide-months-blue-txt" @click="hideShowSplitUp">
          {{ canShowSplitUp ? 'Hide months' : 'Show months' }}
        </div>
      </div>
      <div class="fc-heading-border-width43 mT15"></div>
    </div>
    <div ref="container" class="budget-summary-amounts-table">
      <table
        class="mT40 table-fixed"
        :class="[canShowSplitUp ? 'width100' : 'width50']"
      >
        <thead>
          <tr>
            <th class="width220px">
              INCOME
            </th>
            <th
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
              class="width6"
            >
              {{ value }}
            </th>
            <th>
              {{ getFiscalYearTitle }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="income in getAllIncomes" :key="income.id">
            <td class="td-padding">
              <div class="d-flex flex-col">
                <div class="header-text-2">
                  {{ income.account.name }}
                </div>
                <div class="mT10">
                  Budget
                </div>
                <div class="mT5">
                  Actuals
                </div>
              </div>
            </td>
            <td
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
            >
              <div class="d-flex flex-col">
                <div class="mT30 cost-padding">
                  <div class="overflow-scroll">
                    {{
                      income.monthlyAmountSplitUp[monthIndex]
                        .monthlyAmountString || '0.00'
                    }}
                  </div>
                </div>
                <div
                  class="mT5 cost-padding"
                  :class="[
                    !hasIncomeActualsExceeded(
                      income.monthlyAmountSplitUp[monthIndex]
                    )
                      ? 'bg-td-red'
                      : 'bg-td-green',
                  ]"
                >
                  <div class="overflow-scroll">
                    {{
                      income.monthlyAmountSplitUp[monthIndex]
                        .actualMonthlyAmountString || '0.00'
                    }}
                  </div>
                </div>
              </div>
            </td>
            <td>
              <div class="d-flex flex-col">
                <div class="mT30 cost-padding">
                  <CurrencyPopOver
                    v-if="
                      checkForMultiCurrency('totalIncome', metaFieldTypeMap)
                    "
                    :field="{ displayValue: income.yearlyAmount || 0 }"
                    :details="details"
                    :showInfo="false"
                  />
                  <currency
                    v-else
                    :value="income.yearlyAmount"
                    class="overflow-scroll"
                  ></currency>
                </div>
                <div
                  class="mT5 cost-padding"
                  :class="[
                    (income.actualYearlyAmount || 0) < income.yearlyAmount
                      ? 'bg-td-red'
                      : 'bg-td-green',
                  ]"
                >
                  <CurrencyPopOver
                    v-if="
                      checkForMultiCurrency('totalIncome', metaFieldTypeMap)
                    "
                    :field="{ displayValue: income.actualYearlyAmount || 0 }"
                    :details="details"
                    :showInfo="false"
                  />
                  <currency
                    v-else
                    :value="income.actualYearlyAmount || 0"
                    class="overflow-scroll"
                  ></currency>
                </div>
              </div>
            </td>
          </tr>
          <tr>
            <td class="td-padding bold-text-2">
              TOTAL BUDGET
            </td>
            <td
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
              class="td-padding bold-text-2"
            >
              <div class="overflow-scroll">
                {{ getTotalBudgetIncome(monthIndex, 'monthlyAmount') }}
              </div>
            </td>
            <td class="td-padding bold-text-2">
              <CurrencyPopOver
                v-if="checkForMultiCurrency('totalIncome', metaFieldTypeMap)"
                :field="{ displayValue: getTotalIncomeYearlyBudgetAmount || 0 }"
                :details="details"
                :showInfo="false"
              />
              <currency
                v-else
                :value="getTotalIncomeYearlyBudgetAmount || 0"
                class="overflow-scroll"
              ></currency>
            </td>
          </tr>
          <tr>
            <td class="td-padding bold-text-2">
              TOTAL ACTUALS
            </td>
            <td
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
              class="td-padding bold-text-2"
            >
              <div class="overflow-scroll">
                {{ getTotalBudgetIncome(monthIndex, 'actualMonthlyAmount') }}
              </div>
            </td>
            <td class="td-padding bold-text-2">
              <CurrencyPopOver
                v-if="checkForMultiCurrency('totalIncome', metaFieldTypeMap)"
                :field="{ displayValue: getTotalIncomeYearlyActualAmount || 0 }"
                :details="details"
                :showInfo="false"
              />
              <currency
                v-else
                :value="getTotalIncomeYearlyActualAmount || 0"
                class="overflow-scroll"
              ></currency>
            </td>
          </tr>
        </tbody>
      </table>
      <table
        class="mT40 table-fixed"
        :class="[canShowSplitUp ? 'width100' : 'width50']"
      >
        <thead>
          <tr>
            <th class="width220px">
              EXPENSE
            </th>
            <th
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
              class="width6"
            >
              {{ value }}
            </th>
            <th>
              {{ getFiscalYearTitle }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="expense in getAllExpenses" :key="expense.id">
            <td class="td-padding">
              <div class="d-flex flex-col">
                <div class="header-text-2">
                  {{ expense.account.name }}
                </div>
                <div class="mT10">
                  Budget
                </div>
                <div class="mT5">
                  Actuals
                </div>
              </div>
            </td>
            <td
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
            >
              <div class="d-flex flex-col">
                <div class="mT30 cost-padding">
                  <div class="overflow-scroll">
                    {{
                      expense.monthlyAmountSplitUp[monthIndex]
                        .monthlyAmountString || '0.00'
                    }}
                  </div>
                </div>
                <div
                  class="mT5 cost-padding"
                  :class="[
                    hasExpenseActualsExceeded(
                      expense.monthlyAmountSplitUp[monthIndex]
                    )
                      ? 'bg-td-red'
                      : 'bg-td-green',
                  ]"
                >
                  <div class="overflow-scroll">
                    {{
                      expense.monthlyAmountSplitUp[monthIndex]
                        .actualMonthlyAmountString || '0.00'
                    }}
                  </div>
                </div>
              </div>
            </td>
            <td>
              <div class="d-flex flex-col">
                <div class="mT30 cost-padding">
                  <CurrencyPopOver
                    v-if="
                      checkForMultiCurrency('totalIncome', metaFieldTypeMap)
                    "
                    :field="{ displayValue: expense.yearlyAmount || 0 }"
                    :details="details"
                    :showInfo="false"
                  />
                  <currency
                    v-else
                    :value="expense.yearlyAmount"
                    class="overflow-scroll"
                  ></currency>
                </div>
                <div
                  class="mT5 cost-padding"
                  :class="[
                    (expense.actualYearlyAmount || 0) > expense.yearlyAmount
                      ? 'bg-td-red'
                      : 'bg-td-green',
                  ]"
                >
                  <CurrencyPopOver
                    v-if="
                      checkForMultiCurrency('totalIncome', metaFieldTypeMap)
                    "
                    :field="{ displayValue: expense.actualYearlyAmount || 0 }"
                    :details="details"
                    :showInfo="false"
                  />
                  <currency
                    v-else
                    :value="expense.actualYearlyAmount || 0"
                    class="overflow-scroll"
                  ></currency>
                </div>
              </div>
            </td>
          </tr>
          <tr>
            <td class="td-padding bold-text-2">
              TOTAL BUDGET
            </td>
            <td
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
              class="td-padding bold-text-2"
            >
              <div class="overflow-scroll">
                {{ getTotalBudgetExpense(monthIndex, 'monthlyAmount') }}
              </div>
            </td>
            <td class="td-padding bold-text-2">
              <CurrencyPopOver
                v-if="checkForMultiCurrency('totalExpense', metaFieldTypeMap)"
                :field="{
                  displayValue: getTotalExpenseYearlyBudgetAmount || 0,
                }"
                :details="details"
                :showInfo="false"
              />
              <currency
                v-else
                :value="getTotalExpenseYearlyBudgetAmount || 0"
                class="overflow-scroll"
              ></currency>
            </td>
          </tr>
          <tr>
            <td class="td-padding bold-text-2">
              TOTAL ACTUALS
            </td>
            <td
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
              class="td-padding bold-text-2"
            >
              <div class="overflow-scroll">
                {{ getTotalBudgetExpense(monthIndex, 'actualMonthlyAmount') }}
              </div>
            </td>
            <td class="td-padding bold-text-2">
              <CurrencyPopOver
                v-if="checkForMultiCurrency('totalExpense', metaFieldTypeMap)"
                :field="{
                  displayValue: getTotalExpenseYearlyActualAmount || 0,
                }"
                :details="details"
                :showInfo="false"
              />
              <currency
                v-else
                :value="getTotalExpenseYearlyActualAmount || 0"
                class="overflow-scroll"
              ></currency>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script>
import BudgetMixin from 'src/pages/accounting/budget/mixins/BudgetMixins'
export default {
  mixins: [BudgetMixin],
  data() {
    return {
      canShowSplitUp: true,
    }
  },
  mounted() {
    this.autoResize()
  },
  methods: {
    hasIncomeActualsExceeded(record) {
      return (record.actualMonthlyAmount || 0) >= record.monthlyAmount
    },
    hasExpenseActualsExceeded(record) {
      return (record.actualMonthlyAmount || 0) > record.monthlyAmount
    },
  },
}
</script>
