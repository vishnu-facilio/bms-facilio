import chartModel from 'newcharts/model/chart-model'
import deepmerge from 'util/deepmerge'
import debounce from 'lodash/debounce'

export default {
  props: {
    width: {},
    height: {},
    data: {},
    options: {},
    dateRange: {},
    alarms: {},
    hidecharttypechanger: {},
    clientWidth: {},
    fixedChartHeight: {},
    mergeOption: {},
    widgetBodyDimension: {
      type: Object,
      default: () => ({
        widgetBodyWidth: 0,
        widgetBodyHeight: 0,
      }),
    },
  },
  computed: {
    chartDimension() {
      // We recieve the widget's body dimension from widget wrapper,
      // now if the widget has chart legend we have to adjust the
      // size of the chart, making sure the legend and the chart
      // takes appropriate amount of space.
      const {
        widgetBodyDimension: { widgetBodyWidth, widgetBodyHeight },
      } = this
      return {
        width: widgetBodyWidth,
        height: widgetBodyHeight,
      }
    },
  },
  methods: {
    getOptions() {
      let options = this.options ? this.options : {}
      const {
        chartDimension: { height },
      } = this
      options.size = {
        height: height ? height : this.$el.clientHeight,
      }

      // if (this.clientWidth && this.$el.clientWidth) {
      //   if (!options.size) {
      //     options.size = {}
      //   }
      //   options.size.width = this.$el.clientWidth
      // }

      // if (this.$route.query && this.$route.query.print) {
      //   if (!options.size) {
      //     options.size = {}
      //   }
      //   options.size.width = 800
      // }

      let defaultOptions = {}
      defaultOptions = deepmerge.objectAssignDeep(
        defaultOptions,
        chartModel.constant,
        options
      )
      return defaultOptions
    },
    getUnitMap(dataPoints) {
      let unitMap = {}
      let points = []
      if (dataPoints.length) {
        dataPoints.forEach(dp => {
          if (dp.type === 'group' || dp.type === 'systemgroup') {
            points.push(...dp.children)
          } else {
            points.push(dp)
          }
        })
      }
      if (points.length) {
        points.forEach(dp => {
          unitMap[dp.key] = dp.unitStr
        })
      }
      return unitMap
    },
    getEnumMap(dataPoints) {
      let enumMap = {}
      let points = []
      if (dataPoints.length) {
        dataPoints.forEach(dp => {
          if (dp.type === 'group') {
            points.push(...dp.children)
          } else {
            points.push(dp)
          }
        })
      }
      if (points.length) {
        points.forEach(dp => {
          enumMap[dp.key] = dp.enumMap
        })
      }
      return enumMap
    },
  },
  watch: {
    chartDimension: function() {
      if (this.resize) {
        const self = this
        const debouncedFunction = debounce(function() {
          self.resize()
        }, 500)
        debouncedFunction()
      }
    },
    options: {
      handler: function() {
        if (this.update) {
          this.update()
        }
      },
      deep: true,
    },
  },
}
