<template>
  <div>
  <div
  class="new-folder-pivot-btn"
  @click="newFolderPopup = true"
  style="display:none;"
>
  <i class="el-icon-plus pR5 fwBold" style="transform: scale(0.9);"></i
  >New Folder
</div>
  <div
    class="folder-list-view height100 overflow-y-scroll pB50"
    style="height: calc(100vh - 200px)"
  >
    <PivotFolderRename
      :visibility="showEditDialog"
      :folderObject="editFolder"
      @updateFolderList="updateFolders"
      @cancelRename="showEditDialog = false"
    ></PivotFolderRename>
    <div v-if="isLoading">
      <div
        v-for="index in 3"
        :key="index"
        class="fc__white__bg__asset d-flex width100 height50 mB10"
      >
        <div class="fL width85 self-center">
          <div class="fc-animated-background p10 width140px"></div>
        </div>
        <div class="fR self-center">
          <div class="fc-animated-background p10 width140px"></div>
        </div>
      </div>
    </div>
    <draggable v-else v-model="reportFolders">
      <div class="mB10" v-for="(folder, index) in reportFolders" :key="index">
        <el-collapse class="folder-collapse">
          <el-collapse-item
            :name="folder.name"
            class="manager-views-item visibility-visible-actions"
          >
            <template slot="title">
              <div class="d-flex width100">
                <inline-svg
                  src="svgs/drag-and-drop"
                  class="d-flex self-center mR30 cursor-drag"
                  iconClass="icon fill-lite-grey pointer"
                ></inline-svg>
                <div class="d-flex self-center">
                  <inline-svg
                    src="svgs/folder"
                    class="d-flex mR10 self-center fill-grey"
                    iconClass="icon pointer"
                  ></inline-svg>
                  <div class="text-uppercase">
                    {{ folder.name }}
                  </div>
                </div>
                <div class="d-flex visibility-hide-actions mL-auto">
                  <!-- <button @click="editFolderName(folder)">
                    <inline-svg
                      src="svgs/edit-pencil"
                      class="d-flex mR15 self-center"
                      iconClass="icon icon-xs fill-grey pointer"
                    ></inline-svg>
                  </button> -->
                  <el-button
                    size="mini"
                    class="fl-buttons"
                    @click="showEditDialogBox(folder)"
                    style="padding:7px 15px;border:none;scale:0.8;"
                    circle
                  >
                    <InlineSvg
                      src="svgs/pivot/edit"
                      iconClass="icon-sm flex icon scale6"
                    ></InlineSvg>
                  </el-button>

                  <!-- <button @click="deleteReportFolder(folder)">
                    <inline-svg
                      src="svgs/delete"
                      class="d-flex mR15 self-center fill-grey pointer"
                      iconClass="icon icon-sm default icon-remove"
                    ></inline-svg>
                  </button> -->
                  <el-button
                    size="mini"
                    class="fl-buttons"
                    @click="deleteReportFolder(folder)"
                    style="padding:7px 15px;border:none;scale:0.8;"
                    circle
                  >
                    <InlineSvg
                      src="svgs/pivot/delete-icon"
                      iconClass="icon-md flex icon"
                    ></InlineSvg>
                  </el-button>
                </div>
              </div>
            </template>
            <draggable>
              <div
                v-for="(report, index) in folder.reports"
                :key="index"
                class="views-item"
              >
                <inline-svg
                  src="svgs/drag-and-drop"
                  class="d-flex self-center mR30 cursor-drag"
                  iconClass="icon fill-lite-grey"
                ></inline-svg>
                <inline-svg
                  src="svgs/views-list"
                  class="d-flex self-center mR20 cursor-drag"
                  iconClass="icon icon-sm views-list"
                ></inline-svg>
                <div
                  class="f12 bold letter-spacing1 text-uppercase self-center"
                >
                  {{ report.name }}
                </div>
                <div class="d-flex self-center mL-auto">
                  <!-- <div class="mR15">
                    <span class="shared-label">
                      {{ $t('viewsmanager.sharing_permission.share_with') }}
                    </span>
                    -
                    <span class="shared-txt"> </span>
                  </div> -->
                  <el-button
                    v-if="hasCreateEditPermission"
                    size="mini"
                    class="fl-buttons-report"
                    @click="editReport(report)"
                    style="border:none;scale:0.8;"
                    circle
                  >
                    <InlineSvg
                      src="svgs/pivot/edit"
                      iconClass="icon-md flex icon"
                    ></InlineSvg>
                  </el-button>

                  <el-button
                    v-if="hasDeletePermission"
                    size="mini"
                    class="fl-buttons-report"
                    @click="deleteReport(report)"
                    style="border:none;scale:0.8;"
                    circle
                  >
                    <InlineSvg
                      src="svgs/pivot/delete-icon"
                      iconClass="icon-md flex icon"
                    ></InlineSvg>
                  </el-button>
                  <el-button
                    v-show="showShareButton()"
                    size="mini"
                    class="fl-buttons-report"
                    style="border:none;scale:0.8;"
                    circle
                    @click="
                      ;(showReportSharePopup = true), (reportForShare = report)
                    "
                  >
                    <InlineSvg
                      src="svgs/pivot/Share icon"
                      iconClass="icon-md flex icon"
                    >
                    </InlineSvg>
                  </el-button>
                </div>
              </div>
            </draggable>
          </el-collapse-item>
        </el-collapse>
      </div>
    </draggable>
    <PivotReportShare
      v-if="showShareButton()"
      :reportDetails="reportForShare"
      :showPopUp="showReportSharePopup"
      @cancel="showReportSharePopup = false"
    />
  </div>
