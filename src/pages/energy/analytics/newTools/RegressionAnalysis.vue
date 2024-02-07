<template>
  <div class="regression-analysis-page">
    <div class="row regression-analysis">
      <div class="col-12">
        <div class="fc-report-header-2">
          <div class="pull-left report-title-section">
            <div class="fc-black3-16 pT15">
              {{ $t('common.header.regression_analysis') }}
            </div>
          </div>
          <div class="pull-right report-btn-section">
            <f-report-options
              :savedReport.sync="savedReport"
              :config="configFromReport ? configFromReport : serverConfig"
              :moduleName="moduleName"
              optionClass="analytics-page-options"
              :optionsToEnable="[5, 1, 2]"
              :reportObject="reportObject"
              :resultObject="resultObject"
              :params="reportObject ? reportObject.params : null"
              class="pull-right analytics-page-options-building-analysis newreport-page-options"
            ></f-report-options>
          </div>
        </div>
      </div>
    </div>
    <!-- regression body -->
    <f-regression-point-selecter
      :visibility.sync="showSidebar"
      :enableFloatimngIcon.sync="enableFloatimngIcon"
      @updateDataPoints="getDataPoints"
    ></f-regression-point-selecter>
    <div
      class="report-settings-group1 report-settings-group"
      v-bind:class="{ 'settings-group-edge1': !showSidebar }"
      v-if="enableFloatimngIcon"
    >
      <el-button-group>
        <el-button
          type="primary"
          :class="{ 'report-button-active': showInnerPanel }"
          @click="
            ;(showSidebar = false),
              (showInnerPanel = true),
              (showChartCustomization = false)
          "
          class="report-settings-btn"
          style="padding: 12.3px 10px;"
          ><img
            src="~assets/statistics.svg"
            class="report-option-img report-rotate-icons"
            width="25"
            height="25"
        /></el-button>
        <el-button
          type="primary"
          :class="{ 'report-button-active': showChartCustomization }"
          :disabled="
            reportObject === null || typeof reportObject === 'undefined'
          "
          @click="
            ;(showSidebar = false),
              (showChartCustomization = true),
              (showInnerPanel = false)
          "
          class="report-settings-btn report-settings-btn2"
          ><img
            src="~assets/settings-grey2.svg"
            width="18"
            height="18"
            class="report-option-img"
        /></el-button>
        <el-button
          type="primary"
          @click="toggleSideBar"
          style="transform:rotate(180deg);"
          class="report-settings-btn report-settings-btn3"
          ><img
            src="~assets/arrow-pointing-to-left2.svg"
            width="18"
            height="18"
            class="report-option-img report-rotate-icons"
            :style="{
              transform: showSidebar ? 'rotate(90deg)' : 'rotate(270deg)',
            }"
        /></el-button>
      </el-button-group>
    </div>

    <div
      :class="{
        'report-graph-con-chartCustom-apply':
          showChartCustomization === true && showSidebar === false,
        'report-graph-con': !showSidebar && showChartCustomization === false,
        'report-graph-con-stretch': showSidebar,
      }"
      class="height100 scrollable"
    >
      <div>
        <div
          class="report-preview text-center pT0"
          style="margin-bottom: 50px;"
        >
          <div class="reports-chart">
            <!-- render charts here -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewDataFormatHelper from 'src/pages/report/mixins/NewDataFormatHelper'
