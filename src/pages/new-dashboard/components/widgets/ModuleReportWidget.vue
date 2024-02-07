<template>
  <div>
    <ModuleNewReport
      ref="ModuleNewReport"
      :id="id"
      :item="item"
      :dbCustomScriptFilter="dbCustomScriptFilter"
      :dbFilterJson="dbFilterJson || {}"
      :dbTimelineFilter="dbTimelineFilter || {}"
      :ruleInfo="ruleInfo || {}"
      :hideTimelineFilterInsideWidget="hideTimelineFilterInsideWidget"
      :loadImmediately="loadImmediately"
      :updateWidget="updateWidget"
      :componentVisibleInViewPort="componentVisibleInViewPort"
      :widgetBodyDimension="widgetBodyDimension"
      :config="config"
      @action="onAction"
    ></ModuleNewReport>
    <!-- onAction() is inside of a mixin. -->
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none"
    ></iframe>
    <dashboard-filter-widget-config-dialog
      v-if="showDashboardFilterWidgetConfigDialog"
      :visibility.sync="showDashboardFilterWidgetConfigDialog"
      :widget="widget"
      @widgetFilterConfigChange="saveWidgetFilterSettings"
    >
    </dashboard-filter-widget-config-dialog>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { Message } from 'element-ui'
