import * as d3 from 'd3'
import tooltip from '@/graph/mixins/tooltip'
import common from '../helpers/common'

export default {
  mixins: [tooltip],
  buildContainerGroups(chartContext) {
    chartContext.container = chartContext.svg
      .append('g')
      .classed('container-group', true)
      .attr(
        'transform',
        `translate(${chartContext.getMargin().left +
          chartContext.getOptions().yAxisPaddingBetweenChart}, ${
          chartContext.getMargin().top
        })`
      )

    chartContext.container.append('g').classed('grid-lines-group', true)
    chartContext.container
      .append('g')
      .attr('transform', `translate(0, 0)`)
      .classed('y-axis-group axis', true)
    chartContext.container
      .append('g')
      .attr(
        'transform',
        `translate(${chartContext.getWidth() -
          chartContext.getMargin().right -
          chartContext.getMargin().left}, 0)`
      )
      .classed('y1-axis-group axis', true)
    chartContext.container
      .append('g')
      .classed('x-axis-group', true)
      .append('g')
      .classed('axis x', true)
    chartContext.container
      .selectAll('.x-axis-group')
      .append('g')
      .classed('month-axis', true)
    chartContext.container
      .append('g')
      .classed('chart-group', true)
      .attr('width', 400)
      .attr('height', 100)
    chartContext.container.append('g').classed('metadata-group', true)
    if (chartContext.getOptions().withPoints) {
      chartContext.container.append('g').classed('metadata-group2', true)
    }
    chartContext.container.append('g').classed('brush-group', true)
    chartContext.hover = true
  },
  setEpsilon(context) {
    let dates = context.getMainData().map(({ label }) => label)
    if (context.xScale) {
      context.epsilon =
        (context.xScale(dates[1]) - context.xScale(dates[0])) / 2
      if (context.epsilon <= 0) {
        context.epsilon = 2
      }
    }
  },
  getNearestDataPoint(mouseX, context) {
    if (context.xScale) {
      let points = context
        .getMainData()
        .filter(
          ({ label }) =>
            Math.abs(context.xScale(label) - mouseX) <= context.epsilon
        )
      if (points.length) {
        return points[0]
      }
    }
  },
  highlightDataPoints(values, context) {
    let highlightCircleSize = 12,
      highlightCircleRadius = 5,
      highlightCircleStroke = 1.2
    this.cleanDataPointHighlights(context)
    if (common.isValueArray(context.getMainData())) {
      values.value.forEach((d, index) => {
        let marker = context.verticalMarkerContainer
          .append('g')
          .classed('circle-container', true)
          .append('circle')
          .classed('data-point-highlighter', true)
          .attr('cx', highlightCircleSize)
          .attr('cy', 0)
          .attr('r', highlightCircleRadius)
          .style('stroke-width', highlightCircleStroke)
          .style('stroke', context.categoryColorMap[d.label])
          .style('cursor', 'pointer')
        marker.attr(
          'transform',
          `translate( ${-highlightCircleSize}, ${context.yScale(d.value)} )`
        )
      })
    } else {
      let marker = context.verticalMarkerContainer
        .append('g')
        .classed('circle-container', true)
        .append('circle')
        .classed('data-point-highlighter', true)
        .attr('cx', highlightCircleSize)
        .attr('cy', 0)
        .attr('r', highlightCircleRadius)
        .style('stroke-width', highlightCircleStroke)
        .style('stroke', context.categoryColorMap[0])
        .style('cursor', 'pointer')
      marker.attr(
        'transform',
        `translate( ${-highlightCircleSize}, ${context.yScale(values.value)} )`
      )
    }
  },
  cleanDataPointHighlights(context) {
    context.verticalMarkerContainer.selectAll('.circle-container').remove()
  },
  addMouseEvents(context) {
    let self = this
    if (context.hover === false) {
      tooltip.hideTooltip()
    } else {
      context.svg
        .on('mouseover', function(d) {
          context.verticalMarkerLine.classed('bc-is-active', true)
          context.dispatcher.call('customMouseOver', this, d, d3.mouse(this))
        })
        .on('mouseout', function(d) {
          if (context.hover === true) {
            context.overlay.style('display', 'none')
            context.verticalMarkerLine.classed('bc-is-active', false)
            context.verticalMarkerContainer.attr(
              'transform',
              'translate(9999, 0)'
            )
            tooltip.hideTooltip()

            context.dispatcher.call('customMouseOut', this, d, d3.mouse(this))
          }
        })
        .on('click', function(d) {
          context.dispatcher.call('customClick', this, d, d3.mouse(this))
        })
        .on('mousemove', function(d) {
          if (context.hover === true) {
            context.epsilon || self.setEpsilon(context)
            let [xPosition, yPosition] = d3.mouse(this),
              dataPoint = self.getNearestDataPoint(
                xPosition - context.getMargin().left,
                context
              ),
              dataPointXPosition
            if (dataPoint) {
              dataPointXPosition = context.xScale(dataPoint.label)
              self.moveVerticalMarker(dataPointXPosition, context)
              self.highlightDataPoints(dataPoint, context)
              let typeTooltip = 0
              let toolTipData = null
              let tooltipConfig = {}
              if (dataPoint.value.length) {
                toolTipData = dataPoint
                typeTooltip = 1
                let processedlabel
                if (
                  context.getOptions().xaxis.datatype === 'date_time' ||
                  context.getOptions().xaxis.datatype === 'date'
                ) {
                  if (dataPoint.orgLabel) {
                    processedlabel = dataPoint.orgLabel
                  } else {
                    processedlabel = dataPoint.label
                  }
                } else {
                  processedlabel = dataPoint.label
                }
                tooltipConfig = {
                  position: {
                    left: d3.event.pageX,
                    top: d3.event.pageY,
                  },
                  title: processedlabel,
                  data: toolTipData,
                  mode: typeTooltip,
                }
              } else {
                let processLabel
                if (context.getOptions().xaxis.operation === 'actual') {
                  processLabel = dataPoint.orgLabel
                } else {
                  if (
                    context.getOptions().xaxis.datatype === 'date_time' ||
                    context.getOptions().xaxis.datatype === 'date'
                  ) {
                    // processLabel = dataPoint.orgLabel
                    if (dataPoint.orgLabel) {
                      processLabel = dataPoint.orgLabel
                    } else {
                      processLabel = dataPoint.label
                    }
                  } else {
                    processLabel = dataPoint.label
                  }
                }
                typeTooltip = 2
                toolTipData = [
                  {
                    label: context.getOptions().xaxis.title,
                    value: processLabel,
                    axis: context.getOptions().xaxis,
                  },
                  {
                    label: context.getOptions().y1axis.title,
                    value: dataPoint.value,
                    axis: context.getOptions().y1axis,
                  },
                ]
                tooltipConfig = {
                  position: {
                    left: d3.event.pageX,
                    top: d3.event.pageY,
                  },
                  data: toolTipData,
                  mode: typeTooltip,
                }
              }
              tooltip.showTooltip(tooltipConfig, context)
              context.dispatcher.call(
                'customMouseMove',
                this,
                dataPoint,
                context.categoryColorMap,
                dataPointXPosition,
                yPosition
              )
            }
          }
        })
    }
  },
  moveVerticalMarker(verticalMarkerXPosition, context) {
    context.verticalMarkerContainer.attr(
      'transform',
      `translate(${verticalMarkerXPosition},0)`
    )
  },
  drawHoverOverlay(context) {
    context.overlay = context.svg
      .select('.metadata-group')
      .append('rect')
      .attr('class', 'overlay')
      .attr('y1', 0)
      .attr('y2', context.height)
      .attr('height', context.getHeight)
      .attr('width', context.getWidth)
      .attr('fill', context.overlayColor)
      .style('display', 'none')
  },
  drawVerticalMarker(context) {
    context.verticalMarkerContainer = context.svg
      .select('.metadata-group')
      .append('g')
      .attr('class', 'hover-marker vertical-marker-container')
      .attr('transform', 'translate(9999, 0)')

    context.verticalMarkerLine = context.verticalMarkerContainer
      .selectAll('path')
      .data([
        {
          x1: 0,
          y1: 0,
          x2: 0,
          y2: 0,
        },
      ])
      .enter()
      .append('line')
      .classed('vertical-marker', true)
      .attr('x1', 0)
      .attr('y1', context.chartHeight)
      .attr('x2', 0)
      .attr('y2', 0)
  },
  drawStackedPointsOnFGraph(data, chartContext) {
    drawCirclesGroup(data)

    function drawCirclesGroup(data) {
      chartContext.circleContainer = chartContext.svg
        .select('.chart-group')
        .append('g')
      data.forEach(function(datum, index) {
        let temps = data[index]
        if (common.isValueArray(temps)) {
          temps.forEach(function(drrr) {
            drawCircle(drrr, index)
          })
        } else {
          datum.value.forEach(function(drrr) {
            chartContext.circleContainer
              .datum(datum)
              .append('circle')
              .attr('class', 'lineChart--circle')
              .attr('r', 0)
              .attr('cx', function(d) {
                return chartContext.xScale(d.label)
              })
              .attr('cy', function(d) {
                let val = d.value.find(v => v.label === drrr.label)
                return chartContext.yScale(val.value)
              })
              .style('fill', function(d) {
                return chartContext.categoryColorMap[drrr.label]
              })
              .transition()
              .delay((1500 / 10) * index)
              .attr('r', 4)
          })
        }
      })
    }
    function drawCircle(datum, index) {
      chartContext.circleContainer
        .datum(datum)
        .append('circle')
        .attr('class', 'lineChart--circle')
        .attr('r', 0)
        .attr('cx', function(d) {
          return chartContext.xScale(d.data.label)
        })
        .attr('cy', function(d) {
          return chartContext.yScale(d.data[d.subgroup])
        })
        .style('fill', function(d) {
          return chartContext.categoryColorMap[d.subgroup]
        })
        .transition()
        .delay((1500 / 10) * index)
        .attr('r', 4)
    }
  },
  drawPointsOnGraph(data, chartContext) {
    drawCircles(data)
    function drawCircles(data) {
      chartContext.circleContainer = chartContext.svg
        .select('.chart-group')
        .append('g')
        .attr('class', 'area-point')

      data.forEach(function(datum, index) {
        drawCircle(datum, index)
      })
    }
    function drawCircle(datum, index) {
      chartContext.circleContainer
        .datum(datum)
        .append('circle')
        .attr('class', 'lineChart--circle')
        .attr('r', 0)
        .attr('cx', function(d) {
          return chartContext.xScale(d.label)
        })
        .attr('cy', function(d) {
          return chartContext.yScale(d.value)
        })
        .transition()
        .delay((1500 / 10) * index)
        .attr('r', 6)
    }
  },
}
