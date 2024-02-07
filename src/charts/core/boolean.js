import * as d3 from 'd3'
// import colors from 'charts/helpers/colors'
import formatter from '../helpers/formatter'
import moment from 'moment-timezone'
import Vue from 'vue'
import tooltip from '@/graph/mixins/tooltip'
const MomentRange = require('moment-range')

export default {
  drawBoolean(chartcontext) {
    let datas = chartcontext.fchart.data.map(rt => rt.data2)
    chartcontext.datas = datas
    let options = chartcontext.fchart.data.map(rt => rt.title)
    // let k = chartcontext.fchart.data[0].options.booleanKey
    let taskStatus = {
      '0': 'bar',
      '1': 'bar-null',
    }
    let names = []
    if (options.length) {
      names = options
    }
    let data = []
    if (datas.length) {
      for (let i = 0; i < datas.length; i++) {
        data = data.concat(data, datas[i])
      }
    }
    data.sort(function(a, b) {
      return a.endTime - b.endTime
    })
    let format = '%H:%M'

    let gantt = this.boolean(data, chartcontext)
      .taskTypes(names)
      .taskStatus(taskStatus)
      .tickFormat(format)
    chartcontext.data = data
    gantt(data, chartcontext)
  },
  boolean(data, chartcontext) {
    let FIT_TIME_DOMAIN_MODE = 'fit'

    let margin = {
      top: 50,
      right: 80,
      bottom: 0,
      left: 60,
    }
    let min =
      chartcontext.options.date_range && chartcontext.options.date_range.length
        ? chartcontext.options.date_range[0]
        : null
    let max =
      chartcontext.options.date_range && chartcontext.options.date_range.length
        ? chartcontext.options.date_range[1]
        : null
    chartcontext.min = min
    chartcontext.max = max
    let timeDomainStart = d3.timeDay(min)
    let timeDomainEnd = d3.timeHour.offset(max)
    let timeDomainMode = FIT_TIME_DOMAIN_MODE // fixed or fit
    let taskTypes = []
    let taskStatus = []
    let height = chartcontext.fchart.chartHeight
      ? chartcontext.fchart.chartHeight
      : 400

    let diff =
      chartcontext.fchart.getSecondChartType &&
      chartcontext.fchart.getSecondChartType !== 'timeseries'
        ? 0
        : 21
    let transulate =
      chartcontext.fchart.getSecondChartType &&
      chartcontext.fchart.getSecondChartType !== 'timeseries'
        ? 35
        : 0

    let width = chartcontext.fchart.chartWidth
      ? chartcontext.fchart.chartWidth
      : 800
    let keys = ['0', '1']
    chartcontext.keys = keys
    let tickFormat = '%H:%M'

    let keyFunction = function(d) {
      return d.startTime + d.name + d.endTime
    }

    let rectTransform = function(d) {
      if (chartcontext.datas && chartcontext.datas.length === 1) {
        let transulate = ''
        if (
          chartcontext.fchart.layout &&
          chartcontext.fchart.layout.height === 180
        ) {
          transulate = (height - margin.left - margin.right - 40) / 2 + 22.5
        } else {
          transulate = (height - margin.left - margin.right - 10) / 2 + 25
        }
        return (
          'translate(' +
          (x(d.startTime) + margin.left) +
          ',' +
          (y(d.name) + transulate) +
          ')'
        )
      } else {
        return (
          'translate(' + (x(d.startTime) + margin.left) + ',' + y(d.name) + ')'
        )
      }
    }
    let x, y, xAxis, yAxis, monthAxis
    initAxis(min, max, chartcontext)
    function initAxis(min, max, chartcontext) {
      function getXAxisDomain(dateRange, axis) {
        const momentRange = MomentRange.extendMoment(moment)
        let range = momentRange.range(
          new Date(dateRange[0]),
          new Date(dateRange[1])
        )
        let formatConfig = formatter.getDateFormatConfig(axis)
        return Array.from(range.by(formatConfig.interval)).map(
          r =>
            new Date(
              moment(r.valueOf())
                .tz(Vue.prototype.$timezone)
                .startOf(formatConfig.period)
                .valueOf()
            )
        )
      }

      if (
        chartcontext.fchart.layout &&
        chartcontext.fchart.layout.height === 180 &&
        chartcontext.datas &&
        chartcontext.datas.length === 1
      ) {
        x = d3
          .scaleTime()
          .domain([min, max])
          .range([0, width + diff])
      } else {
        x = d3
          .scaleTime()
          .domain([min, max])
          .range([0, width - margin.left - margin.right])
      }

      y = d3
        .scaleBand()
        .domain(taskTypes)
        .rangeRound([0, height - margin.top - margin.bottom], 0.1)

      /* month axis */
      if (
        chartcontext &&
        chartcontext.options &&
        (chartcontext.options.xaxis.datatype === 'date_time' ||
          chartcontext.options.xaxis.datatype === 'date')
      ) {
        let formatConfig = formatter.getDateFormatConfig(
          chartcontext.options.xaxis
        )
        let idx = 0
        monthAxis = d3
          .axisBottom()
          .scale(x)
          .tickFormat(function(d) {
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
              } else if (
                formatConfig.period === 'month' &&
                minorVal !== 'Jan'
              ) {
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
          .tickSizeOuter(0)
          .tickPadding(15)
          .tickSize(0)
      }
      let xaxisConfig = chartcontext.options.xaxis
        ? chartcontext.options.xaxis
        : { datatype: 'string' }
      let xDomain = getXAxisDomain(chartcontext.options.date_range, xaxisConfig)
      let days = chartcontext.options.timeObject
        ? chartcontext.options.timeObject.field
        : null
      let ticks = days === 'D' ? 15 : 20
      ticks = days === 'W' ? 7 : ticks
      ticks = days === 'M' ? 15 : ticks
      ticks = days === 'Y' ? 12 : ticks

      if (chartcontext.fchart.chartWidth > 500) {
        ticks = Math.round(ticks)
      } else if (chartcontext.fchart.chartWidth > 300) {
        ticks = Math.round(ticks / 3)
      } else if (chartcontext.fchart.chartWidth > 200) {
        ticks = Math.round(ticks / 6)
      } else if (
        chartcontext.fchart.chartWidth > 0 &&
        chartcontext.fchart.chartWidth < 200
      ) {
        ticks = Math.round(ticks / 9)
      }
      let ticksSplit = Math.round(xDomain.length / ticks)
      xAxis = d3
        .axisBottom()
        .scale(x)
        .tickFormat(function(d) {
          if (
            chartcontext &&
            chartcontext.options &&
            (chartcontext.options.xaxis.datatype === 'date_time' ||
              chartcontext.options.xaxis.datatype === 'date')
          ) {
            let formatConfig = formatter.getDateFormatConfig(
              chartcontext.options.xaxis
            )
            if (formatConfig.minor) {
              return moment(d)
                .tz(Vue.prototype.$timezone)
                .format(formatConfig.minor)
            }
            return ''
          }
        })
        .tickSizeOuter(0)
        .tickPadding(7)
        .tickSize(0)
        .ticks(ticks)
        .tickValues(
          xDomain.filter(function(_d, i) {
            return !(i % ticksSplit)
          })
        )
      monthAxis.tickValues(
        xDomain.filter(function(d, i) {
          return !(i % ticksSplit)
        })
      )
      // xAxis = d3.axisBottom().scale(x).tickFormat(d3.timeFormat(tickFormat)).tickSize([0]).tickPadding(8)

      yAxis = d3
        .axisLeft()
        .scale(y)
        .tickSize(0)
    }

    function gantt(data, chartcontext) {
      initAxis(chartcontext.min, chartcontext.max, chartcontext)
      height = chartcontext.fchart.chartHeight
        ? chartcontext.fchart.chartHeight
        : 400
      width = chartcontext.fchart.chartWidth
        ? chartcontext.fchart.chartWidth
        : 800
      let svg = chartcontext.fchart.svg
      if (
        chartcontext.fchart.layout &&
        chartcontext.fchart.layout.height === 180 &&
        chartcontext.datas &&
        chartcontext.datas.length === 1
      ) {
        chartcontext.booleanConatiner = svg
          .append('g')
          .attr('class', 'boolean-conatiner')
          .attr('transform', 'translate(' + transulate + ',' + 155 + ')')
      } else {
        chartcontext.booleanConatiner = svg
          .append('g')
          .attr('class', 'boolean-conatiner')
          .attr(
            'transform',
            'translate(' + margin.left + ',' + margin.left + ' )'
          )
      }

      chartcontext.booleanConatiner
        .selectAll('boolean-conatiner')
        .data(data, keyFunction)
        .enter()
        .append('rect')
        .attr('class', function(d) {
          if (taskStatus[d.fo] == null) {
            return 'bar'
          }
          return taskStatus[d.value]
        })
        .attr('y', 0)
        .attr('transform', rectTransform)
        .attr('height', function() {
          let length = chartcontext.datas.length
          if (
            chartcontext.fchart.layout &&
            chartcontext.fchart.layout.height === 180 &&
            chartcontext.datas &&
            chartcontext.datas.length === 1
          ) {
            return 29
          } else {
            return (
              (height -
                margin.top -
                margin.bottom -
                (height - margin.top - margin.bottom) / 3) /
              (length === 1 ? 5 : length)
            )
          }
        })
        .attr('width', function(d) {
          return x(d.endTime) - x(d.startTime)
        })
        .attr('fill', function(d) {
          // return colors.default[chartcontext.keys.indexOf(d.value + '')]
          let color =
            chartcontext.options.color && chartcontext.options.color.split(',')
          if (
            d.value === '1' ||
            d.value === 1 ||
            d.value === true ||
            d.value === 'true' ||
            d.value === 'on' ||
            d.value === 'ON'
          ) {
            return color && color.length === 2 ? color[0] : '#74d2c8'
          } else {
            return color && color.length === 2 ? color[1] : '#f3fafa'
          }
        })
        .on('mouseover', function(d) {
          chartcontext.hover = false
          d.value =
            d.value === '1' ||
            d.value === 1 ||
            d.value === true ||
            d.value === 'true' ||
            d.value === 'on' ||
            d.value === 'ON'
              ? 'ON'
              : d.value === '0' ||
                d.value === 0 ||
                d.value === false ||
                d.value === 'false' ||
                d.value === 'off' ||
                d.value === 'OFF'
              ? 'OFF'
              : d.value
          let diffTime = moment(d.endTime).diff(d.startTime)
          let duration = moment.duration(diffTime)
          let data = chartcontext.options.timeObject
            ? chartcontext.options.timeObject.field
            : null

          let description =
            data === 'D'
              ? ' ' +
                duration.hours() +
                ' hours' +
                ' ' +
                duration.minutes() +
                ' minutes'
              : ' ' + duration.days() + ' days'
          description =
            ' ' + duration.days() + ' days' === ' 0 days'
              ? ' ' +
                duration.hours() +
                ' hours' +
                ' ' +
                duration.minutes() +
                ' minutes'
              : ' ' + duration.days() + ' days'
          // description = (' ' + duration.days() + ' hours' === ' 0 hours') ? ' ' + duration.days() + ' hours' + ' ' + duration.minutes() + ' minutes' : ' ' + duration.days() + ' hours'
          let color =
            chartcontext.options.color && chartcontext.options.color.split(',')
          let tooltipData = [
            {
              label: chartcontext.options.xaxis.title,
              value:
                moment(d.startTime)
                  .tz(Vue.prototype.$timezone)
                  .format('DD MMM, HH:mm a') +
                ' - ' +
                moment(d.endTime)
                  .tz(Vue.prototype.$timezone)
                  .format('DD MMM, hh:mm a'),
              axis: chartcontext.options.xaxis,
            },
            {
              label: chartcontext.options.y1axis
                ? chartcontext.options.y1axis.title
                : chartcontext.options.y2axis.title,
              value:
                d.value === '1' ||
                d.value === 1 ||
                d.value === true ||
                d.value === 'true' ||
                d.value === 'on' ||
                d.value === 'ON'
                  ? 'ON'
                  : d.value === false ||
                    d.value === 'false' ||
                    d.value === 'off' ||
                    d.value === 'OFF'
                  ? 'OFF'
                  : d.value,
              axis: chartcontext.options.y1axis
                ? chartcontext.options.y1axis
                : chartcontext.options.y2axis,
              description: description,
            },
          ]
          let tooltipConfig = {
            title1: d.title,
            mode: 'boolean',
            position: {
              left: d3.event.pageX,
              top: d3.event.pageY,
            },
            data: tooltipData,
            color:
              d.value === '1' || d.value === 1
                ? color && color.length === 2
                  ? color[0]
                  : '#74d2c8'
                : color && color.length === 2
                ? color[1]
                : '#f3fafa',
          }
          if (d.formatted_date) {
            tooltipConfig.title1 = d.formatted_date
          }
          tooltip.showTooltip(tooltipConfig, chartcontext)
        })
        .on('mouseout', function(_d) {
          chartcontext.hover = true
          tooltip.hideTooltip()
        })

      chartcontext.booleanConatiner
        .append('g')
        .attr('class', 'x axis')
        .attr(
          'transform',
          'translate(' +
            margin.left +
            ',' +
            (height - margin.top - margin.bottom) +
            ')'
        )
        .transition()
        .call(xAxis)

      chartcontext.booleanConatiner
        .append('g')
        .attr('class', 'month-axis')
        .attr(
          'transform',
          'translate(' +
            margin.left +
            ',' +
            (height + 10 - margin.top - margin.bottom) +
            ')'
        )
        .transition()
        .call(monthAxis)

      chartcontext.yconatiner = chartcontext.booleanConatiner
        .append('g')
        .attr('class', 'y  y-axis-group axis')
        .transition()
        .call(yAxis)

      if (chartcontext.datas && chartcontext.datas.length === 1) {
        if (
          chartcontext.fchart.layout &&
          chartcontext.fchart.layout.height === 180
        ) {
          chartcontext.yconatiner.attr(
            'transform',
            'translate(' + 120 + ', -55)'
          )
        } else {
          chartcontext.yconatiner.attr(
            'transform',
            'translate(' + 180 + ', -45)'
          )
        }
      } else {
        chartcontext.yconatiner.attr(
          'transform',
          'translate(' + (margin.left - 10) + ', -5)'
        )
      }

      return gantt
    }

    gantt.redraw = function(data) {
      // initTimeDomain()
      initAxis()

      let svg = d3.select('svg')

      let ganttChartGroup = svg.select('.gantt-chart')
      let rect = ganttChartGroup.selectAll('rect').data(data, keyFunction)

      rect
        .enter()
        .insert('rect', ':first-child')
        .attr('rx', 5)
        .attr('ry', 5)
        .attr('class', function(d) {
          if (taskStatus[d.value] == null) {
            return 'bar'
          }
          return taskStatus[d.value]
        })
        .transition()
        .attr('y', 0)
        .attr('transform', rectTransform)
        .attr('height', function() {
          return y.range()[1]
        })
        .attr('width', function(d) {
          return x(d.endTime) - x(d.startTime)
        })

      rect
        .transition()
        .attr('transform', rectTransform)
        .attr('height', function() {
          return y.range()[1]
        })
        .attr('width', function(d) {
          return x(d.endTime) - x(d.startTime)
        })

      rect.exit().remove()

      svg
        .select('.x')
        .transition()
        .call(xAxis)
      svg
        .select('.y')
        .transition()
        .call(yAxis)

      return gantt
    }

    gantt.margin = function(value) {
      if (!arguments.length) {
        return margin
      }
      margin = value
      return gantt
    }

    // gantt.timeDomain = function (value) {
    //   if (!arguments.length) { return [ timeDomainStart, timeDomainEnd ]}
    //   timeDomainStart = +value[0], timeDomainEnd = +value[1]
    //   return gantt
    // }
    gantt.timeDomainMode = function(value) {
      if (!arguments.length) {
        return timeDomainMode
      }
      timeDomainMode = value
      return gantt
    }

    gantt.taskTypes = function(value) {
      if (!arguments.length) {
        return taskTypes
      }
      taskTypes = value
      return gantt
    }

    gantt.taskStatus = function(value) {
      if (!arguments.length) {
        return taskStatus
      }
      taskStatus = value
      return gantt
    }

    gantt.width = function(value) {
      if (!arguments.length) {
        return width
      }
      width = +value
      return gantt
    }

    gantt.height = function(value) {
      if (!arguments.length) {
        return height
      }
      height = +value
      return gantt
    }

    gantt.tickFormat = function(value) {
      if (!arguments.length) {
        return tickFormat
      }
      tickFormat = value
      return gantt
    }

    return gantt
  },
}
