<template>
  <div class="mobile-chart-legend">
    <div v-if="legends && legends.length && legends.length > 1">
      <div
        v-if="
          legends &&
            legends.length &&
            time &&
            tooltipMap &&
            Object.keys(tooltipMap).length > 1
        "
        class="mobile-legend-time"
      >
        {{ time }}
      </div>
    </div>
    <div
      v-else-if="
        legends &&
          legends.length &&
          legends.length === 1 &&
          legends[0].children &&
          legends[0].children.length
      "
    >
      <div
        v-if="
          legends &&
            legends.length &&
            time &&
            tooltipMap &&
            Object.keys(tooltipMap).length > 1
        "
        class="mobile-legend-time"
      >
        {{ time }}
      </div>
      <div v-if="!time" class="empty-time 2"></div>
    </div>
    <div
      v-if="
        legends &&
          legends.length &&
          (legends.length === 1) & (legends[0].type !== 'group')
      "
      class="fLegendContainer fLegendContainer-new fLegendContainer-right mobile-legend single-leagend"
    >
      <template v-for="(legend, index) in legends">
        <div
          v-if="legend.type === 'datapoint'"
          v-show="legend.visible !== false"
          :key="index"
          class="legendBoxNew legendBoxNew-fchart f-legendnew text-left"
          :class="{ active: hideLegend.indexOf(legend.key) === -1 }"
          @mouseover="focusLegend(legend.key)"
          @mouseout="unfocusLegend(legend.key)"
          :title="legend.label"
        >
          <el-color-picker
            style="padding: 10px;"
            v-model="legend.color"
            size="mini"
            :popper-class="'chart-custom-color-picker'"
          ></el-color-picker>
          <span
            @click="toggleLegend(legend.key)"
            style="padding-left:6px;"
            class="textoverflow-ellipsis text-left"
          >
            <div class="mobile-label">{{ legend.label }}</div>
            <span class="mobile-tootltip-date">{{
              tooltipMapSingle && tooltipMapSingle[legend.key]
                ? tooltipMapSingle[legend.key]
                : getDefaultData(legend).label
            }}</span>
            <div
              class="mobile-tootltip-value f20"
              v-if="
                (tMapUnit && tMapUnit[legend.key] !== null
                  ? tMapUnit[legend.key]
                  : getDefaultData(legend).unit) === '$'
              "
            >
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[legend.key] !== null
                    ? tMapUnit[legend.key]
                    : getDefaultData(legend).unit
                "
              ></span>
              {{
                tooltipMap && tooltipMap[legend.key] !== null
                  ? tooltipMap[legend.key]
                  : getDefaultData(legend).value
              }}
            </div>
            <div class="mobile-tootltip-value f20" v-else>
              {{
                tooltipMap && tooltipMap[legend.key] !== null
                  ? tooltipMap[legend.key]
                  : getDefaultData(legend).value
              }}
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[legend.key] !== null
                    ? tMapUnit[legend.key]
                    : getDefaultData(legend).unit
                "
              ></span>
            </div>
          </span>
        </div>
        <div
          v-else-if="
            legend.children &&
              legend.children.length &&
              legend.children.length === 1
          "
          v-for="(child, cidx) in legend.children"
          v-show="child.visible !== false"
          :key="cidx"
          class="f-legendnew legendBoxNew legendBoxNew-fchart"
          :class="{ active: hideLegend.indexOf(child.key) === -1 }"
          @mouseover="focusLegend(child.key)"
          @mouseout="unfocusLegend(child.key)"
          :title="child.label"
        >
          <el-color-picker
            v-model="child.color"
            size="mini"
            class="mL5 mR5"
            :popper-class="'chart-custom-color-picker'"
          ></el-color-picker>
          <span
            @click="toggleLegend(child.key)"
            style="padding-left:6px;"
            class="textoverflow-ellipsis text-left"
          >
            <div class="mobile-label">{{ child.label }}</div>
            <span class="mobile-tootltip-date">{{
              tooltipMapSingle && tooltipMapSingle[child.key]
                ? tooltipMapSingle[child.key]
                : getDefaultData(child).label
            }}</span>
            <div
              class="mobile-tootltip-value f20"
              v-if="
                (tMapUnit && tMapUnit[child.key] !== null
                  ? tMapUnit[child.key]
                  : getDefaultData(child).unit) === '$'
              "
            >
              {{
                tooltipMap && tooltipMap[child.key] !== null
                  ? tooltipMap[child.key]
                  : getDefaultData(child).value
              }}
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[child.key] !== null
                    ? tMapUnit[child.key]
                    : getDefaultData(child).unit
                "
              ></span>
            </div>
            <div class="mobile-tootltip-value f20" v-else>
              {{
                tooltipMap && tooltipMap[child.key] !== null
                  ? tooltipMap[child.key]
                  : getDefaultData(child).value
              }}
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[child.key] !== null
                    ? tMapUnit[child.key]
                    : getDefaultData(child).unit
                "
              ></span>
            </div>
          </span>
        </div>
        <div
          v-else-if="legend.children && legend.children.length"
          v-for="(child, cidx) in legend.children"
          v-show="child.visible !== false"
          :key="cidx"
          class="f-legendnew legendBoxNew legendBoxNew-fchart"
          :class="{ active: hideLegend.indexOf(child.key) === -1 }"
          @mouseover="focusLegend(child.key)"
          @mouseout="unfocusLegend(child.key)"
          :title="child.label"
        >
          <el-color-picker
            v-model="child.color"
            size="mini"
            class="mL5 mR5"
            :popper-class="'chart-custom-color-picker'"
          ></el-color-picker>
          <span
            @click="toggleLegend(child.key)"
            style="padding-left:6px;"
            class="textoverflow-ellipsis text-left mb-section"
          >
            <span class="mobile-label">{{ child.label }}</span>
            <span class="mobile-tootltip-date">{{
              date && date[child.key]
                ? date[child.key]
                : getDefaultData(child).label
            }}</span>
            <span
              class="mobile-tootltip-value f20"
              v-if="
                (tMapUnit && tMapUnit[child.key] !== null
                  ? tMapUnit[child.key]
                  : getDefaultData(child).unit) === '$'
              "
            >
              {{
                tooltipMap && tooltipMap[child.key] !== null
                  ? tooltipMap[child.key]
                  : getDefaultData(child).value
              }}
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[child.key] !== null
                    ? tMapUnit[child.key]
                    : getDefaultData(child).unit
                "
              ></span>
            </span>
            <span class="mobile-tootltip-value f20" v-else>
              {{
                tooltipMap && tooltipMap[child.key] !== null
                  ? tooltipMap[child.key]
                  : getDefaultData(child).value
              }}
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[child.key] !== null
                    ? tMapUnit[child.key]
                    : getDefaultData(child).unit
                "
              ></span>
            </span>
          </span>
        </div>
      </template>
    </div>
    <div
      v-else-if="legends && legends.length"
      class="fLegendContainer fLegendContainer-new fLegendContainer-right mobile-legend muiltileagend"
    >
      <template v-for="(legend, index) in legends">
        <div
          v-if="legend.type === 'datapoint'"
          v-show="legend.visible !== false"
          :key="index"
          class="legendBoxNew legendBoxNew-fchart f-legendnew text-left"
          :class="{ active: hideLegend.indexOf(legend.key) === -1 }"
          @mouseover="focusLegend(legend.key)"
          @mouseout="unfocusLegend(legend.key)"
          :title="legend.label"
        >
          <el-color-picker
            style="padding: 10px;"
            v-model="legend.color"
            size="mini"
            :popper-class="'chart-custom-color-picker'"
          ></el-color-picker>
          <span
            @click="toggleLegend(legend.key)"
            style="padding-left:6px;"
            class="textoverflow-ellipsis text-left mb-section"
          >
            <span class="mobile-label">{{ legend.label }}</span>
            <span class="mobile-tootltip-date" v-if="showAgg">{{
              date && date[legend.key]
                ? date[legend.key]
                : getDefaultData(legend).label
            }}</span>
            <span
              class="mobile-tootltip-value"
              v-if="
                (tMapUnit && tMapUnit[legend.key] !== null
                  ? tMapUnit[legend.key]
                  : getDefaultData(legend).unit) === '$'
              "
            >
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[legend.key] !== null
                    ? tMapUnit[legend.key]
                    : getDefaultData(legend).unit
                "
              ></span>
              {{
                tooltipMap && tooltipMap[legend.key] !== null
                  ? tooltipMap[legend.key]
                  : getDefaultData(legend).value
              }}
            </span>
            <span class="mobile-tootltip-value" v-else>
              {{
                tooltipMap && tooltipMap[legend.key] !== null
                  ? tooltipMap[legend.key]
                  : getDefaultData(legend).value
              }}
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[legend.key] !== null
                    ? tMapUnit[legend.key]
                    : getDefaultData(legend).unit
                "
              ></span>
            </span>
          </span>
        </div>
        <div
          v-else-if="
            legend.children &&
              legend.children.length &&
              legend.children.length === 1
          "
          v-for="(child, cidx) in legend.children"
          v-show="child.visible !== false"
          :key="cidx"
          class="f-legendnew legendBoxNew legendBoxNew-fchart"
          :class="{ active: hideLegend.indexOf(child.key) === -1 }"
          @mouseover="focusLegend(child.key)"
          @mouseout="unfocusLegend(child.key)"
          :title="child.label"
        >
          <el-color-picker
            v-model="child.color"
            size="mini"
            class="mL5 mR5"
            :popper-class="'chart-custom-color-picker'"
          ></el-color-picker>
          <span
            @click="toggleLegend(child.key)"
            style="padding-left:6px;"
            class="textoverflow-ellipsis text-left"
          >
            <div class="mobile-label">{{ child.label }}</div>
            <span class="mobile-tootltip-date" v-if="showAgg">{{
              date && tooltipMapSingle[child.key]
                ? tooltipMapSingle[child.key]
                : getDefaultData(child).label
            }}</span>
            <div
              class="mobile-tootltip-value f20"
              v-if="
                (tMapUnit && tMapUnit[child.key] !== null
                  ? tMapUnit[child.key]
                  : getDefaultData(child).unit) === '$'
              "
            >
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[child.key] !== null
                    ? tMapUnit[child.key]
                    : getDefaultData(child).unit
                "
              ></span>
              {{
                tooltipMap && tooltipMapSingle[child.key] !== null
                  ? tooltipMapSingle[child.key]
                  : getDefaultData(child).value
              }}
            </div>
            <div class="mobile-tootltip-value f20" v-else>
              <div class="max100 width100 mobile-label f14">
                {{
                  tooltipMap && tooltipMapSingle[child.key] !== null
                    ? tooltipMapSingle[child.key]
                    : getDefaultData(child).value
                }}
              </div>
              <div class="pT10">
                {{
                  tooltipMap && tooltipMap[child.key] !== null
                    ? tooltipMap[child.key]
                    : getDefaultData(child).value
                }}
                <span
                  class="mobile-legend-unit"
                  v-html="
                    tMapUnit && tMapUnit[child.key] !== null
                      ? tMapUnit[child.key]
                      : getDefaultData(child).unit
                  "
                ></span>
              </div>
            </div>
          </span>
        </div>

        <div
          v-else-if="
            legend.children && legend.children.length && legend.children.length
          "
          v-for="(child, cidx) in legend.children"
          v-show="child.visible !== false"
          :key="cidx"
          class="f-legendnew legendBoxNew legendBoxNew-fchart"
          :class="{ active: hideLegend.indexOf(child.key) === -1 }"
          @mouseover="focusLegend(child.key)"
          @mouseout="unfocusLegend(child.key)"
          :title="child.label"
        >
          <el-color-picker
            v-model="child.color"
            size="mini"
            class="mL5 mR5"
            :popper-class="'chart-custom-color-picker'"
          ></el-color-picker>
          <span
            @click="toggleLegend(child.key)"
            style="padding-left:6px;"
            class="textoverflow-ellipsis text-left mb-section"
          >
            <span class="mobile-label">{{ child.label }}</span>
            <span class="mobile-tootltip-date" v-if="showAgg">{{
              date && date[child.key]
                ? date[child.key]
                : getDefaultData(child).label
            }}</span>
            <span
              class="mobile-tootltip-value f20"
              v-if="
                (tMapUnit && tMapUnit[child.key] !== null
                  ? tMapUnit[child.key]
                  : getDefaultData(child).unit) === '$'
              "
            >
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[child.key] !== null
                    ? tMapUnit[child.key]
                    : getDefaultData(child).unit
                "
              ></span>
              {{
                tooltipMap && tooltipMap[child.key] !== null
                  ? tooltipMap[child.key]
                  : getDefaultData(child).value
              }}
            </span>
            <span class="mobile-tootltip-value f20" v-else>
              {{
                tooltipMap && tooltipMap[child.key] !== null
                  ? tooltipMap[child.key]
                  : getDefaultData(child).value
              }}
              <span
                class="mobile-legend-unit"
                v-html="
                  tMapUnit && tMapUnit[child.key] !== null
                    ? tMapUnit[child.key]
                    : getDefaultData(child).unit
                "
              ></span>
            </span>
          </span>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import moment from 'moment-timezone'
