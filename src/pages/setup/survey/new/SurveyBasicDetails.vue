<template>
  <div>
    <div
      id="survey-basic-details-header"
      class="section-header survey-header-color pB10"
    >
      {{ $t('survey.survey_details') }}
    </div>
    <div class="survey-basic-details">
      <el-form
        ref="basicDetailsForm"
        :model="survey"
        label-width="150px"
        label-position="left"
        class="p50 pT10 pB30 pR10"
      >
        <div class="section-container flex-container">
          <div class="form-one-column fc-label-required">
            <el-form-item
              :label="$t('survey.survey_name')"
              prop="qandaTemplateId"
            >
              <el-select
                v-model="survey.qandaTemplateId"
                class="fc-input-full-border2 width50"
                :placeholder="$t('survey.select_survey')"
                @change="handleTemplateChange"
                :autofocus="true"
                default-first-option
              >
                <el-option
                  v-for="template in templates"
                  :key="template.id"
                  :label="template.name"
                  :value="template.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="form-one-column">
            <el-form-item
              :label="$t('maintenance._workorder.execute_on')"
              prop="activityType"
            >
              <el-select
                v-model="survey.event.activityType"
                :placeholder="$t('survey.select')"
                class="width50 fc-input-full-border-select2"
              >
                <el-option
                  v-for="(type, index) in activityTypes"
                  :key="index"
                  :label="type.label"
                  :value="type.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </div>
          <div
            class="form-one-column"
            v-if="checkActivityType(survey) === 'field'"
          >
            <!-- FIELD_CHANGE -->
            <el-row>
              <!-- Field block Start-->
              <div class="form-one-column">
                <el-form-item prop="activityType" class="fc-tag p0 pL10 pR10">
                  <p class="fc-input-label-txt">
                    {{ $t('common.header.field') }}
                  </p>
                  <el-select
                    :multiple-limit="3"
                    v-model="survey.fields"
                    multiple
                    collapse-tags
                    :placeholder="$t('survey.select')"
                    class="fc-input-full-border-select2 width50"
                  >
                    <el-option
                      v-for="(fld, index) in moduleFields"
                      :key="index"
                      :label="fld.displayName"
                      :value="fld.id"
                    >
                    </el-option>
                  </el-select>
                  <el-button slot="reference" class="all-rule-btn"
                    ><inline-svg
                      src="svgs/survey/add-icon"
                      iconClass="mL10"
                    ></inline-svg>
                  </el-button>
                </el-form-item>
              </div>
            </el-row>
          </div>
          <div
            class="form-one-column mL150"
            v-else-if="checkActivityType(survey) === 'scheduled'"
          >
            <!-- SCHEDULED -->
            <el-row>
              <el-col :span="12">
                <p class="fc-input-label-txt">
                  {{ $t('setup.setupLabel.dateField') }}
                </p>
                <el-select
                  v-model="survey.dateFieldId"
                  :placeholder="$t('survey.select')"
                  class="width300px fc-input-full-border-select2"
                >
                  <el-option
                    v-for="(fld, index) in filteredDateFields"
                    :key="index"
                    :label="fld.displayName"
                    :value="fld.id"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="12" class="pL40">
                <p class="fc-input-label-txt mB10">
                  {{ $t('setup.setupLabel.scheduled_type') }}
                </p>
                <el-radio-group
                  v-model="survey.scheduleType"
                  class="criteria-radio-label"
                >
                  <el-radio
                    class="fc-radio-btn"
                    v-for="item in dateFieldHash"
                    :key="item.keyField"
                    :label="item.labelField"
                    ><template
                      v-if="
                        item.labelField === 1 &&
                          !beforeFieldIds.includes(survey.dateFieldId)
                      "
                      >{{ item.fieldName }}</template
                    ><template v-else>{{ item.fieldName }}</template></el-radio
                  >
                </el-radio-group>
              </el-col>
            </el-row>
            <el-row class="mT30">
              <template v-if="checkScheduledType">
                <el-col :span="8">
                  <p class="fc-input-label-txt">
                    {{ $t('maintenance._workorder.days') }}
                  </p>
                  <el-select
                    v-model="dateObject.days"
                    clearable
                    :placeholder="$t('survey.select')"
                    class="fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="index in 60"
                      :label="index"
                      :key="`days-${index}`"
                      :value="index"
                    ></el-option>
                  </el-select>
                </el-col>
                <template v-if="checkDateTimeField">
                  <el-col :span="8">
                    <p class="fc-input-label-txt">
                      {{ $t('common._common.hours') }}
                    </p>
                    <el-select
                      v-model="dateObject.hours"
                      clearable
                      class="fc-input-full-border-select2"
                    >
                      <el-option
                        v-for="index in $constants.HOURS"
                        :label="index"
                        :key="index + 1"
                        :value="index"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="8">
                    <p class="fc-input-label-txt">
                      {{ $t('maintenance._workorder.mins') }}
                    </p>
                    <el-select
                      v-model="dateObject.minute"
                      :placeholder="$t('survey.select')"
                      class="fc-input-full-border-select2"
                    >
                      <el-option
                        v-for="index in $constants.MINUTES"
                        :label="index"
                        :key="index + 1"
                        :value="index"
                      ></el-option>
                    </el-select>
                  </el-col>
                </template>
              </template>
              <el-col :span="8" v-if="!checkDateTimeField">
                <p class="fc-input-label-txt">
                  {{ $t('common.wo_report.time') }}
                </p>
                <el-select
                  v-model="survey.time"
                  collapse-tags
                  class="fc-input-full-border-select2 width100 fc-tag"
                >
                  <el-option
                    v-for="time in timesOption"
                    :label="time"
                    :value="time"
                    :key="time"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-form>
      <div class="d-flex fc-label-required pB50">
        <div class="criteria-section fc-label-required">
          {{ $t('survey.criteria') }}
        </div>
        <CriteriaBuilder
          ref="criteriaBuilder"
          class="pL50 criteria-builder-section"
          v-model="survey.criteria"
          :moduleName="moduleName"
        />
      </div>
    </div>
  </div>
