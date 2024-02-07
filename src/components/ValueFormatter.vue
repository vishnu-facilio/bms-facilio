<template>
  <span>
    <span v-if="val" class="value">
      {{ val }}
    </span>
    <span v-if="withUnit" class="unit">
      {{ uni }}
    </span>
  </span>
</template>

<script>
import { isUndefined } from '@facilio/utils/validation'

export default {
  props: ['withUnit', 'config', 'value', 'unit', 'maxDecimal'],
  data() {
    return {
      val: null,
      uni: '',
    }
  },
  mounted() {
    if (this.config) {
      this.loadDataWithConfig()
    } else {
      this.loadConfig()
    }
  },
  methods: {
    loadConfig() {
      if (this.unit && this.unit !== '') {
        let obj = this.$convert(this.value)
          .from(this.unit)
          .toBest()
        // value set
        if (typeof obj === 'object') {
          if (Number(obj.val) % 1 === 0) {
            this.val = obj.val
          } else if (
            Number(obj.val) % 1 !== 0 &&
            !isUndefined(this.maxDecimal)
          ) {
            this.val = obj.val.toFixed(this.maxDecimal)
          } else {
            this.val = obj.val
          }
        }
        // unit set
        if (this.withUnit) {
          if (typeof obj === 'object') {
            this.uni = obj.unit
          }
        }
      } else {
        this.val = this.value
      }
    },
    loadDataWithConfig() {},
  },
}
</script>

<style></style>
