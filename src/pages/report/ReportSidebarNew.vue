<template>
  <div>
    <div class="reports-sidebar">
      <div class="reports-sidebar-header">
        <div class="report-title flex-middle">
          <div class="pivot-summary-all-pivots">
            <el-popover
              placement="bottom-start"
              width="185"
              trigger="click"
              :disabled="$helpers.isPortalUser()"
            >
              <div style="display:flex;align-items:center;margin-top: 10px;">
                <img
                  class="report-icon flex icon"
                  src="~statics/report/calendar.svg"
                  style="height: 14px;width: 14px;margin-left:2px;margin-right: 8px;"
                />
                <span style="cursor:pointer" @click="openScheduledReports()">{{
                  $t('common.wo_report.scheduled_reports')
                }}</span>
              </div>
              <div style="display:flex;align-items:center;margin-top: 10px;">
                <InlineSvg
                  src="svgs/pivot/Manage"
                  iconClass="icon icon-sm"
                  style="margin-top: 4px;margin-right: 5px;"
                ></InlineSvg>
                <span style="cursor:pointer" @click="openmanagefolders()">{{
                  $t('common.wo_report.manage_folders')
                }}</span>
              </div>

              <span
                slot="reference"
                @click="visible = !visible"
                style="letter-spacing: 0.5px;cursor:pointer;font-weight: 500;"
                >{{ $t('common.wo_report.folders')
                }}<i
                  class="el-icon-arrow-down"
                  style="font-size:13px;font-weight:1000;margin-left:4px;cursor:pointer;text-transform:capitalize;"
                ></i
              ></span>
            </el-popover>
          </div>
        </div>
      </div>
      <div class="search-container mT15 mB25 pR10 pL10">
        <el-input
          placeholder="Search Folder"
          v-model="searchText"
          type="search"
          :prefix-icon="'el-icon-search'"
          class="fc-input-full-border2 pL5"
        >
        </el-input>
      </div>
      <div class="rtree">
        <div class="report-sidebar-scroll">
          <spinner v-if="loading" :show="loading" />
          <div v-else>
            <div
              class="p15 flex-middle height80vh justify-content-center flex-direction-column"
              v-if="!newReportTree.length && !reportTree.length"
            >
              <div>
                <inline-svg
                  src="svgs/emptystate/reportlist"
                  iconClass="icon text-center icon-xxxlg"
                ></inline-svg>
              </div>
              <div class="nowo-label">
                {{ $t('common.wo_report.no_reports') }}
              </div>
            </div>
            <div v-if="newReportTree" class="pB100 folder-list-container">
              <div
                v-for="(folder, index2) in reportFolders"
                :key="'new_' + index2"
                class="folder-container"
                :id="`${folder.id}`"
              >
                <el-collapse v-model="expandFolder" accordion>
                  <el-collapse-item :name="folder.id">
                    <template slot="title">
                      <div class="folder-title-section" @click="expand(folder)">
                        <div class="report-folder-icon-section">
                          <img src="~/assets/folder-pink.svg" />
                        </div>
                        <div class="r-folder-name">
                          {{ folder.name }}
                        </div>
                      </div>
                    </template>

                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <router-view :key="$route.fullPath"></router-view>
  </div>
</template>
<script>
import ReportHelper from 'pages/report/mixins/ReportHelper'
import ModularAnalyticmixin from 'pages/energy/analytics/mixins/ModularAnalyticmixin'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForReport,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'