</template>
<script>
import { loadAllTemplates } from '../SurveyUtil'
import { CriteriaBuilder } from '@facilio/criteria'
import { isEmpty } from '@facilio/utils/validation'
import { isDateTimeField } from '@facilio/utils/field'
import { API } from '@facilio/api'

const activityTypeHash = {
  SCHEDULED: 524288,
  FIELD_CHANGE: 1048576,
}

export default {
  props: ['selectedSurvey'],
  name: 'SurveyBasicdetails',
  components: {
    CriteriaBuilder,
  },
  created() {
    this.initBasicDetails()
    this.getModuleFields()
  },
  data() {
    return {
      timesOption: [],
      criteriaRendered: false,
      isLoading: false,
      templates: null,
      modules: 'workorder',
      survey: {
        name: '',
        description: '',
        criteria: {},
        qandaTemplateId: null,
        event: { activityType: 3, moduleName: this.moduleName },
        siteId: null,
        dateFieldId: null,
        time: null,
        interval: null,
        fields: [],

        scheduleType: 3,
      },
      dateObject: {
        days: null,
        minute: 0,
        hours: 0,
        minu: null,
      },
      dateFields: [],
      dateFieldToExclude: [
        'sysCreatedTime',
        'sysModifiedTime',
        'sysDeletedTime',
      ],
      beforeDateFieldsToExclude: ['createdTime', 'modifiedTime'],
      beforeFieldIds: [],
      dateFieldHash: [
        { fieldName: 'Before', keyField: 1, labelField: 1 },
        { fieldName: 'On', keyField: 2, labelField: 2 },
        { fieldName: 'After', keyField: 3, labelField: 3 },
      ],
      moduleFields: [],
    }
  },
  mounted() {
    for (let i = 0; i <= 23; i++) {
      let time = (i < 10 ? '0' + i : i) + ':'
      let { timesOption } = this
      timesOption.push(time + '00')
      timesOption.push(time + '30')
      this.$set(this, 'timesOption', timesOption)
    }
  },
  watch: {
    moduleFields(newVal) {
      if (!isEmpty(newVal)) {
        this.dateFields = newVal.filter(dateFld => {
          let dateFieldName = this.$getProperty(
            dateFld,
            'dataTypeEnum._name',
            ''
          )
          return dateFieldName === 'DATE' || dateFieldName === 'DATE_TIME'
        })

        this.beforeFieldIds = this.dateFields
          .filter(field => this.beforeDateFieldsToExclude.includes(field.name))
          .map(field => field.id)
      }
    },
  },
  computed: {
    checkScheduledType() {
      let scheduleType = this.$getProperty(this, 'survey.scheduleType', 0)
      return [1, 3].includes(parseInt(scheduleType)) ? true : false
    },
    workFlowActionHash() {
      let { isCustomModule, modules } = this
      let moduleName = isCustomModule ? 'customModules' : modules
      if (
        !isEmpty(modules.extendModule) &&
        modules.extendModule.name === 'basealarm'
      ) {
        moduleName = 'basealarm'
      }
      return (
        this.$constants.WorkFlowAction.module[moduleName] ||
        this.$constants.WorkFlowAction.module.customModules
      )
    },
    activityTypes() {
      let { workFlowActionHash } = this
      return workFlowActionHash.activityTypes
    },
    moduleName() {
      return this.$route.params.moduleName
    },
    filteredDateFields() {
      let { dateFieldToExclude, beforeDateFieldsToExclude } = this
      let fields = [...dateFieldToExclude]
      let scheduleType = this.$getProperty(this, 'survey.scheduleType', null)
      if (scheduleType === 1) {
        fields = [...fields, ...beforeDateFieldsToExclude]
      }
      return (this.dateFields || []).filter(
        field => !fields.includes(field.name)
      )
    },
    checkDateTimeField() {
      let { dateFields, survey } = this
      let { dateFieldId } = survey || {}
      let field =
        (dateFields || []).find(fld => fld.fieldId === dateFieldId) || {}

      return !isEmpty(field) ? isDateTimeField(field) : false
    },
  },
  methods: {
    async initBasicDetails() {
      await this.loadAvailableTemplates()
      this.prefillSurveyDetails()
    },
    async getModuleFields() {
      await API.get(`/module/metafields?moduleName=${this.moduleName}`)
        .then(response => {
          this.moduleFields = response.data.meta.fields
        })
        .catch(() => {})
    },
    prefillSurveyDetails() {
      let { selectedSurvey } = this
      if (!isEmpty(selectedSurvey)) {
        let { workflowRule } = selectedSurvey || {}
        let {
          actions,
          name,
          description,
          criteria,
          siteId,
          event,
          fields,
          interval,
          scheduleType,
          dateFieldId,
        } = workflowRule || {}
        let qandaTemplateId = this.$getProperty(
          actions,
          '0.template.qandaTemplateId',
          null
        )
        if (!isEmpty(fields)) {
          fields = fields.map(field => {
            let { fieldId } = field || {}
            return fieldId
          })
        }

        if (!isEmpty(interval)) {
          let { $helpers } = this
          let { secTodaysHoursMinu } = $helpers || {}
          this.dateObject = secTodaysHoursMinu(interval)
        }

        let surveyDetails = {
          name,
          description,
          criteria,
          siteId,
          qandaTemplateId,
          event,
          fields,
          interval,
          scheduleType,
          dateFieldId,
        }

        this.$set(this, 'survey', surveyDetails)
      }
    },
    updateCriteria(value) {
      this.$set(this.survey, 'criteria', value)
    },
    async loadAvailableTemplates() {
      this.isLoading = true

      let viewName = 'all'
      let templatePage = 1
      let templatePerPage = 50
      let moduleName = 'workorder'
      this.isLoading = true
      let templates = await loadAllTemplates(
        moduleName,
        templatePage,
        templatePerPage,
        viewName
      )
      this.templates = templates.filter(template => {
        let { status, totalQuestions } = template || {}
        return !isEmpty(status) && !isEmpty(totalQuestions)
      })
      this.isLoading = false
    },
    handleTemplateChange() {
      let { survey, templates } = this
      let { qandaTemplateId } = survey || {}

      if (!isEmpty(qandaTemplateId)) {
        let template = templates.find(
          template => template.id === qandaTemplateId
        )
        survey.name = this.$getProperty(template, 'name', null)

        let { siteId } = template || {}

        this.$set(survey, 'siteId', siteId)
      }
    },
    serialize() {
      let { survey, moduleName, dateObject, checkDateTimeField } = this
      let { event, fields, interval, dateFieldId, criteria } = survey || {}
      let { activityType } = event || {}

      event = { ...event, moduleName }
      survey = { ...survey, event }

      if (!isEmpty(fields)) {
        fields = fields.map(field => {
          return { fieldId: field }
        })
        survey = { ...survey, fields }
      }
      if (activityType === activityTypeHash.SCHEDULED) {
        if (!isEmpty(dateObject)) {
          if (dateFieldId) {
            if (!checkDateTimeField) {
              this.dateObject.hours = 0
              this.dateObject.minute = 0
            }
          }
          interval = this.$helpers.daysHoursMinuToSec(this.dateObject)
        }
      }
      survey = { ...survey, interval }

      Object.keys(survey).forEach(property => {
        if (isEmpty(survey[property])) {
          delete survey[property]
        }
      })

      let { conditions } = criteria || {}
      let conditionsEmpty = false
      if (!isEmpty(conditions)) {
        Object.keys(conditions).forEach(condition => {
          let conditionObj = conditions[condition] || {}
          let { fieldName } = conditionObj || {}
          if (isEmpty(fieldName)) {
            conditionsEmpty = conditionsEmpty || true
          }
        })
      }
      if (conditionsEmpty) {
        survey = { ...survey, criteria: null }
      }
      return survey
    },
    checkActivityType(survey) {
      let { event } = survey || {}
      let activityType = this.$getProperty(event, 'activityType', null)

      if (activityType === activityTypeHash.FIELD_CHANGE) {
        return 'field'
      } else if (activityType === activityTypeHash.SCHEDULED) {
        return 'scheduled'
      }
      return ''
    },
  },
}
</script>
<style scoped lang="scss">
.label-required {
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #e6333d;

  &:after {
    display: none;
  }
}

.criteria-section {
  font-size: 14px !important;
  color: #e6333d !important;
  letter-spacing: 0.5px;
  text-transform: capitalize;
  margin: 0;
  padding: 0px 50px 20px;
  margin-top: -10px;
}
.criteria-builder-section {
  margin: -30px 0px 0px -15px;
}
</style>
