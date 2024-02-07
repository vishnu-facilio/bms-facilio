import moment from 'moment-timezone'
import Vue from 'vue'
import deepmerge from 'util/deepmerge'
import tooltip from '@/graph/mixins/tooltip'
import * as d3 from 'd3'
import newDateModel from 'src/newcharts/model/newDateModel'
import NumberFormatHelper from 'src/components/mixins/NumberFormatHelper'
import { formatDuration } from 'charts/helpers/formatter'
import ckmeans from 'third_party/ckmeans.min.js'
import BubbleCompare from 'billboard.js/dist/plugin/billboardjs-plugin-bubblecompare.js'
import { isEmpty, isArray } from '@facilio/utils/validation'
import * as chroma from 'chroma-js'

export default {
  prepare(chartContext) {
    let params = {}
    params.padding = chartContext.options.customPadding
      ? chartContext.options.customPadding
      : chartContext.options.padding
    params.size = chartContext.options.size

    this.prepareGeneralOptions(chartContext, params)
    params.data = this.prepareData(chartContext)
    params.axis = this.prepareAxis(chartContext)
    params.tooltip = this.prepareTooltip(chartContext)

    if (chartContext.options.regionConfig) {
      params.regions = this.prepareRegionData(chartContext)
    }
    if (chartContext.options.point) {
      params.point = chartContext.options.point
    }
    if (chartContext.options.axes) {
      params.data.axes = chartContext.options.axes
    }
    if (chartContext.options.chartType) {
      params.data.type = chartContext.options.chartType
    }
    if (params.data.type === 'scatter' && chartContext.options.combo !== true) {
      params.point = {
        show: true,
        pattern:
          chartContext.Readingdp || chartContext.scatterConfig
            ? params.point.pattern
            : [],
      }
    }
    if (chartContext.options.fixedAxis) {
      Object.keys(chartContext.options.fixedAxis).forEach(rt => {
        if (params.axis[rt].label) {
          params.axis[rt].label.text = chartContext.options.fixedAxis[rt].label
        }
        if (params.axis[rt].min && params.axis[rt].min >= 0) {
          params.axis[rt].min = chartContext.options.fixedAxis[rt].min
        }
        if (params.axis[rt].max) {
          params.axis[rt].max = chartContext.options.fixedAxis[rt].max
        }
      })
    }
    this.applyDefaultOptions(chartContext, params)
    if (
      !chartContext.options.disbaleNiceTickMarks &&
      !chartContext.options.isGroupedByTime
    ) {
      if (chartContext.options.general.normalizeStack !== true) {
        this.applyNiceTickMarks(params, chartContext)
      } else {
        if (chartContext.options.type !== 'stackedbar') {
          this.applyNiceTickMarks(params, chartContext)
        }
      }
    }
    if (chartContext.options.hasOwnProperty('zoom')) {
      params.zoom = chartContext.options.zoom
    }
    if (
      chartContext.options.hasOwnProperty('isDepreciationCostTrend') &&
      params.data.json['Current Price']
    ) {
      let unitMap = {}
      let yData = params.data.json['Current Price'].map(d => d)
      let depData = params.data.json['Depreciated Amount'].map(d => d)
      let firstCost = yData.find(val => val !== null)
      let OBVIndex = yData.indexOf(firstCost)
      let OBV = []
      if (firstCost) {
        for (let idx in yData) {
          if (yData[idx] === firstCost && parseInt(idx) === OBVIndex) {
            yData[idx] = 0
            depData[idx] = null
            OBV.push(firstCost)
          } else {
            OBV.push(null)
          }
        }
      } else {
        OBV = yData.map(() => null)
      }
      unitMap['OBV'] = chartContext.unitMap['Current Price']
      let newParams = {
        data: {
          json: {
            'Current Price': yData,
            'Depreciated Amount': depData,
            OBV: OBV,
          },
          colors: {
            OBV: '#ec6363',
          },
          names: {
            OBV: 'Opening Book Value',
          },
          groups: [
            ['Opening Book Value', 'Current Price', 'Depreciated Amount'],
          ],
          axes: {
            'Depreciated Amount': params.data.axes['Current Price'],
            OBV: params.data.axes['Current Price'],
          },
          order: 'asc',
          labels: {
            format: {
              'Depreciated Amount': function(x) {
                if ([0, null].includes(x)) {
                  return ''
                }
                return x
              },
              OBV: function(x) {
                if ([0, null].includes(x)) {
                  return ''
                }
                return x
              },
            },
          },
        },
        bar: {
          width: {
            ratio: 0.75,
          },
        },
      }
      chartContext.unitMap = deepmerge.objectAssignDeep(
        {},
        chartContext.unitMap,
        unitMap
      )
      return deepmerge.objectAssignDeep({}, params, newParams)
    }
    if (chartContext.scatterConfig && !isEmpty(params.data.types)) {
      let types = Object.values(params.data.types)
      if (types.includes('bubble')) {
        params['plugins'] = [
          new BubbleCompare({
            minR: 10,
            maxR: 50,
            expandScale: 1,
          }),
        ]
      }
    }

    if (
      chartContext.options.type === 'bar' &&
      chartContext.options.bar.showGroupTotal
    ) {
      let groupTotalData = this.getGroupTotalDataPoint(params, null)
      if (groupTotalData) {
        params.data.json.group_total = groupTotalData
        params.data.colors.group_total = 'rgba(126,127,127,0.2)'
        params.data.labels = false
      }
    }

    if (chartContext.baselineData) {
      let json = this.prepareBaselineData(chartContext)
      params.data.json = this.extendObject(params.data.json, json)
      let names = this.getNamesForBaseline(chartContext)
      params.data.names = this.extendObject(params.data.names, names)
      let axes = this.getXYMapForBaseline(chartContext)
      params.data.axes = this.extendObject(params.data.axes, axes)
      let xs = this.getXsMapForBaseline(chartContext)
      params.data.xs = this.extendObject(params.data.xs, xs)
      let types = this.getTypesForBaseline(chartContext)
      params.data.types = this.extendObject(params.data.types, types)
      let colors = this.getColorsForBaseline(chartContext)
      params.data.colors = this.extendObject(params.data.colors, colors)
      params.line.point = []
    }
    return params
  },
  extendObject(originalObj, data) {
    for (let i in data) {
      originalObj[i] = data[i]
    }
    return originalObj
  },
  linearInterpolationForBaseLine(arr1, arr2, resolution) {
    const lerp = (x, y, a) => x * (1 - a) + y * a
    let xaxis = []
    let yaxis = []
    for (let i = 0; i < arr1.length; i++) {
      let diff = arr1[i] - arr1[i + 1]
      let inbetweenCount
      if (diff < 0) {
        inbetweenCount = parseInt((-1 * diff) / resolution)
      } else {
        inbetweenCount = parseInt(diff / resolution)
      }
      for (let j = 0; j <= inbetweenCount; j++) {
        // let x = arr1[i] + j * resolution
        // console.log('x value -> ', x)
        // let y = interpolate(arr1[i], arr2[i], arr1[i + 1], arr2[i + 1], x)
        let percent = j / inbetweenCount
        let x = lerp(arr1[i], arr1[i + 1], percent)
        let y = lerp(arr2[i], arr2[i + 1], percent)
        xaxis.push(x.toFixed(2))
        yaxis.push(y.toFixed(2))
      }
    }
    return [xaxis, yaxis]
  },
  prepareBaselineData(chartContext) {
    let graphPoints = chartContext.baselineData
    let result = []
    let tempX = []
    let tempY = []
    let count = 0
    const alphabet = 'abcdefghijklmnopqrstuvwxyz'.split('')
    graphPoints.forEach(op => {
      JSON.parse(op.graphValue).forEach(td => {
        if (td.x !== '' && td.y !== '') {
          tempX.push(parseFloat(td.x))
          tempY.push(parseFloat(td.y))
        }
      })
      const interploteResult = this.linearInterpolationForBaseLine(
        tempX,
        tempY,
        0.02
      )
      result[alphabet[count]] = interploteResult[1]
      result[alphabet[count + 1]] = interploteResult[0]
      tempX = []
      tempY = []
      count += 2
    })
    return result
  },
  getNamesForBaseline(chartContext) {
    let selectedGraphPoints = chartContext.baselineData
    const alphabet = 'abcdefghijklmnopqrstuvwxyz'.split('')
    let result = {}
    let count = 0
    for (let i = 0; i < selectedGraphPoints.length; i += 1) {
      result[alphabet[count]] = selectedGraphPoints[i].label
      result[alphabet[count + 1]] = selectedGraphPoints[i].label
      count += 2
    }
    return result
  },
  getXYMapForBaseline(chartContext) {
    let selectedGraphPoints = chartContext.baselineData
    let val = selectedGraphPoints.length
    const alphabet = 'abcdefghijklmnopqrstuvwxyz'.split('')
    let result = {}
    for (let i = 0; i < val; i += 1) {
      result[alphabet[i]] = 'y'
      result[alphabet[i + 1]] = 'x'
    }
    return result
  },
  getColorsForBaseline(chartContext) {
    const colors = chartContext.baselineColors
    const alphabet = 'abcdefghijklmnopqrstuvwxyz'.split('')
    let result = {}
    let count = 0
    for (let i = 0; i < chartContext.baselineData.length; i += 1) {
      result[alphabet[count]] = colors[i]
      result[alphabet[count + 1]] = colors[i]
      count += 2
    }
    return result
  },
  getTypesForBaseline(chartContext) {
    let selectedGraphPoints = chartContext.baselineData
    let val = selectedGraphPoints.length
    const alphabet = 'abcdefghijklmnopqrstuvwxyz'.split('')
    let result = {}
    let count = 0
    for (let i = 0; i < val; i += 1) {
      result[alphabet[count]] = 'line'
      result[alphabet[count + 1]] = 'line'
      count += 2
    }
    return result
  },
  getXsMapForBaseline(chartContext) {
    let selectedGraphPoints = chartContext.baselineData
    let val = selectedGraphPoints.length
    const alphabet = 'abcdefghijklmnopqrstuvwxyz'.split('')
    let result = {}
    let count = 0
    for (let i = 0; i < val; i += 1) {
      result[alphabet[count]] = alphabet[count + 1]
      count += 2
    }
    return result
  },
  getGroupTotalDataPoint(c3Params, hiddenTargetIds) {
    let groupedBarPoints = []
    for (let key of Object.keys(c3Params.data.json)) {
      if (c3Params.data.groups.indexOf(key) < 0 && key !== 'x') {
        if (
          c3Params.data.type === 'bar' ||
          c3Params.data.types[key] === 'bar'
        ) {
          groupedBarPoints.push(key)
        }
      }
    }
    if (groupedBarPoints.length > 1) {
      let groupTotalData = []

      for (let i = 0; i < c3Params.data.json.x.length; i++) {
        let sum = 0
        for (let key of groupedBarPoints) {
          if (
            (!hiddenTargetIds || hiddenTargetIds.indexOf(key) < 0) &&
            key !== 'group_total'
          ) {
            sum += parseFloat(
              c3Params.data.json[key][i] ? c3Params.data.json[key][i] : 0
            )
          }
        }
        groupTotalData.push(sum)
      }
      return groupTotalData
    }
    return null
  },
  rerenderInterSecions(chart) {
    if (chart.hasOwnProperty('clipId')) {
      let id = 'intersections-container-' + chart.clipId
      if (document.getElementById(id)) {
        document.getElementById(id).remove()
      }
    } else {
      if (document.getElementById('intersections-container')) {
        document.getElementById('intersections-container').remove()
      }
    }
  },
  drawIntersections(chart, chartContext) {
    if (chartContext.options.intersections) {
      this.rerenderInterSecions(chart, chartContext)
      if (chart.hasOwnProperty('clipId')) {
        chart.intersectionContainer = chart.svg
          .select('g')
          .select('.bb-chart')
          .append('g')
          .attr('class', 'intersections-container')
          .attr('id', 'intersections-container-' + chart.clipId)
      } else {
        chart.intersectionContainer = chart.svg
          .select('g')
          .select('.bb-chart')
          .append('g')
          .attr('class', 'intersections-container')
          .attr('id', 'intersections-container')
      }
      for (let i = 0; i < chartContext.options.intersections.length; i++) {
        if (!chart.hiddenTargetIds.length) {
          this.drawPaths(
            chart,
            chartContext,
            chartContext.options.intersections[i],
            i
          )
        }
      }
    }
  },
  scaleTime(chart) {
    return d3
      .scaleTime()
      .domain(chart.orgXDomain)
      .range(chart.xAxis.config.range)
  },
  prepareSpecificRangeOfChartData(chart, chartContext, params) {
    if (chartContext && chart && params && params.regions) {
      if (params.regions.length) {
        let timeScale = this.scaleTime(chart)
        let d = params.regions[0]
        return {
          minDomain: moment(d.start, chartContext.options.xFormat),
          maxDomain: new Date(chart.orgXDomain[1]),
          xMin: timeScale(moment(d.start, chartContext.options.xFormat)),
          xMax: chart.xMax,
          data: chartContext.data,
        }
      }
    } else {
      return {
        minDomain: new Date(chart.orgXDomain[0]),
        maxDomain: new Date(chart.orgXDomain[1]),
        xMin: chart.xMin,
        xMax: chart.xMax,
        data: chartContext.data,
      }
    }
  },
  drawPaths(chart, chartContext, config, index) {
    if (chart) {
      let pathArea = chart.intersectionContainer
        .append('g')
        .attr('class', 'intersections-' + index)

      let yAxis = chart.yAxis.config.valueOf().tickValues // get yAxis value from billboard context
      let yMin = 0
      let yMax = 0
      if (yAxis && yAxis.length) {
        yMin = yAxis[0]
        yMax = yAxis[yAxis.length - 1]
      }
      let xdata = {}
      xdata = this.prepareSpecificRangeOfChartData(chart, chartContext)
      let xDomain = chart.xAxis.params.orgXScale.domain()
        ? chart.xAxis.params.orgXScale.domain()
        : [new Date(chart.orgXDomain[0]), new Date(chart.orgXDomain[1])]
      let x = d3
        .scaleTime()
        .domain(xDomain)
        .range(chart.xAxis.config.range)
      let y = d3
        .scaleLinear()
        .range(chart.yAxis.config.range.reverse())
        .domain([yMin, yMax])
      let points = []
      points = this.regionAreaPoints(
        xdata.data,
        config.from,
        config.to,
        chartContext.options.xFormat
      )
      let area = d3
        .area()
        .x(function(d) {
          return x(d.x)
        })
        .y1(function(d) {
          return y(d.high)
        })
        .defined(function(d) {
          if (d.low !== null && d.high !== null) {
            return d
          }
        })
      let area1 = d3
        .area()
        .x(function(d) {
          return x(d.x)
        })
        .y0(function(d) {
          return y(d.low)
        })
        .defined(function(d) {
          return d
        })

      pathArea.datum(points)
      pathArea
        .append('defs')
        .append('pattern')
        .attr('id', 'diagonalHatchnew')
        .attr('patternUnits', 'userSpaceOnUse')
        .attr('width', 4)
        .attr('height', 4)
        .append('path')
        .attr('d', 'M-1,1 l2,-2 M0,4 l4,-4 M3,5 l2,-2')
        .attr('stroke', '#dadada')
        .attr('stroke-width', 1)

      pathArea
        .append('clipPath')
        .attr('id', config.context.id + 'clip-below')
        .append('path')
        .attr('d', area.y0(chart.yAxis.config.range[1]))

      pathArea
        .append('clipPath')
        .attr('id', config.context.id + 'clip-above')
        .append('path')
        .attr('d', area.y0(chart.yAxis.config.range[0]))

      pathArea
        .append('path')
        .attr('class', 'area above')
        .attr('clip-path', 'url(#' + config.context.id + 'clip-above)')
        .attr(
          'd',
          area.y0(function(d) {
            return y(d['low'])
          })
        )
        .style('opacity', 1)
        .style('stroke', 'none')
        .style('fill', 'none')

      pathArea
        .append('path')
        .attr('class', 'area below')
        .attr('clip-path', 'url(#' + config.context.id + 'clip-below)')
        .attr(
          'd',
          area1.y1(function(d) {
            return y(d['high'])
          })
        )
        .style('opacity', 1)
        .style('stroke', 'none')
        .style('fill', '#f36886')
    }
  },
  renderDiffPath(chart, chartContext, params, config) {
    if (chart) {
      chart.svg
        .select('g')
        .select('.bb-chart')
        .select(config.context.class)
        .remove()
      let pathArea = chart.svg
        .select('g')
        .select('.bb-chart')
        .append('g')
        .attr('class', config.context.class)
      let yAxis = chart.yAxis.config.valueOf().tickValues
      let yMin = 0
      let yMax = 0
      if (yAxis && yAxis.length) {
        yMin = yAxis[0]
        yMax = yAxis[yAxis.length - 1]
      }
      let xdata = {}
      xdata = this.prepareSpecificRangeOfChartData(chart, chartContext, params)
      let x = d3
        .scaleTime()
        .domain([new Date(chart.orgXDomain[0]), new Date(chart.orgXDomain[1])])
        .range(chart.xAxis.config.range)
      let y = d3
        .scaleLinear()
        .range(chart.yAxis.config.range.reverse())
        .domain([yMin, yMax])
      let points = []
      if (params.regions) {
        points = this.regionAreaPoints(
          xdata.data,
          config.from,
          config.to,
          chartContext.options.xFormat,
          params.regions[0]
        )
      } else {
        points = this.regionAreaPoints(
          xdata.data,
          config.from,
          config.to,
          chartContext.options.xFormat
        )
      }
      let area = d3
        .area()
        .x(function(d) {
          return x(d.x)
        })
        .y1(function(d) {
          return y(d.high)
        })
        .defined(function(d) {
          if (d.low !== null && d.high !== null) {
            return d
          }
        })
      let area1 = d3
        .area()
        .x(function(d) {
          return x(d.x)
        })
        .y0(function(d) {
          return y(d.low)
        })
        .defined(function(d) {
          return d
        })

      pathArea.datum(points)
      pathArea
        .append('defs')
        .append('pattern')
        .attr('id', 'diagonalHatch')
        .attr('patternUnits', 'userSpaceOnUse')
        .attr('width', 4)
        .attr('height', 4)
        .append('path')
        .attr('d', 'M-1,1 l2,-2 M0,4 l4,-4 M3,5 l2,-2')
        .attr('stroke', '#dadada')
        .attr('stroke-width', 1)

      pathArea
        .append('clipPath')
        .attr('id', config.context.id + 'clip-below')
        .append('path')
        .attr('d', area.y0(chart.yAxis.config.range[1]))

      pathArea
        .append('clipPath')
        .attr('id', config.context.id + 'clip-above')
        .append('path')
        .attr('d', area.y0(chart.yAxis.config.range[0]))

      if (config.to && config.to.color && !config.to.disable) {
        pathArea
          .append('path')
          .attr('class', 'area above')
          .attr('clip-path', 'url(#' + config.context.id + 'clip-above)')
          .attr(
            'd',
            area.y0(function(d) {
              return y(d['low'])
            })
          )
          .style('opacity', 0.2)
          .style('stroke', 'none')
          .style('fill', config.to.color)
      }
      if (!config.to.disable) {
        pathArea
          .append('path')
          .attr('class', 'area above')
          .attr('clip-path', 'url(#' + config.context.id + 'clip-above)')
          .attr(
            'd',
            area.y0(function(d) {
              return y(d['low'])
            })
          )
          .style('opacity', 0.2)
          .style('stroke', 'none')
          .style('fill', 'url(#diagonalHatch)')
      }

      if (config.from && config.from.color && !config.from.disable) {
        pathArea
          .append('path')
          .attr('class', 'area below')
          .attr('clip-path', 'url(#' + config.context.id + 'clip-below)')
          .attr(
            'd',
            area1.y1(function(d) {
              return y(d['high'])
            })
          )
          .style('opacity', 0.2)
          .style('stroke', 'none')
          .style('fill', config.from.color)
      }
      if (!config.from.disable) {
        pathArea
          .append('path')
          .attr('class', 'area below')
          .attr('clip-path', 'url(#' + config.context.id + 'clip-below)')
          .attr(
            'd',
            area1.y1(function(d) {
              return y(d['high'])
            })
          )
          .style('opacity', 0.2)
          .style('stroke', 'none')
          .style('fill', 'url(#diagonalHatch)')
      }
    }
  },
  regionAreaPoints(points, from, to, format, region) {
    let data = []
    if (region) {
      points.x.forEach((d, index) => {
        if (
          moment(region.reportingPeriodStartTime, format) <= moment(d, format)
        ) {
          data.push({
            x: moment(d, format),
            high: points[from.point][index]
              ? Number(points[from.point][index])
              : null,
            low: points[to.point][index]
              ? Number(points[to.point][index])
              : null,
          })
        }
      })
    } else {
      points.x.forEach((d, index) => {
        data.push({
          x: moment(d, format),
          high: points[from.point][index]
            ? Number(points[from.point][index])
            : null,
          low: points[to.point][index] ? Number(points[to.point][index]) : null,
        })
      })
    }
    return data
  },
  prepareRegionData(chartContext) {
    let data = {}
    data = chartContext.options.regionConfig
    let endOfMomentDay = Number(
      moment(data.end)
        .endOf('month')
        .format('DD')
    )
    let endOfDay = Number(moment(data.end).format('DD'))
    data['start'] = moment(data.start).format(chartContext.options.xFormat)
    if (
      endOfMomentDay === endOfDay &&
      chartContext.options.xFormat === 'MM-YYYY'
    ) {
      data['end'] = moment(data.end)
        .add(1, 'day')
        .format(chartContext.options.xFormat)
    } else {
      data['end'] = moment(data.end).format(chartContext.options.xFormat)
    }
    data['reportingPeriodStartTime'] = moment(
      data.reportingPeriodStartTime
    ).format(chartContext.options.xFormat)
    return [data]
  },
  prepareColorTile(chartContext, params) {
    return {
      pattern: [params.data.colors['F']],
      tiles: function() {
        let pattern = d3
          .select(document.createElementNS(d3.namespaces.svg, 'pattern'))
          .attr('patternUnits', 'userSpaceOnUse')
          .attr('width', '6')
          .attr('height', '6')

        let g = pattern
          .append('g')
          .attr('fill-rule', 'evenodd')
          .attr('stroke-width', 1)
          .append('g')
          .attr('fill', 'rgb(255, 127, 14)')

        g.append('polygon').attr('points', '5 0 6 0 0 6 0 5')
        g.append('polygon').attr('points', '6 5 6 6 5 6')

        let gradient = d3
          .select(document.createElementNS(d3.namespaces.svg, 'linearGradient'))
          .attr('patternUnits', 'userSpaceOnUse')
          .attr('x2', '0')
          .attr('y2', '80%')

        gradient
          .append('stop')
          .attr('offset', '49%')
          .attr('stop-color', 'blue')

        gradient
          .append('stop')
          .attr('offset', '50%')
          .attr('stop-color', 'grey')

        gradient
          .append('stop')
          .attr('offset', '51%')
          .attr('stop-color', 'red')
        // Should return an array of SVGPatternElement
        return [pattern.node(), gradient.node()]
      },
    }
  },
  prepareMobile(c3Params) {
    let source = Vue.prototype.$source ? Vue.prototype.$source : ''
    if (source === 'analytics') {
      c3Params.padding.left = 45
      c3Params.padding.right = 15
      c3Params.padding.bottom = -20
    } else if (source === 'dashboard') {
      if (c3Params.axis && c3Params.axis.y2 && c3Params.axis.y2.show) {
        c3Params.padding.left = 45
        c3Params.padding.right = 45
        c3Params.padding.bottom =
          c3Params.data.type === 'donut' || c3Params.data.type === 'pie'
            ? 20
            : -20
      } else {
        c3Params.padding.left = 45
        c3Params.padding.right = 15
        c3Params.padding.bottom =
          c3Params.data.type === 'donut' || c3Params.data.type === 'pie'
            ? 20
            : -20
        if (
          c3Params.axis.x &&
          c3Params.axis.x.tick &&
          c3Params.axis.x.type === 'category'
        ) {
          c3Params.axis.x.tick.multiline = false
          c3Params.axis.x.tick.rotate = 20
          c3Params.axis.x.tick.tooltip = false
          c3Params.padding.bottom += 20
        }
      }
    } else {
      c3Params.padding.left = 45
      c3Params.padding.right = 15
      c3Params.padding.bottom =
        c3Params.data.type === 'donut' || c3Params.data.type === 'pie'
          ? 20
          : -20
    }
    c3Params.zoom.enabled = false
    c3Params.mobile = true
    return c3Params
  },
  prepareBooleanAlarmChart(chartContext, x, alarms) {
    let params = {}
    params.padding = {
      top: 20,
      left: 30,
      right: 30,
      bottom: 0,
    }

    let dummyData = []
    if (x && x.length) {
      for (let i = 0; i < x.length; i++) {
        dummyData.push(1)
      }
    }
    params.data = {
      json: {
        x: alarms.data.x,
        alarms: alarms.data.alarms,
      },
      xFormat: '%m-%d-%Y %H:%M',
      x: 'x',
    }
    params.size = {
      height: 50,
    }
    if (alarms.barSize && alarms.barSize === 'mini') {
      params.size.height = 40
    }
    if (alarms.barSize && alarms.barSize === 'small') {
      params.size.height = 50
    }
    if (alarms.barSize && alarms.barSize === 'medium') {
      params.size.height = 57
    }
    params.size.height += 70
    params.grid = {
      x: {
        show: false,
      },
      y: {
        show: false,
      },
    }
    params.axis = this.prepareAxis(chartContext)
    params.axis.x.tick.values = null
    params.axis.x.min = moment(chartContext.dateRange.time[0])
      .tz(Vue.prototype.$timezone)
      .format('MM-DD-YYYY HH:mm')
    params.axis.x.max = moment(chartContext.dateRange.time[1])
      .tz(Vue.prototype.$timezone)
      .format('MM-DD-YYYY HH:mm')
    params.tooltip = {
      show: false,
      contents: function(d) {
        if (alarms.regions[d[0].value]) {
          let alarmList = alarms.regions[d[0].value].alarms

          let tooltipData = []
          for (let alarm of alarmList) {
            tooltipData.push({
              label: 'Alarm',
              value: alarm.subject,
            })
          }

          let tooltipConfig = {
            className: 'bb-chart-tooltip',
            data: tooltipData,
            mode: 1,
          }
          return tooltip.showTooltipForNewChart(tooltipConfig)
        }
        return null
      },
    }
    params.axis.x.show = true
    params.axis.y.show = false
    params.axis.y.tick.format = null
    params.axis.x.tick.outer = false
    params.axis.y2.show = false
    params.legend = {
      show: false,
    }

    let regions = []
    let alarmSeverityValues = {}
    for (let alarm of alarms.regions) {
      let severityMap = {}
      let isCleared = false
      let highSev = null
      let highSevId = null
      for (let a of alarm.alarms) {
        let sevId = a.severity ? a.severity.id : 0
        let severity = sevId
          ? Vue.prototype.$helpers.getAlarmSeverity(sevId, true)
          : null
        if (severity && severity.severity.toLowerCase() === 'clear') {
          isCleared = true
          if (a.previousSeverity) {
            sevId = a.previousSeverity.id
            severity = Vue.prototype.$helpers.getAlarmSeverity(sevId, true)
          }
        }
        if (severity) {
          if (!highSev) {
            highSev = severity.cardinality
            highSevId = sevId
          } else {
            if (highSev > severity.cardinality) {
              highSev = severity.cardinality
              highSevId = sevId
            }
          }
        }

        if (!severityMap[sevId]) {
          severityMap[sevId] = 0
        }
        severityMap[sevId] = severityMap[sevId] + 1
      }
      let maxSeverity =
        highSevId ||
        Object.keys(severityMap).reduce((a, b) =>
          severityMap[a] > severityMap[b] ? a : b
        )
      let maxSeverityCount = severityMap[maxSeverity]

      regions.push({
        start: alarm.start,
        end: alarm.end,
        class: 'fc-boolean-' + (maxSeverity + '_' + maxSeverityCount),
        alarms: alarm.alarms,
        isCleared: isCleared,
        count: alarm.count,
      })

      if (!alarmSeverityValues[maxSeverity]) {
        alarmSeverityValues[maxSeverity] = []
      }
      alarmSeverityValues[maxSeverity].push(maxSeverityCount)
    }

    params.regions = regions

    params.onafterinit = function() {
      for (let severityId in alarmSeverityValues) {
        let color = Vue.prototype.$helpers.getAlarmSeverity(severityId)
          ? Vue.prototype.$helpers.getAlarmSeverity(severityId, true).color
          : '#ff0000'

        let alarmValues = alarmSeverityValues[severityId]
        let distinctAlarmValues = [...new Set(alarmValues)]
        distinctAlarmValues.sort(function(a, b) {
          return b - a
        })

        let parentSelector = d3.select(this.api.element)

        parentSelector.selectAll('.fc-boolean-0').style('fill', color)
        parentSelector
          .selectAll('.fc-boolean-0 rect')
          .style('fill-opacity', '0.1')

        let index = 1
        for (let al of distinctAlarmValues) {
          index = index - 0.1
          let key = '0.8'.replace('.', '')
          parentSelector
            .selectAll('.fc-boolean-' + (severityId + '_' + al) + ' rect')
            .style('fill', color)
          parentSelector
            .selectAll('.fc-boolean-' + (severityId + '_' + al) + ' rect')
            .classed('fc-boolean-rect-' + key, true)
          parentSelector
            .selectAll('.fc-boolean-' + (severityId + '_' + al))
            .on('mouseover', function(d) {
              let tooltipData = []
              let alarmTooltipData = []
              for (let alarm of d.alarms) {
                let severity = alarm.severity
                  ? Vue.prototype.$helpers.getAlarmSeverity(
                      alarm.severity.id,
                      true
                    )
                  : null
                let isCleared = false
                if (severity && severity.severity.toLowerCase() === 'clear') {
                  isCleared = true
                  severity = alarm.previousSeverity
                    ? Vue.prototype.$helpers.getAlarmSeverity(
                        alarm.previousSeverity.id,
                        true
                      )
                    : null
                }

                let message
                let subject
                if (Vue.prototype.$helpers.isLicenseEnabled('NEW_ALARMS')) {
                  message = alarm.alarm.description || ''
                  subject =
                    alarm.alarm && alarm.alarm.subject
                      ? alarm.alarm.subject
                      : ''
                } else {
                  message = alarm.problem || alarm.readingMessage
                  subject = alarm.subject || ''
                }

                alarmTooltipData.push({
                  name: subject,
                  message: message,
                  severity: severity,
                  isCleared: isCleared,
                  count: d.alams && d.alams.legend ? d.alams.length : 0,
                })
              }

              let tooltipConfig = {
                position: {
                  left: d3.event.pageX,
                  top: d3.event.pageY,
                },
                title:
                  moment(d.start).format('LLL') +
                  ' - ' +
                  moment(d.end).format('LLL'),
                subtitle: {
                  start: moment(d.start).format('hh:mm a'),
                  end: moment(d.end).format('hh:mm a'),
                },
                titleObj: {
                  start: moment(d.start).format('DD MMM YYYY'),
                  end: moment(d.end).format('DD MMM YYYY'),
                },
                data: tooltipData,
                alarms: alarms.showTimeOnly ? null : alarmTooltipData,
                count: alarmTooltipData.length ? alarmTooltipData[0].count : 0,
                mode: 1,
              }
              tooltip.showTooltip1(tooltipConfig)
            })
            .on('mouseout', function() {
              tooltip.hideTooltip()
            })
            .on('click', function(d) {
              tooltip.hideTooltip()
              if (d.alarms && d.alarms.length && chartContext.openAlarm) {
                chartContext.openAlarm(d.alarms)
              }
            })
        }
      }
    }

    return params
  },
  prepareBooleanChart(chartContext, x, alarms) {
    let params = {}
    params.padding = {
      top: 20,
      left: chartContext.options.padding.left,
      right: chartContext.options.padding.right,
      bottom: 0,
    }

    let dummyData = []
    for (let i = 0; i < x.length; i++) {
      dummyData.push(1)
    }
    params.data = {
      json: {
        x: alarms.data.x,
        alarms: alarms.data.alarms,
      },
      xFormat: '%m-%d-%Y %H:%M',
      x: 'x',
    }
    params.size = {
      height: 50,
    }
    if (alarms.barSize && alarms.barSize === 'mini') {
      params.size.height = 40
    }
    if (alarms.barSize && alarms.barSize === 'small') {
      params.size.height = 50
    }
    if (alarms.barSize && alarms.barSize === 'medium') {
      params.size.height = 57
    }
    params.grid = {
      x: {
        show: false,
      },
      y: {
        show: false,
      },
    }
    params.axis = this.prepareAxis(chartContext)
    params.axis.x.tick.values = null
    params.axis.x.min = moment(chartContext.dateRange.time[0])
      .tz(Vue.prototype.$timezone)
      .format('MM-DD-YYYY HH:mm')
    params.axis.x.max = moment(chartContext.dateRange.time[1])
      .tz(Vue.prototype.$timezone)
      .format('MM-DD-YYYY HH:mm')

    params.tooltip = {
      show: false,
      contents: function(d) {
        if (alarms.regions[d[0].value]) {
          let alarmList = alarms.regions[d[0].value].alarms

          let tooltipData = []
          for (let alarm of alarmList) {
            tooltipData.push({
              label: 'Alarm',
              value: alarm.subject,
            })
          }

          let tooltipConfig = {
            className: 'bb-chart-tooltip',
            data: tooltipData,
            mode: 1,
          }
          return tooltip.showTooltipForNewChart(tooltipConfig)
        }
        return null
      },
    }
    params.axis.x.show = false
    params.axis.y.show = false
    params.axis.y.tick.format = null
    params.axis.y2.show = false
    params.legend = {
      show: false,
    }

    let regions = []
    let alarmSeverityValues = {}
    for (let alarm of alarms.regions) {
      let severityMap = {}
      let isCleared = false
      let highSev = null
      let highSevId = null
      for (let a of alarm.alarms) {
        let sevId = a.severity ? a.severity.id : 0
        let severity = sevId
          ? Vue.prototype.$helpers.getAlarmSeverity(sevId)
          : null
        if (severity && severity.severity.toLowerCase() === 'clear') {
          isCleared = true
          if (a.previousSeverity) {
            sevId = a.previousSeverity.id
            severity = Vue.prototype.$helpers.getAlarmSeverity(sevId)
          }
        }
        if (severity) {
          if (!highSev) {
            highSev = severity.cardinality
            highSevId = sevId
          } else {
            if (highSev > severity.cardinality) {
              highSev = severity.cardinality
              highSevId = sevId
            }
          }
        }

        if (!severityMap[sevId]) {
          severityMap[sevId] = 0
        }
        severityMap[sevId] = severityMap[sevId] + 1
      }
      let maxSeverity =
        highSevId ||
        Object.keys(severityMap).reduce((a, b) =>
          severityMap[a] > severityMap[b] ? a : b
        )
      let maxSeverityCount = severityMap[maxSeverity]

      regions.push({
        start: alarm.start,
        end: alarm.end,
        class: 'fc-boolean-' + (maxSeverity + '_' + maxSeverityCount),
        alarms: alarm.alarms,
        isCleared: isCleared,
        count: alarm.count,
      })

      if (!alarmSeverityValues[maxSeverity]) {
        alarmSeverityValues[maxSeverity] = []
      }
      alarmSeverityValues[maxSeverity].push(maxSeverityCount)
    }

    params.regions = regions

    params.onafterinit = function() {
      for (let severityId in alarmSeverityValues) {
        let color = Vue.prototype.$helpers.getAlarmSeverity(severityId)
          ? Vue.prototype.$helpers.getAlarmSeverity(severityId).color
          : '#ff0000'

        let alarmValues = alarmSeverityValues[severityId]
        let distinctAlarmValues = [...new Set(alarmValues)]
        distinctAlarmValues.sort(function(a, b) {
          return b - a
        })

        let parentSelector = d3.select(this.api.element)

        parentSelector.selectAll('.fc-boolean-0').style('fill', color)
        parentSelector
          .selectAll('.fc-boolean-0 rect')
          .style('fill-opacity', '0.1')

        let index = 1
        for (let al of distinctAlarmValues) {
          index = index - 0.1
          let key = '0.8'.replace('.', '')
          parentSelector
            .selectAll('.fc-boolean-' + (severityId + '_' + al) + ' rect')
            .style('fill', color)
          parentSelector
            .selectAll('.fc-boolean-' + (severityId + '_' + al) + ' rect')
            .classed('fc-boolean-rect-' + key, true)
          parentSelector
            .selectAll('.fc-boolean-' + (severityId + '_' + al))
            .on('mouseover', function(d) {
              let tooltipData = []
              let alarmTooltipData = []
              for (let alarm of d.alarms) {
                let severity = alarm.severity
                  ? Vue.prototype.$helpers.getAlarmSeverity(alarm.severity.id)
                  : null
                let isCleared = false
                if (severity && severity.severity.toLowerCase() === 'clear') {
                  isCleared = true
                  severity = alarm.previousSeverity
                    ? Vue.prototype.$helpers.getAlarmSeverity(
                        alarm.previousSeverity.id
                      )
                    : null
                }

                let message
                let subject
                if (Vue.prototype.$helpers.isLicenseEnabled('NEW_ALARMS')) {
                  message = alarm.alarm.description || ''
                  subject =
                    alarm.alarm && alarm.alarm.subject
                      ? alarm.alarm.subject
                      : ''
                } else {
                  message = alarm.problem || alarm.readingMessage
                  subject = alarm.subject || ''
                }

                alarmTooltipData.push({
                  name: subject,
                  message: message,
                  severity: severity,
                  isCleared: isCleared,
                  count: d.count,
                })
              }

              let tooltipConfig = {
                position: {
                  left: d3.event.pageX,
                  top: d3.event.pageY,
                },
                title:
                  moment(d.start).format('LLL') +
                  ' - ' +
                  moment(d.end).format('LLL'),
                data: tooltipData,
                alarms: alarms.showTimeOnly ? null : alarmTooltipData,
                count: alarmTooltipData.length ? alarmTooltipData[0].count : 0,
                mode: 1,
              }
              tooltip.showTooltip1(tooltipConfig)
            })
            .on('mouseout', function() {
              tooltip.hideTooltip()
            })
            .on('click', function(d) {
              tooltip.hideTooltip()
              if (d.alarms && d.alarms.length && chartContext.openAlarm) {
                chartContext.openAlarm(d.alarms)
              }
            })
        }
      }
    }

    return params
  },
  prepareMultichart(chartContext) {
    let dataList = this.prepareMultichartData(chartContext)

    let c3Data = []
    let index = 0
    let overallHeight = chartContext.options.size.height - 20
    let withoutBooleanPoints = dataList.filter(
      dl => !dl.booleanChart && !dl.enumChart
    )
    overallHeight =
      overallHeight - (dataList.length - withoutBooleanPoints.length) * 50
    for (let d of dataList) {
      index = index + 1
      let params = {}
      params.label = d.label
      params.key = d.key
      params.padding = {
        top: chartContext.options.padding.top,
        right: chartContext.options.padding.right,
        bottom: chartContext.options.padding.bottom,
        left: chartContext.options.padding.left,
      }

      this.prepareGeneralOptions(chartContext, params, d.key)
      params.data = d
      params.axis = this.prepareAxis(chartContext, d.key)
      if (
        (chartContext.options.regressionConfig &&
          chartContext.options.regressionConfig.length !== 0) ||
        chartContext.Readingdp
      ) {
        params.tooltip = this.prepareTooltip(chartContext)
      } else {
        params.tooltip = this.prepareMultichartTooltip(chartContext)
      }
      if (params.data.point) {
        params.point = params.data.point
      }
      if (
        params.data.type === 'scatter' &&
        chartContext.options.combo !== true
      ) {
        params.point = {
          show: true,
          pattern:
            chartContext.Readingdp || chartContext.scatterConfig
              ? params.point.pattern
              : [],
        }
      }
      this.applyDefaultOptions(chartContext, params)
      if (
        !chartContext.options.disbaleNiceTickMarks &&
        !chartContext.options.isGroupedByTime
      ) {
        if (chartContext.options.general.normalizeStack !== true) {
          this.applyNiceTickMarks(params, chartContext)
        } else {
          if (chartContext.options.type !== 'stackedbar') {
            this.applyNiceTickMarks(params, chartContext)
          }
        }
      }
      if (
        index === dataList.length &&
        params.axis.x.show === true &&
        !chartContext.scatterConfig
      ) {
        overallHeight = overallHeight - 50
      }
      let height = chartContext.groupByMetrics ? 150 : 100
      if (overallHeight) {
        let calcHeight = overallHeight / withoutBooleanPoints.length
        if (calcHeight >= 100 && !chartContext.groupByMetrics) {
          height = calcHeight
        } else if (calcHeight >= 150) {
          height = calcHeight
        }
      }
      params.size = {
        height: height,
        width: chartContext.options.size.width,
      }
      if (chartContext.scatterConfig) {
        if (dataList.length > 2) {
          params.size.height =
            chartContext.options.size.height / Math.ceil(dataList.length / 2)
        } else {
          params.size.height = chartContext.options.size.height
        }
      }

      if (index !== dataList.length && !chartContext.scatterConfig) {
        params.axis.x.label.text = ''
        params.axis.x.show = chartContext.scatterConfig ? true : false
        params.padding.bottom = -6
        params.size.height = params.size.height - 25
      }
      params.padding.top = 0

      if (d.booleanChart) {
        params.grid.y = false
        params.size.height = 25
        params.axis.x.label.text = ''
        params.axis.x.show = false
        params.axis.y.show = false
        params.axis.y2.show = false
        params.padding.bottom = -25

        params.axis.x.min = chartContext.dateRange.range.highResMin
        params.axis.x.max = chartContext.dateRange.range.highResMax

        let offClassName = 'fc-boolean-' + d.key + '-0'
        let onClassName = 'fc-boolean-' + d.key + '-1'

        let booleanTimeline =
          chartContext.booleanData &&
          chartContext.booleanData[d.key.replace('datapoint_', '')]
            ? chartContext.booleanData[d.key.replace('datapoint_', '')]
            : null
        if (booleanTimeline && booleanTimeline.length) {
          let regions = []
          let x = []
          let y = []
          for (let range of booleanTimeline) {
            x.push(range.start)
            y.push(null)
            x.push(range.end)
            y.push(null)
            regions.push({
              start: range.start,
              end: range.end,
              class: range.value ? onClassName : offClassName,
            })
          }
          params.regions = regions
          params.data.json.x = x
          params.data.json[d.key.replace('datapoint_', '')] = y
          params.data.xFormat = '%m-%d-%Y %H:%M'
          params.axis.x.tick.values = null
        } else {
          params.data.xFormat = '%m-%d-%Y %H:%M'
          params.regions = {
            start: moment(chartContext.dateRange.time[0])
              .tz(Vue.prototype.$timezone)
              .format('MM-DD-YYYY HH:mm'),
            end: moment(
              chartContext.dateRange.time[1]
                ? chartContext.dateRange.time[1]
                : chartContext.dateRange.time[0]
            )
              .tz(Vue.prototype.$timezone)
              .format('MM-DD-YYYY HH:mm'),
            class: offClassName,
          }
          params.data.json.x = [
            moment(chartContext.dateRange.time[0])
              .tz(Vue.prototype.$timezone)
              .format('MM-DD-YYYY HH:mm'),
            moment(
              chartContext.dateRange.time[1]
                ? chartContext.dateRange.time[1]
                : chartContext.dateRange.time[0]
            )
              .tz(Vue.prototype.$timezone)
              .format('MM-DD-YYYY HH:mm'),
          ]
          params.data.json[d.key.replace('datapoint_', '')] = [null, null]
          params.axis.x.tick.values = null
        }

        params.tooltip.show = false
        let c3Self = this
        params.onafterinit = function() {
          let parentSelector = d3.select(this.api.element)

          parentSelector
            .selectAll('.' + onClassName)
            .style('fill', d.booleanChart.color)
          parentSelector
            .selectAll('.' + onClassName + ' rect')
            .style('fill-opacity', '0.9')
          parentSelector
            .selectAll('.' + onClassName + ' rect')
            .classed('fc-boolean-rect-09', true)

          parentSelector
            .selectAll('.' + offClassName)
            .style('fill', d.booleanChart.color)
          parentSelector
            .selectAll('.' + offClassName + ' rect')
            .style('fill-opacity', '0.1')
          parentSelector.selectAll('.bb-chart').style('display', 'none') // removed the bb-chart element from the boolean chart. it causes tool tip issue. mouse over event fires when mouse over the alarm charts not a boolean chart
          let self = this
          parentSelector
            .selectAll('.' + onClassName)
            .on('mousemove', function() {
              let mouse = d3.mouse(this)
              let closestDate = c3Self.newFindClosest(
                chartContext.data.x,
                chartContext.options.axis.x.time.format.d3Format,
                self.x.invert(mouse[0])
              )
              let closestIdx = closestDate.i

              let xVal = closestDate.date

              let tooltipData = []

              for (let dataPoint of chartContext.options.dataPoints) {
                if (dataPoint.type === 'group') {
                  if (dataPoint.children) {
                    let children = []
                    for (let dp of dataPoint.children) {
                      let dJson = chartContext.data[dp.key]
                      let unit = chartContext.unitMap[dp.dpKey || dp.key]
                      let enumMap = chartContext.enumMap[dp.dpKey || dp.key]

                      let actualVal =
                        dJson[closestIdx] === null ? 0 : dJson[closestIdx]
                      if (dp.dataType === 'BOOLEAN' || dp.dataType === 'ENUM') {
                        actualVal = c3Self.getBooleanValue(
                          chartContext,
                          xVal,
                          dp.key,
                          closestIdx
                        )
                      }

                      let tVal = [
                        'days',
                        'hours',
                        'minutes',
                        'seconds',
                        'milliseconds',
                      ].includes(unit)
                        ? formatDuration(actualVal, unit, dp.convertTounit)
                        : (unit === '$' ? unit + ' ' : '') +
                          actualVal +
                          (unit && unit !== '$' ? ' ' + unit : '')
                      let val = actualVal

                      if (enumMap && enumMap[val]) {
                        tVal = enumMap[val]
                      }
                      if (dJson) {
                        children.push({
                          label: dp.label,
                          value: tVal,
                          color: dp.color,
                        })
                      }
                    }
                    tooltipData.push({
                      label: dataPoint.label,
                      is_group: true,
                      children: children,
                    })
                  }
                } else {
                  let dJson = chartContext.data[dataPoint.key]
                  let unit =
                    chartContext.unitMap[dataPoint.dpKey || dataPoint.key]
                  let enumMap =
                    chartContext.enumMap[dataPoint.dpKey || dataPoint.key]

                  let actualVal =
                    dJson[closestIdx] === null ? 0 : dJson[closestIdx]
                  if (
                    dataPoint.dataType === 'BOOLEAN' ||
                    dataPoint.dataType === 'ENUM'
                  ) {
                    actualVal = c3Self.getBooleanValue(
                      chartContext,
                      xVal,
                      dataPoint.key,
                      closestIdx
                    )
                  }

                  let tVal = [
                    'days',
                    'hours',
                    'minutes',
                    'seconds',
                    'milliseconds',
                  ].includes(unit)
                    ? formatDuration(actualVal, unit, dataPoint.convertTounit)
                    : (unit === '$' ? unit + ' ' : '') +
                      actualVal +
                      (unit && unit !== '$' ? ' ' + unit : '')
                  let val = actualVal

                  if (enumMap && enumMap[val]) {
                    tVal = enumMap[val]
                  }
                  if (dJson) {
                    tooltipData.push({
                      label: dataPoint.label,
                      value: tVal,
                      color: dataPoint.color,
                    })
                  }
                }
              }

              let dateFormat = 'LLL'
              if (
                chartContext.options.axis.x.time &&
                chartContext.options.axis.x.time.format
              ) {
                dateFormat =
                  chartContext.options.axis.x.time &&
                  chartContext.options.axis.x.time.format.tooltip
              }

              let tooltipTitle = xVal
              if (typeof xVal === 'object') {
                tooltipTitle = moment(xVal).format(dateFormat)
              } else if (typeof xVal === 'number') {
                tooltipTitle = chartContext.data.x[xVal]
              }
              let tooltipConfig = {
                position: {
                  left: d3.event.pageX,
                  top: d3.event.pageY,
                },
                title: tooltipTitle,
                mode: 1,
                data: tooltipData,
                dataPoint: chartContext.options.dataPoints,
              }
              if (chartContext.tooltipCallback) {
                chartContext.tooltipCallback(
                  tooltipData,
                  tooltipConfig,
                  tooltipTitle
                )
                return null
              }
              tooltip.showTooltipForNewChart(tooltipConfig)
            })
            .on('mouseout', function() {
              tooltip.hideTooltip()
            })
          parentSelector
            .selectAll('.' + offClassName)
            .on('mousemove', function() {
              let mouse = d3.mouse(this)
              let closestDate = c3Self.newFindClosest(
                chartContext.data.x,
                chartContext.options.axis.x.time.format.d3Format,
                self.x.invert(mouse[0])
              )
              let closestIdx = closestDate.i

              let xVal = closestDate.date

              let tooltipData = []

              for (let dataPoint of chartContext.options.dataPoints) {
                if (dataPoint.type === 'group') {
                  if (dataPoint.children) {
                    let children = []
                    for (let dp of dataPoint.children) {
                      let dJson = chartContext.data[dp.key]
                      let unit = chartContext.unitMap[dp.dpKey || dp.key]
                      let enumMap = chartContext.enumMap[dp.dpKey || dp.key]

                      let actualVal =
                        dJson[closestIdx] === null ? 0 : dJson[closestIdx]
                      if (dp.dataType === 'BOOLEAN' || dp.dataType === 'ENUM') {
                        actualVal = c3Self.getBooleanValue(
                          chartContext,
                          xVal,
                          dp.key,
                          closestIdx
                        )
                      }

                      let tVal = [
                        'days',
                        'hours',
                        'minutes',
                        'seconds',
                        'milliseconds',
                      ].includes(unit)
                        ? formatDuration(actualVal, unit, dp.convertTounit)
                        : (unit === '$' ? unit + ' ' : '') +
                          actualVal +
                          (unit && unit !== '$' ? ' ' + unit : '')
                      let val = actualVal

                      if (enumMap && enumMap[val]) {
                        tVal = enumMap[val]
                      }
                      if (dJson) {
                        children.push({
                          label: dp.label,
                          value: tVal,
                          color: dp.color,
                        })
                      }
                    }
                    tooltipData.push({
                      label: dataPoint.label,
                      is_group: true,
                      children: children,
                    })
                  }
                } else {
                  let dJson = chartContext.data[dataPoint.key]
                  let unit =
                    chartContext.unitMap[dataPoint.dpKey || dataPoint.key]
                  let enumMap =
                    chartContext.enumMap[dataPoint.dpKey || dataPoint.key]

                  let actualVal =
                    dJson[closestIdx] === null ? 0 : dJson[closestIdx]
                  if (
                    dataPoint.dataType === 'BOOLEAN' ||
                    dataPoint.dataType === 'ENUM'
                  ) {
                    actualVal = c3Self.getBooleanValue(
                      chartContext,
                      xVal,
                      dataPoint.key,
                      closestIdx
                    )
                  }

                  let tVal = [
                    'days',
                    'hours',
                    'minutes',
                    'seconds',
                    'milliseconds',
                  ].includes(unit)
                    ? formatDuration(actualVal, unit, dataPoint.convertTounit)
                    : (unit === '$' ? unit + ' ' : '') +
                      actualVal +
                      (unit && unit !== '$' ? ' ' + unit : '')
                  let val = actualVal

                  if (enumMap && enumMap[val]) {
                    tVal = enumMap[val]
                  }
                  if (dJson) {
                    tooltipData.push({
                      label: dataPoint.label,
                      value: tVal,
                      color: dataPoint.color,
                    })
                  }
                }
              }

              let dateFormat = 'LLL'
              if (
                chartContext.options.axis.x.time &&
                chartContext.options.axis.x.time.format
              ) {
                dateFormat =
                  chartContext.options.axis.x.time &&
                  chartContext.options.axis.x.time.format.tooltip
              }

              let tooltipTitle = xVal
              if (typeof xVal === 'object') {
                tooltipTitle = moment(xVal).format(dateFormat)
              } else if (typeof xVal === 'number') {
                tooltipTitle = chartContext.data.x[xVal]
              }
              let tooltipConfig = {
                position: {
                  left: d3.event.pageX,
                  top: d3.event.pageY,
                },
                title: tooltipTitle,
                mode: 1,
                data: tooltipData,
                dataPoint: chartContext.options.dataPoints,
              }
              if (chartContext.tooltipCallback) {
                chartContext.tooltipCallback(
                  tooltipData,
                  tooltipConfig,
                  tooltipTitle
                )
                return null
              }
              tooltip.showTooltipForNewChart(tooltipConfig)
            })
            .on('mouseout', function() {
              tooltip.hideTooltip()
            })
        }
      }
      if (d.enumChart) {
        params.grid.y = false
        params.size.height = 25
        params.axis.x.label.text = ''
        params.axis.x.show = false
        params.axis.y.show = false
        params.axis.y2.show = false
        params.padding.bottom = -25

        params.axis.x.min = chartContext.dateRange.range.highResMin
        params.axis.x.max = chartContext.dateRange.range.highResMax

        let offClassName = 'fc-boolean-' + d.key + '-0'
        let classes = {}
        for (let enumVal of Object.keys(d.enumMap)) {
          classes[enumVal] = 'fc-boolean-' + d.key + '-' + enumVal
        }

        let booleanTimeline =
          chartContext.booleanData &&
          chartContext.booleanData[d.key.replace('datapoint_', '')]
            ? chartContext.booleanData[d.key.replace('datapoint_', '')]
            : null
        if (booleanTimeline && booleanTimeline.length) {
          let regions = []
          let x = []
          let y = []
          for (let range of booleanTimeline) {
            x.push(range.start)
            y.push(null)
            x.push(range.end)
            y.push(null)
            regions.push({
              start: range.start,
              end: range.end,
              class: range.value ? classes[range.value] : offClassName,
            })
          }
          params.regions = regions
          params.data.json.x = x
          params.data.json[d.key.replace('datapoint_', '')] = y
          params.data.xFormat = '%m-%d-%Y %H:%M'
          params.axis.x.tick.values = null
        } else {
          params.data.xFormat = '%m-%d-%Y %H:%M'
          params.regions = {
            start: moment(chartContext.dateRange.time[0])
              .tz(Vue.prototype.$timezone)
              .format('MM-DD-YYYY HH:mm'),
            end: moment(
              chartContext.dateRange.time[1]
                ? chartContext.dateRange.time[1]
                : chartContext.dateRange.time[0]
            )
              .tz(Vue.prototype.$timezone)
              .format('MM-DD-YYYY HH:mm'),
            class: offClassName,
          }
          params.data.json.x = [
            moment(chartContext.dateRange.time[0])
              .tz(Vue.prototype.$timezone)
              .format('MM-DD-YYYY HH:mm'),
            moment(
              chartContext.dateRange.time[1]
                ? chartContext.dateRange.time[1]
                : chartContext.dateRange.time[0]
            )
              .tz(Vue.prototype.$timezone)
              .format('MM-DD-YYYY HH:mm'),
          ]
          params.data.json[d.key.replace('datapoint_', '')] = [null, null]
          params.axis.x.tick.values = null
        }

        params.tooltip.show = false
        let c3Self = this
        params.onafterinit = function() {
          let self = this
          let parentSelector = d3.select(this.api.element)

          for (let enumVal of Object.keys(d.enumMap)) {
            parentSelector
              .selectAll('.' + classes[enumVal])
              .style('fill', d.enumColorMap[enumVal])
            parentSelector
              .selectAll('.' + classes[enumVal] + ' rect')
              .style('fill-opacity', '0.9')
            parentSelector
              .selectAll('.' + classes[enumVal] + ' rect')
              .classed('fc-boolean-rect-09', true)

            parentSelector
              .selectAll('.' + classes[enumVal])
              .on('mousemove', function() {
                let mouse = d3.mouse(this)
                let closestDate = c3Self.newFindClosest(
                  chartContext.data.x,
                  chartContext.options.axis.x.time.format.d3Format,
                  self.x.invert(mouse[0])
                )
                let closestIdx = closestDate.i

                let xVal = closestDate.date

                let tooltipData = []

                for (let dataPoint of chartContext.options.dataPoints) {
                  if (dataPoint.type === 'group') {
                    if (dataPoint.children) {
                      let children = []
                      for (let dp of dataPoint.children) {
                        let dJson = chartContext.data[dp.key]
                        let unit = chartContext.unitMap[dp.dpKey || dp.key]
                        let enumMap = chartContext.enumMap[dp.dpKey || dp.key]

                        let actualVal =
                          dJson[closestIdx] === null ? 0 : dJson[closestIdx]
                        if (
                          dp.dataType === 'BOOLEAN' ||
                          dp.dataType === 'ENUM'
                        ) {
                          actualVal = c3Self.getBooleanValue(
                            chartContext,
                            xVal,
                            dp.key,
                            closestIdx
                          )
                        }

                        let tVal = [
                          'days',
                          'hours',
                          'minutes',
                          'seconds',
                          'milliseconds',
                        ].includes(unit)
                          ? formatDuration(actualVal, unit, dp.convertTounit)
                          : (unit === '$' ? unit + ' ' : '') +
                            actualVal +
                            (unit && unit !== '$' ? ' ' + unit : '')
                        let val = actualVal

                        if (enumMap && enumMap[val]) {
                          tVal = enumMap[val]
                        }
                        if (dJson) {
                          let data = {
                            label: dp.label,
                            value: tVal,
                          }
                          if (val !== '---') {
                            data['color'] =
                              dp.dataType === 'ENUM'
                                ? dp.enumColorMap[val]
                                : dp.color
                          }
                          children.push(data)
                        }
                      }
                      tooltipData.push({
                        label: dataPoint.label,
                        is_group: true,
                        children: children,
                      })
                    }
                  } else {
                    let dJson = chartContext.data[dataPoint.key]
                    let unit =
                      chartContext.unitMap[dataPoint.dpKey || dataPoint.key]
                    let enumMap =
                      chartContext.enumMap[dataPoint.dpKey || dataPoint.key]

                    let actualVal =
                      dJson[closestIdx] === null ? 0 : dJson[closestIdx]
                    if (
                      dataPoint.dataType === 'BOOLEAN' ||
                      dataPoint.dataType === 'ENUM'
                    ) {
                      actualVal = c3Self.getBooleanValue(
                        chartContext,
                        xVal,
                        dataPoint.key,
                        closestIdx
                      )
                    }

                    let tVal = [
                      'days',
                      'hours',
                      'minutes',
                      'seconds',
                      'milliseconds',
                    ].includes(unit)
                      ? formatDuration(actualVal, unit, dataPoint.convertTounit)
                      : (unit === '$' ? unit + ' ' : '') +
                        actualVal +
                        (unit && unit !== '$' ? ' ' + unit : '')
                    let val = actualVal

                    if (enumMap && enumMap[val]) {
                      tVal = enumMap[val]
                    }
                    if (dJson) {
                      let data = {
                        label: dataPoint.label,
                        value: tVal,
                      }
                      if (val !== '---') {
                        data['color'] =
                          dataPoint.dataType === 'ENUM'
                            ? dataPoint.enumColorMap[val]
                            : dataPoint.color
                      }
                      tooltipData.push(data)
                    }
                  }
                }

                let dateFormat = 'LLL'
                if (
                  chartContext.options.axis.x.time &&
                  chartContext.options.axis.x.time.format
                ) {
                  dateFormat =
                    chartContext.options.axis.x.time &&
                    chartContext.options.axis.x.time.format.tooltip
                }

                let tooltipTitle = xVal
                if (typeof xVal === 'object') {
                  tooltipTitle = moment(xVal).format(dateFormat)
                } else if (typeof xVal === 'number') {
                  tooltipTitle = chartContext.data.x[xVal]
                }
                let tooltipConfig = {
                  position: {
                    left: d3.event.pageX,
                    top: d3.event.pageY,
                  },
                  title: tooltipTitle,
                  mode: 1,
                  data: tooltipData,
                  dataPoint: chartContext.options.dataPoints,
                }
                if (chartContext.tooltipCallback) {
                  chartContext.tooltipCallback(
                    tooltipData,
                    tooltipConfig,
                    tooltipTitle
                  )
                  return null
                }
                tooltip.showTooltipForNewChart(tooltipConfig)
              })
              .on('mouseout', function() {
                tooltip.hideTooltip()
              })
          }

          parentSelector
            .selectAll('.' + offClassName)
            .style('fill', d.enumChart.color)
          parentSelector
            .selectAll('.' + offClassName + ' rect')
            .style('fill-opacity', '0.1')
          parentSelector.selectAll('.bb-chart').style('display', 'none') // removed the bb-chart element from the boolean chart. it causes tool tip issue. mouse over event fires when mouse over the alarm charts not a boolean chart

          parentSelector
            .selectAll('.' + offClassName)
            .on('mousemove', function() {
              let mouse = d3.mouse(this)
              let closestDate = c3Self.newFindClosest(
                chartContext.data.x,
                chartContext.options.axis.x.time.format.d3Format,
                self.x.invert(mouse[0])
              )
              let closestIdx = closestDate.i

              let xVal = closestDate.date

              let tooltipData = []

              for (let dataPoint of chartContext.options.dataPoints) {
                if (dataPoint.type === 'group') {
                  if (dataPoint.children) {
                    let children = []
                    for (let dp of dataPoint.children) {
                      let dJson = chartContext.data[dp.key]
                      let unit = chartContext.unitMap[dp.dpKey || dp.key]
                      let enumMap = chartContext.enumMap[dp.dpKey || dp.key]

                      let actualVal =
                        dJson[closestIdx] === null ? 0 : dJson[closestIdx]
                      if (dp.dataType === 'BOOLEAN' || dp.dataType === 'ENUM') {
                        actualVal = c3Self.getBooleanValue(
                          chartContext,
                          xVal,
                          dp.key,
                          closestIdx
                        )
                      }

                      let tVal = [
                        'days',
                        'hours',
                        'minutes',
                        'seconds',
                        'milliseconds',
                      ].includes(unit)
                        ? formatDuration(actualVal, unit, dp.convertTounit)
                        : (unit === '$' ? unit + ' ' : '') +
                          actualVal +
                          (unit && unit !== '$' ? ' ' + unit : '')
                      let val = actualVal

                      if (enumMap && enumMap[val]) {
                        tVal = enumMap[val]
                      }
                      if (dJson) {
                        let data = {
                          label: dp.label,
                          value: tVal,
                        }
                        if (val !== '---') {
                          data['color'] =
                            dp.dataType === 'ENUM'
                              ? dp.enumColorMap[val]
                              : dp.color
                        }
                        tooltipData.push(data)
                      }
                    }
                    tooltipData.push({
                      label: dataPoint.label,
                      is_group: true,
                      children: children,
                    })
                  }
                } else {
                  let dJson = chartContext.data[dataPoint.key]
                  let unit =
                    chartContext.unitMap[dataPoint.dpKey || dataPoint.key]
                  let enumMap =
                    chartContext.enumMap[dataPoint.dpKey || dataPoint.key]

                  let actualVal =
                    dJson[closestIdx] === null ? 0 : dJson[closestIdx]
                  if (
                    dataPoint.dataType === 'BOOLEAN' ||
                    dataPoint.dataType === 'ENUM'
                  ) {
                    actualVal = c3Self.getBooleanValue(
                      chartContext,
                      xVal,
                      dataPoint.key,
                      closestIdx
                    )
                  }

                  let tVal = [
                    'days',
                    'hours',
                    'minutes',
                    'seconds',
                    'milliseconds',
                  ].includes(unit)
                    ? formatDuration(actualVal, unit, dataPoint.convertTounit)
                    : (unit === '$' ? unit + ' ' : '') +
                      actualVal +
                      (unit && unit !== '$' ? ' ' + unit : '')
                  let val = actualVal

                  if (enumMap && enumMap[val]) {
                    tVal = enumMap[val]
                  }
                  if (dJson) {
                    let data = {
                      label: dataPoint.label,
                      value: tVal,
                    }
                    if (val !== '---') {
                      data['color'] =
                        dataPoint.dataType === 'ENUM'
                          ? dataPoint.enumColorMap[val]
                          : dataPoint.color
                    }
                    tooltipData.push(data)
                  }
                }
              }

              let dateFormat = 'LLL'
              if (
                chartContext.options.axis.x.time &&
                chartContext.options.axis.x.time.format
              ) {
                dateFormat =
                  chartContext.options.axis.x.time &&
                  chartContext.options.axis.x.time.format.tooltip
              }

              let tooltipTitle = xVal
              if (typeof xVal === 'object') {
                tooltipTitle = moment(xVal).format(dateFormat)
              } else if (typeof xVal === 'number') {
                tooltipTitle = chartContext.data.x[xVal]
              }
              let tooltipConfig = {
                position: {
                  left: d3.event.pageX,
                  top: d3.event.pageY,
                },
                title: tooltipTitle,
                mode: 1,
                data: tooltipData,
                dataPoint: chartContext.options.dataPoints,
              }
              if (chartContext.tooltipCallback) {
                chartContext.tooltipCallback(
                  tooltipData,
                  tooltipConfig,
                  tooltipTitle
                )
                return null
              }
              tooltip.showTooltipForNewChart(tooltipConfig)
            })
            .on('mouseout', function() {
              tooltip.hideTooltip()
            })
        }
      }

      if (index === dataList.length && !chartContext.scatterConfig) {
        if (!params.axis.x.show && (d.enumChart || d.booleanChart)) {
          params.axis.x.show = true
          params.size.height = 95
          params.axis.x.label.text = 'Timestamp'
          params.padding.bottom = 0
        } else if (params.axis.x.show) {
          if (!d.enumChart && !d.booleanChart) {
            params.size.height = params.size.height + 50
          }
          params.padding.bottom = 0
        }
      }
      if (params.axis.y && params.axis.y.show) {
        params.axis.y.label = ' '
      }
      if (params.axis.y2 && params.axis.y2.show) {
        params.axis.y2.label = ' '
      }
      if (chartContext.options.customizeC3) {
        let mergedparams = deepmerge.objectAssignDeep(
          params,
          chartContext.options.customizeC3
        )
        params = mergedparams
      }
      params.data.json = Object.freeze(params.data.json)
      c3Data.push(params)
    }
    return c3Data
  },

  newFindClosest(dates, d3Format, testDate) {
    let date = new Date(testDate)
    let max = dates.length

    let parseTime = d3.timeParse(d3Format)

    let lowIdx = null
    let lowDate = null
    let lowDiff = null
    for (let i = 0; i < max; i++) {
      let dateObj = parseTime(dates[i])
      let d = date - dateObj
      if (d < 0) {
        d = -d
      }
      if (lowDiff) {
        if (lowDiff > d) {
          lowDiff = d
          lowDate = dateObj
          lowIdx = i
        }
      } else {
        lowDate = dateObj
        lowDiff = d
        lowIdx = i
      }
    }
    return {
      i: lowIdx,
      date: lowDate,
    }
  },

  prepareGeneralOptions(chartContext, params, multichartKey) {
    let generalOptions = chartContext.options.general

    let grid = {
      x: {
        show: generalOptions.grid.x,
      },
      y: {
        show: generalOptions.grid.y,
      },
    }
    if (
      chartContext.options.type !== 'pie' &&
      chartContext.options.type !== 'donut' &&
      chartContext.options.type !== 'gauge'
    ) {
      grid.y.lines = [
        {
          value: 0,
          class: 'x-axis-zero-line',
        },
      ]
      grid.y2 = {
        lines: [
          {
            value: 0,
            class: 'x-axis-zero-line',
          },
        ],
      }

      if (
        chartContext.options.safeLimit &&
        chartContext.options.safeLimit.length
      ) {
        let points = []
        chartContext.options.dataPoints.forEach(dp => {
          if (dp.type === 'group') {
            if (multichartKey) {
              if (multichartKey === 'group_' + dp.label) {
                points.push(...dp.children)
              }
            } else {
              points.push(...dp.children)
            }
          } else {
            if (multichartKey) {
              if (multichartKey === 'datapoint_' + dp.key) {
                points.push(dp)
              }
            } else {
              points.push(dp)
            }
          }
        })

        let safeLimitColorMap = {}
        let keyIndex = 0
        for (let sl of chartContext.options.safeLimit) {
          keyIndex = keyIndex + 1
          let dpObj = points.find(dp => dp.key === sl.datapoint)
          if (!dpObj) {
            continue
          }
          if (dpObj.safelimit === true) {
            let className = 'safelimit-' + keyIndex
            safeLimitColorMap[className] = dpObj.color

            grid.y.lines.push({
              value: sl.value,
              text: sl.label + ': ' + sl.value,
              class: className + ' ' + sl.key,
              axis: dpObj.axes,
              position: dpObj.axes === 'y2' ? 'end' : 'start',
            })
          }
        }

        params.onafterinit = function() {
          let parentSelector = d3.select(this.api.element)

          for (let key in safeLimitColorMap) {
            parentSelector
              .selectAll('.' + key + ' line')
              .style('stroke', safeLimitColorMap[key])
            parentSelector
              .selectAll('.' + key + ' text')
              .style('fill', safeLimitColorMap[key])

            parentSelector
              .selectAll('.' + key + '.min line')
              .style('stroke-dasharray', '2,2')
            parentSelector
              .selectAll('.' + key + '.max line')
              .style('stroke-dasharray', '8,2')
          }
        }
      }
      if (
        chartContext.options.benchmark &&
        (chartContext.options.benchmark.variances ||
          chartContext.options.benchmark.constant ||
          chartContext.options.benchmark.lines)
      ) {
        let points = []
        chartContext.options.dataPoints.forEach(dp => {
          if (dp.type === 'group') {
            if (multichartKey) {
              if (multichartKey === 'group_' + dp.label) {
                points.push(...dp.children)
              }
            } else {
              points.push(...dp.children)
            }
          } else {
            if (multichartKey) {
              if (multichartKey === 'datapoint_' + dp.key) {
                points.push(dp)
              }
            } else {
              points.push(dp)
            }
          }
        })

        let colorMap = {}
        let keyIndex = 0
        if (chartContext.options.benchmark.constant) {
          grid.y.lines.push({
            value: chartContext.options.benchmark.constant,
            text: `Benchmark : ${chartContext.options.benchmark.constant}`,
            class: `safelimit-constant benchmark`,
            axis: 'y',
            position: 'end',
          })
        }
        if (isArray(chartContext.options.benchmark.lines)) {
          if (!isEmpty(chartContext.options.benchmark.lines)) {
            chartContext.options.benchmark.lines.forEach(constantLine => {
              keyIndex = keyIndex + 1
              if (constantLine.value != '' && constantLine.show === true) {
                let className = `safelimit-constantLine-${keyIndex}`
                colorMap[className] = constantLine.color
                grid.y.lines.push({
                  value: constantLine.value,
                  text: `${constantLine.label} : ${constantLine.value}`,
                  class: `${className} benchmark`,
                  axis: 'y',
                  position: 'end',
                })
              }
            })
          }
        }
        points.forEach(dp => {
          if (
            chartContext.options.benchmark.variances[dp.key] &&
            chartContext.options.benchmark.variances[dp.key].length &&
            chartContext.reportVarianceData
          ) {
            chartContext.options.benchmark.variances[dp.key].forEach(
              variance => {
                if (chartContext.reportVarianceData[`${dp.key}.${variance}`]) {
                  keyIndex = keyIndex + 1
                  let className = `safelimit-${keyIndex}`
                  colorMap[className] = dp.color
                  let value = parseInt(
                    chartContext.reportVarianceData[`${dp.key}.${variance}`]
                  )
                  let label = chartContext.options.benchmark.label
                    ? chartContext.options.benchmark.label
                    : 'Benchmark'
                  grid.y.lines.push({
                    value: value,
                    text: `${label}: ${value}`,
                    class: `${className} benchmark`,
                    axis: dp.axes,
                    position: 'end',
                  })
                }
              }
            )
          }
        })

        params.onafterinit = function() {
          let parentSelector = d3.select(this.api.element)

          for (let key in colorMap) {
            parentSelector
              .selectAll('.' + key + ' line')
              .style('stroke', colorMap[key])
            parentSelector
              .selectAll('.' + key + ' text')
              .style('fill', colorMap[key])

            parentSelector
              .selectAll('.' + key + '.min line')
              .style('stroke-dasharray', '2,2')
            parentSelector
              .selectAll('.' + key + '.max line')
              .style('stroke-dasharray', '8,2')
          }

          parentSelector
            .selectAll('.safelimit-constant line')
            .style('stroke', '#000')
          parentSelector
            .selectAll('.safelimit-constant text')
            .style('fill', '#000')
        }
      }
    }
    if (chartContext.scatterConfig) {
      grid['focus'] = {
        show: true,
        y: true,
      }
    }

    params.grid = grid
    params.point = chartContext.options.general.point
      ? chartContext.options.general.point
      : generalOptions.point

    params.line = {
      connectNull:
        chartContext.options.line &&
        chartContext.options.line.connectNull !== null
          ? chartContext.options.line.connectNull
          : true,
    }
  },

  prepareMultichartData(chartContext) {
    let chartDataList = []
    if (
      chartContext.options.dataPoints &&
      chartContext.options.dataPoints.length
    ) {
      for (let dataPoint of chartContext.options.dataPoints) {
        let data = !chartContext.scatterConfig
          ? {
              x: 'x',
            }
          : {}
        if (
          chartContext.options.axis.x.time &&
          chartContext.options.axis.x.time.format
        ) {
          data.xFormat = chartContext.options.axis.x.time.format.d3Format
        }
        if (chartContext.options.general.labels) {
          data.labels = true
        }

        let names = {}
        let colors = {}
        let types = {}
        let groups = []
        let axes = {}
        let jsonData = {}
        let xs = {}
        let xMap = {}
        if (chartContext.scatterConfig && chartContext.xMap) {
          xMap = chartContext.xMap
        }
        if (!chartContext.options.isGroupedByTime) {
          if (
            dataPoint.type === 'datapoint' ||
            dataPoint.type === 'rangeGroup'
          ) {
            if (dataPoint.visible) {
              if (chartContext.data[dataPoint.key]) {
                jsonData[dataPoint.key] = chartContext.data[dataPoint.key]
              }
              if (
                xMap[dataPoint.key] ||
                (dataPoint.key.split('_').length &&
                  xMap[dataPoint.key.split('_')[0]])
              ) {
                if (dataPoint.pointType === 3) {
                  let key = dataPoint.key.split('_')[0]
                  xs[dataPoint.key] = xMap[key]
                } else {
                  xs[dataPoint.key] = xMap[dataPoint.key]
                }
              }
              if (dataPoint.label) {
                names[dataPoint.key] = dataPoint.label
              }
              if (dataPoint.chartType) {
                types[dataPoint.key] =
                  dataPoint.chartType === 'stackedbar' ||
                  dataPoint.chartType === 'histogram'
                    ? 'bar'
                    : dataPoint.chartType
              }
              if (dataPoint.color) {
                colors[dataPoint.key] = dataPoint.color
              }
              if (dataPoint.axes) {
                axes[dataPoint.key] = dataPoint.axes
              }
              if (chartContext.scatterConfig) {
                types[dataPoint.key] = 'scatter'
                if (dataPoint.pointType === 3) {
                  //trendLine
                  types[dataPoint.key] = 'line'
                }
              } else if (dataPoint.pointType === 3) {
                //trendLine
                types[dataPoint.key] = 'line'
              }
            } else if (dataPoint.visible === false) {
              continue
            }
          } else {
            if (dataPoint.children && dataPoint.children.length) {
              let booleanDpList = dataPoint.children.filter(
                dp => dp.dataType === 'BOOLEAN' || dp.dataType === 'ENUM'
              )
              if (booleanDpList && booleanDpList.length) {
                for (let child of booleanDpList) {
                  let booleanData = !chartContext.scatterConfig
                    ? {
                        x: 'x',
                      }
                    : {}
                  if (
                    chartContext.options.axis.x.time &&
                    chartContext.options.axis.x.time.format
                  ) {
                    booleanData.xFormat =
                      chartContext.options.axis.x.time.format.d3Format
                  }
                  if (chartContext.options.general.labels) {
                    booleanData.labels = true
                  }

                  let booleanNames = {}
                  let booleanColors = {}
                  let booleanTypes = {}
                  let booleanAxes = {}
                  let booleanJsonData = {}
                  let booleanxs = {}

                  if (child.visible) {
                    if (chartContext.data[child.key]) {
                      booleanJsonData[child.key] = chartContext.data[child.key]
                    }
                    if (xMap[child.key]) {
                      booleanxs[child.key] = xMap[child.key]
                    }

                    if (child.label) {
                      booleanNames[child.key] = child.label
                    }
                    if (child.chartType) {
                      booleanTypes[child.key] = child.chartType
                    }
                    if (child.color) {
                      booleanColors[child.key] = child.color
                    }
                    if (child.axes) {
                      booleanAxes[child.key] = child.axes
                    }
                  } else if (child.visible === false) {
                    continue
                  }
                  if (!chartContext.scatterConfig) {
                    booleanJsonData.x = chartContext.data.x
                  }

                  booleanData.json = booleanJsonData
                  booleanData.names = booleanNames
                  booleanData.colors = booleanColors
                  booleanData.types = booleanTypes
                  booleanData.type = 'area'
                  booleanData.axes = booleanAxes
                  booleanData.key =
                    child.type + '_' + (child.key ? child.key : child.label)
                  booleanData.label = child.label
                  if (Object.keys(booleanxs).length !== 0) {
                    booleanData['xs'] = booleanxs
                    for (let xkey of Object.keys(booleanxs)) {
                      if (
                        !booleanData.json[booleanxs[xkey]] &&
                        chartContext.data[booleanxs[xkey]]
                      ) {
                        booleanData.json[booleanxs[xkey]] =
                          chartContext.data[booleanxs[xkey]]
                      }
                    }
                  }
                  booleanData.booleanChart =
                    child.dataType === 'BOOLEAN'
                      ? {
                          color: child.color,
                        }
                      : null
                  if (child.dataType === 'ENUM') {
                    booleanData.enumChart =
                      child.dataType === 'ENUM'
                        ? {
                            color: child.color,
                          }
                        : null
                    booleanData.enumMap = child.enumMap
                    if (child.enumColorMap) {
                      booleanData.enumColorMap = child.enumColorMap
                    }
                  }
                  chartDataList.push(booleanData)
                }
                continue
              } else {
                let group = []
                let visibleChildPoints = 0
                for (let child of dataPoint.children) {
                  if (child.visible) {
                    if (chartContext.data[child.key]) {
                      jsonData[child.key] = chartContext.data[child.key]
                    }
                    if (
                      xMap[child.key] ||
                      (child.key.split('_').length &&
                        xMap[child.key.split('_')[0]])
                    ) {
                      if (dataPoint.pointType === 3) {
                        let key = child.key.split('_')[0]
                        xs[child.key] = xMap[key]
                      } else {
                        xs[child.key] = xMap[child.key]
                      }
                    }

                    if (child.label) {
                      names[child.key] = child.label
                    }
                    if (child.chartType) {
                      types[child.key] = child.chartType
                    }
                    if (child.color) {
                      colors[child.key] = child.color
                    }
                    if (child.axes) {
                      axes[child.key] = child.axes
                    }

                    if (child.chartType === 'stackedbar') {
                      types[child.key] = 'bar'
                      group.push(child.key ? child.key + '' : child.key)
                    }
                    if (chartContext.scatterConfig) {
                      types[child.key] = 'scatter'
                      if (dataPoint.pointType === 3) {
                        //trendLine
                        types[child.key] = 'line'
                      }
                    } else if (dataPoint.pointType === 3) {
                      //trendLine
                      types[child.key] = 'line'
                    }
                  }
                  if (
                    typeof child.visible === 'undefined' ||
                    child.visible === true
                  ) {
                    visibleChildPoints += 1
                  }
                }
                if (group.length) {
                  groups.push(group)
                }
                if (visibleChildPoints === 0) {
                  continue
                }
              }
            }
          }
        } else if (chartContext.options.isGroupedByTime) {
          let columns = []
          let pointaxes = {}
          let pointtypes = {}
          if (
            dataPoint.type === 'datapoint' ||
            dataPoint.type === 'rangeGroup'
          ) {
            columns = Object.keys(chartContext.options.timeGroupOptions).filter(
              k => k.split('_')[0] === dataPoint.key
            )
            pointaxes[dataPoint.key] = dataPoint.axes
            pointtypes[dataPoint.key] = dataPoint.chartType
          } else if (dataPoint.children && dataPoint.children.length) {
            for (let child of dataPoint.children) {
              let col = Object.keys(
                chartContext.options.timeGroupOptions
              ).filter(k => k.split('_')[0] === child.key)
              columns = columns.concat(col)
              pointaxes[child.key] = child.axes
              pointtypes[child.key] = child.chartType
            }
          }
          let group = []
          for (let column of columns) {
            if (chartContext.options.timeGroupOptions[column].name) {
              if (pointaxes[column.split('_')[0]]) {
                axes[column] = pointaxes[column.split('_')[0]]
              }
              if (pointtypes[column.split('_')[0]]) {
                types[column] =
                  pointtypes[column.split('_')[0]] === 'stackedbar'
                    ? 'bar'
                    : pointtypes[column.split('_')[0]]

                if (pointtypes[column.split('_')[0]] === 'stackedbar') {
                  group.push(column)
                }
              }

              names[column] = chartContext.options.timeGroupOptions[column].name
              colors[column] =
                chartContext.options.timeGroupOptions[column].color
              jsonData[column] = chartContext.data[column]
            }
          }
          if (group.length) {
            groups.push(group)
          }
        }
        if (!chartContext.scatterConfig) {
          jsonData.x = chartContext.data.x
        }

        data.json = jsonData
        data.names = names
        data.colors = colors
        data.types = types
        if (Object.keys(xs).length !== 0) {
          data['xs'] = xs
          data['xSort'] = false
          for (let xkey of Object.keys(xs)) {
            if (!data.json[xs[xkey]] && chartContext.data[xs[xkey]]) {
              data.json[xs[xkey]] = chartContext.data[xs[xkey]]
            }
          }
        } else if (
          chartContext.options.regressionConfig &&
          chartContext.options.regressionConfig.length !== 0
        ) {
          data['xSort'] = false
        }
        let allowedTypes = ['scatter', 'bubble']
        let scatterJSOn = {}
        let colorClusters = {}
        if (
          (Object.keys(types).find(key => types[key] === 'scatter') &&
            !(
              chartContext.options.regressionConfig &&
              chartContext.options.regressionConfig.length !== 0
            )) ||
          (chartContext.options.trendLine &&
            chartContext.options.trendLine.enable)
        ) {
          data.point = {}
          data.point['show'] = true
          data.point['pattern'] = []
          if (dataPoint.type === 'datapoint') {
            if (
              allowedTypes.includes(types[dataPoint.key]) ||
              dataPoint.pointType === 6
            ) {
              if (chartContext.scatterConfig && dataPoint.pointCustomization) {
                let pointPattern = this.getPointPattern(
                  dataPoint.pointCustomization.shape
                )
                if (dataPoint.axes !== 'x') {
                  data.point.pattern.push(pointPattern)
                }
                scatterJSOn[dataPoint.key] = dataPoint.pointCustomization
                colorClusters[dataPoint.key] = this.getClusters(
                  data.json[dataPoint.key].slice()
                )
                if (
                  chartContext.options.scatter.size &&
                  dataPoint.axes !== 'x'
                ) {
                  data.json[dataPoint.key] = this.getBubbleFormatedData(
                    data.json[dataPoint.key],
                    data.json[dataPoint.pointCustomization.size.basedOn]
                  )
                  data.types[dataPoint.key] = 'bubble'
                } else {
                  data.types[dataPoint.key] = 'scatter'
                }
              } else {
                data.point.pattern.push('circle')
              }
            } else {
              data.point.pattern.push('<polygon></polygon>')
            }
          } else if (dataPoint.type === 'systemgroup') {
            for (let child of dataPoint.children) {
              if (allowedTypes.includes(types[child.key])) {
                if (chartContext.scatterConfig && child.pointCustomization) {
                  let pointPattern = this.getPointPattern(
                    child.pointCustomization.shape
                  )
                  if (child.axes !== 'x') {
                    data.point.pattern.push(pointPattern)
                  }
                  scatterJSOn[child.key] = child.pointCustomization
                  colorClusters[child.key] = this.getClusters(
                    data.json[child.key]
                  )
                  if (
                    chartContext.options.scatter.size.mode === 'reading' &&
                    child.axes !== 'x'
                  ) {
                    data.json[child.key] = this.getBubbleFormatedData(
                      data.json[child.key],
                      data.json[child.pointCustomization.size.basedOn]
                    )
                    data.types[child.key] = 'bubble'
                  } else {
                    data.types[child.key] = 'scatter'
                  }
                } else {
                  data.point.pattern.push('circle')
                }
              } else {
                data.point.pattern.push('<polygon></polygon>')
              }
            }
          } else if (dataPoint.type === 'rangeGroup') {
            if (allowedTypes.includes(types[dataPoint.key])) {
              if (chartContext.scatterConfig && dataPoint.pointCustomization) {
                let pointPattern = this.getPointPattern(
                  dataPoint.pointCustomization.shape
                )
                if (dataPoint.axes !== 'x') {
                  data.point.pattern.push(pointPattern)
                }
                scatterJSOn[dataPoint.key] = dataPoint.pointCustomization
                colorClusters[dataPoint.key] = this.getClusters(
                  data.json[dataPoint.key]
                )
                if (
                  chartContext.options.scatter.size.mode === 'reading' &&
                  dataPoint.axes !== 'x'
                ) {
                  data.json[dataPoint.key] = this.getBubbleFormatedData(
                    data.json[dataPoint.key],
                    data.json[dataPoint.pointCustomization.size.basedOn]
                  )
                  data.types[dataPoint.key] = 'bubble'
                } else {
                  data.types[dataPoint.key] = 'scatter'
                }
              } else {
                data.point.pattern.push('circle')
              }
            } else {
              data.point.pattern.push('<polygon></polygon>')
            }
          } else {
            if (dataPoint.children && dataPoint.children.length) {
              for (let child of dataPoint.children) {
                if (allowedTypes.includes(types[child.key])) {
                  if (chartContext.scatterConfig && child.pointCustomization) {
                    let pointPattern = this.getPointPattern(
                      child.pointCustomization.shape
                    )
                    if (child.axes !== 'x') {
                      data.point.pattern.push(pointPattern)
                    }
                    scatterJSOn[child.key] = child.pointCustomization
                    colorClusters[child.key] = this.getClusters(
                      data.json[child.key]
                    )
                    if (
                      chartContext.options.scatter.size.mode === 'reading' &&
                      child.axes !== 'x'
                    ) {
                      data.json[child.key] = this.getBubbleFormatedData(
                        data.json[child.key],
                        data.json[child.pointCustomization.size.basedOn]
                      )
                      data.types[child.key] = 'bubble'
                    } else {
                      data.types[child.key] = 'scatter'
                    }
                  } else {
                    data.point.pattern.push('circle')
                  }
                } else {
                  data.point.pattern.push('<polygon></polygon>')
                }
              }
            }
          }
        }
        data.color = function(color, d) {
          if (scatterJSOn[d.id] && chartContext.options.scatter.color.mode) {
            let colorKey = scatterJSOn[d.id].color.basedOn
            if (colorClusters[colorKey]) {
              let colorScale =
                chartContext.options.scatter.color.mode === 'monochrome'
                  ? chroma.scale(['white', color]).colors(6)
                  : ['#1d7f01', '#6cb302', '#e9f501', '#fda504', '#fb5905']
              let colorRange = d3
                .scaleLinear()
                .domain(colorClusters[colorKey])
                .range(
                  chartContext.options.scatter.color.mode === 'monochrome'
                    ? colorScale.slice(1, 6)
                    : colorScale
                )

              if (typeof d === 'object') {
                // for data point
                if (d.value) {
                  if (d.value.y) {
                    return colorRange(d.value.y)
                  }
                  return colorRange(d.value)
                  // for data type (ex. line, bar)
                } else {
                  return color
                }
                // for label
              } else {
                return color
              }
            }
          } else if (
            chartContext.options.scatter &&
            chartContext.options.scatter.color.mode === 'monochrome'
          ) {
            if (colorClusters[d.id]) {
              let colorScale = chroma.scale(['white', color]).colors(6)
              let colorRange = d3
                .scaleLinear()
                .domain(colorClusters[d.id])
                .range(colorScale.slice(1, 6))
              if (typeof d === 'object') {
                if (d.value) {
                  if (d.value.y) {
                    return colorRange(d.value.y)
                  }
                  return colorRange(d.value)
                } else {
                  return color
                }
              } else {
                return color
              }
            }
          } else if (typeof d === 'object' && d.value) {
            let matchpoint = chartContext.options.dataPoints.find(
              dp => dp.key === d.id
            )
            if (matchpoint && matchpoint.colorCriteria) {
              let min, max, mincolor, maxcolor, resultcolor
              for (let crit of matchpoint.colorCriteria) {
                if (typeof min === 'undefined' || min > crit.min) {
                  min = crit.min
                  mincolor = crit.color
                }
                if (typeof max === 'undefined' || max < crit.max) {
                  max = crit.max
                  maxcolor = crit.color
                }

                if (d.value >= crit.min && d.value < crit.max)
                  resultcolor = crit.color
                else if (d.value < min) resultcolor = mincolor
                else if (d.value > max) resultcolor = maxcolor
              }
              return resultcolor
            }
          }
          return color
        }
        data.type =
          chartContext.Readingdp || chartContext.scatterConfig
            ? 'scatter'
            : 'area'
        if (groups.length) {
          data.groups = groups
        }
        data.axes = axes
        data.key =
          dataPoint.type +
          '_' +
          (dataPoint.key ? dataPoint.key : dataPoint.label)
        data.label = dataPoint.label
        data.booleanChart =
          dataPoint.dataType === 'BOOLEAN'
            ? {
                color: dataPoint.color,
              }
            : null
        if (dataPoint.dataType === 'ENUM') {
          data.enumChart =
            dataPoint.dataType === 'ENUM'
              ? {
                  color: dataPoint.color,
                }
              : null
          data.enumMap = dataPoint.enumMap
          if (dataPoint.enumColorMap) {
            data.enumColorMap = dataPoint.enumColorMap
          }
        }
        chartDataList.push(data)
      }
    }
    return chartDataList
  },

  transformPieData(type, chartContext) {
    let data = chartContext.data
    let options = chartContext.options

    let newData = {}
    let colors = {}
    let labels = {}

    let dataKey = Object.keys(data).filter(k => k !== 'x')[0]
    for (let i = 0; i < data.x.length; i++) {
      let key = data.x[i]
      newData[key] = [data[dataKey][i]]

      if (options.axis.x.datatype.indexOf('date') !== -1) {
        let parseTime = d3.timeParse(
          chartContext.options.axis.x.time.format.d3Format
        )
        labels[key] = this.newGetDateFormat(
          parseTime(key),
          chartContext.dateRange.period,
          chartContext.dateRange.rangeType
        )
      }
      if (options.xColorMap && options.xColorMap[key]) {
        colors[key] = options.xColorMap[key]
      }
    }
    newData.x = [dataKey]

    chartContext.xActualDataType = chartContext.options.axis.x.datatype
    chartContext.options.axis.x.datatype = 'string'
    chartContext.xLabelMap = labels

    return {
      json: newData,
      colors: colors,
      labels: labels,
      type: type,
    }
  },

  prepareData(chartContext) {
    let data = {
      json: Object.assign({}, chartContext.data),
      type:
        chartContext.options.type === 'stackedbar' ||
        chartContext.options.type === 'histogram'
          ? 'bar'
          : chartContext.options.type,
    }
    if (!chartContext.scatterConfig) {
      data.x = 'x'
    }

    if (
      chartContext.options.axis.x.time &&
      chartContext.options.axis.x.time.format
    ) {
      data.xFormat = chartContext.options.axis.x.time.format.d3Format
    }
    if (chartContext.options.general.labels) {
      data.labels = true
    }

    if (chartContext.xValueMode) {
      data = this.transformPieData(data.type, chartContext)
      return data
    }
    let xMap = {}
    if (chartContext.scatterConfig && chartContext.xMap) {
      xMap = chartContext.xMap
    }

    if (
      chartContext.options.dataPoints &&
      chartContext.options.dataPoints.length
    ) {
      let names = {}
      let colors = {}
      let types = {}
      let groups = []
      let axes = {}
      let hidden = []
      let stackedGroup = []
      let xs = {}
      if (!chartContext.options.isGroupedByTime) {
        for (let dataPoint of chartContext.options.dataPoints) {
          if (dataPoint.type === 'datapoint') {
            if (dataPoint.label) {
              names[dataPoint.key] = dataPoint.label
            }
            if (dataPoint.chartType) {
              types[dataPoint.key] = dataPoint.chartType
            }
            if (dataPoint.color) {
              colors[dataPoint.key] = dataPoint.color
            }
            if (dataPoint.axes) {
              axes[dataPoint.key] = dataPoint.axes
            }
            if (
              chartContext.options.hideDataPoints &&
              chartContext.options.hideDataPoints.length &&
              chartContext.options.hideDataPoints.find(
                rt => rt === dataPoint.key
              )
            ) {
              dataPoint.visible = false
            }
            if (dataPoint.visible === false) {
              hidden.push(dataPoint.key)
            }
            if (
              (!chartContext.options.combo &&
                chartContext.options.type === 'stackedbar') ||
              dataPoint.chartType === 'stackedbar'
            ) {
              types[dataPoint.key] = 'bar'
              stackedGroup.push(
                dataPoint.key ? dataPoint.key + '' : dataPoint.key
              )
            }
            if (
              chartContext.options.combo &&
              dataPoint.chartType === 'histogram'
            ) {
              types[dataPoint.key] = 'bar'
            }
            if (
              xMap[dataPoint.key] ||
              (dataPoint.key.split('_').length &&
                xMap[dataPoint.key.split('_')[0]])
            ) {
              if (dataPoint.pointType === 3 || dataPoint.pointType === 6) {
                let key = dataPoint.key.split('_')[0]
                xs[dataPoint.key] = xMap[key]
              } else {
                xs[dataPoint.key] = xMap[dataPoint.key]
              }
            }
            if (
              chartContext.Readingdp ||
              (chartContext.options.trendLine &&
                chartContext.options.trendLine.enable)
            ) {
              types[dataPoint.key] = chartContext.options.type
              if (dataPoint.pointType === 3 || dataPoint.pointType === 6) {
                //trendLine
                types[dataPoint.key] = 'line'
              }
            } else if (dataPoint.pointType === 3 || dataPoint.pointType === 6) {
              //trendLine
              types[dataPoint.key] = 'line'
            }
          } else if (dataPoint.type === 'systemgroup') {
            let group = []
            for (let child of dataPoint.children) {
              group.push(child.key ? child.key + '' : child.key)
              if (
                xMap[child.key] ||
                (child.key.toString().split('_').length &&
                  xMap[child.key.toString().split('_')[0]])
              ) {
                if (dataPoint.pointType === 3 || dataPoint.pointType === 6) {
                  let key = dataPoint.key.split('_')[0]
                  xs[child.key] = xMap[key]
                } else {
                  xs[child.key] = xMap[dataPoint.key]
                }
              }
              if (child.label) {
                names[child.key] = child.label
              }
              if (dataPoint.chartType) {
                types[child.key] = dataPoint.chartType
              }
              if (child.color) {
                colors[child.key] = child.color
              }
              if (child.axes) {
                axes[child.key] = child.axes
              }
              if (
                chartContext.options.hideDataPoints &&
                chartContext.options.hideDataPoints.length &&
                chartContext.options.hideDataPoints.find(
                  rt => rt === dataPoint.key
                )
              ) {
                dataPoint.visible = false
              }
              if (dataPoint.visible === false) {
                hidden.push(dataPoint.key)
              }
            }
            if (
              chartContext.options.type !== 'bar' &&
              chartContext.options.type !== 'histogram'
            ) {
              groups.push(group)
            }
          } else if (dataPoint.type === 'rangeGroup') {
            if (dataPoint.label) {
              names[dataPoint.key] = dataPoint.label
            }
            if (dataPoint.chartType) {
              types[dataPoint.key] = dataPoint.chartType
            }
            if (dataPoint.color) {
              colors[dataPoint.key] = dataPoint.color
            }
            if (dataPoint.axes) {
              axes[dataPoint.key] = dataPoint.axes
            }
            if (
              chartContext.options.hideDataPoints &&
              chartContext.options.hideDataPoints.length &&
              chartContext.options.hideDataPoints.find(
                rt => rt === dataPoint.key
              )
            ) {
              dataPoint.visible = false
            }
            if (dataPoint.visible === false) {
              hidden.push(dataPoint.key)
            }
            if (
              xMap[dataPoint.key] ||
              (dataPoint.key.split('_').length &&
                xMap[dataPoint.key.split('_')[0]])
            ) {
              if (dataPoint.pointType === 3 || dataPoint.pointType === 6) {
                let key = dataPoint.key.split('_')[0]
                xs[dataPoint.key] = xMap[key]
              } else {
                xs[dataPoint.key] = xMap[dataPoint.key]
              }
            }
          } else {
            if (dataPoint.children && dataPoint.children.length) {
              let group = []
              for (let child of dataPoint.children) {
                if (child.axes === 'x') {
                  continue
                }
                group.push(child.key ? child.key + '' : child.key)

                if (child.label) {
                  names[child.key] = child.label
                }
                if (dataPoint.chartType) {
                  if (child.chartType === 'scatter') {
                    if (
                      xMap[child.key] ||
                      (child.key.split('_').length &&
                        xMap[child.key.split('_')[0]])
                    ) {
                      if (
                        dataPoint.pointType === 3 ||
                        dataPoint.pointType === 6
                      ) {
                        let key = child.key.split('_')[0]
                        xs[child.key] = xMap[key]
                      } else {
                        xs[child.key] = xMap[child.key]
                      }
                    }
                    types[child.key] = child.chartType
                  } else {
                    types[child.key] = dataPoint.chartType
                  }
                }
                if (child.color) {
                  colors[child.key] = child.color
                }
                if (child.axes) {
                  axes[child.key] = child.axes
                }
                if (dataPoint.visible === false) {
                  hidden.push(dataPoint.key)
                }
              }
              groups.push(group)
            }
          }
        }
      } else {
        if (chartContext.options.timeGroupOptions) {
          let allPoints = []
          chartContext.options.dataPoints.forEach(dp => {
            if (dp.type === 'group') {
              allPoints.push(...dp.children)
            } else {
              allPoints.push(dp)
            }
          })
          for (let column of Object.keys(
            chartContext.options.timeGroupOptions
          )) {
            if (!chartContext.options.timeGroupOptions[column].visible) {
              hidden.push(column)
            }
            if (chartContext.options.type === 'stackedbar') {
              types[column] = 'bar'
              stackedGroup.push(column ? column + '' : column)
            }
            let point = allPoints.find(dp => dp.key === column.split('_')[0])
            if (point && point.axes) {
              axes[column] = point.axes
            }
            names[column] = chartContext.options.timeGroupOptions[column].name
            colors[column] = chartContext.options.timeGroupOptions[column].color
          }
        }
      }
      if (stackedGroup.length !== 0) {
        groups.push(stackedGroup)
      }

      data.names = names
      data.colors = colors
      data.types = types
      let allowedTypes = ['scatter', 'bubble']
      let scatterJSOn = {}
      let colorClusters = {}
      if (
        (Object.keys(types).find(key => allowedTypes.includes(types[key])) &&
          !(
            chartContext.options.regressionConfig &&
            chartContext.options.regressionConfig.length !== 0
          )) ||
        (chartContext.options.trendLine &&
          chartContext.options.trendLine.enable)
      ) {
        chartContext.options.point = {}
        chartContext.options.point['show'] = true
        chartContext.options.point['pattern'] = []
        for (let dataPoint of chartContext.options.dataPoints) {
          if (dataPoint.type === 'datapoint') {
            if (
              allowedTypes.includes(types[dataPoint.key]) ||
              dataPoint.pointType === 6
            ) {
              if (chartContext.scatterConfig && dataPoint.pointCustomization) {
                let pointPattern = this.getPointPattern(
                  dataPoint.pointCustomization.shape
                )
                if (dataPoint.axes !== 'x') {
                  chartContext.options.point.pattern.push(pointPattern)
                }
                scatterJSOn[dataPoint.key] = dataPoint.pointCustomization
                colorClusters[dataPoint.key] = this.getClusters(
                  data.json[dataPoint.key].slice()
                )
                if (
                  chartContext.options.scatter.size.mode === 'reading' &&
                  dataPoint.axes !== 'x'
                ) {
                  data.json[dataPoint.key] = this.getBubbleFormatedData(
                    data.json[dataPoint.key],
                    data.json[dataPoint.pointCustomization.size.basedOn]
                  )
                  data.types[dataPoint.key] = 'bubble'
                } else {
                  data.types[dataPoint.key] = 'scatter'
                }
              } else {
                chartContext.options.point.pattern.push('circle')
              }
            } else {
              chartContext.options.point.pattern.push('<polygon></polygon>')
            }
          } else if (dataPoint.type === 'systemgroup') {
            for (let child of dataPoint.children) {
              if (allowedTypes.includes(types[child.key])) {
                if (chartContext.scatterConfig && child.pointCustomization) {
                  let pointPattern = this.getPointPattern(
                    child.pointCustomization.shape
                  )
                  if (child.axes !== 'x') {
                    chartContext.options.point.pattern.push(pointPattern)
                  }
                  scatterJSOn[child.key] = child.pointCustomization
                  colorClusters[child.key] = this.getClusters(
                    data.json[child.key]
                  )
                  if (
                    chartContext.options.scatter.size.mode === 'reading' &&
                    child.axes !== 'x'
                  ) {
                    data.json[child.key] = this.getBubbleFormatedData(
                      data.json[child.key],
                      data.json[child.pointCustomization.size.basedOn]
                    )
                    data.types[child.key] = 'bubble'
                  } else {
                    data.types[child.key] = 'scatter'
                  }
                } else {
                  chartContext.options.point.pattern.push('circle')
                }
              } else {
                chartContext.options.point.pattern.push('<polygon></polygon>')
              }
            }
          } else if (dataPoint.type === 'rangeGroup') {
            if (allowedTypes.includes(types[dataPoint.key])) {
              if (chartContext.scatterConfig && dataPoint.pointCustomization) {
                let pointPattern = this.getPointPattern(
                  dataPoint.pointCustomization.shape
                )
                if (dataPoint.axes !== 'x') {
                  chartContext.options.point.pattern.push(pointPattern)
                }
                scatterJSOn[dataPoint.key] = dataPoint.pointCustomization
                colorClusters[dataPoint.key] = this.getClusters(
                  data.json[dataPoint.key]
                )
                if (
                  chartContext.options.scatter.size.mode === 'reading' &&
                  dataPoint.axes !== 'x'
                ) {
                  data.json[dataPoint.key] = this.getBubbleFormatedData(
                    data.json[dataPoint.key],
                    data.json[dataPoint.pointCustomization.size.basedOn]
                  )
                  data.types[dataPoint.key] = 'bubble'
                } else {
                  data.types[dataPoint.key] = 'scatter'
                }
              } else {
                chartContext.options.point.pattern.push('circle')
              }
            } else {
              chartContext.options.point.pattern.push('<polygon></polygon>')
            }
          } else {
            if (dataPoint.children && dataPoint.children.length) {
              for (let child of dataPoint.children) {
                if (allowedTypes.includes(types[child.key])) {
                  if (chartContext.scatterConfig && child.pointCustomization) {
                    let pointPattern = this.getPointPattern(
                      child.pointCustomization.shape
                    )
                    if (child.axes !== 'x') {
                      chartContext.options.point.pattern.push(pointPattern)
                    }
                    scatterJSOn[child.key] = child.pointCustomization
                    colorClusters[child.key] = this.getClusters(
                      data.json[child.key]
                    )
                    if (
                      chartContext.options.scatter.size.mode === 'reading' &&
                      child.axes !== 'x'
                    ) {
                      data.json[child.key] = this.getBubbleFormatedData(
                        data.json[child.key],
                        data.json[child.pointCustomization.size.basedOn]
                      )
                      data.types[child.key] = 'bubble'
                    } else {
                      data.types[child.key] = 'scatter'
                    }
                  } else {
                    chartContext.options.point.pattern.push('circle')
                  }
                } else {
                  chartContext.options.point.pattern.push('<polygon></polygon>')
                }
              }
            }
          }
        }
      }
      data.color = function(color, d) {
        if (scatterJSOn[d.id] && chartContext.options.scatter.color.mode) {
          let colorKey = scatterJSOn[d.id].color.basedOn
          if (colorClusters[colorKey]) {
            let colorScale =
              chartContext.options.scatter.color.mode === 'monochrome'
                ? chroma.scale(['white', color]).colors(6)
                : chartContext.options.scatter.color.colors
                ? chartContext.options.scatter.color.colors
                : ['#1d7f01', '#6cb302', '#e9f501', '#fda504', '#fb5905']

            let colorRange = d3
              .scaleLinear()
              .domain(colorClusters[colorKey])
              .range(
                chartContext.options.scatter.color.mode === 'monochrome'
                  ? colorScale.slice(1, 6)
                  : colorScale
              )

            if (typeof d === 'object') {
              let val = data.json[colorKey][d.index]
              // for data point
              if (val) {
                if (val.y) {
                  return colorRange(val.y)
                }
                return colorRange(val)

                // for data type (ex. line, bar)
              } else {
                return color
              }

              // for label
            } else {
              return color
            }
          }
        } else if (
          chartContext.options.scatter &&
          chartContext.options.scatter.color.mode === 'monochrome'
        ) {
          if (colorClusters[d.id]) {
            let colorScale = chroma.scale(['white', color]).colors(6)
            let colorRange = d3
              .scaleLinear()
              .domain(colorClusters[d.id])
              .range(colorScale.slice(1, 6))
            if (typeof d === 'object') {
              if (d.value) {
                if (d.value.y) {
                  return colorRange(d.value.y)
                }
                return colorRange(d.value)
              } else {
                return color
              }
            } else {
              return color
            }
          }
        } else if (typeof d === 'object' && d.value) {
          let matchpoint = chartContext.options.dataPoints.find(
            dp => dp.key === d.id
          )
          if (matchpoint && matchpoint.colorCriteria) {
            let min, max, mincolor, maxcolor, resultcolor
            for (let crit of matchpoint.colorCriteria) {
              if (typeof min === 'undefined' || min > crit.min) {
                min = crit.min
                mincolor = crit.color
              }
              if (typeof max === 'undefined' || max < crit.max) {
                max = crit.max
                maxcolor = crit.color
              }

              if (d.value >= crit.min && d.value < crit.max)
                resultcolor = crit.color
              else if (d.value < min) resultcolor = mincolor
              else if (d.value > max) resultcolor = maxcolor
            }
            return resultcolor
          }
        }
        return color
      }
      data.groups = groups
      data.axes = axes
      if (Object.keys(xs).length !== 0) {
        data['xs'] = xs
        data['xSort'] = false
      } else if (
        chartContext.options.regressionConfig &&
        chartContext.options.regressionConfig.length !== 0
      ) {
        data['xSort'] = false
      }
      if (hidden.length) {
        data.hide = hidden
      }
    }
    if (
      chartContext.options.general &&
      chartContext.options.general.normalizeStack
    ) {
      data.stack = {
        normalize: true,
      }
    }
    if (
      chartContext.options.general &&
      chartContext.options.general.dataOrder &&
      chartContext.options.general.dataOrder !== 'none'
    ) {
      data.order = chartContext.options.general.dataOrder
    }
    return data
  },
  getOnlyDataPoints(dataPoints) {
    let result = []
    for (let dataPoint of dataPoints) {
      if (dataPoint.type.trim().toLowerCase() === 'datapoint') {
        result.push(dataPoint)
      } else {
        for (let child of dataPoint.children) {
          result.push(child)
        }
      }
    }
    return result
  },
  getTickCount(chartContext, tickCount, decimals, axis) {
    let tickObject = {}
    if (!decimals) {
      let onlyDataPoints = this.getOnlyDataPoints(
        chartContext.options.dataPoints
      )

      let maxArr = []
      for (let dataKey in chartContext.data) {
        if (dataKey === 'x') {
          continue
        }

        let dp = onlyDataPoints.find(dp => dp.key === dataKey)
        if ((!dp || !dp.length) && axis === 'y') {
          maxArr.push(d3.max(chartContext.data[dataKey]))
        } else if (dp && dp.axes === axis) {
          maxArr.push(d3.max(chartContext.data[dataKey]))
        }
      }
      let yMax = d3.max(maxArr)
      if (yMax && tickCount && yMax < tickCount) {
        if (yMax < 1 && yMax > 0) {
          tickObject['decimal'] = 2
          tickObject['count'] = tickCount
        } else {
          tickCount = Math.round(yMax) + 1
          tickObject['count'] = tickCount
        }
      } else {
        tickObject['count'] = tickCount
      }
    }
    return tickObject
  },
  prepareAxis(chartContext, multiChartKey) {
    let axisOptions = chartContext.options.axis
    if (
      multiChartKey &&
      chartContext.options.multichart[multiChartKey] &&
      !chartContext.Readingdp
    ) {
      axisOptions = chartContext.options.multichart[multiChartKey].axis
    }

    let c3Axis = {}
    c3Axis.rotated = axisOptions.rotated
    c3Axis.x = {
      show: axisOptions.x.show,
      label: axisOptions.x.label,
      tick: {},
      height: 70,
    }
    if (
      axisOptions.x.datatype.indexOf('number') === -1 &&
      axisOptions.x.datatype.indexOf('decimal') === -1
    ) {
      if (
        (chartContext.options.regressionConfig &&
          chartContext.options.regressionConfig.length !== 0) ||
        chartContext.Readingdp
      ) {
        c3Axis.x.type = 'indexed'
      } else {
        c3Axis.x.type =
          axisOptions.x.datatype.indexOf('date') !== -1 &&
          this.periodCheck(chartContext.dateRange)
            ? 'timeseries'
            : 'category'
      }
      if (c3Axis.x.type === 'category' || c3Axis.x.type === 'indexed') {
        if (axisOptions.x.tick.hasOwnProperty('fit')) {
          c3Axis.x.tick.fit = axisOptions.x.tick.fit
        } else if (
          chartContext.options &&
          chartContext.options.type === 'scatter'
        ) {
          c3Axis.x.tick.fit = false
        } else {
          c3Axis.x.tick.fit = true
        }
      }
    } else {
      if (
        (chartContext.options.regressionConfig &&
          chartContext.options.regressionConfig.length !== 0) ||
        axisOptions.x.datatype.indexOf('number') >= 0 ||
        axisOptions.x.datatype.indexOf('decimal') >= 0
      ) {
        c3Axis.x.type = 'indexed'
      } else {
        c3Axis.x.type = 'category'
      }
      if (axisOptions.x.tick.hasOwnProperty('fit')) {
        c3Axis.x.tick.fit = axisOptions.x.tick.fit
      } else if (
        chartContext.options &&
        chartContext.options.type === 'scatter'
      ) {
        c3Axis.x.tick.fit = false
      } else {
        c3Axis.x.tick.fit = c3Axis.x.type === 'indexed' ? false : true
      }
    }
    if (c3Axis.x.type === 'timeseries') {
      let self = this
      let dateRangeObj = chartContext.dateRange
      let index = 0
      let perDate = null
      c3Axis.x.tick.format = function(date) {
        let axisLabel = chartContext.options.isMobile
          ? self.mobileDateFormat(
              date,
              dateRangeObj.period,
              dateRangeObj.rangeType,
              dateRangeObj
            )
          : self.newGetDateFormat(
              date,
              dateRangeObj.period,
              dateRangeObj.rangeType,
              index,
              perDate
            )
        index++
        perDate = date
        return axisLabel
      }
      if (dateRangeObj.range.domain && dateRangeObj.range.domain.length < 10) {
        c3Axis.x.tick.values = dateRangeObj.range.domain
      }
      if (chartContext.options.isMobileMulti) {
        axisOptions.x.culling = {
          max: 4,
        }
      }
      if (
        axisOptions.x.culling &&
        axisOptions.x.culling.max &&
        chartContext.options.isMobile
      ) {
        let newList = []
        if (dateRangeObj.range.domain && dateRangeObj.range.domain.length < 6) {
          newList = dateRangeObj.range.domain
        } else if (
          dateRangeObj.range.domain &&
          dateRangeObj.range.domain.length === 7
        ) {
          newList.push(dateRangeObj.range.domain[0])
          newList.push(dateRangeObj.range.domain[2])
          newList.push(dateRangeObj.range.domain[4])
          newList.push(
            dateRangeObj.range.domain[dateRangeObj.range.domain.length - 1]
          )
        } else if (
          dateRangeObj.range.domain &&
          dateRangeObj.range.domain.length > 8
        ) {
          let split = parseInt(
            dateRangeObj.range.domain.length / axisOptions.x.culling.max
          )
          for (let i = 0; i < axisOptions.x.culling.max; i++) {
            newList.push(dateRangeObj.range.domain[i * split])
          }
          if (
            newList[newList.length - 1] !==
            dateRangeObj.range.domain[dateRangeObj.range.domain.length - 1]
          ) {
            newList.push(
              dateRangeObj.range.domain[dateRangeObj.range.domain.length - 1]
            )
          }
        } else {
          newList = dateRangeObj.range.domain
        }
        c3Axis.x.tick.values = newList
      }
      c3Axis.x.tick.fit = false
      // c3Axis.x.tick.count = 10

      c3Axis.x.min = dateRangeObj.range.min
      c3Axis.x.max = dateRangeObj.range.max
    }
    // else{
    //   if(axisOptions.x.range){
    //     c3Axis.x.min = axisOptions.x.range.min
    //     c3Axis.x.max = axisOptions.x.range.max
    //   }
    // }

    if (
      axisOptions.x.datatype.indexOf('number') !== -1 ||
      axisOptions.x.datatype.indexOf('decimal') !== -1
    ) {
      if (axisOptions.x.range.min !== null && axisOptions.x.range.min !== '') {
        c3Axis.x.min = axisOptions.x.range.min
      }
      if (axisOptions.x.range.max !== null && axisOptions.x.range.max !== '') {
        c3Axis.x.max = axisOptions.x.range.max
      }
      if (axisOptions.x.tick.format) {
        c3Axis.x.tick.format = axisOptions.x.tick.format
      }
    }
    if (chartContext.data && chartContext.data.x) {
      if (chartContext.data.x.length < 4) {
        c3Axis.x.tick.multiline = true
      }
      if (chartContext.data.x.length > 0 && c3Axis.x.type === 'timeseries') {
        let xData = chartContext.data.x
        if (xData[0] < c3Axis.x.min) {
          c3Axis.x.min = xData[0]
        }
        if (xData[xData.length - 1] > c3Axis.x.max) {
          c3Axis.x.max = xData[xData.length - 1]
        }
        c3Axis.x.tick.culling = false
      }
    }
    if (axisOptions.x.tick.direction === 'vertical') {
      c3Axis.x.tick.rotate = 90
      c3Axis.x.tick.multiline = false
      c3Axis.x.tick.tooltip = true
    } else if (axisOptions.x.tick.direction === 'slanting') {
      c3Axis.x.tick.rotate = 45
      c3Axis.x.tick.multiline = false
      c3Axis.x.tick.tooltip = true
    }
    if (chartContext.scatterConfig) {
      c3Axis.x.tick.count = axisOptions.x.ticks.count
      c3Axis.x.tick.format = function(d) {
        if (axisOptions.x.format.decimals) {
          return d3.format(',.' + axisOptions.x.format.decimals + 'f')(d)
        }
        let val = d3.format(',')(Math.round(Math.ceil(d * 100) / 100))
        return val
      }
    }

    /* y axis formatting start */

    c3Axis.y = {}
    c3Axis.y.show = axisOptions.y.show
    c3Axis.y.label = {
      text: axisOptions.y.label.text,
      position: axisOptions.y.label.position,
    }
    if (axisOptions.y.unit && !['', ' '].includes(axisOptions.y.unit)) {
      c3Axis.y.label.text += ' (' + this.formatUnit(axisOptions.y.unit) + ')'
    }
    let tickCount = this.getTickCount(
      chartContext,
      axisOptions.y.ticks.count,
      axisOptions.y.format.decimals,
      'y'
    )
    if (tickCount.decimal) {
      axisOptions.y.format.decimals = tickCount.decimal
    }
    c3Axis.y.tick = {
      count: tickCount.count ? tickCount.count : axisOptions.y.ticks.count,
      format: function(d) {
        if (axisOptions.y.format.decimals) {
          return d3.format(',.' + axisOptions.y.format.decimals + 'f')(d)
        } else if (
          axisOptions?.y?.formatUnit &&
          axisOptions.y.formatUnit !== 'None'
        ) {
          if (axisOptions.y.formatUnit === 'M') {
            let val = Math.round(Math.round(Math.ceil(d * 100) / 100) / 1000000)
            return val !== 0 ? val + 'M' : val
          } else if (axisOptions.y.formatUnit === 'K') {
            let val = Math.round(Math.round(Math.ceil(d * 100) / 100) / 1000)
            return val !== 0 ? val + 'K' : val
          } else if (axisOptions.y.formatUnit === 'L') {
            let val = Math.round(Math.round(Math.ceil(d * 100) / 100) / 100000)
            return val !== 0 ? val + 'L' : val
          } else if (axisOptions.y.formatUnit === 'C') {
            let val = Math.round(
              Math.round(Math.ceil(d * 100) / 100) / 10000000
            )
            return val !== 0 ? val + 'C' : val
          }
        }
        let val = d3.format(',')(Math.round(Math.ceil(d * 100) / 100))
        if (axisOptions.y.format.unit) {
          val = val + axisOptions.y.format.unit
        } else if (
          chartContext.options.general &&
          chartContext.options.general.normalizeStack
        ) {
          val = val + '%'
        }
        return val
      },
      // stepSize: 1,
      // culling: {
      //   max: 10,
      // },
    }
    c3Axis.y.min = axisOptions.y.range.min
    if (axisOptions.y.range.max) {
      c3Axis.y.max = axisOptions.y.range.max
    }
    c3Axis.y.padding = axisOptions.y.padding
    /* y axis formatting end */

    if (axisOptions.y2) {
      c3Axis.y2 = {}
      c3Axis.y2.show = axisOptions.y2.show
      c3Axis.y2.label = {
        text: axisOptions.y2.label.text,
        position: axisOptions.y2.label.position,
      }
      if (axisOptions.y2.unit && !['', ' '].includes(axisOptions.y2.unit)) {
        c3Axis.y2.label.text +=
          ' (' + this.formatUnit(axisOptions.y2.unit) + ')'
      }
      let y2TickCount = this.getTickCount(
        chartContext,
        axisOptions.y2.ticks.count,
        axisOptions.y2.format.decimals,
        'y2'
      )
      if (y2TickCount.decimal) {
        axisOptions.y2.format.decimals = y2TickCount.decimal
      }
      c3Axis.y2.tick = {
        count: y2TickCount.count
          ? y2TickCount.count
          : axisOptions.y2.ticks.count,
        format: function(d) {
          if (axisOptions.y2.format.decimals) {
            return d3.format(',.' + axisOptions.y2.format.decimals + 'f')(d)
          } else if (
            axisOptions?.y2?.formatUnit &&
            axisOptions.y2.formatUnit !== 'None'
          ) {
            if (axisOptions.y2.formatUnit === 'M') {
              let val = Math.round(
                Math.round(Math.ceil(d * 100) / 100) / 1000000
              )
              return val !== 0 ? val + 'M' : val
            } else if (axisOptions.y2.formatUnit === 'K') {
              let val = Math.round(Math.round(Math.ceil(d * 100) / 100) / 1000)
              return val !== 0 ? val + 'K' : val
            } else if (axisOptions.y2.formatUnit === 'L') {
              let val = Math.round(
                Math.round(Math.ceil(d * 100) / 100) / 100000
              )
              return val !== 0 ? val + 'L' : val
            } else if (axisOptions.y2.formatUnit === 'C') {
              let val = Math.round(
                Math.round(Math.ceil(d * 100) / 100) / 10000000
              )
              return val !== 0 ? val + 'C' : val
            }
          }
          let val = d3.format(',')(Math.round(Math.ceil(d * 100) / 100))
          if (axisOptions.y2.format.unit) {
            val = val + axisOptions.y2.format.unit
          }
          return val
        },
      }
      c3Axis.y2.min = axisOptions.y2.range.min
      if (axisOptions.y2.range.max) {
        c3Axis.y2.max = axisOptions.y2.range.max
      }
      c3Axis.y2.padding = axisOptions.y2.padding
    }

    if (c3Axis.rotated) {
      c3Axis.x.label.position = 'outer-middle'
      c3Axis.y.label.position = 'outer-center'
      c3Axis.y2.label.position = 'outer-center'
    }
    if (chartContext.options.common.mode === 11) {
      c3Axis.y.label.text = 'Duration (Days)'
      c3Axis.y.tick.format = function(timeStamp) {
        return NumberFormatHelper.formatTime(timeStamp, true)
      }
    }
    return c3Axis
  },
  formatUnit(unit) {
    if (unit) {
      switch (unit) {
        case '&deg;C': {
          return '°C'
        }
        case '&deg;F': {
          return '°F'
        }
        case '&#x3BC;V': {
          return 'μV'
        }
        case '&#x3BC;A': {
          return 'μA'
        }
        case '&#x3BC;W': {
          return 'μW'
        }
        case '&#x3BC;Hz': {
          return 'μHz'
        }
        case '&deg;': {
          return '°'
        }
        default:
          return unit
      }
    }
  },
  mobileDateFormat(date, period, rangeType, dateRangeObj) {
    period = period || 'high'
    let mdate = moment(date)
    if (rangeType) {
      if (period === 'yearly') {
        return mdate.format('YYYY')
      } else if (rangeType === 'D') {
        if (parseInt(dateRangeObj.offset) > 1) {
          return mdate.format('MMM D')
        }
        if (period === 'hourly') {
          return mdate.format('hh a')
        } else {
          return mdate.format('hh a')
        }
      } else if (rangeType === 'W') {
        if (period === 'high') {
          return mdate.format('ddd')
        } else if (period === 'hourly') {
          return mdate.format('ddd')
        } else if (period === 'daily') {
          return mdate.format('ddd')
        } else if (period === 'weekly') {
          return mdate.format('ddd')
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        }
      } else if (rangeType === 'M') {
        if (period === 'high') {
          return mdate.format('MMM D')
        } else if (period === 'hourly') {
          return mdate.format('MMM D')
        } else if (period === 'daily') {
          return mdate.format('MMM D')
        } else if (period === 'weekly') {
          return 'W' + mdate.format('WW YYYY')
        } else if (period === 'monthly') {
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        }
      } else if (rangeType === 'Y') {
        if (period === 'high') {
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'hourly') {
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'daily') {
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'weekly') {
          return 'W' + mdate.format('WW YYYY')
        } else if (period === 'monthly') {
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        }
      } else if (rangeType === 'R') {
        if (period === 'high') {
          return mdate.format('MMM D')
        } else if (period === 'hourly') {
          return mdate.format('MMM D')
        } else if (period === 'daily') {
          return mdate.format('MMM D')
        } else if (period === 'weekly') {
          return mdate.format('MMM D')
        } else if (period === 'monthly') {
          return mdate.format('MMM')
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        }
      } else if (rangeType === 'Q') {
        if (period === 'high') {
          return mdate.format('MMM D')
        } else if (period === 'hourly') {
          return mdate.format('MMM D')
        } else if (period === 'daily') {
          return mdate.format('MMM D')
        } else if (period === 'weekly') {
          return mdate.format('MMM D')
        } else if (period === 'monthly') {
          return mdate.format('MMM')
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        }
      }
    }
    if (date.getMilliseconds()) {
      return mdate.format('.SSS')
    }
    if (date.getSeconds()) {
      return mdate.format(':ss')
    }
    if (date.getMinutes()) {
      return mdate.format('hh:mm')
    }
    if (date.getHours()) {
      return mdate.format('hh a')
    }
    if (date.getDay() && date.getDate() !== 1) {
      return mdate.format('MMM D')
    }
    if (date.getDate() !== 1) {
      return mdate.format('MMM D')
    }
    if (date.getMonth()) {
      return mdate.format('MMM')
    }
    if (date.getYear()) {
      return mdate.format('MMM YYYY')
    }
    return mdate.format('MMM D, YYYY')
  },
  getAggregate(childAggregate, isParent) {
    let formatHierarchy = newDateModel.formatHierarchy
    let index = null
    formatHierarchy.forEach(element => {
      if (element.label === childAggregate.trim().toLowerCase()) {
        index = formatHierarchy.indexOf(element)
      }
    })
    if (isParent) {
      return formatHierarchy[index + 1]
    } else {
      return formatHierarchy[index]
    }
  },

  getNewDateFormat(date, dateRangeObject, formatString, tickCount) {
    let parentAggregate = null
    let aggregateObject = null
    let formatHierarchy = newDateModel.formatHierarchy
    for (let index in formatHierarchy) {
      if (
        moment(dateRangeObject.range.min, formatString).isSame(
          moment(dateRangeObject.range.max, formatString),
          formatHierarchy[index].momentLabel
        )
      ) {
        let aggregate = this.getAggregate(dateRangeObject.period, false)
        return moment(date, formatString).format(aggregate.formatString)
      } else {
        let diff = moment(dateRangeObject.range.max, formatString).diff(
          moment(dateRangeObject.range.min, formatString),
          formatHierarchy[index].momentLabel,
          true
        )
        if (diff < formatHierarchy[index].maxLimit) {
          if (diff < tickCount) {
            if (index === 0) {
              aggregateObject = formatHierarchy[index]
              parentAggregate =
                formatHierarchy[formatHierarchy.indexOf(aggregateObject) + 1]
            } else {
              parentAggregate = formatHierarchy[index]
              aggregateObject =
                formatHierarchy[formatHierarchy.indexOf(parentAggregate) - 1]
            }
          } else {
            aggregateObject = formatHierarchy[index]
            parentAggregate =
              formatHierarchy[formatHierarchy.indexOf(aggregateObject) + 1]
          }
          break
        } else {
          if (formatHierarchy[index].label === 'yearly') {
            parentAggregate = formatHierarchy[index]
            aggregateObject =
              formatHierarchy[formatHierarchy.indexOf(parentAggregate) - 1]
          }
        }
      }
    }
    let diff = moment(dateRangeObject.range.max, formatString).diff(
      moment(dateRangeObject.range.min, formatString),
      parentAggregate.momentLabel,
      true
    )
    if (diff <= parentAggregate.maxLimit && diff < tickCount) {
      return moment(date, formatString).format(aggregateObject.formatString)
    } else if (diff <= parentAggregate.maxLimit && diff > tickCount) {
      return moment(date, formatString).format(parentAggregate.formatString)
    } else {
      let totalLabel =
        aggregateObject.formatString + ' ' + parentAggregate.formatString
      return moment(date, formatString).format(totalLabel)
    }
  },
  newGetDateFormat(date, period, rangeType, index, perDate) {
    let mdate = moment(date)
    if (rangeType && period) {
      if (period === 'yearly') {
        return mdate.format('YYYY')
      } else if (rangeType === 'D') {
        if (period === 'hourly') {
          return mdate.format('hh a')
        }
      } else if (rangeType === 'W') {
        if (period === 'hourly') {
          if (date.getHours()) {
            return mdate.format('hh a')
          }
          if (date.getDate()) {
            return mdate.format('ddd')
          }
        } else if (period === 'daily') {
          return mdate.format('ddd')
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        }
      } else if (rangeType === 'M') {
        if (period === 'hourly') {
          if (date.getHours()) {
            return mdate.format('hh a')
          }
          if (date.getDate()) {
            if (index !== undefined && index === 0) {
              return mdate.format('D') + '\n' + mdate.format('MMM')
            }
            if (date.getDate() === 1) {
              return mdate.format('D') + '\n' + mdate.format('MMM')
            } else {
              return mdate.format('D')
            }
          }
        } else if (period === 'daily') {
          if (index !== undefined && index === 0) {
            return mdate.format('D') + '\n' + mdate.format('MMM')
          }
          if (date.getDate() === 1) {
            return mdate.format('D') + '\n' + mdate.format('MMM')
          } else {
            return mdate.format('D')
          }
        } else if (period === 'weekly') {
          return 'W' + mdate.format('ww YYYY')
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        } else if (period === 'dayofmonth') {
          return mdate.format('DD')
        } else if (period === 'weekofyear') {
          return 'W' + mdate.format('WW')
        } else if (period === 'monthofyear') {
          return mdate.format('MMM')
        }
      } else if (rangeType === 'Y') {
        if (period === 'hourly') {
          if (date.getHours()) {
            return mdate.format('hh a')
          }
          if (date.getDay() && date.getDate() !== 1) {
            if (index !== undefined && index === 0) {
              return mdate.format('D') + '\n' + mdate.format('MMM')
            } else {
              return mdate.format('D')
            }
          }
          if (date.getDate() !== 1) {
            if (index !== undefined && index === 0) {
              return mdate.format('D') + '\n' + mdate.format('MMM')
            } else {
              return mdate.format('D')
            }
          }
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'daily') {
          if (date.getDay() && date.getDate() !== 1) {
            if (index !== undefined && index === 0) {
              return mdate.format('D') + '\n' + mdate.format('MMM')
            } else {
              return mdate.format('D')
            }
          }
          if (date.getDate() !== 1) {
            return mdate.format('D') + '\n' + mdate.format('MMM')
          }
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'weekly') {
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'quarterly') {
          return mdate.format('[Q]Q YYYY')
        } else if (period === 'monthly') {
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        } else if (period === 'dayofmonth') {
          return mdate.format('DD')
        } else if (period === 'weekofyear') {
          return 'W' + mdate.format('WW')
        } else if (period === 'monthofyear') {
          return mdate.format('MMM')
        }
      } else if (rangeType === 'Q') {
        if (period === 'hourly') {
          if (index !== undefined && index === 0) {
            return mdate.format('D') + '\n' + mdate.format('MMM')
          }
          if (date.getDate() === 1) {
            return mdate.format('D') + '\n' + mdate.format('MMM')
          } else {
            return mdate.format('D')
          }
        } else if (period === 'daily') {
          if (index !== undefined && index === 0) {
            return mdate.format('D') + '\n' + mdate.format('MMM')
          }
          if (date.getDate() === 1) {
            return mdate.format('D') + '\n' + mdate.format('MMM')
          } else {
            return mdate.format('D')
          }
        } else if (period === 'weekly') {
          return 'W' + mdate.format('ww YYYY')
        } else if (period === 'quarterly') {
          return mdate.format('[Q]Q YYYY')
        } else if (period === 'monthly') {
          return mdate.format('MMM YYYY')
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        } else if (period === 'dayofmonth') {
          return mdate.format('DD')
        } else if (period === 'weekofyear') {
          return 'W' + mdate.format('WW')
        } else if (period === 'monthofyear') {
          return mdate.format('MMM YYYY')
        }
      }
    }

    if (date.getMilliseconds()) {
      return mdate.format('.SSS')
    }
    if (date.getSeconds()) {
      return mdate.format(':ss')
    }
    if (date.getMinutes()) {
      return mdate.format('hh:mm')
    }
    if (date.getHours()) {
      return mdate.format('hh a')
    }
    if (
      date.getDay() &&
      date.getDate() !== 1 &&
      typeof index !== undefined &&
      index === 0
    ) {
      return mdate.format('D') + '\n' + mdate.format('MMM')
    }
    if (date.getDay() && date.getDate() === 1) {
      if (period === 'monthly') {
        return mdate.format('MMM')
      }
      return mdate.format('D') + '\n' + mdate.format('MMM')
    }
    if (date.getDay() && date.getDate() !== 1 && perDate && perDate !== date) {
      return mdate.format('D')
    }
    if (
      date.getDay() === 0 &&
      date.getDate() !== 1 &&
      typeof index !== undefined &&
      index > 0 &&
      rangeType === 'D'
    ) {
      return mdate.format('D')
    }
    if (date.getDate() !== 1) {
      return mdate.format('D') + '\n' + mdate.format('MMM')
    }
    if (date.getMonth() && rangeType && rangeType === 'D') {
      return mdate.format('D') + '\n' + mdate.format('MMM')
    }
    if (date.getMonth()) {
      return mdate.format('MMM')
    }
    if (date.getYear()) {
      return mdate.format('MMM YYYY')
    }
    return mdate.format('MMM D, YYYY')
  },
  getDateFormat(date, period, rangeType) {
    let mdate = moment(date)
    if (rangeType && period) {
      if (period === 'yearly') {
        return mdate.format('YYYY')
      } else if (rangeType === 'D') {
        if (period === 'hourly') {
          return mdate.format('hh a')
        }
      } else if (rangeType === 'W') {
        if (period === 'hourly') {
          if (date.getHours()) {
            return mdate.format('hh a')
          }
          if (date.getDate()) {
            return mdate.format('ddd')
          }
        } else if (period === 'daily') {
          return mdate.format('ddd')
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        }
      } else if (rangeType === 'M') {
        if (period === 'hourly') {
          if (date.getHours()) {
            return mdate.format('hh a')
          }
          if (date.getDate()) {
            return mdate.format('MMM D')
          }
        } else if (period === 'daily') {
          return mdate.format('MMM D')
        } else if (period === 'weekly') {
          return 'W' + mdate.format('ww YYYY')
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        } else if (period === 'dayofmonth') {
          return mdate.format('DD')
        } else if (period === 'weekofyear') {
          return 'W' + mdate.format('WW')
        } else if (period === 'monthofyear') {
          return mdate.format('MMM')
        }
      } else if (rangeType === 'Y') {
        if (period === 'hourly') {
          if (date.getHours()) {
            return mdate.format('hh a')
          }
          if (date.getDay() && date.getDate() !== 1) {
            return mdate.format('MMM D')
          }
          if (date.getDate() !== 1) {
            return mdate.format('MMM D')
          }
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'daily') {
          if (date.getDay() && date.getDate() !== 1) {
            return mdate.format('MMM D')
          }
          if (date.getDate() !== 1) {
            return mdate.format('MMM D')
          }
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'weekly') {
          // if (date.getDate() !== 1) {
          //   return 'W' + mdate.format('WW YYYY')
          // }
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'quarterly') {
          return mdate.format('[Q]Q YYYY')
        } else if (period === 'monthly') {
          if (date.getMonth()) {
            return mdate.format('MMM')
          }
          if (date.getYear()) {
            return mdate.format('MMM YYYY')
          }
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        } else if (period === 'dayofmonth') {
          return mdate.format('DD')
        } else if (period === 'weekofyear') {
          return 'W' + mdate.format('WW')
        } else if (period === 'monthofyear') {
          return mdate.format('MMM')
        }
      } else if (rangeType === 'Q') {
        if (period === 'hourly') {
          return mdate.format('MMM D')
        } else if (period === 'daily') {
          return mdate.format('MMM D')
        } else if (period === 'weekly') {
          return 'W' + mdate.format('ww YYYY')
        } else if (period === 'monthly') {
          return mdate.format('MMM YYYY')
        } else if (period === 'dayofweek') {
          return mdate.format('ddd')
        } else if (period === 'dayofmonth') {
          return mdate.format('DD')
        } else if (period === 'weekofyear') {
          return 'W' + mdate.format('WW')
        } else if (period === 'monthofyear') {
          return mdate.format('MMM YYYY')
        }
      }
    }

    if (date.getMilliseconds()) {
      return mdate.format('.SSS')
    }
    if (date.getSeconds()) {
      return mdate.format(':ss')
    }
    if (date.getMinutes()) {
      return mdate.format('hh:mm')
    }
    if (date.getHours()) {
      return mdate.format('hh a')
    }
    if (date.getDay() && date.getDate() !== 1) {
      return mdate.format('MMM D')
    }
    if (date.getDate() !== 1) {
      return mdate.format('MMM D')
    }
    if (date.getMonth()) {
      return mdate.format('MMM')
    }
    if (date.getYear()) {
      return mdate.format('MMM YYYY')
    }
    return mdate.format('MMM D, YYYY')
  },
  getMatchingAlarmRegion(chartContext, dateStr) {
    let date = new Date(dateStr)
    if (chartContext.alarms) {
      for (let region of chartContext.alarms.regions) {
        let startDate = new Date(region.start)
        let endDate = new Date(region.end)

        if (date >= startDate && date < endDate) {
          return region
        }
      }
    }
    return null
  },

  getBooleanValue(chartContext, date, dataPointKey) {
    // boolean tooltip fix
    if (chartContext.booleanData && chartContext.booleanData[dataPointKey]) {
      let booleanData = chartContext.booleanData[dataPointKey]
      for (let region of booleanData) {
        let startDate = new Date(region.start)
        let endDate = new Date(region.end)
        if (date >= startDate && date < endDate) {
          return region.value
        }
      }
    }
    return '---'
  },

  getLabelWithCaps(name) {
    let firstChar = name.charAt(0)
    firstChar = firstChar.toUpperCase()
    name = name.toLowerCase()
    name = name.slice(1)
    name = firstChar + name
    return name
  },
  prepareTooltip(chartContext) {
    let self = this
    let tooltipOptions = {
      show: true,
      onhide: function() {
        tooltip.hideTooltip()
        if (chartContext.tooltipHideCallback) {
          chartContext.tooltipHideCallback()
          return null
        }
      },
      contents: function(d, defaultTitleFormat, defaultValueFormat, color) {
        if (
          chartContext.options.regressionConfig &&
          chartContext.options.regressionConfig.length !== 0
        ) {
          if (d && d.length) {
            let regressionPoint = d.find(e => e.id.endsWith('regr'))
            if (regressionPoint) {
              return null
            }
          }
        }
        let tooltipData = []
        let alarms = []
        let tooltipTitle = null
        let isSingleMode = false
        let baselinePoints = chartContext.options.dataPoints.filter(
          point => point.isBaseLine
        )
        let diffMap = {}
        if (baselinePoints && baselinePoints.length) {
          for (let bd of baselinePoints) {
            let baseval = d.find(d1 => d1.id === bd.key)
            let val = d.find(d1 => d1.id === bd.dpAlias)
            if (
              baseval &&
              baseval.value !== null &&
              val &&
              val.value !== null
            ) {
              let diff = ((val.value - baseval.value) / baseval.value) * 100
              diffMap[bd.dpAlias] = diff
            }
          }
        }
        let groupTotal = d.find(d1 => d1.id === 'group_total')
        let groupPercentMap = {}
        let groupTotalUnit = null
        if (groupTotal && groupTotal.value) {
          for (let e of d) {
            if (e.id !== 'group_total') {
              groupTotalUnit = chartContext.unitMap[e.id]
              let diff = (e.value / groupTotal.value) * 100
              groupPercentMap[e.id] = diff
            }
          }
        }
        for (let e of d) {
          if (chartContext.options.isDepreciationCostTrend) {
            if (e.id === 'Current Price') {
              continue
            }
          }
          if (e !== null && !e.id.endsWith('TrendLine')) {
            let unit = chartContext.xValueMode
              ? chartContext.options.dataPoints[0].unitStr
              : chartContext.unitMap[e.id]
            let enumMap = chartContext.enumMap[e.id]
            if (
              !chartContext.options.tooltip.showNullValues &&
              e.value === null
            ) {
              continue
            }
            let val = e.value === null ? 0 : e.value
            // fetch data Point from chartContext
            let dataPoints = chartContext.options.dataPoints

            let dataPoint = dataPoints.filter(point => point.key === e.id)

            if (dataPoint.length !== 0 && dataPoint[0].customFormat) {
              if (dataPoint.pointCustomization) {
                val = val && val.y ? val.y : val
              }
              val = NumberFormatHelper.getFormattedValue(
                val,
                null,
                dataPoint[0].customFormat
              )
            } else if (chartContext.options.common.mode !== 11) {
              if (val && val.y) {
                val = val && val.y ? val.y : val
              }
              if (Number.isInteger(val)) {
                val = d3.format(',')(val)
              } else if (Array.isArray(val)) {
                for (let value of val) {
                  if (Number.isInteger(value)) {
                    value = d3.format(',')(value)
                  } else {
                    value = d3.format(',.2f')(value)
                  }
                }
              } else {
                val = d3.format(',.2f')(val)
              }
            }
            let valueStr = null
            let valueUnit = null
            if (
              dataPoint.length &&
              dataPoint[0].type === 'rangeGroup' &&
              Array.isArray(val)
            ) {
              let ranges = ['max', 'avg', 'min']
              let rangeValues = []
              for (let i = ranges.length - 1; i >= 0; i--) {
                let v = val[i]
                v = ranges[i] + ': ' + v
                if (unit) {
                  v = v + ' ' + unit
                }
                rangeValues.push(v)

                valueStr = rangeValues.join()
              }
            } else {
              if (unit === '$') {
                valueStr = unit + val
              } else if (
                [
                  'days',
                  'hours',
                  'minutes',
                  'seconds',
                  'milliseconds',
                ].includes(unit)
              ) {
                valueStr = formatDuration(
                  val,
                  unit,
                  dataPoint.length ? dataPoint[0].convertTounit : null,
                  undefined,
                  unit,
                  chartContext.options.defaultDurationUnit
                )
              } else {
                valueStr = val
                valueUnit = unit
              }
            }

            if (enumMap && enumMap[e.value]) {
              valueStr = enumMap[e.value]
            }

            let label = e.name
            let diff = diffMap ? diffMap[e.id] : null

            if (label === 'group_total') {
              label = chartContext.options.bar.groupTotalLabel || 'Group Total'
              valueUnit = groupTotalUnit
            }
            let percent = null
            if (groupPercentMap && groupPercentMap[e.id]) {
              percent = groupPercentMap[e.id]
            }

            if (chartContext.xValueMode) {
              let percentage = null
              try {
                if (
                  chartContext.xActualDataType &&
                  chartContext.xActualDataType.indexOf('date') !== -1 &&
                  chartContext.options.axis.x.time &&
                  chartContext.options.axis.x.time.format
                ) {
                  let dateFormat =
                    chartContext.options.axis.x.time &&
                    chartContext.options.axis.x.time.format.tooltip
                  label = moment(
                    e.name,
                    chartContext.options.axis.x.time.format.format
                  ).format(dateFormat)
                } else {
                  label =
                    chartContext.xLabelMap && chartContext.xLabelMap[e.name]
                      ? chartContext.xLabelMap[e.name]
                      : e.name
                }

                let calculatePercent = (value, data) => {
                  let total = d3.sum(data)
                  const percent = total ? (value / total) * 100 : 0
                  return d3.format('.1f')(percent)
                }
                percentage = calculatePercent(
                  e.value,
                  chartContext.data[
                    Object.keys(chartContext.data).filter(k => k !== 'x')[0]
                  ]
                )
              } catch (error) {
                label =
                  chartContext.xLabelMap && chartContext.xLabelMap[e.name]
                    ? chartContext.xLabelMap[e.name]
                    : e.name
              }
              tooltipTitle = label

              tooltipData.push({
                label:
                  chartContext.options.dataPoints &&
                  chartContext.options.dataPoints.length
                    ? chartContext.options.dataPoints[0].label
                    : chartContext.options.axis.y.label.text,
                value: valueStr,
                unit: valueUnit,
                color: color(e),
                key: e.id,
              })

              if (percentage) {
                isSingleMode = true
                tooltipData.push({
                  label: 'Percentage',
                  value: percentage,
                  unit: '%',
                  color: color(e),
                  key: e.id,
                })
              }
            } else if (chartContext.options.common.mode === 11) {
              tooltipData.push({
                label: 'Duration',
                value: NumberFormatHelper.formatTime(valueStr),
                color: color(e),
                key: e.id,
              })
            } else if (!chartContext.scatterConfig) {
              if (chartContext.specialScatter) {
                label = chartContext.options.axis.y.label.text
              }
              tooltipData.push({
                label: label,
                value: valueStr,
                unit: valueUnit,
                color: color(e),
                diff: diff,
                percent: percent,
                key: e.id,
              })
            }
          }
        }

        if (chartContext.scatterConfig) {
          let relatedpoints = chartContext.options.dataPoints.filter(
            dp => dp.key.split('_').length && dp.key.split('_')[0] === d[0].id
          )
          for (let dataPoint of relatedpoints) {
            let dJson = chartContext.data[dataPoint.key]
            let unit = chartContext.unitMap[dataPoint.dpKey || dataPoint.key]
            let enumMap = chartContext.enumMap[dataPoint.dpKey || dataPoint.key]

            let actualVal = dJson[d[0].index] === null ? 0 : dJson[d[0].index]

            let tVal = null
            tVal = [
              'days',
              'hours',
              'minutes',
              'seconds',
              'milliseconds',
            ].includes(unit)
              ? formatDuration(actualVal, unit, dataPoint.convertTounit)
              : (unit === '$' ? unit + ' ' : '') +
                actualVal +
                (unit && unit !== '$' ? ' ' + unit : '')
            let val = actualVal

            if (enumMap && enumMap[val]) {
              tVal = enumMap[val]
            }

            if (dJson) {
              tooltipData.push({
                label: dataPoint.name,
                value: tVal,
                key: dataPoint.id ? dataPoint.id : dataPoint.key,
              })
            }
          }
        }

        let dateFormat = 'LLL'
        if (
          chartContext.options.axis.x.time &&
          chartContext.options.axis.x.time.format
        ) {
          dateFormat =
            chartContext.options.axis.x.time &&
            chartContext.options.axis.x.time.format.tooltip
        }

        if (
          ((chartContext.options.regressionConfig &&
            chartContext.options.regressionConfig.length != 0) ||
            chartContext.scatterConfig) &&
          chartContext.options.timeValues
        ) {
          dateFormat =
            chartContext.options.axis.x.time &&
            chartContext.options.axis.x.time.format.tooltip
          let time = null
          if (dateFormat) {
            time = moment(chartContext.options.timeValues[d[0].index]).format(
              chartContext.options.axis.x.time.format.tooltip
            )
            tooltipData.push({
              label: 'Time',
              value: time,
            })
          }
        }

        if (typeof d[0].x === 'object') {
          tooltipTitle = moment(d[0].x).format(dateFormat)

          let matchingAlarmRegion = self.getMatchingAlarmRegion(
            chartContext,
            d[0].x
          )
          if (matchingAlarmRegion && matchingAlarmRegion.alarms) {
            for (let alarm of matchingAlarmRegion.alarms) {
              let severity = alarm.severity
                ? Vue.prototype.$helpers.getAlarmSeverity(alarm.severity.id)
                : null
              let isCleared = false
              if (severity && severity.severity.toLowerCase() === 'clear') {
                isCleared = true
                severity = alarm.previousSeverity
                  ? Vue.prototype.$helpers.getAlarmSeverity(
                      alarm.previousSeverity.id
                    )
                  : null
              }

              let message
              let subject
              if (Vue.prototype.$helpers.isLicenseEnabled('NEW_ALARMS')) {
                message = alarm.alarm.description || ''
                subject =
                  alarm.alarm && alarm.alarm.subject ? alarm.alarm.subject : ''
              } else {
                message = alarm.problem || alarm.readingMessage
                subject = alarm.subject || ''
              }

              alarms.push({
                name: subject,
                message: message,
                severity: severity,
                isCleared: isCleared,
                count: matchingAlarmRegion.count,
              })
            }
          }
        } else if (typeof d[0].x === 'number' && !chartContext.scatterConfig) {
          tooltipTitle = chartContext.data.x[d[0].x]
        }
        if (!tooltipTitle) {
          tooltipTitle = d[0].x
        }

        if (chartContext.Readingdp) {
          if (d[0].x) {
            tooltipTitle = d[0].x
          }
          let unit =
            chartContext.Readingdp.yAxis && chartContext.Readingdp.yAxis.unitStr
              ? chartContext.Readingdp.yAxis.unitStr
              : ''
          tooltipTitle = tooltipTitle + ' ' + unit
        }

        if (
          !chartContext.options.isSystemGroup &&
          chartContext.options.type != 'pie' &&
          chartContext.options.type != 'donut' &&
          chartContext.options.type != 'bubble' &&
          chartContext.options.type != 'scatter' &&
          chartContext.options.type != 'gauge'
        ) {
          let newToolTipData = []

          chartContext.options.dataPoints.forEach(dt => {
            if (dt.type === 'datapoint') {
              let x = tooltipData.findIndex(z => z.key === dt.key)
              if (x !== -1) {
                newToolTipData.push(tooltipData[x])
              }
            } else if (dt.type === 'group') {
              let newData = { label: dt.label, children: [], is_group: true }
              dt.children.forEach(ch => {
                let x = tooltipData.findIndex(z => z.key === ch.key)
                if (x !== -1) {
                  newData.children.push(tooltipData[x])
                }
              })
              if (newData.children.length > 0) {
                newToolTipData.push(newData)
              }
            }
          })
          tooltipData = newToolTipData
        }

        // if (d[0].id === d[0].id.toLowerCase()) {
        //   tooltipData = [
        //     {
        //       label: 'X value',
        //       value: d[0].x,
        //     },
        //     {
        //       label: 'Y value',
        //       value: d[0].value,
        //     },
        //   ]
        //   tooltipTitle = d[0].name
        // }

        if (d.length === 1 || isSingleMode) {
          let tlData = []

          if (chartContext.options.axis.x.label.text && tooltipTitle) {
            tlData.push({
              label: chartContext.options.axis.x.label.text,
              value: tooltipTitle,
            })
          }

          tlData.push(...tooltipData)

          let tooltipConfig = {
            position: {
              left: d3.event.pageX,
              top: d3.event.pageY,
            },
            data: tlData,
            color: tlData[tlData.length - 1].color,
            alarms: alarms.showTimeOnly ? null : alarms,
            count: alarms.length ? alarms[0].count : 0,
            dataPoint: chartContext.options.dataPoints,
          }
          if (chartContext.options?.tooltip.compare_indicator) {
            tooltipConfig.compare_indicator =
              chartContext.options.tooltip.compare_indicator
          }
          if (!chartContext.options.axis.x.label.text) {
            tooltipConfig.title = tooltipTitle
          }
          if (chartContext.specialScatter || chartContext.scatterConfig) {
            tooltipConfig.title = d[0].name
          }
          if (chartContext.tooltipCallback) {
            chartContext.tooltipCallback(d, tooltipConfig, tooltipTitle)
            return null
          }
          tooltip.showTooltipForNewChart(tooltipConfig)
          return null
        } else {
          let tooltipConfig = {
            position: {
              left: d3.event.pageX,
              top: d3.event.pageY,
            },
            title: tooltipTitle,
            mode: 1,
            alarms: alarms.showTimeOnly ? null : alarms,
            count: alarms.length ? alarms[0].count : 0,
            data: tooltipData,
            dataPoint: chartContext.options.dataPoints,
          }
          if (chartContext.options?.tooltip?.compare_indicator) {
            tooltipConfig.compare_indicator =
              chartContext.options.tooltip.compare_indicator
          }
          if (chartContext.tooltipCallback) {
            chartContext.tooltipCallback(d, tooltipConfig, tooltipTitle)
            return null
          }
          tooltip.showTooltipForNewChart(tooltipConfig)
          return null
        }
      },
    }
    if (
      chartContext.options.tooltip.sortOrder === 'asc' ||
      chartContext.options.tooltip.sortOrder === 'desc'
    ) {
      tooltipOptions.order = chartContext.options.tooltip.sortOrder
    }
    if (chartContext.scatterConfig) {
      tooltipOptions['grouped'] = false
    }
    return tooltipOptions
  },

  prepareMultichartTooltip(chartContext) {
    let self = this
    let tooltipOptions = {
      show: true,
      linked: !chartContext.scatterConfig
        ? {
            name: chartContext.multichartUniqueKey,
          }
        : false,
      onshow: function() {
        if (!chartContext.hoveringChart) {
          chartContext.hoveringChart = null
        }
      },
      onhide: function() {
        chartContext.hoveringChart = null
        tooltip.hideTooltip()
      },
      contents: function(d) {
        if (!chartContext.hoveringChart) {
          chartContext.hoveringChart = d[0].id
        } else {
          if (!chartContext.options.isMobile) {
            return null
          }
        }
        let tooltipData = []

        for (let dataPoint of Vue.prototype.$helpers.getDataPoints(
          chartContext.options.dataPoints,
          [1, 2, 4, 6],
          true
        )) {
          if (!chartContext.scatterConfig || d[0].id === dataPoint.key) {
            if (!chartContext.options.isGroupedByTime) {
              if (dataPoint.type === 'group') {
                if (dataPoint.children) {
                  let children = []
                  for (let dp of dataPoint.children) {
                    if (dp.visible) {
                      let dJson =
                        chartContext.data[dp.axes === 'x' ? 'x' : dp.key]
                      let unit = chartContext.unitMap[dp.dpKey || dp.key]
                      let enumMap = chartContext.enumMap[dp.dpKey || dp.key]

                      let actualVal = chartContext.scatterConfig
                        ? d[0].value && d[0].value.y
                          ? d[0].value.y
                          : d[0].value
                        : dJson[d[0].index] === null
                        ? 0
                        : dJson[d[0].index]
                      if (dp.dataType === 'BOOLEAN' || dp.dataType === 'ENUM') {
                        actualVal = self.getBooleanValue(
                          chartContext,
                          d[0].x,
                          dp.key
                        )
                      }

                      let tVal = [
                        'days',
                        'hours',
                        'minutes',
                        'seconds',
                        'milliseconds',
                      ].includes(unit)
                        ? formatDuration(actualVal, unit, dp.convertTounit)
                        : (unit === '$' ? unit + ' ' : '') +
                          actualVal +
                          (unit && unit !== '$' ? ' ' + unit : '')
                      let val = actualVal

                      if (enumMap && enumMap[val]) {
                        tVal = enumMap[val]
                      }
                      let tRawVal =
                        enumMap && enumMap[val]
                          ? enumMap[val]
                          : dJson[d[0].index]
                      if (dJson) {
                        children.push({
                          key: dp.key,
                          rawValue: tRawVal,
                          rawunit: unit,
                          label: dp.label,
                          value: tVal,
                          color: dp.color,
                        })
                      }
                    }
                  }
                  if (children.length) {
                    tooltipData.push({
                      label: dataPoint.label,
                      is_group: true,
                      children: children,
                    })
                  }
                }
              } else if (dataPoint.visible) {
                let dJson = chartContext.data[dataPoint.key]
                let unit =
                  chartContext.unitMap[dataPoint.dpKey || dataPoint.key]
                let enumMap =
                  chartContext.enumMap[dataPoint.dpKey || dataPoint.key]

                let actualVal = chartContext.scatterConfig
                  ? d[0].value && d[0].value.y
                    ? d[0].value.y
                    : d[0].value
                  : dJson[d[0].index] === null
                  ? 0
                  : dJson[d[0].index]
                if (
                  dataPoint.dataType === 'BOOLEAN' ||
                  dataPoint.dataType === 'ENUM'
                ) {
                  actualVal = self.getBooleanValue(
                    chartContext,
                    d[0].x,
                    dataPoint.key
                  )
                }

                let tVal = null
                if (dataPoint.type === 'rangeGroup') {
                  let range = ['max', 'avg', 'min']
                  if (!tVal) {
                    tVal = []
                  }
                  for (let i = range.length - 1; i >= 0; i--) {
                    let finalVal =
                      range[i] +
                      ': ' +
                      dJson[d[0].index][i] +
                      (unit ? ' ' + unit : '')
                    tVal.push(finalVal)
                  }
                  tVal = tVal.join(',')
                } else {
                  tVal = [
                    'days',
                    'hours',
                    'minutes',
                    'seconds',
                    'milliseconds',
                  ].includes(unit)
                    ? formatDuration(actualVal, unit, dataPoint.convertTounit)
                    : (unit === '$' ? unit + ' ' : '') +
                      actualVal +
                      (unit && unit !== '$' ? ' ' + unit : '')
                  let val = actualVal

                  if (enumMap && enumMap[val]) {
                    tVal = enumMap[val]
                  }
                }

                if (dJson) {
                  tooltipData.push({
                    label: dataPoint.label,
                    value: tVal,
                    color: dataPoint.color,
                  })
                }
              }
            } else {
              let columns = []
              let children = []
              if (
                dataPoint.type === 'datapoint' ||
                dataPoint.type === 'rangeGroup'
              ) {
                columns = Object.keys(
                  chartContext.options.timeGroupOptions
                ).filter(k => k.split('_')[0] === dataPoint.key)
              } else if (dataPoint.children && dataPoint.children.length) {
                for (let child of dataPoint.children) {
                  let col = Object.keys(
                    chartContext.options.timeGroupOptions
                  ).filter(k => k.split('_')[0] === child.key)
                  columns = columns.concat(col)
                }
              }
              for (let column of columns) {
                let dJson = chartContext.data[column]
                let unit = chartContext.unitMap[column.split('_')[0]]

                let actualVal = chartContext.scatterConfig
                  ? d[0].value && d[0].value.y
                    ? d[0].value.y
                    : d[0].value
                  : dJson[d[0].index] === null
                  ? 0
                  : dJson[d[0].index]

                let tVal = [
                  'days',
                  'hours',
                  'minutes',
                  'seconds',
                  'milliseconds',
                ].includes(unit)
                  ? formatDuration(actualVal, unit, dataPoint.convertTounit)
                  : (unit === '$' ? unit + ' ' : '') +
                    actualVal +
                    (unit && unit !== '$' ? ' ' + unit : '')

                let tRawVal = chartContext.scatterConfig
                  ? d[0].value && d[0].value.y
                    ? d[0].value.y
                    : d[0].value
                  : dJson[d[0].index]
                if (dJson) {
                  children.push({
                    key: column,
                    rawValue: tRawVal,
                    rawunit: unit,
                    label: chartContext.options.timeGroupOptions[column].name,
                    value: tVal,
                    color: chartContext.options.timeGroupOptions[column].color,
                  })
                }
              }
              tooltipData.push({
                label: dataPoint.label,
                is_group: true,
                children: children,
              })
            }
          }
        }

        let dateFormat = 'LLL'
        if (
          chartContext.options.axis.x.time &&
          chartContext.options.axis.x.time.format
        ) {
          dateFormat =
            chartContext.options.axis.x.time &&
            chartContext.options.axis.x.time.format.tooltip
        }

        let tooltipTitle = d[0].x
        if (typeof d[0].x === 'object') {
          tooltipTitle = moment(d[0].x).format(dateFormat)
        } else if (typeof d[0].x === 'number' && !chartContext.scatterConfig) {
          tooltipTitle = chartContext.data.x[d[0].x]
        }
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          title: tooltipTitle,
          mode: 1,
          data: tooltipData,
          dataPoint: chartContext.options.dataPoints,
        }
        if (chartContext.tooltipCallback) {
          chartContext.tooltipCallback(d, tooltipConfig, tooltipTitle)
          return null
        }
        tooltip.showTooltipForNewChart(tooltipConfig)
        return null
      },
    }
    if (chartContext.scatterConfig) {
      tooltipOptions['grouped'] = false
    }
    return tooltipOptions
  },

  prepareColorPalette() {},
  getTitleList(chartCore, chartContext, params) {
    let data = chartCore
    let timeScale = d3
      .scaleTime()
      .domain([new Date(data.orgXDomain[0]), new Date(data.orgXDomain[1])])
      .range(chartCore.xAxis.config.range) // construct time scale to get the range from time
    let d = {
      start: params.regions.length ? params.regions[0].start : null,
      end: params.regions.length ? params.regions[0].end : null,
    }
    let regionsTitles = [
      {
        title: 'BASELINE PERIOD',
        startWidth: data.xMin,
        endWidth: timeScale(moment(d.start, chartContext.options.xFormat)),
        diff:
          timeScale(moment(d.start, chartContext.options.xFormat)) - data.xMin,
        key: 'FIRST',
        margin: data.margin,
      },
      {
        title: 'ECM',
        startWidth: timeScale(moment(d.start, chartContext.options.xFormat)),
        endWidth: timeScale(moment(d.end, chartContext.options.xFormat)),
        diff:
          timeScale(moment(d.end, chartContext.options.xFormat)) -
          timeScale(moment(d.start, chartContext.options.xFormat)),
        key: 'MID',
        margin: data.margin,
      },
      {
        title: 'REPORTING PERIOD',
        startWidth: timeScale(moment(d.end, chartContext.options.xFormat)),
        endWidth: data.xMax,
        diff:
          data.xMax - timeScale(moment(d.end, chartContext.options.xFormat)),
        key: 'LAST',
        margin: data.margin,
      },
    ]
    chartContext.regionsTitles = regionsTitles
  },
  applyDefaultOptions(chartContext, params) {
    let self = this
    if (
      params.axis.x.type === 'timeseries' &&
      !params.data.booleanChart &&
      !params.data.enumChart
    ) {
      params.zoom = {
        enabled: {
          type: 'drag',
        },
      }

      let lastTime = chartContext.data['x'][chartContext.data['x'].length - 1]
      lastTime = moment(lastTime)
        .tz(Vue.prototype.$timezone)
        .valueOf()

      let currentTime = moment(new Date())
        .tz(Vue.prototype.$timezone)
        .valueOf()
      if (!chartContext.data.x.length || lastTime <= currentTime) {
        params.zoom = {
          enabled: {
            type: 'drag',
          },
        }
      }
    } else {
      params.zoom = {
        enabled: false,
      }
    }

    params.onzoom = function() {}
    params.oninit = function() {}
    let isToggle = false
    params.onrendered = function() {
      try {
        if (
          chartContext.options.diffChartConfig &&
          Object.keys(chartContext.options.diffChartConfig).length > 1
        ) {
          if (params.regions) {
            self.getTitleList(this, chartContext, params)
          }
          if (chartContext.options.diffChartConfig) {
            self.renderDiffPath(
              this,
              chartContext,
              params,
              chartContext.options.diffChartConfig
            ) // to be done
          }
        }
        if (
          chartContext.options.intersections &&
          chartContext.options.intersections.length
        ) {
          self.drawIntersections(this, chartContext)
        }
        let yAxis = null
        let y2Axis = null
        if (params.key && chartContext.options.multichart[params.key]) {
          // multichart case
          yAxis = chartContext.options.multichart[params.key].axis.y
          y2Axis = chartContext.options.multichart[params.key].axis.y2
        } else {
          yAxis = chartContext.options.axis.y
          y2Axis = chartContext.options.axis.y2
        }

        if (yAxis && this.axes.y && this.axes.y.select('text')) {
          let textWidth = this.axes.y
            .select('text')
            .node()
            .getComputedTextLength()
          let text = this.axes.y.select('text').text()
          if (this.height < textWidth) {
            let widthPerChar = textWidth / text.length
            let maxChar = this.height / widthPerChar
            maxChar = Math.floor(maxChar) - 1

            if (text.length > maxChar) {
              let diff = text.length - yAxis.label.text.length
              let trimmedText =
                yAxis.label.text.substring(0, maxChar - diff) + '..'
              if (yAxis.unit) {
                trimmedText += ' (' + yAxis.unit + ')'
              }
              this.axes.y.select('text').text(trimmedText)
              this.axes.y
                .select('text')
                .append('svg:title')
                .text(function() {
                  return text
                })
            }
          }
        }

        if (y2Axis && this.axes.y2 && this.axes.y2.select('text')) {
          let textWidth = this.axes.y2
            .select('text')
            .node()
            .getComputedTextLength()
          let text = this.axes.y2.select('text').text()
          if (this.height < textWidth) {
            let widthPerChar = textWidth / text.length
            let maxChar = this.height / widthPerChar
            maxChar = Math.floor(maxChar) - 1

            if (text.length > maxChar) {
              let diff = text.length - y2Axis.label.text.length
              let trimmedText =
                y2Axis.label.text.substring(0, maxChar - diff) + '..'
              if (y2Axis.unit) {
                trimmedText += ' (' + y2Axis.unit + ')'
              }
              this.axes.y2.select('text').text(trimmedText)
              this.axes.y2
                .select('text')
                .append('svg:title')
                .text(function() {
                  return text
                })
            }
          }
        }

        this.arcs
          .select('.bb-chart-arcs-title')
          .selectAll('tspan')
          .attr('dy', '15')
        this.arcs
          .select('.bb-chart-arcs-title')
          .select('tspan')
          .attr('dy', '-10')

        if (!isToggle) {
          self.applyGroupTotalBar.bind(this, chartContext)()
        } else {
          isToggle = false
        }
        if (
          chartContext.options.type === 'histogram' &&
          !(chartContext.options.dataPoints.length > 1) &&
          !(chartContext.options.dataPoints[0].groupBy.length > 0)
        ) {
          let noOfBars = params.data.json.x.length
          let barParams = []
          if (chartContext.options.axis.rotated) {
            for (let j = 1; j < noOfBars; j++) {
              let barStart = parseInt(
                this.svg
                  .select('g')
                  .select('.bb-chart')
                  .select('.bb-chart-bars')
                  .select('.bb-chart-bar')
                  .select('.bb-bars')
                  .select('.bb-bar-' + (j - 1).toString())
                  .node()
                  .getAttribute('d')
                  .split(',')[1]
                  .split(' ')[1]
                  .split('V')[1]
              )
              barParams = []
              let barStartNext = this.svg
                .select('g')
                .select('.bb-chart')
                .select('.bb-chart-bars')
                .select('.bb-chart-bar')
                .select('.bb-bars')
                .select('.bb-bar-' + j.toString())
                .node()
                .getAttribute('d')
                .split(',')
              barParams.push(barStartNext[0])
              barParams = barParams.concat(barStartNext[1].split(' '))
              let level1 = barParams[1].split('H')
              let changedValue = barStart + 1 + 'H' + level1[1]
              barParams[1] = changedValue
              barParams[1] = barParams.slice(0, 2).join(',')
              this.svg
                .select('g')
                .select('.bb-chart')
                .select('.bb-chart-bars')
                .select('.bb-chart-bar')
                .select('.bb-bars')
                .select('.bb-bar-' + j.toString())
                .attr('d', barParams.splice(1).join(' '))
            }
          } else {
            for (let i = 1; i < noOfBars; i++) {
              let barStart = parseInt(
                this.svg
                  .select('g')
                  .select('.bb-chart')
                  .select('.bb-chart-bars')
                  .select('.bb-chart-bar')
                  .select('.bb-bars')
                  .select('.bb-bar-' + (i - 1).toString())
                  .node()
                  .getAttribute('d')
                  .split(',')[1]
                  .split(' ')[1]
                  .split('H')[1]
              )

              let barStartNext = this.svg
                .select('g')
                .select('.bb-chart')
                .select('.bb-chart-bars')
                .select('.bb-chart-bar')
                .select('.bb-bars')
                .select('.bb-bar-' + i.toString())
                .node()
                .getAttribute('d')
                .split(',')
              let check = 'M' + (barStart + 1).toString()
              barStartNext[0] = check
              this.svg
                .select('g')
                .select('.bb-chart')
                .select('.bb-chart-bars')
                .select('.bb-chart-bar')
                .select('.bb-bars')
                .select('.bb-bar-' + i.toString())
                .attr('d', barStartNext.join(','))
            }
          }
        }
        // eslint-disable-next-line no-empty
      } catch (error) {}
    }
    params.ontoggle = function() {
      isToggle = true
    }
    params.legend = {
      show: false,
    }

    params.bar = {
      padding: 1,
      width: {
        ratio:
          chartContext.options.type === 'histogram' &&
          !(chartContext.options.dataPoints.length > 1) &&
          !(chartContext.options.dataPoints[0].groupBy.length > 0)
            ? 1
            : 0.5,
      },
    }
    if (
      chartContext.options.type != 'histogram' ||
      chartContext.options.dataPoints.length > 1 ||
      chartContext.options.dataPoints[0].groupBy.length > 0
    ) {
      params.bar.width.max = 75
    }
    if (chartContext.options.bar && chartContext.options.bar.radius) {
      params.bar.radius = {
        ratio: chartContext.options.bar.radius * 0.1,
      }
    }
    if (chartContext.options.bar && chartContext.options.bar.padding) {
      params.bar.padding = chartContext.options.bar.padding
    }

    let areaOptions = {}
    if (chartContext.options.area.linearGradient) {
      areaOptions.linearGradient = true
    }
    if (chartContext.options.area.above) {
      areaOptions.above = true
    }
    if (Object.keys(areaOptions).length) {
      params.area = areaOptions
    }

    if (
      chartContext.options.donut.labelType &&
      chartContext.options.donut.labelType !== 'percentage'
    ) {
      params.donut = {
        label: {
          format: function(d) {
            if (chartContext.options.donut.labelType === 'none') {
              return ''
            }
            if (Number.isInteger(d)) {
              return d3.format(',')(d)
            }
            return d3.format(',.2f')(d)
          },
        },
      }
      params.pie = {
        label: {
          format: function(d) {
            if (chartContext.options.donut.labelType === 'none') {
              return ''
            }
            if (Number.isInteger(d)) {
              return d3.format(',')(d)
            }
            return d3.format(',.2f')(d)
          },
        },
      }
    }

    if (
      chartContext.options.donut.centerText.primaryText ||
      chartContext.options.donut.centerText.secondaryText
    ) {
      let totalValList = []
      for (let key in params.data.json) {
        if (
          key !== 'x' &&
          (!params.data.hide || params.data.hide.indexOf(key) === -1)
        ) {
          totalValList.push(d3.sum(params.data.json[key]))
        }
      }

      let fieldMap = {
        _sum: d3.sum(totalValList),
        _min: d3.min(totalValList),
        _max: d3.max(totalValList),
        _avg: d3.mean(totalValList),
      }

      let unit =
        chartContext.xValueMode && chartContext.options.dataPoints.length
          ? chartContext.options.dataPoints[0].unitStr
          : null

      let donutTitle = ''
      if (chartContext.options.donut.centerText.primaryText) {
        let primaryText = chartContext.options.donut.centerText.primaryText
        for (let fl in fieldMap) {
          let fieldVal = fieldMap[fl]
          if (chartContext.options.donut.centerText.primaryRoundOff)
            fieldVal = parseInt(fieldVal)
          if (chartContext.options.donut.centerText.primaryUnit)
            fieldVal = this.formatStringWithUnit(
              fieldVal,
              chartContext.options.donut.centerText.primaryUnit
            )
          else if (unit)
            fieldVal = this.formatDurationToHighestUnit(fieldVal, unit)
          else fieldVal = this.formatNumber(fieldVal)
          primaryText = primaryText.replace(new RegExp(fl, 'g'), fieldVal)
        }
        donutTitle += primaryText
      }
      donutTitle += '\n'
      donutTitle += ' '
      donutTitle += '\n'
      donutTitle += ' '
      donutTitle += '\n'
      if (chartContext.options.donut.centerText.secondaryText) {
        let secondaryText = chartContext.options.donut.centerText.secondaryText
        for (let fl in fieldMap) {
          let fieldVal = fieldMap[fl]
          if (chartContext.options.donut.centerText.secondaryRoundOff)
            fieldVal = parseInt(fieldVal)
          if (chartContext.options.donut.centerText.secondaryUnit)
            fieldVal = this.formatStringWithUnit(
              fieldVal,
              chartContext.options.donut.centerText.secondaryUnit
            )
          else if (unit)
            fieldVal = this.formatDurationToHighestUnit(fieldVal, unit)
          else fieldVal = this.formatNumber(fieldVal)
          secondaryText = secondaryText.replace(new RegExp(fl, 'g'), fieldVal)
        }
        donutTitle += secondaryText
      }

      if (!params.donut) {
        params.donut = {}
      }
      params.donut.title = donutTitle
    }

    if (
      chartContext.options.legend.show &&
      chartContext.options.legend.position === 'right'
    ) {
      if (!params.axis.y2.show) {
        params.padding.right = 10
      }
    }
  },

  applyGroupTotalBar(chartContext) {
    try {
      if (
        chartContext.options.type === 'bar' &&
        chartContext.options.bar.showGroupTotal &&
        this.data.targets.length > 1
      ) {
        let singleBarWidth = this.svg
          .select('g')
          .select('.bb-chart')
          .select('.bb-chart-bars')
          .select('.bb-chart-bar')
          .select('.bb-bars')
          .select('.bb-bar')
          .node()
          .getBoundingClientRect().width

        let noOfBars = Object.keys(chartContext.data).length - 1 // exluding x
        noOfBars = noOfBars - this.hiddenTargetIds.length

        this.svg
          .select('g')
          .select('.bb-chart')
          .select('.bb-chart-bars')
          .selectAll('.bb-chart-bar')
          .style('transform', 'translateX(' + singleBarWidth + 'px)')

        let newWidth = singleBarWidth * noOfBars + noOfBars
        this.svg
          .select('g')
          .select('.bb-chart')
          .select('.bb-chart-bars')
          .selectAll('.bb-bars-group-total .bb-shape')
          .attr('d', function() {
            let existsD = this.getAttribute('d')
            let hStr = existsD.match(/H[0-9\.]+/g) + ''

            let newHStr =
              'H' +
              Math.round(
                parseFloat(hStr.replace('H', '')) + (newWidth - singleBarWidth)
              )
            let newD = existsD.replace(hStr, newHStr)
            return newD
          })
          .style('transform', 'translateX(-' + newWidth + 'px)')
      }
      // eslint-disable-next-line no-empty
    } catch (error) {}
  },

  applyNiceTickMarks(c3Params, chartContext) {
    try {
      let yMaxArr = []
      let y2MaxArr = []

      let groupedKeys = []
      if (c3Params.data.groups && c3Params.data.groups.length) {
        for (let grp of c3Params.data.groups) {
          let grpData = []
          for (let g of grp) {
            groupedKeys.push(g)
            for (let i = 0; i < c3Params.data.json[g].length; i++) {
              if (c3Params.data.json[g][i]) {
                const getGroupData = () => {
                  const y = c3Params.data.json[g][i].y
                  const d = c3Params.data.json[g][i]
                  if (y) {
                    return grpData[i] >= parseFloat(y)
                      ? grpData[i]
                      : parseFloat(y)
                  } else {
                    return grpData[i] >= parseFloat(d)
                      ? grpData[i]
                      : parseFloat(d)
                  }
                }
                if (grpData[i]) {
                  grpData[i] = getGroupData()
                }
                grpData.push(
                  c3Params.data.json[g][i].y
                    ? parseFloat(c3Params.data.json[g][i].y)
                    : parseFloat(c3Params.data.json[g][i])
                )
              }
            }
          }

          if (c3Params.data.axes[grp[0]] === 'y') {
            yMaxArr.push(d3.max(grpData))
          }
          if (c3Params.data.axes[grp[0]] === 'y2') {
            y2MaxArr.push(d3.max(grpData))
          }
        }
      }
      if (c3Params.data.axes) {
        let keys = Object.keys(c3Params.data.axes)
        for (let key of keys) {
          if (groupedKeys.indexOf(key) < 0) {
            if (
              c3Params.data.axes[key] === 'y' &&
              c3Params.data.hide &&
              !c3Params.data.hide.includes(key)
            ) {
              yMaxArr.push(
                d3.max(c3Params.data.json[key], function(d) {
                  return d && d.y ? parseFloat(d.y) : parseFloat(d)
                })
              )
            }
            if (
              c3Params.data.axes[key] === 'y2' &&
              !c3Params.data.hide.includes(key)
            ) {
              y2MaxArr.push(
                d3.max(c3Params.data.json[key], function(d) {
                  return d && d.y ? parseFloat(d.y) : parseFloat(d)
                })
              )
            }
          }
        }
      }
      if (
        yMaxArr &&
        yMaxArr.length &&
        !c3Params.axis.y.min &&
        !c3Params.axis.y.max &&
        d3.max(yMaxArr)
      ) {
        c3Params.axis.y.tick.values = this.findNiceDelta(
          d3.max(yMaxArr),
          c3Params.axis.y.tick.count
        )
      }
      if (
        chartContext.scatterConfig &&
        yMaxArr &&
        yMaxArr.length &&
        (!c3Params.axis.y.max || c3Params.axis.y.max === '') &&
        d3.max(yMaxArr)
      ) {
        c3Params.axis.y.max = d3.max(yMaxArr)
      }
      if (
        y2MaxArr &&
        y2MaxArr.length &&
        !c3Params.axis.y2.min &&
        !c3Params.axis.y2.max &&
        d3.max(y2MaxArr)
      ) {
        c3Params.axis.y2.tick.values = this.findNiceDelta(
          d3.max(y2MaxArr),
          c3Params.axis.y2.tick.count
        )
      }
      if (
        chartContext.scatterConfig &&
        y2MaxArr &&
        y2MaxArr.length &&
        (!c3Params.axis.y2.max || c3Params.axis.y2.max === '') &&
        d3.max(y2MaxArr)
      ) {
        c3Params.axis.y2.max = d3.max(y2MaxArr)
      }
      // eslint-disable-next-line no-empty
    } catch (error) {}
  },

  findNiceDelta(maxVal, count) {
    let step = maxVal / (count - 1)
    let order = Math.pow(10, Math.floor(Math.log10(step)))
    // let delta = step / order + 0.5
    let delta = step / order

    let ndex = [1, 2, 2.5, 3, 4, 5, 10]
    let ndexLenght = ndex.length

    let stepVal = null
    for (let i = ndexLenght - 2; i >= 0; --i) {
      if (delta > ndex[i]) {
        stepVal = ndex[i + 1] * order
        break
      }
    }
    if (!stepVal) {
      stepVal = delta * order
    }

    let stepList = []
    stepList.push(0)
    for (let j = 1; j < count; j++) {
      stepList.push(j * stepVal)
    }
    return stepList
  },

  periodCheck(dateRange) {
    if (dateRange) {
      if (['dayofweek', 'dayofmonth', 'hourofday'].includes(dateRange.period)) {
        return false
      } else {
        return true
      }
    }
    return true
  },

  prepareProgressChart(chartContext, colors, unit) {
    let params = {}
    this.prepareGeneralOptions(chartContext, params)
    params.data = this.prepareData(chartContext)
    if (colors) {
      params.data.colors = colors
    }
    params.axis = this.prepareAxis(chartContext)
    params.padding = {
      top: chartContext.options.padding.top,
      bottom: chartContext.options.padding.bottom,
      left: 10,
    }
    let overallHeight = 100
    this.applyDefaultOptions(chartContext, params)
    if (params.axis.x.show !== true) {
      overallHeight = overallHeight - 50
    }
    params.size = {
      height: overallHeight,
      width: chartContext.options.size.width,
    }
    params.padding.top = 0

    params.grid.y = false
    params.axis.x.label.text = ''
    params.axis.y.show = false
    params.axis.y2.show = false
    params.padding.bottom = -25

    let classes = {}
    let regions = []
    let tooltipData = []
    let labelMap = {
      current: 'Current',
      baseline: 'Baseline',
      median: 'National Median',
      target: 'Target',
    }
    let maxValue = params.data.json.x[params.data.json.x.length - 1]
    let delta =
      maxValue *
      (chartContext.options.regionMultiplier
        ? chartContext.options.regionMultiplier
        : 0.005)
    for (let dkey of Object.keys(params.data.json).filter(k => k !== 'x')) {
      classes[dkey] = 'fc-boolean-' + dkey + '-' + 1
      for (let data of params.data.json[dkey]) {
        if (data) {
          regions.push({
            start: dkey === 'current' ? 0 : data - delta,
            end: dkey === 'current' ? data : data + delta,
            class: classes[dkey],
          })
          tooltipData.push({
            label: labelMap[dkey] ? labelMap[dkey] : dkey,
            value: d3.format(',')(parseInt(data)),
            unit: unit,
            color: colors[dkey],
          })
        }
      }
      params.data.json[dkey] = params.data.json.x.map(function() {
        return null
      })
    }

    params.regions = regions
    params.axis.x.tick.values = params.data.json.x
    if (params.tooltip) {
      params.tooltip['show'] = false
    }
    params.onafterinit = function() {
      let parentSelector = d3.select(this.api.element)
      for (let dkey of Object.keys(params.data.json).filter(k => k !== 'x')) {
        parentSelector
          .selectAll('.' + classes[dkey])
          .style('fill', params.data.colors[dkey])
        parentSelector
          .selectAll('.' + classes[dkey] + ' rect')
          .style('fill-opacity', '0.9')
        parentSelector
          .selectAll('.' + classes[dkey] + ' rect')
          .classed('fc-boolean-rect-09', true)
        parentSelector.selectAll('.bb-chart').style('display', 'none') // removed the bb-chart element from the boolean chart. it causes tool tip issue. mouse over event fires when mouse over the alarm charts not a boolean chart
        parentSelector
          .selectAll('.' + classes[dkey])
          .on('mousemove', function() {
            tooltip.showTooltipForNewChart({
              data: tooltipData,
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              mode: 1,
            })
          })
          .on('mouseout', function() {
            tooltip.hideTooltip()
          })
      }
    }
    if (params.axis.x.show) {
      params.padding.bottom = 0
    }
    if (params.axis.y && params.axis.y.show) {
      params.axis.y.label = ' '
    }
    if (params.axis.y2 && params.axis.y2.show) {
      params.axis.y2.label = ' '
    }
    params.data.json = Object.freeze(params.data.json)
    return params
  },
  getPointPattern(pattern) {
    switch (pattern) {
      case 'circle':
        return 'circle'
      case 'rectangle':
        return 'rectangle'
      case 'triangle':
        return "<polygon points='2.5 0 0 5 5 5'></polygon>"
      case 'diamond':
        return "<polygon points='2.5 0 0 2.5 2.5 5 5 2.5 2.5 0'></polygon>"
      default:
        return 'circle'
    }
  },
  getClusters(data) {
    let clusterData = []
    if (data && data.length) {
      clusterData = [...new Set(data.filter(val => val !== null))].sort(
        (a, b) => parseFloat(a) - parseFloat(b)
      )
    }
    let clusters = []
    if (clusterData.length >= 5) {
      clusters = ckmeans(clusterData, 5)
    } else {
      clusters = clusterData
      while (clusters.length < 5) {
        clusters.push(parseFloat(clusters[clusters.length - 1]) + 1)
      }
    }
    return clusters
  },
  getBubbleFormatedData(yData, zData) {
    if (yData && zData && yData.length === zData.length) {
      return yData.map((val, idx) => {
        return {
          y: val && val.y ? parseFloat(val.y) : parseFloat(val),
          z:
            zData[idx] && zData[idx].y
              ? parseFloat(zData[idx].y)
              : parseFloat(zData[idx]),
        }
      })
    }
    return yData
  },
  formatDurationToHighestUnit(val, unit) {
    if (unit === 'hours') {
      if (val > 24) {
        return `${this.formatNumber(val / 24)} days`
      } else {
        return `${this.formatNumber(val)} hours`
      }
    } else if (unit === 'minutes') {
      if (val > 1440) {
        return `${this.formatNumber(val / 1440)} days`
      } else if (val > 60) {
        return `${this.formatNumber(val / 60)} hours`
      } else {
        return `${val} minutes`
      }
    } else if (unit === 'seconds') {
      if (val > 86400) {
        return `${this.formatNumber(val / 86400)} days`
      } else if (val > 3600) {
        return `${this.formatNumber(val / 3600)} hours`
      } else if (val > 60) {
        return `${this.formatNumber(val / 60)} minutes`
      } else {
        return `${this.formatNumber(val)} seconds`
      }
    } else if (['$', '€', '₹'].includes(unit)) {
      return `${unit} ${this.formatNumber(val)}`
    } else {
      return `${this.formatNumber(val)} ${unit}`
    }
  },
  formatStringWithUnit(val, unit) {
    if (['$', '€', '₹'].includes(unit)) {
      return `${unit} ${this.formatNumber(val)}`
    } else {
      return `${this.formatNumber(val)} ${unit}`
    }
  },
  formatNumber(fieldVal) {
    if (Number.isInteger(fieldVal)) {
      fieldVal = d3.format(',')(fieldVal)
    } else {
      fieldVal = d3.format(',.2f')(fieldVal)
    }
    return fieldVal
  },
}
