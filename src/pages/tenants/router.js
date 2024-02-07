export default {
  name: 'tenantsmanagement',
  path: 'tm',
  meta: { root: 'tm' },
  component: () => import('pages/tenants/Layout'),
  children: [
    {
      name: 'tenanthomev1',
      path: 'tenants/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'tenant',
      }),
      component: () => import('pages/tenants/TenantList'),
    },
    {
      name: 'tenant-new',
      path: 'new/tenant/create',
      component: () => import('pages/tenants/TenantNew'),
    },
    {
      name: 'tenant-edit',
      path: 'new/tenant/edit/:id',
      props: route => ({
        id: route.params.id,
        moduleName: 'tenant',
      }),
      component: () => import('pages/tenants/TenantNew'),
    },
    {
      name: 'tenant-list',
      path: 'tenants/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        id: route.params.id,
        moduleName: 'tenant',
      }),
      component: () => import('pages/tenants/TenantList.vue'),
      children: [
        {
          name: 'tenantSummary',
          path: ':id/overview',
          component: () => import('pages/tenants/TenantOverviewPage.vue'),
        },
      ],
    },
    {
      name: 'tm-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'tm-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'tenantcontact-new',
      path: 'new/tenantcontact/create',
      component: () => import('pages/tenantcontact/TenantContactNew'),
    },
    {
      name: 'tenantcontact-edit',
      path: 'new/tenantcontact/edit/:id',
      component: () => import('pages/tenantcontact/TenantContactNew'),
    },
    {
      name: 'tenantcontactlist',
      path: 'tenantcontact/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        id: route.params.id,
        moduleName: 'tenantcontact',
      }),
      component: () => import('pages/tenantcontact/TenantContactList'),
      children: [
        {
          name: 'tenantcontact',
          path: ':id/overview',
          component: () =>
            import('pages/tenantcontact/TenantContactOverviewPage'),
        },
      ],
    },
    {
      name: 'tenantunit-list',
      path: 'tenantunit/:viewname',
      props: route => ({
        viewname: route.params.viewname,
        id: route.params.id,
        moduleName: 'tenantunit',
      }),
      component: () => import('pages/tenantunit/TenantUnitList'),
      children: [
        {
          name: 'tenantUnitSummary',
          path: ':id/overview',
          component: () => import('pages/tenantunit/TenantUnitOverviewPage'),
        },
      ],
    },
    // temporary fix for altayer, will be removed
    {
      name: 'tenantunit-list',
      path: 'tenantunit',
      props: route => ({
        viewname: route.params.viewname,
        id: route.params.id,
        moduleName: 'tenantunit',
      }),
      component: () => import('pages/tenantunit/TenantUnitList'),
      children: [
        {
          name: 'tenantUnitSummary',
          path: ':id/overview',
          component: () => import('pages/tenantunit/TenantUnitOverviewPage'),
        },
      ],
    },
    {
      name: 'tenantunit-new',
      path: 'new/tenantunit/create',
      component: () => import('pages/tenantunit/TenantUnitForm'),
    },
    {
      name: 'tenantunit-edit',
      path: 'new/tenantunit/edit/:id',
      component: () => import('pages/tenantunit/TenantUnitForm'),
    },
    {
      name: 'tenantunit-viewmanager',
      path: ':moduleName/list/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },

    {
      name: 'tenantanalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'tenant',
        switch: 'yes',
        moduleType: 'tenant',
      },
    },
    {
      name: 'tenantreports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: {
        module: 'tenant',
      },
      children: [
        {
          name: 'tenantnewreport',
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
        },
        // {
        //   name: 'tenantschedule',
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: {
        //     module: 'tenant',
        //   },
        // },
        {
          name: 'tenantschedule',
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'tenant' },
        },
      ],
    },
    // Quotation Routes
    // form

    {
      path: 'quotation/new',
      component: () => import(`pages/quotation/v1/QuotationForm`),
    },
    {
      path: 'quotation/edit/:id',
      component: () => import(`pages/quotation/v1/QuotationForm`),
    },
    // list
    {
      name: 'quotation-list',
      path: 'quotation/:viewname?',
      props: true,
      component: () => import(`pages/quotation/v1/QuotationList`),
    },
    // summary
    {
      path: 'quotation/:viewname',
      component: () => import('pages/quotation/v1/QuotationSummaryList.vue'),
      children: [
        {
          name: 'quotationSummary',
          path: ':id/overview',
          component: () => import('pages/quotation/v1/QuotationSummary.vue'),
        },
        {
          path: ':id/sendmail',
          props: true,
          component: () => import(`pages/quotation/v1/QuotationSendMailForm`),
        },
      ],
    },
  ],
}
