export default {
  name: 'survey',
  path: 'survey',
  component: () => import('pages/survey/Layout.vue'),
  children: [
    // view manager
    {
      name: 'survey-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'survey-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    // survey tempalate
    {
      name: 'surveyTemplateCreate',
      path: 'template/new',
      component: () => import('pages/survey/survey-template/NewSurveyTemplate'),
    },
    {
      name: 'surveyTemplateEdit',
      path: 'template/edit/:id',
      component: () => import('pages/survey/survey-template/NewSurveyTemplate'),
    },
    // survey tempalate
    {
      name: 'surveyTemplateList',
      path: 'template/:viewname?',
      component: () =>
        import('pages/survey/survey-template/SurveyTemplateList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'surveyTemplate',
      }),
      children: [
        {
          name: 'surveyTemplateSummary',
          path: 'summary/:id',
          component: () =>
            import(`pages/survey/survey-template/SurveyTemplateSummary`),
          props: true,
          children: [
            // survey builder
            {
              name: 'survey-builder',
              path: 'builder',
              component: () =>
                import('pages/survey/survey-builder/SurveyBuilder'),
            },
          ],
        },
      ],
    },

    // individual survey
    {
      name: 'individualSurveyList',
      path: 'individual/:viewname?',
      component: () =>
        import('pages/survey/individual-survey/IndividualSurveyList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'surveyResponse',
      }),
      children: [
        {
          name: 'individualSurveySummary',
          path: 'summary/:id',
          component: () =>
            import('pages/survey/individual-survey/IndividualSurveySummary'),
          props: true,
        },
      ],
    },
    // survey builder
    {
      name: 'survey-live-form',
      path: ':id/edit',
      component: () => import('pages/survey/individual-survey/SurveyLiveForm'),
    },
  ],
}