import BaseWidgetMixin from 'src/pages/new-dashboard/components/widgets/BaseWidgetMixin.js'
import ModuleNewReport from 'src/pages/new-dashboard/components/reports/ModuleNewReport.vue'
import { cloneDeep } from 'lodash'
import {
  isWebTabsEnabled,
  findRouteForReport,
  pageTypes,
} from '@facilio/router'
import DashboardFilterWidgetConfigDialog from 'pages/dashboard/dashboard-filters/DashboardFilterWidgetConfigDialog'
import ReportHelper from 'pages/report/mixins/ReportHelper'
export default {
  components: { ModuleNewReport, DashboardFilterWidgetConfigDialog },
  mixins: [BaseWidgetMixin, ReportHelper],
  props: {
    updateWidget: {
      type: Function,
    },
    widgetBodyDimension: {
      type: Object,
      required: true,
    },
    loadImmediately: {
      type: Boolean,
      required: true,
      default: false,
    },
    id: {
      type: String,
      required: true,
    },
    item: {
      type: Object,
      required: true,
    },
    hideTimelineFilterInsideWidget: {
      type: Boolean,
      required: true,
      default: false,
    },
    componentVisibleInViewPort: {
      type: Boolean,
      default: true,
    },
  },
  computed: {
    widget() {
      const { item = {} } = this ?? {}
      const { widget = {} } = item
      return widget
    },
    newReport() {
      const {
        widget: {
          dataOptions: { newReport },
        },
      } = this ?? {}
      return newReport
    },
    reportId() {
      const {
        widget: {
          dataOptions: { newReportId: reportId },
        },
      } = this ?? {}
      return reportId
    },
    config() {
      const {
        item: { widget },
      } = this
      return {
        widget: widget,
      }
    },
    widgetConfig() {
      const { id } = this ?? {}
      const getGoToReport = () => {
        return !this.isPortalApp
          ? [
              {
                label: 'Go to Report',
                action: this.goToReport,
                icon: 'el-icon-top-right',
              },
            ]
          : []
      }
      const getEditReport = () => {
        return !this.isPortalApp
          ? [
              {
                label: 'Edit Report',
                action: this.editReport,
                icon: 'el-icon-edit',
              },
              {
                isLineBreak: true,
              },
            ]
          : []
      }
      return {
        id: id,
        minW: 25,
        maxW: 96,
        minH: 10,
        maxH: 50,
        showHeader: true,
        showExpand: true,
        noResize: false,
        showDropDown: true,
        editMenu: [],
        borderAroundWidget: true,
        viewMenu: [
          ...getGoToReport(),
          ...getEditReport(),
          {
            label: 'Export as CSV',
            action: () => this.exportReport('CSV'),
            icon: 'el-icon-download',
          },
          {
            label: 'Export as Excel',
            action: () => this.exportReport('Excel'),
            icon: 'el-icon-download',
          },
          {
            label: 'Export as PDF',
            action: () => this.exportReport('PDF'),
            icon: 'el-icon-download',
          },
          {
            label: 'Export as Image',
            action: () => this.exportReport('Image'),
            icon: 'el-icon-download',
          },
          {
            isLineBreak: true,
          },
          {
            label: 'Email this Report',
            action: () => {
              this.$refs['ModuleNewReport'].emailReportVisibility = true
            },
            icon: 'el-icon-message',
          },
        ],
        configFilter: [
          {
            label: 'Configure filter',
            action: () => this.configureFilter(),
            icon: 'el-icon-setting',
          },
        ],
      }
    },
  },
  created() {
    this.initWidget(this.widgetConfig)
  },
  data() {
    return {
      showDashboardFilterWidgetConfigDialog: false,
      dbFilterJson: {},
      ruleInfo: {},
      dbTimelineFilter: {},
      exportDownloadUrl: null,
    }
  },
  methods: {
    saveWidgetFilterSettings(widget) {
      this.showDashboardFilterWidgetConfigDialog = false
      let data = {
        widgets: [
          {
            id: widget.id,
            widgetSettingsJsonString: JSON.stringify(widget.widgetSettings),
          },
        ],
      }
      API.post('v2/dashboardFilter/updateWidgetSettings', data).then(resp => {
        let { error } = resp
        if (error) {
          this.$message('error updating widget-filter settings')
        } else {
          const clonedItem = cloneDeep(this.item)
          clonedItem.widget.widgetSettings.excludeDbFilters =
            widget?.widgetSettings?.excludeDbFilters ?? false
          this.updateWidget(clonedItem)
        }
      })
    },
    configureFilter() {
      this.showDashboardFilterWidgetConfigDialog = true
    },
    getCurrentModule() {
      // TODO: Don't know why all this is written, please help.
      let routeObj = this.$route
      let module = null
      let rootPath = null
      if (routeObj.meta.module) {
        module = routeObj.meta.module
        rootPath = routeObj.path
      } else {
        if (routeObj.matched) {
          for (let matchedRoute of routeObj.matched) {
            if (matchedRoute.meta.module) {
              module = matchedRoute.meta.module
              rootPath = matchedRoute.path
              break
            }
          }
        }
      }
      return {
        module: module,
        rootPath: rootPath,
      }
    },
    editReport() {
      // TODO: Don't know why all this is written, please help.
      const {
        widget: {
          dataOptions: { newReportId: reportId },
        },
      } = this
      const { report } = this.getReportObj()
      let moduleName = report.module.name
      let isCustomModule = report.module.custom
      let pageTypeModule = report.module.name
      if (ReportHelper.isHiddenModule(pageTypeModule)) {
        pageTypeModule = ReportHelper.findModuleFromSubmodule(pageTypeModule)
      }
      if (isWebTabsEnabled()) {
        pageTypeModule = pageTypeModule == 'alarm' ? 'newreadingalarm': pageTypeModule
        let { name } =
          findRouteForReport('module_reports', pageTypes.REPORT_FORM, {
            moduleName: pageTypeModule,
          }) || {}
        let query = {
          reportId: reportId,
          module: moduleName,
          fromDashboard: true,
        }
        if (name) {
          this.$router.push({
            name,
            query,
          })
        }
        return
      }
      if (this.$helpers.isEtisalat()) {
        this.$router.push({
          path: '/app/em/modulereports/new',
          query: {
            reportId: reportId,
            fromDashboard: true,
            module: moduleName,
          },
        })
        return
      }
      switch (moduleName) {
        case 'workorder':
        case 'workorderLabour':
        case 'workorderCost':
        case 'workorderItem':
        case 'workorderTools':
        case 'workorderService':
        case 'workorderTimeLog':
        case 'workorderHazard':
        case 'plannedmaintenance':
          this.$router.push({
            path: '/app/wo/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: moduleName,
            },
          })
          break
        case 'purchaseorder':
        case 'poterms':
        case 'purchaseorderlineitems':
        case 'purchaserequest':
        case 'purchaserequestlineitems':
          this.$router.push({
            path: '/app/purchase/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: moduleName,
            },
          })
          break
        case 'inspectionTemplate':
        case 'inspectionResponse':
          this.$router.push({
            path: '/app/inspection/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: moduleName,
            },
          })
          break
        case 'budget':
        case 'budgetamount':
          this.$router.push({
            path: '/app/ac/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: moduleName,
            },
          })
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
          this.$router.push({
            path: '/app/inventory/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: moduleName,
            },
          })
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
          this.$router.push({
            path: '/app/fa/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: this.$helpers.isLicenseEnabled('NEW_ALARMS')
                ? moduleName
                : 'alarm',
            },
          })
          break
        case 'serviceRequest':
          this.$router.push({
            path: '/app/sr/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: this.$helpers.isLicenseEnabled('ServiceRequest')
                ? moduleName
                : 'serviceRequest',
            },
          })
          break
        case 'asset':
          this.$router.push({
            path: '/app/at/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: 'asset',
            },
          })
          break
        case 'visitor':
        case 'visitorlog':
        case 'invitevisitor':
        case 'watchlist':
          this.$router.push({
            path: '/app/vi/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: moduleName,
            },
          })
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
          this.$router.push({
            path: '/app/tm/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: moduleName,
            },
          })
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
          this.$router.push({
            path: '/app/ct/reports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: moduleName,
            },
          })
          break
        default:
          if (isCustomModule) {
            this.$router.push({
              path: '/app/ca/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
          } else {
            this.$router.push({
              path: '/app/at/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
          }
          break
      }
      return
    },
    goToReport(exporturl) {
      // TODO: This method requires a complete rewrite, I don't
      // what it does... Please help.
      const {
        widget,
        widget: {
          dataOptions: { reportId: id },
        },
      } = this
      const { report } = this.getReportObj()
      if (widget?.dataOptions?.reportType === 5) {
        this.$router.push(
          `/app/em/pivot/view/${widget?.dataOptions?.newReportId}`
        )
        return
      }
      let currentModule = this.getCurrentModule()
      if (widget.type === 'view') {
        let modulePath = ''
        if (widget.dataOptions.moduleName === 'workorder') {
          modulePath = '/app/wo/orders/' + widget.dataOptions.viewName
        } else if (
          widget.dataOptions.moduleName === 'alarm' ||
          widget.dataOptions.moduleName === 'alarmoccurrence' ||
          widget.dataOptions.moduleName === 'readingalarmoccurrence' ||
          widget.dataOptions.moduleName === 'mlalarmoccurrence' ||
          widget.dataOptions.moduleName === 'violationalarmoccurrence' ||
          widget.dataOptions.moduleName === 'baseevent' ||
          widget.dataOptions.moduleName === 'newreadingalarm'
        ) {
          modulePath = '/app/fa/faults/' + widget.dataOptions.viewName
        }
        this.$router.push(modulePath)
      } else {
        let moduleName = null
        let modulePath = null
        if (!isEmpty(report)) {
          moduleName = report.module.name
        } else {
          moduleName = currentModule.module
        }
        if (ReportHelper.isHiddenModule(moduleName)) {
          moduleName = ReportHelper.findModuleFromSubmodule(moduleName)
        }
        if (isWebTabsEnabled() && widget.dataOptions.newReportId) {
          moduleName = moduleName == 'alarm' ? 'newreadingalarm': moduleName
          let routeObj
          if (!moduleName) {
            routeObj = findRouteForReport(
              'analytics_reports',
              pageTypes.REPORT_VIEW
            )
          } else if (moduleName === 'energydata') {
            routeObj = findRouteForReport(
              'analytics_reports',
              pageTypes.REPORT_VIEW,
              { moduleName }
            )
          } else {
            routeObj = findRouteForReport(
              'module_reports',
              pageTypes.REPORT_VIEW,
              { moduleName }
            )
          }

          let { name } = routeObj || {}
          let params = { reportid: widget.dataOptions.newReportId }
          if (exporturl === 'exportpdf') {
            return (
              this.$router.resolve({ name }).href +
              '/' +
              widget.dataOptions.newReportId
            )
          }
          if (!isEmpty(name)) {
            this.$router.push({ name, params })
          } else {
            this.$dialog.confirm({
              title: this.$t('common._common.tab_not_configured'),
              message: this.$t('common._common.tab_not_configured_message'),
              rbLabel: this.$t('common._common.ok'),
              lbHide: true,
            })
          }
          return
        }
        if (
          [
            'workorder',
            'workorderLabour',
            'workorderCost',
            'workorderItem',
            'workorderTools',
            'workorderService',
            'workorderTimeLog',
            'workorderHazard',
            'plannedmaintenance',
          ].includes(moduleName)
        ) {
          modulePath = '/app/wo'
        } else if (
          [
            'alarm',
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
          ].includes(moduleName)
        ) {
          modulePath = '/app/fa'
        } else if (moduleName === 'energydata') {
          modulePath = '/app/em'
        } else if (
          ['inspectionTemplate', 'inspectionResponse'].includes(moduleName)
        ) {
          modulePath = '/app/inspection'
        } else if (
          ['asset', 'assetbreakdown', 'vendors'].includes(moduleName)
        ) {
          modulePath = '/app/at'
        } else if (
          [
            'tenant',
            'tenantcontact',
            'tenantunit',
            'quote',
            'contact',
            'tenantspaces',
            'quotelineitems',
            'quoteterms',
            'people',
            'newsandinformationsharing',
            'neighbourhoodsharing',
            'dealsandofferssharing',
            'contactdirectorysharing',
            'admindocumentsharing',
            'audienceSharing',
          ].includes(moduleName)
        ) {
          modulePath = '/app/tm'
        } else if (
          [
            'contracts',
            'purchasecontracts',
            'labourcontracts',
            'warrantycontracts',
            'rentalleasecontracts',
            'purchasecontractlineitems',
            'labourcontractlineitems',
            'warrantycontractlineitems',
            'rentalleasecontractlineitems',
          ].includes(moduleName)
        ) {
          modulePath = '/app/ct'
        } else if (
          ['visitorlog', 'visitor', 'invitevisitor', 'watchlist'].includes(
            moduleName
          )
        ) {
          modulePath = '/app/vi'
        } else if (['serviceRequest'].includes(moduleName)) {
          modulePath = '/app/sr'
        } else if (
          [
            'purchaseorder',
            'purchaseorderlineitems',
            'purchaserequest',
            'poterms',
            'purchaserequestlineitems',
          ].includes(moduleName)
        ) {
          modulePath = '/app/purchase'
        } else if (['budget', 'budgetamount'].includes(moduleName)) {
          modulePath = '/app/ac'
        } else if (
          [
            'item',
            'tool',
            'itemTransactions',
            'tootTransactions',
            'itemTypes',
            'toolTypes',
            'storeRoom',
            'shipment',
            'transferrequest',
            'transferrequestshipmentreceivables',
            'transferrequestpurchaseditems',
          ].includes(moduleName)
        ) {
          modulePath = '/app/inventory'
        } else {
          if (report !== undefined && report.module.custom) {
            modulePath = '/app/ca'
          } else if (currentModule.custom) {
            modulePath = '/app/ca'
          } else {
            modulePath = '/app/at'
          }
        }
        if (this.$helpers.isEtisalat()) {
          let url =
            '/app/em/modulereports/newview/' + widget.dataOptions.newReportId
          this.$router.push(url)
          return
        }
        if (widget.dataOptions.newReportId) {
          let url =
            modulePath + '/reports/newview/' + widget.dataOptions.newReportId
          this.$router.push(url)
        } else if (id) {
          let url = modulePath + '/reports/view/' + id
          if (
            this.$route.params &&
            this.$route.params.dashboardlink === 'buildingdashboard' &&
            this.$route.params.buildingid
          ) {
            let data = { buildingid: parseInt(this.$route.params.buildingid) }
            this.$router.push({
              path: url,
              query: { reportSpaceFilterContext: JSON.stringify(data) },
            })
          } else if (
            this.$route.params &&
            (this.$route.params.dashboardlink === 'chillerplant' ||
              this.$route.params.dashboardlink === 'chillers') &&
            this.$route.params.buildingid
          ) {
            let data = { chillerId: parseInt(this.$route.params.buildingid) }
            this.$router.push({
              path: url,
              query: { reportSpaceFilterContext: JSON.stringify(data) },
            })
          } else {
            this.$router.push(url)
          }
        }
      }
    },
    exportReport(fileType) {
      const self = this
      const fileTypeCode = Number(
        Object.keys(this.$constants.FILE_FORMAT).find(key => {
          return fileType == this.$constants.FILE_FORMAT[key]
        })
      )
      const { params, timelineFilter, userFilter, reportId } =
        this.getReportObj() ?? {}
      this.$message({
        message: `Exporting as ${fileType}`,
        showClose: true,
        duration: 0,
      })
      if (fileType === 'Image') {
        params['exportParams'] = {
          showHeader: true,
          showPrintDetails: true,
        }
      }
      params['fileFormat'] = fileTypeCode
      params['reportId'] = reportId
      params['url'] = this.goToReport('exportpdf')
      if (!isEmpty(timelineFilter)) {
        const {
          value: [startTime, endTime],
        } = timelineFilter ?? {}
        params['startTime'] = startTime
        params['endTime'] = endTime
      }
      if (!isEmpty(userFilter)) {
        params['filters'] = JSON.stringify(userFilter)
      }
      API.post('/v2/report/exportModuleReport', params).then(res => {
        Message.closeAll()
        const {
          data: { fileUrl },
        } = res
        self.exportDownloadUrl = fileUrl
      })
    },
    expandReport() {
      const params = {}
      const { timelineFilter, report: newReport } = this.getReportObj() ?? {}
      const { dbFilterJson: userFilter, reportId } = this ?? {}
      params['type'] = 'report'
      params['dbFilterJson'] = userFilter
      params['url'] = ''
      params['alt'] = ''
      params['dashboardId'] = ''
      params['reportId'] = reportId
      params['newReport'] = newReport
      params['target'] = ''
      if (!isEmpty(timelineFilter)) {
        const { startTime, endTime, operatorId, label, dateField } =
          timelineFilter ?? {}
        params['dbTimelineFilter'] = {
          startTime: startTime,
          endTime: endTime,
          operatorId: operatorId,
          dateLabel: label,
          dateValueString: `${startTime},${endTime}`,
          dateField: dateField,
        }
      } else {
        params['dbTimelineFilter'] = ''
      }
      this.$popupView.openPopup(params)
    },
    getReportObj() {
      const {
        reportObj,
        resultObj: { report },
        dateFilter: timelineFilter, // This timeline filter is present inside the widget, this is not the global timelineFilter.
      } = this.$refs['ModuleNewReport'] ?? {}
      const { params } = reportObj ?? {}
      const { dbFilterJson: userFilter, reportId } = this ?? {}
      return cloneDeep({
        params: params ?? {},
        timelineFilter,
        userFilter,
        reportId,
        report,
      })
    },
  },
}
</script>

<style lang="css" scoped></style>