export default {
  props: [
    'options',
    'chart',
    'multichart',
    'reportVarianceData',
    'chartContext',
    'resultObj',
  ],
  data() {
    return {
      showLegendColorPicker: false,
      hideLegend: [],
      tooltipMap: null,
      dataPoints: null,
      date: null,
      time: null,
      tMapUnit: null,
      showAgg: true,
      tooltipMapSingle: null,
      aggregateFunctions: [
        {
          label: 'TOTAL',
          value: 3,
          name: 'sum',
        },
        {
          label: 'AVG',
          value: 2,
          name: 'avg',
        },
        {
          label: 'MIN',
          value: 4,
          name: 'min',
        },
        {
          label: 'MAX',
          value: 5,
          name: 'max',
        },
        {
          label: 'CURRENT VALUE',
          value: 6,
          name: 'lastValue',
        },
      ],
    }
  },
  computed: {
    legends() {
      if (this.options) {
        if (this.multichart) {
          let mtype = this.multichart.split('_')[0]
          let mkey = this.multichart.substring(this.multichart.indexOf('_') + 1)

          if (mtype === 'group') {
            return this.options.dataPoints.filter(dp => dp.label === mkey)
          } else {
            return this.options.dataPoints.filter(dp => dp.key === mkey)
          }
        }
        return this.options.dataPoints
      }
      return null
    },
    showDefaultAgg() {
      if (this.resultObj) {
        if (this.resultObj && this.resultObj.xAggr === 0) {
          return false
        } else {
          return true
        }
      } else {
        return true
      }
    },
  },
  methods: {
    handleDrillDown(leagend, child) {
      if (!child) {
        let d = this.dataPoints.find(rt => rt.id === leagend.key)
        this.$emit('mobiledrilldown', d)
      }
    },
    focusLegend(key) {
      this.chart.focus(key)
    },
    unfocusLegend() {
      this.chart.revert()
    },
    showTooltip(dPoints, config, title) {
      let d = []
      this.dataPoints = dPoints
      dPoints.forEach(dp => {
        if (dp.type === 'group') {
          d.push(...dp.children)
        } else {
          d.push(dp)
        }
      })
      if (d[0] && !d[0].id) {
        return
      }

      this.date = null
      this.tooltipMap = null
      this.tooltipMapSingle = null
      this.tMapUnit = null
      this.time = null
      let tMap = {}
      let tMap2 = {}
      let tdate = {}
      let tMapUnit = {}
      let time = null
      let points = []
      config.dataPoint.forEach(dp => {
        if (dp.type === 'group') {
          points.push(...dp.children)
        } else {
          points.push(dp)
        }
      })

      for (let i = 0; i < d.length; i++) {
        tMap[d[i].id] = d[i].value !== null ? d[i].value : 'No data'
        tMapUnit[d[i].id] =
          points.find(rt => rt.key === d[i].id) &&
          points.find(rt => rt.key === d[i].id).unitStr
            ? d[i].value !== null
              ? points.find(rt => rt.key === d[i].id).unitStr
              : ''
            : ''
        if (points.length) {
          let agg = points.find(rt => rt.key === d[i].id).aggr
          tdate[d[i].id] = agg ? (agg === 3 ? 'TOTAL' : 'AVG') : 'TOTAL'
          time = title || moment(d[i].x).format('DD MMM hh:MM a')
          tMap2[d[i].id] = title || moment(d[i].x).format('DD MMM hh:MM a')
        } else {
          tdate[d[i].id] = title || moment(d[i].x).format('DD MMM hh:MM a')
          time = title || moment(d[i].x).format('DD MMM hh:MM a')
          tMap2[d[i].id] = title || moment(d[i].x).format('DD MMM hh:MM a')
        }
      }
      this.tooltipMapSingle = tMap2
      this.time = time
      this.tooltipMap = tMap
      this.tMapUnit = tMapUnit
      this.date = tdate
      if (!this.showDefaultAgg) {
        this.showAgg = false
      }
    },
    hideTooltip() {
      this.tooltipMap = null
      this.date = null
      this.time = null
      this.tMapUnit = null
      this.tooltipMapSingle = null
      this.showAgg = true
      this.dataPoints = null
    },
    toggleLegend(key) {
      let idx = this.hideLegend.indexOf(key)
      if (idx === -1) {
        this.hideLegend.push(key)
      } else {
        this.hideLegend.splice(idx, 1)
      }
      this.chart.toggle(key)
    },
    getDefaultData(legend) {
      if (legend && this.reportVarianceData) {
        let returnData = {
          label: '',
          value: '',
          unit: '',
        }
        let sumMetricFields = ['ENERGY', 'LENGTH', 'MASS', 'CURRENCY']
        let aggrMetric = legend.isBaseLine ? 'aggr' : 'metric'
        if (sumMetricFields.find(rt => rt === legend[aggrMetric])) {
          returnData.value = this.reportVarianceData[legend.key + '.sum']
          returnData.value = returnData.value
            ? Math.round(returnData.value * 100) / 100
            : ''
        } else {
          returnData.value = this.reportVarianceData[legend.key + '.avg']
          returnData.value = returnData.value
            ? Math.round(returnData.value * 100) / 100
            : 0
        }
        returnData.value =
          legend.dataType &&
          [4, 8].includes(legend.dataTypeId) &&
          legend.enumMap !== null
            ? legend.enumMap[returnData.value]
              ? legend.enumMap[returnData.value]
              : returnData.value
            : returnData.value
        returnData.label = !this.showDefaultAgg
          ? 'LATEST'
          : legend.aggr &&
            this.aggregateFunctions.find(rt => rt.value === legend.aggr)
          ? this.aggregateFunctions.find(rt => rt.value === legend.aggr).label
          : 'TOTAL'
        returnData.unit = legend.unitStr || ''
        if (returnData.value === null) {
          returnData.value = 'No Data'
        }
        return returnData
      } else {
        return {
          label: '',
          value: '',
          unit: '',
        }
      }
    },
  },
}
</script>

