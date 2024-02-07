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
                <el-col :span="24" class="mB10">
                  <p class="fc-input-label-txt pB10">Color Ranges (%)</p>
                  <GaugeColorRange v-model="cardStateObj.styles.ranges" />
                </el-col>
                <el-col :span="6">
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
                <el-col :span="6">
                  <p class="fc-input-label-txt pB5">Show tooltip</p>
                  <div
                    class="d-flex mT5 card-color-container card-color-container"
                  >
                    <el-checkbox
                      v-model="cardStateObj.styles.showTooltip"
                    ></el-checkbox>
                  </div>
                </el-col>
                <el-col :span="12">
                  <p class="fc-input-label-txt pB5">Arc width</p>
                  <el-form-item prop="perPage">
                    <el-input-number
                      v-model="cardStateObj.styles.arcWidth"
                      :min="1"
                      :max="100"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
              </el-row>
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
import Config from '../type1/Config'
import Card from './Card'
import { isEmpty, isObject } from '@facilio/utils/validation'
import DateHelper from '@/mixins/DateHelper'
import ReadingPicker from 'pages/card-builder/cards/common/ReadingPicker'
import GaugeColorRange from 'pages/card-builder/cards/common/GaugeColorRange'
import ActionPicker from 'pages/card-builder/components/ActionPicker.vue'
import {
  dateOperators,
  aggregateFunctions,
  predefinedColors,
} from 'pages/card-builder/card-constants'
const safeLimitTypes = {
  MIN: 0,
  MAX: 1,
  BETWEEN: 2,
}

export default {
  extends: Config,
  props: ['isNew', 'onClose', 'onGoBack'],
  mixins: [DateHelper],
  components: { Card, ReadingPicker, ActionPicker, GaugeColorRange },
  data() {
    return {
      cardLayout: `gauge_layout_6`,
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
        canResize: true,
        styles: {
          arcWidth: 2,
          showTooltip: true,
          tickColor: { id: 0, hex: '#969aa2' },
          ranges: [
            { id: 0, limit: '20', label: 'Very Poor', color: '#ff7878' },
            { id: 1, limit: '40', label: 'Poor', color: '#7d49ff' },
            { id: 2, limit: '60', label: 'Fair', color: '#514dff' },
            { id: 3, limit: '80', label: 'Good', color: '#1eb9b7' },
            { id: 4, limit: '100', label: 'Very Good', color: '#1eb9b7' },
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
            title: 'Gauge Card 3',
            value: { unit: 'KwH', value: 75 },
          }
    },
    previewState() {
      let { result, cardStateObj } = this
      let { cardContext } = result || {}

      return !isEmpty(result && cardContext)
        ? this.serializeState(cardStateObj)
        : this.serializeState(cardStateObj)
    },
  },
  methods: {
    serializeState() {
      let { styles, ...props } = this.cardStateObj
      return {
        ...props,
        styles: {
          arcWidth: styles.arcWidth,
          tickColor: styles.tickColor.hex,
          ranges: styles.ranges,
          showTooltip: styles.showTooltip,
        },
      }
    },
    deserializeState(cardState) {
      this.cardStateObj = {
        ...this.cardStateObj,
        styles: {
          arcWidth: cardState.styles.arcWidth,
          showTooltip: cardState.styles.showTooltip,
          tickColor: { id: 0, hex: cardState.styles.tickColor.hex },
          ranges: cardState.styles.ranges,
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
  width: 300px;
  height: 340px;
}
</style>
