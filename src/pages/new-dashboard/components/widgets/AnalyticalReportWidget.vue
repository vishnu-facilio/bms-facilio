<template>
  <div>
    <FNewReport
      ref="FNewReport"
      :item="item"
      :config="config"
      :ruleInfo="ruleInfo"
      :dbTimelineFilter="dbTimelineFilter"
      :dbFilterJson="dbFilterJson"
      :loadImmediately="loadImmediately"
      :hideTimelineFilterInsideWidget="hideTimelineFilterInsideWidget"
      :componentVisibleInViewPort="componentVisibleInViewPort"
      :widgetBodyDimension="widgetBodyDimension"
      :updateWidget="updateWidget"
      :viewOrEdit="viewOrEdit"
      @reportLoaded="reportLoaded"
    ></FNewReport>
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
    <el-dialog
      title="Configure widget"
      width="50%"
      :visible.sync="showTemplateConfigDialog"
      :append-to-body="true"
    >
      <div class="height200">
        <div>
          <el-radio-group v-model="templateConfig.showOrHide">
            <el-radio :label="1">{{ $t('panel.card.show_filter') }}</el-radio>
            <el-radio :label="2">{{ $t('panel.card.hide_filter') }}</el-radio>
          </el-radio-group>
        </div>
        <div v-if="templateConfig.showOrHide === 2" class="mT30">
          <div>
            {{ $t('panel.card.pick_def_asset') }}
          </div>
          <div class="mT10">
            <el-select
              class="fc-input-full-border2"
              filterable
              v-model="templateConfig.choosenResource"
            >
              <el-option
                v-for="(resource, resourceIdx) in templateConfig.assets"
                :key="resourceIdx"
                :label="resource.name"
                :value="resource.id"
              ></el-option>
            </el-select>
          </div>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog">{{
          $t('panel.card.cancel__')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="configureWidget"
          >{{ $t('panel.card.configure') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import DashboardFilterWidgetConfigDialog from 'pages/dashboard/dashboard-filters/DashboardFilterWidgetConfigDialog'
import {
  isWebTabsEnabled,
  findRouteForTab,
  findRouteForReport,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { Message } from 'element-ui'
import FNewReport from 'src/pages/new-dashboard/components/reports/FNewReport.vue'
import BaseWidgetMixin from 'src/pages/new-dashboard/components/widgets/BaseWidgetMixin.js'
import { cloneDeep } from 'lodash'
export default {
  components: { FNewReport, DashboardFilterWidgetConfigDialog },
  mixins: [BaseWidgetMixin],
  data() {
    return {
      reportLoadedOnes: false,
      templateConfig: {
        showOrHide: 1,
        choosenResource: null,
        assets: [],
        hideHeader: false,
      },
      showTemplateConfigDialog: false,
      showDashboardFilterWidgetConfigDialog: false,
      exportDownloadUrl: null,
      ruleInfo: {},
      dbTimelineFilter: {},
      dbFilterJson: {},
    }
  },
  created() {
    this.initWidget(this.widgetConfig)
  },
  computed: {
    report() {
      const { resultObj } = this.$refs['FNewReport'] ?? {}
      const { report } = resultObj ?? {}
      return report
    },
    reportId() {
      const {
        widget: {
          dataOptions: { newReportId: reportId },
        },
      } = this ?? {}
      return reportId
    },
    id() {
      const {
        item: { id },
      } = this
      return id
    },
    widgetConfig() {
      const { id } = this
      const { report } = this
      const { type } = report ?? {}
      const self = this
      // const useReportOptions = () => {
      //   if (type == 4) {
      //     return {
      //       label: 'Report Options',
      //       action: self.reportOptions,
      //       icon: 'el-icon-document-remove',
      //     }
      //   } else {
      //     return undefined
      //   }
      // }
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
      const getExploreInAnalytics = () => {
        return !this.isPortalApp
          ? [
              {
                label: 'Explore in Analytics',
                action: this.exploreInAnalytics,
                icon: 'el-icon-data-analysis',
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
          ...getExploreInAnalytics(),
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
              this.$refs['FNewReport'].emailReportVisibility = true
            },
            icon: 'el-icon-message',
          },

          // useReportOptions(),
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
    widget() {
      const { item = {} } = this ?? {}
      const { widget = {} } = item
      return widget
    },
    config() {
      return {
        widget: this.item.widget,
      }
    },
  },
  methods: {
    reportLoaded() {
      // Update the drop down menu here...
      this.$nextTick(() => {
        if (
          this.widget?.dataOptions?.newReport?.type === 4 &&
          this.viewOrEdit == 'edit' &&
          !this.reportLoadedOnes
        ) {
          this.updateEditDropDown([
            {
              label: 'Report Options',
              icon: 'el-icon-s-data',
              action: this.showTemplateReportOptions,
            },
          ])
          this.reportLoadedOnes = true
        }
      })
    },
    configureWidget() {
      const template = cloneDeep(this.currentTemplate)
      const { showOrHide, choosenResource } = this.templateConfig
      if (showOrHide === 1) {
        template.isVisibleInDashBoard = true
      } else {
        template.isVisibleInDashBoard = false
        template.parentId = choosenResource
        template.defaultValue = choosenResource
      }
      template.chooseValues = []
      this.currentTemplate = template
      const clonedItem = cloneDeep(this.item)
      clonedItem.widget.dataOptions.reportTemplate = template
      this.updateWidget(clonedItem)
      this.showTemplateConfigDialog = false
      this.$nextTick(() => {
        this.$refs.FNewReport.init()
      })
    },
    closeDialog() {
      this.showTemplateConfigDialog = false
    },
    async loadReportTemplateValues(reportTemplate, fieldVsValueList) {
      if (reportTemplate) {
        let categoryId = reportTemplate.categoryId
        let request = {}
        if (reportTemplate.buildingId && reportTemplate.buildingId !== -1) {
          request['space'] = {
            operatorId: 38,
            value: [reportTemplate.buildingId + ''],
          }
        } else if (reportTemplate.siteId && reportTemplate.siteId !== -1) {
          request['space'] = {
            operatorId: 36,
            value: [reportTemplate.siteId + ''],
          }
        }

        if (reportTemplate.defaultValue) {
          request['id'] = {
            operatorId: 9,
            value: [reportTemplate.defaultValue + ''],
            orFilters: [
              { field: 'category', operatorId: 36, value: [categoryId + ''] },
            ],
          }
        }

        if (fieldVsValueList && fieldVsValueList.length !== 0) {
          for (let key of fieldVsValueList) {
            let values = []
            if (key.values) {
              if (typeof key.values === 'string') {
                if (key.operatorId === 5) {
                  values = key.values.split(',')
                } else {
                  values = [key.values]
                }
              } else {
                for (let val of key.values) {
                  values.push(val + '')
                }
              }
            } else {
              values = null
            }

            request[key.fieldName] = {
              operatorId: key.operatorId,
              value: values,
            }
          }
        }

        let url =
          '/asset/all?filters=' +
          encodeURIComponent(JSON.stringify(request)) +
          '&orderBy=FIELD(Assets.ID,' +
          reportTemplate.defaultValue +
          ')' +
          '&orderType=desc&overrideViewOrderBy=true'

        const {
          data: { assets },
          error,
        } = await API.get(url)
        if (isEmpty(error)) {
          reportTemplate.chooseValues = assets
        }
      }
    },
    async showTemplateReportOptions() {
      const self = this
      const {
        reportTemplate,
        newReport: { type, reportTemplate: newReportTemplate },
      } = self.widget?.dataOptions ?? {}
      if (type === 4) {
        self.currentTemplate = newReportTemplate ?? reportTemplate
        if (self.templateConfig.assets.length === 0) {
          const template = cloneDeep(self.currentTemplate)
          await self.loadReportTemplateValues(template)
          self.templateConfig.assets = template.chooseValues
        }
        self.showTemplateConfigDialog = true
      }
    },
    configureFilter() {
      this.showDashboardFilterWidgetConfigDialog = true
    },
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
    reportOptions() {
      const { report } = this.getReportObj()
      const { type } = report ?? {}
      if (type == 4) {
        type
      }

      console.log()
    },
    exploreInAnalytics() {
      const { reportId } = this
      const { reportObj, report } = this.getReportObj()
      let deafultPath = '/app/em/analytics/'
      let routePath
      let {
        ANALYTIC_PORTFOLIO,
        ANALYTIC_BUILDING,
        HEAT_MAP,
        ANALYTIC_SITE,
        TREE_MAP,
        SCATTER,
      } = pageTypes

      switch (report.analyticsType) {
        case 1:
          if (
            reportObj &&
            reportObj.options.common.filters &&
            reportObj.options.common.filters.xCriteriaMode
          ) {
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
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(routePath.pageType) || {}
        let query = { reportId: reportId, fromDashboard: true }
        name &&
          this.$router.push({
            name,
            query,
          })
      } else {
        this.$router.push({
          path: deafultPath + routePath.path,
          query: { reportId: reportId, fromDashboard: true },
        })
      }
    },
    expandReport() {
      const params = {}

      const {
        timelineFilter,
        report,
        userFilter,
        reportId,
      } = this.getReportObj()
      params['type'] = 'report'
      if (!isEmpty(userFilter)) {
        params['dbFilterJson'] = userFilter
      }
      if (!isEmpty(timelineFilter)) {
        const {
          value: [startTime, endTime],
          operatorId,
          label,
        } = timelineFilter
        params['dbTimelineFilter'] = cloneDeep({
          startTime: startTime,
          endTime: endTime,
          operatorId: operatorId,
          dateLabel: label,
          dateValueString: `${startTime},${endTime}`,
        })
      }
      params['url'] = ''
      params['alt'] = ''
      params['dashboardId'] = ''
      params['reportId'] = reportId
      params['newReport'] = reportId ? report : null
      params['target'] = ''
      this.$popupView.openPopup(params)
    },
    exportReport(fileType) {
      const fileTypeCode = Number(
        Object.keys(this.$constants.FILE_FORMAT).find(key => {
          return fileType == this.$constants.FILE_FORMAT[key]
        })
      )
      const {
        params,
        timelineFilter: {
          value: [startTime, endTime],
        },
        dateRange,
        options,
        reportId,
      } = this.getReportObj()
      const {
        widget: {
          dataOptions: { reportTemplate },
        },
      } = this
      this.$message({
        message: 'Exporting as ' + fileType,
        showClose: true,
        duration: 0,
      })
      if (fileType == 'Image') {
        params.exportParams = {
          showHeader: true,
          showPrintDetails: true,
        }
      }
      if (!isEmpty(reportTemplate) && reportTemplate != 'null') {
        params['templateString'] = reportTemplate
      }
      params['startTime'] = startTime
      params['endTime'] = endTime
      params['dateOperator'] = dateRange.operatorId
      params['dateOperatorValue'] = dateRange.value
      params['chartType'] = options.type
      params['mode'] = 1
      params['reportId'] = reportId
      params['fileFormat'] = fileTypeCode
      params['url'] = this.getLink()
      API.put('/v3/report/reading/export', params).then(response => {
        Message.closeAll()
        if (response.error) {
          this.$message.error('Error while downloading widget PDF')
        } else if (!isEmpty(response.data.fileUrl)) {
          this.exportDownloadUrl = response.data.fileUrl
        }
      })
    },

    getReportObj() {
      const {
        reportObj,
        reportObj: { dateRange, options },
        resultObj: { report },
        dateFilter: timelineFilter, // This timeline filter is present inside the widget, this is not the global timelineFilter.
      } = this.$refs['FNewReport'] ?? {}
      const { params } = reportObj ?? {}
      const { dbFilterJson: userFilter, reportId } = this ?? {}
      return cloneDeep({
        params: params ?? {},
        timelineFilter,
        userFilter,
        reportId,
        report,
        dateRange,
        options,
        reportObj,
      })
    },
    getCurrentModule() {
      // TODO:  Don't know the use of this, this needs to be rewritten,
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
    getModuleName() {
      const { report } = this.getReportObj()
      const currentModule = this.getCurrentModule()
      if (!isEmpty(report?.module)) {
        return report?.module?.name
      } else {
        return currentModule?.module
      }
    },
    getModulePath(moduleName) {
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
        return '/app/wo'
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
        return '/app/fa'
      } else if (moduleName === 'energydata') {
        return '/app/em'
      } else if (
        ['inspectionTemplate', 'inspectionResponse'].includes(moduleName)
      ) {
        return '/app/inspection'
      } else if (['asset', 'assetbreakdown', 'vendors'].includes(moduleName)) {
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
        return '/app/tm'
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
        return '/app/ct'
      } else if (
        ['visitorlog', 'visitor', 'invitevisitor', 'watchlist'].includes(
          moduleName
        )
      ) {
        modulePath = '/app/vi'
      } else if (['serviceRequest'].includes(moduleName)) {
        return '/app/sr'
      } else if (
        [
          'purchaseorder',
          'purchaseorderlineitems',
          'purchaserequest',
          'poterms',
          'purchaserequestlineitems',
        ].includes(moduleName)
      ) {
        return '/app/purchase'
      } else if (['budget', 'budgetamount'].includes(moduleName)) {
        return '/app/ac'
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
        return '/app/inventory'
      } else {
        const { report } = this.getReportObj()
        const currentModule = this.getCurrentModule()
        if (report?.module?.custom) {
          return '/app/ca'
        } else if (currentModule.custom) {
          return '/app/ca'
        } else {
          return '/app/at'
        }
      }
    },
    getRoute(moduleName) {
      if (!moduleName) {
        return findRouteForReport('analytics_reports', pageTypes.REPORT_VIEW)
          ?.name
      } else if (moduleName === 'energydata') {
        return findRouteForReport('analytics_reports', pageTypes.REPORT_VIEW, {
          moduleName,
        })?.name
      } else {
        return findRouteForReport('module_reports', pageTypes.REPORT_VIEW, {
          moduleName,
        })?.name
      }
    },
    getLink() {
      const moduleName = this.getModuleName()
      const name = this.getRoute(moduleName)
      return this.$router.resolve({ name }).href + '/' + this.reportId
    },
    goToReport() {
      const moduleName = this.getModuleName()
      if (isWebTabsEnabled() && !isEmpty(this.reportId)) {
        const name = this.getRoute(moduleName)
        if (!isEmpty(name)) {
          const params = { reportid: this.reportId }
          this.$router.push({ name, params })
        } else {
          this.$dialog.confirm({
            title: this.$t('common._common.tab_not_configured'),
            message: this.$t('common._common.tab_not_configured_message'),
            rbLabel: this.$t('common._common.ok'),
            lbHide: true,
          })
        }
      } else {
        const { reportId: id } = this.widget?.dataOptions ?? {}
        if (this.$helpers.isEtisalat()) {
          let url = '/app/em/modulereports/newview/' + reportId
          this.$router.push(url)
        }
        if (!isEmpty(this.reportId)) {
          const modulePath = this.getModulePath(moduleName)
          const url = modulePath + '/reports/newview/' + this.reportId
          this.$router.push(url)
        } else if (!isEmpty(id)) {
          const url = modulePath + '/reports/view/' + id
          this.$router.push(url)
        }
      }
    },
  },
  props: {
    viewOrEdit: {
      type: String,
      default: 'view',
    },
    updateWidget: {
      type: Function,
    },
    widgetBodyDimension: {
      type: Object,
      required: true,
    },
    hideTimelineFilterInsideWidget: {
      type: Boolean,
      required: true,
      default: false,
    },
    item: {
      type: Object,
      required: true,
      default: () => ({}),
    },
    loadImmediately: {
      type: Boolean,
      default: false,
      required: true,
    },
    componentVisibleInViewPort: {
      type: Boolean,
      required: true,
      default: false,
    },

    // config:undefined
    // datePickerTarget:undefined
    // hideChartSettings:undefined
    // hideTabularReport:undefined
    // isVisibleInViewport:undefined
    // qs:undefined
    // showTimePeriod:undefined
    // tabular:undefined
    // templateJson:undefined
  },
}
</script>

<style lang="scss" scoped></style>
