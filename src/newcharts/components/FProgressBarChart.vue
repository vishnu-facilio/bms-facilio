<template>
  <div class="height100">
    <div class="f-multichart">
      <div :class="['f-multichart-print']">
        <div v-if="chartContext" style="text-align: center;">
          <div class="fc-newchart-container">
            <div ref="newChartEle" class="fc-new-chart fc-boolean-chart"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { bb } from 'billboard.js'
import basechart from 'newcharts/mixins/basechart'
import c3helper from '../helpers/c3-helper'
import JumpToHelper from '@/mixins/JumpToHelper'
import tooltip from '@/graph/mixins/tooltip'
import colorHelper from 'newcharts/helpers/color-helper'
import deepmerge from 'util/deepmerge'

export default {
  mixins: [basechart, JumpToHelper],
  props: ['resultObj', 'isWidget', 'showWidgetLegends', 'colors', 'unit'],
  computed: {},
  watch: {
    data: {
      handler() {
        this.reRender()
      },
      deep: true,
    },
  },
  data() {
    return {
      chartContext: null,
      chartId: Math.random()
        .toString(36)
        .substr(2, 9),
      c3: {
        params: null,
        chart: null,
      },
      updateTimeout: null,
    }
  },
  mounted() {
    this.render()
  },
  beforeDestroy() {
    tooltip.hideTooltip()
    if (this.c3 && this.c3.chart) {
      this.c3.chart.destroy()
    }

    this.chartContext = null
    this.c3.params = null
    this.c3.chart = null
  },
  methods: {
    render() {
      if (this.data) {
        this.chartContext = null

        this.$nextTick(() => {
          this.chartContext = {}
          this.chartContext.options = this.getOptions()
          this.chartContext.dateRange = this.dateRange
          this.chartContext.data = deepmerge.objectAssignDeep({}, this.data)
          this.chartContext.xValueMode = this.isXValueMode(
            this.chartContext.options,
            this.data
          )
          this.chartContext.unitMap = this.chartContext.options.dataPoints
            ? this.getUnitMap(this.chartContext.options.dataPoints)
            : {}
          this.chartContext.enumMap = this.chartContext.options.dataPoints
            ? this.getEnumMap(this.chartContext.options.dataPoints)
            : {}
          if (
            this.chartContext.options.type === 'pie' ||
            this.chartContext.options.type === 'donut'
          ) {
            this.chartContext.options.padding.top = 20
          }

          let c3Params = c3helper.prepareProgressChart(
            this.chartContext,
            this.colors,
            this.unit
          )

          if (c3Params) {
            c3Params.data.onclick = d => {
              this.$emit('drilldown', d)
            }
          }

          let self = this

          if (this.chartContext.options.customizeC3) {
            let defaultOptions = {}
            let mergedOptions = deepmerge.objectAssignDeep(
              defaultOptions,
              c3Params,
              this.chartContext.options.customizeC3
            )
            c3Params = mergedOptions
          }

          if (self.$refs['newChartEle']) {
            c3Params.bindto = self.$refs['newChartEle']

            if (self.fixedChartHeight) {
              c3Params.size.height = self.fixedChartHeight
            }
            self.c3.params = c3Params
            self.c3.chart = bb.generate(c3Params)
          } else {
            this.$nextTick(function() {
              c3Params.bindto = self.$refs['newChartEle']

              if (self.fixedChartHeight) {
                c3Params.size.height = self.fixedChartHeight
              }
              self.c3.params = c3Params
              self.c3.chart = bb.generate(c3Params)
            })
          }
        })
      }
    },

    isXValueMode(options, data) {
      if (options.type === 'pie' || options.type === 'donut') {
        if (options.xColorMap) {
          return true
        } else {
          if (
            data &&
            data.x &&
            Object.keys(data).length === 2 &&
            data.x.length <= 15
          ) {
            let colors = colorHelper.newColorPicker(data.x.length)
            let xColorMap = {}
            for (let i = 0; i < data.x.length; i++) {
              let xVal = data.x[i]
              xColorMap[xVal] =
                options.xColorMap && options.xColorMap[xVal]
                  ? options.xColorMap[xVal]
                  : colors[i]
            }
            options.xColorMap = xColorMap
            return true
          }
        }
      }
      return false
    },

    reRender() {
      if (this.updateTimeout) {
        clearTimeout(this.updateTimeout)
      }
      this.updateTimeout = setTimeout(() => this.render(), 500)
    },

    resize() {
      this.reRender()
      if (this.$refs['newWidget']) {
        this.$refs['newWidget'].resize()
      }
    },
    update() {
      this.reRender()
    },
  },
}
</script>
