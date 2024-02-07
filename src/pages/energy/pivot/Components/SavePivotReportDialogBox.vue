<template>
  <div>
    <base-dialog-box
      :visibility.sync="visibility"
      :onConfirm="saveReport"
      :onCancel="cancel"
      cancelText="Cancel"
      confirmText="Done"
      :title="$t('pivot.save')"
      width="560px"
    >
      <div class="dialog-content-body" slot="body">
        <div class="save-name-folder-name-section">
          <el-row class="input-section">
            <el-col :span="8">
              <div class="module-text">
                {{ $t('pivot.name') }}
              </div>
            </el-col>
            <el-col :span="16">
              <el-input
                v-model="newReport.name"
                class="fc-input-full-border-select2 name-field"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="input-section">
            <el-col :span="8">
              <div class="module-text">
                {{ $t('pivot.description') }}
              </div>
            </el-col>
            <el-col :span="16">
              <el-input
                v-model="newReport.description"
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
            <el-col :span="16">
              <el-select
                v-model="newReport.reportFolderId"
                :filterable="true"
                :allow-create="true"
                :placeholder="$t('common._common.enter_new_folder_name')"
                class="fc-input-full-border2 width100"
              >
                <el-option
                  v-for="(folder, idx) in reportFolders"
                  :key="idx"
                  :label="folder.name"
                  :value="folder.id"
                ></el-option> </el-select
            ></el-col>
          </el-row>
        </div>
      </div>
    </base-dialog-box>
  </div>
</template>

<script>
import BaseDialogBox from './BaseDialogBox.vue'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import { API } from '@facilio/api'
import { mapGetters } from 'vuex'

