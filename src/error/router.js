export default [
  {
    name: 'unauthorized',
    path: '/error/unauthorized',
    props: true,
    component: () => import('./Error409'),
    meta: {
      requiresAuth: false,
    },
  },
  {
    name: 'scheduledmaintenance',
    path: '/error/scheduledmaintenance',
    props: true,
    component: () => import('./Error503'),
    meta: {
      requiresAuth: false,
    },
  },
  {
    name: 'notsupported',
    path: '/error/notsupported',
    component: () => import('./NotSupported'),
  },
  {
    name: 'nopermission',
    path: '/error/nopermission',
    props: true,
    component: () => import('./Error403'),
    meta: {
      requiresAuth: false,
    },
  },
  {
    name: 'pagenotfound',
    path: '/error/pagenotfound',
    props: true,
    component: () => import('./Error404'),
    meta: {
      requiresAuth: false,
    },
  },
  {
    name: 'toManyRequests',
    path: '/error/tomanyrequests',
    props: true,
    component: () => import('./Error429'),
    meta: {
      requiresAuth: false,
    },
  },
  {
    name: 'orgsetup',
    path: '/orgsetup',
    props: true,
    component: () => import('./OrgSetup.vue'),
    meta: {
      requiresAuth: false,
    },
  },
  {
    name: 'fetchdetailserror',
    path: '/error/fetchdetailserror',
    props: true,
    component: () => import('./FetchDetailsError'),
    meta: {
      requiresAuth: false,
    },
  },
  {
    name: 'appredirection',
    path: '/app/redirection',
    props: true,
    component: () => import('./AppRedirectionHelper.vue'),
  },
  {
    name: 'webviewerror',
    path: '/error/webview',
    props: true,
    component: () => import('./WebviewError'),
    meta: {
      requiresAuth: false,
    },
  },
  // Always leave this last one -> Not found
  {
    path: '*',
    component: () => import(`./Error404`),
  },
]
