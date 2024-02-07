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
      {{ (cardMeta && cardMeta.name) || 'Reading Card Layout 6' }}
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
              <el-form-item prop="yAggr" class="mB10">
                <p class="fc-input-label-txt pB5">Aggregation</p>
                <el-select
                  v-model="cardDataObj.yAggr"
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

          <el-row class="mB10 mT20">
            <el-col :span="6">
              <el-form-item prop="name" class="mB10">
                <p class="fc-input-label-txt pB5">Primary Text</p>
                <div class="d-flex mT5 card-color-container fc-color-picker">
                  <el-color-picker
                    v-model="cardStateObj.styles.primaryColor.hex"
                    :key="
                      '' +
                        cardStateObj.styles.primaryColor.id +
                        cardStateObj.styles.primaryColor.hex
                    "
                    :predefine="predefinedColors"
                    size="small"
                    :popper-class="'chart-custom-color-picker'"
                  ></el-color-picker>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="name" class="mB10">
                <p class="fc-input-label-txt pB5">Secondary Text</p>
                <div class="d-flex mT5 card-color-container fc-color-picker">
                  <el-color-picker
                    v-model="cardStateObj.styles.secondaryColor.hex"
                    :key="
                      '' +
                        cardStateObj.styles.secondaryColor.id +
                        cardStateObj.styles.secondaryColor.hex
                    "
                    :predefine="predefinedColors"
                    size="small"
                    :popper-class="'chart-custom-color-picker'"
                  ></el-color-picker>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="name" class="mB10">
                <p class="fc-input-label-txt pB5">Background Color</p>
                <div class="d-flex mT5 card-color-container fc-color-picker">
                  <el-color-picker
                    v-model="cardStateObj.styles.backgroundColor.hex"
                    :key="
                      '' +
                        cardStateObj.styles.backgroundColor.id +
                        cardStateObj.styles.backgroundColor.hex
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
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper card-wrapper-3">
          <Card
            :cardData="previewData"
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
import Config from '../type1/Config'
import Card from './Card'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'ReadingCard5',
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
  components: { Card },

  data() {
    return {
      cardLayout: `readingcard_layout_5`,
      resourceProps: ['title', 'readings', 'dateRange'],
      cardDataObj: {
        title: '',
        operatorId: null,
        readings: [
          {
            moduleName: null,
            parentId: null,
            fieldName: null,
            yAggr: null,
          },
        ],
        yAggr: null,
      },
      result: null,
    }
  },
  computed: {
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: 'Reading Card',
            period: 'This Month',
            values: [
              {
                name: 'Energy Meter 1',
                value: '768',
                unit: 'kWh',
              },
              {
                name: 'Energy Meter 2',
                value: '1221',
                unit: 'kWh',
              },
              {
                name: 'Energy Meter 3',
                value: '855',
                unit: 'kWh',
              },
            ],
          }
    },
    layout() {
      return {
        w: 24,
        h: 12,
      }
    },
  },
  methods: {
    setReading(prop, index, reading) {
      if (reading) {
        let {
          cardDataObj,
          cardDataObj: { readings },
        } = this

        this.$setProperty(readings, index, reading)
        this.$set(this, 'cardDataObj', { ...cardDataObj, readings })
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
  width: 220px;
}
.card-wrapper-3 {
  height: unset;
}
</style>
