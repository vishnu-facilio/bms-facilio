export default {
  name: 'serviceRequestMain',
  path: 'sr',
  component: () => import('pages/servicerequest/Layout'),
  children: [
    // viewsmanager
    {
      name: 'serviceRequest-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'serviceRequest-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    // form
    {
      name: 'newServiceRequest',
      path: 'serviceRequest/new',
      component: () => import('pages/servicerequest/ServiceRequestForm'),
    },
    {
      name: 'editServiceRequest',
      path: 'serviceRequest/edit/:id',
      component: () => import('pages/servicerequest/ServiceRequestForm'),
    },
    // list & summary
    {
      name: 'serviceRequestList',
      path: 'serviceRequest/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'serviceRequest',
      }),
      component: () => import('pages/servicerequest/ServiceRequestList'),
    },
    {
      name: 'serviceRequestSummary',
      path: 'serviceRequest/:viewname/:id/overview',
      props: route => ({
        viewname: route.params.viewname,
        id: route.params.id,
        moduleName: 'serviceRequest',
      }),
      component: () => import('pages/servicerequest/ServiceRequestSummary'),
    },
    //Contact Directory
    {
      name: 'contact-directory-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      path: 'contactdir',
      redirect: 'contactdir/all',
    },
    {
      name: 'new-contactdir',
      path: 'contactdir/new',
      props: true,
      component: () => import(`pages/community/contactdirectory/ContactsForm`),
    },
    {
      name: 'edit-contactdir',
      path: 'contactdir/:id/edit',
      props: true,
      component: () => import(`pages/community/contactdirectory/ContactsForm`),
    },
    {
      name: 'list-contactdir',
      path: 'contactdir/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'contactdirectory',
        attachmentsModuleName: 'contactdirectoryattachments',
      }),
      component: () => import(`pages/community/contactdirectory/ContactsList`),
      children: [
        {
          name: 'contactDirectorySummary',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
          }),
          component: () =>
            import(`pages/community/contactdirectory/ContactDirectorySummary`),
        },
      ],
    },

    // Admin Documents
    {
      name: 'admindocs-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      path: 'admindocs',
      redirect: 'admindocs/all',
    },
    {
      name: 'new-admindocs',
      path: 'admindocs/new',
      props: true,
      component: () => import(`pages/community/admindocs/AdminDocForm`),
    },
    {
      name: 'edit-admindocs',
      path: 'admindocs/:id/edit',
      props: true,
      component: () => import(`pages/community/admindocs/AdminDocForm`),
    },
    {
      name: 'list-admindocs',
      path: 'admindocs/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'admindocuments',
        attachmentsModuleName: 'admindocumentsattachments',
      }),
      component: () => import(`pages/community/admindocs/AdminDocList`),
      children: [
        {
          name: 'adminDocumentsSummary',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
          }),
          component: () => import(`pages/community/admindocs/AdminDocSummary`),
        },
      ],
    },
    {
      name: 'sranalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'serviceRequest',
        switch: 'yes',
        moduleType: 'serviceRequest',
      },
    },
    {
      name: 'srreports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: {
        module: 'serviceRequest',
      },
      children: [
        {
          name: 'serviceRequestnewreport',
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
        },
        // {
        //   name: 'serviceRequestschedule',
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: {
        //     module: 'serviceRequest',
        //   },
        // },
        {
          name: 'serviceRequestschedule',
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'serviceRequest'}
        },
      ],
    },
  ],
}
