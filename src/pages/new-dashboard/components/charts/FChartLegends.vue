<template>
  <div
    v-if="legends && legends.length"
    class="fLegendContainer fLegendContainer-new fLegendContainer-right"
  >
    <template v-for="legend in filterLegends(legends)">
      <div
        v-if="legendShow(legend)"
        :key="legend.key"
        class="legendBoxNew legendBoxNew-fchart f-legendnew"
        :class="{
          active: hideLegend.indexOf(legend.key) === -1,
          hideLegend: !getVisibledDataPoints(legend),
        }"
        @mouseover="focusLegend(legend.key)"
        @mouseout="unfocusLegend(legend.key)"
        :title="
          isDuplicatePoint(legend)
            ? aggrMap[legend.aggr] + ' of ' + legend.label
            : legend.label
        "
      >
        <div
          class="mL10 mR5"
          v-if="
            isScatterPlot && legend.pointCustomization && legend.pointType != 3
          "
          @click="openScatterOptions(legend.key)"
        >
          <img
            v-if="options && options.scatter.color.mode === 'palette'"
            src="~statics/report/color-wheel.svg"
            style="width: 12px;height: 12px;"
          />
          <chart-shapes
            v-else
            :activeColor="legend.color"
            :activeShape="legend.pointCustomization.shape"
          />
        </div>
        <img
          v-else-if="groupedByTime"
          @click="openGroupByColorPalette(legend.key)"
          src="~statics/report/color-wheel.svg"
          style="width: 12px;height: 12px;"
          class="mL10 mR5"
        />
        <img
          v-else-if="legend.dataType === 'ENUM'"
          @click="openEnumDialog(legend)"
          src="~statics/report/color-wheel.svg"
          style="width: 12px;height: 12px;"
          class="mL10 mR5"
        />
        <el-color-picker
          v-else
          v-model="legend.color"
          size="mini"
          class="mL5 mR5"
          :popper-class="'chart-custom-color-picker'"
        ></el-color-picker>
        <span @click="toggleLegend(legend.key)" class="datapoint-leabel">{{
          isDuplicatePoint(legend)
            ? aggrMap[legend.aggr] + ' of ' + legend.label
            : legend.label
        }}</span>
        <el-tooltip
          class="dp-remove-icon"
          effect="dark"
          :content="'remove data point'"
          v-if="
            $route.meta &&
              $route.meta.component &&
              $route.meta.component === 'analytics'
          "
        >
          <span
            class="datapoint-remove"
            @click="removeDataPoint(options, config, legend, legend.key)"
            ><i class="el-icon-close"></i
          ></span>
        </el-tooltip>
      </div>
      <div
        v-else-if="legend.children && legend.children.length"
        v-for="child in legend.children"
        v-show="child.visible !== false && child.axes !== 'x'"
        :key="child.key"
        class="f-legendnew legendBoxNew legendBoxNew-fchart"
        :class="{ active: hideLegend.indexOf(child.key) === -1 }"
        @mouseover="focusLegend(child.key)"
        @mouseout="unfocusLegend(child.key)"
        :title="
          isDuplicatePoint(child)
            ? aggrMap[child.aggr] + ' of ' + child.label
            : child.label
        "
      >
        <div
          class="mL10 mR5"
          v-if="
            isScatterPlot && child.pointType != 3 && child.pointCustomization
          "
          @click="openScatterOptions(child.key)"
        >
          <img
            v-if="options && options.scatter.color.mode === 'palette'"
            src="~statics/report/color-wheel.svg"
            style="width: 12px;height: 12px;"
          />
          <chart-shapes
            v-else
            :activeColor="child.color"
            :activeShape="child.pointCustomization.shape"
          />
        </div>
        <img
          v-else-if="groupedByTime"
          @click="openGroupByColorPalette(child.key)"
          src="~statics/report/color-wheel.svg"
          style="width: 12px;height: 12px;"
          class="mL10 mR5"
        />
        <img
          v-else-if="child.dataType === 'ENUM'"
          @click="openEnumDialog(child)"
          src="~statics/report/color-wheel.svg"
          style="width: 12px;height: 12px;"
          class="mL10 mR5"
        />
        <el-color-picker
          v-else
          v-model="child.color"
          size="mini"
          class="mL5 mR5"
          :popper-class="'chart-custom-color-picker'"
        ></el-color-picker>
        <span @click="toggleLegend(child.key)" class="datapoint-leabel">{{
          isDuplicatePoint(child)
            ? aggrMap[child.aggr] + ' of ' + child.label
            : child.label
        }}</span>
        <el-tooltip
          class="dp-remove-icon"
          effect="dark"
          :content="'remove data point'"
          v-if="
            $route.meta &&
              $route.meta.component &&
              $route.meta.component === 'analytics'
          "
        >
          <span
            class="datapoint-remove"
            @click="
              removeDataPoint(options, config, legend, false, child, child.key)
            "
            ><i class="el-icon-close"></i
          ></span>
        </el-tooltip>
      </div>
    </template>
    <el-dialog
      title="Color picker"
      :append-to-body="true"
      width="25%"
      :visible.sync="showEnumColorDialog"
      class="enum-color-dialog fc-dialog-center-container fc-dialog-center-container-pt0"
      :lock-scroll="true"
    >
      <div
        v-if="
          selectedEnumDataPoint && selectedEnumDataPoint.dataType === 'ENUM'
        "
        class="overflow-y-scroll height250 pB50"
      >
        <el-row
          class="pB5 border-bottom4 pT5"
          v-for="(enumLabel, idx) in selectedEnumDataPoint.enumMap"
          :key="idx"
        >
          <el-col :span="2">
            <el-color-picker
              v-model="selectedEnumDataPoint.enumColorMap[idx]"
              size="mini"
              class="mL5 mR5"
              :popper-class="'chart-custom-color-picker'"
            ></el-color-picker>
          </el-col>
          <el-col class="mT5" :span="21">{{ enumLabel }}</el-col>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel width100"
          @click.stop="closeEnumDialog()"
          >CLOSE</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      title="Color picker"
      :append-to-body="true"
      width="25%"
      :visible.sync="showgroupByColorDialog"
      class="groupby-color-dialog fc-dialog-center-container fc-dialog-center-container-pt0"
      :lock-scroll="true"
    >
      <div v-if="groupedByTime" class="overflow-y-scroll height250 pB50 ">
        <el-row
          class="pB5 border-bottom4 pT5"
          v-for="(legend, key) in groupedlegends"
          :key="key"
          :class="{ 'datapoint-hidden': !legend.visible }"
        >
          <el-col :span="2" class="mT4">
            <i
              @click="legend.visible = !legend.visible"
              class="pointer el-icon-view"
            ></i>
          </el-col>
          <el-col class="mT5" :span="18">{{ legend.name }}</el-col>
          <el-col :span="3">
            <el-color-picker
              v-model="legend.color"
              size="mini"
              class="mL5 mR5"
              :popper-class="'chart-custom-color-picker'"
            ></el-color-picker>
          </el-col>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel width100"
          @click.stop="closeGroupByColorPalette()"
          >CLOSE</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      title="Customize"
      :append-to-body="true"
      width="40%"
      :visible.sync="showScatterDialog"
      class="groupby-color-dialog fc-dialog-center-container fc-dialog-center-container-pt0"
      :lock-scroll="true"
    >
      <div
        v-if="isScatterPlot && scatterLegend"
        class="overflow-y-scroll height350 pB30"
      >
        <div
          v-if="scatterLegend.type === 'datapoint'"
          class="data-points-drag-block mT10"
        >
          <div
            class="data-points-checkbox"
            :class="{ 'datapoint-hidden': !scatterLegend.visible }"
          >
            <el-input
              ref="dataPointEditName"
              v-model="scatterLegend.label"
              @blur="checkForEmpty(scatterLegend)"
              @keyup.native.enter="checkForEmpty(scatterLegend)"
              class="width100"
            >
            </el-input>
          </div>
          <div
            class="data-points-settings-icon-set scatter-data-points-settings-icon-set pR10"
          >
            <el-radio-group
              class="dp-yaxis"
              v-if="
                scatterLegend.dataTypeId !== 4 && scatterLegend.dataTypeId !== 8
              "
              v-model="scatterLegend.axes"
              size="mini"
              @change="changeYAxis"
            >
              <el-radio-button label="y">Y1</el-radio-button>
              <el-radio-button label="y2">Y2</el-radio-button>
            </el-radio-group>
            <el-color-picker
              v-if="options && options.scatter.color.mode !== 'palette'"
              v-model="scatterLegend.color"
              size="mini"
              class="mL5 mR5"
              :popper-class="'chart-custom-color-picker'"
            ></el-color-picker>
          </div>
        </div>
        <div
          class="customize-input-block"
          v-if="options && options.scatter.shape === 'predefined'"
        >
          <el-row class="pT10">
            <el-col :span="24">
              <div class="label-txt-black">Shape</div>
              <el-select
                v-model="scatterLegend.pointCustomization.shape"
                placeholder="Select"
                class="fc-input-full-border2 width100 pT10"
              >
                <el-option label="Circle" value="circle">
                  <div class="flex-middle">
                    <InlineSvg
                      src="svgs/chart/circle"
                      class="mR10 pointer"
                      iconClass="icon icon-md vertical-sub "
                    ></InlineSvg>
                    <div>
                      Circle
                    </div>
                  </div>
                </el-option>
                <el-option label="Rectangle" value="rectangle">
                  <div class="flex-middle">
                    <InlineSvg
                      src="svgs/chart/rectangle"
                      class=" mR10 pointer"
                      iconClass="icon icon-md vertical-sub "
                    ></InlineSvg>
                    <div>
                      Rectangle
                    </div>
                  </div>
                </el-option>
                <el-option label="Triangle" value="triangle">
                  <div class="flex-middle">
                    <InlineSvg
                      src="svgs/chart/triangle"
                      class=" mR10 pointer"
                      iconClass="icon icon-md vertical-sub "
                    ></InlineSvg>
                    <div>
                      Triangle
                    </div>
                  </div>
                </el-option>
                <el-option label="Diamond" value="diamond">
                  <div class="flex-middle">
                    <InlineSvg
                      src="svgs/chart/diamond"
                      class=" mR10 pointer"
                      iconClass="icon icon-md vertical-sub "
                    ></InlineSvg>
                    <div>
                      Diamond
                    </div>
                  </div>
                </el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel width100"
          @click.stop="closeScatterOptions()"
          >CLOSE</el-button
        >
      </div>
    </el-dialog>
  </div>
  <div
    v-else-if="xValueMode"
    class="fLegendContainer fLegendContainer-new fLegendContainer-right"
  >
    <div
      v-for="(color, key) in options.xColorMap"
      :key="key"
      class="legendBoxNew legendBoxNew-fchart f-legendnew"
      :class="{ active: hideLegend.indexOf(key) === -1 }"
      @mouseover="focusLegend(key)"
      @mouseout="unfocusLegend(key)"
      :title="xLabelMap && xLabelMap[key] ? xLabelMap[key] : key"
    >
      <el-color-picker
        v-model="options.xColorMap[key]"
        size="mini"
        class="mL5 mR5"
        :popper-class="'chart-custom-color-picker'"
      ></el-color-picker>
      <span @click="toggleLegend(key)" class="textoverflow-ellipsis">{{
        xLabelMap && xLabelMap[key] ? xLabelMap[key] : key
      }}</span>
    </div>
  </div>
