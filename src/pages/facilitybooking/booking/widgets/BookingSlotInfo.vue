<template>
  <div class="p30" ref="container">
    <div
      v-if="$validation.isEmpty(details.slotList)"
      class="d-flex justify-content-center flex-direction-column align-center"
    >
      <div>
        <inline-svg
          src="svgs/emptystate/commonempty"
          class="vertical-middle'"
          iconClass="icon icon-130"
        ></inline-svg>
      </div>
      <div class="fc-black-dark f18 self-center bold">
        No Slots Booked
      </div>
    </div>
    <table v-else class="facility-summary-table width100">
      <thead>
        <tr>
          <th class="width200px">START TIME</th>
          <th class="width200px">END TIME</th>
          <th v-if="!$validation.isEmpty(details.bookingAmount)">COST</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(slot, index) in details.slotList" :key="index">
          <td>
            {{
              $helpers.formatMillistoHHMM(
                $getProperty(slot, 'slot.slotStartTime')
              )
            }}
          </td>
          <td>
            {{
              $helpers.formatMillistoHHMM(
                $getProperty(slot, 'slot.slotEndTime')
              )
            }}
          </td>
          <td v-if="!$validation.isEmpty(details.bookingAmount)">
            <currency
              :value="$getProperty(slot, 'slot.slotCost') || 0"
            ></currency>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details', 'resizeWidget', 'calculateDimensions'],
  mounted() {
    this.autoResize()
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['container'])) {
          let height = this.$refs['container'].scrollHeight + 30
          let width = this.$refs['container'].scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
  },
}
</script>
