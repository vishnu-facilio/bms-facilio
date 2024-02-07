<template>
  <div class="operating-hours-card f12 p20 pT20 d-flex flex-direction-column">
    <div class="header">
      <span class="fc-black-18">{{ $t('asset.assets.operating_hours') }}</span>
      <div
        v-if="businessHour && $hasPermission('setup:GENERAL')"
        class="fc-dark-blue4-12 pointer"
        @click="showOphoursChooser"
      >
        {{ $t('asset.assets.change') }}
      </div>
    </div>
    <operating-hours
      class="f14"
      ref="operating-hours-view"
      :model.sync="businessHour"
      :changeBH="changeBH"
      :resourceId="details.id"
      :hideChangeBtn="!!businessHour"
    ></operating-hours>
  </div>
</template>
<script>
import OperatingHours from '@/widget/NewOperatingHoursView'
import OperatingHoursCard from './OperatingHoursCard.vue'
export default {
  extends: OperatingHoursCard,
  components: {
    OperatingHours,
  },
}
</script>

<style lang="scss" scoped>
.operating-hours-card {
  .fc-black-18 {
    font-size: 18px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: normal;
    color: #324056;
  }
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
  }
}
</style>
