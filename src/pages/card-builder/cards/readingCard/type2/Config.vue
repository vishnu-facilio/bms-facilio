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
      {{ (cardMeta && cardMeta.name) || 'Reading Card Layout 2' }}
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
              :rules="validationRules"
              :ref="`${this.cardLayout}_form`"
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
                    <p class="fc-input-label-txt pB5">Baseline</p>
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
                <el-col :span="8">
                  <el-form-item prop="arrowUpColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Arrow up</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.arrowUpColor"
                        :key="'arrowUpColor' + cardStateObj.styles.arrowUpColor"
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
                          'arrowDownColor' + cardStateObj.styles.arrowDownColor
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
import Config from '../type1/Config'
import { isEmpty } from '@facilio/utils/validation'
import Card from './Card'
import ActionPicker from 'pages/card-builder/components/ActionPicker.vue'
import ImageUpload from 'pages/card-builder/components/ImageUpload'

export default {
  extends: Config,
  components: { Card, ActionPicker, ImageUpload },
  created() {
    this.$http.get('/baseline/all').then(response => {
      if (response.status === 200) {
        this.baselines = response.data
      }
    })
  },
  mounted() {
    this.cardStateObj.canResize = true
    const decimalPlace = this.$getProperty(
      this,
      'cardStateObj.styles.decimalPlace',
      null
    )
    if (decimalPlace === 'Auto') {
      this.cardStateObj.styles.decimalPlace = -1
    }
  },
  data() {
    return {
      activeTab: 'config',
      baselines: [],
      cardLayout: 'readingcard_layout_2',
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
          ],
        },
        'dateRange',
        'baseline',
        'imageId',
      ],
      cardDataObj: {
        title: '',
        operatorId: null,
        reading: {
          yAggr: 'avg',
          baseline: 'Previous Day',
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
          arrowUpColor: '#008000',
          arrowDownColor: '#ff0100',
          decimalPlace: -1,
        },
      },
      conditionalVariables: [
        {
          name: 'value',
          dataType: 'NUMBER',
          displayName: 'Value',
        },
        {
          name: 'baselineValue',
          dataType: 'NUMBER',
          displayName: 'Baseline value',
        },
      ],
      validationRules: {},
    }
  },
  computed: {
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: null,
            value: {},
            baselineValue: {},
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
        width: isEmpty(imageId) ? '290px' : '370px',
      }
    },
  },
}
</script>
<style scoped lang="scss">
.card-wrapper {
  height: 200px;
}
.header {
  font-size: 22px;
  font-weight: 400;
  color: #324056;
}
</style>
