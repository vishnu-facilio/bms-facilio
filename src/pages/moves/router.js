export default {
  path: 'mv',
  component: () => import('pages/moves/Layout'),
  children: [
    // moves Routes
    {
      name: 'moves-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'moves-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'new-move',
      path: 'moves/new',
      component: () => import(`pages/moves/movesForm`),
    },
    {
      name: 'edit-move',
      path: 'moves/:id/edit',
      props: true,
      component: () => import(`pages/moves/movesForm`),
    },
    {
      name: 'movesList',
      path: 'moves/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/moves/movesList`),
    },
    {
      path: 'moves/:viewname',
      component: () => import('pages/moves/movesListOverview.vue'),
      children: [
        {
          name: 'moveSummary',
          path: ':id/overview',
          component: () => import('pages/moves/movesOverview.vue'),
        },
      ],
    },
  ],
}
