import * as d3 from 'd3'
import formatter from 'charts/helpers/formatter'

export default {
  drawProgress(chartObj) {
    if (chartObj.fchart.$refs['f-chart']) {
      d3.select(chartObj.fchart.$refs['f-chart'])
        .selectAll('*')
        .remove()
    }
    let charts = []
    let count
    if (chartObj.data.length > 7) {
      count = 7
    } else {
      count = chartObj.data.length
    }
    for (let i = 0; i < count; i++) {
      charts.push(chartObj.data[i])
    }
    chartObj.selector = chartObj.fchart.$refs['f-chart']
    chartObj.arcColorFn = d3.interpolateHsl(
      d3.rgb(chartObj.options.gColors[0]),
      d3.rgb(chartObj.options.gColors[1])
    )
    chartObj.progressContianer = d3
      .select(chartObj.selector)
      .attr('class', 'fchart-group')
      .selectAll('.fchart div')
      // .attr('class', 'fchart')
      .data(charts)
    chartObj.progressContianerBack = chartObj.progressContianer
      .enter()
      .append('div')
      .attr('class', 'probar-background')
    chartObj.progressContianerRow = chartObj.progressContianerBack
      .selectAll('.probar-background')
      .append('div')
      .attr('class', 'probar-data row')

    // progress label value and units elements
    chartObj.progressTextContent = chartObj.progressContianerBack
      .append('div')
      .attr('class', 'col-9 title-div')
    chartObj.progressTextContent
      .append('span')
      .attr('class', 'title-label')
      // .append('span')
      .text(function(d, i) {
        return formatter.formatValue(d.label, chartObj.options.xaxis)
      })
    chartObj.progressTextContent
      .append('span')
      .attr('class', 'title-value')
      .html(function(d, i) {
        return d.formatted_value
      })
    let sumOfValue = d3.max(charts, function(d) {
      return d.value
    })
    chartObj.progressContianerBack
      .append('div')
      .append('div')
      .attr('class', 'progbar-shadow')
      .attr('width', chartObj.fchart.getWidth())
      .append('div')
      .attr('class', 'progbar-fill')
      .transition()
      .delay(function(d, i) {
        return i * 200
      })
      .duration(1000)
      .style('width', function(d, i) {
        return (d.value / sumOfValue) * 100 + '%'
      })
      .style('background-color', function(d, i) {
        // eslint-disable-next-line no-loss-of-precision
        return chartObj.arcColorFn(0.12333333333333333 * i)
      })

    chartObj.progressContianerBack
      .selectAll('.probar-background')
      .append('div')
      .attr('class', 'progbar-shadow')
      .attr('width', chartObj.fchart.getWidth())
  },
}
