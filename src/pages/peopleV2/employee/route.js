export default [
  // view manager
  // {
  //   name: 'employee-v2-viewmanager',
  //   path: ':moduleName/viewmanager',
  //   props: true,
  //   component: () => import('src/newapp/viewmanager/ViewManagerLayout'),
  // },
  // list view
  // {
  //   name: 'employee-v2-list',
  //   path: 'employee/:viewname',
  //   component: () => import('pages/peopleV2/employee/EmployeeList.vue'),
  //   props: route => ({
  //     viewname: route.params.viewname,
  //     moduleName: 'employee',
  //   }),
  // },

  // {
  //   path: 'employee',
  //   redirect: 'employee/all-employees',
  // },

  // summary & list
  {
    name: 'employee-v2-list',
    path: 'employee/:viewname?',
    component: () => import('pages/peopleV2/employee/EmployeeList.vue'),
    props: route => ({
      viewname: route.params.viewname,
      moduleName: 'employee',
    }),
    children: [
      {
        name: 'employee-v2-summary',
        path: 'summary/:id',
        component: () => import('pages/peopleV2/employee/EmployeeSummary.vue'),
        props: route => ({
          viewname: route.params.viewname,
          moduleName: 'employee',
          id: route.params.id,
        }),
      },
    ],
  },
  // create
  {
    name: 'employee-v2-create',
    path: 'employee/new',
    component: () => import('pages/peopleV2/employee/EmployeeForm.vue'),
  },
  // edit
  {
    name: 'employee-v2-edit',
    path: 'employee/edit/:id',
    component: () => import('pages/peopleV2/employee/EmployeeForm.vue'),
  },
]