</template>

<script>
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import ChartShapes from 'pages/energy/analytics/components/ChartShapes'
export default {
  props: [
    'options',
    'chart',
    'multichart',
    'xValueMode',
    'xLabelMap',
    'config',
    'resultObj',
    'isScatterPlot',
  ],
  components: {
    ChartShapes,
  },
  mixins: [AnalyticsMixin, NewDataFormatHelper],
  data() {
    return {
      showLegendColorPicker: false,
      hideLegend: [],
      aggrMap: {
        '2': 'Avg',
        '3': 'Sum',
        '4': 'Min',
        '5': 'Max',
      },
      showgroupByColorDialog: false,
      showEnumColorDialog: false,
      selectedEnumDataPoint: null,
      selectedgroupByDataPoint: null,
      showScatterDialog: false,
      selectedScatterDataPoint: null,
    }
  },
  computed: {
    groupedByTime() {
      if (this.options.isGroupedByTime) {
        return true
      }
      return false
    },
    groupedlegends() {
      if (this.selectedgroupByDataPoint) {
        let mkey = this.selectedgroupByDataPoint
        return Object.keys(this.options.timeGroupOptions)
          .filter(key => key.split('_')[0] === mkey)
          .reduce((obj, key) => {
            obj[key] = this.options.timeGroupOptions[key]
            return obj
          }, {})
      }
      return {}
    },
    legends() {
      let baselineDataPoints = []
      if (this.xValueMode) {
        return null
      }
      if (this.options) {
        if (this.multichart) {
          let mtype = this.multichart.split('_')[0]
          let mkey = this.multichart.substring(this.multichart.indexOf('_') + 1)

          if (mtype === 'group') {
            if (
              this.options.dataPoints.filter(dp => String(dp.label) === mkey)
                .length !== 0
            ) {
              return this.options.dataPoints.filter(
                dp => String(dp.label) === mkey
              )
            }
            return this.options.dataPoints.filter(
              dp => dp.key === mkey && dp.axes !== 'x'
            )
          } else {
            if (
              this.options.dataPoints.filter(
                dp => dp.key === mkey && dp.axes !== 'x'
              ).length !== 0
            ) {
              return this.options.dataPoints.filter(
                dp => dp.key === mkey && dp.axes !== 'x'
              )
            } else {
              return this.allPoints.filter(
                dp => dp.key === mkey && dp.axes !== 'x'
              )
            }
          }
        } else if (this.isScatterPlot) {
          baselineDataPoints = this.getScatterbaseline()
        }
        return [
          ...this.options.dataPoints.filter(dp => dp.axes !== 'x'),
          ...baselineDataPoints,
        ]
      }
      return null
    },
    allPoints() {
      let points = []
      this.options.dataPoints.forEach(dp => {
        if (dp.type === 'group') {
          points.push(...dp.children)
        } else {
          points.push(dp)
        }
      })
      return points
    },
    scatterLegend() {
      return this.allPoints.find(dp => dp.key === this.selectedScatterDataPoint)
    },
  },
  methods: {
    focusLegend(key) {
      if (this.chart && !this.groupedByTime) {
        this.chart.focus(key)
      }
    },
    legendShow(legend) {
      if (legend) {
        if (
          (legend.type === 'datapoint' || legend.type === 'rangeGroup') &&
          this.getVisibledDataPoints(legend)
        ) {
          return true
        }
        // else if (this.getVisibledDataPoints(legend)) {
        //   return true
        // }
        else {
          return false
        }
      } else {
        return false
      }
    },
    unfocusLegend(key) {
      if (this.chart && !this.groupedByTime) {
        this.chart.revert()
      }
    },
    filterLegends(legends) {
      if (legends && legends.length) {
        let self = this
        let datapoints = []
        datapoints = legends.filter(function(rt) {
          if (
            rt &&
            self.config &&
            self.config.hideDataPoints &&
            self.config.hideDataPoints.length
          ) {
            if (
              self.config.hideDataPoints.findIndex(rl => rl === rt.key) === -1
            ) {
              return rt
            }
          } else {
            return rt
          }
        })
        return datapoints
      } else {
        return legends
      }
    },
    getVisibledDataPoints(legend) {
      if (
        this.config &&
        this.config.hideDataPoints &&
        this.config.hideDataPoints.length &&
        this.config.hideDataPoints.find(rt => rt === legend.key)
      ) {
        return false
      } else if (legend.visible) {
        return true
      } else {
        return false
      }
    },
    toggleLegend(key) {
      if (this.chart && !this.groupedByTime) {
        let idx = this.hideLegend.indexOf(key)
        if (idx === -1) {
          this.hideLegend.push(key)
        } else {
          this.hideLegend.splice(idx, 1)
        }
        this.chart.toggle(key)
        if (this.options && this.options.type === 'donut') {
          this.toggleDonutLabel(this.xValueMode)
        }
        this.$emit('ontoggle', key, this.hideLegend)
      }
    },
    isDuplicatePoint(legend) {
      if (legend.duplicateDataPoint && this.aggrMap[legend.aggr]) {
        if (
          this.config.widget &&
          this.config.widget.dataOptions &&
          this.config.widget.dataOptions.newReport
        ) {
          let reportObj = this.config.widget.dataOptions.newReport
          let chartStateObj = null
          if (reportObj.chartState) {
            chartStateObj = JSON.parse(reportObj.chartState)
          }
          if (
            reportObj.xAggr !== 0 ||
            (chartStateObj &&
              ![1, 4, 'reading'].includes(chartStateObj.common.mode))
          ) {
            return true
          }
        } else if (
          this.config.period !== 0 ||
          ![1, 4, 'reading'].includes(this.config.mode)
        ) {
          return true
        }
      }
      return false
    },
    openGroupByColorPalette(key) {
      this.selectedgroupByDataPoint = key
      this.showgroupByColorDialog = true
    },
    closeGroupByColorPalette() {
      this.selectedgroupByDataPoint = null
      this.showgroupByColorDialog = false
    },
    openEnumDialog(legend) {
      this.selectedEnumDataPoint = legend
      this.showEnumColorDialog = true
    },
    closeEnumDialog() {
      this.selectedEnumDataPoint = null
      this.showEnumColorDialog = false
    },
    openScatterOptions(key) {
      if (key == key.toLowerCase()) {
        return
      }
      this.selectedScatterDataPoint = key
      this.showScatterDialog = true
    },
    closeScatterOptions() {
      this.selectedScatterDataPoint = null
      this.showScatterDialog = false
    },
    checkForEmpty(dataPoint) {
      if (dataPoint.label === '') {
        alert('Datapoint name cannot be empty')
      } else {
        this.$forceUpdate()
      }
    },
    changeYAxis() {
      if (this.options && this.options.common.mode !== 2 && this.resultObj) {
        this.options.multichart = this.getMultichartOptions(
          this.options.axis,
          this.$helpers.getDataPoints(this.options.dataPoints, [1, 2, 4], true),
          this.resultObj.report.dataPoints
        )
        this.prepareYAxes(
          this.options.axis,
          this.$helpers.getDataPoints(this.options.dataPoints, [1, 2, 4], true),
          this.resultObj.report.dataPoints
        )
      }
    },
    getScatterbaseline() {
      const baselineData = this.resultObj.baselineData
      let legends = []
      if (baselineData) {
        let i = 0
        let count = 0
        const alphabet = 'abcdefghijklmnopqrstuvwxyz'.split('')
        baselineData.forEach(data => {
          let legend = {
            label: data.label,
            key: alphabet[count],
            color: this.resultObj.baselineDataColors[i],
            pointType: 1,
            safelimit: false,
            type: 'datapoint',
            visible: true,
            pointCustomization: {
              shape: this.options.dataPoints.filter(dp => dp.axes !== 'x')[0]
                .pointCustomization.shape,
              color: { enable: false, basedOn: alphabet[count] },
              size: { enable: false, basedOn: alphabet[count] },
            },
          }
          legends.push(legend)
          i += 1
          count += 2
        })
      }
      return legends
    },
  },
}
</script>

