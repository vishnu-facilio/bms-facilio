<template>
  <div class="row report-user-filters">
    <div v-if="false"></div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['filters', 'report'],
  watch: {
    filters: {
      handler(newVal, oldVal) {
        // watch it
        console.log(newVal + '    ' + oldVal)
        if (newVal != oldVal) {
          this.applyDateTimeFilter(newVal)
        }
      },
    },
  },
  methods: {
    applyDateTimeFilter(filters) {
      let ttimeFilter = this.formTTimeFilter(filters)
      if (ttimeFilter && this?.report?.reportType !== 4) {
        this.$emit('applyTTimeFilter', {
          filters: ttimeFilter,
          isTTimeFilter: true,
        })
      }
    },
    formTTimeFilter(filters) {
      let formTTimeFilerObj = {}
      let flag = true
      if (filters) {
        const keys = Object.keys(filters)
        keys.forEach((key, index) => {
          if (key && key.startsWith('ttime_')) {
            formTTimeFilerObj[key] = filters[key]
          }
        })
        if (flag && !isEmpty(formTTimeFilerObj)) {
          return formTTimeFilerObj
        }
      }
      return null
    },
  },
}
</script>
