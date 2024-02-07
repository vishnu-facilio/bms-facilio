export default [
  // list
  {
    name: 'break-v2-list',
    path: 'break/:viewname?',
    component: () => import('pages/peopleV2/break/BreakList.vue'),
    props: route => ({
      viewname: route.params.viewname,
      moduleName: 'break',
    }),
  },
  {
    name: 'break-viewmanager',
    path: ':moduleName/viewmanager',
    props: true,
    component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
  },
  {
    name: 'break-view-creation-form',
    path: ':moduleName/viewform/:viewname?',
    props: true,
    component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
  },
]
