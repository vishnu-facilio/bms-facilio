import { pageTypes, tabTypes, DEFAULT } from '@facilio/router'
import ModuleList from './module-list/routes'
import Other from './others/routes'

const {
  APP_HOME,
  LIST,
  OVERVIEW,
  CREATE,
  EDIT,
  VIEW_MANAGER,
  VIEW_CREATION,
} = pageTypes

const { CUSTOM } = tabTypes

const routes = [
  {
    pageType: LIST,
    moduleName: DEFAULT,
    component: () => import('src/newapp/list/CommonModuleList.vue'),
    props: route => {
      let { params = {} } = route || {}
      return {
        ...params,
        isCustomModule: true,
      }
    },
  },
  {
    pageType: OVERVIEW,
    moduleName: DEFAULT,
    component: () => import('src/newapp/list/CommonModuleList.vue'),
    props: route => {
      let { params = {} } = route || {}
      return {
        ...params,
        isCustomModule: true,
      }
    },
    children: [
      {
        path: '',
        component: () => import('pages/custom-module/CustomModuleSummary.vue'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: DEFAULT,
    component: () => import('pages/custom-module/ModuleForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: DEFAULT,
    component: () => import('pages/custom-module/ModuleForm.vue'),
  },
  {
    pageType: VIEW_MANAGER,
    moduleName: DEFAULT,
    component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
  },
  {
    pageType: VIEW_CREATION,
    moduleName: DEFAULT,
    component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
  },
  {
    tabType: CUSTOM,
    config: {
      type: 'serviceCatalog',
    },
    component: () => import('src/OperationalVisibility/DefaultLayout.vue'),
    children: [
      {
        path: '',
        redirect: { path: 'requests' },
      },
      {
        path: 'requests',
        component: () => import('PortalTenant/catalog/CatalogList'),
      },
      {
        path: 'requests/:catalogId',
        component: () => import('PortalTenant/catalog/CatalogRequest'),
      },
    ],
  },
  ...ModuleList,
  ...Other,
]

export default [
  {
    pageType: APP_HOME,
    // This route will be used as the default app route for apps with layoutType as 2
    // unless explicity overriden using appName down below
    appName: DEFAULT,
    layoutType: 2,
    component: () => import('pages/Home'),
    meta: {
      requiresAuth: true,
    },
    // The following components will be used to render default pages for tabs
    // unless explicity overriden by specifying criteria like moduleName
    children: [...routes],
  },
]
