<template>
  <div class="d-flex flex-direction-column">
    <div
      v-show="datePickerHide ? false : true"
      class="border-bottom1px d-flex justify-content-space"
    >
      <div class="f16 bold mT20 mB20 mL30 inline">
        <slot name="title"></slot>
      </div>
      <div :class="{ mT15: isDateFixed, mT10: !isDateFixed }">
        <new-date-picker
          :zone="$timezone"
          class="filter-field date-filter-comp"
          :dateObj="dateObj"
          @date="changeDateFilter"
          :isDateFixed="isDateFixed"
        ></new-date-picker>
      </div>
    </div>
    <div
      class="widget-chart-container"
      v-if="!$validation.isEmpty(moduleMeta) && isChartPrepared && !loading"
    >
      <f-new-analytic-modular-report
        :serverConfig.sync="serverConfig"
        :module="moduleObj"
        :defaultChartType="type || 'spline'"
        :hideTabs="true"
        :hideHeader="true"
        :hidecharttypechanger="true"
        :chartType="type"
        :isWidget="isWidget"
        :showPeriodSelect="showPeriodSelect"
        @reportLoaded="onReportLoaded"
      ></f-new-analytic-modular-report>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import NewDatePicker from '@/NewDatePicker'
import FNewAnalyticModularReport from 'src/pages/energy/analytics/components/FNewAnalyticModularReport'
import NewDateHelper from 'src/components/mixins/NewDateHelper'

