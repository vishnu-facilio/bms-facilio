<template>
  <div class="pivot-table-wrapper height100">
    <PivotTable
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
      :isInfoIconVisible="isInfoIconVisible"
      :diable_userfilter_drillown="diable_userfilter_drillown"
      ref="pivotTable"
      @sortChanged="sortChanged"
      @pickerChanged="pickerChanged"
    >
    </PivotTable>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import PivotTable from './PivotTable'
import { isEqual } from 'lodash'
import NewDateHelper from '@/mixins/NewDateHelper'
import './pivot.scss'

export default {
  components: {
    PivotTable,
  },
  data() {
    return {
      loading: true,
      initialLoading: true,
      theme: null,
      columnFormatting: null,
      sortBy: null,
      pivotTable: null,
      dateOperator: null,
      dateValue: null,
      showTimelineFilter: false,
      startTime: null,
      endTime: null,
      fetchPivotDataError: false,
      diable_userfilter_drillown: false,
    }
  },
  props: [
    'reportId',
    'dbFilterJson',
    'dbTimelineFilter',
    'dashbord',
    'config',
    'widgetDatePicker',
    'isMobileDashboard',
    'isLazyDashboard',
    'isVisibleInViewport',
  ],
  mounted() {
    if (
      (this.isLazyDashboard && !this.isVisibleInViewport) ||
      this.dbFilterJson ||
      this.dbTimelineFilter
    ) {
      return
    }
    this.executeReport(true)
  },
  computed: {
    isInfoIconVisible() {
      return this?.config?.widget?.widgetSettings?.showHelpText
    },
    allFilters() {
      return {
        dbFilterJson: this.dbFilterJson,
        dbTimelineFilter: this.dbTimelineFilter,
      }
    },
  },
  watch: {
    isVisibleInViewport(val) {
      if (val) {
        this.init(true)
      }
    },
    allFilters: {
      handler: function(newVal, oldVal) {
        if (!newVal) {
          // This watcher gets invoked `immediately` and might container `undefined`.
          this.init(true)
          return
        } else if (isEqual(newVal, oldVal)) {
          return
        }
        this.startTime = this.dbTimelineFilter?.startTime
        this.endTime = this.dbTimelineFilter?.endTime
        this.dateOperator = this.dbTimelineFilter?.operatorId
        this.dateValue = this.dbTimelineFilter?.dateValueString
        this.init()
      },
      immediate: true, // Don't know why this watcher is invoked immediately.
    },
  },
  methods: {
    init(initial) {
      if (this.isLazyDashboard && !this.isVisibleInViewport) {
        return
      }
      this.executeReport(initial)
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
      this.$emit('timelineFilterChange', dateObj)
      this.executeReport()
    },
    getDatePickerObj() {
      this.datePickerObj = NewDateHelper.getDatePickerObject(
        this.dateOperator,
        this.dateValue
      )
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

      if (this.dbFilterJson) {
        params['filters'] = JSON.stringify(this.dbFilterJson)
      }

      if (this.sortBy) {
        params['sortBy'] = this.sortBy
      }

      return params
    },
    async executeReport(initial) {
      this.loading = true
      let params = this.buildPivotParams()
      let self = this
      let resp = await API.post('v2/report/executePivotReport', params)
      let { data, error } = resp
      if (error) {
        this.fetchPivotDataError = true
        this.loading = false
        return
      }
      if (!this.theme) {
        this.theme = data.pivotTemplateJSON.theme
      }
      if (!this.columnFormatting) {
        this.columnFormatting = data.pivotTemplateJSON.columnFormatting
      }
      if('diable_userfilter_drillown' in data.pivotTemplateJSON ){
        this.diable_userfilter_drillown = data.pivotTemplateJSON.diable_userfilter_drillown
      }

      if (!this.sortBy) {
        this.sortBy = data.sorting
      }
      this.showTimelineFilter = data.showTimelineFilter
      if (initial && !this.dbTimelineFilter) {
        this.dateOperator = data.dateOperator
        this.dateValue = data.dateOperatorValue
      }

      this.pivotTable = { ...data }
      this.loading = false
      this.getDatePickerObj()
      this.$emit('pivotReportLoaded', data.report, self.datePickerObj) //reportObj
      this.$emit('reportLoaded', data.report, data)
    },
  },
}
</script>

<style lang="scss" scoped></style>
