<template>
  <shimmer-loading :config="{ type: 'form' }" v-if="loading"> </shimmer-loading>
  <div class="dragabale-card h100" v-else>
    <div class="h100" v-if="!buildingDetails.loading">
      <div class="h100">
        <div
          class="db-container energy_background energy-card2 h100 energy-card"
        >
          <div class="energy-card-H mobile-energy-card-H">
            {{ $t('panel.card.consumption') }}
          </div>
          <div class="thismonth-section mobile-energy-card">
            <center>
              <div>
                <span
                  class="selfcenter align readings-result-txt mobile-readings-result-txt"
                  >{{ buildingDetails.data.currentVal.consumption || 0 }}</span
                >
                <span class="meter-formula-txt mobile-meter-formula-txt"
                  >{{ buildingDetails.data.currentVal.units }}
                </span>
              </div>
            </center>
            <center class="thismonth fwBold mobile-this-month">
              {{ $t('panel.tyre.month') }}
            </center>
          </div>
          <div
            class="divider-section divider-section-padding mobile-divider-section"
          >
            <div
              class="dividerwhite mobile-divider-white"
              style="opacity: 0.26;height: 1px;"
            ></div>
          </div>
          <div class="lastmonth-section mobile-lastmonth-section">
            <center>
              <div>
                <span
                  class="selfcenter readings-result-txt  mobile-readings-result-txt"
                  >{{ buildingDetails.data.previousVal.consumption || 0 }}</span
                >
                <span class="meter-formula-txt mobile-meter-formula-txt"
                  >{{ buildingDetails.data.previousVal.units }}
                </span>
              </div>
            </center>
            <center style="" class="lastmonth mobile-last-month">
              {{ $t('panel.card.last_month') }}
            </center>
          </div>
          <center
            style="padding-top:35px;"
            class="varience-class mobile-varience-class"
            v-if="Math.round(buildingDetails.data.variance) !== 0"
          >
            <i
              v-bind:class="buildingDetails.arrow"
              aria-hidden="true"
              class="mobile-arrow-down"
              style="color: #fff ; padding-top:10px"
            ></i>
            <span class="varience mobile-varience"
              >{{ Math.round(buildingDetails.data.variance) }} %</span
            >
          </center>
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
  props: ['currentDashboard'],
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
      self.loading = true
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
          self.buildingDetails.data = self.prepareData(reportData)
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
    prepareData(data) {
      if (data) {
        if (data.previousVal.units !== data.currentVal.units) {
          let curentValue = Number(data.currentVal.consumption)
          let preValue = Number(data.previousVal.consumption)
          if (
            data.previousVal.units === 'MWh' &&
            data.currentVal.units === 'kWh'
          ) {
            data.currentVal.consumption = Number(curentValue / 1000).toFixed(1)
            data.currentVal.units = 'MWh'
          } else if (
            data.previousVal.units === 'kWh' &&
            data.currentVal.units === 'MWh'
          ) {
            data.previousVal.consumption = Number(preValue / 1000).toFixed(1)
            data.previousVal.units = 'MWh'
          }
        } else {
          if (data.currentVal && data.previousVal) {
            data.currentVal.consumption = Number(
              data.currentVal.consumption
            ).toFixed(1)
            data.previousVal.consumption = Number(
              data.previousVal.consumption
            ).toFixed(1)
          }
        }
      }
      return data
    },
  },
}
</script>
<style>
.h100 {
  height: 100%;
}
.thismonth {
  font-size: 0.8vw;
  opacity: 0.7;
  letter-spacing: 0.9px;
}
.lastmonth {
  font-size: 0.8vw;
  opacity: 0.7;
  letter-spacing: 0.9px;
  color: #ffffff;
  line-height: 20px;
  font-weight: bold;
}
.varience {
  letter-spacing: 0.4px;
  font-size: 15px;
}
.thismonth-section {
  position: absolute;
  width: 100%;
  top: 23%;
  right: 0;
  left: 0;
}
.energy-card {
  position: relative;
}
.lastmonth-section {
  position: absolute;
  width: 100%;
  top: 55%;
  right: 0;
  left: 0;
}
.divider-section {
  position: absolute;
  width: 100%;
  top: 45%;
  right: 0;
  left: 0;
}
.varience-class {
  padding-top: 15px;
  position: absolute;
  width: 100%;
  top: 75%;
  right: 0;
  left: 0;
  padding-top: 15px;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #fff;
}
.energy-card2 {
  padding: 0 !important;
}
.readings-result-txt {
  align-self: center;
  text-align: center;
  font-size: 2.5vw;
  font-weight: 300;
  letter-spacing: 0px;
  color: #ffffff;
}
.meter-formula-txt {
  font-size: 0.8vw;
  letter-spacing: 0px;
  color: #ffffff;
  font-weight: 400;
}
.divider-section-padding {
  padding: 3% 19% 10px;
}
.energy-card-H {
  font-size: 1vw;
  padding: 8% 8px;
  align-self: center;
  letter-spacing: 1.5px;
  text-align: center;
  text-transform: uppercase;
  font-weight: bold;
}
</style>
