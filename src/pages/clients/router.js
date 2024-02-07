export default {
  name: 'clientMain',
  path: 'cl',
  component: () => import('pages/clients/Layout'),
  children: [
    {
      name: 'cl-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'cl-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'client-list',
      path: 'client/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'client',
      }),
      component: () => import('pages/clients/ClientList'),
      children: [
        {
          name: 'clientSummary',
          path: ':id/overview',
          component: () => import('pages/clients/ClientOverview'),
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'client',
          }),
        },
      ],
    },
    {
      name: 'client-new',
      path: 'client/create',
      props: () => ({
        moduleName: 'client',
      }),
      component: () => import('pages/clients/ClientForm'),
    },
    {
      name: 'client-edit',
      path: 'client/edit/:id',
      props: route => ({
        id: route.params.id,
        moduleName: 'client',
      }),
      component: () => import('pages/clients/ClientForm'),
    },
    {
      name: 'clientcontacts',
      path: 'clientcontact/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'clientcontact',
      }),
      component: () => import('pages/clients/clientcontact/ClientContacts'),
      children: [
        {
          name: 'clientContactSummary',
          path: ':id/overview',
          component: () =>
            import('pages/clients/clientcontact/ClientContactOverviewPage'),
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'clientcontact',
          }),
        },
      ],
    },
    {
      name: 'clientcontact-new',
      path: 'clientcontact/create',
      component: () => import('pages/clients/clientcontact/ClientContactForm'),
      props: {
        moduleName: 'clientcontact',
      },
    },
    {
      name: 'clientcontact-edit',
      path: 'clientcontact/edit/:id',
      component: () => import('pages/clients/clientcontact/ClientContactForm'),
      props: route => ({
        id: route.params.id,
        moduleName: 'clientcontact',
      }),
    },
  ],
}
