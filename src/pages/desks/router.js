export default {
  path: 'de',
  component: () => import('pages/desks/Layout'),
  children: [
    // desks Routes
    {
      name: 'desks-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'desks-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'new-desk',
      path: 'desks/new',
      component: () => import(`pages/desks/desksForm`),
    },
    {
      name: 'edit-desk',
      path: 'desks/:id/edit',
      props: true,
      component: () => import(`pages/desks/desksForm`),
    },
    {
      name: 'desksList',
      path: 'desks/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/desks/desksList`),
    },
    {
      path: 'desks/:viewname',
      component: () => import('pages/desks/desksListOverview.vue'),
      children: [
        {
          name: 'deskSummary',
          path: ':id/overview',
          component: () => import('pages/desks/desksOverview.vue'),
        },
      ],
    },
  ],
}