</div>
</template>
<script>
import { API } from '@facilio/api'
import draggable from 'vuedraggable'
import { mapGetters } from 'vuex'
import PivotFolderRename from './PivotFolderRename.vue'
import PivotReportShare from './PivotReportShare.vue'
import ReportTabPermissions from 'pages/report/mixins/ReportTabPermissions'
import { isWebTabsEnabled,findRouteForTab, pageTypes } from '@facilio/router'
export default {
  components: {
    draggable,
    PivotFolderRename,
    PivotReportShare,
  },
  mixins:[ ReportTabPermissions ],
  data() {
    return {
      isLoading: false,
      reportFolders: [],
      url: '/v3/report/folders?moduleName=energyData&isPivot=true',
      editFolder: null,
      showEditDialog: false,
      reportForShare: null,
      showReportSharePopup: false,
    }
  },
  computed: {
    ...mapGetters(['getCurrentUser']),
  },
  created() {
    this.loadFoldersList()

  },
  methods: {
    showShareButton() {
      if (
        this.getCurrentUser().role.isPrevileged == true &&
        this.$helpers.isLicenseEnabled('REPORT_SHARE')
      ) {
        return true
      } else {
        return false
      }
    },
    showEditDialogBox(folder) {
      this.editFolder = folder
      this.showEditDialog = true
    },
    updateFolders({ name, id }) {
      this.showEditDialog = false
      this.reportFolders.forEach(folder => {
        if (folder.id == id) folder.name = name
      })
    },
    async loadFoldersList() {
      let { data, error } = await API.get(this.url)
      if (!error) {
        this.reportFolders = data.reportFolders
      } else {
        this.$message.error('Error on loading pivot folders')
      }
    },
    deleteReportFolder(folder) {
      let self = this
      let promptObj = {
        title: self.$t('common.wo_report.delete_folder'),
        message: self.$t('common.wo_report.delete_folder_msg'),
        rbDanger: true,
        rbLabel: self.$t('common._common.delete'),
      }
      self.$dialog.confirm(promptObj).then(function(value) {
        if (value) {
          API.post('/v3/report/folder/delete', { reportFolder: folder })
            .then(response => {
              if (response.error) {
                self.$message({
                  message: self.$t('common.wo_report.cannot_delete_msg'),
                  type: 'error',
                })
              } else {
                let index = self.reportFolders.indexOf(folder)
                self.reportFolders.splice(index, 1)
                self.$message({
                  message: self.$t('common.wo_report.folder_delete_success'),
                  type: 'success',
                })
              }
            })
            .catch(function(error) {
              self.$message({
                message: self.$t('common.wo_report.cannot_delete_msg'),
                type: 'error',
              })
            })
        }
      })
    },
    editFolderName(folder) {
      let newName = this.folderState[folder.id].newName
      if (newName !== '' && newName !== null) {
        let reportFolder = {}
        reportFolder['name'] = newName
        reportFolder['id'] = folder.id
        this.editFolderId = null
        API.put('/v3/report/folder/update', { reportFolder: reportFolder })
          .then(response => {
            if (response.error) {
              this.$message({
                message: this.$t('common.wo_report.cannot_rename_folder'),
                type: 'error',
              })
              folder.name = this.folderState[folder.id].name
            } else {
              folder.name = response.data.reportFolder.name
              this.folderState[folder.id].name = folder.name
              this.folderState[folder.id].newName = folder.name
            }
          })
          .catch(() => {
            this.$message({
              message: this.$t('common.wo_report.cannot_rename_folder'),
              type: 'error',
            })
            folder.name = this.folderState[folder.id].name
          })
      } else {
        this.$message({
          message: this.$t('common.wo_report.foldername_cannot_empty'),
          type: 'error',
        })
      }
      this.$forceUpdate()
    },
    editReport(report) {
      let path = `/app/em/pivotbuilder/new?reportId=${report.id}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_FORM)
        path = this.$router.resolve({
          name,
          query: { reportId: report.id },
        }).href
      }
      this.$router.push({ path: path })
    },
    async deleteReport(row) {
      let { id } = row
      let promptObj = {
        title: 'Delete Report',
        message: 'Are you sure you want to delete this Report?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      let deleteConfirmation = await this.$dialog.confirm(promptObj)
      if (deleteConfirmation) {
        let { data, error } = API.post('/v2/report/deleteReport', {
          reportId: id ? id : this.reportId,
        })

        if (error) {
          this.$message.error('Error deleting pivot report', error)
          // API error on delete
        } else {
          if (data && data.errorString) {
            // Cannot delete as it's present in dashboard,confirm again
            let confirmObj = {
              title: 'Delete Report',
              message:
                'This report is associated to a dashboard widget. Deleting this report will remove the widget from the dashboard. Are you sure you want to continue?',
              rbLabel: 'Yes, delete',
              lbLabel: 'No, cancel',
            }
            let widgetDeleteConfirm = await this.$dialog.confirm(confirmObj)

            if (widgetDeleteConfirm) {
              let resp = await API.post('/v2/report/deleteReport', {
                reportId: this.reportId,
                deleteWithWidget: true,
              })
              if (resp.error) {
                this.$message.error(
                  'Error deleting pivot report and widget',
                  error
                )
              } else {
                this.$message.success('Report deleted successfully')
              }
            }
          } else {
            this.$message.success('Report deleted successfully')
            this.handleDelete(row)
          }
        }
      }
    },
    handleDelete(report) {
      this.reportFolders.forEach(folder => {
        return folder.reports.forEach((r, index, reports) => {
          if (r.id == report.id) {
            reports.splice(index, 1)
          }
        })
      })
    },
  },
}
</script>
<style lang="scss">
.folder-list-view {
  button {
    border: none !important;
    &:hover {
      color: black !important;
    }
  }
  .fl-buttons {
    &:hover {
      background-color: #ebedf4 !important;
    }
  }
  .fl-buttons-report {
    display: none;
    &:hover {
      background-color: #ebedf4 !important;
    }
  }

  .views-item {
    &:hover {
      .fl-buttons-report {
        display: inline;
      }
    }
  }
}
</style>
