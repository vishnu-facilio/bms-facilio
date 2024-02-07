<template>
  <div class="box">
    <div class="label">{{ $t('common.shift.employees_associated') }}</div>
    <h3>{{ employeesAssociated }}</h3>
    <div>
      <i>{{ todayDate }}</i>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details'],
  name: 'ShiftAssociatedEmployees',
  computed: {
    widgetTitle() {
      return 'ShiftAssociatedEmployees'
    },
    shift() {
      return this.details
    },
    employeesAssociated() {
      let { associatedEmployees } = this.shift
      if (!isEmpty(associatedEmployees)) {
        return associatedEmployees
      }
      return 0
    },
    todayDate() {
      return this.formatDateWithoutTime(new Date())
    },
  },
  methods: {
    formatDate(date, exclTime, onlyTime) {
      return this.$options.filters.formatDate(date, exclTime, onlyTime)
    },
    formatDateWithoutTime(date) {
      return this.formatDate(date, true, false)
    },
    formatDateWithOnlyTime(date) {
      return this.formatDate(date, false, true)
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
