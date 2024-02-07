import colors from 'charts/helpers/colors'
import constant from 'charts/helpers/constant'
import common from 'charts/helpers/common'
import * as d3 from 'd3'

export default {
  props: [
    'width',
    'height',
    'data',
    'options',
    'type',
    'alarms',
    'rightmar',
    'config',
  ],
  computed: {
    layout() {
      if (this.width && this.height) {
        return {
          width: this.width,
          height: this.height,
        }
      }
      return null
    },
  },
  methods: {
    getLayout() {
      let w = {
        width:
          parseInt(d3.select(this.$el).style('width')) -
          (this.rightmar ? this.rightmar : 50),
        height: 400,
      }
      if (this.layout) {
        w.height = this.layout.height
      }
      return w
    },
    getWidth() {
      return this.getLayout().width
    },
    getHeight() {
      return this.getLayout().height
    },
    getAlarms() {
      return this.alarms
    },
    getMainData() {
      return this.isMultiData() ? this.data[0].data : this.data
    },
    getYAxisRange() {
      if (this.isMultiData()) {
        let y1axisArray = []
        let y2axisArray = []
        let y2Series = []
        for (let d of this.data) {
          let yMin = 0
          let yMax = 0
          if (d.options.type === 'stackedbar') {
            let minMax = common.getTotalMinMax(d.data)
            yMin = minMax.min
            yMax = minMax.max
          } else {
            let minMax = common.getMinMax(d.data)
            yMin = minMax.min
            yMax = minMax.max
          }
          if (d.options.y2axis) {
            y2axisArray.push(yMin)
            y2axisArray.push(yMax)
            y2Series.push(d)
          } else {
            y1axisArray.push(yMin)
            y1axisArray.push(yMax)
          }
        }

        let result = {
          y1axis: {
            min: d3.min(y1axisArray),
            max: d3.max(y1axisArray),
          },
        }
        if (y2axisArray && y2axisArray.length) {
          result.y2axis = {
            min: d3.min(y2axisArray),
            max: d3.max(y2axisArray),
            series: y2Series,
          }
        }
        return result
      } else {
        let yMin = 0
        let yMax = 0
        if (this.options.type === 'stackedbar') {
          let minMax = common.getTotalMinMax(this.data)
          yMin = minMax.min
          yMax = minMax.max
        } else {
          let minMax = common.getMinMax(this.data)
          yMin = minMax.min
          yMax = minMax.max
        }

        return {
          y1axis: {
            min: yMin,
            max: yMax,
          },
        }
      }
    },
    isMultiData() {
      if (this.data && this.data.length) {
        if (this.data[0].data) {
          return true
        }
      }
      return false
    },
    isSeriesData() {
      if (this.data && this.data.length >= 2) {
        return true
      }
      return false
    },
    getColorSchema() {
      let options = this.getOptions()
      if (options && options.colorSchema) {
        return options.colorSchema
      }
      return colors.default
    },
    getOptions() {
      let options = this.options
        ? this.options
        : this.isMultiData()
        ? this.getMainData()[0].options
        : {}

      let commonOptions = constant.defaultOptions.common
        ? constant.defaultOptions.common
        : {}
      let barDefaultOptions = constant.defaultOptions[this.type]
        ? constant.defaultOptions[this.type]
        : {}

      let defaultOptions = {}
      defaultOptions = Object.assign(
        defaultOptions,
        commonOptions,
        barDefaultOptions
      )

      let mergedOptions = {}
      Object.assign(mergedOptions, defaultOptions, options)

      return mergedOptions
    },
    getChartOptions(options) {
      if (!options) {
        options = {}
      }

      let commonOptions = constant.defaultOptions.common
        ? constant.defaultOptions.common
        : {}
      let chartDefaultOptions = constant.defaultOptions[options.type]
        ? constant.defaultOptions[options.type]
        : {}

      let defaultOptions = {}
      defaultOptions = Object.assign(
        defaultOptions,
        commonOptions,
        chartDefaultOptions
      )

      let mergedOptions = {}
      Object.assign(mergedOptions, defaultOptions, options)
      return mergedOptions
    },
    getMargin() {
      let marginObj = this.getOptions().margin
        ? this.getOptions().margin
        : constant.defaultMargin

      if (this.isMultiData()) {
        let y2Axis = this.data.find(
          d => typeof d.options.y2axis !== 'undefined'
        )
        if (y2Axis) {
          marginObj.right = 70
        }
      }
      return marginObj
    },
    getLegends() {
      // get legends with color
      let colorSchema = null
      let isAarry = common.isValueArray(this.getMainData())
      let arryColor = this.getColorSchema().length
      let colorNewMapping = {}
      if (
        typeof this.getOptions().axis === 'undefined' ||
        this.getOptions().axis
      ) {
        if (isAarry) {
          colorSchema = this.getColorSchema()
        } else {
          colorSchema = [this.getColorSchema()[0]]
        }
      } else {
        colorSchema = this.getColorSchema()
      }
      let colorMapping
      if (isAarry) {
        common.getGroup(this.getMainData()).forEach(function(v, i) {
          colorNewMapping[v] = colorSchema[i % arryColor]
        })
        if (this.getOptions() && this.getOptions().metaJson) {
          let options = this.getOptions()
          common.getGroup(this.getMainData()).forEach(function(v, i) {
            colorNewMapping[v] = options.metaJson[v]
              ? options.metaJson[v]
              : colorNewMapping[v]
          })
        }
        colorMapping = colorNewMapping
      } else {
        if (this.type === 'pie' || this.type === 'doughnut') {
          if (this.getMainData()) {
            this.getMainData().map(function(d, i) {
              colorNewMapping[d.label] = colorSchema[i % arryColor]
            })
          }
          colorMapping = colorNewMapping
        } else {
          colorMapping = colorSchema
        }
      }
      return colorMapping
    },
  },
  watch: {
    layout: function() {
      setTimeout(() => {
        if (this.resize) {
          this.resize()
        }
      }, 250)
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
