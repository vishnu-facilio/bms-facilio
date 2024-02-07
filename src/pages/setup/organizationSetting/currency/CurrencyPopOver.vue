<template>
  <div>
    <div class="d-flex" v-if="showCurrencyField">
      <span v-if="showInfo">{{ getMultiCurrencyFieldValue || '---' }}</span>
      <el-popover
        v-if="showMultiCurrencyInfo"
        placement="right"
        :title="$t('setup.currency.rate')"
        width="230"
        trigger="hover"
        :content="currencyContent"
        :open-delay="5"
        :disabled="isEmpty(baseValue)"
      >
        <div
          v-if="!showInfo"
          :class="[baseValue && 'pointer']"
          slot="reference"
        >
          <div>{{ `${getMultiCurrencyFieldValue}` }}</div>
          <div class="f12">{{ baseValue }}</div>
        </div>

        <fc-icon
          v-if="!isBaseCode && showInfo && !hideInfoBtn"
          slot="reference"
          group="dsm"
          class="pointer info-position mL2"
          name="info"
          size="13"
        ></fc-icon>
      </el-popover>
      <span v-else>{{ getMultiCurrencyFieldValue || '---' }}</span>
    </div>
    <span v-else>---</span>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { getCurrencyInDecimalValue } from './CurrencyUtil'
import { mapState } from 'vuex'
export default {
  props: ['field', 'details', 'showInfo', 'hideInfoBtn'],
  data() {
    return {
      baseValue: '',
      isEmpty,
    }
  },
  computed: {
    ...mapState({
      account: state => state.account,
      currencyList: state => state.activeCurrencies,
    }),
    showMultiCurrencyInfo() {
      let { multiCurrencyEnabled } = this.multiCurrency || {}
      return multiCurrencyEnabled
    },
    isBaseCode() {
      let { currencyCode: baseCode } = this.multiCurrency || {}
      let { currencyCode } = this.details || {}
      return baseCode === currencyCode
    },
    showCurrencyField() {
      let { currencyValue } = this
      return !isEmpty(currencyValue) && currencyValue != '---'
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.$getProperty(this.account, 'data.currencyInfo') || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
    currencyContent() {
      let { details, currencyValue } = this
      let { displaySymbol: baseSymbol } = this.multiCurrency

      if (!isEmpty(currencyValue)) {
        let { exchangeRate, currencyCode } = details || {}
        let currency = (this.currencyList || []).find(
          currency => currencyCode === currency.currencyCode
        )

        if (isEmpty(currency) || currency.displaySymbol === baseSymbol) {
          return null
        }

        let { displaySymbol } = currency || {}
        let content = `${baseSymbol} 1 = ${displaySymbol} ${exchangeRate}`

        return content
      }
      return '---'
    },
    currencyValue() {
      let { details, field } = this
      let { data } = details || {}
      return field.displayValue || details[field.name] || data?.[field.name]
    },
    getMultiCurrencyFieldValue() {
      let { currencyValue, showInfo } = this
      let {
        displaySymbol: baseSymbol,
        currencyCode: baseCode,
        multiCurrencyEnabled,
      } = this.multiCurrency

      let { currencyCode, exchangeRate } = this.details || {}
      let currency = (this.currencyList || []).find(
        currency => currencyCode === currency.currencyCode
      )

      let baseCurrency = (this.currencyList || []).find(
        currency => baseCode === currency.currencyCode
      )

      if (isEmpty(currency) && !isEmpty(currencyValue)) {
        return `${baseSymbol} ${currencyValue}`
      }

      let { displaySymbol } = currency || {}
      displaySymbol = !isEmpty(displaySymbol) ? displaySymbol : baseSymbol

      let baseValue = ''
      if (multiCurrencyEnabled && baseCode !== currencyCode) {
        let value = getCurrencyInDecimalValue(
          currencyValue / exchangeRate,
          baseCurrency
        )
        baseValue = `( ${baseSymbol} ${value} )`
      }
      currencyValue = getCurrencyInDecimalValue(currencyValue, currency)
      this.setBaseValue(baseValue)

      return showInfo
        ? `${displaySymbol} ${currencyValue} ${baseValue}`
        : `${displaySymbol} ${currencyValue}`
    },
  },
  async created() {
    await this.$store.dispatch('getActiveCurrencyList')
  },
  methods: {
    setBaseValue(value) {
      this.baseValue = value
    },
  },
}
</script>

<style scoped>
.info-position {
  position: relative;
  top: 2px;
}
</style>
