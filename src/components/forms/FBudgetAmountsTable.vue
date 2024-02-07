<template>
  <div class="budget-amount-form-table">
    <div>
      <FNewCurrencyField
        v-if="canShowCurrencyDropDown"
        :moduleData="currencyDetails"
        :isLineItem="true"
        :initialCurrency="initialCurrency"
        @setCurrencyCode="setCurrencyCodeOnChange"
        @setIsDefaultCurrency="val => (isDefaultCurrency = val)"
        class="budget-currency-select"
      />
      <span class="blue-txt" @click="hideShowSplitUp">
        {{ canShowSplitUp ? 'Hide months' : 'Show months' }}
      </span>
    </div>
    <table
      class="mT10 table-fixed"
      :class="[canShowSplitUp ? 'width100' : 'width50']"
    >
      <thead>
        <tr>
          <th class="width150px">
            INCOME
          </th>
          <th
            v-for="(value, monthIndex) in monthsDisplayName"
            :key="monthIndex"
            class="width6o5 text-right"
          >
            {{ value }}
          </th>
          <th class="text-right">
            {{ getFiscalYearTitle }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(data, index) in budgetamount.incomes" :key="index">
          <td>
            <el-form
              :ref="`budget-item-account-1-${index}`"
              :rules="allValidationRules"
              :model="data"
              class="budget-sub-form"
            >
              <el-form-item prop="account" :required="true" class="hide-error">
                <FLookupField
                  class="width400px budget-table-select"
                  :model.sync="data.account.id"
                  :field="selectedLookupField"
                />
              </el-form-item>
            </el-form>
          </td>
          <td
            v-for="(splitup, sIndex) in canShowSplitUp
              ? data.monthlyAmountSplitUp
              : []"
            :key="sIndex"
          >
            <div>
              <el-input
                type="number"
                v-model.number="splitup.monthlyAmount"
                @change="computeYearlyAmount(data)"
                class="fc-budget-table-input width100"
              >
              </el-input>
            </div>
          </td>
          <td class="position-relative">
            <div>
              <el-input
                type="number"
                @change="overwriteMonthlySplitUp(data)"
                v-model.number="data.yearlyAmount"
                class="fc-budget-table-input width100"
              >
              </el-input>
            </div>
            <div class="add-delete-action-buttons">
              <div class="flex-middle">
                <i
                  @click="addIncomeBudgetItem()"
                  v-if="budgetamount.incomes.length - 1 === index"
                  class="el-icon-circle-plus-outline fc-row-add-icon f20 bold pL5"
                ></i>
                <i
                  @click="removeIncomeBudgetItem(index)"
                  v-if="budgetamount.incomes.length > 1"
                  class="el-icon-remove-outline fc-row-delete-icon f20 bold pL10"
                ></i>
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <table
      class="mT50 table-fixed"
      :class="[canShowSplitUp ? 'width100' : 'width50']"
    >
      <thead>
        <tr>
          <th class="width150px">
            EXPENSE
          </th>
          <th
            v-for="(value, monthIndex) in monthsDisplayName"
            :key="monthIndex"
            class="width6o5 text-right"
          >
            {{ value }}
          </th>
          <th class="text-right">
            {{ getFiscalYearTitle }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(data, index) in budgetamount.expenses" :key="index">
          <td>
            <el-form
              :ref="`budget-item-account-2-${index}`"
              :rules="allValidationRules"
              :model="data"
              class="budget-sub-form"
            >
              <el-form-item prop="account" :required="true" class="hide-error">
                <FLookupField
                  class="width400px budget-table-select"
                  :model.sync="data.account.id"
                  :field="selectedExpenseField"
                />
              </el-form-item>
            </el-form>
          </td>
          <td
            v-for="(splitup, sIndex) in canShowSplitUp
              ? data.monthlyAmountSplitUp
              : []"
            :key="sIndex"
          >
            <div>
              <el-input
                type="number"
                v-model.number="splitup.monthlyAmount"
                @change="computeYearlyAmount(data)"
                class="fc-budget-table-input width100"
              >
              </el-input>
            </div>
          </td>
          <td class="position-relative">
            <div>
              <el-input
                type="number"
                @change="overwriteMonthlySplitUp(data)"
                v-model.number="data.yearlyAmount"
                class="fc-budget-table-input width100"
              >
              </el-input>
            </div>
            <div class="add-delete-action-buttons">
              <div class="flex-middle">
                <i
                  @click="addExpenseBudgetItem()"
                  v-if="budgetamount.expenses.length - 1 === index"
                  class="el-icon-circle-plus-outline fc-row-add-icon f20 bold pL5"
                ></i>
                <i
                  @click="removeExpenseBudgetItem(index)"
                  v-if="budgetamount.expenses.length > 1"
                  class="el-icon-remove-outline fc-row-delete-icon f20 bold pL10"
                ></i>
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <table
      class="mT50 table-fixed"
      :class="[canShowSplitUp ? 'width100' : 'width50']"
    >
      <thead>
        <tr>
          <th class="width150px">
            TOTAL
          </th>
          <th
            v-for="(value, monthIndex) in monthsDisplayName"
            :key="monthIndex"
            class="width6o5 text-right"
          >
            {{ value }}
          </th>
          <th class="text-right">
            {{ getFiscalYearTitle }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td class="td-padding">
            {{ `INCOME (${$currency})` }}
          </td>
          <td
            v-for="(value, monthIndex) in monthsDisplayName"
            :key="monthIndex"
            class="text-right td-padding"
          >
            <div class="overflow-scroll">
              {{
                $d3.format(',.2f')(getTotalCostForMonth(monthIndex, 'incomes'))
              }}
            </div>
          </td>
          <td class="td-padding text-right">
            <div class="overflow-scroll">
              {{ $d3.format(',.2f')(getTotalIncome) }}
            </div>
          </td>
        </tr>
        <tr>
          <td class="td-padding">
            {{ `EXPENSE (${$currency})` }}
          </td>
          <td
            v-for="(value, monthIndex) in monthsDisplayName"
            :key="monthIndex"
            class="text-right td-padding"
          >
            <div class="overflow-scroll">
              {{
                $d3.format(',.2f')(getTotalCostForMonth(monthIndex, 'expenses'))
              }}
            </div>
          </td>
          <td class="td-padding text-right">
            <div class="overflow-scroll">
              {{ $d3.format(',.2f')(getTotalExpense) }}
            </div>
          </td>
        </tr>
        <tr>
          <td class="td-padding bold">
            {{ `NET INCOME (${$currency})` }}
          </td>
          <td
            v-for="(value, monthIndex) in monthsDisplayName"
            :key="monthIndex"
            class="text-right td-padding"
          >
            <div class="overflow-scroll bold">
              {{ $d3.format(',.2f')(getNetIncomeForMonth(monthIndex)) }}
            </div>
          </td>
          <td class="td-padding bold text-right">
            <div class="overflow-scroll">
              {{ $d3.format(',.2f')(getNetIncome) }}
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import { deepCloneObject } from 'util/utility-methods'
import FLookupField from 'src/components/forms/FLookupField.vue'
import { isEmpty } from '@facilio/utils/validation'
import { validateForDropDownField } from 'util/form-validator'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'
import { mapState } from 'vuex'

export default {
  props: ['model', 'field', 'budgetamount', 'initialCurrency', 'currencyObj'],
  components: { FLookupField, FNewCurrencyField },

  data() {
    return {
      canShowSplitUp: true,
      sample: null,
      unitPrice: 100,
      incomeAccountTypes: [],
      expenseAccountTypes: [],
      selectedExpenseField: {
        isDataLoading: false,
        options: [],
        config: {},
        lookupModuleName: 'chartofaccount',
        filters: {
          accountType: {
            operatorId: 36,
            value: ['5'],
          },
        },
        field: {
          lookupModule: {
            name: 'chartofaccount',
            displayName: 'chart of Account',
          },
        },
        multiple: false,
        selectedItems: [],
      },
      selectedLookupField: {
        isDataLoading: false,
        options: [],
        config: {},
        lookupModuleName: 'chartofaccount',
        filters: {
          accountType: {
            operatorId: 36,
            value: ['4'],
          },
        },
        field: {
          lookupModule: {
            name: 'chartofaccount',
            displayName: 'chart of Account',
          },
        },
        multiple: false,
        selectedItems: [],
      },
      allValidationRules: {
        account: [
          {
            required: true,
            message: 'Please input',
            trigger: 'change',
            validator: validateForDropDownField,
          },
        ],
      },
      currencyData: {},
    }
  },
  mounted() {
    this.loadAllIncomeAccountTypes()
    this.loadAllExpenseAccountTypes()
    this.setCurrencyData()
  },
  computed: {
    ...mapState({
      account: state => state.account,
      currencyList: state => state.activeCurrencies,
    }),
    isMulticurrencyEnabled() {
      let { multiCurrencyEnabled } =
        this.$getProperty(this.account, 'data.currencyInfo') || {}
      return multiCurrencyEnabled
    },
    canShowCurrencyDropDown() {
      let { isMulticurrencyEnabled, isDefaultCurrency } = this
      return !isDefaultCurrency && isMulticurrencyEnabled
    },
    currencyDetails: {
      get() {
        let { currencyCode, exchangeRate, symbol, initialCurrencyCode } =
          this.currencyData || this.model || {}

        return { currencyCode, exchangeRate, symbol, initialCurrencyCode }
      },
      set(value) {
        let { expenses, incomes } =
          this.$getProperty(this, 'model.budgetamount') || {}
        let { currencyCode, exchangeRate } = value || {}
        this.currencyData = { ...(this.currencyData || {}), ...value }

        expenses = (expenses || []).map(expense => {
          return {
            ...(expense || {}),

            currencyCode,
            exchangeRate,
          }
        })
        incomes = (incomes || []).map(income => {
          return {
            ...(income || {}),
            currencyCode,
            exchangeRate,
          }
        })
        this.$set(this.model, 'budgetamount', { expenses, incomes })
        this.$emit('update:model', {
          ...this.model,
          currencyCode,
          exchangeRate,
        })
      },
    },
    incomeListMap() {
      return (
        (this.budgetamount.incomes || []).map(item =>
          this.$getProperty(item, 'account.id', -1)
        ) || []
      )
    },
    expenseListMap() {
      return (
        (this.budgetamount.expenses || []).map(item =>
          this.$getProperty(item, 'account.id', -1)
        ) || []
      )
    },
    getTotalIncome() {
      let { incomes } = this.budgetamount
      let total = 0
      incomes.forEach(income => {
        if (!isEmpty(income.yearlyAmount)) {
          total += Number(income.yearlyAmount)
        }
      })
      return Number(total)
    },
    getTotalExpense() {
      let { expenses } = this.budgetamount
      let total = 0
      expenses.forEach(income => {
        if (!isEmpty(income.yearlyAmount)) {
          total += Number(income.yearlyAmount)
        }
      })
      return Number(total)
    },
    getNetIncome() {
      return this.getTotalIncome - this.getTotalExpense
    },
    monthsDisplayName() {
      if (this.canShowSplitUp) {
        let { model } = this
        if (!isEmpty(model.fiscalYear.id)) {
          let month = Number(model.fiscalYear.id)
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
      let { model } = this
      return `FY${
        !isEmpty(model.fiscalYearStart.id)
          ? 2018 + Number(model.fiscalYearStart.id)
          : ''
      }`
    },
  },
  watch: {
    'currencyData.currencyCode': {
      handler(newVal, oldVal) {
        if (newVal != oldVal) {
          this.getExchangeRate(oldVal)
        }
      },
      deep: true,
    },
  },
  methods: {
    setCurrencyData() {
      let { currencyCode, exchangeRate, initialCurrencyCode } =
        this.currencyObj || this.model || {}
      this.currencyDetails = { currencyCode, exchangeRate, initialCurrencyCode }
    },
    setCurrencyCodeOnChange(currencyCode, exchangeRate, symbol) {
      if (!isEmpty(currencyCode))
        this.currencyDetails = { currencyCode, exchangeRate, symbol }
      this.$emit('setCurrencyCode', currencyCode, exchangeRate)
    },
    hideShowSplitUp() {
      this.canShowSplitUp = !this.canShowSplitUp
    },
    addIncomeBudgetItem() {
      let { budgetIncomeDefaults } = this.$constants || {}
      let { monthlyAmountSplitUp } = budgetIncomeDefaults || {}
      let { currencyCode, exchangeRate } = this.currencyDetails || {}
      monthlyAmountSplitUp = monthlyAmountSplitUp.map(monthlyAmount => {
        return {
          ...monthlyAmount,
          currencyCode,
          exchangeRate,
        }
      })
      budgetIncomeDefaults.monthlyAmountSplitUp = monthlyAmountSplitUp
      this.budgetamount.incomes.push(
        deepCloneObject({
          ...(budgetIncomeDefaults || {}),
          currencyCode,
          exchangeRate,
        })
      )
    },
    removeIncomeBudgetItem(index) {
      this.budgetamount.incomes.splice(index, 1)
    },
    addExpenseBudgetItem() {
      let { budgetExpenseDefaults } = this.$constants || {}
      let { monthlyAmountSplitUp } = budgetExpenseDefaults || {}
      let { currencyCode, exchangeRate } = this.currencyDetails || {}
      monthlyAmountSplitUp = monthlyAmountSplitUp.map(monthlyAmount => {
        return {
          ...monthlyAmount,
          currencyCode,
          exchangeRate,
        }
      })
      budgetExpenseDefaults.monthlyAmountSplitUp = monthlyAmountSplitUp
      this.budgetamount.expenses.push(
        deepCloneObject({
          ...(budgetExpenseDefaults || {}),
          currencyCode,
          exchangeRate,
        })
      )
    },
    removeExpenseBudgetItem(index) {
      this.budgetamount.expenses.splice(index, 1)
    },
    getTotalCostForMonth(index, type) {
      let costForTheMonth = 0
      this.budgetamount[type].forEach(data => {
        if (!isEmpty(data.monthlyAmountSplitUp[index].monthlyAmount)) {
          costForTheMonth += data.monthlyAmountSplitUp[index].monthlyAmount
        }
      })
      return Number(costForTheMonth) || 0
    },
    getNetIncomeForMonth(index) {
      return (
        Number(
          this.getTotalCostForMonth(index, 'incomes') -
            this.getTotalCostForMonth(index, 'expenses')
        ) || 0
      )
    },
    computeYearlyAmount(data) {
      let yearlyAmount = 0
      data.monthlyAmountSplitUp.forEach(record => {
        if (!isEmpty(record.monthlyAmount)) {
          yearlyAmount += Number(record.monthlyAmount)
        }
      })
      data.yearlyAmount = Number(yearlyAmount)
    },
    async overwriteMonthlySplitUp(data) {
      if (!isEmpty(data.yearlyAmount)) {
        let { currencyCode, exchangeRate } = this.currencyDetails || {}
        let monthlyAmount = Number(data.yearlyAmount) / 12
        let isConfirmationRequired = false
        let oldYearlyAmount = 0
        data.monthlyAmountSplitUp.forEach(record => {
          if (Number(record.monthlyAmount) !== 0) {
            isConfirmationRequired = true
          }
          oldYearlyAmount += Number(record.monthlyAmount)
          record.currencyCode = currencyCode
          record.exchangeRate = exchangeRate
        })
        if (isConfirmationRequired) {
          let value = await this.$dialog.confirm({
            title: 'Overwrite Monthly amount',
            message: 'Are you sure to overwrite monthly amounts',
            rbDanger: true,
            rbLabel: 'Yes',
          })
          if (value) {
            this.overwriteMontlyAmount(data, monthlyAmount)
          } else {
            data.yearlyAmount = oldYearlyAmount
          }
        } else {
          this.overwriteMontlyAmount(data, monthlyAmount)
        }
      }
    },
    overwriteMontlyAmount(data, monthlyAmount) {
      data.monthlyAmountSplitUp.forEach(record => {
        record.monthlyAmount = monthlyAmount
      })
    },
    validateForm() {
      let isIncomeValid = true
      this.budgetamount.incomes.forEach((data, index) => {
        let accountFormRef = this.$refs[`budget-item-account-1-${index}`]
        if (!isEmpty(accountFormRef)) {
          accountFormRef[0].validate(valid => {
            if (!valid) {
              isIncomeValid = false
            }
          })
        }
      })
      let isExpenseValid = true
      this.budgetamount.expenses.forEach((data, index) => {
        let accountFormRef = this.$refs[`budget-item-account-2-${index}`]
        if (!isEmpty(accountFormRef)) {
          accountFormRef[0].validate(valid => {
            if (!valid) {
              isExpenseValid = false
            }
          })
        }
      })
      if (
        isIncomeValid &&
        !isExpenseValid &&
        this.budgetamount.expenses.length === 1
      ) {
        return true
      } else if (
        !isIncomeValid &&
        isExpenseValid &&
        this.budgetamount.incomes.length === 1
      ) {
        return true
      }
      return isIncomeValid && isExpenseValid
    },
    getExchangeRate(prevCurrencyCode) {
      let { currencyList, currencyDetails, currencyObj } = this
      let currencyInfoObj = {}
      if (!isEmpty(currencyList)) {
        let { currencyCode } = currencyDetails || {}

        let result = (currencyList || []).find(
          currency => currencyCode === currency.currencyCode
        )

        prevCurrencyCode = prevCurrencyCode || currencyObj?.oldCurrencyCode
        let previousCurrency = (currencyList || []).find(
          currency => prevCurrencyCode === currency.currencyCode
        )

        let { exchangeRate } = result || {}
        let { currencyCode: baseCode } =
          this.$getProperty(this.account, 'data.currencyInfo') || {}
        let isBaseCurrencyCode = currencyCode === baseCode
        let isPrevBaseCurrencyCode = prevCurrencyCode === baseCode

        let oldExchangeRate = previousCurrency?.exchangeRate

        let { currencyCode: initCode, exchangeRate: initRate } =
          this.initialCurrency || {}
        if (currencyCode === initCode) {
          exchangeRate = initRate || exchangeRate
        }
        currencyInfoObj = {
          exchangeRate,
          oldExchangeRate,
          isBaseCurrencyCode,
          isPrevBaseCurrencyCode,
          currencyCode,
        }
      }

      this.$emit('calculateExchangeRate', currencyInfoObj || {})
    },
  },
}
</script>
<style>
.budget-section-container {
  position: relative;
}
.budget-currency-select {
  position: absolute;
  top: 36px;
  right: 214px;
}
</style>
