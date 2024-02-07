export default {
  name: 'ca',
  path: 'ca',
  component: () => import('pages/custom-module/Layout.vue'),
  children: [
    {
      name: 'connected-apps',
      path: 'apps/:widgetid',
      props: true,
      component: () => import('pages/connectedapps/ConnectedAppView'),
    },
    {
      name: 'custommodules-viewmanager',
      path: 'modules/:moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'custommodules-new',
      path: 'modules/:moduleName/create',
      component: () => import('pages/custom-module/ModuleForm.vue'),
      props: true,
    },
    {
      name: 'custommodules-edit',
      path: 'modules/:moduleName/edit/:id',
      component: () => import('pages/custom-module/ModuleForm.vue'),
      props: true,
    },
    {
      name: 'custommodules-view-creation-form',
      path: 'modules/:moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'custommodules-list',
      path: 'modules/:moduleName/:viewname?',
      component: () => import('src/newapp/list/CommonModuleList.vue'),
      props: route => {
        let { params = {} } = route || {}
        return {
          ...params,
          isCustomModule: true,
        }
      },
      children: [
        {
          name: 'custommodules-summary',
          path: ':id/summary',
          component: () =>
            import('pages/custom-module/CustomModuleSummary.vue'),
          props: true,
        },
      ],
    },
    {
      name: 'custommoduleanalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'custom',
        switch: 'yes',
        moduleType: 'custom',
      },
    },
    {
      name: 'custommodulereports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: { module: 'custom' },
      children: [
        {
          name: 'custommodulenewreport',
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
        },
        {
          name: 'custommodulereportschedule',
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout'),
          meta: { module: 'custom' },
        },
      ],
    },
  ],
}
