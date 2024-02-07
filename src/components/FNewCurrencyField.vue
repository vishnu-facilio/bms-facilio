<template>
  <div class="fNewCurrencyField d-flex">
    <div class="width100">
      <template v-if="isLineItem">
        <FSelectCurrencyField
          v-model="currencyCode"
          :isDisabled="isDisabled"
          :isLoading="isLoading"
          :allSymbols="allSymbols"
          :isLineItem="true"
        ></FSelectCurrencyField>
      </template>
      <el-input
        v-else
        type="number"
        v-model="currencyValue"
        :placeholder="$t('setup.currency.enter_amount')"
        :disabled="disabled"
        class="input-with-select fc-input-full-border width100"
        @change="handleChange"
      >
        <template v-if="isDefaultCurrency || hideCurrency" slot="prepend">
          <span>{{ baseCurrency.symbol }}</span>
        </template>
        <FSelectCurrencyField
          v-else
          v-model="currencyCode"
          slot="prepend"
          :isDisabled="isDisabled"
          :isLoading="isLoading"
          :allSymbols="allSymbols"
        ></FSelectCurrencyField>
      </el-input>
      <div v-if="!hideDesc" class="mT5">
        <span :class="[showDescription ? 'description' : 'hideDescription']"
          >{{ description }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { getCurrencyInDecimalValue } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'
import FSelectCurrencyField from 'src/components/FSelectCurrencyField.vue'
import Vue from 'vue'

export default {
  components: { FSelectCurrencyField },
  props: [
    'disabled',
    'isEdit',
    'value',
    'field',
    'moduleData',
    'hideCurrency',
    'isLineItem',
    'initialCurrency',
    'isSubform',
    'hideDesc',
  ],
  data() {
    return {
      symbol: '',
      exchangeRate: '',
      allSymbols: [],
      isLoading: false,
      loading: false,
      currentCurrencyCode: null,
      currentCurrency: {},
      initialCurrencyCode: null,
      currencyList: [],
    }
  },
  computed: {
    ...mapState({
      account: state => state.account,
      currencies: state => state.activeCurrencies,
    }),
    showDescription() {
      let { currencyCode, baseCurrency } = this
      let { code } = baseCurrency || {}
      return currencyCode !== code && !this.isDefaultCurrency
    },
    description() {
      let { baseCurrency, exchangeRate, symbol } = this
      return `1 ${baseCurrency.symbol} = ${exchangeRate} ${symbol}`
    },
    currencyValue: {
      get() {
        return getCurrencyInDecimalValue(this.value, this.currentCurrency)
      },
      set(value) {
        value = getCurrencyInDecimalValue(value, this.currentCurrency)
        this.$emit('input', value)
        this.$emit('value', value)
      },
    },
    currencyCode: {
      get() {
        let defaultUserCurrency = Vue.cookie.get('userCurrency') || null
        let { moduleData, currentCurrencyCode } = this
        let code = moduleData?.currencyCode || null

        if (code != currentCurrencyCode && !isEmpty(currentCurrencyCode))
          return currentCurrencyCode
        return code || defaultUserCurrency || this.baseCurrency.code
      },
      set(value) {
        this.currentCurrencyCode = value
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
      return this.disabled
    },
    isDefaultCurrency() {
      let { currencyList, filteredCurrencyList } = this
      return (
        isEmpty(currencyList) ||
        currencyList.length === 1 ||
        filteredCurrencyList?.length === 1
      )
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.$getProperty(this.account, 'data.currencyInfo') || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
    filteredCurrencyList() {
      let { moduleData, currencyCode, currencyList } = this
      let { initialCurrencyCode } = moduleData || {}
      initialCurrencyCode = initialCurrencyCode || currencyCode
      return (currencyList || []).filter(
        currency =>
          currency.status || currency.currencyCode === initialCurrencyCode
      )
    },
  },
  watch: {
    code: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) this.currentCurrencyCode = newVal
      },
    },
    currencyCode: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) this.getExchangeRate(false, oldVal)
      },
    },
  },
  async created() {
    this.loading = true
    await this.$store.dispatch('getActiveCurrencyList')

    this.currencyList = [...(this.currencies || [])]

    this.$emit('setCurrencyCode', this.currencyCode, this.exchangeRate)
    this.setInitialExchangeRate()

    this.getExchangeRate(true)
    this.getCurrencyCode()

    this.$emit('setIsDefaultCurrency', this.isDefaultCurrency)
    this.loading = false
  },
  methods: {
    setInitialExchangeRate() {
      if (!isEmpty(this.currencyList)) {
        let { currencyCode, exchangeRate } = this.initialCurrency || {}
        this.currencyList = (this.currencyList || []).map(cur => {
          return currencyCode === cur.currencyCode
            ? { ...cur, exchangeRate }
            : cur
        })
      }
    },
    getCurrencyCode() {
      this.allSymbols = (this.filteredCurrencyList || []).map(currency => {
        let { displaySymbol, currencyCode, exchangeRate } = currency
        let displayName = `${currencyCode} - ${displaySymbol}`
        let label = this.isLineItem
          ? `${currencyCode} - ${displaySymbol}`
          : displaySymbol

        return {
          label,
          displayName,
          value: currencyCode,
          rate: exchangeRate,
        }
      })
    },
    getExchangeRate(omitCalculateExchangeRate, prevCurrencyCode) {
      let { currencyCode, currencyList } = this

      if (!isEmpty(currencyList)) {
        let result = (this.currencyList || []).find(
          currency => currencyCode === currency.currencyCode
        )
        this.currentCurrency = result

        prevCurrencyCode = prevCurrencyCode || this.moduleData?.oldCurrencyCode
        let previousCurrency = (this.currencyList || []).find(
          currency => prevCurrencyCode === currency.currencyCode
        )

        let { exchangeRate, displaySymbol } = result || {}
        this.exchangeRate = exchangeRate
        this.symbol = displaySymbol

        let isBaseCurrencyCode = currencyCode === this.baseCurrency.code
        let isPrevBaseCurrencyCode = prevCurrencyCode === this.baseCurrency.code

        let oldExchangeRate = previousCurrency?.exchangeRate

        let { currencyCode: initCode, exchangeRate: initRate } =
          this.initialCurrency || {}
        if (currencyCode === initCode) {
          exchangeRate = initRate || exchangeRate
        }
        if (!omitCalculateExchangeRate && oldExchangeRate > 0) {
          this.$emit('calculateExchangeRate', {
            exchangeRate,
            oldExchangeRate,
            isBaseCurrencyCode,
            isPrevBaseCurrencyCode,
            currencyCode,
          })
        }

        this.$emit('setCurrencyCode', currencyCode, exchangeRate, displaySymbol)

        this.isSubform &&
          this.$emit('setCurrencyCodeInSubform', currencyCode, exchangeRate)

        if (!isEmpty(prevCurrencyCode) && currencyCode != prevCurrencyCode)
          this.handleChange()
      }
    },
    handleChange() {
      let { currencyValue } = this
      if (!isEmpty(currencyValue) && !isNaN(currencyValue)) {
        this.$emit('handleChange', currencyValue)
      }
    },
  },
}
</script>

<style lang="scss">
.fNewCurrencyField {
  .el-input__inner {
    border-top-left-radius: 0 !important;
    border-bottom-left-radius: 0 !important;
    font-weight: 400;
    font-size: 14px;
    padding-left: 10px !important;
  }
  .description {
    color: #808080;
    font-size: 12px;
    letter-spacing: 0.43px;
    visibility: visible;
  }
  .hideDescription {
    visibility: hidden;
  }
  .currencyChangeDesc {
    color: #808080;
    font-size: 12px;
    letter-spacing: 0.43px;
    transition: all 1s ease-out;
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
        border: none;
      }
      .el-input .el-input--suffix {
        height: 40px;
      }
    }
  }
}
</style>
