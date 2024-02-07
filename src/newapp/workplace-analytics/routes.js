import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'workplace-analytics',
    },
    component: () => import('pages/workplaceAnalytics/Layout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/workplaceAnalytics/Overview.vue'),
      },
      {
        path: ':buildingId/:floorId?',
        component: () =>
          import('pages/workplaceAnalytics/WorkPlaceFloorAnalysis.vue'),
        meta: {
          module: 'desks',
          analyticsType: 'workplacetreemap',
        },
      },
    ],
  },
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'stacking-analytics',
    },
    component: () => import('src/error/UnderConstruction.vue'),
  },
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'stacking-analytics',
    },
    component: () => import('src/error/UnderConstruction.vue'),
  },
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'iwms-survey',
    },
    component: () => import('src/error/UnderConstruction.vue'),
  },
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'survey-template',
    },
    component: () => import('src/error/UnderConstruction.vue'),
  },
]
