import { pageTypes } from '@facilio/router'

export default [
  {
    pageType: pageTypes.ANALYTIC_BUILDING,
    component: () => import('pages/energy/analytics/newLayout.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/energy/analytics/newTools/NewBuildingAnalysis.vue'),
      },
    ],
  },
  {
    pageType: pageTypes.ANALYTIC_PORTFOLIO,
    component: () => import('pages/energy/analytics/newLayout.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/energy/analytics/newTools/v1/Fportforlionew.vue'),
      },
    ],
  },
  {
    pageType: pageTypes.ANALYTIC_SITE,
    analyticsType: 'site',
    component: () => import('pages/energy/analytics/newLayout.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/energy/analytics/newTools/BuildingAnalysis.vue'),
      },
    ],
  },
  {
    pageType: pageTypes.HEAT_MAP,
    analyticsType: 'heatmap',
    component: () => import('pages/energy/analytics/newLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/energy/analytics/newTools/Heatmap.vue'),
      },
    ],
  },
  {
    pageType: pageTypes.TREE_MAP,
    analyticsType: 'treemap',
    component: () => import('pages/energy/analytics/newLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/energy/analytics/newTools/Treemap.vue'),
      },
    ],
  },
  {
    pageType: pageTypes.WORKPLACE_TREEMAP,
    analyticsType: 'workplacetreemap',
    component: () => import('pages/energy/analytics/newLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/workplaceAnalytics/Layout.vue'),
      },
    ],
  },
  {
    pageType: pageTypes.REGRESSION,
    component: () => import('pages/energy/analytics/newLayout.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/energy/analytics/newTools/BuildingAnalysis.vue'),
      },
    ],
  },
  {
    pageType: pageTypes.SCATTER,
    component: () => import('pages/energy/analytics/newLayout.vue'),
    children: [
      {
        path: '',
        component: () =>
          import(
            'pages/energy/analytics/scatterAnalytics/scatterAnalytics.vue'
          ),
      },
    ],
  },
]
