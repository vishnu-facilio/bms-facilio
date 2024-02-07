<template>
  <div ref="barChart"></div>
</template>
<script>
import * as d3 from 'd3'
import basechart from 'charts/mixins/basechart'
import common from 'charts/helpers/common'
import axis from 'charts/core/axis'
import bar from 'charts/core/bar'
import container from 'charts/core/container'

export default {
  mixins: [basechart],
  components: {},
  data() {
    return {
      defaultLayout: {
        width: 500,
        height: 600,
      },
      margin: {
        top: 20,
        right: 20,
        bottom: 30,
        left: 60,
      },
      defaultOptions: {
        isHorizontal: false,
        yAxisPaddingBetweenChart: 10,
        isAnimated: true,
        ease: d3.easeQuadInOut,
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
      this.selector = this.$refs['barChart']

      this.chartWidth =
        this.getWidth() -
        this.margin.left -
        this.margin.right -
        this.getOptions().yAxisPaddingBetweenChart * 1.2
      this.chartHeight = this.getHeight() - this.margin.top - this.margin.bottom
      this.dataZeroed = common.cleanData(this.getData())

      this.getLabel = ({ label }) => label
      this.getValue = ({ value }) => value

      axis.buildScales(this)

      axis.buildAxis(this)

      this.svg = d3
        .select(this.selector)
        .append('svg')
        .classed('fchart bar-chart', true)

      container.buildContainerGroups(this)

      this.svg.attr('width', this.getWidth()).attr('height', this.getHeight())

      if (this.getOptions().type === 'grouped') {
        bar.drawGroupedBars(this)
      } else if (this.getOptions().type === 'stacked') {
        bar.drawStackedBars(this)
      } else {
        bar.drawBars(this)
      }
      axis.drawAxis(this)
    },
    update() {},
    resize() {},
  },
}
</script>
<style></style>
