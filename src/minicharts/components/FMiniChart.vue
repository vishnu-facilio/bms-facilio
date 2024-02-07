<template>
  <div ref="f-minichart" id="f-minichart"></div>
</template>
<script>
import * as d3 from 'd3'
import semidoughnut from '../lib/semidoughnut'
import progress from '../lib/progress'

export default {
  props: ['type', 'data'],
  data() {
    return {
      chartcontext: null,
    }
  },
  mounted() {
    this.generate()
  },
  watch: {
    data: function() {
      this.$refs['f-minichart'].innerHTML = ''
      this.generate()
    },
  },
  methods: {
    generate() {
      let chartContext = {}
      chartContext.selecter = this.$refs['f-minichart']
      chartContext.width = 100
      chartContext.height = 100
      chartContext.svg = d3
        .select(chartContext.selecter)
        .append('svg')
        .attr('width', '100%')
        .attr('height', '100%')
        .attr(
          'viewBox',
          '0 0 ' +
            Math.min(chartContext.width, chartContext.height) +
            ' ' +
            Math.min(chartContext.width, chartContext.height)
        )
        .attr('preserveAspectRatio', 'xMinYMin')
        .append('g')
      if (this.type === 'semidoughnut') {
        chartContext.svg.attr(
          'transform',
          'translate(' +
            Math.min(chartContext.width, chartContext.height) / 2 +
            ',' +
            Math.min(chartContext.width, chartContext.height) / 2 +
            ') '
        )
      }

      chartContext.data = this.data

      switch (this.type) {
        case 'semidoughnut':
          semidoughnut.generate(chartContext)
          break
        case 'progress':
          progress.generate(chartContext)
          break
      }
    },
  },
}
</script>
<style>
text.progress-center-text {
  font-size: 2em;
  color: #000;
}
.progressParent {
  margin: auto;
}
#f-minichart path {
  transform: rotate(-140deg);
}
</style>
