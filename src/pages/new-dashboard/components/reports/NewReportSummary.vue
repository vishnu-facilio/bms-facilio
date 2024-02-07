<template>
  <div class="reports-summary new-reports-summary">
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <div class="reports-header" v-if="resultObj">
      <div class="report-title pull-left" style="width: 50%;">
        <div class="title row">
          <div
            style="overflow:hidden;border-bottom: none;"
            class="report-pdf-header"
            v-if="showHeader"
          >
            <div v-show="$org.logoUrl" class="fc-report-cus-logo">
              <img :src="$org.logoUrl" style="width: 100px;" />
            </div>
            <div
              class="f18 fc-widget-label ellipsis max-width350px"
              @click="renameDialogVisibility = !renameDialogVisibility"
            >
              {{ resultObj.report.name }}
            </div>
            <div class="fc-logo-report">
              <img src="~assets/facilio-logo-black.svg" />
            </div>
            <!-- <rename-report :visibility.sync="renameDialogVisibility" :report="resultObj.report"></rename-report> -->
          </div>
          <div
            class="fc-text-pink13 text-center fc-pdf-date-picker"
            v-if="showPrintDetails"
          >
            <span class="fc-black-13">{{
              getDateRangeString.dateFieldLabel +
                getDateRangeString.dateFieldLabel ===
              ''
                ? ''
                : ':'
            }}</span>
            {{ getDateRangeString.label }}
          </div>
          <div
            v-if="
              resultObj && resultObj.report.hasEdit && moduleRolePermission()
            "
            class="report-header-title"
            :class="{
              'title-actions': !dialogSettings.duplicateReportOptions,
              'title-actions-fixed': dialogSettings.duplicateReportOptions,
            }"
          >
            <i
              class="el-icon-edit"
              @click="editReport(resultObj.report.id)"
              data-position="top"
              data-arrow="true"
              :title="$t('home.dashboard.edit_report')"
              v-tippy
            ></i>
            <i
              class="el-icon-delete"
              @click="deleteReport(resultObj.report.id)"
              data-position="top"
              data-arrow="true"
              :title="$t('common.wo_report.report_delete')"
              v-tippy
            ></i>
            <i
              v-if="
                !newDuplicate &&
                  !isRegressionReport &&
                  resultObj.report.type !== 4
              "
              :class="['fa fa-clone']"
              @click="duplicateReport(resultObj.report.id)"
              data-position="top"
              data-arrow="true"
              :title="$t('home.dashboard.duplicate_report')"
              v-tippy
            ></i>
            <el-popover
              popper-class="duplicate-points-popover"
              placement="bottom"
              trigger="click"
              width="250"
            >
              <outside-click
                :visibility="dialogSettings.duplicateReportOptions"
                @onOutsideClick="dialogSettings.duplicateReportOptions = false"
              >
                <div
                  class="pointer duplicate-report-option"
                  @click="duplicateReport(resultObj.report.id)"
                >
                  {{ $t('common._common.duplicate_report') }}
                </div>
                <div
                  class="pointer duplicate-report-option"
                  v-if="
                    getDuplicateCase(reportObj) &&
                      (getDuplicateCase(reportObj).case === 1 ||
                        getDuplicateCase(reportObj).case === 2)
                  "
                  @click="duplicatePointsToAsset"
                >
                  {{ $t('common._common.duplicate_report_another_asset') }}
                </div>
                <div
                  class="pointer duplicate-report-option"
                  v-if="
                    getDuplicateCase(reportObj) &&
                      getDuplicateCase(reportObj).case === 3
                  "
                  @click="duplicatePointsToAsset"
                >
                  {{ $t('common._common.duplicate_report_other_fields') }}
                </div>
              </outside-click>
              <div
                slot="reference"
                @click.stop="dialogSettings.duplicateReportOptions = true"
              >
                <i
                  v-if="
                    newDuplicate &&
                      !isRegressionReport &&
                      resultObj.report.type !== 4
                  "
                  :class="['fa fa-clone']"
                  data-position="top"
                  data-arrow="true"
                  :title="$t('home.dashboard.duplicate_report')"
                  v-tippy
                ></i>
              </div>
            </el-popover>
            <!-- <img
              v-if="!isRegressionReport"
              class="pointer"
              src="~assets/dashboard.svg"
              @click="sendToDashBoard = true"
              data-position="top"
              data-arrow="true"
              :title="$t('home.dashboard.dashboard_add')"
              v-tippy
            /> -->
          </div>
        </div>
        <div class="description">
          {{
            resultObj.report.description ? resultObj.report.description : '---'
          }}
        </div>
      </div>
      <div
        v-if="!isRegressionReport"
        class="pull-right"
        style="padding-right: 5px;display: inline-flex;padding-top: 10px;"
      >
        <f-report-options
          optionClass="report-options"
          :optionsToEnable="[1, 2, 4]"
          :params="reportParams"
          :pdf="true"
          :reportObject="resultObj"
          :optionSettings="
            reportObj && reportObj.options ? reportObj.options.settings : null
          "
        ></f-report-options>
      </div>
      <div style="clear:both"></div>
    </div>
    <div class="height100 scrollable chart-table-layout mT10 p20">
      <f-new-report
        v-if="reportId && isOptimize === false"
        :id="reportId"
        :showTimePeriod="
          resultObj && [1, 4].includes(resultObj.mode) ? true : false
        "
        @reportLoaded="reportLoaded"
        :loadImmediately="true"
      ></f-new-report>
      <f-new-report-optimize
        v-if="reportId && isOptimize === true"
        :showTimePeriod="
          resultObj && [1, 4].includes(resultObj.mode) ? true : false
        "
        :id="reportId"
        @reportLoaded="reportLoaded"
      ></f-new-report-optimize>
    </div>
    <el-dialog
      :title="dialogSettings.dialogTitle"
      width="30%"
      class="fc-dialog-center-container duplicate-dialog-body"
      :visible.sync="dialogSettings.chooseDuplicateAssetSync"
    >
      <div class="pL30 pR30">
        <div v-if="!dialogSettings.reportLoading" class="pT10">
          <el-select
            v-if="loadedAssets.length !== 0"
            :placeholder="
              dialogSettings.chooseDuplicateAssetSync
                ? loadedAssets[0].name
                : ''
            "
            multiple
            collapse-tags
            filterable
            v-model="duplicateTo"
            class="width300px fc-input-full-border-select2 fc-tag"
          >
            <el-option
              v-for="(value, index) in loadedAssets"
              :key="index"
              :label="value['name']"
              :value="value['id']"
            ></el-option>
          </el-select>
          <el-select
            v-else
            :placeholder="$t('common.products.select_fields')"
            multiple
            filterable
            v-model="duplicateTo"
            collapse-tags
            class="width300px fc-input-full-border-select2 fc-tag"
          >
            <el-option
              v-for="(value2, index2) in onlyReadings"
              :key="index2"
              :label="value2['displayName']"
              :value="value2['id']"
            ></el-option>
          </el-select>
        </div>
        <div v-else class="pT10">
          <div
            v-if="!dialogSettings.spinnerToggle"
            class="pointer auto-created-report"
            v-for="(newReport, index3) in newlyCreatedReports"
            :key="index3"
            @click="openReportInView(newReport.id)"
          >
            {{ newReport.name }}
          </div>
          <spinner
            v-if="dialogSettings.spinnerToggle"
            :show="dialogSettings.spinnerToggle"
            :size="40"
          ></spinner>
        </div>
      </div>
      <div class="fc-dialog-footer">
        <el-button
          v-if="!dialogSettings.reportLoading"
          type="primary"
          @click="addReport"
          class="btn-green-full"
          >{{ $t('common.header.add_as_report') }}</el-button
        >
      </div>
    </el-dialog>
    <MoveReportToDashBoard
      v-bind:enableMoveDialog.sync="sendToDashBoard"
      :reportObj="resultObj"
      moduleName="energyData"
    ></MoveReportToDashBoard>
  </div>
