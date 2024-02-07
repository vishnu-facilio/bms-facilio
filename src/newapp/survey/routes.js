import { pageTypes } from '@facilio/router'

export default [
  {
    pageType: pageTypes.SURVEY_LIST,
    props: () => ({
      viewname: 'workorder',
      moduleName: 'surveyResponse',
    }),
    component: () => import(`pages/setup/survey/SurveyListPage.vue`),
  },
]
