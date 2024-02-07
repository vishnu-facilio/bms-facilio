export default {
  path: 'ps',
  component: () => import('pages/parkingstall/Layout'),
  children: [
    // parkingstall Routes
    {
      name: 'parkingstall-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'parkingstall-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'new-parkingstall',
      path: 'parkingstall/new',
      component: () => import(`pages/parkingstall/parkingstallForm`),
    },
    {
      name: 'edit-parkingstall',
      path: 'parkingstall/:id/edit',
      props: true,
      component: () => import(`pages/parkingstall/parkingstallForm`),
    },
    {
      name: 'parkingstallList',
      path: 'parkingstall/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/parkingstall/parkingstallList`),
    },
    {
      path: 'parkingstall/:viewname',
      component: () =>
        import('pages/parkingstall/parkingstallListOverview.vue'),
      children: [
        {
          name: 'parkingstallSummary',
          path: ':id/overview',
          component: () =>
            import('pages/parkingstall/parkingstallOverview.vue'),
        },
      ],
    },
  ],
}
