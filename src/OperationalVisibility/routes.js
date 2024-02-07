import { pageTypes, tabTypes } from '@facilio/router'

export default {
  pageType: pageTypes.APP_HOME,
  appName: 'operations',
  layoutType: 3,
  component: () => import('src/OperationalVisibility/Home.vue'),
  meta: {
    requiresAuth: true,
  },
  children: [
    {
      pageType: pageTypes.DASHBOARD_VIEWER,
      component: () => import('src/OperationalVisibility/DashboardLayout.vue'),
    },
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
          component: () =>
            import('src/pages/energyAnalytics/energy/Energy.vue'),
        },
        {
          name: 'energystar-summary',
          path: 'energy/summary/:id',
          component: () =>
            import('src/pages/energyAnalytics/energy/EnergySummary.vue'),
        },
      ],
    },
  ],
}
