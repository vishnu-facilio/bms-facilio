<template>
  <shimmer-loading v-if="loading" class="card-shimmer"> </shimmer-loading>
  <div
    class="dragabale-card h100 user-layout carbon-weather-container carbon-weather-mini"
    v-else
  >
    <div class="col-12 carbon-weather text-left" style="height:100%;">
      <div class="area uppercase">{{ $t('panel.tyre.emissions') }}</div>
      <div>
        <div v-if="data.carbon" class="mT15 text-center">
          <img class="svg-icon sun-icon carbon-icon " src="~statics/co2.svg" />
          <span class="degree" v-if="data.carbon"
            >{{ data.carbon.value
            }}<span class="sub-text">{{ data.carbon.unit }}</span></span
          >
        </div>
        <div v-else class="text-center white-color">---</div>
        <center class="thismonth fwBold" style="padding-top: 10px;">
          {{ $t('panel.tyre.month') }}
        </center>
      </div>
    </div>
  </div>
</template>

<script>
import shimmerLoading from '@/ShimmerLoading'
export default {
  props: ['widget', 'currentDashboard'],
  data() {
    return {
      loading: false,
      data: [],
      weatherBg: 'clear-night',
      demoMode: false,
      building: null,
      buildingDetails: {
        loading: true,
        data: [],
      },
      wloading: true,
    }
  },
  components: {
    shimmerLoading,
  },
  mounted() {
    this.initData()
  },
  computed: {
    buildingId() {
      return this.building ? this.building : null
    },
  },
  methods: {
    initData() {
      let self = this
      self.loading = true
      let params = null
      if (this.widget.dataOptions.building) {
        this.building = this.widget.dataOptions.building
        params = {
          staticKey: 'carbonmini',
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
          self.data.carbon = self.prepareEmission(
            response.data.cardResult.carbonEmission
          )
          self.loading = false
        })
        .catch(function(error) {
          self.loading = false
        })
    },
    prepareEmission(data) {
      if (data > 1000) {
        let d = {}
        d.value = (data / 1000).toFixed(2)
        d.unit = ' Tons'
        return d
      } else {
        let d = {}
        d.value = data.toFixed(2)
        d.unit = ' Kg'
        return d
      }
    },
  },
}
</script>
<style>
.user-layout .el-icon-close:before {
  content: '';
}
.setting-header .add-btn {
  position: fixed;
  right: 0;
  z-index: 111;
}
.user-layout .setting-page-btn {
  top: 7.5rem !important;
}
.user-layout .setting-Rlayout {
  padding: 1rem 1.7rem !important;
}
.add-btn {
  position: relative;
  right: 21px;
  top: -47px;
  z-index: 1111;
}
.h100 {
  height: 100% !important;
}
.carbon-weather-mini .carbon-icon {
  width: 15%;
}
.carbon-weather-container {
  top: 0%;
}
.carbon-weather-mini .carbon-weather {
  position: relative;
  width: 100%;
  top: 0%;
}
.carbon-weather-mini .degree {
  font-size: 2.5rem;
  color: #fff;
  margin-left: 10px;
  font-weight: 200 !important;
}
.carbon-weather-mini .area.uppercase {
  font-size: 1rem;
  color: #fff;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-align: center;
  font-weight: 600;
  padding-top: 20px;
}
.carbon-weather-mini .sub-text {
  font-size: 1.2rem;
  padding-left: 8px;
  letter-spacing: 1.5px;
  font-weight: 500;
}
.carbon-weather-mini .thismonth {
  color: #fff;
  text-align: center;
}
</style>
