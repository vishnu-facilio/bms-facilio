import * as d3 from 'd3'
import container from 'charts/core/container'
import common from 'charts/helpers/common'
import tooltip from '@/graph/mixins/tooltip'

export default {
  drawAreas(chartObj) {
    if (common.isValueArray(chartObj.data)) {
      this.drawStackedAreas(chartObj)
    } else {
      this.drawSingleArea(chartObj)
    }
  },

  drawSingleArea(chartObj) {
    let startData = chartObj.data.map(function(d) {
      return {
        label: d.label,
        value: 0,
      }
    })
    chartObj.areaOpacity = 0.24
    chartObj.ease = d3.easeQuadInOut
    chartObj.maxAreaNumber = 8
    chartObj.areaAnimationDelayStep = 20
    chartObj.areaAnimationDelays = d3.range(
      chartObj.areaAnimationDelayStep,
      chartObj.maxAreaNumber * chartObj.areaAnimationDelayStep,
      chartObj.basechartareaAnimationDelayStep
    )
    chartObj.area = d3
      .area()
      .x(function(d) {
        return chartObj.fchart.xScale(d.label)
      })
      .y0(function(d) {
        return chartObj.options.yScale
          ? chartObj.options.yScale(d.value)
          : chartObj.fchart.yScale(d.value)
      })
      .y1(function(d) {
        return chartObj.fchart.chartHeight
      })
    chartObj.areaOutline = d3
      .line()
      .curve(chartObj.area.curve())
      .x(function(d) {
        return chartObj.fchart.xScale(d.label)
      })
      .y(function(d) {
        return chartObj.options.yScale
          ? chartObj.options.yScale(d.value)
          : chartObj.fchart.yScale(d.value)
      })

    chartObj.series = chartObj.fchart.svg.select('.chart-group')

    chartObj.series
      .data([startData])
      .append('path')
      .attr('class', 'layer')
      .attr('d', chartObj.area)
      .style('opacity', chartObj.areaOpacity)
      .style('fill', function(d) {
        return chartObj.options.color
          ? chartObj.options.color
          : chartObj.fchart.categoryColorMap[0]
      })
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
      .transition()
      .duration(1500)
      .attrTween('d', tween(chartObj.data, chartObj.area))

    if (chartObj.options.withPoints) {
      chartObj.fchart.svg
        .select('.chart-group')
        .selectAll('dot')
        .data(chartObj.data)
        .enter()
        .append('circle')
        .attr('r', 3.5)
        .style('fill', function(d) {
          return chartObj.options.color
            ? chartObj.options.color
            : chartObj.fchart.categoryColorMap[0]
        })
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
          tooltip.showTooltip(tooltipConfig, chartObj.fchart)
        })
        .on('mouseout', function(d) {
          d3.select(this)
            .transition()
            .duration(300)
            .attr('r', 3.5)

          tooltip.hideTooltip()
        })
    }

    chartObj.series
      .data([startData])
      .append('path')
      .attr('class', 'area-outline')
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
      .attr('d', chartObj.areaOutline)
      .style('stroke', function(d) {
        return chartObj.options.color
          ? chartObj.options.color
          : chartObj.fchart.categoryColorMap[0]
      })
      .transition()
      .duration(1500)
      .attrTween('d', tween(chartObj.data, chartObj.areaOutline))
      .on('end', function() {
        // if (chartObj.options.withPoints) {
        //   container.drawPointsOnGraph(chartObj.data, chartObj)
        // }
      })
    // Exit
    chartObj.series
      .exit()
      .transition()
      .style('opacity', 0)
      .remove()
    function tween(b, callback) {
      return function(a) {
        let i = d3.interpolateArray(a, b)
        return function(t) {
          return callback(i(t))
        }
      }
    }
  },

  drawStackedAreas(chartObj) {
    chartObj.areaOpacity = 0.1
    chartObj.ease = d3.easeQuadInOut
    chartObj.maxAreaNumber = 8
    chartObj.areaAnimationDelayStep = 20
    chartObj.areaAnimationDelays = d3.range(
      chartObj.areaAnimationDelayStep,
      chartObj.maxAreaNumber * chartObj.areaAnimationDelayStep,
      chartObj.basechartareaAnimationDelayStep
    )
    chartObj.stacks = common.getGroup(chartObj.data)
    let dataInitial = chartObj.data.map(item => {
      let ret = {}
      for (let v of item.value) {
        ret[v.label] = v.value
      }
      return Object.assign({}, item, ret)
    })

    let stack3 = d3
      .stack()
      .keys(chartObj.stacks)
      .order(d3.stackOrderNone)
      .offset(d3.stackOffsetNone)

    let layers = stack3(dataInitial)
    chartObj.area = d3
      .area()
      .curve(d3.curveMonotoneX)
      .x(function(d) {
        return chartObj.fchart.xScale(d.data.label)
      })
      .y0(function(d) {
        return chartObj.fchart.yScale(d.data[d.subgroup])
      })
      .y1(chartObj.fchart.chartHeight)

    chartObj.areaOutline = d3
      .line()
      .curve(chartObj.area.curve())
      .x(function(d) {
        return chartObj.fchart.xScale(d.data.label)
      })
      .y(function(d) {
        return chartObj.options.yScale
          ? chartObj.options.yScale(d.data[d.subgroup])
          : chartObj.fchart.yScale(d.data[d.subgroup])
      })
    chartObj.series = chartObj.fchart.svg
      .select('.chart-group')
      .selectAll('.layer')
      .data(common.getGroup(chartObj.data))
      .enter()
      .append('g')
      .classed('layer-container', true)

    chartObj.series
      .append('path')
      .attr('class', 'layer')
      .attr('d', function(d, i) {
        let temp = layers[i]
        for (let t of temp) {
          t.subgroup = d
        }
        return chartObj.area(temp)
      })
      .attr('id', function(d) {
        return d
      })
      .style('opacity', chartObj.areaOpacity)
      .style('fill', function(d) {
        return chartObj.fchart.categoryColorMap[d]
      })

    chartObj.series
      .append('path')
      .attr('class', 'area-outline')
      .attr('d', function(d, i) {
        let temp = layers[i]
        for (let t of temp) {
          t.subgroup = d
        }
        return chartObj.areaOutline(temp)
      })
      .style('stroke', function(d) {
        return chartObj.fchart.categoryColorMap[d]
      })

    // Update
    chartObj.fchart.svg
      .select('.chart-group')
      .selectAll('.layer')
      .data(layers)
      .transition()
      .delay((_, i) => chartObj.areaAnimationDelays[i])
      .duration(1000)
      .ease(chartObj.ease)
      .attr('d', chartObj.area)
      .style('opacity', chartObj.areaOpacity)
      .style('fill', function(d) {
        return chartObj.fchart.categoryColorMap[d]
      })

    chartObj.fchart.svg
      .select('.chart-group')
      .selectAll('.area-outline')
      .data(layers)
      .transition()
      .delay((_, i) => chartObj.areaAnimationDelays[i])
      .duration(1000)
      .ease(chartObj.ease)
      .attr('d', chartObj.areaOutline)
    if (chartObj.options.withPoints) {
      container.drawStackedPointsOnFGraph(layers, chartObj)
    }
    // Exit
    chartObj.series
      .exit()
      .transition()
      .style('opacity', 0)
      .remove()
  },
}
