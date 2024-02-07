export default {
  name: 'people',
  path: 'pl',
  meta: { root: 'pl' },
  component: () => import('pages/people/Layout'),
  children: [
    {
      path: 'attendance',
      component: () => import('pages/people/attendance/Layout'),
      meta: { remember: true },
      children: [
        {
          name: 'attendancetablehome',
          path: 'table/:viewname',
          meta: { listType: 'table' },
          component: () =>
            import('pages/people/attendance/AttendanceTableList'),
        },
        {
          name: 'attendancecalendarhome',
          path: 'calendar/:viewname',
          meta: { listType: 'calendar' },
          component: () =>
            import('pages/people/attendance/AttendanceTableList'),
        },
      ],
    },
    {
      path: 'sft',
      component: () => import('pages/people/shift/Layout'),
      meta: { remember: true },
      children: [
        {
          name: 'shifthome',
          path: 'shift/:viewname',
          meta: { module: 'shift' },
          component: () => import('pages/people/shift/ShiftList'),
        },
        {
          name: 'breakhome',
          path: 'break/:viewname',
          meta: { module: 'break' },
          component: () => import('pages/people/shift/BreakList'),
        },
        {
          name: 'shiftplanner',
          path: 'shiftplanner/:viewname',
          meta: { module: 'shiftplanner' },
          component: () => import('pages/people/shift/ShiftPlanner'),
        },
        {
          name: 'shiftrotationhome',
          path: 'shiftrt/:viewname',
          meta: { module: 'shiftRotation' },
          component: () => import('pages/people/shift/ShiftRotationList'),
        },
      ],
    },
    {
      name: 'employee-new',
      path: 'employee/create',
      component: () => import('pages/people/employees/EmployeeForm'),
    },
    {
      name: 'employee-edit',
      path: 'employee/edit/:id',
      component: () => import('pages/people/employees/EmployeeForm'),
    },
    {
      path: 'employee',
      redirect: 'employee/all-employees',
    },
    {
      name: 'employee-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'employee-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'employees-list',
      path: 'employee/:viewname',
      props: true,
      component: () => import('pages/people/employees/Employees.vue'),
    },
    {
      name: 'employeeoverview',
      path: 'employee/:viewName/:id',
      component: () => import('pages/people/employees/EmployeeOverviewList'),
      children: [
        {
          name: 'employeeSummary',
          path: 'summary',
          component: () =>
            import('pages/people/employees/EmployeeOverviewPage'),
        },
      ],
    },
    // department Routes
    {
      name: 'department-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'new-department',
      path: 'department/new',
      component: () => import(`pages/people/department/departmentForm`),
    },
    {
      name: 'edit-department',
      path: 'department/:id/edit',
      props: true,
      component: () => import(`pages/people/department/departmentForm`),
    },
    {
      name: 'departmentList',
      path: 'department/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/people/department/departmentList`),
    },
    {
      path: 'department/:viewname',
      component: () =>
        import('pages/people/department/departmentListOverview.vue'),
      children: [
        {
          name: 'departmentSummary',
          path: ':id/overview',
          component: () =>
            import('pages/people/department/departmentOverview.vue'),
        },
      ],
    },
  ],
}
