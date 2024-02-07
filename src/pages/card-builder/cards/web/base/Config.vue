<template>
  <div class="p30">
    <div class="header cards-config-header ">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ (cardMeta && cardMeta.name) || 'Connected App' }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div class="container mT20 mB60">
      <div class="section">
        <el-form
          :model="cardDataObj"
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
              <el-form-item prop="title" class="mB10">
                <p class="fc-input-label-txt pB5">Type</p>
                <template>
                  <el-radio v-model="cardDataObj.type" label="url"
                    >Custom URL</el-radio
                  >
                  <el-radio v-model="cardDataObj.type" label="conncetdapp"
                    >Connected App
                  </el-radio>
                </template>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row class="mB10" v-if="cardDataObj.type === 'url'">
            <el-col :span="24">
              <el-form-item prop="title" class="mB10">
                <p class="fc-input-label-txt pB5">Web URL</p>
                <el-input
                  v-model="cardDataObj.url"
                  class="width100 fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row class="mB10" v-else>
            <el-col :span="24">
              <el-form-item prop="dateRange" class="mB10">
                <p class="fc-input-label-txt pB5">Connected Apps</p>
                <el-select
                  v-model="cardDataObj.connectedAppWidgetId"
                  placeholder="Please select the connected app widget"
                  class="width100  el-input-textbox-full-border"
                >
                  <template v-for="(apps, index) in connectedAppWidgets">
                    <el-option
                      :label="apps.widgetName"
                      :value="apps.id"
                      :key="index"
                    ></el-option>
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper">
          <Card
            :cardDataObj="cardDataObj"
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
import CommonConfig from 'pages/card-builder/cards/common/BaseConfig'
import { isEmpty } from '@facilio/utils/validation'
import Card from './Card'
export default {
  name: 'WebCard1',
  extends: CommonConfig,
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
  mounted() {
    this.loadConnectedApps()
  },
  data() {
    return {
      cardLayout: `web_layout_1`,
      isPreviewLoading: false,
      connectedAppWidgets: [],
      resourceProps: ['title', 'url', 'type', 'connectedAppWidgetId'],
      cardDataObj: {
        title: '',
        url: null,
        type: 'url',
        connectedAppWidgetId: null,
      },
      layout: {
        w: 48,
        h: 24,
      },
      cardStateObj: {
        canResize: true,
      },
      result: null,
    }
  },
  computed: {
    previewData() {
      let { result } = this
      let { data } = result || {}

      return !isEmpty(this.result)
        ? data
        : {
            title: 'Web Card',
            value: null,
          }
    },
  },
  methods: {
    validateProperty() {
      let { type, connectedAppWidgetId, url } = this.cardDataObj
      return {
        connectedAppWidgetId: () => {
          if (type === 'conncetdapp' && isEmpty(connectedAppWidgetId)) {
            return true
          }
          return false
        },
        url: () => {
          if (type === 'url' && isEmpty(url)) {
            return true
          }
          return false
        },
      }
    },
    loadConnectedApps() {
      let that = this
      let url =
        '/v2/connectedApps/widgetList?filters=' +
        encodeURIComponent(
          JSON.stringify({ entityType: { operatorId: 9, value: ['3'] } })
        )
      that.$http
        .get(url)
        .then(response => {
          that.connectedAppWidgets = response.data.result.connectedAppWidgets
            ? response.data.result.connectedAppWidgets
            : []
        })
        .catch(() => {})
    },
  },
}
</script>
<style lang="scss" scoped>
.card-builder-popup .container .section {
  flex-basis: 40%;
}
.card-builder-popup .container .preview-panel {
  flex-basis: 50%;
  margin: auto;
}
.card-wrapper {
  width: 450px;
  height: 400px;
}
</style>
