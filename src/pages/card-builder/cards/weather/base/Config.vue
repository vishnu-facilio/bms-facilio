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
      {{ (cardMeta && cardMeta.name) || 'Weather Card Layout 1' }}
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
          :rules="validationRules"
          :ref="`${this.cardLayout}_form`"
          :label-position="'top'"
        >
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="baseSpaceId" class="mB10">
                <p class="fc-input-label-txt pB5">Site</p>
                <el-select
                  v-model="cardDataObj.baseSpaceId"
                  placeholder="Select a site"
                  class="width100 pR20 el-input-textbox-full-border"
                >
                  <template v-for="(site, index) in sites">
                    <el-option
                      :label="site.name"
                      :value="site.id"
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
import { isEmpty } from '@facilio/utils/validation'
import CommonConfig from 'pages/card-builder/cards/common/BaseConfig'
import Card from './Card'
import { mapState } from 'vuex'

export default {
  name: 'WeatherCard1',
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

  created() {
    this.$store.dispatch('loadSites')
  },
  data() {
    return {
      cardLayout: `weathercard_layout_1`,
      isPreviewLoading: false,
      resourceProps: ['title', 'baseSpaceId'],
      cardDataObj: {
        baseSpaceId: null,
      },
      cardState: {
        canResize: false,
        styles: {},
      },
      layout: {
        w: 24,
        h: 12,
      },
      result: null,
      buildings: [],
      validationRules: {},
    }
  },
  computed: {
    ...mapState({
      sites: state => state.sites,
    }),
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            value: {
              value: {
                temperature: 32,
                actualTtime: new Date().valueOf(),
              },
              unit: 'C',
            },
          }
    },
  },
  methods: {
    serializeProperty(prop) {
      if (prop === 'title') return ''
    },
    validateProperty() {
      return { title: () => false }
    },
  },
}
</script>
<style lang="scss" scoped>
.header {
  font-size: 22px;
  color: #324056;
  font-weight: 400;
}
.card-wrapper {
  width: 340px;
  height: 180px;
}
</style>
