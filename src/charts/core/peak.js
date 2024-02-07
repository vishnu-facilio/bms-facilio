import * as d3 from 'd3'
import constant from '../helpers/constant'
import formatter from '../helpers/formatter'
import tooltip from '@/graph/mixins/tooltip'
import moment from 'moment-timezone'
import Vue from 'vue'

let defaultColors = [
  '#a6cee3',
  '#ff7f00',
  '#b2df8a',
  '#1f78b4',
  '#fdbf6f',
  '#33a02c',
  '#cab2d6',
  '#6a3d9a',
  '#fb9a99',
  '#e31a1c',
  '#ffff99',
  '#b15928',
]

// utils
function functorkey(v) {
  return typeof v === 'function'
    ? v
    : function(d) {
        return d[v]
      }
}

function functorkeyscale(v, scale) {
  let f =
    typeof v === 'function'
      ? v
      : function(d) {
          return d[v]
        }
  return function(d) {
    return scale(f(d))
  }
}

function keyNotNull(k) {
  return function(d) {
    return d.hasOwnProperty(k) && d[k] !== null && !isNaN(d[k])
  }
}

function fk(v) {
  return function(d) {
    return d[v]
  }
}

export default function() {
  // default
  let height
  let width
  let route
  let margin = {
    top: 40,
    right: 40,
    bottom: 20,
    left: 60,
  }

  let series = []
  let yscale = d3.scaleLinear()
  let xscale = d3.scaleTime()
  yscale.label = ''
  xscale.label = ''
  let storkeWidth
  let svg, container, annotationsContainer, serieContainer, mousevline
  let tooltipDiv

  yscale.setformat = function(value) {
    let format = '.2s'
    if (value < 10) {
      format = '.2f'
    } else if (value < 100) {
      format = '.1f'
    }
    return d3.format(format)(value)
  }
  xscale.setformat = xscale.tickFormat()
  // default tool tip function
  let _tipFunction = function(date, series, alarms) {
    let simpleTooltip = series.length <= 1
    let baseline = series.find(sr => sr.options.dashed)
    if (!baseline) {
      simpleTooltip = true
    }

    let tooltipData = []
    let clickToOpen = false
    let alarmsData = []
    let xaxis = null
    series
      .filter(function(d) {
        return d.item !== undefined && d.item !== null
      })
      .map(function(d) {
        xaxis = d.options.xaxis
        let formatConfig = formatter.getDateFormatConfig(xaxis)
        let tipDate = d.item.orig_label ? d.item.orig_label : d.item.label
        if (
          alarms &&
          alarms.length &&
          date > alarms[0].from &&
          date < alarms[0].to
        ) {
          alarmsData.push({
            isAlarms: true,
            label: 'Message',
            value: alarms[0].message,
          })
          clickToOpen = true
        } else {
          clickToOpen = false
        }

        if (!d.inactive) {
          tooltipData.push({
            label: simpleTooltip
              ? d.options.label
              : moment(tipDate)
                  .tz(Vue.prototype.$timezone)
                  .format(formatConfig.tooltip),
            type: simpleTooltip ? 'circle' : 'line',
            lineStyle: d.options.dashed ? 'dashed' : 'solid',
            color: d.options.color,
            value: formatter.formatValue(d.item[d.aes.y], d.options.axis),
            unit: '',
          })
        }
      })

    if (tooltipData.length) {
      let formatConfig = formatter.getDateFormatConfig(xaxis)
      let tooltipConfig = {
        position: {
          left: d3.event.pageX,
          top: d3.event.pageY,
        },
        mode: 1,
        title: simpleTooltip
          ? moment(date)
              .tz(Vue.prototype.$timezone)
              .format(formatConfig.tooltip)
          : series[0].options.axis.title,
        data: tooltipData,
        clickToOpen: clickToOpen,
        alarm: alarmsData,
      }
      tooltip.showTooltip1(tooltipConfig)
    }
  }

  function updatefocusRing(xdate) {
    let s = annotationsContainer.selectAll('circle.d3_timeseries.focusring')
    let focusing = d3.select('.cross-hair')
    focusing.style('display', null)

    if (xdate == null) {
      s = s.data([])
    } else {
      s = s.data(
        series
          .map(function(s) {
            return {
              x: xdate,
              item: s.find(xdate),
              aes: s.aes,
              axis: s.aixs,
              inactive: s.inactive,
              color: s.options.color,
            }
          })
          .filter(function(d) {
            return (
              d.item !== undefined &&
              d.item !== null &&
              d.item[d.aes.y] !== null &&
              !isNaN(d.item[d.aes.y]) &&
              !d.inactive
            )
          })
      )
    }

    let xPosition = 0
    s.transition()
      .duration(50)
      .attr('cx', function(d) {
        xPosition = xscale(d.item[d.aes.x])
        return xscale(d.item[d.aes.x])
      })
      .attr('cy', function(d) {
        return yscale(d.item[d.aes.y])
      })
    s.enter()
      .append('circle')
      .attr('class', 'd3_timeseries focusring')
      .attr('fill', 'none')
      .attr('stroke-width', 2)
      .attr('r', 5)
      .attr('stroke', fk('color'))
    mousevline
      .attr('transform', function(d) {
        return 'translate(' + (margin.left + xPosition) + ',' + margin.top + ')'
      })
      .style('opacity', function(d) {
        return d.visible ? 1 : 0
      })

    s.exit().remove()
  }

  function updateTip(xdate, alarms) {
    if (xdate == null) {
      tooltipDiv.style('opacity', 0)
      tooltip.hideTooltip()
    } else {
      let s = series.map(function(s) {
        return {
          item: s.find(xdate),
          aes: s.aes,
          inactive: s.inactive,
          options: s.options,
        }
      })

      tooltipDiv
        .style('opacity', 0.9)
        .style('left', margin.left + 5 + xscale(xdate) + 'px')
        .style('top', '0px')
        .html(_tipFunction(xdate, s, alarms))
    }
  }

  function mouseMove(alarms) {
    let x = d3.mouse(container.node())[0]
    x = xscale.invert(x)
    mousevline.datum({ x: x, visible: true })
    // mousevline.update()
    updatefocusRing(x)
    updateTip(x, alarms)
  }
  function mouseOut() {
    mousevline.datum({ x: null, visible: false })
    // mousevline.update()
    updatefocusRing(null)
    updateTip(null)
  }
  function clickAlarm(alarms) {
    tooltip.hideTooltip()
    if (alarms) {
      let alarmID = alarms[0].id
      route.push({ path: '/app/fa/faults/summary/' + alarmID })
    }
  }
  let chart = function(elem, clipId, alarms, legendsDiv, dateRange) {
    if (!clipId) {
      clipId = 'clip'
    }
    // compute mins max on all series
    series = series.map(function(s) {
      let extent = d3.extent(s.data.map(functorkey(s.aes.y)))
      s.min = extent[0]
      s.max = extent[1]
      s.aixs = s.options.y2Axis
      extent = d3.extent(s.data.map(functorkey(s.aes.x)))
      if (dateRange) {
        s.dateMin = dateRange[0]
        s.dateMax = dateRange[1]
      } else {
        s.dateMin = extent[0]
        s.dateMax = extent[1]
      }
      return s
    })
    yscale
      .range([height - margin.top - margin.bottom, 0])
      .domain([0, d3.max(series.map(fk('max')))])
    // .nice()

    xscale
      .range([0, width - margin.left - margin.right])
      .domain([
        d3.min(series.map(fk('dateMin'))),
        d3.max(series.map(fk('dateMax'))),
      ])
    if (yscale.fixedomain) {
      if (yscale.fixedomain.length === 1) {
        yscale.fixedomain.push(yscale.domain()[1])
      }
      yscale.domain(yscale.fixedomain)
    }

    if (xscale.fixedomain) {
      xscale.domain(yscale.fixedomain)
    }

    // create svg
    svg = d3
      .select(elem)
      .append('svg')
      .attr('width', width)
      .attr('height', height + 30)

    // clipping for scrolling in focus area
    svg
      .append('defs')
      .append('clipPath')
      .attr('id', clipId + '')
      .append('rect')
      .attr('width', width - margin.left - margin.right)
      .attr('height', height - margin.bottom)
      .attr('y', -margin.top)
    // container for focus area
    container = svg
      .insert('g', 'rect.mouse-catch')
      .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')')
      .attr('clip-path', 'url(#' + clipId + ')')

    serieContainer = container.append('g')
    annotationsContainer = container.append('g')
    // alarmsContainer = container.append('g').attr('class', 'alarmsGroups')
    // vertical line moving with mouse tip
    mousevline = svg.append('g').datum({
      x: new Date(),
      visible: false,
    })
    mousevline
      .append('line')
      .attr('x1', 0)
      .attr('x2', 0)
      .attr('y1', yscale.range()[0])
      .attr('y2', yscale.range()[1])
      .attr('class', 'focusLine mousevline')
    // update mouse vline
    mousevline.update = function() {}

    updatefocusRing()
    let xAxis = d3
      .axisBottom()
      .scale(xscale)
      .tickFormat(xscale.setformat)
      .tickSizeOuter(0)
      .tickPadding(7)
      .tickSize(7)
    svg
      .append('rect')
      .attr('width', width)
      .attr('class', 'd3_timeseries mouse-catch')
      .attr('height', height)
      .style('opacity', 0)
      .on('click', function(d) {
        clickAlarm(alarms)
      })
      .on('mousemove', function(d) {
        mouseMove(alarms)
      })
      .on('mouseout', mouseOut)
    tooltipDiv = d3
      .select(elem)
      // .style('position', 'relative')
      .append('div')
      .attr('class', 'd3_timeseries tooltip')
      .style('opacity', 0)
    svg
      .append('g')
      .attr('class', 'x-axis-group axis brush-axis')
      .attr(
        'transform',
        'translate(' + margin.left + ',' + (height - margin.bottom) + ')'
      )
      .call(xAxis)
      .selectAll('line')
      .attr('y2', 0)
    svg
      .select('.x-axis-group.axis path.domain')
      .attr('d', 'M-20,0V0.5H' + (width - 80) + '.5V0')
      .attr('class', 'x-axis-line-path')
    svg
      .select('x-axis-group.axis .domain')
      .attr('d', 'M-20,0V0.5H' + (width - 80) + '.5V0')
      .attr('class', 'x-axis-line-path')
    svg
      .append('g')
      .append('text')
      .attr('transform', 'rotate(-90)')
      .attr('class', 'Yaxis-label timeseries')
      .attr('x', 0 - height / 2)
      .attr('y', margin.left / 2 - 10)
      .style('text-anchor', 'middle')
      .attr('dy', '0em')
      .text(function(d) {
        return (
          (series[0].options.axis ? series[0].options.axis.title : '') +
          ' (' +
          (series[0].options.axis ? series[0].options.axis.unit : '') +
          ')'
        )
      })
    svg
      .select('.d3_timeseries.brush')
      .append('text')
      .attr('transform', 'translate(' + (width / 2 - 30) + ' ,' + 90 + ')')
      .attr('class', 'Yaxis-label timeseries')
      .style('text-anchor', 'middle')
      .text('date')

    series.forEach(drawArea)
  }
  function drawArea(serie) {
    let maxpointsss = d3.max(series.map(fk('max')))
    yscale = d3
      .scaleLinear()
      .range([height * (1 / serie.aes.count), 0])
      .domain([0, maxpointsss + maxpointsss / 2])
    let yAxis = d3
      .axisLeft()
      .scale(yscale)
      .tickFormat(yscale.setformat)
      .ticks(5)
      .tickPadding(7)
      .tickSize(0)
    let aes = serie.aes
    let gridline = svg.append('g')

    gridline
      .append('g')
      .attr('class', 'd3_timeseries y axis y-axis-group')
      .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')')
      .call(yAxis)
      .append('text')
      .attr('transform', 'rotate(-90)')
      .attr('x', -margin.top - d3.mean(yscale.range()))
      .attr('dy', '.71em')
      .attr('y', -margin.left + 5)
      .style('text-anchor', 'middle')
      .text(yscale.label)
    svg
      .selectAll('.y-axis-group .tick text')
      .attr('class', 'yaxis-tick-label')
      .attr('dy', '-0.5em')
    svg
      .selectAll('.y-axis-group .tick line')
      .attr('class', 'yaxis-tick-line')
      .attr('x1', '-20')
      .attr('x2', width - margin.left - margin.right)
    if (serie.options.chartType === 'area') {
      storkeWidth = 1.2
    } else {
      storkeWidth = 1.8
    }
    let curveName = serie.options.interpolate
    serie.interpolationFunction = constant.curveMap[curveName]
    let line = d3
      .line()
      .x(functorkeyscale(aes.x, xscale))
      .y(functorkeyscale(aes.y, yscale))
      .curve(serie.interpolationFunction)
      .defined(keyNotNull(aes.y))
    let area = d3
      .area()
      .x(functorkeyscale(aes.x, xscale))
      .y0(functorkeyscale(aes.y, yscale))
      .y1(height)
      .defined(keyNotNull(aes.y))
    serie.line = line
    serie.area = area
    serie.options.label =
      serie.options.label ||
      serie.options.name ||
      serie.aes.label ||
      serie.aes.y
    let linepath = serieContainer
      .append('path')
      .datum(serie.data)
      .attr('class', 'd3_timeseries line')
      .attr('d', line)
      .attr('id', serie.key)
      .attr('stroke', serie.options.color)
      .attr('stroke-linecap', 'round')
      .attr('stroke-width', serie.options.width || storkeWidth)
      .attr('fill', 'none')
    let areapath = serieContainer
      .append('path')
      .datum(serie.data)
      .attr('class', 'layer')
      .attr('d', area)
      .style('opacity', 0.24)
      .style('fill', function(d) {
        return serie.options.color
      })
    serie.linepath = linepath
    serie.areapath = areapath
  }
  chart.width = function(_) {
    if (!arguments.length) return width
    width = _
    return chart
  }
  chart.path = function(_) {
    if (!arguments.length) return route
    route = _
    return chart
  }
  chart.height = function(_) {
    if (!arguments.length) return height
    height = _
    return chart
  }

  chart.margin = function(_) {
    if (!arguments.length) return margin
    margin = _
    return chart
  }
  d3.keys(margin).forEach(function(k) {
    chart.margin[k] = function(_) {
      if (!arguments.length) return margin[k]
      margin[k] = _
      return chart
    }
  })
  // scales accessors
  let scaleGetSet = function(scale) {
    return {
      tickFormat: function(_) {
        if (!arguments.length) return scale.setformat
        scale.setformat = _
        return chart
      },
      label: function(_) {
        if (!arguments.length) return scale.label
        scale.label = _
        return chart
      },
      domain: function(_) {
        if (!arguments.length && scale.fixedomain) return scale.fixedomain
        if (!arguments.length) return null
        scale.fixedomain = _
        return chart
      },
    }
  }
  chart.yscale = scaleGetSet(yscale)
  chart.xscale = scaleGetSet(xscale)
  chart.addSerie = function(data, aes, options) {
    if (!data && series.length > 0) {
      data = series[0].data
    }
    if (!options.color) {
      options.color = defaultColors[series.length % defaultColors.length]
    }
    series.push({ data: data, aes: aes, options: options })
    return chart
  }
  return chart
}
