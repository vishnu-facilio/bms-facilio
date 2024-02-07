<template>
  <div class="p30 fc-card-builder-page-animation">
    <div class="header">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ (cardMeta && cardMeta.name) || 'Map Layout' }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div class="container mT20">
      <div class="section">
        <el-form
          :model="cardDataObj"
          :rules="validationRules"
          :ref="`${this.cardLayout}_form`"
          :label-position="'top'"
        >
          <el-tabs v-model="activeTab" class="card-tab-fixed">
            <el-tab-pane label="Config" name="config">
              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="title" class="mB10">
                    <p class="fc-input-label-txt pB5">Title</p>
                    <el-input
                      :autofocus="isNew"
                      v-model="cardDataObj.title"
                      class="width100 pR20 fc-input-full-border2"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="mB10" :gutter="20">
                <el-col :span="12">
                  <el-form-item prop="module" class="mB10">
                    <p class="fc-input-label-txt pB5">Location Module</p>
                    <el-select
                      v-model="cardDataObj.moduleName"
                      @change="selectModuleAction()"
                      placeholder="Please select a building"
                      class="width100 el-input-textbox-full-border"
                    >
                      <el-option
                        v-for="(value, key) in locationModules"
                        :label="key"
                        :value="value"
                        :key="key"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item prop="marker" class="mB10">
                    <p class="fc-input-label-txt pB5">Marker type</p>
                    <el-select
                      v-model="cardDataObj.marker.type"
                      placeholder="Please select a building"
                      class="width100 el-input-textbox-full-border"
                    >
                      <el-option
                        v-for="(value, key) in markerValues"
                        :label="key"
                        :value="value"
                        :key="key"
                      ></el-option>
                      <el-option
                        label="Reading"
                        value="reading"
                        v-if="cardDataObj.moduleName === 'asset'"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <template v-if="cardDataObj.marker.type === 'reading'">
                <el-row class="mB10">
                  <el-col :span="24">
                    <el-form-item prop="marker" class="mB10">
                      <p class="fc-input-label-txt pB5">Asset Category</p>
                      <el-select
                        @change="cardDataObj.marker.reading.fieldName = null"
                        v-model="cardDataObj.assetCategory"
                        placeholder="Please select a period"
                        class="width100 pR20 el-input-textbox-full-border"
                      >
                        <el-option
                          v-for="(category, index) in assetCategory"
                          :key="index"
                          :label="category.name"
                          :value="category.id"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row class="mB10">
                  <el-col :span="24">
                    <el-form-item prop="marker" class="mB10">
                      <p class="fc-input-label-txt pB5">Asset Fields</p>
                      <el-select
                        @change="
                          setReadingModuleName(
                            cardDataObj.marker.reading.fieldName
                          )
                        "
                        v-model="cardDataObj.marker.reading.fieldName"
                        placeholder="Please select a period"
                        class="width100 pR20 el-input-textbox-full-border"
                      >
                        <el-option
                          v-for="(value, key) in readings.categoryWithFields[
                            this.cardDataObj.assetCategory
                          ] || {}"
                          :key="key"
                          :label="fields[key].displayName"
                          :value="fields[key].name"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row class="mB10">
                  <el-col :span="24">
                    <el-form-item prop="yAggr" class="mB10">
                      <p class="fc-input-label-txt pB5">Aggregation</p>
                      <el-select
                        v-model="cardDataObj.marker.reading.yAggr"
                        placeholder="Please select an aggregation"
                        class="width100 pR20 el-input-textbox-full-border"
                      >
                        <el-option
                          v-for="(fn, index) in aggregateFunctions"
                          :label="fn.label"
                          :value="fn.value"
                          :key="index"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row class="mB10">
                  <el-col :span="24">
                    <el-form-item prop="dateRange" class="mB10">
                      <p class="fc-input-label-txt pB5">Period</p>
                      <el-select
                        v-model="cardDataObj.dateRange"
                        placeholder="Please select a period"
                        class="width100 pR20 el-input-textbox-full-border"
                      >
                        <template v-for="(dateRange, index) in dateOperators">
                          <el-option
                            :label="dateRange.label"
                            :value="dateRange.value"
                            :key="index"
                          ></el-option>
                        </template>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>
            </el-tab-pane>

            <el-tab-pane label="Styles" name="styles">
              <el-row class="mB10">
                <el-col :span="12">
                  <el-form-item class="mB10">
                    <p class="fc-input-label-txt pB5">Map Theme</p>
                    <el-select
                      v-model="cardStateObj.styles.theme"
                      placeholder="Please select a building"
                      class="width100 pR20 el-input-textbox-full-border"
                    >
                      <el-option
                        v-for="(theme, idx) in mapTheams"
                        :key="idx"
                        :value="theme.value"
                        :label="theme.displayName"
                      >
                      </el-option>
                      <el-option :value="null" :label="'Default'"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item class="mB10">
                    <p class="fc-input-label-txt pB5">Icon</p>

                    <el-select
                      v-model="cardDataObj.marker.icon"
                      :filterable="true"
                      :default-first-option="true"
                      class="fc-input-full-border2 icon-picker mR15"
                      value-key="name"
                    >
                      <inline-svg
                        slot="prefix"
                        :src="
                          mapIcons.find(
                            rt => rt.value === cardDataObj.marker.icon
                          ).path
                        "
                        iconClass="icon icon-sm-md"
                        class="mR10"
                      ></inline-svg>
                      <el-option
                        v-for="(icon, idx) in mapIcons"
                        :key="idx + icon.name"
                        :value="icon.value"
                        :label="icon.name"
                      >
                        <inline-svg
                          :src="icon.path"
                          class="vertical-middle"
                          iconClass="icon"
                        ></inline-svg>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item class="mB10">
                    <p class="fc-input-label-txt pB5">Icon color</p>
                    <div class="fc-color-picker card-color-container">
                      <el-color-picker
                        :predefine="getPredefinedColors()"
                        v-model="cardStateObj.marker.styles.color"
                      ></el-color-picker>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10" :gutter="20">
                <el-col :span="11">
                  <el-form-item class="mB10">
                    <p class="fc-input-label-txt pB5">Select Layer</p>
                    <el-select
                      v-model="cardStateObj.layers"
                      multiple
                      collapse-tags
                      :filterable="true"
                      placeholder="Select layer"
                      @change="setDefaultData"
                      class="width100 el-input-textbox-full-border"
                    >
                      <el-option
                        v-for="item in layers"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="11">
                  <el-form-item class="mB10">
                    <p class="fc-input-label-txt pB5">
                      Map Zoom level
                    </p>
                    <el-select
                      v-model="cardStateObj.zoom"
                      collapse-tags
                      :filterable="true"
                      placeholder="Select Zoom"
                      class="width100 el-input-textbox-full-border"
                    >
                      <el-option :label="'Auto'" :value="null"> </el-option>
                      <el-option
                        v-for="number in 19"
                        :key="number"
                        :label="number"
                        :value="number"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10" v-if="isHeatmapEnabled">
                <el-col :span="16">
                  <el-form-item class="mB10">
                    <f-color-palettes
                      :colorIndex="cardStateObj.heatmap.colorIndex"
                      @colorSelected="onColorChange"
                    ></f-color-palettes>
                  </el-form-item>
                </el-col>
                <!-- <el-col :span="16">
                  <el-form-item class="mB10">
                    <p class="fc-input-label-txt pB5">Radius</p>
                    <el-slider
                      v-model="cardStateObj.heatmap.radius"
                      :step="5"
                      show-stops
                    >
                    </el-slider>
                  </el-form-item>
                </el-col>
                <el-col :span="16">
                  <el-form-item class="mB10">
                    <p class="fc-input-label-txt pB5">Opacity</p>
                    <el-slider
                      :min="0.1"
                      :max="1"
                      v-model="cardStateObj.heatmap.opacity"
                      :step="0.1"
                      show-stops
                    >
                    </el-slider>
                  </el-form-item>
                </el-col> -->
              </el-row>
            </el-tab-pane>
          </el-tabs>
        </el-form>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper">
          <Card
            :cardData="previewData"
            :cardStyle="previewStyles"
            :cardState="previewState"
            :loading="isPreviewLoading"
          ></Card>
        </div>
      </div>
    </div>
    <div class="d-flex mT-auto form-action-btn">
      <el-button
        class="form-btn f13 bold secondary text-center text-uppercase"
        @click="onGoBack()"
        >Cancel</el-button
      >
      <el-button
        type="primary"
        class="form-btn f13 bold primary m0 text-center text-uppercase"
        @click="save()"
        >Save</el-button
      >
    </div>
  </div>
