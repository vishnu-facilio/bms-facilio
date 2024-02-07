<template>
  <div class="pm-tigger-dialog-form">
    <div
      class="pm-tigger-dialog-form-left"
      :class="{
        'triger-right-side-width': !(
          trigger.type === 1 && trigger.schedule.frequencyType !== 0
        ),
      }"
    >
      <div class="label-txt-black text-uppercase fwBold trigger-header-left">
        {{ $t('maintenance._workorder.trigger') }}
      </div>
      <div class="pL30 pR30">
        <schedule-trigger
          v-if="trigger.type === 1"
          :trigger="trigger"
          :canShowCustomFrequency="true"
          :fromPage="'pmtrigger'"
        ></schedule-trigger>
      </div>
    </div>
    <template v-if="trigger.type === 1 && trigger.schedule.frequencyType !== 0">
      <div
        class="pm-tigger-dialog-form-right"
        :class="{
          'triger-right-side-hide': !(
            trigger.type === 1 && trigger.schedule.frequencyType !== 0
          ),
        }"
      >
        <div>
          <div class="flex-middle pm-trigger-left-header">
            <InlineSvg
              src="svgs/schedule"
              iconClass="icon icon-md new-icon"
            ></InlineSvg>
            <div class="label-txt-black fwBold mL10">
              {{ $t('maintenance.pm.next_ten', { totalSchedules }) }}
            </div>
          </div>
          <div class="pR30 pL30 pT20">
            <scheduled-dates
              :trigger="trigger"
              @setTotalSchedules="setTotalSchedules"
            ></scheduled-dates>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import Trigger from '@/PMTrigger'
//import { API } from '@facilio/api'

export default {
  name: 'Trigger',
  extends: Trigger,
  props: ['pmProps'],
  data: () => ({ totalSchedules: 10 }),
  methods: {
    setTotalSchedules(schedules) {
      this.$set(this, 'totalSchedules', schedules)
    },
  },

  // Will Enable in few days hence commenting
  // data: () => ({
  //   availableFrequency: [],
  // }),
  // created() {
  //   this.loadAvailableFrequency()
  // },
  // methods: {
  //   async loadAvailableFrequency() {
  //     let { pmProps } = this
  //     let { id: pmId } = pmProps || {}
  //     let url = `v3/plannedmaintenance/getTriggerFrequencyList?pmId=${pmId}`
  //     let { error, data } = await API.get(url)
  //     if (!error) this.availableFrequency = data
  //   },
  // },
}
</script>
