<template>
  <shimmer-loading v-if="loading" class="card-shimmer"> </shimmer-loading>
  <div class="dragabale-card h100 energy-mini" v-else>
    <div class="h100">
      <div class="h100">
        <div
          class="db-container energy_background energy-card2 h100 energy-card"
        >
          <div class="energy-card-H">{{ $t('panel.layout.e_cost') }}</div>
          <div class="thismonth-section">
            <center>
              <div class="energy-card-readigdata">
                <span
                  class="meter-formula-txt pR5 card-min-unit"
                  v-if="$currency.includes('$')"
                  >{{ $currency }}</span
                >
                <span
                  class="selfcenter align readings-result-txt"
                  :style="getFontcolor(buildingDetails.data.currentMonth)"
                  >{{ buildingDetails.data.currentMonth || '---' }}</span
                >
                <span
                  class="meter-formula-txt pL5 card-min-unit"
                  v-if="!$currency.includes('$')"
                  >{{ $currency }}</span
                >
              </div>
            </center>
            <center class="thismonth fwBold" style="padding-top: 10px;">
              {{ $t('panel.tyre.month') }}
            </center>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import moment from 'moment'
import * as d3 from 'd3'
import shimmerLoading from '@/ShimmerLoading'
export default {
  props: ['currentDashboard', 'widget'],
  data() {
    return {
      building: null,
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
      return this.building ? this.building : null
    },
  },
  mounted() {
    this.loadBuildingDetails()
  },
  methods: {
    loadBuildingDetails() {
      let self = this
      self.loading = true
      let params = null
      if (this.widget.dataOptions.building) {
        this.building = this.widget.dataOptions.building
        params = {
          staticKey: 'energycostmini',
          baseSpaceId: this.building.id,
        }
      } else {
        params = {
          widgetId: self.widget.id,
        }
      }
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.buildingDetails.data = self.prepareData(response.data.cardResult)
          self.loading = false
        })
        .catch(() => {})
      self.loading = false
    },
    getFontcolor(title) {
      if (title) {
        let font = 2
        if (title.length < 7) {
          font = 2.4
        } else if (title.length < 8) {
          font = 2.2
        } else if (title.length < 12) {
          font = 2
        } else if (title.length > 12) {
          font = 1.8
        }
        return 'font-size:' + font + 'rem'
      }
    },
    prepareData(data) {
      let d = {}
      if (data.currentMonth) {
        data.currentMonth = parseInt(data.currentMonth)
        d.currentMonth = d3.format(',')(data.currentMonth)
      }
      return d
    },
  },
}
</script>
<style>
.h100 {
  height: 100%;
}
.thismonth {
  font-size: 0.9vw;
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
  font-size: 1.1vw;
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
  font-size: 1.1vw;
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
  font-weight: 600;
}
.divider-section-padding {
  padding: 3% 19% 10px;
}
.energy-card-H {
  font-size: 1vw;
  align-self: center;
  letter-spacing: 1.5px;
  text-align: center;
  text-transform: uppercase;
  font-weight: bold;
  padding-top: 20px;
}
/**/
.energy-mini .readings-result-txt {
  font-size: 2.5vw;
}
.energy-card-readigdata {
  padding-top: 5%;
}
@media only screen and (min-width: 1800px) {
  .energy-card2 .energy-card-H {
    padding-top: 40px !important;
  }
  .energy-card-readigdata {
    padding-top: 8%;
  }
}
.card-min-unit {
  padding-bottom: 25px;
  position: relative;
  bottom: 5px;
  font-size: 25px;
}
</style>