</template>

<script>
import FNewReport from './components/FNewReport'
import FNewReportOptimize from './components/FNewReportOptimize'
import FReportOptions from 'pages/report/components/FReportOptions'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import MoveReportToDashBoard from 'src/pages/energy/analytics/newTools/MoveReportToDashBoard'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import OutsideClick from '@/OutsideClick'
import { API } from '@facilio/api'
import Vue from 'vue'
import moment from 'moment-timezone'

export default {
  mixins: [ReportHelper, NewReportSummaryHelper, NewDataFormatHelper],
  components: {
    FNewReport,
    FNewReportOptimize,
    FReportOptions,
    MoveReportToDashBoard,
    OutsideClick,
  },
  data() {
    return {
      spinnerToggle: false,
      newlyCreatedReports: [],
      loadedFields: [],
      loadedAssets: [],
      duplicateTo: [],
      dialogSettings: {
        reportLoading: false,
        chooseDuplicateAssetSync: false,
        duplicateReportOptions: false,
        dialogTitle: 'Duplicate Points',
      },
      duplicateCase: null,
      reportObj: null,
      resultObj: null,
      exportDownloadUrl: null,
      schedule: false,
      email: false,
      duplicateReportToggle: false,
      currentDateFilter: null,
      renameDialogVisibility: false,
      duplicateCases: {
        SASP: 1, // Single Point Single Asset
        SAMP: 2, // Single Point Multiple asset
        MASP: 3, // Multiple Asset Single Point
        MPMA: 4, // Multiple Point Multiple Asset
      },
      sendToDashBoard: false,
    }
  },
  computed: {
    isOptimize() {
      if (this.$route.query.newchart) {
        return true
      } else {
        return false
      }
    },
    isScatter() {
      if (
        this.resultObj &&
        this.resultObj.report &&
        this.resultObj.report.analyticsType === 8
      ) {
        return true
      }
      return false
    },
    isRegressionReport() {
      if (this.resultObj) {
        if (
          this.resultObj.report.reportState &&
          this.resultObj.report.reportState.regressionConfig &&
          this.resultObj.report.reportState.regressionConfig.length !== 0
        ) {
          return true
        } else {
          return false
        }
      }
      return null
    },
    newDuplicate() {
      if (this.getDuplicateCase(this.reportObj) === null) {
        return false
      } else if (this.getDuplicateCase(this.reportObj).case === 4) {
        return false
      }
      return true
    },
    getDateRangeString() {
      let temp = {
        dateFieldLabel: '',
        label: '',
      }
      if (this.resultObj) {
        let xField = this.resultObj.report.dataPoints[0].xAxis
        let dateField = this.resultObj.report.dataPoints[0].dateField
        let dateFieldString = ''
        if (xField) {
          if (dateField) {
            let a = moment(this.resultObj.report.dateRange.startTime).tz(
              Vue.prototype.$timezone
            )
            dateFieldString = a.format('DD/MM/YYYY') + ' ' + 'to '
            a = moment(this.resultObj.report.dateRange.endTime).tz(
              Vue.prototype.$timezone
            )
            dateFieldString = dateFieldString + a.format('DD/MM/YYYY') + ' '
            temp['label'] = dateFieldString
            return temp
          }
        }
        return temp
      }
      return temp
    },
    onlyReadings() {
      if (this.loadedFields.length !== 0) {
        let readings = []
        for (let readingModuleIndex in this.loadedFields) {
          for (let readingIndex of Object.keys(
            this.loadedFields[readingModuleIndex].readings
          )) {
            readings.push(
              this.loadedFields[readingModuleIndex].readings[readingIndex]
            )
          }
        }
        return readings
      } else {
        return []
      }
    },
    reportId() {
      let reportId = this.$attrs.reportid || this.$route.params.reportid

      return parseInt(reportId)
    },
    reportParams() {
      let params = { reportId: this.reportId }
      if (this.resultObj) {
        params.mode = this.resultObj.mode
        params.startTime = this.reportObj.dateRange.time[0]
        params.endTime = this.reportObj.dateRange.time[1]
        params.dateOperator = this.reportObj.dateRange.operatorId
        params.dateOperatorValue =
          this.reportObj.dateRange.operatorId === 20
            ? this.reportObj.dateRange.time.join(',')
            : this.reportObj.dateRange.value
        params.chartType = this.reportObj.options.type
        const parameter = this.reportObj?.params ?? {}
        params.xCriteriaMode = parameter.xCriteriaMode
        params.parentId = parameter.parentId
        params.spaceId = parameter.spaceId
      }
      return params
    },
  },
  methods: {
    handleNewDuplicate() {
      if (this.dialogSettings.duplicateReportOptions) {
        this.dialogSettings.duplicateReportOptions = false
      } else {
        this.dialogSettings.duplicateReportOptions = true
      }
    },
    addReport() {
      this.dialogSettings.chooseDuplicateAssetSync = false
      if (this.duplicateCase.case === 1 || this.duplicateCase.case === 2) {
        this.dialogSettings.chooseDuplicateAssetSync = true
        this.dialogSettings.reportLoading = true
        this.dialogSettings.spinnerToggle = true
        for (let assetIdIndex in this.duplicateTo) {
          let reportParams = this.getReadingReportDefaultParams(this.resultObj)
          let fields = []
          fields = this.getFields(this.duplicateTo[assetIdIndex])
          reportParams['fields'] = JSON.stringify(fields)
          this.$http
            .post('v2/report/readingReport', reportParams)
            .then(response => {
              let result = response.data.result
              result.mode = this.resultObj.mode
              result.analyticsType = this.resultObj.report.analyticsType
              result.xAggr = this.resultObj.xAggr
              result.dateRange = this.resultObj.dateRange
              let report = this.prepareReport(result)
              report.options.common.buildingIds = this.resultObj.report.chartState.common.buildingIds
              let finalReportParams = this.getDefaultReportParams(
                this.resultObj
              )
              report.options.settings = this.resultObj.report.chartState.settings
              report.options.dataPoints = this.groupDataPoints(
                this.resultObj.report.chartState.dataPoints,
                report.options.dataPoints
              )
              finalReportParams['fields'] = JSON.stringify(
                this.copyNamesToyAxis(fields, result.report.dataPoints)
              )
              finalReportParams['chartState'] = JSON.stringify(report.options)
              finalReportParams.reportContext.name =
                this.getNewAssetName(
                  this.loadedAssets,
                  this.duplicateTo[assetIdIndex]
                ) +
                '(created from: ' +
                this.resultObj.report.name +
                ')'
              finalReportParams[
                'startTime'
              ] = this.resultObj.report.dateRange.startTime
              finalReportParams[
                'endTime'
              ] = this.resultObj.report.dateRange.endTime
              if (
                this.resultObj.report.dateOperatorEnum.indexOf('LAST_N') !== -1
              ) {
                finalReportParams['dateOperatorValue'] = Number(
                  this.resultObj.report.dateValue
                )
              }
              this.$http
                .post('/v2/report/addReadingReport', finalReportParams)
                .then(response => {
                  this.newlyCreatedReports.push(response.data.result.report)
                  if (
                    assetIdIndex === (this.duplicateTo.length - 1).toString()
                  ) {
                    this.dialogSettings.dialogTitle =
                      'Reports created successfully'
                    this.dialogSettings.spinnerToggle = false
                    this.duplicateTo = []
                  }
                })
            })
        }
      } else if (this.duplicateCase.case === 3) {
        this.dialogSettings.chooseDuplicateAssetSync = true
        this.dialogSettings.reportLoading = true
        this.dialogSettings.spinnerToggle = true
        for (let dataPointIdIndex in this.duplicateTo) {
          let reportParams = this.getReadingReportDefaultParams(this.resultObj)
          let fields = this.getFields(this.duplicateTo[dataPointIdIndex])
          reportParams['fields'] = JSON.stringify(fields)
          this.$http
            .post('v2/report/readingReport', reportParams)
            .then(response => {
              let result = response.data.result
              result.mode = this.resultObj.mode
              result.analyticsType = this.resultObj.report.analyticsType
              result.xAggr = this.resultObj.xAggr
              result.dateRange = this.resultObj.dateRange
              let report = this.prepareReport(result)
              report.options.common.buildingIds = this.resultObj.report.chartState.common.buildingIds
              let finalReportParams = this.getDefaultReportParams(
                this.resultObj
              )
              report.options.settings = this.resultObj.report.chartState.settings
              report.options.dataPoints = this.groupDataPoints(
                this.resultObj.report.chartState.dataPoints,
                report.options.dataPoints
              )
              finalReportParams['fields'] = JSON.stringify(
                this.copyNamesToyAxis(fields, result.report.dataPoints)
              )
              finalReportParams['chartState'] = JSON.stringify(report.options)
              finalReportParams.reportContext.name =
                this.getFieldName(
                  this.loadedFields,
                  this.duplicateTo[dataPointIdIndex]
                ) +
                '(created from: ' +
                this.resultObj.report.name +
                ')'
              finalReportParams[
                'startTime'
              ] = this.resultObj.report.dateRange.startTime
              finalReportParams[
                'endTime'
              ] = this.resultObj.report.dateRange.endTime
              this.$http
                .post('/v2/report/addReadingReport', finalReportParams)
                .then(response => {
                  if (
                    dataPointIdIndex ===
                    (this.duplicateTo.length - 1).toString()
                  ) {
                    this.dialogSettings.dialogTitle = this.$t(
                      'common._common.reports_created_successfully'
                    )
                    this.dialogSettings.spinnerToggle = false
                    this.duplicateTo = []
                  }
                  this.newlyCreatedReports.push(response.data.result.report)
                })
            })
        }
      }
    },
    duplicatePointsToAsset() {
      this.duplicateTo = []
      this.dialogSettings.reportLoading = false
      this.newlyCreatedReports = []
      this.dialogSettings.duplicateReportOptions = false
      this.duplicateCase = this.getDuplicateCase(this.reportObj)
      if (
        this.duplicateCase &&
        (this.duplicateCase.case === 1 || this.duplicateCase.case === 2)
      ) {
        // get assets of the same category
        this.loadedFields = []
        if (this.loadedAssets.length > 0) {
          this.dialogSettings.chooseDuplicateAssetSync = true
        } else {
          this.$http
            .get('/asset/summary/' + this.duplicateCase.parentIds[0])
            .then(response => {
              let categoryId = response.data.asset.category.id
              let filter = {
                category: { operator: 'is', value: [categoryId + ''] },
              }
              this.$http
                .get(
                  '/asset/all?filters=' +
                    encodeURIComponent(JSON.stringify(filter))
                )
                .then(response => {
                  this.loadedAssets = response.data.assets
                  this.dialogSettings.chooseDuplicateAssetSync = true
                })
            })
        }
      } else if (this.duplicateCase && this.duplicateCase.case === 3) {
        this.loadedAssets = []
        this.$http
          .get('/asset/summary/' + this.duplicateCase.parentIds[0])
          .then(response => {
            let categoryId = response.data.asset.category.id
            this.$http
              .get('/reading/getassetreadings?parentCategoryId=' + categoryId)
              .then(response => {
                for (let readingModuleIndex in response.data) {
                  let temp = {}
                  temp['readingModuleName'] =
                    response.data[readingModuleIndex].name
                  temp['readingModuleDisplayName'] =
                    response.data[readingModuleIndex].displayName
                  temp['readingModuleId'] =
                    response.data[readingModuleIndex].moduleId
                  let readings = {}
                  for (let readingIndex in response.data[readingModuleIndex]
                    .fields) {
                    readings[
                      response.data[readingModuleIndex].fields[
                        readingIndex
                      ].fieldId
                    ] = response.data[readingModuleIndex].fields[readingIndex]
                  }
                  temp['readings'] = readings
                  this.loadedFields.push(temp)
                }
                this.dialogSettings.chooseDuplicateAssetSync = true
              })
          })
      }
    },
    duplicateReport(reportId) {
      this.dialogSettings.duplicateReportOptions = false
      this.duplicateReportToggle = true
      this.editReport(reportId)
    },
    moduleRolePermission() {
      let moduleName = null
      if (isWebTabsEnabled() && this.$attrs.moduleName) {
        moduleName = this.$attrs.moduleName
        if (moduleName === 'energydata') {
          moduleName = 'energy'
        }
      } else {
        let currentModule = this.getCurrentModule()
        moduleName = currentModule.module
        if (currentModule.module === 'energydata') {
          moduleName = 'energy'
        }
      }
      if (['workorder', 'alarm', 'energy'].includes(moduleName)) {
        return this.$hasPermission(moduleName + ':CREATE_EDIT_REPORTS')
      }
      return true
    },
    openReportInView(reportId) {
      if (reportId) {
        window.location.href = this.getNewReportLink(reportId)
      } else {
        this.duplicateDialog = false
        window.location.href = this.getNewReportLink(this.duplicatedReport.id)
      }
    },
    reportLoaded(reportObj, resultObj) {
      this.reportObj = reportObj
      this.resultObj = resultObj
    },

    exportData() {},

    printReport() {},
    getNewReportLink(reportId) {
      return this.getCurrentModule().rootPath + '/newview/' + reportId
    },
    editReport(id) {
      let routePath
      let {
        ANALYTIC_PORTFOLIO,
        ANALYTIC_BUILDING,
        HEAT_MAP,
        ANALYTIC_SITE,
        TREE_MAP,
        REGRESSION,
        SCATTER,
      } = pageTypes

      switch (this.resultObj.report.analyticsType) {
        case 1:
          if (this.resultObj.filters && this.resultObj.filters.xCriteriaMode) {
            routePath = { path: 'portfolio', pageType: ANALYTIC_PORTFOLIO }
          } else {
            routePath = { path: 'building', pageType: ANALYTIC_BUILDING }
          }
          break
        case 3:
          routePath = { path: 'heatmap', pageType: HEAT_MAP }
          break
        case 6:
          routePath = { path: 'site', pageType: ANALYTIC_SITE }
          break
        case 7:
          routePath = { path: 'treemap', pageType: TREE_MAP }
          break
        case 8:
          routePath = { path: 'scatter', pageType: SCATTER }
          break
        default:
          routePath = { path: 'building', pageType: ANALYTIC_BUILDING }
      }

      if (this.isRegressionReport) {
        routePath = { path: 'regression', pageType: REGRESSION }
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(routePath.pageType) || {}
        let query = { reportId: id }

        if (this.duplicateReportToggle === true) {
          query.duplicate = true
        }
        name &&
          this.$router.push({
            name,
            query,
          })
      } else {
        let path = '/app/em/analytics/' + routePath.path
        if (this.duplicateReportToggle === true) {
          this.$router.push({
            path: path,
            query: { reportId: id, duplicate: true },
          })
        } else {
          this.$router.push({ path: path, query: { reportId: id } })
        }
      }
    },

    async deleteReport(id) {
      let promptObj = {
        title: this.$t('common.wo_report.report_delete'),
        message: this.$t('common.wo_report.delete_report'),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      }
      let deleteConfirmation = await this.$dialog.confirm(promptObj)
      if (deleteConfirmation) {
        let { data, error } = await API.delete(`/v3/report/delete`, {
          reportId: id,
        })
        if (error) {
          this.$message.error(
            'Failed to delete report, please try again.',
            error
          )
        } else {
          if (data && data.errorString) {
            let confirmObj = {
              title: this.$t('common.wo_report.report_delete'),
              message: this.$t('common.wo_report.this_report_dashboard_widget'),
              rbLabel: this.$t('common.dashboard.yes_delete'),
              lbLabel: this.$t('common.dashboard.no_cancel'),
            }
            let widgetDeleteConfirm = await this.$dialog.confirm(confirmObj)
            if (widgetDeleteConfirm) {
              let resp = await API.delete('/v3/report/delete', {
                reportId: id,
                deleteWithWidget: true,
              })
              if (resp.error) {
                this.$message.error(
                  'Failed to delete report, please try again.',
                  error
                )
              } else {
                this.$emit('reportDeleted', { type: 'new', reportId: id })
                this.$message.success('Report deleted successfully')
              }
            }
          } else {
            this.$emit('reportDeleted', { type: 'new', reportId: id })
            this.$message.success('Report deleted successfully')
          }
        }
      }
    },
  },
}
</script>

