<template>
  <div class="box">
    <div class="label">{{ $t('common.shift.breaks') }}</div>
    <h3>{{ breaks }}</h3>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details'],
  name: 'ShiftBreaks',
  computed: {
    widgetTitle() {
      return 'ShiftBreaks'
    },
    shift() {
      return this.details
    },
    breaks() {
      let { associatedBreaks } = this.shift
      if (!isEmpty(associatedBreaks)) {
        return associatedBreaks
      }
      return 0
    },
  },
}
</script>

<style scoped>
.box {
  padding: 20px;
}
.label {
  font-size: 1.1em;
}
</style>
