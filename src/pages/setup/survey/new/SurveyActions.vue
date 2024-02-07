<template>
  <div>
    <div id="surveyActions-header" class="section-header survey-header-color">
      {{ $t('survey.survey_actions') }}
    </div>
    <div class="pL50 pT10 pB30 el-form pR0 survey-action-block">
      <SurveyActionConfig
        ref="create-action-config"
        class="mT15 mB20 mR15"
        :module="moduleName"
        :configuredActions="executeCreateActions"
        :actionsHash="actionsHash"
        :supportedActions="supportedActions"
        :hideAction.sync="hideCreateAction"
        @enableAction="enableCreationAction"
        @disableAction="disableCreateAction"
      >
        <template #header>
          <div class="fc-input-label-txt">
            {{ $t('survey.specify_action_when_survey_triggered') }}
          </div>
        </template>
      </SurveyActionConfig>
      <SurveyActionConfig
        ref="respondent-action-config"
        class="mT15 mB20 mR15"
        :configuredActions="executeResponseActions"
        :module="moduleName"
        :actionsHash="actionsHash"
        :supportedActions="supportedActions"
        :hideAction.sync="hideResponseAction"
        @enableAction="enableResponseAction"
        @disableAction="disableResponseAction"
      >
        <template #header>
          <div class="fc-input-label-txt">
            {{ $t('survey.specify_action_when_survey_taken') }}
          </div>
        </template>
      </SurveyActionConfig>
    </div>
  </div>
</template>
<script>
import SurveyActionConfig from '../SurveyActionConfig.vue'
import { isEmpty } from '@facilio/utils/validation'

const actionsHash = {
  email: {
    label: 'Send Email',
    actionParam: 'mail',
  },
}
export default {
  name: 'SurveyActions',
  components: { SurveyActionConfig },
  props: ['selectedSurvey'],
  data() {
    return {
      executeResponseActions: [],
      executeCreateActions: [],
      actionsHash: actionsHash,
      hideCreateAction: true,
      hideResponseAction: true,
    }
  },
  created() {
    this.prefillActions()
    this.hideActionButton()
  },
  computed: {
    moduleName() {
      return 'surveyResponse'
    },
    supportedActions() {
      const actions = ['email']
      return actions
    },
  },
  watch: {
    selectedSurvey: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.prefillActions()
        }
      },
      immediate: true,
    },
  },
  methods: {
    enableCreationAction() {
      this.hideCreateAction = false
    },
    enableResponseAction() {
      this.hideResponseAction = false
    },
    disableCreateAction() {
      this.hideCreateAction = true
    },
    disableResponseAction() {
      this.hideResponseAction = true
    },
    prefillActions() {
      let { selectedSurvey } = this
      if (!isEmpty(selectedSurvey)) {
        let { executeResponseActions = [], executeCreateActions = [] } =
          selectedSurvey || {}

        this.$set(this, 'executeResponseActions', executeResponseActions)
        this.$set(this, 'executeCreateActions', executeCreateActions)
      }
    },
    hideActionButton() {
      let { executeCreateActions, executeResponseActions } = this
      this.hideCreateAction = (executeCreateActions || []).length > 0
      this.hideResponseAction = (executeResponseActions || []).length > 0
    },
    serialize() {
      let { executeResponseActions, executeCreateActions } = this
      executeCreateActions = this.$refs['create-action-config'].serialize()
      executeResponseActions = this.$refs[
        'respondent-action-config'
      ].serialize()

      return { executeResponseActions, executeCreateActions }
    },
  },
}
</script>
<style>
.survey-action-block {
  min-height: 600px;
  margin-top: -10px;
}
</style>
