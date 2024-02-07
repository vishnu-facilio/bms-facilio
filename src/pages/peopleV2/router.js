import employeeRoutes from 'src/pages/peopleV2/employee/route.js'
import shiftRoutes from 'src/pages/peopleV2/shift/route.js'
import shiftPlannerRoutes from 'src/pages/peopleV2/shift-planner/route.js'
import breakRoutes from 'src/pages/peopleV2/break/route.js'
import attendanceRoutes from 'src/pages/peopleV2/attendance/route.js'

export default {
  name: 'people',
  path: 'pl',
  component: () => import('pages/peopleV2/Layout'),
  children: [
    {
      name: 'people-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'people-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },

    ...employeeRoutes,
    ...shiftRoutes,
    ...shiftPlannerRoutes,
    ...breakRoutes,
    ...attendanceRoutes,
  ],
}
