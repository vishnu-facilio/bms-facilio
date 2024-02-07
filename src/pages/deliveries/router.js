export default {
  path: 'dl',
  component: () => import('pages/deliveries/Layout'),
  children: [
    // Delivery Routes
    {
      name: 'deliveries-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'deliveries-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'new-delivery',
      path: 'deliveries/new',
      component: () => import(`pages/deliveries/deliveriesForm`),
    },
    {
      name: 'edit-delivery',
      path: 'deliveries/:id/edit',
      props: true,
      component: () => import(`pages/deliveries/deliveriesForm`),
    },
    {
      name: 'deliveriesList',
      path: 'deliveries/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/deliveries/deliveriesList`),
    },
    {
      path: 'deliveries/:viewname',
      component: () => import('pages/deliveries/deliveriesListOverview.vue'),
      children: [
        {
          name: 'deliverySummary',
          path: ':id/overview',
          component: () => import('pages/deliveries/deliveriesOverview.vue'),
        },
      ],
    },
    // DeliveryArea Routes
    {
      name: 'deliveryArea-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'new-deliveryArea',
      path: 'deliveryArea/new',
      component: () => import(`pages/deliveryArea/deliveryAreaForm`),
    },
    {
      name: 'edit-deliveryArea',
      path: 'deliveryArea/:id/edit',
      props: true,
      component: () => import(`pages/deliveryArea/deliveryAreaForm`),
    },
    {
      name: 'deliveryAreaList',
      path: 'deliveryArea/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/deliveryArea/deliveryAreaList`),
    },
    {
      path: 'deliveryArea/:viewname',
      component: () =>
        import('pages/deliveryArea/deliveryAreaListOverview.vue'),
      children: [
        {
          name: 'deliveryAreaSummary',
          path: ':id/overview',
          component: () =>
            import('pages/deliveryArea/deliveryAreaOverview.vue'),
        },
      ],
    },
  ],
}
