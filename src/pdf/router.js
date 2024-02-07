export default [
  {
    name: 'pdfHome',
    path: '/:linkName/pdf/summary/:moduleName/:id',
    component: () => import('src/pdf/pages/pdfHome'),
  },
  {
    path: '/:linkName/pdf/report/:id',
    component: () => import('src/pdf/pages/Report/reportPdf'),
  },
  {
    path: '/:linkName/pdf/readingReport/:id',
    component: () => import('src/pdf/pages/Report/ReadingReportPdf'),
  },
  {
    path: '/:linkName/pdf/alarmReport',
    component: () => import('src/pdf/pages/Report/AlarmPdf'),
  },
  {
    path: '/:linkName/pdf/dashboard/:dashboardlink',
    component: () => import('src/pdf/pages/Report/dashboardPdf'),
  },
  {
    path: '/:linkName/pdf/readingreportedit',
    component: () => import('src/pdf/pages/Report/ReadingReportPdf'),
  },
  {
    path: '/:linkName/pdf/site/:siteId',
    component: () => import('src/pdf/pages/Portfolio/SiteQrGeneratorPdf'),
  },
  {
    path: '/:linkName/pdf/building/:buildingId',
    component: () => import('src/pdf/pages/Portfolio/BuildingQrGenerator'),
  },
  {
    path: '/:linkName/pdf/floor/:floorId',
    component: () => import('src/pdf/pages/Portfolio/FloorQrGenerator'),
  },
  {
    path: '/:linkName/pdf/space/:spaceId',
    component: () => import('src/pdf/pages/Portfolio/SpaceQrGenerator'),
  },
  {
    path: '/:linkName/pdf/asset/:assetId',
    component: () => import('src/pdf/pages/assetQr/AssetQrCode'),
  },
]
