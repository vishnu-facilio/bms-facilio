export default {
  name: 'spacemanagement',
  path: 'home',
  component: () => import('pages/spacemanagement/Layout.vue'),
  meta: {
    remember: true,
  },
  children: [
    {
      path: 'dashboard',
      component: () =>
        import(
          'src/pages/new-dashboard/components/dashboard/DashboardPicker.vue'
        ),
      meta: {
        remember: true,
      },
      props: { type: 'viewer' },
      children: [
        {
          path: 'customdashboard/:id',
          component: () => import('pages/dashboard/Customdashboard'),
        },
        {
          path: ':dashboardlink',
          component: () => import('pages/dashboard/DashboardViewer'),
          meta: {
            layout: 'viewer',
          },
        },
        {
          path: ':dashboardlink/:buildingid',
          component: () => import('pages/dashboard/DashboardViewer'),
          meta: {
            layout: 'viewer',
          },
        },
      ],
    },
    {
      path: 'editdashboard',
      props: { type: 'editer' },
      component: () =>
        import(
          'src/pages/new-dashboard/components/dashboard/DashboardPicker.vue'
        ),
      meta: {},
      children: [
        {
          path: ':dashboardlink',
          component: () =>
            import(
              'src/pages/new-dashboard/components/dashboard/DashboardPicker.vue'
            ),
        },
      ],
    },
    {
      path: 'dashboardrules/:dashboardlink',
      component: () => import('pages/new-dashboard/rules/DashboardRules.vue'),
    },
    {
      path: 'newdashboard',
      component: () =>
        import(
          'src/pages/new-dashboard/components/dashboard/DashboardLayout.vue'
        ),
      meta: {
        remember: true,
      },
      children: [
        {
          path: ':dashboardlink',
          component: () =>
            import(
              'src/pages/new-dashboard/components/dashboard/DashboardViewer.vue'
            ),
          meta: {
            layout: 'viewer',
          },
        },
      ],
    },
    {
      path: 'neweditdashboard',
      component: () =>
        import('pages/new-dashboard/components/dashboard/DashboardEditer.vue'),
      meta: {},
      children: [
        {
          path: ':dashboardlink',
          component: () =>
            import(
              'pages/new-dashboard/components/dashboard/DashboardEditer.vue'
            ),
        },
      ],
    },
    {
      name: 'portfolio',
      path: 'portfolio',
      redirect: {
        name: 'sites-portfolio-home',
        params: { moduleName: 'site', viewname: 'all' },
      },
    },
    // {
    //   name: 'portfolio-sites',
    //   path: 'portfolio/sites/:viewname',
    //   moduleName: 'site',
    //   component: () =>
    //     import('pages/spacemanagement/overview/PortfolioHome.vue'),
    //   props: route => ({
    //     viewname: route.params.viewname,
    //     moduleName: 'site',
    //   }),
    // },
    {
      name: 'portfolio-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () =>
        import('src/pages/spacemanagement/overview/ViewManagerLayout.vue'),
    },
    {
      name: 'portfolio-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () =>
        import('src/pages/spacemanagement/overview/ViewCreationForm.vue'),
    },
    {
      name: 'sites-portfolio-home',
      path: 'portfolio/sites/:viewname?',
      component: () =>
        import('pages/spacemanagement/overview/PortfolioHome.vue'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'site',
      }),
    },
    {
      name: 'buildings-portfolio-home',
      path: 'portfolio/buildings/:viewname?',
      component: () =>
        import('pages/spacemanagement/overview/PortfolioHome.vue'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'building',
      }),
    },
    // {
    //   name: 'portfolio-sites',
    //   path: 'portfolio/sites/:viewname',
    //   moduleName: 'site',
    //   component: () =>
    //     import('pages/spacemanagement/overview/PortfolioHome.vue'),
    //   props: route => ({
    //     viewname: route.params.viewname,
    //     moduleName: 'site',
    //   }),
    // },
    // {
    //   name: 'portfolio-buildings',
    //   path: 'portfolio/buildings/:viewname',
    //   moduleName: 'building',
    //   component: () =>
    //     import('pages/spacemanagement/overview/PortfolioHome.vue'),
    //   props: route => ({
    //     viewname: route.params.viewname,
    //     moduleName: 'building',
    //   }),
    // },
    {
      name: 'site',
      path: 'portfolio/:pathname/:viewname/site/:siteid',
      component: () =>
        import('pages/spacemanagement/overview/OverviewLayout.vue'),
      children: [
        {
          name: 'siteOverview-view',
          path: 'overview',
          component: () =>
            import('pages/spacemanagement/overview/SiteOverview.vue'),
        },
        {
          name: 'buildingOverview-view',
          path: 'building/:buildingid',
          component: () =>
            import('pages/spacemanagement/overview/BuildingOverview.vue'),
        },

        {
          name: 'floorOverview-view',
          path: 'floor/:floorid',
          component: () =>
            import('pages/spacemanagement/overview/FloorOverview.vue'),
        },
        {
          name: 'spaceOverview-view',
          path: 'space/:id',
          component: () =>
            import('pages/spacemanagement/overview/SpaceOverview.vue'),
        },
      ],
    },
    {
      path: 'portfolio/site/:siteid',
      component: () =>
        import('pages/spacemanagement/overview/OverviewLayout.vue'),
      children: [
        {
          path: 'overview',
          name: 'site-overview',
          component: () =>
            import('pages/spacemanagement/overview/SiteOverview.vue'),
        },
        {
          path: 'building/:buildingid',
          name: 'building-overview',
          component: () =>
            import('pages/spacemanagement/overview/BuildingOverview.vue'),
        },
        {
          path: 'floor/:floorid',
          name: 'floor-overview',
          component: () =>
            import('pages/spacemanagement/overview/FloorOverview.vue'),
        },
        {
          path: 'space/:id',
          component: () =>
            import('pages/spacemanagement/overview/SpaceOverview.vue'),
        },
      ],
    },
    {
      name: 'zones',
      path: 'portfolio/zones',
      component: () => import('pages/spacemanagement/Zones.vue'),
    },
    {
      name: 'zone',
      path: 'portfolio/zone/:zoneid',
      component: () => import('pages/spacemanagement/OverviewLayout.vue'),
      children: [
        {
          path: 'overview',
          component: () =>
            import('pages/spacemanagement/overview/ZoneOverview.vue'),
        },
      ],
    },
    {
      path: 'reservation',
      component: () => import('pages/resourcebooking/reservation/Layout'),
      meta: {
        remember: true,
      },
      children: [
        {
          name: 'reservationlist',
          path: 'list/:viewname',
          meta: {
            type: 'table',
            module: 'reservation',
          },
          component: () =>
            import('pages/resourcebooking/reservation/ReservationTableList'),
        },
        {
          name: 'reservationplanner',
          path: 'planner',
          meta: {
            type: 'calendar',
            module: 'planner',
          },
          component: () =>
            import('pages/resourcebooking/reservation/ResourceBookingPlanner'),
        },
      ],
    },
    {
      name: 'reservationsummary',
      path: 'reservation/:viewname/summary/:id',
      meta: {
        module: 'reservation',
      },
      component: () =>
        import(`pages/resourcebooking/reservation/ReservationSummary`),
    },
    {
      name: 'contacts-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'contacts-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'contactlist',
      path: 'contact/:viewname?',
      component: () => import('pages/contact/v1/ContactList'),
    },
    {
      name: 'contactsummary',
      path: 'contact/:viewname/summary/:id',
      meta: {
        module: 'contact',
      },
      component: () => import(`pages/contact/v1/ContactSummary`),
    },
    {
      name: 'approvals',
      path: 'approvals',
      component: () => import(`pages/workorder/approval/NewLayout`),
      meta: {
        remember: true,
      },
      children: [
        {
          path: 'wr/:viewname',
          meta: {
            module: 'approval',
          },
          component: () => import('pages/workorder/approval/ApprovalList'),
          children: [
            {
              path: 'summary/:id',
              meta: {
                module: 'approval',
              },
              components: {
                summary: () =>
                  import('pages/workorder/approval/ApprovalSummary'),
              },
            },
          ],
        },
        {
          path: 'wp/:viewname',
          meta: {
            module: 'workpermit',
          },
          component: () =>
            import('pages/workorder/approval/RequestedWorkPermitList'),
        },
      ],
    },
    // etisalt change to be removed
    {
      name: 'Meters',
      path: 'assets/:viewname?',
      alias: 'newassets',
      meta: {
        remember: true,
      },
      component: () => import('pages/assets/asset/v1/AssetList.vue'),
    },
    {
      name: 'Meters overview',
      path: 'assets/:viewname/:assetid',
      component: () => import('pages/assets/asset/v1/AssetOverviewLayout.vue'),
      children: [
        {
          path: 'oldoverview',
          redirect: 'metersummary',
        },
        {
          name: 'metersummary',
          path: 'overview',
          component: () => import('pages/assets/asset/v1/Overview.vue'),
        },
      ],
    },
    {
      name: 'et2-connected-apps',
      path: 'app/:widgetid',
      props: true,
      component: () => import('pages/connectedapps/ConnectedAppView'),
    },
    {
      name: 'et2-custommodules-new',
      path: ':moduleName/create',
      component: () => import('pages/custom-module/CustomModulesCreation.vue'),
    },
    {
      name: 'et2-custommodules-edit',
      path: ':moduleName/edit/:id',
      component: () => import('pages/custom-module/CustomModulesCreation.vue'),
    },
    {
      name: 'et2-custommodules-overview',
      path: ':moduleName/:viewName/:id',
      component: () => import('src/pages/etisalat/utility/UtilitySummaryList'),
      children: [
        {
          name: 'et2-custommodules-summary',
          path: 'summary',
          component: () => import('pages/etisalat/utility/UtilitySummary'),
        },
      ],
    },
    {
      name: 'et2-custommodules-list',
      path: ':moduleName/:viewname?',
      props: true,
      component: () => import('pages/base-module-v2/ModuleList.vue'),
    },
  ],
}
