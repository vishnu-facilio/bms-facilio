export default {
  name: 'wp',
  path: 'wp',
  component: () => import('pages/workplaceAnalytics/Layout.vue'),
  children: [
    {
      path: 'workplacetreemap',
      component: () => import('pages/workplaceAnalytics/Overview.vue'),
    },
    {
      path: 'workplacetreemap/:buildingId/:floorId',
      component: () =>
        import('pages/workplaceAnalytics/WorkPlaceFloorAnalysis.vue'),
      meta: {
        module: 'desks',
        analyticsType: 'workplacetreemap',
      },
    },
    {
      path: 'workplacetreemap/:buildingId',
      component: () =>
        import('pages/workplaceAnalytics/WorkPlaceFloorAnalysis.vue'),
      meta: {
        module: 'desks',
        analyticsType: 'workplacetreemap',
      },
    },
  ],
}
