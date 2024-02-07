<template>
  <div>
    <div ref="reading-gauge" class="p10 reading-gauge"></div>
  </div>
</template>

<script>
import * as d3 from 'd3'
import formatter from 'charts/helpers/formatter'
import tooltip from '@/graph/mixins/cardtooltip'
export default {
  props: ['widget', 'config', 'gaugeData'],
  mixins: [tooltip],
  data() {
    return {
      type: 'semidoughnut',
      id: Math.random().toFixed(2) * 100,
      data: {
        value: 100,
        target: 10,
        max: 200,
        color: '#f866a0,#ecb0c8',
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
        this.$refs['reading-gauge'].innerHTML = ''
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
    generate() {
      let chartContext = {}
      this.id = Math.random().toFixed(2) * 100
      chartContext.id = this.id
      chartContext.selecter = this.$refs['reading-gauge']
      chartContext.width = 100
      chartContext.height = 100
      chartContext.isMaxpointer = false
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
      this.gaugeData.meta.color = '#f866a0,#ecb0c8'
      chartContext.data = this.gaugeData

      this.draw(chartContext)
    },
    draw(chartContext) {
      {
        let self = this
        let data = chartContext.data.meta || null
        let value = chartContext.data.value || null
        let targetPercentage = data.maxPercentage
          ? data.maxPercentage
          : data.maxConstant && data.max
          ? data.max - data.maxConstant / data.max
          : 0
        let showpercentage = data.percent && data.isPlus ? data.percent : -1
        // let showpercentage = (data.maxPercentage && data.maxPercentage > -1) ? data.maxPercentage : ((data.maxConstant && data.max) ? (data.max - (data.maxConstant / data.max)) : 0)
        let percentage = data.value && data.max ? data.value / data.max : 0
        let maxShowValue = data.max
        if (
          chartContext.isMaxpointer &&
          data.maxConstant &&
          data.maxConstant > data.max
        ) {
          if (data.maxPercentage > -1) {
            targetPercentage =
              (data.max / data.maxConstant > 1
                ? 1
                : data.max / data.maxConstant) || 0
            percentage = data.value / data.maxConstant || 0
            maxShowValue = data.maxConstant
          } else {
            targetPercentage =
              (data.max / data.maxConstant > 1
                ? 1
                : data.max / data.maxConstant) || 0
            percentage = data.value / data.maxConstant || 0
            maxShowValue = data.maxConstant
          }
        } else if (
          chartContext.isMaxpointer &&
          data.maxConstant &&
          data.maxConstant < data.max
        ) {
          if (data.maxPercentage === -1 || data.maxPercentage === -0.01) {
            targetPercentage = 1
            percentage = data.value / data.max || 0
            maxShowValue = data.max
          } else {
            targetPercentage = 1
            percentage = data.value / data.max || 0
            maxShowValue = data.max
          }
        }
        let tau = (1 + 5 / 9) * Math.PI,
          width = 100,
          height = 100,
          outerRadius = Math.min(width, height) / 2,
          innerRadius = (outerRadius / 5) * 4.5
        // outerRadius1 = (Math.min(width, height) / 2),
        // innerRadius1 = ((outerRadius / 5) * 4.3),
        // fontSize = (Math.min(width, height) / 4)

        chartContext.arc = d3
          .arc()
          .innerRadius(innerRadius)
          .outerRadius(outerRadius)
          .cornerRadius(outerRadius - innerRadius)
          .startAngle(0)

        chartContext.targetArc = d3
          .arc()
          .innerRadius(innerRadius)
          .outerRadius(outerRadius)

        // chartContext.shadowArc = d3.arc()
        //   .innerRadius(innerRadius1)
        //   .outerRadius(outerRadius1)
        //   .startAngle(0)

        let svg = chartContext.svg
        if (value.enableCenterText1) {
          chartContext.text = svg
            .append('text')
            .html('0%')
            .attr('text-anchor', 'middle')
            .style('font-size', 1.1 + 'vw')
            .style('fill', '#19191B')
            .attr('dy', -2)
            .attr('dx', 0)
            .attr('class', 'gauge-center-text')
        }

        chartContext.svg
          .append('text')
          .attr('text-anchor', 'middle')
          .style('font-size', 0.6 + 'vw')
          .style('fill', '#202024')
          .attr('dy', value.enableCenterText1 ? 13 : 5)
          .attr('dx', 0)
          .attr('class', 'gauge-center-value')
          .html(function() {
            let lable1 = value.centerText[0] ? value.centerText[0].label : ''
            let lable2 = value.centerText[0] ? value.centerText[0].unit : ''
            let isLeft = value.centerText[0]
              ? value.centerText[0].unitLeft || false
              : false
            return lable1.length > 14
              ? lable1.slice(0, 14) + '...'
              : isLeft
              ? lable2 + ' ' + lable1
              : lable1 + ' ' + lable2
          })
        chartContext.svg
          .append('text')
          .attr('text-anchor', 'middle')
          .style('font-size', 0.5 + 'vw')
          .attr('dy', 50)
          .attr('dx', 0)
          .style('text-transform', 'uppercase')
          .style('letter-spacing', '0.3px')
          .text(function() {
            let lable2 = value.centerText[1] ? value.centerText[1].label : ''
            return lable2.length > 14 ? lable2.slice(0, 14) + '...' : lable2
          })
          .style('fill', '#b9b8be')
          .style('font-weight', 'bold')
          .style('letter-spacing', ' 0.3px')
          .style('padding-top', ' 5px')
          .attr('class', 'gauge-center-period')

        chartContext.defs = svg.append('defs')

        chartContext.gradient = chartContext.defs
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
            return value && value.startColor ? value.startColor : '#f87a60'
          })
          .attr('stop-opacity', 0.2)

        chartContext.gradient
          .append('stop')
          .attr('class', 'end')
          .attr('offset', 20 * 100 + '%')
          .attr('stop-color', function() {
            return value && value.startColor ? value.startColor : '#9c5fb8'
          })
          .attr('stop-opacity', 1)
        let tooltipdata = []
        if (chartContext.isMaxpointer) {
          let MaximumData = formatter.formatCardValue(
            Number(maxShowValue),
            value.centerText[3].label,
            value.centerText[3].label.toLowerCase()
          )
          tooltipdata = [
            {
              label: value.centerText[0] ? value.centerText[0].title : '',
              value: value.centerText[0]
                ? value.centerText[0].label +
                  ' ' +
                  (value.centerText[0].unit ? value.centerText[0].unit : '')
                : '',
            },
            {
              label: value.centerText[2] ? value.centerText[2].title : '',
              value: value.centerText[2] ? value.centerText[2].label : '',
              color: value && value.maxColor ? value.maxColor : '#532d80',
            },
            {
              label: 'Maximum',
              value: maxShowValue
                ? maxShowValue && value.centerText[3].label
                  ? MaximumData.value + ' ' + MaximumData.unit
                  : ''
                : '',
              color: value && value.maxColor ? value.maxColor : '#532d80',
            },
          ]
        } else {
          tooltipdata = [
            {
              label: value.centerText[0] ? value.centerText[0].title : '',
              value: value.centerText[0]
                ? value.centerText[0].label +
                  ' ' +
                  (value.centerText[0].unit ? value.centerText[0].unit : '')
                : '',
            },
            {
              label: value.centerText[2] ? value.centerText[2].title : '',
              value: value.centerText[2] ? value.centerText[2].label : '',
              color: value && value.maxColor ? value.maxColor : '#532d80',
            },
          ]
        }
        svg
          .append('path')
          .datum({ endAngle: tau })
          .style('fill', '#f7f8f9')
          .attr('d', chartContext.arc)
          .attr('class', 'arc')
          .on('mouseover', function(d) {
            let tooltipConfig = {
              title1: '',
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              data: tooltipdata,
            }
            if (!this.$mobile) {
              self.showTooltipForCard1(tooltipConfig, 'kpi-gauge-chart')
            }
          })
          .on('mouseout', function(d) {
            self.hideTooltip('kpi-gauge-chart')
          })

        // svg.append('path')
        //   .datum({endAngle: tau})
        //   .style('fill', '#f7f8f9')
        //   .attr('d', chartContext.targetArc)
        //   .attr('class', 'arc')
        percentage = percentage > 1 ? 1 : percentage < -1 ? 0 : percentage
        chartContext.midground = svg
          .append('path')
          .datum({ endAngle: (percentage || 0) * tau })
          .style('fill', function() {
            return 'url(#svgGradient' + chartContext.id + ')'
          })
          .attr('d', chartContext.arc)
          .attr('class', 'arc')
          .on('mouseover', function(d) {
            let tooltipConfig = {
              title1: '',
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              data: tooltipdata,
            }
            if (!this.$mobile) {
              self.showTooltipForCard1(tooltipConfig, 'kpi-gauge-chart')
            }
          })
          .on('mouseout', function(d) {
            self.hideTooltip('kpi-gauge-chart')
          })
        // svg.append('path')
        //   .datum({endAngle: ((percentage || 0) * tau)})
        //   .style('fill', function () {
        //     return 'blue'
        //   })
        //   .attr('d', chartContext.shadowArc)
        //   .attr('class', 'arc')

        if (chartContext.isMaxpointer) {
          chartContext.midground1 = svg
            .append('path')
            .datum({
              startAngle: (targetPercentage - 0.01) * tau,
              endAngle: targetPercentage * tau,
            })
            .style('fill', value && value.maxColor ? value.maxColor : '#532d80')
            .attr('d', chartContext.targetArc)
            .attr('class', 'arc twin')
            .on('mouseover', function(d) {
              let tooltipConfig = {
                title1: '',
                position: {
                  left: d3.event.pageX,
                  top: d3.event.pageY,
                },
                data: tooltipdata,
              }
              if (!this.$mobile) {
                self.showTooltipForCard1(tooltipConfig, 'kpi-gauge-chart')
              }
            })
            .on('mouseout', function(d) {
              self.hideTooltip('kpi-gauge-chart')
            })
        }
        percentage = percentage > 1 ? 1 : percentage < -1 ? 0 : percentage
        let r = innerRadius
        let pointerConfg = {
          pointerWidth: 10,
          pointerHeadLengthPercent: 1,
          pointerTailLength: 5,
          pointerHeadLength: Math.round(r * 1),
          labelInset: 10,
          centerTx: function() {
            return (
              'rotate(' +
              percentage * 280 +
              ') translate(' +
              -25 +
              ',' +
              32 +
              ')'
            )
          },
          minValue: 0,
          maxValue: 0,
          minAngle: -90,
          maxAngle: 90,
          transitionMs: 750,
          majorTicks: 5,
          labelFormat: d3.format('.2'),
          arcColorFn: d3.interpolateHsl(d3.rgb('#e8e2ca'), d3.rgb('#3e6c0a')),
        }

        let pg = svg
          .append('g')
          .attr('class', 'gauge-arrow')
          .attr('transform', pointerConfg.centerTx)

        pg.append('path')
          .attr('class', 'target')
          .attr(
            'd',
            'M0,-1.398088489694245L2.942830956382712,1.6990442448471226L-2.942830956382712,1.6990442448471226Z'
          )
          .attr('fill', function() {
            return value && value.startColor ? value.startColor : '#9c5fb8'
          })
          .on('mouseover', function(d) {
            let tooltipConfig = {
              title1: '',
              position: {
                left: d3.event.pageX,
                top: d3.event.pageY,
              },
              data: tooltipdata,
            }
            if (!this.$mobile) {
              self.showTooltipForCard1(tooltipConfig, 'kpi-gauge-chart')
            }
          })
          .on('mouseout', function(d) {
            self.hideTooltip('kpi-gauge-chart')
          })
        if (this.$mobile) {
          let tooltipdata = null
          if (chartContext.isMaxpointer) {
            let MaximumData = formatter.formatCardValue(
              Number(maxShowValue),
              value.centerText[3].label,
              value.centerText[3].label.toLowerCase()
            )
            tooltipdata = [
              {
                label: value.centerText[0] ? value.centerText[0].title : '',
                value: value.centerText[0]
                  ? value.centerText[0].label +
                    ' ' +
                    (value.centerText[0].unit ? value.centerText[0].unit : '')
                  : '',
              },
              {
                label: value.centerText[2] ? value.centerText[2].title : '',
                value: value.centerText[2] ? value.centerText[2].label : '',
                color: value && value.maxColor ? value.maxColor : '#532d80',
              },
              {
                label: 'Maximum',
                value: maxShowValue
                  ? maxShowValue && value.centerText[3].label
                    ? MaximumData.value + ' ' + MaximumData.unit
                    : ''
                  : '',
                color: value && value.maxColor ? value.maxColor : '#532d80',
              },
            ]
          } else {
            tooltipdata = [
              {
                label: value.centerText[0] ? value.centerText[0].title : '',
                value: value.centerText[0]
                  ? value.centerText[0].label +
                    ' ' +
                    (value.centerText[0].unit ? value.centerText[0].unit : '')
                  : '',
              },
              {
                label: value.centerText[2] ? value.centerText[2].title : '',
                value: value.centerText[2] ? value.centerText[2].label : '',
                color: value && value.maxColor ? value.maxColor : '#532d80',
              },
            ]
          }
          svg
            .on('mouseover', function(d) {
              let tooltipConfig = {
                title1: '',
                position: {
                  left: d3.event.pageX,
                  top: d3.event.pageY,
                },
                data: tooltipdata,
              }
              if (
                document.getElementById('q-app') &&
                tooltipConfig.position &&
                tooltipConfig.position.left
              ) {
                let totalWidth = document.getElementById('q-app').offsetWidth
                let currentWidth = tooltipConfig.position.left
                let tootipWidth = 150
                if (totalWidth / 2 < currentWidth) {
                  tooltipConfig.position.left = currentWidth - tootipWidth
                }
              }
              self.showTooltipForCard1(tooltipConfig, 'kpi-gauge-chart')
            })
            .on('mouseout', function(d) {
              self.hideTooltip('kpi-gauge-chart')
            })
        }

        let arcTween = function arcTween(transition, newAngle) {
          transition.attrTween('d', function(d) {
            if (value.enableCenterText1) {
              let interpolate = d3.interpolate(d.endAngle, newAngle)
              return function(t) {
                d.endAngle = interpolate(t)
                if (showpercentage > -1) {
                  chartContext.text.html(
                    '+' + showpercentage.toFixed(1) + '' + '%'
                  )
                } else {
                  chartContext.text.html(
                    (percentage * 100).toFixed(1) + '' + '%'
                  )
                }
                return chartContext.arc(d)
              }
            }
          })
        }
        percentage = percentage > 1 ? 1 : percentage < -1 ? 0 : percentage
        chartContext.midground
          .transition()
          .duration(1000)
          .call(arcTween, percentage * tau)
      }
    },
  },
}
</script>
<style>
.reading-gauge .arc {
  transform: rotate(-140deg);
}
.layout4 .readingcard-header {
  padding-top: 10px;
  line-height: 0.8rem;
  padding-bottom: 8px;
}
.reading-gauge {
  width: 76%;
  margin: auto;
}
.target {
  transform: rotate(212deg);
}
/* .kpi-gauge-chart{
   box-shadow: 0 4px 18px 0 rgba(0, 0, 0, 0.1) !important;
   border: solid 1px #949090 !important;
   background-color: rgba(0, 0, 0, 0.8) !important;
   color: #fff !important;
   opacity: 0.9 !important;
} */

