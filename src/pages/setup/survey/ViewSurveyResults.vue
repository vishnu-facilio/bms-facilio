<template>
  <div class="related-list-container" ref="timeLogListContainer">
    <div class="related-list-header">
      <div class="header justify-content-space">
        <div class="flex-direction-column">
          <div
            class="releated-widget-title d-flex flex-direction-column mL0 mB20"
          >
            {{ $t('survey.surveys') }}
          </div>
          <div class="d-flex flex-direction-column justify-center mL0 mB20">
            {{ $t('survey.overall_score') }}{{ overAllScore }}
          </div>
        </div>
        <div class="flex-middle">
          <div
            class="pointer fwBold pL15 f16"
            @click="loadAvailableSurveys()"
            v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
            content="Refresh"
          >
            <i class="el-icon-refresh fwBold f16"></i>
          </div>
          <pagination
            :total="totalSurveyPerPage"
            :perPage="4"
            :current-page="surveyPage"
            @pagechanged="setPage"
            class="mL10"
          ></pagination>
        </div>
      </div>
    </div>
    <div class="width100">
      <div class="fc-related-list-table contract-table">
        <div v-if="isLoading" class="height55vh flex-center-vH">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <div
          class="text-center  flex-center-vH"
          v-else-if="$validation.isEmpty(surveys) && !isLoading"
        >
          <InlineSvg
            src="svgs/emptystate/readings-empty"
            class="mL40"
            iconClass="icon text-center icon-xxxxlg emptystate-icon-size "
          ></InlineSvg>

          <div class="fc-black-dark f18 bold pL50 line-height10">
            {{ $t('survey.no_surveys_available') }}
          </div>
        </div>
        <el-table
          :data="surveys"
          style="width: 100%;"
          class="fc-table-th-pad0"
          height="100%"
          :fit="true"
          v-else
        >
          <el-table-column :label="$t('survey.survey_name')" class="pR0">
            <template v-slot="survey">
              <div
                class="table-subheading main-field-column"
                @click="showIndividualResult(survey.row)"
              >
                {{ survey.row.name }}
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('survey.survey_respondent')" class="pR0">
            <template v-slot="survey">
              <div class="table-subheading">
                {{ getSurveyRespondent(survey.row) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('survey.score')" class="pR0">
            <template v-slot="survey">
              <div class="table-subheading">
                {{ getSurveyScore(survey.row) }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <IndividualSurveyResult
      :showResultDialog.sync="showResultDialog"
      :details="details"
      :surveyLoading.sync="surveyLoading"
      :recordName="recordName"
      :moduleName="moduleName"
      :moduleDisplayName="moduleDisplayName"
      :surveyName="surveyName"
    />
  </div>
</template>

<script>
import Pagination from 'pages/setup/AuditLog/AuditLogPagination'
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import IndividualSurveyResult from './IndividualSurveyResult.vue'
import { getSurveyDetails, loadAllSurveys } from './SurveyUtil'

export default {
  name: 'surveyResponseWidget',
  data() {
    return {
      surveyPage: 1,
      surveyPerPage: 4,
      totalSurveyPerPage: 0,
      templatePage: 1,
      templatePerPage: 50,
      isLoading: false,
      surveys: [],
      templates: [],
      totalScore: null,
      showResultDialog: false,
      details: null,
      surveyLoading: false,
      surveyName: '',
    }
  },
  components: { Pagination, Spinner, IndividualSurveyResult },
  props: ['moduleName', 'moduleDisplayName', 'recordName'],
  computed: {
    recordId() {
      return parseInt(this.$route.params.id)
    },
    viewName() {
      return 'all'
    },
    answeredSurveyCount() {
      let { surveys } = this
      if (!isEmpty(surveys)) {
        surveys = surveys.filter(survey => survey.responseStatus === 4)
        return surveys.length
      }
      return null
    },
    overAllScore() {
      let { surveys } = this
      let overAllScore = 0
      if (!isEmpty(surveys)) {
        let nonZeroScoreSurveys = surveys.filter(
          survey => survey.hasOwnProperty('fullScore') && survey.fullScore !== 0
        )
        if (!isEmpty(nonZeroScoreSurveys)) {
          overAllScore =
            nonZeroScoreSurveys.reduce(
              (acc, survey) =>
                acc + (survey.totalScore / survey.fullScore) * 100,
              0
            ) / nonZeroScoreSurveys.length

          return `${Math.round(overAllScore)}%`
        }
      }
      return `${overAllScore}%`
    },
    ...mapGetters(['getUser']),
  },
  created() {
    this.initSurveyDetails()
  },
  watch: {
    surveyPage: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadAvailableSurveys()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async initSurveyDetails() {
      await this.loadAvailableSurveys()
    },
    async loadAvailableSurveys() {
      this.isLoading = true
      let { recordId, moduleName, surveyPage, surveyPerPage, viewName } = this
      let type = { isViewSurveyResult: true }
      this.surveys = await loadAllSurveys(
        moduleName,
        recordId,
        type,
        surveyPage,
        surveyPerPage,
        viewName
      )
      this.totalSurveyPerPage = this.surveys.length
      this.isLoading = false
    },
    async loadSurveyResponse(selectedSurvey) {
      this.surveyLoading = true
      this.details = await getSurveyDetails(selectedSurvey)
      this.surveyLoading = false
    },
    showIndividualResult(survey) {
      this.showResultDialog = true
      this.loadSurveyResponse(survey)
      let { name } = survey
      this.surveyName = name
    },
    getSurveyRespondent(survey) {
      let { assignedTo } = survey || {}
      if (!isEmpty(assignedTo)) {
        let { name } = assignedTo || {}
        return name || '---'
      }
      return '---'
    },
    getSurveyScore(survey) {
      let { scorePercent } = survey || {}
      if (!isEmpty(scorePercent)) {
        return `${scorePercent}/100 (${scorePercent}%)`
      }
      return '---'
    },
    setPage(page) {
      this.surveyPage = page
    },
  },
}
</script>
