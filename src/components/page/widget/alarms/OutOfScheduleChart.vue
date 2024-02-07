<template>
  <div class="height100 d-flex rule-impact-page">
    <div v-if="widget.widgetParams.isEmpty" class="block">
      <div class="mT40 mB40 text-center p30imp">
        <InlineSvg
          src="svgs/emptystate/alarmEmpty"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
        ></InlineSvg>
        <div class="pT20 fc-black-dark f18 bold">No Alarm!</div>
      </div>
    </div>
    <div v-else class="height100">
      <common-widget-chart
        v-bind="$props"
        key="impactdetails"
        type="stackedbar"
        showPeriodSelect="true"
        :customizeChartOptions="getDatePickerObject()"
        moduleName="operationalarmoccurrence"
        isWidget="true"
      >
        <template slot="title">Operation Schedule Violation Report</template>
      </common-widget-chart>
    </div>
  </div>
</template>

<script>
import CommonWidgetChart from '@/page/widget/performance/charts/CommonWidgetChart'
import newDateHelper from '@/mixins/NewDateHelper'

export default {
  props: [
    'details',
    'layoutParams',
    'resizeWidget',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'widget',
  ],
  components: { CommonWidgetChart },
  data() {
    return {}
  },
  methods: {
    getDatePickerObject() {
      let dateObj = newDateHelper.getDatePickerObject(
        20,
        this.details.alarm
          ? [
              this.details.occurrence.createdTime,
              this.details.alarm.lastOccurredTime,
            ]
          : null
      )

      return { dateFilter: JSON.stringify(dateObj) }
    },
  },
}
</script>

<style lang="scss">
.rule-impact-page {
  .chart-icon {
    position: absolute;
    right: 20px;
    top: 80px;
    margin-left: 0 !important;
    .el-select__caret {
      line-height: 25px;
    }
  }
  .analytics-section {
    margin-top: 20px;
  }
}
</style>
