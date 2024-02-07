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
      {{ (cardMeta && cardMeta.name) || 'Reading Card Layout 3' }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div class="container mT20">
      <div class="section config-panel panel-scroll">
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
              <el-row class="mB10" :gutter="4">
                <el-col :span="10">
                  <el-form-item prop="name" class="mB10">
                    <p class="fc-input-label-txt pB5">Trend Period</p>
                    <el-select
                      v-model="cardDataObj.trend.xAggr"
                      placeholder="Please select an aggregation"
                      class="width100 pR20 el-input-textbox-full-border"
                    >
                      <el-option
                        v-for="(value, key) in periodAggregateFunctions"
                        :label="key"
                        :value="value"
                        :key="value"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="10">
                  <el-form-item prop="name" class="mB10">
                    <p class="fc-input-label-txt pB5">Trend Aggregation</p>
                    <el-select
                      v-model="cardDataObj.trend.yAggr"
                      placeholder="Please select an aggregation"
                      class="width100 pR20 el-input-textbox-full-border"
                    >
                      <el-option
                        v-for="(fn, index) in aggregateFunctions.filter(
                          ({ value }) => value !== 'lastValue'
                        )"
                        :label="fn.label"
                        :value="fn.enumValue"
                        :key="index"
                      ></el-option>
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
                <el-col :span="8" class="mT30">
                  <el-form-item prop="graphColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Graph Color</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.graphColor"
                        :key="
                          'backgroundColor' + cardStateObj.styles.graphColor
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
              </el-row>
            </el-form>
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
            :cardData="previewData"
            :cardState="previewState"
            :isLoading="isPreviewLoading"
          ></Card>
        </div>
        <!-- card tools -->
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
import {
  aggregateFunctions,
  periodAggregateFunctions,
} from 'pages/card-builder/card-constants'
import Config from '../type1/Config'
import Card from './Card'
import ActionPicker from 'pages/card-builder/components/ActionPicker.vue'

export default {
  extends: Config,
  components: { Card, ActionPicker },
  data() {
    return {
      cardLayout: `readingcard_layout_3`,
      resourceProps: [
        'title',
        {
          prop: 'reading',
          resourceProps: [
            'moduleName',
            'parentId',
            'fieldName',
            'yAggr',
            'parentType',
            'parentName',
            'fieldId',
          ],
        },
        {
          prop: 'trend',
          resourceProps: ['xAggr', 'yAggr'],
        },
        'dateRange',
      ],
      cardDataObj: {
        title: '',
        operatorId: null,
        reading: {
          yAggr: 'avg',
        },
        trend: {
          xAggr: 12,
          yAggr: 3,
        },
        dateRange: 'Today',
      },
      cardStateObj: {
        canResize: true,
        styles: {
          primaryColor: '#110d24',
          secondaryColor: '#969caf',
          backgroundColor: '#FFF',
          graphColor: '#54a5ff',
          decimalPlace: -1,
        },
      },
      periodAggregateFunctions,
      aggregateFunctions,
    }
  },
}
</script>
