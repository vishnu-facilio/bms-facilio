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

              <el-row class="">
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
                      class="fc-input-label-txt pB5"
                    >
                      Minimum
                    </p>
                    <div class="mR20">
                      <el-row :gutter="20">
                        <el-col :span="12">
                          <div class="">
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
                        </el-col>
                        <el-col
                          :span="12"
                          v-if="cardDataObj.minSafeLimitType === 'constant'"
                        >
                          <div class="">
                            <el-input
                              :autofocus="true"
                              class="addReading-title el-input-textbox-full-border width100"
                              v-model="cardDataObj.minSafeLimitConstant"
                              :placeholder="'Enter the minimum value'"
                            ></el-input>
                          </div>
                        </el-col>
                        <el-col :span="12" v-else>
                          <div>
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
                        </el-col>
                      </el-row>
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
                      class="fc-input-label-txt pB5"
                    >
                      Maximum
                    </p>
                    <div class="mR20">
                      <el-row :gutter="20">
                        <el-col :span="12">
                          <div>
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
                        </el-col>
                        <el-col
                          :span="12"
                          v-if="cardDataObj.maxSafeLimitType === 'constant'"
                        >
                          <div>
                            <el-input
                              :autofocus="true"
                              class="addReading-title el-input-textbox-full-border width100"
                              v-model="cardDataObj.maxSafeLimitConstant"
                              :placeholder="'Enter the maximum value'"
                            ></el-input>
                          </div>
                        </el-col>
                        <el-col :span="12" v-else>
                          <div>
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
                        </el-col>
                      </el-row>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10" :gutter="20">
                <el-col :span="12">
                  <p class="fc-input-label-txt pB5">No of ticks</p>
                  <div class="d-flex mT5 card-color-container">
                    <div class="mR10 fc-color-picker">
                      <el-input
                        type="number"
                        v-model="cardStateObj.styles.majorTicksCount"
                        class="width100 pR20 fc-input-full-border2"
                      ></el-input>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>

            <el-tab-pane label="Styles" name="styles">
              <el-row class="mB30" :gutter="20">
                <el-col :span="6">
                  <p class="fc-input-label-txt pB5">Bar start Color</p>
                  <div class="d-flex mT5 card-color-container">
                    <div class="mR10 fc-color-picker">
                      <el-color-picker
                        v-model="cardStateObj.styles.colorBarProgress"
                        :key="'' + cardStateObj.styles.colorBarProgress"
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <p class="fc-input-label-txt pB5">Bar end color</p>
                  <div class="d-flex mT5 card-color-container">
                    <div class="mR10 fc-color-picker">
                      <el-color-picker
                        v-model="cardStateObj.styles.colorBarProgressEnd"
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </div>
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
export default {
  extends: Config,
  components: { Card },
  data() {
    return {
      cardLayout: `gauge_layout_5`,
      cardStateObj: {
        canResize: true,
        styles: {
          colorBarProgress: '#3adaad',
          colorBarProgressEnd: '#0232ab',
          majorTicksCount: 5,
        },
      },
    }
  },
  methods: {
    serializeState() {
      let { styles, ...props } = this.cardStateObj
      return {
        ...props,
        styles: styles,
      }
    },

    deserializeState(cardState) {
      this.cardStateObj = {
        ...this.cardStateObj,
        styles: cardState.styles,
      }
    },
  },
}
</script>

<style></style>
