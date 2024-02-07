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
    'unit',
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
          A: '#baf0ff',
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
            bottom: 10,
            left: 55,
            right: 10,
            top: 10,
          },
          grid: {
            y: {
              lines: [],
            },
          },
        },
      },
    }
  },
  watch: {
    lines: {
      handler: function(newVal, oldVal) {
        this.prepareConfig()
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
  computed: {
    lines() {
      return this.baseline, this.median, this.target, this.fieldId, Date.now()
    },
  },
  methods: {
    prepareConfig() {
      let current = {
        visible: true,
        parentId: this.parentId,
        name: 'Current',
        reportModuleName: 'energyStarPropertyData',
        aliases: {
          actual: 'A',
        },
        yAxis: {
          fieldId: this.fieldId,
          aggr: 2,
        },
      }
      this.analyticsConfig.dataPoints = [current]
      this.prepareDateFilter()
      this.analyticsConfig.customizeC3.grid.y.lines = []
      if (this.baseline) {
        this.analyticsConfig.customizeC3.grid.y.lines.push({
          value: this.baseline,
          text:
            'Baseline: ' + this.baseline + (this.unit ? ' ' + this.unit : ''),
          class: 'bb-y-grid-baseline',
        })
      }
      if (this.median) {
        this.analyticsConfig.customizeC3.grid.y.lines.push({
          value: this.median,
          text:
            'National Median: ' +
            this.median +
            (this.unit ? ' ' + this.unit : ''),
          class: 'bb-y-grid-median',
        })
      }
      if (this.target) {
        this.analyticsConfig.customizeC3.grid.y.lines.push({
          value: this.target,
          text: 'Target: ' + this.target + (this.unit ? ' ' + this.unit : ''),
          class: 'bb-y-grid-target',
        })
      }
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
.bb-y-grid-baseline line {
  stroke: #37a1f2;
}
.bb-y-grid-median line {
  stroke: #f98484;
}
.bb-y-grid-target line {
  stroke: #55d685;
}
.bb-y-grid-baseline text {
  fill: #37a1f2;
}
.bb-y-grid-median text {
  fill: #f98484;
}
.bb-y-grid-target text {
  fill: #55d685;
}
</style>
