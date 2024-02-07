import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'mandv',
    },
    component: () => import('src/pages/energyAnalytics/Layout.vue'),
    children: [
      {
        path: '',
        redirect: 'open',
      },
      {
        name: 'mandv-energy-project-new-webtab',
        path: 'new',
        component: () =>
          import('pages/energyAnalytics/mv/MVProjectCreation.vue'),
      },
      {
        name: 'mandv-energy-project-edit-webtab',
        path: 'edit/:id',
        component: () =>
          import('pages/energyAnalytics/mv/MVProjectCreation.vue'),
      },
      {
        name: 'mandv-energy-project-summary-webtab',
        path: ':viewname/:id/overview',
        component: () => import('pages/energyAnalytics/mv/MVSummary.vue'),
      },
      {
        name: 'mandv-energy-project-list-webtab',
        path: ':viewname?',
        component: () => import('pages/energyAnalytics/mv/MVList.vue'),
      },
    ],
  },
]
