<template>
  <div class="alarm-summary-graph-container">
    <div class="alarm-summary-graph-inner" ref="wA-container">
      <div class="alarm-summary-graph-header">
        <div class="fc-black-16 bold">
          {{
            moduleName === 'newreadingalarm' ? 'Fault Report' : 'Alarms Report'
          }}
        </div>
      </div>
      <div class="">
        <f-new-analytic-report
          v-if="analyticsConfig"
          @reportLoaded="resizeWidgetContainer()"
          class="alarm-v1-summary-chart clearboth mT20"
          ref="newAlarmAnalyticReport"
          :config="analyticsConfig"
          :moduleName="moduleName"
        ></f-new-analytic-report>
      </div>
      <div class="analytics-txt pB0" v-if="!(moduleName === 'operationalarm')">
        <div class="fc-summary-content-div">
          <div
            @click="jumpToAnalytics()"
            class="content analytics-txt"
            style="
              cursor: pointer;
              color: rgb(57, 178, 194);
              font-size: 13px;
              text-align: right;
              font-weight: 500;
              margin-right: 20px;
            "
          >
            Go to Analytics
            <img
              style="width: 13px; height: 9px"
              src="~statics/icons/right-arrow.svg"
            />
          </div>
        </div>
      </div>
      <div>
        <div
          v-if="
            ($getProperty(occurrence, 'additionInfo') && possibleCauses) ||
              recommendation
          "
        >
          <div class="pL20 pR20 mT10 pT20 pB20 border-top3">
            <div v-if="validPossibleCause">
              <div
                class="fc-blue-label f11 text-uppercase letter-spacing07 pB3"
              >
                {{ $t('rule.create.possible_causes') }}
              </div>
              <div v-if="isNewRule">
                <li
                  v-for="(possibleCause, index) in possibleCauses"
                  :key="index"
                  class="pT14 pL15"
                >
                  {{ possibleCause }}
                </li>
              </div>
              <div
                v-else
                class="label-txt-black mT10 line-height20 space-preline break-word"
              >
                {{ possibleCauses }}
              </div>
            </div>
          </div>
          <div class="mT10 pL20 pR20 pB20">
            <div v-if="validRecommendation">
              <div class="fc-blue-label f11 text-uppercase letter-spacing07">
                {{ $t('rule.create.recommendation') }}
              </div>
              <div v-if="isNewRule">
                <li
                  v-for="(_recommendation, index) in recommendation"
                  :key="index"
                  class="pT14 pL15"
                >
                  {{ _recommendation }}
                </li>
              </div>
              <div
                v-else
                class="label-txt-black mT10 line-height20 space-preline break-word"
              >
                {{ recommendation }}
              </div>
            </div>
            <div v-else-if="!recommendation"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import JumpToHelper from '@/mixins/JumpToHelper'
