<template>
  <div v-if="heatMapStore.length !== 0" class="height100 heatmap">
    <f-chart-type
      v-if="!hidecharttypechanger"
      :options="this.reportObject ? this.reportObject.options : null"
      @getOptions="getOptionsFromTypeChart"
      class="charttypechanger fc-new-chart-type-single"
    ></f-chart-type>
    <div ref="heatmapDiv" class="height100"></div>
  </div>
</template>

<script>
import debounce from 'lodash/debounce'
import isEqual from 'lodash/isEqual'
import * as d3 from 'd3'
import FChartType from 'newcharts/components/FChartType'
import ReportDataUtil from 'src/pages/report/mixins/ReportDataUtil'
import ckmeans from 'third_party/ckmeans.min.js'
import * as core from 'charts/core'
import tooltip from '@/graph/mixins/tooltip'
import moment from 'moment-timezone'
import Vue from 'vue'

export default {
  data() {
    return {
      constantMargins: {
        top: 30,
        right: 55,
        bottom: 100,
        left: 55,
      },
      clientWidth: 980,
      clientHeight: 620,
      cellWidth: 50,
      cellHeight: 30,
      xformat: 'hours',
      hidecharttypechanger: true,
      gridSize: 0,
      xAlias: null,
      yAliasList: [],
      updateTimeout: null,
      heatMapStore: [],
      selectedLabel: null,
      groupLabels: [],
      defaultColors: ['#1d7f01', '#6cb302', '#e9f501', '#fda504', '#fb5905'],
    }
  },
  mixins: [ReportDataUtil],
  components: {
    FChartType,
  },
  mounted() {
    this.render()
  },
  props: {
    resultObject: {},
    reportObject: {},
    config: {},
    widgetBodyDimension: {
      type: Object,
      required: true,
    },
  },
  watch: {
    widgetBodyDimension(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        const deboundedFunc = debounce(this.render, 300)
        deboundedFunc()
      }
    },
  },
  computed: {
    width() {
      const {
        widgetBodyDimension: { widgetBodyWidth: width },
      } = this
      return width
    },
    height() {
      const {
        widgetBodyDimension: { widgetBodyHeight: height },
      } = this
      return height
    },
    chosenColors() {
      if (this.reportObject.options.heatMapOptions) {
        let key = this.reportObject.options.heatMapOptions.chosenColors
        return this.reportObject.options.heatMapOptions.Colors[key]
      }
      return this.defaultColors
    },
    heatMapMinValue() {
      if (
        this.reportObject.options.heatMapOptions &&
        this.reportObject.options.heatMapOptions.minValue !== null &&
        this.reportObject.options.heatMapOptions.minValue !== ''
      ) {
        return this.reportObject.options.heatMapOptions.minValue
      } else {
        return null
      }
    },
    heatMapMaxValue() {
      if (
        this.reportObject.options.heatMapOptions &&
        this.reportObject.options.heatMapOptions.maxValue !== null &&
        this.reportObject.options.heatMapOptions.maxValue !== ''
      ) {
        return this.reportObject.options.heatMapOptions.maxValue
      } else {
        return null
      }
    },
  },
  methods: {
    render() {
      if (this.reportObject) {
        this.loadHeatMapStore()
        this.$nextTick(() => {
          if (this.resultObject) {
            for (let dataPoint of this.resultObject.report.dataPoints) {
              this.yAliasList.push(dataPoint.aliases.actual)
            }
            this.xAlias = 'x'
          }
          this.drawHeatMap()
        })
      }
    },
    loadHeatMapStore() {
      this.heatMapStore = this.reportObject.options.dataPoints
    },
    resize() {
      this.rerender()
    },
    rerender() {
      if (this.updateTimeout) {
        clearTimeout(this.updateTimeout)
      }
      this.updateTimeout = setTimeout(() => this.render(), 500)
    },

    groupValues(minValue, maxValue, n) {
      const deviation = (maxValue - minValue) / n
      let group = []
      for (let i = 0; i < n; i++) {
        let temp = minValue + i * deviation
        group.push(temp)
      }

      return group
    },

    drawHeatMap() {
      let starttime = Date.now()
      if (this.$mobile) {
        this.constantMargins.left = 30
        this.constantMargins.right = 20
        this.constantMargins.top = 10
      }
      let width =
        (this.width !== null && this.width > 0
          ? this.width
          : this.clientWidth) -
        (this.constantMargins.left + this.constantMargins.right)
      let height =
        (this.height !== null && this.height > 0
          ? this.height
          : this.clientHeight) -
        (this.constantMargins.top + this.constantMargins.bottom)

      let dataPoints = this.resultObject.report.dataPoints

      for (let dp of dataPoints) {
        let index = dataPoints.indexOf(dp)
        let unit = dp.yAxis.unitStr !== null ? dp.yAxis.unitStr : ' '

        let isContinous =
          this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
          this.resultObject.report.dataPoints[0].xAxis.dataType === 6
            ? true
            : false
        let dataAlias = dp.aliases.actual
        let yMax = this.resultObject.reportData.aggr[dataAlias + '.max']
        let yMin = this.resultObject.reportData.aggr[dataAlias + '.min']
        yMin =
          this.reportObject.options.heatMapOptions &&
          this.reportObject.options.heatMapOptions.minValue !== null &&
          this.reportObject.options.heatMapOptions.minValue !== ''
            ? this.reportObject.options.heatMapOptions.minValue
            : yMin
        yMax =
          this.reportObject.options.heatMapOptions &&
          this.reportObject.options.heatMapOptions.maxValue != null &&
          this.reportObject.options.heatMapOptions.maxValue !== ''
            ? this.reportObject.options.heatMapOptions.maxValue
            : yMax
        if (this.resultObject.report.type === 2) {
          if (this.reportObject.dateRange.period === 'hourly') {
            this.xformat = 'hours'
          } else if (this.reportObject.dateRange.period === 'daily') {
            this.xformat = 'days'
          } else {
            this.xformat = 'hours'
          }
        } else {
          this.xformat =
            this.resultObject &&
            this.resultObject.report &&
            this.resultObject.report.hmAggr &&
            this.resultObject.report.hmAggr !== null
              ? this.resultObject.report.hmAggr
              : 'hours'
        }
        let dataWithAxis = this.transformDataForHeatMap(
          this.reportObject,
          this.resultObject.reportData.heatMapData,
          'X',
          dp.aliases.actual,
          isContinous,
          this.xformat
        )

        let transformedData = dataWithAxis[0]

        let xD = dataWithAxis[1]
        let cellWidth = width / xD.length

        let x = d3
          .scaleBand()
          .range([0, width])
          .domain(xD)
          .padding(0.005)

        let clusterData = null
        if (this.resultObject.report.type === 2) {
          this.hidecharttypechanger = false
          clusterData = transformedData
            .filter(data => data.value !== null)
            .map(data => {
              return parseFloat(data.value)
            })
          yMin = d3.min(clusterData)
          yMax = d3.max(clusterData)
          clusterData.sort()
        } else {
          clusterData = transformedData
            .filter(
              data =>
                data.value !== null && data.value >= yMin && data.value <= yMax
            )
            .map(data => {
              return data.value
            })
        }
        let clusters = null
        if (
          clusterData.length > 5 &&
          (this.heatMapMinValue === null || this.heatMapMinValue === '') &&
          (this.heatMapMaxValue === null || this.heatMapMaxValue === '')
        ) {
          clusters = ckmeans(clusterData, 5)
        } else if (
          (this.heatMapMinValue === null || this.heatMapMinValue === '') &&
          (this.heatMapMaxValue === null || this.heatMapMaxValue === '')
        ) {
          clusters = clusterData
        } else {
          let minValue =
            this.heatMapMinValue === null || this.heatMapMinValue === ''
              ? d3.min(clusterData)
              : this.heatMapMinValue

          let maxValue =
            this.heatMapMaxValue === null || this.heatMapMaxValue === ''
              ? d3.max(clusterData)
              : this.heatMapMaxValue

          clusters = this.groupValues(minValue, maxValue, 5)
        }

        // if (clusters && clusters[4] < yMax) {
        //   clusters.push(yMax)
        // }

        let yData = []
        if (
          this.reportObject.dateRange.period === 'hourly' &&
          (['month', 'year', 'quarter'].includes(
            this.reportObject.dateRange.operationOn
          ) ||
            (this.reportObject.dateRange.operationOn === 'day' &&
              this.reportObject.dateRange.offset >= 32))
        ) {
          yData = dataWithAxis[2][0]
        } else if (
          this.xformat === 'weeks' &&
          ['month', 'year', 'quarter'].includes(
            this.reportObject.dateRange.operationOn
          )
        ) {
          yData = dataWithAxis[2][0]
          yData.sort()
        } else {
          yData = dataWithAxis[2]
        }
        let cellHeight = height / yData.length
        if (cellHeight > this.cellHeight) {
          height = this.cellHeight * yData.length
          cellHeight = this.cellHeight
        }
        let y = d3
          .scaleBand()
          .range([0, height])
          .domain(yData)
          .padding(0.005)

        let myColor = d3
          .scaleLinear()
          .range(this.chosenColors)
          .domain(clusters)

        let border = 1
        let bordercolor = '#CCCCCC'
        this.$refs['heatmapDiv'].innerHTML = ''
        let svg = d3
          .select(this.$refs['heatmapDiv'])
          .append('svg')
          .attr(
            'width',
            width + this.constantMargins.right + this.constantMargins.left
          )
          .attr(
            'height',
            height + this.constantMargins.top + this.constantMargins.bottom - 10
          )
          .attr('border', border)
          .attr('class', this.$mobile ? '' : 'widget-heatmap')
          .append('g')
          .attr(
            'transform',
            'translate(' +
              this.constantMargins.left +
              ',' +
              this.constantMargins.top +
              ')'
          )

        let borderPath = svg
          .append('rect')
          .attr('x', 0)
          .attr('y', 0)
          .attr('height', height)
          .attr('width', width)
          .style('stroke', bordercolor)
          .style('fill', 'none')
          .style('stroke-width', border)

        if (this.xformat === 'hours') {
          let xAxis = !this.$mobile
            ? [
                '12a',
                '1a',
                '2a',
                '3a',
                '4a',
                '5a',
                '6a',
                '7a',
                '8a',
                '9a',
                '10a',
                '11a',
                '12p',
                '1p',
                '2p',
                '3p',
                '4p',
                '5p',
                '6p',
                '7p',
                '8p',
                '9p',
                '10p',
                '11p',
              ]
            : ['12a', '4a', '8a', '12p', '4p', '8p', '11p']
          let x1 = d3
            .scaleBand()
            .range([0, width])
            .domain(xAxis)
            .padding(0.005)
          svg
            .append('g')
            .attr('class', this.$mobile ? 'xaxis' : 'x axis')
            .call(d3.axisTop(x1).tickSize([0]))
            .append('text')
            .attr('class', 'label')
            .attr('text-anchor', 'start')
        } else if (this.xformat === 'days' && this.$mobile) {
          let xAxis = ['01', '06', '11', '16', '21', '26', '31']
          let x1 = d3
            .scaleBand()
            .range([0, width])
            .domain(xAxis)
            .padding(0.005)
          svg
            .append('g')
            .attr('class', this.$mobile ? 'xaxis' : 'x axis')
            .call(d3.axisTop(x1).tickSize([0]))
            .append('text')
            .attr('class', 'label')
            .attr('text-anchor', 'start')
        } else {
          svg
            .append('g')
            .attr('class', this.$mobile ? 'xaxis' : 'x axis')
            .call(d3.axisTop(x).tickSize([0]))
            .append('text')
            .attr('class', 'label')
            .attr('text-anchor', 'start')
        }
        if (
          this.reportObject.dateRange.period === 'hourly' &&
          (['month', 'year', 'quarter'].includes(
            this.reportObject.dateRange.operationOn
          ) ||
            (this.reportObject.dateRange.operationOn === 'day' &&
              this.reportObject.dateRange.offset >= 32))
        ) {
          let y1 =
            cellHeight < 20
              ? d3
                  .scaleBand()
                  .range([0, height])
                  .domain(dataWithAxis[2][1])
                  .padding(0.01)
              : d3
                  .scaleBand()
                  .range([0, height])
                  .domain(dataWithAxis[2][0])
                  .padding(0.01)
          svg
            .append('g')
            .attr('class', 'y axis')
            .call(d3.axisLeft(y1).tickSize([0]))
            .append('text')
            .attr('class', 'label')
            .attr('text-anchor', 'end')
            .attr('transform', 'translate(' + 0 + ',' + 90 + ')')
        } else if (
          this.xformat === 'weeks' &&
          ['month', 'year', 'quarter'].includes(
            this.reportObject.dateRange.operationOn
          )
        ) {
          let y1 =
            cellHeight < 20
              ? d3
                  .scaleBand()
                  .range([0, height])
                  .domain(dataWithAxis[2][1])
                  .padding(0.01)
              : d3
                  .scaleBand()
                  .range([0, height])
                  .domain(dataWithAxis[2][0])
                  .padding(0.01)
          svg
            .append('g')
            .attr('class', 'y axis')
            .call(d3.axisLeft(y1).tickSize([0]))
            .append('text')
            .attr('class', 'label')
            .attr('text-anchor', 'end')
            .attr('transform', 'translate(' + 0 + ',' + 90 + ')')
        } else {
          svg
            .append('g')
            .attr('class', 'y axis')
            .call(d3.axisLeft(y).tickSize([0]))
            .append('text')
            .attr('class', 'label')
            .attr('text-anchor', 'end')
        }
        let self = this
        svg
          .selectAll()
          .data(transformedData, function(d) {
            d['X'] + ':' + d['Y']
          })
          .enter()
          .append('rect')
          .attr('x', function(d) {
            return x(d['X'])
          })
          .attr('y', function(d) {
            return y(d['Y'])
          })
          // .attr("rx", 1)
          // .attr("ry", 1)
          .attr('width', x.bandwidth() > cellWidth ? cellWidth : x.bandwidth())
          .attr(
            'height',
            y.bandwidth() > cellHeight ? cellHeight : y.bandwidth()
          )
          .style('stroke', '#CCCCCC')
          .style('fill', 'none')
          .style('stroke-width', 0.05)
          .style('fill', function(d) {
            let c =
              d.value !== null
                ? myColor(d.value)
                : window.localStorage && window.localStorage.theme === 'black'
                ? '#090f2e'
                : '#FFFFFF'
            if (d.value !== null && d.value < yMin) {
              c = myColor(clusters[0])
            } else if (d.value !== null && d.value > yMax) {
              c = myColor(clusters[4])
            }
            return c
          })
          .on('mouseover', function(d) {
            let tooltipData = [
              {
                label: d['xTitle'],
                value: moment(d['originalValue'])
                  .tz(Vue.prototype.$timezone)
                  .format(self.getTooltipFormat()),
              },
              {
                label: d['yTitle'],
                value:
                  d.value !== null
                    ? d['value'] + ' ' + unit
                    : d.violated_value !== null
                    ? d['violated_value'] + ' ' + unit + ' (violated value)'
                    : 'No data',
              },
            ]
            let tooltipConfig = {
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              data: tooltipData,
              color: self.getTooltipColor(
                d.value,
                myColor,
                yMin,
                yMax,
                clusters
              ),
            }
            tooltip.showTooltip(tooltipConfig)
          })
          .on('mouseout', function(d) {
            tooltip.hideTooltip()
          })
        let svgDefs = svg.append('defs')

        let legendref = 'legendGradient' + Date.now()

        let mainGradient = svgDefs
          .append('linearGradient')
          .attr('id', legendref)

        mainGradient
          .append('stop')
          .style('stop-color', function(d) {
            return myColor(clusters[0])
          })
          .attr('offset', '0%')
        mainGradient
          .append('stop')
          .style('stop-color', function(d) {
            return myColor(clusters[1])
          })
          .attr('offset', '25%')
        mainGradient
          .append('stop')
          .style('stop-color', function(d) {
            return myColor(clusters[2])
          })
          .attr('offset', '50%')
        mainGradient
          .append('stop')
          .style('stop-color', function(d) {
            return myColor(clusters[3])
          })
          .attr('offset', '75%')
        mainGradient
          .append('stop')
          .style('stop-color', function(d) {
            return myColor(clusters[4])
          })
          .attr('offset', '100%')

        let legendContainer = svg.append('g')
        let legend = legendContainer.selectAll('.heatLegend').data(clusters)

        let legendSection = legend
          .enter()
          .append('g')
          .attr('class', 'heatLegend')

        let min = this.getRoundedNumber(yMin, 'floor')
        let max = this.getRoundedNumber(yMax, 'ceil')

        legendSection
          .append('rect')
          .attr('x', function(d, i) {
            return self.$mobile ? 0 : width / 4
          })
          .attr('y', function(d) {
            return height + 40
          })
          .attr('width', self.$mobile ? width : width / 2)
          .attr('height', 10)
          .style('fill', 'url(#' + legendref + ')')

        legendSection
          .append('text')
          .attr('class', 'legendLabel')
          .attr('x', function(d) {
            return self.$mobile ? 0 : width / 4
          })
          .attr('y', function(d) {
            return height + 70
          })
          .text(function(d) {
            return min + ' ' + unit
          })
        // let textWidth = max.node().getComputedTextLength()
        // console.log("textWidth",textWidth)
        legendSection
          .append('text')
          .text(function(d) {
            return max + ' ' + unit
          })
          .attr('class', 'legendLabel')
          .attr('x', function(d) {
            return self.$mobile
              ? width - this.getComputedTextLength()
              : width / 4 + width / 2 - this.getComputedTextLength()
          })
          .attr('y', function(d) {
            return height + 70
          })
      }
    },
    getTooltipColor(value, myColor, yMin, yMax, clusters) {
      let c = value !== null ? myColor(value) : '#FFFFFF'
      if (value !== null && value < yMin) {
        c = myColor(clusters[0])
      } else if (value !== null && value > yMax) {
        c = myColor(clusters[4])
      }
      return c
    },
    getTooltipFormat() {
      switch (this.xformat) {
        case 'hours':
          return 'dddd, MMM D, YYYY h a'
        case 'days':
          return 'dddd, MMM D, YYYY'
        case 'weeks':
          return 'dddd, MMM D, [W]ww gggg'
        default:
          return 'LLLL'
      }
    },
    getOptionsFromTypeChart(options, ctype) {
      //to set the chart type based on the widget

      if (this.config && this.config.widget && this.config.widget.dataOptions) {
        this.config.widget.dataOptions.chartType = ctype.chartTypeInt
        this.config.widget.dataOptions.chartTypeInt = ctype.chartTypeInt
      }
    },
  },
}
</script>

<style>
.widget-body .widget-heatmap {
  margin-top: 10px;
}

.heatmap .fc-new-chart-type-single {
  top: -42px;
  right: 70px !important;
}
</style>
