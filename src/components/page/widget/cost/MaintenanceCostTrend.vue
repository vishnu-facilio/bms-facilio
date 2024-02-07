<template>
  <div class="height100 d-flex">
    <common-widget-chart
      v-bind="$props"
      key="maintenancecosttrend"
      moduleName="workorder"
      isWidget="true"
      :customizeChartOptions="options"
      @reportLoaded="onReportLoaded"
      class="maintenancecosttrend"
    >
      <template slot="title">Maintenance Cost Trend</template>
    </common-widget-chart>
    <div
      v-show="showLegends"
      class="fc-maintenance-dot flex-middle width100 pB20 pL60 bottom-0 position-absolute height60px"
    >
      <div class="flex-middle">
        <div class="fc-dot fc-dot-yellow3"></div>
        <div class="fc-black-12 pL10">
          Planned Maintenance
        </div>
      </div>
      <div class="flex-middle mL20">
        <div class="fc-dot fc-dot-red2"></div>
        <div class="fc-black-12 pL10">
          Unplanned Maintenance
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import CommonWidgetChart from '@/page/widget/performance/charts/CommonWidgetChart'

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
    return {
      showLegends: false,
    }
  },
  computed: {
    options() {
      let { yLabel, xLabel, isChartTypeLine } = this
      let options = {
        axis: {
          y: {
            label: {
              text: yLabel,
            },
          },
          x: {
            label: {
              text: xLabel,
            },
          },
        },
        customizeC3: {
          data: {
            colors: {
              Unplanned: '#ec6363',
              Planned: '#ecb163',
            },
            names: {
              Unplanned: 'Unplanned Maintenance',
              Planned: 'Planned Maintenance',
            },
          },
          // line: { zerobased: true }
        },
        general: {
          hideZeroes: !isChartTypeLine,
        },
        legend: {
          show: false,
        },
      }
      return options
    },
    chartParams() {
      return this.$getProperty(this, 'widget.widgetParams.chartParams', {})
    },
    isChartTypeLine() {
      let { chartParams } = this
      let { chartType } = chartParams || {}
      return chartType === 'line'
    },
    xLabel() {
      let { chartParams } = this
      return this.$getProperty(chartParams, 'xField.displayName', '')
    },
    yLabel() {
      let { chartParams } = this
      return this.$getProperty(chartParams, 'yField.displayName', '')
    },
  },
  methods: {
    onReportLoaded(report, result) {
      if (
        report &&
        report.data &&
        Object.keys(report.data).filter(key => key !== 'x').length
      ) {
        this.showLegends = true
      } else {
        this.showLegends = false
      }
    },
  },
}
</script>
<style>
.maintenancecosttrend .bb-axis-x-label,
.maintenancecosttrend .bb-axis-y-label,
.maintenancecosttrend .bb-axis-y2-label,
.maintenancecosttrend .bb .bb-axis-x .tick text,
.maintenancecosttrend .bb .bb-axis-y .tick text,
.maintenancecosttrend .bb .bb-axis-y2 .tick text {
  fill: #324056 !important;
}
.maintenancecosttrend .bb .bb-axis-y path.domain,
.maintenancecosttrend .bb .bb-axis-x path.domain {
  opacity: 1 !important;
  stroke: #eceef1 !important;
}
</style>
