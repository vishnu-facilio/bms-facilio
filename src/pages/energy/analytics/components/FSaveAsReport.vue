<template>
  <span>
    <el-popover
      :popper-class="'f-popover'"
      placement="left"
      v-model="visibility"
      :width="300"
    >
      <div class="fc-popover-content">
        <slot name="header">
          <div
            class="title uppercase f12 bold"
            style="letter-spacing: 1.1px; color:#000000"
            v-if="newReport.id && !userSpecific"
          >
            {{ $t('common.wo_report.update_report') }}
          </div>
          <div
            class="title uppercase f12 bold"
            style="letter-spacing: 1.1px; color:#000000"
            v-else-if="!userSpecific"
          >
            {{ $t('common.wo_report.save_as_report') }}
          </div>
        </slot>
        <div v-if="userSpecific">
          <div class="fc-dark-blue-txt13 f13 pointer fwBold" @click="back()">
            <i class="el-icon-back fw6 mR10 fc-dark-blue-txt13"></i>
            {{ $t('common.header.back') }}
          </div>
          <div>
            <div>
              <el-radio
                v-model="shareTo"
                :label="1"
                class="fc-radio-btn pB10 pT10"
                >{{ $t('common.wo_report.only_me') }}</el-radio
              >
            </div>
            <div>
              <el-radio
                v-model="shareTo"
                :label="2"
                class="fc-radio-btn pB10"
                >{{ $t('common.wo_report.everyone') }}</el-radio
              >
            </div>
            <div>
              <el-radio
                v-model="shareTo"
                :label="3"
                class="fc-radio-btn pB10"
                >{{ $t('common.wo_report.specific') }}</el-radio
              >
            </div>
            <el-row v-if="shareTo === 3" class="mT20 el-select-block">
              <el-col :span="24">
                <div class="label-txt-black pB5">
                  {{ $t('common.wo_report.team') }}
                </div>
                <el-select
                  filterable
                  v-model="sharedGroups"
                  multiple
                  collapse-tags
                  class="width100 fc-full-border-select-multiple2"
                  :placeholder="$t('common.wo_report.choose_users')"
                >
                  <el-option
                    v-for="group in groups"
                    :key="group.id"
                    :label="group.name"
                    :value="group.id"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>

            <el-row v-if="shareTo === 3" class="mT20 el-select-block">
              <el-col :span="24">
                <div class="label-txt-black pB5">
                  {{ $t('common.wo_report.role') }}
                </div>
                <el-select
                  filterable
                  v-model="sharedRoles"
                  multiple
                  collapse-tags
                  class="width100 fc-full-border-select-multiple2"
                  :placeholder="$t('common.wo_report.choose_roles')"
                >
                  <el-option
                    v-for="role in roles"
                    :key="role.id"
                    :label="role.name"
                    :value="role.id"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>

            <el-row v-if="shareTo === 3" class="mT20 el-select-block">
              <el-col :span="24">
                <div class="label-txt-black pB5">
                  {{ $t('common.wo_report.staff') }}
                </div>
                <el-select
                  filterable
                  v-model="sharedUsers"
                  multiple
                  collapse-tags
                  class="width100 fc-full-border-select-multiple2"
                  :placeholder="$t('common.wo_report.choose_teams')"
                >
                  <el-option
                    v-for="user in users"
                    :key="user.id"
                    :label="user.name"
                    :value="user.id"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
        </div>
        <div v-else>
          <el-form
            :model="newReport"
            ref="saveAsReportForm"
            :label-position="'top'"
          >
            <el-form-item prop="name" class="mT10 mB0">
              <p class="label-txt-black pB5 mB0">
                {{ $t('common.products.name') }}
              </p>
              <el-input
                :autofocus="true"
                v-model="newReport.name"
                :placeholder="$t('common.products.name')"
                class="fc-input-full-border2"
              ></el-input>
            </el-form-item>
            <el-form-item prop="description" class="mB0 mT10">
              <p class="label-txt-black pB5 mB0">
                {{ $t('common.wo_report.report_description') }}
              </p>
              <el-input
                :autofocus="true"
                v-model="newReport.description"
                type="textarea"
                :placeholder="$t('common.wo_report.report_description')"
                class="fc-input-full-border2 fc-input-full-border-select2 width100"
              ></el-input>
            </el-form-item>
            <el-row>
              <el-col :span="24">
                <el-form-item prop="folder" class="mB0">
                  <p class="label-txt-black pB5 mB0">
                    {{ $t('common.products.folder') }}
                  </p>
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
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <!-- <el-form-item prop="addToDashboard" class="mB0">
              <el-checkbox v-model="newReport.addToDashboard">
                <p class="label-txt-black pB5">
                  {{ $t('home.dashboard.dashboard_add') }}
                </p>
              </el-checkbox>
            </el-form-item> -->
            <!-- <el-form-item v-if="activeTemplate && !newReport.id" prop="isTemplate" class="mB0">
            <el-checkbox v-model="newReport.isTemplate"><p class="label-txt-black pB5">Save as template</p></el-checkbox>
            </el-form-item>-->
            <!-- <el-form-item prop="userSpecific" class="mB0">
            <el-checkbox v-model="userSpecific"><p class="label-txt-black pB5">Sharing Permissions</p></el-checkbox>
            </el-form-item>-->
          </el-form>
        </div>
      </div>
      <div class="f-footer row" style="height:46px;">
        <slot name="footer">
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="close">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveReport"
              :loading="saving"
              >{{
                saving
                  ? $t('common._common._saving')
                  : $t('common._common._save')
              }}</el-button
            >
          </div>
        </slot>
      </div>
      <template slot="reference">
        <slot name="reference"></slot>
      </template>
    </el-popover>
  </span>
