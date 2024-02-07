export default {
  path: 'ac',
  component: () => import('pages/accounting/Layout'),
  children: [
    // Budget Routes
    {
      name: 'budget-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'budget-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'new-budget',
      path: 'budget/new',
      component: () => import(`pages/accounting/budget/BudgetForm`),
    },
    {
      name: 'edit-budget',
      path: 'budget/:id/edit',
      props: true,
      component: () => import(`pages/accounting/budget/BudgetForm`),
    },
    {
      name: 'budgetList',
      path: 'budget/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/accounting/budget/BudgetList`),
    },
    {
      name: 'budgetSummary',
      path: 'budget/summary/:id',
      component: () => import(`pages/accounting/budget/BudgetSummary`),
    },
    // Chart Of Accounts Routes
    {
      name: 'coaList',
      path: 'chartofaccount/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () =>
        import(`pages/accounting/chartofaccounts/ChartOfAccountsList`),
    },
    {
      name: 'accountTypeList',
      path: 'accounttype/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/accounting/accounttype/AccountTypeList`),
    },
    {
      path: 'schedule',
      component: () =>
        import('pages/workorder/workorders/v1/woViewScheduleList'),
      meta: { module: 'budget' },
    },
    {
      name: 'budgetreports',
      path: 'reports',
      component: () => import('pages/report/Layout.vue'),
      meta: { module: 'budget' },
      children: [
        {
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary.vue'),
        },
        // {
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: { module: 'budget' },
        // },
        {
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'budget'},
        },
      ],
    },
    {
      name: 'budgetanalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'budget',
        switch: 'yes',
      },
    },
  ],
}
