export default {
  name: 'floorplan',
  path: 'fp/:floorId',
  meta: {
    root: 'fp',
  },
  component: () => import('pages/floorPlan/Layout'),
  children: [
    {
      path: 'edit',
      name: 'edit',
      component: () => import('pages/floorPlan/FloorPlanBuilder'),
    },
    {
      path: 'view',
      name: 'view',
      component: () => import('pages/floorPlan/FloorPlanViewer'),
    },
  ],
}
