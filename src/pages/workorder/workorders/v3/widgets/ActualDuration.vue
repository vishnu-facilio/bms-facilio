<template>
  <div class="col-4 white-bg-block new-sum-scheduled height212px width100">
    <div class="fc__wo__sum__chart_block position-relative">
      <div class="fc-black-13 fw-bold text-left pL20">
        {{ $t('maintenance._workorder.actual') }}
      </div>
    </div>
    <div
      class="row pL20 pR20 pT30"
      v-if="
        (workorder.actualWorkStart && workorder.actualWorkStart > -1) ||
          (workorder.actualWorkEnd && workorder.actualWorkEnd > -1)
      "
    >
      <div class="col-5 n-su-left-cont">
        <div class>
          <div class="fc-text-pink11 text-uppercase">
            {{ $t('common.header.start') }}
          </div>
          <div
            class="fc-black-13 text-left pT10"
            v-if="workorder.actualWorkStart && workorder.actualWorkStart > -1"
          >
            {{ formatDateWithoutTime(workorder.actualWorkStart) }}
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
            v-if="workorder.actualWorkEnd && workorder.actualWorkEnd > -1"
          >
            {{ formatDateWithoutTime(workorder.actualWorkEnd) }}
          </div>
          <div class="fc-black-13 text-left" v-else>
            ---
          </div>
        </div>
      </div>
      <div class="col-2 timer-line">
        <div class="nu-su-timer-icon">
          <i class="el-icon-time"></i>
        </div>
        <div
          class="nu-su-time-line"
          v-bind:class="{
            shnotfinished:
              workorder.actualWorkEnd && workorder.actualWorkEnd < 0,
          }"
        ></div>
        <div
          class="nu-su-timer-icon nu-bottom-time"
          v-bind:class="{
            shnotfinished:
              workorder.actualWorkEnd && workorder.actualWorkEnd < 0,
          }"
        >
          <i class="el-icon-time"></i>
        </div>
      </div>
      <div class="col-5">
        <div
          class="fc-black-22 pT10"
          v-if="workorder.actualWorkStart && workorder.actualWorkStart > -1"
        >
          {{ formatDateWithOnlyTime(workorder.actualWorkStart) }}
        </div>
        <div v-else class="fc-black-22 pT35">---</div>
        <div
          class="fc-black-22 pT35"
          v-if="workorder.actualWorkEnd && workorder.actualWorkEnd > -1"
        >
          {{ formatDateWithOnlyTime(workorder.actualWorkEnd) }}
        </div>
        <div v-else class="fc-black-22 pT35">---</div>
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
  name: 'ActualDuration',
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