.card-tooltip {
  position: absolute;
  padding: 8px 12px;
  box-shadow: 0 4px 18px 0 rgba(0, 0, 0, 0.1) !important;
  border: none;
  background-color: rgba(0, 0, 0, 0.75) !important;
  color: #fff !important;
  opacity: 0.9 !important;
  transition: visibility 0.2s, opacity 0.3s linear;
  z-index: 10000;
  max-width: 500px;
  border-radius: 3px;
}

.card-tooltip .axis-row {
  padding: 3px;
  padding-left: 0px;
}

.card-tooltip .tooltip-title {
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
.card-tooltip .axis-color {
  margin: 2px 5px;
}

.card-tooltip .axis-tip {
  font-size: 12px;
  color: #333333bd;
  padding-top: 5px;
}

.card-tooltip .axis-label {
  font-weight: 400;
  padding-right: 8px;
  color: #d8d2d2;
  font-size: 12px;
  letter-spacing: 0.5px;
}
.card-tooltip .icon {
  height: 20px;
  width: 20px;
  margin-right: 4px;
  margin-top: 4px;
}
.card-tooltip .imgcls {
  display: flex;
  cursor: pointer;
  /* align-items: center; */
  flex-direction: row;
}
.card-tooltip .axis-value {
  font-weight: 500;
  font-size: 12.5px;
}

.card-tooltip .axis-unit {
  font-weight: 400;
  font-size: 13px;
  margin-left: 2px;
  color: #979797;
}
.layout4 .readingcard-header {
  letter-spacing: 0.8px !important;
}
</style>
