export default [
  {
    name: 'visitorKiosk',
    path: 'visitor',
    component: () => import('devices/VisitorKiosk/Home'),
    children: [
      {
        path: 'welcome',
        name: 'welcome',
        component: () => import('devices/VisitorKiosk/WelcomeScreen'),
      },
      {
        path: 'checkin',
        name: 'checkin',
        component: () => import('devices/VisitorKiosk/KioskCheckin'),
      },
      {
        path: 'checkout',
        name: 'checkout',
        component: () => import('devices/VisitorKiosk/KioskCheckout'),
      },
      {
        path: 'visitorbadge',
        name: 'visitorbadge',
        component: () => import('devices/VisitorKiosk/KioskVisitorInfo'),
      },
      {
        path: 'visitormessage',
        name: 'visitormessage',
        component: () => import('devices/VisitorKiosk/KioskBlockMessage'),
      },
      {
        path: 'thankyou',
        name: 'visitorthankyou',
        component: () => import('devices/VisitorKiosk/KioskThankYou'),
      },
    ],
  },

  {
    name: 'logbook',
    path: 'logbook/wo',
    component: () => import('devices/DigitalLogBook'),
    children: [
      {
        name: 'deviceWoSummary',
        path: 'summary/:id',
        component: () =>
          import('pages/workorder/workorders/v1/WorkorderSummary'),

        // children: DeviceRouter,
      },
      //needed for workorder component
      {
        name: 'deviceWoList',
        path: ':viewname',
        component: () => import('pages/workorder/workorders/v1/WorkOrderList'),
      },
    ],
  },

  // {
  //   name: 'logbook',
  //   path: 'logbook',
  //   component: () => import('devices/DigitalLogBook'),
  // },
]
