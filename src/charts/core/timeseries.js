import * as d3 from 'd3'
import constant from '../helpers/constant'
import formatter from '../helpers/formatter'
import tooltip from '@/graph/mixins/tooltip'
import moment from 'moment-timezone'
import Vue from 'vue'
import colors from 'charts/helpers/colors'
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
  let height = 880
  let width = 600
  let route

  let drawerHeight = 0
  let drawerTopMargin = 0
  let margin = {
    top: 40,
    right: 40,
    bottom: 55,
    left: 60,
  }

  let series = []
  let isDual = false
  let yscale = d3.scaleLinear()
  let y2scale = d3.scaleLinear()
  let xscale = d3.scaleTime()
  let xscaleOrg = d3.scaleTime()
  let storkeWidth
  yscale.label = ''
  xscale.label = ''
  y2scale.label = ''
  let brush = d3.brushX()

  let svg,
    container,
    serieContainer,
    annotationsContainer,
    alarmsContainer,
    drawerContainer,
    mousevline
  let fullxscale, tooltipDiv

  yscale.setformat = function(value) {
    let format = '.2s'
    if (value < 10) {
      format = '.2f'
    } else if (value < 100) {
      format = '.1f'
    }
    return d3.format(format)(value)
  }
  y2scale.setformat = function(value) {
    return value
  }
  xscale.setformat = xscale.tickFormat()
  xscaleOrg.setformat = xscaleOrg.tickFormat()
  // default tool tip function
  let _tipFunction = function(date, series, alarms) {
    let simpleTooltip = series.length <= 1
    let baseline = series.find(sr => sr.options.dashed || sr.options.baseLineId)
    if (!baseline) {
      simpleTooltip = true
    }

    let tooltipData = []
    let alarmsData = []
    let xaxis = null
    // let clickToOpen = false
    series
      .filter(function(d) {
        return d.item !== undefined && d.item !== null
      })
      .map(function(d) {
        xaxis = d.options.xaxis
        let formatConfig = formatter.getDateFormatConfig(xaxis)
        let tipDate = d.item.orig_label ? d.item.orig_label : d.item.label
        if (alarms && alarms.length) {
          let toDate =
            alarms[0].to <= 0
              ? new Date(alarms[0].from.getTime() + 2 * 60000)
              : alarms[0].to
          if (date >= alarms[0].from && date <= toDate) {
            alarmsData.push({
              isAlarms: true,
              label: 'Message',
              value: alarms[0].message,
            })
            // clickToOpen = true
          } else {
            // clickToOpen = false
          }
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
        clickToOpen: false,
        alarm: alarmsData,
      }
      tooltip.showTooltip1(tooltipConfig)
    }
  }

  function createLines(serie) {
    let aes = serie.aes

    if (!serie.options.interpolate) {
      serie.options.interpolate = 'linear'
    } else {
      // translate curvenames
      serie.options.interpolate =
        serie.options.interpolate === 'monotone'
          ? 'monotoneX'
          : serie.options.interpolate === 'step-after'
          ? 'stepAfter'
          : serie.options.interpolate === 'step-before'
          ? 'stepBefore'
          : serie.options.interpolate
    }
    // to uppercase for d3 curve name
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

    let line2Dual = d3
      .line()
      .x(functorkeyscale(aes.x, xscale))
      .y(functorkeyscale(aes.y, y2scale))
      .curve(serie.interpolationFunction)
      .defined(keyNotNull(aes.y))
    let area2Dueal = d3
      .area()
      .x(functorkeyscale(aes.x, xscale))
      .y0(functorkeyscale(aes.y, y2scale))
      .y1(height)
      .defined(keyNotNull(aes.y))
    if (serie.aixs) {
      serie.line = line2Dual
      serie.area = area2Dueal
    } else {
      serie.line = line
      serie.area = area
    }
    serie.options.label =
      serie.options.label ||
      serie.options.name ||
      serie.aes.label ||
      serie.aes.y

    if (aes.ci_up && aes.ci_down) {
      let ciArea = d3
        .area()
        .x(functorkeyscale(aes.x, xscale))
        .y0(functorkeyscale(aes.ci_down, yscale))
        .y1(functorkeyscale(aes.ci_up, yscale))
        .curve(serie.interpolationFunction)
      serie.ciArea = ciArea
    }

    if (aes.diff) {
      serie.diffAreas = [
        d3
          .area()
          .x(functorkeyscale(aes.x, xscale))
          .y0(functorkeyscale(aes.y, yscale))
          .y1(function(d) {
            if (d[aes.y] > d[aes.diff]) {
              return yscale(d[aes.diff])
            }
            return yscale(d[aes.y])
          })
          .curve(serie.interpolationFunction),
        d3
          .area()
          .x(functorkeyscale(aes.x, xscale))
          .y1(functorkeyscale(aes.y, yscale))
          .y0(function(d) {
            if (d[aes.y] < d[aes.diff]) {
              return yscale(d[aes.diff])
            }
            return yscale(d[aes.y])
          })
          .curve(serie.interpolationFunction),
      ]
    }

    serie.find = function(date) {
      let bisect = d3.bisector(fk(aes.x)).left
      let i = bisect(serie.data, date) - 1
      if (i === -1) {
        return null
      }

      // look to far after serie is defined
      if (
        i === serie.data.length - 1 &&
        serie.data.length > 1 &&
        Number(date) - Number(serie.data[i][aes.x]) >
          Number(serie.data[i][aes.x]) - Number(serie.data[i - 1][aes.x])
      ) {
        return null
      }
      return serie.data[i]
    }
  }

  function drawSerie(serie) {
    if (!serie.key) {
      serie.key =
        Math.random()
          .toString(36)
          .substring(2, 15) +
        Math.random()
          .toString(36)
          .substring(2, 15)
    }
    if (serie.options.chartType === 'area') {
      storkeWidth = 1.2
    } else {
      storkeWidth = 1.8
    }
    if (!serie.linepath) {
      let linepath = serieContainer
        .append('path')
        .datum(serie.data)
        .attr('class', 'd3_timeseries line')
        .attr('d', serie.line)
        .attr('id', serie.key)
        .attr('stroke', serie.options.color)
        .attr('stroke-linecap', 'round')
        .attr('stroke-width', serie.options.width || storkeWidth)
        .attr('fill', 'none')
      let areapath
      if (serie.options.chartType === 'area') {
        areapath = serieContainer
          .append('path')
          .datum(serie.data)
          .attr('class', 'layer')
          .attr('d', serie.area)
          .style('opacity', 0.24)
          .style('fill', function(d) {
            return serie.options.color
          })
      }
      if (serie.options.dashed) {
        if (
          serie.options.dashed === true ||
          serie.options.dashed === 'dashed'
        ) {
          serie['stroke-dasharray'] = '5,5'
        } else if (serie.options.dashed === 'long') {
          serie['stroke-dasharray'] = '10,10'
        } else if (serie.options.dashed === 'dot') {
          serie['stroke-dasharray'] = '2,4'
        } else {
          serie['stroke-dasharray'] = serie.options.dashed
        }
        linepath.attr('stroke-dasharray', serie['stroke-dasharray'])
      }
      serie.linepath = linepath
      serie.areapath = areapath

      if (serie.ciArea) {
        serie.cipath = serieContainer
          .insert('path', ':first-child')
          .datum(serie.data)
          .attr('class', 'd3_timeseries ci-area')
          .attr('d', serie.ciArea)
          .attr('stroke', 'none')
          .attr('fill', serie.options.color)
          .attr('opacity', serie.options.ci_opacity || 0.3)
      }
      if (serie.diffAreas) {
        serie.diffpaths = serie.diffAreas.map(function(area, i) {
          let c = (serie.options.diff_colors
            ? serie.options.diff_colors
            : ['green', 'red'])[i]
          return serieContainer
            .insert('path', function() {
              return linepath.node()
            })
            .datum(serie.data)
            .attr('class', 'd3_timeseries diff-area')
            .attr('d', area)
            .attr('stroke', 'none')
            .attr('fill', c)
            .attr('opacity', serie.options.diff_opacity || 0.5)
        })
      }
    } else {
      serie.linepath.attr('d', serie.line)
      if (serie.options.chartType === 'area') {
        serie.areapath.attr('d', serie.area)
      }
      if (serie.ciArea) {
        serie.cipath.attr('d', serie.ciArea)
      }
      if (serie.diffAreas) {
        serie.diffpaths[0].attr('d', serie.diffAreas[0])
        serie.diffpaths[1].attr('d', serie.diffAreas[1])
      }
    }
  }
  function drawAlarms(alarms) {
    if (!alarms) {
      return
    }
    if (alarmsContainer.rect) {
      alarmsContainer.rect.remove()
    }
    let rect = alarmsContainer
      .selectAll('alarm-pointers')
      .data(alarms)
      .enter()
      .append('svg:rect')
      .attr('x', function(d, i) {
        return xscale(d.from)
      })
      .attr('y', function(d) {
        return 0
      })
      .attr('width', function(d, i) {
        let from = xscale(moment(d.from).tz(Vue.prototype.$timezone))
        let to = xscale(moment(d.to).tz(Vue.prototype.$timezone))
        let diff = to - from
        return diff <= 4 ? 4 : diff
      })
      .attr('height', function(d, i) {
        return yscale(0)
      })
      .attr('stroke', 'black')
      .attr('stroke-width', 0)
      .attr('opacity', '0.5')
      .on('click', function(d) {
        clickAlarm(d)
      })
      .attr('class', 'alarm-indication')
    alarmsContainer.rect = rect
    alarmsContainer.rect
      .on('click', function(d) {
        clickAlarm(d)
      })
      .on('mouseover', function(d) {
        let typeTooltip = 2
        let toolTipData = [
          {
            label: 'Message',
            value: d.message,
          },
        ]
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          clickToOpen: true,
          data: toolTipData,
          mode: typeTooltip,
        }
        tooltip.showTooltip1(tooltipConfig)
      })
      .on('mouseout', function(d) {
        tooltip.hideTooltip()
      })
  }
  function getTextWidth(text) {
    // re-use canvas object for better performance
    let textWidthCanvas = null
    let canvas =
      textWidthCanvas || (textWidthCanvas = document.createElement('canvas'))
    let context = canvas.getContext('2d')
    let metrics = context.measureText(text)
    return metrics.width
  }
  function drawLegends(series, elm, width) {
    if (elm) {
      elm.innerHTML = '' // empting the legends
    }
    let firstRowCount = 0
    let secoundRowCount = 0
    let firstRowLength = 0
    let secoundRowLength = 0
    let firstRowComplete = false
    let secoundRowComplete = false
    let index = series.length
    series.forEach((element, i) => {
      let len = getTextWidth(element.options.label) + 50
      if (firstRowLength + len < width - 30 && !firstRowComplete) {
        firstRowCount = firstRowCount + 1
        firstRowLength = firstRowLength + len
      } else {
        firstRowComplete = true
      }
      if (firstRowComplete) {
        let len2 = getTextWidth(element.options.label) + 50
        if (secoundRowLength + len2 < width - 70) {
          secoundRowCount = secoundRowCount + 1
          secoundRowLength = secoundRowLength + len2
        } else {
          if (!secoundRowComplete) {
            index = i
          }
          secoundRowComplete = true
        }
      }
    })
    let baseline = series.find(sr => sr.options.dashed || sr.options.baseLine)
    if (baseline) {
      return
    }
    let sliceSeries = series.slice(0, index)
    let legendsContainer = d3
      .select(elm)
      .selectAll('legend')
      .data(sliceSeries)

    let legendRow = legendsContainer
      .enter()
      .append('div')
      .attr('class', 'd3_timeseries legends')
      .append('div')
      .attr('class', 'legend-entry')

    legendRow
      .append('div')
      .attr('class', function(d, i) {
        if (baseline) {
          return 'linesmall legend-color'
        }
        return 'circlesmall legend-color'
      })
      .style('border', function(d, i) {
        if (baseline) {
          if (d.options.dashed) {
            return '1px dashed ' + d.options.color
          } else {
            return '1px solid ' + d.options.color
          }
        } else {
          return ''
        }
      })
      .style('background', function(d, i) {
        if (!baseline) {
          return d.options.color
        }
      })
    legendRow
      .append('div')
      .attr('class', 'legend-label')
      .text(function(d, i) {
        return d.options.label
      })
    legendRow.on('click', function(d, i) {
      let inactive = !d.inactive
      let newOpacity = inactive ? 0 : 1
      d.linepath.style('opacity', newOpacity)
      if (d.areapath) {
        d.areapath.style('opacity', newOpacity === 0 ? 0 : 0.24)
      }
      d3.select(this).classed('inactive', inactive)
      d.inactive = inactive
    })
    if (index !== series.length) {
      let data = series.slice(index, series.length)
      let tooltipContent = ''
      for (let i = 0; i < data.length; i++) {
        tooltipContent +=
          '<div style="padding: 8px 16px;min-height:40px;align-items: center;display: flex;justify-content: row;">'
        tooltipContent +=
          '<div class="circlesmall legend-color" style="background: ' +
          data[i].options.color +
          '"></div>'
        tooltipContent +=
          '<div class="legend-label" style="font-size: 12px;padding-left: 10px;">' +
          data[i].options.label +
          '</div>'
        tooltipContent += '</div>'
      }
      let tip = d3
        .select('body')
        .append('div')
        .attr('class', 'tip')
        .html(tooltipContent)
        .style('box-shadow', '0 5px 10px rgba(0,0,0,0.12)')
        .style('background', '#fff')
        .style('padding', '5px')
        .style('position', 'absolute')
        .style('max-width', '250px')
        .style('display', 'none')
        .style('z-index', '4')
        .on('mouseover', function(d, i) {
          tip.transition().duration(100)
        })
        .on('mouseout', function(d, i) {
          tip
            .style('display', 'none')
            .transition()
            .duration(1000)
        })
      let textcontent = '+' + data.length
      let legendsDropdownContainer = d3
        .select(elm)
        .append('div')
        .style('padding', '5px 2px')
        .style('font-size', '12px')
        .style('cursor', 'pointer')
      legendsDropdownContainer
        .text(textcontent)
        .on('mouseover', function(e) {
          tip.transition().duration(0)
          tip.style('top', d3.event.pageY + 5 + 'px')
          tip.style('left', d3.event.pageX + 'px')
          tip.style('display', 'block')
        })
        .on('mouseout', function(d, i) {
          tip
            .transition()
            .delay(1000)
            .style('display', 'none')
        })
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
        if (d.axis) {
          return y2scale(d.item[d.aes.y])
        } else {
          return yscale(d.item[d.aes.y])
        }
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

  function drawMiniDrawer() {
    let smallyscale = yscale.copy().range([drawerHeight - 30, 0])
    let serie = series[0]
    let area = d3
      .area()
      .x(functorkeyscale(serie.aes.x, fullxscale))
      .y0(functorkeyscale(serie.aes.y, smallyscale))
      .y1(function(d) {
        return drawerHeight - 30
      })
      .curve(serie.interpolationFunction)
      .defined(keyNotNull(serie.aes.y))
    let linepath = drawerContainer
      .insert('path', ':first-child')
      .datum(serie.data)
      .attr('class', 'd3_timeseries.line')
      .attr('transform', 'translate(0,' + (drawerTopMargin + 10) + ')')
      .attr('d', area)
      .attr('stroke', function(d) {
        let theme = window.localStorage.getItem('theme')
        if (theme && theme === 'black') {
          return '#eff2f550'
        } else {
          return '#eff2f5'
        }
      })
      .attr('stroke-width', serie.options.width || 1.5)
      .attr('fill', function(d) {
        let theme = window.localStorage.getItem('theme')
        if (theme && theme === 'black') {
          return '#eff2f550'
        } else {
          return '#eff2f5'
        }
      })
    if (serie.hasOwnProperty('stroke-dasharray')) {
      linepath.attr('stroke-dasharray', serie['stroke-dasharray'])
    }
  }
  function drawsafeLimit(chartContext, config) {
    if (
      chartContext.options.safelimit &&
      chartContext.options.safelimit.length
    ) {
      let safelimit = chartContext.options.safelimit
      for (let i = 0; i < chartContext.options.safelimit.length; i++) {
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
        let safeLimits = svg
          .append('g')
          .attr(
            'transform',
            'translate(' + config.margin.left + ',' + config.margin.top + ')'
          )
        safeLimits
          .append('circle')
          .attr(
            'cx',
            config.isDual
              ? config.width - (config.margin.left + config.margin.right)
              : 0
          )
          .attr('cy', yscale(safelimit[i].safelimitValue))
          .attr('r', 5)
          .attr('class', 'safe-circle')
          .on('mouseover', function(d) {
            // chartContext.hover = false
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
              title1: safelimit[i].title,
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              data: tooltipData,
              color: safelimit[i].color
                ? safelimit[i].color
                : colors.default[safelimit[i].index],
            }
            tooltip.showTooltip(tooltipConfig, chartContext)
          })
          .on('mouseout', function(d) {
            // chartContext.hover = true
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
              : colors.default[safelimit[i].index]
          )
        safeLimits
          .append('circle')
          .attr(
            'cx',
            config.isDual
              ? config.width - (config.margin.left + config.margin.right)
              : 0
          )
          .attr('cy', config.yscale(safelimit[i].safelimitValue))
          .attr('r', 1)
          .attr('fill', '#fff')
          .on('mouseover', function() {})
        safeLimits
          .append('line')
          .attr('x1', 5)
          .attr('y1', config.yscale(safelimit[i].safelimitValue))
          .attr('y2', config.yscale(safelimit[i].safelimitValue))
          .attr('x2', config.width - (config.margin.left + config.margin.right))
          .style('shape-rendering', 'crispEdges')
          .attr('id', 'safe' + i)
          .attr(
            'stroke',
            safelimit[i].color
              ? safelimit[i].color
              : colors.default[safelimit[i].index]
          )
          .attr('class', 'safelimit-line ' + safeLimitLineClass)

        safeLimits
          .append('text')
          .attr('x', 20)
          .attr('y', config.yscale(safelimit[i].safelimitValue) - 15)
          .attr('dy', '1em')
          .attr('text-anchor', 'start')
          .text(safelimit[i].formated_value)
          .attr(
            'fill',
            safelimit[i].color
              ? safelimit[i].color
              : colors.default[safelimit[i].index]
          )
          .attr('class', 'safelimit safelimit-value ' + safeLimitValueClass)
      }
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
  function clickAlarm(alarm) {
    tooltip.hideTooltip()
    if (alarm) {
      let alarmID = alarm.id
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
    let y1MaxList = []
    let y2MaxList = []
    let y2AxisData = null
    for (let serie of series) {
      if (serie.aixs) {
        y2AxisData = serie
        y2MaxList.push(d3.extent(serie.data.map(functorkey(serie.aes.y)))[1])
      } else {
        y1MaxList.push(d3.extent(serie.data.map(functorkey(serie.aes.y)))[1])
      }
    }
    let y1max = d3.max(y1MaxList)
    let safelimit = series[0].options.safelimit
    if (safelimit) {
      let maxvalue = 0
      for (let i = 0; i < safelimit.length; i++) {
        if (safelimit[i].safelimitValue > maxvalue) {
          maxvalue = safelimit[i].safelimitValue
        }
      }
      if (maxvalue > y1max) {
        y1max = maxvalue
      }
    }
    yscale
      .range([
        height - margin.top - margin.bottom - drawerHeight - drawerTopMargin,
        0,
      ])
      .domain([0, y1max])

    if (y2MaxList && y2MaxList.length) {
      let y2max = d3.max(y2MaxList)
      y2scale
        .range([
          height - margin.top - margin.bottom - drawerHeight - drawerTopMargin,
          0,
        ])
        .domain([0, y2max])
      isDual = true
    } else {
      isDual = false
    }

    xscale
      .range([0, width - margin.left - margin.right])
      .domain([
        d3.min(series.map(fk('dateMin'))),
        d3.max(series.map(fk('dateMax'))),
      ])
    xscaleOrg
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

    fullxscale = xscale.copy()
    // let fullxscaleY = xscale.copy()
    // create svg
    svg = d3
      .select(elem)
      .append('svg')
      .attr('width', width)
      .attr('height', height + 30)
      .attr('class', 'timeseries')

    let gridline = svg.append('g')
    // clipping for scrolling in focus area
    svg
      .append('defs')
      .append('clipPath')
      .attr('id', clipId + '')
      .append('rect')
      .attr('width', width - margin.left - margin.right)
      .attr('height', height - margin.bottom - drawerHeight - drawerTopMargin)
      .attr('y', -margin.top)
    // container for focus area
    container = svg
      .insert('g', 'rect.mouse-catch')
      .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')')
      .attr('clip-path', 'url(#' + clipId + ')')
      .attr('class', 'data-conatiner')
    serieContainer = container.append('g')
    annotationsContainer = container.append('g')
    // alarmsContainer = container.append('g').attr('class', 'alarmsGroups')

    // mini container at the bottom
    drawerContainer = svg
      .append('g')
      .attr(
        'transform',
        'translate(' +
          margin.left +
          ',' +
          (height + 20 - drawerHeight - margin.bottom) +
          ')'
      )
      .attr('class', 'bottomaxis')
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
    mousevline.update = function() {}
    updatefocusRing()
    brush
      .extent([
        [fullxscale.range()[0], 0],
        [fullxscale.range()[1], drawerHeight - 20 - drawerTopMargin],
      ])

      .on('end', () => {
        let selection = d3.event.selection
        if (selection === null) {
          resetScale()
        }
      })
    let brushAxis = d3
      .axisBottom()
      .scale(xscale)
      .tickFormat(xscale.setformat)
      .tickSizeOuter(0)
      .tickPadding(10)
      .tickSize(0)
    let xAxis = d3
      .axisBottom()
      .scale(xscale)
      .tickFormat(function(d) {
        if (
          series &&
          series[0].options.xaxis &&
          (series[0].options.xaxis.datatype === 'date_time' ||
            series[0].options.xaxis.datatype === 'date')
        ) {
          let formatConfig = formatter.getDateFormatConfig(
            series[0].options.xaxis
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
      .tickSize(7)
      .ticks(7)
    let yAxis = d3
      .axisLeft()
      .scale(yscale)
      .tickFormat(yscale.setformat)
      .ticks(5)
      .tickPadding(7)
      .tickSize(0)
    let y2Axis = d3
      .axisRight()
      .scale(y2scale)
      .tickFormat(yscale.setformat)
      .ticks(5)
      .tickPadding(7)
      .tickSize(0)

    /* month axis */
    let monthAxis = null
    if (
      series &&
      series[0].options.xaxis &&
      (series[0].options.xaxis.datatype === 'date_time' ||
        series[0].options.xaxis.datatype === 'date')
    ) {
      let formatConfig = formatter.getDateFormatConfig(series[0].options.xaxis)
      let idx = 0
      monthAxis = d3
        .axisBottom()
        .scale(xscale)
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
        .tickSizeOuter(0)
        .tickPadding(7)
        .tickSize(7)
    }
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
    if (isDual) {
      gridline
        .append('g')
        .attr('class', 'd3_timeseries y2 axis y2-axis-group')
        .attr(
          'transform',
          'translate(' +
            (width - margin.left + 10) +
            ',' +
            (margin.top - 10) +
            ')'
        )
        .call(y2Axis)
        .append('text')
        .attr('transform', 'rotate(-90)')
        .attr('x', -margin.top - d3.mean(y2scale.range()))
        .attr('dy', '.71em')
        .attr('y', -margin.left + 10)
        .style('text-anchor', 'top')
        .text(function() {
          return y2scale.label
        })
    }
    drawerContainer
      .append('g')
      .attr('class', 'd3_timeseries x axis')
      .attr('transform', 'translate(0,' + (drawerHeight - 10) + ')')
      .call(xAxis)

    // drawerContainer.append('g')
    //   .attr('class', 'd3_timeseries brush')
    //   .call(brush)
    //   .attr('transform', `translate(0, ${drawerTopMargin + 10})`)
    //   .attr('height', ((drawerHeight - 30) - drawerTopMargin))
    svg
      .selectAll('.y-axis-group .tick text')
      .attr('class', 'yaxis-tick-label')
      .attr('dy', '-0.5em')
    svg
      .selectAll('.y-axis-group .tick line')
      .attr('class', 'yaxis-tick-line')
      .attr('x1', '-20')
      .attr('x2', width - margin.left - margin.right)
    svg
      .append('rect')
      .attr('width', width)
      .attr('class', 'd3_timeseries mouse-catch')
      .attr('height', height - height / 8)
      .style('opacity', 0)
      .on('mousedown', function(d) {
        let e = this,
          origin = d3.mouse(e),
          rect = svg.append('rect').attr('class', 'zoom')

        d3.select('body').classed('noselect', true)
        origin[0] = Math.max(0, Math.min(width, origin[0]))
        origin[1] = Math.max(0, Math.min(height, origin[1]))
        d3.select(window)
          .on('mousemove.zoomRect', function() {
            let m = d3.mouse(e)
            m[0] = Math.max(0, Math.min(width, m[0]))
            m[1] = Math.max(0, Math.min(height, m[1]))
            if (m) {
              rect
                .attr('x', Math.min(origin[0], m[0]))
                .attr('y', Math.min(origin[1], m[1]))
                .attr('width', Math.abs(m[0] - origin[0]))
                .attr('height', Math.abs(m[1] - origin[1]))
            }
          })
          .on(
            'mouseup.zoomRect',
            function() {
              if (
                rect.attr('x') + rect.attr('width') !== 0 &&
                rect.attr('width') * rect.attr('height') > 1000
              ) {
                let m = d3.mouse(e)
                let x1, x2
                m[0] = Math.max(0, Math.min(width, m[0]))
                m[1] = Math.max(0, Math.min(height, m[1]))
                x1 = parseInt(rect.attr('x')) - 63
                x2 =
                  parseInt(rect.attr('x')) - 63 + parseInt(rect.attr('width'))
                let selection = [x1, x2]

                fullxscale = xscale.domain(
                  selection.map(fullxscale.invert, fullxscale)
                )

                svg.select('.x-axis-group').call(brushAxis)
                svg.select('.month-axis').attr('opacity', '0')

                series.forEach(drawSerie)
                if (alarms) {
                  drawAlarms(alarms)
                }

                d3.select(window)
                  .on('mousemove.zoomRect', null)
                  .on('mouseup.zoomRect', null)
                d3.select('body').classed('noselect', false)
                d3.select(elem)
                  .select('.resetButtom')
                  .attr('display', 'block')
                rect.remove()
              } else {
                rect.remove()
              }
            },
            true
          )
        d3.event.stopPropagation()
      })
      .on('mousemove', function(d) {
        mouseMove(alarms)
      })
      .on('mouseout', mouseOut)
    svg
      .append('g')
      .attr('class', 'resetButtom')
      .attr('display', 'none')
      .attr('cursor', 'pointer')
      .on('click', function(d) {
        resetScale()
      })
      .append('rect')
      .attr('width', '85')
      .attr('height', '20')
      .attr('x', width - 120)
      .attr('y', 0)
      .attr('rx', 3)
      .attr('ry', 3)
      .attr('display', 'block')
      .attr('fill', 'rgba(0,0,0,0)')
      .attr('stroke', '#00d8d2')
      .attr('stroke-width', '1')
    d3.select(elem)
      .select('.resetButtom')
      .append('text')
      .text('Reset zoom')
      .attr('y', 1)
      .attr('x', width - 108)
      .attr('y', 5)
      .attr('display', 'block')
      .attr('dy', '0.71em')
      .attr('fill', '#00d8d2')
      .style('font-size', '12px')
    let alarmSection = svg
      .append('g', 'rect.mouse-catch')
      .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')')
      .attr('clip-path', 'url(#' + clipId + ')')
    alarmsContainer = alarmSection.append('g').attr('class', 'alarmsGroups')
    let config = {
      xscale: xscale,
      yscale: yscale,
      width: width,
      height: height,
      margin: margin,
      isDual: isDual,
    }
    drawsafeLimit(series[0], config)
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
        'translate(' +
          margin.left +
          ',' +
          (height - margin.bottom - drawerHeight - drawerTopMargin) +
          ')'
      )
      .call(xAxis)
      .selectAll('line')
      .attr('y2', 0)
    svg
      .append('g')
      .attr('class', 'month-axis')
      .attr(
        'transform',
        'translate(' +
          margin.left +
          ',' +
          (height + 20 - margin.bottom - drawerHeight - drawerTopMargin) +
          ')'
      )
      .call(monthAxis)
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
    drawerContainer
      .select('.d3_timeseries.x.axis path.domain')
      .attr('d', 'M0 0 L0 0 L0 0')
      .attr('class', 'x-axis-line-path')
    // left axis label
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
        let txt = series[0].options.axis ? series[0].options.axis.title : ''
        if (series[0].options.axis && series[0].options.axis.unit) {
          txt += ' (' + series[0].options.axis.unit + ')'
        }
        return txt
      })
    if (isDual) {
      svg
        .append('g')
        .append('text')
        .attr('transform', 'rotate(-90)')
        .attr('class', 'Yaxis-label timeseries')
        .attr('x', 0 - height / 2)
        .attr('y', width + margin.right - margin.left / 2 - 10)
        .style('text-anchor', 'middle')
        .attr('dy', '0em')
        .text(function(d) {
          if (y2AxisData) {
            return y2AxisData.options.label
          } else {
            let txt = series[1].options.axis ? series[1].options.axis.title : ''
            if (series[1].options.axis && series[1].options.axis.unit) {
              txt += ' (' + series[1].options.axis.unit + ')'
            }
            return txt
          }
        })
    }
    svg
      .select('.x-axis-group.axis.brush-axis')
      .append('text')
      .attr('transform', 'translate(' + (width / 2 - 30) + ' ,' + 60 + ')')
      .attr('class', 'Yaxis-label timeseries')
      .style('text-anchor', 'middle')
      .text('TIMESTAMP')
    function resetScale() {
      xscale.domain(xscaleOrg.domain())
      svg.select('.x-axis-group').call(xAxis)
      // svg.select('.month-axis').call(monthAxis)
      series.forEach(drawSerie)
      d3.select('body').classed('noselect', false)
      if (alarms) {
        drawAlarms(alarms)
      }
      // mousevline.update()
      d3.select(elem)
        .select('.resetButtom')
        .attr('display', 'none')
      svg.select('.x-axis-group path.domain').attr('display', 'none')
      svg.select('.month-axis').attr('opacity', '1')
      updatefocusRing()
    }
    series.forEach(createLines)
    series.forEach(drawSerie)
    drawLegends(series, legendsDiv, width)
    if (alarms) {
      drawAlarms(alarms)
    }
    drawMiniDrawer()
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
