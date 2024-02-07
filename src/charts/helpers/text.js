import * as d3 from 'd3'
export default {
  wrapText(xOffset, fontSize, availableWidth, node) {
    const wrapConfig = {
      lineHeight: 1.2,
      smallTextOffset: 10,
      smallTextLineHeightRatio: 0.9,
      smallTextRatio: 0.6,
      valueClassName: 'value',
      labelClassName: 'label',
    }
    let text = d3.select(node),
      words = text
        .text()
        .split(/\s+/)
        .reverse(),
      word,
      line = [],
      lineNumber = 0,
      smallLineHeight =
        wrapConfig.lineHeight * wrapConfig.smallTextLineHeightRatio,
      y = text.attr('y'),
      dy = parseFloat(text.attr('dy')),
      smallFontSize = fontSize * wrapConfig.smallTextRatio,
      tspan = text
        .text(null)
        .append('tspan')
        .attr('x', xOffset)
        .attr('y', y - 5)
        .attr('dy', dy + 'em')
        .classed(wrapConfig.valueClassName, true)
        .style('font-size', fontSize + 'px')
    tspan.text(words.pop())
    tspan = text
      .append('tspan')
      .classed(wrapConfig.labelClassName, true)
      .attr('x', xOffset)
      .attr('y', y + wrapConfig.smallTextOffset)
      .attr('dy', ++lineNumber * smallLineHeight + dy + 'em')
      .style('font-size', smallFontSize + 'px')

    while ((word = words.pop())) {
      line.push(word)
      tspan.text(line.join(' '))
      if (
        tspan.node() &&
        tspan.node().getComputedTextLength() > availableWidth - 50
      ) {
        line.pop()
        tspan.text(line.join(' '))
        line = [word]
        tspan = text
          .append('tspan')
          .classed(wrapConfig.labelClassName, true)
          .attr('x', xOffset)
          .attr('y', y + wrapConfig.smallTextOffset)
          .attr('dy', ++lineNumber * smallLineHeight + dy + 'em')
          .text(word)
          .style('font-size', smallFontSize + 'px')
      }
    }
  },
  wrapEllipse(xOffset, fontSize, availableWidth, node) {
    let texts = d3.select(node),
      textLength = texts.node().getComputedTextLength(),
      text = self.text()
    while (textLength > 100 - 2 * 16 && text.length > 0) {
      text = text.slice(0, -1)
      texts.text(text + '...')
      textLength = texts.node().getComputedTextLength()
    }
  },
  getTextWidth(text) {
    const fontSize = 12
    const fontFace = 'Aktiv-Grotesk'
    let a = document.createElement('canvas'),
      b = a.getContext('2d')
    b.font = fontSize + 'px ' + fontFace
    return b.measureText(text).width
  },
  wrapTextWithEllipses(text, width, xpos = 0, limit = 2) {
    text.each(function() {
      let words, word, line, lineNumber, lineHeight, y, dy, tspan

      text = d3.select(this)

      words = text
        .text()
        .split(/\s+/)
        .reverse()
      line = []
      lineNumber = 0
      lineHeight = 1.2
      y = text.attr('y')
      dy = parseFloat(text.attr('dy'))
      tspan = text
        .text(null)
        .append('tspan')
        .attr('x', xpos)
        .attr('y', y)

      while ((word = words.pop())) {
        line.push(word)
        tspan.text(line.join(' '))

        if (tspan.node() && tspan.node().getComputedTextLength() > width) {
          line.pop()
          tspan.text(line.join(' '))

          if (lineNumber < limit - 1) {
            line = [word]
            tspan = text
              .append('tspan')
              .attr('x', xpos)
              .attr('y', y)
              .attr('dy', ++lineNumber * lineHeight + dy + 'em')
              .text(word)
            // if we need two lines for the text, move them both up to center them
            text.classed('adjust-upwards', true)
          } else {
            line.push('...')
            tspan.text(line.join(' '))
            break
          }
        }
      }
    })
  },
}
