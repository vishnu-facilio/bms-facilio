import * as d3 from 'd3'
import tooltip from '@/graph/mixins/tooltip'
import common from 'charts/helpers/common'

export default {
  drawBars(chartObj, callbackFunc) {
    chartObj.bars = null

    if (chartObj.options.type === 'grouped') {
      this.drawGroupedBars(chartObj)
    } else if (chartObj.options.type === 'stacked') {
      this.drawStackedBars(chartObj)
    } else {
      let dataZeroed = common.cleanData(chartObj.data)
      if (chartObj.options.isAnimated) {
        chartObj.bars = chartObj.fchart.svg
          .select('.chart-group')
          .selectAll('.bar')
          .data(dataZeroed)

        if (chartObj.options.isHorizontal) {
          this.drawHorizontalBars(chartObj)
        } else {
          this.drawVerticalBars(chartObj)
        }

        chartObj.bars = chartObj.fchart.svg
          .select('.chart-group')
          .selectAll('.bar')
          .data(chartObj.data)

        if (chartObj.options.isHorizontal) {
          this.drawAnimatedHorizontalBars(chartObj)
        } else {
          this.drawAnimatedVerticalBars(chartObj)
        }
      } else {
        chartObj.bars = chartObj.fchart.svg
          .select('.chart-group')
          .selectAll('.bar')
          .data(chartObj.data)

        if (chartObj.options.isHorizontal) {
          this.drawHorizontalBars(chartObj)
        } else {
          this.drawVerticalBars(chartObj)
        }
      }

      callbackFunc(chartObj.fchart.xScale.bandwidth())

      // Exit
      chartObj.bars
        .exit()
        .transition()
        .style('opacity', 0)
        .remove()
    }
  },

  drawHorizontalBars(chartObj) {
    // Enter + Update
    chartObj.bars
      .enter()
      .append('rect')
      .classed('bar', true)
      .attr('y', chartObj.fchart.chartHeight)
      .attr('x', 0)
      .attr('height', chartObj.fchart.yScale.bandwidth())
      .attr('width', ({ value }) => chartObj.fchart.xScale(value))
      .on('mouseover', function(d) {
        if (chartObj.fchart.handleMouseOver) {
          chartObj.fchart.handleMouseOver(d)
        }
      })
      .on('mousemove', function(d) {
        if (chartObj.fchart.handleMouseMove) {
          chartObj.fchart.handleMouseMove(d)
        }
      })
      .on('mouseout', function(d) {
        if (chartObj.fchart.handleMouseOut) {
          chartObj.fchart.handleMouseOut(d)
        }
      })
      .on('click', function(d) {
        if (chartObj.fchart.handleClick) {
          chartObj.fchart.handleClick(d)
        }
      })
      .merge(chartObj.bars)
      .attr('x', 0)
      .attr('y', ({ label }) => chartObj.fchart.yScale(label))
      .attr('height', chartObj.fchart.yScale.bandwidth())
      .attr('width', ({ value }) => chartObj.fchart.xScale(value))
      .attr('fill', ({ label }) => chartObj.fchart.categoryColorMap[label])
  },

  drawAnimatedHorizontalBars(chartObj) {
    // Enter + Update
    chartObj.bars
      .enter()
      .append('rect')
      .classed('bar', true)
      .attr('x', 0)
      .attr('y', chartObj.fchart.chartHeight)
      .attr('height', chartObj.fchart.yScale.bandwidth())
      .attr('width', ({ value }) => chartObj.fchart.xScale(value))
      .on('mouseover', function(d) {
        if (chartObj.fchart.handleMouseOver) {
          chartObj.fchart.handleMouseOver(d)
        }
      })
      .on('mousemove', function(d) {
        if (chartObj.fchart.handleMouseMove) {
          chartObj.fchart.handleMouseMove(d)
        }
      })
      .on('mouseout', function(d) {
        if (chartObj.fchart.handleMouseOut) {
          chartObj.fchart.handleMouseOut(d)
        }
      })
      .on('click', function(d) {
        if (chartObj.fchart.handleClick) {
          chartObj.fchart.handleClick(d)
        }
      })

    chartObj.bars
      .attr('x', 0)
      .attr('y', ({ label }) => chartObj.fchart.yScale(label))
      .attr('height', chartObj.fchart.yScale.bandwidth())
      .attr('fill', ({ label }) => chartObj.fchart.categoryColorMap[label])
      .transition()
      .duration(chartObj.options.animationDuration)
      .delay(chartObj.options.interBarDelay)
      .ease(chartObj.options.ease)
      .attr('width', ({ value }) => chartObj.fchart.xScale(value))
  },

  drawVerticalBars(chartObj) {
    // Enter + Update
    chartObj.bars
      .enter()
      .append('rect')
      .classed('bar', true)
      .attr('x', chartObj.fchart.chartWidth)
      .attr('transform', function() {
        if (chartObj.fchart.options.id === 3990) {
          if (
            chartObj.fchart.xScale &&
            chartObj.fchart.xScale.hasOwnProperty('bandwidth')
          ) {
            return (
              'translate(' + -(chartObj.fchart.xScale.bandwidth() / 2) + ' , 0)'
            )
          } else if (chartObj.fchart.chartWidth) {
            let width = chartObj.fchart.chartWidth / chartObj.data.length - 2
            return 'translate(' + -(width / 4) + ' , 0)'
          } else {
            return 'translate(0,0)'
          }
        } else {
          return 'translate(0,0)'
        }
      })
      .attr('y', ({ value }) => chartObj.fchart.yScale(value))
      .attr('width', function(d) {
        if (chartObj.fchart.xScale.bandwidth) {
          return chartObj.fchart.xScale.bandwidth()
        } else {
          let width = chartObj.fchart.chartWidth / chartObj.data.length - 2
          return width
        }
      })
      .attr(
        'height',
        ({ value }) =>
          chartObj.fchart.chartHeight - chartObj.fchart.yScale(value)
      )
      .merge(chartObj.bars)
      .attr('x', function(d) {
        return chartObj.fchart.xScale(d.label)
      })
      .attr('y', ({ value }) => chartObj.fchart.yScale(value))
      .attr('width', function(d) {
        if (chartObj.fchart.xScale.bandwidth) {
          return chartObj.fchart.xScale.bandwidth()
        } else {
          let width = chartObj.fchart.chartWidth / chartObj.data.length - 2
          return width
        }
      })
      .attr(
        'height',
        ({ value }) =>
          chartObj.fchart.chartHeight - chartObj.fchart.yScale(value)
      )
      .attr('fill', ({ label }) =>
        chartObj.options.color
          ? chartObj.options.color
          : chartObj.fchart.categoryColorMap[0]
      )
      .on('mouseover', function(d) {
        let processLabel
        if (
          chartObj.options.xaxis.datatype === 'date_time' ||
          chartObj.options.xaxis.datatype === 'date'
        ) {
          // processLabel = dataPoint.orgLabel
          if (d.orgLabel) {
            processLabel = d.orgLabel
          } else {
            processLabel = d.label
          }
        } else {
          processLabel = d.label
        }
        let tooltipData = [
          {
            label: chartObj.options.xaxis.title,
            value: processLabel,
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
          color: chartObj.options.color
            ? chartObj.options.color
            : chartObj.fchart.categoryColorMap[0],
        }
        tooltip.showTooltip(tooltipConfig, chartObj.fchart)
        d3.select(this).style('fill', function() {
          return d3.rgb(d3.select(this).style('fill')).darker(0.6)
        })
      })
      .on('mouseout', function(d, i) {
        d3.select(this).style(
          'fill',
          chartObj.options.color
            ? chartObj.options.color
            : chartObj.fchart.categoryColorMap[0]
        )
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
  },

  drawAnimatedVerticalBars(chartObj) {
    // Enter + Update
    let bandwidth = 0
    let bandwidthArray = []
    if (chartObj.data) {
      for (let i = 0; i < chartObj.data.length - 1; i++) {
        bandwidth =
          chartObj.fchart.xScale(chartObj.data[i + 1].label) -
          chartObj.fchart.xScale(chartObj.data[i].label)
        bandwidthArray[i] = bandwidth
      }
    }

    let width = chartObj.fchart.chartWidth / chartObj.data.length - 2
    let a = []
    for (let i = 0; i < bandwidthArray.length; i++) {
      if (bandwidthArray[i] !== 0) {
        a[i] = bandwidthArray[i]
      } else {
        if (bandwidthArray[bandwidthArray.length]) {
          bandwidthArray[i] = bandwidthArray[i + 0]
        } else {
          bandwidthArray[i] = bandwidthArray[i + 1]
        }
      }
    }
    if (a.length > 0) {
      a = a.sort(function(a, b) {
        return a - b
      })
      bandwidth = a[0]
    } else {
      bandwidth = chartObj.fchart.chartWidth / chartObj.data.length - 2
    }
    width = bandwidth - bandwidth / 10
    let newBarwidth = width

    chartObj.bars
      .enter()
      .append('rect')
      .classed('bar', true)
      .attr('x', chartObj.fchart.chartWidth)
      .attr('y', ({ value }) => chartObj.fchart.yScale(value))
      .attr('width', function(d) {
        if (chartObj.fchart.xScale.bandwidth) {
          return chartObj.fchart.xScale.bandwidth()
        } else {
          let width = chartObj.fchart.chartWidth / chartObj.data.length - 2
          return width
        }
      })
      .attr(
        'height',
        ({ value }) =>
          chartObj.fchart.chartHeight - chartObj.fchart.yScale(value)
      )
      .on('mouseover', function(d) {
        if (chartObj.fchart.handleMouseOver) {
          chartObj.fchart.handleMouseOver(d)
        }
      })
      .on('mousemove', function(d) {
        if (chartObj.fchart.handleMouseMove) {
          chartObj.fchart.handleMouseMove(d)
        }
      })
      .on('mouseout', function(d) {
        if (chartObj.fchart.handleMouseOut) {
          chartObj.fchart.handleMouseOut(d)
        }
      })
      .merge(chartObj.bars)
      .attr('x', function(d) {
        return chartObj.fchart.xScale(d.label)
      })
      .attr('width', function(d) {
        if (chartObj.fchart.xScale.bandwidth) {
          return chartObj.fchart.xScale.bandwidth()
        } else {
          let width = chartObj.fchart.chartWidth / chartObj.data.length - 2
          let barwidth = chartObj.fchart.chartWidth / chartObj.data.length - 2
          if (newBarwidth > 0) {
            barwidth = newBarwidth
          } else {
            barwidth = chartObj.fchart.chartWidth / chartObj.data.length - 2
          }
          if (barwidth > width) {
            barwidth = width / 2
          }
          if (barwidth < 0.5) {
            barwidth = 0.5
          }
          return barwidth
        }
      })
      .attr('fill', ({ label }) =>
        chartObj.options.color
          ? chartObj.options.color
          : chartObj.fchart.categoryColorMap[0]
      )
      .transition()
      .duration(chartObj.options.animationDuration)
      .delay(chartObj.options.interBarDelay)
      .ease(chartObj.options.ease)
      .attr('y', ({ value }) => chartObj.fchart.yScale(value))
      .attr(
        'height',
        ({ value }) =>
          chartObj.fchart.chartHeight - chartObj.fchart.yScale(value)
      )
  },

  drawGroupedBars(chartObj) {
    chartObj.fchart.svg.selectAll('.layer').remove()

    let series = chartObj.fchart.svg.select('.chart-group').selectAll('.layer')

    if (chartObj.options.isHorizontal) {
      this.drawGroupedHorizontalBars(chartObj, series)
    } else {
      this.drawGroupedVerticalBars(chartObj, series)
    }

    // Exit
    series
      .exit()
      .transition()
      .style('opacity', 0)
      .remove()
  },

  drawGroupedHorizontalBars(chartObj, layersSelection) {
    let layerJoin = layersSelection.data(chartObj.data)

    chartObj.layerElements = layerJoin
      .enter()
      .append('g')
      .attr(
        'transform',
        ({ label }) => `translate(0, ${chartObj.fchart.yScale(label)})`
      )
      .classed('layer', true)

    let barJoin = chartObj.layerElements.selectAll('.bar').data(function(d) {
      return d.value.map(function(v) {
        v.group = d.label
        return v
      })
    })

    let bars = barJoin
      .enter()
      .append('rect')
      .classed('bar', true)
      .attr('x', 1)
      .attr('y', function(d) {
        return chartObj.fchart.yScale2(d.label)
      })
      .attr('height', chartObj.fchart.yScale2.bandwidth())
      .attr('fill', ({ label }) => chartObj.fchart.categoryColorMap[label])

    if (chartObj.options.isAnimated) {
      bars
        .style('opacity', 0.24)
        .transition()
        .duration(chartObj.options.animationDuration)
        .delay(chartObj.options.interBarDelay)
        .ease(chartObj.options.ease)
        .tween('attr.width', function(d) {
          let node = d3.select(this),
            i = d3.interpolateRound(0, chartObj.fchart.xScale(d.value)),
            j = d3.interpolateNumber(0, 1)

          return function(t) {
            node.attr('width', i(t)).style('opacity', j(t))
          }
        })
    } else {
      bars.attr('width', d => chartObj.fchart.xScale(d.value))
    }
  },

  drawGroupedVerticalBars(chartObj, layersSelection) {
    let layerJoin = layersSelection.data(chartObj.data)

    chartObj.layerElements = layerJoin
      .enter()
      .append('g')
      .attr(
        'transform',
        ({ label }) => `translate(${chartObj.fchart.xScale(label)},0)`
      )
      .classed('layer', true)

    let barJoin = chartObj.layerElements.selectAll('.bar').data(function(d) {
      return d.value.map(function(v) {
        v.group = d.label
        v.data = d
        return v
      })
    })

    let bars = barJoin
      .enter()
      .append('rect')
      .classed('bar', true)
      .attr('x', function(d) {
        return chartObj.fchart.xScale2(d.label)
      })
      .attr('y', function(d) {
        return chartObj.fchart.yScale(d.value)
      })
      .attr('width', chartObj.fchart.xScale2.bandwidth)
      .attr('fill', ({ label }) => chartObj.fchart.categoryColorMap[label])

    if (chartObj.options.isAnimated) {
      bars
        .style('opacity', 0.24)
        .transition()
        .duration(chartObj.options.animationDuration)
        .delay(chartObj.options.interBarDelay)
        .ease(chartObj.options.ease)
        .tween('attr.height', function(d) {
          let node = d3.select(this),
            i = d3.interpolateRound(
              0,
              chartObj.fchart.chartHeight -
                chartObj.fchart.yScale(chartObj.fchart.getValue(d))
            ),
            y = d3.interpolateRound(
              chartObj.fchart.chartHeight,
              chartObj.fchart.yScale(chartObj.fchart.getValue(d))
            ),
            j = d3.interpolateNumber(0, 1)

          return function(t) {
            node
              .attr('y', y(t))
              .attr('height', i(t))
              .style('opacity', j(t))
          }
        })
    } else {
      bars.attr(
        'height',
        d => chartObj.fchart.chartHeight - chartObj.fchart.yScale(d.value)
      )
    }
    bars
      .on('mouseover', function(d) {
        let processLabel
        if (
          chartObj.options.xaxis.datatype === 'date_time' ||
          chartObj.options.xaxis.datatype === 'date'
        ) {
          // processLabel = dataPoint.orgLabel
          if (d.data.orgLabel) {
            processLabel = d.data.orgLabel
          } else {
            processLabel = d.data.label
          }
        } else {
          processLabel = d.data.label
        }
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          mode: 1,
          title: processLabel,
          data: d.data,
          color: chartObj.fchart.categoryColorMap[d.label],
        }
        tooltip.showTooltip(tooltipConfig, chartObj.fchart)

        d3.select(this).style('fill', function() {
          return d3.rgb(d3.select(this).style('fill')).darker(0.6)
        })
      })
      .on('mouseout', function(d, i) {
        d3.select(this).style('fill', chartObj.fchart.categoryColorMap[d.label])
        tooltip.hideTooltip()
      })
      .on('click', function(d, i) {
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

  drawStackedBars(chartObj) {
    let stacks = common.getDescendingGroup(chartObj.data, chartObj.options)
    let stack = d3.stack().keys(stacks),
      dataInitial = chartObj.data.map(item => {
        let ret = {}
        for (let v of item.value) {
          ret[v.label] = v.value
        }
        return Object.assign({}, item, ret)
      })

    let layers = stack(dataInitial)

    // Not ideal, we need to figure out how to call exit for nested elements
    chartObj.fchart.svg.selectAll('.layer').remove()

    let series = chartObj.fchart.svg.select('.chart-group').selectAll('.layer')

    if (chartObj.options.isHorizontal) {
      this.drawStackedHorizontalBars(chartObj, layers, series)
    } else {
      this.drawStackedVerticalBars(chartObj, layers, series)
    }
    // Exit
    series
      .exit()
      .transition()
      .style('opacity', 0)
      .remove()
  },

  drawStackedHorizontalBars(chartObj, layers, layersSelection) {
    let layerJoin = layersSelection.data(layers)

    let layerElements = layerJoin
      .enter()
      .append('g')
      .attr('fill', ({ key }) => chartObj.fchart.categoryColorMap[key])
      .classed('layer', true)

    let barJoin = layerElements.selectAll('.bar').data(d => d)

    // Enter + Update
    let bars = barJoin
      .enter()
      .append('rect')
      .classed('bar', true)
      .attr('x', d => chartObj.fchart.xScale(d[0]))
      .attr('y', d => chartObj.fchart.yScale(d.data.label))
      .attr('height', chartObj.fchart.yScale.bandwidth)
      .attr(
        'fill',
        ({ data }) => chartObj.fchart.categoryColorMap[`${data.label}`]
      )

    if (chartObj.options.isAnimated) {
      bars
        .style('opacity', 0.24)
        .transition()
        .duration(chartObj.options.animationDuration)
        .delay(chartObj.options.interBarDelay)
        .ease(chartObj.options.ease)
        .tween('attr.width', function(d) {
          let node = d3.select(this),
            i = d3.interpolateRound(0, chartObj.fchart.xScale(d[1] - d[0])),
            j = d3.interpolateNumber(0, 1)

          return function(t) {
            node.attr('width', i(t)).style('opacity', j(t))
          }
        })
    } else {
      bars.attr('width', d => chartObj.fchart.xScale(d[1] - d[0]))
    }
  },

  drawStackedVerticalBars(chartObj, layers, layersSelection) {
    let layerJoin = layersSelection.data(layers)

    let layerElements = layerJoin
      .enter()
      .append('g')
      .attr('fill', ({ key }) => chartObj.fchart.categoryColorMap[key])
      .attr('key', ({ key }) => key)
      .classed('layer', true)

    let barJoin = layerElements.selectAll('.bar').data(d => d)

    // Enter + Update
    let bars = barJoin
      .enter()
      .append('rect')
      .classed('bar', true)
      .attr('x', d => chartObj.fchart.xScale(d.data.label))
      .attr('y', d => chartObj.fchart.yScale(d[1]))
      .attr('width', function(d) {
        if (chartObj.fchart.xScale.bandwidth) {
          return chartObj.fchart.xScale.bandwidth()
        } else {
          let width =
            chartObj.fchart.chartWidth /
              (chartObj.data.length + chartObj.data.length / 2) -
            20
          return width
        }
      })
      .attr(
        'fill',
        ({ data }) => chartObj.fchart.categoryColorMap[`${data.label}`]
      )

    if (chartObj.options.isAnimated) {
      bars
        .style('opacity', 0.24)
        .transition()
        .duration(chartObj.options.animationDuration)
        .delay(chartObj.options.interBarDelay)
        .ease(chartObj.options.ease)
        .tween('attr.height', function(d) {
          let node = d3.select(this),
            i = d3.interpolateRound(
              0,
              chartObj.fchart.yScale(d[0]) - chartObj.fchart.yScale(d[1])
            ),
            j = d3.interpolateNumber(0, 1)

          return function(t) {
            node.attr('height', i(t)).style('opacity', j(t))
          }
        })
    } else {
      bars.attr(
        'height',
        d => chartObj.fchart.yScale(d[0]) - chartObj.fchart.yScale(d[1])
      )
    }
    bars
      .on('mouseover', function(d) {
        let processLabel
        if (
          chartObj.options.xaxis.datatype === 'date_time' ||
          chartObj.options.xaxis.datatype === 'date'
        ) {
          // processLabel = dataPoint.orgLabel
          if (d.data.orgLabel) {
            processLabel = d.data.orgLabel
          } else {
            processLabel = d.data.label
          }
        } else {
          processLabel = d.data.label
        }
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          mode: 1,
          title: processLabel,
          data: d.data,
          color: chartObj.fchart.categoryColorMap[d.label],
        }
        tooltip.showTooltip(tooltipConfig, chartObj.fchart)
        d3.select(this).style('fill', function() {
          return d3.rgb(d3.select(this).style('fill')).darker(0.6)
        })
      })
      .on('mouseout', function(d, i) {
        d3.select(this).style('fill', chartObj.fchart.categoryColorMap[d.label])
        tooltip.hideTooltip()
      })
      .on('click', function(d, i) {
        let stackedKey = d3
          .select(this)
          .node()
          .parentNode.getAttribute('key')
        if (chartObj.onclick) {
          let stackedEntry = d.data.value.find(v => v.label === stackedKey)
          if (stackedEntry) {
            chartObj.onclick([
              {
                data: d.data,
                axis: chartObj.options.xaxis,
              },
              {
                data: stackedEntry,
                axis: chartObj.options.groupby,
              },
            ])
          }
        }
      })
  },
}
