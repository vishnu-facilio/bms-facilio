<template>
  <div>
    <div class="fc-black-12 text-left bold text-uppercase letter-spacing1">
      IMPORT SETTINGS
    </div>
    <div class="fc-heading-border-width43 mT10 height2px"></div>
    <div class="import-file-body border-bottom6">
      <div v-if="attachments && attachments.length > 0" class="pT40 pB40">
        <el-row
          v-for="attachment in attachments"
          :key="attachment.attachmentId"
        >
          <el-col :span="20">
            <el-col :span="1" class>
              <InlineSvg
                src="svgs/fileupload-xls"
                iconClass="icon icon-xlg vertical-middle"
              ></InlineSvg>
            </el-col>
            <el-col :span="19">
              <div class="pL20">
                <div
                  class="this-center show text-left fc-black-16 textoverflow-ellipsis max-width300px"
                >
                  {{ attachment.fileName }}
                </div>
                <div class="fc-grey3-text14">
                  {{ attachment.fileSize | prettyBytes }}
                </div>
              </div>
            </el-col>
          </el-col>
          <el-col :span="4" class="text-right">
            <div class="fc-warning2-12 pointer" @click="deleteAttachment()">
              Remove
            </div>
          </el-col>
        </el-row>
      </div>
      <form v-else enctype="multipart/form-data" novalidate class="pT20 pB40">
        <div class="dropbox">
          <input
            type="file"
            accept=".xls, .xlsx"
            class="input-file z-10"
            @change="filesChange($event.target.files[0])"
          />
          <div class="upload-parent">
            <div class="upload-folder-box">
              <InlineSvg
                src="svgs/folder-open"
                iconClass="icon z-index1 icon-xxxl vertical-middle"
              ></InlineSvg>
            </div>
          </div>
          <p>
            {{ $t('common.attachment_form.drag_and_drop_files') }}
            <br />
            {{ $t('common.attachment_form.click_to_browse') }}
          </p>
        </div>
      </form>
    </div>

    <div v-if="importProcessContext" class="asset-import-rCol-fields pT20 pB40">
      <div v-if="moduleStaticFields && moduleStaticFields.length > 0">
        <el-row
          v-if="!hideSites"
          :gutter="20"
          v-for="(moduleStaticField,
          moduleStaticFieldIdx) in moduleStaticFields"
          :key="moduleStaticFieldIdx"
        >
          <el-col :span="4">
            <div class="label-txt-black pT10">
              {{ moduleStaticField.displayName }}
            </div>
          </el-col>
          <el-col :span="8">
            <FLookupFieldWrapper
              v-model="moduleStaticField.value"
              :field="{
                lookupModule: {
                  name: lookupModuleName,
                },
                multiple: false,
              }"
              :hideLookupIcon="false"
            >
            </FLookupFieldWrapper>
          </el-col>
        </el-row>
      </div>
      <div class="pT20 label-txt-black">
        What do you want to do with the records in the file?
      </div>
      <div class="pT20">
        <el-radio-group v-model="importSetting.radio" @input="radioToggler">
          <el-radio
            label="New"
            v-if="importProcessContext.options['INSERT']"
            class="fc-radio-btn"
            >{{ 'As New ' + printableModuleName() }}</el-radio
          >
          <el-radio
            label="Update"
            v-if="importProcessContext.options['UPDATE']"
            class="fc-radio-btn"
            >{{ 'Update Existing ' + printableModuleName() }}</el-radio
          >
          <el-radio
            label="Both"
            v-if="importProcessContext.options['BOTH']"
            class="fc-radio-btn"
            >Both</el-radio
          >
        </el-radio-group>
      </div>
      <div
        class="pT20"
        v-if="radioToggle && importProcessContext.options['INSERT_SKIP']"
      >
        <el-checkbox v-model="importSetting.checkSkip" label="skip">{{
          'Skip existing ' + printableModuleName()
        }}</el-checkbox>
      </div>
      <div
        class="pT20"
        v-if="
          updateToggle &&
            (importProcessContext.options['BOTH_NOT_NULL'] ||
              importProcessContext.options['UPDATE_NOT_NULL'])
        "
      >
        <el-checkbox v-model="importSetting.checkNull" label="skip"
          >Do not update empty fields</el-checkbox
        >
      </div>
      <div
        v-if="importSetting.checkSkip || updateToggle"
        class="label-txt-black mT20"
      >
        {{ text }}
        <el-select
          class="fc-input-full-border-select2"
          collapse-tags
          multiple
          v-model="updateOrInsertByFields"
          placeholder="Select"
        >
          <el-option
            v-for="(value, index) in columnHeading"
            :key="index"
            :label="value"
            :value="value"
          ></el-option>
        </el-select>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button
        :loading="loading"
        @click="nextButton"
        class="fc-full-btn-fill-green f13"
      >
        Proceed to next
        <img
          src="~assets/arrow-pointing-white-right.svg"
          width="17px"
          class="fR"
        />
      </el-button>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import ImportHelper from './ImportHelper'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { prettyBytes } from '@facilio/utils/filters'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'

