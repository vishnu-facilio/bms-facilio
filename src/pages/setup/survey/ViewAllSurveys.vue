<template>
  <el-dialog
    :title="$t('survey.all_surveys')"
    :visible="showSurveyDialog || showAllResponses"
    width="40%"
    class="fc-dialog-center-container all-surveys"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="survey-container mT10">
      <div v-if="isLoading" class="mT20">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <div v-else>
        <div v-if="!checkSurveyAvailable" style="margin-left:35%;">
          <InlineSvg
            src="svgs/emptystate/readings-empty"
            class="self-center mT10"
            iconClass="icon text-center icon-130 emptystate-icon-size "
          ></InlineSvg>
          <div
            class="fc-black-dark
             f16 bold mT10 line-height10 self-center"
            style="margin-left:-30px"
          >
            {{ $t('survey.no_more_surveys_available') }}
          </div>
        </div>
        <div v-else v-for="survey in surveys" :key="`survey-${survey.id}`">
          <div
            v-if="showAllResponses"
            class="survey-row flex-middle d-flex mB20"
          >
            <div class="survey-title pL30">
              {{ survey.name }}
            </div>
            <div class="survey-btn">
              <div
                class="retake-survey mR20"
                @click="showIndividualResponseDialog(survey)"
              >
                {{ $t('survey.view_survey_answer') }}
              </div>
            </div>
          </div>
          <div v-else class="survey-row flex-middle d-flex mB20">
            <div class="survey-title pL30 mR40">
              {{ survey.name }}
            </div>
            <div class="survey-btn">
              <div
                v-if="canShowTakeSurvey(survey)"
                class="retake-survey mL10 mR20"
                @click="redirectToSurvey(survey.id)"
              >
                {{ $t('survey.take_survey') }}
              </div>
              <div
                class="retake-survey mL10 mR20"
                @click="redirectToSurvey(survey.id)"
                v-else-if="
                  canShowReTakeSurvey(survey) && !isSurveyExpired(survey)
                "
              >
                {{ $t('survey.retake_survey') }}
              </div>
              <span v-else></span>
            </div>
          </div>
          <div class="row-seperator mB10"></div>
        </div>
      </div>
    </div>
    <IndividualSurveyResult
      ref="user-response"
      :showResultDialog.sync="showIndividualResponse"
      :details="details"
      :surveyLoading.sync="surveyLoading"
      :module="moduleName"
      :recordName="recordName"
    />
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import router from 'src/router'
import { getApp } from '@facilio/router'
import { loadAllSurveys, getSurveyDetails } from './SurveyUtil'
import helpers from 'src/util/helpers'
import IndividualSurveyResult from 'pages/setup/survey/IndividualSurveyResult'

const { getOrgMoment: moment } = helpers

