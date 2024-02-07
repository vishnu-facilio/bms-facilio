import chartModel from 'newcharts/model/chart-model'
import deepmerge from 'util/deepmerge'

export default {
  props: [
    'width',
    'height',
    'data',
    'options',
    'dateRange',
    'alarms',
    'hidecharttypechanger',
    'clientWidth',
    'fixedChartHeight',
    'mergeOption',
  ],
  computed: {
    size() {
      if (this.width && this.height) {
        return {
          width: this.width,
          height: this.height,
        }
      } else if (this.width === null && this.height) {
        return {
          height: this.height,
        }
      }
      return null
    },
  },
  methods: {
    getOptions() {
      let options = this.options ? this.options : {}
      if (this.size) {
        options.size = {
          height: this.$el.clientHeight
            ? this.$el.clientHeight
            : this.size.height,
        }
      }
      if (this.clientWidth && this.$el.clientWidth) {
        if (!options.size) {
          options.size = {}
        }
        options.size.width = this.$el.clientWidth
      }

      if (this.$route.query && this.$route.query.print) {
        if (!options.size) {
          options.size = {}
        }
        options.size.width = 800
      }

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
    size: function() {
      if (this.resize) {
        this.resize()
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
