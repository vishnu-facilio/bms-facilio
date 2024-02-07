<template>
  <div class="dragabale-card height100">
    <shimmer-loading v-if="loading" class="map-shimmer"> </shimmer-loading>
    <div v-else class="height100">
      <div class="map-select-box"></div>
      <f-map
        v-if="result && result.length"
        :markers="result"
        :data="data"
      ></f-map>
    </div>
  </div>
</template>
<script>
import DashboardFilterMixin from 'pages/dashboard/mixins/DashboardFilters'
import DateHelper from '@/mixins/DateHelper'
import colors from 'charts/helpers/colors'
import shimmerLoading from '@/ShimmerLoading'
import FMap from '@/FSmartMap'
import FMAPMixin from '@/mixins/FMAPMixin'
import * as d3 from 'd3'
export default {
  props: ['widget', 'config'],
  mixins: [DateHelper, DashboardFilterMixin, FMAPMixin],
  data() {
    return {
      backupdata: [],
      gaugeData: {
        meta: {
          baseValue: 0,
          targetValue: 0,
          color: '#1ce6e5,#ecb0c8',
        },
        value: {
          needMax: 'usetarget',
        },
      },
      data: {
        style: {
          color: 'red',
          bgcolor: 'pink',
        },
        unitString: '',
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
  mounted() {
    this.loadCardData()
  },
  components: {
    shimmerLoading,
    FMap,
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
  watch: {
    dashboardDateFilter: {
      handler(newData, oldData) {
        if (newData) {
          console.log('KFI  card ,Dashboard filter changed')
          this.refresh()
        }
      },
    },
  },
  methods: {
    refresh() {
      this.updateCard()
    },
    loadCardData() {
      this.getParams()
      console.log('******** new map data', this.data)
      this.loadWorkflow()
    },
    loadWorkflow() {
      let self = this
      this.$util.getWorkFlowResult(107, [this.data]).then(response => {
        if (response.locations) {
          self.perparedata(response.locations)
        }
      })
    },
    perparedata(data) {
      let self = this
      if (this.data.module === 'asset') {
        data.forEach(rt => {
          let geoLocation = rt.location
          rt.location = {
            lat: Number(geoLocation.split(',')[0]),
            lng: Number(geoLocation.split(',')[1]),
          }
          rt.count = rt.markerValue
        })
        let assetCountMap = data.map(rt => rt.markerValue)
        let min = Math.min(...assetCountMap)
        let max = Math.max(...assetCountMap)
        let opacityScale = d3
          .scaleLinear()
          .range([0, 0.5])
          .domain([min, max])
        data.forEach(rt => {
          rt.opacity = opacityScale(rt.count) + 0.5
          rt.markerUrl = self.getMapMarkers(rt, 1)
        })
        this.result = this.$helpers.cloneObject(data)
        this.backupdata = this.$helpers.cloneObject(data)
      } else {
        data.forEach(rt => {
          rt.count = rt.markerValue
        })
        let assetCountMap = data.map(rt => rt.markerValue)
        let min = Math.min(...assetCountMap)
        let max = Math.max(...assetCountMap)
        let opacityScale = d3
          .scaleLinear()
          .range([0, 0.5])
          .domain([min, max])
        data.forEach(rt => {
          rt.opacity = opacityScale(rt.count) + 0.5
          rt.markerUrl = self.getMapMarkers(rt, 1)
        })
        this.result = this.$helpers.cloneObject(data)
        this.backupdata = this.$helpers.cloneObject(data)
      }
    },
    updateCard() {
      let self = this
      let params = null
      params = {
        workflow: {
          expressions:
            this.widget.dataOptions.data &&
            this.widget.dataOptions.data.expressions
              ? this.widget.dataOptions.data.expressions
              : [],
          workflowUIMode: 1,
        },
        staticKey: 'kpiCard',
      }

      this.loading = true
      self.getParams()

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
      } else if (this.widget.id > -1 && this.widget.dataOptions.metaJson) {
        this.data = JSON.parse(this.widget.dataOptions.metaJson)
      }
      if (
        this.widget.hasOwnProperty('widgetVsWorkflowContexts') &&
        this.widget.widgetVsWorkflowContexts.length &&
        this.widget.widgetVsWorkflowContexts[0].workflow
      ) {
        this.data[
          'v2Script'
        ] = this.widget.widgetVsWorkflowContexts[0].workflow['v2Script']
        this.data[
          'workflowV2String'
        ] = this.widget.widgetVsWorkflowContexts[0].workflow['workflowV2String']
      }
      this.setParams()
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
        this.gaugeData = this.resultToGaugeData(data)
      } else {
        this.gaugeData = {}
      }
    },
    formatData(result) {
      if (result && result.result) {
        if (result.result.value) {
          return Number(result.result.value)
        }
        return Number(result.result)
      }
      return result
    },
    getUnit(result) {
      if (result && result.result) {
        if (result.result.unit) {
          return result.result.unit
        }
        return ''
      }
      return ''
    },
    resultToGaugeData(data) {
      if (data) {
        return {
          meta: {
            baseValue: this.formatData(data),
            targetValue: this.data.targetConstant || 0,
            color: '#1ce6e5,#ecb0c8',
            fieldObj: this.data.fieldObj || {
              unit: this.getUnit(data),
            },
          },
          value: {
            needMax: 'usetarget',
          },
        }
      } else {
        this.gaugeData
      }
    },
  },
}
</script>
<style>
.kpi-sections {
  font-size: 14px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.36;
  letter-spacing: 1.5px;
  text-align: center;
}

.kpi-period {
  font-size: 12px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.3px;
  text-align: center;
  /* color: #ffffff; */
  text-transform: uppercase;
  opacity: 0.6;
}
.kpi-container:hover .color-choose-icon {
  display: block !important;
}
</style>
