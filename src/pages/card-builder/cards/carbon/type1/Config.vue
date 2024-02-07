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
      Carbon Card
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
          <el-row class="mB10 mR20">
            <el-col :span="24">
              <el-form-item prop="reading" class="mB10">
                <p class="fc-input-label-txt pB5">Reading</p>
                <ReadingPicker
                  :options="readingPickerOptions"
                  :initialReading="cardDataObj.reading"
                  @onReadingSelect="reading => setReading('reading', reading)"
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

          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="multiplier" class="mB10">
                <p class="fc-input-label-txt pB5">
                  Carbon - Multiplication Factor
                </p>
                <el-input
                  :autofocus="isNew"
                  v-model="cardDataObj.multiplier"
                  class="width100 pR20 fc-input-full-border2"
                ></el-input>
                <div class="fc-grey2 f11 pT5">
                  * (Multiplication Factor * Reading )
                </div>
              </el-form-item>
            </el-col>
          </el-row>
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
} from 'pages/card-builder/card-constants'

export default {
  name: 'CarbonCard1',
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
  components: { Card, ReadingPicker },
  data() {
    return {
      cardLayout: `carboncard_layout_1`,
      isPreviewLoading: false,
      resourceProps: [
        {
          prop: 'reading',
          resourceProps: [
            'moduleName',
            'parentId',
            'fieldName',
            'fieldId',
            'dataType',
            'yAggr',
          ],
        },
        'multiplier',
        'dateRange',
      ],
      cardDataObj: {
        reading: {
          yAggr: 'sum',
        },
        dateRange: 'Current Month',
        multiplier: 0.44,
      },
      cardState: {
        canResize: false,
        styles: {},
      },
      layout: {
        w: 24,
        h: 12,
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
      result: null,
      validationRules: {},
      dateOperators,
      aggregateFunctions,
    }
  },
  computed: {
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            value: {
              value: null,
              unit: null,
            },
            period: 'This Month',
          }
    },
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
  height: 180px;
}
</style>
