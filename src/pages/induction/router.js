export default {
  name: 'induction',
  path: 'induction',
  component: () => import('pages/induction/Layout.vue'),
  children: [
    // view manager
    {
      name: 'induction-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'induction-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    // induction tempalate
    {
      name: 'inductionTemplateCreate',
      path: 'template/new',
      component: () =>
        import('pages/induction/induction-template/NewInductionTemplate'),
    },
    {
      name: 'inductionTemplateEdit',
      path: 'template/edit/:id',
      component: () =>
        import('pages/induction/induction-template/NewInductionTemplate'),
    },
    // induction tempalate
    {
      name: 'inductionTemplateList',
      path: 'template/:viewname?',
      component: () =>
        import('pages/induction/induction-template/InductionTemplateList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'inductionTemplate',
      }),
      children: [
        {
          name: 'inductionTemplateSummary',
          path: 'summary/:id',
          component: () =>
            import(
              `pages/induction/induction-template/InductionTemplateSummary`
            ),
          props: true,
          children: [
            // induction builder
            {
              name: 'induction-builder',
              path: 'builder',
              component: () =>
                import('pages/induction/induction-builder/InductionBuilder'),
            },
          ],
        },
      ],
    },

    // individual induction
    {
      name: 'individualInductionList',
      path: 'individual/:viewname?',
      component: () =>
        import('pages/induction/individual-induction/IndividualInductionList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'inductionResponse',
      }),
      children: [
        {
          name: 'individualInductionSummary',
          path: 'summary/:id',
          component: () =>
            import(
              'pages/induction/individual-induction/IndividualInductionSummary'
            ),
          props: true,
        },
      ],
    },
    // induction builder
    {
      name: 'induction-live-form',
      path: ':id/edit',
      component: () =>
        import('pages/induction/individual-induction/InductionLiveForm'),
    },
  ],
}
