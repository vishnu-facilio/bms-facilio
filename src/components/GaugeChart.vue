<template>
  <div>
    <div ref="gauge" class="p10 gauge"></div>
  </div>
</template>

<script>
import * as d3 from 'd3'
// import formatter from 'charts/helpers/formatter'
import tooltip from '@/graph/mixins/cardtooltip'
export default {
  props: ['widget', 'config', 'gaugeData'],
  mixins: [tooltip],
  data() {
    return {
      type: 'semidoughnut',
      id: Math.random().toFixed(2) * 100,
      configdata: {
        size: 710,
        clipWidth: 200,
        clipHeight: 110,
        ringInset: 20,
        ringWidth: 20,

        pointerWidth: 10,
        pointerTailLength: 5,
        pointerHeadLengthPercent: 0.9,

        minValue: 0,
        maxValue: 10,

        minAngle: -90,
        maxAngle: 90,

        transitionMs: 750,

        majorTicks: 5,
        labelFormat: d3.format('d'),
        labelInset: 10,

        arcColorFn: d3.interpolateHsl(d3.rgb('#e8e2ca'), d3.rgb('#3e6c0a')),
      },
      data: {
        value: 100,
        target: 10,
        max: 200,
        color: '#1ce6e5,#1ce6e5',
        centerText: [
          {
            label: '6174.20 kwh',
          },
          {
            label: 'THIS MONTH',
          },
        ],
      },
    }
  },
  watch: {
    gaugeData: {
      handler(newData, oldData) {
        this.$refs['gauge'].innerHTML = ''
        let self = this
        setTimeout(function() {
          self.generate()
        }, 400)
      },
      deep: true,
    },
  },
  mounted() {
    this.generate()
  },
  methods: {
    deg2rad(deg) {
      return (deg * Math.PI) / 180
    },
    generate() {
      let chartContext = {}
      this.id = Math.random().toFixed(2) * 100
      chartContext.id = this.id
      chartContext.selecter = this.$refs['gauge']
      chartContext.width = 100
      chartContext.height = 100
      chartContext.isMaxpointer = false
      chartContext.pointerColor =
        this.gaugeData.meta.pointerColor || 'rgba(29, 25, 50, 0.85)'
      chartContext.unitTextSize = this.gaugeData.meta.unitTextSize
        ? this.gaugeData.meta.unitTextSize
        : 0.4 + 'vw'
      chartContext.remainingTextSize = this.gaugeData.meta.remainingTextSize
        ? this.gaugeData.meta.remainingTextSize
        : 0.5 + 'vw'

      if (
        this.gaugeData &&
        this.gaugeData.value &&
        this.gaugeData.value.needMax &&
        this.gaugeData.value.needMax !== 'usetarget'
      ) {
        chartContext.isMaxpointer = true
      }
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
            Math.min(chartContext.width, chartContext.height)
        )
        .attr('preserveAspectRatio', 'xMinYMin')
        .append('g')
      chartContext.svg.attr(
        'transform',
        'translate(' +
          Math.min(chartContext.width, chartContext.height) / 2 +
          ',' +
          Math.min(chartContext.width, chartContext.height) / 2 +
          ') '
      )
      this.gaugeData.meta.color = this.gaugeData.meta.color || '#1ce6e5,#1ce6e5'
      chartContext.data = this.gaugeData

      this.draw(chartContext)
    },
    centerTranslation(r) {
      return 'translate(' + r + ',' + r + ')'
    },
    draw(chartContext, newValue) {
      {
        let self = this
        let data = chartContext.data.meta || null
        let value = chartContext.data.value || null
        let config = this.configdata
        let percentage =
          data.targetValue && data.baseValue
            ? data.baseValue / data.targetValue
            : 0
        let maxPointer = false
        let maxPercentage =
          data.targetValue && data.baseValue
            ? data.baseValue / data.targetValue
            : 0
        if (percentage > 1) {
          maxPointer = true
          maxPercentage = maxPercentage % 1
        }

        let t = Math.PI,
          width = 100,
          height = 100,
          outerRadius = Math.min(width, height) / 2,
          innerRadius = (outerRadius / 5) * 3

        chartContext.arc = d3
          .arc()
          .innerRadius(innerRadius)
          .outerRadius(outerRadius)

        chartContext.arc1 = d3
          .arc()
          .innerRadius(innerRadius)
          .outerRadius(outerRadius)

        chartContext.targetArc = d3
          .arc()
          .innerRadius(innerRadius)
          .outerRadius(outerRadius)

        let svg = chartContext.svg

        chartContext.defs = svg.append('defs')

        chartContext.gradient = chartContext.defs // gradiant path
          .append('linearGradient')
          .attr('id', function() {
            return 'svgGradient' + chartContext.id
          })
          .attr('x1', '100%')
          .attr('x2', '0%')
          .attr('y1', '0%')
          .attr('y2', '100%')

        chartContext.gradient
          .append('stop')
          .attr('class', 'start')
          .attr('offset', '0%')
          .attr('stop-color', function() {
            return value && value.startColor ? value.startColor : '#1ce6e5'
          })
          .attr('stop-opacity', 0.2)

        chartContext.gradient
          .append('stop')
          .attr('class', 'end')
          .attr('offset', 20 * 100 + '%')
          .attr('stop-color', function() {
            return value && value.end ? value.end : '#1ce6e5'
          })
          .attr('stop-opacity', 1)
        percentage = percentage > 1 ? 1 : percentage < -1 ? 0 : percentage // to omit the -ve percentage
        let unit =
          data.fieldObj && data.fieldObj.unit ? data.fieldObj.unit : null
        svg
          .append('path')
          .datum({ startAngle: 0, endAngle: t })
          .style('fill', '#f1f3f4')
          .attr('d', chartContext.arc)
          .attr('class', 'arc')
          .on('mouseover', function(d) {
            let tooltipdata = [
              {
                label: 'Target',
                value: data.targetValue + ' ' + (unit ? unit : ''),
              },
              {
                label: 'Achieved',
                value: data.baseValue + ' ' + (unit ? unit : ''),
              },
              {
                label: maxPointer ? 'Exceeded by' : 'Remaining',
                value: data
                  ? maxPointer
                    ? (
                        Number(data.baseValue) - Number(data.targetValue)
                      ).toFixed(2) +
                      ' ' +
                      (unit || '')
                    : (
                        Number(data.targetValue) - Number(data.baseValue)
                      ).toFixed(2) +
                      ' ' +
                      (unit || '')
                  : '',
              },
            ]
            let tooltipConfig = {
              title1: '',
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              data: tooltipdata,
            }
            // if (!self.$mobile) {
            //   self.showTooltipForCard1(tooltipConfig, 'kpi-gauge-chart')
            // }
            self.showTooltipForCard1(tooltipConfig, 'kpi-gauge-chart')
          })
          .on('mouseout', function(d) {
            self.hideTooltip('kpi-gauge-chart')
          })
        if (data.colorPlates && data.colorPlates.length) {
          data.colorPlates.forEach(rt => {
            if (rt.startAngle !== rt.endAngle && rt.startAngle < percentage) {
              svg
                .append('path')
                .datum({
                  startAngle: rt.startAngle * t,
                  endAngle:
                    percentage >= rt.endAngle
                      ? rt.endAngle * t
                      : percentage * t,
                })
                .style('fill', function() {
                  return rt.color
                })
                .attr('d', chartContext.arc)
                .attr('class', 'arc')
            }
          })
        } else {
          chartContext.midground = svg
            .append('path')
            .datum({ startAngle: 0, endAngle: (percentage || 0) * t })
            .style('fill', function() {
              return 'url(#svgGradient' + chartContext.id + ')'
            })
            .attr('d', chartContext.arc)
            .attr('class', 'arc')
            .on('mouseover', function(d) {
              let tooltipdata = [
                {
                  label: 'Target',
                  value: data.targetValue + ' ' + (unit ? unit : ''),
                },
                {
                  label: 'Achieved',
                  value: data.baseValue + ' ' + (unit ? unit : ''),
                },
                {
                  label: maxPointer ? 'Exceeded by' : 'Remaining',
                  value: maxPointer
                    ? (
                        Number(data.baseValue) - Number(data.targetValue)
                      ).toFixed(2) +
                      ' ' +
                      (unit || '')
                    : (
                        Number(data.targetValue) - Number(data.baseValue)
                      ).toFixed(2) +
                      ' ' +
                      (unit || ''),
                },
              ]
              let tooltipConfig = {
                title1: '',
                position: {
                  left: d3.event.pageX,
                  top: d3.event.pageY,
                },
                data: tooltipdata,
              }
              self.showTooltipForCard1(tooltipConfig, 'kpi-gauge-chart')
              // if (!self.$mobile) {
              //   self.showTooltipForCard1(tooltipConfig, 'kpi-gauge-chart')
              // }
            })
            .on('mouseout', function(d) {
              self.hideTooltip('kpi-gauge-chart')
            })
        }

        let arcTween = function arcTween(transition, newAngle) {
          transition.attrTween('d', function(d) {})
        }
        // chartContext.midground
        //   .transition()
        //   .duration(1000)
        //   .call(arcTween, percentage * t)

        // max pointer
        if (maxPointer) {
          chartContext.midground1 = svg
            .append('path')
            .datum({
              startAngle: (maxPercentage - 0.005) * t,
              endAngle: maxPercentage * t,
            })
            .style('fill', '#000')
            .attr('d', chartContext.arc1)
            .attr('class', 'arc')
          chartContext.midground1
            .transition()
            .duration(1000)
            .call(arcTween, maxPercentage * t)
        }

        // draw pointer
        let centerTx = this.centerTranslation(innerRadius)
        let range = config.maxAngle - config.minAngle
        let pointerHeadLength = Math.round(
          innerRadius * config.pointerHeadLengthPercent
        )
        let lineData = [
          [config.pointerWidth / 4, 0],
          [0, -pointerHeadLength],
          [-(config.pointerWidth / 4), 0],
          [0, 0],
          [config.pointerWidth / 4, 0],
        ]
        let pointerLine = d3.line().curve(d3.curveLinear)

        let pg = svg
          .append('g')
          .data([lineData])
          .attr('class', 'pointer')
          .attr('transform', (0, 0))

        let pointer = pg
          .append('path')
          .attr('d', pointerLine /*function(d) { return pointerLine(d) +'Z';}*/)
          .attr('transform', 'rotate(' + config.minAngle + ')')
          .attr('fill', chartContext.pointerColor)

        // svg
        //   .append('circle')
        //   .attr('cx', 0)
        //   .attr('cy', 0)
        //   .attr('r', 5)
        //   .attr('fill', '#fff')
        //   .attr('transform', 'rotate(' + config.minAngle + ')')

        // svg
        //   .append('circle')
        //   .attr('cx', 0)
        //   .attr('cy', 0)
        //   .attr('r', 4)
        //   .attr('fill', 'rgb(102, 102, 102)')
        //   .attr('transform', 'rotate(' + config.minAngle + ')')
        svg
          .append('text')
          .attr('text-anchor', 'middle')
          .style('font-size', chartContext.remainingTextSize)
          .attr('dy', 25)
          .attr('dx', 0)
          .style('letter-spacing', '0.3px')
          .text(function() {
            let value = 'Remaining : '
            if (unit) {
              let obj = self
                .$convert(Number(data.targetValue) - Number(data.baseValue))
                .from(unit)
                .toBest()
              let ob = self
                .$convert(Number(data.baseValue) - Number(data.targetValue))
                .from(unit)
                .toBest()
              value += Number(obj.val).toFixed(2)
              if (maxPointer) {
                value = 'Exceeded by : '
                value += Number(ob.val).toFixed(2)
              }
              if (data.fieldObj && data.fieldObj.unit && !maxPointer) {
                value += ' ' + obj.unit
              } else if (maxPointer) {
                value += ' ' + ob.unit
              }
            } else {
              value += (
                Number(data.targetValue) - Number(data.baseValue)
              ).toFixed(2)
              if (maxPointer) {
                value = 'Exceeded by : '
                value += (
                  Number(data.targetValue) - Number(data.baseValue)
                ).toFixed(2)
              }
            }
            return value
          })
          .style('letter-spacing', ' 0.3px')
          .style('padding-top', ' 5px')
          .attr('class', 'gauge-center-period')
          .attr('fill', '#6d7278')

        svg
          .append('text')
          .attr('text-anchor', 'middle')
          .style('font-size', chartContext.unitTextSize)
          .attr('dy', 10)
          .attr('dx', -40)
          .style('letter-spacing', '0.3px')
          .text(function() {
            return 0 + ' ' + (unit || '')
          })
          .style('letter-spacing', ' 0.3px')
          .style('padding-top', ' 5px')
          .attr('fill', '#6d7278')

        svg
          .append('text')
          .attr('text-anchor', 'middle')
          .style('font-size', chartContext.unitTextSize)
          .attr('dy', 10)
          .attr('dx', unit ? 30 : 40)
          .style('letter-spacing', '0.3px')
          .text(function() {
            if (unit) {
              let obj = self
                .$convert(
                  Number(data.baseValue) > Number(data.targetValue)
                    ? data.baseValue
                    : data.targetValue
                )
                .from(unit)
                .toBest()
              return Number(obj.val).toFixed(2) + ' ' + obj.unit
            } else {
              return Number(data.baseValue) > Number(data.targetValue)
                ? data.baseValue
                : data.targetValue
            }
          })
          .style('letter-spacing', ' 0.3px')
          .style('padding-top', ' 5px')
          .attr('fill', '#6d7278')

        svg
          .append('line')
          .attr('x1', -50)
          .attr('y1', 30)
          .attr('x2', 50)
          .attr('y2', 30)
          .attr('stroke', '#6d7278')
          .attr('stroke-width', '0.1')
          .attr('fill', '#f1f3f4')

        let tooltip = svg.append('g').attr('class', 'target-meter-group')
        tooltip
          .append('div')
          .attr('class', 'target-meter-tooltip')
          .style('background-color', '#fff')
          .text('test')

        let scale = d3
          .scaleLinear()
          .range([0, 1])
          .domain([config.minValue, config.maxValue])
        let ratio = scale(newValue)
        let newAngle = config.minAngle + ratio * range

        pointer
          .transition()
          .duration(config.transitionMs)
          .ease(d3.easeLinear)
          .attr('transform', 'rotate(' + ((percentage || 0) * 180 - 90) + ')')
      }
    },
  },
}
</script>
<style>
.gauge .arc {
  transform: rotate(-90deg);
}
.layout4 .readingcard-header {
  padding-top: 10px;
  line-height: 0.8rem;
  padding-bottom: 8px;
}
.gauge {
  width: 86%;
  margin: auto;
}
.target {
  transform: rotate(212deg);
}

