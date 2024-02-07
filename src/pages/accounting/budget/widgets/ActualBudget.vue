<template>
  <div class="d-flex flex-col total-budget">
    <div class="section-1">
      <div class="heading-black18">
        Actual Amount
      </div>
      <div class="amount-text1 mT5">
        <CurrencyPopOver
          v-if="checkForMultiCurrency('totalIncome', metaFieldTypeMap)"
          :field="{ displayValue: getActualAmount || 0 }"
          :details="details"
          :showInfo="true"
          :hideInfoBtn="true"
        />
        <currency v-else :value="getActualAmount || 0"></currency>
      </div>
    </div>
    <div class="section-2 d-flex flex-col">
      <div class="flex-middle flex-row">
        <div class="heading-text1 width100px">Income</div>
        <div class="amount-text2">
          <CurrencyPopOver
            v-if="checkForMultiCurrency('totalIncome', metaFieldTypeMap)"
            :field="{ displayValue: getTotalIncomeYearlyActualAmount || 0 }"
            :details="details"
            :showInfo="true"
          />
          <currency
            v-else
            :value="getTotalIncomeYearlyActualAmount || 0"
          ></currency>
        </div>
      </div>
      <div class="flex-middle flex-row mT20">
        <div class="heading-text1 width100px">Expense</div>
        <div class="amount-text2">
          <CurrencyPopOver
            v-if="checkForMultiCurrency('totalExpense', metaFieldTypeMap)"
            :field="{ displayValue: getTotalExpenseYearlyActualAmount || 0 }"
            :details="details"
            :showInfo="true"
          />
          <currency
            v-else
            :value="getTotalExpenseYearlyActualAmount || 0"
          ></currency>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import BudgetMixins from 'src/pages/accounting/budget/mixins/BudgetMixins'
export default {
  mixins: [BudgetMixins],
}
</script>
