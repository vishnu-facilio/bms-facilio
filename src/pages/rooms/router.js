export default {
  path: 'ro',
  component: () => import('pages/rooms/Layout'),
  children: [
    // rooms Routes
    {
      name: 'rooms-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'rooms-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'new-rooms',
      path: 'rooms/new',
      component: () => import(`src/pages/rooms/RoomsForm.vue`),
    },
    {
      name: 'edit-rooms',
      path: 'rooms/:id/edit',
      props: true,
      component: () => import(`src/pages/rooms/RoomsForm.vue`),
    },
    {
      name: 'roomsList',
      path: 'rooms/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`src/pages/rooms/RoomsList.vue`),
    },
    {
      path: 'rooms/:viewname',
      component: () =>
        import('src/pages/rooms/RoomsListOverview.vue'),
      children: [
        {
          name: 'roomsSummary',
          path: ':id/overview',
          component: () =>
            import('src/pages/rooms/RoomsSummary.vue'),
        },
      ],
    },
  ],
}
