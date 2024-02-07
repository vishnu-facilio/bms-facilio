<template>
  <div>
    <el-row class="mtb10 underlying-report-filters">
      <el-col
        v-if="!loading && isXFieldCrossModule === false"
        :span="6"
        class="text-left"
      >
        <el-select
          filterable
          class="fc-input-full-border2 mB15 width250px"
          @change="setDimensionFilter"
          v-model="dimensionValues"
          :placeholder="
            $t('home.dashboard.filter_by') +
              ' ' +
              resultObject.report.dataPoints[0].xAxis.label
          "
        >
          <el-option
            v-for="(dimension, dimensionIdx) in computedDimensionValues"
            :key="dimensionIdx"
            :value="dimension.id"
            :label="dimension.label"
          ></el-option>
        </el-select>
      </el-col>
      <el-col
        :span="8"
        v-if="
          !loading &&
            isGroupByCrossModule === false &&
            reportObject.options.isSystemGroup === true
        "
      >
        <el-select
          filterable
          class="fc-input-full-border2 mB15 width250px mL10"
          @change="setGroupByFilter"
          v-model="groupByValues"
          :placeholder="$t('home.dashboard.group_by')"
        >
          <el-option
            v-for="(groupBy, groupByIdx) in computedGroupByValues"
            :key="groupByIdx"
            :value="groupBy.id"
            :label="groupBy.label"
          ></el-option>
        </el-select>
      </el-col>
      <el-col :span="8" class="text-right mT10 fR">
        <f-pagination
          ref="pagination"
          :total="totalCount"
          :perPage="perPageCount"
        ></f-pagination>
      </el-col>
    </el-row>
    <div
      class="underlying-scroll clearboth"
      :class="{
        zoom95: isZoomLarge,
        zoom40: isZoomMedium,
        zoom35: isZoomSmall,
      }"
    >
      <spinner v-if="loading" :show="loading" size="80"></spinner>
      <!-- <div v-if="!loading && ">{{'No ' + moduleName + 's '}}</div> -->
      <div
        class="text-center mT50 mB50"
        v-if="!loading && recordData.length === 0"
      >
        <img src="~statics/noData-light.png" width="100" height="100" />
        <div class="mT20">No data available</div>
      </div>
      <table
        class="setting-list-view-table"
        v-if="!loading && recordData.length > 0"
      >
        <thead>
          <tr>
            <th class="setting-table-th setting-th-text">NO</th>
            <th
              class="setting-table-th setting-th-text"
              v-for="(defaultModule, defaultModuleIdx) in tableHeadings"
              :key="defaultModuleIdx"
            >
              {{ defaultModule.field.displayName }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            class="tablerow"
            style="cursor:default !important;"
            v-for="(row, rowIdx) in recordData"
            :key="rowIdx"
          >
            <td>{{ rowIdx + 1 }}</td>
            <template v-for="(entry, entryIdx) in Object.keys(row)">
              <td v-if="entry !== 'id' || showId" :key="entryIdx">
                <div
                  class="width200px underlying-worker-td"
                  :class="
                    entry === 'subject'
                      ? 'width200px'
                      : '' || entry === 'id'
                      ? 'width50px'
                      : '' || entry === 'createdTime'
                      ? 'width180px'
                      : '' || entry === 'modifiedTime'
                      ? 'width170px'
                      : '' || entry === 'actualWorkEnd'
                      ? 'width170px'
                      : '' || entry === 'resource'
                      ? 'width150px'
                      : ''
                  "
                >
                  {{ row[entry] }}
                  <img
                    v-if="entry === 'subject'"
                    src="~statics/report/arrow-report.svg"
                    style="padding-top:4px;"
                    class="chart-icon-report pointer"
                    @click="openSummary(row.id)"
                  />
                </div>
              </td>
            </template>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import FPagination from 'src/components/list/FPagination'
import NewDataFormatHelper from 'src/pages/report/mixins/NewDataFormatHelper'
import moment from 'moment-timezone'
import ModularAnalyticmixin from 'src/pages/energy/analytics/mixins/ModularAnalyticmixin'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'
import Spinner from '@/Spinner'
import http from 'util/http'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  components: {
    FPagination,
    Spinner,
  },
  mixins: [NewDataFormatHelper, ModularAnalyticmixin],
  data() {
    return {
      moduleName: null,
      moduleResourceField: null,
      dimensionValues: null,
      groupByValues: null,
      operatorId: 36, // Only IS operator
      filters: {},
      defaultFilters: {},
      totalCount: 0,
      perPageCount: 30,
      viewName: 'all',
      moduleDefaults: null,
      loading: false,
      summaryPageMap: {},
      dataKey: {
        workorder: 'workOrders',
        alarm: 'alarms',
        asset: 'assets',
      },
      isZoomSmall: false,
      isZoomMedium: false,
      isZoomLarge: false,
      recordData: [],
      selectedFields: null,
      metaFields: [],
      lookupMap: {},
      tableHeadings: [],
    }
  },
  watch: {
    pageNumber(val) {
      this.loadInitalFilters()
    },
    tableHeadings: {
      handler(val) {
        this.handleTextSize(val)
        if (val) {
          let shownFieldNames = val.map(field => field.field.name)
          let fields = [...val]
          if (this.moduleDefaults && this.moduleDefaults.fields) {
            fields.push(
              ...this.moduleDefaults.fields.filter(
                field => field && !shownFieldNames.includes(field.field.name)
              )
            )
          }
          this.$set(
            this.reportObject,
            'underlyingFields',
            fields.map(field => ({
              name: field.field.name,
              displayName: field.field.displayName,
              selected: shownFieldNames.includes(field.field.name),
            }))
          )
        } else {
          this.$set(this.reportObject, 'underlyingFields', [])
        }
      },
    },
  },
  computed: {
    isPrinting() {
      return this.$route.query.print || this.$route.query.daterange // daterange check temp
    },
    isCustomModule() {
      if (
        this.resultObject &&
        this.resultObject.module &&
        this.resultObject.module.custom
      ) {
        return true
      }
      return false
    },
    isGroupByCrossModule() {
      if (
        this.resultObject.report.dataPoints[0].groupByFields &&
        this.resultObject.report.dataPoints[0].groupByFields !== null
      ) {
        let groupField = this.resultObject.report.dataPoints[0].groupByFields[0]
        if (
          groupField.field.module.name ===
            this.resultObject.report.module.name ||
          (this.resultObject.report.module.extendModule &&
            groupField.field.module.name ===
              this.resultObject.report.module.extendModule.name)
        ) {
          return false
        }
        return true
      }
      return false
    },
    isXFieldCrossModule() {
      let xAxis = this.resultObject.report.dataPoints[0].xAxis
      if (
        xAxis.field.module.name === this.resultObject.report.module.name ||
        (this.resultObject.report.module.extendModule &&
          xAxis.field.module.name ===
            this.resultObject.report.module.extendModule.name)
      ) {
        return false
      }
      return true
    },
    isGroupByFormulaField() {
      if (this.resultObject.report.dataPoints[0].groupByFields) {
        for (let groupField of this.resultObject.report.dataPoints[0]
          .groupByFields) {
          if (groupField.fieldId === -1) {
            return true
          }
        }
        return false
      } else {
        return false
      }
    },
    pageNumber() {
      if (this.$route.query.page) {
        return this.$route.query.page
      } else {
        return 1
      }
    },
    selectedFieldNames() {
      return this.$route.query.fields
    },
    showId() {
      return (
        this.selectedFieldNames &&
        this.selectedFields.find(field => field.field.name === 'id')
      )
    },
    computedDimensionValues() {
      let dimensions = []
      let xField = this.resultObject.report.dataPoints[0].xAxis
      if (xField.dataType === 5 || xField.dataType === 6) {
        // X axis has date and time
        let periodObject = this.getDateFormat(
          this.reportObject.dateRange.period
        )
        let momentFormat = periodObject.format
        for (let timeString of this.reportObject.dateRange.range.domain) {
          let exactMillis = this.reverseIterate(timeString, periodObject)
          let temp = {}
          temp['id'] = this.reportObject.dateRange.range.domain.indexOf(
            timeString
          )
          temp['label'] = moment(exactMillis).format(periodObject.tooltip)
          let millis = this.getMillis(
            timeString,
            momentFormat,
            this.reportObject.dateRange.period
          )
          temp['value'] = millis
          dimensions.push(temp)
        }
      } else if (
        xField.dataTypeEnum.toLowerCase() === 'lookup' ||
        xField.fieldName === 'siteId'
      ) {
        // let xFieldId = this.resultObject.report.dataPoints[0].xAxis.fieldId
        // let labelMap = this.resultObject.reportData.labelMap[xFieldId]
        let labelMap = this.resultObject.report.dataPoints[0].xAxis.lookupMap
        let xValues = []
        let xAlias =
          this.resultObject.report.xAlias !== null
            ? this.resultObject.report.xAlias
            : 'X'
        for (let data of this.resultObject.reportData.data) {
          let xId = data[xAlias]
          xValues.push(xId)
        }
        for (let key of Object.keys(labelMap)) {
          if (xValues.includes(parseInt(key))) {
            let temp = {}
            temp['id'] = key
            temp['label'] = labelMap[key]
            temp['value'] = null
            dimensions.push(temp)
          }
        }
      } else if (
        xField.dataTypeEnum.toLowerCase() === 'enum' ||
        xField.dataTypeEnum.toLowerCase() === 'boolean'
      ) {
        let enumMap = this.resultObject.report.dataPoints[0].xAxis.enumMap
        let xValues = []
        let xAlias =
          this.resultObject.report.xAlias !== null
            ? this.resultObject.report.xAlias
            : 'X'
        for (let data of this.resultObject.reportData.data) {
          let xId = data[xAlias]
          xValues.push(xId)
        }
        for (let key of Object.keys(enumMap)) {
          if (xValues.includes(parseInt(key))) {
            let temp = {}
            temp['id'] = key
            temp['label'] = enumMap[key]
            temp['value'] = null
            dimensions.push(temp)
          }
        }
      } else {
        let xAlias =
          this.resultObject.report.xAlias !== null
            ? this.resultObject.report.xAlias
            : 'X'
        for (let data of this.resultObject.reportData.data) {
          let temp = {}
          temp['id'] = this.resultObject.reportData.data.indexOf(data) + ''
          temp['label'] = data[xAlias]
          temp['value'] = data[xAlias]
          dimensions.push(temp)
        }
      }
      return dimensions
    },
    computedGroupByValues() {
      let groups = []
      let groupIds = []
      if (
        this.resultObject.report.dataPoints[0].groupByFields &&
        this.resultObject.report.dataPoints[0].groupByFields.length !== 0 &&
        (this.resultObject.report.dataPoints[0].groupByFields[0].dataTypeEnum.toLowerCase() ===
          'lookup' ||
          this.resultObject.report.dataPoints[0].groupByFields[0].fieldName ===
            'siteId')
      ) {
        /*
        only for first level group by
        **** Additional context:
        Here, Group by is only applied as a filter for the data
        *****
        */
        for (let data of this.resultObject.reportData.data) {
          let firstLevelGroupLabel = this.resultObject.report.dataPoints[0]
            .groupByFields[0].field.name
          let yData = data[firstLevelGroupLabel]
          for (let record of yData) {
            if (Number.isInteger(parseInt(record[firstLevelGroupLabel]))) {
              groupIds.push(parseInt(record[firstLevelGroupLabel]))
            }
          }
        }

        let groupLabelMap = this.resultObject.report.dataPoints[0]
          .groupByFields[0].lookupMap
        for (let key of Object.keys(groupLabelMap)) {
          if (groupIds.includes(parseInt(key))) {
            let temp = {}
            temp['id'] = key
            temp['label'] = groupLabelMap[key]
            temp['value'] = null
            groups.push(temp)
          }
        }
      } else {
        let requiredGroups = this.requiredGroupValues(this.resultObject)
        for (let key of Object.keys(requiredGroups)) {
          let temp = {}
          temp['id'] = parseInt(key)
          temp['label'] = requiredGroups[key]
          temp['value'] = requiredGroups[key]
          groups.push(temp)
        }
      }
      return groups
    },
  },
  created() {
    // load defaults Headers
    this.loading = true
    if (this.module) {
      this.moduleName = this.module['moduleName']
      this.moduleResourceField = this.module['resourceField']
    }
    if (typeof this.moduleName === 'undefined') {
      this.moduleName = 'workorder'
    }

    this.loadMetaFields().then(() => {
      http.get('/view/all?moduleName=' + this.moduleName).then(response => {
        this.moduleDefaults = response.data
        this.computeTableHeadings()
        if (this.moduleDefaults && this.moduleDefaults.fields) {
          let availableFields = this.metaFields.map(field => field.name)
          this.moduleDefaults.fields = this.moduleDefaults.fields.filter(
            field =>
              field.field &&
              !['noOfTasks', 'noOfNotes', 'noOfAttachments'].includes(
                field.field.name
              ) &&
              availableFields.includes(field.field.name)
          )
        }
        this.loadInitalFilters()
      })
    })
  },
  methods: {
    handleTextSize(val) {
      if (!val) {
        return
      }
      if (this.isPrinting) {
        this.isZoomSmall = val.length > 10
        this.isZoomMedium = val.length > 7 && val.length <= 10
        this.isZoomLarge = val.length <= 7
      }
    },
    reverseIterate(timeString, periodObject) {
      return moment(timeString, periodObject.format).valueOf()
    },
    getMillis(timeString, momentFormat, period) {
      let time = []
      let periodString = this.getMomentPeriod(period)
      time.push(
        moment(timeString, momentFormat)
          .startOf(periodString)
          .valueOf()
      )
      time.push(
        moment(timeString, momentFormat)
          .endOf(periodString)
          .valueOf()
      )
      return time
    },
    getMomentPeriod(period) {
      if (period === 'hourly') {
        return 'hour'
      } else if (period === 'daily') {
        return 'day'
      } else if (period === 'weekly') {
        return 'week'
      } else if (period === 'monthly') {
        return 'month'
      }
    },
    setDimensionFilter(val) {
      this.dimensionValues = val
      this.loadInitalFilters()
    },
    setGroupByFilter(val) {
      this.groupByValues = val
      this.loadInitalFilters()
    },
    getDefaultFilters() {
      let defaultFilter = {}
      if (
        this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
        this.resultObject.report.dataPoints[0].xAxis.dataType === 6 ||
        this.resultObject.report.dateOperator !== -1
      ) {
        // x axis has time
        let fieldName =
          this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
          this.resultObject.report.dataPoints[0].xAxis.dataType === 6
            ? this.resultObject.report.dataPoints[0].xAxis.fieldName
            : this.resultObject.report.dataPoints[0].dateField.fieldName
        let dateOperatorId = 20
        let dateValues = this.resultObject.dateRange.value
        let temp = {}
        temp['operatorId'] = dateOperatorId
        temp['value'] = dateValues.map(element => element + '')
        defaultFilter[fieldName] = temp
      }
      // if crietria is enabled
      if (
        this.resultObject.report.dataPoints[0].criteria &&
        this.resultObject.report.dataPoints[0].criteria !== null
      ) {
        for (let conditionIdx in this.resultObject.report.dataPoints[0].criteria
          .conditions) {
          let condition = this.resultObject.report.dataPoints[0].criteria
            .conditions[conditionIdx]
          let fieldName = condition.fieldName
          let value = null
          if (condition.value) {
            value = condition.value.split(',')
          } else {
            value = []
          }
          let temp = {}
          temp['operatorId'] = condition.operatorId
          temp['value'] = value
          defaultFilter[fieldName] = temp
        }
      }

      // applying user filters
      if (this.resultObject.report.userFilters) {
        // user filters are only lookupField
        for (let userFilterConfig of this.resultObject.report.userFilters) {
          if (userFilterConfig.criteria) {
            let conditions = userFilterConfig.criteria.conditions
            for (let condition of Object.keys(conditions)) {
              let temp = {}
              temp['operatorId'] = conditions[condition].operatorId
              if (conditions[condition].value?.split(',').length > 1) {
                temp['value'] = conditions[condition].value.split(',')
              } else {
                temp['value'] = [conditions[condition].value]
              }
              defaultFilter[conditions[condition].fieldName] = temp
            }
          }
        }
      }
      return defaultFilter
    },
    getFilters() {
      let filters = this.getDefaultFilters()
      // apply xFilter
      if (this.dimensionValues !== null) {
        let xFieldName = this.resultObject.report.dataPoints[0].xAxis.fieldName
        let temp = {}
        if (
          this.resultObject.report.dataPoints[0].xAxis.fieldId === -1 &&
          xFieldName !== 'siteId'
        ) {
          let dimensionName = this.computedDimensionValues.filter(
            element => element.id === this.dimensionValues
          )[0].value
          let condition = this.resultObject.report.dataPoints[0].xAxis.field
            .conditions[dimensionName]
          let temp = {}
          temp['operatorId'] = condition.operatorId
          if (condition.value.split(',').length > 1) {
            temp['value'] = condition.value.split(',')
          } else {
            temp['value'] = [condition.value]
          }
          filters[condition.fieldName] = temp
        } else if (
          this.resultObject.report.dataPoints[0].xAxis.field.lookupModule &&
          ((this.resultObject.report.dataPoints[0].xAxis.field.lookupModule
            .name === 'resource' &&
            [21, 22, 23, 26].includes(this.resultObject.report.xAggr)) ||
            this.resultObject.report.dataPoints[0].xAxis.field.lookupModule
              .name === 'basespace')
        ) {
          temp['operatorId'] = 38
          temp['value'] = [this.dimensionValues]
          filters[xFieldName] = temp
        } else if (
          this.resultObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'lookup'
        ) {
          temp['operatorId'] = this.operatorId
          temp['value'] = [this.dimensionValues]
          filters[xFieldName] = temp
        } else if (
          this.resultObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'string'
        ) {
          temp['operatorId'] = this.operatorId
          temp['value'] = [this.dimensionValues]
          filters[xFieldName] = temp
        } else {
          temp['operatorId'] =
            this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
            this.resultObject.report.dataPoints[0].xAxis.dataType === 6
              ? 20
              : this.operatorId
          if (
            this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
            this.resultObject.report.dataPoints[0].xAxis.dataType === 6
          ) {
            let objectById = this.computedDimensionValues.filter(
              element => element.id === parseInt(this.dimensionValues)
            )
            let values = objectById[0].value.map(element => element + '')
            temp['value'] = values
            filters[xFieldName] = temp
          } else {
            // for boolean and enum fields and siteId
            temp['operatorId'] = this.operatorId
            temp['value'] = [this.dimensionValues]
            filters[xFieldName] = temp
          }
        }
      } else if (this.isXFieldCrossModule === false) {
        if (
          !isEmpty(
            this.resultObject.report.dataPoints[0].xAxis.selectValuesOnly
          )
        ) {
          let chooseValues = this.resultObject.report.dataPoints[0].xAxis.selectValuesOnly.map(
            val => val.toString()
          )
          let xFieldName = this.resultObject.report.dataPoints[0].xAxis
            .fieldName
          if (
            this.resultObject.report.dataPoints[0].xAxis.field.lookupModule &&
            (this.resultObject.report.dataPoints[0].xAxis.field.lookupModule
              .name === 'resource' ||
              this.resultObject.report.dataPoints[0].xAxis.field.lookupModule
                .name === 'basespace')
          ) {
            let temp = {}
            temp['operatorId'] = 38
            temp['value'] = chooseValues
            if (!filters[xFieldName]) filters[xFieldName] = temp
          } else if (
            this.resultObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
            'lookup'
          ) {
            let temp = {}
            temp['operatorId'] = this.operatorId
            temp['value'] = chooseValues
            if (!filters[xFieldName]) filters[xFieldName] = temp
          } else {
            let xField = this.resultObject.report.dataPoints[0].xAxis
            if (xField.fieldId !== -1) {
              if (!filters[xField.fieldName])
                filters[xField.fieldName] = { operatorId: 2 }
            }
          }
        } else {
          let xField = this.resultObject.report.dataPoints[0].xAxis
          if (xField.fieldId !== -1) {
            if (!filters[xField.fieldName])
              filters[xField.fieldName] =
                this.resultObject.report.dataPoints[0].xAxis.dataTypeEnum ===
                  'MULTI_ENUM' ||
                this.resultObject.report.dataPoints[0].xAxis.dataTypeEnum ===
                  'MULTI_LOOKUP'
                  ? { operatorId: 105 }
                  : { operatorId: 2 }
          }
        }
      }
      // groupByFilters
      if (this.groupByValues !== null) {
        // group by does not have time
        let group = this.resultObject.report.dataPoints[0].groupByFields[0]
        if (group.fieldId === -1 && group.fieldName !== 'siteId') {
          let groupName = this.computedGroupByValues.filter(
            element => element.id === this.groupByValues
          )[0].value
          let condition = group.field.conditions[groupName]
          let temp = {}
          temp['operatorId'] = condition.operatorId
          if (condition.value.split(',').length > 1) {
            temp['value'] = condition.value.split(',')
          } else {
            temp['value'] = [condition.value]
          }
          filters[condition.fieldName] = temp
        } else if (
          group.field.lookupModule &&
          (group.field.lookupModule.name === 'resource' ||
            group.field.lookupModule.name === 'basespace')
        ) {
          let groupName = group.field.name
          let temp = {}
          let val = this.computedGroupByValues.filter(
            element => element.id === this.groupByValues
          )[0].id
          temp['operatorId'] = 38
          temp['value'] = [val + '']
          filters[groupName] = temp
        } else {
          let groupName = group.field.name
          let temp = {}
          let val = this.computedGroupByValues.filter(
            element => element.id === this.groupByValues
          )[0].id
          temp['operatorId'] = this.operatorId
          temp['value'] = [val + '']
          filters[groupName] = temp
        }
      } else if (
        this.isGroupByCrossModule === false &&
        this.resultObject.report.dataPoints[0].groupByFields
      ) {
        let group = this.resultObject.report.dataPoints[0].groupByFields[0]
        if (group.fieldId !== -1) {
          if (!filters[group.fieldName]) {
            filters[group.fieldName] =
              this.resultObject.report.dataPoints[0].groupByFields[0]
                .dataTypeEnum === 'MULTI_ENUM' ||
              this.resultObject.report.dataPoints[0].groupByFields[0]
                .dataTypeEnum === 'MULTI_LOOKUP'
                ? { operatorId: 105 }
                : { operatorId: 2 }
          }
        }
      }
      return filters
    },
    openSummary(rowId) {
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let viewMap = {
          tenantcontact: 'all-contacts',
          bmsalarm: 'bmsAlarm',
        }
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          let viewname = viewMap[moduleName] || 'all'
          let routerPath = this.$router.resolve({
            name,
            params: { viewname, id: rowId },
          }).href

          window.open(routerPath)
        }
      } else {
        let summaryUrl = this.summaryPageMap[rowId]
        window.open(summaryUrl)
      }
    },
    getUrlBasedOnModule() {
      let url = ''
      if (typeof this.moduleName === 'undefined') {
        url = '/workorder/'
      } else {
        switch (this.moduleName) {
          case 'workorder':
            url = '/workorder/'
            break
          case 'alarm':
            url = '/alarm/'
            break
          case 'asset':
            url = '/asset/'
            break
          default:
            url = '/workorder/'
            break
        }
      }
      return url
    },
    loadMetaFields() {
      // this.loading = true
      return http.get(`/v2/modules/meta/${this.moduleName}`).then(response => {
        this.metaFields = response.data.result.meta.fields || []
        let fieldMap = {}
        this.metaFields.forEach(field => {
          fieldMap[field.name] = field
        })
        if (this.selectedFieldNames) {
          this.selectedFields = this.selectedFieldNames
            .split(',')
            .map(name => ({
              columnDisplayName: null,
              fieldId: name === 'id' ? -1 : fieldMap[name].fieldId,
              field:
                name === 'id'
                  ? { name: 'id', displayName: 'ID', default: true }
                  : fieldMap[name],
            }))
          this.loadInitalFilters()
        }
        return
      })
    },
    rerender() {
      if (this.$refs['pagination']) {
        this.$refs['pagination'].reset()
      }
    },
    loadInitalFilters() {
      this.loading = true
      let filter = this.getFilters()
      this.reportObject['filters'] = {}
      this.reportObject.filters['underlyingDataFilters'] = filter
      let filterString = encodeURIComponent(JSON.stringify(filter))

      let url = ''
      if (typeof this.moduleName === 'undefined') {
        this.moduleName = 'workorder'
        url = 'workorder/workOrderCount?viewName=all&count=true'
      } else if (this.isCustomModule) {
        url =
          '/v2/module/data/list?moduleName=' +
          this.moduleName +
          '&viewName=all&fetchCount=true&includeParentFilter=true'
      } else {
        switch (this.moduleName) {
          case 'workorder':
            url = 'workorder/workOrderCount?viewName=all&count=true'
            break
          case 'alarm':
            url = 'v2/alarms/alarmCount?viewName=all&isCount=true'
            break
          case 'asset':
            url = 'asset/assetCount?viewName=all&count=true'
            break
          default:
            url =
              '/v2/module/data/list?moduleName=' +
              this.moduleName +
              '&viewName=all&fetchCount=true&includeParentFilter=true'
            break
        }
      }
      if (Object.keys(filter).length !== 0) {
        url = url + '&filters=' + filterString
      }
      http.get(url).then(response => {
        if (this.moduleName === 'workorder') {
          this.totalCount = response.data.woCount
        } else if (this.moduleName === 'asset') {
          this.totalCount = response.data.assetCount
        } else {
          this.totalCount = response.data.result.count
        }

        let pageLimit =
          this.$route.query.pageLimit ||
          (this.printQuery &&
          this.printQuery.underlyingData === true &&
          this.printQuery.pageLimit
            ? this.printQuery.pageLimit
            : null)
        if (pageLimit) {
          if (pageLimit === 'all') {
            this.perPageCount = this.totalCount > 2000 ? 2000 : this.totalCount
          } else {
            this.perPageCount =
              typeof pageLimit === 'number'
                ? Math.abs(pageLimit)
                : Math.abs(parseInt(pageLimit))
          }
        }

        let url = this.getUrlBasedOnModule()
        url =
          url +
          this.viewName +
          '?page=' +
          this.pageNumber +
          '&' +
          'perPage=' +
          this.perPageCount

        if (
          this.isCustomModule ||
          !['workorder', 'alarm', 'asset'].includes(this.moduleName)
        ) {
          url =
            '/v2/module/data/list?moduleName=' +
            this.moduleName +
            '&page=' +
            this.pageNumber +
            '&perPage=' +
            this.perPageCount +
            '&viewName=all&includeParentFilter=true'
        }

        url = url + '&filters=' + filterString

        http.get(url).then(response => {
          let key = this.dataKey[this.moduleName]
          let dataResponse = []
          if (
            this.isCustomModule ||
            !['workorder', 'alarm', 'asset'].includes(this.moduleName)
          ) {
            dataResponse = response.data.result.moduleDatas
          } else {
            dataResponse = response.data[key]
          }
          this.recordData = []
          for (let record of dataResponse) {
            this.summaryPageMap[record.id] = record.url
            let row = {}
            row['id'] = record.id
            for (let column of this.tableHeadings) {
              if (
                column.field.default &&
                (column.field.dataType === 2 || column.field.dataType === 3)
              ) {
                if (
                  record[column.field.name] &&
                  (record[column.field.name] === null ||
                    record[column.field.name] === -1)
                ) {
                  if (
                    !column.field.default &&
                    record.data &&
                    record.data !== null &&
                    record.data[column.field.name] !== null &&
                    record.data[column.field.name] !== -1
                  ) {
                    row[column.field.name] === record.data[column.field.name]
                  } else {
                    row[column.field.name] = '--'
                  }
                } else {
                  row[column.field.name] = record[column.field.name]
                }
              } else if (
                column.field.name === 'dueDate' ||
                column.field.name === 'createdTime'
              ) {
                let data =
                  !column.field.default && record.data ? record.data : record
                if (
                  record[column.field.name + 'String'] &&
                  record[column.field.name + 'String'] !== null
                ) {
                  row[column.field.name] = record[column.field.name + 'String']
                } else if (
                  record[column.field.name] !== null &&
                  record[column.field.name] !== -1
                ) {
                  row[column.field.name] = record[column.field.name]
                } else {
                  if (
                    data[column.field.name] !== null &&
                    data[column.field.name] !== -1
                  ) {
                    row[column.field.name] === record.data[column.field.name]
                  } else if (
                    record[column.field.module.name] !== null &&
                    record[column.field.module.name] !== -1 &&
                    typeof record[column.field.module.name] !== 'undefined' &&
                    record[column.field.module.name][column.field.name] !==
                      null &&
                    record[column.field.module.name][column.field.name] !== -1
                  ) {
                    row[column.field.name] ===
                      record[column.field.module.name][column.field.name]
                  } else {
                    row[column.field.name] = '--'
                  }
                }
              } else if (
                column.field.dataType === 5 ||
                column.field.dataType === 6
              ) {
                let data =
                  !column.field.default && record.data ? record.data : record
                if (
                  data[column.field.name] === -1 ||
                  data[column.field.name] === null
                ) {
                  row[column.field.name] = '--'
                } else {
                  let date =
                    record[column.field.name] !== null
                      ? record[column.field.name]
                      : typeof record[column.field.module.name] !==
                          'undefined' &&
                        record[column.field.module.name] !== null &&
                        record[column.field.module.name][column.field.name] !==
                          null
                      ? record[column.field.module.name][column.field.name]
                      : data[column.field.name]
                  if (date) {
                    if (
                      this.reportObject.dateRange &&
                      this.reportObject.dateRange.period
                    ) {
                      row[column.field.name] = this.$options.filters.formatDate(
                        date,
                        false,
                        false
                      )
                    } else {
                      row[column.field.name] = this.$options.filters.formatDate(
                        date,
                        false,
                        false
                      )
                    }
                  } else {
                    row[column.field.name] = '--'
                  }
                }
              } else if (column.field.dataType === 7) {
                if (
                  (record[column.field.name] === null ||
                    typeof record[column.field.name] === 'undefined') &&
                  record.data === null
                ) {
                  row[column.field.name] = '--'
                }
                // TO remove after formula drill down
                else if (column.field.name === 'sourceType') {
                  row[column.field.name] =
                    record[column.field.name] &&
                    record[column.field.name] !== null
                      ? this.$constants.SourceType[[record[column.field.name]]]
                      : record.data &&
                        record.data !== null &&
                        record.data[column.field.name]
                      ? this.$constants.SourceType[
                          [record.data[column.field.name]]
                        ]
                      : '--'
                }
                // else if (column.field.name === 'category') {
                //   row[column.field.name] =
                //     record[column.field.name] &&
                //     record[column.field.name].displayName !== null
                //       ? record[column.field.name].displayName
                //       : record.data &&
                //         record.data !== null &&
                //         record.data[column.field.name]
                //       ? record.data[column.field.name]
                //       : '--'
                // }
                else if (
                  column.field.name === 'resource' ||
                  column.field.name === 'assignmentGroup' ||
                  column.field.name === 'assignedTo' ||
                  (record[column.field.name] &&
                    record[column.field.name]['name'])
                ) {
                  row[column.field.name] =
                    record[column.field.name] &&
                    record[column.field.name].name !== null
                      ? record[column.field.name].name
                      : record[column.field.name] &&
                        record[column.field.name].id !== null &&
                        this.lookupMap[column.field.name]
                      ? this.lookupMap[column.field.name][
                          record[column.field.name].id
                        ]
                      : record.data &&
                        record.data !== null &&
                        record.data[column.field.name]
                      ? record.data[column.field.name].id !== null &&
                        this.lookupMap[column.field.name]
                        ? this.lookupMap[column.field.name][
                            record.data[column.field.name].id
                          ]
                        : record.data[column.field.name]
                      : '--'
                } else {
                  row[column.field.name] =
                    record[column.field.name] &&
                    record[column.field.name] !== -1
                      ? record[column.field.name]['displayName'] !== null
                        ? record[column.field.name]['displayName']
                        : record[column.field.name][column.field.name] !==
                            null &&
                          typeof record[column.field.name][
                            column.field.name
                          ] !== 'undefined'
                        ? record[column.field.name][column.field.name]
                        : record[column.field.name]['primaryValue'] !== null
                        ? record[column.field.name]['primaryValue']
                        : this.lookupMap[column.field.name] &&
                          record[column.field.name].id &&
                          this.lookupMap[column.field.name][
                            record[column.field.name].id
                          ]
                        ? this.lookupMap[column.field.name][
                            record[column.field.name].id
                          ]
                        : '---'
                      : record.data &&
                        record.data !== null &&
                        record.data[column.field.name]
                      ? record.data[column.field.name].id !== null &&
                        this.lookupMap[column.field.name]
                        ? this.lookupMap[column.field.name][
                            record.data[column.field.name].id
                          ]
                        : typeof record.data[column.field.name] === 'object' &&
                          !isEmpty(record.data[column.field.name].primaryValue)
                        ? record.data[column.field.name].primaryValue
                        : '--'
                      : '--'
                }
              } else if (column.field.dataType === 8) {
                if (column.field.enumMap === null) {
                  row[column.field.name] = '--'
                } else {
                  if (
                    record[column.field.name] &&
                    record[column.field.name] !== null
                  ) {
                    row[column.field.name] = column.field.enumMap[
                      record[column.field.name]
                    ]
                      ? column.field.enumMap[record[column.field.name]]
                      : '---'
                  } else if (
                    !column.field.default &&
                    record.data &&
                    record.data !== null &&
                    record.data[column.field.name] !== null
                  ) {
                    row[column.field.name] = column.field.enumMap[
                      record.data[column.field.name]
                    ]
                      ? column.field.enumMap[record.data[column.field.name]]
                      : '---'
                  } else if (
                    typeof record[column.field.module.name] !== 'undefined' &&
                    record[column.field.module.name] !== null &&
                    record[column.field.module.name][column.field.name] !== null
                  ) {
                    row[column.field.name] =
                      record[column.field.module.name][column.field.name]
                  } else {
                    row[column.field.name] = '--'
                  }
                }
              } else {
                let data =
                  !column.field.default && record.data ? record.data : record
                if (
                  typeof data[column.field.name] !== 'undefined' &&
                  data[column.field.name] !== null &&
                  data[column.field.name] !== -1
                ) {
                  row[column.field.name] = data[column.field.name]
                  if (
                    column.field.dataType === 2 &&
                    column.field.displayTypeInt === 23
                  ) {
                    row[column.field.name] = this.$helpers.getFormattedDuration(
                      row[column.field.name],
                      'seconds'
                    )
                  } else if (
                    column.field.dataType === 14 &&
                    column.field.values
                  ) {
                    let multi_val_list = row[column.field.name]
                    let result_val = []
                    for (let val_id in multi_val_list) {
                      if (
                        multi_val_list[val_id] &&
                        column.field.enumMap[multi_val_list[val_id]]
                      ) {
                        result_val.push(
                          column.field.enumMap[multi_val_list[val_id]]
                        )
                      }
                    }
                    row[column.field.name] = result_val
                  } else if (
                    column.field.dataType === 13 &&
                    column.field.lookupMap
                  ) {
                    let multi_val_list = row[column.field.name]
                    let result_val = []
                    for (let val_id in multi_val_list) {
                      if (multi_val_list[val_id].id) {
                        result_val.push(
                          column.field.lookupMap[multi_val_list[val_id].id]
                        )
                      }
                    }
                    row[column.field.name] = result_val
                  }
                } else if (
                  record[column.field.module.name] !== null &&
                  record[column.field.module.name] !== -1 &&
                  typeof record[column.field.module.name] !== 'undefined' &&
                  record[column.field.module.name][column.field.name] !==
                    null &&
                  record[column.field.module.name][column.field.name] !== -1
                ) {
                  row[column.field.name] =
                    record[column.field.module.name][column.field.name]
                  if (
                    column.field.dataType === 2 &&
                    column.field.displayTypeInt === 23
                  ) {
                    row[column.field.name] = this.$helpers.getFormattedDuration(
                      row[column.field.name],
                      'seconds'
                    )
                  }
                } else {
                  row[column.field.name] = '---'
                }
              }
            }
            this.recordData.push(row)
          }
          this.loading = false
        })
      })
    },
    computeTableHeadings() {
      if (this.selectedFields) {
        this.tableHeadings = this.selectedFields
        return
      }
      let fields = []
      if (this.moduleDefaults) {
        for (let field of this.moduleDefaults.fields) {
          if (field.field && field.field.dataType === 7) {
            let moduleName = field.field.lookupModule.name
            getFieldOptions({
              field: { lookupModuleName: moduleName, skipDeserialize: true },
            }).then(({ error, options }) => {
              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                this.lookupMap[field.field.name] = options
              }
            })
          }
          if (fields.length !== 0) {
            let filteredFields = fields.filter(
              element => element.fieldId === field.fieldId
            )
            if (filteredFields.length === 0) {
              if (
                field.field &&
                !['noOfAttachments', 'noOfTasks', 'noOfNotes'].includes(
                  field.field.name
                )
              ) {
                fields.push(field)
              }
            } else {
              continue
            }
          } else {
            if (
              field.field &&
              !['noOfAttachments', 'noOfTasks', 'noOfNotes'].includes(
                field.field.name
              )
            ) {
              fields.push(field)
            }
          }
        }
      }

      // push xField
      let xAxisField = this.resultObject.report.dataPoints[0].xAxis
      let temp = fields.filter(
        element => element.fieldId === xAxisField.fieldId
      ) // TODO remove after formula fields are handled
      xAxisField.displayName = xAxisField.label
      xAxisField.name = xAxisField.fieldName
      if (temp.length === 0 && xAxisField.fieldId !== -1) {
        if (xAxisField.dataType === 7) {
          let moduleName = xAxisField.field.lookupModule.name
          getFieldOptions({
            field: { lookupModuleName: moduleName, skipDeserialize: true },
          }).then(({ error, options }) => {
            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              this.lookupMap[xAxisField.field.name] = options
            }
          })
        }
        fields.push({
          columnDisplayName: null,
          fieldId: xAxisField.fieldId,
          field: xAxisField,
        })
      }
      // push group by field
      if (
        this.resultObject.report.dataPoints[0].groupByFields &&
        this.resultObject.report.dataPoints[0].groupByFields.length !== 0
      ) {
        temp = fields.filter(
          element =>
            element.fieldId ===
            this.resultObject.report.dataPoints[0].groupByFields[0].fieldId
        )
        if (temp.length === 0 && !this.isGroupByFormulaField) {
          let groupByField = this.resultObject.report.dataPoints[0]
            .groupByFields[0]
          if (groupByField.dataType === 7 || groupByField.dataType === 13) {
            let moduleName = groupByField.field.lookupModule.name
            if (groupByField.dataType === 13) {
              groupByField.field.lookupMap = groupByField.lookupMap
            }
            getFieldOptions({
              field: { lookupModuleName: moduleName, skipDeserialize: true },
            }).then(({ error, options }) => {
              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                this.lookupMap[groupByField.field.name] = options
              }
            })
          }
          fields.push({
            columnDisplayName: null,
            fieldId: groupByField.fieldId,
            field: groupByField.field,
          })
        }
      }
      this.tableHeadings = fields
    },
  },
  props: ['reportObject', 'resultObject', 'module'],
}
</script>

<style>
.underlying-scroll {
  overflow-x: scroll;
  border: 1px solid #e6ecf3;
  margin-bottom: 100px;
}
.underlying-scroll .setting-list-view-table {
  width: 100%;
}
.zoom95 {
  zoom: 90% !important;
}
.zoom40 {
  zoom: 50% !important;
}
.zoom35 {
  zoom: 39% !important;
}
.underlying-worker-td {
  word-break: break-all;
}
@media print {
  .underlying-scroll {
    border: none !important;
    overflow-x: inherit !important;
    zoom: 35%;
  }
  .underlying-scroll table {
    table-layout: auto;
  }
  .setting-list-view-table tr {
    border: 1px solid #2d394c !important;
  }
  .underlying-worker-td {
    width: auto;
  }
  .width272px {
    width: 272px !important;
  }
  .setting-list-view-table td {
    padding: 15px !important;
  }
}
</style>
