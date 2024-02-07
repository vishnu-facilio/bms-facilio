<template>
  <div class="p30">
    <div class="header">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ (cardMeta && cardMeta.name) || 'Energy Card' }}
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
          :ref="`${this.cardLayout}_form`"
          :rules="validationRules"
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
                      class="width100 fc-input-full-border2"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="">
                <el-col :span="24">
                  <el-form-item prop="imageSpaceId" class="mB10">
                    <p class="fc-input-label-txt pB5">Image Field</p>
                    <el-select
                      v-model="cardDataObj.imageSpaceId"
                      placeholder="Select a site or building"
                      class="width100 el-input-textbox-full-border"
                    >
                      <el-option-group label="Sites">
                        <el-option
                          v-for="site in sites"
                          :key="site.id"
                          :label="site.name"
                          :value="site.id"
                        ></el-option>
                      </el-option-group>
                      <el-option-group label="Buildings">
                        <el-option
                          v-for="building in buildings"
                          :key="building.id"
                          :label="building.name"
                          :value="building.id"
                        ></el-option>
                      </el-option-group>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="reading" class="mB10">
                    <p class="fc-input-label-txt pB5">Reading</p>
                    <ReadingPicker
                      :options="readingPickerOptions"
                      :initialReading="cardDataObj.reading"
                      @onReadingSelect="
                        reading => setReading('reading', reading)
                      "
                    ></ReadingPicker>
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
                      class="width100 el-input-textbox-full-border"
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

              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="baseline" class="mB10">
                    <p class="fc-input-label-txt pB5">Baseline (Optional)</p>
                    <el-select
                      v-model="cardDataObj.baseline"
                      placeholder="Please select a period"
                      class="width100 el-input-textbox-full-border"
                    >
                      <template v-for="(dateRange, index) in baselines">
                        <el-option
                          :label="dateRange.name"
                          :value="dateRange.name"
                          :key="index"
                        ></el-option>
                      </template>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-tab-pane>

            <el-tab-pane label="Styles" name="styles">
              <el-form :model="cardStateObj" :label-position="'top'">
                <el-row class="mB10 mT20">
                  <el-col :span="8">
                    <el-form-item prop="arrowUpColor" class="mB10">
                      <p class="fc-input-label-txt pB5">Arrow up</p>
                      <div
                        class="d-flex mT5 card-color-container fc-color-picker"
                      >
                        <el-color-picker
                          v-model="cardStateObj.styles.arrowUpColor"
                          :key="
                            'arrowUpColor' + cardStateObj.styles.arrowUpColor
                          "
                          :predefine="predefinedColors"
                          size="small"
                          :popper-class="'chart-custom-color-picker'"
                        ></el-color-picker>
                      </div>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item prop="arrowDownColor" class="mB10">
                      <p class="fc-input-label-txt pB5">Arrow down</p>
                      <div
                        class="d-flex mT5 card-color-container fc-color-picker"
                      >
                        <el-color-picker
                          v-model="cardStateObj.styles.arrowDownColor"
                          :key="
                            'arrowDownColor' +
                              cardStateObj.styles.arrowDownColor
                          "
                          :predefine="predefinedColors"
                          size="small"
                          :popper-class="'chart-custom-color-picker'"
                        ></el-color-picker>
                      </div>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="Actions" name="actions">
              <ActionPicker
                ref="action-picker"
                v-model="cardActions"
                :variables="variables"
                :elements="[{ name: 'default', displayName: 'Default' }]"
              >
                <template slot="element-title">
                  <div class="pT15"></div>
                </template>
              </ActionPicker>
            </el-tab-pane>
          </el-tabs>
        </el-form>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper">
          <Card
            :cardData="previewData"
            :cardState="previewState"
            :loading="isPreviewLoading"
          ></Card>
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
  </div>
</template>
<script>
import Config from '../base/Config'
import Card from './Card'
import { isEmpty, isObject } from '@facilio/utils/validation'
import ReadingPicker from 'pages/card-builder/cards/common/ReadingPicker'
import {
  dateOperators,
  aggregateFunctions,
  predefinedColors,
} from 'pages/card-builder/card-constants'
import ActionPicker from 'pages/card-builder/components/ActionPicker.vue'

