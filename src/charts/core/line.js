import * as d3 from 'd3'
import common from '../helpers/common'
import constant from '../helpers/constant'
import container from 'charts/core/container'
import tooltip from '@/graph/mixins/tooltip'

export default {
  drawLines(chartObj) {
    if (common.isValueArray(chartObj.data)) {
      this.drawMultiLines(chartObj)
    } else {
      this.drawSingleLine(chartObj)
    }
  },
  drawSingleLine(chartObj) {
    let lineCurve = chartObj.options.curvetype || constant.defaultCurve

    let lines

    chartObj.lineContainer = d3
      .line()
      .curve(constant.curveMap[lineCurve])
      .x(function(d) {
        return chartObj.fchart.xScale(d.label)
      })
      .y(function(d) {
        return chartObj.options.yScale
          ? chartObj.options.yScale(d.value)
          : chartObj.fchart.yScale(d.value)
      })
    lines = chartObj.fchart.svg
      .select('.chart-group')
      .selectAll('.line' + chartObj.options.id)
      .data([chartObj.data])
    chartObj.paths = lines
      .enter()
      .append('g')
      .attr('class', 'line-group')
      .attr('transform', function(d) {
        if (
          chartObj.fchart.xScale &&
          chartObj.fchart.xScale.hasOwnProperty('bandwidth')
        ) {
          return 'translate(' + chartObj.fchart.xScale.bandwidth() / 2 + ' ,0)'
        } else {
          return 'translate(' + 0 + ' ,0)'
        }
      })
      .append('path')
      .attr('class', 'line')
      .attr('id', 'singlePath')
      .attr('d', function(d) {
        return chartObj.lineContainer(d)
      })
      .style('stroke', function(d) {
        return chartObj.options.color
          ? chartObj.options.color
          : chartObj.fchart.categoryColorMap[0]
      })
    lines.exit().remove()

    if (chartObj.options.withPoints) {
      chartObj.fchart.svg
        .select('.chart-group')
        .selectAll('dot')
        .data(chartObj.data)
        .enter()
        .append('circle')
        .attr('r', 3.5)
        .attr('transform', function(d) {
          if (
            chartObj.fchart.xScale &&
            chartObj.fchart.xScale.hasOwnProperty('bandwidth')
          ) {
            return (
              'translate(' + chartObj.fchart.xScale.bandwidth() / 2 + ' ,0)'
            )
          } else {
            return 'translate(' + 0 + ' ,0)'
          }
        })
        .style('fill', function(d) {
          return chartObj.options.color
            ? chartObj.options.color
            : chartObj.fchart.categoryColorMap[0]
        })
        .attr('cx', function(d) {
          return chartObj.fchart.xScale(d.label)
        })
        .attr('cy', function(d) {
          return chartObj.options.yScale
            ? chartObj.options.yScale(d.value)
            : chartObj.fchart.yScale(d.value)
        })
        .on('mouseover', function(d) {
          d3.select(this)
            .transition()
            .duration(300)
            .attr('r', 7)

          let tooltipData = [
            {
              label: chartObj.options.xaxis.title,
              value: d.label,
              axis: chartObj.options.xaxis,
            },
            {
              label: chartObj.options.y1axis
                ? chartObj.options.y1axis.title
                : chartObj.options.y2axis.title,
              value: d.value,
              axis: chartObj.options.y1axis
                ? chartObj.options.y1axis
                : chartObj.options.y2axis,
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
          tooltip.showTooltip(tooltipConfig, chartObj)
        })
        .on('mouseout', function(d) {
          d3.select(this)
            .transition()
            .duration(300)
            .attr('r', 3.5)

          tooltip.hideTooltip()
        })
    }

    chartObj.maskingRectangle = chartObj.fchart.svg
      .append('rect')
      .attr('class', 'masking-rectangle')
      .attr('width', chartObj.fchart.getWidth() - 30)
      .attr('height', chartObj.fchart.getHeight() - 30)
      .attr('x', 0)
      .attr('y', 0)
    chartObj.fchart.svg
      .select('.chart-group')
      .selectAll('.line')
      .style('stroke-width', 2)
      .on('end', function(d) {})
    chartObj.maskingRectangle
      .transition()
      .duration(1000)
      .ease(d3.easeQuadInOut)
      .attr('x', chartObj.fchart.getWidth())
      .on('end', () => chartObj.maskingRectangle.remove())
  },

  drawMultiLines(chartObj) {
    let lineCurve = chartObj.options.curvetype || constant.defaultCurve
    let lines, lineContainer

    lineContainer = d3
      .line()
      .curve(constant.curveMap[lineCurve])
      .x(function(d) {
        return chartObj.fchart.xScale(d.label)
      })
      .y(function(d) {
        let val = d.value.find(v => v.label === d.subgroup)
        return chartObj.fchart.yScale(val.value)
      })

    lines = chartObj.fchart.svg
      .select('.chart-group')
      .selectAll('.line')
      .data(common.getGroup(chartObj.data))
    chartObj.paths = lines
      .enter()
      .append('g')
      .attr('class', 'line-group')
      .append('path')
      .attr('class', 'line')
      .attr('id', function(d) {
        return d.replace(/\s/g, '')
      })
      .attr('d', function(d) {
        let temp = chartObj.data
        for (let t of temp) {
          t.subgroup = d
        }
        temp.active = true
        temp[d] = true
        return lineContainer(temp)
      })
      .style('stroke', function(d) {
        return chartObj.fchart.categoryColorMap[d]
      })

    chartObj.fchart.svg
      .select('.chart-group')
      .selectAll('.line')
      .transition()
      .duration(1000)
      .delay(100)
      .ease(d3.easeLinear)
      .style('stroke-width', 2)
      .on('end', function(d) {
        if (chartObj.options.withPoints) {
          container.drawStackedPointsOnFGraph(chartObj.data, chartObj)
        }
      })
    lines.exit().remove()
    chartObj.maskingRectangle = chartObj.fchart.svg
      .append('rect')
      .attr('class', 'masking-rectangle')
      .attr('width', chartObj.fchart.getWidth() - 30)
      .attr('height', chartObj.fchart.getHeight() - 30)
      .attr('x', 0)
      .attr('y', 0)

    chartObj.maskingRectangle
      .transition()
      .duration(1000)
      .ease(d3.easeQuadInOut)
      .attr('x', chartObj.fchart.getWidth())
      .on('end', () => chartObj.maskingRectangle.remove())
  },
  dualAxisY2Plot(chartObj) {
    let lineCurve = chartObj.options.curvetype || constant.defaultCurve
    let lineContainers
    lineContainers = d3
      .line()
      .curve(constant.curveMap[lineCurve])
      .x(function(d) {
        return chartObj.fchart.xScale(d.label)
      })
      .y(function(d) {
        return chartObj.fchart.yDualScale(d.value)
      })
    chartObj.fchart.svg
      .select('.chart-group')
      .append('path')
      .style('stroke', 'red')
      .attr('class', 'line')
      .attr('d', lineContainers(chartObj.data))
  },
}
