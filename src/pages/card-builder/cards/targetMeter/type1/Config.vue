<template>
  <div class="p30 ">
    <div class="header">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ (cardMeta && cardMeta.name) || 'Gauge Card' }}
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

              <el-row class="mB10 mR20">
                <el-col :span="24">
                  <el-form-item prop="reading.fieldName" class="mB10">
                    <p class="fc-input-label-txt pB5">Reading</p>
                    <ReadingPicker
                      :options="readingPickerOptions"
                      :gaugeCard="true"
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

              <el-row class="mB20" :gutter="20">
                <el-col :span="24">
                  <el-form-item prop="safeLimitType" class="mB10">
                    <p class="fc-input-label-txt pB5">Safe Limit</p>
                    <div>
                      <el-radio
                        v-model="cardDataObj.safeLimitType"
                        :label="safeLimitTypes.MAX"
                        :value="safeLimitTypes.MAX"
                        class="fc-radio-btn"
                        >Lesser than</el-radio
                      >
                      <el-radio
                        v-model="cardDataObj.safeLimitType"
                        :label="safeLimitTypes.MIN"
                        :value="safeLimitTypes.MIN"
                        class="fc-radio-btn"
                        >Greater than</el-radio
                      >
                      <el-radio
                        v-model="cardDataObj.safeLimitType"
                        :label="safeLimitTypes.BETWEEN"
                        :value="safeLimitTypes.BETWEEN"
                        class="fc-radio-btn"
                        >Between</el-radio
                      >
                    </div>
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="
                    [safeLimitTypes.MIN, safeLimitTypes.BETWEEN].includes(
                      cardDataObj.safeLimitType
                    )
                  "
                  :span="24"
                >
                  <el-form-item prop="minSafeLimitType" class="mB10">
                    <p
                      v-if="cardDataObj.safeLimitType === 2"
                      class="fc-input-label-txt pB5 f12"
                    >
                      Minimum
                    </p>
                    <div class="d-flex flex-row mR20">
                      <div class="width50">
                        <el-select
                          v-model="cardDataObj.minSafeLimitType"
                          placeholder="Select the option"
                          class="el-input-textbox-full-border mB10 width100"
                        >
                          <el-option
                            label="Reading"
                            value="reading"
                          ></el-option>
                          <el-option
                            label="Constant"
                            value="constant"
                          ></el-option>
                        </el-select>
                      </div>
                      <div
                        class="width50 mL10"
                        v-if="cardDataObj.minSafeLimitType === 'constant'"
                      >
                        <el-input
                          :autofocus="true"
                          class="addReading-title el-input-textbox-full-border width100"
                          v-model="cardDataObj.minSafeLimitConstant"
                          :placeholder="'Enter the minimum value'"
                        ></el-input>
                      </div>
                      <div v-else>
                        <ReadingPicker
                          :options="readingPickerOptions"
                          :gaugeCard="true"
                          :initialReading="cardDataObj.minSafeLimitReading"
                          @onReadingSelect="
                            reading =>
                              setReading('minSafeLimitReading', reading)
                          "
                        ></ReadingPicker>
                      </div>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="
                    [safeLimitTypes.MAX, safeLimitTypes.BETWEEN].includes(
                      cardDataObj.safeLimitType
                    )
                  "
                  :span="24"
                >
                  <el-form-item prop="maxSafeLimitType" class="mB10">
                    <p
                      v-if="cardDataObj.safeLimitType === 2"
                      class="fc-input-label-txt pB5 f12"
                    >
                      Maximum
                    </p>
                    <div class="d-flex flex-row mR20">
                      <div class="width50">
                        <el-select
                          v-model="cardDataObj.maxSafeLimitType"
                          placeholder="Select the option"
                          class="el-input-textbox-full-border mB10 width100"
                        >
                          <el-option
                            label="Reading"
                            value="reading"
                          ></el-option>
                          <el-option
                            label="Constant"
                            value="constant"
                          ></el-option>
                        </el-select>
                      </div>
                      <div
                        v-if="cardDataObj.maxSafeLimitType === 'constant'"
                        class="width50 mL10"
                      >
                        <el-input
                          :autofocus="true"
                          class="addReading-title el-input-textbox-full-border width100"
                          v-model="cardDataObj.maxSafeLimitConstant"
                          :placeholder="'Enter the maximum value'"
                        ></el-input>
                      </div>
                      <div v-else class="width50 mL10">
                        <ReadingPicker
                          :options="readingPickerOptions"
                          :gaugeCard="true"
                          :initialReading="cardDataObj.maxSafeLimitReading"
                          @onReadingSelect="
                            reading =>
                              setReading('maxSafeLimitReading', reading)
                          "
                        ></ReadingPicker>
                      </div>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-tab-pane>

            <el-tab-pane label="Styles" name="styles">
              <el-row class="mB30" :gutter="20">
                <el-col
                  :span="12"
                  v-if="
                    ['gauge_layout_1', 'gauge_layout_3'].includes(cardLayout)
                  "
                >
                  <p class="fc-input-label-txt pB5">Gauge Colors</p>
                  <div class="mT5 card-color-container">
                    <div
                      v-for="(color, index) in cardStateObj.styles.colors"
                      :key="index"
                      class="mR10 fc-color-picker card-color-container"
                    >
                      <el-color-picker
                        v-model="color.hex"
                        :key="'' + color.id + color.hex"
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col
                  :span="12"
                  v-else-if="
                    ['gauge_layout_2', 'gauge_layout_4'].includes(cardLayout)
                  "
                >
                  <p class="fc-input-label-txt pB5">Gauge Color</p>
                  <div class="mT5">
                    <div class="mR10 fc-color-picker card-color-container">
                      <el-color-picker
                        v-model="cardStateObj.styles.color.hex"
                        :key="
                          '' +
                            cardStateObj.styles.color.id +
                            cardStateObj.styles.color.hex
                        "
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker '"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col
                  :span="12"
                  v-if="
                    ['gauge_layout_3', 'gauge_layout_4'].includes(cardLayout)
                  "
                >
                  <p class="fc-input-label-txt pB5">Tick Color</p>
                  <div
                    class="d-flex mT5 card-color-container card-color-container"
                  >
                    <div class="mR10 fc-color-picker">
                      <el-color-picker
                        v-model="cardStateObj.styles.tickColor.hex"
                        :key="
                          '' +
                            cardStateObj.styles.tickColor.id +
                            cardStateObj.styles.tickColor.hex
                        "
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>

            <el-tab-pane label="Actions" style="actions">
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

            <el-tab-pane label="FORMATTING" name="conditionalformatting">
              <div class="card-builder-criteria-block">
                <ConditionalFormating
                  ref="conditional-formatting"
                  :cardLayout="cardLayout"
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
        </el-form>
      </div>

      <div class="preview-panel section">
        <div class="card-wrapper">
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
import {
  dateOperators,
  aggregateFunctions,
  predefinedColors,
} from 'pages/card-builder/card-constants'
import { isEmpty, isObject } from '@facilio/utils/validation'
import DateHelper from '@/mixins/DateHelper'
import ReadingPicker from 'pages/card-builder/cards/common/ReadingPicker'
import ActionPicker from 'pages/card-builder/components/ActionPicker.vue'
import ConditionalFormating from 'pages/card-builder/components/CardConditionalFormating'
const safeLimitTypes = {
  MIN: 0,
  MAX: 1,
  BETWEEN: 2,
}

