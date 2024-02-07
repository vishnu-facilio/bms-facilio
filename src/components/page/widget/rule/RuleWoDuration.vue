<template>
  <div class="p30 pT40 rule-wo-card">
    <div v-if="loading" class="flex-middle fc-empty-white">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <portal :to="widget.key + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
      >
        <div class="widget-header-name" style="height: 18px;"></div>
      </div>
    </portal>
    <div v-if="values">
      <div class="fc-black-com f13 text-uppercase bold">
        {{ $t('rule.create.average_time_taken') }}
      </div>
      <div class="flex-middle pT10">
        <div class="text-left width50 border-right3">
          <div class="fc-black-com f24" v-html="avgAcknowledgeTime"></div>
          <div class="label-txt-blue f12 bold pT10">
            {{ $t('rule.create.average_resp_time') }}
          </div>
        </div>
        <div class="text-left pL30 width50">
          <div class="fc-black-com f24" v-html="avgCompletionTime"></div>
          <div class="fc-grey2 f12 bold pT10">
            {{ $t('rule.create.average_resolution_time') }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { getFormattedDuration } from '../utils/index'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details', 'widget'],
  data() {
    return {
      loading: false,
      values: null,
      avgAcknowledgeTime: null,
      avgCompletionTime: null,
    }
  },
  mounted() {
    this.loadRuleWoDetails()
    if (this.details && !isEmpty(this.details.alarmRulewoSummary)) {
      this.avgCompletionTime = getFormattedDuration(
        this.details.alarmRulewoSummary.avgResolutionTimeInMins,
        'seconds'
      )
      this.avgAcknowledgeTime = getFormattedDuration(
        this.details.alarmRulewoSummary.avgResponseTimeInMins,
        'seconds'
      )
    }
  },
  computed: {
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
  methods: {
    loadRuleWoDetails() {
      this.loading = true
      let workFlowId = null
      if (this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        workFlowId = 51
      } else {
        workFlowId = 103
      }
      let { ruleId } = this
      this.$util.getdefaultWorkFlowResult(workFlowId, ruleId).then(d => {
        this.values = d
        this.avgCompletionTime = getFormattedDuration(
          d.previousMonthresolutionTime,
          'seconds'
        )
        this.avgAcknowledgeTime = getFormattedDuration(
          d.previousMonthresponseTime,
          'seconds'
        )
        this.loading = false
      })
    },
    averageDuration(duration) {
      if (duration) {
        let days = Math.floor(duration / 86400)
        duration -= days * 86400
        let hours = (duration / 3600).toFixed(2)
        return { days: days, hours: hours }
      }
      return null
    },
  },
}
</script>

<style>
.rule-wo-card span.period {
  font-size: 20px;
  letter-spacing: 0.5px;
  color: #8ca1ad;
}
</style>
