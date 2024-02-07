<template>
  <div :style="style" class="inline-flex growth-arrow">
    <i :class="`el-icon-caret-${val.arrow} arrow-class`"></i>
    <div
      id="kpi2-percentage"
      :class="['value-class', noArrow && 'percentage-color']"
    >
      {{ val.percentage }} <span class="percentage-class">%</span>
    </div>
  </div>
</template>
<script>
import { isEmpty, isNumber } from '@facilio/utils/validation'
import helper from 'pages/card-builder/card-helpers'
export default {
  props: ['value', 'baselineValue', 'arrowStyles'],
  mixins: [helper],
  data() {
    return {
      noArrow: false,
      val: {
        percentage: 0,
        arrow: 'bottom',
      },
      style: {
        color: 'red',
      },
    }
  },
  created() {
    this.formatValue()
  },
  watch: {
    value() {
      this.formatValue()
    },
    baselineValue() {
      this.formatValue()
    },
  },
  methods: {
    compareValue(value, baseValue = null) {
      let val = value
      let base = baseValue
      let per = '--'
      if (!isEmpty(base) && !isEmpty(val)) {
        if (base !== 0 && isNumber(base) && isNumber(val)) {
          per = ((val - base) / base) * 100
          let va = Math.abs(per)
          let sign = Math.sign(per)
          return {
            percentage: `${this.formatDecimal(va, 2)}`,
            arrow: sign === 0 || sign === 1 ? 'top' : 'bottom',
          }
        }
      }
      this.noArrow = true
      return {
        percentage: '--',
      }
    },
    formatValue() {
      this.val = this.compareValue(this.value, this.baselineValue)
      if (
        this.arrowStyles &&
        this.arrowStyles.arrowDownColor &&
        this.arrowStyles.arrowUpColor
      ) {
        if (this.val.arrow === 'top') {
          this.style.color = this.arrowStyles.arrowUpColor
        } else {
          this.style.color = this.arrowStyles.arrowDownColor
        }
      } else {
        if (this.val.arrow === 'top') {
          this.style.color = 'green'
        }
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.growth-arrow {
  .arrow-class {
    font-size: 20px;
    padding-top: 5px;
  }
  .value-class {
    font-size: 25px;
    padding-left: 5px;
  }
  .percentage-class {
    font-size: 18px;
    padding-left: 2px;
  }
  .percentage-color {
    color: #25243e;
  }
}
</style>
