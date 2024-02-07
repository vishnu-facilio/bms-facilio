import WorkOrderRouter from 'pages/workorder/router'
import FireAlarmRouter from 'pages/firealarm/router'
import HomeRouter from 'pages/spacemanagement/router'
import AssetRouter from 'pages/assets/router'
import PurchaseRoutes from 'pages/purchase/router'
import VendorRoutes from 'pages/vendor/router'
import TenantRouter from 'pages/tenants/router'
import PeopleRouter from 'pages/peopleV2/router'
import PeopleV2Router from 'pages/peopleV2/router'
import ControlRouter from 'pages/controls/router'
import CustomModules from 'pages/custom-module/router'
import EnergyRouter from 'pages/energy/router'
import VisitorRouter from 'pages/visitors/router'
import ServiceRequestRouter from 'pages/servicerequest/router'
import SafetyPlanRouter from 'pages/safetyplan/router'
import ClientRouter from 'pages/clients/router'
import FloorPlanRouter from 'pages/floorPlan/router'
import ContractModules from 'pages/contract/router'
import EnergyAnalyticsRouter from 'pages/energyAnalytics/router'
import CommunityRouter from 'pages/community/router'
import AccountingRouter from 'pages/accounting/router'
import BookingRouter from 'pages/facilitybooking/router'
import InventoryRouter from 'pages/Inventory/router'
import InspectionRouter from 'pages/inspection/router'
import DeliveriesRouter from 'pages/deliveries/router'
import LockersRouter from 'pages/lockers/router'
import ParkingStallRouter from 'pages/parkingstall/router'
import MovesRouter from 'pages/moves/router'
import DesksRouter from 'pages/desks/router'
import InductionRouter from 'pages/induction/router'
import WorkPlaceRouter from 'pages/workplaceAnalytics/router'
import SurveyRouter from 'pages/survey/router'
import PlanningRouter from 'pages/planning/router'
import DispatcherRouter from 'src/beta/pages/dispatcher/router'

