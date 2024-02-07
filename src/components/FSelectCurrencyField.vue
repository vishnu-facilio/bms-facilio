<template>
  <el-select
    v-model="currencyCode"
    :disabled="isDisabled"
    :loading="isLoading"
    :class="[isLineItem && 'currencySelect-lineItem']"
    popper-class="fCurrencyCodeList"
  >
    <el-option
      v-for="(record, index) in allSymbolsList"
      :key="index"
      :label="record.label"
      :value="record.value"
      :disabled="record.disabled"
    >
      {{ record.displayName }}
    </el-option>
  </el-select>
</template>

<script>
export default {
  props: ['value', 'isDisabled', 'isLoading', 'allSymbols', 'isLineItem'],
  computed: {
    currencyCode: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      },
    },
    allSymbolsList() {
      let text = this.$t('common.products.change_currency_text')
      return [{ displayName: text, disabled: true }, ...(this.allSymbols || [])]
    },
  },
}
</script>
<style>
.currencySelect-lineItem .el-input__inner {
  width: 8.063rem;
  height: 2.5rem;
  padding: 0.625rem 1rem;
  border-radius: 2px;
  border: solid 1px #d0d9e2;
}

.fCurrencyCodeList {
  width: 15%;
}

.fCurrencyCodeList .el-select-dropdown__list li:nth-child(1) {
  font-size: 12px;
  padding: 0 20px;
  position: relative;
  line-height: 18px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  background-color: #f2f7ff;
  padding: 10px 20px;
  color: #2e2e49;
  margin-bottom: 5px;
  white-space: normal;
  overflow: auto;
  font-weight: 300;
  height: 100% !important;
  border-top-left-radius: 3px;
  border-top-right-radius: 3px;
}

.fCurrencyCodeList .el-select-dropdown__list {
  padding: 0 0 6px 0;
}

.fCurrencyCodeList .el-select-dropdown__item.is-disabled:hover {
  background-color: #f2f7ff;
}
</style>