export default {
  props: ['selectedFolderObj', 'appId', 'tabId', 'newReportTree', 'perPage'],
  mixins: [ReportHelper, ModularAnalyticmixin],
  data() {
    return {
      loading: false,
      reportTree: [],
      searchText: '',
      editFolderId: null,
      visible: false,
      expandFolder: [],
      count: null,
    }
  },
  watch: {
    newReportTree: {
      handler() {
        this.init()
      },
      immediate: true,
    },
  },
  computed: {
    reportId() {
      let reportId = this.$attrs.reportid || this.$route.params.reportid
      return parseInt(reportId)
    },
    reportFolders() {
      // let activeFolder=document.getElementById(`${this.newReportTree[0].id}`)
      // activeFolder.classList.add('selected-folder')
      let list = this.newReportTree
      if (isEmpty(this.searchText)) {
        return list
      } else {
        let filteredReports = list.filter(report => {
          return report.name
            .toLowerCase()
            .includes(this.searchText.toLowerCase())
        })

        if (!isEmpty(filteredReports)) {
          return filteredReports
        }
        return []
      }
    },
  },
  methods: {
    init() {
      if (this.selectedFolderObj) {
        this.expandFolder.push(this.selectedFolderObj.id)
      }
    },
    toggleEditFolder(folder) {
      this.editFolderId = folder.id
      this.$nextTick(() => {
        if (Array.isArray(this.$refs['FolderName'])) {
          this.$refs['FolderName'][0].focus()
        } else {
          this.$refs['FolderName'].focus()
        }
      })
    },
    openmanagefolders() {
      if (isWebTabsEnabled()) {
        this.moduleName = this.$attrs.moduleName
        let path
        let routeObj
        if (!this.moduleName) {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_SCHEDULED
          )
        } else if (this.moduleName === 'energydata') {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_SCHEDULED,
            { moduleName: this.moduleName }
          )
        } else {
          routeObj = findRouteForReport(
            'module_reports',
            pageTypes.REPORT_SCHEDULED,
            { moduleName: this.moduleName }
          )
        }
        let { name } = routeObj || {}

        if (name) {
          path = this.$router.resolve({ name }).href + '?activeTab=folderViews'
        }
        if (path) this.$router.push({ path })
      } else {
        if (this.$route.meta.module === 'custom') {
          this.$router.push('reports/foldermanager?activeTab=folderViews')
        } else {
          this.$router.push(
            `${
              this.getCurrentModule(true).rootPath
            }/foldermanager?activeTab=folderViews`
          )
        }
      }
      this.$emit('foldermanager')
    },
    collapseControl(id) {
      if (this.expandFolder.indexOf(id) === -1 && !isNaN(id)) {
        this.expandFolder.push(parseInt(id))
      } else if (!isNaN(id)) {
        this.expandFolder.splice(this.expandFolder.indexOf(id), 1)
      }
    },
    async expand(folder, page, searchText) {
      if (this.$route.query?.folderId) {
        this.$parent.loading = true
      }
      this.expandFolder = []
      this.toggleIsActive()
      await this.countCall(folder, searchText)
      await this.listCall(folder, page, searchText)
      if (folder) {
        let activeFolder = document.getElementById(`${folder?.id}`)
        activeFolder.classList.add('selected-folder')
        this.$emit('folderchanged', folder)
      }
      this.$parent.loading = false
      this.$parent.showSearchIcon = true
      this.getPaginationDetails()
    },
    async countCall(folder, searchText) {
      let apiUrl = 'v3/report/reportListView'
      let params = this.paginationParams(folder, true, 1)
      if (searchText != null) {
        params['searchText'] = searchText
      }
      params['orderType'] = 'desc'
      await API.get(apiUrl, params).then(response => {
        if (!response.error) {
          this.count = response.data.count
        }
      })
    },
    async listCall(folder, page, searchText, orderType) {
      let self=this
      let apiUrl = 'v3/report/reportListView'
      let pageno = page ? page : 1
      let params = this.paginationParams(folder, false, pageno)
      if (searchText != null) {
        params['searchText'] = searchText
      }
      params['orderType'] = orderType ? orderType : 'desc'
      await API.get(apiUrl, params).then(response => {
        if (!response.error) {
          self.reportsList = response.data.reportsList
        }
      })
      this.$emit('reportsList', self.reportsList)
    },
    getPaginationDetails() {
      let { count } = this
      let from = count > 0 ? 1 : 0
      let to = null
      if (count > this.perPage) {
        to = this.reportsList.length
      } else {
        to = count
      }
      this.$emit('paginationDetails', from, to, count)
    },
    paginationParams(folder, count, page) {
      let params = {
        folderId: folder?.id,
        appId: this.appId,
        moduleId: folder?.moduleId,
        page: page,
        perPage: this.perPage,
        withCount: count,
        isPivot: false,
      }
      return params
    },
    toggleIsActive() {
      let headers =
        document.querySelectorAll('div.is-active') &&
        document.querySelectorAll('i.is-active')
      headers.forEach(h => h.classList.remove('is-active'))
      let prevActiveFolders = document.querySelectorAll('.selected-folder')
      prevActiveFolders.forEach(af => af?.classList?.remove('selected-folder'))
    },
    openScheduledReports() {
      let path
      if (isWebTabsEnabled()) {
        this.moduleName = this.$attrs.moduleName
        let routeObj
        if (!this.moduleName) {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_SCHEDULED
          )
        } else if (this.moduleName === 'energydata') {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_SCHEDULED,
            { moduleName: this.moduleName }
          )
        } else {
          routeObj = findRouteForReport(
            'module_reports',
            pageTypes.REPORT_SCHEDULED,
            { moduleName: this.moduleName }
          )
        }
        let { name } = routeObj || {}

        if (name) {
          path =
            this.$router.resolve({ name }).href + '?activeTab=ScheduleViews'
        }
      } else {
        path = `${
          this.getCurrentModule(true).rootPath
        }/foldermanager?activeTab=ScheduleViews`
      }
      if (path) this.$router.push({ path })
      this.$emit('foldermanager')
    },
  },
}
</script>
<style>
.edit-icon-set {
  position: absolute;
  right: 10px;
  top: 12px;
  z-index: 3;
}
.row.reports-layout {
  height: 100%;
}

.rtree {
  position: relative;
  padding-bottom: 70px;
}

.rfolder-name {
  padding: 14px 10px 14px 20px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}

.folder-container {
  position: relative;
}