<style lang="scss">
.f-multichart .f-multichart-print .fLegendContainer {
  display: flex;
  overflow-y: auto;
  margin: 0px 10px 0px 10px;
}

.f-multichart .fLegendContainer-new {
  justify-content: flex-start;
}

.fLegendContainer {
  display: inline-flex;
  /* max-width: 700px; */
  overflow-y: auto;
  margin: 10px 10px 0px 10px;
}
.legendBoxNew.f-legendnew {
  padding-left: 10px;
  padding-right: 10px;
  white-space: nowrap;
  max-width: 80%;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  opacity: 0.4;
  display: flex;
  /*align-items: center;*/
  font-size: 13px;
}
.legendBoxNew.f-legendnew.active {
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
  line-height: 20px !important;
}
.fLegendContainer-new {
  max-width: 1200px !important;
  width: calc(93% - 82px);
  flex-wrap: wrap;
  margin-left: auto;
  margin-right: auto;
  justify-content: center;
}
.mobile-chart-legend .fLegendContainer-new {
  max-width: 1100px !important;
  width: 100% !important;
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
  padding-top: 3px !important;
}
.fLegendContainer .el-color-picker__color-inner {
  height: 12px;
  width: 12px;
  border-radius: 50%;
  display: inline-block;
}

.fLegendContainer .el-color-picker__color-inner {
  height: 11px !important;
  width: 11px !important;
}
.f-multichart
  .f-multichart-print
  .fLegendContainer:not(.mobile-legend)
  .el-color-picker__color-inner {
  height: 8px !important;
  width: 8px !important;
}

