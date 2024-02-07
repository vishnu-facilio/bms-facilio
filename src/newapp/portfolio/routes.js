import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'portfolio',
    },
    component: () => import('src/newapp/portfolio/PortfolioDefaultLayout.vue'),
    children: [
      {
        path: '',
        redirect: 'sites',
      },
      {
        name: 'portfolio-viewsmanager-webtab',
        path: ':moduleName/viewmanager',
        props: true,
        component: () =>
          import('src/pages/spacemanagement/overview/ViewManagerLayout.vue'),
      },
      // {
      //   path: 'site/viewmanager',
      //   name: 'site-viewsmanager',
      //   props: true,
      //   component: () =>
      //     import('src/pages/spacemanagement/overview/ViewManagerLayout.vue'),
      // },
      {
        name: 'portfolio-view-creation-form-webtab',
        path: ':moduleName/viewform/:viewname?',
        props: true,
        component: () =>
          import('src/pages/spacemanagement/overview/ViewCreationForm.vue'),
      },
      // {
      //   path: 'buildings',
      //   redirect: { name: 'default-buildings' },
      // },
      {
        name: 'site-portfolio-module',
        path: 'sites/:viewname?',
        component: () =>
          import('pages/spacemanagement/overview/PortfolioHome.vue'),
        props: {
          moduleName: 'site',
        },
      },
      {
        name: 'building-portfolio-module',
        path: 'buildings/:viewname?',
        component: () =>
          import('pages/spacemanagement/overview/PortfolioHome.vue'),
        props: {
          moduleName: 'building',
        },
      },
      // {
      //   name: 'default-buildings',
      //   path: 'buildings/:viewname',
      //   component: () =>
      //     import('pages/spacemanagement/overview/PortfolioHome.vue'),
      //   props: route => ({
      //     viewname: route.params.viewname,
      //     moduleName: 'building',
      //   }),
      // },
      {
        path: 'site/:siteid',
        component: () =>
          import('pages/spacemanagement/overview/OverviewLayout.vue'),
        children: [
          {
            path: 'overview',
            component: () =>
              import('pages/spacemanagement/overview/SiteOverview.vue'),
          },
          {
            path: 'building/:buildingid',
            component: () =>
              import('pages/spacemanagement/overview/BuildingOverview.vue'),
          },
          {
            path: 'floor/:floorid',
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
        path: ':pathname/:viewname/site/:siteid',
        component: () =>
          import('pages/spacemanagement/overview/OverviewLayout.vue'),
        children: [
          {
            path: 'overview',
            component: () =>
              import('pages/spacemanagement/overview/SiteOverview.vue'),
          },

          {
            path: 'building/:buildingid',
            component: () =>
              import('pages/spacemanagement/overview/BuildingOverview.vue'),
          },

          {
            path: 'floor/:floorid',
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
    ],
  },
]
