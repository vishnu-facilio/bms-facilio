<template>
  <div>
    <div v-if="!(model && model.id)" class="no-operating-hours">
      {{ $t('asset.assets.no_operating_hours') }}
    </div>

    <div v-else-if="model.businessHourTypeVal === 'DAYS_24_7'">
      {{ $t('space.sites.mon_sun') }}: 24 {{ $t('space.sites.hours') }}
    </div>

    <div v-else-if="model.businessHourTypeVal === 'DAYS_24_5'">
      <div
        v-for="(operatingDays, index) in model.singleDaybusinessHoursList"
        :key="index"
      >
        <div class="pB10 text-left">
          {{ dayOfWeekMap.get(operatingDays.dayOfWeek.toString()) }}: 24
          {{ $t('space.sites.hours') }}
        </div>
      </div>
    </div>

    <div v-else-if="model.businessHourTypeVal === 'CUSTOM'">
      <div
        v-for="(operatingDays, index) in model.singleDaybusinessHoursList"
        :key="index"
      >
        <div class="pB10 text-left">
          {{ dayOfWeekMap.get(operatingDays.dayOfWeek.toString()) }}:
          {{ operatingDays.startTime }} - {{ operatingDays.endTime }}
        </div>
      </div>
    </div>

    <div
      v-if="
        !hideChangeBtn &&
          ((!isAssetBh && $hasPermission('setup:GENERAL')) ||
            (isAssetBh && $hasPermission('asset:UPDATE')))
      "
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
import OperatingHourChooser from '@/OperatingHourChooser'

export default {
  props: ['model', 'resourceId', 'changeBH', 'hideChangeBtn', 'isAssetBh'],
  components: { OperatingHourChooser },
  data() {
    const businessHourtypeMap = new Map()
    businessHourtypeMap.set('DAYS_24_7', '24 Hours 7 Days')
    businessHourtypeMap.set('DAYS_24_5', '24 Hours 5 Days')
    businessHourtypeMap.set('CUSTOM', 'Custom Hours')

    const customHourtypeMap = new Map()
    customHourtypeMap.set('SAME_TIMING_ALLDAY', 'Same timing all day')
    customHourtypeMap.set('DIFFERENT_TIMING_ALLDAY', 'Different timing all day')

    const dayOfWeekMap = new Map()
    dayOfWeekMap.set('1', 'Monday')
    dayOfWeekMap.set('2', 'Tuesday')
    dayOfWeekMap.set('3', 'Wednesday')
    dayOfWeekMap.set('4', 'Thursday')
    dayOfWeekMap.set('5', 'Friday')
    dayOfWeekMap.set('6', 'Saturday')
    dayOfWeekMap.set('7', 'Sunday')

    return {
      businessHourtypeMap: businessHourtypeMap,
      customHourtypeMap: customHourtypeMap,
      dayOfWeekMap: dayOfWeekMap,
      showOperatingHoursDialog: false,
    }
  },
  methods: {
    onBHChange(businessHour) {
      this.changeBH(businessHour)
      this.showOperatingHoursDialog = false
    },
  },
}
</script>