.legendBoxNew.f-legendnew {
  font-size: 12px !important;
}

.f-multichart
  .f-multichart-print
  .fLegendContainer-right:not(.mobile-legend)
  .legendBoxNew.f-legendnew {
  font-size: 11px !important;
  align-items: center;
  /*margin-right: auto;*/
}

.legendBoxNew-fchart {
  line-height: 18px !important;
}

.fLegendContainer .el-color-picker--mini .el-color-picker__trigger {
  width: 18px !important;
  height: 18px !important;
}

.fc-widget .f-multichart .fLegendContainer.fLegendContainer-new {
  top: 0px !important;
}

.fc-widget .f-multichart .fc-new-chart.bb {
  top: 0px !important;
}

.fLegendContainer .el-color-picker__color-inner {
  left: 4px !important;
}
.fLegendContainer-right {
  max-height: 280px;
  overflow-y: scroll;
  overflow-x: hidden;
}
.f-multichart .f-multichart-print .boolean-chart:not(.mobile-legend) {
  border-bottom: 1px solid transparent !important;
}
.legendBoxNew.f-legendnew:hover .datapoint-leabel {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.dp-remove-icon {
  padding-left: 10px;
  visibility: hidden;
}
.legendBoxNew.f-legendnew:hover .dp-remove-icon {
  visibility: visible !important;
}
.fLegendContainer .hideLegend {
  display: none !important;
}
.groupby-color-dialog .el-color-picker__trigger {
  border: none !important;
}
.groupby-color-dialog .el-color-picker__color {
  border: none !important;
}
.groupby-color-dialog .el-icon-arrow-down {
  display: none;
}
.groupby-color-dialog .el-dialog__header {
  display: block;
}
.groupby-color-dialog .el-icon-view {
  font-size: 18px;
  color: #25243e;
  padding-right: 10px;
  cursor: pointer;
  float: left;
  align-self: center;
}
</style>
