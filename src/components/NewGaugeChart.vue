<template>
  <div class="card-builder-gauge-chart">
    <div
      ref="gauge"
      class="p10 gauge"
      :style="{ 'max-width': zoom + '%' }"
    ></div>
  </div>
</template>

<script>
import * as d3 from 'd3'
import tooltip from '@/graph/mixins/cardtooltip'
import CardHelpers from 'pages/card-builder/card-helpers'
export default {
  props: ['gaugeData', 'fit'],
  mixins: [tooltip, CardHelpers],
  data() {
    return {
      zoom: 100,
      innerWidth: null,
      type: 'semidoughnut',
      id: Math.random().toFixed(2) * 100,
    }
  },
  created() {
    window.addEventListener('resize', this.handleResize)
  },
  watch: {
    gaugeData: {
      handler: 'regen',
      deep: true,
    },
  },
  mounted() {
    this.handleResize()
  },
  methods: {
    handleResize() {
      if (this.$el) {
        this.innerWidth = this.$el.clientWidth - 60
      }
      this.zoom =
        window.devicePixelRatio > 1
          ? Math.round(window.devicePixelRatio * 50)
          : Math.round(window.devicePixelRatio * 100)
      this.regen()
    },
    regen() {
      if (this.$refs['gauge']) this.$refs['gauge'].innerHTML = ''
      this.$nextTick(() => this.generate())
    },
    generate() {
      if (this.$refs['gauge']) this.$refs['gauge'].innerHTML = ''

      let chartContext = {}
      this.id = Math.random().toFixed(2) * 100
      chartContext.id = this.id
      chartContext.selecter = this.$refs['gauge']
      chartContext.width = 100
      chartContext.height = 100
      chartContext.pointerColor = '#122328'
      chartContext.unitTextSize = 0.4 + 'vw'
      chartContext.tickFontSize = 0.35 + 'vw'
      chartContext.svg = d3
        .select(chartContext.selecter)
        .append('svg')
        .attr('width', '100%')
        .attr('height', '100%')
        .attr(
          'viewBox',
          '0 0 ' +
            Math.min(chartContext.width, chartContext.height) +
            ' ' +
            Math.min(
              this.gaugeData.config && this.gaugeData.config.type === 1
                ? 65
                : chartContext.height,
              chartContext.height
            )
        )
        .attr('preserveAspectRatio', 'xMinYMin')
        .append('g')

      chartContext.svg.attr(
        'transform',
        'translate(' +
          chartContext.width / 2 +
          ',' +
          chartContext.height / 2 +
          ') '
      )
      chartContext.data = this.gaugeData

      this.draw(chartContext)
    },
    centerTranslation(r) {
      return 'translate(' + r + ',' + r + ')'
    },
    angToRad(ang) {
      return ang * (Math.PI / 180)
    },
    percentageToAng(percentage, config, data) {
      let scale = d3
        .scaleLinear()
        .range([config.startAngle, config.endAngle])
        .domain([0, 1])
      return scale(percentage)
    },
    valueToPercentage(value, data) {
      let scale = null
      let { minValue } = data || 0
      let { maxValue } = data
      let { baseValue } = data
      if (baseValue > maxValue) {
        scale = d3
          .scaleLinear()
          .range([0, 1])
          .domain([minValue, baseValue])
        return scale(value)
      } else if (baseValue < minValue) {
        scale = d3
          .scaleLinear()
          .range([0, 1])
          .domain([baseValue, data.maxValue])
        return scale(value)
      } else {
        scale = d3
          .scaleLinear()
          .range([0, 1])
          .domain([minValue, data.maxValue])
        return scale(value)
      }
    },
    calPercentage(data) {
      let percentage = 0
      let { minValue } = data || 0
      let { maxValue } = data
      let { baseValue } = data
      if (baseValue > maxValue) {
        return 1
      } else if (baseValue < minValue) {
        return 0
      } else {
        let range = maxValue - minValue
        let correctedStartValue = baseValue - minValue
        percentage = (correctedStartValue * 100) / range / 100
        return Math.sqrt(percentage * percentage)
      }
    },
    calActualPercentage(data) {
      let percentage = 0
      let { minValue } = data || 0
      let { maxValue } = data
      let { baseValue } = data
      if (baseValue > maxValue) {
        let range = baseValue - minValue
        let correctedStartValue = maxValue - minValue
        percentage = (correctedStartValue * 100) / range / 100
        return {
          maxPercentage: Math.sqrt(percentage * percentage),
          minPercentage: null,
        }
      } else if (baseValue < minValue) {
        let range = maxValue - baseValue
        let correctedStartValue = minValue - baseValue
        percentage = (correctedStartValue * 100) / range / 100
        return {
          minPercentage: Math.sqrt(percentage * percentage),
          maxPercentage: null,
        }
      } else {
        return {
          minPercentage: null,
          maxPercentage: null,
        }
      }
    },
    draw(chartContext, newValue) {
      let self = this
      let { data } = chartContext.data || null
      let config = this.getExtaOptions(chartContext.data.config)

      let percentage = this.calPercentage(data)
      let { maxPercentage } = this.calActualPercentage(data)
      let { minPercentage } = this.calActualPercentage(data)
      let startAngle = this.angToRad(config.startAngle)
      let endAngle = this.angToRad(config.endAngle)
      let t = endAngle || Math.PI

      let width = config.width || 100
      let height = config.height || 100
      let showTooltip = true
      if (config.hasOwnProperty('showTooltip')) {
        showTooltip = config.showTooltip
      } else {
        showTooltip = true
      }
      let outerRadius = config.outerRadius || Math.min(width, height) / 2 - 5
      let innerRadius = config.innerRadius || Math.min(width, height) / 2.13 - 5
      let outerRadiusDefault = Math.min(width, height) / 2 - 5
      chartContext.arc = d3
        .arc()
        .cornerRadius(outerRadius - innerRadius)
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .startAngle(startAngle)

      chartContext.tickarc = d3
        .arc()
        .cornerRadius(outerRadiusDefault - 5 - (innerRadius - 2.5))
        .innerRadius(innerRadius - 2.5)
        .outerRadius(outerRadiusDefault - 5)

      chartContext.bgarc = d3
        .arc()
        .innerRadius(0)
        .outerRadius(innerRadius)
        .startAngle(startAngle)

      chartContext.pointerArc = d3
        .arc()
        .innerRadius(innerRadius)
        .cornerRadius(outerRadius - innerRadius)
        .outerRadius(outerRadius)

      let svg = chartContext.svg
      chartContext.defs = svg.append('defs')

      // Gradient handling
      if (config.offsets) {
        chartContext.gradient = chartContext.defs
          .append('linearGradient')
          .attr('id', function() {
            return 'svgGradient' + chartContext.id
          })
          .attr('gradientUnits', 'userSpaceOnUse')
          .attr('x1', 0)
          .attr('x2', percentage)

        let colorArrayForThreshold = [...config.colors]
        let colorFn = d3
          .scaleThreshold()
          .domain(config.offsets.map(o => Number(o)))
          .range(colorArrayForThreshold)

        let pathColors = config.offsets.map(offset => {
          return {
            color: colorFn(Number(offset)),
            offset: `${Number(offset)}%`,
            percentage: offset / 100,
          }
        })
        let paths = []
        for (let i = 0; i < pathColors.length; i++) {
          let current = null
          let pre = null
          if (i !== 0) {
            current = pathColors[i].percentage
            pre = pathColors[i - 1].percentage
            if (pathColors[i].percentage <= percentage) {
              this.$set(pathColors[i], 'start', pre)
              this.$set(pathColors[i], 'end', current)
              paths.push(pathColors[i])
            }
          }
        }
        if (paths.length && paths[paths.length - 1].current !== percentage) {
          let p = pathColors[paths.length + 1]
          let v = paths[paths.length - 1]
          if (p) {
            this.$set(p, 'start', v.end)
            this.$set(p, 'end', percentage)
            paths.push(p)
          } else {
            this.$set(paths[paths.length], 'end', percentage)
          }
        }
        for (let i = 0; i < paths.length; i++) {
          this.$set(paths[i], 'clor', pathColors[i].color)
        }
        config.paths = paths
        pathColors.forEach(rt => {
          chartContext.gradient
            .append('stop')
            .attr('class', 'start')
            .attr('offset', rt.offset)
            .attr('stop-color', function() {
              return rt.color
            })
            .attr('stop-opacity', 1)
        })
      } else {
        chartContext.gradient = chartContext.defs
          .append('linearGradient')
          .attr('id', function() {
            return 'svgGradient' + chartContext.id
          })
          .attr('x1', '0%')
          .attr('x2', '100%')
          .attr('y1', '0%')
          .attr('y2', '0%')

        let pathColors = config.gaugeColors || [
          { color: '#ff7878', offset: '0%' },
          { color: '#7d49ff', offset: '31%' },
          { color: '#514dff', offset: '70%' },
          { color: '#1eb9b7', offset: '100%' },
        ]

        pathColors.forEach(rt => {
          chartContext.gradient
            .append('stop')
            .attr('class', 'start')
            .attr('offset', rt.offset)
            .attr('stop-color', function() {
              return rt.color
            })
            .attr('stop-opacity', 1)
        })
      }

      if (config.gaugeBackgroundColors && config.gaugeBackgroundColors.length) {
        chartContext.bggradient = chartContext.defs // gradiant path
          .append('linearGradient')
          .attr('id', function() {
            return 'GaugeBgGradient' + chartContext.id
          })
          .attr('x1', '0%')
          .attr('x2', '100%')
          .attr('y1', '0%')
          .attr('y2', '0%')

        config.gaugeBackgroundColors.forEach(rt => {
          chartContext.bggradient
            .append('stop')
            .attr('class', 'start')
            .attr('offset', rt.offset)
            .attr('stop-color', function() {
              return rt.color
            })
            .attr('stop-opacity', rt.opacity)
        })
      }

      // Unfilled arc
      let fullpath = svg
        .append('path')
        .datum({ endAngle: t })
        .style('fill', '#f1f3f4')
        .attr('d', chartContext.arc)
        .attr('class', 'arc')
        .attr('transform', `rotate(-90)`)
      if (showTooltip) {
        fullpath
          .on('mouseover', d => {
            let tooltipConfig = {
              title1: '',
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              data: self.getFormatedData(chartContext.data.tooltipdata),
            }
            self.showTooltipForCard1(tooltipConfig, 'chart-tooltip')
          })
          .on('mouseout', function(d) {
            self.hideTooltip('chart-tooltip')
          })
      }

      // background color for gauge charts
      svg
        .append('path')
        .datum({
          startAngle: startAngle,
          endAngle: this.angToRad(
            this.percentageToAng(percentage, config, data)
          ),
        })
        .style('fill', d => {
          return 'url(#GaugeBgGradient' + chartContext.id + ')'
        })
        .attr('d', chartContext.bgarc)
        .attr('class', 'arc')
        .attr('transform', `rotate(-90)`)

      // Filled arc (with gradient) arc path

      if (config.offsets) {
        config.paths.reverse().forEach(rt => {
          let pathSvg = svg
            .append('path')
            .datum({
              startAngle: this.angToRad(
                this.percentageToAng(rt.start, config, data)
              ),
              endAngle: this.angToRad(
                this.percentageToAng(rt.end, config, data)
              ),
            })
            .attr('d', chartContext.arc)
            .attr('class', 'arc')
            .attr('transform', `rotate(-90)`)
            .style('fill', () => {
              return rt.clor
            })
          if (showTooltip) {
            pathSvg
              .on('mouseover', d => {
                let tooltipConfig = {
                  title1: '',
                  position: {
                    left: d3.event.pageX,
                    top: d3.event.pageY,
                  },
                  data: self.getFormatedData(chartContext.data.tooltipdata),
                }
                self.showTooltipForCard1(tooltipConfig, 'chart-tooltip')
              })
              .on('mouseout', function(d) {
                self.hideTooltip('chart-tooltip')
              })
          }
        })
      } else {
        svg
          .append('path')
          .datum({
            startAngle: startAngle,
            endAngle: this.angToRad(
              this.percentageToAng(percentage, config, data)
            ),
          })
          .attr('d', chartContext.arc)
          .attr('class', 'arc')
          .attr('transform', `rotate(-90)`)
          .style('fill', () => {
            return 'url(#svgGradient' + chartContext.id + ')'
          })
          .on('mouseover', function(d) {
            let tooltipConfig = {
              title1: '',
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              data: self.getFormatedData(chartContext.data.tooltipdata),
            }
            self.showTooltipForCard1(tooltipConfig, 'chart-tooltip')
          })
          .on('mouseout', function(d) {
            self.hideTooltip('chart-tooltip')
          })
      }

      //
      let arcTween = function arcTween(transition, newAngle) {
        transition.attrTween('d', function(d) {})
      }

      // max pointer
      if (maxPercentage) {
        maxPercentage = maxPercentage % 1
        chartContext.midground1 = svg
          .append('path')
          .datum({
            startAngle: this.angToRad(
              this.percentageToAng(maxPercentage - 0.005, config, data)
            ),
            endAngle: this.angToRad(
              this.percentageToAng(maxPercentage, config, data)
            ),
          })
          .style('fill', '#000')
          .attr('d', chartContext.pointerArc)
          .attr('class', 'arc')
          .attr('transform', `rotate(-90)`)
        chartContext.midground1
          .transition()
          .duration(1000)
          .call(arcTween, maxPercentage * t)
      } else if (minPercentage) {
        minPercentage = minPercentage % 1
        chartContext.midground1 = svg
          .append('path')
          .datum({
            startAngle: this.angToRad(
              this.percentageToAng(minPercentage - 0.005, config, data)
            ),
            endAngle: this.angToRad(
              this.percentageToAng(minPercentage, config, data)
            ),
          })
          .style('fill', '#000')
          .attr('d', chartContext.pointerArc)
          .attr('class', 'arc')
          .attr('transform', `rotate(-90)`)
        chartContext.midground1
          .transition()
          .duration(1000)
          .call(arcTween, minPercentage * t)
      }

      // draw pointer
      let range = config.maxAngle - config.minAngle
      let pointerHeadLength = Math.round(
        innerRadius * config.pointerHeadLengthPercent
      )
      if (!config.hideneedle) {
        let lineData = [
          [2, 0],
          [0, -pointerHeadLength],
          [-2, 0],
          [0, 0],
          [2, 0],
        ]
        let pointerLine = d3.line().curve(d3.curveLinear)

        let pg = svg
          .append('g')
          .data([lineData])
          .attr('class', 'pointer')
          .attr('transform', `rotate(0)`)

        let pointer = pg
          .append('path')
          .attr('d', pointerLine)
          .attr('transform', 'rotate(' + config.minAngle + ')')
          .attr('fill', config.pointerColor || '#122328')

        svg
          .append('circle')
          .attr('cx', 0)
          .attr('cy', 0)
          .attr('r', 5)
          .attr('fill', config.pointerColor || '#122328')
          .attr('transform', 'rotate(' + config.minAngle + ')')

        svg
          .append('circle')
          .attr('cx', 0)
          .attr('cy', 0)
          .attr('r', 2)
          .attr('fill', '#fff')
          .attr('transform', 'rotate(' + config.minAngle + ')')

        pointer
          .transition()
          .duration(config.transitionMs)
          .ease(
            config.needleAnimation ? d3[config.needleAnimation] : d3.easePolyIn
          )
          .attr(
            'transform',
            'rotate(' +
              (this.percentageToAng(percentage, config, data) - 90) +
              ')'
          )
      }

      // end labels
      svg
        .append('text')
        .attr('text-anchor', 'middle')
        .style('font-size', chartContext.unitTextSize)
        .attr('dy', 10)
        .attr('dx', function() {
          if (config.startLabel) {
            return config.startLabel.length > 3 ? -41 : -44
          }
          return -44
        })
        .style('letter-spacing', '0.3px')
        .text(function() {
          return config.startLabel || ''
        })
        .style('letter-spacing', ' 0.3px')
        .style('padding-top', ' 5px')
        .attr('fill', '#324056')
        .style('letter-spacing', 'normal')
        .style('font-weight', ' 500')

      svg
        .append('text')
        .attr('text-anchor', 'middle')
        .style('font-size', chartContext.unitTextSize)
        .attr('dy', 10)
        .attr('dx', function() {
          if (config.endLabel) {
            return config.endLabel.length > 3 ? 41 : 43
          }
          return 43
        })
        .style('letter-spacing', '0.3px')
        .text(function() {
          return config.endLabel || ''
        })
        .style('letter-spacing', ' 0.3px')
        .style('padding-top', ' 5px')
        .style('letter-spacing', 'normal')
        .style('font-weight', ' 500')
        .attr('fill', '#324056')

      let tooltip = svg.append('g').attr('class', 'target-meter-group')
      tooltip
        .append('div')
        .attr('class', 'target-meter-tooltip')
        .style('background-color', '#fff')
        .text('test')

      if (config.ticks && config.ticks.length) {
        chartContext.ticks = svg
          .append('g')
          .attr('class', 'label')
          .attr('transform', d => {
            return config.showTicksOutside
              ? `rotate(0) translate(-5,3)`
              : `rotate(0) translate(-3,3)`
          })

        chartContext.ticks
          .selectAll('text')
          .data(config.ticks)
          .enter()
          .append('g')
          .attr('transform', d => {
            let angle = this.percentageToAng(
              this.valueToPercentage(d.value, data),
              config,
              data
            )
            return config.showTicksOutside
              ? `rotate(${angle - 180}) translate(${innerRadius + 10}, ${0})`
              : `rotate(${angle - 180}) translate(${innerRadius - 10}, ${0})`
          })
          .style('text-anchor', (d, i, n) => {
            if (!config.showTicksOutside) {
              return
            }
            let len = n.length
            let current = i + 1

            if (len % 2 === 0) {
              return current <= len / 2 ? 'middle' : 'start'
            } else {
              return current < len / 2 ? 'middle' : 'start'
            }
          })
          .append('text')
          .style('font-size', config.tickFontSize || chartContext.tickFontSize)
          .style('text-transform', () =>
            config.showTicksOutside ? 'uppercase' : null
          )
          .style('font-weight', () => (config.showTicksOutside ? 'bold' : null))
          .style('fill', function(d) {
            return d.color || '#969aa2'
          })
          .attr('transform', d => {
            let angle = this.percentageToAng(
              this.valueToPercentage(d.value, data),
              config,
              data
            )

            return `rotate(${-(angle - 180)})`
          })
          .text(function(d) {
            return d.displayValue
          })
      }
      if (config.ticks) {
        svg.valueTicks = svg.append('g').attr('class', 'innerTicks')

        let tickScale = d3
          .scaleLinear()
          .range([0, 1])
          .domain([0, 100])
        for (let step = 0; step < 100; step++) {
          svg.valueTicks
            .append('path')
            .datum({
              startAngle: this.angToRad(
                this.percentageToAng(tickScale(step), config, data)
              ),
              endAngle: this.angToRad(
                this.percentageToAng(tickScale(step + 1), config, data)
              ),
            })
            .style('fill', function() {
              return config.ticksColor || '#d4dae6'
            })
            .attr('d', chartContext.tickarc)
            .attr('class', 'arc')
            .attr('transform', `rotate(-90)`)
          step++
        }
      }
    },
    getFormatedData(data) {
      let d = []
      data.forEach(rt => {
        let t = rt
        this.$set(t, 'value', this.formatDecimal(rt.value))
        d.push(t)
      })
      return d
    },
    getExtaOptions(config) {
      let type_1 = {
        pointerHeadLengthPercent: 0.8, // 1 is the maximum
        minAngle: -90,
        maxAngle: 90,
        transitionMs: 750,
        startAngle: 0,
        endAngle: 180,
        gaugeColors: [
          { color: '#ff7878', offset: '0%' },
          { color: '#7d49ff', offset: '31%' },
          { color: '#514dff', offset: '70%' },
          { color: '#1eb9b7', offset: '100%' },
        ],
        gaugeBackgroundColors: [
          { color: '#ea8496', offset: '0%', opacity: 0.1 },
          { color: '#ea8496', offset: '100%', opacity: 0.2 },
        ],
      }
      let type_2 = {
        pointerHeadLengthPercent: 0.65, // 1 is the maximum
        minAngle: -90,
        maxAngle: 90,
        transitionMs: 750,
        startAngle: -45,
        endAngle: 225,
        gaugeColors: [
          { color: '#ff7878', offset: '0%' },
          { color: '#7d49ff', offset: '31%' },
          { color: '#514dff', offset: '70%' },
          { color: '#1eb9b7', offset: '100%' },
        ],
        gaugeBackgroundColors: [
          { color: '#ea8496', offset: '0%', opacity: 0.1 },
          { color: '#ea8496', offset: '100%', opacity: 0.2 },
        ],
      }

      let type_3 = {
        hideneedle: true,
        showTicksOutside: true,
        tickFontSize: '0.25vw',
        splitGradients: true,
        pointerHeadLengthPercent: 0.65,
        minAngle: -90,
        maxAngle: 90,
        transitionMs: 750,
        startAngle: -45,
        endAngle: 225,
        width: 70,
        height: 70,
        outerRadius: 70 / 2.05 - 5,
        innerRadius: 70 / 2.2 - 5,
      }

      if (config.type === 1) {
        if (config.colors) {
          type_1.gaugeColors.forEach((rt, index) => {
            rt.color = config.colors[index]
          })
        }
        if (config.backgroundColors) {
          type_1.gaugeBackgroundColors.forEach((rt, index) => {
            rt.color = config.backgroundColors[index]
          })
        } else {
          delete type_1.gaugeBackgroundColors
        }
        return { ...type_1, ...config }
      } else if (config.type === 2) {
        if (config.colors) {
          type_2.gaugeColors.forEach((rt, index) => {
            rt.color = config.colors[index]
          })
        }
        if (config.backgroundColors) {
          type_2.gaugeBackgroundColors.forEach((rt, index) => {
            rt.color = config.backgroundColors[index]
          })
        } else {
          delete type_2.gaugeBackgroundColors
        }
        return { ...type_2, ...config }
      } else if (config.type === 3) {
        return { ...type_3, ...config }
      }

      return { ...type_1, ...config }
    },
  },
}
</script>
<style lang="scss">
.card-builder-gauge-chart {
  .gauge {
    width: 100%;
    margin: auto;
  }
  .target {
    transform: rotate(212deg);
  }

  .cardbuilder-gauge-chart {
    position: absolute;
    padding: 10px;
    box-shadow: 0 4px 18px 0 rgba(0, 0, 0, 0.1) !important;
    border: none;
    background-color: #fff;
    color: #000 !important;
    opacity: 1 !important;
    transition: visibility 0.2s, opacity 0.3s linear;
    z-index: 10000;
    max-width: 500px;
    border-radius: 3px;
    padding-bottom: 5px;
  }
}
.card-gauge {
  .gauge {
    height: 100%;
  }
}
.cardbuilder-gauge-chart {
  .axis-row {
    padding: 3px;
    padding-left: 0px;
  }
  .tooltip-title {
    font-weight: 500;
    font-size: 13px;
    padding: 5px 0;
  }
  .axis-color {
    margin: 2px 5px;
  }
  .axis-tip {
    font-size: 12px;
    color: #333333bd;
    padding-top: 5px;
  }
  .axis-label {
    font-weight: 400;
    padding-right: 8px;
    color: #7f7f7f;
    font-size: 12px;
    letter-spacing: 0.5px;
  }
  .icon {
    height: 20px;
    width: 20px;
    margin-right: 4px;
    margin-top: 4px;
  }
  .imgcls {
    display: flex;
    cursor: pointer;
    flex-direction: row;
  }
  .axis-value {
    font-weight: 500;
    font-size: 12.5px;
  }
  .axis-unit {
    font-weight: 400;
    font-size: 13px;
    margin-left: 2px;
    color: #979797;
  }
}
</style>
