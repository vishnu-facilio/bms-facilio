<template>
  <div
    class="fchart-layout"
    v-bind:class="{ removealarm: showAlarm === false }"
  >
    <div
      title="Toggle Alarms"
      v-tippy
      data-arrow="true"
      class="fchart-alarm-toggle fchart-alarm"
      v-bind:class="{ 'fchart-alarm-toggle-on': showAlarm === false }"
      v-if="this.alarms && this.alarms.length && this.type !== 'boolean'"
      @click="changeRenderAlarm"
    >
      <i class="material-icons alarm-icon-size">alarm</i>
    </div>
    <legends
      v-if="showLegends"
      :chartContext="this"
      class="legendsAll"
    ></legends>
    <div class="emptyLegends" v-else v-show="showLegends"></div>
    <div ref="f-chart"></div>
    <chart-options
      :chartContext="options"
      v-if="options.showWidgetLegends"
      :width="width"
      :height="height"
    ></chart-options>
  </div>
</template>
<script>
import * as d3 from 'd3'
import basechart from 'charts/mixins/basechart'
import common from 'charts/helpers/common'
import * as core from 'charts/core'
import Legends from 'charts/components/Legends'
import ChartOptions from 'charts/components/chartOptions'

export default {
  mixins: [basechart],
  components: {
    Legends,
    ChartOptions,
  },
  data() {
    return {
      defaultLayout: {
        width: 500,
        height: 600,
      },
      showAlarm: true,
      barwidth: 0,
    }
  },
  computed: {
    showLegends() {
      let isLegends = false
      if (
        this.config &&
        this.config.layoutConfig &&
        this.config.layoutConfig.hideleagend
      ) {
        return false
      }
      if (
        typeof this.getOptions().legends === 'undefined' ||
        this.getOptions().legends
      ) {
        if (this.type === 'boolean') {
          isLegends = true
        }
        if (common.isValueArray(this.getMainData())) {
          isLegends = true
        } else {
          if (this.type === 'pie' || this.type === 'doughnut') {
            if (
              typeof this.getOptions().isWidget === 'undefined' ||
              !this.getOptions().isWidget
            ) {
              isLegends = true
            } else {
              isLegends = true
            }
          } else {
            isLegends = false
          }
        }
      } else {
        isLegends = false
      }
      if (this.isMultiData()) {
        if (this.type !== 'progress') {
          isLegends = true
        } else {
          isLegends = false
        }
      }
      if (this.type === 'heatMap') {
        isLegends = false
      }
      return isLegends
    },
  },
  mounted() {
    window.addEventListener('resize', this.rerender)
    this.render()
  },
  destroyed() {
    window.removeEventListener('resize', this.rerender)
  },
  watch: {
    options: {
      handler: function(newVal, oldVal) {
        this.rerender()
      },
      deep: true,
    },
    alarms: {
      handler: function(newVal, oldVal) {
        this.renderAlarms(this.getAlarms())
      },
      deep: true,
    },
  },
  methods: {
    changeRenderAlarm() {
      this.showAlarm = !this.showAlarm
    },
    rerender() {
      if (this.$refs['f-chart']) {
        this.$refs['f-chart'].innerHTML = ''
      }
      this.render()
      this.renderAlarms(this.getAlarms())
    },
    render() {
      if (this.getOptions().sort) {
        if (this.getOptions().sort === 'desc') {
          this.data = common.sortDescending(this.getMainData())
        } else {
          this.data = common.sortAscending(this.getMainData())
        }
      }
      if (this.type !== 'progress') {
        // Prepare for chart generation
        this.prepareChart(this)
      }

      // Draw axis
      if (
        typeof this.getOptions().axis === 'undefined' ||
        this.getOptions().axis
      ) {
        core.axis.buildScales(this)
        core.axis.buildAxis(this)
        core.axis.drawAxis(this)
        core.axis.drawsafeLimit(this)
      }

      // draw chart
      if (this.isMultiData()) {
        for (let serie of this.data) {
          if (typeof serie.options.show === 'undefined' || serie.options.show) {
            this.drawChart(
              serie.data,
              this.getChartOptions(serie.options),
              serie.data2
            )
          }
        }
      } else {
        if (this.type) {
          this.options.type = this.type
        }
        this.drawChart(this.data, this.getChartOptions(this.options))
      }
      // if (this.getOptions().y2axis) {
      //   core.line.dualAxisY2Plot(this)
      // }
    },
    prepareChart(context) {
      context.selector = this.$refs['f-chart']

      context.getValue = ({ value }) => value
      context.getLabel = ({ label }) => label

      context.chartWidth =
        context.getWidth() -
        context.getMargin().left -
        context.getMargin().right
      context.chartHeight =
        context.getHeight() -
        context.getMargin().top -
        context.getMargin().bottom
      context.getSecondChartType =
        this.config &&
        this.config.chartData1 &&
        this.config.chartData1.dataOptions.chartType
          ? this.config.chartData1.dataOptions.chartType
          : null
      context.dispatcher = d3.dispatch(
        'customMouseOver',
        'customMouseOut',
        'customMouseMove',
        'customClick',
        'customBrushStart',
        'customBrushEnd'
      )

      context.svg = d3
        .select(context.selector)
        .append('svg')
        .classed('fchart', true)
      if (
        this.config &&
        this.config.layoutConfig &&
        this.config.layoutConfig.hideleagend
      ) {
        context.svg
          .attr('width', context.getWidth() + 400)
          .attr('height', context.getHeight())
      } else {
        context.svg
          .attr('width', context.getWidth())
          .attr('height', context.getHeight())
      }

      if (
        typeof this.getOptions().axis === 'undefined' ||
        this.getOptions().axis
      ) {
        core.container.buildContainerGroups(this)
      }
    },
    createHighlightOverlay(context) {
      core.container.drawHoverOverlay(context)
      core.container.drawVerticalMarker(context)
      core.container.addMouseEvents(context)
      if (context.getAlarms()) {
        core.scatter.drawAlarms(context, context.getAlarms())
      }
    },
    renderAlarms(alarms) {
      if (alarms && alarms.length) {
        if (this.type === 'area' || this.type === 'line') {
          core.scatter.drawAlarms(this, alarms)
        }
      }
    },
    drawChart(data, options, data2) {
      let self = this
      let chartObj = null
      if (data2) {
        chartObj = {
          fchart: this,
          data: data,
          options: options,
          onclick: this.drilldown,
          data2: data2,
        }
      } else {
        chartObj = {
          fchart: this,
          data: data,
          options: options,
          onclick: this.drilldown,
        }
      }

      switch (options.type) {
        case 'pie':
          core.pie.drawPie(chartObj)
          break
        case 'doughnut':
          core.pie.drawDoughnut(chartObj)
          break
        case 'bar':
          core.bar.drawBars(chartObj, function(data) {})
          break
        case 'stackedbar':
          core.bar.drawStackedBars(chartObj)
          break
        case 'groupedbar':
          core.bar.drawGroupedBars(chartObj)
          break
        case 'line':
          core.line.drawLines(chartObj)
          if (!chartObj.options.withPoints) {
            this.createHighlightOverlay(this)
          }
          break
        case 'area':
          core.area.drawAreas(chartObj)
          if (!chartObj.options.withPoints) {
            this.createHighlightOverlay(this)
          }
          break
        case 'scatter':
          core.scatter.drawScatterPlot(chartObj)
          break
        case 'regression':
          core.scatter.drawScatterPlot(chartObj)
          break
        case 'progress':
          core.progress.drawProgress(chartObj)
          break
        case 'funnel':
          core.funnel.drawTrapezoids(chartObj)
          break
        case 'heatMap':
          core.heatmap.drawHeatMap(chartObj, function(event, data) {
            if (event === 'click') {
              self.$emit('drilldown', data)
            }
          })
          break
        case 'runtime':
          core.runtime.drawRuntime(chartObj)
          break
        case 'boolean':
          core.boolean.drawBoolean(chartObj)
          break
      }
    },
    drilldown(data) {
      this.$emit('drilldown', data)
    },
    update() {},
    resize() {
      this.rerender()
    },
  },
}
</script>
<style></style>
