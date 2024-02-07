<template>
  <div style="">
    <canvas :ref="`Gauge-linear`"></canvas>
  </div>
</template>

<script>
import { LinearGauge } from 'canvas-gauges'
import { isEmpty } from '@facilio/utils/validation'
import * as d3 from 'd3'
export default {
  props: ['gaugeData', 'cardStyle'],
  data() {
    return {
      options: {
        renderTo: {},
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
        highlights: '__vue_devtool_undefined__',
      },
    }
  },
  mounted() {
    this.$nextTick(() => {
      if (!isEmpty(this.$refs)) {
        this.$set(this.gaugeData.data, 'value', this.gaugeData.data.baseValue)
        let canvasElement = this.$refs[`Gauge-linear`]
        this.options.majorTicks = [0, 2, 4, 6, 8, 10, 12, 14]
        this.options.minorTicks = 2
        this.options = { ...this.options, ...this.gaugeData.data }
        this.options = { ...this.options, ...this.cardStyle }
        this.options = this.setMajorTicks(this.options, this.cardStyle)
        this.$set(this.options, 'renderTo', canvasElement)
        this.linearGaugeChart = new LinearGauge(this.options).draw()
      }
    })
  },
  methods: {
    setMajorTicks(options, cardStyle) {
      let scale = d3
        .scaleLinear()
        .domain([Number(options.minValue), Number(options.maxValue)])
      options.majorTicks = scale.ticks(Number(cardStyle.majorTicksCount))
      return { ...options, ...cardStyle }
    },
  },
}
</script>
