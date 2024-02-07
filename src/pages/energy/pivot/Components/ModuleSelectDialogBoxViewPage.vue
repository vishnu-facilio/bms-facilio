<template>
  <div>
    <base-dialog-box
      :visibility.sync="visibility"
      :onConfirm="saveModuleName"
      :onCancel="closeDialog"
      :disableCancel="false"
      cancelText="Cancel"
      confirmText="Next"
      :title="$t('pivot.moduleAnalyze')"
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
              class="description-area fc-input-full-border-select2 name-field"
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
              <span> Click enter to add new folder</span>
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
            <div v-if="!moduleNameLocal && validate" class="error-message">
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
import { pageTypes, isWebTabsEnabled, findRouteForTab } from '@facilio/router'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['visibility', 'moduleName'],
  components: {
    BaseDialogBox,
  },
  computed:{
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
  },
  mounted() {
    this.loadModuleOptions()
  },
  mixins: [saveReportMixin],
  data() {
    return {
      moduleOptions: {},
      folderId: null,
      reportName: null,
      description: null,
      moduleLoading: true,
      moduleNameLocal: null,
      validate: false,
    }
  },

  methods: {
    async loadModuleOptions() {

      this.moduleOptions.customModules = []
      this.moduleOptions.systemModules = []
      let params ={}
      if(isWebTabsEnabled()){
        if(!isEmpty(this.currentTab)){
          params['webTabId']= this.currentTab.id
        }
      }
      let {data, error} = await API.get('/v3/report/modules/list', params)
      if(!error)
      {
        this.moduleOptions.systemModules = data.systemModules
        this.moduleOptions.customModules = data.customModules
        this.moduleLoading = false
      }
    },
    closeDialog() {
      this.$emit('dialogClosed')
      this.validate = false
    },
    async saveModuleName() {
      this.getReportObject()
      if (!this.moduleNameLocal || !this.folderId || !this.reportName) {
        this.validate = true
        return
      }
      let url = '/app/em/pivotbuilder/new'
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_FORM)
        url = this.$router.resolve({ name }).href
      }
      this.$router.push({
        path: url,
        query: {
          mName: this.moduleNameLocal,
          folderId: this.folderId,
          reportName: this.reportName,
          description: this.description ? this.description : '',
        },
      })
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
  font-size: 14px;
  color: #324056;
  margin-bottom: 8px;
}

.input-section {
  padding: 8px 0;
  display: flex;
  align-items: left;
  flex-direction: column;
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
  width: 408px;
}

.module-select-heading {
  font-size: 14px;
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
}
</style>
