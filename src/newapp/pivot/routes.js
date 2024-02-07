import { pageTypes } from '@facilio/router'

const { PIVOT_VIEW, PIVOT_FORM, PIVOT_VIEW_MANAGER } = pageTypes

export default [
  {
    pageType: PIVOT_VIEW,
    component: () => import('pages/energy/pivot/Layout.vue'),
    props: route => {
      return {
        id: route.params.reportId ? parseInt(route.params.reportId) : null,
      }
    },
    children: [
      {
        path: '',
        component: () => import('pages/energy/pivot/PivotReportSideBar.vue'),
      },
    ],
  },
  {
    pageType: PIVOT_FORM,
    component: () => import('pages/energy/pivot/PivotBuilderNew.vue'),
    // children: [
    //   {
    //     path: '',
    //     component: () => import('pages/report/NewReportSummary.vue'),
    //   },
    // ],
  },
  {
    pageType: PIVOT_VIEW_MANAGER,
    component: () => import('pages/energy/pivot/Components/FolderManagerLayout.vue'),
    // children: [
    //   {
    //     path: '',
    //     component: () => import('pages/report/NewReportSummary.vue'),
    //   },
    // ],
  },
]
