import PMRoutes from './pm/router'
export default {
  name: 'wo',
  path: 'wo',
  component: () => import('pages/workorder/Layout'),
  children: [
    {
      path: 'create',
      props: { moduleName: 'workorder' },
      component: () => import('pages/workorder/workorders/v3/WoV3Form'),
    },
    {
      name: 'workorder-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'workorder-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'workorderhomev1',
      path: 'orders/:viewname?',
      component: () =>
        import('pages/workorder/workorders/v3/WorkOrderList.vue'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'workorder',
      }),
    },
    {
      name: 'v3summary',
      path: 'orders/v3summary/:id',
      component: () =>
        import('pages/workorder/workorders/v3/WorkorderOverview'),
    },
    {
      name: 'wosummarynew',
      path: 'orders/summary/:id',
      component: () =>
        import('pages/workorder/workorders/v3/WorkorderOverview'),
      props: route => ({
        viewname: route.params.viewname,
      }),
    },
    {
      name: 'resource-scheduler-viewmanager',
      path: 'timelineviewmanager',
      props: { moduleName: 'workorder' },
      component: () =>
        import('src/newapp/timeline-view/view-layout/ViewManagerLayout.vue'),
    },
    {
      name: 'timeline-view-create',
      path: 'timeline/create',
      props: route => ({
        viewname: route.query.viewname,
        moduleName: 'workorder',
      }),
      component: () =>
        import('src/newapp/timeline-view/view-layout/ViewCreationForm.vue'),
    },
    {
      name: 'timeline-view-edit',
      path: 'timeline/:viewname/edit',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'workorder',
      }),
      component: () =>
        import('src/newapp/timeline-view/view-layout/ViewCreationForm.vue'),
    },
    {
      name: 'resource-scheduler',
      path: 'timeline/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'workorder',
      }),
      component: () => import('src/newapp/timeline-view/TimeLineView.vue'),
    },
    {
      path: 'workpermits',
      redirect: 'workpermits/list',
    },
    // Form
    {
      name: 'new-workpermit',
      path: 'workpermit/new',
      component: () => import('pages/workorder/workpermit/v3/WorkPermitFormV3'),
      props: true,
    },
    {
      name: 'edit-workpermit',
      path: 'workpermit/edit/:id',
      component: () => import('pages/workorder/workpermit/v3/WorkPermitFormV3'),
      props: true,
    },
    // List
    {
      path: 'workpermit',
      redirect: 'workpermit/all',
    },
    {
      name: 'workPermitList',
      path: 'workpermit/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'workpermit',
      }),
      component: () => import('pages/workorder/workpermit/v3/V3WorkPermitList'),
    },
    // Summary
    {
      path: 'workpermit/:viewname/:id',
      component: () =>
        import('pages/workorder/workpermit/v3/WorkPermitSummaryListV3'),
      children: [
        {
          name: 'workPermitSummaryV3',
          path: 'overview',
          component: () =>
            import('pages/workorder/workpermit/v3/WorkPermitSummaryV3'),
          props: true,
        },
      ],
    },
    // New Work Permit routes end
    {
      path: 'schedule',
      component: () =>
        import('pages/workorder/workorders/v1/woViewScheduleList'),
      meta: { module: 'workorder' },
    },
    {
      path: 'planned',
      redirect: 'planned/active',
    },
    {
      path: 'planned/new',
      component: () => import('pages/workorder/preventive/pmnewform'),
    },
    {
      path: 'multiplanned/new',
      component: () => import('pages/workorder/multisitepreventive/pmnewform'),
    },
    {
      name: 'pm-planned-list',
      path: 'planned/:viewname',
      component: () => import('pages/workorder/preventive/v1/NewPmList'),
    },
    {
      path: 'planned/summary/:id',
      component: () => import('pages/workorder/preventive/v1/Pmsummary'),
    },
    {
      path: 'planned/multisummary/:id',
      component: () =>
        import('pages/workorder/multisitepreventive/v1/Pmsummary'),
    },
    // old approvals
    {
      name: 'approvalfullview',
      path: 'approvals/summary/:id',
      component: () =>
        import('pages/workorder/approval/ApprovalSummaryFullView'),
    },
    {
      path: 'approvals',
      component: () => import(`pages/workorder/approval/NewLayout`),
      meta: { remember: true },
      children: [
        {
          path: 'wr/:viewname',
          meta: { module: 'approval' },
          component: () => import('pages/workorder/approval/ApprovalList'),
          children: [
            {
              path: 'summary/:id',
              meta: { module: 'approval' },
              components: {
                summary: () =>
                  import('pages/workorder/approval/ApprovalSummary'),
              },
            },
          ],
        },
        {
          path: 'wp/:viewname',
          meta: { module: 'workpermit' },
          component: () =>
            import('pages/workorder/approval/RequestedWorkPermitList'),
        },
      ],
    },
    {
      name: 'newInventoryRequest',
      path: 'approvals/ir/new/ir/:id?',
      meta: { module: 'inventoryrequests' },
      alias: ':id/edit',
      component: () =>
        import(
          `pages/workorder/approval/inventoryrequest/NewInventoryRequestForm`
        ),
    },
    // New Approvals
    {
      name: 'ApprovalActivities',
      path: 'newapprovals/:moduleName/activities',
      component: () => import(`pages/approvals/ApprovalActivities.vue`),
      props: true,
    },
    {
      name: 'ApprovalList',
      path: 'newapprovals/:moduleName?/:viewname?/:id?',
      component: () => import(`pages/approvals/ApprovalList.vue`),
      props: true,
    },
    // Surveys
    {
      name: 'surveyList',
      path: 'surveys/:viewname?',
      props: () => ({
        viewname: 'workorder',
        moduleName: 'surveyResponse',
      }),
      component: () => import('src/pages/setup/survey/SurveyListPage.vue'),
    },
    // WO & PM Planner
    {
      name: 'calendar',
      path: 'calendar',
      component: () => import('pages/workorder/calendar/Layout'),
      children: [
        {
          path: 'pmplannernew',
          component: () => import('pages/workorder/PMAssetPlanner'),
        },
        {
          path: 'staffplannernew',
          component: () => import('pages/workorder/PMStaffPlanner'),
        },
        {
          path: 'spaceplanner',
          component: () => import('pages/workorder/PMSpacePlanner'),
        },
        {
          path: ':viewname',
          component: () => import('pages/workorder/Calendar'),
        },
      ],
    },

    //Job Plan
    {
      name: 'new-jobplan',
      path: 'jobplan/create',
      component: () => import(`pages/workorder/jobplan/NewJobPlan`),
      props: true,
    },
    {
      name: 'edit-jobplan',
      path: 'jobplan/:id/edit',
      props: true,
      component: () => import(`pages/workorder/jobplan/NewJobPlan`),
    },
    {
      path: 'jobplan',
      redirect: 'jobplan/all',
    },
    {
      name: 'jobPlanList',
      path: 'jobplan/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'jobplan',
      }),
      component: () => import(`pages/workorder/jobplan/JobPlanList`),
    },
    {
      name: 'jobPlanSummary',
      path: 'jobplan/:viewname/:id/summary',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'jobplan',
      }),
      component: () => import(`pages/workorder/jobplan/JobPlanSummary`),
    },
    {
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport'),
      meta: { module: 'workorder', switch: 'yes' },
    },
    {
      path: 'reports/edit/:reportid',
      component: () => import('pages/report/forms/NewReport'),
      meta: { module: 'workorder' },
    },
    {
      path: 'reports/newmatrix',
      component: () => import('pages/report/forms/NewMatrixReport'),
      meta: { module: 'workorder' },
    },
    {
      path: 'reports/newtabular',
      component: () => import('pages/report/forms/NewTabularReport'),
      meta: { module: 'workorder' },
    },
    {
      name: 'woreports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: { module: 'workorder' },
      children: [
        {
          path: 'view/:reportid',
          component: () => import('pages/report/ReportSummary'),
        },
        {
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
          meta: { module: 'workorder' },
        },
        // {
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: { module: 'workorder' },
        // },
        {
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'workorder' },
        },
      ],
    },
    {
      path: 'reports/view/:reportid/show',
      component: () => import('pages/report/ReportChart'),
      meta: { module: 'workorder' },
    },
    ...PMRoutes,
  ],
}
