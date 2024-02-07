<template>
  <div>
    <div id="piechart" ref="piechart" class="piechart"></div>
    <div id="legend" ref="legend" class="legend" style="display: none;"></div>
  </div>
</template>
<script>
import * as d3 from 'd3'
import basechart from 'charts/mixins/basechart'
import tooltip from '@/graph/mixins/tooltip'
import pie from 'charts/core/pie'

// import legend from 'charts/core/legend'

export default {
  mixins: [basechart, tooltip],
  data() {
    return {
      margin: {
        top: 20,
        right: 20,
        bottom: 20,
        left: 20,
      },
      defaultLayout: {
        width: 400,
        height: 400,
      },
      defaultOptions: {
        type: 'doughnut',
        diff: 5,
        centerText: 'New Data',
        height: 328,
        innerText: true,
        gDiff: 10,
        sublabel: 'Kwh',
      },
      defaultData: [
        {
          value: 67,
          label: 'Life Safety',
        },
        {
          value: 54,
          label: 'Critical',
        },
        {
          value: 23,
          label: 'Maintenance',
        },
        {
          value: 11,
          label: 'Lifes',
        },
        {
          value: 8,
          label: 'LifeK',
        },
        {
          value: 31,
          label: 'LifeCritical',
        },
        {
          value: 43,
          label: 'LifeNew',
        },
      ],
      updateData: [
        {
          value: 67,
          label: 'Lifeg',
        },
      ],
    }
  },
  methods: {
    pushData: function() {},
    update: function(updateData) {
      this.deleteGraph()
    },
    resize() {},
    render() {
      pie.prepareChart(this)
      pie.buildSvg(this)
      pie.buildLayout(this)
      pie.buildShape(this)
      pie.drawSlices(this)
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
  },
  mounted() {
    let self = this
    self.render()
  },
}
</script>
