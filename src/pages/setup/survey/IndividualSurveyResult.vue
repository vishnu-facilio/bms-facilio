<template>
  <el-dialog
    :visible="showResultDialog"
    :append-to-body="true"
    custom-class="add-new-form-animated slideInRight fc-dialog-form fc-dialog-right setup-dialog40 add-new-form"
    style="z-index: 999999"
    :before-close="closeDialog"
  >
    <div v-if="surveyLoading || loading" class="mT20">
      <spinner :show="surveyLoading || loading" size="80"></spinner>
    </div>
    <div
      v-else
      class="qanda-response-widget survey-response"
      ref="qanda-section"
    >
      <div class="template-name">{{ surveyName || surveyTitle }}</div>
      <el-row class="survey-details">
        <el-col :span="24" class="details-section">
          <span class="details-header">
            {{ moduleDisplayName || recordModuleName }}
          </span>
          <span
            class="survey-detail truncate-text"
            v-tippy
            :title="recordName"
            >{{ recordName }}</span
          >
        </el-col>
      </el-row>
      <el-row class="survey-details">
        <el-col :span="8" class="details-section mR20">
          <span class="details-header">
            {{ $t('survey.survey_respondent') }}
          </span>
          <span class="survey-detail">{{ attenderName }}</span>
        </el-col>
        <el-col :span="8" class="details-section">
          <span class="details-header">
            {{ $t('survey.response_date') }}
          </span>
          <span class="survey-detail">{{ responseTime }}</span>
        </el-col>
        <el-col
          :span="8"
          class="details-section"
          v-if="!$validation.isEmpty(responseScore)"
        >
          <span class="details-header">
            {{ $t('survey.total_score') }}
          </span>
          <span class="survey-detail">{{ responseScore }}</span>
        </el-col>
      </el-row>
      <div class="height680 overflow-y-scroll">
        <table class="width100 border-tlbr-none">
          <tbody>
            <tr>
              <td colspan="100%">
                <div class="qanda-section" v-if="!$validation.isEmpty(pages)">
                  <div
                    v-for="(page, pageIndex) in pages"
                    :key="page.id"
                    :class="[
                      'page-section',
                      !isLastItem(pageIndex, page.pages) && 'border-bottom',
                    ]"
                  >
                    <div class="page-details-header">
                      <div>
                        <div class="page-name">{{ page.name }}</div>
                        <div class="page-description">
                          {{ page.description }}
                        </div>
                      </div>
                      <div
                        v-if="!$validation.isEmpty(getPageScore(page))"
                        class="score-text"
                      >
                        {{ getPageScore(page) }}
                      </div>
                    </div>
                    <template v-if="!$validation.isEmpty(page.questions)">
                      <table
                        class="width100 border-tlbr-none print-inner-table mB150"
                      >
                        <tbody>
                          <tr
                            v-for="(question, questionIndex) in page.questions"
                            :key="question.id"
                            class="fc-tr-border"
                          >
                            <td colspan="100%">
                              <div
                                :class="[
                                  'qandacontainer',
                                  !isLastItem(questionIndex, page.questions) &&
                                    'border-bottom',
                                ]"
                              >
                                <div class="fc__layout__align">
                                  <div class="question">
                                    {{ question.questionNo }}
                                    {{ question.question }}
                                  </div>
                                  <div
                                    v-if="
                                      !$validation.isEmpty(
                                        getQuestionScore(question)
                                      )
                                    "
                                    class="score-text"
                                  >
                                    {{ getQuestionScore(question) }}
                                  </div>
                                </div>
                                <div
                                  v-if="
                                    !$validation.isEmpty(question.description)
                                  "
                                  class="question-description pT5"
                                >
                                  {{ question.description }}
                                </div>
                                <div>
                                  <div
                                    class="answer-heading-text mL27"
                                    v-if="question.answerRequired"
                                  >
                                    {{ $t('qanda.response.answer') }}
                                  </div>
                                  <!-- Answer -->
                                  <FieldDisplayValue :question="question" />
                                  <!-- Comments -->
                                  <template
                                    v-if="(question.answer || {}).comments"
                                  >
                                    <div
                                      class="bold f14 pT15 text-fc-grey remarks-field mL27"
                                    >
                                      {{ (question || {}).commentsLabel }}
                                    </div>
                                    <div class="answer mL27">
                                      {{ (question.answer || {}).comments }}
                                    </div>
                                  </template>
                                  <!-- Attachments -->
                                  <template
                                    v-if="
                                      !$validation.isEmpty(
                                        (question.answer || {}).attachmentList
                                      )
                                    "
                                  >
                                    <div
                                      class="bold f14 pT15 text-fc-grey remarks-field mL27"
                                    >
                                      {{ (question || {}).attachmentLabel }}
                                    </div>
                                    <FileFieldPreview
                                      class="mL27"
                                      :attachments="
                                        (question.answer || {}).attachmentList
                                      "
                                    />
                                  </template>
                                </div>
                              </div>
                            </td>
                          </tr>
                        </tbody>

                        <tbody></tbody>
                      </table>
                    </template>
                    <div v-else class="question-empty-state">
                      <inline-svg
                        src="svgs/inspection-empty"
                        iconClass="icon text-center icon-80"
                      ></inline-svg>
                      <div class="questions-empty-text">
                        {{ $t('qanda.response.no_questions') }}
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import InspectionPDF from 'pages/inspection/individual-inspection/InspectionPdfWidget.vue'
import { isEmpty } from '@facilio/utils/validation'
import helpers from 'src/util/helpers.js'
const { getOrgMoment: moment } = helpers

export default {
  name: 'IndividualSurveyResult',
  extends: InspectionPDF,
  props: [
    'showResultDialog',
    'details',
    'surveyLoading',
    'recordName',
    'module',
    'moduleDisplayName',
    'surveyName',
  ],
  watch: {
    details: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          await this.loadTemplate('surveyResponse')
        }
      },
      immediate: true,
    },
  },
  computed: {
    moduleName() {
      let { moduleName } = this.$attrs
      return moduleName
    },
    surveyTitle() {
      let { details } = this
      let { name } = details || {}
      if (!isEmpty(name)) {
        return name
      }
      return ''
    },
    recordModuleName() {
      let { moduleName } = this.$attrs
      return moduleName
    },
    responseTime() {
      let { details } = this
      let { sysModifiedTime } = details || {}
      if (!isEmpty(sysModifiedTime))
        return moment(sysModifiedTime).format('DD/MM/YYYY')
      else return 'N/A'
    },
  },
  methods: {
    closeDialog() {
      this.$emit('update:showResultDialog', false)
    },
  },
}
</script>
<style scoped>
.survey-response {
  margin-top: -30px;
  overflow-y: scroll;
}
.survey-details {
  display: flex;
  margin-bottom: 20px;
}
.details-section {
  height: 50px;
  display: flex;
  flex-direction: column;
}
.template-name {
  font-size: 16px;
  margin-bottom: 10px;
}
.details-header {
  color: #324056;
  font-weight: normal;
  font-size: 13px;
  margin-bottom: 7px;
}
.survey-detail {
  color: #324056;
  font-size: 14px;
  font-weight: bold;
  text-transform: capitalize;
}
.question {
  font-size: 16px;
  margin-bottom: 2px;
  font-weight: 500;
  color: #324056;
  letter-spacing: 0.7px;
  font-size: 14px;
  word-break: keep-all;
}
.score-text {
  font-size: 13px;
  color: #16b676;
  letter-spacing: 0.46px;
  white-space: nowrap;
}
</style>
