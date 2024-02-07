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
            Drag and drop file(s) here
            <br />or click to browse
          </p>
        </div>
      </form>
    </div>
    <div class="asset-import-rCol-fields pT20 pB40">
      <div>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="label-txt-black">
              Import and create from scratch for any space or asset
            </div>
            <div class="mT20">
              <el-radio-group @change="subModuleToggler" v-model="baseModule">
                <el-radio
                  v-if="$hasPermission('space:IMPORT')"
                  label="space"
                  class="fc-radio-btn"
                  >Space</el-radio
                >
                <el-radio
                  v-if="$hasPermission('asset:IMPORT')"
                  label="asset"
                  class="fc-radio-btn"
                  :disabled="showAsset"
                  >Asset</el-radio
                >
              </el-radio-group>
            </div>
            <el-row :gutter="20" class="mT20">
              <el-col :span="12" v-if="subModuleToggle">
                <el-select
                  filterable
                  clearable
                  v-model="choosenModule"
                  placeholder="Select"
                  class="fc-input-full-border2"
                >
                  <el-option
                    v-for="(item, index1) in fillSubModules()"
                    :key="index1"
                    :label="item.label"
                    :value="item.name"
                  ></el-option>
                </el-select>
              </el-col>
              <el-col
                :span="12"
                v-if="choosenModule && choosenModule.toLowerCase() === 'site'"
              >
                <el-select
                  filterable
                  clearable
                  v-model="subModuleFilter"
                  placeholder
                  class="fc-input-full-border2"
                >
                  <el-option
                    v-for="(subModule, subModuleIndex) in fillSubModuleFilter()"
                    :key="subModuleIndex"
                    :label="subModule.label"
                    :value="subModule.name"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </el-col>
          <el-col :span="1">
            <div class="import-sepration-line"></div>
          </el-col>
          <el-col :span="11">
            <div class="label-txt-black">Choose from Template</div>
            <div class="mT20">
              <el-select
                filterable
                clearable
                placeholder="Select"
                v-model="choosenTemplateId"
                class="fc-input-full-border2"
              >
                <el-option
                  v-for="(template, index) in userTemplateList"
                  :value="template.id"
                  :key="index"
                  :label="template.templateName"
                  class="visibility-visible-actions"
                >
                  <span class="fL">{{ template.templateName }}</span>
                  <span class="select-float-right-text13"
                    ><i
                      @click.stop="deleteTemplate(template)"
                      class="el-icon-delete visibility-hide-actions"
                    ></i
                  ></span>
                </el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
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
import ImportHelper from '../v1/ImportHelper'
import { prettyBytes } from '@facilio/utils/filters'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['module', 'importMode', 'importProcessContext'],
  mixins: [ImportHelper],
  data() {
    return {
      loading: true,
      categoryId: -1,
      attachments: [],
      uploadFile: null,
      baseModule: 'space',
      subModuleToggle: false,
      templateList: null,
      choosenModule: null,
      space: [
        {
          label: 'Site',
          name: 'site',
        },
        {
          label: 'Building',
          name: 'building',
        },
        {
          label: 'Floor',
          name: 'floor',
        },
        {
          label: 'Space',
          name: 'space',
        },
      ],
      subModuleFilter: null,
      choosenTemplateId: null,
      choosenTemplate: null,
      userTemplateList: [],
      showAsset: true,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory').then(() => {
      this.subModuleToggler()
    })

    if (this.$hasPermission('space:IMPORT')) {
      this.baseModule = 'space'
    } else if (this.$hasPermission('asset:IMPORT')) {
      this.baseModule = 'asset'
    }
    this.loading = true
    this.$http('/v2/importTemplate/getnames').then(response => {
      this.templateList = response.data.result.importTemplateContext
      this.userTemplateList = (
        response.data.result.importTemplateContext || []
      ).filter(template => template.templateName)
      this.loading = false
    })
  },
  mounted() {
    if (this.$route.query?.assetreading === 'true') {
      this.showAsset = false
      this.baseModule = 'asset'
    }
  },
  computed: {
    computedModule() {
      if (this.baseModule === 'space') {
        return this.choosenModule
      } else {
        for (let j in this.assets) {
          if (this.assets[j].name === this.choosenModule) {
            this.categoryId = this.assets[j].id
          }
        }
        return 'asset'
      }
    },
    moduleMeta() {
      let temp = {}
      if (this.subModuleFilter) {
        temp['subModuleFilter'] = this.subModuleFilter
      }
      temp['baseModule'] = this.baseModule
      temp['module'] = this.$getProperty(
        this.importProcessContext,
        'module.name',
        ''
      )
      if (!isEmpty(this.choosenTemplateId)) {
        temp['moduleForClient'] = this.moduleInfo.moduleForClient
        if (temp['moduleName'] !== '') {
          temp['moduleName'] = this.moduleInfo.moduleName
        }
        temp['parentId'] = this.choosenTemplate.moduleJSON.parentId
      } else if (this.baseModule === 'asset') {
        temp['moduleForClient'] = this.choosenModule
        temp['parentId'] = this.categoryId
      } else {
        temp['moduleForClient'] = this.choosenModule
      }
      return temp
    },
  },
  methods: {
    fillSubModuleFilter() {
      if (this.choosenModule && this.choosenModule.toLowerCase() === 'site') {
        return [
          {
            label: 'Weather',
            name: 'weather',
          },
          {
            label: 'Daily Weather',
            name: 'weatherDaily',
          },
          {
            label: 'Psychrometric',
            name: 'psychrometric',
          },
          {
            label: 'Cooling degree days',
            name: 'cdd',
          },
          {
            label: 'Heating degree days',
            name: 'hdd',
          },
          {
            label: 'WDD',
            name: 'wdd',
          },
        ]
      }
      return []
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
      } else {
        this.$message.error('File size limit exceeded (Max File Size is 2MB)')
      }
    },
    addAttachment() {
      const formData = new FormData()
      if (this.computedModule === 'asset') {
        formData.append('assetCategory', this.categoryId)
      }
      formData.append('moduleName', this.computedModule)
      formData.append('fileUpload', this.uploadFile)
      formData.append('importMode', this.importMode)
      formData.append('moduleMeta', JSON.stringify(this.moduleMeta))
      if (this.choosenTemplateId) {
        formData.append('templateId', this.choosenTemplateId)
      }
      this.loading = true
      this.$http
        .post('/v2/import/upload', formData)
        .then(response => {
          if (response.data) {
            this.loading = false
            if (response.data.responseCode !== 0) {
              this.$message.error(response.data.message)
            } else {
              this.$emit(
                'update:importProcessContext',
                response.data.result.importProcessContext
              )
              this.$message.success('File Uploaded Successfully')
              this.$emit('proceed')
            }
          }
        })
        .catch(error => {
          this.loading = false
          this.$message.error(error)
        })
    },
    deleteAttachment() {
      this.attachments.splice(0, 1)
    },
    nextButton() {
      if (!this.validateForNext()) {
        return
      }
      this.addAttachment()
    },
    validateForNext() {
      if (this.attachments && this.attachments.length === 0) {
        this.$message.error({
          showClose: true,
          message: 'Please Upload a File to Proceed',
        })
        return false
      }
      if (isEmpty(this.choosenTemplateId)) {
        if (isEmpty(this.baseModule) || isEmpty(this.choosenModule)) {
          this.$message.error({
            showClose: true,
            message: 'Please choose a module to proceed.',
          })
          return false
        }
      } else {
        this.choosenTemplate = this.templateList.find(
          i => i.id === this.choosenTemplateId
        )
        this.moduleInfo = JSON.parse(this.choosenTemplate.module)
        this.choosenModule = this.moduleInfo.subModule
        this.baseModule = this.moduleInfo.baseModule
        if (this.moduleInfo.subModuleFilter) {
          this.subModuleFilter = this.moduleInfo.subModuleFilter
        }
      }
      return true
    },
    subModuleToggler() {
      this.subModuleToggle = true
      this.choosenModule = null
      if (this.baseModule === 'space') {
        this.subModuleFilter = null
      }
      if (this.baseModule === 'asset') {
        this.assets = []
        let assetCategoryList = this.$store.state.assetCategory
        for (let assetCategory of assetCategoryList) {
          let temp = {}
          temp['label'] = assetCategory.displayName
          temp['name'] = assetCategory.name
          temp['id'] = assetCategory.id
          this.assets.push(temp)
        }
      }
    },
    fillSubModules() {
      if (this.baseModule === 'space') {
        return this.space
      } else {
        return this.assets
      }
    },
    deleteTemplate(template) {
      template.save = 1
      this.$http
        .post('/v2/importTemplate/deletetemplate', {
          importTemplateContext: template,
        })
        .then(({ data: { responseCode, message } }) => {
          if (responseCode === 0) {
            let { userTemplateList } = this
            this.userTemplateList = userTemplateList.filter(
              tm => tm.id != template.id
            )
            this.choosenTemplateId = null
            this.$message.success('Template deleted successfully')
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
  },
}
</script>