<style>
.auto-created-report {
  padding-top: 10px;
  padding-bottom: 10px;
  letter-spacing: 0.3px;
  color: #333333;
  padding-left: 10px;
  padding-right: 10px;
}
.auto-created-report:hover {
  background: #f1f8fa;
  color: #39b2c2;
}
.duplicate-report-option {
  padding-top: 10px;
  padding-left: 5px;
  padding-right: 5px;
}
.duplicate-report-option:hover {
  background: #f1f8fa;
}
.reports-summary {
  background: white;
  height: 100%;
}

.reports-header .title {
  font-size: 18px;
  letter-spacing: 0.3px;
  color: #333333;
}

.reports-header .description {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #898989;
}

.reports-header {
  background: white;
  padding: 5px;
  border-bottom: solid 1px #eae9e9;
  box-shadow: 0 3px 4px 0 rgba(218, 218, 218, 0.32);
}

.report-title {
  padding: 5px 18px;
}

.report-title div {
  padding: 5px 0;
}

.report-title .title-actions {
  font-size: 16px;
}
.report-title .title-actions-fixed {
  font-size: 16px;
}
.report-title .title-actions i:not(.default) {
  display: none;
}
.report-title .title-actions img {
  display: none;
}
.report-title:hover .title-actions img {
  display: inline-block;
}

