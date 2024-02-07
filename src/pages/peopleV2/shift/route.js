export default [
  // summary & list
  {
    name: 'shift-v2-list',
    path: 'shift/:viewname?',
    component: () => import('pages/peopleV2/shift/ShiftList.vue'),
    props: route => ({
      viewname: route.params.viewname,
      moduleName: 'shift',
    }),
    children: [
      {
        name: 'shift-v2-summary',
        path: 'summary/:id',
        component: () => import('pages/peopleV2/shift/ShiftSummary.vue'),
        props: true,
      },
    ],
  },
  {
    name: 'shift-viewmanager',
    path: ':moduleName/viewmanager',
    props: true,
    component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
  },
  {
    name: 'shift-view-creation-form',
    path: ':moduleName/viewform/:viewname?',
    props: true,
    component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
  },
]
