import * as d3 from 'd3'
import tooltip from '@/graph/mixins/tooltip'
import formatter from 'charts/helpers/formatter'

export default {
  drawBool(chartContext) {
    let margin = chartContext.margin
    let width = chartContext.chartWidth
    let height = chartContext.chartHeight
    let data = chartContext.getData()
    let renderData = []
    data.forEach((currentValue, index, arr) => {
      if (index === 0) {
        renderData.push(currentValue)
      } else if (index + 1 === arr.length) {
        renderData.push(currentValue)
      } else if (currentValue.value !== arr[index - 1].value) {
        renderData.push(currentValue)
      }
    })
    let xscale = d3
      .scaleTime()
      .domain([renderData[0].label, renderData[renderData.length - 1].label])
      .range([0, width - (margin.right + margin.left)])
    let onColor = chartContext.options.onColor
      ? chartContext.options.onColor
      : '#aa70ae'
    let offColor = chartContext.options.offColor
      ? chartContext.options.offColor
      : '#F7F8F9'
    // chartContext.svg
    //   .attr('stroke', '#f7768c')
    //   .attr('stroke-width', 4)
    let container = chartContext.svg
      .append('g')
      .attr('width', width - (margin.right + margin.left))
      .attr('height', height - (margin.top + margin.bottom))
      .attr('transform', 'translate(' + margin.left + ',' + margin.bottom + ')')
    container
      .selectAll('rect-bool')
      .data(renderData)
      .enter()
      .append('rect')
      .attr('x', function(d, i) {
        return xscale(d.label)
      })
      .attr('y', function(d) {
        return 0
      })
      .attr('width', function(d, i) {
        let from = xscale(d.label)
        if (renderData.length === i + 1) {
          return chartContext.chartWidth - (margin.right + margin.left) - from
        } else {
          let to = xscale(renderData[i + 1].label)
          return to - from
        }
      })
      .attr('height', function(d, i) {
        return height
      })
      .attr('opacity', '0.5')
      .attr('fill', function(d) {
        return d.value === 0 ? offColor : onColor
      })
      .on('mouseover', function(d, i) {
        let processLabel = renderData[i + 1].label
          ? [d.label, renderData[i + 1].label]
          : d.label
        let tooltipData = [
          {
            label: chartContext.getOptions().xaxis.title,
            value: processLabel,
            axis: chartContext.getOptions().xaxis,
          },
          {
            label: chartContext.getOptions().y1axis.title,
            value: d.value,
            axis: chartContext.getOptions().y1axis,
          },
        ]
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          data: tooltipData,
          color: '#95ACD0',
        }
        tooltip.showTooltip(tooltipConfig, chartContext)
      })
      .on('mouseout', function(d, i) {
        tooltip.hideTooltip()
      })

    let xAxis = d3
      .axisBottom(xscale)
      .tickSize([0])
      .tickPadding(15)

    formatter.formatAxis(xAxis, chartContext.getOptions().xaxis)

    container
      .append('g')
      .attr('transform', 'translate(' + 0 + ',' + height + ')')
      .attr('class', 'x-axis-group axis')
      .call(xAxis)
      .append('text')
      .attr('x', width)
      .attr('y', 0)
      .attr('text-anchor', 'start')

    container
      .append('text')
      .attr('x', 20)
      .attr('y', height / 2)
      .text(chartContext.getOptions().chartname)
      .attr('fill', '#333333')
      .attr('font-size', '16px')
  },
}