import NewReportHelper from 'pages/report/mixins/NewReportHelper'
import newDateHelper from '@/mixins/NewDateHelper'
import moment from 'moment-timezone'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [JumpToHelper, NewReportHelper, AnalyticsMixin, newDateHelper],
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
  ],
  components: {
    FNewAnalyticReport,
  },
  data() {
    return {
      analyticsConfig: null,
      validPossibleCause: false,
      validRecommendation: false,
    }
  },
  computed: {
    ...mapGetters(['getAlarmSeverity']),
    alarm() {
      let { details } = this
      return this.$getProperty(details, 'alarm')
    },
    occurrence() {
      let { details } = this
      return this.$getProperty(details, 'occurrence')
    },
    resourceDetails() {
      let { details } = this
      return this.$getProperty(details, 'alarm.resource', null)
    },
    isNewRule() {
      let { details } = this
      return this.$getProperty(details, 'alarm.isNewReadingRule')
    },
    possibleCauses() {
      let { occurrence, isNewRule } = this
      let possibleCauses = this.$getProperty(
        occurrence,
        'additionInfo.possibleCauses'
      )
      if (isNewRule) {
        possibleCauses = this.$getProperty(occurrence, 'possibleCauses')
        return JSON.parse(possibleCauses)
      }
      return possibleCauses
    },
    recommendation() {
      let { occurrence, isNewRule } = this
      let recommendation = this.$getProperty(occurrence, 'recommendation')
      if (isNewRule && !isEmpty(recommendation)) {
        return JSON.parse(recommendation)
      }
      return recommendation
    },
  },
  watch: {
    occurrence: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadReport()
          this.isValidRecommendation()
          this.isValidPossibleCause()
        }
      },
      immediate: true,
    },
  },
  methods: {
    jumpToAnalytics() {
      let { occurrence, alarm, moduleName } = this
      let { id: occurrenceId } = occurrence || {}
      let { resource, spaceDetails } = alarm || {}
      let { resourceType } = resource || {}
      let { buildingId } = spaceDetails || {}
      this.jumpAlarmToAnalytics(
        occurrenceId,
        null,
        resourceType !== 12 && moduleName !== 'sensorrollupalarm',
        null,
        !isEmpty(buildingId) ? buildingId : null,
        moduleName === 'sensorrollupalarm'
      )
    },
    resizeWidgetContainer() {
      this.$nextTick(() => {
        setTimeout(d => {
          let height = this.$refs['wA-container'].scrollHeight + 40
          let width = this.$refs['wA-container'].scrollWidth
          this.resizeWidget({ height, width })
        }, 500)
      })
    },
    loadReport() {
      let { moduleName } = this
      let sourceType = this.$getProperty(this, 'alarm.sourceType')
      let config = {
        alarmId: this.occurrence?.id,
        isWithPrerequsite:
          this.$getProperty(this, 'alarm.rule') &&
          moduleName !== 'sensorrollupalarm'
            ? true
            : false,
        dateFilter: this.getDatePickerObject(),
        hidechartoptions: true,
        hidetabular: true,
        hidecharttypechanger: true,
        fixedChartHeight: 300,
        isFromAlarmSummary: true,
        hideAlarmSubject: true,
        showAlarms: true,
        groupByMetrics: true,
        applyReportDate: sourceType === 12 || sourceType === 9,
        customizeChartOptions: {
          settings: {
            chartMode: 'multi',
          },
        },
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
      let lastOccurredTime = this.$getProperty(this, 'alarm.lastOccurredTime')
      if (lastOccurredTime > 0) {
        lastOccurredTime =
          this.moduleName === 'operationalarm'
            ? this.getAlarmSeverity(this.occurrence.severity.id).severity ===
              'Clear'
              ? this.occurrence.createdTime
              : lastOccurredTime
            : lastOccurredTime
      }
      if (this.moduleName === 'operationalarm') {
        let endTime = moment(new Date(lastOccurredTime))
          .tz(this.$timezone)
          .endOf('day')
          .valueOf()
        let startTime = moment(
          new Date(lastOccurredTime - 7 * 24 * 60 * 60 * 1000)
        )
          .tz(this.$timezone)
          .startOf('day')
          .valueOf()
        return newDateHelper.getDatePickerObject(
          20,
          this.details.alarm ? [startTime, endTime] : null
        )
      } else {
        return newDateHelper.getDatePickerObject(
          this.$getProperty(this, 'alarm.sourceType') === 12 ? 20 : 62,
          '' + lastOccurredTime
        )
      }
    },
    isValidPossibleCause() {
      let { occurrence } = this
      let { additionInfo } = occurrence || {}
      let possibleCauses = this.$getProperty(additionInfo, 'possibleCauses', '')
      if (isEmpty(possibleCauses)) {
        possibleCauses = this.$getProperty(occurrence, 'possibleCauses', '')
      }
      this.validPossibleCause =
        !isEmpty(possibleCauses) && possibleCauses !== '[]'
    },
    isValidRecommendation() {
      let { occurrence } = this
      let { recommendation } = occurrence || {}

      this.validRecommendation =
        !isEmpty(recommendation) && recommendation !== '[]'
    },
  },
}
</script>
<style>
.alarm-v1-summary-chart .date-filter-comp {
  position: absolute;
  top: 10px;
  z-index: 400;
  right: 10px;
}
</style>