export default {
  props: ['module', 'importMode', 'importProcessContext', 'moduleMeta'],
  mixins: [ImportHelper],
  components: { FLookupFieldWrapper },
  data() {
    return {
      loading: false,
      attachments: [],
      uploadFile: null,
      siteId: null,
      updateOrInsertByFields: [],
      text: 'Insert By',
      importSetting: {
        checkNull: false,
        checkSkip: false,
        radio: 'New',
      },
      setting: null,
      radioToggle: true,
      updateToggle: false,
      moduleStaticFields: null,
      hideSites: false,
    }
  },
  mounted() {
    if (!isEmpty(this.$route.query)) {
      this.hideSites = this.$route.query.hideSite
    }
  },
  created() {
    this.$store.dispatch('loadSites').then(() => {
      this.loadModuleStaticFields()
    })
  },
  computed: {
    lookupModuleName() {
      if (['purchasedItem', 'purchasedTool'].includes(this.module)) {
        return 'storeRoom'
      } else if (['asset', 'workorder'].includes(this.module)) {
        return 'site'
      }
      return null
    },
    ...mapState({
      siteList: state => state.sites,
    }),
    columnHeading() {
      if (
        this.importProcessContext &&
        this.importProcessContext.columnHeadings
      ) {
        return this.importProcessContext.columnHeadings.filter(i => {
          return i
        })
      } else {
        return []
      }
    },
    isAssetImport() {
      return (
        this.module === 'asset' ||
        this.$getProperty(this.moduleMeta, 'extendModule.name', null) ===
          'asset'
      )
    },
  },
  methods: {
    loadModuleStaticFields() {
      this.moduleStaticFields = []
      let moduleName = this.module
      if (['purchasedItem', 'purchasedTool'].includes(moduleName)) {
        this.$http.get('/v2/storeRoom/view/all').then(response => {
          let temp = {}
          temp['name'] = 'storeRoom'
          temp['displayName'] = 'Store Room'
          temp['values'] = []
          temp['value'] = null
          for (let room of response.data.result.storeRooms) {
            let storeRoom = {}
            storeRoom['displayName'] = room.name
            storeRoom['id'] = room.id
            temp.values.push(storeRoom)
          }
          this.moduleStaticFields.push(temp)
        })
      } else if (
        ['asset', 'workorder'].includes(moduleName) ||
        this.$getProperty(this.moduleMeta, 'extendModule.name', null) ===
          'asset'
      ) {
        let temp = {
          name: 'site',
          displayName: 'Site',
          values: [],
          value: null,
        }
        for (let site of this.siteList) {
          let tempSite = {
            displayName: site.name,
            id: site.id,
          }
          temp.values.push(tempSite)
        }
        this.moduleStaticFields.push(temp)
      }
    },
    filesChange(selectedFile) {
      if ((selectedFile || {}).size < 2097152) {
        this.uploadFile = selectedFile
        let fileEntry = {
          attachmentId: -1,
          fileName: selectedFile.name,
          fileSize: selectedFile.size,
          prettyFileSize: prettyBytes(selectedFile.size),
          contentType: selectedFile.type,
          status: null,
          error: null,
        }
        this.attachments.push(fileEntry)
        this.addAttachment()
      } else {
        this.$message.error('File size limit exceeded (Max File Size is 2MB)')
      }
    },
    moduleStaticFieldsValidation() {
      let { moduleStaticFields } = this
      if (
        !this.isAssetImport &&
        !isEmpty(moduleStaticFields) &&
        isArray(moduleStaticFields) &&
        moduleStaticFields.length > 0
      ) {
        for (let field of moduleStaticFields) {
          if (field.name === 'site' && this.hideSites) {
            // omitting siteValidation
          } else if (isEmpty(field.value) || field.value < 0) {
            this.$message.error(
              `Please Select a ${field.displayName} to Proceed`
            )
            return false
          }
        }
      }
      return true
    },
    mapModuleStaticFields(importProcessContext) {
      if (this.moduleStaticFields) {
        let importMeta = null
        if (isEmpty(importProcessContext.importJobMeta)) {
          let importMeta = {}
          importMeta['moduleStaticFields'] = {}
        } else {
          try {
            importMeta = JSON.parse(importProcessContext.importJobMeta)
          } catch (err) {
            console.error('Cannot parse importJobMeta.')
          }
          if (importMeta) {
            importMeta['moduleStaticFields'] = {}
          }
        }
        for (let staticField of this.moduleStaticFields) {
          importMeta.moduleStaticFields[staticField.name] = staticField.value
          if (staticField.name === 'site') {
            importProcessContext.siteId = staticField.value || -1
          }
        }
        importProcessContext.importJobMeta = JSON.stringify(importMeta)
      }
      return importProcessContext
    },
    updateImportProcessContext() {
      if (this.attachments.length === 0) {
        this.$message.error('Please Upload a File to Proceed')
        return
      }
      if (!this.moduleStaticFieldsValidation()) {
        return
      }
      let { importProcessContext } = this
      importProcessContext = this.mapModuleStaticFields(importProcessContext)
      this.loading = true
      this.$http
        .post('/v2/import/updateImportProcessContext', {
          importProcessContext: importProcessContext,
        })
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$emit(
              'update:importProcessContext',
              response.data.result.importProcessContext
            )
            this.$emit('proceed')
          } else {
            this.$message.error(response.data.message)
          }
          this.loading = false
        })
        .catch(error => {
          this.$message.error(error)
        })
    },
    addAttachment() {
      const formData = new FormData()
      formData.append('moduleName', this.module)
      formData.append('fileUpload', this.uploadFile)
      formData.append('importMode', this.importMode)
      this.loading = true
      this.$http
        .post('/v2/import/upload', formData)
        .then(response => {
          if (response.data) {
            this.loading = false
            if (response.data.responseCode !== 0) {
              this.$message.error(response.data.message)
              this.deleteAttachment()
            } else {
              this.$emit(
                'update:importProcessContext',
                response.data.result.importProcessContext
              )
              this.$message.success('File Uploaded Successfully')
            }
          }
        })
        .catch(error => {
          this.loading = false
          this.$message.error(error)
          this.deleteAttachment()
        })
    },
    deleteAttachment() {
      this.attachments.splice(0, 1)
    },
    radioToggler(e) {
      if (e === 'New') {
        this.importSetting.checkNull = false
        this.updateToggle = false
        this.radioToggle = true
        this.text = 'Insert by'
      } else if (e === 'Update' || e === 'Both') {
        this.importSetting.checkSkip = false
        this.importSetting.checkNull = false
        this.radioToggle = false
        this.updateToggle = true
        this.text = 'Update by'
      }
    },
    nextButton() {
      if (this.importSetting.radio) {
        if (this.importSetting.radio === 'New') {
          if (this.importSetting.checkSkip) {
            this.setting = 2
          } else {
            this.setting = 1
          }
        } else if (this.importSetting.radio === 'Update') {
          if (this.importSetting.checkNull) {
            this.setting = 4
          } else {
            this.setting = 3
          }
        } else if (this.importSetting.radio === 'Both') {
          if (this.importSetting.checkNull) {
            this.setting = 6
          } else {
            this.setting = 5
          }
        }
        this.importProcessContext.importSetting = this.setting
        let meta
        if (this.importProcessContext.importJobMeta) {
          meta = JSON.parse(this.importProcessContext.importJobMeta)
        } else {
          meta = {}
        }
        if (this.setting === 2) {
          if (this.updateOrInsertByFields.length === 0) {
            meta['insertBy'] = null
          } else {
            meta['insertBy'] = this.updateOrInsertByFields
          }
        } else if (
          this.setting === 3 ||
          this.setting === 4 ||
          this.setting === 5 ||
          this.setting === 6
        ) {
          if (this.updateOrInsertByFields.length === 0) {
            meta['updateBy'] = null
          } else {
            meta['updateBy'] = this.updateOrInsertByFields
          }
        }
        this.importProcessContext.importJobMeta = JSON.stringify(meta)
        this.updateImportProcessContext()
      } else {
        this.$message({
          message: 'Choose a setting',
          type: 'error',
        })
      }
    },
  },
}
</script>
