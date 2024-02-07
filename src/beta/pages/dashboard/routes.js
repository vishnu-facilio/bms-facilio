import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.NEW_DASHBOARD,
    component: () => import('src/beta/pages/dashboard/Dashboard.vue'),
    children: [
      {
        path: '*',
        component: () => import('src/beta/pages/dashboard/Dashboard.vue'),
      },
    ],
  },
]
