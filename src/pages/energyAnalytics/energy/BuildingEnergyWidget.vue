<template>
  <div
    class="building-summary-graph new-chart-css"
    style="
    height: 50%;position: relative;    box-sizing: border-box;
"
  >
    <div
      class="flex-middle justify-content-center"
      v-if="loading || chartloading"
    >
      <spinner :show="chartloading" size="80"></spinner>
    </div>
    <f-new-analytic-report
      v-if="!loading"
      v-show="!chartloading"
      ref="newAnalyticReport"
      :config.sync="analyticsConfig"
      class="pT10"
      @reportLoaded="onReportLoaded"
      :chartTypeTarget="chartTypeTarget"
    ></f-new-analytic-report>
  </div>
</template>

<script>
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import NewDateHelper from '@/mixins/NewDateHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import moment from 'moment-timezone'
export default {
  props: [
    'parentId',
    'fieldId',
    'baseline',
    'median',
    'target',
    'period',
    'chartTypeTarget',
  ],
  mixins: [AnalyticsMixin],
  components: {
    FNewAnalyticReport,
  },
  data() {
    return {
      loading: true,
      chartloading: true,
      fields: null,
      analyticsConfig: {
        name: 'Building Analysis',
        key: 'BUILDING_ANALYSIS',
        analyticsType: 2,
        hidedatepicker: true,
        hidechartoptions: true,
        colors: {
          A: '#eed9a2',
        },
        zoom: {
          enabled: false,
        },
        hidetabular: true,
        hidecharttypechanger: false,
        showWidgetLegends: false,
        axes: {
          A: 'y',
        },
        type: 'reading',
        period: 10,
        mode: 4,
        dateFilter: NewDateHelper.getDatePickerObject(44),
        chartViewOption: 0,
        dataPoints: [],
        // chartType: 'bar',
        xFormat: 'MM-YYYY',
        predictionTimings: [],
        customizeChartOptions: {
          axis: {
            x: {
              label: {
                text: '',
              },
            },
            // y: {
            //   label: {
            //     text: '',
            //   },
            // },
          },
          legend: {
            show: false,
          },
          type: 'bar',
        },
        customizeC3: {
          size: {
            height: null,
          },
          padding: {
            // bottom: 10,
            // left: 40,
            // right: 10,
            // top: 10,
          },
        },
      },
    }
  },
  computed: {},
  watch: {
    parentId: {
      handler: function(newVal, oldVal) {
        this.perepareConfig()
      },
      immediate: true,
    },
    period: {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.prepareDateFilter()
        }
      },
      immediate: true,
    },
  },
  mounted() {},
  methods: {
    perepareConfig() {
      let current = {
        visible: true,
        parentId: this.parentId,
        name: 'Current',
        reportModuleName: 'energyStarMeterData',
        aliases: {
          actual: 'A',
        },
        yAxis: {
          fieldId: this.fieldId,
          aggr: 3,
        },
      }
      this.analyticsConfig.dataPoints = [current]
      this.loading = false
    },
    onReportLoaded() {
      this.$emit('reportLoaded')
      this.chartloading = false
    },
    prepareDateFilter() {
      if (this.period) {
        if (this.period === 8) {
          this.analyticsConfig.period = 8
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            65,
            10
          )
          this.analyticsConfig.customizeChartOptions.axis.x.label.text = 'YEAR'
        } else {
          this.analyticsConfig.period = 10
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            44
          )
          this.analyticsConfig.customizeChartOptions.axis.x.label.text = 'MONTH'
        }
      }
    },
  },
}
</script>

<style>
.building-summary-graph .bb-axis.bb-axis-y2,
.building-summary-graph g.bb-ygrid-line.x-axis-zero-line {
  display: none;
}
</style>
