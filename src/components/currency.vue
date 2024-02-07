<template>
  <div>
    <span
      :class="`symbol-${symbolSize}`"
      v-if="prefixUnit"
      class="fc-currency-value"
      >{{ recordCurrency || $currency }}</span
    >
    <span :class="`curreny-${valueSize}`">{{ formatedValue }}</span>
    <span
      :class="`symbol-${symbolSize} mL5`"
      class="fc-currency-value"
      v-if="!prefixUnit"
      >{{ recordCurrency || $currency }}</span
    >
  </div>
</template>
<script>
export default {
  props: ['value', 'symbolSize', 'valueSize', 'recordCurrency'],
  computed: {
    formatedValue() {
      return this.$d3.format(',.2f')(this.value)
    },
    prefixUnit() {
      let { $currency } = this
      return ['$', '£', '₹'].includes($currency)
    },
  },
}
</script>
<style lang="scss">
.curreny-xs,
.symbol-xlg {
  font-size: 25px;
}
.currency-xxs {
  font-size: 13px;
}
.curreny-mmd {
  font-size: 18px;
}
.curreny-md {
  font-size: 28px;
}
.curreny-lg {
  font-size: 30px;
}
.curreny-xlg {
  font-size: 35px;
}
.symbol-xs {
  font-size: 15px;
}
.symbol-md {
  font-size: 18px;
}
.symbol-lg {
  font-size: 20px;
}
</style>
