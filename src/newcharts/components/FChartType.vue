<template>
  <div style="margin-right: 12px;position: absolute;" class="fc-chart-type">
    <span v-if="options">
      <el-popover
        v-model="typePopover"
        :popper-class="'chart-type-popover'"
        :width="isCombo ? 452 : 200"
      >
        <div>
          <div class="chart-type-selector" v-if="!isCombo" style="width:100%">
            <ul>
              <li
                v-for="(ctype, index) in getChartTypes(
                  options,
                  allPoints,
                  'all',
                  skipTableType
                )"
                :key="index"
                @click="onTypeChange(ctype)"
                :class="{ active: currentChartType === ctype.value }"
              >
                <span class="chart-icon" style="margin-right: 5px;">
                  <chart-icon :icon="ctype.value"></chart-icon>
                </span>
                <span class="chart-label">{{ ctype.label }}</span>
              </li>
            </ul>
          </div>
          <div class="chart-type-selector pT20 pB20" v-else>
            <div class="point-type pB20 pL20 pR20 border-bottom1px">
              <div class="type-label label-txt-black bold pT10">
                Default Chart
              </div>
              <el-select
                :disabled="isRangeMode"
                class="fc-input-full-border-select2"
                v-model="defaultOnCombo"
                @change="resetCombo"
              >
                <template slot="prefix"
                  ><chart-icon :icon="currentChartType"></chart-icon
                ></template>
                <el-option
                  v-for="(ctype, index) in getChartTypes(
                    options,
                    allPoints,
                    'default'
                  )"
                  :key="index"
                  :label="ctype.label"
                  :value="ctype.value"
                >
                  <span
                    class="chart-icon flLeft pR5"
                    style="margin-right: 5px;"
                  >
                    <chart-icon :icon="ctype.value"></chart-icon>
                  </span>
                  <div class="chart-label">{{ ctype.label }}</div>
                </el-option>
              </el-select>
            </div>
            <ul>
              <li
                class="point-type pB20"
                v-for="(dataPoint, index) in allPoints"
                :key="index"
                v-if="dataPoint.visible !== false"
              >
                <svg class="dp-circle flLeft mR5">
                  <circle :cx="5" :cy="5" :r="5" :fill="dataPoint.color" />
                </svg>
                <div class="flLeft type-label label-chart">
                  {{ dataPoint.label }}
                </div>
                <el-select
                  class="flLeft type-select"
                  v-model="dataPoint.chartType"
                  placeholder="Default"
                >
                  <template slot="prefix" v-if="dataPoint.chartType"
                    ><chart-icon :icon="dataPoint.chartType"></chart-icon
                  ></template>
                  <el-option
                    v-for="(ctype, index) in getChartTypes(
                      options,
                      allPoints,
                      dataPoint.type === 'rangeGroup' ? 'range' : 'point'
                    )"
                    :key="index"
                    :label="ctype.label"
                    :value="ctype.value"
                  >
                    <span
                      class="chart-icon flLeft pR5"
                      style="margin-right: 5px;"
                    >
                      <chart-icon :icon="ctype.value"></chart-icon>
                    </span>
                    <div class="chart-label">{{ ctype.label }}</div>
                  </el-option>
                </el-select>
              </li>
            </ul>
          </div>
        </div>
        <span
          slot="reference"
          style="display: inline-block;"
          class="chart-icon pointer"
          :title="$t('home.dashboard.change_chart')"
          data-arrow="true"
          v-tippy
          ><chart-icon :icon="currentChartType"></chart-icon
        ></span>
      </el-popover>
    </span>
  </div>
