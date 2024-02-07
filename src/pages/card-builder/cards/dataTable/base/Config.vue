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
      {{ (cardMeta && cardMeta.name) || 'Table Layout' }}
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
            <el-tabs type="border-card" class="mT30">
              <el-tab-pane label="Script">
                <code-mirror
                  v-model="cardDataObj.script"
                  class="left-con pT20 pR20"
                ></code-mirror>
              </el-tab-pane>
              <el-tab-pane label="Config">
                <el-row>
                  <el-col :span="24">
                    <div
                      v-for="(column, index) in cardStateObj.columns"
                      :key="index"
                      class="relative"
                    >
                      <el-row class="mT10 maxWidth90" :gutter="10">
                        <el-col :span="4">
                          <el-input
                            v-model="column.key"
                            placeholder="key"
                            class="fc-input-full-border2"
                          ></el-input>
                        </el-col>
                        <el-col :span="8">
                          <el-input
                            v-model="column.label"
                            placeholder="Label"
                            class="fc-input-full-border2"
                          ></el-input>
                        </el-col>
                        <el-col :span="8">
                          <el-select
                            v-model="column.datatype"
                            :placeholder="$t('setup.setupLabel.data_type')"
                            class="fc-input-full-border-select2"
                          >
                            <el-option
                              v-for="(type, idx) in dataTypes"
                              :key="idx"
                              :label="type"
                              :value="type"
                            ></el-option>
                          </el-select>
                        </el-col>
                        <el-col :span="4">
                          <el-checkbox v-model="column.merge"></el-checkbox>
                        </el-col>
                        <el-col :span="4" v-if="column.merge">
                          <el-select
                            v-model="column.mergeKey"
                            :placeholder="$t('setup.setupLabel.data_type')"
                            class="fc-input-full-border-select2"
                          >
                            <el-option
                              v-for="(key, idx) in cardStateObj.columns.map(
                                rt => rt.key
                              )"
                              :key="idx"
                              :label="key"
                              :value="key"
                            ></el-option>
                          </el-select>
                        </el-col>
                      </el-row>
                      <div class="contion-action-btn2 pointer">
                        <img
                          src="~assets/add-icon.svg"
                          style="height:18px;width:18px;"
                          class="add-icon"
                          v-if="cardStateObj.columns.length - 1 === index"
                          @click="addContions"
                        />
                        <img
                          src="~assets/remove-icon.svg"
                          style="height:18px;width:18px;margin-right: 3px;margin-left: 3px;position:relative;top:10px;"
                          class="delete-icon"
                          v-if="cardStateObj.columns.length > 1"
                          @click="deleteCondition(index)"
                        />
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </el-tab-pane>
            </el-tabs>
          </el-row>
        </el-form>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper">
          <Card
            :cardData="previewData"
            :cardStyle="previewStyles"
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
import CommonConfig from 'pages/card-builder/cards/common/BaseConfig'
import CodeMirror from '@/CodeMirror'
import Card from './Card'
import { isEmpty } from '@facilio/utils/validation'
import DateHelper from '@/mixins/DateHelper'
import colors from 'charts/helpers/colors'
export default {
  extends: CommonConfig,
  props: ['isNew', 'onClose', 'onGoBack'],
  mixins: [DateHelper],
  components: { Card, CodeMirror },
  data() {
    return {
      cardLayout: `datatable`,
      rerenderCriteriaBuilder: false,
      isPreviewLoading: false,
      resourceProps: ['title', 'script'],
      dataTypes: ['TEXT', 'BOOLEAN', 'NUMBER', 'DATE'],
      cardDataObj: {
        title: '',
        script: null,
      },
      cardStateObj: {
        canResize: true,
        columns: [
          {
            key: '',
            label: '',
            datatype: 'TEXT',
            width: '',
            merge: false,
            mergeKey: null,
            conditionalFormatting: [],
          },
        ],
      },
      layout: {
        w: 96,
        h: 40,
      },
      result: null,
      validationRules: {},
    }
  },
  computed: {
    previewData() {
      let { result } = this
      let { data } = result || {}

      return this.result
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
    addContions() {
      let emptyData = {
        key: 'a',
        label: 'Name',
        datatype: 'TEXT',
        width: '',
        merge: false,
        mergeKey: null,
      }
      this.cardStateObj.columns.push(emptyData)
    },
    deleteCondition(index) {
      this.cardStateObj.columns.splice(index, 1)
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    serializeState() {
      return this.cardStateObj
    },

    serializeProperty(prop, data) {},

    validateProperty() {},

    validateField() {},
  },
}
</script>
