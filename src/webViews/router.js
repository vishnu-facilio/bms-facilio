export default [
  {
    path: '/webview/tenant',
    component: () => import('src/webViews/pages/TenantLayout'),
    children: [
      {
        path: 'home',
        component: () => import('src/webViews/pages/TenantHomepage'),
      },
      {
        path: 'faq',
        component: () => import('src/webViews/pages/TenantFaq'),
      },
    ],
  },
  {
    path: '/webview/occupant',
    component: () => import('src/webViews/pages/Occupant/OccupantLayout'),
    children: [
      {
        path: 'home',
        component: () => import('src/webViews/pages/Occupant/OccupantHomepage'),
      },
      {
        path: 'overview',
        component: () => import('src/webViews/pages/Occupant/OccupantOverview'),
      },
    ],
  },
  {
    path: '/webview/employee',
    component: () => import('src/webViews/pages/Employee/EmployeeLayout'),
    children: [
      {
        path: 'home',
        component: () => import('src/webViews/pages/Employee/EmployeeHomepage'),
      },
      {
        path: 'overview',
        component: () => import('src/webViews/pages/Employee/EmployeeOverview'),
      },
    ],
  },
  {
    path: '/webview/vendor',
    component: () => import('src/webViews/pages/Vendor/VendorLayout'),
    children: [
      {
        path: 'home',
        component: () => import('src/webViews/pages/Vendor/VendorHomepage'),
      },
    ],
  },
  {
    path: '/webview/client',
    component: () => import('src/webViews/pages/ClientLayout'),
    children: [
      {
        path: 'home',
        component: () => import('src/webViews/pages/TenantHomepage'),
      },
    ],
  },

  {
    path: '/webview/tenant/dashboard/:dashboardlink?',
    alias: '/service/mobiledashboard/:dashboardlink?',
    component: () => import('src/webViews/pages/PortalDashboard'),
    meta: {
      module: 'energydata',
      dashboardlayout: 'mobile',
      source: 'dashboard',
      pageLoading: false,
    },
  },
  {
    path: '/webview/mobiledashboard/:dashboardlink?',
    alias: '/service/mobiledashboard/:dashboardlink?',
    component: () => import('src/webViews/pages/PortalDashboard'),
    meta: {
      module: 'energydata',
      dashboardlayout: 'mobile',
      source: 'dashboard',
      pageLoading: false,
    },
  },
  {
    path: '/webview/qanda',
    component: () => import('src/webViews/pages/Qanda/QandaFormLayout'),
    children: [
      {
        path: ':module/:id',
        component: () => import('src/webViews/pages/Qanda/LiveForm'),
        props: true,
      },
    ],
  },
  {
    path: '/webview/connectedapp/:appname/:widgetname',
    component: () => import('src/webViews/pages/ConnectedAppWebView'),
  },
  {
    path: '/webview/sc',
    component: () => import('src/webViews/pages/SmartControl/Layout'),
    children: [
      {
        path: 'home',
        component: () =>
          import('src/webViews/pages/SmartControl/ControlHomepage'),
      },
      {
        path: ':asset',
        component: () => import('src/webViews/pages/SmartControl/ControlList'),
        children: [
          {
            path: 'lighting',
            name: 'lighting',
            component: () =>
              import('src/webViews/pages/SmartControl/ControlLight'),
          },
          {
            path: 'temperature',
            name: 'temperature',
            component: () =>
              import('src/webViews/pages/SmartControl/ControlTemperature'),
          },
          {
            path: 'fan',
            name: 'fan',
            component: () =>
              import('src/webViews/pages/SmartControl/ControlFan'),
          },
        ],
      },
    ],
  },
  {
    path: '/webview/newanalytics/generate',
    component: () =>
      import('pages/energy/analytics/newTools/MobileAnalytics'),
    meta: {
      module: 'energydata',
      layout: 'mobile',
      source: 'analytics',
      pageLoading: false,
    },
  },
]
