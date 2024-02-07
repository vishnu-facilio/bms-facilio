export default [
  {
    name: 'permalink',
    path: '/link',
    component: () => import('permalink/pages/transition/Layout'),
    meta: {
      requiresAuth: false,
    },
    children: [
      {
        name: 'permalink-transition',
        path: '1',
        component: () => import('permalink/pages/transition/Transition.vue'),
      },
      {
        name: 'auth-transition',
        path: ':moduleName/:recordId/transition/:transitionId',
        props: true,
        component: () =>
          import('permalink/pages/transition/TransitionAuth.vue'),
      },
      {
        name: 'permalink-approval',
        path: '2',
        component: () => import('permalink/pages/approval/Approval.vue'),
      },
    ],
  },
  {
    name: 'serviceportal',
    path: '/link',
    component: () => import('permalink/pages/public/SubmitRequestLayout'),
    children: [
      {
        name: 'submitRequest',
        path: 'submitrequest',
        component: () => import('permalink/pages/public/SubmitRequest'),
        meta: {
          requiresAuth: false,
        },
      },
      {
        name: 'submitThankYou',
        path: 'thankyou/:id',
        component: () => import('permalink/pages/public/RequestSuccess'),
      },
    ],
  },
]
