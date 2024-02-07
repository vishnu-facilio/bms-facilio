<template>
  <div v-if="hasImpact && hasWidgetParams">
    <FNewAnalyticReport :config="analyticsConfig"></FNewAnalyticReport>
    <div class="fc-summary-content-div">
      <div
        @click="jumpToAnalytics()"
        class="content analytics-txt jump-to-style"
      >
        {{ $t('common.products.go_to_analytics') }}
        <img class="arrow-size" src="~statics/icons/right-arrow.svg" />
      </div>
    </div>
  </div>
  <div v-else class="flex-center-vH">
    <div class="text-center flex-center-vH align-center">
      <InlineSvg
        src="svgs/emptystate/alarmEmpty"
        iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
      ></InlineSvg>
      <div class="pT20 fc-black-dark f18 bold">
        {{ $t('common.products.no_impact') }}!
      </div>
    </div>
  </div>
</template>
<script>
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import newDateHelper from '@/mixins/NewDateHelper'
import JumpToHelper from '@/mixins/JumpToHelper'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: { FNewAnalyticReport },
  mixins: [newDateHelper, JumpToHelper],
  props: ['details', 'layoutParams', 'resizeWidget', 'primaryFields', 'widget'],
  computed: {
    hasImpact() {
      let { details } = this
      let { alarm } = details || {}
      let { rule } = alarm || {}
      let { impact } = rule || {}
      return !isEmpty(impact)
    },
    hasWidgetParams() {
      let { widget } = this
      let { widgetParams } = widget || {}
      return !isEmpty(widgetParams)
    },
    lastOccurredTime() {
      let { details } = this
      let { alarm } = details || {}
      let { lastOccurredTime } = alarm || {}
      return lastOccurredTime
    },
    costImpactFieldId() {
      let { widget } = this
      let { widgetParams } = widget || {}
      let { costImpactId } = widgetParams || {}
      return costImpactId
    },
    energyImpactFieldId() {
      let { widget } = this
      let { widgetParams } = widget || {}
      let { energyImpactId } = widgetParams || {}
      return energyImpactId
    },
    parentId() {
      let { details } = this
      let { alarm } = details || {}
      let { resource } = alarm || {}
      let { id } = resource || {}
      return id
    },
    analyticsConfig() {
      let {
        analyticsDefaultConfig,
        lastOccurredTime,
        costImpactFieldId,
        energyImpactFieldId,
      } = this
      let { params, customizeChartOptions } = analyticsDefaultConfig
      let fields = [],
        dataPoints = []
      let costImpactField = this.getField('Cost Impact', costImpactFieldId)
      let energyImpactField = this.getField(
        'Energy Impact',
        energyImpactFieldId
      )
      let costImpactDataPoint = this.getDataPoint(
        'Cost Impact',
        costImpactFieldId,
        '#e0113a'
      )
      let energyImpactDataPoint = this.getDataPoint(
        'Energy Impact',
        energyImpactFieldId,
        '#afe00e'
      )
      fields.push(costImpactField, energyImpactField)
      dataPoints.push(costImpactDataPoint, energyImpactDataPoint)
      let dateFilter = newDateHelper.getDatePickerObject(
        63,
        `${lastOccurredTime}`
      )
      this.$setProperty(params, 'fields', JSON.stringify(fields))
      this.$setProperty(customizeChartOptions, 'dataPoints', dataPoints)
      this.$setProperty(analyticsDefaultConfig, 'dateFilter', dateFilter)
      return analyticsDefaultConfig
    },
  },
  data() {
    return {
      analyticsDefaultConfig: {
        analyticsType: 2,
        hidechartoptions: true,
        hidetabular: true,
        isFromFaultInsight: true,
        hideAlarmSubject: true,
        showAlarms: false,
        groupByMetrics: true,
        showWidgetLegends: false,
        dateFilter: {},
        params: {
          xAggr: 0,
          mode: 1,
        },
        customizeChartOptions: {
          dataPoints: [],
        },
      },
    }
  },
  methods: {
    getField(name, fieldId) {
      let { parentId } = this
      return {
        parentId: [parentId],
        name: name,
        yAxis: {
          fieldId: fieldId,
          aggr: 2,
          dataType: 3,
          label: name,
        },
        aliases: {
          actual: name,
        },
        type: 1,
        duplicateDataPoint: false,
      }
    },
    getDataPoint(name, fieldId, color) {
      let { parentId } = this
      return {
        aggr: 2,
        safelimit: false,
        visible: true,
        type: 'datapoint',
        axes: 'y',
        chartType: '',
        pointType: 1,
        dataType: 'DECIMAL',
        dataTypeId: 3,
        xDataPoint: false,
        duplicateDataPoint: false,
        parentId: parentId,
        label: name,
        name: name,
        key: name,
        color: color,
        alias: name,
        fieldId: fieldId,
      }
    },
    jumpToAnalytics() {
      let { analyticsConfig } = this
      let { dateFilter, params } = analyticsConfig || {}
      let { fields } = params
      let dataPoints = JSON.parse(fields)
      this.jumpReadingsToAnalytics(dataPoints, dateFilter, 0, null)
    },
  },
}
</script>
<style scoped>
.arrow-size {
  width: 13px;
  height: 9px;
}
.jump-to-style {
  cursor: pointer;
  color: rgb(57, 178, 194);
  font-size: 13px;
  text-align: right;
  font-weight: 500;
  margin-right: 20px;
}
</style>