</template>
<script>
import BaseConfig from 'pages/card-builder/cards/common/BaseConfig'
import Card from './Card'
import {
  locationModules,
  markerValues,
  iconTypes,
  aggregateFunctions,
  dateOperators,
  mapIcons,
} from 'pages/card-builder/card-constants'
import { isEmpty } from '@facilio/utils/validation'
import DateHelper from '@/mixins/DateHelper'
import colors from 'charts/helpers/colors'
import { mapTheams } from 'src/util/map-constant'
import FColorPalettes from 'newcharts/components/FColorPalettes.vue'
import { API } from '@facilio/api'
export default {
  extends: BaseConfig,
  props: ['isNew', 'onClose', 'onGoBack'],
  mixins: [DateHelper],
  components: { Card, FColorPalettes },
  data() {
    return {
      layers: [
        { value: 'marker', label: 'Marker' },
        { value: 'heatmap', label: 'HeatMap' },
      ],
      cardLayout: `mapcard_layout_1`,
      rerenderCriteriaBuilder: false,
      activeTab: 'config',
      readings: null,
      isPreviewLoading: false,
      resourceProps: [
        'title',
        'moduleName',
        {
          prop: 'marker',
          resourceProps: ['icon', 'type', 'reading'],
        },
        'dateRange',
      ],
      cardDataObj: {
        title: '',
        moduleName: 'site',
        assetCategory: null,
        marker: {
          icon: 1,
          type: 'No of Workoders',
          reading: {
            fieldName: null,
            moduleName: null,
            yAggr: 'sum',
          },
        },
        dateRange: 'Current Month',
      },
      cardStateObj: {
        canResize: true,
        styles: {
          theme: null,
        },
        zoom: null,
        layers: ['marker'],
        heatmap: {
          colorIndex: 1,
          gradient: [],
          radius: 30,
          opacity: 1,
        },
        marker: {
          styles: {
            color: '#FE2C25',
          },
        },
      },
      cardActions: {
        default: {
          actionType: 'showTrend',
        },
      },
      layout: {
        w: 16,
        h: 19,
      },
      result: null,
      validationRules: {},
      locationModules: locationModules,
      markerValues: markerValues,
      iconTypes: iconTypes,
      aggregateFunctions,
      dateOperators,
      mapIcons,
      mapTheams,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.loadReadings()
  },
  computed: {
    isHeatmapEnabled() {
      let { layers } = this.cardStateObj
      if (!isEmpty(layers)) {
        let heatmapIndex = layers.findIndex(layer => layer === 'heatmap')
        if (heatmapIndex > -1) {
          return true
        }
      }
      return false
    },
    previewData() {
      let { result } = this
      let { data } = result || {}

      return !isEmpty(this.result)
        ? data
        : {
            period: 'Last Month',
            targetValue: { value: 100 },
            title: 'Gauge Card 1',
            value: { unit: 'KwH', value: 65 },
          }
    },
    previewStyles() {
      let { result, cardStateObj } = this
      let { cardContext } = result || {}

      return !isEmpty(this.result)
        ? cardContext.cardState || {}
        : this.serializeState(cardStateObj)
    },
    assetCategory() {
      return this.$store.state.assetCategory
    },
    categoryWithFields() {
      if (this.cardDataObj.assetCategory) {
        return this.readings.categoryWithFields[this.cardDataObj.assetCategory]
      }
      return {}
    },
    fields() {
      return this.readings.fields
    },
    markerstyle() {
      if (!isEmpty(this.cardStateObj) && this.cardStateObj.marker) {
        return this.cardStateObj.marker
      }
      return {}
    },
  },
  methods: {
    setDefaultData() {
      let heatmap = {
        colorIndex: 1,
        gradient: [],
        radius: 20,
        opacity: 1,
      }
      let colorArr = ['#1d7f01', '#6cb302', '#e9f501', '#fda504', '#fb5905']
      if (!isEmpty(colorArr)) {
        let rgbaColors = []
        let firstColor = colorArr[0]
        rgbaColors.push(this.hexToRgbA(firstColor, 0)) // doing bcs google map taking first color as default so which is rendered to full google map
        colorArr.forEach(hex => {
          rgbaColors.push(this.hexToRgbA(hex))
        })
        heatmap.gradient = rgbaColors
      }
      if (!this.cardStateObj.heatmap) {
        this.$set(this.cardStateObj, 'heatmap', heatmap)
      }
    },
    hexToRgbA(hex, opacity) {
      let c
      if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
        c = hex.substring(1).split('')
        if (c.length == 3) {
          c = [c[0], c[0], c[1], c[1], c[2], c[2]]
        }
        c = '0x' + c.join('')
        return (
          'rgba(' +
          [(c >> 16) & 255, (c >> 8) & 255, c & 255].join(',') +
          ',' +
          (typeof opacity !== 'undefined' ? opacity : 1) +
          ')'
        )
      }
    },
    onColorChange(idx, colorArr) {
      let heatmap = {
        colorIndex: idx,
        gradient: [],
      }

      if (!isEmpty(colorArr)) {
        let rgbaColors = []
        let firstColor = colorArr[0]
        rgbaColors.push(this.hexToRgbA(firstColor, 0)) // doing bcs google map taking first color as default so which is rendered to full google map
        colorArr.forEach(hex => {
          rgbaColors.push(this.hexToRgbA(hex))
        })
        heatmap.gradient = rgbaColors
      }

      this.$set(this.cardStateObj, 'heatmap', heatmap)
    },
    setReadingModuleName(fieldName) {
      this.cardDataObj.marker.reading.moduleName = Object.values(
        this.fields
      ).find(rt => rt.name === fieldName).module.name
    },
    async loadReadings() {
      let { data } = await API.get(`/asset/getreadings`)

      if (data) {
        this.readings = data
      }
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    serializeState() {
      return this.cardStateObj
    },

    validateProperty() {
      let { marker } = this.cardDataObj
      let { reading } = marker || {}
      return {
        marker: () => {
          if (marker.type === 'reading' && isEmpty(reading.moduleName)) {
            return true
          } else if (marker.type === 'reading' && isEmpty(reading.fieldName)) {
            return true
          }
          return false
        },
      }
    },

    validateReadingField(marker) {
      let hasReading = true
      let { reading } = marker
      if (marker.type === 'reading') {
        if (isEmpty(reading)) hasReading = false
        else hasReading = true
        ;['moduleName', 'yAggr', 'fieldName'].forEach(prop => {
          hasReading = !isEmpty(reading[prop])
        })
      }
      return hasReading
    },
    validateField() {
      let validator = function(rule, value, callback) {
        let { marker } = this.cardDataObj
        if (!this.validateReadingField(marker)) {
          callback(new Error('Reading Fields can not be empty'))
        }
        callback()
      }.bind(this)

      return {
        marker: {
          trigger: 'change',
          validator,
        },
      }
    },

    getCriteria(condition) {
      this.cardDataObj.criteria = condition
    },

    selectModuleAction() {
      this.rerenderCriteriaBuilder = true
      setTimeout(() => {
        this.rerenderCriteriaBuilder = false
      }, 100)
    },
  },
}
</script>
<style scoped lang="scss">
.header {
  font-size: 22px;
  font-weight: 400;
  color: #324056;
}
.card-wrapper {
  width: 400px;
  height: 100%;
}
</style>
<style lang="scss">
.icon-picker {
  width: 60px;

  .el-input__prefix {
    background: #fff;
    width: 85%;
    height: 30px;
    top: 5px;
    text-align: center;
    overflow: hidden;
  }
}
</style>
