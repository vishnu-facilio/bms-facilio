<template>
  <div>
    <base-dialog-box
      v-if="showDialog && visibility"
      :visibility.sync="visibility"
      :onConfirm="saveModuleName"
      :onCancel="closeDialog"
      :disableCancel="false"
      cancelText="Cancel"
      :confirmText="editMode ? 'Save' : 'Next'"
      :title="$t('pivot.editPopupHeader')"
      width="490px"
    >
      <div class="dialog-content-body" slot="body">
        <el-row class="input-section">
          <el-col :span="8">
            <div class="module-text">
              {{ $t('pivot.name') }}
            </div>
          </el-col>
          <el-col>
            <el-input
              v-model="reportName"
              class="fc-input-full-border-select2 name-field"
            ></el-input>
            <div v-if="!reportName && validate" class="error-message">
              Please enter Name
            </div>
          </el-col>
        </el-row>
        <el-row class="input-section">
          <el-col :span="8">
            <div class="module-text description">
              {{ $t('pivot.description') }}
            </div>
          </el-col>
          <el-col>
            <el-input
              type="textarea"
              :autosize="{ minRows: 2 }"
              v-model="description"
              class="fc-input-full-border-select2 name-field"
            ></el-input>
          </el-col>
        </el-row>
        <el-row class="input-section">
          <el-col :span="8">
            <div class="module-text">
              {{ $t('pivot.folder') }}
            </div>
          </el-col>
          <el-col>
            <el-select
              v-model="folderId"
              :filterable="true"
              :allow-create="true"
              :placeholder="$t('pivot.click_enter_new_folder_name')"
              class="fc-input-full-border2 width100"
            >
              <el-option
                v-for="(folder, idx) in reportFolders"
                :key="idx"
                :label="folder.name"
                :value="folder.id"
              ></el-option>
            </el-select>
            <div v-if="!folderId && validate" class="error-message">
              Click enter to add new folder
            </div>
          </el-col>
        </el-row>
        <div class="module-select-heading">Choose data you want to analyse</div>
        <el-row class="input-section">
          <el-col :span="8">
            <div class="module-text">
              {{ $t('pivot.module') }}
            </div>
          </el-col>
          <el-col>
            <el-select
              v-model="moduleNameLocal"
              filterable
              :disabled="editMode"
              placeholder="Module Name"
              class="fc-input-full-border-select2 width100"
              popper-class="fc-group-select"
              :loading="moduleLoading"
              :loading-text="$t('pivot.loading')"
            >
              <el-option-group :label="$t('pivot.systemModules')">
                <el-option
                  v-for="(module, index) in moduleOptions.systemModules"
                  :key="index"
                  :label="module.displayName"
                  :value="module.name"
                ></el-option>
              </el-option-group>
              <el-option-group
                :label="$t('pivot.customModules')"
                v-if="
                  moduleOptions.customModules &&
                    moduleOptions.customModules.length > 0
                "
              >
                <el-option
                  v-for="(module, index) in moduleOptions.customModules"
                  :key="index"
                  :label="module.displayName"
                  :value="module.name"
                ></el-option>
              </el-option-group>
            </el-select>
            <div
              v-if="!moduleNameLocal && validate && !editMode"
              class="error-message"
            >
              Please select Module
            </div>
          </el-col>
        </el-row>
      </div>
    </base-dialog-box>
  </div>
</template>

<script>
import BaseDialogBox from './BaseDialogBox.vue'
import saveReportMixin from './saveReportMixin.vue'
import { API } from '@facilio/api'

export default {
  props: ['visibility', 'moduleName', 'editMode', 'report'],
  components: {
    BaseDialogBox,
  },
  created() {
    if (this.report) this.reportObject = JSON.parse(JSON.stringify(this.report))
  },
  watch: {
    report: {
      handler(newState) {
        this.reportObject = JSON.parse(JSON.stringify(newState))
        this.reportName = newState.name
        this.description = newState?.description ? newState.description : ''
        this.folderId = newState?.reportFolderId ? newState.reportFolderId : -1
        this.moduleNameLocal = newState?.module?.name
          ? newState?.module?.name
         : this.moduleNameLocal
      },
      immediate: true,
      deep: true,
    },
    editMode: {
      handler(newState) {
        if (newState == true) this.showDialog = newState
      },
      immediate: true,
    },
  },
  mounted() {
    if (
      this.$route.query.mName &&
      this.$route.query.folderId &&
      this.$route.query.reportName
    ) {
      ;(this.showDialog = false),
        (this.moduleNameLocal = this.$route.query.mName)
      this.folderId = this.$route.query.folderId
      this.reportName = this.$route.query.reportName
      this.description = this.$route.query.description
        ? this.$route.query.description
        : ''
      this.saveModuleName()
    } else {
      this.loadModuleOptions()
    }
  },
  mixins: [saveReportMixin],
  data() {
    return {
      showDialog: true,
      folderId: null,
      reportName: '',
      description: '',
      reportObject: {},
      validate: false,
      moduleOptions: {},
      moduleLoading: true,
      moduleNameLocal: null,
    }
  },

  methods: {
    async loadModuleOptions() {
      let resp = await API.get('/v2/automation/module')
      let { data, error } = resp
      if (error) {
        this.$message.error('Error loading module list')
      } else {
        this.moduleOptions.systemModules = data.modules.filter(e => !e.custom)
        this.moduleOptions.customModules = data.modules.filter(e => e.custom)
        this.moduleLoading = false
      }
    },
    async saveModuleName() {
      if (
        ((!this.moduleNameLocal && !this.editMode) ||
          !this.folderId ||
          !this.reportName) &&
        !isNaN(this.folderId)
      ) {
        this.validate = true
        return
      }
      if (this.editMode == true) {
        this.reportObject.name = this.reportName
        this.reportObject.description = this.description
        this.reportObject.reportFolderId = this.folderId
        if (!this.newReport?.reportType) {
          this.newReport.reportType = 5
        }
        this.$emit('closeDialog')
        this.$emit('reportSaved', this.reportObject)
        return
      }
      this.newReport.name = this.reportName
      this.newReport.description = this.description
      this.newReport.reportFolderId = this.folderId
      if(!isNaN(this.folderId)){
        this.newReport.reportFolderId = parseInt(this.folderId)
      }
      this.newReport.reportType = 5
      this.getReportObject()
      this.$emit('update:moduleName', this.moduleNameLocal)
      this.$emit('closeDialog')
      this.$emit('moduleSelected')
    },
    closeDialog() {
      this.validate = false
      if (this.editMode) {
        this.$emit('closeDialog')
      } else {
        this.$router.go(-1)
      }
    },
  },
}
</script>

<style scoped lang="scss">
.dialog-content-body {
  display: flex;
  flex-direction: column;
  padding: 10px 0px;
  .error-message {
    color: #f56c6c;
    font-size: 12px;
    line-height: 1;
    padding-top: 4px;
  }
  .module-text:not(.description)::after {
    content: '*';
    color: #f56c6c;
    margin-right: 4px;
  }
}
.module-text {
  font-size: 13px;
  color: #324056;
  margin-bottom: 8px;
}

.input-section {
  padding: 8px 0;
  display: flex;
  align-items: left;
  flex-direction: column;
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
  width: 408px;
}

.module-select-heading {
  font-size: 13px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #ff3184;
  margin: 25px 0 10px 0;
}
.dialog-footer {
  padding-bottom: 10px !important;
  margin-top: 15px !important;
  padding-right: 22px;
}
</style>
