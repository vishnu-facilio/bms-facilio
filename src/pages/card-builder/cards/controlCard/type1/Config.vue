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
      {{ (cardMeta && cardMeta.name) || 'Control Card Layout' }}
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

              <ActionPicker
                ref="action-picker"
                v-model="cardActions"
                :variables="variables"
                :elements="[
                  { name: 'set-reading-button', displayName: 'Button' },
                ]"
              >
                <template slot="element-title">
                  <div></div>
                </template>
                <template slot="action-type">
                  <div></div>
                </template>
              </ActionPicker>

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
                      <el-option :label="'None'" :value="'none'"></el-option>
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
                    <p class="fc-input-label-txt pB5">Button Color</p>
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
                <el-col :span="8">
                  <el-form-item prop="backgroundColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Show Secondary label</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-checkbox
                        v-model="cardStateObj.styles.showSecondaryText"
                      ></el-checkbox>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="8" v-if="cardStateObj.styles.showSecondaryText">
                  <el-form-item prop="backgroundColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Secondary label color</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.secondaryTextColor"
                        :key="
                          'backgroundColor' +
                            cardStateObj.styles.secondaryTextColor
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
        </el-tabs>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper">
          <Card
            :componentKey="componentKey"
            :cardData="previewData"
            :cardState="previewState"
            :cardDrilldown="cardActions"
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
export default {
  name: 'controlCard1',
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
  },

  data() {
    return {
      cardLayout: `controlcard_layout_1`,
      activeTab: 'config',
      isPreviewLoading: false,
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
      ],
      cardDataObj: {
        title: '',
        operatorId: null,
        reading: {
          yAggr: 'avg',
        },
        dateRange: 'Today',
      },
      cardActions: {
        'set-reading-button': {
          actionType: 'controlAction',
          data: {
            buttonLabel: 'Set',
            controlType: 'point',
            controlPointId: null,
            controlGroupId: null,
          },
        },
      },
      cardStateObj: {
        canResize: false,
        styles: {
          showSecondaryText: false,
          primaryColor: '#110d24',
          secondaryColor: '#1F95DA',
          secondaryTextColor: '#abb0be',
          backgroundColor: '#FFF',
        },
      },
      layout: {
        w: 16,
        h: 12,
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
      },
      predefinedColors: predefinedColors,
      dateOperators: dateOperators,
      aggregateFunctions: aggregateFunctions,
      controlPoints: [],
      controlGroups: [],
    }
  },
  computed: {
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: null,
            value: {},
            unit: null,
          }
    },
  },
  mounted() {
    if (!this.isNew) {
      let { control } = this.cardDataObj
      if (!isEmpty(control)) {
        this.cardActions['set-reading-button'].data = control
        delete this.cardDataObj.control

        this.$refs['action-picker'].init()
      }
    }
  },
  methods: {
    setReading(prop, reading) {
      if (reading) {
        let { cardDataObj } = this

        this.$set(this, 'cardDataObj', { ...cardDataObj, [prop]: reading })
        this.setConditionalVariables(reading)
      }
    },
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
        dateRange: () => false,
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.header {
  font-size: 22px;
  font-weight: 400;
  color: #324056;
}

.card-wrapper {
  width: 230px;
  height: 175px;
}
</style>
