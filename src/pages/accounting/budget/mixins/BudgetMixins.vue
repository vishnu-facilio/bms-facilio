<script>
import { isEmpty, isArray } from '@facilio/utils/validation'
import {
  checkForMultiCurrency,
  getCurrencyInDecimalValue,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'

export default {
  props: ['calculateDimensions', 'resizeWidget', 'details'],
  components: { CurrencyPopOver },
  data() {
    return {
      metaFieldTypeMap: {},
      checkForMultiCurrency,
    }
  },
  computed: {
    monthsDisplayName() {
      if (this.canShowSplitUp) {
        let { details } = this
        if (!isEmpty(details.fiscalYear)) {
          let month = Number(details.fiscalYear)
          return [
            ...this.$constants.budgetMonthsDisplayName.slice(month - 1, 12),
            ...this.$constants.budgetMonthsDisplayName.slice(0, month - 1),
          ]
        }
        return this.$constants.budgetMonthsDisplayName
      }
      return []
    },
    getFiscalYearTitle() {
      let { details } = this
      return `FY${
        !isEmpty(details.fiscalYearStart)
          ? 2018 + Number(details.fiscalYearStart)
          : ''
      }`
    },
    getAllIncomes() {
      let { details } = this
      let list = []
      if (
        !isEmpty(details.budgetAmountList) &&
        isArray(details.budgetAmountList)
      ) {
        details.budgetAmountList.forEach(element => {
          if (element.amountType === 1) {
            list.push(element)
          }
        })
      }
      return list
    },
    getAllExpenses() {
      let { details } = this
      let list = []
      if (
        !isEmpty(details.budgetAmountList) &&
        isArray(details.budgetAmountList)
      ) {
        details.budgetAmountList.forEach(element => {
          if (element.amountType === 2) {
            list.push(element)
          }
        })
      }
      return list
    },
    getTotalIncomeYearlyBudgetAmount() {
      let total = 0
      this.getAllIncomes.forEach(record => {
        total += Number(record.yearlyAmount || 0)
      })
      return total
    },
    getTotalIncomeYearlyActualAmount() {
      let total = 0
      this.getAllIncomes.forEach(record => {
        total += Number(record.actualYearlyAmount || 0)
      })
      return total
    },
    getNetBudgetIncome() {
      return (
        Number(this.getTotalIncomeYearlyBudgetAmount) -
        Number(this.getTotalExpenseYearlyBudgetAmount)
      )
    },
    getNetActualIncome() {
      return (
        Number(this.getTotalIncomeYearlyActualAmount) -
        Number(this.getTotalExpenseYearlyActualAmount)
      )
    },
    getTotalExpenseYearlyBudgetAmount() {
      let total = 0
      this.getAllExpenses.forEach(record => {
        total += Number(record.yearlyAmount || 0)
      })
      return total
    },
    getTotalExpenseYearlyActualAmount() {
      let total = 0
      this.getAllExpenses.forEach(record => {
        total += Number(record.actualYearlyAmount || 0)
      })
      return total
    },
    getActualAmount() {
      return (
        (this.getTotalIncomeYearlyActualAmount || 0) -
        (this.getTotalExpenseYearlyActualAmount || 0)
      )
    },
    getTotalBudgetAmount() {
      return (this.details.totalIncome || 0) - (this.details.totalExpense || 0)
    },
    getRemainingAmount() {
      return (this.details.totalNetIncome || 0) - (this.getActualAmount || 0)
    },
    getRemainingIncomeAmount() {
      return (
        Number(this.details.totalIncome || 0) -
        Number(this.getTotalIncomeYearlyActualAmount)
      )
    },
    getRemainingExpenseAmount() {
      return (
        Number(this.details.totalExpense || 0) -
        Number(this.getTotalExpenseYearlyActualAmount)
      )
    },
    percentageOfAmountUsed() {
      return (
        (this.getActualAmount / this.details.totalNetIncome || 0) * 100 || 0
      )
    },
  },
  async created() {
    let { metaFieldTypeMap } = this.$attrs || {}
    this.metaFieldTypeMap = metaFieldTypeMap
  },
  methods: {
    roundOffCurrencyvalue(val) {
      return getCurrencyInDecimalValue(val, { decimalPlaces: 2 })
    },
    getTotalBudgetIncome(index, key) {
      let total = 0
      this.getAllIncomes.forEach(record => {
        total += Number(record.monthlyAmountSplitUp[index][key] || 0)
      })
      return this.$d3.format(',.2f')(total)
    },
    getTotalBudgetExpense(index, key) {
      let total = 0
      this.getAllExpenses.forEach(record => {
        total += Number(record.monthlyAmountSplitUp[index][key] || 0)
      })
      return this.$d3.format(',.2f')(total)
    },
    getNetMonthlyBudgetIncome(index) {
      return this.$d3.format(',.2f')(
        Number(this.getTotalBudgetIncome(index, 'monthlyAmount')) -
          Number(this.getTotalBudgetExpense(index, 'monthlyAmount')) || 0
      )
    },
    getNetMonthlyActualIncome(index) {
      return this.$d3.format(',.2f')(
        Number(this.getTotalBudgetIncome(index, 'actualMonthlyAmount')) -
          Number(this.getTotalBudgetExpense(index, 'actualMonthlyAmount')) || 0
      )
    },
    autoResize() {
      this.$nextTick(() => {
        let height = this.$refs['container'].scrollHeight + 100
        let width = this.$refs['container'].scrollWidth
        let { h } = this.calculateDimensions({ height, width })
        this.resizeWidget({
          h: h,
        })
      })
    },
    hideShowSplitUp() {
      this.canShowSplitUp = !this.canShowSplitUp
    },
  },
}
</script>
