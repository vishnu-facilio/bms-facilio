<template>
  <div
    class="wp-analytics-leagend flex width100 fc__layout_media_center justify-content-center p20"
    v-if="!loading"
  >
    <div class="p10 pR20">
      <div class="wp-l-value">{{ cardData.vacant }}</div>
      <div class="wp-l-key">{{ $t('floorplan.vacant_desks') }}</div>
    </div>
    <div class="p10 pR20">
      <div class="wp-l-value">{{ cardData.occupied }}</div>
      <div class="wp-l-key">{{ $t('floorplan.occupied_desks') }}</div>
    </div>
    <div class="p10 pR20">
      <div class="wp-l-value">
        {{ cardData.occupied | percentage(cardData.deskCount) }}
      </div>
      <div class="wp-l-key">{{ $t('floorplan.occupiedPercentage') }}</div>
    </div>
  </div>
</template>

<script>
import WorkplaceAnalyticsStatusCard from 'src/pages/workplaceAnalytics/WorkplaceAnalyticsStatusCard.vue'
export default {
  extends: WorkplaceAnalyticsStatusCard,
  computed: {
    leagendData() {
      let newObj = {}
      let obj = this.cardData

      Object.keys(obj)
        .sort()
        .reverse()
        .forEach(key => {
          newObj[key] = obj[key]
        })

      return newObj
    },
  },
}
</script>

<style>
.wp-l-value {
  font-size: 24px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
  text-align: center;
}
.wp-l-key {
  font-size: 16px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #6b7e91;
}
</style>
