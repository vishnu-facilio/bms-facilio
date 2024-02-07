import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.SHIFT_PLANNER,
    component: () => import('src/components/shiftPlanner/PlannerOverview.vue'),
  },
]
