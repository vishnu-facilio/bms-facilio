export default {
  name: 'ct',
  path: 'ct',
  component: () => import('pages/contract/TabLayout.vue'),
  children: [
    //Reports
    {
      name: 'contractanalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'contracts',
        switch: 'yes',
        moduleType: 'contracts',
      },
    },
    {
      name: 'contratreports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: {
        module: 'contracts',
      },
      children: [
        {
          name: 'contractnewreport',
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
        },
        // {
        //   name: 'contractschedule',
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: {
        //     module: 'contracts',
        //   },
        // },
        {
          name: 'contractschedule',
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'contracts'},
        },
      ],
    },
    // Forms
    {
      name: 'newcontract',
      path: ':moduleName/new',
      component: () => import(`pages/contract/form/ContractForms`),
    },
    {
      name: 'editcontract',
      path: ':moduleName/edit/:id',
      component: () => import(`pages/contract/form/ContractForms`),
    },
    // List
    {
      path: ':moduleName',
      redirect: ':moduleName/all',
    },
    {
      name: 'contractlist-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'contractlist-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'contractList',
      path: ':moduleName/:viewname',
      props: true,
      component: () => import('pages/contract/list/ContractCommonList.vue'),
    },
    // Summary
    {
      name: 'purchasecontractsummary',
      path: 'purchasecontracts/:viewname/summary/:id',
      component: () => import(`pages/contract/summary/PurchaseContractSummary`),
    },
    {
      name: 'labourcontractsummary',
      path: 'labourcontracts/:viewname/summary/:id',
      component: () => import(`pages/contract/summary/LabourContractSummary`),
    },
    {
      name: 'rentalleasecontractsummary',
      path: 'rentalleasecontracts/:viewname/summary/:id',
      component: () =>
        import(`pages/contract/summary/RentalLeaseContractSummary`),
    },
    {
      name: 'warrantycontractsummary',
      path: 'warrantycontracts/:viewname/summary/:id',
      component: () => import(`pages/contract/summary/WarrantyContractSummary`),
    },
  ],
}
