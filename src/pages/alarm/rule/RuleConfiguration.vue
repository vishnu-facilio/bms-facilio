<template>
  <div class="mv-container">
    <FStepper
      :steps="steps"
      :initialStep="steps[0]"
      :keepAliveData="true"
      @finalStep="saveRecord"
    >
      <div
        slot="title"
        class="rule-title-container d-flex flex-direction-column justify-content-end"
      >
        <div class="rule-title">
          {{ $t('rule.create.new_custom_alarm_rule') }}
        </div>
        <div class="mT5 f20 fw3 letter-spacing0_5">
          <portal-target name="rule-title"></portal-target>
        </div>
      </div>
    </FStepper>
  </div>
</template>

<script>
import FStepper from '@/FStepper'
import BasicInfoForm from 'pages/alarm/rule/BasicInfoForm'
import RuleConditionForm from 'pages/alarm/rule/RuleConditionForm'
import RootCauseNImpactForm from 'pages/alarm/rule/RootCauseNImpactForm'
import ImpactForm from 'pages/alarm/rule/ImpactForm'
import ActionForm from 'pages/alarm/rule/ActionsForm'
import RuleMixin from '@/mixins/RuleMixin'
import FormMixin from '@/mixins/FormMixin'
import { isEmpty } from '@facilio/utils/validation'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'

