<template>
  <div
    id="spark-heatmap"
    ref="sparkline-heatmap"
    class="p10 gauge"
    :style="{ 'max-width': zoom + '%', 'max-height': zoom + '%' }"
  ></div>
</template>
<script>
import * as d3 from 'd3'
export default {
  // eslint-disable-next-line no-unused-vars
  props: ['value'],
  data() {
    return {
      zoom: 100,
      innerWidth: 100,
      innerHeight: 100,
      type: 'sparkline-heatmap',
      id: Math.round(Math.random().toFixed(2) * 100),
    }
  },
  created() {
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    this.destroyTooltip()
  },
  mounted() {
    this.$nextTick(() => {
      this.handleResize()
    })
  },
  methods: {
    handleResize() {
      if (this.$el) {
        this.innerWidth = this.$el.clientWidth
        this.innerHeight = this.$el.clientHeight || 200
      }
      this.zoom =
        window.devicePixelRatio > 1
          ? Math.round(window.devicePixelRatio * 50)
          : Math.round(window.devicePixelRatio * 100)
      this.regen()
    },
    regen() {
      if (this.$refs['sparkline-heatmap'])
        this.$refs['sparkline-heatmap'].innerHTML = ''
      this.$nextTick(() => {
        this.destroyTooltip()
        this.generate()
      })
    },
    destroyTooltip() {
      document
        .getElementsByClassName('sparkline-heat-maptooltip')
        .forEach(element => {
          element.remove()
        })
    },
    generate() {
      let chartContext = {}

      let { innerWidth, innerHeight, id } = this
      chartContext.id = this.id
      chartContext.selecter = this.$refs['sparkline-heatmap']
      let width = 200,
        height = 30

      let dataValue = this.getData().map(rt => rt.value)
      let min = d3.min(dataValue)
      let max = d3.max(dataValue)

      let svg = d3
        .select(chartContext.selecter)
        .append('svg')
        .attr('width', '100%')
        .attr('height', '100%')
        .attr('viewBox', '0 0 ' + width + ' ' + height)
        .attr('preserveAspectRatio', 'xMinYMin')
        .append('g')

      let time = [
        '12am',
        '1am',
        '2am',
        '3am',
        '4am',
        '5am',
        '6am',
        '7am',
        '8am',
        '9am',
        '10am',
        '11am',
        '12pm',
        '1pm',
        '2pm',
        '3pm',
        '4pm',
        '5pm',
        '6pm',
        '7pm',
        '8pm',
        '9pm',
        '10pm',
        '11pm',
      ]
      let emptyArray = [
        '12am',
        '1am',
        '2am',
        '3am',
        '4am',
        '5am',
        '6am',
        '7am',
        '8am',
        '9am',
        '10am',
        '11am',
        '12pm',
        '1pm',
        '2pm',
        '3pm',
        '4pm',
        '5pm',
        '6pm',
        '7pm',
        '8pm',
        '9pm',
        '10pm',
        '11pm',
      ]

      let x = d3
        .scaleBand()
        .range([0, width])
        .domain(time)

      // Build color scale
      let myColor = d3
        .scaleLinear()
        .range(['#f5d4bb', '#fa8830'])
        .domain([1, max])

      let div = d3
        .select('body')
        .append('div')
        .attr('class', 'sparkline-heat-maptooltip')
        .style('opacity', 0)
      svg
        .selectAll()
        .data(emptyArray, d => {
          return d
        })
        .enter()
        .append('rect')
        .attr('x', function(d) {
          return x(d)
        })
        .attr('width', x.bandwidth())
        .attr('height', height)
        .style('fill', () => {
          return '#f1f1f1'
        })
      svg
        .selectAll()
        .data(this.getData(), d => {
          return d.time
        })
        .enter()
        .append('rect')
        .attr('x', function(d) {
          return x(d.time)
        })
        .attr('width', x.bandwidth())
        .attr('height', height)
        .style('fill', function(d) {
          return myColor(d.value)
        })
        .on('mouseover', (e, d) => {
          div
            .transition()
            .duration(100)
            .style('opacity', 0.9)
          div
            .html(d.time + '<br/>' + d.value)
            .style('left', e.pageX + 'px')
            .style('top', e.pageY - 28 + 'px')
        })
        .on('mouseout', function(d) {
          div
            .transition()
            .duration(500)
            .style('opacity', 0)
        })
    },
    getData() {
      return [
        {
          time: '1am',
          value: 11,
        },
        {
          time: '2am',
          value: 21,
        },
        {
          time: '3am',
          value: 31,
        },
        {
          time: '5am',
          value: 15,
        },
        {
          time: '8am',
          value: 17,
        },
        {
          time: '10am',
          value: 32,
        },
        {
          time: '11am',
          value: 33,
        },
        {
          time: '11pm',
          value: 20,
        },
      ]
    },
  },
}
</script>
<style>
.gauge {
  width: 100%;
  height: 100%;
  margin: auto;
}
.sparkline-heat-maptooltip {
  position: absolute;
  text-align: center;
  width: 60px;
  height: 28px;
  padding: 2px;
  font: 12px sans-serif;
  background: #313131;
  border: 0px;
  border-radius: 2px;
  color: #fff;
  pointer-events: none;
}
</style>