</template>

<script>
import deepMerge from 'util/deepmerge'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import {
  isWebTabsEnabled,
  // findRouteForTab,
  findRouteForReport,
  pageTypes,
} from '@facilio/router'

export default {
  mixins: [NewReportSummaryHelper, ReportHelper],
  props: [
    'report',
    'savedReport',
    'moduleName',
    'iscustomModule',
    'config',
    'template',
    'moduleFromRoute',
  ],
  data() {
    return {
      reportTemplateError: false,
      visibility: false,
      reportSharing: [],
      userSpecific: false,
      sharedGroups: [],
      sharedUsers: [],
      sharedRoles: [],
      shareTo: 2,
      newReport: {
        name: '',
        description: '',
        reportFolderId: '',
        analyticsType: 1,
        addToDashboard: false,
        isTemplate: false,
      },
      reportFolders: [],
      saving: false,
    }
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
      roles: state => state.roles,
    }),

    ...mapGetters(['getCurrentUser']),

    reportId() {
      if (
        this.$route.query &&
        this.$route.query.reportId &&
        this.$route.query.duplicate
      ) {
        return null
      } else {
        return parseInt(this.$route.query.reportId)
      }
    },
  },
  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadUsers')
  },
  mounted() {
    this.loadReportFolders()
    this.init()
  },
  watch: {
    visibility(val) {
      if (val) {
        this.init()
      }
    },
  },
  methods: {
    back() {
      this.userSpecific = false
    },
    init() {
      if (this.reportId && this.savedReport) {
        this.$helpers.copy(this.newReport, this.savedReport)
        this.newReport.id = this.reportId
        if (typeof this.template !== 'undefined' && this.template !== null) {
          this.newReport.isTemplate = true
        }
      } else if (this.report) {
        this.$helpers.copy(this.newReport, this.report)
      }
    },
    async loadReportFolders() {
      // no module name case is energy data reports, DON't send moduleName prop as energydata.;leave null for energreports

      if (
        (isWebTabsEnabled() || this.$helpers.isEtisalat()) &&
        this.moduleFromRoute
      ) {
        API.get('/v3/report/folders?moduleName=' + this.moduleFromRoute).then(
          response => {
            if (!response.error) {
              this.reportFolders = response.data.reportFolders
            }
          }
        )
      } else if (this.iscustomModule && this.iscustomModule === true) {
        API.get('/v3/report/folders?moduleName=custommodule').then(response => {
          if (!response.error) {
            this.reportFolders = response.data.reportFolders
          }
        })
      } else if (this.$route.meta.module) {
        API.get(
          `/v3/report/folders?moduleName=${this.$route.meta.module}`
        ).then(response => {
          if (!response.error) {
            this.reportFolders = response.data.reportFolders
          }
        })
      } else if (this.moduleName) {
        API.get(`/v3/report/folders?moduleName=${this.moduleName}`).then(
          response => {
            if (!response.error) {
              this.reportFolders = response.data.reportFolders
            }
          }
        )
      } else {
        let url = '/v3/report/folders?moduleName=energydata'
        let self = this
        if (this.report && this.report.reportType == 5) {
          //pivot
          url += '&isPivot=true'
        }
        API.get(url).then(response => {
          if (!response.error) {
            self.reportFolders = response.data.reportFolders
          }
        })
      }
    },
    overrideTemplate() {
      this.visibility = false
      this.reportTemplateError = false
      let self = this
      if (self.newReport.id) {
        delete self.newReport.id
      }

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
              reportFolderId = response.data.result.reportFolder.id

              self.newReport.reportFolderId = reportFolderId

              self.addReport(true)
            })
        } else {
          self.addReport(true)
        }
      }
    },
    saveFolder(folderName) {
      let self = this
      this.applySharing()
      let apiUrl = null
      if (typeof this.moduleName !== 'undefined' && this.moduleName) {
        if (
          ['workorder', 'alarm'].includes(this.moduleName) ||
          (this.iscustomModule && this.iscustomModule === true)
        ) {
          apiUrl = '/v3/report/folder/create?moduleName=' + this.moduleName
        } else if (
          [
            'newreadingalarm',
            'readingalarmoccurrence',
            'bmsalarm',
            'mlAnomalyAlarm',
            'anomalyalarmoccurrence',
            'violationalarm',
            'violationalarmoccurrence',
            'operationalarm',
            'operationalarmoccurrence',
            'sensoralarm',
            'sensoralarmoccurrence',
            'sensorrollupalarm',
            'sensorrollupalarmoccurrence',
            'basealarm',
            'alarmoccurrence',
            'readingevent',
            'bmsevent',
            'mlAnomalyEvent',
            'violationevent',
            'operationevent',
            'sensorevent',
            'baseevent',
          ].includes(this.moduleName)
        ) {
          apiUrl = '/v3/report/folder/create?moduleName=alarm'
        } else if (this.$route.meta.module === 'visitorlog') {
          apiUrl = '/v3/report/folder/create?moduleName=visitorlog'
        } else if (this.$route.meta.module === 'workorder') {
          apiUrl = '/v3/report/folder/create?moduleName=workorder'
        } else if (this.$route.meta.module === 'tenant') {
          apiUrl = '/v3/report/folder/create?moduleName=tenant'
        } else if (this.$route.meta.module === 'contracts') {
          apiUrl = '/v3/report/folder/create?moduleName=contracts'
        } else if (this.$route.meta.module === 'serviceRequest') {
          apiUrl = '/v3/report/folder/create?moduleName=serviceRequest'
        } else if (this.$route.meta.module === 'purchaseorder') {
          apiUrl = '/v3/report/folder/create?moduleName=purchaseorder'
        } else if (this.$route.meta.module === 'budget') {
          apiUrl = '/v3/report/folder/create?moduleName=budget'
        } else if (this.$route.meta.module === 'item') {
          apiUrl = '/v3/report/folder/create?moduleName=item'
        } else if (this.$route.meta.module === 'inspectionTemplate') {
          apiUrl = '/v3/report/folder/create?moduleName=inspectionTemplate'
        } else if (
          this.$route.meta.module === undefined &&
          this.moduleName.includes('custom_') &&
          this.moduleFromRoute != undefined &&
          this.moduleFromRoute
        ) {
          apiUrl = '/v3/report/folder/create?moduleName=' + this.moduleName
        } else if (
          this.$route.meta.module === undefined &&
          this.moduleName !== undefined &&
          this.moduleFromRoute != undefined &&
          this.moduleFromRoute
        ) {
          apiUrl = '/v3/report/folder/create?moduleName=' + this.moduleFromRoute
        } else {
          apiUrl = '/v3/report/folder/create?moduleName=asset'
        }
      } else {
        apiUrl = '/v3/report/folder/create?moduleName=energydata'
      }
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

    saveReport() {
      let self = this
      if (!this.newReport.name || !this.newReport.name.trim().length) {
        alert(this.$t('common.header.please_enter_the_report_name'))
      } else if (!this.newReport.reportFolderId) {
        alert(this.$t('common.header.please_choose_folder_enter_new_folder'))
      } else {
        let reportFolderId = -1
        try {
          if (!isNaN(this.newReport.reportFolderId)) {
            reportFolderId = parseInt(this.newReport.reportFolderId)
          }
        } catch (err) {
          console.error(err)
        }

        if (isNaN(reportFolderId) || reportFolderId < 0) {
          self
            .saveFolder(this.newReport.reportFolderId)
            .then(function(response) {
              let reportFolder =
                self.$getProperty(response, 'data.reportFolder') || {}
              self.newReport.reportFolderId = reportFolder.id
              self.reportFolders.push(response.data.reportFolder)
              self.addReport()
            })
        } else {
          self.addReport()
        }
      }
    },

    addReport(override) {
      let self = this
      let newReportParams = {}
      let setModuleName =
        typeof this.moduleName !== 'undefined' && this.moduleName
          ? this.$helpers.isLicenseEnabled('NEW_ALARMS') &&
            this.moduleName === 'alarm'
            ? 'alarmoccurrence'
            : this.moduleName
          : 'energydata'
      if (
        this.report &&
        this.report.params &&
        this.report.params.regressionConfig &&
        this.report.params.regressionConfig.length != 0
      ) {
        let params = this.report.params
        this.newReport['type'] = 3
        if (params.regressionType === 'multiple') {
          newReportParams = {
            reportContext: this.newReport,
            chartState: JSON.stringify(
              this.report ? this.report.options : null
            ),
            tabularState: '',
            moduleName: setModuleName,
          }
        } else if (params.regressionType === 'single') {
          //TODO save report for single regression
        }
      } else {
        newReportParams = {
          reportContext: this.newReport,
          chartState: JSON.stringify(this.report ? this.report.options : null),
          tabularState: JSON.stringify(
            this.report ? this.report.tabularState : null
          ),
          moduleName: setModuleName, // TODO Change based on module
        }
      }

      if (
        typeof self.template !== 'undefined' &&
        self.template != null &&
        self.template.show
      ) {
        self.newReport['template'] = JSON.stringify(self.template)
        self.newReport['type'] = 4
      }

      let apiUrl = null
      if (this.report && this.report.reportType == 5) {
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
          reportId: reportId,
        }
      } else if (
        typeof this.moduleName === 'undefined' ||
        this.moduleName === null
      ) {
        apiUrl = newReportParams.reportContext.id
          ? 'v3/report/analytics/update'
          : 'v3/report/analytics/create'
        if (this.report.options.settings) {
          newReportParams.showAlarms = this.report.options.settings.alarm
          newReportParams.showSafeLimit = this.report.options.settings.safelimit
        }
        if (this.report.dateRange.operatorId) {
          newReportParams.dateOperator = this.report.dateRange.operatorId
          if (
            this.report.dateRange.operatorId === 42 ||
            this.report.dateRange.operatorId === 49 ||
            this.report.dateRange.operatorId === 50 ||
            this.report.dateRange.operatorId === 51
          ) {
            // last N days/weeks/months/quarters/years
            newReportParams.dateOperatorValue = this.report.dateRange.offset
          } else if (
            this.report.dateRange.operatorId === 62 ||
            this.report.dateRange.operatorId === 63 ||
            this.report.dateRange.operatorId === 64 ||
            this.report.dateRange.operatorId === 65
          ) {
            // custom date/week/month/quarter/year
            newReportParams.dateOperatorValue = this.report.dateRange.time[0]
          } else if (this.report.dateRange.rangeType === 'R') {
            newReportParams.dateOperatorValue = this.report.dateRange.time.join(
              ','
            )
          }
        }
        newReportParams = Object.assign(newReportParams, this.report.params)
      } else {
        apiUrl = newReportParams.reportContext.id
          ? 'v3/report/update'
          : 'v3/report/create'
        if (this.config) {
          let params = this.$helpers.cloneObject(this.config)
          params.dateField =
            this.config.isCustomDateField || this.config.isTime
              ? this.config.dateField
              : null
          params.dateFilter = null
          newReportParams = deepMerge.objectAssignDeep(newReportParams, params)
          let baselines
          if (
            this.config.baseLine &&
            Array.isArray(this.config.baseLine) &&
            this.config.baseLine.length
          ) {
            if (this.config.baseLine[0] > 0) {
              let bl = {}
              bl.baseLineId = this.config.baseLine[0]
              if (this.config.baseLine.length > 1) {
                bl.adjustType = this.config.baseLine[1]
              }

              let blList = []
              blList.push(bl)

              baselines = blList
            }
          } else if (this.config.baseLine && this.config.baseLine > 0) {
            baselines = [
              {
                baseLineId: this.config.baseLine,
              },
            ]
          }
          if (baselines) {
            newReportParams.baseLines = JSON.stringify(baselines)
          }
        }
      }
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
            let addToDashboard = !!self.newReport.addToDashboard
            self.close(addToDashboard, response.data)
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
                reportURL = self.getRouterUrl(
                  reportContext,
                  self.viewModes().VIEW,
                  true
                )
                self.$router.push({ path: reportURL })
              },
            })

            let addToDashboard = !!self.newReport.addToDashboard

            self.close(addToDashboard, response.data)
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
    getRouterUrl(reportContext, mode, append) {
      let reportURL
      if (mode === this.viewModes().VIEW) {
        if (isWebTabsEnabled()) {
          let routeObj
          if (!this.moduleName) {
            routeObj = findRouteForReport(
              'analytics_reports',
              pageTypes.REPORT_VIEW
            )
          } else if (this.moduleName === 'energydata') {
            routeObj = findRouteForReport(
              'analytics_reports',
              pageTypes.REPORT_VIEW,
              { moduleName: this.moduleName }
            )
          } else {
            routeObj = findRouteForReport(
              'module_reports',
              pageTypes.REPORT_VIEW,
              { moduleName: this.moduleName }
            )
            if (isEmpty(routeObj)) {
              let parent_module_name = ReportHelper.findModuleFromSubmodule(
                this.moduleName
              )
              if (!isEmpty(parent_module_name)) {
                routeObj = findRouteForReport(
                  'module_reports',
                  pageTypes.REPORT_VIEW,
                  { moduleName: parent_module_name }
                )
              }
            }
          }
          let { name } = routeObj || {}
          let params = {}
          if (append) {
            params = { reportid: reportContext.id }
          }
          if (name) {
            reportURL = this.$router.resolve({ name, params: params }).href
          }

          return reportURL
        } else if (reportContext.typeEnum == 'PIVOT_REPORT') {
          reportURL = '/app/em/pivot/view/'
        } else if (
          typeof this.moduleName === 'undefined' ||
          this.moduleName === null
        ) {
          reportURL = '/app/em/reports/newview/'
        } else if (this.$helpers.isEtisalat()) {
          reportURL = '/app/em/modulereports/newview/'
        } else if (this.iscustomModule && this.iscustomModule === true) {
          reportURL = '/app/ca/reports/newview/'
        } else {
          switch (this.moduleName.toLowerCase()) {
            case 'workorder':
            case 'workorderLabour':
            case 'workorderCost':
            case 'workorderItem':
            case 'workorderTools':
            case 'workorderService':
            case 'workorderTimeLog':
            case 'workorderHazard':
            case 'plannedmaintenance':
              reportURL = '/app/wo/reports/newview/'
              break
            case 'purchaseorder':
            case 'poterms':
            case 'purchaseorderlineitems':
            case 'purchaserequest':
            case 'purchaserequestlineitems':
              reportURL = '/app/purchase/reports/newview/'
              break
            case 'inspectiontemplate':
            case 'inspectionresponse':
              reportURL = '/app/inspection/reports/newview/'
              break
            case 'budget':
            case 'budgetamount':
              reportURL = '/app/ac/reports/newview/'
              break
            case 'item':
            case 'tool':
            case 'itemtransactions':
            case 'toottransactions':
            case 'itemtypes':
            case 'tooltypes':
            case 'storeroom':
            case 'shipment':
            case 'transferrequest':
            case 'transferrequestpurchaseditems':
            case 'transferrequestshipmentreceivables':
              if (
                this.$route.path &&
                this.$route.path.includes('/at/reports/')
              ) {
                reportURL = '/app/at/reports/newview/'
              } else {
                reportURL = '/app/inventory/reports/newview/'
              }
              break
            case 'asset':
              reportURL = '/app/at/reports/newview/'
              break
            case 'alarm':
            case 'newreadingalarm':
            case 'readingalarmoccurrence':
            case 'bmsalarm':
            case 'mlAnomalyAlarm':
            case 'anomalyalarmoccurrence':
            case 'violationalarm':
            case 'violationalarmoccurrence':
            case 'operationalarm':
            case 'operationalarmoccurrence':
            case 'sensoralarm':
            case 'sensoralarmoccurrence':
            case 'sensorrollupalarm':
            case 'sensorrollupalarmoccurrence':
            case 'basealarm':
            case 'alarmoccurrence':
            case 'readingevent':
            case 'bmsevent':
            case 'mlAnomalyEvent':
            case 'violationevent':
            case 'operationevent':
            case 'sensorevent':
            case 'baseevent':
              reportURL = '/app/fa/reports/newview/'
              break
            case 'servicerequest':
              reportURL = '/app/sr/reports/newview/'
              break
            case 'visitor':
            case 'visitorlog':
            case 'invitevisitor':
            case 'watchlist':
              reportURL = '/app/vi/reports/newview/'
              break
            case 'tenant':
            case 'tenantunit':
            case 'tenantcontact':
            case 'quote':
            case 'contact':
            case 'tenantspaces':
            case 'quotelineitems':
            case 'quoteterms':
            case 'people':
            case 'newsandinformationsharing':
            case 'neighbourhoodsharing':
            case 'dealsandofferssharing':
            case 'contactdirectorysharing':
            case 'admindocumentsharing':
            case 'audienceSharing':
              reportURL = '/app/tm/reports/newview/'
              break
            case 'contracts':
            case 'purchasecontracts':
            case 'purchasecontractlineitems':
            case 'labourcontracts':
            case 'labourcontractlineitems':
            case 'warrantycontracts':
            case 'warrantycontractlineitems':
            case 'rentalleasecontracts':
            case 'rentalleasecontractlineitems':
              reportURL = '/app/ct/reports/newview/'
              break
            default:
              reportURL = '/app/at/reports/newview/'
              break
          }
        }
        if (append) {
          reportURL = reportURL + reportContext.id
        }
      } else if (mode === this.viewModes().EDIT) {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForReport('module_reports', pageTypes.REPORT_FORM, {
              moduleName: this.moduleName,
            }) || {}

          if (name) {
            reportURL = this.$router.resolve({ name }).href
          }

          return reportURL
        } else if (
          typeof this.moduleName === 'undefined' ||
          this.moduleName === null
        ) {
          reportURL = '/app/em/analytics/building?reportId=$$'
        } else if (this.$helpers.isEtisalat()) {
          reportURL =
            '/app/em/modulereports/new?reportId=$$' +
            '&module=' +
            this.moduleName
        } else if (this.iscustomModule && this.iscustomModule === true) {
          reportURL =
            '/app/ca/reports/new?reportId=$$' + '&module=' + this.moduleName
        } else {
          switch (this.moduleName) {
            case 'workorder':
            case 'workorderLabour':
            case 'workorderCost':
            case 'workorderItem':
            case 'workorderTools':
            case 'workorderService':
            case 'workorderTimeLog':
            case 'workorderHazard':
              reportURL =
                'app/wo/reports/new?reportId=$$' + '&module=' + this.moduleName
              break
            case 'purchaseorder':
            case 'poterms':
            case 'purchaseorderlineitems':
            case 'purchaserequest':
            case 'purchaserequestlineitems':
              reportURL =
                'app/wo/reports/new?reportId=$$' + '&module=purchaseorder'
              break
            case 'budget':
            case 'budgetamount':
              reportURL = 'app/ac/reports/new?reportId=$$' + '&module=budget'
              break
            case 'item':
            case 'tool':
            case 'itemTransactions':
            case 'tootTransactions':
            case 'itemTypes':
            case 'toolTypes':
            case 'storeRoom':
            case 'shipment':
            case 'transferrequest':
            case 'transferrequestpurchaseditems':
            case 'transferrequestshipmentreceivables':
              reportURL =
                'app/inventory/reports/new?reportId=$$' + '&module=item'
              break
            case 'asset':
              reportURL = 'app/at/reports/new?reportId=$$' + '&module=asset'
              break
            case 'alarm':
            case 'newreadingalarm':
            case 'readingalarmoccurrence':
            case 'bmsalarm':
            case 'mlAnomalyAlarm':
            case 'anomalyalarmoccurrence':
            case 'violationalarm':
            case 'violationalarmoccurrence':
            case 'operationalarm':
            case 'operationalarmoccurrence':
            case 'sensoralarm':
            case 'sensoralarmoccurrence':
            case 'sensorrollupalarm':
            case 'sensorrollupalarmoccurrence':
            case 'basealarm':
            case 'alarmoccurrence':
            case 'readingevent':
            case 'bmsevent':
            case 'mlAnomalyEvent':
            case 'violationevent':
            case 'operationevent':
            case 'sensorevent':
            case 'baseevent':
              reportURL =
                '/app/fa/reports/new?reportId=$$' + '&module=' + this.moduleName
              break
            case 'servicerequest':
              reportURL =
                '/app/sr/reports/new?reportId=$$' + '&module=' + this.moduleName
              break
            case 'visitor':
            case 'visitorlog':
            case 'invitevisitor':
            case 'watchlist':
              reportURL =
                '/app/vi/reports/new?reportId=$$' + '&module=' + this.moduleName
              break
            case 'tenant':
            case 'tenantunit':
            case 'tenantcontact':
            case 'quote':
            case 'contact':
            case 'tenantspaces':
            case 'quotelineitems':
            case 'quoteterms':
            case 'people':
            case 'newsandinformationsharing':
            case 'neighbourhoodsharing':
            case 'dealsandofferssharing':
            case 'contactdirectorysharing':
            case 'admindocumentsharing':
            case 'audienceSharing':
              reportURL =
                '/app/tm/reports/new?reportId=$$' + '&module=' + this.moduleName
              break
            case 'contracts':
            case 'purchasecontracts':
            case 'purchasecontractlineitems':
            case 'labourcontracts':
            case 'labourcontractlineitems':
            case 'warrantycontracts':
            case 'warrantycontractlineitems':
            case 'rentalleasecontracts':
            case 'rentalleasecontractlineitems':
              reportURL =
                '/app/ct/reports/new?reportId=$$' + '&module=' + this.moduleName
              break
            default:
              reportURL =
                'app/at/reports/new?reportId=$$' + '&module=' + this.moduleName
              break
          }
        }

        if (append) {
          reportURL = reportURL.replace('$$', reportContext.id)
        }
      }

      return reportURL
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
    close(addToDashboard, reportObj) {
      this.newReport = {
        name: '',
        description: '',
        reportFolderId: '',
        addToDashboard: false,
      }
      this.visibility = false
      this.$emit('close', addToDashboard, reportObj)
    },
  },
}
</script>

<style></style>
