<template>
  <div v-if="loading">
    <spinner :show="true"></spinner>
  </div>
  <div
    v-else-if="noData"
    class="d-flex justify-content-center flex-direction-column align-center"
  >
    <div>
      <inline-svg
        src="svgs/cardNodata"
        class="vertical-middle'"
        iconClass="icon icon-80"
      ></inline-svg>
    </div>
    <div class="fc-black3-16 self-center bold">
      No Occupancy Data
    </div>
  </div>
  <div v-else class="p30 occupancy-card">
    <div class="header-text">
      Occupancy
    </div>
    <div>
      <el-progress
        :percentage="occupancyPercentage"
        :show-text="false"
        :color="customColors"
        :stroke-width="10"
        class="occupancy-progress-bar mT20"
      ></el-progress>
      <div class="f14 fc-black-color mT10">
        {{
          `${assignedOccupancy > 0 ? assignedOccupancy : '---'} / ${
            maxOccupancy > 0 ? maxOccupancy : '---'
          }`
        }}
      </div>
    </div>
  </div>
</template>
<script>
import { eventBus } from '../utils/eventBus'
import { isNumber, isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details'],
  data() {
    return {
      assignedOccupancy: 0,
      loading: false,
    }
  },
  computed: {
    maxOccupancy() {
      return this.details.maxOccupancy
    },
    noData() {
      let { maxOccupancy, assignedOccupancy } = this
      if (isEmpty(maxOccupancy) || isEmpty(assignedOccupancy)) {
        return true
      }
      return false
    },
    occupancyPercentage() {
      let { maxOccupancy, assignedOccupancy } = this
      if (!isEmpty(maxOccupancy) && !isEmpty(assignedOccupancy)) {
        let percentage = (assignedOccupancy / maxOccupancy) * 100
        return percentage > 100 ? 100 : percentage
      }
      return 0
    },
    customColors() {
      return '#835af8'
    },
  },
  created() {
    this.loading = true
    eventBus.$on('occupancyCount', count => {
      this.$set(this, 'assignedOccupancy', count)
      this.loading = false
    })
  },
}
</script>
