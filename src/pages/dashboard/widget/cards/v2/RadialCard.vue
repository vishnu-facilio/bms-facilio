<template>
  <div class="d-flex flex-direction-column justify-center" v-if="loading">
    <spinner :show="loading"></spinner>
  </div>
  <div v-else class="radial-gauge dragabale-card fc-widget height-100">
    <div class="pT10 pB10 title text-left">
      <div class="f16 bold letter-spacing0_4 text-left pL15">
        {{ data.title }}
      </div>
    </div>
    <div class="mT15">
      <canvas :ref="`canvasGauges-linear-${fieldObj.fieldId}`"></canvas>
    </div>
  </div>
</template>
<script>
import { RadialGauge } from 'canvas-gauges'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['widget', 'config'],
  data() {
    return {
      loading: false,
      result: null,
      data: {
        style: {
          color: 'red',
          bgcolor: 'pink',
        },
        unitString: '',
        workflowV2String: '',
      },
      radialGaugeChart: null,
      options: {
        renderTo: '',
        width: 210,
        height: 210,
        minValue: null,
        maxValue: null,
        value: null,
        units: null,
        minorTicks: null,
        majorTicks: [],
        highlights: [],
        strokeTicks: true,
        colorStrokeTicks: '#fff',
        colorMajorTicks: ['#000'],
        colorMinorTicks: '#000',
        colorTitle: '#324056',
        colorUnits: '#324056',
        colorNumbers: '#324056',
        borderRadius: 0,
        borders: false,
        needleCircleInner: false,
        needleType: 'arrow',
        needleWidth: 2,
        needleCircleSize: 7,
        barStrokeWidth: 0,
        colorValueBoxShadow: false,
        colorValueBoxBackground: '#fff',
        colorValueText: '#324056',
        valueBoxStroke: 0,
        valueBoxWidth: 10,
        valueTextShadow: false,
        valueInt: 1,
        animationDuration: 1500,
        animationRule: 'bounce',
        animateOnInit: true,
      },
    }
  },
  computed: {
    fieldObj() {
      let { result } = this
      let { fieldObj } = result || {}
      return fieldObj || {}
    },
  },
  mounted() {
    this.getParams()
    this.loadCardData().then(() => {
      let { $refs, fieldObj } = this
      this.$nextTick(() => {
        if (!isEmpty($refs)) {
          let canvasElement = $refs[`canvasGauges-linear-${fieldObj.fieldId}`]
          this.$set(this.options, 'renderTo', canvasElement)
          this.radialGaugeChart = new RadialGauge(this.options).draw()
        }
      })
    })
  },
  methods: {
    refresh() {
      this.updateCard()
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
      let { gaugeMeta } = this.data
      if (this.data && !this.data.style) {
        this.data.style = {
          color: this.color,
          bgcolor: this.bgcolor,
        }
      } else {
        this.style = this.data.style
      }
      this.widget.dataOptions.metaJson = JSON.stringify(this.data)
      this.$set(this.options, 'minorTicks', gaugeMeta.minorTicks)
      this.$set(this.options, 'majorTicks', gaugeMeta.majorTicks)
      this.$set(this.options, 'highlights', gaugeMeta.highlights)
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
    loadCardData() {
      let self = this
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
      return self.$http
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
    getData(data) {
      if (data.hasOwnProperty('result')) {
        this.result = data.result
        let { radialGaugeChart } = this
        this.$set(this.options, 'minValue', Number(this.result.minValue))
        this.$set(this.options, 'maxValue', Number(this.result.maxValue))
        this.$set(this.options, 'value', Number(this.result.lastValue))
        this.$set(this.options, 'units', this.result.fieldObj.unit)
        radialGaugeChart.update(this.options)
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.radial-gauge {
  .title {
    font-size: 1.1vw;
    font-weight: 500;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.5px;
    text-align: left;
    color: #2f2e49;
    padding-bottom: 0px;
    border-bottom: solid 1px #f0f4f6;
  }
}
</style>
