<template>
  <div ref="lineChart"></div>
</template>
<script>
import * as d3 from 'd3'
import basechart from 'charts/mixins/basechart'
import axis from 'charts/core/axis'
import line from 'charts/core/line'
import container from 'charts/core/container'

export default {
  mixins: [basechart],
  data() {
    return {
      margin: {
        top: 20,
        right: 20,
        bottom: 30,
        left: 40,
      },
      defaultOptions: {
        isHorizontal: false,
        yAxisPaddingBetweenChart: 10,
        isAnimated: true,
        ease: d3.easeQuadInOut,
        type: 'grouped',
        curvetype: 'monotoneX',
        animationDuration: 800,
        animationStepRatio: 70,
        interBarDelay: (d, i) => this.defaultOptions.animationStepRatio * i,
      },
    }
  },
  mounted() {
    this.render()
  },
  methods: {
    render() {
      this.selector = this.$refs['lineChart']
      this.getValue = ({ value }) => value
      this.getLabel = ({ label }) => label
      this.colorSchema = this.getColorSchema()
      this.chartWidth = this.getWidth() - this.margin.left - this.margin.right
      this.chartHeight = this.getHeight() - this.margin.top - this.margin.bottom
      this.pathYCache = {}
      this.dispatcher = d3.dispatch(
        'customMouseOver',
        'customMouseOut',
        'customMouseMove'
      )

      line.buildScales(this)

      axis.buildAxis(this)

      this.svg = d3
        .select(this.selector)
        .append('svg')
        .classed('fchart line-chart', true)

      container.buildContainerGroups(this)

      this.svg.attr('width', this.getWidth()).attr('height', this.getHeight())

      axis.drawAxis(this)

      if (this.getOptions().type === 'grouped') {
        line.drawgroupLines(this)
      } else {
        line.drawLines(this)
      }
      container.drawHoverOverlay(this)
      container.drawVerticalMarker(this)
      container.addMouseEvents(this)
    },
    update() {},
    resize() {},
  },
}
</script>
