<template>
  <div class="col-4 white-bg-block new-sum-scheduled height212px width100">
    <div class="fc__wo__sum__chart_block position-relative">
      <div class="fc-black-13 fw-bold text-left pL20">
        {{ $t('maintenance.pm_list.scheduled') }}
      </div>
    </div>
    <div
      class="row pL20 pR20 pT30"
      v-if="
        (workorder.scheduledStart && workorder.scheduledStart > -1) ||
          (workorder.estimatedEnd && workorder.estimatedEnd > -1)
      "
    >
      <div class="col-6 n-su-left-cont">
        <div class>
          <div class="fc-text-pink11 text-uppercase">
            {{ $t('common.header.start') }}
          </div>
          <div
            class="fc-black-13 text-left pT10"
            v-if="workorder.scheduledStart && workorder.scheduledStart > -1"
          >
            {{ formatDateWithoutTime(workorder.scheduledStart) }}
          </div>
          <div class="fc-black-13 text-left pT10" v-else>
            ---
          </div>
        </div>
        <div class="mT40">
          <div class="fc-text-pink11 text-uppercase">
            {{ $t('maintenance._workorder.finish') }}
          </div>
          <div
            class="fc-black-13 text-left"
            v-if="workorder.estimatedEnd && workorder.estimatedEnd > -1"
          >
            {{ formatDateWithoutTime(workorder.estimatedEnd) }}
          </div>
          <div
            class="fc-black-13 text-left"
            v-else-if="workorder.dueDate && workorder.dueDate > -1"
          >
            {{ formatDateWithoutTime(workorder.dueDate) }}
          </div>
          <div class="fc-black-13 text-left" v-else>
            ---
          </div>
        </div>
      </div>
      <div class="col-6">
        <div
          class="fc-black-22 pT10"
          v-if="workorder.scheduledStart && workorder.scheduledStart > -1"
        >
          {{ formatDateWithOnlyTime(workorder.scheduledStart) }}
        </div>
        <div v-else class="fc-black-22 pT10">---</div>
        <div
          class="fc-black-22 mT40"
          v-if="workorder.estimatedEnd && workorder.estimatedEnd > -1"
        >
          {{ formatDateWithOnlyTime(workorder.estimatedEnd) }}
        </div>
        <div
          class="fc-black-22 mT40"
          v-else-if="workorder.dueDate && workorder.dueDate > -1"
        >
          {{ formatDateWithOnlyTime(workorder.dueDate) }}
        </div>
        <div v-else class="fc-black-22 mT40">---</div>
      </div>
    </div>
    <div v-else>
      <div class="text-center fc-black-14 pT60 pB60">
        {{ $t('common._common.nodata') }}
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'ScheduledDuration',
  props: ['details'],
  computed: {
    workorder() {
      return this.details.workorder
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

<style></style>
