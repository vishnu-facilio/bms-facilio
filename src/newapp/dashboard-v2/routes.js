import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.NEW_DASHBOARD,
    component: () => import('src/pages/dasboard-v2/Dashboard.vue'),
    children: [
      {
        path: '*',
        component: () => import('src/pages/dasboard-v2/Dashboard.vue'),
      },
    ],
  },
]
