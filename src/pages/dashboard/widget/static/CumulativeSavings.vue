<template>
  <div class="height100 energy-saving-card">
    <shimmer-loading :config="{ type: 'form' }" v-show="loading">
    </shimmer-loading>
    <div class="dragabale-card  saving-card">
      <div
        class="f14 fw6 uppercase f16 energy-savings-txt"
        style="padding:8% 20% 6%;"
      >
        {{ $t('panel.card.savings') }}
      </div>
      <div class="progressParent energy-saving">
        <div id="donutProgress"></div>
      </div>
      <div class="sv-month">{{ $t('panel.tyre.month') }}</div>
      <div class="sv-date">
        {{ $t('panel.card.as_on') }} {{ day }} {{ month }}
      </div>
    </div>
  </div>
</template>
<script>
import * as d3 from 'd3'
import moment from 'moment-timezone'
import shimmerLoading from '@/ShimmerLoading'
export default {
  props: ['widget', 'currentDashboard'],
  data() {
    return {
      loading: true,
      percentage: 0,
      data: null,
      day: moment().format('Do'),
      month: moment().format('MMM'),
    }
  },
  components: {
    shimmerLoading,
  },
  computed: {
    buildingId() {
      if (this.currentDashboard) {
        return this.currentDashboard.buildingId
      }
      if (this.$route.params.buildingid) {
        return parseInt(this.$route.params.buildingid)
      }
    },
  },
  mounted() {
    this.loadcardData()
  },
  methods: {
    loadcardData() {
      let self = this
      self.loading = true
      let params = {
        widgetId: self.widget.id,
        reportSpaceFilterContext: { buildingId: self.buildingId },
      }
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.data = self.prepareData(response.data.cardResult)
          self.loading = false
          self.drawChrat()
        })
        .catch(() => {})
    },
    prepareData(data) {
      let d = {}
      this.percentage = data.savedPercentage || 0
      d.percentage = data.savedPercentage || 0
      d.savedConsumption = this.formater1(data.savedConsumption)
      d.baselineConsumption = this.formater1(data.baselineConsumption)
      d.baselineCost = this.formater(data.baselineCost)
      d.savedCost = this.formater(data.savedCost)
      return d
    },
    formater(value) {
      if (value > -1) {
        value = value.toFixed(0)
        value = value.toString()
        let unit = ''
        if (value.length > 6) {
          value = (value / 1000000).toFixed(2)
          unit = 'M'
        } else if (value.length > 3) {
          value = (value / 1000).toFixed(2)
          unit = 'k'
        }
        return Number(value) + unit
      }
    },
    formater1(value) {
      if (value > -1) {
        value = value.toFixed(0)
        value = value.toString()
        let unit = 'kWh'
        if (value.length > 6) {
          value = (value / 1000000).toFixed(2)
          unit = 'GWh'
        } else if (value.length > 3) {
          value = (value / 1000).toFixed(2)
          unit = 'MWh'
        }
        return Number(value) + unit
      }
    },
    drawChrat() {
      this.percentage = this.percentage.toFixed(2)
      let percentage = (this.percentage ? this.percentage : 0) / 100
      let tau = (1 + 4 / 9) * Math.PI,
        width = 120,
        height = 120,
        outerRadius = Math.min(width, height) / 2,
        innerRadius = (outerRadius / 5) * 4.5,
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
        .text(this.data.savedCost)
        .attr('text-anchor', 'middle')
        .style('font-size', fontSize / 2 + 'px')
        .style('fill', '#333')
        .attr('dy', -fontSize)
        .attr('dx', -1 - this.data.savedCost)
        .style('fill', '#fff')
      let textUnit = svg
        .append('text')
        .text('AED')
        .style('font-size', fontSize / 4 + 'px')
        .style('fill', '#333')
        .style('font-weight', 'bold')
        .attr('dy', -fontSize)
        .attr('dx', this.data.savedCost.length * 3 + 1)
        .style('fill', '#fff')
      svg
        .append('text')
        .text(this.data.savedConsumption)
        .attr('text-anchor', 'middle')
        .style('font-size', fontSize / 3 + 'px')
        .style('fill', '#333')
        .attr('class', 'op6')
        .attr('dy', -14)
        .attr('dx', 2)
        .style('fill', '#fff')

      svg
        .append('text')
        .text(this.percentage + '% SAVINGS')
        .attr('text-anchor', 'middle')
        .style('font-size', fontSize / 4 + 'px')
        .style('fill', '#333')
        .attr('class', 'savings-txt')
        .attr('dy', -3)
        .attr('dx', 2)
        .style('fill', '#53c6e3')

      svg
        .append('line')
        .style('stroke', '#ffffff')
        .style('stroke-width', '1')
        .style('opacity', '0.1')
        .attr('dy', fontSize)
        .attr('dx', 2)
        .attr('x1', 30)
        .attr('x2', -25)
        .attr('y1', 5)
        .attr('y2', 5)

      let fontSizeChange =
        this.data.baselineCost + ' AED for ' + this.data.baselineConsumption
      // fontSize.length = fontSizeChange
      svg
        .append('text')
        .text(fontSizeChange)
        .attr('text-anchor', 'middle')
        .style('font-size', fontSize / (fontSizeChange.length / 5) + 'px')
        .style('fill', '#333')
        .attr('dy', fontSize * (2.5 / 10) * 3)
        .attr('dx', 2)
        .attr('class', 'baseline-aed-kwh')
        .style('fill', '#fff')

      svg
        .append('text')
        .text('BASELINE MODEL')
        .attr('text-anchor', 'middle')
        .style('font-size', fontSize / 5 + 'px')
        .style('fill', '#333')
        .attr('dy', fontSize * (3.5 / 10) * 3)
        .attr('dx', 2)
        .attr('class', 'baseline-model-txt')
        .style('fill', '#fff')

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
        .attr('stop-color', '#96d6b7')
        .attr('stop-opacity', 1)

      gradient
        .append('stop')
        .attr('class', 'end')
        .attr('offset', percentage * 100 + '%')
        .attr('stop-color', '#39b2c2')
        .attr('stop-opacity', 1)

      svg
        .append('path')
        .datum({ endAngle: tau })
        .style('fill', '#ffffff')
        .style('opacity', '0.1')
        .attr('d', arc)
        .style('transform', 'rotate(-130deg)')

      let midground = svg
        .append('path')
        .datum({ endAngle: 0 * tau })
        .style('fill', 'url(#svgGradient)')
        .attr('d', arc)
        .style('transform', 'rotate(-130deg)')

      midground
        .transition()
        .duration(750)
        .call(arcTween, percentage * tau, this.data)
      function arcTween(transition, newAngle, data) {
        transition.attrTween('d', function(d) {
          let interpolate = d3.interpolate(d.endAngle, newAngle)

          return function(t) {
            d.endAngle = interpolate(t)
            text.text(data.savedCost)
            text.attr('dx', -1 - data.savedCost.length)
            textUnit.attr('dx', data.savedCost.length * 3)
            return arc(d)
          }
        })
      }
    },
  },
}
</script>
<style>
.progressParent {
  margin: auto;
}
.energy-saving {
  padding: 17%;
  padding-top: 0;
  padding-bottom: 0;
}
.saving-card {
  background-image: linear-gradient(to bottom, #5d2fbc, #3b236e);
  color: #fff;
  height: 100%;
}
.energy-savings-txt {
  font-size: 1vw;
  font-weight: bold;
  letter-spacing: 1.5px;
  text-align: center;
  color: #ffffff;
}
.savings-txt {
  font-weight: 500;
  letter-spacing: 0.5px;
  text-align: center;
  color: #53c6e3;
}
.sv-month {
  font-size: 0.8vw;
  font-weight: bold;
  letter-spacing: 0.9px;
  text-align: center;
  color: #ffffff;
  line-height: 20px;
  position: relative;
  top: -9px;
}
.sv-date {
  font-size: 0.8vw;
  letter-spacing: 0px;
  text-align: center;
  color: #ffffff;
  opacity: 0.7;
  position: relative;
  top: -8px;
}
.baseline-aed-kwh {
  font-size: 12px;
  letter-spacing: 0.3px;
  color: #ffffff;
}
.baseline-model-txt {
  font-size: 11px;
  letter-spacing: 0.5px;
  text-align: center;
  color: #ffffff;
  opacity: 0.7;
}
.energy-saving-card .shimmer {
  height: 100%;
}
</style>
