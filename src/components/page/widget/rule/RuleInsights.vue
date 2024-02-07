<template>
  <div
    class="p30 pT30 d-flex flex-direction-column metrics-card rule-insights-page"
    v-if="values"
  >
    <div class="bold text-uppercase pB10 fc-black-12 text-left letter-spacing1">
      {{ isNewAlarm ? 'No of Faults' : 'Mean time to Trigger' }}
    </div>
    <el-row v-if="isNewAlarm">
      <el-col :span="8">
        <div class="flex-middle position-relative rule-insights-arrow">
          <div
            class="f26 featured flex-middle"
            v-html="stats.noOfOccurrence"
          ></div>
          <inline-svg
            src="svgs/arrow"
            :iconClass="
              `${getTrendClasses(
                values.noOfOccurrence,
                values.noOfOccurrenceTillLstMnt
              )} icon arrow mL10 icon-lg up-down-arrow`
            "
          ></inline-svg>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="flex-middle pT7">
          <span class="f13 mL30 letter-spacing0_5 flex-middle">
            <span
              class="fwBold letter-spacing0_5 flex-middle pR5"
              v-html="stats.noOfOccurrenceTillLstMnt"
            ></span>
            {{ $t('asset.performance.till_last_month') }}
          </span>
        </div>
      </el-col>
    </el-row>
    <el-row v-else>
      <el-col :span="8">
        <div class="flex-middle position-relative rule-insights-arrow">
          <div class="f26 featured flex-middle" v-html="stats.mtbt"></div>
          <inline-svg
            src="svgs/arrow"
            :iconClass="
              `${getTrendClasses(
                values.mtbt,
                values.pmtbt
              )} icon arrow mL10 icon-lg up-down-arrow`
            "
          ></inline-svg>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="flex-middle pT7">
          <span class="f13 mL30 letter-spacing0_5 flex-middle">
            <span
              class="fwBold letter-spacing0_5 flex-middle pR5"
              v-html="stats.pmtbt"
            ></span>
            {{ $t('asset.performance.till_last_month') }}
          </span>
        </div>
      </el-col>
    </el-row>
    <hr class="separator-line width100" />
    <div class="bold text-uppercase pB10 text-left fc-black-12 letter-spacing1">
      {{ $t('rule.create.mean_time_clear') }}
    </div>
    <div class>
      <el-row>
        <el-col :span="8">
          <div class="flex-middle position-relative rule-insights-arrow">
            <div class="f26 featured flex-middle" v-html="stats.mttc"></div>
            <inline-svg
              src="svgs/arrow"
              :iconClass="
                `${getTrendClasses(
                  values.mttc,
                  values.pmttc
                )} icon arrow mL10 icon-lg up-down-arrow`
              "
            ></inline-svg>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="flex-middle pT7">
            <span class="f13 mL30 letter-spacing0_5 flex-middle">
              <span
                class="fwBold letter-spacing0_5 flex-middle pR5"
                v-html="stats.pmttc"
              ></span>
              {{ $t('asset.performance.till_last_month') }}
            </span>
          </div>
        </el-col>
      </el-row>
    </div>
    <hr class="separator-line width100" />
    <div class="bold text-uppercase pB10 fc-black-12 text-left letter-spacing1">
      {{
        isNewAlarm ? 'Mean time taken to acknowledge' : 'Overall Alarm Duration'
      }}
    </div>
    <div class>
      <el-row>
        <el-col :span="8">
          <div class="flex-middle position-relative rule-insights-arrow">
            <div
              class="f26 featured flex-middle"
              v-html="
                isNewAlarm ? stats.acknowledgedDuration : stats.timeToClear
              "
            ></div>
            <inline-svg
              src="svgs/arrow"
              :iconClass="
                `${getTrendClasses(
                  isNewAlarm ? values.acknowledgedDuration : values.timeToClear,
                  isNewAlarm
                    ? values.tillLstMntAcknowledgedDuration
                    : values.previousMonthTimeToClear
                )} icon arrow mL10 icon-lg up-down-arrow`
              "
            ></inline-svg>
          </div>
        </el-col>

        <el-col :span="12">
          <div class="flex-middle pT7">
            <span class="f13 mL30 letter-spacing0_5 flex-middle">
              <span
                class="fwBold letter-spacing0_5 flex-middle pR5"
                v-html="
                  isNewAlarm
                    ? stats.tillLstMntAcknowledgedDuration
                    : stats.previousMonthTimeToClear
                "
              ></span>
              {{ $t('asset.performance.till_last_month') }}
            </span>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details'],
  computed: {
    stats() {
      if (isEmpty(this.values)) {
        return {
          mtbt: '00:00 <span class="period">Hrs</span>',
          pmtbt: '00:00 <span class="period">Hrs</span>',
          mttc: '00:00 <span class="period">Hrs</span>',
          pmttc: '00:00 <span class="period">Hrs</span>',
          timeToClear: '00:00 <span class="period">Hrs</span>',
          previousMonthTimeToClear: '00:00 <span class="period">Hrs</span>',
          acknowledgedDuration: '00:00 <span class="period">Hrs</span>',
          tillLstMntAcknowledgedDuration:
            '00:00 <span class="period">Hrs</span>',
          noOfOccurrence: '--',
          noOfOccurrenceTillLstMnt: '--',
        }
      } else {
        return Object.entries(this.values).reduce((result, [key, value]) => {
          if (key === 'noOfOccurrence' || key === 'noOfOccurrenceTillLstMnt') {
            if (value > 1) {
              result[key] = `${value} <span class="period">Faults</span>`
            } else if (value === 0) {
              result[key] = `${value} <span class="period">Fault</span>`
            } else {
              result[key] = `<span class="period">No alarm</span>`
            }
          } else {
            result[key] = this.getFormattedDuration(value, 'milliseconds')
          }
          return result
        }, {})
      }
    },
    ruleId() {
      let { details } = this
      let { moduleName } = details
      let ruleId = null
      if (moduleName === 'newreadingrules') {
        ruleId = this.$getProperty(details, 'id', null)
      } else {
        let { preRequsite } = details
        ruleId = this.$getProperty(preRequsite, 'id', null)
      }
      return ruleId
    },
  },
  data() {
    return {
      values: null,
      meanTimeBWFailure: null,
      isNewAlarm: this.$helpers.isLicenseEnabled('NEW_ALARMS'),
    }
  },
  mounted() {
    this.loadInsightsDetails()
  },
  methods: {
    loadInsightsDetails() {
      let workFlowId
      if (this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        workFlowId = 53
      } else {
        workFlowId = 101
      }
      let { ruleId } = this
      this.$util.getdefaultWorkFlowResult(workFlowId, ruleId).then(d => {
        this.values = d
      })
    },
    getTrendClasses(durationA, durationB) {
      if (durationA === 0 && durationB === 0) return 'hide'
      return durationA - durationB ? 'fill-green' : 'fill-red rotate-bottom'
    },
    getFormattedDuration(value, format) {
      if (!value) return '00:00 <span class="period ">Hrs</span>'

      let duration = moment.duration(parseInt(value, 10), format)
      let days = parseInt(duration.asDays(), 10)
      let hours = duration.hours()
      let minutes = duration.minutes()
      let seconds = duration.seconds()

      const pad = val => String(val).padStart(2, '0')

      if (days > 0) {
        return hours
          ? `${pad(days)} <span class="period">Days</span> ${pad(
              hours
            )} <span class="period pT8">Hrs</span>`
          : `${pad(days)} <span class="period">Days</span>`
      } else if (hours > 0) {
        return minutes
          ? `${pad(hours)} <span class="period">Hrs</span> ${pad(
              minutes
            )} <span class="period pT8">Mins</span>`
          : `${pad(hours)} <span class="period">Hrs</span>`
      } else if (minutes > 0) {
        return `${pad(minutes)}:${pad(
          seconds
        )} <span class="period">Mins</span>`
      } else {
        return `${pad(seconds)} <span class="period">Secs</span>`
      }
    },
  },
}
</script>

<style lang="scss">
.rule-insights-page {
  .flex-middle {
    display: flex !important;
  }
  .rule-insights-arrow .up-down-arrow {
    position: absolute;
    right: -26px;
    top: -9px;
  }
  .rule-insights-arrow .period {
    padding-right: 5px;
  }
  .period {
    padding-left: 5px;
  }
}
</style>
