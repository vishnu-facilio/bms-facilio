<template>
  <div class="pivot-table-wrapper height100">
    <PivotTable
      :printMode="printMode"
      :loading="loading"
      :columnFormatting="columnFormatting"
      :theme="theme"
      :sortBy="sortBy"
      :pivotTable="pivotTable"
      :showTimelineFilter="showTimelineFilter"
      :dateOperator="dateOperator"
      :dateValue="dateValue"
      :dashbord="dashbord"
      :config="config"
      :isMobileDashboard="isMobileDashboard"
      :widgetDatePicker="widgetDatePicker"
      :fetchPivotDataError="fetchPivotDataError"
      :updateWidget="updateWidget"
      :item="item"
      :diable_userfilter_drillown="diable_userfilter_drillown"
      ref="pivotTable"
      @sortChanged="sortChanged"
      @pickerChanged="pickerChanged"
    >
    </PivotTable>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none"
    ></iframe>
  </div>
</template>
<script>
import { Message } from 'element-ui'
import { cloneDeep } from 'lodash'
import { isEmpty } from '@facilio/utils/validation'
import BaseWidgetMixin from 'src/pages/new-dashboard/components/widgets/BaseWidgetMixin.js'
import { API } from '@facilio/api'
import PivotTable from './PivotTable'
import { isEqual } from 'lodash'
import NewDateHelper from '@/mixins/NewDateHelper'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import './pivot.scss'
export default {
  components: {
    PivotTable,
  },
  mixins: [BaseWidgetMixin],
  data() {
    return {
      exportDownloadUrl: '',
      loading: true,
      initialLoading: true,
      theme: null,
      columnFormatting: null,
      sortBy: null,
      pivotTable: {},
      dateOperator: null,
      dateValue: null,
      showTimelineFilter: false,
      startTime: null,
      endTime: null,
      fetchPivotDataError: false,
      dbFilterJson: {},
      dbTimelineFilter: {},
      dateLabel: '',
      diable_userfilter_drillown: false,
    }
  },
  props: {
    printMode: { type: Boolean, default: false },
    dashbord: {},
    item: {
      type: Object,
      required: true,
    },
    widgetDatePicker: {},
    isMobileDashboard: {},
    componentVisibleInViewPort: {
      type: Boolean,
      default: true,
    },
    updateWidget: {
      type: Function,
    },
  },
  created() {
    this.initWidget(this.widgetConfig)
  },
  mounted() {
    this.init()
  },
  computed: {
    allFilters() {
      return {
        dbFilterJson: this.dbFilterJson,
        dbTimelineFilter: this.dbTimelineFilter,
      }
    },
    id() {
      const {
        item: { id },
      } = this
      return id
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
        showDatePickerInHeader: true,
        alwaysShowWidgetControls: true,
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
        ],
      }
    },
    widget() {
      const {
        item: { widget },
      } = this
      return widget
    },
    reportId() {
      const {
        widget: {
          dataOptions: { newReportId },
        },
      } = this
      return newReportId
    },
    config() {
      const {
        item: { widget },
      } = this
      return {
        widget: widget,
      }
    },
  },
  watch: {
    allFilters(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        const { dbTimelineFilter } = newVal ?? {}
        if (!isEmpty(dbTimelineFilter)) {
          const {
            startTime,
            endTime,
            dateOperator,
            dateValueString,
          } = dbTimelineFilter
          this.startTime = startTime
          this.endTime = endTime
          this.dateOperator = dateOperator
          this.dateValue = dateValueString
        }
        this.init()
      }
    },
    componentVisibleInViewPort(componentVisibleInViewPort) {
      if (componentVisibleInViewPort) {
        this.init()
      }
    },
  },
  methods: {
    updateItem(report) {
      // This is lame, but it workings.
      const item = cloneDeep(this.item)
      item.widget.dataOptions.newReport = report
      this.updateWidget(item)
    },
    expandReport() {
      let params = {}
      params['type'] = 'pivot'
      params['dbFilterJson'] = this.dbFilterJson
      params['dbTimelineFilter'] = {
        startTime: this.startTime,
        endTime: this.endTime,
        operatorId: this.dateOperator,
        dateLabel: this.dateLabel,
        dateValueString: `${this.startTime},${this.endTime}`,
        dateField: this.dbTimelineFilter?.dateField, // There is no `dateField` in this.dbTimelineFilter, don't know what error this will cause.
      }
      params['url'] = ''
      params['alt'] = ''
      params['dashboardId'] = ''
      params['reportId'] = this.widget.dataOptions.newReportId
      params['newReport'] = this.widget.dataOptions.newReportId
        ? this.widget.dataOptions.newReport
        : null
      params['target'] = ''
      this.$popupView.openPopup(params)
    },
    goToReport() {
      const {
        widget: {
          dataOptions: { reportType, newReportId },
        },
      } = this
      if (reportType === 5) {
        // `reportType` = 5 is pivot report.
        let path = `/app/em/pivot/view/${newReportId}`
        if (isWebTabsEnabled()) {
          let { name } = findRouteForTab(pageTypes.PIVOT_VIEW) ?? {}
          path = this.$router.resolve({
            name,
            query: { reportId: newReportId },
          }).href
          if (isEmpty(name)) {
            this.$dialog.confirm({
              title: this.$t('common._common.tab_not_configured'),
              message: this.$t('common._common.tab_not_configured_message'),
              rbLabel: this.$t('common._common.ok'),
              lbHide: true,
            })
            return
          }
        }
        this.$router.push({ path: path })
      }
    },
    editReport() {
      const {
        widget: {
          dataOptions: { newReportId },
        },
      } = this

      let path = `/app/em/pivotbuilder/new?reportId=${newReportId}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_FORM)
        path = this.$router.resolve({
          name,
          query: { reportId: newReportId },
        }).href
      }
      this.$router.push({ path: path })
    },
    exportReport(fileType = 'Excel') {
      const fileTypeCode = Number(
        Object.keys(this.$constants.FILE_FORMAT).find(key => {
          return fileType == this.$constants.FILE_FORMAT[key]
        })
      )
      const {
        pivotTable: {
          report: { params = {} },
        },
        reportId,
      } = this
      this.$message({
        message: 'Exporting as ' + fileType,
        showClose: true,
        duration: 0,
      })
      params['reportId'] = reportId
      params['fileFormat'] = fileTypeCode
      const { startTime, endTime, dbTimelineFilter, dbFilterJson } = this
      if (!isEmpty(startTime) && !isEmpty(endTime)) {
        params['startTime'] = startTime
        params['endTime'] = endTime
      } else if (!isEmpty(dbTimelineFilter)) {
        const { startTime, endTime } = dbTimelineFilter
        params['startTime'] = startTime
        params['endTime'] = endTime
      }
      if (!isEmpty(dbFilterJson)) {
        params['filters'] = JSON.stringify(dbFilterJson)
      }

      API.get('/v2/report/exportPivotReport', params).then(
        ({ data, error }) => {
          Message.closeAll()
          if (!error) {
            this.exportDownloadUrl = data.fileUrl
          }
        }
      )
    },
    init() {
      if (!this.componentVisibleInViewPort) {
        return
      }
      this.executeReport()
    },
    sortChanged(newSortBy) {
      this.sortBy = newSortBy
      this.executeReport()
    },
    pickerChanged(dateObj) {
      this.startTime = dateObj.value[0]
      this.endTime = dateObj.value[1]
      this.dateValue = `${this.startTime}, ${this.endTime}`
      this.dateOperator = dateObj.operatorId
      this.dateLabel = dateObj.label
      this.$emit('timelineFilterChange', dateObj)
      this.executeReport()
    },
    buildPivotParams() {
      let params = {}
      if (this.startTime > 0 && this.endTime > 0) {
        params['startTime'] = this.startTime
        params['endTime'] = this.endTime
      }

      if (this.reportId) {
        params['reportId'] = this.reportId
      }

      if (!isEmpty(this.dbFilterJson)) {
        params['filters'] = JSON.stringify(this.dbFilterJson)
      }

      if (this.sortBy) {
        params['sortBy'] = this.sortBy
      }

      return params
    },
    async executeReport() {
      this.loading = true
      let params = this.buildPivotParams()
      const self = this
      await API.cancel({ uniqueKey: this.reportId + '' })
      API.get('v2/report/executePivotReport', params, {
        force: true,
        uniqueKey: this.reportId + '',
      }).then(resp => {
        const { data, error } = resp
        if (error) {
          if (error.isCancelled) {
            return
          }
          self.fetchPivotDataError = true
          self.loading = false
          return
        }
        if (!self.theme) {
          self.theme = data.pivotTemplateJSON.theme
        }
        if (!self.columnFormatting) {
          self.columnFormatting = data.pivotTemplateJSON.columnFormatting
        }
        if ('diable_userfilter_drillown' in data.pivotTemplateJSON) {
          this.diable_userfilter_drillown =
            data.pivotTemplateJSON.diable_userfilter_drillown
        }
        if (!self.sortBy) {
          self.sortBy = data.sorting
        }
        self.showTimelineFilter = data.showTimelineFilter
        if (isEmpty(self.dbTimelineFilter) && !self.dateOperator) {
          // Global timeline filter absent and local timeline filter not set.
          self.dateOperator = data.dateOperator
          self.dateValue = data.dateOperatorValue
        }
        self.pivotTable = data
        self.loading = false
        self.$emit('pivotReportLoaded', data.report) //reportObj
        self.$emit('reportLoaded', data.report, data)
        this.updateItem(data.report)
      })
    },
  },
}
</script>

<style lang="scss" scoped></style>
