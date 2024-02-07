<template>
  <div
    ref="legend-container"
    class="row legend-container"
    style="position:relative;margin-top: 10px;"
    v-if="options.widgetLegend.show"
    v-bind:class="{ singlecard: legends.length === 1 || legends.length === 0 }"
  >
    <template v-if="isPrinting">
      <div v-for="(legend, idx) in legends" :key="idx" class="variance-item">
        <div
          v-for="(legand, idx) in legend"
          :key="idx"
          class="variance-item-display"
        >
          <div
            class="ellipsis variance-point"
            v-if="anyPointHidden && legand.dataPoint.visible"
          >
            {{ '&nbsp;' }}
          </div>
          <!-- Temp -->
          <div class="ellipsis variance-x-label">
            {{ legand.xLabel || '&nbsp;' }}
          </div>
          <div
            class="ellipsis variance-point"
            v-if="legand.dataPoint.visible === false"
          >
            {{ legand.dataPoint.label }}
          </div>
          <div
            v-if="isBaseLineSelected && legand.dataPoint.visible"
            class="variance-baseline"
            :style="getBaseLineStyle(legand)"
          ></div>
          <svg
            v-else-if="
              legand.showColor &&
                legand.dataPoint &&
                [8].includes(legand.dataPoint.dataTypeId)
            "
            class="variance-circle flLeft"
            :style="{ 'margin-top': isSmall ? '3px' : '5px' }"
            @mouseover="focusLegend(legand.dataPoint.key)"
            @mouseout="unfocusLegend(legand.dataPoint.key)"
          >
            <circle
              :cx="isSmall ? 5 : 7"
              :cy="isSmall ? 5 : 7"
              :r="isSmall ? 5 : 7"
              :fill="legand.color"
            />
          </svg>
          <svg
            v-else-if="legand.showColor"
            class="variance-circle flLeft"
            :style="{ 'margin-top': isSmall ? '3px' : '5px' }"
            @mouseover="focusLegend(legand.dataPoint.key)"
            @mouseout="unfocusLegend(legand.dataPoint.key)"
          >
            <circle
              :cx="isSmall ? 5 : 7"
              :cy="isSmall ? 5 : 7"
              :r="isSmall ? 5 : 7"
              :fill="legand.dataPoint.color"
            />
          </svg>

          <div
            :class="{
              'variance-data': legand.showColor && !isBaseLineSelected,
            }"
          >
            <div
              class="variance-value ellipsis"
              :title="
                (legand.unit === '$' ? legand.unit : '') +
                  legand.value +
                  ' ' +
                  (legand.unit && legand.unit !== '$' ? legand.unit : '')
              "
              data-placement="right"
              data-arrow="true"
              v-tippy
              :style="getvalueStyle(legand.value + '', legends.length)"
            >
              <span
                v-if="legand.unit === '$'"
                class="variance-unit variance-unit-left"
                v-html="legendd.unit"
                :style="getunitStyle(legand.value + '', legends.length)"
              ></span>
              {{ legand.value }}
              <span
                v-if="legand.unit && legand.unit !== '$'"
                class="variance-unit"
                v-html="legand.unit"
                :style="getunitStyle(legand.value + '', legends.length)"
              ></span>
            </div>
            <div class="variance-label uppercase">
              {{
                legand.varianceLabel ||
                  $constants.VarianceLabels[legand.name] ||
                  legand.name
              }}
            </div>
          </div>
        </div>
      </div>
    </template>
    <el-carousel height="100px" :autoplay="false" v-else>
      <el-carousel-item
        v-for="(item, index) in legends"
        :key="index"
        class="widgetleagend-slider"
      >
        <div
          v-if="legend.dataPoint.axes !== 'x'"
          v-for="(legend, idx) in item"
          :key="idx"
          class="variance-item"
        >
          <div
            class="ellipsis variance-point"
            v-if="anyPointHidden && legend.dataPoint.visible"
          >
            {{ '&nbsp;' }}
          </div>
          <!-- Temp -->
          <div class="ellipsis variance-x-label">
            {{ legend.xLabel || '&nbsp;' }}
          </div>
          <div
            class="ellipsis variance-point"
            v-if="legend.dataPoint.visible === false"
          >
            {{ legend.dataPoint.label }}
          </div>
          <div
            v-if="isBaseLineSelected && legend.dataPoint.visible"
            class="variance-baseline"
            :style="getBaseLineStyle(legend)"
          ></div>
          <svg
            v-else-if="
              legend.showColor &&
                legend.dataPoint &&
                [8].includes(legend.dataPoint.dataTypeId)
            "
            class="variance-circle flLeft"
            :style="{ 'margin-top': isSmall ? '3px' : '5px' }"
            @mouseover="focusLegend(legend.dataPoint.key)"
            @mouseout="unfocusLegend(legend.dataPoint.key)"
          >
            <circle
              :cx="isSmall ? 5 : 7"
              :cy="isSmall ? 5 : 7"
              :r="isSmall ? 5 : 7"
              :fill="legend.color"
            />
          </svg>
          <svg
            v-else-if="legend.showColor"
            class="variance-circle flLeft"
            :style="{ 'margin-top': isSmall ? '3px' : '5px' }"
            @mouseover="focusLegend(legend.dataPoint.key)"
            @mouseout="unfocusLegend(legend.dataPoint.key)"
          >
            <circle
              :cx="isSmall ? 5 : 7"
              :cy="isSmall ? 5 : 7"
              :r="isSmall ? 5 : 7"
              :fill="legend.dataPoint.color"
            />
          </svg>

          <div
            :class="{
              'variance-data': legend.showColor && !isBaseLineSelected,
            }"
          >
            <div
              class="variance-value ellipsis"
              :title="
                (legend.unit === '$' ? legend.unit : '') +
                  legend.value +
                  ' ' +
                  (legend.unit && legend.unit !== '$' ? legend.unit : '')
              "
              data-placement="right"
              data-arrow="true"
              v-tippy
              :style="getvalueStyle(legend.value + '', legends.length)"
            >
              <span
                v-if="legend.unit === '$'"
                class="variance-unit variance-unit-left"
                v-html="legend.unit"
                :style="getunitStyle(legend.value + '', legends.length)"
              ></span>
              {{ legend.value }}
              <span
                v-if="legend.unit && legend.unit !== '$'"
                class="variance-unit"
                v-html="legend.unit"
                :style="getunitStyle(legend.value + '', legends.length)"
              ></span>
            </div>
            <div class="variance-label uppercase">
              {{
                legend.varianceLabel ||
                  $constants.VarianceLabels[legend.name] ||
                  legend.name
              }}
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script>
import moment from 'moment-timezone'
import * as d3 from 'd3'

