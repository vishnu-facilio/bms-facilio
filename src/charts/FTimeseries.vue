<template>
  <div
    v-bind:class="{ removealarm: showAlarm === false }"
    class="ftimeseries-alarm-toggle-container"
  >
    <div
      title="Toggle Alarms"
      v-tippy
      data-arrow="true"
      class="ftimeseries-alarm-toggle"
      v-bind:class="{ 'ftimeseries-alarm-toggle-on': showAlarm === false }"
      v-if="this.alarms && this.alarms.length"
      @click="changeRenderAlarm"
    >
      <i class="material-icons alarm-icon-size">alarm</i>
    </div>
    <div ref="f-legends" class="f-legends"></div>
    <div ref="f-timeseries"></div>
    <chart-options
      :chartContext="data[0].options"
      v-if="data && data[0] && data[0].options.showWidgetLegends"
      :width="width"
      :height="height"
    ></chart-options>
  </div>
</template>
<script>
import * as d3 from 'd3'
import timeseries from 'charts/core/timeseries'
import moment from 'moment-timezone'
import ChartOptions from 'charts/components/chartOptions'
export default {
  props: ['data', 'alarms', 'width', 'height', 'datefilter', 'type'],
  components: { ChartOptions },
  data() {
    return {
      showAlarm: true,
    }
  },
  mounted() {
    window.addEventListener('resize', this.rerender)
    this.render()
  },
  destroyed() {
    window.removeEventListener('resize', this.rerender)
  },
  watch: {
    layout: {
      handler: function(newVal, oldVal) {
        setTimeout(() => {
          this.rerender()
        }, 250)
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
    changeRenderAlarm() {
      this.showAlarm = !this.showAlarm
    },
    rerender() {
      if (this.$refs['f-timeseries']) {
        this.$refs['f-timeseries'].innerHTML = ''
      }
      this.render()
    },
    greet() {},
    render() {
      let self = this
      let width = parseInt(d3.select(this.$el).style('width'))
      let height = 400
      if (this.layout) {
        let diff = width - this.layout.width
        width = this.layout.width + diff
        height = this.layout.height - 20
      }

      if (this.data) {
        // dip fix
        // let currentHour = moment().tz(this.$timezone).startOf('hour').valueOf()
        // for (let i = 0; i < this.data.length ; i++) {
        //   for (let j = 0; j < this.data[i].data.length; j++) {

        //   }
        // }
        let elm = this.$refs['f-timeseries']

        let unitList = {}
        let chart = timeseries()
        let y2Axis = false
        for (let serie of this.data) {
          let chartType = this.type ? this.type : 'line'
          if (serie.type) {
            chartType = serie.type
          }
          let options = {
            interpolate: 'linear',
            dashed: serie.options.dashed,
            color: serie.options.color,
            chartType: chartType,
            path: this.$router,
            label: serie.title,
            axis: serie.options.y1axis,
            xaxis: this.data[0].options.xaxis,
            baseLineId: serie.baseLineId,
            safelimit: this.data[0].options.safelimit,
          }
          if (!unitList[options.axis.unit] && !y2Axis) {
            unitList[options.axis.unit] = true
            if (Object.keys(unitList).length > 1) {
              options.y2Axis = true
              y2Axis = true
            }
          }
          chart.addSerie(serie.data, { x: 'label', y: 'value' }, options)
        }

        chart.xscale.tickFormat(function(date) {
          let format =
            d3.timeDay(date) < date
              ? 'hh A'
              : d3.timeMonth(date) < date
              ? 'MMM DD'
              : d3.timeYear(date) < date
              ? 'MMM'
              : 'YYYY'
          return moment(date)
            .tz(self.$timezone)
            .format(format)
        })
        chart.width(width)
        chart.height(height)
        chart.path(this.$router)

        chart(
          elm,
          this.data[0].options.id,
          this.alarms,
          this.$refs['f-legends'],
          this.data[0].options.date_range
        )
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
.d3_timeseries.line,
.d3_timeseries.layer {
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
  stroke: #d2d6df;
}
/* .fc-black-theme .brush-axis .axis path, .axis line {
  stroke: #D2D6DF !important;
} */

.f-legends {
  padding: 10px 10px 10px 10px;
  display: inline-flex;
  flex-wrap: wrap;
  max-width: 100% !important;
  justify-content: center;
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

.removealarm .alarmsGroups {
  display: none;
}
.ftimeseries-alarm-toggle {
  position: absolute;
  right: 25px;
  top: 80px;
  color: #5bbaa7;
  cursor: pointer;
  font-size: 19px;
}
/* .ftimeseries-alarm-toggle{
  position: absolute;
  right: 85px;
  top: 13px;
  color: #5bbaa7;
  cursor: pointer;
  font-size: 19px;
} */
.analytic-summary .ftimeseries-alarm-toggle {
  right: 70px;
  top: -31px;
}
.ftimeseries-alarm-toggle:hover {
  color: #5bbaa7;
  opacity: 1;
}
.ftimeseries-alarm-toggle-on {
  opacity: 0.5;
  color: #000;
}
.month-axis g.tick line {
  opacity: 0;
}
</style>
