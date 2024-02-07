// import tooltip from '@/graph/mixins/tooltip'
import common from 'charts/helpers/common'
// import formatter from 'charts/helpers/formatter
import tooltip from '@/graph/mixins/tooltip'
import moment from 'moment-timezone'
import Vue from 'vue'
import * as d3 from 'd3'
import ckmeans from 'third_party/ckmeans.min.js'

export default {
  drawHeatMap(chartObj, callbackFunc) {
    let data = chartObj.data.filter(rt => !rt.violated_value)

    // preparing chart
    let margin = chartObj.fchart.getMargin()
    let width = chartObj.fchart.chartWidth + margin.left - margin.left / 4
    let height = chartObj.fchart.chartHeight
    let svg = chartObj.fchart.svg
    let x = d3.scaleLinear().range([0, width]),
      y = d3.scaleTime().range([height, 0]),
      z = d3
        .scaleLinear()
        .range([
          '#1d7f01',
          '#6cb302',
          '#e9f501',
          '#fda504',
          '#fb5905',
          '#fa0000',
        ])
    let Ymonths = d3
      .scalePoint()
      .range([1, 12])
      .domain([
        'Jan',
        'Feb',
        'Mar',
        'Apr',
        'May',
        'Jun',
        'Jul',
        'Aug',
        'Sep',
        'Oct',
        'Nov',
        'Dec',
      ])
    let Ydata = [
      'Jul 20',
      'Jul 21',
      'Jul 22',
      'Jul 23',
      'Jul 24',
      'Jul 25',
      'Jul 26',
      'Jul 27',
      'Jul 28',
      'Jul 29',
      'Jul 30',
      '',
    ]
    let dayScale = d3
      .scalePoint()
      .range([0, height])
      .domain(Ydata)
    let timeScale = d3
      .scalePoint()
      .range([0, width])
      .domain([
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
        '',
      ])
    let timeScale2 = d3
      .scalePoint()
      .range([0, width])
      .domain([
        '12 AM',
        '01 AM',
        '02 AM',
        '03 AM',
        '04 AM',
        '05 AM',
        '06 AM',
        '07 AM',
        '08 AM',
        '09 AM',
        '10 AM',
        '11 AM',
        '12 PM',
        '01 PM',
        '02 PM',
        '03 PM',
        '04 PM',
        '05 PM',
        '06 PM',
        '07 PM',
        '08 PM',
        '09 PM',
        '10 PM',
        '11 PM',
        '',
      ])
    let daysOfDate = d3
      .scalePoint()
      .range([0, 6])
      .domain([
        'Sunday',
        'Monday',
        'Tuesday',
        'Wednesday',
        'Thursday',
        'Friday',
        'Saturday',
      ])
    function daysInMonth(month, year) {
      return new Date(year, month, 0).getDate()
    }
    function dayOfDate(year, month, day) {
      return new Date(year, month, day).getDay()
    }
    let currentHour =
      moment()
        .tz(Vue.prototype.$timezone)
        .format(WeekDay) +
      ',' +
      moment()
        .tz(Vue.prototype.$timezone)
        .format(fullDate)
    function prepareData() {
      if (
        currentHour === data[data.length - 1].fullDate ||
        data[data.length - 1].fullDate === data[data.length - 2].fullDate
      ) {
        data.pop()
      }
    }
    data.forEach(function(d) {
      d.formatedlabel = moment(d.label)
        .tz(Vue.prototype.$timezone)
        .format(formatDate)
      d.fullDate =
        moment(d.label)
          .tz(Vue.prototype.$timezone)
          .format(WeekDay) +
        ',' +
        moment(d.label)
          .tz(Vue.prototype.$timezone)
          .format(fullDate)
      d.value = +d.value
      d.heatData = +d.heatData
      d.month = moment(d.label)
        .tz(Vue.prototype.$timezone)
        .format(monthFormat)
      d.time = Number(
        moment(d.label)
          .tz(Vue.prototype.$timezone)
          .format(timeFormat)
      )
      d.monthInNumber =
        Number(
          moment(d.label)
            .tz(Vue.prototype.$timezone)
            .format('M')
        ) - 1
      d.yearInNumber = Number(
        moment(d.label)
          .tz(Vue.prototype.$timezone)
          .format('YYYY')
      )
      d.timeString = moment(d.label)
        .tz(Vue.prototype.$timezone)
        .format(newtimeFormat)
      d.monthAndYear = moment(d.label)
        .tz(Vue.prototype.$timezone)
        .format(monthAndYear)
      d.dayMonthYear = moment(d.label)
        .tz(Vue.prototype.$timezone)
        .format(dayMonthYear)
    })
    let monthAndYearLabel = []
    let fullDateLabel = []

    function getYlabel() {
      let dayCount = 0
      dayCount = daysInMonth(data[0].monthInNumber, data[0].yearInNumber)
      let month = data[0].monthAndYear
      monthAndYearLabel.push(month)

      for (let i = 0; i < data.length; i++) {
        if (data[i].violated_value) {
          data.splice(i, 1)
        }
        if (month !== data[i].monthAndYear) {
          month = data[i].monthAndYear
          monthAndYearLabel.push(month)
        } else {
          i++
        }
      }
      monthAndYearLabel.push('')
      return dayCount
    }

    let ylabelData = []
    let ylabelRawData = []
    function Ylabel1() {
      let temp = ''
      let year = data[0].yearInNumber
      let month = data[0].monthInNumber

      for (
        let i = data[0].yearInNumber;
        i <= data[data.length - 1].yearInNumber;
        i++
      ) {
        for (let j = month; j < 12; j++) {
          temp = year + ''
          temp = ' ' + Ymonths.domain()[j]
          ylabelData.push(temp)
          ylabelRawData.push(year + ' ' + j)
          temp = ''
          if (
            j === data[data.length - 1].monthInNumber &&
            year === data[data.length - 1].yearInNumber
          ) {
            break
          } else {
            month = 0
          }
        }
        year++
      }
      ylabelData.push('')
    }
    let yLabelData2 = []
    let fullData = []
    function ylabel2() {
      let splitYear = 0
      let splitMonth = 0
      for (let i = 0; i < ylabelRawData.length; i++) {
        splitYear = ylabelRawData[i].split(' ', 1)
        splitMonth = ylabelRawData[i].split(Number(splitYear[0]))

        for (
          let j = 1;
          j <= daysInMonth(Number(splitMonth[1]) + 1, Number(splitYear[0]));
          j++
        ) {
          yLabelData2.push(
            (j <= 9 ? '0' + j : j) +
              ' ' +
              Ymonths.domain()[Number(splitMonth[1])] +
              ' ' +
              Number(splitYear[0])
          )
          for (let i = -12; i <= 12; i++) {
            if (i !== 0) {
              fullData.push({
                label:
                  (j <= 9 ? '0' + j : j) +
                  ' ' +
                  Ymonths.domain()[Number(splitMonth[1])] +
                  ' ' +
                  Number(splitYear[0]) +
                  ' ' +
                  ((i < 0 ? -i : i) <= 9
                    ? '0' + (i < 0 ? -i : i)
                    : i < 0
                    ? -i
                    : i) +
                  ' ' +
                  (i < 0 ? 'AM' : 'PM'),
                value: 0,
                date:
                  (j <= 9 ? '0' + j : j) +
                  ' ' +
                  Ymonths.domain()[Number(splitMonth[1])] +
                  ' ' +
                  Number(splitYear[0]),
                time:
                  ((i < 0 ? -i : i) <= 9
                    ? '0' + (i < 0 ? -i : i)
                    : i < 0
                    ? -i
                    : i) +
                  ' ' +
                  (i < 0 ? 'AM' : 'PM'),
                day: daysOfDate.domain()[
                  dayOfDate(Number(splitYear[0]), Number(splitMonth[1]), j)
                ],
              })
            }
          }
        }
      }
      yLabelData2.push('')
    }

    // Compute the scale domains.
    let xMin = common.getMinMaxNumberLabel(data).min
    let xMax = common.getMinMaxNumberLabel(data).max
    let yMin = d3.min(data, function(d) {
      return d.value
    })
    let yMax = d3.max(data, function(d) {
      return d.value
    })

    let culsertdata = data.map(rt => rt.value)
    // let zvalue = (yMax - yMin) / 4
    // let dataMin = yMin
    x.domain([1, 25])
    y.domain([xMax, xMin])
    let result = ckmeans(culsertdata, 5)
    if (result[4] < yMax) {
      result.push(yMax)
    }
    z.domain(result)

    // Extend the x- and y-domain to fit the last bucket.
    // For example, the y-bucket 3200 corresponds to values [3200, 3300].

    ylabelRawData.push('')
    yLabelData2.push('')
    let w = width / 24
    let h = height / (yLabelData2.length - 1)

    let ytileRange = d3
      .scalePoint()
      .range([0, height])
      .domain(yLabelData2)
    let ylabelRange = d3
      .scalePoint()
      .range([0, height])
      .domain(ylabelData)
    let minValue = yMin
    let maxValue = yMax

    let container = svg
      .append('g')
      .attr('width', width - margin.left)
      .attr('height', height - (margin.top + margin.bottom))
      .attr('class', 'heatMap')
      .attr(
        'transform',
        'translate(' +
          margin.left / 4 +
          ',' +
          (chartObj.options.legendPostion === 'top'
            ? parseInt(margin.top) + 60
            : margin.top) +
          ')'
      )

    // Add an x-axis with label.
    container
      .append('g')
      .attr('class', 'x axis')
      .call(
        d3
          .axisTop()
          .scale(timeScale)
          .ticks(d3.timeHour)
          .tickSize([0])
      )
      .append('text')
      .attr('class', 'label')
      .attr('x', width)
      .attr('y', margin.top + 5)
      .attr('text-anchor', 'start')

    container.select('.x').selectAll('text')
    // .attr('transform', 'translate(' + w / 2 + ', 0 )')

    // Add a y-axis with label.
    container
      .append('g')
      .attr('class', 'y axis')
      .call(
        d3
          .axisLeft()
          .scale(ylabelRange)
          .ticks(ylabelData.length - 1)
          .tickSize([-width])
      )
      .append('text')
      .attr('class', 'label')
      .attr('y', 0)
      .attr('text-anchor', 'end')
      .attr('transform', 'rotate(-90)')

    container
      .append('g')
      .attr('class', 'yRight')
      .append('line')
      .attr('x1', width)
      .attr('y1', 0)
      .attr('y2', height)
      .attr('x2', width)
      .attr('stroke', '#9E9E9E')
      .attr('class', 'heatmap-rightScale')

    container
      .select('.y')
      .selectAll('text')
      .attr(
        'transform',
        'translate( -10, ' +
          ((height - (margin.top + margin.bottom)) /
            (ylabelData.length - 1) /
            2 +
            10) +
          ')' +
          'rotate(90)'
      )
      .style('text-transform', 'uppercase')
      .style('letter-spacing', '0.6px')
    container
      .selectAll('.tile')
      .data(fullData)
      .enter()
      .append('rect')
      .attr('class', 'tile_empty')
      .attr('x', function(d) {
        return timeScale2(d.time)
      })
      .attr('y', function(d) {
        return ytileRange(d.date)
      })
      .attr('width', w)
      .attr('height', h)
      .style('fill', function(d) {
        return '#f8f8f8'
      })
      .on('mousemove', function(d) {})
      .on('mouseover', function(d) {
        let tooltipData = [
          {
            label: chartObj.options.xaxis.title,
            value: d.day + ',' + d.label,
          },
          {
            label: chartObj.options.y1axis.title,
            value: 'No Data',
          },
        ]
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          data: tooltipData,
          color: '#f8f8f8',
        }
        tooltip.showTooltip(tooltipConfig, chartObj.fchart)
      })
      .on('mouseout', function(d) {
        tooltip.hideTooltip()
      })
    container
      .selectAll('.tile')
      .data(data)
      .enter()
      .append('rect')
      .attr('class', 'tile')
      .attr('x', function(d) {
        return timeScale2(d.timeString)
      })
      .attr('y', function(d) {
        return ytileRange(d.dayMonthYear)
      })
      .attr('width', w)
      .attr('height', h)
      .style('fill', function(d) {
        if (d.violated_value) {
          return '#f8f8f8'
        } else {
          return d.value <= minValue
            ? d.value >= maxValue
              ? '#fa1000'
              : '#1b7f01'
            : z(d.value)
        }
      })
      .on('mousemove', function(d) {})
      .on('click', function(d) {
        let data = {
          view: true,
          chartData: d,
          options: chartObj.options,
        }
        callbackFunc('click', data)
        tooltip.hideTooltip()
      })
      .on('mouseover', function(d) {
        let tooltipData = [
          {
            label: chartObj.options.xaxis.title,
            // label: d.dayMonthYear + ((d.timeString === '12 AM') ? '12 AM to 01 AM' : ((d.timeString === '11 PM') ? '11 PM to 12 AM' : (' ' + d.timeString + ' to ' + (+d.timeString)))),
            value: d.fullDate,
          },
          {
            label: chartObj.options.y1axis.title,
            value: d.violated_value ? 'No Data' : d.formatted_value,
          },
        ]
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          data: tooltipData,
          color: d.violated_value
            ? '#f8f8f8'
            : d.value <= minValue
            ? d.value >= maxValue
              ? '#fa1000'
              : '#1b7f01'
            : z(d.value),
        }
        tooltip.showTooltip(tooltipConfig, chartObj.fchart)
      })
      .on('mouseout', function(d) {
        tooltip.hideTooltip()
      })
    let svgDefs = container.append('defs')

    let mainGradient = svgDefs
      .append('linearGradient')
      .attr('id', 'legendGradient')

    // Create the stops of the main gradient. Each stop will be assigned
    // a class to style the stop using CSS.
    // mainGradient.append('stop')
    //   .style('stop-color', function (d) {
    //     return z(legendData[0].value)
    //   })
    //   .attr('offset', '0%')
    // mainGradient.append('stop')
    //   .style('stop-color', function (d) {
    //     return z(legendData[1].value)
    //   })
    //   .attr('offset', '25%')
    // mainGradient.append('stop')
    //   .style('stop-color', function (d) {
    //     return z(legendData[2].value)
    //   })
    //   .attr('offset', '50%')
    // mainGradient.append('stop')
    //   .style('stop-color', function (d) {
    //     return z(legendData[3].value)
    //   })
    //   .attr('offset', '75%')
    // mainGradient.append('stop')
    //   .style('stop-color', function (d) {
    //     return z(legendData[4].value)
    //   })
    //   .attr('offset', '100%')
    mainGradient
      .append('stop')
      .style('stop-color', function(d) {
        return z(result[0])
      })
      .attr('offset', '0%')
    mainGradient
      .append('stop')
      .style('stop-color', function(d) {
        return z(result[1])
      })
      .attr('offset', '25%')
    mainGradient
      .append('stop')
      .style('stop-color', function(d) {
        return z(result[2])
      })
      .attr('offset', '50%')
    mainGradient
      .append('stop')
      .style('stop-color', function(d) {
        return z(result[3])
      })
      .attr('offset', '75%')
    mainGradient
      .append('stop')
      .style('stop-color', function(d) {
        return z(yMax)
      })
      .attr('offset', '100%')

    let legendContainer = container.append('g')
    let legend = legendContainer.selectAll('.heatLegend').data(result)

    let legendSection = legend
      .enter()
      .append('g')
      .attr('class', 'heatLegend')

    // legendSection.append('rect')
    //   .attr('x', function (d, i) {
    //     return (((width / 4) / 3) * (i + 4))
    //   })
    //   .attr('y', function (d) {
    //     if (chartContext.getOptions().legendPostion === 'top') {
    //       return -45
    //     }
    //     else {
    //       return height + (height / 10)
    //     }
    //   })
    //   .attr('width', (width / 4) / 3)
    //   .attr('height', 10)
    //   .style('fill', function (d) {
    //     return z(d.value)
    //   })

    legendSection
      .append('rect')
      .attr('x', function(d, i) {
        return width / 4 / 3 + width / 4
      })
      .attr('y', function(d) {
        if (chartObj.options.legendPostion === 'top') {
          return -45
        } else {
          return height + 10
        }
      })
      .attr('width', width / 3)
      .attr('height', 10)
      .style('fill', 'url(#legendGradient)')
    // legendSection.append('text')
    //   .attr('class', 'legendLabel')
    //   .attr('x', function (d, i) {
    //     return (((width / 4) / 3) * (i + 5)) - 55
    //   })
    //   .attr('y', function (d) {
    //     if (chartContext.getOptions().legendPostion === 'top') {
    //       return -55
    //     }
    //     else {
    //       return height + (height / 10) + 25
    //     }
    //   })
    //   .text(function (d, i) {
    //     return '≥ ' + d.value + ' ' + (chartContext.getOptions().y1axis.unit ? chartContext.getOptions().y1axis.unit : '')
    //   })

    legendSection
      .append('text')
      .attr('class', 'legendLabel')
      .attr('x', function(d, i) {
        if (result.length > 5) {
          if (i === 5) {
            return (width / 7 / 3) * (i + 8)
          } else {
            return (width / 7 / 3) * (i + 7)
          }
        } else {
          return (width / 5 / 3) * (i + 5)
        }
      })
      .attr('y', function(d) {
        if (chartObj.options.legendPostion === 'top') {
          return -55
        } else {
          return height + 35
        }
      })
      .text(function(d, i) {
        if (result.length > 5) {
          if (i === 0 || i === 3 || i === 5) {
            return (
              d.toFixed(2) +
              ' ' +
              (chartObj.options.y1axis.unit ? chartObj.options.y1axis.unit : '')
            )
          }
        } else {
          if (i === 0 || i === 2 || i === 4) {
            return (
              d.toFixed(2) +
              ' ' +
              (chartObj.options.y1axis.unit ? chartObj.options.y1axis.unit : '')
            )
          }
        }
      })
  },
}
