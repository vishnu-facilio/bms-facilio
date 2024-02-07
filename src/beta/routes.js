import { pageTypes, DEFAULT, tabTypes } from '@facilio/router'
import DispatcherRoutes from '../beta/pages/dispatcher/routes'
import DashboardV2 from '../beta/pages/dashboard/routes.js'
import ModuleList from 'newapp/module-list/routes'
import Other from 'newapp/others/routes'

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
    component: () => import('src/beta/list/ModuleList.vue'),
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
    component: () => import('src/beta/list/ModuleList.vue'),
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
        component: () => import('src/beta/summary/ModuleSummary.vue'),
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
  ...DispatcherRoutes,
  ...DashboardV2,
]

export default [
  {
    pageType: APP_HOME,
    // This route will be used as the default app route for apps with layoutType as 2
    // unless explicity overriden using appName down below
    appName: DEFAULT,
    layoutType: 2,
    component: () => import('src/beta/layouts/HomeLayout'),
    meta: {
      requiresAuth: true,
    },
    // The following components will be used to render default pages for tabs
    // unless explicity overriden by specifying criteria like moduleName
    children: [...routes],
  },
]
