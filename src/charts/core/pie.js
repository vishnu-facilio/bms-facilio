import * as d3 from 'd3'
import textHelper from 'charts/helpers/text'
import tooltip from '@/graph/mixins/tooltip'
import common from '../helpers/common'
// import formatter from '../helpers/formatter'

export default {
  drawPie(chartObj) {
    this.prepareChart(chartObj)
    this.buildSvg(chartObj)
    this.buildLayout(chartObj)
    this.buildShape(chartObj)
    this.drawSlices(chartObj)
  },
  drawDoughnut(chartObj) {
    this.prepareChart(chartObj)
    this.buildSvg(chartObj)
    this.buildLayout(chartObj)
    this.buildShape(chartObj)
    this.drawSlices(chartObj)
  },
  drawCenterText(chartObj) {
    if (chartObj.options.centerText) {
      chartObj.text = chartObj.options.centerText
    }
    chartObj.legendWidth = chartObj.fchart.getHeight()
    chartObj.chartGroup
      .append('text')
      .attr('class', 'donut-text')
      .attr('dy', '.1em')
      .attr('text-anchor', 'middle')
      .text(function() {
        let value = chartObj.fchart.options.centerValue
        if (!isNaN(value)) {
          value = Number(chartObj.fchart.options.centerValue)
          value = value % 1 !== 0 ? d3.format('.2f')(value) : value
        }
        if (chartObj && chartObj.innerRadius > 0) {
          return value
        }
      })
      .attr('font-size', function() {
        if (chartObj && chartObj.innerRadius) {
          let size = chartObj.innerRadius / 2.2
          return size
        }
      })
    chartObj.chartGroup
      .append('text')
      .attr('class', 'donut-text')
      .attr('dy', '1.8em')
      .attr('text-anchor', 'middle')
      .text(function() {
        let label = chartObj.fchart.options.centerLabel
          ? chartObj.fchart.options.centerLabel
          : ''
        if (chartObj && chartObj.innerRadius > 0) {
          if (label.length > 15) {
            return label.slice(0, 14) + '...'
          } else {
            return label
          }
        }
      })
      .attr('font-size', function() {
        if (chartObj && chartObj.innerRadius > 0) {
          let size = chartObj.innerRadius / 5
          return size
        }
      })
  },
  wrapText(text, legendWidth, fontSize) {
    textHelper.wrapText.call(null, 0, fontSize, legendWidth, text.node())
  },
  deleteGraph() {
    let self = this
    let svg = d3.select(self.$refs['piechart']).transition()
    let legends = d3.select(self.$refs['legend']).transition()
    svg.selectAll('*').remove()
    legends.selectAll('*').remove()
    setTimeout(function() {
      self.render()
    }, 500)
  },
  buildSvg(chartObj) {
    chartObj.fchart.svg.data([chartObj.data])

    chartObj.container = chartObj.fchart.svg
      .append('g')
      .attr('class', 'container-group')
      .attr(
        'transform',
        'translate(' +
          (chartObj.fchart.chartWidth / 2 + chartObj.fchart.getMargin().left) +
          ',' +
          (chartObj.fchart.chartHeight / 2 + chartObj.fchart.getMargin().top) +
          ')'
      )

    chartObj.chartGroup = chartObj.container
      .append('g')
      .classed('chart-group', true)
  },
  buildLayout(chartObj) {
    chartObj.pieLayout = d3.pie().value(function(d) {
      return d.value
    })
    chartObj.temp = chartObj.r
  },
  buildShape(chartObj) {
    chartObj.shape = d3
      .arc()
      .innerRadius(chartObj.innerRadius)
      .outerRadius(function(d) {
        chartObj.maxPercentage1 = chartObj.maxPercentage1 - chartObj.diff
        chartObj.temp = chartObj.temp - chartObj.diff
        return chartObj.temp
      })
    chartObj.arcShape = d3
      .arc()
      .innerRadius(function(d) {
        return chartObj.innerRadius
      })
      .outerRadius(function(d) {
        chartObj.maxPercentage1 = chartObj.maxPercentage1 - chartObj.diff
        chartObj.temp = chartObj.temp - chartObj.diff
        return chartObj.temp + chartObj.temp / 10
      })

    chartObj.shapeOver = d3
      .arc()
      .innerRadius(chartObj.innerRadius)
      .outerRadius(function(d) {
        chartObj.maxPercentage1 = chartObj.maxPercentage1 - chartObj.diff
        chartObj.temp = chartObj.temp - chartObj.diff
        return chartObj.temp + 20
      })
    chartObj.OuterLayers = d3
      .arc()
      .innerRadius(chartObj.innerRadius + 30)
      .outerRadius(function(d) {
        chartObj.maxPercentage1 = chartObj.maxPercentage1 - chartObj.diff
        chartObj.temp = chartObj.temp - chartObj.diff
        return chartObj.temp + 30
      })
  },
  drawSlices(chartObj) {
    chartObj.categoryColorMap = chartObj.fchart.getLegends()

    chartObj.slices = chartObj.chartGroup
      .selectAll('g.arc')
      .data(chartObj.pieLayout)

    chartObj.newSlicesG = chartObj.slices
      .enter()
      .append('g')
      .classed('arc', true)
    chartObj.newSlices = chartObj.newSlicesG.append('path')
    chartObj.newSlices
      .merge(chartObj.slices)
      // .attr('fill', function (d, i) {
      //   return context.categoryColorMap[d.data.label]
      // })//old
      .attr('fill', function(d, i) {
        return chartObj.categoryColorMap[d.data.label]
      })
      .on('click', function(d, i) {
        if (chartObj.onclick) {
          chartObj.onclick([
            {
              data: d.data,
              axis: chartObj.options.xaxis,
            },
          ])
        }
      })
      .on('mousemove', function(d, i) {
        d3.select(this)
          .transition()
          .duration(400)
          .attr('d', chartObj.shapeOver)
        let tooltipData = [
          {
            label: chartObj.options.xaxis.title,
            value: d.data.label,
          },
          {
            label: chartObj.options.y1axis.title,
            value: d.data.formatted_value,
          },
          {
            label: 'Percentage',
            value: common.calculatePercent(d.data.value, chartObj.data),
            unit: '%',
          },
        ]
        let tooltipConfig = {
          position: {
            left: d3.event.pageX,
            top: d3.event.pageY,
          },
          data: tooltipData,
          color: chartObj.categoryColorMap[d.data.label],
        }
        tooltip.showTooltip(tooltipConfig, chartObj.fchart)
      })
      .on('mouseout', function(d) {
        d3.select(this)
          .transition()
          .duration(400)
          .attr('d', chartObj.shape)
        tooltip.hideTooltip()
      })
      .attr('d', chartObj.shape)
      .transition()
      .ease(d3.easeCubicInOut)
      .duration(500)
      .attrTween('d', function(d) {
        let i
        i = d3.interpolate({ startAngle: 0, endAngle: 0 }, d)
        return function(t) {
          return chartObj.shape(i(t))
        }
      })
    if (chartObj.options.arcSliceEnable) {
      chartObj.newSlicesG
        .append('text')
        .text(function(d) {
          let value
          if (d.endAngle - d.startAngle > 0.2) {
            let showFormat = 'percent'
            if (chartObj.options.arcSliceEnable) {
              showFormat = chartObj.options.arcSliceValueFormat
                ? chartObj.options.arcSliceValueFormat
                : 'percent'
            }
            if (d && showFormat) {
              if (showFormat === 'value') {
                value = d.data.formatted_value
              } else if (showFormat === 'percent') {
                value =
                  common.calculatePercent(d.data.value, chartObj.data) + ' %'
              } else {
                value =
                  common.calculatePercent(d.data.value, chartObj.data) + ' %'
              }
            }
            return value
          }
        })
        .attr('transform', function(d) {
          d.outerRadius = chartObj.innerRadius // Set Outer Coordinate
          d.innerRadius = chartObj.innerRadius // Set Inner Coordinate
          return 'translate(' + chartObj.arcShape.centroid(d) + ')'
        })
        .attr('fill', '#fff')
    }
    if (chartObj.options.innerText) {
      this.drawCenterText(chartObj)
    }
  },
  drawLabel(chartObj) {
    let cDim = chartObj.temp + 30
    chartObj.container.append('g').attr('class', 'labelName')
    let enteringLabels = chartObj.container
      .select('.labelName')
      .selectAll('.label')
      .data(chartObj.pieLayout)
      .enter()
    let labelGroups = enteringLabels.append('g').attr('class', 'label')

    let lines = labelGroups.append('line').attr({
      x1: function(d, i) {
        return chartObj.shape.centroid(d)[0]
      },
      y1: function(d) {
        return chartObj.shape.centroid(d)[1]
      },
      x2: function(d) {
        let centroid = chartObj.shape.centroid(d),
          midAngle = Math.atan2(centroid[1], centroid[0])
        return Math.cos(midAngle) * cDim
      },
      y2: function(d) {
        let centroid = chartObj.shape.centroid(d),
          midAngle = Math.atan2(centroid[1], centroid[0])
        return Math.sin(midAngle) * cDim
      },

      class: 'label-line',
      stroke: '#000',
    })

    let textLabels = labelGroups
      .append('text')
      .attr({
        x: function(d, i) {
          let centroid = chartObj.shape.centroid(d),
            midAngle = Math.atan2(centroid[1], centroid[0]),
            x = Math.cos(midAngle) * cDim,
            sign = x > 0 ? 1 : -1
          return x + 5 * sign
        },

        y: function(d, i) {
          let centroid = chartObj.shape.centroid(d),
            midAngle = Math.atan2(centroid[1], centroid[0]),
            y = Math.sin(midAngle) * cDim
          return y
        },

        'text-anchor': function(d, i) {
          let centroid = chartObj.shape.centroid(d),
            midAngle = Math.atan2(centroid[1], centroid[0]),
            x = Math.cos(midAngle) * cDim.labelRadius
          return x > 0 ? 'start' : 'end'
        },

        class: 'label-text',
      })
      .text(function(d) {
        return d.data.name + ' ( ' + d.data.pct + ' ) '
      })

    // relax the label!
    let alpha = 0.5,
      spacing = 15

    function relax() {
      let again = false
      textLabels.each(function(d, i) {
        let a = this,
          da = d3.select(a),
          y1 = da.attr('y')
        textLabels.each(function(d, j) {
          let b = this
          if (a === b) {
            return
          }

          let db = d3.select(b)
          if (da.attr('text-anchor') !== db.attr('text-anchor')) {
            return
          }

          let y2 = db.attr('y')
          let deltaY = y1 - y2

          if (Math.abs(deltaY) > spacing) {
            return
          }

          again = true
          let sign = deltaY > 0 ? 1 : -1
          let adjust = sign * alpha
          da.attr('y', +y1 + adjust)
          db.attr('y', +y2 - adjust)
        })
      })

      if (again) {
        let labelElements = textLabels[0]
        lines.attr('y2', function(d, i) {
          let labelForLine = d3.select(labelElements[i])
          return labelForLine.attr('y')
        })
        setTimeout(relax, 20)
      }
    }

    relax()
  },
  drawMapLabel(chartObj) {
    chartObj.container.append('g').attr('class', 'labelName')
    chartObj.container.append('g').attr('class', 'lines')
    chartObj.container
      .select('.labelName')
      .selectAll('text')
      .data(chartObj.pieLayout(chartObj.data))
      .enter()
      .append('text')
      .attr('text-anchor', 'middle')
      .attr('transform', function(d, i) {
        let pos = chartObj.OuterLayers.centroid(d)
        pos[0] = chartObj.temp * 1.55 * (midAngle(d) < Math.PI ? 1 : -1)
        pos[1] = pos[1] + i * 3
        return 'translate(' + pos + ')'
      })
      .attr('dy', '.35em')
      .text(function(d) {
        if (d.endAngle - d.startAngle < (4 * Math.PI) / 180) {
          return ''
        }
        return d.data.label
      })
      .style('text-anchor', function(d) {
        // if slice centre is on the left, anchor text to start, otherwise anchor to end
        return midAngle(d) < Math.PI ? 'start' : 'end'
      })
      .text(function(d) {
        return d.data.label
      })
      .each(function(d) {
        let bbox = this.getBBox()
        d.sx = d.x - bbox.width / 2 - 2
        d.ox = d.x + bbox.width / 2 + 2
        d.sy = d.oy = d.y + 5
      })
    function midAngle(d) {
      return d.startAngle
    }
    let prev
    let text = chartObj.container.select('.labelName').selectAll('text')
    text.each(function(d, i) {
      if (i > 0) {
        let thisbb = this.getBoundingClientRect(),
          prevbb = prev.getBoundingClientRect()
        if (
          !(
            thisbb.right < prevbb.left ||
            thisbb.left > prevbb.right ||
            thisbb.bottom < prevbb.top ||
            thisbb.top > prevbb.bottom
          )
        ) {
          let ctx = thisbb.left + (thisbb.right - thisbb.left) / 2,
            cty = thisbb.top + (thisbb.bottom - thisbb.top) / 2,
            cpx = prevbb.left + (prevbb.right - prevbb.left) / 2,
            cpy = prevbb.top + (prevbb.bottom - prevbb.top) / 2,
            off = Math.sqrt(Math.pow(ctx - cpx, 2) + Math.pow(cty - cpy, 2))
          d3.select(this).attr(
            'transform',
            'translate(' +
              Math.sin((d.startAngle + d.endAngle - Math.PI) / 2) *
                (chartObj.temp + 20 + off) +
              ',' +
              Math.cos((d.startAngle + d.endAngle - Math.PI) / 2) *
                (chartObj.temp + 20 + off) +
              ')'
          )
        }
      }
      prev = this
    })
    let polyline = chartObj.container
      .select('.lines')
      .selectAll('polyline')
      .data(chartObj.pieLayout(chartObj.data), function(d) {
        return d.data.label
      })
      .enter()
      .append('polyline')
      .attr('points', function(d, i) {
        let pos = chartObj.OuterLayers.centroid(d)
        pos[0] = chartObj.temp * 1.55 * (midAngle(d) < Math.PI ? 1 : -1)
        pos[1] = pos[1] + i * 3
        return [
          chartObj.shape.centroid(d),
          chartObj.OuterLayers.centroid(d),
          pos,
        ]
      })

    polyline.enter().append('polyline')

    polyline.exit().remove()
  },
  prepareChart(chartObj) {
    chartObj.diff = chartObj.options.diff
    chartObj.innerRadius = chartObj.options.innerRadius
    let chartType = chartObj.options.type
    if (chartType === 'pie') {
      chartObj.innerRadius = 0
      chartObj.diff = 0
    } else if (chartType === 'doughnut') {
      chartObj.innerRadius = chartObj.fchart.getHeight() / 4
      chartObj.diff = 0
    } else if (chartType === 'gear') {
      chartObj.innerRadius = chartObj.fchart.getHeight() / 4
    }
    chartObj.r = chartObj.fchart.chartHeight / 2
    chartObj.selector = chartObj.fchart.$refs['f-chart']

    chartObj.maxPercentage1 = chartObj.r + chartObj.data.length * chartObj.diff
  },
}
