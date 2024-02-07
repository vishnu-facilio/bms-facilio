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
      {{ (cardMeta && cardMeta.name) || 'Reading Card Layout 4' }}
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
              <el-row class="mT20">
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
            </el-tab-pane>

            <el-tab-pane label="Styles" name="styles">
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
              <!-- <el-row class="mB10 mT20">
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
              </el-row> -->
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
        :loading="isSaving"
        @click="save()"
        >Save</el-button
      >
    </div>
  </div>
</template>
<script>
import Config from '../type1/Config'
import Card from './Card'
import { isEmpty } from '@facilio/utils/validation'
import DateHelper from '@/mixins/DateHelper'
import {
  dateOperators,
  aggregateFunctions,
  predefinedColors,
} from 'pages/card-builder/card-constants'

export default {
  name: 'ReadingCard4',
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
  components: { Card },

  data() {
    return {
      cardLayout: `readingcard_layout_4`,
      activeTab: 'config',
      isLoading: false,
      isSaving: false,
      isPreviewLoading: false,
      resourceProps: [
        'title',
        {
          prop: 'reading',
          resourceProps: [
            'moduleName',
            'parentId',
            'fieldName',
            'yAggr',
            'dataType',
            'fieldId',
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
          yAggr: ['sum', 'min', 'max'],
        },
        dateRange: 'Today',
      },
      cardStateObj: {
        canResize: true,
        styles: {
          primaryColor: '#110d24',
          secondaryColor: '#969caf',
          backgroundColor: '#FFF',
          decimalPlace: 'Auto',
        },
      },
      result: null,
      readingPickerOptions: {
        parentId: {
          type: 'single',
        },
        fieldId: {
          type: 'single',
        },
        dataType: {
          type: 'single',
        },
        fieldName: {
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
          type: 'multiple',
        },
      },
      dateOperators,
      aggregateFunctions,
      predefinedColors,
      validationRules: {},
    }
  },
  computed: {
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: null,
            values: [
              { value: '--', aggregation: 'sum', unit: 'kWh' },
              { value: '--', aggregation: 'min', unit: 'kWh' },
              { value: '--', aggregation: 'max', unit: 'kWh' },
            ],
            period: null,
            unit: null,
          }
    },
    layout() {
      return {
        w: 37,
        h: 12,
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
  width: 210px;
  height: unset;
}
</style>
