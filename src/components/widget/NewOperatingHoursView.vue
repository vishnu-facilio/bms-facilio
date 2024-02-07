<template>
  <div class="operating-hour-body">
    <div v-if="!(model && model.id)" class="no-operating-hours">
      {{ $t('asset.assets.no_operating_hours') }}
    </div>
    <div v-else class="operating-hour-container">
      <div
        v-for="(operatingDays, index) in singleDaybusinessHoursList"
        :key="index"
        class="operating-hour-single-day hv-center-block mB15"
      >
        <div class="operating-hour-day mb5 font-medium">
          {{ dayOfWeekMap.get(operatingDays.dayOfWeek.toString()) }}
        </div>
        <div
          v-if="model.businessHourTypeVal === 'CUSTOM'"
          class="operating-hour-timing"
        >
          {{ operatingDays.startTime }} - {{ operatingDays.endTime }}
        </div>
        <div v-else class="operating-hour-timing">
          24 {{ $t('space.sites.hours') }}
        </div>
      </div>
    </div>

    <div
      v-if="hasUpdatePermission"
      class="op-hrs-change pointer pT10"
      @click="showOperatingHoursDialog = true"
    >
      <div v-if="model" class="fc-dark-blue4-12">
        {{ $t('space.sites.change_opertaing_hours') }}
      </div>
      <div v-else class="fc-dark-blue4-12">
        {{ $t('space.sites.assign_opertaing_hours') }}
      </div>
    </div>

    <operating-hour-chooser
      v-if="showOperatingHoursDialog"
      :showOperatingHourDialog.sync="showOperatingHoursDialog"
      @businessHourChangeInChooser="onBHChange"
      :resourceid="resourceId"
      :businessHour.sync="model"
      @closeOperatingHourChooser="showOperatingHoursDialog = false"
      :isAssetBh="isAssetBh"
    ></operating-hour-chooser>
  </div>
</template>
<script>
import OperatingHoursView from './OperatingHoursView'
import { isEmpty } from '@facilio/utils/validation'
const dayOfWeekMap = new Map()
dayOfWeekMap.set('1', 'Mon')
dayOfWeekMap.set('2', 'Tue')
dayOfWeekMap.set('3', 'Wed')
dayOfWeekMap.set('4', 'Thu')
dayOfWeekMap.set('5', 'Fri')
dayOfWeekMap.set('6', 'Sat')
dayOfWeekMap.set('7', 'Sun')
export default {
  extends: OperatingHoursView,
  data() {
    return {
      dayOfWeekMap: dayOfWeekMap,
    }
  },
  computed: {
    hasUpdatePermission() {
      return (
        !this.hideChangeBtn &&
        ((!this.isAssetBh && this.$hasPermission('setup:GENERAL')) ||
          (this.isAssetBh && this.$hasPermission('asset:UPDATE')))
      )
    },
    singleDaybusinessHoursList() {
      if (!isEmpty(this.model?.singleDaybusinessHoursList)) {
        return this.model.singleDaybusinessHoursList
      } else if (
        !isEmpty(isEmpty(this.model?.singleDaybusinessHoursListOrDefault))
      ) {
        return this.model.singleDaybusinessHoursListOrDefault
      }
      return []
    },
  },
}
</script>

<style lang="scss" scoped>
.operating-hour-body {
  overflow: scroll;
  .hv-center-block {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  .operating-hour-container {
    display: flex;
    align-content: center;
    flex-wrap: wrap;
    flex-grow: 1;
    .operating-hour-single-day {
      width: 25%;
      padding: 0px 10px;
      margin: 10px 0px;
      border-right: 1px solid #f0f0f0;
      .operating-hour-timing {
        font-size: 11px;
        color: #324056;
        text-align: center;
      }
    }
  }
}
</style>
