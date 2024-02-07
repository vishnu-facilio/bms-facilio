import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.INDOOR_FLOORPLAN,

    component: () => import('pages/indoorFloorPlan/Layout.vue'),
  },
]
