import { pageTypes, tabTypes, DEFAULT } from '@facilio/router'
import ConnectedApps from '../newapp/connectedApps/routes.js'
import EnergyStar from '../newapp/energyStar/routes'
import Portfolio from '../newapp/portfolio/routes.js'
const {
  APP_HOME,
  DASHBOARD_VIEWER,
  LIST,
  OVERVIEW,
  CREATE,
  EDIT,
  MODULE_CUSTOM,
  CATALOG_LIST,
  CATALOG_REQUEST,
  TIMELINE_LIST,
  REPORT_VIEW,
} = pageTypes
const { CUSTOM, INDOOR_FLOORPLAN, HOMEPAGE } = tabTypes

const portalRoutes = [
  {
    pageType: DASHBOARD_VIEWER,
    component: () =>
      import('src/PortalTenant/dashboard/DashboardLayoutWrapper.vue'),
  },
  // workorder
  {
    pageType: LIST,
    moduleName: 'workorder',
    component: () => import('PortalTenant/workorder/WorkOrderList.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'workorder',
    component: () => import('PortalTenant/workorder/SubmitRequest'),
  },
  {
    pageType: EDIT,
    moduleName: 'workorder',
    component: () => import('PortalTenant/workorder/SubmitRequest'),
  },
  // workpermit
  {
    pageType: LIST,
    moduleName: 'workpermit',
    component: () => import('PortalTenant/workpermit/WorkPermits'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'workpermit',
    component: () => import('PortalTenant/workpermit/PermitSummary'),
  },
  {
    pageType: CREATE,
    moduleName: 'workpermit',
    component: () => import('PortalTenant/workpermit/CreateWorkpermit'),
  },
  {
    pageType: EDIT,
    moduleName: 'workpermit',
    component: () => import('PortalTenant/workpermit/CreateWorkpermit'),
  },
  // vendor
  {
    pageType: LIST,
    moduleName: 'vendors',
    component: () => import('PortalTenant/vendor/VendorList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'vendors',
    component: () => import('PortalTenant/vendor/VendorSummary'),
  },
  {
    pageType: CREATE,
    moduleName: 'vendors',
    component: () => import('PortalTenant/vendor/VendorCreation'),
  },
  {
    pageType: EDIT,
    moduleName: 'vendors',
    component: () => import('PortalTenant/vendor/VendorCreation'),
  },
  // invites
  {
    pageType: LIST,
    moduleName: 'invitevisitor',
    component: () => import('PortalTenant/invite/InvitesList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'invitevisitor',
    component: () => import('PortalTenant/invite/InvitesList'),
    children: [
      {
        path: '',
        component: () => import('pages/visitors/invites/InvitesSummary.vue'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'invitevisitor',
    component: () => import('PortalTenant/invite/InvitesForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'invitevisitor',
    component: () => import('PortalTenant/invite/InvitesForm'),
  },
  //group-invites
  {
    pageType: LIST,
    moduleName: 'groupinvite',
    component: () =>
      import('PortalTenant/groupInvites/GroupInvitesPortalList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'groupinvite',
    component: () =>
      import('PortalTenant/groupInvites/GroupInvitesPortalSummary.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'groupinvite',
    component: () => import('PortalTenant/invite/GroupInvitesForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'groupinvite',
    component: () => import('PortalTenant/invite/GroupInvitesForm.vue'),
  },
  // visits
  {
    pageType: LIST,
    moduleName: 'visitorlog',
    component: () => import('PortalTenant/visits/VisitsList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'visitorlog',
    props: {
      notesModuleName: 'newvisitorlognotes',
      attachmentsModuleName: 'newvisitorlogattachments',
    },
    component: () => import('PortalTenant/visits/VisitsList.vue'),
    children: [
      {
        path: '',
        props: {
          notesModuleName: 'newvisitorlognotes',
          attachmentsModuleName: 'newvisitorlogattachments',
        },
        component: () => import('pages/visitors/visits/VisitsSummary.vue'),
      },
    ],
  },
  {
    pageType: REPORT_VIEW,
    config: {
      type: 'analytics_reports',
    },
    component: () => import('pages/report/Layout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/report/NewReportSummary.vue'),
      },
    ],
  },

  {
    pageType: REPORT_VIEW,
    config: {
      type: 'module_reports',
    },
    component: () => import('pages/report/Layout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/report/ModuleNewReportSummary.vue'),
      },
    ],
  },
  // announcements
  {
    pageType: LIST,
    moduleName: 'peopleannouncement',
    component: () => import('PortalTenant/announcement/AnnouncementList'),
    props: () => ({
      notesModuleName: 'announcementnotes',
      attachmentsModuleName: 'announcementattachments',
    }),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'peopleannouncement',
    component: () => import('PortalTenant/announcement/AnnouncementList'),
    props: () => ({
      notesModuleName: 'announcementnotes',
      attachmentsModuleName: 'announcementattachments',
    }),
    children: [
      {
        path: '',
        props: () => ({
          notesModuleName: 'announcementnotes',
          attachmentsModuleName: 'announcementattachments',
        }),
        component: () =>
          import(`PortalTenant/announcement/AnnouncementSummary`),
      },
    ],
  },
  {
    pageType: EDIT,
    moduleName: 'peopleannouncement',
    component: () =>
      import('pages/community/announcements/AnnouncementForm.vue'),
  },
  // neighbourhood
  {
    pageType: LIST,
    moduleName: 'neighbourhood',
    component: () => import('PortalTenant/neighbourhood/NeighbourhoodList'),
    props: () => ({
      notesModuleName: 'neihgbourhoodnotes',
      attachmentsModuleName: 'neighbourhoodattachments',
    }),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'neighbourhood',
    component: () => import('PortalTenant/neighbourhood/NeighbourhoodList'),
    props: () => ({
      notesModuleName: 'neihgbourhoodnotes',
      attachmentsModuleName: 'neighbourhoodattachments',
    }),
    children: [
      {
        path: '',
        props: () => ({
          notesModuleName: 'neihgbourhoodnotes',
          attachmentsModuleName: 'neighbourhoodattachments',
        }),
        component: () =>
          import(`PortalTenant/neighbourhood/NeighbourhoodSummary`),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'neighbourhood',
    component: () => import('PortalTenant/neighbourhood/NeighbourhoodForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'neighbourhood',
    component: () => import('PortalTenant/neighbourhood/NeighbourhoodForm'),
  },
  // deals
  {
    pageType: LIST,
    moduleName: 'dealsandoffers',
    component: () => import('PortalTenant/deals/DealsAndOffersList'),
    props: () => ({
      notesModuleName: 'dealsandoffersnotes',
      attachmentsModuleName: 'dealsandoffersattachments',
    }),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'dealsandoffers',
    component: () => import('PortalTenant/deals/DealsAndOffersList'),
    props: () => ({
      notesModuleName: 'dealsandoffersnotes',
      attachmentsModuleName: 'dealsandoffersattachments',
    }),
    children: [
      {
        path: '',
        props: () => ({
          notesModuleName: 'dealsandoffersnotes',
          attachmentsModuleName: 'dealsandoffersattachments',
        }),
        component: () => import(`PortalTenant/deals/DealsSummary`),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'dealsandoffers',
    component: () => import('PortalTenant/custom-module/ModuleForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'dealsandoffers',
    component: () => import('PortalTenant/custom-module/ModuleForm'),
  },
  // news and info
  {
    pageType: LIST,
    moduleName: 'newsandinformation',
    component: () => import('PortalTenant/news/NewsList'),
    props: () => ({
      notesModuleName: 'newsandinformationnotes',
      attachmentsModuleName: 'newsandinformationattachments',
    }),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'newsandinformation',
    component: () => import('PortalTenant/news/NewsSummary'),
    props: () => ({
      notesModuleName: 'newsandinformationnotes',
      attachmentsModuleName: 'newsandinformationattachments',
    }),
  },
  //  contact directory
  {
    pageType: LIST,
    moduleName: 'contactdirectory',
    component: () => import('PortalTenant/contactdirectory/ContactsList'),
    props: () => ({
      attachmentsModuleName: 'contactdirectoryattachments',
    }),
  },
  {
    pageType: CREATE,
    moduleName: 'contactdirectory',
    component: () => import(`pages/community/contactdirectory/ContactsForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'contactdirectory',
    component: () => import(`pages/community/contactdirectory/ContactsForm`),
  },
  //  admin documents
  {
    pageType: LIST,
    moduleName: 'admindocuments',
    component: () => import('PortalTenant/admindocs/AdminDocList'),
    props: () => ({
      attachmentsModuleName: 'admindocsattachments',
    }),
  },
  {
    pageType: CREATE,
    moduleName: 'admindocuments',
    component: () => import(`pages/community/admindocs/AdminDocForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'admindocuments',
    component: () => import(`pages/community/admindocs/AdminDocForm`),
  },
  // servicecatalog
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

  // servicecatalog
  {
    pageType: CATALOG_LIST,
    component: () => import('PortalTenant/catalog/CatalogList'),
  },
  {
    pageType: CATALOG_REQUEST,
    component: () => import('PortalTenant/catalog/CatalogRequest'),
  },

  {
    pageType: LIST,
    moduleName: 'workorder',
    component: () => import('PortalTenant/catalog/CatalogList'),
  },
  {
    pageType: CREATE,
    moduleName: 'workorder',
    component: () => import('PortalTenant/catalog/CatalogRequest'),
  },

  //insurance
  {
    pageType: LIST,
    moduleName: 'insurance',
    component: () => import('PortalTenant/insurance/InsuranceList'),
  },
  {
    pageType: CREATE,
    moduleName: 'insurance',
    component: () => import('PortalTenant/insurance/InsuranceForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'insurance',
    component: () => import('PortalTenant/insurance/InsuranceForm'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'insurance',
    component: () => import('PortalTenant/insurance/InsuranceSummary'),
  },
  //service request
  {
    pageType: LIST,
    moduleName: 'serviceRequest',
    component: () => import('PortalTenant/service-request/ServiceRequestList'),
  },
  {
    pageType: CREATE,
    moduleName: 'serviceRequest',
    component: () => import('PortalTenant/service-request/ServiceRequestForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'serviceRequest',
    component: () => import('PortalTenant/service-request/ServiceRequestForm'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'serviceRequest',
    component: () =>
      import('PortalTenant/service-request/ServiceRequestOverview'),
  },
  // booking module entry
  {
    pageType: OVERVIEW,
    moduleName: 'facilitybooking',
    component: () => import('PortalTenant/booking/BookingOverview'),
  },
  {
    pageType: LIST,
    moduleName: 'facilitybooking',
    component: () => import('PortalTenant/booking/BookingList'),
  },
  {
    pageType: CREATE,
    moduleName: 'facilitybooking',
    component: () => import('PortalTenant/booking/BookingForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'facilitybooking',
    component: () => import('PortalTenant/booking/BookingForm'),
  },
  //spacebooking module
  {
    pageType: OVERVIEW,
    moduleName: 'spacebooking',
    component: () => import('src/pages/spacebooking/spaceBookingSummary'),
  },
  {
    pageType: CREATE,
    moduleName: 'spacebooking',
    component: () =>
      import('src/PortalTenant/spaceBooking/SpaceBookingWithSideBar'),
  },
  {
    pageType: EDIT,
    moduleName: 'spacebooking',

    component: () =>
      import('src/PortalTenant/spaceBooking/SpaceBookingWithSideBar'),
  },
  ///
  {
    pageType: OVERVIEW,
    moduleName: 'facility',
    component: () => import('PortalTenant/facility/FacilityOverview'),
  },
  {
    pageType: LIST,
    moduleName: 'facility',
    component: () => import('PortalTenant/facility/FacilityList'),
  },
  {
    pageType: CREATE,
    moduleName: 'facility',
    component: () => import(`pages/facilitybooking/facility/FacilityForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'facility',
    component: () => import(`pages/facilitybooking/facility/FacilityForm`),
  },
  // Terms and conditions
  {
    pageType: LIST,
    moduleName: 'termsandconditions',
    component: () => import('pages/purchase/tandc/TermsAndConditionList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'termsandconditions',
    component: () => import('pages/purchase/tandc/TermsAndConditionSummary'),
  },
  //purchase request
  {
    pageType: LIST,
    moduleName: 'purchaserequest',
    component: () => import(`pages/purchase/pr/PurchaseRequestList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'purchaserequest',
    component: () => import(`pages/purchase/pr/PurchaseRequestList`),
    children: [
      {
        path: '',
        component: () => import(`pages/purchase/pr/PrSummary`),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'purchaserequest',
    component: () => import(`pages/purchase/pr/PurchaseRequestCreation`),
  },
  {
    pageType: EDIT,
    moduleName: 'purchaserequest',
    component: () => import(`pages/purchase/pr/PurchaseRequestCreation`),
  },
  {
    pageType: MODULE_CUSTOM,
    moduleName: 'purchaserequest',
    component: () => import('src/PortalTenant/DefaultLayout.vue'),
    children: [
      {
        path: 'prpdf',
        component: () => import(`pages/pdf/PrPdf.vue`),
      },
    ],
  },
  // Purchase Order
  {
    pageType: CREATE,
    moduleName: 'purchaseorder',
    component: () => import('pages/purchase/po/PurchaseOrderCreation'),
  },
  {
    pageType: EDIT,
    moduleName: 'purchaseorder',
    component: () => import('pages/purchase/po/PurchaseOrderCreation'),
  },
  {
    pageType: LIST,
    moduleName: 'purchaseorder',
    component: () => import('PortalTenant/procurement/PurchaseOrderList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'purchaseorder',
    component: () => import('pages/purchase/po/PoSummary'),
  },
  {
    pageType: MODULE_CUSTOM,
    moduleName: 'purchaseorder',
    component: () => import('src/PortalTenant/DefaultLayout.vue'),
    children: [
      {
        path: 'popdf',
        component: () => import(`pages/pdf/PoPdf.vue`),
      },
    ],
  },
  // request for quotation
  {
    pageType: LIST,
    moduleName: 'requestForQuotation',
    component: () =>
      import(`src/pages/purchase/rfq/RequestForQuotationList.vue`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'requestForQuotation',
    component: () =>
      import(`src/pages/purchase/rfq/RequestForQuotationList.vue`),
    children: [
      {
        path: '',
        component: () =>
          import(`src/pages/purchase/rfq/RequestForQuotationSummary.vue`),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'requestForQuotation',
    component: () => import(`pages/purchase/rfq/RequestForQuotationForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'requestForQuotation',
    component: () => import(`pages/purchase/rfq/RequestForQuotationForm`),
  },
  {
    pageType: MODULE_CUSTOM,
    moduleName: 'requestForQuotation',
    component: () => import('src/PortalTenant/DefaultLayout.vue'),
    children: [
      {
        path: 'rfqpdf',
        component: () => import(`pages/pdf/RfqPdf.vue`),
      },
    ],
  },
  // Vendor Quotes
  {
    pageType: CREATE,
    moduleName: 'vendorQuotes',
    component: () => import(`pages/purchase/vendorQuotes/VendorQuotesForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'vendorQuotes',
    component: () => import(`pages/purchase/vendorQuotes/VendorQuotesForm`),
  },
  {
    pageType: LIST,
    moduleName: 'vendorQuotes',
    component: () => import('PortalTenant/procurement/VendorQuotesList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'vendorQuotes',
    component: () => import('pages/purchase/vendorQuotes/VendorQuotesSummary'),
  },
  // custom modules
  {
    pageType: LIST,
    moduleName: DEFAULT,
    component: () => import('PortalTenant/custom-module/ModuleList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: DEFAULT,
    component: () => import('PortalTenant/custom-module/ModuleOverview'),
  },
  {
    pageType: CREATE,
    moduleName: DEFAULT,
    component: () => import('PortalTenant/custom-module/NewModuleForm'),
  },
  {
    pageType: EDIT,
    moduleName: DEFAULT,
    component: () => import('PortalTenant/custom-module/NewModuleForm'),
  },
  //control groups
  {
    pageType: LIST,
    moduleName: 'controlGroupv2TenantSharing',
    component: () => import('PortalTenant/control-groups/ControlGroupList'),
    props: () => ({
      moduleName: 'controlGroupv2TenantSharing',
    }),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'controlGroupv2TenantSharing',
    component: () => import('PortalTenant/control-groups/ControlGroupSummary'),
  },
  {
    pageType: LIST,
    moduleName: 'controlScheduleExceptionTenant',
    component: () => import('PortalTenant/control-groups/ScheduleChangesList'),
    props: () => ({
      moduleName: 'controlScheduleExceptionTenant',
    }),
  },
  // qanda modules

  {
    pageType: LIST,
    moduleName: 'inductionResponse',
    component: () => import('PortalTenant/qanda-modules/QandaList'),
    props: () => ({
      moduleName: 'inductionResponse',
    }),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'inductionResponse',
    component: () => import('PortalTenant/qanda-modules/QandaSummary'),
    props: () => ({
      moduleName: 'inductionResponse',
    }),
  },
  {
    pageType: LIST,
    moduleName: 'inspectionResponse',
    component: () => import('PortalTenant/qanda-modules/QandaList'),
    props: () => ({
      moduleName: 'inspectionResponse',
    }),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'inspectionResponse',
    component: () => import('PortalTenant/qanda-modules/QandaSummary'),
    props: () => ({
      moduleName: 'inspectionResponse',
    }),
  },
  {
    pageType: EDIT,
    moduleName: 'inductionResponse',
    component: () =>
      import('pages/induction/individual-induction/InductionLiveForm'),
    props: () => ({
      moduleName: 'inductionResponse',
    }),
  },
  {
    pageType: EDIT,
    moduleName: 'inspectionResponse',
    component: () =>
      import('pages/inspection/individual-inspection/InspectionLiveForm'),
    props: () => ({
      moduleName: 'inspectionResponse',
    }),
  },
  // FAQ for altayer
  {
    tabType: CUSTOM,
    config: {
      type: 'faqlist',
    },
    component: () => import('src/webViews/pages/TenantFaq'),
  },
  //profile
  {
    name: 'portalProfile',
    path: 'profile',
    component: () => import('PortalTenant/profile/MyProfile'),
  },
  {
    name: 'portalPdf',
    path: 'qandapdf/:moduleName',
    component: () => import('PortalTenant/qanda-modules/QandaPdf.vue'),
  },
  //floormap
  // {
  //   tabType: CUSTOM,
  //   config: {
  //     type: 'floormap',
  //   },
  //   component: () => import('pages/indoorFloorPlan/Layout.vue'),
  //   children: [
  //     {
  //       path: ':floorplanid',
  //       name: 'floorplanId',
  //       component: () => import('pages/indoorFloorPlan/Layout.vue'),
  //     },
  //   ],
  // },

  {
    tabType: INDOOR_FLOORPLAN,

    component: () => import('pages/indoorFloorPlan/Layout.vue'),
  },

  // quotation
  {
    pageType: CREATE,
    moduleName: 'quote',
    component: () => import(`pages/quotation/v1/QuotationForm`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'quote',
    component: () => import('pages/quotation/v1/QuotationSummary.vue'),
  },
  {
    pageType: MODULE_CUSTOM,
    moduleName: 'quote',
    component: () => import('./DefaultLayout.vue'),
    children: [
      {
        path: ':viewname/:id/sendmail',
        component: () => import(`pages/quotation/v1/QuotationSendMailForm`),
      },
    ],
  },
  // approvals
  {
    pageType: pageTypes.APPROVAL_ACTIVITY,
    component: () =>
      import(`PortalTenant/approvals/PortalApprovalActivities.vue`),
  },
  {
    pageType: pageTypes.APPROVAL_LIST,
    component: () => import(`PortalTenant/approvals/PortalApprovalList.vue`),
  },
  //FloorPlan
  {
    tabType: INDOOR_FLOORPLAN,
    component: () => import('pages/indoorFloorPlan/Layout.vue'),
  },
  // homepage
  {
    tabType: HOMEPAGE,

    component: () =>
      import('PortalTenant/employeePortalOverview/EmployeePortalHomePage.vue'),
  },
  // TimeLine tab
  {
    pageType: TIMELINE_LIST,
    moduleName: DEFAULT,
    component: () => import('./timeline-view/TimeLineView.vue'),
  },

  ...ConnectedApps,
  ...EnergyStar,
  ...Portfolio,
]
const employeePortRoutes = [
  {
    pageType: DASHBOARD_VIEWER,
    component: () =>
      import('src/PortalTenant/dashboard/DashboardLayoutWrapper.vue'),
  },
  {
    tabType: HOMEPAGE,

    component: () =>
      import('PortalTenant/employeePortalOverview/EmployeePortalHomePage.vue'),
  },
  {
    tabType: INDOOR_FLOORPLAN,

    component: () => import('src/pages/indoorFloorPlan/ListLayout.vue'),
  },
  {
    pageType: CATALOG_LIST,
    component: () =>
      import('src/PortalTenant/catalog/EmployeePortalCatalogList'),
  },
  {
    pageType: CATALOG_REQUEST,
    component: () =>
      import('PortalTenant/catalog/EmployeePortalCatalogRequest'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'facilitybooking',
    component: () => import('PortalTenant/booking/BookingOverview'),
  },
  {
    pageType: LIST,
    moduleName: 'facilitybooking',
    component: () =>
      import('PortalTenant/employeePortalOverview/EmployeeBookingList'),
  },
  {
    pageType: CREATE,
    moduleName: 'facilitybooking',
    component: () => import('PortalTenant/booking/BookingForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'facilitybooking',
    component: () => import('PortalTenant/booking/BookingForm'),
  },

  {
    pageType: LIST,
    moduleName: 'serviceRequest',
    component: () =>
      import('PortalTenant/employeePortalOverview/EmployeeServiceRequestList'),
  },
  {
    pageType: CREATE,
    moduleName: 'serviceRequest',
    component: () => import('PortalTenant/service-request/ServiceRequestForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'serviceRequest',
    component: () => import('PortalTenant/service-request/ServiceRequestForm'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'serviceRequest',
    component: () =>
      import(
        'PortalTenant/service-request/EmployeeportalServiceRequestOverview'
      ),
  },

  {
    pageType: LIST,
    moduleName: DEFAULT,
    component: () =>
      import('PortalTenant/employeePortalOverview/EmployeeModuleList'),
  },

  // invites
  {
    pageType: LIST,
    moduleName: 'invitevisitor',
    component: () =>
      import(
        'PortalTenant/employeePortalOverview/EmployeePortalInvitesList.vue'
      ),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'invitevisitor',
    component: () => import('PortalTenant/invite/InvitesList'),
    children: [
      {
        path: '',
        component: () => import('pages/visitors/invites/InvitesSummary.vue'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'invitevisitor',
    component: () => import('PortalTenant/invite/InvitesForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'invitevisitor',
    component: () => import('PortalTenant/invite/InvitesForm'),
  },
  //spacebooking module
  {
    pageType: OVERVIEW,
    moduleName: 'spacebooking',
    component: () => import('src/pages/spacebooking/spaceBookingSummary'),
  },
  {
    pageType: CREATE,
    moduleName: 'spacebooking',
    component: () =>
      import('src/PortalTenant/spaceBooking/SpaceBookingWithSideBar'),
  },
  {
    pageType: EDIT,
    moduleName: 'spacebooking',

    component: () =>
      import('src/PortalTenant/spaceBooking/SpaceBookingWithSideBar'),
  },
  ///
  //group-invites
  {
    pageType: LIST,
    moduleName: 'groupinvite',
    component: () =>
      import('PortalTenant/groupInvites/EmployeeGroupInviteList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'groupinvite',
    component: () =>
      import('PortalTenant/groupInvites/GroupInvitesPortalSummary.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'groupinvite',
    component: () => import('PortalTenant/invite/GroupInvitesForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'groupinvite',
    component: () => import('PortalTenant/invite/GroupInvitesForm.vue'),
  },
  // visits
  {
    pageType: LIST,
    moduleName: 'visitorlog',
    component: () =>
      import('PortalTenant/employeePortalOverview/EmployeeModuleList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'visitorlog',
    props: {
      notesModuleName: 'newvisitorlognotes',
      attachmentsModuleName: 'newvisitorlogattachments',
    },
    component: () => import('PortalTenant/visits/VisitsList.vue'),
    children: [
      {
        path: '',
        props: {
          notesModuleName: 'newvisitorlognotes',
          attachmentsModuleName: 'newvisitorlogattachments',
        },
        component: () => import('pages/visitors/visits/VisitsSummary.vue'),
      },
    ],
  },

  //  admin documents
  {
    pageType: LIST,
    moduleName: 'admindocuments',
    component: () => import('PortalTenant/admindocs/AdminDocList'),
    props: () => ({
      attachmentsModuleName: 'admindocsattachments',
    }),
  },
  {
    pageType: CREATE,
    moduleName: 'admindocuments',
    component: () => import(`pages/community/admindocs/AdminDocForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'admindocuments',
    component: () => import(`pages/community/admindocs/AdminDocForm`),
  },
  //deliveries modules
  {
    pageType: OVERVIEW,
    moduleName: 'deliveries',
    component: () =>
      import('PortalTenant/employeePortalOverview/EmployeeOverviewDeliveries'),
  },
  {
    pageType: OVERVIEW,
    moduleName: DEFAULT,
    component: () => import('PortalTenant/custom-module/ModuleOverview'),
  },
  {
    pageType: CREATE,
    moduleName: DEFAULT,
    component: () => import('PortalTenant/custom-module/NewModuleForm'),
  },
  {
    pageType: EDIT,
    moduleName: DEFAULT,
    component: () => import('PortalTenant/custom-module/NewModuleForm'),
  },

  {
    pageType: LIST,
    moduleName: 'workorder',
    component: () =>
      import('PortalTenant/employeePortalOverview/EmployeeWorkOrderList'),
  },
  {
    pageType: CREATE,
    moduleName: 'workorder',
    component: () => import('PortalTenant/workorder/SubmitRequest'),
  },
  {
    pageType: EDIT,
    moduleName: 'workorder',
    component: () => import('PortalTenant/workorder/SubmitRequest'),
  },
  {
    name: 'portalProfile',
    path: 'profile',
    component: () =>
      import(
        'src/PortalTenant/employeePortalOverview/EmployeePortalMyProfile.vue'
      ),
    children: [
      {
        name: 'myProfile',
        path: 'myprofile',
        component: () =>
          import('src/PortalTenant/employeePortalOverview/EPEditProfile.vue'),
      },
      {
        name: 'changePassword',
        path: 'changepassword',
        component: () =>
          import(
            'src/PortalTenant/employeePortalOverview/EPChangePassword.vue'
          ),
      },
      {
        name: 'notifications',
        path: 'notifications',
        component: () =>
          import(
            'src/PortalTenant/employeePortalOverview/EmployePortalNotificationList.vue'
          ),
      },
    ],
  },
]
export default [
  {
    pageType: APP_HOME,
    appName: 'tenant',
    layoutType: 3,
    component: () => import('PortalTenant/Home.vue'),
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        pageType: OVERVIEW,
        moduleName: 'workorder',
        component: () => import('PortalTenant/workorder/WorkRequestSummary'),
      },
      {
        tabType: CUSTOM,
        config: {
          type: 'portalOverview',
        },
        component: () => import('PortalTenant/overview/Overview'),
      },
      ...portalRoutes,
    ],
  },
  {
    pageType: APP_HOME,
    appName: 'vendor',
    layoutType: 3,
    component: () => import('PortalTenant/Home.vue'),
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        pageType: OVERVIEW,
        moduleName: 'workorder',
        component: () =>
          import('pages/workorder/workorders/v1/WorkorderSummary'),
      },
      {
        tabType: CUSTOM,
        config: {
          type: 'portalOverview',
        },
        component: () => import('PortalTenant/overview/Overview'),
      },
      ...portalRoutes,
    ],
  },
  {
    pageType: APP_HOME,
    appName: 'service',
    layoutType: 1,
    component: () => import('PortalTenant/Home'),
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        pageType: OVERVIEW,
        moduleName: 'workorder',
        component: () => import('PortalTenant/workorder/WorkRequestSummary'),
      },
      {
        tabType: CUSTOM,
        config: {
          type: 'portalOverview',
        },
        component: () =>
          import('src/PortalTenant/servicePortalOverview/serviceHome'),
      },
      ...portalRoutes,
    ],
  },
  {
    pageType: APP_HOME,
    appName: 'client',
    layoutType: 1,
    component: () => import('PortalTenant/Home'),
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        pageType: OVERVIEW,
        moduleName: 'workorder',
        component: () =>
          import('src/PortalTenant/workorder/WorkRequestSummary'),
      },
      {
        tabType: CUSTOM,
        config: {
          type: 'portalOverview',
        },
        component: () => import('src/PortalTenant/overview/Overview'),
      },
      ...portalRoutes,
      {
        pageType: LIST,
        moduleName: 'quote',
        component: () =>
          import('PortalTenant/custom-module/ClientPortalQuotationList'),
      },
    ],
  },
  {
    pageType: APP_HOME,
    appName: 'employee',
    layoutType: 1,
    component: () =>
      import('PortalTenant/employeePortalOverview/EmployeePortalHome.vue'),
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        pageType: OVERVIEW,
        moduleName: 'workorder',
        component: () => import('PortalTenant/workorder/WorkRequestSummary'),
      },
      {
        tabType: CUSTOM,
        config: {
          type: 'portalOverview',
        },
        component: () =>
          import('src/PortalTenant/employeePortalOverview/employeeHome'),
      },
      ...employeePortRoutes,
    ],
  },
]
