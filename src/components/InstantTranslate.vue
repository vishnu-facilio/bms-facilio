<template>
  <div>
    <div v-if="detectLangShow">
      <div v-if="orgAndUserLanguage !== detectLanguage.language">
        <div v-if="extraContent">
          <div v-for="(translate, inex) in translateListMoreThan" :key="inex">
            <div class="fc-translate-text">
              <div class="fc-grey8-14 f13 bold">
                {{ $t('common.translation.translate_text') }}
                <el-tooltip
                  class="item"
                  effect="dark"
                  v-if="translate && translate.detectedSourceLanguage"
                  :content="
                    'Translated from' +
                      ' ' +
                      languagesData[translate.detectedSourceLanguage] || ''
                  "
                  placement="top-start"
                >
                  <i class="el-icon-info"></i>
                </el-tooltip>
              </div>
              <div class="pT5 fc-grey8-14 f13 line-height20">
                {{ translate.translatedText }}
              </div>
            </div>
          </div>
        </div>
        <div class="fc-grey8-14 f13 fc-translate-text" v-else>
          <div class="fc-grey8-14 f13 bold">
            {{ $t('common.translation.translate_text') }}
            <el-tooltip
              class="item"
              effect="dark"
              v-if="
                translateListData && translateListData.detectedSourceLanguage
              "
              :content="
                'Translated from' +
                  ' ' +
                  languagesData[translateListData.detectedSourceLanguage]
              "
              placement="top-start"
            >
              <i class="el-icon-info"></i>
            </el-tooltip>
          </div>
          <div class="pT5 fc-grey8-14 f13 line-height20">
            {{ translateListData.translatedText }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import axios from 'axios'
import { isEmpty } from '../util/validation'
import languages from 'util/data/languages'
export default {
  props: ['content', 'extraContent'],
  data() {
    return {
      detectLanguage: [],
      loading: true,
      translateListData: [],
      languageData: this.$org.language,
      seeTranslation: false,
      seeTranslationStartContent: true,
      googleKey: 'AIzaSyAeJBX0k0BxzMvvlRyCsOtPYWKVs62603w',
      translateListMoreThan: [],
      detectLangShow: false,
      languages: null,
      languageTranslateData: {},
    }
  },
  computed: {
    orgAndUserLanguage() {
      if (!isEmpty(this.$account.user.language)) {
        return this.$account.user.language
      } else {
        return this.$org.language
      }
    },
    languagesData() {
      let data = {}
      languages.forEach(rt => {
        this.$set(data, rt.value, rt.label)
      })
      return data
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.getDetectLanguage()
    })
  },
  methods: {
    // This API for detect app language
    async detectAppLanguage() {
      this.loading = true
      const response = await axios.post(
        `https://translation.googleapis.com/language/translate/v2/detect?key=${this.googleKey}&q=${this.content}`
      )
      this.detectLanguage =
        response.data &&
        response.data.data &&
        response.data.data.detections[0][0]
      this.loading = false
    },
    // This API for translate text
    async translateLanguageList() {
      this.loading = true
      let params = {
        q: [this.content],
      }
      if (this.extraContent) {
        params.q = [this.content, this.extraContent]
      }

      const response = await axios.post(
        `https://translation.googleapis.com/language/translate/v2?key=${this.googleKey}&target=${this.orgAndUserLanguage}&format=html`,
        params
      )
      this.translateListData =
        response.data &&
        response.data.data &&
        response.data.data.translations[0]
      this.translateListMoreThan =
        response.data && response.data.data && response.data.data.translations
      this.loading = false
    },
    async showLanguageHideList() {
      this.seeTranslation = true
      this.seeTranslationStartContent = false
      await this.translateLanguageList()
      this.$emit('onData')
    },
    backToOriginal() {
      this.seeTranslation = false
      this.seeTranslationStartContent = true
    },
    async getDetectLanguage() {
      this.detectAppLanguage()
      await this.translateLanguageList()
      this.$emit('reLoad')
      this.$emit('onData')
      this.detectLangShow = true
    },
  },
}
</script>
