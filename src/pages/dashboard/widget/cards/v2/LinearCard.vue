<template>
  <div class="d-flex flex-direction-column justify-center" v-if="loading">
    <spinner :show="loading"></spinner>
  </div>
  <div v-else class="linear-gauge dragabale-card fc-widget height-100">
    <div class="pT10 pB10 title text-left">
      <div class="f16 bold letter-spacing0_4 text-left pL15">
        {{ data.title }}
      </div>
    </div>
    <div class="mT15 d-flex">
      <canvas :ref="`canvasGauges-linear-${fieldObj.fieldId}`"></canvas>
      <div class="d-flex flex-direction-column mL-auto pR25 justify-start">
        <div v-if="!$validation.isEmpty(this.result)" class="pB20">
          <div class="text-right f12 letter-spacing0_4 fc-black-color bold">
            {{ $t('panel.layout.latest') }}
          </div>
          <div
            class="text-right f18 pT5 fc-black-color value-contianer fwBold value-contianer"
          >
            {{ getFormattedValue(this.result.lastValue) }}
            <span class="f12">{{ fieldUnit }}</span>
          </div>
        </div>
        <div v-if="!$validation.isEmpty(this.result)" class="pB20">
          <div class="text-right f12 letter-spacing0_4 fc-black-color bold">
            {{ $t('panel.layout.max') }}
          </div>
          <div
            class="text-right f18 pT5 fc-black-color value-contianer fwBold value-contianer"
          >
            {{ getFormattedValue(this.result.max) }}
            <span class="f12">{{ fieldUnit }}</span>
          </div>
        </div>
        <div v-if="!$validation.isEmpty(this.result)" class="pB20">
          <div class="text-right f12 letter-spacing0_4 fc-black-color bold">
            {{ $t('panel.layout.min') }}
          </div>
          <div
            class="text-right f18 pT5 fc-black-color value-contianer fwBold value-contianer"
          >
            {{ getFormattedValue(this.result.min) }}
            <span class="f12">{{ fieldUnit }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { LinearGauge } from 'canvas-gauges'
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
      linearGaugeChart: null,
      options: {
        renderTo: '',
        width: 150,
        height: 230,
        minValue: null,
        maxValue: null,
        value: null,
        minorTicks: null,
        majorTicks: [],
        strokeTicks: true,
        colorStrokeTicks: '#fff',
        colorMajorTicks: ['#000'],
        colorMinorTicks: '#324056',
        colorPlate: '#fff',
        borderShadowWidth: 0,
        barBeginCircle: 25,
        barWidth: 15,
        colorBar: '#F7F4F4',
        colorBarProgress: '#3adaad',
        colorBarProgressEnd: '#0232ab',
        barStrokeWidth: 0,
        type: 'linearGauge',
        tickSide: 'right',
        numberSide: 'right',
        needleSide: 'right',
        ticksWidth: 13,
        ticksWidthMinor: 8,
        ticksPadding: 3,
        fontNumbersSize: 18,
        borders: 0,
        needleType: 'arrow',
        needleWidth: 12,
        animationDuration: 1500,
        animationRule: 'bounce',
        animateOnInit: true,
        units: false,
        valueBox: false,
      },
    }
  },
  computed: {
    fieldObj() {
      let { result } = this
      let { fieldObj } = result || {}
      return fieldObj || {}
    },
    fieldUnit() {
      let { fieldObj } = this
      return fieldObj.unit ? fieldObj.unit : ''
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
          this.linearGaugeChart = new LinearGauge(this.options).draw()
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
        let { linearGaugeChart } = this
        this.$set(this.options, 'minValue', Number(this.result.minValue))
        this.$set(this.options, 'maxValue', Number(this.result.maxValue))
        this.$set(this.options, 'value', Number(this.result.lastValue))
        linearGaugeChart.update(this.options)
      }
    },
    getFormattedValue(value) {
      if (!isEmpty(value)) {
        return `${value}`
      }
      return `--`
    },
  },
}
</script>
<style lang="scss" scoped>
.linear-gauge {
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
  .value-contianer {
    margin-top: -3px;
  }
}
</style>
