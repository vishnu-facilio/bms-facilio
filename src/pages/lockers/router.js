export default {
  path: 'lo',
  component: () => import('pages/lockers/Layout'),
  children: [
    // Lockers Routes
    {
      name: 'lockers-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'lockers-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'new-locker',
      path: 'lockers/new',
      component: () => import(`pages/lockers/lockersForm`),
    },
    {
      name: 'edit-locker',
      path: 'lockers/:id/edit',
      props: true,
      component: () => import(`pages/lockers/lockersForm`),
    },
    {
      name: 'lockersList',
      path: 'lockers/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/lockers/lockersList`),
    },
    {
      path: 'lockers/:viewname',
      component: () => import('pages/lockers/lockersListOverview.vue'),
      children: [
        {
          name: 'lockerSummary',
          path: ':id/overview',
          component: () => import('pages/lockers/lockersOverview.vue'),
        },
      ],
    },
  ],
}