</template>
<script>
import ChartTypes from 'pages/report/mixins/NewChartTypes'
import ChartIcon from 'charts/components/chartIcon'
export default {
  props: ['options', 'multichartkey','skipTableType'],
  mixins: [ChartTypes],
  components: { ChartIcon },
  data() {
    return {
      typePopover: false,
      defaultOnCombo: 'combo',
    }
  },
  computed: {
    isRangeMode() {
      if (
        this.options.dataPoints.filter(dP => dP.type === 'rangeGroup')
          .length !== 0
      ) {
        return true
      }
      return false
    },
    isMultiMode() {
      return this.options && this.options.settings.chartMode === 'multi'
    },
    isCombo() {
      if(this.isMultiMode && this.options?.type == 'table'){
        return false
      }
      let isCombo = this.isMultiMode
        ? this.options.multichart[this.multichartkey] &&
          this.options.multichart[this.multichartkey].combo
        : this.options && this.options.combo
      return (
        isCombo ||
        (this.allPoints.length &&
          this.allPoints.some(
            dp => dp.chartType !== this.allPoints[0].chartType
          ))
      )
    },
    currentChartType() {
      if (this.isMultiMode) {
        if (this.isCombo) {
          return 'combo'
        }
        if (this.allPoints && this.allPoints.length) {
          return this.allPoints[0].chartType
        }
        return 'area'
      }
      return this.options.type
    },
    allPoints() {
      let points = []
      if (this.options) {
        let dataPoints = this.options.dataPoints
        if (this.isMultiMode && this.multichartkey) {
          let keys = this.multichartkey.split('_')
          let type = keys[0]
          let multiPointName = keys[1]
          dataPoints = dataPoints.filter(dp =>
            type === 'datapoint' || type === 'rangeGroup'
              ? dp.key === multiPointName
              : dp.label === multiPointName
          )
        }
        dataPoints.forEach(dp => {
          if (dp.type === 'group') {
            points.push(...dp.children)
          } else {
            points.push(dp)
          }
        })
      }
      return points
    },
  },
  methods: {
    resetCombo() {
      this.typePopover = false
      if (this.isMultiMode && this.multichartkey) {
        if(this.multichartkey){
          this.$set(this.options.multichart[this.multichartkey], 'combo', false)
        }
      } else {
        this.$set(this.options, 'combo', false)
      }
      this.allPoints.forEach(dp => {
        // dp.chartType = this.isMultiMode ? this.defaultOnCombo : ''
        this.$set(dp, 'chartType', this.isMultiMode ? this.defaultOnCombo : '')
      })
      if (!this.isMultiMode) {
        this.options.type = this.defaultOnCombo
      }
      this.defaultOnCombo = 'combo'
    },
    onTypeChange(ctype) {
      this.typePopover = false
      if (ctype.value === 'table') {
        if (this.options.settings.chart) {
          this.options.settings.chart = false
        }
        this.options.type = ctype.value
      } else {
        this.options.settings.chart = true
        if (ctype.value === 'combo') {
          let currentType
          if (this.isMultiMode) {
            this.$set(
              this.options.multichart[this.multichartkey],
              'combo',
              true
            )
            currentType = this.allPoints[0].chartType
          } else {
            this.$set(this.options, 'combo', true)
            currentType = this.options.type
          }
          let defaultType =
            !currentType || currentType === 'pie' || currentType === 'donut'
              ? 'area'
              : currentType
          this.allPoints.forEach(dp => {
            if (!dp.chartType) {
              dp.chartType = defaultType
            }
          })
          this.$nextTick(_ => {
            this.typePopover = true
          })
        } else {
          if (this.isMultiMode) {
            this.allPoints.forEach(dp => {
                if('datapoint_'+dp.alias == this.multichartkey){
                  dp.chartType = ctype.value  
                }else{
                  dp.chartType = dp.chartType && dp.chartType !='' ? dp.chartType : ctype.value
                }
            })
          } else {
            this.options.type = ctype.value
          }
        }
      }
      this.$emit('getOptions', this.options, ctype)
    },
  },
}
</script>
<style>
.chart-type-selector {
  max-height: 400px;
  overflow: scroll;
}

.chart-type-selector ul {
  list-style: none;
  padding: 0 0px;
  margin: 0;
}

.chart-type-selector ul li {
  padding: 8px 20px;
  vertical-align: middle;
  cursor: pointer;
}

.chart-type-selector ul li .chart-icon div {
  display: inline-block;
}

.chart-type-selector ul li .chart-label {
  position: relative;
  top: -4px;
}

.chart-type-selector ul li:hover:not(.point-type),
.chart-type-selector ul li.active {
  background: #f4f4f4;
}

.point-type {
  display: flex;
  justify-content: space-between;
  flex-direction: row;
}

.chart-type-selector .type-label {
  display: flex;
  text-align: left;
  /* word-break: break-all; */
  justify-content: flex-start;
  padding-top: 10px;
}

.chart-type-selector .type-select {
  display: flex;
  justify-content: flex-end;
  width: 161px;
}

.chart-type-selector .el-input__inner {
  background: transparent;
  padding: 7px 7px 10px 32px !important;
  border: none;
  width: 160px;
  height: 40px;
  line-height: 40px;
}
.chart-type-selector .el-input__inner:hover {
  border: 1px solid #d0d9e2;
  cursor: pointer;
  border-radius: 3px;
}
.type-select .el-input--suffix {
  padding-left: 7px;
}
.chart-type-selector .el-input__prefix {
  padding-top: 10px;
  padding-left: 8px;
}

.chart-icon-group {
  width: 0.8vw;
  top: 2px;
  position: relative;
}

.chart-type-selector .el-select-dropdown__item {
  padding: 0 12px !important;
}

.chart-type-selector .dp-circle {
  width: 11px;
  height: 11px;
  margin-top: 14px;
}
.label-chart {
  width: 267px;
  padding-left: 10px;
  word-break: break-all;
  overflow-x: hidden;
}
.fc-chart-type .fc-input-full-border-select2 .el-input__inner {
  height: 40px;
  line-height: 40px;
  padding-left: 15px;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #d0d9e2;
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.4px;
  color: #333333;
}
.fc-chart-type .fc-input-full-border-select2 .el-input--suffix {
  padding-left: 3px;
}

.f-multichart .fc-chart-type {
  right: 0px;
}

.f-singlechart .fc-chart-type {
  right: 50px;
}
</style>