<style>
.fLegendContainer {
  display: inline-flex;
  max-width: 700px;
  overflow-y: auto;
  margin: 10px 10px 0px 10px;
}

.mobile-chart-legend .legendBoxNew.f-legendnew {
  padding-left: 10px;
  padding-right: 10px;
  padding-bottom: 10px;
  max-width: 350px;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  opacity: 0.4;
  display: flex;
}

.mobile-chart-legend .legendBoxNew.f-legendnew.active {
  opacity: 1;
}

.legendcircle {
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -ms-flex-item-align: center;
  -ms-grid-row-align: center;
  align-self: center;
}

.legend-dot {
  height: 14px;
  width: 14px;
  border-radius: 50%;
  display: inline-block;
}

.y-axis-td {
  padding-top: 8px;
}

.chartLegends-td {
  padding-top: 10px;
}

.chartLegends-td .el-color-picker__trigger {
  border: none;
}

.chartLegends-td .el-color-picker__color {
  border: none;
}

.chartLegends-td .el-color-picker--mini .el-color-picker__trigger {
  width: 33px;
  height: 33px;
}

.chartLegends-td .el-color-picker__icon {
  font-size: 20px;
  font-weight: 600;
}

.chartLegends-td .el-input__inner {
  width: 150px;
  margin-left: 10px;
}

