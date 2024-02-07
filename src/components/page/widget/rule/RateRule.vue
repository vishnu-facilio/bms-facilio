<template>
  <div class="" style="padding: 23px 30px">
    <div class="flex-middle flex-direction-row f14">
      <div class="mR15 summary-widget-icon-bg blue line-height30">
        <InlineSvg
          src="svgs/star"
          iconClass="icon icon-md fc-white-color text-center"
        ></InlineSvg>
      </div>
      <div class="fc-royal-blue pR10 f16">
        {{ $t('rule.create.rank') }}{{ rank > 0 ? rank : ' --' }}
      </div>
      <div class="bold label-txt-black">
        {{ $t('rule.create.out_of') }}{{ outOfRule > 0 ? outOfRule : '--' }}
        {{
          ['newreadingalarm', 'readingrule'].includes(moduleName)
            ? ' Most Common Faults'
            : ' Most Common Alarms'
        }}
      </div>
    </div>
  </div>
</template>
<script>
import InlineSvg from '@/InlineSvg'
export default {
  components: {
    InlineSvg,
  },
  props: ['details', 'moduleName'],
  data() {
    return {
      outOfRule: null,
      rank: null,
    }
  },
  mounted() {
    this.loadRateCardDetails()
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
    loadRateCardDetails() {
      this.loading = true
      let workFlowId = null
      if (this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        if (
          this.moduleName === 'readingalarm' ||
          this.moduleName === 'newreadingalarm'
        ) {
          workFlowId = 114
        } else if (this.moduleName === 'operationalarm') {
          workFlowId = 114
        } else {
          workFlowId = 52
        }
      } else {
        workFlowId = 104
      }
      this.$util
        .getWorkFlowResult(
          workFlowId,
          this.moduleName === 'operationalarm' ||
            this.moduleName === 'newreadingalarm'
            ? [
                this.details.alarmRule
                  ? this.details.alarmRule.preRequsite.id
                  : this.details.alarm.id,
                this.moduleName,
              ]
            : [this.ruleId]
        )
        .then(d => {
          if (d) {
            this.outOfRule = d.outOfRule
            this.rank = d.ranking
            this.loading = false
          }
        })
    },
  },
}
</script>
