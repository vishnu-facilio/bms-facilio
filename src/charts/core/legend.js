import * as d3 from 'd3'

import textHelper from 'charts/helpers/text'

export default {
  dotme(text) {
    text.each(function() {
      let text = d3.select(this)
      let words = text.text().split(/\s+/)
      let texts = text.text()

      let ellipsis = text
        .text('')
        .append('tspan')
        .attr('class', 'elip')
        .text('...')
      let width = 73
      let numWords = words.length
      let tspan = text.insert('tspan', ':first-child').text(words.join(' '))

      while (tspan.node().getComputedTextLength() > width && words.length) {
        texts = texts.slice(0, -1)
        words.pop()
        tspan.text(words.join(' '))
      }
      if (words.length === numWords) {
        ellipsis.remove()
      }
    })
  },
  drawHorizontalLegend(legendContext) {
    let color = legendContext.getColorSchema()
    let margin = legendContext.margin,
      width = legendContext.getLayout().width,
      height = legendContext.getLayout().height
    let markerSize = 16,
      marginRatio = 1.5
    let textSize = 12
    let xOffset = markerSize
    let markerYOffset = -(textSize - 2) / 2
    let getLineElementMargin = marginRatio * markerSize
    let data = legendContext.getData()
    let chartHeight = height - margin.top - margin.bottom

    let svg = d3
      .select(legendContext.$refs['legend'])
      .append('svg')
      .classed('legend', true)
      .attr('width', width)
      .attr('height', 160)
    let container = svg
      .append('g')
      .classed('legend-container-group', true)
      .attr('transform', 'translate(0,-100)')
    container.append('g').classed('legend-group', true)
    svg
      .select('.legend-group')
      .append('g')
      .classed('legend-line', true)
    let entries = svg
      .select('.legend-line')
      .selectAll('g.legend-entry')
      .data(data)
    let lineHeight = chartHeight / 2

    // Enter
    entries
      .enter()
      .append('g')
      .classed('legend-entry', true)
      .attr('transform', function(d, i) {
        if (xOffset >= width) {
          xOffset = markerSize
          lineHeight = lineHeight + 30
        }
        let horizontalOffset = xOffset
        let verticalOffset = lineHeight,
          labelWidth = textHelper.getTextWidth(d.label)
        xOffset += markerSize + 2 + getLineElementMargin + labelWidth
        return 'translate(' + horizontalOffset + ',' + verticalOffset + ')'
      })
      .merge(entries)
      .append('circle')
      .classed('legend-circle', true)
      .attr('cx', markerSize / 2)
      .attr('cy', markerYOffset)
      .attr('r', markerSize / 2)
      .style('fill', function(d, i) {
        return color[i]
      })
      .style('stroke-width', 1)

    svg
      .select('.legend-group')
      .selectAll('g.legend-entry')
      .append('text')
      .classed('legend-entry-name', true)
      .text(function(d, i) {
        return d.label
      })
      .attr('x', getLineElementMargin)
      .style('font-size', '12px')
      .style('letter-spacing', '0.5px')

    // Exit
    svg
      .select('.legend-group')
      .selectAll('g.legend-entry')
      .exit()
      .transition()
      .style('opacity', 0)
      .remove()
    d3.selectAll('.legend-entry-name').call(this.dotme)

    let legendGroupSize =
      svg
        .select('g.legend-container-group')
        .node()
        .getBoundingClientRect().width + getLineElementMargin
    let emptySpace = width - legendGroupSize
    if (emptySpace > 0) {
      svg
        .select('g.legend-container-group')
        .attr('transform', 'translate(' + emptySpace / 2 + ', 0)')
    }
  },
}