.kpi-gauge-chart {
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

.kpi-gauge-chart .axis-row {
  padding: 3px;
  padding-left: 0px;
}

.kpi-gauge-chart .tooltip-title {
  font-weight: 500;
  font-size: 13px;
  padding: 5px 0;
}
.tooltip-type-title {
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  color: #879eb5;
  padding: 5px;
  padding-left: 0px;
  text-transform: uppercase;
  font-size: 11px;
}
.kpi-gauge-chart .axis-color {
  margin: 2px 5px;
}

.kpi-gauge-chart .axis-tip {
  font-size: 12px;
  color: #333333bd;
  padding-top: 5px;
}

.kpi-gauge-chart .axis-label {
  font-weight: 400;
  padding-right: 8px;
  color: #7f7f7f;
  font-size: 12px;
  letter-spacing: 0.5px;
}
.kpi-gauge-chart .icon {
  height: 20px;
  width: 20px;
  margin-right: 4px;
  margin-top: 4px;
}
.kpi-gauge-chart .imgcls {
  display: flex;
  cursor: pointer;
  flex-direction: row;
}
.kpi-gauge-chart .axis-value {
  font-weight: 500;
  font-size: 12.5px;
}

.kpi-gauge-chart .axis-unit {
  font-weight: 400;
  font-size: 13px;
  margin-left: 2px;
  color: #979797;
}
.layout4 .readingcard-header {
  letter-spacing: 0.8px !important;
}
</style>
