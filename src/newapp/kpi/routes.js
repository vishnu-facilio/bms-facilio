import { pageTypes } from '@facilio/router'

export default [
  {
    pageType: pageTypes.READING_KPI_LIST,
    component: () => import('pages/energy/kpi/ReadingKpiList.vue'),
  },
  {
    pageType: pageTypes.MODULE_KPI,
    component: () => import('pages/energy/kpi/ModulekpiList.vue'),
  },
  {
    pageType: pageTypes.READING_KPI_TEMPLATE,
    component: () => import('pages/energy/kpi/ReadingKpiTemplates.vue'),
  },
  {
    pageType: pageTypes.READING_KPI_SUMMARY,
    component: () => import('pages/energy/kpi/kpiSummaryLayout.vue'),
  },
]
