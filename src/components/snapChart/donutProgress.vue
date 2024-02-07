<template>
  <div
    class="progressParent"
    :class="className"
    :style="{ width: width + '%' }"
  >
    <div id="donutProgress"></div>
  </div>
</template>
<script>
import * as d3 from 'd3'

export default {
  props: ['percent', 'className', 'width'],
  mounted() {
    this.drawChrat()
  },
  methods: {
    drawChrat() {
      let percentage = (this.percent ? this.percent : 0) / 100
      let tau = 2 * Math.PI,
        width = 100,
        height = 100,
        outerRadius = Math.min(width, height) / 2,
        innerRadius = (outerRadius / 5) * 4.2,
        fontSize = Math.min(width, height) / 4

      let arc = d3
        .arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .cornerRadius(outerRadius - innerRadius)
        .startAngle(0)

      let svg = d3
        .select('#donutProgress')
        .append('svg')
        .attr('width', '100%')
        .attr('height', '100%')
        .attr(
          'viewBox',
          '0 0 ' + Math.min(width, height) + ' ' + Math.min(width, height)
        )
        .attr('preserveAspectRatio', 'xMinYMin')
        .append('g')
        .attr(
          'transform',
          'translate(' +
            Math.min(width, height) / 2 +
            ',' +
            Math.min(width, height) / 2 +
            ')'
        )

      let text = svg
        .append('text')
        .text('0%')
        .attr('text-anchor', 'middle')
        .style('font-size', fontSize + 'px')
        .style('fill', '#333')
        .attr('dy', fontSize / 3)
        .attr('dx', 2)

      let defs = svg.append('defs')

      let gradient = defs
        .append('linearGradient')
        .attr('id', 'svgGradient')
        .attr('x1', '0%')
        .attr('x2', '100%')
        .attr('y1', '0%')
        .attr('y2', '100%')

      gradient
        .append('stop')
        .attr('class', 'start')
        .attr('offset', '0%')
        .attr('stop-color', '#ffaacc')
        .attr('stop-opacity', 1)

      gradient
        .append('stop')
        .attr('class', 'end')
        .attr('offset', percentage * 100 + '%')
        .attr('stop-color', '#ff3184')
        .attr('stop-opacity', 1)

      svg
        .append('path')
        .datum({ endAngle: tau })
        .style('fill', '#f7f8f9')
        .attr('d', arc)

      let midground = svg
        .append('path')
        .datum({ endAngle: 0 * tau })
        .style('fill', 'url(#svgGradient)')
        .attr('d', arc)

      midground
        .transition()
        .duration(750)
        .call(arcTween, percentage * tau)
      function arcTween(transition, newAngle) {
        transition.attrTween('d', function(d) {
          let interpolate = d3.interpolate(d.endAngle, newAngle)

          return function(t) {
            d.endAngle = interpolate(t)

            text.text(Math.round((d.endAngle / tau) * 100) + '%')

            return arc(d)
          }
        })
      }
    },
  },
}
</script>
<style>
text.progress-center-text {
  font-size: 2em;
  color: #000;
}
.progressParent {
  margin: auto;
}
</style>
