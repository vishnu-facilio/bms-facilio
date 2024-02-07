<template>
  <component
    v-if="componentName"
    :is="componentName"
    :widget="widget"
    :eventBus="eventBus"
    :key="recordId"
    v-bind="$attrs"
  ></component>
</template>
<script>
/* eslint-disable vue/no-unused-components */
import { eventBus } from './utils/eventBus'

export default {
  props: ['widget'],
  components: {
    summaryFieldsWidget: () =>
      import('./common/field-details/SummaryFieldsWidget.vue'),
    primaryDetailsWidget: () => import('./common/AssetSummary.vue'),
    secondaryDetailsWidget: () =>
      import('./common/field-details/FieldDetailsLayout.vue'),
    workOrderSecondaryDetailsWidget: () =>
      import('./common/field-details/WorkOrderFieldDetailsLayout.vue'),
    comment: () => import('./common/NotesWidget'),
    attachment: () => import('./common/DocumentsWidget'),
    relatedList: () => import('./common/DeprecatedRelatedListWidget'),

    newRelatedList: () => import('./common/line-items/RelatedList.vue'),

    readings: () => import('./readings/ReadingsList'),
    commandsWidget: () => import('./readings/CommandsWidget'),
    spaceReadings: () => import('./readings/SpaceReadings'),
    relatedReadings: () => import('./readings/RelatedReadings'),
    assetMovements: () => import('./assetMovement/AssetMovementsList'),
    spareParts: () =>
      import('src/components/page/widget/asset/SparePartsWidget.vue'),
    whereUsed: () =>
      import('src/components/page/widget/inventory/WhereUsedWidget.vue'),
    assetReadingWidgetchart: () => import('./asset/AssetReadingWidget'),
    plannedWorkorder: () => import('./pm/list/PlannedList'),
    unPlannedWorkorder: () => import('./workorders/list/UnPlannedList'),
    workordercount: () => import('./workorders/SummaryCard'),
    alarmcount: () => import('./alarms/AlarmSummaryCard'),
    failureMetricscard: () => import('./common/MetricsSummary'),
    woDetailscard: () => import('./workorders/DetailsCard'),
    nextPmcard: () => import('./pm/NextScheduledCard'),
    rankRulecard: () => import('./rule/RateRule'),
    recentlyClosedPmcard: () => import('./pm/RecentlyClosedCard'),
    history: () => import('./common/History.vue'),
    setCommandcard: () => import('./readings/Commands'),
    assetLifecard: () => import('./performance/AssetLife'),
    lastDownTimecard: () => import('./performance/LastReportedDownTime'),
    overallDownTimecard: () => import('./performance/OverallDownTime'),
    alarmInsightscard: () => import('./performance/AlarmInsights'),
    failureRatechart: () => import('./performance/FailureRate'),
    avgTtrchart: () => import('./performance/AvgRepairTime'),
    assetCostDetailscard: () => import('./cost/AssetCostDetails'),
    costBreakupcard: () => import('./cost/CostBreakup'),
    maintenanceCostTrendchart: () => import('./cost/MaintenanceCostTrend'),
    depreciationSchedulecard: () => import('./cost/DepreciationSchedule'),
    depreciationCostTrendchart: () => import('./cost/DepreciationCostTrend'),
    assetsAlarmcard: () => import('./rule/RuleAssetsNAlarm'),
    ruleInsightscard: () => import('./rule/RuleInsights.vue'),
    ruleWoDurationcard: () => import('./rule/RuleWoDuration'),
    ruleAssociatedWocard: () => import('./rule/RuleAssociatedWO'),
    alarmInsightcard: () => import('./rule/RuleInsights.vue'),
    ruleDetialsWidget: () => import('./rule/RulePrimaryDetails'),
    RuleAssociatedWo: () => import('./rule/RuleAssociatedWO'),
    ruleRCA: () => import('./rule/RuleRCA'),
    ecmcard: () => import('./mv/ECMOptionMeasurementCard'),
    mvSummaryWidget: () => import('./mv/MVSummaryWidget'),
    baselineVsActualchart: () => import('./mv/MVBaselineVsActual'),
    costTrendchart: () => import('./mv/MVCostTrend'),
    cumulativeSavingschart: () => import('./mv/MVCumulativeSavings'),
    savingscard: () => import('./mv/MVSavingsCard'),
    mvEnergycard: () => import('./mv/MVEnergyCard'),
    carbonEmisioncard: () => import('./mv/MVCarbonEmisionCard'),
    baselineEquationcard: () => import('./mv/MVBaselineEquationCard'),
    mvAdjustmentscard: () => import('./mv/MVAdjustmentsCard'),
    baselineEquation: () => import('./mv/MVBaselineEquation'),
    mvPercentageSavings: () => import('./mv/MVPercentageSavings'),
    mvPercentageSavingschart: () => import('./mv/MVPercentageSavings'),
    anomalyDetailsWidget: () => import('./anomalies/AnomalyDetailsWidget'),
    agentDetailsWidget: () => import('./alarms/AgentDetailsWidget'),
    alarmDetails: () => import('./alarms/AlarmDetailsCard'),
    alarmSecondaryDetails: () => import('./alarms/AlarmSecondaryDetails'),
    bmsAlarmDetails: () => import('./alarms/BmsAlarmDetailsCard'),
    sensorAlarmDetail: () => import('./alarms/SensorAlarmDetailsWidget'),
    alarmReport: () => import('./alarms/AlarmReportCard'),
    noOfAnomaliescard: () => import('./anomalies/NoOfAnomalies'),
    alarmTimeLine: () => import('./alarms/AlarmTimeLine'),
    mlAlarmEnergyCddcard: () => import('./anomalies/MLAlarmEnergyCdd'),
    energWastageDetailscard: () => import('./anomalies/EnergWastageDetails'),
    anomaliesTrendWidget: () => import('./anomalies/AnomaliesTrendWidget'),
    anomalySubMeterWidget: () => import('./anomalies/AnomalySubMeterWidget'),
    mlMtbacard: () => import('./anomalies/AnomalyMTBAWidget'),
    mlMttccard: () => import('./anomalies/AnomalyMTTCWidget'),
    mlDeviationcard: () => import('./anomalies/AnomalyDeviationWidget'),
    contracts: () => import('./common/AssetContracts.vue'),
    alarmDurationcard: () => import('./alarms/AlarmDurationcard'),
    impactDetailschart: () => import('./alarms/ImpactDetails'),
    impactReport: () => import('./alarms/ImpactDetailsWidget.vue'),
    impactInfo: () => import('./alarms/ImpactInfoWidget.vue'),
    energyImpact: () => import('./alarms/EnergyImpactWidget.vue'),
    costImpact: () => import('./alarms/CostImpactWidget.vue'),
    outOfSchedulechart: () => import('./alarms/OutOfScheduleChart'),
    itemTransactions: () => import('./inventory/AssetItemTransactions.vue'),
    toolTransactions: () => import('./inventory/AssetToolTransactions.vue'),
    defaultComparisoncard: () => import('./utils/DefaultComparisonCard.vue'),
    templateSummaryDetails: () =>
      import('./template/templateSummaryDetails.vue'),
    templateConditions: () => import('./template/templateConditions.vue'),
    templateAlarmDetails: () => import('./template/templateAlarmDetails.vue'),

    // KPI
    timecard: () => import('./performance/AssetLife'),
    kpiDetailsWidget: () => import('./common/FixedFieldDetails'),
    metersAssociatedcard: () => import('./energy/kpi/AssociatedResources'),
    kpiViolationscard: () => import('./energy/kpi/ViolationsSummary'),
    kpiTargetcard: () => import('./energy/kpi/kpiGaugeChart'),
    kpiTrendchart: () => import('./energy/kpi/kpiTrendChart'),
    kpiTabularcard: () => import('./energy/kpi/kpiTabularCard'),
    kpiLatestValuecard: () => import('./energy/kpi/kpiBubbleChart'),

    analyticsChartchart: () =>
      import('./performance/charts/AnalyticsWidgetChart.vue'),
    recurringInfo: () => import('pages/visitors/visitor/RecurringInfoSummary'),
    addressLocation: () => import('pages/vendor/vendors/VendorAddress'),
    insurance: () => import('pages/workorder/workpermit/WorkpermitInsurance'),
    workRequestDetails: () =>
      import('pages/workorder/preventive/v1/WorkRequestDetailsWidget'),
    portalActivity: () =>
      import('src/PortalTenant/components/PortalActivities'),
    spaceWidget: () => import('./common/SpaceDetailsWidget'),

    safetyPlanHazards: () =>
      import('src/pages/safetyplan/component/SafetyPlanHazardList.vue'),
    safetyPlanPrecautions: () =>
      import('pages/safetyplan/component/SafetyPlanPrecautionList'),
    precautionHazardsList: () =>
      import('src/pages/safetyplan/component/PrecautionHazardsList.vue'),
    workAsset: () => import('src/pages/safetyplan/sp/WorkAssetLineItem.vue'),
    tenantSpecialWidget: () => import('./common/TenantSpaceAssets.vue'),
    siteListWidget: () => import('./site/ClientSiteList.vue'),
    weathercard: () => import('components/page/widget/site/WeatherCard'),
    currentWeathercard: () =>
      import('components/page/widget/site/CurrentWeatherCard'),
    weatherChart: () => import('components/page/widget/site/WeatherChart'),
    weatherTable: () => import('components/page/widget/site/WeatherTable'),
    energycard: () =>
      import('components/page/widget/site/EnergyConsumptionCard'),
    relatedCount: () => import('components/page/widget/site/ModuleCountCard'),
    newRelatedCount: () =>
      import('components/page/widget/site/NewModuleCountCard'),
    operatingHourscard: () =>
      import('components/page/widget/site/OperatingHoursCard'),
    newOperatingHourscard: () =>
      import('components/page/widget/site/NewOperatingHoursCard'),
    siteBuildings: () =>
      import('components/page/widget/site/RelatedCardListComponent'),
    newSiteBuildings: () =>
      import('components/page/widget/site/NewRelatedCardListComponent'),
    fixedDetailsWidget: () => import('./common/FixedFieldDetails'),
    siteSpaces: () =>
      import('components/page/widget/site/RelatedTabListComponent'),
    newSiteSpaces: () =>
      import('components/page/widget/site/NewRelatedTabListComponent'),
    floorMap: () => import('components/page/widget/floor/FloorMapView'),
    buildingSpaces: () =>
      import('components/page/widget/site/RelatedTabListComponent'),
    floorSpaces: () =>
      import('components/page/widget/site/NewRelatedCardListComponent'),
    relatedSubSpaces: () =>
      import('components/page/widget/site/RelatedCardListComponent'),
    occupancycard: () =>
      import('components/page/widget/site/OccupancyCountCard'),
    spacePrimaryReadings: () =>
      import('components/page/widget/site/SpacePrimaryReadings'),
    quotationPreview: () =>
      import('src/pages/quotation/v1/QuotationPreviewParent'),
    workPermitPreview: () =>
      import('src/pages/workorder/workpermit/v3/WorkPermitPreview'),
    prPreview: () => import('src/pages/purchase/pr/PrPreviewParent'),
    poPreview: () => import('src/pages/purchase/po/PoPreviewParent'),
    billList: () =>
      import('src/pages/etisalat/BillAlerts/BillAlertSummaryDetailWidget'),
    billAlertDetails: () =>
      import('src/pages/etisalat/BillAlerts/BillAlertSummaryPreviewWidget'),
    invoiceDetails: () =>
      import('src/pages/etisalat/Invoice/paymentMemoPreview'),
    invoiceDetails2: () =>
      import('src/pages/etisalat/Invoice/paymentMemoPreview2'),
    // tariff
    tariffInformation: () =>
      import('src/pages/etisalat/Tariff/TariffInformationWidget'),
    tariffDetails: () => import('src/pages/etisalat/Tariff/TariffDetails.vue'),
    // supplier
    supplierElectrycity: () =>
      import('src/pages/etisalat/suppliers/SupplierElectricityWidget'),
    supplierWater: () =>
      import('src/pages/etisalat/suppliers/SupplierWaterWidget'),
    supplierSeawage: () =>
      import('src/pages/etisalat/suppliers/SupplierSewageWidget'),
    supplierDetails: () =>
      import('src/pages/etisalat/suppliers/SupplierDetailWidget'),
    supplierActivePayment: () =>
      import('src/pages/etisalat/suppliers/SupplierActivePayments'),
    supplierCancelledPayments: () =>
      import('src/pages/etisalat/suppliers/SupplierCancelPayments'),
    supplierTransaction: () =>
      import('src/pages/etisalat/suppliers/SupplierSummaryTransactionsWidget'),
    supplierCards: () =>
      import('src/pages/etisalat/suppliers/SupplierUtilityAccount'),
    supplierCards2: () =>
      import('src/pages/etisalat/suppliers/SupplierSummaryDetails'),
    arrears: () => import('src/pages/etisalat/suppliers/SupplierArrearWidget'),
    underReview: () =>
      import('src/pages/etisalat/suppliers/SupplierUnderReview'),
    detailsTab: () =>
      import('src/pages/etisalat/suppliers/SupplierUnderReviewTablewidget'),
    // utility
    utilityTransactions: () =>
      import('src/pages/etisalat/utility/UtilityTranscationwidget'),
    utilityDetails: () =>
      import('src/pages/etisalat/utility/UtilityDetailsWidget'),
    utilityConnections: () =>
      import('src/pages/etisalat/utility/UtilityConnections'),
    detailstable: () =>
      import('src/pages/etisalat/suppliers/SupplierDetailsTableWidget'),
    activity: () => import('components/page/widget/common/ActivityWidget'),
    connectedApp: () => import('./common/ConnectedAppWidget'),
    // new approval
    approvalFields: () => import('./approvals/ApprovalFields'),
    approvers: () => import('./approvals/ApproversList'),
    // Community Features
    richTextPreview: () =>
      import('src/pages/community/components/RichTextPreview'),
    publishToInfo: () =>
      import('src/pages/community/components/PublishedSites'),
    attachmentsPreview: () =>
      import('src/pages/community/components/AttachmentPreview'),
    announcementSecondaryDetailsWidget: () =>
      import('src/pages/community/components/AnnouncementFields'),
    neighbourhoodDetailsWidget: () =>
      import('src/pages/community/neighbourhood/NeighbourhoodDetailsWidget'),
    dealsDetailsWidget: () =>
      import('src/pages/community/deals/DealsDetailsWidget'),
    neighbourhoodDeals: () =>
      import('src/pages/community/neighbourhood/NeighbourhoodDeals'),
    newsAndInformationWidget: () =>
      import('src/pages/community/components/NewsAndInfoFields.vue'),
    publishedAudienceInfo: () =>
      import('src/pages/community/components/AudiencePublished'),

    // Budget
    totalBudget: () =>
      import('src/pages/accounting/budget/widgets/TotalBudget.vue'),
    actualBudgetAmount: () =>
      import('src/pages/accounting/budget/widgets/ActualBudget.vue'),
    remainingBudget: () =>
      import('src/pages/accounting/budget/widgets/RemainingBudget.vue'),
    budgetPrimaryDetails: () =>
      import('src/pages/accounting/budget/widgets/BudgetDetails.vue'),
    budgetSplitUp: () =>
      import('src/pages/accounting/budget/widgets/BudgetSplitUp.vue'),
    budgetNetIncome: () =>
      import('src/pages/accounting/budget/widgets/BudgetNetIncome.vue'),
    budgetTransactions: () =>
      import('src/pages/accounting/budget/widgets/BudgetTransactions.vue'),

    // Facility
    facilityPhotos: () =>
      import('src/pages/facilitybooking/facility/widgets/FacilityPhotos'),
    facilityFeatures: () =>
      import('src/pages/facilitybooking/facility/widgets/FacilityAmenties'),
    facilitySlotInformation: () =>
      import(
        'src/pages/facilitybooking/facility/widgets/FacilitySlotInformation'
      ),
    facilitySpecialAvailability: () =>
      import('src/pages/facilitybooking/facility/widgets/FacilityAvailability'),
    bookingInfo: () =>
      import('src/pages/facilitybooking/booking/widgets/BookingInfo'),
    bookingSlotInformation: () =>
      import('src/pages/facilitybooking/booking/widgets/BookingSlotInfo'),
    bookingInternalAttendees: () =>
      import(
        'src/pages/facilitybooking/booking/widgets/BookingInternalAttendees'
      ),
    bookingExternalAttendees: () =>
      import(
        'src/pages/facilitybooking/booking/widgets/BookingExternalAttendees'
      ),
    hazards: () =>
      import('src/pages/safetyplan/component/SafetyPlanHazardList.vue'),
    hazardPrecautionsList: () =>
      import('src/pages/safetyplan/component/HazardPrecautionList.vue'),
    workorderSafetyPlan: () =>
      import('src/pages/safetyplan/component/WorkOrderSafetyPlan.vue'),
    workorderHazardPrecautions: () =>
      import('src/pages/safetyplan/component/WorkorderHazardPrecaution.vue'),
    //inspection
    qandASecondaryDetailsWidget: () =>
      import('src/pages/inspection/inspection-template/InspectionFieldDetails'),
    inspectionDetails: () =>
      import('src/pages/inspection/inspection-template/InspectionWidgetCount'),
    inspectionPageDetails: () =>
      import('src/pages/inspection/inspection-template/InspectionPageDetails'),
    inspectionTriggers: () =>
      import(
        'src/pages/inspection/inspection-template/InspectionTriggersWidget'
      ),
    inspectionQuestions: () =>
      import(
        'src/pages/inspection/inspection-template/InspectionQuestionWidget'
      ),
    inspectionResponseWidget: () =>
      import('src/pages/inspection/individual-inspection/InspectionPdfWidget'),
    inspectionHistory: () =>
      import('src/pages/inspection/inspection-template/InspectionHistory'),
    inspectionCountInsight: () =>
      import('src/pages/inspection/inspection-template/InspectionCountInsight'),
    inspectionChartchart: () =>
      import('src/pages/inspection/inspection-template/InspectionCountInsight'),

    //induction
    inductionDetails: () =>
      import('src/pages/inspection/inspection-template/InspectionWidgetCount'),
    inductionPageDetails: () =>
      import('src/pages/inspection/inspection-template/InspectionPageDetails'),
    inductionTriggers: () =>
      import(
        'src/pages/inspection/inspection-template/InspectionTriggersWidget'
      ),
    inductionQuestions: () =>
      import(
        'src/pages/inspection/inspection-template/InspectionQuestionWidget'
      ),
    inductionResponseWidget: () =>
      import('src/pages/inspection/individual-inspection/InspectionPdfWidget'),
    inductionHistory: () =>
      import('src/pages/induction/induction-template/InductionHistory'),
    inductionCountInsight: () =>
      import('src/pages/inspection/inspection-template/InspectionCountInsight'),
    inductionChartchart: () =>
      import('src/pages/inspection/inspection-template/InspectionCountInsight'),

    // survey
    surveyHistory: () =>
      import('src/pages/survey/survey-template/SurveyHistory'),

    // Workorder
    tasksCompleted: () =>
      import('src/pages/workorder/workorders/v3/widgets/TasksCompleted.vue'),
    workorderComments: () =>
      import('src/pages/workorder/workorders/v3/widgets/WorkorderComments.vue'),
    workorderAttachments: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/WorkorderAttachments.vue'
      ),
    scheduledDuration: () =>
      import('src/pages/workorder/workorders/v3/widgets/ScheduledDuration.vue'),
    actualDuration: () =>
      import('src/pages/workorder/workorders/v3/widgets/ActualDuration.vue'),
    workorderDetails: () =>
      import('src/pages/workorder/workorders/v3/widgets/WorkorderDetails.vue'),
    workDuration: () =>
      import('src/pages/workorder/workorders/v3/widgets/WorkDuration.vue'),
    slaRemainingTime: () =>
      import('src/pages/workorder/workorders/v3/widgets/SLARemainingTime.vue'),
    tenant: () =>
      import('src/pages/workorder/workorders/v3/widgets/Tenant.vue'),
    resource: () =>
      import('src/pages/workorder/workorders/v3/widgets/Resource.vue'),
    responsibility: () =>
      import('src/pages/workorder/workorders/v3/widgets/Responsibility.vue'),
    relatedRecords: () =>
      import('src/pages/workorder/workorders/v3/widgets/RelatedRecords.vue'),
    relationshipwidget: () => import('./relationship/RelationWidget.vue'),
    workorderProgress: () =>
      import('src/pages/workorder/workorders/v3/widgets/WorkorderProgress.vue'),
    workorderHistory: () =>
      import('src/pages/workorder/workorders/v3/widgets/History.vue'),
    tasks: () => import('src/pages/workorder/workorders/v3/widgets/Tasks.vue'),
    workorderSidebar: () =>
      import('src/pages/workorder/workorders/v3/widgets/WorkorderSidebar.vue'),
    precautions: () =>
      import('src/pages/workorder/workorders/v3/widgets/Precautions.vue'),
    itemsAndLabor: () =>
      import('src/pages/workorder/workorders/v3/widgets/ItemsAndLabor.vue'),
    taskMonolith: () =>
      import('src/pages/workorder/workorders/v3/widgets/TasksMonolith.vue'),
    prerequisites: () =>
      import('src/pages/workorder/workorders/v3/widgets/Prerequisites.vue'),
    totalCost: () =>
      import('src/pages/workorder/workorders/v3/widgets/TotalCost.vue'),
    workorderDescription: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/WorkorderDescription.vue'
      ),
    quotation: () =>
      import('src/pages/workorder/workorders/v3/widgets/Quotation.vue'),

    inventoryItems: () =>
      import('src/pages/workorder/workorders/v3/widgets/InventoryItems.vue'),
    actualsCost: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/actuals/ActualsCost.vue'
      ),
    workorderitems: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/actuals/WorkorderActualsItems.vue'
      ),
    workordertools: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/actuals/WorkorderActualsTools.vue'
      ),
    workorderservice: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/actuals/WorkorderActualsServices.vue'
      ),
    workorderlabour: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/actuals/WorkOrderActualsLabour.vue'
      ),
    jobplanitems: () =>
      import('src/pages/workorder/jobplan/plans/JobPlanItems.vue'),
    jobplantools: () =>
      import('src/pages/workorder/jobplan/plans/JobPlanTools.vue'),
    jobplanservice: () =>
      import('src/pages/workorder/jobplan/plans/JobPlanService.vue'),
    jobplanlabours: () =>
      import('src/pages/workorder/jobplan/plans/JobPlanLabour.vue'),
    inventoryServices: () =>
      import('src/pages/workorder/workorders/v3/widgets/InventoryServices.vue'),

    inventoryLabor: () =>
      import('src/pages/workorder/workorders/v3/widgets/InventoryLabor.vue'),

    inventoryTools: () =>
      import('src/pages/workorder/workorders/v3/widgets/InventoryTools.vue'),

    inventoryOverallCost: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/InventoryOverallCost.vue'
      ),
    failurereports: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/failureClass/FailureReport.vue'
      ),
    //Job Plan
    jobPlanTasks: () =>
      import('src/pages/workorder/jobplan/widgets/JobPlanSectionLayout.vue'),

    //visit & invite
    visitsPrimaryFields: () =>
      import('pages/visitors/visits/VisitsPrimaryFields.vue'),
    invitesPrimaryFields: () =>
      import('pages/visitors/invites/InvitesPrimaryFields.vue'),

    //TO BE REMOVED
    portalWorkrequestFormDetails: () =>
      import('./common/field-details/FieldDetailsLayout.vue'),
    portalWorkPermit: () =>
      import('./common/field-details/FieldDetailsLayout.vue'),
    tenantPortalVisitsDetails: () =>
      import('./portal/TenantPortalVisitsDetails'),

    //Moves
    combinedRelatedListWidget: () =>
      import('./common/CombinedRelatedListWidget.vue'),

    // inventory request
    // inventoryrequestcard1: () =>
    //   import(
    //     'pages/Inventory/InventoryRequest/component/inventoryrequestcard1'
    //   ),
    // inventoryrequestcard2: () =>
    //   import(
    //     'pages/Inventory/InventoryRequest/component/inventoryrequestcard2'
    //   ),
    // inventoryrequestcard3: () =>
    //   import(
    //     'pages/Inventory/InventoryRequest/component/inventoryrequestcard3'
    //   ),
    bookingRelatedListWidget: () =>
      import(
        'pages/facilitybooking/booking/widgets/BookingRelatedListWidget.vue'
      ),

    //Service-inventory
    servicevendors: () => import('pages/Inventory/Service/ServiceVendor.vue'),
    //transfer request
    transferrequestlineitems: () =>
      import('pages/Inventory/TransferRequest/component/TrLineItems.vue'),
    descriptioncard: () => import('components/page/widget/DescriptionCard.vue'),
    // transfer request shipment
    transferrequestshipmentreceivables: () =>
      import(
        'pages/Inventory/TransferRequest/component/TrShipmentReceivables.vue'
      ),
    //rfq
    rfqPreview: () => import('src/pages/purchase/rfq/RfqPreviewParent.vue'),
    rfqVendor: () => import('src/pages/purchase/rfq/components/RfqVendor.vue'),
    vendorQuotesLineItems: () =>
      import(
        'src/pages/purchase/vendorQuotes/components/VendorQuotesLineItemsTabs.vue'
      ),
    vendorQuotesRfqSummaryWidget: () =>
      import(
        'src/pages/purchase/vendorQuotes/components/VendorQuotesRfqDetails.vue'
      ),
    inventoryrequestlineitems: () =>
      import(
        'pages/Inventory/InventoryRequest/component/InventoryRequestLineItems.vue'
      ),

    storeroomcard1: () =>
      import('pages/Inventory/Storerooms/component/StoreRoomCard1.vue'),
    storeroomcard2: () =>
      import('pages/Inventory/Storerooms/component/StoreRoomCard2.vue'),
    storeroomcard3: () =>
      import('pages/Inventory/Storerooms/component/StoreRoomCard3.vue'),
    issuesReturns: () =>
      import('pages/Inventory/Storerooms/component/IssuesAndReturns.vue'),
    itemstools: () =>
      import('pages/Inventory/Storerooms/component/ItemsTools.vue'),
    servingsites: () =>
      import('pages/Inventory/Storerooms/component/ServingSites.vue'),
    storeroomphotos: () =>
      import('pages/Inventory/Storerooms/component/StoreRoomPhotos.vue'),
    storeroom: () =>
      import('pages/Inventory/ItemTypes/components/StoreroomTable.vue'),
    transactions: () =>
      import('pages/Inventory/ItemTypes/components/TransactionsTable.vue'),
    // metrics widgets
    statetransitiontimelog: () =>
      import('components/page/widget/common/MetricsWorkLog'),
    surveyResponseWidget: () => import('pages/setup/survey/ViewSurveyResults'),
    inventorycard: () =>
      import('pages/Inventory/component/InventorySummaryCard.vue'),
    tenantdetailoverview: () =>
      import('pages/tenants/components/TenantDetailsCard.vue'),
    tenantdetailcontact: () =>
      import('pages/tenants/components/TenantPrimaryContactDetails.vue'),
    tenantworkorders: () =>
      import('pages/tenants/components/TenantWorkorders.vue'),
    tenantbookings: () => import('pages/tenants/components/TenantBookings.vue'),
    tenantdescription: () =>
      import('pages/tenants/components/TenantDescription.vue'),
    associatedterms: () =>
      import('pages/purchase/component/AssociatedTermsWidget.vue'),
    purchaseditems: () =>
      import('pages/Inventory/Items/components/PurchasedItems.vue'),
    purchasedtools: () =>
      import('pages/Inventory/Items/components/PurchasedTools.vue'),
    transactionsitem: () =>
      import('pages/Inventory/Items/components/TransactionsItem.vue'),
    transactionstool: () =>
      import('pages/Inventory/Items/components/TransactionsTool.vue'),
    tenantunitoverview: () =>
      import('pages/tenantunit/components/TenantUnitOverview.vue'),
    tenantunittenant: () =>
      import('pages/tenantunit/components/TenantUnitTenant.vue'),
    tenantunitworkorder: () =>
      import('pages/tenantunit/components/TenantUnitWorkorder.vue'),
    tenantunitlocation: () =>
      import('pages/tenantunit/components/TenantUnitLocation.vue'),
    tenantunitphoto: () =>
      import('pages/tenantunit/components/TenantUnitPhoto.vue'),
    tenantunitspecialwidget: () =>
      import('pages/tenantunit/components/TenantUnitTenantHistory.vue'),
    //purchaseOrder receivable
    receivablereceipts: () =>
      import('pages/purchase/rv/ReceivableReceiptsWidget.vue'),
    // Planned Inventory
    plannedInventoryItems: () =>
      import(
        'src/pages/workorder/jobplan/plannedInventory/PlannedInventoryItems.vue'
      ),
    plannedInventoryTools: () =>
      import(
        'src/pages/workorder/jobplan/plannedInventory/PlannedInventoryTools.vue'
      ),
    plannedInventoryServices: () =>
      import(
        'src/pages/workorder/jobplan/plannedInventory/PlannedInventoryServices.vue'
      ),
    plannedInventoryOverallCost: () =>
      import(
        'src/pages/workorder/jobplan/plannedInventory/PlannedInventoryOverallCost.vue'
      ),
    //Failure Class
    failureclassdetails: () =>
      import(
        'src/pages/assets/failureclass/failureclassPage/FailureClassDetails.vue'
      ),
    failureclassassociatedproblems: () =>
      import(
        'src/pages/assets/failureclass/failureclassPage/FailureClassAssociatedProblems.vue'
      ),
    // Planned Maintenance
    preventiveMaintenanceDetails: () =>
      import('src/pages/workorder/pm/summary/PMFieldDetails.vue'),
    pmPlannerDetails: () =>
      import('src/pages/workorder/pm/summary/PMPlannerDetails.vue'),
    pmSchedulerDetails: () =>
      import('src/pages/workorder/pm/summary/timeline-view/PmTimelineView.vue'),

    // Space Booking
    spaceBookingFullSummary: () =>
      import('src/pages/spacebooking/SpaceBookingFullSummary.vue'),
    spaceBookingCardDetails: () =>
      import('src/pages/spacebooking/SpaceBookingCard.vue'),
    spaceBookingSecondaryDetails: () =>
      import('src/components/SpaceBookingSecondaryDetails.vue'),
    Classification: () =>
      import('pageWidgets/specification/ClassificationSpecificationWidget.vue'),
    //wo plans
    plans: () =>
      import(
        '/src/pages/workorder/workorders/v3/widgets/plans/WorkOrderInventoryPlan.vue'
      ),
    plansCost: () =>
      import(
        '/src/pages/workorder/workorders/v3/widgets/plans/WorkOrderInventoryPlansCost.vue'
      ),
    multiResource: () => import('src/pages/workorder/MultiResource.vue'),
    shiftDescription: () =>
      import('src/components/page/widget/shift/ShiftDescription.vue'),
    shiftBreaks: () =>
      import('src/components/page/widget/shift/ShiftBreaks.vue'),
    shiftAssociatedEmployees: () =>
      import('src/components/page/widget/shift/ShiftAssociatedEmployees.vue'),
    shiftStartTime: () =>
      import('src/components/page/widget/shift/ShiftStartTime.vue'),
    shiftEndTime: () =>
      import('src/components/page/widget/shift/ShiftEndTime.vue'),
    shiftWeeklyOff: () =>
      import('src/components/page/widget/shift/ShiftWeeklyOff.vue'),
  },
  data() {
    return {
      eventBus,
    }
  },
  computed: {
    componentName() {
      let moduleWidgetKeys = ['LIST', 'COUNT']

      if (moduleWidgetKeys.includes(this.widget.widgetTypeEnum)) {
        if (
          [
            'plannedWorkorder',
            'unPlannedWorkorder',
            'readings',
            'baselineEquation',
            'assetMovements',
            'contracts',
            'itemTransactions',
            'toolTransactions',
            'siteBuildings',
            'newSiteBuildings',
            'safetyPlanPrecautions',
            'hazardPrecautionsList',
            'precautionHazardsList',
            'siteSpaces',
            'newSiteSpaces',
            'buildingSpaces',
            'floorSpaces',
            'relatedSubSpaces',
            'spaceReadings',
            'moduleReadings',
            'utilityConnections',
            'relatedReadings',
            'commandReadings',
          ].includes(this.widget.name)
        ) {
          return this.widget.name
        }
        return this.widget.widgetParams.module + this.widget.widgetTypeObj.name
      } else if (['CARD', 'CHART'].includes(this.widget.widgetTypeEnum)) {
        return this.widget.widgetParams.type + this.widget.widgetTypeObj.name
      } else {
        return this.widget.widgetTypeObj.name
      }
    },
    recordId() {
      return (
        this.$getProperty(this.$attrs, 'details.id') ||
        this.$getProperty(this.$attrs, 'id')
      )
    },
  },
}
</script>