.folder-container:hover .icon-display {
  visibility: visible;
}
.icon-display {
  visibility: hidden;
  padding: 5px;
}
.rfolder-name i {
  margin-right: 6px;
  font-size: 16px;
  color: #66666696;
}

.rfolder-children div {
  /* padding: 10px 10px 10px 25px; */
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.rfolder-children div:not(.rempty) {
  cursor: pointer;
  font-size: 14px;
  font-weight: 400;
  font-style: normal;
  font-stretch: normal;
  letter-spacing: 0.4px;
  text-align: left;
  color: #324056;
}

.rfolder-name:hover,
.rfolder-children div:not(.rempty):hover,
.rfolder-children div.active,
.scheduled-viewall:hover,
.scheduled-viewall div.active {
  background: #f0f7f8;
}

.rfolder-children div a:hover {
  color: #324056;
}
.rfolder-icon {
  display: inline-block;
}

.rempty {
  font-size: 13px;
  color: rgba(102, 102, 102, 0.57);
}
.fc-chart-side-btn {
  padding: 0;
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  float: left;
  padding-top: 17px;
  /* color: #717b85; */
}
.fc-chart-side-btn .el-button-group button.el-button {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.9px;
  text-align: left;
  /* color: #000000; */
}
.m20 {
  margin: 20px;
}
.pR10 {
  padding-right: 10px;
}
.pL10 {
  padding-right: 10px;
}
.r-sidebar-btn {
  padding-left: 10px;
  color: #615f89;
}
.r-sidebar-btn .el-button {
  color: #615f89;
  border-left: none;
}
.r-sidebar-btn .el-button-group .el-button:last-child {
  border-left: none;
}

.scheduled-viewall {
  width: 100%;
  left: 0;
  right: 0;
  border-top: solid 1px #f4f4f4;
  position: absolute;
  bottom: 30px;
  background-color: #ffffff;
  font-size: 14px;
  line-height: 25px;
  letter-spacing: 0.4px;
  color: #46a2bf;
  cursor: pointer;
  padding-left: 30px;
  text-align: left;
}

.scheduled-viewall div {
  padding: 13px 20px;
}

.new-folder-label {
  text-align: center;
  color: #303133;
  font-size: 14px;
  line-height: 1;
  margin-bottom: 12px;
  margin-top: 3px;
}
.new-folder-button {
  border: 0px;
  width: 100%;
}
.report-new-folder:hover .show-folder-delete {
  display: inline;
  float: right;
}
.ul-folder-list {
  list-style-type: none;
  padding-left: 0;
  margin-top: 0;
}

.ul-folder-list li {
  padding: 10px;
  cursor: pointer;
  line-height: 24px;
}
.ul-folder-list li:hover {
  background: #f0f7f8;
}

.folder-popover {
  height: 200px;
  max-width: 200px;
  overflow-y: scroll;
  overflow-x: hidden;
}
.show-folder-delete {
  display: none;
  margin-right: 0 !important;
}
.folder-list-txt span {
  padding-right: 10px;
}
.folder-list-txt.active {
  background: #f0f7f8;
}
.report-sidebar-scroll {
  height: calc(100vh - 150px);
  overflow-y: scroll;
}
</style>
<style lang="scss">
.reports-sidebar {
  background: white;
  height: 100%;
  overflow-y: hidden;
  border-right: 1px solid #6666662f;
  position: relative;
  .reports-sidebar-header {
    height: 60px !important;
    border-bottom: none !important;
    padding-left: 15px !important;
  }
  .report-title {
    font-size: 14px !important;
    display: flex;
    align-items: center;
  }
}
.search-container {
  .el-input__inner {
    padding-left: 40px !important;
    border-radius: 5px !important;
  }
  .el-input__prefix {
    left: 10px;
  }
}
.report-sidebar-scroll {
  .el-collapse-item__header {
    height: 36px !important;
    border-bottom: none !important;
    width: 318px;
    background: transparent;
  }
  .el-collapse-item__content {
    padding-bottom: 0px !important;
  }
  .el-collapse-item__wrap {
    border-bottom: none !important;
  }
  .is-active {
    background-color: #f1f4f8;
  }
  .focusing {
    background-color: #f1f4f8;
  }
  .selected-folder {
    background-color: #f1f4f8;
  }
}
.folder-title-section {
  display: flex;
  align-items: baseline;
  padding-left: 15px;
  align-content: space-around;
  .active {
    background-color: #f1f4f8;
  }
}
.reports-sidebar {
  .el-collapse-item__arrow {
    display: none;
  }
}
.reports-sidebar {
  .el-collapse {
    border: none !important;
  }

  .label-txt-black {
    font-size: 13px !important;
  }

  .folder-list-text {
    padding-top: 5px;
    padding-bottom: 8px;
    padding-left: 35px;
  }
  .report-folder-icon-section {
    padding-right: 10px;
  }
  .r-folder-name {
    color: #ff3184;
    text-transform: uppercase;
    font-size: 12px !important;
    width: 251px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
</style>
