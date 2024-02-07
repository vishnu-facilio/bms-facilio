<template>
  <div
    v-if="showBar"
    class="survey-bar-container"
    data-testid="survey-bar-container"
  >
    <div :class="['sidebar-loader', isLoading && 'on']"></div>

    <div v-if="isLoading" class="d-flex survey-bar">
      {{ $t('panel.loading_load') }}
    </div>

    <div v-else class="d-flex survey-bar">
      <i class="el-icon-warning-outline warning-icon" />
      <el-tooltip
        effect="dark"
        class="m4"
        :content="surveyBarTitle"
        placement="top-start"
      >
        <div class="survey-bar-helper mR40" data-testid="survey-bar-helper">
          {{ surveyBarTitle }}
        </div>
      </el-tooltip>

      <div v-if="showSurveyOptions">
        <div class="survey-btn mR15" v-if="buttonTitle === 'Take Survey'">
          <div
            class="take-survey-btn"
            data-testid="take-survey-btn"
            @click="redirectToLatestSurvey()"
          >
            {{ buttonTitle }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import router from 'src/router'
import { getApp } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import helpers from 'src/util/helpers'
const { getOrgMoment: moment } = helpers

export default {
  name: 'SurveyBar',
  data() {
    return {
      isLoading: false,
      responseStatusHash: {
        1: 'DISABLED',
        2: 'NOT_ANSWERED',
        3: 'PARIALLY_ANSWERED',
        4: 'COMPLETED',
      },
    }
  },
  props: ['surveyDetails', 'moduleName', 'recordId'],
  computed: {
    getCurrentUserId() {
      let { $account } = this
      let { user } = $account || {}
      let peopleId = this.$getProperty(user, 'peopleId', null)
      return peopleId
    },
    surveyBarTitle() {
      let { surveyDetails } = this
      let { response = [] } = surveyDetails || {}
      let name = this.$t('survey.survey_title')
      if (!isEmpty(response)) {
        let latestSurvey = response[response.length - 1] || {}
        let surveyTitle = this.$t('survey.survey_title')
        name = this.$getProperty(latestSurvey, 'name', surveyTitle)
      }
      return name
    },
    buttonTitle() {
      let { surveyDetails, responseStatusHash } = this
      let { response = [] } = surveyDetails || {}
      if (!isEmpty(response)) {
        let latestSurvey = response[response.length - 1] || {}
        if (this.isSurveyExpired()) {
          return null
        } else {
          let { responseStatus, isRetakeAllowed } = latestSurvey || {}

          if (
            responseStatusHash[responseStatus] === 'COMPLETED' &&
            !isRetakeAllowed
          ) {
            return ''
          } else if (
            ['COMPLETED', 'PARIALLY_ANSWERED'].includes(
              responseStatusHash[responseStatus]
            )
          ) {
            return this.$t('survey.retake_survey')
          }

          return this.$t('survey.take_survey')
        }
      }
      return null
    },
    canShowResults() {
      let { surveyDetails, responseStatusHash } = this
      let { response, isSuperAdmin } = surveyDetails || {}

      let canShowResult = false
      response.forEach(survey => {
        let { responseStatus } = survey || {}

        canShowResult =
          canShowResult || responseStatusHash[responseStatus] === 'COMPLETED'
      })
      return canShowResult && isSuperAdmin
    },
    isSurveyForCurrentUser() {
      let { surveyDetails, getCurrentUserId } = this
      let { response = [] } = surveyDetails || {}
      let { assignedTo } = response[response.length - 1] || {}
      let { id } = assignedTo || {}
      return id === getCurrentUserId
    },
    showBar() {
      let {
        showViewAllSurvey,
        buttonTitle,
        canShowResults,
        isSurveyForCurrentUser,
      } = this
      let canShowSurveyBtn = !isEmpty(buttonTitle)

      return (
        (canShowSurveyBtn || showViewAllSurvey || canShowResults) &&
        buttonTitle === this.$t('survey.take_survey') &&
        isSurveyForCurrentUser
      )
    },
    showSurveyOptions() {
      let { buttonTitle } = this
      return !isEmpty(buttonTitle)
    },
    allSurveysRetakeAvailable() {
      let { surveyDetails, responseStatusHash } = this
      let { response } = surveyDetails || {}
      let allSurveysAvailable = true

      response.forEach(survey => {
        let currentSurveyAvailable = true
        let { responseStatus, isRetakeAllowed } = survey || {}

        currentSurveyAvailable =
          ['COMPLETED', 'PARIALLY_ANSWERED'].includes(
            responseStatusHash[responseStatus]
          ) ||
          (responseStatusHash[responseStatus] === 'COMPLETED' &&
            isRetakeAllowed)
        allSurveysAvailable = allSurveysAvailable || currentSurveyAvailable
      })

      return allSurveysAvailable
    },
    showViewAllSurvey() {
      let { surveyDetails, allSurveysRetakeAvailable } = this
      let { isViewAllSurvey, response } = surveyDetails || {}
      let activeSurveyCount = 0
      let allSurveysExpired = true

      response.forEach(survey => {
        let surveyExpired = this.isSurveyExpired(survey)
        if (!surveyExpired) {
          activeSurveyCount += 1
        }
        allSurveysExpired = allSurveysExpired && surveyExpired
      })

      return (
        isViewAllSurvey &&
        !allSurveysExpired &&
        allSurveysRetakeAvailable &&
        activeSurveyCount > 1
      )
    },
  },
  methods: {
    isSurveyExpired(surveyInfo) {
      let { surveyDetails } = this
      let { response = [] } = surveyDetails || {}
      let retakeTime = null
      let expiryTime = null
      if (!isEmpty(response)) {
        let latestSurvey = response[response.length - 1] || {}
        let { retakeExpiry, expiryDate } = latestSurvey || {}
        retakeTime = retakeExpiry
        expiryTime = expiryDate
      }

      let currentTimeStamp = moment().valueOf()
      let reTakeOptionExpired = false
      let surveyExpired = false

      if (!isEmpty(surveyInfo)) {
        let { retakeExpiry, expiryDate } = surveyInfo || {}
        retakeTime = retakeExpiry
        expiryTime = expiryDate
      }

      if (!isEmpty(retakeTime) && currentTimeStamp > retakeTime) {
        reTakeOptionExpired = true
      }
      if (!isEmpty(expiryTime) && currentTimeStamp > expiryTime) {
        surveyExpired = true
      }
      return reTakeOptionExpired || surveyExpired
    },
    redirectToLatestSurvey() {
      let { surveyDetails } = this
      let { linkName: appName } = getApp()
      let { response = [] } = surveyDetails || {}
      let { id } = response[response.length - 1] || {}
      let params = { id, appName }

      let { href } = router.resolve({
        name: 'newSurveyLiveForm',
        params,
      })

      window.open(href, '_blank')
      window.addEventListener('message', event => {
        if (event.origin !== window.origin) return
        this.$router.go(0)
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.survey-bar {
  height: 45px;
  background-color: #0078b9;
  padding: 20px;
  justify-content: center;
  align-items: center;

  .survey-bar-helper {
    font-size: 14px;
    font-weight: normal;
    line-height: 1.14;
    letter-spacing: 0.5px;
    color: #fff;
    max-width: 230px;
    overflow: hidden;
    word-break: break-all;
    -webkit-line-clamp: 1;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .view-survey {
    font-size: 13px;
    font-weight: bold;
    cursor: pointer;
    line-height: 1.14;
    color: #0263e0;
    &:hover {
      text-decoration: underline;
    }
  }
}

.sidebar-loader.on {
  background: repeating-linear-gradient(
    to right,
    #f6f7f8 0%,
    #ebf4ff 25%,
    #ebf4ff 50%,
    #ebf4ff 75%,
    #ebf4ff 100%
  );
  background-size: 200% auto;
  background-position: 0 100%;
  animation-timing-function: cubic-bezier(0.4, 0, 1, 1);
}

.take-survey-btn {
  color: #fff;
  width: 120px;
  height: 35px;
  line-height: 5px;
  padding: 15px;
  letter-spacing: 0.5px;
  border: solid 1px #fff;
  border-radius: 4px;
  padding-left: 17px;

  &:hover,
  &:active,
  &:focus {
    color: #fff;
    font-weight: 500;
    cursor: pointer;
  }
}
.warning-icon {
  height: 16px;
  width: 16px;
  font-size: 24px;
  color: #fff;
  margin-right: 15px;
  margin-top: -7px;
}
</style>
