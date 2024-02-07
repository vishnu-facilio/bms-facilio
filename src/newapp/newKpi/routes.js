import { pageTypes } from '@facilio/router'

export default [
  {
    pageType: pageTypes.NEW_READING_KPI_LIST,
    component: () =>
      import('pages/energy/readingkpi/ReadingKpiListWrapper.vue'),
  },
  {
    pageType: pageTypes.NEW_MODULE_KPI,
    component: () => import('pages/energy/kpi/ModulekpiList.vue'),
  },
]
