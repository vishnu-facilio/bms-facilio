import * as d3 from 'd3'
export default {
  generate(chartContext) {
    {
      let data = chartContext.data
      let percentage =
        data.value && data.currentValue ? data.currentValue / data.value : 0
      let prepercent =
        data.value && data.existingvalue ? data.existingvalue / data.value : 0
      let colors = chartContext.data.color.split(',')
      let tau = (1 + 5 / 9) * Math.PI,
        width = 100,
        height = 100,
        outerRadius = Math.min(width, height) / 2,
        innerRadius = (outerRadius / 5) * 4.6,
        fontSize = Math.min(width, height) / 4

      chartContext.arc = d3
        .arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .cornerRadius(outerRadius - innerRadius)
        .startAngle(0)

      let svg = chartContext.svg

      chartContext.text = svg
        .append('text')
        .text('0%')
        .attr('text-anchor', 'middle')
        .style('font-size', fontSize + 'px')
        .style('fill', '#333')
        .attr('dy', -(fontSize / 4))
        .attr('dx', 2)

      chartContext.svg
        .append('text')
        .attr('text-anchor', 'middle')
        .style('font-size', fontSize / 2 - fontSize / 4 + 'px')
        .style('fill', '#333')
        .attr('dy', fontSize / 4)
        .attr('dx', 2)
        .style('text-transform', 'uppercase')
        .text(function() {
          let lable1 = data.centerText[0] ? data.centerText[0].label : ''
          return lable1.length > 14 ? lable1.slice(0, 14) + '...' : lable1
        })
        .style('fill', '#f6766b')
        .style('font-weight', 'bold')
        .style('letter-spacing', ' 0.6px')

      chartContext.svg
        .append('text')
        .attr('text-anchor', 'middle')
        .style('font-size', fontSize / 2 - fontSize / 5 + 'px')
        .style('fill', '#333')
        .attr('dy', fontSize / 1.5)
        .attr('dx', 2)
        .style('text-transform', 'uppercase')
        .text(function() {
          let lable2 = data.centerText[1] ? data.centerText[1].label : ''
          return lable2.length > 14 ? lable2.slice(0, 14) + '...' : lable2
        })
        .style('fill', '#666666')
        .style('font-weight', 'bold')
        .style('letter-spacing', ' 0.3px')

      chartContext.defs = svg.append('defs')

      chartContext.gradient = chartContext.defs
        .append('linearGradient')
        .attr('id', 'svgGradient')
        .attr('x1', '100%')
        .attr('x2', '0%')
        .attr('y1', '0%')
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
        .append('path')
        .datum({ endAngle: tau })
        .style('fill', '#f7f8f9')
        .attr('d', chartContext.arc)

      chartContext.midground = svg
        .append('path')
        .datum({ endAngle: (prepercent || 0) * tau })
        .style('fill', 'url(#svgGradient)')
        .attr('d', chartContext.arc)

      let arcTween = function arcTween(transition, newAngle) {
        transition.attrTween('d', function(d) {
          let interpolate = d3.interpolate(d.endAngle, newAngle)

          return function(t) {
            d.endAngle = interpolate(t)

            // chartContext.text.text(Math.round((d.endAngle / tau) * 100) + '%') existingvalue
            chartContext.text.text(
              data.currentValue + ' ' + (data.unit ? data.unit : '')
            )

            return chartContext.arc(d)
          }
        })
      }
      chartContext.midground
        .transition()
        .duration(1000)
        .call(arcTween, percentage * tau)
    }
  },
}
