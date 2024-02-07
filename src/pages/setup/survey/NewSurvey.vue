<template>
  <div class="formbuilder-fullscreen-popup sla-policy-container">
    <div class="setting-header">
      <div v-if="!isNew">
        <div class="pointer fc-link fw-normal f13" @click="goBack">
          <inline-svg
            src="left-arrow"
            iconClass="icon icon-sm vertical-text-top mR5"
          ></inline-svg>
          {{ $t('setup.setupLabel.go_back') }}
        </div>
        <div class="mT10 mB5 f22 fw3 letter-spacing0_5">
          {{ `${$t('common._common.edit')} ${policyTitle}` }}
        </div>
      </div>
      <div class="d-flex flex-direction-column" v-else>
        <div class="mT10 mB10 f22 fw3 letter-spacing0_5">
          {{ $t('survey.configure_survey') }}
        </div>
      </div>
      <div class="fR stateflow-btn-wrapper">
        <async-button buttonClass="asset-el-btn" :clickAction="goBack">
          {{ $t('setup.users_management.cancel') }}
        </async-button>
        <async-button
          buttonType="primary"
          buttonClass="asset-el-btn"
          :clickAction="save"
        >
          {{ $t('maintenance._workorder.save') }}
        </async-button>
      </div>
    </div>
    <div class="d-flex setup-grey-bg">
      <div class="sla-sidebar pT10">
        <a
          id="surveyBasicDetails-link"
          @click="scrollTo('surveyBasicDetails')"
          class="sla-link active"
        >
          {{ $t('survey.survey_details') }}
        </a>
        <a
          id="surveyRespondent-link"
          @click="scrollTo('surveyRespondent')"
          class="sla-link"
        >
          {{ $t('survey.survey_respondent') }}
        </a>
        <a
          id="surveyCustomization-link"
          @click="scrollTo('surveyCustomization')"
          class="sla-link"
        >
          {{ $t('survey.survey_customization') }}
        </a>
        <a
          id="surveyActions-link"
          @click="scrollTo('surveyActions')"
          class="sla-link"
        >
          {{ $t('survey.survey_actions') }}
        </a>
      </div>
      <div class="scroll-container">
        <div v-if="isLoading" class="mT20">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <template v-else>
          <SurveyBasicDetails
            id="surveyBasicDetails-section"
            ref="surveyBasicDetails-section"
            :selectedSurvey="selectedSurvey"
            class="mB20"
          ></SurveyBasicDetails>
          <SurveyRespondent
            id="surveyRespondent-section"
            ref="surveyRespondent-section"
            :selectedSurvey="selectedSurvey"
            :moduleFields="moduleFields"
            class="mB20"
          ></SurveyRespondent>
          <SurveyCustomization
            id="surveyCustomization-section"
            ref="surveyCustomization-section"
            :selectedSurvey="selectedSurvey"
            class="mB20"
          ></SurveyCustomization>
          <SurveyActions
            id="surveyActions-section"
            ref="surveyActions-section"
            :selectedSurvey="selectedSurvey"
            :data="data"
            class="mB70"
          ></SurveyActions>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import AsyncButton from '@/AsyncButton'
import SurveyBasicDetails from './new/SurveyBasicDetails.vue'
import SurveyRespondent from './new/SurveyRespondent.vue'
import SurveyCustomization from './new/SurveyCustomization.vue'
import SurveyActions from './new/SurveyActions.vue'
import SidebarScrollMixin from 'pages/setup/sla/mixins/SidebarScrollMixin'
import { API } from '@facilio/api'
import RuleCreation from 'pages/alarm/rule-creation/AlarmRuleCreation.vue'
import Spinner from '@/Spinner'

