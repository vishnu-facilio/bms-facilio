import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'energyStar',
    },
    component: () => import('src/OperationalVisibility/DefaultLayout.vue'),
    children: [
      {
        path: '',
        redirect: { name: 'energystar-home' },
      },
      {
        name: 'energystar-home',
        path: 'energy',
        component: () => import('src/pages/energyAnalytics/energy/Energy.vue'),
      },
      {
        name: 'energystar-summary',
        path: 'energy/summary/:id',
        component: () =>
          import('src/pages/energyAnalytics/energy/EnergySummary.vue'),
      },
    ],
  },
]
