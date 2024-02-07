<template>
  <div
    v-if="$hasPermission('asset:IMPORT') || $hasPermission('space:IMPORT')"
    class="asset-import-page"
  >
    <div class="info-banner" v-if="showBanner">
      Asset reading import will be available only in
      <span @click="goToAppDownload" class="pointer link-text-2"
        >Facilio dataloader</span
      >
    </div>
    <div class="asset-import-main-con">
      <div class="asset-import-lCol">
        <div class="asset-import-lList">
          <ul>
            <li class="label-txt-black pointer">
              <div
                :class="uploadPage ? 'dot-active-pink' : 'dot-inactive-grey'"
              ></div>
              Upload
            </li>
            <li class="label-txt-black pointer">
              <div
                :class="fieldMapping ? 'dot-active-pink' : 'dot-inactive-grey'"
              ></div>
              Field Mapping
            </li>
            <li class="label-txt-black pointer">
              <div
                :class="
                  dataValidation ? 'dot-active-pink' : 'dot-inactive-grey'
                "
              ></div>
              Data Validation
            </li>
            <li class="label-txt-black pointer">
              <div
                :class="finalUpload ? 'dot-active-pink' : 'dot-inactive-grey'"
              ></div>
              Finish
            </li>
          </ul>
        </div>
      </div>
      <div class="asset-import-rCol">
        <div class="fc__layout__align pB20">
          <div class="heading-black22 line line-height0">
            {{ 'Readings Import' }}
          </div>
          <div
            class="fc-dark-blue4-12 pointer line-height0"
            @click="historyPageVisibility = true"
          >
            Show Import History
          </div>
        </div>
        <div class="asset-import-main-bg">
          <reading-import-setting
            importMode="2"
            v-if="uploadPage"
            :importProcessContext.sync="importProcessContext"
            @proceed="moveToFieldMapping"
          ></reading-import-setting>
          <import-reading
            v-if="fieldMapping"
            @hideBanner="
              value => {
                this.showBanner = value
              }
            "
            @setTimeFormat="
              data => {
                timeFormat = data
              }
            "
            @moveToValidation="moveToValidation"
            @begin="moveToBegin"
            :importProcessContext.sync="importProcessContext"
          ></import-reading>
          <data-validation
            v-if="dataValidation"
            :timeColumnDateFormat="timeFormat"
            :dataValidation.sync="dataValidation"
            :parseError="parseError"
            :importProcessContext.sync="importProcessContext"
            @finishValidation="finishValidation"
            @begin="moveToBegin"
          ></data-validation>
          <final-upload
            v-if="finalUpload"
            @finalDone="finalDone"
            :importProcessContext.sync="importProcessContext"
          ></final-upload>
        </div>
      </div>
    </div>
    <import-history
      v-if="historyPageVisibility"
      :visibility.sync="historyPageVisibility"
      @showUpload="toggleHistoryPage"
      @continueImport="data => continueImport(data)"
      :historyMeta="{ importMode: 2 }"
    ></import-history>
  </div>