export default {
  path: '/app',
  component: () => import('pages/Home'),
  meta: {
    requiresAuth: true,
  },
  children: [
    {
      path: 'play',
      component: () => import('pages/Play'),
    },
    {
      path: 'homepage',
      component: () => import('src/components/homepage/homePageScreen.vue'),
    },
    {
      path: 'apicheck',
      component: () => import('pages/ApiCheck'),
    },
    {
      path: 'floorplay',
      component: () => import('pages/FloorplanPlayground'),
    },
    {
      path: 'map',
      component: () => import('pages/DevloperMap'),
    },
    {
      path: 'chatbot',
      component: () => import('pages/Chatbot'),
    },
    {
      path: 'newdashboardmanager',
      component: () => import('pages/NewDashboardManger'),
    },
    {
      path: 'ftl',
      component: () => import('pages/Ftl'),
    },
    {
      path: 'floorplan/edit/:floorplanid',
      component: () => import('pages/indoorFloorPlan/IndoorFloorPlanEditor'),
    },
    {
      path: 'floorplan/view/:floorplanid',
      component: () => import('pages/indoorFloorPlan/Layout.vue'),
    },
    {
      path: 'floorplan/bookingsview/:floorplanid',
      component: () =>
        import('pages/indoorFloorPlan/IndoorFloorPlanBookingsView'),
    },
    //to be removed
    {
      name: 'al',
      path: 'al',
      component: () => import('pages/etisalat/AlertsLayout.vue'),
      children: [
        {
          name: 'et-connected-apps',
          path: 'apps/:widgetid',
          props: true,
          component: () => import('pages/connectedapps/ConnectedAppView'),
        },
        {
          name: 'et-custommodules-new',
          path: ':moduleName/create',
          component: () =>
            import('pages/custom-module/CustomModulesCreation.vue'),
        },
        {
          name: 'et-custommodules-edit',
          path: ':moduleName/edit/:id',
          component: () =>
            import('pages/custom-module/CustomModulesCreation.vue'),
        },
        {
          name: 'et-custommodules-overview',
          path: ':moduleName/:viewName/:id',
          props: true,
          component: () => import('pages/etisalat/summaryLayoutSwitch.vue'),
          children: [
            {
              name: 'et-custommodules-summary',
              path: 'summary',
              props: true,
              component: () =>
                import('pages/etisalat/summaryViewLayoutSwitch.vue'),
            },
          ],
        },
        {
          name: 'et-custommodules-viewmanger',
          path: ':moduleName/viewmanager',
          props: true,
          component: () =>
            import('src/newapp/viewmanager/ViewManagerLayout.vue'),
        },
        {
          name: 'et-custommodules-list',
          path: ':moduleName/:viewname?',
          props: true,
          component: () =>
            import('pages/etisalat/etisalatListLayoutSwitch.vue'),
        },
      ],
    },
    {
      name: 'supp',
      path: 'supp',
      component: () => import('pages/etisalat/UtilityProvidersLayout.vue'),
      children: [
        {
          name: 'et-vendors-viewmanger',
          path: ':moduleName/viewmanager',
          props: true,
          component: () =>
            import('src/newapp/viewmanager/ViewManagerLayout.vue'),
        },
        {
          name: 'et1-vendorForm',
          path: 'vendors/new/:id?',
          alias: ':id/edit',
          component: () => import('pages/vendor/vendors/VendorForm'),
        },
        {
          name: 'et-vendorshome',
          path: 'vendors/:viewname',
          component: () => import(`pages/vendor/vendors/VendorList`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'vendors',
          }),
          children: [
            {
              name: 'et1-vendorsummary',
              path: 'summary/:id',
              component: () =>
                import('pages/etisalat/suppliers/SupplierSummary'),
            },
          ],
          meta: {
            module: 'vendors',
          },
        },
        {
          path: 'ct/:moduleName',
          redirect: ':moduleName/all',
        },
        {
          name: 'et-contractList',
          path: 'ct/:moduleName/:viewname',
          props: true,
          component: () => import('pages/contract/list/ContractCommonList.vue'),
        },
        {
          name: 'et-warrantycontractsummary',
          path: 'ct/warrantycontracts/:viewname/summary/:id',
          component: () =>
            import(`pages/contract/summary/WarrantyContractSummary`),
        },
        {
          name: 'et1-connected-apps',
          path: 'apps/:widgetid',
          props: true,
          component: () => import('pages/connectedapps/ConnectedAppView'),
        },
        {
          name: 'et1custommodules-new',
          path: ':moduleName/create',
          component: () =>
            import('pages/custom-module/CustomModulesCreation.vue'),
        },
        {
          name: 'et1custommodules-edit',
          path: ':moduleName/edit/:id',
          component: () =>
            import('pages/custom-module/CustomModulesCreation.vue'),
        },
        {
          name: 'et1custommodules-overview',
          path: ':moduleName/:viewName/:id',
          props: true,
          component: () => import('pages/etisalat/summaryLayoutSwitch.vue'),
          children: [
            {
              name: 'et1custommodules-summary',
              path: 'summary',
              props: true,
              component: () =>
                import('pages/etisalat/summaryViewLayoutSwitch.vue'),
            },
          ],
        },
        {
          name: 'et1custommodules-viewmanger',
          path: ':moduleName/viewmanager',
          props: true,
          component: () =>
            import('src/newapp/viewmanager/ViewManagerLayout.vue'),
        },
        {
          name: 'et1custommodules-list',
          path: ':moduleName/:viewname?',
          props: true,
          component: () => import('pages/base-module-v2/ModuleList.vue'),
        },
      ],
    },
    // end
    PeopleV2Router,
    WorkOrderRouter,
    FireAlarmRouter,
    HomeRouter,
    AssetRouter,
    PurchaseRoutes,
    VendorRoutes,
    CustomModules,
    EnergyRouter,
    TenantRouter,
    PeopleRouter,
    ControlRouter,
    VisitorRouter,
    ServiceRequestRouter,
    SafetyPlanRouter,
    ClientRouter,
    FloorPlanRouter,
    ContractModules,
    EnergyAnalyticsRouter,
    CommunityRouter,
    AccountingRouter,
    BookingRouter,
    InventoryRouter,
    InspectionRouter,
    DeliveriesRouter,
    LockersRouter,
    ParkingStallRouter,
    MovesRouter,
    DesksRouter,
    InductionRouter,
    WorkPlaceRouter,
    SurveyRouter,
    PlanningRouter,
    DispatcherRouter,
  ],
}