export default {
  props: ['chart', 'options', 'reportVarianceData', 'data'],
  data() {
    return {
      legends: [],
      item: [],
      xLabelVariance: ['min', 'max'],
    }
  },
  mounted() {
    this.initData()
  },
  computed: {
    chunkSize() {
      if (this.$el && this.$el.offsetWidth) {
        return parseInt(this.$el.offsetWidth / 160) > 8
          ? 8
          : parseInt(this.$el.offsetWidth / 160) < 1
          ? 2
          : parseInt(this.$el.offsetWidth / 160)
      } else {
        return 4
      }
    },
    isSmall() {
      return false
    },
    isPrinting() {
      return this.$route.query.print
    },
    allPoints() {
      let points = []
      if (this.options) {
        this.options.dataPoints.forEach(dp => {
          if (dp.type === 'group') {
            points.push(...dp.children)
          } else {
            points.push(dp)
          }
        })
      }
      return points
    },
    anyPointHidden() {
      return this.allPoints.some(dp => dp.visible === false)
    },
    isBaseLineSelected() {
      return this.allPoints.some(dp => dp.isBaseLine)
    },
    allPointsMap() {
      let pointsMap = []
      if (this.options) {
        this.options.dataPoints.forEach(dp => {
          if (dp.type === 'group') {
            dp.children.reduce((ch, value) => {
              ch[value.alias] = value
              return ch
            }, pointsMap)
          } else {
            pointsMap[dp.alias || dp.key] = dp
          }
        })
      }
      return pointsMap
    },
  },
  watch: {
    options: {
      handler() {
        this.initData()
      },
      deep: true,
    },
    reportVarianceData() {
      this.initData()
    },
  },
  methods: {
    initData() {
      this.legends = []
      this.item = []
      if (!this.options.widgetLegend || !this.reportVarianceData) {
        return
      }

      this.allPoints.forEach(dp => {
        this.options.widgetLegend.variances[dp.alias || dp.key].forEach(
          variance => {
            this.setVariance(dp, variance)
          }
        )
      })
      let array = this.prepareitem(this.legends, this.chunkSize)
      this.legends = this.$helpers.cloneObject(array)
    },
    getvalueStyle(data, length) {
      let size = 1.2
      if (data.length < 7) {
        size = 1.3
      } else if (data.length < 8) {
        size = 1.2
      } else if (data.length < 10) {
        size = 1.1
      } else if (data.length < 12) {
        size = 1.0
      } else if (data.length >= 12) {
        size = 0.8
      }
      return 'font-size:' + size + 'vw;'
    },
    getunitStyle(data, length) {
      let size = 14
      if (data.length < 7) {
        size = 13
      } else if (data.length < 8) {
        size = 13
      } else if (data.length < 10) {
        size = 11
      } else if (data.length < 13) {
        size = 10
      } else if (data.length > 12) {
        size = 10
      }
      return 'font-size:' + size + 'px;'
    },
    setVariance(dp, variance) {
      let enumKey
      if (dp.dataTypeId === 4 || dp.dataTypeId === 8) {
        enumKey = variance
        variance = 'duration'
      }
      let varianceVal = this.reportVarianceData[
        (dp.alias || dp.key) + '.' + variance
      ]
      if (varianceVal === undefined || varianceVal === null) {
        return
      }
      let formattedVal = this.formatValue(dp, varianceVal, enumKey)
      let legend = {
        name: enumKey ? dp.enumMap[enumKey] : variance,
        value: formattedVal.value,
        unit: formattedVal.unit,
        isBaseLine: dp.isBaseLine,
        showColor:
          (this.isBaseLineSelected || this.allPoints.length > 1) && dp.visible,
        dataPoint: dp,
      }
      if (dp.dataTypeId === 8 && dp.enumColorMap) {
        legend.color = dp.enumColorMap[enumKey]
      }
      if (
        this.xLabelVariance.includes(variance) &&
        this.data[dp.alias || dp.key]
      ) {
        let xAxisDataType = this.options.axis.x.datatype
        this.data[dp.alias || dp.key].forEach((val, idx) => {
          let value = !isNaN(val) ? Number(val) : val
          if (value === varianceVal) {
            let dateObj = null
            if (
              this.options.axis.x.time &&
              this.options.axis.x.time.format.format
            ) {
              dateObj = moment(
                this.data.x[idx],
                this.options.axis.x.time.format.format
              ).toDate()
            } else {
              dateObj = new Date(this.data.x[idx])
            }
            let time = this.$helpers.getTimeInOrg(dateObj)
            legend.xLabel =
              xAxisDataType === 'date_time'
                ? this.$options.filters.formatDate(time)
                : ''
          }
        })
      }
      this.legends.push(legend)
    },
    prepareitem(item, chunkSize) {
      let groups = [],
        i
      for (i = 0; i < item.length; i += chunkSize) {
        groups.push(item.slice(i, i + chunkSize))
      }
      return groups
    },
    getBaseLineStyle(legend) {
      return {
        border:
          (legend.isBaseLine ? 'dashed' : 'solid') +
          ' 1.5px ' +
          legend.dataPoint.color,
      }
    },
    resize() {},
    focusLegend(key) {
      if (this.chart) {
        this.chart.focus(key)
      }
    },
    unfocusLegend() {
      if (this.chart) {
        this.chart.revert()
      }
    },
    formatValue(dp, value, enumKey) {
      let json = { value: value, unit: dp.unitStr }
      if (!value) {
        return json
      }
      if (enumKey) {
        json.value = ''
        let currentValue = value[enumKey]
        if (!currentValue) {
          json.value = '0 Hours'
        }
        if (currentValue >= 86400000) {
          let days = Math.floor(currentValue / 86400000)
          json.value =
            Math.round(days * 100) / 100 + ' Day' + (days > 1 ? 's' : '')
          currentValue -= days * 86400000
        }
        if (currentValue >= 3600000) {
          let hours = currentValue / 3600000
          if (!json.value) {
            hours = Math.floor(hours)
          } else {
            json.value += ' '
          }
          json.value +=
            Math.round(hours * 100) / 100 + ' Hour' + (hours > 1 ? 's' : '')
          currentValue -= hours * 3600000
        }
        if (currentValue >= 60000) {
          let minutes = currentValue / 60000
          if (json.value) {
            json.value += ' '
          }
          json.value +=
            Math.round(minutes * 100) / 100 +
            ' Minute' +
            (minutes > 1 ? 's' : '')
        } else if (currentValue > 0) {
          let sec = currentValue / 60
          if (json.value) {
            json.value += ' '
          }
          json.value +=
            Math.round(sec * 100) / 100 + ' Seconds' + (sec > 1 ? 's' : '')
        }
        return json
      }
      // Temporary handled...should get from chart state
      else if (dp.metric === 'CURRENCY') {
        if (value > 10000) {
          json.value = value / 1000
          json.value = d3.format(',.2f')(json.value) + 'k'
        } else {
          json.value = d3.format(',.2f')(json.value)
        }
      } else if (dp.unitStr && dp.unitStr.toLowerCase() === 'kwh') {
        if (value > 10000) {
          json.value = value / 1000
          json.unit = 'MWh'
        }
        json.value = d3.format(',.2f')(json.value)
      } else if (dp.dataType === 'DECIMAL' || dp.dataType === 'NUMBER') {
        json.value = Math.round(json.value * 100) / 100
        json.value = d3.format(',')(json.value)
      }
      return json
    },
  },
}
</script>

