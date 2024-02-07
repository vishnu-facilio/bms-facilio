import * as d3 from 'd3'
// import { select } from 'd3-selection'
const d3Brush = require('d3-brush')
import formatter from 'charts/helpers/formatter'
// const d3Time = require('d3-time')
// import common from 'charts/helpers/common'
const d3Selection = require('d3-selection')
// import container from 'charts/core/container'
export default {
  buildContainerGroups(chartContext) {
    chartContext.chartHeightBrush = 60
    chartContext.svgBrush = d3
      .select(chartContext.brushContainer)
      .append('svg')
      .classed('fbrush', true)
    chartContext.svgBrush
      .attr('width', chartContext.getWidth())
      .attr('height', chartContext.chartHeightBrush)
    chartContext.containerBrush = chartContext.svgBrush
      .append('g')
      .classed('container-group-brush', true)
      .attr('transform', `translate(${chartContext.getMargin().left}, -20)`)

    chartContext.containerBrush.append('g').classed('chart-group-brush', true)
    // .attr('transform', `translate(0, ${chartContext.chartHeightBrush})`)
    chartContext.containerBrush
      .append('g')
      .classed('metadata-group-brush', true)
    chartContext.containerBrush.append('g').classed('x-axis-group-brush', true)
    chartContext.containerBrush.append('g').classed('brush-group-brush', true)
  },
  drawArea(chartContext) {
    // Create and configure the area generator
    chartContext.areas = d3
      .area()
      .x(function(d) {
        return chartContext.x2Scale(d.label)
      })
      .y0(function(d) {
        return chartContext.y2Scale(d.value)
      })
      .y1(function(d) {
        return chartContext.chartHeightBrush
      })

    // Create the area path
    chartContext.svgBrush
      .select('.chart-group-brush')
      .append('rect')
      .attr('x', 0)
      .attr('y', 20)
      .attr('height', chartContext.chartHeightBrush)
      .attr(
        'width',
        chartContext.getWidth() -
          chartContext.getMargin().left -
          chartContext.getMargin().right
      )
      .attr('fill', '#000')
      .attr('stroke', '#000')
      .attr('fill-opacity', '0.005')
      .classed('brush-group-brush-rect', true)

    chartContext.svgBrush
      .select('.chart-group-brush')
      .append('path')
      .datum(chartContext.getData())
      .attr('class', 'brush-area')
      .attr('d', chartContext.areas)
  },
  drawSingleArea(chartContext) {
    chartContext.xOrgScale = chartContext.xScale
    chartContext.xAxis2 = d3
      .axisBottom(chartContext.x2Scale)
      .tickFormat(formatter.multiFormat)
    this.buildContainerGroups(chartContext)
    this.drawAxis(chartContext)
    this.drawArea(chartContext)
    this.buildBrush(chartContext)
    this.drawBrush(chartContext)
    this.drawHandles(chartContext)
  },
  buildBrush(chartContext) {
    chartContext.brush = d3Brush
      .brushX()
      .extent([
        [0, 0],
        [chartContext.chartWidth, chartContext.chartHeight],
      ])
      // .on('brush', this.handleBrushStart(chartContext))
      // .on('end', this.handleBrushEnd(chartContext))
      .on('brush end', this.handleBrushEnd(chartContext))
    chartContext.zoom = d3
      .zoom()
      .scaleExtent([1, Infinity])
      .translateExtent([
        [0, 0],
        [chartContext.chartWidth, chartContext.chartHeight],
      ])
      .extent([
        [0, 0],
        [chartContext.chartWidth, chartContext.chartHeight],
      ])
      .on('zoom', this.zoomed(chartContext))
  },
  zoomed(chartContext) {
    if (
      d3Selection.event &&
      d3Selection.event.sourceEvent &&
      d3Selection.event.sourceEvent.type === 'brush'
    )
      return // ignore zoom-by-brush

    if (!d3Selection.event) {
      return
    }
    let t = d3Selection.event.transform
    chartContext.xScale.domain(t.rescaleX(chartContext.x2Scale).domain())
    chartContext.svg.select('.layer').attr('d', chartContext.area)
    chartContext.svg.select('.x-axis-group axis').call(chartContext.xAxis)
    chartContext
      .select('.brush')
      .call(
        chartContext.brush.move,
        chartContext.xScale.range().map(t.invertX, t)
      )
  },
  handleBrushEnd(chartContext) {
    if (!d3Selection.event) {
      return
    } // Only transition after input.
    if (!d3Selection.event) {
      return
    }

    if (d3.event.sourceEvent && d3.event.sourceEvent.type === 'end') return
    let s = d3.event.selection

    chartContext.xScale.domain(
      s.map(chartContext.x2Scale.invert, chartContext.x2Scale)
    )
    let dataFiltered = []
    chartContext.orgData = chartContext.getData()
    chartContext.orgData.filter(function(d, i) {
      if (
        d.label >= chartContext.xScale.domain()[0] &&
        d.label <= chartContext.xScale.domain()[1]
      ) {
        dataFiltered.push(d)
      }
    })

    chartContext.svg
      .select('.x-axis-group')
      .call(chartContext.xAxis.scale(chartContext.xScale))
    chartContext.svg
      .select('.y-axis-group')
      .call(chartContext.yAxis.scale(chartContext.yScale))
    chartContext.svg.select('.line-group').attr('d', function(d, i) {
      return chartContext.lineContainer(dataFiltered)
    })
    chartContext.svg.select('.layer').attr('d', function(d, i) {
      return chartContext.area(dataFiltered)
    })
    chartContext.svg.select('.area-outline').attr('d', function(d, i) {
      return chartContext.areaOutline(dataFiltered)
    })
  },
  drawAxis(chartContext) {
    chartContext.svgBrush
      .select('.x-axis-group-brush')
      .append('g')
      .attr('class', 'x axis')
      .attr('transform', `translate(0, ${chartContext.chartHeightBrush})`)
      .call(chartContext.xAxis2)
  },
  drawHandles(chartContext) {
    // Styling
    chartContext.handle = chartContext.chartBrush.selectAll(
      '.handle.brush-rect'
    )
  },
  drawBrush(chartContext) {
    chartContext.chartBrush = d3
      .select('.brush-group-brush')
      .append('g')
      .attr('class', 'brush-container')
      .call(chartContext.brush)
      .call(chartContext.brush.move, chartContext.xScale.range())

    // Update the height of the brushing rectangle
    chartContext.chartBrush
      .selectAll('rect')
      .classed('brush-rect', true)
      .attr('height', chartContext.chartHeightBrush)

    chartContext.chartBrush
      .selectAll('.selection')
      .attr('fill-opacity', 0.08)
      .attr('fill', `url(#3)`)
  },
}
