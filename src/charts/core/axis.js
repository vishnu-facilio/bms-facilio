import * as d3 from 'd3'
import formatter from 'charts/helpers/formatter'
import common from 'charts/helpers/common'
import Vue from 'vue'
import moment from 'moment-timezone'
import tooltip from '@/graph/mixins/tooltip'

const MomentRange = require('moment-range')

let textWidthCanvas = null

export default {
  getXAxisDomain(dateRange, axis) {
    const momentRange = MomentRange.extendMoment(moment)
    let range = momentRange.range(
      new Date(dateRange[0]),
      new Date(dateRange[1])
    )
    let currentTimezone = Vue.prototype.$timezone
    let formatConfig = formatter.getDateFormatConfig(axis)
    return Array.from(range.by(formatConfig.interval)).map(
      r =>
        new Date(
          moment(r.valueOf())
            .tz(currentTimezone)
            .startOf(formatConfig.period)
            .valueOf()
        )
    )
  },
  getSafelimitMaxValue(safelimit, ymax) {
    if (safelimit) {
      let maxvalue = 0
      for (let i = 0; i < safelimit.length; i++) {
        if (safelimit[i].safelimitValue > maxvalue) {
          maxvalue = safelimit[i].safelimitValue
        }
      }
      if (maxvalue > ymax) {
        return maxvalue
      } else {
        return ymax
      }
    }
  },
  buildScales(chartContext) {
    let xaxisConfig = chartContext.getOptions().xaxis
      ? chartContext.getOptions().xaxis
      : {
          datatype: 'string',
        }

    let yAxisRange = chartContext.getYAxisRange()
    let yMax =
      chartContext.getOptions().safelimit &&
      chartContext.getOptions().safelimit.length
        ? this.getSafelimitMaxValue(
            chartContext.getOptions().safelimit,
            yAxisRange.y1axis.max
          )
        : yAxisRange.y1axis.max
    chartContext.yMax =
      chartContext.getOptions().safelimit &&
      chartContext.getOptions().safelimit.length
        ? this.getSafelimitMaxValue(
            chartContext.getOptions().safelimit,
            yAxisRange.y1axis.max
          )
        : yMax

    let xDomain = null
    if (
      (xaxisConfig.datatype === 'date' ||
        xaxisConfig.datatype === 'date_time') &&
      chartContext.getOptions().date_range
    ) {
      common.sortDateAscending(chartContext.getMainData())
      xDomain = this.getXAxisDomain(
        chartContext.getOptions().date_range,
        xaxisConfig
      )
    } else if (
      chartContext.getOptions().xaxis.datatype === 'number' ||
      chartContext.getOptions().xaxis.datatype === 'decimal'
    ) {
      common.sortAscending1(chartContext.getMainData())
      xDomain = chartContext.getMainData().map(chartContext.getLabel)
    } else {
      xDomain = chartContext.getMainData().map(chartContext.getLabel)
    }
    chartContext.xDomain = xDomain

    if (chartContext.getOptions().isHorizontal) {
      chartContext.xScale = d3
        .scaleLinear()
        .domain([0, yMax])
        .rangeRound([0, chartContext.chartWidth - 1])

      chartContext.yScale = d3
        .scaleBand()
        .domain(xDomain)
        .rangeRound([chartContext.chartHeight, 0])
        .padding(0.2)

      if (chartContext.getOptions().type === 'groupedbar') {
        chartContext.yScale2 = d3
          .scaleBand()
          .domain(common.getGroup(chartContext.getMainData()))
          .rangeRound([chartContext.yScale.bandwidth(), 1])
          .padding(0.1)
      }
    } else {
      if (
        chartContext.getOptions().xaxis.datatype === 'number' ||
        chartContext.getOptions().xaxis.datatype === 'decimal'
      ) {
        chartContext.xScale = d3
          .scaleLinear()
          .domain([d3.min(xDomain), d3.max(xDomain)])
          .rangeRound([0, chartContext.chartWidth])
          .nice()
      } else if (
        chartContext.getOptions().type === 'line' ||
        chartContext.getOptions().type === 'area' ||
        chartContext.getOptions().type === 'scatter'
      ) {
        chartContext.xScale = d3
          .scaleTime()
          .domain([xDomain[0], xDomain[xDomain.length - 1]])
          .rangeRound([0, chartContext.chartWidth])
      } else {
        chartContext.xScale = d3
          .scaleBand()
          .domain(xDomain)
          .rangeRound([0, chartContext.chartWidth])
          .padding(chartContext.getOptions().type === 'groupedbar' ? 0.2 : 0.1)
      }

      if (chartContext.getOptions().type === 'groupedbar') {
        chartContext.xScale2 = d3
          .scaleBand()
          .domain(common.getGroup(chartContext.getMainData()))
          .rangeRound([0, chartContext.xScale.bandwidth()])
          .padding(0.05)
      }

      chartContext.yScale = d3
        .scaleLinear()
        .domain([0, yMax])
        .rangeRound([chartContext.chartHeight, 0])
        .nice()
      chartContext.getOptions().yScale = chartContext.yScale

      if (yAxisRange.y2axis) {
        chartContext.yMax2 = yAxisRange.y2axis.max
        chartContext.yScale2 = d3
          .scaleLinear()
          .domain([0, yAxisRange.y2axis.max])
          .rangeRound([chartContext.chartHeight, 0])
          .nice()

        if (yAxisRange.y2axis.series) {
          for (let serie of yAxisRange.y2axis.series) {
            serie.options.yScale = chartContext.yScale2
          }
        }
      }
    }

    chartContext.categoryColorMap = chartContext.getLegends()
  },

  drawsafeLimit(chartContext) {
    if (
      chartContext.getOptions().safelimit &&
      chartContext.getOptions().safelimit.length
    ) {
      let safelimit = chartContext.getOptions().safelimit
      for (let i = 0; i < chartContext.getOptions().safelimit.length; i++) {
        let safeLimitLineClass = 'safelimit-line'
        let safeLimitLabelClass = 'safelimit-label'
        let safeLimitValueClass = 'safelimit-value'
        if (safelimit[i].lineStyle) {
          safeLimitLineClass = safeLimitLineClass + '-' + safelimit[i].lineStyle
          safeLimitLabelClass =
            safeLimitLabelClass + '-' + safelimit[i].lineStyle
          safeLimitValueClass =
            safeLimitValueClass + '-' + safelimit[i].lineStyle
        } else {
          safeLimitLineClass = safeLimitLineClass + '-' + 'default'
        }
        let safeLimits = chartContext.svg.select('.container-group').append('g')
        safeLimits
          .append('circle')
          .attr('cx', 0)
          .attr('cy', chartContext.yScale(safelimit[i].safelimitValue))
          .attr('r', 5)
          .attr('class', 'safe-circle')
          .on('mouseover', function(d) {
            chartContext.hover = false
            d3.select(this)
              .transition()
              .duration(500)
              .attr('r', 10)
            d3.select('#safe' + i)
              .transition()
              .duration(500)
              .attr('stroke-width', '2px')

            let tooltipData = [
              {
                label: safelimit[i].safelimitName
                  ? safelimit[i].safelimitName
                  : '',
                value: safelimit[i].safelimitValue
                  ? safelimit[i].formated_value
                  : '',
              },
            ]
            let tooltipConfig = {
              header: safelimit[i].header,
              title1: safelimit[i].title,
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              data: tooltipData,
              color: safelimit[i].color
                ? safelimit[i].color
                : chartContext.getColorSchema()[safelimit[i].index],
            }
            tooltip.showTooltip(tooltipConfig, chartContext)
          })
          .on('mouseout', function(d) {
            chartContext.hover = true
            tooltip.hideTooltip()
            d3.select(this)
              .transition()
              .duration(500)
              .attr('r', 5)
            d3.select('#safe' + i)
              .transition()
              .duration(500)
              .attr('stroke-width', '1px')
          })
          .attr(
            'fill',
            safelimit[i].color
              ? safelimit[i].color
              : chartContext.getColorSchema()[safelimit[i].index]
          )
        safeLimits
          .append('circle')
          .attr('cx', 0)
          .attr('cy', chartContext.yScale(safelimit[i].safelimitValue))
          .attr('r', 1)
          .attr('fill', '#fff')
          .on('mouseover', function() {})
        safeLimits
          .append('line')
          .attr('x1', 5)
          .attr('y1', chartContext.yScale(safelimit[i].safelimitValue))
          .attr('y2', chartContext.yScale(safelimit[i].safelimitValue))
          .attr('x2', chartContext.chartWidth)
          .style('shape-rendering', 'crispEdges')
          .attr('id', 'safe' + i)
          .attr(
            'stroke',
            safelimit[i].color
              ? safelimit[i].color
              : chartContext.getColorSchema()[safelimit[i].index]
          )
          .attr('class', 'safelimit-line ' + safeLimitLineClass)
        safeLimits
          .append('text')
          .attr('x', 10)
          .attr('y', chartContext.yScale(safelimit[i].safelimitValue) - 15)
          .attr('dy', '1em')
          .attr('text-anchor', 'start')
          .html(safelimit[i].formated_value)
          .attr(
            'fill',
            safelimit[i].color
              ? safelimit[i].color
              : chartContext.getColorSchema()[safelimit[i].index]
          )
          .attr('class', 'safelimit safelimit-value ' + safeLimitValueClass)
      }
    }
  },

  getTextWidth(text) {
    // re-use canvas object for better performance
    let canvas =
      textWidthCanvas || (textWidthCanvas = document.createElement('canvas'))
    let context = canvas.getContext('2d')
    let metrics = context.measureText(text)
    return metrics.width
  },
  gettextSliceLength(minwidth, length, maxwidth, chartContext) {
    let l = Math.round(minwidth / length)
    const newLocal = Math.round(maxwidth / l)
    const len = Math.round((newLocal / 100) * 80)
    return len
  },
  getFormatedLabel(bandwidth, d, chartContext, bw) {
    let self = this
    if (chartContext) {
      if (bw) {
        if (self.getTextWidth(d) > bw) {
          chartContext.lableroate = true
        }
      } else {
        if (self.getTextWidth(d) > bandwidth) {
          chartContext.lableroate = true
        }
      }
    }
    if (self.getTextWidth(d) > bandwidth + bandwidth / 10 && d.slice) {
      return (
        d.slice(
          0,
          self.gettextSliceLength(
            self.getTextWidth(d),
            d.length,
            Math.round(bandwidth)
          ),
          chartContext
        ) + '...'
      )
    } else {
      return d
    }
  },
  lableLength(chartContext) {
    let label = chartContext.xDomain
    let length = 0
    for (let i = 0; i < label.length; i++) {
      length = length + this.getTextWidth(label[i])
    }
    return length
  },
  buildAxis(chartContext) {
    if (chartContext.getOptions().isHorizontal) {
      chartContext.xAxis = d3
        .axisBottom(chartContext.xScale)
        .tickSizeInner([-chartContext.chartHeight])

      chartContext.yAxis = d3.axisLeft(chartContext.yScale)
    } else {
      let ticks = 10
      if (chartContext.chartWidth > 800) {
        ticks = 31
      } else if (chartContext.chartWidth > 500) {
        ticks = 15
      } else if (chartContext.chartWidth > 300) {
        ticks = 7
      } else if (chartContext.chartWidth > 200) {
        ticks = 4
      } else if (chartContext.chartWidth > 0 && chartContext.chartWidth < 200) {
        ticks = 3
      }
      let ticksSplit = Math.round(chartContext.xDomain.length / ticks)
      chartContext.ticksSplit = Math.round(chartContext.xDomain.length / ticks)
      if (
        chartContext.getOptions().xaxis.datatype === 'number' ||
        chartContext.getOptions().xaxis.datatype === 'decimal'
      ) {
        chartContext.xAxis = d3
          .axisBottom(chartContext.xScale)
          .tickSize([0])
          .tickPadding(15)
      } else {
        let self = this
        chartContext.totalLabelWidth = 0
        chartContext.totalLabelWidth = self.lableLength(chartContext)
        chartContext.xAxis = d3
          .axisBottom(chartContext.xScale)
          .tickValues(
            chartContext.xDomain.filter(function(d, i) {
              return !(i % ticksSplit)
            })
          )
          .tickSize([0])
          .tickPadding(15)
      }

      if (
        chartContext.getOptions().xaxis.datatype === 'date' ||
        chartContext.getOptions().xaxis.datatype === 'date_time'
      ) {
        let formatConfig = formatter.getDateFormatConfig(
          chartContext.getOptions().xaxis
        )

        chartContext.xAxis.tickFormat(function(d) {
          return moment(d)
            .tz(Vue.prototype.$timezone)
            .format(formatConfig.minor)
        })

        chartContext.xMonthAxis = d3
          .axisBottom(chartContext.xScale)
          .tickSize(0, 0)
          .tickValues(
            chartContext.xDomain.filter(function(d, i) {
              return !(i % ticksSplit)
            })
          )

        let idx = 0
        chartContext.xMonthAxis.tickFormat(function(d) {
          idx++
          let minorVal = moment(d)
            .tz(Vue.prototype.$timezone)
            .format(formatConfig.minor)
          if (idx !== 1) {
            if (formatConfig.period === 'hour' && minorVal !== '12am') {
              return ''
            } else if (formatConfig.period === 'day' && minorVal !== '1') {
              return ''
            } else if (formatConfig.period === 'week' && minorVal !== '1') {
              return ''
            } else if (formatConfig.period === 'month' && minorVal !== 'Jan') {
              return ''
            }
          }
          if (formatConfig.major) {
            return moment(d)
              .tz(Vue.prototype.$timezone)
              .format(formatConfig.major)
          }
          return ''
        })
      } else if (
        chartContext.getOptions().xaxis.datatype === 'string' ||
        chartContext.getOptions().xaxis.datatype === 'lookup' ||
        chartContext.getOptions().xaxis.datatype === 'text'
      ) {
        let bandwidth = 2
        let self = this
        if (
          chartContext.xScale &&
          chartContext.xScale.hasOwnProperty('bandwidth')
        ) {
          bandwidth = chartContext.xScale.bandwidth()
        }
        let bandwidthChange =
          chartContext.ticksSplit && chartContext.ticksSplit !== 0
            ? chartContext.ticksSplit
            : 1
        bandwidth = bandwidth * bandwidthChange
        if (
          chartContext.options.id === 3481 ||
          chartContext.options.id === 3663 ||
          chartContext.options.id === 3664 ||
          chartContext.options.id === 3668 ||
          chartContext.options.id === 3754 ||
          chartContext.options.id === 1122 ||
          chartContext.options.id === 1138 ||
          chartContext.options.id === 2871
        ) {
          chartContext.xAxis.tickFormat(function(d) {
            return self.getFormatedLabel(
              bandwidth * 2,
              d,
              chartContext,
              bandwidth * 1
            )
          })
        } else {
          chartContext.xAxis.tickFormat(function(d) {
            return self.getFormatedLabel(bandwidth, d)
          })
        }
      }

      chartContext.yAxis = d3
        .axisLeft(chartContext.yScale)
        .ticks(chartContext.yMax > 5 ? 5 : chartContext.yMax)
        .tickSize(-chartContext.chartWidth - 10)
        .tickPadding(7)

      let yaxisConfig = chartContext.getOptions().y1axis
        ? chartContext.getOptions().y1axis
        : {
            datatype: 'number',
          }
      formatter.formatAxis(chartContext.yAxis, yaxisConfig)

      if (chartContext.yScale2) {
        chartContext.yAxis2 = d3
          .axisRight(chartContext.yScale2)
          .ticks(chartContext.yMax2 > 5 ? 5 : chartContext.yMax2)
          .tickSize(-chartContext.chartWidth - 10)
          .tickPadding(7)

        let y2axisConfig = chartContext.getOptions().y2axis
          ? chartContext.getOptions().y2axis
          : {
              datatype: 'number',
            }
        formatter.formatAxis(chartContext.yAxis2, y2axisConfig)
      }
    }
  },
  buildScaleDualAxis(chartContext) {
    let yMax = common.getMinMax(chartContext.getData2()).max
    chartContext.yDualScale = d3
      .scaleLinear()
      .domain([0, yMax])
      .rangeRound([chartContext.chartHeight, 0])
      .nice()
    if (chartContext.getOptions().y1axis.isTitle) {
      // draw y axis label
      chartContext.svg
        .select('.y1-axis-group.axis')
        .append('text')
        .attr('transform', 'rotate(-90)')
        .attr('class', 'Yaxis-label')
        .attr('x', 0 - chartContext.chartHeight / 2)
        .attr('y', chartContext.getMargin().right)
        .style('text-anchor', 'middle')
        .attr('dy', '0em')
        .html(
          (chartContext.getOptions().y1axis
            ? chartContext.getOptions().y1axis.title.toUpperCase()
            : '') +
            (chartContext.getOptions().y1axis.unit
              ? ' (' +
                (chartContext.getOptions().y1axis
                  ? chartContext.getOptions().y1axis.datatype === 'currency'
                    ? Vue.prototype.$currency
                    : chartContext.getOptions().y1axis.unit === 'eui'
                    ? chartContext.$account.org.id === 78
                      ? 'kWh/m&sup2'
                      : 'kWh/ft&sup2'
                    : chartContext.getOptions().y1axis.unit
                  : '') +
                ')'
              : '')
        )
    }
    chartContext.y2Axis = d3
      .axisRight(chartContext.yDualScale)
      .ticks(5)
      .tickPadding(7)
    chartContext.svg.select('.y1-axis-group.axis').call(chartContext.y2Axis)
  },

  drawAxis(chartContext) {
    chartContext.svg
      .select('.x-axis-group .axis.x')
      .attr('transform', `translate(0, ${chartContext.chartHeight})`)
      .call(chartContext.xAxis)

    if (chartContext.xMonthAxis) {
      chartContext.svg
        .select('.x-axis-group .month-axis')
        .attr('transform', `translate(0, ${chartContext.chartHeight + 34})`)
        .call(chartContext.xMonthAxis)
    }

    chartContext.svg.select('.y-axis-group.axis').call(chartContext.yAxis)

    chartContext.svg
      .select('.x-axis-group .axis.x path.domain')
      .attr('d', 'M-20,0V0.5H' + (chartContext.chartWidth + 10) + '.5V0')
      .attr('class', 'x-axis-line-path')

    chartContext.svg
      .selectAll('.y-axis-group.axis .tick text')
      .attr('class', 'yaxis-tick-label')
      .attr('dy', '-0.5em')

    if (chartContext.yAxis2) {
      chartContext.svg.select('.y1-axis-group.axis').call(chartContext.yAxis2)

      chartContext.svg
        .selectAll('.y1-axis-group.axis .tick text')
        .attr('class', 'yaxis-tick-label')
        .attr('dy', '-0.5em')
    }

    this.titleXYWrite(chartContext, false)
  },

  titleXYWrite(chartContext, isLableRotating) {
    if (
      chartContext.getOptions().y1axis &&
      chartContext.getOptions().y1axis.title
    ) {
      // draw y axis label
      chartContext.svg
        .select('.y-axis-group.axis')
        .append('text')
        .attr('transform', 'rotate(-90)')
        .attr('class', 'Yaxis-label')
        .attr('x', 0 - chartContext.chartHeight / 2)
        .attr('y', 0 - chartContext.getMargin().left)
        .style('text-anchor', 'middle')
        .attr('dy', '1em')
        .html(
          (chartContext.getOptions().y1axis
            ? chartContext.getOptions().y1axis.title.toUpperCase()
            : '') +
            (chartContext.getOptions().y1axis.unit
              ? ' (' +
                (chartContext.getOptions().y1axis
                  ? chartContext.getOptions().y1axis.datatype === 'currency'
                    ? Vue.prototype.$currency
                    : chartContext.getOptions().y1axis.unit === 'eui'
                    ? chartContext.$account.org.id === 78
                      ? 'kWh/m&sup2'
                      : 'kWh/ft&sup2'
                    : chartContext.getOptions().y1axis.unit
                  : '') +
                ')'
              : '')
        )
    }

    let y2AxisOptions = chartContext.data.find(
      d => typeof d.options.y2axis !== 'undefined'
    )
    if (y2AxisOptions && y2AxisOptions.options.y2axis.title) {
      // draw y1 axis label
      chartContext.svg
        .select('.y1-axis-group.axis')
        .append('text')
        .attr('transform', 'rotate(-90)')
        .attr('class', 'Yaxis-label')
        .attr('x', 0 - chartContext.chartHeight / 2)
        .attr('y', chartContext.getMargin().right - 25)
        .style('text-anchor', 'middle')
        .attr('dy', '1em')
        .html(
          y2AxisOptions.options.y2axis.title.toUpperCase() +
            (y2AxisOptions.options.y2axis.unit
              ? ' (' +
                (y2AxisOptions.options.y2axis
                  ? y2AxisOptions.options.y2axis.datatype === 'currency'
                    ? Vue.prototype.$currency
                    : y2AxisOptions.options.y2axis.unit === 'eui'
                    ? chartContext.$account.org.id === 78
                      ? 'kWh/m&sup2'
                      : 'kWh/ft&sup2'
                    : y2AxisOptions.options.y2axis.unit
                  : '') +
                ')'
              : '')
        )
    }

    // draw x axis label
    if (chartContext.getOptions().xaxis.title) {
      if (isLableRotating) {
        let text = chartContext.svg
          .select('.x-axis-group.axis')
          .append('text')
          .attr('class', 'Xaxis-label')
          .style('text-anchor', 'middle')
        if (chartContext.getOptions().xaxis.operation === 'fulldate') {
          text
            .attr(
              'transform',
              'translate(' + chartContext.chartWidth / 2 + ' ,' + 100 + ')'
            )
            .html(
              chartContext.getOptions().xaxis
                ? chartContext.getOptions().xaxis.title.toUpperCase()
                : ''
            )
        } else if (chartContext.getOptions().xaxis.operation === 'hoursofday') {
          text
            .attr(
              'transform',
              'translate(' + chartContext.chartWidth / 2 + ' ,' + 60 + ')'
            )
            .html(
              chartContext.getOptions().xaxis
                ? chartContext.getOptions().xaxis.title.toUpperCase()
                : ''
            )
        } else {
          text
            .attr(
              'transform',
              'translate(' + chartContext.chartWidth / 2 + ' ,' + 120 + ')'
            )
            .html(
              chartContext.getOptions().xaxis
                ? chartContext.getOptions().xaxis.title.toUpperCase()
                : ''
            )
        }
      } else {
        chartContext.svg
          .select('.x-axis-group .axis.x')
          .append('text')
          .attr(
            'transform',
            'translate(' +
              chartContext.chartWidth / 2 +
              ' ,' +
              ((chartContext.options.id === 3481 ||
                chartContext.options.id === 3663 ||
                chartContext.options.id === 3664 ||
                chartContext.options.id === 3668 ||
                chartContext.options.id === 3754 ||
                chartContext.options.id === 1122 ||
                chartContext.options.id === 1138 ||
                chartContext.options.id === 2871) &&
              chartContext.lableroate
                ? 85
                : 60) +
              ')'
          )
          .attr('class', 'Xaxis-label')
          .style('text-anchor', 'middle')
          .attr('dy', '1em')
          .html(
            chartContext.getOptions().xaxis
              ? chartContext.getOptions().xaxis.title.toUpperCase()
              : ''
          )
      }
    }

    /* temp fix for reportID */
    if (
      chartContext.options.id === 3481 ||
      chartContext.options.id === 3663 ||
      chartContext.options.id === 3664 ||
      chartContext.options.id === 3668 ||
      chartContext.options.id === 3754 ||
      chartContext.options.id === 1122 ||
      chartContext.options.id === 1138 ||
      chartContext.options.id === 2871
    ) {
      if (chartContext.lableroate) {
        chartContext.svg
          .selectAll('.axis.x .tick text')
          .attr('transform', 'rotate(45)')
          .style('text-anchor', 'start')
      }
    }
  },
}
