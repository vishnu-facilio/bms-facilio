<template>
  <div>
    <div ref="f-timeseries"></div>
  </div>
</template>
<script>
import * as d3 from 'd3'
import basechart from 'charts/mixins/basechart'
// import timeseries from 'charts/core/timeseries'
import peak from 'charts/core/cubism'
// import moment from 'moment-timezone'
// peak.cubism.drawAxis(this)
export default {
  mixins: [basechart],
  props: ['data', 'alarms', 'width', 'height', 'datefilter', 'type'],
  data() {
    return {}
  },
  mounted() {
    window.addEventListener('resize', this.rerender)
    this.render()
  },
  watch: {
    layout: {
      handler: function(newVal, oldVal) {
        this.rerender()
      },
      deep: true,
    },
    data: {
      handler: function(newVal, oldVal) {
        this.rerender()
      },
      deep: true,
    },
    type: {
      handler: function(newVal, oldVal) {
        this.rerender()
      },
      deep: true,
    },
  },
  computed: {
    layout() {
      if (this.width && this.height) {
        return {
          width: this.width,
          height: this.height,
        }
      }
      return null
    },
  },
  methods: {
    rerender() {
      if (this.$refs['f-timeseries']) {
        this.$refs['f-timeseries'].innerHTML = ''
      }
      this.render()
    },
    buildContainerGroups(chartContext) {
      chartContext.container = chartContext.svg
        .append('g')
        .classed('container-group', true)
        .attr(
          'transform',
          `translate(${chartContext.getMargin().right}, ${
            chartContext.getMargin().top
          })`
        )

      chartContext.container.append('g').classed('grid-lines-group', true)
      chartContext.container
        .append('g')
        .attr('transform', `translate(0, 0)`)
        .classed('y-axis-group axis', true)
      chartContext.container
        .append('g')
        .attr(
          'transform',
          `translate(${chartContext.getWidth() -
            chartContext.getMargin().right -
            chartContext.getMargin().left}, 0)`
        )
        .classed('y1-axis-group axis', true)
      chartContext.container.append('g').classed('x-axis-group axis', true)
      chartContext.container
        .append('g')
        .classed('chart-group', true)
        .attr('width', 400)
        .attr('height', 100)
      chartContext.container.append('g').classed('metadata-group', true)
      chartContext.container.append('g').classed('brush-group', true)
      chartContext.hover = true
    },
    render() {
      let context = this
      if (this.data) {
        context.selector = this.$refs['f-timeseries']
        context.data = this.data

        this.svg = d3
          .select(context.selector)
          .append('svg')
          .classed('fchart', true)
        this.buildContainerGroups(context)

        peak.drawAxis(context)
      }
    },
  },
}
</script>
<style>
.y-axis-group g.tick > line {
  fill: none;
  shape-rendering: crispEdges;
  stroke: #eff2f5;
  stroke-width: 1;
  stroke-dasharray: 4, 4;
}
.axis path,
.axis line {
  fill: none;
  shape-rendering: crispEdges;
  /* stroke: #D2D6DF !important; */
  stroke-width: 1 !important;
}
.d3_timeseries.line {
  fill: none;
  /* stroke-width: 1.; */
  shape-rendering: geometricPrecision;
  transition: opacity 0.7s;
}
.focusLine {
  fill: none;
  stroke: black;
  opacity: 0.1;
  stroke-width: 1.5px;
}
.fc-white-theme .axis path.domain {
  stroke: #d2d6df !important;
}
/* .fc-black-theme .brush-axis .axis path, .axis line {
  stroke: #D2D6DF !important;
} */

.f-legends {
  padding: 15px 10px 10px 10px;
  text-align: center;
  display: inline-flex;
}

.f-legends .legend-entry {
  float: left;
  margin-right: 8px;
  cursor: pointer;
}

.f-legends .legend-color {
  margin: 2px 5px;
}

.f-legends .legend-color,
.f-legends .legend-label {
  display: inline-block;
  vertical-align: middle;
  padding: 4px 2px;
}
.f-legends .legend-entry.inactive {
  opacity: 0.3;
}
</style>
