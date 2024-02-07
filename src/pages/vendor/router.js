export default {
  name: 'vendor',
  path: 'vendor',
  component: () => import('pages/vendor/Layout.vue'),
  children: [
    // view manager
    {
      name: 'vendor-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    // vendor
    {
      name: 'vendor-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'vendorCreate',
      path: 'vendors/new',
      component: () => import('pages/vendor/vendors/VendorForm'),
    },
    {
      name: 'vendorEdit',
      path: 'vendors/edit/:id',
      component: () => import('pages/vendor/vendors/VendorForm'),
    },
    {
      name: 'createVendorDocumentApp',
      path: 'documents/form/new',
      meta: { module: 'vendorDocuments' },
      component: () => import(`pages/vendor/vendors/VendorDocumentCreation`),
    },
    {
      name: 'vendorList',
      path: 'vendors/:viewname?',
      component: () => import('pages/vendor/vendors/VendorList.vue'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'vendors',
      }),
    },
    {
      path: 'vendors/:viewname',
      component: () => import('pages/vendor/vendors/VendorList.vue'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'vendors',
      }),
      children: [
        {
          name: 'vendorsSummary',
          path: 'summary/:id',
          component: () => import('pages/vendor/vendors/VendorSummary.vue'),
          props: true,
        },
      ],
    },
    //insurance
    {
      name: 'insuranceEdit',
      path: 'insurance/edit/:id',
      component: () => import('pages/vendor/insurance/InsuranceCreation.vue'),
    },
    {
      name: 'insuranceCreate',
      path: 'insurance/new',
      component: () => import('pages/vendor/insurance/InsuranceCreation.vue'),
    },
    {
      name: 'insurancesList',
      path: 'insurance/:viewname?',
      component: () => import('pages/vendor/insurance/InsuranceList.vue'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'insurance',
      }),
    },
    {
      path: 'insurance/:viewname',
      component: () => import('pages/vendor/insurance/InsuranceList.vue'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'insurance',
      }),
      children: [
        {
          name: 'insurancesSummary',
          path: 'summary/:id',
          component: () =>
            import('pages/vendor/insurance/InsuranceOverview.vue'),
          props: true,
        },
      ],
    },
    // vendor contacts
    {
      name: 'vendorContactEdit',
      path: 'vendorcontact/edit/:id',
      component: () =>
        import('pages/vendor/vendorcontacts/VendorContactForm.vue'),
    },
    {
      name: 'vendorContactCreate',
      path: 'vendorcontact/new',
      component: () =>
        import('pages/vendor/vendorcontacts/VendorContactForm.vue'),
    },
    {
      name: 'vendorContactsList',
      path: 'vendorcontact/:viewname?',
      component: () => import('pages/vendor/vendorcontacts/VendorContacts.vue'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'vendorcontact',
      }),
    },
    {
      path: 'vendorcontact/:viewname',
      component: () => import('pages/vendor/vendorcontacts/VendorContacts.vue'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'vendorcontact',
      }),
      children: [
        {
          name: 'vendorContactsSummary',
          path: 'summary/:id',
          component: () =>
            import('pages/vendor/vendorcontacts/VendorContactOverviewPage.vue'),
          props: true,
        },
      ],
    },
  ],
}
