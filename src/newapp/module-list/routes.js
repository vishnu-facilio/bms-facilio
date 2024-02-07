import { pageTypes } from '@facilio/router'

const { LIST, OVERVIEW, CREATE, EDIT, MODULE_CUSTOM } = pageTypes

export default [
  //asset
  {
    pageType: LIST,
    moduleName: 'asset',
    component: () => import('pages/assets/asset/v1/AssetList.vue'),
  },
  // {
  //   pageType: OVERVIEW,
  //   moduleName: 'asset',
  //   component: () => import('pages/assets/asset/v1/AssetOverviewLayout.vue'),
  //   children: [
  //     {
  //       path: '',
  //       component: () => import('pages/assets/asset/v1/Overview.vue'),
  //     },
  //   ],
  // },
  //vendor
  {
    pageType: LIST,
    moduleName: 'vendors',
    component: () => import('pages/vendor/vendors/VendorList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'vendors',
    component: () => import('pages/vendor/vendors/VendorList.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/vendor/vendors/VendorSummary.vue'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'vendors',
    component: () => import('pages/vendor/vendors/VendorForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'vendors',
    component: () => import('pages/vendor/vendors/VendorForm'),
  },
  //vendor contacts
  {
    pageType: LIST,
    moduleName: 'vendorcontact',
    component: () => import('pages/vendor/vendorcontacts/VendorContacts.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'vendorcontact',
    component: () => import('pages/vendor/vendorcontacts/VendorContacts.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/vendor/vendorcontacts/VendorContactOverviewPage.vue'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'vendorcontact',
    component: () =>
      import('pages/vendor/vendorcontacts/VendorContactForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'vendorcontact',
    component: () =>
      import('pages/vendor/vendorcontacts/VendorContactForm.vue'),
  },
  //custom_alert
  {
    pageType: OVERVIEW,
    moduleName: 'custom_alert',
    component: () =>
      import('pages/etisalat/BillAlerts/BillAlertSummaryListSummary.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/etisalat/BillAlerts/BillAlertSummary.vue'),
      },
    ],
  },
  //custom_invoices
  {
    pageType: OVERVIEW,
    moduleName: 'custom_invoices',
    component: () =>
      import('pages/etisalat/Invoice/paymentmemoSummaryListView.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/etisalat/Invoice/PaymentSummary.vue'),
      },
    ],
  },
  //custom_tariffinfo
  {
    pageType: OVERVIEW,
    moduleName: 'custom_tariffinfo',
    component: () => import('pages/etisalat/Tariff/TariffListSummary.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/etisalat/Tariff/TariffSummary.vue'),
      },
    ],
  },
  //custom_utilityaccounts_1
  {
    pageType: OVERVIEW,
    moduleName: 'custom_utilityaccounts_1',
    component: () => import('pages/etisalat/utility/UtilitySummaryList.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/etisalat/utility/UtilitySummary.vue'),
      },
    ],
  },
  //custom_utilitybills
  {
    pageType: LIST,
    moduleName: 'custom_utilitybills',
    component: () => import('pages/etisalat/UtilityBills/UtilityBillsList.vue'),
  },

  {
    pageType: OVERVIEW,
    moduleName: 'custom_utilitybills',
    component: () => import('pages/etisalat/UtilityBills/UtilityBillsList.vue'),
  },
  //work permit
  {
    pageType: LIST,
    moduleName: 'workpermit',
    component: () => import(`pages/workorder/workpermit/v3/V3WorkPermitList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'workpermit',
    component: () =>
      import(`pages/workorder/workpermit/v3/WorkPermitSummaryListV3`),
    children: [
      {
        path: '',
        component: () =>
          import(`pages/workorder/workpermit/v3/WorkPermitSummaryV3`),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'workpermit',
    component: () => import(`pages/workorder/workpermit/v3/WorkPermitFormV3`),
  },
  {
    pageType: EDIT,
    moduleName: 'workpermit',
    component: () => import(`pages/workorder/workpermit/v3/WorkPermitFormV3`),
  },
  //workorder
  {
    pageType: LIST,
    moduleName: 'workorder',
    component: () => import('pages/workorder/workorders/v3/WorkOrderList.vue'),
  },
  // {
  //   pageType: OVERVIEW,
  //   moduleName: 'workorder',
  //   component: () =>
  //     import('pages/workorder/workorders/v3/WorkorderOverview.vue'),
  // },
  {
    pageType: CREATE,
    moduleName: 'workorder',
    component: () => import('pages/workorder/workorders/v3/WoV3Form.vue'),
  },
  //JobPlan
  {
    pageType: LIST,
    moduleName: 'jobplan',
    component: () => import('pages/workorder/jobplan/JobPlanList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'jobplan',
    component: () => import('pages/workorder/jobplan/JobPlanSummary.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'jobplan',
    component: () => import('pages/workorder/jobplan/NewJobPlan.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'jobplan',
    component: () => import('pages/workorder/jobplan/NewJobPlan.vue'),
  },
  //Planned Maintenance
  {
    pageType: LIST,
    moduleName: 'plannedmaintenance',
    component: () => import('pages/workorder/pm/PMList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'plannedmaintenance',
    component: () => import('pages/workorder/pm/summary/PMSummary.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'plannedmaintenance',
    component: () => import('pages/workorder/pm/create/PMCreation.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'plannedmaintenance',
    component: () => import('pages/workorder/pm/create/PMCreation.vue'),
  },
  //preventivemaintenance
  {
    pageType: LIST,
    moduleName: 'preventivemaintenance',
    component: () => import('pages/workorder/preventive/v1/NewPmList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'preventivemaintenance',
    component: () => import('pages/workorder/pmV1/PmV1Summary.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'preventivemaintenance',
    component: () => import('pages/workorder/pmV1/PmV1Form.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'preventivemaintenance',
    component: () => import('pages/workorder/pmV1/PmV1Form.vue'),
  },
  //tenant
  {
    pageType: LIST,
    moduleName: 'tenant',
    component: () => import('pages/tenants/TenantList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'tenant',
    component: () => import('pages/tenants/TenantOverviewList.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'tenant',
    component: () => import('pages/tenants/TenantNew.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'tenant',
    component: () => import('pages/tenants/TenantNew.vue'),
  },
  //tenantcontact
  {
    pageType: OVERVIEW,
    moduleName: 'tenantcontact',
    component: () => import(`pages/tenantcontact/TenantContactList.vue`),
    children: [
      {
        path: '',
        component: () =>
          import(`pages/tenantcontact/TenantContactOverviewPage.vue`),
      },
    ],
  },
  {
    pageType: LIST,
    moduleName: 'tenantcontact',
    component: () => import('pages/tenantcontact/TenantContactList.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'tenantcontact',
    component: () => import('pages/tenantcontact/TenantContactNew.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'tenantcontact',
    component: () => import('pages/tenantcontact/TenantContactNew.vue'),
  },
  //tenantunit
  {
    pageType: LIST,
    moduleName: 'tenantunit',
    component: () => import('pages/tenantunit/TenantUnitList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'tenantunit',
    component: () => import('pages/tenantunit/TenantUnitList.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/tenantunit/TenantUnitOverviewPage.vue'),
      },
    ],
  },
  // quotation
  {
    pageType: CREATE,
    moduleName: 'quote',
    component: () => import(`pages/quotation/v1/QuotationForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'quote',
    component: () => import(`pages/quotation/v1/QuotationForm`),
  },
  {
    pageType: LIST,
    moduleName: 'quote',
    component: () => import(`pages/quotation/v1/QuotationList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'quote',
    component: () => import('pages/quotation/v1/QuotationSummaryList.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/quotation/v1/QuotationSummary.vue'),
      },
      {
        path: 'sendmail',
        component: () => import(`pages/quotation/v1/QuotationSendMailForm`),
      },
    ],
  },
  //announcement
  {
    pageType: LIST,
    moduleName: 'announcement',
    props: {
      attachmentsModuleName: 'announcementattachments',
    },
    component: () =>
      import('pages/community/announcements/AnnouncementList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'announcement',
    props: {
      notesModuleName: 'announcementnotes',
      attachmentsModuleName: 'announcementattachments',
    },
    component: () =>
      import('pages/community/announcements/AnnouncementList.vue'),
    children: [
      {
        props: {
          attachmentsModuleName: 'announcementattachments',
        },
        path: '',
        component: () =>
          import('pages/community/announcements/AnnouncementSummary'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'announcement',
    component: () =>
      import('pages/community/announcements/AnnouncementForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'announcement',
    component: () =>
      import('pages/community/announcements/AnnouncementForm.vue'),
  },
  //audience
  {
    pageType: LIST,
    moduleName: 'audience',
    component: () => import('pages/community/audience/AudienceList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'audience',
    component: () => import('pages/community/audience/AudienceList.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/community/audience/AudienceSummary'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'audience',
    component: () => import('pages/community/audience/AudienceForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'audience',
    component: () => import('pages/community/audience/AudienceForm.vue'),
  },
  //neighbourhood
  {
    pageType: LIST,
    moduleName: 'neighbourhood',
    props: { attachmentsModuleName: 'neighbourhoodattachments' },
    component: () =>
      import('pages/community/neighbourhood/NeighbourhoodList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'neighbourhood',
    props: {
      notesModuleName: 'neighbourhoodnotes',
      attachmentsModuleName: 'neighbourhoodattachments',
    },
    component: () =>
      import('pages/community/neighbourhood/NeighbourhoodList.vue'),
    children: [
      {
        props: {
          attachmentsModuleName: 'neighbourhoodattachments',
        },
        path: '',
        component: () =>
          import('pages/community/neighbourhood/NeighbourhoodSummary'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'neighbourhood',
    component: () =>
      import('pages/community/neighbourhood/NeighbourhoodForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'neighbourhood',
    component: () =>
      import('pages/community/neighbourhood/NeighbourhoodForm.vue'),
  },
  //dealsandoffers
  {
    pageType: LIST,
    moduleName: 'dealsandoffers',
    props: { attachmentsModuleName: 'dealsandoffersattachments' },
    component: () => import('pages/community/deals/DealsAndOffersList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'dealsandoffers',
    props: {
      notesModuleName: 'dealsandoffersnotes',
      attachmentsModuleName: 'dealsandoffersattachments',
    },
    component: () => import('pages/community/deals/DealsAndOffersList.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/community/deals/DealsSummary'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'dealsandoffers',
    component: () => import('pages/community/deals/DealsAndOffersForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'dealsandoffers',
    component: () => import('pages/community/deals/DealsAndOffersForm.vue'),
  },
  //newsandinformation
  {
    pageType: LIST,
    moduleName: 'newsandinformation',
    props: { attachmentsModuleName: 'newsandinformationattachments' },
    component: () => import('pages/community/news/NewsList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'newsandinformation',
    props: {
      notesModuleName: 'newsandinformationnotes',
      attachmentsModuleName: 'newsandinformationattachments',
    },
    component: () => import('pages/community/news/NewsList.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/community/news/NewsSummary'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'newsandinformation',
    component: () => import('pages/community/news/NewsForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'newsandinformation',
    component: () => import('pages/community/news/NewsForm.vue'),
  },
  //contactdirectory
  {
    pageType: LIST,
    moduleName: 'contactdirectory',
    props: { attachmentsModuleName: 'contactdirectoryattachments' },
    component: () =>
      import('pages/community/contactdirectory/ContactsList.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'contactdirectory',
    component: () =>
      import('pages/community/contactdirectory/ContactsForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'contactdirectory',
    component: () =>
      import('pages/community/contactdirectory/ContactsForm.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'contactdirectory',
    props: {
      attachmentsModuleName: 'contactdirectoryattachments',
      notesModuleName: 'contactdirectorynotes',
    },
    component: () => import(`pages/community/contactdirectory/ContactsList`),
    children: [
      {
        path: '',
        component: () =>
          import(`pages/community/contactdirectory/ContactDirectorySummary`),
      },
    ],
  },
  //admindocuments
  {
    pageType: LIST,
    moduleName: 'admindocuments',
    props: { attachmentsModuleName: 'admindocumentsattachments' },
    component: () => import('pages/community/admindocs/AdminDocList.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'admindocuments',
    component: () => import('pages/community/admindocs/AdminDocForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'admindocuments',
    component: () => import('pages/community/admindocs/AdminDocForm.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'admindocuments',
    props: {
      attachmentsModuleName: 'admindocumentsattachments',
      notesModuleName: 'admindocumentnotes',
    },
    component: () => import(`pages/community/admindocs/AdminDocList`),
    children: [
      {
        path: '',
        component: () => import(`pages/community/admindocs/AdminDocSummary`),
      },
    ],
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
  //purchase order
  {
    pageType: LIST,
    moduleName: 'purchaseorder',
    component: () => import(`pages/purchase/po/PurchaseOrderList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'purchaseorder',
    component: () => import(`pages/purchase/po/PurchaseOrderList`),
    children: [
      {
        path: '',
        component: () => import(`pages/purchase/po/PoSummary`),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'purchaseorder',
    component: () => import(`pages/purchase/po/PurchaseOrderCreation`),
  },
  {
    pageType: EDIT,
    moduleName: 'purchaseorder',
    component: () => import(`pages/purchase/po/PurchaseOrderCreation`),
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
  // Receivables
  {
    pageType: LIST,
    moduleName: 'receivable',
    component: () => import(`pages/purchase/rv/ReceivablesList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'receivable',
    component: () => import(`pages/purchase/rv/ReceivablesList`),
    children: [
      {
        path: '',
        component: () => import(`pages/purchase/rv/RvSummary`),
      },
    ],
  },
  // Terms And Condition
  {
    pageType: LIST,
    moduleName: 'termsandconditions',
    component: () => import(`pages/purchase/tandc/TermsAndConditionList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'termsandconditions',
    component: () => import(`pages/purchase/tandc/TermsAndConditionList`),
    children: [
      {
        path: '',
        component: () =>
          import('pages/purchase/tandc/TermsAndConditionSummary'),
      },
    ],
  },
  //visits
  {
    pageType: LIST,
    moduleName: 'visitorlog',
    component: () => import('pages/visitors/visits/VisitsList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'visitorlog',
    props: {
      notesModuleName: 'newvisitorlognotes',
      attachmentsModuleName: 'newvisitorlogattachments',
    },
    component: () => import('pages/visitors/visits/VisitsList.vue'),
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
  //invites
  {
    pageType: LIST,
    moduleName: 'invitevisitor',
    component: () => import('pages/visitors/invites/InvitesList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'invitevisitor',
    component: () => import('pages/visitors/invites/InvitesList.vue'),
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
    component: () => import('pages/visitors/invites/InviteForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'invitevisitor',
    component: () => import('pages/visitors/invites/InviteForm.vue'),
  },
  //group invites
  {
    pageType: LIST,
    moduleName: 'groupinvite',
    component: () => import('pages/visitors/groupinvites/GroupInvitesList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'groupinvite',
    component: () =>
      import('pages/visitors/groupinvites/GroupInvitesOverviewList.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'groupinvite',
    component: () => import('pages/visitors/invites/InviteFormRedirect.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'groupinvite',
    component: () => import('pages/visitors/invites/InviteFormRedirect.vue'),
  },
  //visitor
  {
    pageType: LIST,
    moduleName: 'visitor',
    component: () => import('pages/visitors/visitor/VisitorList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'visitor',
    component: () => import('pages/visitors/visitor/VisitorOverviewList'),
  },
  //watchlist
  {
    pageType: LIST,
    moduleName: 'watchlist',
    component: () => import('pages/visitors/watchlist/WatchListRecords.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'watchlist',
    component: () => import('pages/visitors/watchlist/WatchListRecords.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/visitors/watchlist/WatchListSummary.vue'),
      },
    ],
  },
  // Budget Module routes
  {
    pageType: CREATE,
    moduleName: 'budget',
    component: () => import(`pages/accounting/budget/BudgetForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'budget',
    component: () => import(`pages/accounting/budget/BudgetForm`),
  },
  {
    pageType: LIST,
    moduleName: 'budget',
    component: () => import(`pages/accounting/budget/BudgetList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'budget',
    component: () => import(`pages/accounting/budget/BudgetSummary`),
  },
  {
    pageType: LIST,
    moduleName: 'chartofaccount',
    component: () =>
      import(`pages/accounting/chartofaccounts/ChartOfAccountsList`),
  },
  {
    pageType: LIST,
    moduleName: 'accounttype',
    component: () => import(`pages/accounting/accounttype/AccountTypeList`),
  },
  // Facility Booking routes
  {
    pageType: CREATE,
    moduleName: 'facilitybooking',
    component: () => import(`pages/facilitybooking/booking/BookingForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'facilitybooking',
    component: () => import(`pages/facilitybooking/booking/BookingForm`),
  },
  {
    pageType: LIST,
    moduleName: 'facilitybooking',
    component: () => import(`pages/facilitybooking/booking/BookingList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'facilitybooking',
    component: () =>
      import('pages/facilitybooking/booking/BookingSummaryList.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/facilitybooking/booking/BookingSummary.vue'),
      },
    ],
  },
  // Facility Routes
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
  {
    pageType: LIST,
    moduleName: 'facility',
    component: () => import(`pages/facilitybooking/facility/FacilityList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'facility',
    component: () =>
      import('pages/facilitybooking/facility/FacilitySummaryList.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/facilitybooking/facility/FacilitySummary.vue'),
      },
    ],
  },
  {
    pageType: LIST,
    moduleName: 'amenity',
    component: () => import(`pages/facilitybooking/amenity/AmenityList`),
  },
  // Service Request
  {
    pageType: CREATE,
    moduleName: 'serviceRequest',
    component: () => import('pages/servicerequest/ServiceRequestForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'serviceRequest',
    component: () => import('pages/servicerequest/ServiceRequestForm'),
  },
  {
    pageType: LIST,
    moduleName: 'serviceRequest',
    component: () => import('pages/servicerequest/ServiceRequestList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'serviceRequest',
    component: () => import('pages/servicerequest/ServiceRequestSummary'),
  },
  {
    pageType: LIST,
    moduleName: 'purchasecontracts',
    component: () => import('pages/contract/list/ContractCommonList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'purchasecontracts',
    component: () => import('pages/contract/summary/PurchaseContractSummary'),
  },
  {
    pageType: CREATE,
    moduleName: 'purchasecontracts',
    component: () => import('pages/contract/form/ContractForms'),
  },
  {
    pageType: EDIT,
    moduleName: 'purchasecontracts',
    component: () => import('pages/contract/form/ContractForms'),
  },
  // Labour Contracts
  {
    pageType: LIST,
    moduleName: 'labourcontracts',
    component: () => import('pages/contract/list/ContractCommonList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'labourcontracts',
    component: () => import('pages/contract/summary/LabourContractSummary'),
  },
  {
    pageType: CREATE,
    moduleName: 'labourcontracts',
    component: () => import('pages/contract/form/ContractForms'),
  },
  {
    pageType: EDIT,
    moduleName: 'labourcontracts',
    component: () => import('pages/contract/form/ContractForms'),
  },
  // Rental lease contracts
  {
    pageType: LIST,
    moduleName: 'rentalleasecontracts',
    component: () => import('pages/contract/list/ContractCommonList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'rentalleasecontracts',
    component: () =>
      import('pages/contract/summary/RentalLeaseContractSummary'),
  },
  {
    pageType: CREATE,
    moduleName: 'rentalleasecontracts',
    component: () => import('pages/contract/form/ContractForms'),
  },
  {
    pageType: EDIT,
    moduleName: 'rentalleasecontracts',
    component: () => import('pages/contract/form/ContractForms'),
  },
  // Warranty Contracts
  {
    pageType: LIST,
    moduleName: 'warrantycontracts',
    component: () => import('pages/contract/list/ContractCommonList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'warrantycontracts',
    component: () => import('pages/contract/summary/WarrantyContractSummary'),
  },
  {
    pageType: CREATE,
    moduleName: 'warrantycontracts',
    component: () => import('pages/contract/form/ContractForms'),
  },
  {
    pageType: EDIT,
    moduleName: 'warrantycontracts',
    component: () => import('pages/contract/form/ContractForms'),
  },

  // Items
  {
    pageType: LIST,
    moduleName: 'item',
    component: () => import(`pages/Inventory/Items/ItemList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'item',
    component: () => import(`pages/Inventory/Items/ItemList`),
    children: [
      {
        path: '',
        component: () => import(`pages/Inventory/Items/ItemSummary.vue`),
      },
    ],
  },
  // Item Types
  {
    pageType: LIST,
    moduleName: 'itemTypes',
    component: () => import(`pages/Inventory/ItemTypes/ItemTypeList.vue`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'itemTypes',
    component: () => import(`pages/Inventory/ItemTypes/ItemTypeList`),
    children: [
      {
        path: '',
        component: () => import(`pages/Inventory/ItemTypes/ItemTypeSummary`),
      },
    ],
  },
  // Gate Pass
  {
    pageType: LIST,
    moduleName: 'gatePass',
    component: () => import(`pages/Inventory/Gatepass/GatePassList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'gatePass',
    component: () => import(`pages/Inventory/Gatepass/GatePassList`),
    children: [
      {
        path: '',
        component: () => import(`pages/Inventory/Gatepass/GatepassSummary`),
      },
    ],
  },
  // Service
  {
    pageType: LIST,
    moduleName: 'service',
    component: () => import(`pages/Inventory/Service/ServiceList`),
  },
  {
    pageType: CREATE,
    moduleName: 'service',
    component: () => import('pages/Inventory/Service/NewServiceForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'service',
    component: () => import('pages/Inventory/Service/NewServiceForm'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'service',
    component: () => import(`pages/Inventory/Service/ServiceList`),
    children: [
      {
        path: '',
        component: () => import(`pages/Inventory/Service/ServiceSummary.vue`),
      },
    ],
  },
  //Transfer Request
  {
    pageType: LIST,
    moduleName: 'transferrequest',
    component: () =>
      import(`pages/Inventory/TransferRequest/TransferRequestList`),
  },
  {
    pageType: CREATE,
    moduleName: 'transferrequest',
    component: () =>
      import('pages/Inventory/TransferRequest/NewTransferRequestForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'transferrequest',
    component: () =>
      import('pages/Inventory/TransferRequest/NewTransferRequestForm'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'transferrequest',
    component: () =>
      import(`pages/Inventory/TransferRequest/TransferRequestList`),
    children: [
      {
        path: '',
        component: () =>
          import(`pages/Inventory/TransferRequest/TransferRequestSummary`),
      },
    ],
  },
  // Transfer Request Shipment
  {
    pageType: LIST,
    moduleName: 'transferrequestshipment',
    component: () =>
      import(`pages/Inventory/TransferRequest/Shipment/TrShipmentList`),
  },
  {
    pageType: EDIT,
    moduleName: 'transferrequestshipment',
    component: () =>
      import('pages/Inventory/TransferRequest/Shipment/TrShipmentForm'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'transferrequestshipment',
    component: () =>
      import(`pages/Inventory/TransferRequest/Shipment/TrShipmentList`),
    children: [
      {
        path: '',
        component: () =>
          import(`pages/Inventory/TransferRequest/Shipment/TrShipmentSummary`),
      },
    ],
  },
  // StoreRoom
  {
    pageType: LIST,
    moduleName: 'storeRoom',
    component: () => import(`pages/Inventory/Storerooms/StoreroomList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'storeRoom',
    component: () => import(`pages/Inventory/Storerooms/StoreroomList`),
    children: [
      {
        path: '',
        component: () =>
          import(`pages/Inventory/Storerooms/StoreroomSummary.vue`),
      },
    ],
  },
  // Tool
  {
    pageType: LIST,
    moduleName: 'tool',
    component: () => import(`pages/Inventory/Tools/ToolList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'tool',
    component: () => import(`pages/Inventory/Tools/ToolList`),
    children: [
      {
        path: '',
        component: () => import(`pages/Inventory/Tools/ToolSummary`),
      },
    ],
  },
  // Tool type
  {
    pageType: LIST,
    moduleName: 'toolTypes',
    component: () => import(`pages/Inventory/Tooltypes/ToolTypesList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'toolTypes',
    component: () => import(`pages/Inventory/Tooltypes/ToolTypesList`),
    children: [
      {
        path: '',
        component: () => import(`pages/Inventory/Tooltypes/TooltypesSummary`),
      },
    ],
  },
  //inventory request
  // create
  {
    pageType: CREATE,
    moduleName: 'inventoryrequest',
    component: () =>
      import(`pages/Inventory/InventoryRequest/InventoryRequestForm`),
  },
  // edit
  {
    pageType: EDIT,
    moduleName: 'inventoryrequest',
    component: () =>
      import(`pages/Inventory/InventoryRequest/InventoryRequestForm`),
  },
  {
    pageType: LIST,
    moduleName: 'inventoryrequest',
    component: () =>
      import(`pages/Inventory/InventoryRequest/InventoryRequestList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'inventoryrequest',
    component: () =>
      import(`pages/Inventory/InventoryRequest/InventoryRequestList`),
    children: [
      {
        path: '',
        component: () =>
          import(
            `pages/Inventory/InventoryRequest/InventoryRequestSummary.vue`
          ),
      },
    ],
  },
  // Deliveries
  {
    pageType: CREATE,
    moduleName: 'deliveries',
    component: () => import(`pages/deliveries/deliveriesForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'deliveries',
    component: () => import(`pages/deliveries/deliveriesForm`),
  },
  {
    pageType: LIST,
    moduleName: 'deliveries',
    component: () => import(`pages/deliveries/deliveriesList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'deliveries',
    component: () => import(`pages/deliveries/deliveriesListOverview`),
    children: [
      {
        path: '',
        component: () => import(`pages/deliveries/deliveriesOverview`),
      },
    ],
  },
  // Lockers
  {
    pageType: CREATE,
    moduleName: 'lockers',
    component: () => import(`pages/lockers/lockersForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'lockers',
    component: () => import(`pages/lockers/lockersForm`),
  },
  {
    pageType: LIST,
    moduleName: 'lockers',
    component: () => import(`pages/lockers/lockersList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'lockers',
    component: () => import(`pages/lockers/lockersListOverview`),
    children: [
      {
        path: '',
        component: () => import(`pages/lockers/lockersOverview`),
      },
    ],
  },
  // Parking Stall
  {
    pageType: CREATE,
    moduleName: 'parkingstall',
    component: () => import(`pages/parkingstall/parkingstallForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'parkingstall',
    component: () => import(`pages/parkingstall/parkingstallForm`),
  },
  {
    pageType: LIST,
    moduleName: 'parkingstall',
    component: () => import(`pages/parkingstall/parkingstallList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'parkingstall',
    component: () => import(`pages/parkingstall/parkingstallListOverview`),
    children: [
      {
        path: '',
        component: () => import(`pages/parkingstall/parkingstallOverview`),
      },
    ],
  },
  // ROOMS
  {
    pageType: CREATE,
    moduleName: 'rooms',
    component: () => import(`src/pages/rooms/RoomsForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'rooms',
    component: () => import(`src/pages/rooms/RoomsForm`),
  },
  {
    pageType: LIST,
    moduleName: 'rooms',
    component: () => import(`src/pages/rooms/RoomsList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'rooms',
    component: () => import(`src/pages/rooms/RoomsListOverview`),
    children: [
      {
        path: '',
        component: () => import(`src/pages/rooms/RoomsSummary`),
      },
    ],
  },
  // moves
  {
    pageType: CREATE,
    moduleName: 'moves',
    component: () => import(`pages/moves/movesForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'moves',
    component: () => import(`pages/moves/movesForm`),
  },
  {
    pageType: LIST,
    moduleName: 'moves',
    component: () => import(`pages/moves/movesList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'moves',
    component: () => import(`pages/moves/movesListOverview`),
    children: [
      {
        path: '',
        component: () => import(`pages/moves/movesOverview`),
      },
    ],
  },
  // delivery area
  {
    pageType: CREATE,
    moduleName: 'deliveryArea',
    component: () => import(`pages/deliveryArea/deliveryAreaForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'deliveryArea',
    component: () => import(`pages/deliveryArea/deliveryAreaForm`),
  },
  {
    pageType: LIST,
    moduleName: 'deliveryArea',
    component: () => import(`pages/deliveryArea/deliveryAreaList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'deliveryArea',
    component: () => import(`pages/deliveryArea/deliveryAreaListOverview`),
    children: [
      {
        path: '',
        component: () => import(`pages/deliveryArea/deliveryAreaOverview`),
      },
    ],
  },
  // Department
  {
    pageType: CREATE,
    moduleName: 'department',
    component: () => import(`pages/people/department/departmentForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'department',
    component: () => import(`pages/people/department/departmentForm`),
  },
  {
    pageType: LIST,
    moduleName: 'department',
    component: () => import(`pages/people/department/departmentList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'department',
    component: () => import(`pages/people/department/departmentListOverview`),
    children: [
      {
        path: '',
        component: () => import(`pages/people/department/departmentOverview`),
      },
    ],
  },
  // Desks
  {
    pageType: CREATE,
    moduleName: 'desks',
    component: () => import(`pages/desks/desksForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'desks',
    component: () => import(`pages/desks/desksForm`),
  },
  {
    pageType: LIST,
    moduleName: 'desks',
    component: () => import(`pages/desks/desksList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'desks',
    component: () => import(`pages/desks/desksListOverview`),
    children: [
      {
        path: '',
        component: () => import(`pages/desks/desksOverview`),
      },
    ],
  },
  // Employee
  {
    pageType: CREATE,
    moduleName: 'employee',
    component: () => import(`pages/people/employees/EmployeeForm`),
  },
  {
    pageType: EDIT,
    moduleName: 'employee',
    component: () => import(`pages/people/employees/EmployeeForm`),
  },
  {
    pageType: LIST,
    moduleName: 'employee',
    component: () => import(`pages/people/employees/Employees`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'employee',
    component: () => import(`pages/people/employees/EmployeeOverviewList`),
    children: [
      {
        path: '',
        component: () => import(`pages/people/employees/EmployeeOverviewPage`),
      },
    ],
  },
  // Inspection Template
  {
    pageType: CREATE,
    moduleName: 'inspectionTemplate',
    component: () =>
      import('pages/inspection/inspection-template/NewInspectionTemplate'),
  },
  {
    pageType: EDIT,
    moduleName: 'inspectionTemplate',
    component: () =>
      import('pages/inspection/inspection-template/NewInspectionTemplate'),
  },
  {
    pageType: LIST,
    moduleName: 'inspectionTemplate',
    component: () =>
      import('pages/inspection/inspection-template/InspectionTemplateList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'inspectionTemplate',
    component: () =>
      import('pages/inspection/inspection-template/InspectionTemplateList'),
    children: [
      {
        path: '',
        component: () =>
          import(
            'pages/inspection/inspection-template/InspectionTemplateSummary'
          ),
      },

      {
        path: 'builder',
        component: () =>
          import('pages/inspection/inspection-builder/InspectionBuilder'),
      },
    ],
  },
  // Inspection response
  {
    pageType: EDIT,
    moduleName: 'inspectionResponse',
    component: () =>
      import('pages/inspection/individual-inspection/InspectionLiveForm'),
  },
  {
    pageType: LIST,
    moduleName: 'inspectionResponse',
    component: () =>
      import('pages/inspection/individual-inspection/IndividualInspectionList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'inspectionResponse',
    component: () =>
      import('pages/inspection/individual-inspection/IndividualInspectionList'),
    children: [
      {
        path: '',
        component: () =>
          import(
            'pages/inspection/individual-inspection/IndividualInspectionSummary'
          ),
      },
    ],
  },
  // Induction Template
  {
    pageType: CREATE,
    moduleName: 'inductionTemplate',
    component: () =>
      import('pages/induction/induction-template/NewInductionTemplate'),
  },
  {
    pageType: EDIT,
    moduleName: 'inductionTemplate',
    component: () =>
      import('pages/induction/induction-template/NewInductionTemplate'),
  },
  {
    pageType: LIST,
    moduleName: 'inductionTemplate',
    component: () =>
      import('pages/induction/induction-template/InductionTemplateList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'inductionTemplate',
    component: () =>
      import('pages/induction/induction-template/InductionTemplateList'),
    children: [
      {
        path: '',
        component: () =>
          import(`pages/induction/induction-template/InductionTemplateSummary`),
      },

      {
        path: 'builder',
        component: () =>
          import('pages/induction/induction-builder/InductionBuilder'),
      },
    ],
  },
  // Induction response
  {
    pageType: EDIT,
    moduleName: 'inductionResponse',
    component: () =>
      import('pages/induction/individual-induction/InductionLiveForm'),
  },
  {
    pageType: LIST,
    moduleName: 'inductionResponse',
    component: () =>
      import('pages/induction/individual-induction/IndividualInductionList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'inductionResponse',
    component: () =>
      import('pages/induction/individual-induction/IndividualInductionList'),
    children: [
      {
        path: '',
        component: () =>
          import(
            'pages/induction/individual-induction/IndividualInductionSummary'
          ),
      },
    ],
  },
  // Survey Template
  {
    pageType: CREATE,
    moduleName: 'surveyTemplate',
    component: () => import('pages/survey/survey-template/NewSurveyTemplate'),
  },
  {
    pageType: EDIT,
    moduleName: 'surveyTemplate',
    component: () => import('pages/survey/survey-template/NewSurveyTemplate'),
  },
  {
    pageType: LIST,
    moduleName: 'surveyTemplate',
    component: () => import('pages/survey/survey-template/SurveyTemplateList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'surveyTemplate',
    component: () => import('pages/survey/survey-template/SurveyTemplateList'),
    children: [
      {
        path: '',
        component: () =>
          import(`pages/survey/survey-template/SurveyTemplateSummary`),
      },

      {
        path: 'builder',
        component: () => import('pages/survey/survey-builder/SurveyBuilder'),
      },
    ],
  },
  // Survey response
  {
    pageType: EDIT,
    moduleName: 'surveyResponse',
    component: () => import('pages/survey/individual-survey/SurveyLiveForm'),
  },
  {
    pageType: LIST,
    moduleName: 'surveyResponse',
    component: () =>
      import('pages/survey/individual-survey/IndividualSurveyList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'surveyResponse',
    component: () =>
      import('pages/survey/individual-survey/IndividualSurveyList'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/survey/individual-survey/IndividualSurveySummary'),
      },
    ],
  },
  //insurance
  {
    pageType: EDIT,
    moduleName: 'insurance',
    component: () => import('pages/vendor/insurance/InsuranceCreation.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'insurance',
    component: () => import('pages/vendor/insurance/InsuranceCreation.vue'),
  },
  {
    pageType: LIST,
    moduleName: 'insurance',
    component: () => import('pages/vendor/insurance/InsuranceList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'insurance',
    component: () => import('pages/vendor/insurance/InsuranceList.vue'),

    children: [
      {
        path: '',
        component: () => import('pages/vendor/insurance/InsuranceOverview.vue'),
      },
    ],
  },
  //safety plan
  {
    pageType: LIST,
    moduleName: 'safetyPlan',
    component: () => import(`pages/safetyplan/sp/SafetyPlanCommonList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'safetyPlan',
    component: () => import(`pages/safetyplan/sp/SafetyPlanCommonList`),
    children: [
      {
        path: '',
        component: () => import(`pages/safetyplan/sp/SafetyPlanOverview`),
      },
    ],
  },
  //hazard
  {
    pageType: LIST,
    moduleName: 'hazard',
    component: () => import(`pages/safetyplan/hazard/HazardsList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'hazard',
    component: () => import(`pages/safetyplan/hazard/HazardsList`),
    children: [
      {
        path: '',
        component: () => import(`pages/safetyplan/hazard/HazardPlanOverview`),
      },
    ],
  },
  //precaution
  {
    pageType: LIST,
    moduleName: 'precaution',
    component: () => import(`pages/safetyplan/precaution/PrecautionsList`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'precaution',
    component: () => import(`pages/safetyplan/precaution/PrecautionsList`),
    children: [
      {
        path: '',
        component: () =>
          import(`pages/safetyplan/precaution/PrecautionOverview`),
      },
    ],
  },
  //faults
  {
    pageType: LIST,
    moduleName: 'newreadingalarm',
    component: () =>
      import('src/pages/firealarm/alarms/alarms/v3/NewFaultList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'newreadingalarm',
    component: () =>
      import('src/pages/firealarm/alarms/alarms/v3/NewFaultList.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('src/pages/firealarm/alarms/alarms/v3/FaultsSummary.vue'),
      },
    ],
  },
  //bmsalarm
  {
    pageType: LIST,
    moduleName: 'bmsalarm',
    component: () =>
      import('src/pages/firealarm/alarms/alarms/v3/BmsAlarmsList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'bmsalarm',
    component: () =>
      import('src/pages/firealarm/alarms/alarms/v3/BmsAlarmsList'),
    children: [
      {
        path: '',
        component: () =>
          import('src/pages/firealarm/alarms/alarms/v3/BmsAlarmSummary.vue'),
      },
    ],
  },
  //newreadingrules
  {
    pageType: LIST,
    moduleName: 'newreadingrules',
    component: () => import('pages/firealarm/rules/NewRulesList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'newreadingrules',
    component: () => import('pages/firealarm/rules/NewRulesList.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/firealarm/rules/NewRulesSummary.vue'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'newreadingrules',
    component: () => import('pages/alarm/rule-creation/AlarmRuleCreation.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'newreadingrules',
    component: () => import('pages/alarm/rule-creation/AlarmRuleCreation.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'site',
    component: () => import('src/newapp/list/CommonModuleList.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('src/pages/spacemanagement/overview/SiteOverview.vue'),
      },
    ],
  },
  //shift
  {
    pageType: LIST,
    moduleName: 'shift',
    component: () => import('pages/peopleV2/shift/ShiftList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'shift',
    component: () => import('pages/peopleV2/shift/ShiftList'),
    children: [
      {
        path: '',
        component: () => import('pages/peopleV2/shift/ShiftSummary'),
      },
    ],
  },
  //break
  {
    pageType: LIST,
    moduleName: 'break',
    component: () => import('pages/peopleV2/break/BreakList'),
  },
  //Routes

  {
    pageType: LIST,
    moduleName: 'routes',
    component: () => import('src/pages/planning/routes/RouteList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'routes',
    component: () => import('src/pages/planning/routes/RouteList'),
    children: [
      {
        path: '',
        component: () => import('pages/planning/routes/RouteSummary'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'routes',
    component: () => import('pages/planning/routes/RouteForm'),
  },
  {
    pageType: EDIT,
    moduleName: 'routes',
    component: () => import('pages/planning/routes/RouteForm'),
  },
  // request for quotation
  {
    pageType: CREATE,
    moduleName: 'quote',
    component: () => import(`src/pages/quotation/QuotationForm.vue`),
  },
  {
    pageType: EDIT,
    moduleName: 'quote',
    component: () => import(`src/pages/quotation/QuotationForm.vue`),
  },
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
  // vendor quotes
  {
    pageType: LIST,
    moduleName: 'vendorQuotes',
    component: () =>
      import(`src/pages/purchase/vendorQuotes/VendorQuotesList.vue`),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'vendorQuotes',
    component: () =>
      import(`src/pages/purchase/vendorQuotes/VendorQuotesList.vue`),
    children: [
      {
        path: '',
        component: () =>
          import(`src/pages/purchase/vendorQuotes/VendorQuotesSummary.vue`),
      },
    ],
  },
  // client
  {
    pageType: OVERVIEW,
    moduleName: 'clientcontact',
    component: () => import(`src/newapp/list/CommonModuleList.vue`),
    children: [
      {
        path: '',
        component: () =>
          import('pages/clients/clientcontact/ClientContactOverviewPage.vue'),
      },
    ],
  },
  //Control Group
  {
    pageType: LIST,
    moduleName: 'controlGroupv2',
    component: () =>
      import('pages/controls/controlgroups/ControlGroupList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'controlGroupv2',
    component: () =>
      import('pages/controls/controlgroups/ControlGroupList.vue'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/controls/controlgroups/ControlGroupSummary.vue'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'controlGroupv2',
    component: () => import('pages/controls/controlgroups/NewGroup.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'controlGroupv2',
    component: () => import('pages/controls/controlgroups/NewGroup.vue'),
  },
  //Control Schedule
  {
    pageType: LIST,
    moduleName: 'controlSchedule',
    component: () =>
      import('pages/controls/controlschedule/ControlScheduleList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'controlSchedule',
    component: () =>
      import('pages/controls/controlschedule/ControlScheduleList'),
    children: [
      {
        path: '',
        component: () =>
          import('pages/controls/controlschedule/ControlScheduleSummary'),
      },
    ],
  },
  {
    pageType: CREATE,
    moduleName: 'controlSchedule',
    component: () => import('pages/controls/controlschedule/NewSchedule'),
  },
  {
    pageType: EDIT,
    moduleName: 'controlSchedule',
    component: () => import('pages/controls/controlschedule/NewSchedule'),
  },

  {
    pageType: LIST,
    moduleName: 'sensorrollupalarm',
    component: () =>
      import('src/pages/firealarm/alarms/alarms/v3/SensorAlarmList'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'sensorrollupalarm',
    component: () =>
      import('src/pages/firealarm/alarms/alarms/v3/SensorAlarmList'),
    children: [
      {
        path: '',
        component: () =>
          import('src/pages/firealarm/alarms/alarms/v3/SensorAlarmSummary'),
      },
    ],
  },

  {
    pageType: OVERVIEW,
    moduleName: 'workorder',
    component: () =>
      import(
        'src/beta/summary/widgets/WorkorderWidget/WorkorderSummaryWidget.vue'
      ),
  },
  {
    pageType: CREATE,
    moduleName: 'calendarEvent',
    component: () =>
      import('src/beta/pages/Event/CalenderEventForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'calendarEvent',
    component: () =>
      import('src/beta/pages/Event/CalenderEventForm.vue'),
  },
  {
    pageType: LIST,
    moduleName: 'calendarEvent',
    component: () =>
      import('src/beta/pages/Event/CalendarEventListPage.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'calendar',
    component: () => import('src/beta/pages/Calendar/CalendarForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'calendar',
    component: () => import('src/beta/pages/Calendar/CalendarForm.vue'),
  },
  {
    pageType: LIST,
    moduleName: 'calendar',
    component: () =>
      import('src/beta/pages/Calendar/CalendarList.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'controlAction',
    component: () => import('src/beta/pages/ControlAction/ControlActionNewForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'controlAction',
    component: () => import('src/beta/pages/ControlAction/ControlActionNewForm.vue'),
  },
  {
    pageType: LIST,
    moduleName: 'controlAction',
    component: () =>
      import('src/beta/pages/ControlAction/ControlActionList.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'controlActionTemplate',
    component: () => import('src/beta/pages/ControlActionTemplete/ControlActionTempleteForm.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'controlActionTemplate',
    component: () => import('src/beta/pages/ControlActionTemplete/ControlActionTempleteForm.vue'),
  },
  {
    pageType: LIST,
    moduleName: 'controlActionTemplate',
    component: () =>
      import('src/beta/pages/ControlActionTemplete/ControlActionTemplateList.vue'),
  },
  //Utility customer
  {
    pageType: CREATE,
    moduleName: 'utilityIntegrationCustomer',
    component: () =>
      import('src/beta/pages/UtilityApiIntegration/AddUtilityApiAccount.vue'),
  },
  {
    pageType: LIST,
    moduleName: 'utilityIntegrationCustomer',
    component: () =>
      import(
        'src/beta/pages/UtilityApiIntegration/UtilityIntegrationCustomerList.vue'
      ),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'utilityIntegrationCustomer',
    component: () =>
      import(
        'src/beta/pages/UtilityApiIntegration/UtilityIntegrationCustomerDetails.vue'
      ),
  },
  //utility Integration Bill
  {
    pageType: LIST,
    moduleName: 'utilityIntegrationBills',
    name: 'utilityIntegrationBillList',
    component: () =>
      import(
        'src/beta/pages/UtilityApiIntegration/UtilityIntegrationBillList.vue'
      ),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'utilityIntegrationBills',
    component: () =>
      import(
        'src/beta/pages/UtilityApiIntegration/UtilityIntegrationBillSummary.vue'
      ),
  },
  {
    pageType: MODULE_CUSTOM,
    moduleName: 'utilityIntegrationBills',
    component: () => import('src/PortalTenant/DefaultLayout.vue'),
    children: [
      {
        path: 'utilityBillspdf',
        component: () => import(`src/pages/pdf/UtilityBillPdf.vue`),
      },
    ],
  },
  //utility Integration Tariff

  {
    pageType: CREATE,
    moduleName: 'utilityIntegrationTariff',
    name: 'utilityTariffCreate',
    component: () =>
      import(
        'src/beta/pages/UtilityApiIntegration/UtilityIntegrationTariffForm.vue'
      ),
  },
  {
    pageType: EDIT,
    moduleName: 'utilityIntegrationTariff',
    name: 'utilityTariffEdit',
    component: () =>
      import(
        'src/beta/pages/UtilityApiIntegration/UtilityIntegrationTariffForm.vue'
      ),
  },
  {
    pageType: LIST,
    moduleName: 'utilityIntegrationTariff',
    name: 'utilityTariffList',
    component: () =>
      import(
        'src/beta/pages/UtilityApiIntegration/UtilityIntegrationTariffList.vue'
      ),
  },

  //Utility Dispute
  {
    pageType: LIST,
    moduleName: 'utilityDispute',
    name: 'utilityDisputeList',
    component: () =>
      import('src/beta/pages/UtilityApiIntegration/UtilityDisputeList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'utilityDispute',
    component: () =>
      import('src/beta/pages/UtilityApiIntegration/UtilityDisputeSummary.vue'),
  },
  //meter
  {
    pageType: LIST,
    moduleName: 'meter',
    component: () => import('src/beta/pages/meter/MeterList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'meter',
    component: () => import('src/beta/pages/meter/MeterSummary.vue'),
  },
  {
    pageType: CREATE,
    moduleName: 'meter',
    component: () => import('src/beta/pages/meter/NewMeter.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'meter',
    component: () => import('src/beta/pages/meter/NewMeter.vue'),
  },
  //virtualMeterTemplate
  {
    pageType: CREATE,
    moduleName: 'virtualMeterTemplate',
    component: () => import('src/beta/pages/meter/vmTemplate/NewVMTemplate.vue'),
  },
  {
    pageType: EDIT,
    moduleName: 'virtualMeterTemplate',
    component: () => import('src/beta/pages/meter/vmTemplate/NewVMTemplate.vue'),
  },
  {
    pageType: LIST,
    moduleName: 'virtualMeterTemplate',
    component: () =>
      import('src/beta/pages/meter/vmTemplate/VirtualMeterTemplateList.vue'),
  },
  {
    pageType: OVERVIEW,
    moduleName: 'virtualMeterTemplate',
    component: () =>
      import('src/beta/pages/meter/vmTemplate/VirtualMeterTemplateSummary.vue'),
  },
]