.legendBoxNew-fchart {
  line-height: 24px;
}

.fLegendContainer-new {
  max-width: 1100px;
  width: 100%;
  flex-wrap: wrap;
  margin-left: auto;
  margin-right: auto;
  justify-content: center;
}

.fLegendContainer .el-color-picker__trigger,
.fLegendContainer .el-color-picker__color {
  border: none !important;
}

.fLegendContainer .el-icon-arrow-down {
  display: none;
}

.fLegendContainer .el-color-picker--mini .el-color-picker__trigger {
  width: 20px;
  height: 20px;
  border-radius: none;
  cursor: pointer;
}

.fLegendContainer .el-color-picker__trigger {
  padding: 0;
}

.fLegendContainer .el-color-picker--mini {
  padding: 0px !important;
  padding-top: 5px !important;
}
.mobile-dashboard-container .fLegendContainer .el-color-picker--mini {
  padding-top: 2px !important;
}
.mobile-dashboard-container .mobile-legend .legendBoxNew.f-legendnew {
  padding-bottom: 2px !important;
}
.fLegendContainer .el-color-picker__color-inner {
  height: 14px;
  width: 14px;
  border-radius: 50%;
  display: inline-block;
}

.fLegendContainer .el-color-picker__color-inner {
  left: 5px !important;
}

