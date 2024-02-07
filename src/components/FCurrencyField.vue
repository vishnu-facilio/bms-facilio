<template>
  <div class="currencyField">
    <el-input
      type="number"
      v-model="currency.currencyValue"
      :placeholder="$t('setup.currency.enter_amount')"
      :disabled="disabled"
      class="input-with-select fc-input-full-border width100"
      @change="handleChange"
    >
      <template v-if="isDefaultCurrency" slot="prepend">
        <span>{{ baseCurrency.symbol }}</span>
      </template>
      <el-select
        v-else
        v-model="currency.currencyCode"
        slot="prepend"
        :disabled="isDisabled"
        :loading="isLoading"
        :placeholder="' '"
        @change="getExchangeRate"
      >
        <el-option
          v-for="(record, index) in allSymbols"
          :key="index"
          :label="record.label"
          :value="record.value"
        ></el-option>
      </el-select>
    </el-input>
    <div class="mT5">
      <span class="description" v-if="showDescription">{{ description }}</span>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { CurrencyListModel } from 'src/pages/setup/organizationSetting/currency/CurrencyModel'
import { mapState } from 'vuex'

export default {
  props: ['disabled', 'isEdit', 'value', 'field'],
  data() {
    return {
      symbol: '',
      exchangeRate: '',
      allSymbols: [],
      currencyList: null,
      isLoading: false,
      loading: false,
    }
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    showDescription() {
      let { currency, baseCurrency } = this
      let { currencyCode } = currency || {}
      let { code } = baseCurrency || {}
      return currencyCode !== code && !this.isDefaultCurrency
    },
    description() {
      let { baseCurrency, exchangeRate, symbol } = this
      return `1 ${baseCurrency.symbol} = ${exchangeRate} ${symbol}`
    },
    currency: {
      get() {
        let { field, baseCurrency, value } = this
        let { value: fieldValue } = field || {}
        value = value || fieldValue

        if (isEmpty(value)) {
          value = { currencyCode: null, currencyValue: null }
        } else {
          value = typeof value === 'string' ? JSON.parse(value) : value
          let { currencyValue } = value || {}
          value.currencyValue = !isEmpty(currencyValue)
            ? `${currencyValue}`
            : null
        }

        if (isEmpty(value?.currencyCode) && baseCurrency.isCurrencyEnabled)
          value.currencyCode = baseCurrency.code

        return value
      },
      set(value) {
        if (!isEmpty(value?.currencyValue)) this.emitValues(value)
        else {
          value = !this.loading ? value : null
          this.emitValues(value)
        }
      },
    },
    baseCurrency() {
      let {
        displaySymbol,
        currencyCode,
        multiCurrencyEnabled,
      } = this.multiCurrency
      return {
        code: currencyCode,
        symbol: displaySymbol,
        isCurrencyEnabled: multiCurrencyEnabled,
      }
    },
    isDisabled() {
      let { disabled, isEdit, currency } = this
      return disabled || (isEdit && !isEmpty(currency.currencyValue))
    },
    isDefaultCurrency() {
      let { currencyList } = this
      return isEmpty(currencyList) || currencyList.length === 1
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.account.data.currencyInfo || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
  },
  async created() {
    this.loading = true
    this.setCurrencyData()
    await this.fetchRecords()
    this.getExchangeRate()
    this.loading = false

    if (isEmpty(this.currency?.currencyValue)) {
      this.currency = { currencyCode: null, currencyValue: null }
    }
  },
  methods: {
    emitValues(value) {
      this.$emit('input', value)
      this.$emit('value', value)
    },
    getCurrencyCode() {
      this.allSymbols = (this.currencyList || []).map(currency => {
        let { displaySymbol, currencyCode, exchangeRate } = currency
        return {
          label: displaySymbol,
          value: currencyCode,
          rate: exchangeRate,
        }
      })
    },
    async fetchRecords(force = true) {
      this.isLoading = true
      try {
        this.currencyList = await CurrencyListModel.fetchAll({ force })
        this.getCurrencyCode()
      } catch (error) {
        this.$message.error(this.$t('common._common.error_occured'))
      }
      this.isLoading = false
    },
    getExchangeRate() {
      if (!isEmpty(this.currencyList)) {
        let { currencyCode } = this.currency
        let result = this.currencyList.find(
          currency => currency.currencyCode === currencyCode
        )
        let { exchangeRate, displaySymbol } = result || {}
        this.exchangeRate = exchangeRate
        this.symbol = displaySymbol
        this.handleChange()
      }
    },
    setCurrencyData() {
      let { field, baseCurrency, value } = this
      let { value: fieldValue } = field || {}
      value = value || fieldValue

      this.currency = { currencyCode: null, currencyValue: null }
      if (!isEmpty(value)) {
        value = typeof value === 'string' ? JSON.parse(value) : value
        value.currencyValue = !isEmpty(value.currencyValue)
          ? `${value.currencyValue}`
          : null

        if (isEmpty(value.currencyCode) && baseCurrency.isCurrencyEnabled)
          value.currencyCode = baseCurrency.code

        this.currency = value
      }
    },
    handleChange() {
      !isEmpty(this.value?.currencyValue) &&
        this.$emit('handleChange', this.currency)
    },
  },
}
</script>

<style lang="scss">
.currencyField {
  .el-input__inner {
    border-top-left-radius: 0 !important;
    border-bottom-left-radius: 0 !important;
    font-weight: 400;
    font-size: 14px;
  }
  .description {
    color: #808080;
    font-size: 12px;
    letter-spacing: 0.43px;
  }
  .el-input-group__prepend {
    width: 75px;
    border-right: solid 0.5px #d0d9e2;
    div.el-select {
      .el-input__inner {
        border-top: 1px solid #d0d9e2;
        border-bottom: 1px solid #d0d9e2;
        border-top-right-radius: 0px;
        border-bottom-right-radius: 0px;
        color: #000;
        font-size: 14px;
      }
    }
  }
}
</style>