export default {
  name: 'EnergyCard1',
  extends: Config,
  props: [
    'isNew',
    'cardType',
    'onClose',
    'onGoBack',
    'onCardSave',
    'onCardUpdate',
    'closePopup',
  ],
  components: { Card, ReadingPicker, ActionPicker },
  created() {
    this.loadSpaces()
    this.loadBaselines()
  },
  data() {
    return {
      cardLayout: `energycard_layout_1`,
      isPreviewLoading: false,
      activeTab: 'config',
      resourceProps: [
        'title',
        {
          prop: 'reading',
          resourceProps: [
            'moduleName',
            'parentId',
            'fieldName',
            'yAggr',
            'fieldId',
            'dataType',
          ],
        },
        'baseline',
        'imageSpaceId',
        'dateRange',
      ],
      cardDataObj: {
        title: null,
        reading: {
          yAggr: 'sum',
        },
        baseline: 'Previous Month',
        dateRange: 'Current Month',
        imageSpaceId: null,
      },
      cardState: {
        canResize: false,
        styles: {},
      },
      layout: {
        w: 24,
        h: 12,
      },
      cardStateObj: {
        styles: {
          arrowUpColor: '#008000',
          arrowDownColor: '#ff0100',
        },
      },
      conditionalVariables: [
        {
          name: 'title',
          dataType: 'STRING',
          displayName: 'Title',
        },
        {
          name: 'value',
          dataType: 'NUMBER',
          displayName: 'Value',
        },
      ],
      readingPickerOptions: {
        pickerModule: '',
        parentId: {
          type: 'single',
        },
        fieldName: {
          type: 'single',
        },
        fieldId: {
          type: 'single',
        },
        dataType: {
          type: 'single',
        },
        moduleName: {
          type: 'single',
        },
        parentName: {
          type: 'single',
        },
        parentType: {
          type: 'single',
        },
        yAggr: {
          type: 'single',
        },
        selectedFieldId: {
          type: 'single',
        },
        kpiType: {
          type: 'single',
        },
      },
      cardActions: {
        default: {
          actionType: 'showTrend',
        },
      },
      sites: [],
      buildings: [],
      baselines: [],
      result: null,
      validationRules: {},
      dateOperators,
      aggregateFunctions,
      predefinedColors,
    }
  },
  computed: {
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: 'Building 1',
            image: null,
            period: 'Last Month',
            baselineValue: {
              value: 500,
            },
            value: {
              unit: 'kWh',
              value: 422.5,
            },
          }
    },
  },
  methods: {
    loadSpaces() {
      let filters = {
        spaceType: { operator: '=', value: ['1', '2'] },
        photoId: { operator: 'is not empty' },
      }
      let url = '/basespace?orderBy=spaceType&orderType=asc'
      url += '&filters=' + encodeURIComponent(JSON.stringify(filters))

      return this.$http
        .get(url)
        .then(response => {
          if (response.data && response.data.basespaces) {
            this.sites = response.data.basespaces.filter(
              ({ spaceType }) => spaceType === 1
            )
            this.buildings = response.data.basespaces.filter(
              ({ spaceType }) => spaceType === 2
            )
          }
        })
        .catch(() => {})
    },

    loadBaselines() {
      return this.$http
        .get('/baseline/all')
        .then(response => {
          if (response.status === 200) {
            this.baselines = response.data
          }
        })
        .catch(() => {})
    },

    serializeProperty(prop, data) {
      if (prop === 'reading') {
        return {
          yAggr: data.yAggr,
          ...data.reading,
        }
      }
    },

    validateProperty() {
      return {
        baseline: () => false,
        imageSpaceId: () => false,
        reading: function(data) {
          let readingProp = this.resourceProps.find(prop => {
            if (isObject(prop)) return prop.prop === 'reading'
            else return prop === 'reading'
          })

          if (isObject(readingProp)) {
            let { resourceProps } = readingProp
            let nullableFields = ['fieldId', 'dataType']
            let hasEmptyField = false

            resourceProps.forEach(prop => {
              if (
                !nullableFields.includes(prop) &&
                isEmpty(data['reading'][prop])
              ) {
                hasEmptyField = true
              }
            })

            return hasEmptyField
          } else {
            return isEmpty(data['reading'])
          }
        }.bind(this),
      }
    },

    validateField() {
      const validator = function(rule, value, callback) {
        callback()
      }

      return {
        baseline: {
          trigger: 'change',
          validator,
        },
        imageSpaceId: {
          trigger: 'change',
          validator,
        },
      }
    },

    setReading(prop, reading) {
      if (reading) {
        let { cardDataObj } = this
        this.$set(this, 'cardDataObj', { ...cardDataObj, [prop]: reading })
        this.setConditionalVariables(reading)
      }
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
  width: 340px;
  height: 160px;
}
</style>
