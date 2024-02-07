<template>
  <div class="anomalies-widget-page">
    <div class="alarm-summary-graph-container anomalies-graph-con">
      <div class="alarm-summary-graph-inner">
        <div class="alarm-summary-graph-header">
          <div class="fc-black-16 bold fL">
            Anomalies Report
          </div>
        </div>
        <div class="">
          <f-new-analytic-report
            v-if="analyticsConfig"
            class="alarm-summary-chart anomalies-summary-chart"
            ref="newAlarmAnalyticReport"
            :config="analyticsConfig"
          >
            <div slot="relatedAlarmBarLayout">
              <!-- <related-assets v-if="details.alarm" :id="details.alarm.resource.id" :dateFilterQuery="getDatePickerObject()" @ruleCount="ruleCount"></related-assets> -->
            </div>
          </f-new-analytic-report>
        </div>

        <!-- <div class="analytics-txt pB0">
        <div class="fc-summary-content-div">
        <div @click="jumpAlarmToAnalytics(occurrence.id, null, alarm.resource.resourceType !== 12)" class="content analytics-txt" style="cursor: pointer; color: rgb(57, 178, 194); font-size: 13px; text-align: right; font-weight: 500; margin-right: 20px;">Go to Analytics
    <img style="width:13px; height: 9px;" src="~statics/icons/right-arrow.svg" />
    </div>
    </div>
    </div> -->
        <div></div>
      </div>
    </div>
  </div>
</template>
<script>
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import JumpToHelper from '@/mixins/JumpToHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import NewReportHelper from 'pages/report/mixins/NewReportHelper'
import newDateHelper from '@/mixins/NewDateHelper'
export default {
  props: ['details', 'layoutParams', 'resizeWidget'],
  mixins: [JumpToHelper, NewReportHelper, AnalyticsMixin, newDateHelper],
  data() {
    return {
      analyticsConfig: null,
    }
  },
  watch: {},
  components: {
    FNewAnalyticReport,
  },
  computed: {
    alarm() {
      return this.details.alarm
    },
    alarmId() {
      return this.details.alarm.id
    },
    occurrence() {
      return this.details.occurrence
    },
  },
  mounted() {
    this.loadChartData()
  },
  methods: {
    ruleCount(data) {
      if (data > 0) {
        this.resizeWidget({ h: this.layoutParams.h + data * 2 })
      }
    },
    loadChartData() {
      let config = {
        alarmId: this.occurrence.id,
        dateFilter: this.getDatePickerObject(),
        hidechartoptions: true,
        hidetabular: true,
        hidecharttypechanger: true,
        fixedChartHeight: 300,
        barTitle: 'Anomaly',
        isFromAlarmSummary: true,
        period: 20,
        colors: {
          A: '#39c2b0',
          B: '#c7d1e9',
        },
        axes: {
          A: 'y',
          B: 'y',
        },
        point: {
          show: false,
        },
        pointShowRule: {
          //based on the dataRange object so it will custamize the rule show options
          offset: 1,
          operationOn: 'day',
          period: 'hourly',
          show: true,
        },
        xFormat: 'MM-DD-YYYY HH',
        disbaleNiceTickMarks: true,
        intersections: [
          {
            from: {
              point: 'B',
              color: 'red',
              disable: false,
              patter: '',
              class: 'above',
            },
            to: {
              point: 'A',
              color: 'blue',
              disable: false,
              patter: '',
              class: 'below',
            },
            context: {
              id: 'anomaliesbounds',
              class: 'anomaliesbounds',
            },
          },
        ],
        chartType: 'line',
        applyReportDate:
          this.alarm.sourceType === 12 || this.alarm.sourceType === 9,
      }
      if (this.analyticsConfig) {
        this.analyticsConfig = null
        this.$nextTick(() => {
          this.analyticsConfig = config
        })
      } else {
        this.analyticsConfig = config
      }
    },
    getDatePickerObject() {
      let lastOccurredTime
      lastOccurredTime = this.alarm.lastOccurredTime
      if (this.alarm.lastOccurredTime > 0) {
        lastOccurredTime = this.alarm.lastOccurredTime
      }
      return newDateHelper.getDatePickerObject(
        this.alarm.sourceType === 12 ? 20 : 20,
        [lastOccurredTime - 6 * 24 * 60 * 60 * 1000, lastOccurredTime]
      )
    },
  },
}
</script>
<style lang="scss">
.anomalies-summary-chart g.bb-axis.bb-axis-y2 {
  display: none;
}
.anomalies-widget-page {
  .anomalies-graph-con .anomalies-summary-chart .date-filter-comp {
    position: absolute;
    top: 4px;
    z-index: 12;
    right: 0px;
  }
  .anomalies-summary-chart {
    margin-top: 10px !important;
  }
  .alarm-summary-graph-header {
    padding: 12px 30px;
    border-bottom: 1px solid #ededed;
    height: 46px;
    line-height: 46px;
  }
  .new-analytics-filter-section {
    text-align: right !important;
  }
}
</style>
