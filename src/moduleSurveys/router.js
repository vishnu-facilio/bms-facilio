export default {
  name: 'surveyResponse',
  path: '/:appName/surveys',
  props: true,
  meta: {
    requiresAuth: true,
  },
  component: () => import('moduleSurveys/pages/Layout'),
  children: [
    //survey live form for all modules --> Customer Satisfaction Survey
    {
      name: 'newSurveyLiveForm',
      path: 'response/:id/attend',
      props: true,
      component: () => import('moduleSurveys/pages/ModuleSurveyLiveForm.vue'),
    },
    {
      name: 'thankYouScreen',
      path: 'response/:id/thankyou',
      component: () => import('moduleSurveys/pages/ThankYouScreen.vue'),
    },
  ],
}