export default {
  mixins: [RuleMixin, FormMixin],
  components: {
    FStepper,
  },
  data() {
    return {
      isMultiResource: false,
      thresholdFields: null,
      selectedResourceList: null,
    }
  },
  computed: {
    steps() {
      let step = [
        {
          title: `${this.$t('rule.create.new_alarm_rule')}`,
          component: BasicInfoForm,
        },
        {
          title: `${this.$t('rule.create.alarm_condition')}`,
          component: RuleConditionForm,
        },
        {
          title: `${this.$t('rule.create.root_cause')}`,
          component: RootCauseNImpactForm,
        },
        {
          title: `${this.$t('rule.create.impact')}`,
          component: ImpactForm,
        },
      ]
      if (this.$route.name !== 'rule-creation-edit') {
        step.push({
          title: `${this.$t('rule.create.actions')}`,
          component: ActionForm,
        })
      }
      return step
    },
    moduleName() {
      return 'readingrule'
    },
  },
  methods: {
    getthresholdFields(assetCategoryId) {
      return this.$store.getters['formulabuilder/getAssetReadings'](
        assetCategoryId,
        true
      )
    },
    parseThersholdType(readingRule, metricFieldObject) {
      if (isEmpty(metricFieldObject)) {
        this.setMetricModule(readingRule.readingFieldId, readingRule)
        metricFieldObject = readingRule.selectedMetric
      }
      if (isEmpty(metricFieldObject)) {
        return
      }
      if (readingRule.thresholdType === 1) {
        this.simpleConditionParse(readingRule, metricFieldObject)
      } else if (readingRule.thresholdType === 2) {
        this.aggregationParse(readingRule, metricFieldObject)
      } else if (readingRule.thresholdType === 3) {
        this.baseLineParse(readingRule, metricFieldObject)
      } else if (readingRule.thresholdType === 4) {
        readingRule.flapInterval = readingRule.flapInterval * (60 * 1000)
      } else if (
        readingRule.thresholdType === 5 ||
        readingRule.thresholdType === 6
      ) {
        if (readingRule.workflow && readingRule.workflow.occurences) {
          Object.assign(readingRule, readingRule.workflow.occurences)
        }
      }
    },
    constructActionObj(action, ruleObject) {
      let template = {}
      if (!isEmpty(action)) {
        if (action.hasOwnProperty('message')) {
          template['message'] = action.message
        }
        if (action.hasOwnProperty('problem')) {
          template['problem'] = action.problem
        }
        if (action.hasOwnProperty('possibleCauses')) {
          template['possibleCauses'] = action.possibleCauses.join(' \n ')
        }
        if (action.hasOwnProperty('recommendation')) {
          template['recommendation'] = action.recommendation.join(' \n ')
        }
        if (action.hasOwnProperty('severity')) {
          template['severity'] = action.severity
        }
        if (ruleObject.enableimpacts) {
          template['impact'] = {}
          template.impact.cost = [ruleObject.impactsContext.id]
        }
      }
      return template
    },
    saveRecord(rule) {
      this.isFormSaved = true
      let ruleObject = this.$helpers.cloneObject(rule)
      ruleObject.preRequsite.matchedResources = null
      ruleObject.alarmTriggerRule.matchedResources = null
      ruleObject.alarmTriggerRule.faultType = ruleObject.faultType
      if (!(ruleObject.preRequsite.assetCategoryId > 0)) {
        ruleObject.preRequsite.assetCategoryId = -1
        ruleObject.preRequsite.resourceId = ruleObject.selectedResourceList.id
      } else {
        this.selectedResourceList = ruleObject.selectedResourceList
        this.isMultiResource = true
      }
      if (isEmpty(this.thresholdFields)) {
        this.thresholdFields = this.getthresholdFields(
          ruleObject.preRequsite.assetCategoryId
        )
      }
      if (
        !(
          ruleObject.preRequsite.thresholdType === 1 &&
          (isEmpty(ruleObject.preRequsite.operatorId) ||
            ruleObject.preRequsite.operatorId < 1)
        )
      ) {
        this.parseThersholdType(
          ruleObject.preRequsite,
          ruleObject.preRequsite.selectedMetric
        )
      }
      if (!ruleObject.isPrerequest) {
        ruleObject.preRequsite.workflow = null
        ruleObject.preRequsite.workflowId = -99
        ruleObject.preRequsite.operatorId = -99
        ruleObject.preRequsite.percentage = ''
      }
      if (ruleObject.selectedResourceList) {
        let fieldName = ruleObject.isIncludeResource
          ? 'includedResources'
          : 'excludedResources'
        let emptyFields =
          fieldName === 'includedResources'
            ? 'excludedResources'
            : 'includedResources'
        ruleObject.preRequsite[fieldName] = Array.isArray(
          ruleObject.selectedResourceList
        )
          ? ruleObject.selectedResourceList.map(resource => resource.id)
          : null
        ruleObject.preRequsite[emptyFields] = null
      }
      if (ruleObject.autoClear) {
        ruleObject.autoClear = true
        ruleObject.isAutoClear = true
        ruleObject.alarmClearRule = null
      } else {
        ruleObject.autoClear = false
        ruleObject.isAutoClear = false
        this.parseThersholdType(
          ruleObject.alarmClearRule,
          ruleObject.preRequsite.selectedMetric
        )
      }
      if (
        ruleObject.alarmTriggerRule &&
        (ruleObject.alarmTriggerRule.isChanged ||
          ruleObject.alarmTriggerRule.actions[0].isChanged)
      ) {
        ruleObject.alarmTriggerRule.readingFieldId =
          ruleObject.preRequsite.readingFieldId
        this.parseThersholdType(
          ruleObject.alarmTriggerRule,
          ruleObject.preRequsite.selectedMetric
        )
        let actions = this.parseAction(
          this.constructActionObj(ruleObject.action, ruleObject)
        )
        ruleObject.alarmTriggerRule.actions = []
        ruleObject.alarmTriggerRule.actions.push(actions)
      } else {
        ruleObject.alarmTriggerRule = null
      }
      if (ruleObject.rcaSelectObj.length > 0) {
        ruleObject.alarmRCARules = ruleObject.rcaSelectObj.map(
          ({ value }) => value
        )
      }
      if (ruleObject.reportBreakdown) {
        let reportDowntime = {
          actions: [
            {
              actionType: 22,
              templateJson: {},
            },
          ],
          name: 'Report Downtime',
          event: { moduleName: 'newreadingalarm', activityType: 1 },
        }
        if (ruleObject.readingAlarmRuleContexts) {
          ruleObject.readingAlarmRuleContexts.forEach(d => {
            d.actions = reportDowntime.actions
          })
        } else {
          ruleObject.readingAlarmRuleContexts = [reportDowntime]
        }
      } else {
        ruleObject.readingAlarmRuleContexts = null
      }
      if (ruleObject.createWo) {
        if (!ruleObject.workflowRulesForAlarms)
          ruleObject.workflowRulesForAlarms = []
        ruleObject.workflowRulesForAlarms.push({
          name: 'Create Wo',
          event: { moduleName: 'newreadingalarm', activityType: 1 },
          actions: [
            {
              actionType: 11,
              templateJson: {},
            },
          ],
        })
        if (ruleObject.autoCloseWo) {
          ruleObject.workflowRulesForAlarms.push({
            name: 'Close Wo',
            event: { moduleName: 'newreadingalarm', activityType: 1 },
            actions: [
              {
                actionType: 12,
                templateJson: {},
              },
            ],
          })
        }
      }
      delete ruleObject.actions
      this.$util
        .newAddRule(
          'alarm',
          { alarmRule: ruleObject },
          ['rule-creation-edit', 'rule-edit'].includes(this.$route.name)
        )
        .then(() => {
          if (isWebTabsEnabled()) {
            let { moduleName } = this
            let { name } = findRouteForTab(pageTypes.RULES_LIST) || {}
            name &&
              this.$router.push({
                name,
                params: { viewname: 'all' },
                query: this.$route.query,
              })
          } else {
            this.$router.push({ path: `/app/fa/rules/all` })
          }
        })
        .catch(() => {})
    },
  },
}
</script>

<style lang="scss">
.rule-title {
  font-weight: 300;
  letter-spacing: 0.5px;
  font-size: 22px;
}
.rule-title-container {
  padding-top: 20px;
  padding-bottom: 10px;
}
</style>
