<template>
  <div class="pL30 pR30 d-flex flex-direction-column">
    <div class="d-flex flex-col mT30">
      <div class="d-flex flex-row">
        <div class="budget-header-text">NET INCOME</div>
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
              TOTAL
            </th>
            <th
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
              class="width6 "
            >
              {{ value }}
            </th>
            <th>
              {{ getFiscalYearTitle }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="td-padding">
              <div class="d-flex flex-col">
                <div class="header-text-2">
                  Income
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
                    {{ getTotalBudgetIncome(monthIndex, 'monthlyAmount') }}
                  </div>
                </div>
                <div
                  class="mT5 bg-td-green cost-padding"
                  :class="[
                    Number(
                      getTotalBudgetIncome(monthIndex, 'actualMonthlyAmount') ||
                        0
                    ) <
                    Number(
                      getTotalBudgetIncome(monthIndex, 'monthlyAmount') || 0
                    )
                      ? 'bg-td-red'
                      : 'bg-td-green',
                  ]"
                >
                  <div class="overflow-scroll">
                    {{
                      getTotalBudgetIncome(monthIndex, 'actualMonthlyAmount')
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
                    :field="{
                      displayValue: getTotalIncomeYearlyBudgetAmount || 0,
                    }"
                    :details="details"
                    :showInfo="false"
                  />
                  <currency
                    v-else
                    :value="getTotalIncomeYearlyBudgetAmount || 0"
                    class="overflow-scroll"
                  ></currency>
                </div>
                <div
                  class="mT5 cost-padding"
                  :class="[
                    Number(getTotalIncomeYearlyActualAmount || 0) <
                    Number(getTotalIncomeYearlyBudgetAmount || 0)
                      ? 'bg-td-red'
                      : 'bg-td-green',
                  ]"
                >
                  <CurrencyPopOver
                    v-if="
                      checkForMultiCurrency('totalIncome', metaFieldTypeMap)
                    "
                    :field="{
                      displayValue: getTotalIncomeYearlyActualAmount || 0,
                    }"
                    :details="details"
                    :showInfo="false"
                  />
                  <currency
                    v-else
                    :value="getTotalIncomeYearlyActualAmount || 0"
                    class="overflow-scroll"
                  ></currency>
                </div>
              </div>
            </td>
          </tr>
          <tr>
            <td class="td-padding">
              <div class="d-flex flex-col">
                <div class="header-text-2">
                  Expense
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
                    {{ getTotalBudgetExpense(monthIndex, 'monthlyAmount') }}
                  </div>
                </div>
                <div
                  class="mT5 cost-padding"
                  :class="[
                    Number(
                      getTotalBudgetExpense(
                        monthIndex,
                        'actualMonthlyAmount'
                      ) || 0
                    ) >
                    Number(
                      getTotalBudgetExpense(monthIndex, 'monthlyAmount') || 0
                    )
                      ? 'bg-td-red'
                      : 'bg-td-green',
                  ]"
                >
                  <div class="overflow-scroll">
                    {{
                      getTotalBudgetExpense(monthIndex, 'actualMonthlyAmount')
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
                      checkForMultiCurrency('totalExpense', metaFieldTypeMap)
                    "
                    :field="{
                      displayValue: getTotalExpenseYearlyBudgetAmount || 0,
                    }"
                    :details="details"
                    :showInfo="false"
                  />
                  <currency
                    v-else
                    class="overflow-scroll"
                    :value="getTotalExpenseYearlyBudgetAmount || 0"
                  ></currency>
                </div>
                <div
                  class="mT5 cost-padding"
                  :class="[
                    Number(getTotalExpenseYearlyActualAmount || 0) >
                    Number(getTotalExpenseYearlyBudgetAmount || 0)
                      ? 'bg-td-red'
                      : 'bg-td-green',
                  ]"
                >
                  <CurrencyPopOver
                    v-if="
                      checkForMultiCurrency('totalExpense', metaFieldTypeMap)
                    "
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
                </div>
              </div>
            </td>
          </tr>
          <tr>
            <td class="td-padding bold-text-2">
              NET BUDGET INCOME
            </td>
            <td
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
              class="td-padding  bold-text-2"
            >
              <div class="overflow-scroll">
                {{ getNetMonthlyBudgetIncome(monthIndex) }}
              </div>
            </td>
            <td class="td-padding bold-text-2 ">
              <CurrencyPopOver
                v-if="checkForMultiCurrency('totalIncome', metaFieldTypeMap)"
                :field="{
                  displayValue: getNetBudgetIncome || 0,
                }"
                :details="details"
                :showInfo="false"
              />
              <currency
                v-else
                :value="getNetBudgetIncome || 0"
                class="overflow-scroll"
              ></currency>
            </td>
          </tr>
          <tr>
            <td class="td-padding bold-text-2">
              NET ACTUAL INCOME
            </td>
            <td
              v-for="(value, monthIndex) in monthsDisplayName"
              :key="monthIndex"
              class="td-padding  bold-text-2"
            >
              <div class="overflow-scroll">
                {{ getNetMonthlyActualIncome(monthIndex) }}
              </div>
            </td>
            <td class="td-padding bold-text-2 ">
              <CurrencyPopOver
                v-if="checkForMultiCurrency('totalIncome', metaFieldTypeMap)"
                :field="{
                  displayValue: getNetActualIncome || 0,
                }"
                :details="details"
                :showInfo="false"
              />
              <currency
                v-else
                :value="getNetActualIncome || 0"
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
}
</script>
