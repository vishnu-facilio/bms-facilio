import * as d3 from 'd3'
import tooltip from '@/graph/mixins/tooltip'
import Vue from 'vue'
import moment from 'moment-timezone'
import common from '../helpers/common'

export default {
  drawScatterPlot(chartObj) {
    if (common.isValueArray(chartObj.data)) {
      this.drawMultiScatterPlot(chartObj)
    } else {
      this.drawSingleScatterPlot(chartObj)
    }
  },

  leastSquares(chartObj, data) {
    let xSeries = data.map(chartObj.fchart.getLabel)
    let ySeries = data.map(chartObj.fchart.getValue)

    let reduceSumFunc = function(prev, cur) {
      return prev + cur
    }

    let xBar = (xSeries.reduce(reduceSumFunc) * 1.0) / xSeries.length
    let yBar = (ySeries.reduce(reduceSumFunc) * 1.0) / ySeries.length

    let ssXX = xSeries
      .map(function(d) {
        return Math.pow(d - xBar, 2)
      })
      .reduce(reduceSumFunc)

    let ssYY = ySeries
      .map(function(d) {
        return Math.pow(d - yBar, 2)
      })
      .reduce(reduceSumFunc)

    let ssXY = xSeries
      .map(function(d, i) {
        return (d - xBar) * (ySeries[i] - yBar)
      })
      .reduce(reduceSumFunc)

    let slope = ssXY / ssXX
    let intercept = yBar - xBar * slope
    let rSquare = Math.pow(ssXY, 2) / (ssXX * ssYY)

    return [slope, intercept, rSquare]
  },

  regression(x, slope, intercept) {
    return x * slope + intercept
  },

  drawSingleScatterPlot(chartObj) {
    let circles = chartObj.fchart.svg
      .select('.chart-group')
      .selectAll('.scatter-dot')
      .data(chartObj.data)
      .enter()
      .append('circle')
      .attr('class', 'scatter-dot')
      .attr('r', 5)
      .attr('cx', function(d) {
        return chartObj.fchart.xScale(d.label)
      })
      .attr('cy', function(d) {
        return chartObj.fchart.yScale(d.value)
      })
      .style('fill', function(d) {
        return chartObj.fchart.categoryColorMap[0]
      })
      .style('opacity', 0)

    circles
      .transition()
      .delay(500)
      .duration(500)
      .ease(d3.easeCircleIn)
      .style('opacity', function(d, i) {
        if (d.value > 0) {
          return 1
        }
        return 0
      })

    circles
      .on('mouseover', function(d) {
        d3.select(this)
          .transition()
          .duration(300)
          .attr('r', 10)

        let tooltipData = [
          {
            label: chartObj.options.xaxis.title,
            value: d.label,
            axis: chartObj.options.xaxis,
          },
          {
            label: chartObj.options.y1axis.title,
            value: d.value,
            axis: chartObj.options.y1axis,
          },
        ]
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          data: tooltipData,
          color: chartObj.fchart.categoryColorMap[d.label],
        }
        if (d.formatted_date) {
          tooltipConfig.title1 = d.formatted_date
        }
        tooltip.showTooltip(tooltipConfig, chartObj.fchart)
      })
      .on('mouseout', function(d) {
        d3.select(this)
          .transition()
          .duration(300)
          .attr('r', 5)

        tooltip.hideTooltip()
      })
      .on('click', function(d) {
        if (chartObj.onclick) {
          chartObj.onclick([
            {
              data: d,
              axis: chartObj.options.xaxis,
            },
          ])
        }
      })

    if (chartObj.options.trendline) {
      let leastSquaresCoeff = this.leastSquares(chartObj, chartObj.data)

      let self = this
      let line = d3
        .line()
        .curve(d3.curveLinear)
        .x(function(d) {
          return chartObj.fchart.xScale(d.label)
        })
        .y(function(d) {
          return chartObj.fchart.yScale(
            self.regression(d.label, leastSquaresCoeff[0], leastSquaresCoeff[1])
          )
        })

      chartObj.fchart.svg
        .select('.chart-group')
        .append('path')
        .datum(chartObj.data)
        .attr('class', 'trendline')
        .attr('d', line)
        .attr('stroke', function(d) {
          return '#84e7f7'
        })
        .attr('stroke-width', 1.6)

      chartObj.fchart.svg
        .select('.chart-group')
        .append('text')
        .text('R²: ' + d3.format('0.2f')(leastSquaresCoeff[2]))
        .attr('class', 'r-squared-label')
        .attr('x', function(d) {
          return chartObj.fchart.chartWidth - 50
        })
        .attr('y', function(d) {
          return 0
        })
    }
  },

  drawMultiScatterPlot(chartObj) {
    let layersSelection = chartObj.fchart.svg
      .select('.chart-group')
      .selectAll('.layer')

    let layerJoin = layersSelection.data(chartObj.data)

    chartObj.layerElements = layerJoin
      .enter()
      .append('g')
      .classed('layer', true)

    let scatterJoin = chartObj.layerElements
      .selectAll('.scatter-plot')
      .data(function(d) {
        return d.value.map(function(v) {
          v.group = d.label
          v.data = d
          return v
        })
      })

    let circles = scatterJoin
      .enter()
      .append('circle')
      .attr('class', 'scatter-dot')
      .attr('r', 5)
      .attr('cx', function(d) {
        return chartObj.fchart.xScale(d.group)
      })
      .attr('cy', function(d) {
        return chartObj.fchart.yScale(d.value)
      })
      .style('fill', function(d) {
        return chartObj.fchart.categoryColorMap[d.label]
      })
      .style('opacity', 0)

    circles
      .transition()
      .delay(500)
      .duration(500)
      .ease(d3.easeCircleIn)
      .style('opacity', function(d, i) {
        if (d.value > 0) {
          return 1
        }
        return 0
      })

    circles
      .on('mouseover', function(d) {
        d3.select(this)
          .transition()
          .duration(300)
          .attr('r', 10)

        let tooltipData = [
          {
            label: chartObj.options.xaxis.title,
            value: d.group,
            axis: chartObj.options.xaxis,
          },
          {
            label: chartObj.options.groupby.title,
            value: d.label,
            axis: chartObj.options.groupby,
          },
          {
            label: chartObj.options.y1axis.title,
            value: d.value,
            axis: chartObj.options.y1axis,
          },
        ]
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          data: tooltipData,
          color: chartObj.fchart.categoryColorMap[d.label],
        }
        tooltip.showTooltip(tooltipConfig, chartObj.fchart)
      })
      .on('mouseout', function(d) {
        d3.select(this)
          .transition()
          .duration(300)
          .attr('r', 5)

        tooltip.hideTooltip()
      })
      .on('click', function(d) {
        if (chartObj.onclick) {
          chartObj.onclick([
            {
              data: d.data,
              axis: chartObj.options.xaxis,
            },
            {
              data: d,
              axis: chartObj.options.groupby,
            },
          ])
        }
      })
  },

  drawAlarms(chartcontext, data) {
    chartcontext.series = chartcontext.svg.select('.metadata-group')
    chartcontext.series
      .selectAll('scatter-dots')
      .data(data)
      .enter()
      .append('svg:rect')
      .attr('x', function(d, i) {
        return chartcontext.xScale(moment(d.from).tz(Vue.prototype.$timezone))
      })
      .attr('y', function(d) {
        return 0
      })
      .attr('width', function(d, i) {
        let from = chartcontext.xScale(
          moment(d.from).tz(Vue.prototype.$timezone)
        )
        let to = chartcontext.xScale(moment(d.to).tz(Vue.prototype.$timezone))
        let diff = to - from
        return diff <= 4 ? 4 : diff
      })
      .attr('height', function(d, i) {
        return chartcontext.yScale(0)
      })
      .attr('stroke', 'black')
      .attr('stroke-width', 0)
      .attr('opacity', '0.5')
      .attr('class', 'alarm-indication')
      .on('mouseover', function(d) {
        chartcontext.hover = false
        chartcontext.dispatcher.call('customMouseOut', this, d, d3.mouse(this))
        d3.select(this)
          .transition()
          .duration(500)
          .attr('opacity', '0.6')

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
        tooltip.showTooltip(tooltipConfig, chartcontext)
      })
      .on('mouseout', function() {
        chartcontext.hover = true
        d3.select(this)
          .transition()
          .duration(500)
          .attr('opacity', '0.5')
        tooltip.hideTooltip()
      })
      .on('click', function(d) {
        tooltip.hideTooltip()
        let alarmID = d.id
        chartcontext.$router.push({ path: '/app/fa/faults/summary/' + alarmID })
      })
  },
}
