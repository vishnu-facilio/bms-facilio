export default {
  name: 'assetmanagement',
  path: 'at',
  meta: { root: 'at' },
  component: () => import('pages/assets/Layout.vue'),
  children: [
    {
      name: 'assets-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'assets-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'assethomev1',
      path: 'assets/:viewname?',
      alias: 'newassets',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'asset',
      }),
      meta: { remember: true },
      component: () => import('pages/assets/asset/v1/AssetList.vue'),
    },
    {
      name: 'assetOverview',
      path: 'assets/:viewname/:assetid',
      component: () => import('pages/assets/asset/v1/AssetOverviewLayout.vue'),
      children: [
        {
          name: 'assetsummary',
          path: 'overview',
          component: () => import('pages/assets/asset/v1/Overview.vue'),
        },
      ],
    },
    {
      path: 'schedule',
      component: () =>
        import('pages/workorder/workorders/v1/woViewScheduleList'),
      meta: { module: 'asset' },
    },
    {
      name: 'assetanalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'asset',
        switch: 'yes',
      },
    },
    {
      name: 'assetreports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: { module: 'asset' },
      children: [
        {
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
          meta: { module: 'asset' }
        },
        // {
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: { module: 'workorder' },
        // },
        {
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'asset'}
        },
      ],
    },
    //failure class
    {
      name: 'failure-class-list',
      path: 'failureclass/:viewname?',
      component: () => import(`pages/assets/failureclass/FailureClassList.vue`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'failureclass',
      }),
      children: [
        {
          name: 'failure-class-summary',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'failureclass',
          }),
          component: () =>
            import(`src/pages/assets/failureclass/FailureClassOverview.vue`),
        },
      ],
    },
    {
      name: 'new-failure-class',
      path: 'failureclass/new',
      props: true,
      component: () =>
        import(`src/pages/assets/failureclass/forms/NewFailureCode.vue`),
    },
    {
      name: 'edit-failure-class',
      path: 'failureclass/:id/edit',
      props: true,
      component: () =>
        import(`src/pages/assets/failureclass/forms/NewFailureCode.vue`),
    },
  ],
}