</template>
<script>
import ImportHistory from '@/ImportHistory'
import ReadingImportSetting from 'pages/assets/import/readings/ReadingImportSetting'
import DataValidation from 'pages/assets/import/v1/DataValidation'
import ImportReading from 'pages/assets/import/readings/ReadingFieldMapping'
import FinalUpload from 'pages/assets/import/v1/FinalUpload'
import importHelper from 'pages/assets/import/v1/ImportHelper.vue'
import { findTab, isWebTabsEnabled, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  mixins: [importHelper],
  data() {
    return {
      showBanner: true,
      historyPageVisibility: false,
      uploadPage: true,
      fieldMapping: false,
      dataValidation: false,
      finalUpload: false,
      importProcessContext: null,
      timeFormat: null,
      parseError: {
        visibility: false,
        errorMessage: '',
      },
    }
  },
  components: {
    ImportHistory,
    ReadingImportSetting,
    ImportReading,
    DataValidation,
    FinalUpload,
  },
  title() {
    return 'Readings Import'
  },
  created() {
    this.init()
  },
  mounted() {
    if (this.$route.query?.assetreading === 'true') {
      this.showBanner = false
    }
  },
  methods: {
    init() {
      let hasPermission = false

      if (isWebTabsEnabled()) {
        let { tabId, groupId } = findTab(tabTypes.MODULE, {
          moduleName: 'asset',
        })

        if (!isEmpty(tabId)) {
          hasPermission = this.$hasPermission('asset:IMPORT', tabId)
          if (hasPermission) {
            this.$store.dispatch('webtabs/setTabGroup', groupId)
            this.$store.dispatch('webtabs/setTab', tabId)
          }
        }
      } else {
        hasPermission =
          this.$hasPermission('asset:IMPORT') ||
          this.$hasPermission('space:IMPORT')
      }

      if (!hasPermission) {
        this.$message.error('Permission Denied')
      }
    },
    toggleHistoryPage() {
      this.historyPageVisibility = !this.historyPageVisibility
    },
    moveToFieldMapping() {
      this.fieldMapping = true
      this.uploadPage = false
      this.dataValidation = false
      this.finalUpload = false
    },
    moveToValidation() {
      this.uploadPage = false
      this.fieldMapping = false
      this.dataValidation = true
      this.finalUpload = false
      this.interval = setInterval(this.checkStatus, 40000)
    },
    finishValidation(validationBoolean) {
      if (validationBoolean) {
        this.dataValidation = false
        this.loadFinal(this.importProcessContext)
      }
    },
    loadFinal(data) {
      this.importProcessContext = data.importProcessContext
        ? data.importProcessContext
        : data
      this.dataValidation = false
      this.finalUpload = true
    },
    moveToBegin() {
      this.$router.go(this.$route.path)
    },
    finalDone() {
      this.finalUpload = false
      this.uploadPage = true
      this.importProcessContext = this.initialImportContext
    },
    continueImport(importContext) {
      this.historyPageVisibility = !this.historyPageVisibility
      this.importProcessContext = importContext
      if (importContext.status === 1) {
        this.importProcessContext.firstRow = JSON.parse(
          this.importProcessContext.firstRowString
        )
        this.moveToFieldMapping()
      } else if (importContext.status === 5) {
        if (this.importProcessContext.templateId) {
          this.$http
            .post('/v2/importTemplate/fetchTemplateForId', {
              templateId: this.importProcessContext.templateId,
            })
            .then(({ data: { responseCode, result, message } }) => {
              if (responseCode === 0) {
                let { importTemplateContext } = result
                this.setTimeFormatFromTemplate(importTemplateContext)
                this.dataValidation = true
                this.uploadPage = false
                this.fieldMapping = false
                this.finalUpload = false
              } else {
                throw new Error(message)
              }
            })
            .catch(({ message }) => {
              this.$message.error(message)
            })
        }
      } else if (importContext.status === 6) {
        this.dataValidation = true
        this.uploadPage = false
        this.fieldMapping = false
        this.finalUpload = false
      }
    },
    setTimeFormatFromTemplate(template) {
      let fieldMapping = JSON.parse(template.fieldMappingString)
      for (let keys in Object.keys(fieldMapping)) {
        let field = Object.keys(fieldMapping)[keys]
        let columnName = fieldMapping[Object.keys(fieldMapping)[keys]]
        if (field === 'sys__ttime') {
          this.timeFormat = this.$getProperty(
            template,
            `templateMetaJSON.dateFormats.${columnName}`,
            'Timestamp'
          )
        }
      }
    },
    async goToAppDownload() {
      let { data, error } = await API.get('/v2/application/list')
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let appList = data?.application || []
        let appObj = appList.find(value => value.linkName === 'dataloader')
        this.$router.push(`/app/setup/general/portal/summary/${appObj.id}`)
      }
    },
  },
}
</script>
<style scoped>
.info-banner {
  background-color: #ff7600;
  height: 40px;
  text-align: center;
  vertical-align: middle;
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  color: #fff;
  padding: 12px;
  line-height: 16px;
}
</style>
