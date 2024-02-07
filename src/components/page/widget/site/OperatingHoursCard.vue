<template>
  <div class="f12 p30 d-flex flex-direction-column">
    <div class="d-flex">
      <span class="fc-blue-label mB15 mR10">{{
        $t('asset.assets.operating_hours')
      }}</span>
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
import OperatingHours from '@/widget/OperatingHoursView'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details'],
  components: {
    OperatingHours,
  },
  data() {
    return { businessHour: null }
  },
  mounted() {
    if (this.details && this.details.operatingHour) {
      this.getOperatingHours()
    }
  },
  methods: {
    getOperatingHours() {
      return this.$http.get('/v2/businesshours/list').then(response => {
        if (response.data.responseCode === 0) {
          let { result } = response.data
          let list = !isEmpty(result.list) ? result.list : []

          this.businessHour = list.find(
            businessHour => businessHour.id === this.details.operatingHour
          )
        }
      })
    },
    changeBH(businesshour) {
      this.businessHour = businesshour
    },
    showOphoursChooser() {
      this.$refs['operating-hours-view'].showOperatingHoursDialog = true
    },
  },
}
</script>