.fLegendContainer-right {
  max-height: 140px;
  overflow-y: auto;
  overflow-x: auto;
  padding-bottom: 0px;
}

span.mobile-tootltip-date,
span.mobile-tootltip-value {
  font-size: 11px;
  color: #606266;
}

.mobile-label {
  font-size: 13px;
  color: #606266;
}
.mobile-legend .legendBoxNew.f-legendnew {
  padding-left: 0px !important;
  width: 100%;
}
.mb-section {
  display: inline-flex;
  width: 100%;
}
.mb-section .mobile-label {
  width: 50%;
  font-size: 12px;
  white-space: pre-wrap;
}
.mb-section span.mobile-tootltip-date,
span.mobile-tootltip-value {
  width: 20%;
  white-space: nowrap;
}
.mobile-legend-time {
  font-size: 10px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  color: #979797;
  text-align: left;
  margin-left: 15px;
  padding-top: 10px;
  margin-right: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f3f3f3;
}
.empty-time {
  height: 32px;
}
span.mobile-tootltip-date {
  text-align: center;
  border-left: 1px solid #f3f3f3;
  -ms-flex-align: center;
  align-items: center;
  font-size: 10px;
  height: 20px;
}
.mobile-legend-unit {
  padding-left: 5px;
}
.max100 {
  max-width: 100%;
}
.drill-down-arrow {
  font-size: 16px;
  padding-left: 20px;
}
</style>
