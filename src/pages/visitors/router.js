export default {
  name: 'visitorMain',
  path: 'vi',
  component: () => import('pages/visitors/Layout'),
  children: [
    {
      name: 'vi-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'vi-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'visits-list',
      path: 'visits/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'visitorlog',
      }),
      component: () => import('pages/visitors/visits/VisitsList.vue'),
      children: [
        {
          name: 'visits-overview',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'visitorlog',
            notesModuleName: 'newvisitorlognotes',
            attachmentsModuleName: 'newvisitorlogattachments',
          }),
          component: () => import('pages/visitors/visits/VisitsSummary.vue'),
        },
      ],
    },
    {
      name: 'invites-create',
      path: 'invites/new',
      component: () => import(`pages/visitors/invites/InviteForm.vue`),
    },
    {
      name: 'invites-edit',
      path: 'invites/:id/edit',
      component: () => import(`pages/visitors/invites/InviteForm.vue`),
    },
    {
      name: 'invites-list',
      path: 'invites/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'invitevisitor',
      }),
      component: () => import('pages/visitors/invites/InvitesList.vue'),
      children: [
        {
          name: 'invites-overview',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'invitevisitor',
          }),
          component: () => import('pages/visitors/invites/InvitesSummary.vue'),
        },
      ],
    },
    {
      name: 'group-invites-list',
      path: 'groupinvite/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'groupinvite',
      }),
      component: () =>
        import('pages/visitors/groupinvites/GroupInvitesList.vue'),
    },
    {
      name: 'groupinvite-summary',
      path: 'groupinvite/:viewname/:id/summary',
      props: route => ({
        viewname: route.params.viewname,
        id: route.params.id,
        moduleName: 'groupinvite',
      }),
      component: () =>
        import(`pages/visitors/groupinvites/GroupInvitesOverviewList.vue`),
    },
    {
      name: 'group-invites-edit',
      path: 'groupinvite/:id/edit',
      component: () => import(`pages/visitors/invites/InviteForm.vue`),
    },
    {
      name: 'group-invites-create',
      path: 'groupinvite/new',
      component: () => import(`pages/visitors/invites/InviteForm.vue`),
    },

    {
      path: 'visitor/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'visitor',
      }),
      component: () => import(`pages/visitors/visitor/VisitorList`),
    },
    {
      name: 'visitorsummary',
      path: 'visitor/:viewname/:id/summary',
      props: route => ({
        viewname: route.params.viewname,
        id: route.params.id,
        moduleName: 'visitor',
      }),
      component: () => import(`pages/visitors/visitor/VisitorOverviewList`),
    },

    {
      name: 'watchlist-list',
      path: 'watchlist/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'watchlist',
      }),
      component: () => import(`pages/visitors/watchlist/WatchListRecords`),
      children: [
        {
          name: 'watchlist-overview',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'watchlist',
          }),
          component: () =>
            import('pages/visitors/watchlist/WatchListSummary.vue'),
        },
      ],
    },

    {
      name: 'visitoranalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'visitorlog',
        switch: 'yes',
        moduleType: 'visitorlog',
      },
    },
    {
      name: 'visitorreports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: { module: 'visitorlog' },
      children: [
        {
          name: 'visitornewreport',
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
        },
        // {
        //   name: 'visitorschedule',
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: { module: 'visitorlog' },
        // },
        {
          name: 'visitorschedule',
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'visitorlog'}
        },
      ],
    },
  ],
}