import FReportOptions from 'src/pages/report/components/FReportOptions'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import ModularUserFilterMixin from 'src/pages/report/mixins/modularUserFilter'
import FRegressionPointSelecter from 'src/pages/report/components/FRegressionPointSelecter'
import { API } from '@facilio/api'
export default {
  components: {
    FReportOptions,
    FRegressionPointSelecter,
  },
  mixins: [NewDataFormatHelper, ModularUserFilterMixin],
  data() {
    return {
      loading: false,
      dialogVisible: false,
      toggleMetricAggregation: false,
      newReportFolderName: null,
      enableFloatimngIcon: true,
      reportObject: null,
      resultObject: null,
      sortingOrder: [
        {
          label: this.$t('common._common.ascending'),
          value: 2,
        },
        {
          label: this.$t('common._common.descending'),
          value: 3,
        },
      ],
      ranges: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15],
      reportName: 'New Report',
      reportDescription: 'description',
      showInnerPanel: true,
      showChartCustomization: false,
      showSidebar: true,
      savedReport: null,
      configFromReport: null,
      currentUserFilter: null,
      showCriteriaBuilder: false,
      showUserFilter: false,
      showFilterConfigDialog: false,
      showChart: false,
      serverConfig: {},
      criteria: null,
      // togglePanel: false,
      currentMetric: null,
      moduleName: null,
      moduleField: null,
      aggr: {
        YEARLY: 8,
        MONTHLY: 10,
        WEEKLY: 11,
        DAILY: 12,
        HOURLY: 20,
      },
      moduleResourceField: null,
      metrics: [],
      metricAggregation: [
        {
          label: this.$t('common.header.avg'),
          value: 2,
        },
        {
          label: this.$t('common._common.sum'),
          value: 3,
        },
        {
          label: this.$t('common._common.min'),
          value: 4,
        },
        {
          label: this.$t('common._common.max'),
          value: 5,
        },
      ],
      spaceAggregators: [
        {
          displayName: this.$t('common.products.site'),
          name: this.$t('common.products.site'),
          value: 21,
          field_Id: 5,
          dataType: 7,
          show: true,
        },
        {
          displayName: this.$t('common._common.building'),
          name: this.$t('common._common.building'),
          value: 22,
          field_Id: 6,
          dataType: 7,
          show: true,
        },
        {
          displayName: this.$t('common._common.floor'),
          name: this.$t('common._common.floor'),
          value: 23,
          field_Id: 7,
          dataType: 7,
          show: true,
        },
        {
          displayName: this.$t('common.space_asset_chooser.space'),
          name: this.$t('common.space_asset_chooser.space'),
          value: 26,
          field_Id: 8,
          dataType: 7,
          show: true,
        },
      ],
      moduleTypes: [],
      moduleMap: null,
      timeAggregators: [
        {
          displayName: this.$t('common.wo_report.hourly'),
          label: this.$t('common.wo_report.hourly'),
          value: 20,
          selected: false,
          field_Id: 1,
        },
        {
          displayName: this.$t('common._common.daily'),
          label: this.$t('common._common.daily'),
          value: 12,
          selected: false,
          field_Id: 2,
        },
        {
          displayName: this.$t('common._common.weekly'),
          label: this.$t('common._common.weekly'),
          value: 11,
          selected: false,
          field_Id: 3,
        },
        {
          displayName: this.$t('common._common.monthly'),
          label: this.$t('common._common.monthly'),
          value: 10,
          selected: false,
          field_Id: 4,
        },
      ],
      showReportFolderList: false,
      reportFolders: [],
      activePanel: 1,
      activePanelEnum: {
        DIMENSION: 1,
        METRIC: 2,
        GROUPBY: 3,
      },
      groupBy: [],
      initialDimensionConfig: [],
      secondPanelToggle: false,
      thirdPanelLoadingToggle: false,
      timeFields: [],
      config: {
        moduleType: 1,
        dimension: {
          dimension: null,
          subDimension: null,
        },
        metric: [],
        metricAggregation: null,
        groupBy: {
          dimension: null,
          subDimension: null,
        },
        range: null,
        sorting: null,
        customDateField: null,
        criteria: null,
        userFilters: null,
      },
      criteriaTrigger: false,
    }
  },
  computed: {
    enableUserFilter() {
      if (this.$route.query.enableUserFilter) {
        return true
      } else {
        return false
      }
    },
    computedModule() {
      let module = {}
      module['moduleName'] = this.moduleName
      module['moduleId'] = this.moduleId
      if (this.moduleResourceField) {
        module['resourceField'] = this.moduleResourceField
      }
      module['meta'] = { fieldMeta: {} }
      return module
    },
    cssLeft() {
      if (this.showChartCustomization) {
        return 414 + 'px !important'
      } else {
        return 284 + 'px !important'
      }
    },
    computedCriteriaDescription() {
      if (this.isCriteriaEmpty()) {
        return 'All ' + this.moduleName
      } else {
        return 'Criteria Applied'
      }
    },
    computedReportName: {
      get: function() {
        if (this.savedReport) {
          return this.savedReport.name
        } else {
          if (this.resultObject) {
            if (this.resultObject.report.name) {
              return this.resultObject.report.name
            } else {
              return this.reportName
            }
          } else {
            return this.reportName
          }
        }
      },
      set: function(newValue) {
        if (this.savedReport) {
          this.savedReport.name = newValue
        } else {
          if (this.reportObject) {
            this.reportName = newValue
            this.reportObject.name = newValue
          } else {
            this.reportName = newValue
          }
        }
      },
    },
    reportId() {
      if (this.$route.query.reportId && this.$route.query.module) {
        this.moduleName = this.$route.meta.module
        return this.$route.query.reportId
      }
      return null
    },
    showDatePicker() {
      if (this.serverConfig) {
        if (this.serverConfig.isTime) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
  },
  created() {
    this.moduleName = this.$route.meta.module
  },
  methods: {
    getDataPoints(data) {
      console.log('******** ---->', data)
    },
    setConfigForUserFilter(changedConfig) {
      this.showFilterConfigDialog = false
      let temp = []
      for (let config of this.config.userFilters) {
        if (config.fieldId === changedConfig.fieldId) {
          temp.push(changedConfig)
        } else {
          temp.push(config)
        }
      }
      this.$set(this.config, 'userFilters', temp)
      this.$set(this.serverConfig, 'userFilters', temp)
      console.log('USER FILTER')
      console.log(this.config.userFilters)
    },
    openFilterConfig(filterConfig) {
      this.currentUserFilter = filterConfig
      this.showFilterConfigDialog = true
    },
    metricSelected(metric) {
      for (let m of this.config.metric) {
        if (metric.fieldId === m.fieldId && metric.name === m.name) {
          return true
        }
      }
      return false
    },
    showMetricAggregation(metric) {
      if (metric.defaultMetric) {
        this.toggleMetricAggregation = false
        return false
      } else {
        return true
      }
    },
    addMetric() {
      this.currentMetric = null
      this.activePanel = this.activePanelEnum.METRIC
      this.secondPanelToggle = true
    },
    resetUserFilters() {
      this.config.userFilters = null
      this.currentUserFilter = null
      this.$set(this.serverConfig, 'userFilters', null)
    },
    setUserFilter(val) {
      console.log('Setting user filters' + val)
      this.config.userFilters = val
      this.showUserFilter = false
      if (val && val.length == 0) {
        this.currentUserFilter = null
      }
      this.$set(this.serverConfig, 'userFilters', val)
    },
    setModuleType(val) {
      this.config.moduleType = val
      if (this.serverConfig.moduleType) {
        this.serverConfig.moduleType = val
      } else {
        this.$set(this.serverConfig, 'moduleType', val)
      }
    },
    isCriteriaEmpty() {
      if (this.config.criteria) {
        let numberOfConditions = 0
        for (let condition of Object.keys(this.config.criteria.conditions)) {
          if (
            Object.keys(this.config.criteria.conditions[parseInt(condition)])
              .length !== 0 &&
            typeof this.config.criteria.conditions[parseInt(condition)]
              .columnName !== 'undefined'
          ) {
            numberOfConditions = numberOfConditions + 1
          }
        }
        if (numberOfConditions > 0) {
          return false
        } else {
          return true
        }
      } else if (this.config.criteria === null) {
        return true
      }
      return false
    },
    resetGroupBy() {
      for (let group of this.groupBy) {
        for (let subField of group.subFields) {
          subField.show = true
        }
      }
    },
    setCustomDateField(customDateFieldId) {
      this.config.customDateField = customDateFieldId
      this.updateReportObject()
      let oldValue = this.serverConfig
      if (customDateFieldId === 0) {
        oldValue['isCustomDateField'] = false
      } else {
        let customDateField = this.timeFields.filter(
          field => field.fieldId && field.fieldId === customDateFieldId
        )
        this.computedModule['meta']['fieldMeta']['customDateField'] =
          customDateField.length > 0 ? customDateField[0].moduleId : null
        oldValue['isCustomDateField'] = true
        if (
          this.serverConfig.dateField === null ||
          typeof this.serverConfig.dateField === 'undefined'
        ) {
          let dateObject = NewDateHelper.getDatePickerObject(22)
          oldValue['dateFilter'] = dateObject
          let dateField = {}
          dateField['operator'] = dateObject.operatorId
          dateField['field_id'] = customDateFieldId
          // dateField['module_id'] = customDateField[0].moduleId
          dateField['module_id'] = this.moduleId
          dateField['date_value'] = dateObject.value.join(',')
          oldValue['dateField'] = dateField
          this.serverConfig = oldValue
        } else {
          let dateField = this.serverConfig.dateField
          dateField.field_id = customDateFieldId
          // dateField['module_id'] = customDateField[0].moduleId
          dateField['module_id'] = this.moduleId
          this.$set(this.serverConfig, 'dateField', dateField)
        }
      }
    },
    filtergroupBy() {
      for (let dimension of this.groupBy) {
        if (
          dimension.displayName === this.config.dimension.dimension.displayName
        ) {
          for (let field of dimension.subFields) {
            if (field.hasOwnProperty('fieldId')) {
              if (field.fieldId === -1) {
                if (
                  field.displayName ===
                  this.config.dimension.subDimension.displayName
                ) {
                  field.show = false
                } else {
                  field.show = true
                }
              } else if (
                field.fieldId === this.config.dimension.subDimension.fieldId
              ) {
                field.show = false
              } else {
                field.show = true
              }
            } else if (
              field.hasOwnProperty('value') &&
              field.value === this.config.dimension.subDimension.value
            ) {
              field.show = true
            } else {
              field.show = true
            }
          }
        }
      }
    },
    resetCriteria() {
      this.config.criteria = {
        conditions: {
          1: {
            columnName: undefined,
            fieldName: undefined,
            operatorId: undefined,
            value: undefined,
          },
        },
        pattern: '(1)',
        resourceOperator: false,
      }
      this.criteriaTrigger = false
      this.updateReportObject()
      this.$set(this.serverConfig, 'criteria', null)
    },
    prepareData(data) {
      this.initialDimensionConfig = []
      this.groupBy = []
      // metrics
      if (this.moduleName !== 'workorder') {
        // this.getAllDefaults(data)
      } else {
        this.moduleMap = data.moduleMap
        this.timeFields = []
        this.timeFields.push({
          name: this.$t('common.wo_report.none'),
          displayName: this.$t('common.wo_report.none'),
          id: 0,
        })
        for (let dimension of data.dimension.time) {
          if (
            dimension.name === 'createdTime' ||
            dimension.name === 'dueDate' ||
            dimension.name === 'actualWorkStart' ||
            dimension.name === 'actualWorkEnd'
          ) {
            this.timeFields.push(dimension)
          }
        }
        for (let metric of data.metrics) {
          if (metric.name === 'actualWorkDuration') {
            metric.displayName = 'Work Duration'
            this.metrics.push(metric)
          }
          if (metric.id === -1 || metric.name === 'totalCost') {
            this.metrics.push(metric)
          }
        }
        let defaultMetric = {}
        defaultMetric['displayName'] = 'Number of ' + this.moduleName + 's'
        defaultMetric['defaultMetric'] = true
        defaultMetric['fieldId'] = -1
        defaultMetric['name'] = 'default'
        this.metrics.push(defaultMetric)
        for (let dimension of data.dimensionListOrder) {
          if (dimension.toLowerCase() !== 'resource_fields') {
            let dimensionData = data.dimension[dimension]
            let baseconfig = {
              displayName: this.getStringWithCaps(dimension),
              subFields: [],
              showSubFields: true,
            }

            for (let field of dimensionData) {
              if (field.name.toLowerCase() === 'actualworkstart') {
                field.displayName = 'Start Time'
              }
              field['selected'] = true
              baseconfig.subFields.push(field)
            }

            if (dimension === this.moduleName) {
              let sourceTypeField = data.metrics.filter(
                element => element.name === 'sourceType'
              )[0]
              sourceTypeField['selected'] = true
              baseconfig.subFields.push(sourceTypeField)
            }
            this.initialDimensionConfig.push(baseconfig)
            if (dimension !== 'time') {
              let fields = []
              for (let field of baseconfig.subFields) {
                if (field.dataType !== 5 && field.dataType !== 6) {
                  fields.push(field)
                }
              }
              let groupBaseField = this.$helpers.cloneObject(baseconfig)
              groupBaseField.subFields = fields
              this.groupBy.push(groupBaseField)
            }
          }
        }
        // load space in initial Dimension config and group by

        this.moduleResourceField = data.dimension['resource_fields'][0]
        let spaceConfig = {
          displayName: 'Space',
          subFields: this.spaceAggregators,
          showSubFields: true,
        }

        this.initialDimensionConfig.push(spaceConfig)
        this.groupBy.push(spaceConfig)

        // // Load ModuleTypes
        if (data.moduleType && data.moduleType.length !== 0) {
          for (let type of data.moduleType) {
            let temp = {}
            ;(temp['displayName'] = type.displayName), (temp['id'] = type.type)
            temp['selected'] = this.moduleTypes.length === 0 ? true : false
            temp['field_id'] = type.type

            this.moduleTypes.push(temp)
          }
        }
      }
      this.addGroupRequirements()
    },
    updateReportObject() {
      if (this.reportObject) {
        this.reportObject.options.initialConfig = this.config
      }
    },
    addGroupRequirements() {
      for (let groupBy of this.groupBy) {
        for (let field of groupBy.subFields) {
          field['show'] = true
        }
      }
    },
    removeGroupBy() {
      this.config.groupBy.subDimension = null
      this.config.groupBy.dimension = null
      this.updateReportObject()
      // this.$set(this.serverConfig, 'groupBy', null)
      this.build(this.config, this.moduleResourceField)
    },
    removeMetric(metricIdx) {
      // only for one Metric
      // this.config.metric = {}
      // this.config.metricAggregation = null
      let metric = this.config.metric[metricIdx]
      delete this.config.metricAggregation[metric.fieldId + '__' + metric.name]
      this.config.metric.splice(metricIdx, 1)
      this.config.sorting = null
      this.config.range = null
      this.build(this.config, this.moduleResourceField)
    },
    handleSort(sortVal) {
      this.config.sorting = sortVal
      this.$set(this.serverConfig, 'sortOrder', sortVal)
    },
    handleRange(rangeVal) {
      this.config.range = rangeVal
      this.$set(this.serverConfig, 'limit', rangeVal)
      // this.build(this.config, this.moduleResourceField)
    },
    setMetricAggregation(metricAggr, metricIdx) {
      this.toggleMetricAggregation = false
      let metric = this.config.metric[metricIdx]
      this.config.metricAggregation[
        metric.fieldId + '__' + metric.name
      ] = metricAggr
      this.updateReportObject()
      if (this.serverConfig.yField) {
        this.serverConfig.yField[metricIdx].aggr = metricAggr
        // this.serverConfig.yField[0].aggr = metricAggr
      } else {
        console.error('yField cannot be null')
        // this.$set(this.serverConfig.yField, 'aggr', metricAggr)
      }
    },
    showReportFolders() {
      let url = '/v3/report/folders?moduleName=' + this.moduleName
      API.get(url)
        .then(response => {
          if (!response.error) {
            this.reportFolders = response.data.reportFolders
            this.showReportFolderList = true
          }
        })
        .catch(error => {
          this.showReportFolderList = false
          this.reportFolders = []
          console.log(
            this.$t('common._common.error_in_fetching_report_folder_name')
          )
          console.log(error)
        })
    },
    closeActivePanel() {
      if (this.secondPanelToggle) {
        this.secondPanelToggle = false
      }
    },
    showActiveArrow(name) {
      console.log('show active row')
      console.log(name)
      this.$nextTick(() => {
        if (this.config.dimension.dimension) {
          if (name === this.config.dimension.dimension.displayName) {
            return true
          } else {
            return false
          }
        } else {
          return false
        }
      })
    },
    showSecondPanel(activator) {
      this.activePanel = activator
      if (activator === this.activePanelEnum.DIMENSION) {
        // this.filterQuery = ''
        this.secondPanelToggle = true
      } else if (activator === this.activePanelEnum.METRIC) {
        this.secondPanelToggle = true
      } else {
        // group by case
        // this.filterQuery = ''
        this.secondPanelToggle = true
      }
    },
    cancel() {
      this.openPanel = false
      this.$emit('update:openPanel', this.openPanel)
    },
    setDimension(dimension, child) {
      console.log('SET DIMENSION')
      console.log(child)
      console.log(dimension)
      if (this.activePanel === this.activePanelEnum.DIMENSION) {
        if (dimension.displayName === 'Time') {
          this.resetGroupBy()
        }
        this.config.dimension.dimension = dimension
        this.config.dimension.subDimension = child
        this.filtergroupBy()
        this.build(this.config, this.moduleResourceField)
      } else if (this.activePanel === this.activePanelEnum.METRIC) {
        // this.config.metric = dimension
        if (this.currentMetric !== null) {
          this.config.metric[this.currentMetric] = dimension
          this.build(this.config, this.moduleResourceField)
        } else if (this.currentMetric == null) {
          this.config.metric.push(dimension)
          this.build(this.config, this.moduleResourceField)
        }
      } else {
        this.config.groupBy.dimension = dimension
        this.config.groupBy.subDimension = child
        this.build(this.config, this.moduleResourceField)
        //  apply group by
      }
      this.updateReportObject()
    },
    toggleSideBar() {
      if (this.showSidebar) {
        this.showSidebar = false
        if (this.$refs['analyticReport']) {
          this.$refs['analyticReport'].resize()
        }
      } else {
        this.showSidebar = true
        if (this.$refs['analyticReport']) {
          this.$refs['analyticReport'].resize()
        }
      }
    },
    populateReportObjects(report, result) {
      this.reportObject = report
      this.resultObject = result
      if (this.reportObject !== null && !this.reportObject.name) {
        this.reportObject['name'] = this.reportName
      }
      if (this.reportId && this.savedReport === null) {
        this.savedReport = result.report
      }
      if (
        this.config.dimension.dimension !== null &&
        this.reportObject !== null
      ) {
        this.reportObject.options['initialConfig'] = this.config
      }
      if (
        this.$route.query.duplicate &&
        this.$route.query.reportId &&
        this.reportObject !== null
      ) {
        this.reportObject['name'] = this.computedReportName
        this.reportObject['description'] = this.computedReportDescription
      }
      if (this.reportId && this.config.dimension.dimension === null) {
        let config = this.reconstructConfig(result)
        if (config.userFilters) {
          this.loadAllValues(config)
        } else {
          this.config = config
          this.build(this.config, this.moduleResourceField)
        }
      }
    },
    setCriteria(criteria) {
      console.log('setting criteria')
      this.config.criteria = criteria
      this.updateReportObject()
    },
    reconstructConfig(result) {
      let config = null
      if (result.report.chartState) {
        if (typeof result.report.chartState === 'object') {
          config = result.report.chartState.initialConfig
        } else {
          let chartState = JSON.parse(result.report.chartState)
          config = chartState.initialConfig
        }
      }

      // Done for backward compatibility for existing new reports (metric structure and user Filters)
      if (!Array.isArray(config.metric)) {
        let temp = config.metric
        if (config.metric.defaultMetric) {
          temp['fieldId'] = -1
          temp['name'] = 'default'
          config.metric = []
          config.metric.push(temp)
        } else {
          config.metric.push(temp)
        }

        if (config.metricAggregation) {
          let aggr = config.metricAggregation
          config.metricAggregation = {}
          config.metricAggregation[temp.fieldId + '__' + temp.name] = aggr
        }
        if (!config.hasOwnProperty('userFilters')) {
          config['userFilters'] = null
        }
      }
      console.log('Reconstructed config')
      console.log(config)
      return config
    },
    getDimensionModuleId(config) {
      if (
        this.moduleMap &&
        Object.keys(this.moduleMap).includes(
          config.dimension.dimension.displayName.toLowerCase()
        )
      ) {
        return this.moduleMap[
          config.dimension.dimension.displayName.toLowerCase()
        ]
      }
      return this.moduleId
    },
    getGroupByModuleId(config) {
      if (
        this.moduleMap &&
        Object.keys(this.moduleMap).includes(
          config.groupBy.dimension.displayName.toLowerCase()
        )
      ) {
        return this.moduleMap[
          config.groupBy.dimension.displayName.toLowerCase()
        ]
      }
      return this.moduleId
    },
    buildDimensionObjectForServer(config, moduleResourceField) {
      let serverConfig = {}
      let xField = {}
      if (config.moduleType && this.moduleTypes.length !== 0) {
        serverConfig['moduleType'] = config.moduleType
      } else if (this.moduleTypes.length !== 0) {
        // backward compatability for reports without module type
        config.moduleType = 1
        this.config['moduleType'] = 1
        serverConfig['moduleType'] = 1
      }
      if (config.dimension.dimension.displayName === 'Time') {
        if (config.dimension.subDimension.fieldId === -1) {
          xField['field_id'] = config.dimension.subDimension.name
        } else {
          xField['field_id'] = config.dimension.subDimension.fieldId
        }
        xField['aggr'] =
          this.resultObject &&
          this.resultObject.report.xAggr &&
          (this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
            this.resultObject.report.dataPoints[0].xAxis.dataType === 6)
            ? this.resultObject.report.xAggr
            : 12
        xField['module_id'] = this.moduleId
        this.computedModule['meta']['fieldMeta']['xField'] = xField.module_id
        serverConfig['xField'] = xField
        serverConfig['isTime'] = true
      } else if (config.dimension.dimension.displayName === 'Space') {
        if (config.dimension.subDimension.displayName === 'Site') {
          xField['field_id'] = 'siteId'
        } else {
          xField['field_id'] = moduleResourceField.fieldId
          xField['aggr'] = config.dimension.subDimension.value
        }
        xField['module_id'] = this.moduleId
        // xField['module_id'] = moduleResourceField.module.moduleId
        this.computedModule['meta']['fieldMeta']['xField'] = xField.module_id
        serverConfig['xField'] = xField
        serverConfig['isTime'] = false
      } else {
        // covered for asset and the module itself
        if (config.dimension.subDimension.fieldId === -1) {
          xField['field_id'] = config.dimension.subDimension.name
        } else {
          xField['field_id'] = config.dimension.subDimension.fieldId
        }
        // xField['module_id'] = this.moduleId
        xField['module_id'] = this.getDimensionModuleId(config)
        this.computedModule['meta']['fieldMeta']['xField'] = xField.module_id
        if (
          config.dimension.subDimension.dataType === 5 ||
          (config.dimension.subDimension.dataType === 6 &&
            config.dimension.subDimension.moduleId !== this.moduleId)
        ) {
          serverConfig['isTime'] = true
          xField['aggr'] =
            this.resultObject &&
            this.resultObject.report.xAggr &&
            (this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
              this.resultObject.report.dataPoints[0].xAxis.dataType === 6)
              ? this.resultObject.report.xAggr
              : 12
        } else {
          serverConfig['isTime'] = false
        }
        serverConfig['xField'] = xField
      }
      if (config.metric.length !== 0) {
        serverConfig['yField'] = []
        for (let metric of config.metric) {
          if (metric.defaultMetric) {
            serverConfig['yField'].push(null)
          } else {
            let yField = {}
            if (metric.fieldId === -1) {
              yField['field_id'] = metric.name
              yField['module_id'] = this.moduleId
              this.computedModule['meta']['fieldMeta']['yField'] =
                yField.module_id
            } else {
              yField['field_id'] = metric.fieldId
              yField['module_id'] = this.moduleId
              this.computedModule['meta']['fieldMeta']['yField'] =
                yField.module_id
            }
            if (config.metricAggregation === null && metric.fieldId) {
              this.config.metricAggregation = {}
              this.config.metricAggregation[
                metric.fieldId + '__' + metric.name
              ] = this.metricAggregation[0].value
              yField['aggr'] = this.config.metricAggregation[
                metric.fieldId + '__' + metric.name
              ]
            } else if (
              config.metricAggregation &&
              !config.metricAggregation.hasOwnProperty(
                metric.fielId + '__' + metric.name
              )
            ) {
              this.config.metricAggregation[
                metric.fieldId + '__' + metric.name
              ] = this.metricAggregation[0].value
              yField['aggr'] = this.config.metricAggregation[
                metric.fieldId + '__' + metric.name
              ]
            } else {
              yField['aggr'] =
                config.metricAggregation[metric.fieldId + '__' + metric.name]
            }
            serverConfig['yField'].push(yField)
          }
        }
      } else {
        serverConfig['yField'] = null
      }
      if (config.groupBy.dimension && config.groupBy.subDimension) {
        let groupBy = {}
        if (config.groupBy.dimension.displayName === 'Time') {
          // groupBy['field_id'] = config.groupBy.subDimension.fieldId
          if (config.groupBy.subDimension.fieldId === -1) {
            groupBy['field_id'] = config.groupBy.subDimension.name
            groupBy['module_id'] = this.moduleId
            // groupBy['module_id'] = config.groupBy.subDimension.module.moduleId
            this.computedModule['meta']['fieldMeta']['groupBy'] =
              groupBy.module_id
          } else {
            groupBy['field_id'] = config.groupBy.subDimension.fieldId
            groupBy['module_id'] = this.moduleId
            // groupBy['module_id'] = config.groupBy.subDimension.module.moduleId
            this.computedModule['meta']['fieldMeta']['groupBy'] =
              groupBy.module_id
          }
          groupBy['aggr'] =
            this.serverConfig.groupBy && this.serverConfig.groupBy.aggr
              ? this.serverConfig.groupBy.aggr
              : 12
          serverConfig['groupBy'] = [groupBy]
        } else if (config.groupBy.dimension.displayName === 'Space') {
          if (config.groupBy.subDimension.displayName === 'Site') {
            groupBy['field_id'] = 'siteId'
          } else {
            groupBy['field_id'] = moduleResourceField.fieldId
            groupBy['aggr'] = config.groupBy.subDimension.value
          }
          groupBy['module_id'] = this.moduleId
          // groupBy['module_id'] = moduleResourceField.module.moduleId
          this.computedModule['meta']['fieldMeta']['groupBy'] =
            groupBy.module_id

          serverConfig['groupBy'] = [groupBy]
        } else {
          // covered for asset and the module itself
          if (config.groupBy.subDimension.fieldId === -1) {
            groupBy['field_id'] = config.groupBy.subDimension.name
          } else {
            groupBy['field_id'] = config.groupBy.subDimension.fieldId
          }
          groupBy['module_id'] = this.getGroupByModuleId(config)
          // groupBy['module_id'] = config.groupBy.subDimension.module.moduleId
          this.computedModule['meta']['fieldMeta']['groupBy'] =
            groupBy.module_id
          // groupBy['field_id'] = config.groupBy.subDimension.fieldId
          serverConfig['groupBy'] = [groupBy]
        }
      } else {
        serverConfig['groupBy'] = null
      }

      // apply criteria
      if (!this.isCriteriaEmpty()) {
        serverConfig['criteria'] = this.config.criteria
      } else {
        serverConfig['criteria'] = null
      }
      // sorting
      if (
        config.dimension.dimension.displayName !== 'Time' &&
        config.groupBy.dimension === null
      ) {
        let sortFields = []
        let sortOrder = 0

        sortFields.push({ field_id: 'y-field' })

        if (this.config.sorting) {
          sortOrder = this.config.sorting
        } else {
          this.config.sorting = 3
          sortOrder = this.config.sorting
        }
        serverConfig['sortOrder'] = sortOrder
        serverConfig['sortFields'] = sortFields

        if (config.range) {
          serverConfig['limit'] = config.range
        } else {
          this.config.range = 15
          serverConfig['limit'] = this.config.range
        }
      } else {
        serverConfig['sortFields'] = null
        serverConfig['sortOrder'] = null
        serverConfig['limit'] = null
      }
      if (
        config.customDateField &&
        config.dimension.dimension.displayName !== 'Time'
      ) {
        serverConfig['isCustomDateField'] = true
      } else {
        serverConfig['isCustomDateField'] = false
      }
      if (this.config.userFilters) {
        serverConfig['userFilters'] = this.config.userFilters
      }
      console.log('config for server')
      console.log(serverConfig)
      return serverConfig
    },
    build(config, moduleResourceField) {
      // handle for fsave as report
      this.secondPanelToggle = false

      this.configFromReport = null
      let element = this.buildDimensionObjectForServer(
        config,
        moduleResourceField
      )
      console.log('setting dimensions')
      console.log(element)
      element['mode'] = 4
      element['hideChart'] = false
      if (
        element.xField.aggr &&
        config.dimension.dimension.displayName === 'Time' &&
        this.activePanel === this.activePanelEnum.DIMENSION &&
        !this.serverConfig.dateField
      ) {
        if (!this.savedReport) {
          switch (element.xField.aggr) {
            case this.aggr.YEARLY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(44)
              break
            case this.aggr.MONTHLY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(44)
              break
            case this.aggr.WEEKLY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(28)
              break
            case this.aggr.DAILY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(31)
              break
            case this.aggr.HOURLY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(22)
              break
          }
        } else {
          element['dateFilter'] = NewDateHelper.getDatePickerObject(
            this.savedReport.dateOperator,
            [
              this.savedReport.dateRange.startTime,
              this.savedReport.dateRange.endTime,
            ]
          )
        }

        let dateField = {}
        dateField['operator'] = element.dateFilter.operatorId
        dateField['field_id'] = this.config.dimension.subDimension.fieldId
        dateField['module_id'] = this.moduleId
        if (
          element.dateFilter.operatorId === 49 ||
          element.dateFilter.operatorId === 50 ||
          element.dateFilter.operatorId === 51
        ) {
          dateField['date_value'] = Math.abs(element.dateFilter.offset) + ''
        } else if (element.dateFilter.operatorId === 20) {
          dateField['date_value'] = element.dateFilter.value.join(',')
        } else {
          dateField['date_value'] = element.dateFilter.value[0] + ''
        }
        element['dateField'] = dateField
      } else {
        if (
          this.savedReport &&
          !this.serverConfig.dateField &&
          this.resultObject.report.dateOperator !== -1
        ) {
          element['dateFilter'] = this.resultObject.dateRange
          // let moduleId = this.config.customDateField && this.config.customDateField !== 0 ? this.timeFields.filter((field) => field.fieldId && field.fieldId === this.config.customDateField)[0].moduleId : (typeof(this.config.dimension.subDimension.moduleId) === 'undefined' || this.config.dimension.subDimension.moduleId === this.moduleId) ? this.moduleId : this.config.dimension.subDimension.moduleId
          element['dateField'] = this.config.customDateField
            ? this.getDateFieldParam(
                this.resultObject.dateRange,
                this.config.customDateField,
                this.moduleId
              )
            : this.getDateFieldParam(
                this.resultObject.dateRange,
                element.xField.field_id,
                this.moduleId
              )
        } else {
          if (this.serverConfig.dateField && element.isTime === true) {
            this.serverConfig.dateField[
              'field_id'
            ] = this.config.dimension.subDimension.fieldId
            this.serverConfig.dateField['module_id'] = this.moduleId
            element['dateField'] = this.serverConfig.dateField
          } else {
            // element['dateField'] = this.serverConfig.dateField ? this.serverConfig.dateField : this.getDateFieldParam(NewDateHelper.getDatePickerObject(22), element.xField.field_id, (typeof(this.config.dimension.subDimension.moduleId) === 'undefined' || this.config.dimension.subDimension.moduleId === this.moduleId) ? this.moduleId : this.config.dimension.subDimension.moduleId)
            element['dateField'] = this.serverConfig.dateField
              ? this.computeDateFieldParamFromExisting(
                  this.serverConfig.dateField,
                  element
                )
              : this.getDateFieldParam(
                  NewDateHelper.getDatePickerObject(22),
                  element.xField.field_id,
                  this.moduleId
                )
          }
          element['dateFilter'] = this.serverConfig.dateFilter
            ? this.serverConfig.dateFilter
            : NewDateHelper.getDatePickerObject(22)
        }
      }
      if (this.config.metric && this.config.metric.length === 0) {
        this.currentMetric = 0
        let defaultMetric = {}
        if (element.moduleType === 2) {
          defaultMetric['displayName'] = 'Number of ' + 'workrequest' + 's'
        } else {
          defaultMetric['displayName'] = 'Number of ' + this.moduleName + 's'
        }
        defaultMetric['fieldId'] = -1
        defaultMetric['name'] = 'default'
        defaultMetric['defaultMetric'] = true
        this.config.metric.push(defaultMetric)
      }
      this.showChart = true
      this.serverConfig = element
      console.log('This is config')
      console.log(this.config)
    },
    computeDateFieldParamFromExisting(existingDateField, newServerConfig) {
      let dateField = {}
      dateField['operator'] = existingDateField.operator
      dateField['field_id'] =
        newServerConfig.isTime === true
          ? newServerConfig.xField.field_id
          : this.config.customDateField
      if (newServerConfig.isTime === true) {
        dateField['module_id'] = newServerConfig.xField.module_id
      } else {
        // let field = this.timeFields.filter((field) => field.fieldId && field.fieldId === this.config.customDateField)
        dateField['module_id'] = this.moduleId
        // if(field.length > 0){

        // }
      }
      dateField['date_value'] = existingDateField.date_value
      return dateField
    },
    getDateFieldParam(dateObj, element, moduleId) {
      let dateField = {}
      dateField['operator'] = dateObj.operatorId
      dateField['field_id'] = element
      dateField['module_id'] = moduleId
      if (
        dateObj.operatorId === 49 ||
        dateObj.operatorId === 50 ||
        dateObj.operatorId === 51
      ) {
        dateField['date_value'] = Math.abs(dateObj.offset) + ''
      } else if (dateObj.operatorId === 20) {
        dateField['date_value'] = dateObj.value.join(',')
      } else {
        dateField['date_value'] = dateObj.value[0] + ''
      }
      return dateField
    },
    addCriteriaAndResconstruct() {
      console.log('adding criteria')
      console.log(this.config.criteria)
      if (!this.isCriteriaEmpty()) {
        this.criteriaTrigger = true
        this.$set(this.serverConfig, 'criteria', this.config.criteria)
      }
      this.createTrigger = false
      this.showCriteriaBuilder = false
      console.log(this.serverConfig)
    },
  },
}
</script>
<style lang="scss">
.regression-analysis-page {
  .regression-analysis .new-analytics-subheader .fc-report-header {
    box-shadow: none !important;
  }
  .fc-report-header-2 {
    width: 100%;
    height: 70px;
    box-sizing: border-box;
    padding: 14px 25px 14px 10px;
    background-color: #ffffff;
    border-bottom: 1px solid #e6e6e6;
    position: -webkit-sticky;
    position: sticky;
    z-index: 1;
  }
  .new-analytics-subheader .newanalytics-sidebar {
    box-shadow: none !important;
  }
  .report-settings-group1 {
    position: absolute;
    -webkit-transform: rotate(90deg);
    transform: rotate(90deg);
    top: 160px;
    left: 245px;
    z-index: 7;
  }
  .report-settings-group1.settings-group-edge1 {
    top: 160px;
    left: -36px;
  }
  .new-analytics-subheader .building-points-search .el-input__inner {
    height: 40px;
    line-height: 40px;
    padding-left: 24px;
    padding-right: 24px;
    border-bottom: 1px solid #e0e0e0;
  }
}
</style>
