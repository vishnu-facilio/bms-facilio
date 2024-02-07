import { isEmpty } from '@facilio/utils/validation'
import { getSurveyDetails } from 'pages/setup/survey/SurveyUtil'

export default {
  data() {
    return {
      surveyDetails: {
        answeredSurveys: [],
      },
      showSurveyDialog: false,
      surveyLoading: false,
      showIndividualResponse: false,
      selectedSurvey: {},
      responseStatusHash: {
        1: 'DISABLED',
        2: 'NOT_ANSWERED',
        3: 'PARIALLY_ANSWERED',
        4: 'COMPLETED',
      },
      showAllResponses: false,
    }
  },
  computed: {
    checkIfSurveyLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers
      return isLicenseEnabled('SURVEY')
    },
    isViewAnswersEnabled() {
      let { surveyDetails } = this
      if (!isEmpty(surveyDetails)) {
        let { answeredSurveys } = surveyDetails || {}
        return answeredSurveys.length > 0
      }
      return false
    },
    showViewAllSurvey() {
      let { response } = this.surveyDetails || {}

      let filteredResponse = (response || []).filter(survey => {
        let { responseStatus, isRetakeAllowed, expiryDate } = survey || {}
        let currentTimeInMs = Date.now()

        return (
          (['PARIALLY_ANSWERED', 'NOT_ANSWERED'].includes(
            this.responseStatusHash[responseStatus]
          ) ||
            (this.responseStatusHash[responseStatus] === 'COMPLETED' &&
              isRetakeAllowed)) &&
          expiryDate > currentTimeInMs
        )
      })

      let resLength = this.$getProperty(filteredResponse, 'length', 0)

      return resLength > 0 ? true : false
    },
    isSurveyEnabled() {
      let { surveyDetails } = this
      if (!isEmpty(surveyDetails)) {
        let { isSurveyAvailable } = surveyDetails || {}
        return isSurveyAvailable
      }
      return false
    },
  },
  methods: {
    setshowSurveyDialog() {
      this.showSurveyDialog = true
    },
    async loadSurveyResponse(selectedSurvey) {
      this.surveyLoading = true
      this.selectedSurvey = await getSurveyDetails(selectedSurvey)
      this.surveyLoading = false
    },
    async showAnswers() {
      let { surveyDetails } = this
      let { answeredSurveys = [] } = surveyDetails || {}
      if (answeredSurveys.length === 1) {
        this.showIndividualResponse = true
        this.loadSurveyResponse(answeredSurveys[0])
      } else {
        this.showAllResponses = true
      }
    },
  },
}
