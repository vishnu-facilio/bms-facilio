export default {
  name: 'purchase',
  path: 'purchase',
  component: () => import('pages/purchase/Layout.vue'),
  children: [
    // view manager
    {
      name: 'purchase-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'purchase-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    // purchase request
    {
      name: 'purchaserequest-edit',
      path: 'pr/new/:id',
      component: () => import(`pages/purchase/pr/PurchaseRequestCreation`),
      props: true,
    },
    {
      name: 'purchaserequest-create',
      path: 'pr/new',
      component: () => import(`pages/purchase/pr/PurchaseRequestCreation`),
      props: true,
    },
    {
      name: 'purchaserequest',
      path: 'pr/:viewname?',
      component: () => import(`pages/purchase/pr/PurchaseRequestList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'purchaserequest',
      }),
      children: [
        {
          name: 'prSummary',
          path: 'summary/:id',
          component: () => import(`pages/purchase/pr/PrSummary`),
          props: true,
        },
      ],
    },
    // purchase order
    {
      name: 'purchaseorder-edit',
      path: 'po/new/:id',
      component: () => import(`pages/purchase/po/PurchaseOrderCreation`),
      props: true,
    },
    {
      name: 'purchaseorder-create',
      path: 'po/new',
      component: () => import(`pages/purchase/po/PurchaseOrderCreation`),
      props: true,
    },
    {
      name: 'purchaseorder',
      path: 'po/:viewname?',
      component: () => import(`pages/purchase/po/PurchaseOrderList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'purchaseorder',
      }),
      children: [
        {
          name: 'poSummary',
          path: 'summary/:id',
          component: () => import(`pages/purchase/po/PoSummary`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'purchaseorder',
            id: route.params.id,
          }),
        },
      ],
    },
    // Receivables
    {
      name: 'receivable',
      path: 'rv/:viewname?',
      component: () => import(`pages/purchase/rv/ReceivablesList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'receivable',
      }),
      children: [
        {
          name: 'rvsummary',
          path: 'summary/:id',
          component: () => import(`pages/purchase/rv/RvSummary`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'receivable',
            id: route.params.id,
          }),
        },
      ],
    },

    // terms and conditions
    {
      name: 'tandclist',
      path: 'tandc/:viewname?',
      component: () => import(`pages/purchase/tandc/TermsAndConditionList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'termsandconditions',
      }),
      children: [
        {
          name: 'tandcsummary',
          path: 'summary/:id',
          component: () =>
            import(`pages/purchase/tandc/TermsAndConditionSummary`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'termsandconditions',
            id: route.params.id,
          }),
        },
      ],
    },
    {
      path: 'schedule',
      component: () =>
        import('pages/workorder/workorders/v1/woViewScheduleList'),
      meta: { module: 'purchaseorder' },
    },
    {
      name: 'purchaseorderreports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: { module: 'purchaseorder' },
      children: [
        {
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
        },
        // {
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: { module: 'purchaseorder' },
        // },
        {
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'purchaseorder'}
        },
      ],
    },
    {
      name: 'purchaseanalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'purchaseorder',
        switch: 'yes',
      },
    },
    // Request For Quotation
    {
      name: 'new-requestForQuotation',
      path: 'requestForQuotation/new',
      component: () => import(`pages/purchase/rfq/RequestForQuotationForm`),
      props: true,
    },
    {
      name: 'edit-requestForQuotation',
      path: 'requestForQuotation/:id/edit',
      props: true,
      component: () => import(`pages/purchase/rfq/RequestForQuotationForm`),
    },
    {
      name: 'requestForQuotation',
      path: 'requestForQuotation/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'requestForQuotation',
      }),
      component: () => import(`pages/purchase/rfq/RequestForQuotationList`),

      children: [
        {
          name: 'requestForQuotationSummary',
          path: ':id/overview',
          component: () =>
            import(`pages/purchase/rfq/RequestForQuotationSummary`),
          props: true,
        },
      ],
    },
    // Vendor Quotes
    {
      name: 'new-vendorQuotes',
      path: 'vendorQuotes/new',
      component: () => import(`pages/purchase/vendorQuotes/VendorQuotesForm`),
      props: true,
    },
    {
      name: 'edit-vendorQuotes',
      path: 'vendorQuotes/:id/edit',
      props: true,
      component: () => import(`pages/purchase/vendorQuotes/VendorQuotesForm`),
    },
    {
      name: 'vendorQuotes',
      path: 'vendorQuotes/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'vendorQuotes',
      }),
      component: () => import(`pages/purchase/vendorQuotes/VendorQuotesList`),

      children: [
        {
          name: 'vendorQuotesSummary',
          path: ':id/overview',
          component: () =>
            import(`pages/purchase/vendorQuotes/VendorQuotesSummary`),
          props: true,
        },
      ],
    },
  ],
}