.report-title:hover .title-actions i:not(.default) {
  display: inline-block;
}

.title-actions-fixed i:not(.default) {
  display: inline-block;
}

/* .duplicate-points-popover{
  margin:50px;
} */

.report-title .title-actions i {
  padding: 0 5px;
  cursor: pointer;
}

.report-title .title-actions img {
  padding: 0 5px;
  cursor: pointer;
}

.report-title .title-actions-fixed i {
  padding: 0 5px;
  cursor: pointer;
}

.report-title .title-actions-fixed img {
  padding: 0 5px;
  cursor: pointer;
}

.report-title .title-actions img:hover {
  opacity: 1;
}

.report-title .title-actions i:hover {
  opacity: 1;
}

.report-options {
  margin: 10px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5);
  border: solid 1px #d9e0e7;
  border-radius: 5px;
}
/*
.report-options .el-button {
    margin: 2px;
} */

.reports-chart {
  text-align: center;
  min-height: 300px;
}

.reports-underlyingdata {
  padding: 24px;
  padding-top: 10px;
}
.fc-chart-btn {
  color: #333333;
  font-size: 17px;
  padding: 7px;
  padding-left: 15px;
  padding-right: 10px;
}
.title-actions .el-icon-delete {
  opacity: 0 !important;
}
.reports-header:hover .title-actions .el-icon-delete {
  opacity: 1 !important;
}
.title-actions .el-icon-edit {
  opacity: 0 !important;
}
.reports-header:hover .title-actions .el-icon-edit {
  opacity: 1 !important;
}
.chart-table-layout {
  padding-bottom: 150px;
}
.reports-summary .fc-report {
  background: #f7f8f9 !important;
}
.reports-summary .fc-report-section {
  background: #fff !important;
  box-shadow: 0 4px 10px 0 rgba(178, 178, 178, 0.18);
  min-height: 500px;
  border: solid 1px #e6ebf0;
}
.reports-summary .reports-chart {
  padding-left: 25px;
  padding-right: 25px;
  padding-top: 15px;
  padding-bottom: 40px;
}
.chart-table-layout .fc-report-section {
  position: relative;
}
.reports-summary {
  background: transparent !important;
}
.new-reports-summary {
  background: white !important;
  overflow-y: scroll;
}
.reports-summary .fc-list-view-table {
  background: white !important;
}
.reports-summary .fc-underlyingdata {
  box-shadow: 0 4px 10px 0 rgba(178, 178, 178, 0.18);
}
.reports-summary .reports-underlyingdata .table-header {
  font-size: 16px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
  padding-bottom: 35px;
}
.reports-summary .fc-list-view-table thead th {
  background: #fff;
  padding: 25px 20px;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  color: #879eb5;
}
.reports-summary .fc-list-view-table td {
  padding-left: 20px;
}
.chart-table-layout .fc-report-section .header .chart-select {
  position: absolute;
  right: 15px;
  margin-top: -14px;
  display: inline-flex;
}