export default {
  props: [
    'details',
    'layoutParams',
    'resizeWidget',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'widget',
    'moduleName',
    'type',
    'refresh',
    'datePickerHide',
    'isWidget',
    'customizeChartOptions',
    'showPeriodSelect',
    'isDateFixed',
  ],
  components: { NewDatePicker, FNewAnalyticModularReport },
  data() {
    return {
      loading: false,
      isChartPrepared: false,
      moduleMeta: {},
      chartType: 'line',
      serverConfig: {
        criteria: null,
        dateFilter: null,
        xField: null,
        yField: null,
      },
      moduleObj: null,
      dateObj: NewDateHelper.getDatePickerObject(44, null),
    }
  },
  created() {
    this.initializeReport()
    this.getMeta().then(this.prepareReportData)
  },

  methods: {
    initializeReport() {
      let {
        dateOperator,
        dateOperatorValue,
      } = this.widget.widgetParams.chartParams

      this.dateObj = NewDateHelper.getDatePickerObject(
        dateOperator,
        dateOperatorValue
      )
    },

    getMeta() {
      this.loading = true
      let { moduleName } = this || this.widget.widgetParams.chartParams
      return this.$http
        .get('/module/meta?moduleName=' + moduleName)
        .then(response => {
          this.moduleMeta = response.data.meta

          this.moduleObj = {
            moduleName: response.data.meta.name,
            moduleId: response.data.meta.module.moduleId,
            meta: {
              fieldMeta: {},
            },
          }

          this.loading = false
        })
    },

    prepareReportData() {
      let { customizeChartOptions } = this
      if (isEmpty(this.moduleMeta)) return

      let {
        criteria,
        chartType,
        xField,
        yField,
        groupBy,
        isMultipleMetric,
        dateField,
      } = this.widget.widgetParams.chartParams

      if (criteria) {
        Object.values(criteria.conditions).forEach(condition => {
          delete condition.operator
        })
      }

      let xFieldObj = (this.moduleMeta.fields || {}).find(
        field => field.name === xField.fieldName
      )
      let groupByFieldObj = null
      let yFieldObj = null
      let dateFieldObj = null
      if (dateField) {
        dateFieldObj = (this.moduleMeta.fields || {}).find(
          field => field.name === dateField.fieldName
        )
      }
      let newConfig = {
        criteria,
        xField: {
          field_id: xFieldObj.fieldId,
          module_id: xFieldObj.moduleId,
          aggr: xField.aggr,
        },
        dateFilter: { ...this.dateObj },
      }
      if (xFieldObj && xFieldObj.dataType === 6 && newConfig.dateFilter) {
        let tempdateField = {
          field_id: xFieldObj.fieldId,
          fieldName: xFieldObj.name,
          module_id: xFieldObj.moduleId,
          operator: newConfig.dateFilter.operatorId,
        }
        if (
          newConfig.dateFilter.operatorId === 49 ||
          newConfig.dateFilter.operatorId === 50 ||
          newConfig.dateFilter.operatorId === 51
        ) {
          tempdateField['date_value'] =
            Math.abs(newConfig.dateFilter.offset) + ''
        } else if (newConfig.dateFilter.operatorId === 20) {
          tempdateField['date_value'] = newConfig.dateFilter.value.join(',')
        } else {
          tempdateField['date_value'] = newConfig.dateFilter.value[0] + ''
        }
        newConfig.dateField = tempdateField
        newConfig.isTime = true
      } else if (dateFieldObj) {
        let tempDateField = {
          field_id: dateFieldObj.fieldId,
          fieldName: dateFieldObj.name,
          module_id: dateFieldObj.moduleId,
          operator: newConfig.dateFilter.operatorId,
        }
        if (
          newConfig.dateFilter.operatorId === 49 ||
          newConfig.dateFilter.operatorId === 50 ||
          newConfig.dateFilter.operatorId === 51
        ) {
          tempDateField['date_value'] =
            Math.abs(newConfig.dateFilter.offset) + ''
        } else if (newConfig.dateFilter.operatorId === 20) {
          tempDateField['date_value'] = newConfig.dateFilter.value.join(',')
        } else {
          tempDateField['date_value'] = newConfig.dateFilter.value[0] + ''
        }
        newConfig.dateField = tempDateField
        newConfig.isTime = true
      } else {
        newConfig.dateField = null
        newConfig.isTime = false
      }

      if (isMultipleMetric) {
        newConfig['yField'] = []
        for (let yObj of yField) {
          if (yObj) {
            yFieldObj = (this.moduleMeta.fields || {}).find(
              field => field.name === yObj.fieldName
            )
            newConfig.yField.push({
              field_id: yFieldObj.fieldId,
              module_id: yFieldObj.moduleId,
              aggr: yObj.aggr,
            })
          } else {
            newConfig.yField.push(null)
          }
        }
      } else if (yField) {
        yFieldObj = (this.moduleMeta.fields || {}).find(
          field => field.name === yField.fieldName
        )
        newConfig['yField'] = [
          {
            field_id: yFieldObj.fieldId,
            module_id: yFieldObj.moduleId,
            aggr: yField.aggr,
          },
        ]
      } else {
        newConfig['yField'] = null
      }

      if (groupBy.fieldName) {
        if (groupBy.fieldName === 'plannedvsunplanned') {
          groupByFieldObj = {
            fieldId: 'plannedvsunplanned',
            module_id: xFieldObj.moduleId,
          }
        } else {
          groupByFieldObj = (this.moduleMeta.fields || {}).find(
            field => field.name === groupBy.fieldName
          )
        }
        newConfig['groupBy'] = [
          {
            field_id: groupByFieldObj.fieldId,
            module_id: groupByFieldObj.moduleId,
          },
        ]
      }

      this.serverConfig = {
        ...this.serverConfig,
        ...newConfig,
      }

      if (!isEmpty(customizeChartOptions)) {
        this.$set(
          this.serverConfig,
          'customizeChartOptions',
          customizeChartOptions
        )
      }

      this.chartType = chartType
      this.isChartPrepared = true
    },

    changeDateFilter(dateFilter) {
      this.dateObj = dateFilter
    },

    onReportLoaded(report, result) {
      this.$emit('reportLoaded', report, result)
    },
  },
  watch: {
    dateObj() {
      this.prepareReportData()
    },
    refresh() {
      this.prepareReportData()
    },
  },
}
</script>
