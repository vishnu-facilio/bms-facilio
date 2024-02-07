import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'pmCalendar',
    },
    component: () => import('pages/workorder/calendar/Layout.vue'),
    children: [
      {
        path: '',
        redirect: { name: 'pmCalender', params: { viewname: 'planned' } },
      },
      {
        name: 'pmPlanner',
        path: 'pmplannernew',
        component: () => import('pages/workorder/PMAssetPlanner'),
      },
      {
        name: 'staffPlanner',
        path: 'staffplannernew',
        component: () => import('pages/workorder/PMStaffPlanner'),
      },
      {
        name: 'spacePlanner',
        path: 'spaceplanner',
        component: () => import('pages/workorder/PMSpacePlanner'),
      },
      {
        name: 'pmCalender',
        path: ':viewname',
        component: () => import('pages/workorder/Calendar'),
      },
    ],
  },
]