export default {
  extends: Config,
  props: ['isNew', 'onClose', 'onGoBack'],
  mixins: [DateHelper],
  components: { Card, ReadingPicker, ActionPicker, ConditionalFormating },
  data() {
    return {
      cardLayout: `gauge_layout_1`,
      activeTab: 'config',
      cardActions: {
        default: {
          actionType: 'showTrend',
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
        {
          name: 'minValue',
          dataType: 'NUMBER',
          displayName: 'Min value',
        },
        {
          name: 'maxValue',
          dataType: 'NUMBER',
          displayName: 'max value',
        },
      ],
      isPreviewLoading: false,
      resourceProps: [
        'title',
        {
          prop: 'reading',
          resourceProps: [
            'moduleName',
            'parentId',
            'fieldName',
            'dataType',
            'fieldId',
            'yAggr',
            'fieldId',
            'parentType',
          ],
        },
        {
          prop: 'minSafeLimitReading',
          resourceProps: [
            'moduleName',
            'parentId',
            'fieldName',
            'yAggr',
            'fieldId',
            'parentType',
          ],
        },
        'minSafeLimitType',
        'minSafeLimitConstant',
        {
          prop: 'maxSafeLimitReading',
          resourceProps: [
            'moduleName',
            'parentId',
            'fieldName',
            'yAggr',
            'fieldId',
            'parentType',
          ],
        },
        'maxSafeLimitType',
        'maxSafeLimitConstant',
        'safeLimitType',
        'dateRange',
      ],
      cardDataObj: {
        title: '',
        reading: {
          yAggr: 'lastValue',
        },
        minSafeLimitReading: {
          yAggr: 'lastValue',
        },
        minSafeLimitType: 'constant',
        minSafeLimitConstant: null,
        maxSafeLimitReading: {
          yAggr: 'lastValue',
        },
        maxSafeLimitType: 'constant',
        maxSafeLimitConstant: null,
        dateRange: 'Today',
        safeLimitType: safeLimitTypes.MAX,
      },
      cardStateObj: {
        canResize: false,
        styles: {
          tickColor: { id: 0, hex: '#969aa2' },
          color: { id: 0, hex: '#FF728E' },
          colors: [
            { id: 0, hex: '#ff7878' },
            { id: 1, hex: '#7d49ff' },
            { id: 2, hex: '#514dff' },
            { id: 3, hex: '#1eb9b7' },
          ],
          backgroundColors: [
            { id: 0, hex: '#fff' },
            { id: 0, hex: '#fff' },
          ],
        },
      },
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
      layout: {
        w: 16,
        h: 19,
      },
      result: null,
      validationRules: {},
      safeLimitTypes,
      predefinedColors: predefinedColors,
      dateOperators: dateOperators,
      aggregateFunctions: aggregateFunctions,
    }
  },
  computed: {
    previewData() {
      let { result } = this
      let { data } = result || {}

      return !isEmpty(this.result)
        ? data
        : {
            period: 'Last Month',
            maxValue: { value: 100 },
            title: 'Gauge Card 1',
            value: { unit: 'KwH', value: 65 },
          }
    },
    previewState() {
      let { result, cardStateObj } = this
      let { cardContext } = result || {}

      return !isEmpty(result && cardContext)
        ? result.state
          ? this.serializeConditionalState(result.state)
          : this.serializeState(cardStateObj)
        : this.serializeState(cardStateObj)
    },
  },
  methods: {
    serializeState() {
      let { styles, ...props } = this.cardStateObj
      return {
        ...props,
        styles: {
          colors: styles.colors.map(color => color.hex),
          backgroundColors: styles.backgroundColors.map(color => color.hex),
        },
      }
    },
    serializeConditionalState(state) {
      let { styles } = state
      let { ...props } = this.cardStateObj
      let style = {
        colors: styles.colors.map(color => {
          if (color.hex) {
            return color.hex
          } else {
            return color
          }
        }),
        backgroundColors: styles.backgroundColors.map(color => {
          if (color.hex) {
            return color.hex
          } else {
            return color
          }
        }),
      }
      return {
        ...props,
        styles: {
          ...styles,
          ...style,
        },
      }
    },

    deserializeState(cardState) {
      this.cardStateObj = {
        ...this.cardStateObj,
        styles: {
          colors: cardState.styles.colors.map((hex, index) => ({
            id: index,
            hex: hex,
          })),
          backgroundColors: cardState.styles.backgroundColors.map(
            (hex, index) => ({
              id: index,
              hex: hex,
            })
          ),
        },
      }
    },

    serializeProperty(prop, data) {
      if (['minSafeLimitReading', 'maxSafeLimitReading'].includes(prop)) {
        if (
          prop === 'minSafeLimitReading' &&
          data.minSafeLimitType === 'reading'
        ) {
          return data.minSafeLimitReading
        } else if (
          prop === 'maxSafeLimitReading' &&
          data.maxSafeLimitType === 'reading'
        ) {
          return data.maxSafeLimitReading
        } else return null
      } else if (
        prop === 'minSafeLimitConstant' &&
        data.minSafeLimitType !== 'constant'
      ) {
        return null
      } else if (
        prop === 'maxSafeLimitConstant' &&
        data.maxSafeLimitType !== 'constant'
      ) {
        return null
      } else if (['minSafeLimitType', 'maxSafeLimitType'].includes(prop)) {
        if (
          prop === 'minSafeLimitType' &&
          ![safeLimitTypes.MIN, safeLimitTypes.BETWEEN].includes(
            this.cardDataObj.safeLimitType
          )
        ) {
          return null
        } else if (
          prop === 'maxSafeLimitType' &&
          ![safeLimitTypes.MAX, safeLimitTypes.BETWEEN].includes(
            this.cardDataObj.safeLimitType
          )
        ) {
          return null
        }
      }
    },

    validateProperty() {
      let { safeLimitType } = this.cardDataObj

      let isMinRequired = type =>
        [safeLimitTypes.MIN, safeLimitTypes.BETWEEN].includes(type)
      let isMaxRequired = type =>
        [safeLimitTypes.MAX, safeLimitTypes.BETWEEN].includes(type)

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
        minSafeLimitType: data => {
          return isMinRequired(safeLimitType) && isEmpty(data.minSafeLimitType)
        },
        minSafeLimitReading: data => {
          return (
            data.minSafeLimitType === 'reading' &&
            isMinRequired(safeLimitType) &&
            isEmpty(data.minSafeLimitReading)
          )
        },
        minSafeLimitConstant: data => {
          return (
            data.minSafeLimitType === 'constant' &&
            isMinRequired(safeLimitType) &&
            isEmpty(data.minSafeLimitConstant)
          )
        },
        maxSafeLimitType: data => {
          return isMaxRequired(safeLimitType) && isEmpty(data.maxSafeLimitType)
        },
        maxSafeLimitReading: data => {
          return (
            data.maxSafeLimitType === 'reading' &&
            isMaxRequired(safeLimitType) &&
            isEmpty(data.maxSafeLimitReading)
          )
        },
        maxSafeLimitConstant: data => {
          return (
            data.maxSafeLimitType === 'constant' &&
            isMaxRequired(safeLimitType) &&
            isEmpty(data.maxSafeLimitConstant)
          )
        },
      }
    },

    validateField() {
      return {
        minSafeLimitType: {
          trigger: 'change',
          validator: function(rule, value, callback) {
            if (
              value === 'reading' &&
              isEmpty(this.cardDataObj['minSafeLimitReading'])
            ) {
              callback(new Error('Field can not be empty'))
            } else if (
              value === 'constant' &&
              isEmpty(this.cardDataObj['minSafeLimitConstant'])
            ) {
              callback(new Error('Field can not be empty'))
            } else callback()
          }.bind(this),
        },
        maxSafeLimitType: {
          trigger: 'change',
          validator: function(rule, value, callback) {
            if (
              value === 'reading' &&
              isEmpty(this.cardDataObj['maxSafeLimitReading'])
            ) {
              callback(new Error('Field can not be empty'))
            } else if (
              value === 'constant' &&
              isEmpty(this.cardDataObj['maxSafeLimitConstant'])
            ) {
              callback(new Error('Field can not be empty'))
            } else callback()
          }.bind(this),
        },
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
  width: 280px;
  height: 310px;
}
</style>
