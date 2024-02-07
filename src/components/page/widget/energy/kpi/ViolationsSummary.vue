<template>
  <div class="p30 d-flex">
    <div class="d-flex flex-direction-row" style="height: 100%">
      <div class="violations-section border-right3">
        <div class="f13 bold fc-black-13 text-uppercase text-center">
          KPI Violations
        </div>
        <template v-if="!$validation.isEmpty(violationsThisMonth)">
          <div class="f45 fc-black-com mT20 text-center">
            {{ violationsThisMonth }}
          </div>
          <div class="mT10 f12 bold fc-black-com text-capitalize text-center">
            {{ $t('common.date_picker.this_month') }}
          </div>
        </template>
        <div v-else class="pT50 text-center empty-grey-desc2">
          No violations found
        </div>
      </div>
      <div class="violations-section">
        <div class="f13 bold fc-black-13 text-uppercase text-center">
          KPI Violations
        </div>
        <template v-if="!$validation.isEmpty(violationsLastMonth)">
          <div class="f45 fc-black-com mT20 text-center">
            {{ violationsLastMonth }}
          </div>
          <div class="mT10 f12 bold fc-black-com text-capitalize text-center">
            Last Month
          </div>
        </template>
        <div v-else class="pT50 text-center empty-grey-desc2">
          No violations found
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  components: {},
  props: ['details', 'widget', 'resizeWidget', 'moduleName'],
  data() {
    return {
      violationsLastMonth: 0,
      violationsThisMonth: 0,
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$util
        .getWorkFlowResult(7, [[this.details.id]])
        .then(data => {
          let obj = data[this.details.id]
          let { thisMonth = 0, lastMonth = 0 } = obj || {}

          this.$set(this, 'violationsThisMonth', thisMonth)
          this.$set(this, 'violationsLastMonth', lastMonth)
        })
        .catch(() => {
          this.$message.error('Could not fetch violations count.')
        })
    },
  },
}
</script>
<style lang="scss" scoped>
.violations-section {
  flex-basis: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
</style>
