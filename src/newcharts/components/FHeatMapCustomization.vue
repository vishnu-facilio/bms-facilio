<template>
  <div style="position: relative;">
    <div class="customize-tab-body-axis clearboth pT0">
      <div class="customize-scroll-hidden">
        <div
          class="customize-scroll datapoints-malign mL20 mR20"
          v-if="heatMapOptions"
          style="margin-right: 20px;"
        >
          <p class="label-txt-black mB0">Range</p>
          <div class="customize-input-block pT10">
            <el-row :gutter="20">
              <el-col :span="12">
                <p class="customize-label">Min</p>
                <el-input
                  v-model.number="heatMapOptions.minValue"
                  placeholder="auto"
                  class="fc-input-full-border2 width100"
                ></el-input>
              </el-col>
              <el-col :span="12">
                <p class="customize-label">Max</p>
                <el-input
                  v-model.number="heatMapOptions.maxValue"
                  placeholder="auto"
                  class="fc-input-full-border2 width100"
                ></el-input>
              </el-col>
            </el-row>
          </div>
          <div class="flex-middle">
            <p class="label-txt-black pT20 line-height20 mB10">Color Palette</p>
          </div>
          <div class="analytics-data-point flex-middle">
            <div class="colorPalette-main" @click="openDialog">
              <div
                v-for="(color, idx) in heatMapOptions.Colors[
                  heatMapOptions.chosenColors
                ]"
                :key="idx"
                class="colorBlock"
                :style="{ background: color }"
              ></div>
            </div>
            <div @click="openDialog" class="flex-middle mL30 pB10 pointer">
              <div class="fc-link2 f11">Change</div>
              <InlineSvg
                src="svgs/painter-palette"
                class="vertical-middle pointer color-pallete"
                iconClass="icon icon-xs mL5"
                style="top: 2px;"
              ></InlineSvg>
            </div>
          </div>
          <div v-if="typeof heatMapOptions.reversePallete !== 'undefined'">
            <el-row>
              <el-col :span="18">
                <div class="flex-middle">
                  <p class="label-txt-black pT20 line-height20 mB10">
                    reverse pallete
                  </p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="flex-middle pT20">
                  <span class="customize-label pR10"></span>
                  <el-checkbox v-model="heatMapOptions.reversePallete" />
                </div>
              </el-col>
            </el-row>
          </div>
          <div v-if="typeof heatMapOptions.showGrandParent !== 'undefined'">
            <el-row>
              <el-col :span="18">
                <div class="flex-middle">
                  <p class="label-txt-black pT20 line-height20 mB10">Header</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="flex-middle pT20">
                  <span class="customize-label pR10">Show</span>
                  <el-checkbox v-model="heatMapOptions.showGrandParent" />
                </div>
              </el-col>
            </el-row>
          </div>
          <div
            class="customize-input-block"
            v-if="typeof heatMapOptions.treemapTextMode !== 'undefined'"
          >
            <el-row>
              <div class="label-txt-black pT20 pB10 label-line-height">
                Display Label
              </div>
              <el-select
                v-model="heatMapOptions.treemapTextMode"
                placeholder="Select"
                class="fc-input-full-border2 width100"
              >
                <el-option label="Size Metric" :value="1"></el-option>
                <el-option label="Color Metric" :value="2"></el-option>
                <el-option label="Dimension Only" :value="3"></el-option>
              </el-select>
            </el-row>
          </div>
          <div v-if="typeof heatMapOptions.showColorScale !== 'undefined'">
            <el-row>
              <el-col :span="18">
                <div class="flex-middle">
                  <div class="label-txt-black pT20 line-height20 mB10">
                    Color Scale
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="flex-middle pT20">
                  <span class="customize-label pR10">Show</span>
                  <el-checkbox v-model="heatMapOptions.showColorScale" />
                </div>
              </el-col>
            </el-row>
          </div>
          <div v-if="typeof heatMapOptions.showWidgetLegends !== 'undefined'">
            <el-row>
              <el-col :span="18">
                <div class="flex-middle">
                  <div class="label-txt-black pT20 line-height20 mB10">
                    Widget Legends
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="flex-middle pT20">
                  <span class="customize-label pR10">Show</span>
                  <el-checkbox v-model="heatMapOptions.showWidgetLegends" />
                </div>
              </el-col>
            </el-row>
            <el-collapse
              v-if="heatMapOptions.showWidgetLegends"
              v-model="activeWidgetLegendItem"
              accordion
              style="padding: 20 15px;"
              class="customize-widget-legends mT20"
            >
              <el-collapse-item
                v-for="(point, index) in allPoints.filter(
                  dp => dp.axes !== 'x'
                )"
                :key="index"
                :title="point.label"
                :name="point.alias"
                class="fc-analytics-legend-collapse"
              >
                <el-checkbox-group
                  class="pT20"
                  v-model="report.options.widgetLegend.variances[point.alias]"
                >
                  <template v-if="[4, 8].includes(point.dataTypeId)">
                    <el-checkbox
                      v-for="(value, key) in point.enumMap"
                      :label="key"
                      :key="key"
                      class="width35 pL20 pB20"
                      >{{ value }}</el-checkbox
                    >
                  </template>
                  <el-checkbox
                    v-else
                    v-for="(legend, index) in [
                      'min',
                      'max',
                      'sum',
                      'avg',
                      'lastValue',
                    ]"
                    :key="index"
                    :label="legend"
                    class="width35 pL20 pB20"
                    >{{ $constants.VarianceLabels[legend] }}</el-checkbox
                  >
                </el-checkbox-group>
              </el-collapse-item>
            </el-collapse>
          </div>
          <el-dialog
            title="Colour palettes"
            width="35%"
            :visible.sync="showColorPalette"
            :append-to-body="true"
            class="fc-dialog-center-container color-pallete-lock"
            :lock-scroll="true"
          >
            <div class="heatmap-custom-con">
              <el-row :gutter="20">
                <el-col
                  :span="12"
                  v-for="(colors, index) in heatMapOptions.Colors"
                  :key="index"
                  class="dialogcolorpalette mB10"
                >
                  <!-- loading effect -->
                  <div class="color-palette-loading-con mB10" v-if="loading">
                    <div
                      class="fc-animated-background width100px height20"
                    ></div>
                    <div
                      class="width100 height20 mT10 fc-animated-background"
                    ></div>
                  </div>
                  <div v-else>
                    <div
                      class="fc-input-label-txt f13 flex-middle position-relative"
                    >
                      {{ index }}
                      <div
                        class="selected-icon-color position-absolute"
                        v-if="heatMapOptions.chosenColors === index"
                      >
                        <InlineSvg
                          src="svgs/check-icon"
                          class="vertical-middle"
                          iconClass="icon icon-xs mL10"
                        ></InlineSvg>
                      </div>
                    </div>
                    <div
                      class="colorPalette position-relative"
                      @click="changeColor(index)"
                    >
                      <div
                        v-for="(color, idx) in colors"
                        :key="idx"
                        class="colorBlock"
                        :style="{ background: color }"
                      ></div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-dialog>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import InlineSvg from '@/InlineSvg'
