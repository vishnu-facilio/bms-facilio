export const moduleRouteHash = {
  workorder: {
    LIST: { name: 'workorderhomev1' },
    VIEW_MANAGER: { name: 'workorder-viewmanager' },
    VIEW_CREATION: { name: 'workorder-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return { name: 'wosummarynew', params: { id, viewname } }
    },
  },
  jobplan: {
    LIST: { name: 'jobPlanList' },
    VIEW_MANAGER: { name: 'workorder-viewmanager' },
    VIEW_CREATION: { name: 'workorder-view-creation-form' },
  },
  asset: {
    LIST: { name: 'assethomev1' },
    VIEW_MANAGER: { name: 'assets-viewmanager' },
    VIEW_CREATION: { name: 'assets-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return { path: `/app/at/assets/${viewname}/${id}/overview` }
    },
  },
  failureclass: {
    LIST: { name: 'failure-class-list' },
    VIEW_MANAGER: { name: 'assets-viewmanager' },
    VIEW_CREATION: { name: 'assets-view-creation-form' },
  },
  contact: {
    LIST: { name: 'contactlist' },
    VIEW_MANAGER: { name: 'contacts-viewmanager' },
    VIEW_CREATION: { name: 'contacts-view-creation-form' },
  },
  purchaserequest: {
    LIST: { name: 'purchaserequest' },
    VIEW_MANAGER: { name: 'purchase-viewmanager' },
    VIEW_CREATION: { name: 'purchase-view-creation-form' },
  },
  purchaseorder: {
    LIST: { name: 'purchaseorder' },
    VIEW_MANAGER: { name: 'purchase-viewmanager' },
    VIEW_CREATION: { name: 'purchase-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return { name: 'poSummary', params: { id, viewname } }
    },
  },
  receivable: {
    LIST: { name: 'receivable' },
    VIEW_MANAGER: { name: 'purchase-viewmanager' },
    VIEW_CREATION: { name: 'purchase-view-creation-form' },
  },
  termsandconditions: {
    LIST: { name: 'tandclist' },
    VIEW_MANAGER: { name: 'purchase-viewmanager' },
    VIEW_CREATION: { name: 'purchase-view-creation-form' },
  },
  vendors: {
    LIST: { name: 'vendorList' },
    VIEW_MANAGER: { name: 'vendor-viewmanager' },
    VIEW_CREATION: { name: 'vendor-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return { path: `/app/vendor/vendors/${viewname}/summary/${id}` }
    },
  },
  insurance: {
    LIST: { name: 'insurancesList' },
    VIEW_MANAGER: { name: 'vendor-viewmanager' },
    VIEW_CREATION: { name: 'vendor-view-creation-form' },
  },
  employee: {
    LIST: { name: 'employee-v2-list' },
    VIEW_MANAGER: { name: 'people-viewmanager' },
    VIEW_CREATION: { name: 'people-view-creation-form' },
  },
  // department: {
  //   LIST: { name: 'departmentList' },
  //   VIEW_MANAGER: { name: 'employee-viewmanager' },
  //   VIEW_CREATION: { name: 'employee-view-creation-form' },
  // },
  tenantcontact: {
    LIST: { name: 'tenantcontactlist' },
    VIEW_MANAGER: { name: 'tm-viewmanager' },
    VIEW_CREATION: { name: 'tm-view-creation-form' },
  },
  tenant: {
    LIST: { name: 'tenantreports' },
    VIEW_MANAGER: { name: 'tm-viewmanager' },
    VIEW_CREATION: { name: 'tm-view-creation-form' },
  },
  tenantunit: {
    LIST: { name: 'tenantunit-list' },
    VIEW_MANAGER: { name: 'tm-viewmanager' },
    VIEW_CREATION: { name: 'tm-view-creation-form' },
  },
  quote: {
    LIST: { name: 'quotation-list' },
    VIEW_MANAGER: { name: 'tm-viewmanager' },
    VIEW_CREATION: { name: 'tm-view-creation-form' },
  },
  sensorrollupalarm: {
    LIST: { name: 'sensorrollupalarm-list' },
    VIEW_MANAGER: { name: 'sensoralarm-viewmanager' },
    VIEW_CREATION: { name: 'sensoralarm-view-creation-form' },
  },
  newreadingalarm: {
    LIST: { name: 'newreadingalarm-list' },
    VIEW_MANAGER: { name: 'sensoralarm-viewmanager' },
    VIEW_CREATION: { name: 'sensoralarm-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return {
        path: `/app/fa/faults/${viewname}/newsummary/${id}`,
      }
    },
  },
  bmsalarm: {
    LIST: { name: 'bmsalarm-list' },
    VIEW_MANAGER: { name: 'sensoralarm-viewmanager' },
    VIEW_CREATION: { name: 'sensoralarm-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return {
        path: `/app/fa/bmsalarms/${viewname}/summary/${id}`,
      }
    },
  },
  newreadingrules: {
    LIST: { name: 'newrules' },
    VIEW_MANAGER: { name: 'sensoralarm-viewmanager' },
    VIEW_CREATION: { name: 'sensoralarm-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return {
        path: `/app/fa/newrules/${viewname}/${id}/summary`,
      }
    },
  },
  visitorlog: {
    LIST: { name: 'visits-list' },
    VIEW_MANAGER: { name: 'vi-viewmanager' },
    VIEW_CREATION: { name: 'vi-view-creation-form' },
  },
  invitevisitor: {
    LIST: { name: 'invites-list' },
    VIEW_MANAGER: { name: 'vi-viewmanager' },
    VIEW_CREATION: { name: 'vi-view-creation-form' },
  },
  groupinvite: {
    LIST: { name: 'group-invites-list' },
    VIEW_MANAGER: { name: 'vi-viewmanager' },
    VIEW_CREATION: { name: 'vi-view-creation-form' },
  },
  watchlist: {
    LIST: { name: 'watchlist-list' },
    VIEW_MANAGER: { name: 'vi-viewmanager' },
    VIEW_CREATION: { name: 'vi-view-creation-form' },
  },
  serviceRequest: {
    LIST: { name: 'serviceRequestList' },
    VIEW_MANAGER: { name: 'serviceRequest-viewmanager' },
    VIEW_CREATION: { name: 'serviceRequest-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return {
        path: `/app/sr/serviceRequest/${viewname}/${id}/overview`,
      }
    },
  },
  contactdirectory: {
    LIST: { name: 'list-contactdir' },
    VIEW_MANAGER: { name: 'serviceRequest-viewmanager' },
    VIEW_CREATION: { name: 'serviceRequest-view-creation-form' },
  },
  admindocuments: {
    LIST: { name: 'list-admindocs' },
    VIEW_MANAGER: { name: 'serviceRequest-viewmanager' },
    VIEW_CREATION: { name: 'serviceRequest-view-creation-form' },
  },
  safetyPlan: {
    LIST: { name: 'safetyplan' },
    VIEW_MANAGER: { name: 'safetyplan-viewmanager' },
    VIEW_CREATION: { name: 'safetyplan-view-creation-form' },
  },
  hazard: {
    LIST: { name: 'hazard' },
    VIEW_MANAGER: { name: 'safetyplan-viewmanager' },
    VIEW_CREATION: { name: 'safetyplan-view-creation-form' },
  },
  precaution: {
    LIST: { name: 'precaution' },
    VIEW_MANAGER: { name: 'safetyplan-viewmanager' },
    VIEW_CREATION: { name: 'safetyplan-view-creation-form' },
  },
  client: {
    LIST: { name: 'client-list' },
    VIEW_MANAGER: { name: 'cl-viewmanager' },
    VIEW_CREATION: { name: 'cl-view-creation-form' },
  },
  purchasecontracts: {
    LIST: { name: 'contractList' },
    VIEW_MANAGER: { name: 'contractlist-viewmanager' },
    VIEW_CREATION: { name: 'contractlist-view-creation-form' },
  },
  labourcontracts: {
    LIST: { name: 'contractList' },
    VIEW_MANAGER: { name: 'contractlist-viewmanager' },
    VIEW_CREATION: { name: 'contractlist-view-creation-form' },
  },
  rentalleasecontracts: {
    LIST: { name: 'contractList' },
    VIEW_MANAGER: { name: 'contractlist-viewmanager' },
    VIEW_CREATION: { name: 'contractlist-view-creation-form' },
  },
  warrantycontracts: {
    LIST: { name: 'contractList' },
    VIEW_MANAGER: { name: 'contractlist-viewmanager' },
    VIEW_CREATION: { name: 'contractlist-view-creation-form' },
  },
  announcement: {
    LIST: { name: 'announcementsList' },
    VIEW_MANAGER: { name: 'announcements-viewmanager' },
    VIEW_CREATION: { name: 'announcements-view-creation-form' },
  },
  newsandinformation: {
    LIST: { name: 'newsList' },
    VIEW_MANAGER: { name: 'announcements-viewmanager' },
    VIEW_CREATION: { name: 'announcements-view-creation-form' },
  },
  budget: {
    LIST: { name: 'budgetList' },
    VIEW_MANAGER: { name: 'budget-viewmanager' },
    VIEW_CREATION: { name: 'budget-view-creation-form' },
  },
  chartofaccount: {
    LIST: { name: 'coaList' },
    VIEW_MANAGER: { name: 'budget-viewmanager' },
    VIEW_CREATION: { name: 'budget-view-creation-form' },
  },
  accounttype: {
    LIST: { name: 'accountTypeList' },
    VIEW_MANAGER: { name: 'budget-viewmanager' },
    VIEW_CREATION: { name: 'budget-view-creation-form' },
  },
  facilitybooking: {
    LIST: { name: 'bookingList' },
    VIEW_MANAGER: { name: 'booking-viewmanager' },
    VIEW_CREATION: { name: 'booking-view-creation-form' },
  },
  facility: {
    LIST: { name: 'facilityList' },
    VIEW_MANAGER: { name: 'booking-viewmanager' },
    VIEW_CREATION: { name: 'booking-view-creation-form' },
  },
  amenity: {
    LIST: { name: 'amenityList' },
    VIEW_MANAGER: { name: 'booking-viewmanager' },
    VIEW_CREATION: { name: 'booking-view-creation-form' },
  },
  storeRoom: {
    LIST: { name: 'storerooms' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  transferrequest: {
    LIST: { name: 'transferrequestList' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  transferrequestshipment: {
    LIST: { name: 'trShipmentList' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  item: {
    LIST: { name: 'item' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  itemTypes: {
    LIST: { name: 'itemtypes' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  gatepass: {
    LIST: { name: 'gatepass' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  service: {
    LIST: { name: 'service' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  tool: {
    LIST: { name: 'tool' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  toolTypes: {
    LIST: { name: 'tooltypes' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  inventoryrequest: {
    LIST: { name: 'inventoryrequest' },
    VIEW_MANAGER: { name: 'inventory-viewmanager' },
    VIEW_CREATION: { name: 'inventory-view-creation-form' },
  },
  inspectionTemplate: {
    LIST: { name: 'inspectionTemplateList' },
    VIEW_MANAGER: { name: 'inspection-viewmanager' },
    VIEW_CREATION: { name: 'inspection-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return {
        path: `/app/inspection/template/${viewname}/summary/${id}`,
      }
    },
  },
  inspectionResponse: {
    LIST: { name: 'individualInspectionList' },
    VIEW_MANAGER: { name: 'inspection-viewmanager' },
    VIEW_CREATION: { name: 'inspection-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return {
        path: `/app/inspection/individual/${viewname}/summary/${id}`,
      }
    },
  },
  deliveries: {
    LIST: { name: 'deliveriesList' },
    VIEW_MANAGER: { name: 'deliveries-viewmanager' },
    VIEW_CREATION: { name: 'deliveries-view-creation-form' },
  },
  deliveryArea: {
    LIST: { name: 'deliveryAreaList' },
    VIEW_MANAGER: { name: 'deliveries-viewmanager' },
    VIEW_CREATION: { name: 'deliveries-view-creation-form' },
  },
  lockers: {
    LIST: { name: 'lockersList' },
    VIEW_MANAGER: { name: 'lockers-viewmanager' },
    VIEW_CREATION: { name: 'lockers-view-creation-form' },
  },
  parkingstall: {
    LIST: { name: 'parkingstallList' },
    VIEW_MANAGER: { name: 'parkingstall-viewmanager' },
    VIEW_CREATION: { name: 'parkingstall-view-creation-form' },
  },
  moves: {
    LIST: { name: 'movesList' },
    VIEW_MANAGER: { name: 'moves-viewmanager' },
    VIEW_CREATION: { name: 'moves-view-creation-form' },
  },
  desks: {
    LIST: { name: 'desksList' },
    VIEW_MANAGER: { name: 'desks-viewmanager' },
    VIEW_CREATION: { name: 'desks-view-creation-form' },
  },
  inductionTemplate: {
    LIST: { name: 'inductionTemplateList' },
    VIEW_MANAGER: { name: 'induction-viewmanager' },
    VIEW_CREATION: { name: 'induction-view-creation-form' },
  },
  inductionResponse: {
    LIST: { name: 'individualInductionList' },
    VIEW_MANAGER: { name: 'induction-viewmanager' },
    VIEW_CREATION: { name: 'induction-view-creation-form' },
  },
  surveyTemplate: {
    LIST: { name: 'surveyTemplateList' },
    VIEW_MANAGER: { name: 'survey-viewmanager' },
    VIEW_CREATION: { name: 'survey-view-creation-form' },
  },
  surveyResponse: {
    LIST: { name: 'individualSurveyList' },
    VIEW_MANAGER: { name: 'survey-viewmanager' },
    VIEW_CREATION: { name: 'survey-view-creation-form' },
  },
  preventivemaintenance: {
    LIST: { name: 'pm-planned-list' },
    VIEW_MANAGER: { name: 'workorder-viewmanager' },
    VIEW_CREATION: { name: 'workorder-view-creation-form' },
  },
  controlGroupv2: {
    LIST: { name: 'group-list' },
    VIEW_MANAGER: { name: 'controls-viewmanager' },
    VIEW_CREATION: { name: 'controls-view-creation-form' },
  },
  audience: {
    LIST: { name: 'audienceList' },
    VIEW_MANAGER: { name: 'announcements-viewmanager' },
    VIEW_CREATION: { name: 'announcements-view-creation-form' },
  },
  neighbourhood: {
    LIST: { name: 'neighbourhoodList' },
    VIEW_MANAGER: { name: 'announcements-viewmanager' },
    VIEW_CREATION: { name: 'announcements-view-creation-form' },
  },
  dealsandoffers: {
    LIST: { name: 'dealsandoffersList' },
    VIEW_MANAGER: { name: 'announcements-viewmanager' },
    VIEW_CREATION: { name: 'announcements-view-creation-form' },
  },
  shift: {
    LIST: { name: 'shift-v2-list' },
    VIEW_MANAGER: { name: 'shift-viewmanager' },
    VIEW_CREATION: { name: 'shift-view-creation-form' },
  },
  break: {
    LIST: { name: 'break-v2-list' },
    VIEW_MANAGER: { name: 'break-viewmanager' },
    VIEW_CREATION: { name: 'break-view-creation-form' },
  },
  DEFAULT: {
    LIST: { name: 'custommodules-list' },
    VIEW_MANAGER: { name: 'custommodules-viewmanager' },
    VIEW_CREATION: { name: 'custommodules-view-creation-form' },
  },
  workpermit: {
    LIST: { name: 'workPermitList' },
    VIEW_MANAGER: { name: 'workorder-viewmanager' },
    VIEW_CREATION: { name: 'workorder-view-creation-form' },
  },
  alarm: {
    OVERVIEW: (id, viewname) => {
      return { path: `/app/fa/faults/${viewname}/newsummary/${id}` }
    },
  },
  ticket: {
    OVERVIEW: (id, viewname) => {
      return { name: 'wosummarynew', params: { id, viewname } }
    },
  },
  energymeter: {
    LIST: { name: 'energyHome' },
    VIEW_MANAGER: { name: 'energymeter-viewmanager' },
    VIEW_CREATION: { name: 'energymeter-view-creation-form' },
    OVERVIEW: (id, viewname) => {
      return { path: `/app/en/energymeter/${viewname}/${id}/overview` }
    },
  },
  plannedmaintenance: {
    LIST: { name: 'pm-list' },
    VIEW_MANAGER: { name: 'pm-viewmanager' },
    VIEW_CREATION: { name: 'pm-view-creation-form' },
  },
  custom: {
    LIST: { name: 'custommodules-list' },
    VIEW_MANAGER: { name: 'custommodules-viewmanager' },
    VIEW_CREATION: { name: 'custommodules-view-creation-form' },
    OVERVIEW: (id, viewname, moduleName) => {
      return {
        name: 'custommodules-summary',
        params: { moduleName, viewname, id },
      }
    },
  },
}

export function findRouterForModuleInApp(moduleName, type) {
  let moduleRouteObj = moduleRouteHash[moduleName] || moduleRouteHash.DEFAULT
  let currentRoute = moduleRouteObj[type] || {}

  return currentRoute || null
}
