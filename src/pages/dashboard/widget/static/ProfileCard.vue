<template>
  <shimmer-loading :config="{ type: 'profile' }" v-if="loading">
  </shimmer-loading>
  <div class="dragabale-card pofilecard" v-else>
    <div class="" v-if="!buildingDetails.loading">
      <div>
        <img :src="buildingDetails.data.avatar" />
        <div class="p10">
          <div class="text-left">
            <div>
              <div class="p4 profile-building-name">
                {{
                  buildingDetails.data.displayName
                    ? buildingDetails.data.displayName
                    : buildingDetails.data.name
                }}
              </div>
              <div class="p4 building-address">
                {{ buildingDetails.data.city }}
              </div>
              <div class="pull-right " style="display:none">
                <router-link to=""
                  ><span class="fc-widget-sublable-color">
                    <i class="fa fa-map-marker" aria-hidden="true"></i
                    >{{ $t('space.sites.view_map') }}</span
                  ></router-link
                >
              </div>
            </div>
            <div>
              <div class="f13 p4 primary-color fw5 building-sq fvw13">
                {{ buildingDetails.data.area }} sqft
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else>{{ $t('panel.card.loading') }}</div>
  </div>
</template>
<script>
import moment from 'moment'
import shimmerLoading from '@/ShimmerLoading'
export default {
  props: ['widget', 'currentDashboard'],
  data() {
    return {
      buildingDetails: {
        loading: false,
        data: [],
      },
      loading: true,
    }
  },
  components: {
    shimmerLoading,
  },
  computed: {
    buildingId() {
      if (this.currentDashboard) {
        if (this.currentDashboard.linkName === 'dsoem1dailyconsumption') {
          return 616227
        }
        return this.currentDashboard.buildingId
      }
      if (this.$route.params.dashboardlink === 'dsoem1dailyconsumption') {
        return 616227
      }
      if (this.$route.params.buildingid) {
        return parseInt(this.$route.params.buildingid)
      }
    },
  },
  mounted() {
    this.loadBuildingDetails()
  },
  methods: {
    loadBuildingDetails() {
      let self = this
      self.buildingDetails.loading = true
      this.loading = true
      const formData = new FormData()
      formData.append('buildingId', self.buildingId)
      self.$http
        .post('/report/energy/building/getBuildingDetails', formData)
        .then(function(response) {
          console.log(
            'dayCOunt',
            moment()
              .endOf('month')
              .format('DD')
          )
          let reportData = response.data.reportData
          self.buildingDetails.data = reportData
          self.buildingDetails.arrow = self.$helpers.arrowHandleClass(
            reportData.variance
          )
          self.purpose = reportData.purpose
          self.loading = false
        })
        .catch(function(error) {
          // self.energyConsumption.loading = false
          self.loading = false
        })
      self.buildingDetails.loading = false
    },
  },
}
</script>
<style>
.pofilecard img {
  width: 100%;
  height: 15vw;
}
.profile-building-name {
  font-size: 1vw;
  font-weight: bold;
  letter-spacing: 1.1px;
  color: #000000;
  text-transform: uppercase;
  text-align: left;
}
.building-address {
  font-size: 0.8vw;
  color: #999999;
  text-align: left;
}
.building-sq {
  font-size: 13px;
  font-weight: 500;
  text-align: left;
  color: #000000;
  padding-top: 5px;
}
</style>
