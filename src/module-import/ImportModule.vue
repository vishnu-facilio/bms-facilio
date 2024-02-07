<template>
  <div class="asset-import-page">
    <div class="asset-import-main-con" v-if="moduleExists">
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
          <div class="heading-black22 line line-height0">{{ 'Import' }}</div>
          <div
            class="fc-dark-blue4-12 pointer line-height0"
            @click="historyPageVisibility = true"
          >
            Show Import History
          </div>
        </div>
        <div class="asset-import-main-bg">
          <import-setting
            importMode="1"
            v-if="uploadPage"
            :importProcessContext.sync="importProcessContext"
            :module="moduleName"
            :moduleMeta="moduleMeta"
            @proceed="moveToFieldMapping"
          ></import-setting>
          <field-mapping
            v-if="fieldMapping"
            @mappingResponse="moveToValidation"
            @begin="moveToBegining"
            :importProcessContext.sync="importProcessContext"
          ></field-mapping>
          <data-validation
            v-if="dataValidation"
            :dataValidation.sync="dataValidation"
            :parseError="parseError"
            :importProcessContext.sync="importProcessContext"
            @finishValidation="finishValidation"
            @begin="moveToBegining"
          ></data-validation>
          <final-upload
            v-if="finalUpload"
            @finalDone="finalDone"
            :importProcessContext.sync="importProcessContext"
          ></final-upload>
        </div>
      </div>
    </div>
    <!-- Import dialog history -->
    <import-history
      v-if="historyPageVisibility"
      :visibility.sync="historyPageVisibility"
      @continueImport="data => continueImport(data)"
      @showUpload="toggleHistoryPage"
      :historyMeta="{ importMode: 1, moduleName: this.moduleName }"
    ></import-history>
  </div>
</template>
<script>
import ImportHistory from '@/ImportHistory'
import ImportSetting from 'pages/assets/import/v1/ImportSetting.vue'
import FieldMapping from 'pages/assets/import/v1/ColumnMapping'
import DataValidation from 'pages/assets/import/v1/DataValidation'
import FinalUpload from 'pages/assets/import/v1/FinalUpload'
import importHelper from 'pages/assets/import/v1/ImportHelper'
export default {
  props: ['moduleName'],
  mixins: [importHelper],
  data() {
    return {
      importProcessContext: null,
      initialImportContext: null,
      historyPageVisibility: false,
      parseError: {
        visibility: false,
        errorMessage: '',
      },
      uploadPage: true,
      fieldMapping: false,
      dataValidation: false,
      finalUpload: false,
      moduleExists: false,
      interval: null,
      moduleMeta: null,
    }
  },
  components: {
    ImportHistory,
    ImportSetting,
    FieldMapping,
    DataValidation,
    FinalUpload,
  },
  created() {
    this.loadImportData()
  },
  destroyed() {
    clearInterval(this.interval)
  },
  title() {
    return 'Import'
  },
  methods: {
    loadImportData() {
      this.$http
        .post('/import/checkModule', { moduleName: this.moduleName })
        .then(response => {
          this.moduleExists = response.data.moduleExists.moduleExists
          this.moduleMeta = response.data.moduleExists.moduleMeta
          if (!this.moduleExists) {
            this.$message({
              showClose: true,
              message: 'Entered URL is invalid. Please try again.',
              type: 'error',
            })
          } else {
            this.importProcessContext = response.data.importProcessContext
            this.initialImportContext = response.data.importProcessContext
          }
        })
    },
    finalDone() {
      this.finalUpload = false
      this.uploadPage = true
      this.importProcessContext = this.initialImportContext
    },
    finishValidation(validationBoolean) {
      if (validationBoolean) {
        this.dataValidation = false
        this.loadFinal(this.importProcessContext)
      }
    },
    moveToBegining() {
      this.$router.go(this.$route.path)
    },
    loadFinal(data) {
      this.importProcessContext = data.importProcessContext
        ? data.importProcessContext
        : data
      this.dataValidation = false
      this.finalUpload = true
    },
    moveToFieldMapping() {
      this.fieldMapping = true
      this.uploadPage = false
      this.dataValidation = false
      this.finalUpload = false
    },
    continueImport(importContext) {
      this.historyPageVisibility = !this.historyPageVisibility
      this.importProcessContext = importContext
      if (importContext.status === 1) {
        this.moveToFieldMapping()
      } else if (importContext.status === 6) {
        this.dataValidation = true
        this.uploadPage = false
        this.fieldMapping = false
        this.finalUpload = false
      } else if (importContext.status === 5) {
        this.dataValidation = true
        this.uploadPage = false
        this.fieldMapping = false
        this.finalUpload = false
      }
    },
    toggleHistoryPage() {
      this.historyPageVisibility = !this.historyPageVisibility
    },
    moveToValidation(data) {
      this.fieldMapping = false
      this.dataValidation = true
      this.interval = setInterval(this.checkStatus, 40000)
      this.importProcessContext = data.importProcessContext
    },
    stringWithCaps(string) {
      let firstChar = string.charAt(0)
      let remaining = string.slice(1)
      return firstChar.toUpperCase() + remaining.toLowerCase()
    },
  },
}
</script>
