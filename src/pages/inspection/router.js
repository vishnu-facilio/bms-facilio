export default {
  name: 'inspection',
  path: 'inspection',
  component: () => import('pages/inspection/Layout.vue'),
  children: [
    // view manager
    {
      name: 'inspection-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'inspection-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    // inspection tempalate
    {
      name: 'inspectionTemplateCreate',
      path: 'template/new',
      component: () =>
        import('pages/inspection/inspection-template/NewInspectionTemplate'),
    },
    {
      name: 'inspectionTemplateEdit',
      path: 'template/edit/:id',
      component: () =>
        import('pages/inspection/inspection-template/NewInspectionTemplate'),
    },
    {
      name: 'inspectionTemplateList',
      path: 'template/:viewname?',
      component: () =>
        import('pages/inspection/inspection-template/InspectionTemplateList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'inspectionTemplate',
      }),
      children: [
        {
          name: 'inspectionTemplateSummary',
          path: 'summary/:id',
          component: () =>
            import(
              `pages/inspection/inspection-template/InspectionTemplateSummary`
            ),
          props: true,
          children: [
            // inspection builder
            {
              name: 'inspection-builder',
              path: 'builder',
              component: () =>
                import('pages/inspection/inspection-builder/InspectionBuilder'),
            },
          ],
        },
      ],
    },
    // individual inspection
    {
      name: 'individualInspectionList',
      path: 'individual/:viewname?',
      component: () =>
        import(
          'pages/inspection/individual-inspection/IndividualInspectionList'
        ),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'inspectionResponse',
      }),
      children: [
        {
          name: 'individualInspectionSummary',
          path: 'summary/:id',
          component: () =>
            import(
              'pages/inspection/individual-inspection/IndividualInspectionSummary'
            ),
          props: true,
        },
      ],
    },
    // inspection builder
    {
      name: 'inspection-live-form',
      path: ':id/edit',
      component: () =>
        import('pages/inspection/individual-inspection/InspectionLiveForm'),
    },
    {
      name: 'assetsanalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'inspectionTemplate',
        switch: 'yes',
      },
    },
    {
      name: 'inspection-reports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: { module: 'inspectionTemplate' },
      children: [
        {
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
        },
        // {
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: { module: 'workorder' },
        // },
        {
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'inspectionTemplate'}
        },
      ],
    },
  ],
}