<style>
.widget-legends {
  padding: 20px 0 0 50px;
  position: relative;
  clear: both;
}

.variance-item {
  padding-right: 30px;
  text-align: left;
  position: relative;
}

.legend-container .variance-item {
  padding-left: 0px;
}

.variance-x-label {
  color: #9e9e9e;
  font-size: 0.8vw;
  font-weight: 500;
}

.variance-baseline {
  width: 50px;
  height: 1.5px;
  border: dashed 1.5px #a96fae;
  margin-bottom: 5px;
  margin-top: 2px;
}

.variance-baseline.normal {
  border: solid 1.5px #a96fae;
}

.variance-data {
  padding-left: 24px;
}

.variance-value {
  font-size: 1.2vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.1px;
  text-align: left;
}

.variance-unit {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.1px;
  text-align: left;
  margin-left: -3px;
}

.variance-unit-left {
  margin-left: 0;
}

.variance-label {
  font-size: 0.8vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  color: #ef508f;
  padding-left: 1px;
}

.variance-circle {
  width: 14px;
  height: 14px;
}

.variance-x-label {
  color: #9e9e9e;
  font-size: 0.8vw;
  font-weight: 500;
}

.variance-point {
  font-size: 0.8vw;
  font-weight: 500;
  padding-top: 2px;
  padding-bottom: 1px;
}

@media print {
  .variance-item-display {
    display: inline-block;
    padding-right: 30px;
    padding-bottom: 10px;
  }
  .variance-x-label {
    font-size: 10px !important;
  }
  .variance-label {
    font-size: 10px !important;
  }
  .variance-value {
    font-size: 14px !important;
  }
  .widget-legends .el-carousel {
    margin-left: 100px !important;
  }
}
.widgetleagend-slider .el-carousel .el-carousel__item {
  display: inline-flex !important;
  align-items: center;
}
.widget-legends .el-carousel {
  width: 100%;
  padding-left: 40px;
  padding-right: 40px;
}
.widgetleagend-slider {
  display: inline-flex;
  align-items: center;
}
.dashboard-container .widget-legends {
  padding: 0 !important;
  bottom: 10px !important;
  background: none !important;
}
.legend-container .el-carousel__indicator {
  display: none;
}
.singlecard .el-carousel__container .el-carousel__arrow {
  display: none;
}
</style>
