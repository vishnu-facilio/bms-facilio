<template>
  <shimmer-loading v-if="loading" class="card-shimmer"> </shimmer-loading>
  <div
    class="dragabale-card h100 user-layout carbon-weather-container carbon-weather-mini"
    v-else
  >
    <div class="col-12 carbon-weather text-left" style="height:100%;">
      <div class="area uppercase">{{ $t('panel.layout.carbon') }}</div>
      <div>
        <div v-if="data.carbon" class="mT15 text-center">
          <img class="svg-icon sun-icon carbon-icon " src="~statics/co2.svg" />
          <span class="degree" v-if="result.result && result.result.currentVal"
            >{{
              result.result && result.result.currentVal
                ? formatValue(result.result.currentVal)
                : '---'
            }}<span class="sub-text">{{
              formatUnit(result.result.currentVal)
            }}</span></span
          >
        </div>
        <div v-else class="text-center white-color">---</div>
        <center class="thismonth fwBold uppercase" style="padding-top: 10px;">
          {{ getPeriod() }}
        </center>
      </div>
    </div>
  </div>
</template>

<script>
import DateHelper from '@/mixins/DateHelper'
import colors from 'charts/helpers/colors'
import shimmerLoading from '@/ShimmerLoading'
export default {
  props: ['widget', 'config'],
  mixins: [DateHelper],
  data() {
    return {
      enable: {
        10227: 10227,
        10228: 10228,
        10229: 10229,
        10230: 10230,
        10231: 10231,
        10232: 10232,
        10233: 10233,
        10234: 10234,
        10235: 10235,
      },
      data: {
        style: {
          color: 'red',
          bgcolor: 'pink',
        },
        unitString: '',
        workflowV2String: '',
      },
      style: {
        bgcolor: '#fff',
        color: '#000',
      },
      result: null,
      domRerender: true,
      predefineColors: colors.readingcardColors,
      loading: false,
    }
  },
  components: {
    shimmerLoading,
  },
  mounted() {
    this.loadCardData()
  },
  computed: {
    mode() {
      if (
        this.$route.query &&
        this.$route.query.create &&
        this.$route.query.create === 'edit'
      ) {
        return true
      } else if (
        this.$route.query &&
        this.$route.query.create &&
        this.$route.query.create === 'new'
      ) {
        return true
      } else {
        return false
      }
    },
    getScale() {
      return this.$style.responsiveScale(
        200,
        200,
        1,
        this.currentWidth,
        this.currentHeight
      )
    },
    currentWidth() {
      if (this.$el && this.$el.clientWidth) {
        return this.$el.clientWidth
      }
      return 200
    },
    currentHeight() {
      if (this.$el && this.$el.clientHeight) {
        return this.$el.clientHeight
      }
      return 200
    },
    getStyle() {
      return 'background:' + this.style.bgcolor + ';color:' + this.style.color
    },
  },
  methods: {
    formatValue(value) {
      if (value < 999) {
        return value
      } else {
        return (value / 1000).toFixed(2)
      }
    },
    formatUnit(value) {
      if (value < 999) {
        return 'kg'
      } else {
        return 'Tons'
      }
    },
    getPeriod() {
      return this.getdateOperators().find(
        rt => rt.value === this.data.operatorId
      ).label
    },
    refresh() {
      this.updateCard()
    },
    loadCardData() {
      let self = this
      this.getParams()
      let params = null
      if (this.widget && this.widget.id > -1) {
        params = {
          widgetId: self.widget.id,
        }
      } else {
        params = {
          workflow: {
            isV2Script: true,
            workflowV2String: this.data.workflowV2String,
          },
          staticKey: 'kpiCard',
        }
      }

      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.getData(response.data.cardResult)
          self.loading = false
        })
        .catch(function(error) {
          console.log('******** error', error)
          self.loading = false
        })
    },
    updateCard() {
      let self = this
      let params = null
      self.getParams()
      params = {
        workflow: {
          isV2Script: true,
          workflowV2String: this.data.workflowV2String,
        },
        staticKey: 'kpiCard',
      }
      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.getData(response.data.cardResult)
          self.loading = false
        })
        .catch(function(error) {
          console.log('******** error', error)
          self.loading = false
        })
    },
    setColor(mode, color) {
      if (mode === 'bg') {
        this.style.bgcolor = color
      } else if (mode === 'color') {
        this.style.color = color
      }
      this.domRerender = false
      this.domRerender = true
      this.setParams()
    },
    getParams() {
      if (this.widget.dataOptions.data) {
        this.widget.dataOptions.metaJson = JSON.stringify(
          this.widget.dataOptions.data
        )
        this.data = this.widget.dataOptions.data
        this.setParams()
      } else if (this.widget.id > -1 && this.widget.dataOptions.metaJson) {
        this.data = JSON.parse(this.widget.dataOptions.metaJson)
        this.setParams()
      }
    },
    setParams() {
      if (this.data && !this.data.style) {
        this.data.style = {
          color: this.color,
          bgcolor: this.bgcolor,
        }
      } else {
        this.style = this.data.style
      }
      this.widget.dataOptions.metaJson = JSON.stringify(this.data)
    },
    getData(data) {
      if (data.hasOwnProperty('result')) {
        this.result = data
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