export default {
  props: ['visibility', 'report', 'config'],
  name: 'SavePivotReportDialogBox',
  components: {
    BaseDialogBox,
  },
  mixins: [NewReportSummaryHelper],
  mounted() {
    this.loadReportFolders()
    this.init()
  },
  computed: {
    ...mapGetters(['getCurrentUser']),
  },

  data() {
    return {
      moduleOptions: {},
      moduleLoading: true,
      moduleNameLocal: null,
      reportFolders: [],
      newReport: {
        name: '',
        description: '',
        reportFolderId: '',
        analyticsType: 1,
        addToDashboard: false,
        isTemplate: false,
      },
    }
  },

  methods: {
    async loadReportFolders() {
      // no module name case is energy data reports, DON't send moduleName prop as energydata.;leave null for energreports
      let url = '/v3/report/folders?moduleName=energydata'
      let self = this
      //pivot
      url += '&isPivot=true'
      API.get(url).then(response => {
        if (!response.error) {
          self.reportFolders = response.data.reportFolders
        }
      })
    },
    init() {
      if (this.report) {
        this.$helpers.copy(this.newReport, this.report)
      }
    },
    saveFolder(folderName) {
      let self = this
      this.applySharing()
      let apiUrl = null
      apiUrl = '/v3/report/folder/create?moduleName=energydata'
      return new Promise((resolve, reject) => {
        let a = {
          reportFolder: {
            name: folderName,
            reportSharing: this.reportSharing,
          },
        }
        //adding folder type =>3 for pivot reports.TODO  send typeEnum for Reading and module reports too
        if (self.report && self.report.reportType == 5) {
          a.reportFolder.folderType = 3
        }
        API.post(apiUrl, a)
          .then(response => {
            if (response.error) {
              this.$message.error(response.error.message)
              reject(response.error)
            } else if (
              response.data.reportFolder &&
              response.data.reportFolder.id
            ) {
              resolve(response)
            } else {
              reject(response)
            }
          })
          .catch(function(error) {
            if (error) {
              reject(error)
            }
          })
      })
    },
    applySharing: function() {
      let self = this
      this.reportSharing = []
      if (self.shareTo === 1) {
        this.reportSharing.push({
          type: 1,
          userId: self.getCurrentUser().ouid,
        })
      } else if (self.shareTo === 3) {
        if (self.sharedUsers.length > 0) {
          this.reportSharing.push({
            type: 1,
            userId: self.getCurrentUser().ouid,
          })
          for (let i = 0; i < self.sharedUsers.length; i++) {
            if (self.sharedUsers[i] !== self.getCurrentUser().ouid) {
              this.reportSharing.push({
                type: 1,
                userId: self.sharedUsers[i],
              })
            }
          }
        }
        if (self.sharedRoles.length > 0) {
          for (let i = 0; i < self.sharedRoles.length; i++) {
            this.reportSharing.push({
              type: 2,
              roleId: self.sharedRoles[i],
            })
          }
        }
        if (self.sharedGroups.length > 0) {
          for (let i = 0; i < self.sharedGroups.length; i++) {
            this.reportSharing.push({
              type: 3,
              groupId: self.sharedGroups[i],
            })
          }
        }
      }
    },
    addReport(override) {
      let self = this
      let newReportParams = {}

      let apiUrl = null
      //pivot report
      apiUrl = 'v2/report/savePivotReport'
      let reportId =
        this.$route.query.reportId && !this.$route.query.duplicate
          ? this.$route.query.reportId
          : -1
      //apiUrl = reportId === -1 ? 'v3/report/pivot/create' : 'v3/report/pivot/update'
      newReportParams = {
        ...this.config,
        reportContext: this.newReport,
      }

      if (reportId) newReportParams['reportId'] = reportId

      self.saving = true
      API.post(apiUrl, newReportParams)
        .then(function(response) {
          let reportContext = response.data.report
          if (override) {
            let reportURL = self.getRouterUrl(
              reportContext,
              self.viewModes().EDIT,
              false
            )
            let reportURLSplit = reportURL.split('?')
            reportURL = reportURLSplit[0]
            self.close(response.data)
            self.$router.replace({
              path: reportURL,
              query: { reportId: reportContext.id },
            })
          } else {
            self.saving = false
            self.$router.replace({ query: { reportId: reportContext.id } })
            let notifyInstance = self.$notify({
              title: self.$t('common._common.report_saved_successfully'),
              message: self.$t('common._common.click_here_to_open_report'),
              type: 'success',
              duration: 0,
              customClass: 'report-save-success',
              onClick: function() {
                if (notifyInstance) {
                  notifyInstance.close()
                }
                let reportURL = null
                reportURL = self.getRouterUrl()
                self.$router.push({ path: reportURL })
              },
            })
            self.close(response.data)
          }
        })
        .catch(function(error) {
          self.saving = false
          alert(error)
        })
    },
    viewModes() {
      let viewModes = {}
      viewModes['VIEW'] = 1
      viewModes['EDIT'] = 2
      return viewModes
    },
    getRouterUrl() {
      let reportURL = '/app/em/pivot/view/'
      return reportURL
    },
    saveReport() {
      let self = this
      if (!this.newReport.name || !this.newReport.name.trim().length) {
        alert(this.$t('common.header.please_enter_the_report_name'))
      } else if (!this.newReport.reportFolderId) {
        alert(this.$t('common.header.please_choose_folder_enter_new_folder'))
      } else {
        let reportFolderId = -1
        try {
          reportFolderId = parseInt(this.newReport.reportFolderId)
        } catch (err) {
          console.error(err)
        }

        if (isNaN(reportFolderId) || reportFolderId < 0) {
          self
            .saveFolder(this.newReport.reportFolderId)
            .then(function(response) {
              reportFolderId = response.data.reportFolder.id

              self.newReport.reportFolderId = reportFolderId

              self.addReport()
            })
        } else {
          self.addReport()
        }
      }
    },
    cancel() {
      this.$emit('update:visibility', false)
    },
    close(reportObj) {
      this.newReport = {
        name: '',
        description: '',
        reportFolderId: '',
        addToDashboard: false,
      }

      this.$emit('reportSaved', reportObj.report)
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style scoped lang="scss">
.dialog-content-body {
  padding: 40px 0px;

  .el-input {
    position: relative;
    font-size: 14px;
    display: inline-block;
    width: 100% !important;
  }
}
.module-text {
  font-size: 16px;
  color: #324056;
}
.name-field {
  width: 260px;
}
.input-section {
  padding: 8px;
}
</style>
