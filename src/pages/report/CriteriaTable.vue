<template>
  <div class="fc-report-pdf-details mT40">
    <div class="fc-report-pdf-heading">
      REPORT CONDITIONS
      <span v-if="criteriaObj.conditions.length > 1" class="fw4"
        >( Matching Criteria : {{ criteriaObj.pattern }} )</span
      >
    </div>

    <div class="pT20 flex-middle flex-wrap">
      <template v-for="(condition, index) in criteriaObj.conditions">
        <div
          :class="condition.width === 100 ? 'width50' : 'width100'"
          class="fc-report-pdf-border mB20 report-option-table"
          :key="index"
        >
          <table class="width100">
            <td class="fc-rpeort-pdf-col-bg text-center" style="width: 7%;">
              {{ condition.key }}
            </td>
            <td
              :class="condition.width === 'width100' ? 'width25' : 'width25'"
              class="fc-report-pdf-border-left pl10"
            >
              {{ condition.field }}
            </td>
            <td
              :class="condition.width === 'width100' ? 'width25' : 'width25'"
              class="fc-report-pdf-border-left pl10 pR10 text-lowercase"
            >
              {{ condition.operator }}
            </td>
            <td
              :class="condition.width === 'width100' ? 'width60' : 'width70'"
              class="fc-report-pdf-border-left pl10 pR10"
            >
              {{ formatCriteriaValue(condition.value, condition.fieldType) }}
            </td>
          </table>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
export default {
  props: ['criteriaObj'],
  mounted() {
    this.criteriaObj.conditions.forEach(condition => {
      if (condition.value && condition.value.length > 30) {
        this.$set(condition, 'width', 100)
      }
    })
  },
  methods: {
    formatCriteriaValue(valueArray, type) {
      let self = this
      let values = []
      if (Array.isArray(valueArray)) {
        if (type == 'Date') {
          valueArray.forEach(value => {
            if (value != -1) {
              values.push(self.$options.filters.formatDate(value, true))
            }
          })
        } else if (type == 'DateTime') {
          valueArray.forEach(value => {
            if (value != -1) {
              values.push(self.$options.filters.formatDate(value))
            }
          })
        } else {
          values = valueArray
        }
      }
      return values.join(', ')
    },
  },
}
</script>
