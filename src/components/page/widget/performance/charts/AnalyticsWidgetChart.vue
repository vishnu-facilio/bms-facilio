<template>
  <div class="d-flex flex-direction-column page-analytics-widget-chart">
    <div class="width100 border-bottom1px d-flex justify-content-space">
      <div
        class="f16 bold mT20 mB20 mL30 inline supplyTemperature"
        v-if="widget.name === 'supplyTemperature'"
      >
        {{ name }}
      </div>
      <div class="f16 bold mT15 mB15 mL30 inline" v-else>{{ name }}</div>
      <!-- <div class="f12 font-normal mT20 mR30">
        <a>Go to Analytics</a>
      </div>-->
    </div>
    <div class="pT15 position-relative flex-grow">
      <spinner v-if="chartloading" :show="chartloading" size="80"></spinner>

      <f-new-analytic-report
        class="mT0"
        v-if="!loading && !$validation.isEmpty(analyticsConfig.dataPoints)"
        v-show="!chartloading"
        :config.sync="analyticsConfig"
        @reportLoaded="chartloading = false"
      ></f-new-analytic-report>
    </div>
  </div>
</template>
<script>
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import NewDateHelper from '@/mixins/NewDateHelper'

export default {
  components: {
    FNewAnalyticReport,
  },
  mixins: [NewDateHelper],
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'widget',
    'resizeWidget',
    'moduleName',
  ],
  data() {
    return {
      name: null,
      loading: false,
      chartloading: true,
      fields: null,
      analyticsConfig: {
        chartType: 'line',
        point: {
          show: false,
        },
        mode: 1,
        period: 0,
        dataPoints: [],
        dateFilter: NewDateHelper.getDatePickerObject(49, 7),
        hidechartoptions: true,
        hidetabular: true,
        hidecharttypechanger: true,
        showWidgetLegends: false,
        fixedChartHeight: 230,
        isFromAlarmSummary: true,
        zoom: {
          enabled: false,
        },
        size: {
          height: 250,
        },
      },
    }
  },
  created() {
    this.prepareChart()
  },
  methods: {
    prepareChart() {
      let { analyticsConfig: config } = this

      let points = []
      if (this.widget.name === 'supplyTemperature') {
        points.push(
          {
            parentId: this.details.id,
            yAxis: {
              fieldId: 724730,
              aggr: 2,
            },
          },
          {
            parentId: this.details.id,
            yAxis: {
              fieldId: 724732,
              aggr: 2,
            },
          },
          {
            parentId: this.details.id,
            yAxis: {
              fieldId: 724698,
              aggr: 2,
            },
          }
        )
        this.name = 'Temperature Trend'
      } else {
        points.push({
          parentId: this.details.id,
          yAxis: {
            fieldId: this.widget.name === 'supplyFeedback' ? 573794 : 724726,
            aggr: 2,
          },
        })
        this.name =
          this.widget.name === 'supplyFeedback'
            ? 'Supply Fan VFD Speed Feedback'
            : 'Cooling Coil Valve FeedBack'
        config.hideLegends = true
        config.paddingOptions = { left: 50, right: 10 }
        // config.size = {width: 600, height: 150}
      }
      config.dataPoints = points
      this.$set(this, 'analyticsConfig', config)
    },
  },
}
</script>
<style lang="scss">
.page-analytics-widget-chart {
  .new-analytics-filter-section {
    position: absolute;
    top: -44px;
    right: 0px;
  }
  .supplyTemperature .new-analytics-filter-section {
    top: -48px;
  }
}
</style>