export default {
  name: 'ViewAllSurveys',
  props: [
    'showSurveyDialog',
    'moduleName',
    'recordId',
    'showAllResponses',
    'recordName',
  ],
  data() {
    return {
      isLoading: false,
      surveys: [],
      templates: [],
      responseStatusHash: {
        1: 'DISABLED',
        2: 'NOT_ANSWERED',
        3: 'PARIALLY_ANSWERED',
        4: 'COMPLETED',
      },
      buttonTitleHash: {},
      details: {},
      surveyLoading: false,
      surveyPage: 1,
      surveyPerPage: 50,
      templatePage: 1,
      templatePerPage: 50,
      viewName: 'all',
      showIndividualResponse: false,
    }
  },
  components: { IndividualSurveyResult },
  watch: {
    showSurveyDialog: {
      async handler(newVal) {
        if (newVal) {
          await this.loadAvailableSurveys()
        }
      },
      immediate: true,
    },
    showAllResponses: {
      async handler(newVal) {
        if (newVal) {
          await this.loadAvailableSurveys()
        }
      },
      immediate: true,
    },
  },
  computed: {
    checkSurveyAvailable() {
      let { surveys } = this
      return surveys.length > 0
    },
    getCurrentUserId() {
      let { $account } = this
      let { user } = $account || {}
      let peopleId = this.$getProperty(user, 'peopleId', null)
      return peopleId
    },
  },
  methods: {
    async loadAvailableSurveys() {
      this.isLoading = true
      let {
        recordId,
        moduleName,
        surveyPage,
        surveyPerPage,
        viewName,
        showAllResponses,
      } = this
      let type = {}
      let filters = {}
      if (!showAllResponses) {
        type = { isViewAllSurvey: true }
      } else if (showAllResponses) {
        filters = { responseStatus: { operatorId: 54, value: ['4'] } }
      }

      let surveys = []
      surveys = await loadAllSurveys(
        moduleName,
        recordId,
        type,
        surveyPage,
        surveyPerPage,
        viewName,
        null,
        filters
      )

      surveys = surveys.slice()
      surveys = surveys.reverse()
      this.surveys = surveys
      this.fillButtonTitles()
      this.isLoading = false
    },
    isSurveyForCurrentUser(survey) {
      let { assignedTo } = survey || {}
      let { id } = assignedTo || {}
      return id === this.getCurrentUserId
    },
    showAllSurveyResponses(survey) {
      let { responseStatusHash } = this
      let { responseStatus, isRetakeAllowed } = survey || {}
      return (
        ['COMPLETED', 'PARIALLY_ANSWERED', 'NOT_ANSWERED'].includes(
          responseStatusHash[responseStatus]
        ) ||
        (responseStatusHash[responseStatus] === 'COMPLETED' && isRetakeAllowed)
      )
    },
    showIndividualResponseDialog(survey) {
      this.showIndividualResponse = true
      this.loadSurveyDetail(survey)
    },
    async loadSurveyDetail(selectedSurvey) {
      this.surveyLoading = true
      this.details = await getSurveyDetails(selectedSurvey)
      this.surveyLoading = false
    },
    fillButtonTitles() {
      let { surveys, buttonTitleHash } = this
      surveys.forEach(survey => {
        let { id } = survey || {}
        let buttonTitle = this.getButtonTitle(survey)
        this.$set(buttonTitleHash, `${id}`, buttonTitle)
      })
    },
    isSurveyExpired(survey) {
      let { retakeExpiry, expiryDate, isRetakeAllowed } = survey || {}
      let currentTimeStamp = moment().valueOf()
      let reTakeOptionExpired = false
      let surveyExpired = false

      if (!isEmpty(retakeExpiry) && currentTimeStamp > retakeExpiry) {
        reTakeOptionExpired = true
      }
      if (
        !isEmpty(expiryDate) &&
        isEmpty(isRetakeAllowed) &&
        currentTimeStamp > expiryDate
      ) {
        surveyExpired = true
      }
      return reTakeOptionExpired || surveyExpired
    },
    getButtonTitle(survey) {
      let { responseStatusHash } = this
      let { responseStatus, isRetakeAllowed } = survey || {}
      if (
        ['NOT_ANSWERED', 'PARIALLY_ANSWERED'].includes(
          responseStatusHash[responseStatus]
        )
      ) {
        return this.$t('survey.take_survey')
      }
      if (
        responseStatusHash[responseStatus] === 'COMPLETED' &&
        isRetakeAllowed
      ) {
        return this.$t('survey.retake_survey')
      }
      return this.$t('survey.survey_taken')
    },
    canShowTakeSurvey(survey) {
      let { buttonTitleHash } = this
      let { id } = survey || {}
      let buttonTitle = this.$getProperty(buttonTitleHash, `${id}`, '')
      return buttonTitle === 'Take Survey'
    },
    canShowReTakeSurvey(survey) {
      let { buttonTitleHash } = this
      let { id } = survey || {}
      let buttonTitle = this.$getProperty(buttonTitleHash, `${id}`, '')
      return buttonTitle === 'Retake Survey'
    },
    redirectToSurvey(id) {
      let { moduleName, recordId } = this
      let { linkName: appName } = getApp()
      let params = { id, appName }

      let { href } = router.resolve({
        name: 'newSurveyLiveForm',
        params,
        query: { name: moduleName, recordId },
      })
      window.open(href, '_blank')
    },
    closeDialog() {
      let { showAllResponses } = this
      if (showAllResponses) {
        this.$emit('update:showAllResponses', false)
      } else {
        this.$emit('update:showSurveyDialog', false)
      }
    },
  },
}
</script>
<style scoped lang="scss">
.survey-title {
  width: 65%;
}
.survey-btn {
  width: auto;
  margin-top: 10px;
}
.survey-row {
  height: 30px;
  width: 100%;
}
.row-seperator {
  border: 1px solid #d8d8d873;
}
.survey-container {
  cursor: pointer;
  height: 250px !important;
  overflow: hidden !important;
  overflow-y: scroll !important;
}
.retake-survey {
  font-size: 14px;
  font-weight: bold;
  font-style: normal;
  line-height: 1.14;
  letter-spacing: normal;
  color: #0263e0;
  margin-left: 10px;
  &:hover {
    text-decoration: underline;
  }
}
</style>
<style lang="scss">
.all-surveys {
  .el-dialog__body {
    padding: 0px !important;
  }
}
</style>