export default {
  name: 'New-Survey',
  props: ['id', 'moduleName'],
  mixins: [SidebarScrollMixin],
  extends: [RuleCreation],
  components: {
    SurveyBasicDetails,
    SurveyRespondent,
    SurveyCustomization,
    SurveyActions,
    AsyncButton,
    Spinner,
  },
  mounted() {
    this.$nextTick(this.registerScrollHandler)
  },
  data() {
    return {
      isLoading: true,
      hasChanged: false,
      sectionResponses: {},
      selectedSurvey: {},
      policyTitle: 'Survey',
      rootElementForScroll: '.scroll-container',
      sidebarElements: [
        '#surveyBasicDetails-link',
        '#surveyRespondent-link',
        '#surveyCustomization-link',
        '#surveyActions-link',
      ],
      sectionElements: [
        '#surveyBasicDetails-section',
        '#surveyRespondent-section',
        '#surveyCustomization-section',
        '#surveyActions-section',
      ],
      actionObj: { actionType: 3, templateJson: {} },
      data: {
        rule: {
          name: '',
          scheduleType: 3,
          description: '',
          ruleType: 46,
          event: {
            moduleName: 'workorder',
            activityType: 3,
          },
          dateFieldId: null,
          time: null,
          interval: null,
          fields: [],
          criteria: {},
          siteId: null,
          actions: [
            {
              actionType: 36,
              templateJson: {
                isRetakeAllowed: true,
                retakeExpiryDay: null,
                expiryDay: null,
                qandaTemplateId: null,
              },
            },
          ],
        },
      },
      moduleFields: [],
    }
  },
  created() {
    this.fetchMetaFields()
    this.fetchSurveyDetails()
  },
  computed: {
    isNew() {
      return isEmpty(this.id)
    },
    ruleId() {
      return parseInt(this.$route.params.id)
    },
  },
  methods: {
    goBack() {
      this.$router.push({
        name: 'survey.list',
        moduleName: this.moduleName,
      })
    },
    async fetchSurveyDetails() {
      this.isLoading = true
      let { isNew, ruleId } = this
      if (!isNew) {
        let params = { workFlowRuleId: ruleId }
        let { error, data } = await API.get('v2/setup/survey/fetch', params)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          if (!isEmpty(data)) {
            this.selectedSurvey = data || {}
          }
        }
      }
      this.isLoading = false
    },
    async save() {
      let { isNew } = this
      let basicDetails = this.$refs['surveyBasicDetails-section'].serialize()
      let respondentDetails = this.$refs['surveyRespondent-section'].serialize()
      let customizationDetails = this.$refs[
        'surveyCustomization-section'
      ].serialize()

      let actions = this.$refs['surveyActions-section'].serialize()
      this.sectionResponses = {
        ...basicDetails,
        ...respondentDetails,
        customizationDetails,
        ...actions,
      }
      this.sectionResponses = this.serializeSurvey(this.sectionResponses)
      let isValidSurvey = this.validateSurvey(this.sectionResponses)
      if (isValidSurvey) {
        let promise
        let params = { ...this.sectionResponses, moduleName: 'workorder' }
        if (!isNew) {
          promise = await API.post('v2/setup/survey/edit', params)
        } else {
          promise = await API.post('v2/setup/survey/add', params)
        }

        let { error } = await promise
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          if (isNew) {
            this.$message.success('Survey created successfully!')
          } else {
            this.$message.success('Survey updated successfully!')
          }
          this.$router.push({
            name: 'survey.list',
            moduleName: this.moduleName,
          })
        }
      } else {
        this.$message.error('Please fill all mandatory fields')
      }
    },
    validateSurvey(survey) {
      let validSurvey = true
      let { rule } = survey || {}
      let { name, actions, criteria } = rule || {}
      let { templateJson } = actions[0] || {}
      let {
        qandaTemplateId,
        sharingType,
        expiryDay,
        retakeExpiryDay,
        userId,
        fieldId,
        isRetakeAllowed,
      } = templateJson || {}
      let mandatoryFields = {
        name,
        qandaTemplateId,
        sharingType,
        expiryDay,
        criteria,
      }
      if (isRetakeAllowed) {
        mandatoryFields = { ...mandatoryFields, retakeExpiryDay }
      }
      if (sharingType === 1) {
        if (!isEmpty(fieldId)) {
          mandatoryFields = { ...mandatoryFields, fieldId }
        } else {
          mandatoryFields = { ...mandatoryFields, userId }
        }
      } else {
        mandatoryFields = { ...mandatoryFields, fieldId }
      }

      validSurvey = Object.values(mandatoryFields).every(
        value => !isEmpty(value)
      )
      return validSurvey
    },
    serializeSurvey(survey) {
      let { data, isNew, ruleId } = this
      let { rule } = data || {}
      let { actions } = rule || {}
      let { templateJson } = actions[0] || {}

      let {
        name,
        description,
        criteria,
        qandaTemplateId,
        executeResponseActions,
        executeCreateActions,
        customizationDetails,
        siteId,
        sharingType,
        userId,
        fieldId,
        event,
        fields,
        interval,
        dateFieldId,
      } = survey || {}
      let replaceableFields = {
        name,
        description,
        siteId,
        event,
        fields,
        interval,
        dateFieldId,
      }

      Object.entries(replaceableFields).forEach(([key, value]) => {
        if (Object.prototype.hasOwnProperty.call(rule, `${key}`)) {
          rule[key] = value
        }
      })

      if (!isEmpty(rule) && !isEmpty(actions)) {
        templateJson = { ...templateJson, ...customizationDetails }
        this.$set(rule, 'criteria', criteria)
        this.$set(actions[0], 'templateJson', templateJson)
        this.$set(actions[0].templateJson, 'qandaTemplateId', qandaTemplateId)
        this.$set(actions[0].templateJson, 'sharingType', sharingType)
        if (sharingType === 1) {
          if (!isEmpty(fieldId)) {
            this.$set(actions[0].templateJson, 'fieldId', fieldId)
          } else {
            this.$set(actions[0].templateJson, 'userId', userId)
          }
        } else {
          this.$set(actions[0].templateJson, 'fieldId', fieldId)
        }
      }

      if (!isNew) {
        rule = { ...rule, id: ruleId }
        let serializedSurveyActions = this.serializeActions(survey)
        survey = { ...serializedSurveyActions }
      } else {
        survey = { executeResponseActions, executeCreateActions }
      }
      survey = { ...survey, rule }

      return survey
    },
    serializeActions(survey) {
      let { executeResponseActions, executeCreateActions } = survey || {}
      //handling only for email actions  hence actions[0]
      let { templateJson: createTemplate = {} } = executeCreateActions[0] || {}
      let { templateJson: responseTemplate = {} } =
        executeResponseActions[0] || {}
      let serializedCreateAction = {
        actionType: 3,
        templateJson: createTemplate,
      }
      let serializedResponseAction = {
        actionType: 3,
        templateJson: responseTemplate,
      }

      return {
        executeCreateActions: !isEmpty(createTemplate)
          ? [serializedCreateAction]
          : [],
        executeResponseActions: !isEmpty(responseTemplate)
          ? [serializedResponseAction]
          : [],
      }
    },
    async fetchMetaFields() {
      let { error, data } = await API.get('/module/metafields', {
        moduleName: this.moduleName,
      })
      if (!error) {
        let fields = this.$getProperty(data, 'meta.fields', []) || []
        this.moduleFields =
          fields.filter(field => field.displayTypeEnum !== 'TASKS') || []
      }
    },
  },
}
</script>
<style>
.survey-header-color {
  color: #39b2c2 !important;
}
</style>
