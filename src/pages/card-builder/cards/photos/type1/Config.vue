<template>
  <div class="p30">
    <div class="header cards-config-header">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ 'Photos Card' }}
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
          :ref="`${cardLayout}_form`"
          :rules="validationRules"
          :label-position="'top'"
        >
          <el-tabs v-model="activeTab" class="card-tab-fixed">
            <el-tab-pane label="Module" name="module">
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
                  <el-form-item prop="moduleName" class="mB10">
                    <p class="fc-input-label-txt pB5">Module</p>
                    <el-select
                      v-model="cardDataObj.moduleName"
                      filterable
                      clearable
                      @change="resetRelatedFields"
                      placeholder="Please select a module"
                      class="width100 fc-input-full-border2"
                    >
                      <el-option
                        v-for="(moduleObj, key) in modulesList"
                        :label="moduleObj.label"
                        :value="moduleObj.name"
                        :key="key"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="moduleName" class="mB10">
                    <p class="fc-input-label-txt pB5">Photos</p>
                    <el-select
                      v-model="cardDataObj.attachmentModule"
                      filterable
                      placeholder="Please select"
                      class="width100 fc-input-full-border2"
                    >
                      <el-option
                        v-for="(moduleObj, key) in $constants
                          .moduleVsPhotoModules[cardDataObj.moduleName] || []"
                        :label="moduleObj.label"
                        :value="moduleObj.name"
                        :key="key"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="limit" class="mB10">
                    <p class="fc-input-label-txt pB5">Limit</p>
                    <el-input-number
                      v-model="cardDataObj.limit"
                      size="mini"
                      :min="10"
                      :max="100"
                      :step="10"
                      class="fc-input-full-border2"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane label="Filters" name="critera">
              <NewCriteriaBuilder
                class="graphics-criteria-builder"
                title="Specify conditions"
                @condition="getCriteria"
                :exrule="cardDataObj.criteria"
                :module="'workorder'"
              ></NewCriteriaBuilder>
            </el-tab-pane>
            <el-tab-pane label="Settings" name="config">
              <el-row :gutter="20" class="mB10">
                <el-col :span="12">
                  <p class="fc-input-label-txt pB5">Mode</p>
                  <el-select
                    v-model="cardStateObj.configuration.mode"
                    filterable
                    placeholder="Please select"
                    class="width100 fc-input-full-border2"
                  >
                    <el-option
                      label="Carousel"
                      value="carousel"
                      key="'1'"
                    ></el-option>
                    <el-option
                      label="Thumbnail"
                      value="thumbnail"
                      key="'2'"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row :gutter="20" class="mB10">
                <el-col :span="8">
                  <p class="fc-input-label-txt pB5">Show Details</p>
                  <el-checkbox
                    v-model="cardStateObj.configuration.showDetails"
                  ></el-checkbox>
                </el-col>
                <el-col :span="8">
                  <p class="fc-input-label-txt pB5">Hide Header</p>
                  <el-checkbox
                    v-model="cardStateObj.configuration.hideHeader"
                  ></el-checkbox>
                </el-col>
                <el-col :span="8">
                  <p class="fc-input-label-txt pB5">Background Color</p>
                  <div class="d-flex mT5 card-color-container fc-color-picker">
                    <el-color-picker
                      v-model="cardStateObj.configuration.backgroundColor"
                      :key="
                        'backgroundColor' +
                          cardStateObj.configuration.backgroundColor
                      "
                      :predefine="predefinedColors"
                      size="small"
                      :popper-class="'chart-custom-color-picker'"
                    ></el-color-picker>
                  </div>
                </el-col>
              </el-row>
              <div v-if="cardStateObj.configuration.mode === 'carousel'">
                <el-row :gutter="20" class="mB10">
                  <el-col :span="8">
                    <p class="fc-input-label-txt pB5">
                      Carousel Type
                    </p>
                    <el-select
                      v-model="cardStateObj.configuration.type"
                      filterable
                      placeholder="Please select"
                      class="width100 fc-input-full-border2"
                    >
                      <el-option
                        label="Basic"
                        value="basic"
                        key="'1'"
                      ></el-option>
                      <el-option
                        label="Card"
                        value="card"
                        key="'2'"
                      ></el-option>
                    </el-select>
                  </el-col>

                  <el-col :span="8">
                    <p class="fc-input-label-txt pB5">Trigger</p>
                    <el-select
                      v-model="cardStateObj.configuration.trigger"
                      filterable
                      placeholder="Please select"
                      class="width100 fc-input-full-border2"
                    >
                      <el-option
                        label="Hover"
                        value="hover"
                        key="'1'"
                      ></el-option>
                      <el-option
                        label="Click"
                        value="click"
                        key="'2'"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="8">
                    <p class="fc-input-label-txt pB5">Show Prev/Next Arrow</p>
                    <el-select
                      v-model="cardStateObj.configuration.arrow"
                      filterable
                      placeholder="Please select"
                      class="width100 fc-input-full-border2"
                    >
                      <el-option
                        label="Always"
                        value="always"
                        key="'1'"
                      ></el-option>
                      <el-option
                        label="Hover"
                        value="hover"
                        key="'2'"
                      ></el-option>
                      <el-option
                        label="Never"
                        value="never"
                        key="'3'"
                      ></el-option>
                    </el-select>
                  </el-col>
                </el-row>
                <el-row :gutter="20" class="mB10">
                  <el-col :span="8">
                    <p class="fc-input-label-txt pB5">
                      Interval(in seconds)
                    </p>
                    <el-input
                      v-model="cardStateObj.configuration.interval"
                      type="number"
                      :min="1"
                      :max="100"
                      class="width100 fc-input-full-border2"
                    ></el-input>
                  </el-col>
                  <el-col :span="8">
                    <p class="fc-input-label-txt pB5">Auto Play</p>
                    <el-checkbox
                      v-model="cardStateObj.configuration.autoplay"
                    ></el-checkbox>
                  </el-col>
                  <el-col :span="8">
                    <p class="fc-input-label-txt pB5">Loop</p>
                    <el-checkbox
                      v-model="cardStateObj.configuration.loop"
                    ></el-checkbox>
                  </el-col>
                </el-row>
                <el-row :gutter="20" class="mB10"> </el-row>
              </div>
              <div v-else-if="cardStateObj.configuration.mode === 'thumbnail'">
                <el-row :gutter="20" class="mB10">
                  <el-col :span="12">
                    <p class="fc-input-label-txt pB5">
                      Rows
                    </p>
                    <el-input
                      v-model="cardStateObj.configuration.rows"
                      type="number"
                      :min="1"
                      :max="20"
                      class="width100 fc-input-full-border2"
                    ></el-input>
                  </el-col>
                  <el-col :span="12">
                    <p class="fc-input-label-txt pB5">
                      Columns
                    </p>
                    <el-input
                      v-model="cardStateObj.configuration.cols"
                      type="number"
                      :min="1"
                      :max="20"
                      class="width100 fc-input-full-border2"
                    ></el-input>
                  </el-col>
                </el-row>
                <el-row :gutter="20" class="mB10">
                  <el-col :span="12">
                    <p class="fc-input-label-txt pB5">Scroll</p>
                    <el-select
                      v-model="cardStateObj.configuration.overflow"
                      filterable
                      placeholder="Please select"
                      class="width100 fc-input-full-border2"
                    >
                      <el-option
                        label="Horizontal"
                        value="column"
                        key="'1'"
                      ></el-option>
                      <el-option
                        label="Vertical"
                        value="row"
                        key="'2'"
                      ></el-option>
                      <el-option
                        label="Hidden"
                        value="hidden"
                        key="'3'"
                      ></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-form>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper width90 height100">
          <Card
            :cardData="previewData"
            :cardStyle="previewStyles"
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
import NewCriteriaBuilder from 'src/components/NewCriteriaBuilder'
import BaseConfig from 'pages/card-builder/cards/common/BaseConfig'
import { isEmpty } from '@facilio/utils/validation'
import Card from './Card'
import criteriaHelper from 'src/util/criteriaHelper'
import { predefinedColors } from 'pages/card-builder/card-constants'
export default {
  props: [
    'isNew',
    'cardType',
    'onClose',
    'onGoBack',
    'onCardSave',
    'onCardUpdate',
    'closePopup',
  ],
  extends: BaseConfig,
  components: { NewCriteriaBuilder, Card },
  mixins: [criteriaHelper],
  data() {
    return {
      cardLayout: `photos_layout_1`,
      resourceProps: [
        'title',
        'moduleName',
        'attachmentModule',
        'criteria',
        'configuration',
        'limit',
      ],
      activeTab: 'module',
      modulesList: [{ name: 'workorder', label: 'Workorder' }],
      result: null,
      cardDataObj: {
        moduleName: null,
        title: null,
        attachmentModule: null,
        criteria: null,
        limit: 100,
      },
      validationRules: {},
      layout: {
        w: 30,
        h: 30,
      },
      cardStateObj: {
        canResize: true,
        configuration: {
          mode: 'carousel',
          showDetails: true,
          interval: 4,
          autoplay: true,
          loop: true,
          trigger: 'click',
          arrow: 'hover',
          type: 'basic',
          rows: 2,
          cols: 2,
          overflow: 'column',
          hideHeader: false,
          backgroundColor: '#fff',
        },
      },
      isPreviewLoading: false,
      predefinedColors,
    }
  },
  created() {},
  computed: {
    previewData() {
      let { result } = this
      let { data } = result || {}

      return !isEmpty(this.result) ? data : this.getSamplePreviewData
    },
    getSamplePreviewData() {
      let sampleData = {}
      sampleData['recordsPrimaryValue'] = { 0: 'Record Name' }
      sampleData['title'] = 'Title'
      sampleData['subModuleVsRecords'] = [{ records: [] }]
      let sampleRecords = []
      for (let i = 0; i < 50; i++) {
        sampleRecords.push({
          contentType: 'image/jpeg',
          previewUrl: require('src/assets/dummy-images/nyc-building.jpg'),
          uploadedBy: (this.$account.user || {}).id || -1,
          uploadedTime: this.$options.filters.now(),
          fileId: -1,
          parentId: 0,
          fileName: 'FileName.jpg',
        })
      }
      sampleData.subModuleVsRecords[0].records = sampleRecords
      return sampleData
    },
    previewStyles() {
      let { result, cardStateObj } = this
      let { cardContext } = result || {}

      return !isEmpty(this.result)
        ? cardContext.cardState || {}
        : this.serializeState(cardStateObj)
    },
  },
  methods: {
    getCriteria(criteria) {
      this.cardDataObj.criteria = criteria
    },
    resetRelatedFields() {
      this.cardDataObj.criteria = null
      this.cardDataObj.attachmentModule = null
    },
    serializeProperty(prop, data) {
      if (prop === 'criteria') {
        let criteria = this.$helpers.cloneObject(data.criteria)
        criteria = !isEmpty(criteria) ? this.serializeCriteria(criteria) : null
        return criteria
      }
    },
  },
}
</script>
