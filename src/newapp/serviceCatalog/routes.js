import { pageTypes } from '@facilio/router'

export default [
  {
    pageType: pageTypes.CATALOG_LIST,
    component: () => import('PortalTenant/catalog/CatalogList'),
  },
  {
    pageType: pageTypes.CATALOG_REQUEST,
    component: () => import('PortalTenant/catalog/CatalogRequest'),
  },
]