/* .chart-table-layout .fc-report-section .header .chart-select.noreportdata {
  top: 10px;
} */
.chart-table-layout .fc-report-section .header {
  padding-top: 25px;
  padding-bottom: 25px;
  font-size: 18px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: center;
  color: #000000;
}
.reports-summary .chart-table-layout .fc-report-section .header {
  height: 70px;
}
.fc-report-section .header .header-content {
  margin: auto;
  width: 50%;
  overflow: hidden;
  white-space: nowrap;
}
.fc-report-filter .filter-field {
  margin-right: 5px;
}
.report-title .title {
  font-size: 18px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  padding-bottom: 0px;
  color: #000000;
}
.report-title .title .pin {
  font-size: 24px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.5px;
  text-align: left;
  color: #e65b5b;
}
.report-options .el-button + .el-button {
  margin: 0px;
}
.report-options button:first-child {
  border: none !important;
}
.report-options button {
  border-left: 1px solid rgb(217, 224, 231);
}
.report-options button:hover {
  border-left: 1px solid rgb(217, 224, 231);
}
.report-options .fc-cmp-btn {
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.2px;
  text-align: left;
  color: #615f89;
  padding: 10px;
}
.report-options .i.el-icon-date.el-icon--right {
  font-size: 15px;
}
.report-icon {
  width: 17px;
  height: 17px;
}
.nounderlinedata {
  height: 500px;
  background: #fff;
  text-align: center;
}
.nounderlinedata .content {
  margin: auto;
  padding-top: 200px;
}
.reports-summary .fc-list-view-table td {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.57;
  letter-spacing: 0.3px;
  text-align: left;
  color: #333333;
}
.reports-summary table.fc-list-view-table.fc-chart-table {
  border: 0 !important;
}
/* .reports-header:hover .title-actions  {
  padding-top: 12px;
  padding-left: 10px;
} */
.title-actions {
  padding-left: 10px;
}
.cursor-load {
  cursor: progress;
}
.report-diplicate-dialog .el-dialog__header {
  border-bottom: 1px solid #e4eaf0;
}
.report-diplicate-dialog .el-dialog {
  width: 20%;
}
.report-header-title {
  width: 100px;
  margin-left: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: stretch;
}
.report-created-time,
.fc-pdf-date-picker {
  display: none;
}
@media print {
  .report-created-time {
    display: block;
    text-align: center !important;
    padding-top: 10px !important;
  }
  .report-tab .el-tabs__header .is-top {
    display: none;
  }
  .reports-header .analytics-page-options-building-analysis {
    display: none;
  }
  .report-pdf-header .fc-widget-label {
    font-size: 18px;
  }
  .tablular-container {
    page-break-after: always;
    page-break-inside: auto;
    margin-top: 20px;
  }
  .fc-pdf-date-picker {
    display: block;
  }
}
</style>
