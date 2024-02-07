import { pageTypes } from '@facilio/router'

const { REPORT_VIEW, REPORT_SCHEDULED, REPORT_FORM } = pageTypes

export default [
  {
    pageType: REPORT_VIEW,
    config: {
      type: 'analytics_reports',
    },
    component: () => import('pages/report/Layout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/report/NewReportSummary.vue'),
      },
    ],
  },

  {
    pageType: REPORT_VIEW,
    config: {
      type: 'module_reports',
    },
    component: () => import('pages/report/Layout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/report/ModuleNewReportSummary.vue'),
      },
    ],
  },

  {
    pageType: REPORT_SCHEDULED,
    config: {
      type: 'analytics_reports',
    },
    component: () => import('pages/report/Layout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/report/FolderManagerLayout.vue'),
      },
    ],
  },

  {
    pageType: REPORT_SCHEDULED,
    config: {
      type: 'module_reports',
    },
    component: () => import('pages/report/Layout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/report/FolderManagerLayout.vue'),
      },
    ],
  },

  {
    pageType: REPORT_FORM,
    config: {
      type: 'module_reports',
    },
    component: () => import('pages/report/forms/NewReport.vue'),
  },
]
