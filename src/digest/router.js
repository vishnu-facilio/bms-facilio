export default [
  {
    path: '/digest',
    component: () => import('src/digest/pages/digestLayout.vue'),
    children: [
      {
        path: 'qualityfmCheckinCheckout',
        component: () => import('src/digest/pages/qualityFmCheckinCheckout'),
      },
      {
        path: 'citdailydigest',
        component: () => import('src/digest/pages/CitDailyDigest'),
      },
    ],
  },
]
