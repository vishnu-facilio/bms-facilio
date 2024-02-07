import * as d3 from 'd3'
export default {
  generate(chartContext) {
    if (chartContext.data.value) {
      let svg = chartContext.svg
      let textwidth = this.getTextWidth(
        chartContext.data.value + ' ' + chartContext.data.unit
      )
      let colors = chartContext.data.color.split(',')
      colors[0] = colors.length && colors[0] ? colors[0] : '#f87a60'
      colors[1] = colors.length && colors[1] ? colors[1] : '#9c5fb8'
      let segmentWidth = 100
      let width = 100,
        height = 100,
        fontSize = Math.min(width, height) / 4
      // let font = (chartContext.data.value && chartContext.data.currentValue) ? ((chartContext.data.value.length > 3 || chartContext.data.currentValue.length) ? (fontSize - (fontSize / chartContext.data.value.length)) : fontSize) : 0
      let percentage =
        chartContext.data.value && chartContext.data.currentValue
          ? (((chartContext.data.currentValue / chartContext.data.value) *
              100) /
              width) *
            100
          : 0
      let lastpercentage =
        chartContext.data.value && chartContext.data.existingvalue
          ? (((chartContext.data.existingvalue / chartContext.data.value) *
              100) /
              width) *
            100
          : 0
      let colorScale = d3
        .scaleLinear()
        .domain([0, Number(chartContext.data.value)])
        .range([colors[1], colors[0]])
      chartContext.defs = svg.append('defs')
      chartContext.gradient = chartContext.defs
        .append('linearGradient')
        .attr('id', 'svgGradient')
        .attr('x1', '100%')
        .attr('x2', '0%')
        .attr('y1', '100%')
        .attr('y2', '100%')

      chartContext.gradient
        .append('stop')
        .attr('class', 'start')
        .attr('offset', '0%')
        .attr('stop-color', function() {
          return colors.length && colors[0] ? colors[0] : '#f87a60'
        })
        .attr('stop-opacity', 1)

      chartContext.gradient
        .append('stop')
        .attr('class', 'end')
        .attr('offset', 20 * 100 + '%')
        .attr('stop-color', function() {
          return colors.length && colors[1] ? colors[1] : '#9c5fb8'
        })
        .attr('stop-opacity', 1)
      svg
        .append('rect')
        .attr('class', 'bg-rect')
        .attr('rx', 1.5)
        .attr('ry', 1.5)
        .attr('fill', '#f6f6f6')
        .attr('height', 3)
        .attr('width', function() {
          return segmentWidth * 1
        })
        .attr('x', 0)
      chartContext.svg
        .append('text')
        .attr('text-anchor', 'middle')
        .style('font-size', function() {
          let font = fontSize / 2 - fontSize / 4
          if (chartContext.data.value.toString().length > 2) {
            font = font - (font / chartContext.data.value.toString().length - 1)
            return font + 'px'
          } else {
            return font + 'px'
          }
        })
        .attr('dy', fontSize / 2 - 3)
        .attr('dx', function() {
          return 103 - textwidth
        })
        .style('text-transform', 'uppercase')
        .text(function() {
          return chartContext.data.currentValue
        })
        .style('fill', colorScale(Number(chartContext.data.currentValue)))

      chartContext.svg
        .append('text')
        .attr('text-anchor', 'middle')
        .style('font-size', function() {
          let font = fontSize / 2 - fontSize / 4
          if (chartContext.data.value.toString().length > 2) {
            font = font - (font / chartContext.data.value.toString().length - 1)
            return font + 'px'
          } else {
            return font + 'px'
          }
        })
        .attr('dy', fontSize / 2 - 3)
        .attr('dx', function() {
          return 115 - textwidth
        })
        .style('text-transform', 'uppercase')
        .text(function() {
          return '/' + chartContext.data.value + ' ' + chartContext.data.unit
        })
        .style('fill', '#666666')

      let progress = svg
        .append('rect')
        .attr('class', 'progress-rect')
        .style('fill', 'url(#svgGradient)')

        .attr('height', 3)
        .attr('width', lastpercentage || 0)
        .attr('rx', 1.5)
        .attr('ry', 1.5)
        .attr('x', 0)

      progress
        .transition()
        .duration(1000)
        .attr('width', function() {
          return percentage
        })
    }
  },
  getTextWidth(text) {
    // re-use canvas object for better performance
    let textWidthCanvas = null
    let canvas =
      textWidthCanvas || (textWidthCanvas = document.createElement('canvas'))
    let context = canvas.getContext('2d')
    let metrics = context.measureText(text)
    return metrics.width
  },
}
