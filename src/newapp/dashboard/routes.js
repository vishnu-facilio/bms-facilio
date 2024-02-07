import { pageTypes } from '@facilio/router'

const {
  DASHBOARD_VIEWER,
  DASHBOARD_EDITOR,
  DASHBOARD_CREATION,
  DASHBOARD_MANAGER,
  DASHBOARD_RULES,
} = pageTypes

export default [
  {
    pageType: DASHBOARD_VIEWER,
    component: () =>
      import('pages/new-dashboard/components/dashboard/DashboardPicker.vue'),
    props: {
      type: 'viewer',
    },
    children: [
      {
        path: '',
        component: () => import('pages/dashboard/DashboardViewer'),
      },
    ],
  },
  {
    pageType: DASHBOARD_CREATION,
    component: () =>
      import('pages/new-dashboard/components/dashboard/DashboardPicker.vue'),
    props: {
      type: 'editer',
    },
  },
  {
    pageType: DASHBOARD_EDITOR,
    component: () =>
      import('pages/new-dashboard/components/dashboard/DashboardPicker.vue'),
    props: {
      type: 'editer',
    },
  },
  {
    pageType: DASHBOARD_MANAGER,
    component: () => import('pages/NewDashboardManger'),
  },
  {
    pageType: DASHBOARD_RULES,
    component: () => import('pages/new-dashboard/rules/DashboardRules'),
  },
]
