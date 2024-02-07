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
      {{ (cardMeta && cardMeta.name) || 'Reading Card Layout' }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div class="container mT20">
      <div class="section config-panel">
        <el-tabs v-model="activeTab" class="card-tab-fixed">
          <el-tab-pane label="Config" name="config">
            <el-form
              :model="cardDataObj"
              :ref="`${this.cardLayout}_form`"
              :rules="validationRules"
              :label-position="'top'"
            >
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
              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="reading" class="mB10">
                    <p class="fc-input-label-txt pB5">Readings</p>
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
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="Styles" name="styles">
            <el-form :model="cardStateObj" :label-position="'top'">
              <el-row class="mB10 mT20">
                <el-col :span="8">
                  <el-form-item prop="primaryColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Primary Text</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.primaryColor"
                        :key="'primaryColor' + cardStateObj.styles.primaryColor"
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item prop="secondaryColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Secondary Text</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.secondaryColor"
                        :key="
                          'secondaryColor' + cardStateObj.styles.secondaryColor
                        "
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item prop="backgroundColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Background Color</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.backgroundColor"
                        :key="
                          'backgroundColor' +
                            cardStateObj.styles.backgroundColor
                        "
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10 mT20">
                <el-col :span="8">
                  <el-form-item prop="backgroundColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Decimal Place</p>
                    <el-select
                      v-model="cardStateObj.styles.decimalPlace"
                      placeholder="Please select a period"
                      class="width100 el-input-textbox-full-border"
                    >
                      <template v-for="(value, key) in decimalPlaces">
                        <el-option
                          :label="key"
                          :value="value"
                          :key="key"
                        ></el-option>
                      </template>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="16">
                  <el-form-item prop="backgroundColor" class="mB10">
                    <unit-picker
                      class="pL10"
                      :unitConfig="cardStateObj.styles.unitConfig"
                    ></unit-picker>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10 mT20">
                <el-form-item class="mB10">
                  <p class="fc-input-label-txt pB10">Card Image</p>
                  <ImageUpload v-model="cardDataObj['imageId']"></ImageUpload>
                </el-form-item>
              </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="Actions" name="actions">
            <ActionPicker
              ref="action-picker"
              v-model="cardActions"
              :elements="[{ name: 'default', displayName: 'Default' }]"
              :variables="variables"
            >
              <template slot="element-title">
                <div class="pT15"></div>
              </template>
            </ActionPicker>
          </el-tab-pane>
          <el-tab-pane label="FORMATTING" name="conditionalformatting">
            <div class="card-builder-criteria-block">
              <ConditionalFormating
                ref="conditional-formatting"
                :variables="conditionalVariables"
                :cardData="previewData"
                v-model="conditionalFormatting"
                :cardStyles="cardStateObj.styles"
              ></ConditionalFormating>
            </div>
          </el-tab-pane>
          <el-tab-pane label="CUSTOM SCRIPT" v-if="nameSpaces.length">
            <div class="p10">
              <el-checkbox
                v-model="enableBoxchecked"
                @change="changescriptModeInt()"
                >Enable Custom Script</el-checkbox
              >
              <div v-if="enableBoxchecked == true" class="mT30">
                <div v-if="nameSpaces.length">
                  <p class="fc-input-label-txt pB5">
                    Select Function
                  </p>
                  <el-select
                    placeholder="Please Select a Script"
                    class="width100 el-input-textbox-full-border"
                    v-model="customScriptId"
                  >
                    <el-option-group
                      v-for="(nameSpace, index) in nameSpaces"
                      :key="index"
                      :label="nameSpace.name"
                    >
                      <el-option
                        v-for="(func, index) in nameSpace.functions"
                        :key="index"
                        :label="func.name"
                        :value="func.id"
                      >
                      </el-option>
                    </el-option-group>
                  </el-select>
                </div>
                <div v-else class="fc-input-label-txt mT30">
                  No functions available. Kindly add some functions and check.
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper" :style="cardWrapper">
          <Card
            :cardData="previewData"
            :cardState="previewState"
            :isLoading="isPreviewLoading"
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
import Config from '../base/Config'
import Card from './Card'
import { isEmpty, isObject } from '@facilio/utils/validation'
import DateHelper from '@/mixins/DateHelper'
import {
  dateOperators,
  aggregateFunctions,
  predefinedColors,
} from 'pages/card-builder/card-constants'
import ReadingPicker from 'pages/card-builder/cards/common/ReadingPicker'
import ActionPicker from 'pages/card-builder/components/ActionPicker.vue'
import ConditionalFormating from 'pages/card-builder/components/CardConditionalFormating'
import ImageUpload from 'pages/card-builder/components/ImageUpload'

export default {
  name: 'ReadingCard1',
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
  mixins: [DateHelper],
  components: {
    Card,
    ReadingPicker,
    ActionPicker,
    ConditionalFormating,
    ImageUpload,
  },

  data() {
    return {
      cardLayout: `readingcard_layout_1`,
      activeTab: 'config',
      isPreviewLoading: false,
      resourceProps: [
        'title',
        {
          prop: 'reading',
          resourceProps: [
            'moduleName',
            'parentId',
            'fieldName',
            'fieldId',
            'dataType',
            'yAggr',
            'parentType',
            'parentName',
          ],
        },
        'dateRange',
        'imageId',
      ],
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
      cardDataObj: {
        title: '',
        operatorId: null,
        reading: {
          yAggr: 'avg',
        },
        dateRange: 'Today',
        imageId: null,
      },
      cardStateObj: {
        canResize: true,
        styles: {
          primaryColor: '#110d24',
          secondaryColor: '#969caf',
          backgroundColor: '#FFF',
          decimalPlace: -1,
          unitConfig: {
            unit: null,
            position: 2,
          },
        },
      },
      cardActions: {
        default: {
          actionType: 'showTrend',
        },
      },
      result: null,
      validationRules: {},
      readingPickerOptions: {
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
      predefinedColors: predefinedColors,
      dateOperators: dateOperators,
      aggregateFunctions: aggregateFunctions,
    }
  },
  mounted() {
    const decimalPlace = this.$getProperty(
      this,
      'cardStateObj.styles.decimalPlace',
      null
    )
    if (decimalPlace === 'Auto') {
      this.cardStateObj.styles.decimalPlace = -1
    }
  },
  computed: {
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: null,
            value: {},
            period: null,
            unit: null,
          }
    },
    layout() {
      let { imageId } = this.cardDataObj
      return {
        w: isEmpty(imageId) ? 24 : 26,
        h: 16,
      }
    },
    cardWrapper() {
      let { imageId } = this.cardDataObj
      return {
        width: isEmpty(imageId) ? '220px' : '300px',
      }
    },
  },
  methods: {
    validateProperty() {
      return {
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
        imageId: () => false,
        baseline: () => false,
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
  height: 155px;
}
</style>