export default {
  props: ['heatMapOptions', 'report'],
  data() {
    return {
      showColorPalette: false,
      loading: true,
      updateTimeout: null,
      activeWidgetLegendItem: null,
    }
  },
  mounted() {},
  components: {
    InlineSvg,
  },
  methods: {
    openDialog() {
      this.showColorPalette = true
      this.loading = true
      if (this.updateTimeout) {
        clearTimeout(this.updateTimeout)
      }
      this.updateTimeout = setTimeout(() => {
        this.loading = false
      }, 500)
    },
    changeColor(index) {
      this.heatMapOptions.chosenColors = index
      this.showColorPalette = false
    },
  },
  computed: {
    dataPointObject: {
      get: function() {
        return this.report
          ? this.$helpers.getDataPoints(
              this.report.options.dataPoints,
              [1, 2, 4],
              true
            )
          : []
      },
      set: function(newDataPointObject) {
        this.report.options.dataPoints = newDataPointObject
      },
    },
    allPoints() {
      let points = []
      let FieldsIncluded = []
      if (this.dataPointObject) {
        this.dataPointObject.forEach(dp => {
          if (dp.type === 'group') {
            dp.children.forEach(children => {
              if (!FieldsIncluded.includes(children.fieldId)) {
                FieldsIncluded.push(children.fieldId)
                points.push(children)
              }
            })
          } else {
            if (!FieldsIncluded.includes(dp.fieldId)) {
              FieldsIncluded.push(dp.fieldId)
              points.push(dp)
            }
          }
        })
      }
      return points
    },
  },
}
</script>
<style lang="scss">
.colorPalette,
.colorPalette-main {
  position: relative;
  margin-bottom: 10px;
  flex-direction: row;
  display: flex;
  align-items: center;
  border-radius: 3px;
  cursor: pointer;
  border: 1px solid #e4eaf0;
  box-shadow: 0 2px 5px 0 rgba(198, 198, 198, 0.5);
  -webkit-box-shadow: 0 2px 5px 0 rgba(198, 198, 198, 0.5);
  -moz-box-shadow: 0 2px 5px 0 rgba(198, 198, 198, 0.5);
  &:hover {
    box-shadow: 1px 1px 7px 3px rgba(198, 198, 198, 0.5);
  }
}

.colorPalette-main {
  .colorBlock {
    width: 35px;
    height: 15px;
  }
}

.colorPalette .colorBlock {
  width: 46px;
  height: 25px;
  position: inline;
  cursor: pointer;
}
.colorBlock:first-child {
  border-top-left-radius: 3px;
  border-bottom-left-radius: 3px;
}
.colorBlock:last-child {
  border-top-right-radius: 3px;
  border-bottom-right-radius: 3px;
}
.colorPalette-selected {
  background: rgba(255, 255, 255, 0.3);
  position: absolute;
  width: 100%;
  height: 30px;
}
.colorPalette-selected .el-icon-check {
  color: rgba(0, 0, 0, 0.6);
  font-size: 14px;
  text-align: center;
  position: relative;
  left: 50%;
  top: 8px;
  font-weight: 600;
}
.color-pallete-lock .el-dialog {
  margin-top: 10vh !important;
}
.selected-icon-color {
  padding-left: 60px;
}
.color-pallete {
  fill: #6c6a91;
}
.heatmap-custom-con {
  max-height: 650px;
  padding-bottom: 40px;
}
.label-line-height {
  line-height: 21px;
}
</style>
