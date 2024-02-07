export default {
  path: '/:app/catalog',
  component: () => import('pages/catalog/CatalogHome'),
  children: [
    {
      name: 'service-catalog-list',
      path: 'requests',
      component: () => import('pages/catalog/CatalogList'),
      meta: {
        isApp: true,
      },
    },
    {
      name: 'service-catalog-request',
      path: 'requests/:catalogId',
      component: () => import('pages/catalog/CatalogRequest'),
      meta: {
        isApp: true,
      },
    },
  ],
}
