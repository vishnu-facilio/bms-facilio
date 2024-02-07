<template>
  <div
    id="solidGuage"
    ref="multigauge"
    class="gauge solidgauge"
    :style="{ 'max-width': zoom + '%', 'max-height': zoom + '%' }"
  ></div>
</template>
<script>
import * as d3 from 'd3'
import tooltip from '@/graph/mixins/cardtooltip'
export default {
  mixins: [tooltip],
  props: [
    'height',
    'width',
    'data',
    'maxValue',
    'centerText',
    'styles',
    'editMode',
    'centerTextJson',
  ],
  data() {
    let now = new Date()
    return {
      zoom: 100,
      innerWidth: 100,
      innerHeight: 100,
      type: 'multigauge',
      id: Math.random().toFixed(2) * 100,
    }
  },
  watch: {
    data: {
      handler: 'regen',
      deep: true,
    },
  },
  created() {
    window.addEventListener('resize', this.handleResize)
  },
  computed: {
    getPathWidth() {
      let dataLength = this.gaugeData.length >= 5 ? this.gaugeData.length : 5
      return dataLength * 0.28
    },
    gaugeData() {
      let data = []
      let { maxValue } = this
      this.data.forEach((rt, index) => {
        let d = {
          text: rt.text,
          lable: rt.lable,
          label: rt.lable,
          value: this.valueToPercentage(Number(rt.value), Number(maxValue)),
          index: Number((index / 10 + 0.2).toFixed(1)),
          color: rt.color,
          textColor: rt.textColor,
          showPathtext: rt.showPathtext,
          tooltipData: this.getTooltipData(rt),
        }
        data.push(d)
      })
      return data.reverse()
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.handleResize()
    })
  },
  methods: {
    getTooltipData(data) {
      return {
        label: data.lable,
        value: data.formatedValue,
        unit: data.unit,
      }
    },
    valueToPercentage(value, maxvalue) {
      if (value && maxvalue) {
        let scale = d3
          .scaleLinear()
          .range([0, 1])
          .domain([0, maxvalue])
        let val = scale(value)
        return val > 1 ? 1 : val
      }
    },
    handleResize() {
      if (this.$el) {
        this.innerWidth = this.$el.clientWidth
        this.innerHeight = this.$el.clientHeight || 100
      }
      this.zoom =
        window.devicePixelRatio > 1
          ? Math.round(window.devicePixelRatio * 50)
          : Math.round(window.devicePixelRatio * 100)
      this.regen()
    },
    regen() {
      if (this.$refs['multigauge']) this.$refs['multigauge'].innerHTML = ''
      this.$nextTick(() => {
        this.generate()
      })
    },
    generate() {
      if (this.$refs['multigauge']) this.$refs['multigauge'].innerHTML = ''
      let chartContext = {}
      let self = this
      let { gaugeData, getPathWidth, innerWidth, innerHeight, id } = this
      this.id = Math.random().toFixed(2) * 100
      chartContext.id = this.id
      chartContext.selecter = this.$refs['multigauge']

      let endAngle = this.degToRad(270)
      let centerAngle = this.degToRad(360)
      let width = innerWidth,
        height = innerHeight,
        radius = Math.min(width, height) / getPathWidth,
        spacing = 0.09
      let minDimension = Math.min(width, height)
      chartContext.fontSize = (2 * minDimension) / 100
      let color = d3
        .scaleLinear()
        .range(['hsl(-180,60%,50%)', 'hsl(180,60%,50%)'])
        .interpolate(function(a, b) {
          let i = d3.interpolateString(a, b)
          return function(t) {
            return d3.hsl(i(t))
          }
        })

      let arcBody = d3
        .arc()
        .startAngle(0)
        .endAngle(function(d) {
          return d.value * endAngle
        })
        .innerRadius(function(d) {
          return d.index * radius
        })
        .outerRadius(function(d) {
          return (d.index + spacing) * radius
        })
        .cornerRadius(100)

      let arcBackground = d3
        .arc()
        .startAngle(0)
        .endAngle(function(d) {
          return endAngle
        })
        .innerRadius(function(d) {
          return d.index * radius
        })
        .outerRadius(function(d) {
          return (d.index + spacing) * radius
        })
        .cornerRadius(100)

      let arcStartText = d3
        .arc()
        .startAngle(0)
        .endAngle(function(d) {
          return endAngle
        })

      let arcCircle = d3
        .arc()
        .startAngle(0)
        .endAngle(function(d) {
          return centerAngle
        })
        .innerRadius(function(d) {
          return 0
        })
        .outerRadius(function(d) {
          return (d.index - 0.1 + spacing) * radius
        })
      let arcCenter = d3
        .arc()
        .startAngle(0)
        .endAngle(function(d) {
          return d.value * endAngle
        })
        .innerRadius(function(d) {
          return (d.index + spacing / 2) * radius
        })
        .outerRadius(function(d) {
          return (d.index + spacing / 2) * radius
        })

      chartContext.svg = d3
        .select(chartContext.selecter)
        .append('svg')
        .attr('id', '#' + this.id)
        .attr('width', '100%')
        .attr('height', '100%')
        .attr(
          'viewBox',
          '0 0 ' + Math.min(width, height) + ' ' + Math.min(width, height)
        )
        .attr('preserveAspectRatio', 'xMinYMin')
        .append('g')
        .attr('transform', () => {
          let x = width / 2
          let y = height / 2
          if (this.editMode) {
            x = width / 2
            y = Math.min(width, height) / 2
          }
          if (this.$mobile) {
            x = Math.min(width, height) / 2
          }
          return `translate(${x},${y})`
        })
      chartContext.field = chartContext.svg
        .selectAll('g')
        .data(gaugeData)
        .enter()
        .append('g')
      let field = chartContext.field
      let svg = chartContext.svg
      let center = field
        .append('path')
        .attr('class', 'arc-circle-body')
        .on('mouseover', d => {
          let color = 'gray'
          let { gaugeCenterColors } = this.styles
          if (gaugeCenterColors && gaugeCenterColors.pathColor) {
            color = gaugeCenterColors.pathColor
          }
          let tooltipConfig = {
            title: '',
            position: {
              left: d3.event.pageX,
              top: d3.event.pageY,
            },
            data: [{ ...this.centerTextJson }],
            color: color,
          }
          this.showTooltipForCard1(tooltipConfig, 'chart-tooltip')
        })
        .on('mouseout', d => {
          this.hideTooltip('chart-tooltip')
        })

      chartContext.arcCircleBodyText = chartContext.svg
        .append('text')
        .attr('class', 'arc-circle-body-text')

      field.append('path').attr('class', 'arc-bg-body')

      field
        .append('path')
        .attr('class', 'arc-body')
        .on('mouseover', d => {
          let tooltipConfig = {
            title: '',
            position: {
              left: d3.event.pageX,
              top: d3.event.pageY,
            },
            data: [{ ...d.tooltipData }],
            color: d.color,
          }
          this.showTooltipForCard1(tooltipConfig, 'chart-tooltip')
        })
        .on('mouseout', d => {
          this.hideTooltip('chart-tooltip')
        })

      field
        .append('path')
        .attr('id', function(d, i) {
          return `arc-center-${i}-${id}`
        })
        .attr('class', 'arc-center')

      field
        .append('text')
        .attr('dy', '.35em')
        .attr('dx', '.75em')
        .attr('class', 'arc-text-parent')
        .attr('font-size', (3 * minDimension) / 100)
        .append('textPath')
        .attr('startOffset', '50%')
        .attr('id', (d, i) => {
          return `#${this.id}_${i}`
        })
        .attr('class', 'arc-text')
        .attr('xlink:href', function(d, i) {
          return `#arc-center-${i}-${id}`
        })

      field
        .append('text')
        .attr('dy', '.5em')
        .attr('dx', '0.5em')
        .attr('class', 'arc-start-txt')
        .style('text-anchor', 'start')
        .append('textPath')
        .attr('startOffset', '0%')
        .attr('class', 'arc-start-text')
        .attr('xlink:href', function(d, i) {
          return `#arc-center-${i}-${id}`
        })

      field
        .append('text')
        .attr('dy', '.5em')
        .attr('dx', '0.5em')
        .attr('class', 'arc-start1-txt')
        .style('text-anchor', 'start')
        .append('text')
        .attr('startOffset', '0%')
        .attr('class', 'arc-start-text')
        .attr('xlink:href', function(d, i) {
          return `#arc-start-center-${i}-${id}`
        })

      // tick()
      field
        .each(function(d, index) {
          if (self.gaugeData.length - 1 === index) {
            this._lastIndex = true
          }
          this._value = d.value
        })
        .data(gaugeData)
        .each(function(d) {
          d.previousValue = this._value
        })
        .transition()
        .ease(d3.easePolyIn)
        .duration(0)
        .each(fieldTransition)

      d3.select(self.frameElement).style('height', height + 'px')

      // function tick() {
      //   if (!document.hidden)
      //     field
      //       .each(function(d) {
      //         this._value = d.value
      //       })
      //       .data(gaugeData)
      //       .each(function(d) {
      //         d.previousValue = this._value
      //       })
      //       .transition()
      //       .ease(d3.easePolyIn)
      //       .duration(500)
      //       .each(fieldTransition)

      //   setTimeout(tick, 1000 - (Date.now() % 1000))
      // }

      function fieldTransition(r, i) {
        let field = d3.select(this).transition()
        if (this._lastIndex) {
          field
            .select('.arc-circle-body')
            .attrTween('d', arcTween(arcCircle))
            .style('fill', () => {
              let { gaugeCenterColors } = self.styles
              if (gaugeCenterColors && gaugeCenterColors.pathColor) {
                return gaugeCenterColors.pathColor
              }
              return 'gray'
            })
        }

        field
          .select('.arc-bg-body')
          .attrTween('d', arcTween(arcBackground))
          .style('fill', function(d) {
            return '#f1f3f4'
          })
        field
          .select('.arc-body')
          .attrTween('d', arcTween(arcBody))
          .style('fill', function(d) {
            return d.color || color(d.value)
          })

        field.select('.arc-center').attrTween('d', arcTween(arcCenter))

        field
          .select('.arc-text')
          .text(function(d) {
            return d.text
          })
          .attr('fill', d => {
            let { textColor } = d
            return textColor || '#000'
          })

        field.select('.arc-text-parent').attr('text-anchor', function(d) {
          if (d.value > 0.2) {
            return 'start'
          }
          return 'end'
        })
      }

      function arcTween(arc) {
        return function(d) {
          let i = d3.interpolateNumber(d.previousValue, d.value)
          return function(t) {
            d.value = i(t)
            return arc(d)
          }
        }
      }
      svg.selectAll('g').each(function(d, index) {
        let el = d3.select(this)
        let gText = svg
          .selectAll('.arc-start1-text')
          .data([{}])
          .enter()
          .append('g')
          .classed('textClass', true)
          .attr('transform', 'translate(' + 0 + ',' + 0 + ') rotate(0)')

        let path = el.selectAll('path').each(function(r, i) {
          if (i === 0) {
            let centroidText = arcStartText.centroid({
              startAngle: 0,
              endAngle: 0,
              innerRadius: -r.index * (radius * 1.9),
              outerRadius: -(r.index + spacing + 0) * radius,
            })
            let lableObj = r.text
            gText
              .append('text')
              .attr('font-size', (3 * minDimension) / 100)
              // .attr('fill', () => {
              //   let { textColor } = self.styles
              //   return textColor || '#000'
              // })
              .text(r.lable)
              .attr(
                'transform',
                'translate(' +
                  (-2 + ',' + centroidText[1] + ') rotate(' + 0 + ')')
              )
              .attr('text-anchor', 'end')
              .attr('dominant-baseline', 'central')
          }
        })
      })

      svg
        .select('.arc-circle-body-text')
        .attr('text-anchor', 'middle')
        .attr('font-size', () => {
          let actualWidth = (3 * minDimension) / 100
          let textWidth = this.getTextWidth(this.centerText)
          let textWidthInfontsize = textWidth / 25
          return actualWidth / textWidthInfontsize
        })
        .attr('font-weight', 'bold')
        .attr('fill', () => {
          let { gaugeCenterColors } = self.styles

          if (gaugeCenterColors && gaugeCenterColors.textColor) {
            return gaugeCenterColors.textColor
          }
          return '#000'
        })
        .attr('transform', () => {
          let textWidth = this.getTextWidth(this.centerText)
          let offset = 0
          if (textWidth > 0 && textWidth < 12) {
            offset = 6
          } else if (textWidth > 12 && textWidth < 20) {
            offset = 3
          } else if (textWidth > 20) {
            offset = 1
          }
          if (this.editMode) {
            offset = offset - 2
          }
          return 'translate(' + (0 + ',' + offset + ') rotate(' + 0 + ')')
        })
        .text(d => {
          return this.centerText
        })
        .append('tspan')
      // .text('300 kWh')
      // .attr('x', 0)
      // .attr('y', 4)
    },
    getTextWidth(text) {
      let canvas = document.createElement('canvas'),
        context = canvas.getContext('2d')
      return context.measureText(text).width
    },
    degToRad(ang) {
      return ang * (Math.PI / 180)
    },
  },
}
</script>
<style>
.gauge {
  width: 100%;
  margin: auto;
}
.solidgauge {
  width: 100%;
  height: 100%;
}
</style>
