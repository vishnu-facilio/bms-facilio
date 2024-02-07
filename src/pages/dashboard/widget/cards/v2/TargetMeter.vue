<template>
  <div class="dragabale-card height100">
    <shimmer-loading v-if="loading && !hideLoading"></shimmer-loading>
    <div v-else class="p10">
      <div class="p10 target-meter-header">{{ data.title }}</div>
      <gauge-chart
        :widget="widget"
        :config="config"
        :gaugeData="sampleGaugedata"
        id="widget"
        v-if="!loading"
      ></gauge-chart>
    </div>
  </div>
</template>

<script>
import GaugeChart from '@/GaugeChart'
import shimmerLoading from '@/ShimmerLoading'
export default {
  props: ['widget', 'config', 'hideLoading'],
  data() {
    return {
      loading: true,
      data: {
        style: {
          color: 'red',
          bgcolor: 'pink',
        },
        unitString: '',
        workflowV2String: '',
      },
      sampleGaugedata: {
        meta: {
          baseValue: 0,
          targetValue: 0,
          color: '#1ce6e5,#ecb0c8',
        },
        value: {
          needMax: 'usetarget',
          centerText: [
            {
              title: 'SPYKR Main Meter (Energy)',
              label: '273.75',
              unit: ' MWh',
              unitLeft: false,
              enable: true,
            },
            {
              label: 'This Month',
            },
            {
              title: 'Target',
              label: '1000.00  MWh',
            },
            {
              label: 'kWh',
              label2: '1000.00',
            },
          ],
          startColor: '#1ce6e5',
          endColor: '#ecb0c8',
          maxColor: '#532d80',
          enableCenterText1: true,
          parentName1: 'SPYKR Main Meter (Energy)',
          parentName2: '',
        },
        raw: {
          maxConstant: -1,
          fieldName: 'totalEnergyConsumptionDelta',
          aggregateOpperator: 'sum',
          constant: '1000000.00',
          dateOperator: 28,
          fieldName1: -1,
          val1: '273746.15',
          moduleName: 'energydata',
          moduleName1: 'energydata',
          parentId1: -1,
          percent: '27.37',
          parentId: 908699,
          unit: {
            displayName: 'kilowatt-hour',
            fromSiUnit: null,
            isLeft: false,
            metric: {
              decimalPoints: 2,
              metricId: 1,
              name: 'Energy',
              siUnitId: 1,
              _name: 'ENERGY',
            },
            siUnit: true,
            symbol: 'kWh',
            toSiUnit: null,
            unitId: 1,
            _name: 'KWH',
          },
          maxPercentage: -1,
          aggregateOpperator1: 'sum',
        },
      },
      result: null,
    }
  },
  mounted() {
    this.loadCardData()
  },
  components: {
    GaugeChart,
    shimmerLoading,
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
        .catch(function() {
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
        .catch(function() {
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
    formattedResult(result) {
      if (
        result &&
        Object.keys(result) &&
        Object.keys(result).length &&
        result.hasOwnProperty('unit')
      ) {
        if (
          this.$convert()
            .possibilities()
            .find(rt => rt === result.unit)
        ) {
          let obj = this.$convert(Number(result.value))
            .from(result.unit)
            .toBest()
          return `${Number(obj.val).toFixed(2)} ${obj.unit}`
        } else {
          return `${Number(result.value).toFixed(2)} ${result.unit}`
        }
      } else {
        return result
      }
    },
    getData(data) {
      if (data.hasOwnProperty('result')) {
        this.result = this.formattedResult(data)
      }
      this.sampleGaugedata.meta = this.formattedResult(data.result)
      // this.sampleGaugedata.meta.colorPlates = [
      //   {
      //     startAngle: 0,
      //     endAngle: 0.1,
      //     color: '#18c1c8',
      //   },
      //   {
      //     startAngle: 0.1,
      //     endAngle: 0.3,
      //     color: '#65ece9',
      //   },
      //   {
      //     startAngle: 0.3,
      //     endAngle: 0.45,
      //     color: '#8dcbb6',
      //   },
      //   {
      //     startAngle: 0.45,
      //     endAngle: 0.8,
      //     color: 'red',
      //   },
      //   {
      //     startAngle: 0.8,
      //     endAngle: 1,
      //     color: '#78b7b5',
      //   },
      // ]
    },
  },
}
</script>
<style>
.target-meter-header {
  font-size: 0.97vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.33;
  letter-spacing: normal;
  color: rgba(0, 0, 0, 0.5);
  text-align: left;
}
.target-meter-header text.gauge-center-period {
  fill: #6d7278;
}
</style>
