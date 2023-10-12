package com.facilio.apiv3;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.alarms.sensor.commands.FetchSensorRuleSummaryCommand;
import com.facilio.alarms.sensor.commands.PrepareSensorRuleForUpdateCommand;
import com.facilio.alarms.sensor.commands.UpdateSensorRuleCommand;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.agent.commands.AgentLoggerSummaryAfterFetchCommand;
import com.facilio.agent.commands.FetchAgentDataLoggerSupplementsCommand;
import com.facilio.agent.commands.FetchAgentDataSummarySupplementsCommand;
import com.facilio.attribute.chain.ClassificationAttributeChain;
import com.facilio.attribute.command.BeforeSaveClassificationAttributeCommand;
import com.facilio.attribute.command.BeforeUpdateClassificationAttributeCommand;
import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.backgroundactivity.commands.FetchBackgroundActivitySupplementsCommand;
import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.context.*;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmContext;
import com.facilio.bmsconsole.context.WorkflowRuleLogContext;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.LookUpPrimaryFieldHandlingCommandV3;
import com.facilio.bmsconsoleV3.commands.*;
import com.facilio.bmsconsoleV3.commands.Audience.ValidateAudienceSharingCommandV3;
import com.facilio.bmsconsoleV3.commands.Calendar.CalendarSlotCreateCommand;
import com.facilio.bmsconsoleV3.commands.asset.*;
import com.facilio.bmsconsoleV3.commands.assetCategory.SetAssetCategoryModuleCommandV3;
import com.facilio.bmsconsoleV3.commands.assetdepreciationrel.CheckAssetDepreciationExistingCommand;
import com.facilio.bmsconsoleV3.commands.assetdepreciationrel.DeleteAssetDepreciationCalCommand;
import com.facilio.bmsconsoleV3.commands.assetdepreciationrel.SaveDepreciationLastCalCommand;
import com.facilio.bmsconsoleV3.commands.assetdepreciationrel.ValidateAssetDepreciationRelCommand;
import com.facilio.bmsconsoleV3.commands.basespace.DeleteBasespaceChildrenCommandV3;
import com.facilio.bmsconsoleV3.commands.basespace.FetchBasespaceChildrenCountCommandV3;
import com.facilio.bmsconsoleV3.commands.budget.*;
import com.facilio.bmsconsoleV3.commands.ValidateDeleteChartOfAccountCommand;
import com.facilio.bmsconsoleV3.commands.building.AddOrUpdateBuildingLocation;
import com.facilio.bmsconsoleV3.commands.building.BuildingFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.building.CreateBuildingAfterSave;
import com.facilio.bmsconsoleV3.commands.building.SetBuildingRelatedContextCommand;
import com.facilio.bmsconsoleV3.commands.client.DisassociateClientFromSiteCommand;
import com.facilio.bmsconsoleV3.commands.client.*;
import com.facilio.bmsconsoleV3.commands.clientcontact.DeleteClientContactPeopleUsersCommandV3;
import com.facilio.bmsconsoleV3.commands.clientcontact.LoadClientContactLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.FillAudienceSharingInfoCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.LoadAudienceLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments.FillAdminDocumentsSharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments.LoadAdminDocumentsLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.AnnouncementFillDetailsCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.DeleteChildAnnouncementsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.LoadAnnouncementLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory.ContactDirectoryFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory.FillContactDirectorySharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.DealsAndOffersFillLookupFields;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.FillDealsAndOffersSharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.FillNeighbourhoodSharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.NeighbourhoodFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation.FillNewsAndInformationDetailsCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation.FillNewsRelatedModuleDataInListCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation.LoadNewsAndInformationLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.controlActions.CallToCommandGenerationCommand;
import com.facilio.bmsconsoleV3.commands.controlActions.CallToControlActionGenerationCommand;
import com.facilio.bmsconsoleV3.commands.decommission.DecommissionLogLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.decommission.DecommissionPicklistCheckCommand;
import com.facilio.bmsconsoleV3.commands.employee.LoadEmployeeLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.facility.*;
import com.facilio.bmsconsoleV3.commands.failureclass.*;
import com.facilio.bmsconsoleV3.commands.floor.CreateFloorAfterSave;
import com.facilio.bmsconsoleV3.commands.floor.FloorFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.floor.SetFloorRelatedContextCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.*;
import com.facilio.bmsconsoleV3.commands.imap.UpdateLatestMessageUIDCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.LoadInsuranceLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.ValidateDateCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.*;
import com.facilio.bmsconsoleV3.commands.item.*;
import com.facilio.bmsconsoleV3.commands.itemtypes.LoadItemTypesLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.itemtypes.ValidateItemTypeCommandV3;
import com.facilio.bmsconsoleV3.commands.jobPlanInventory.*;
import com.facilio.bmsconsoleV3.commands.jobplan.FetchJobPlanLookupCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.ValidationForJobPlanCategory;
import com.facilio.bmsconsoleV3.commands.jobplanSection.AddCriteriaForJobPlanSectionInputOptionsBeforeFetchCommand;
import com.facilio.bmsconsoleV3.commands.jobplanTask.AddCriteriaForJobPlanTaskInputOptionsBeforeFetchCommand;
import com.facilio.bmsconsoleV3.commands.labour.*;
import com.facilio.bmsconsoleV3.commands.moves.UpdateEmployeeInDesksCommandV3;
import com.facilio.bmsconsoleV3.commands.moves.ValidateMovesCommand;
import com.facilio.bmsconsoleV3.commands.people.*;
import com.facilio.bmsconsoleV3.commands.peoplegroup.FetchPeopleGroupMembersCommand;
import com.facilio.bmsconsoleV3.commands.purchaseorder.DeleteReceivableByPOIdV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.FetchPODetailsCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.LoadAssociatedTermsLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.LoadPOSummaryLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaserequest.FetchPurchaseRequestDetailsCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaserequest.LoadPurchaseRequestSummaryLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.*;
import com.facilio.bmsconsoleV3.commands.receipts.SetReceiptTimeAndLocalIdCommand;
import com.facilio.bmsconsoleV3.commands.receivable.LoadReceivableLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.receivable.SetPOLineItemCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.LoadRequestForQuotationLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.UpdateRfqIdInPrCommandV3;
import com.facilio.bmsconsoleV3.commands.safetyplan.*;
import com.facilio.bmsconsoleV3.commands.service.GetServiceVendorListCommandV3;
import com.facilio.bmsconsoleV3.commands.service.UpdateStatusCommandV3;
import com.facilio.bmsconsoleV3.commands.service.UpdateVendorV3;
import com.facilio.bmsconsoleV3.commands.servicerequest.LoadServiceRequestLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.site.*;
import com.facilio.bmsconsoleV3.commands.space.*;
import com.facilio.bmsconsoleV3.commands.storeroom.LoadStoreRoomLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.FillTenantsLookupCommand;
import com.facilio.bmsconsoleV3.commands.tenant.ValidateTenantSpaceCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.LoadTenantcontactLookupsCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantspaces.LoadTenantSpacesLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantunit.AddSpaceCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantunit.CheckForTenantBeforeDeletionCommand;
import com.facilio.bmsconsoleV3.commands.tenantunit.LoadTenantUnitLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.termsandconditions.CheckForPublishedCommand;
import com.facilio.bmsconsoleV3.commands.tool.*;
import com.facilio.bmsconsoleV3.commands.tooltypes.LoadToolTypesLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.tooltypes.SetToolRateCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.*;
import com.facilio.bmsconsoleV3.commands.vendor.AddOrUpdateLocationForVendorCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.LoadVendorLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorQuotes.*;
import com.facilio.bmsconsoleV3.commands.vendorcontact.LoadVendorContactLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.LoadVisitorLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.*;
import com.facilio.bmsconsoleV3.commands.visitorlogging.GetTriggerForRecurringLogCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.LoadVisitorLoggingLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.watchlist.CheckForExisitingWatchlistRecordsCommandV3;
import com.facilio.bmsconsoleV3.commands.watchlist.GetLogsForWatchListCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.*;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.*;
import com.facilio.bmsconsoleV3.commands.workorder.ValidateWorkOrderLabourPlanCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.*;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.asset.*;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.context.budget.AccountTypeContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.bmsconsoleV3.context.budget.ChartOfAccountContext;
import com.facilio.bmsconsoleV3.context.calendar.*;
import com.facilio.bmsconsoleV3.context.communityfeatures.*;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3ExternalAttendeeContext;
import com.facilio.bmsconsoleV3.context.failurecode.*;
import com.facilio.bmsconsoleV3.context.floorplan.*;
import com.facilio.bmsconsoleV3.context.inspection.InspectionPriorityContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.*;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.bmsconsoleV3.context.labour.LabourCraftAndSkillContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PoAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3ReceiptContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3ReceivableContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.PrAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.context.safetyplans.*;
import com.facilio.bmsconsoleV3.context.shift.Break;
import com.facilio.bmsconsoleV3.context.spacebooking.FetchCriteriaObjectCommand;
import com.facilio.bmsconsoleV3.context.spacebooking.SpaceBookingSupplementsCommand;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingPolicyContext;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.context.spacebooking.*;
import com.facilio.bmsconsoleV3.context.tasks.SectionInputOptionsContext;
import com.facilio.bmsconsoleV3.context.tasks.TaskInputOptionsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourPlanContext;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistCategoryContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistContext;
import com.facilio.bmsconsoleV3.interfaces.customfields.*;
import com.facilio.bmsconsoleV3.interfaces.customfields.modules.WorkOrderModuleCustomFieldCount;
import com.facilio.classification.chain.ClassificationChain;
import com.facilio.classification.command.BeforeSaveClassificationCommand;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.assetcatergoryfeature.commands.AddAssetCategoryFeatureStatusCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.*;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.elasticsearch.command.PushDataToESCommand;
import com.facilio.faults.LoadOccurrenceForAlarmCommand;
import com.facilio.faults.UpdateOccurrenceCommand;
import com.facilio.faults.alarmoccurrence.LoadSupplementsForAlarmOccurrenceCommand;
import com.facilio.faults.baseevent.LoadSupplementsForBaseEventCommand;
import com.facilio.faults.bmsalarm.LoadSupplementsForBMSAlarmCommand;
import com.facilio.faults.newreadingalarm.HandleV3AlarmListLookupCommand;
import com.facilio.faults.newreadingalarm.LoadSupplementsForFaultsCommand;
import com.facilio.faults.sensoralarm.LoadSupplementsForSensorAlarmCommand;
import com.facilio.faults.AfterDeleteAlarmCommand;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.fsm.commands.LoadServiceTaskLookupCommandV3;
import com.facilio.fsm.commands.LoadWorkTypeLineItemsCommandV3;
import com.facilio.fsm.commands.LoadWorkTypeLookupsCommandV3;
import com.facilio.fsm.commands.actuals.*;
import com.facilio.fsm.commands.people.FetchLocationHistorySupplements;
import com.facilio.fsm.commands.people.FetchPeopleSkillLevelSupplementsCommand;
import com.facilio.fsm.commands.people.UpdatePeopleLocationFromTripCommand;
import com.facilio.fsm.commands.people.UpdatePeopleLocationHistoryCommand;
import com.facilio.fsm.commands.plans.*;
import com.facilio.fsm.commands.serviceAppointment.AddTimeSheetServiceAppointmentActivity;
import com.facilio.fsm.commands.serviceAppointment.AddTripServiceAppointmentActivity;
import com.facilio.fsm.commands.serviceAppointment.FetchServiceAppointmentSupplementsCommand;
import com.facilio.fsm.commands.serviceOrders.AddOrUpdateServiceOrderCostCommand;
import com.facilio.fsm.commands.serviceOrders.LoadSupplementsForSOCommand;
import com.facilio.fsm.commands.serviceOrders.SetServiceTaskCommandV3;
import com.facilio.fsm.commands.serviceTasks.DeleteSATaskRelCommand;
import com.facilio.fsm.commands.serviceTasks.LoadTaskPlansCommandV3;
import com.facilio.fsm.commands.timeOff.FetchTimeOffSupplementsCommand;
import com.facilio.fsm.commands.timeSheet.CheckRecordLockForTimeSheetCommand;
import com.facilio.fsm.commands.timeSheet.FetchTimeSheetSupplementsCommand;
import com.facilio.fsm.commands.trip.FetchTripSupplementsCommand;
import com.facilio.fsm.context.*;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.commands.MailReadOnlyChainFactory;
import com.facilio.mailtracking.commands.OutgoingRecipientLoadSupplementsCommand;
import com.facilio.mailtracking.commands.UpdateMailRecordsModuleNameCommand;
import com.facilio.mailtracking.context.V3OutgoingMailAttachmentContext;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.mailtracking.context.V3OutgoingRecipientContext;
import com.facilio.modules.FacilioModule;
import com.facilio.readingkpi.commands.delete.DeleteKpiLoggersCommand;
import com.facilio.readingkpi.commands.list.LoadSupplementsForKpiLogger;
import com.facilio.readingkpi.commands.delete.ReadingKpiPostDeleteCommand;
import com.facilio.readingkpi.commands.list.LoadSupplementsForReadingKPICommand;
import com.facilio.readingkpi.commands.update.NullFieldIdAndModuleIdForDynamicKpi;
import com.facilio.readingkpi.context.KpiLoggerContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.readingkpi.context.ReadingKpiLogsContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.context.ReadingRuleLogsContext;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.faultimpact.FaultImpactNameSpaceFieldContext;
import com.facilio.relation.context.RelationDataContext;
import com.facilio.remotemonitoring.commands.*;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.util.FacilioUtil;
import com.facilio.utility.commands.*;
import com.facilio.utility.context.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.annotation.ModuleType;
import com.facilio.v3.commands.ConstructAddCustomActivityCommandV3;
import com.facilio.v3.commands.ConstructUpdateCustomActivityCommandV3;
import com.facilio.v3.commands.FetchChangeSetForCustomActivityCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.workflowlog.context.WorkflowLogContext;

import com.facilio.workflowlog.context.WorkflowVersionHistoryContext;


import org.apache.commons.chain.Command;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.facilio.bmsconsole.commands.FacilioChainFactory.getSpaceReadingsChain;

@Config
public class APIv3Config {
    //
    // @Module("_custom_test")
    // // we are returning a lambda so as to generate new V3Config object each
    // // and every time a request happens
    // public static Supplier<V3Config> customTest() {
    // return () -> new V3Config(CustomModuleData.class)
    // .create()
    // .init(new DefaultInit())
    // .beforeSave(new SampleBeforeSaveCommand())
    // .afterSave(new SampleAfterSaveCommand())
    // .afterTransaction(new SampleAfterTransactionCommand())
    // .summary()
    // .afterFetch(new SampleAfterFetchCommand())
    // .list()
    // .beforeFetch(new SampleBeforeFetchCommand())
    // .afterFetch(new SampleAfterFetchCommand())
    // .showStateFlowList()
    // .update()
    // .init(new DefaultInit())
    // .beforeSave(new SampleBeforeSaveCommand())
    // .afterSave(new SampleAfterSaveCommand())
    // .afterTransaction(new SampleAfterTransactionCommand())
    // .delete()
    // .beforeDelete(new SampleBeforeDeleteCommand())
    // .afterDelete(new SampleAfterDeleteCommand())
    // .afterTransaction(new SampleAfterTransactionCommand())
    // .build();
    // }

    @Module(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
    public static Supplier<V3Config> getEmailFromAddressCRUD() {
        return () -> new V3Config(EmailFromAddress.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getEmailFromAddressBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getEmailFromAddressAfterSaveChain())
                .update()
                .beforeSave(new EmailFromAddressValidateCommand())
                .build();
    }

    @ModuleType(type = FacilioModule.ModuleType.RELATION_DATA)
    public static Supplier<V3Config> getRelationHandler() {
        return () -> new V3Config(RelationDataContext.class, null)
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getRelationDataListChain())
                .afterFetch(ReadOnlyChainFactoryV3.updateRelationSupplementsChain())
                .create()
                .beforeSave(TransactionChainFactoryV3.getRelationDataAddBeforeSaveChain())
                .update()
                .beforeSave(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        throw new IllegalArgumentException("Update is not supported");
                    }
                })
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getRelationDataBeforeDeleteChain())
                .build();
    }

    @ModuleType(type = FacilioModule.ModuleType.NOTES)
    public static Supplier<V3Config> getNotesHandler() {
        return () -> new V3Config(NoteContext.class, null)
                .list()
                .beforeFetch(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        long parentId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, "parentId"));
                        Condition condition = CriteriaAPI.getCondition("PARENT_ID", "parentId",
                                String.valueOf(parentId), NumberOperators.EQUALS);
                        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
                        return false;
                    }
                })
                .build();
    }

    @Module("assetdepreciation")
    public static Supplier<V3Config> getAssetDepreciation() {
        return () -> new V3Config(AssetDepreciationContext.class, null)
                .create()
                .beforeSave(new ValidateAssetDepreciationCommand())
                .summary()
                .afterFetch(new AssetDepreciationFetchAssetDetailsCommand())
                .build();
    }

    @Module("quote")
    public static Supplier<V3Config> getQuotation() {
        return () -> new V3Config(QuotationContext.class, new ModuleCustomFieldCount30_BS2())

                .create()
                .beforeSave(TransactionChainFactoryV3.getQuotationBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getQuotationAfterSaveChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTE_ACTIVITY))

                .update()
                .beforeSave(TransactionChainFactoryV3.getQuotationBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getQuotationAfterUpdateChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTE_ACTIVITY))

                .summary()
                .beforeFetch(new QuotationFillLookupFields())
                .afterFetch(ReadOnlyChainFactoryV3.getQuoteAfterfetchSummayChain())

                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getQuoteBeforeFetchChain())

                .build();
    }

    @Module(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME)
    public static Supplier<V3Config> getEmailConversationThreadingCRUD() {
        return () -> new V3Config(EmailConversationThreadingContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getAddEmailConversationThreadingBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getAddEmailConversationThreadingAfterSaveChain())
                .afterTransaction(new AddActivitiesCommandV3())
                .update()
                .afterSave(new ExecuteWorkflowInRelatedModuleForEmailConversationThreadingCommand())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getEmailConversationThreadingBeforeListChain())
                .afterFetch(TransactionChainFactoryV3.getEmailConversationThreadingAfterListChain())
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getEmailConversationThreadingBeforeListChain())
                .afterFetch(TransactionChainFactoryV3.getEmailConversationThreadingAfterListChain())
                .build();
    }

    @Module(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME)
    public static Supplier<V3Config> getEmailToModuleDataCRUD() {
        return () -> new V3Config(EmailToModuleDataContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getAddEmailToModuleDataBeforeSaveChain())
                .build();
    }

    @Module(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME)
    public static Supplier<V3Config> getScheduleCRUD() {
        return () -> new V3Config(ControlScheduleContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getAddControlScheduleBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getAddControlScheduleAfterSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getUpdateControlScheduleBeforeSaveCommandChain())
                .afterSave(TransactionChainFactoryV3.getUpdateControlScheduleAfterSaveCommandChain())
                .summary()
                .beforeFetch(new ControlScheduleSuplimentFieldSupplyCommand())
                .afterFetch(new ControlScheduleAfterFetchCommand())
                .list()
                .beforeFetch(new ControlScheduleSuplimentFieldSupplyCommand())
                .afterFetch(new ControlScheduleAfterFetchCommand())
                .build();
    }

    @Module(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME)
    public static Supplier<V3Config> getScheduleExceptionCRUD() {
        return () -> new V3Config(ControlScheduleExceptionContext.class, null)
                .create()
                .beforeSave(new ControlScheduleExceptionBeforeSaveCommand())
                .afterSave(TransactionChainFactoryV3.getAddControlScheduleExceptionAfterSaveChain())
                .update()
                .beforeSave(new ControlScheduleExceptionBeforeSaveCommand())
                .afterSave(TransactionChainFactoryV3.getUpdateControlScheduleExceptionAfterSaveChain())
                .delete()
                .afterDelete(new PlanControlScheduleExceptionSlotCommand())
                .build();
    }

    @Module(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME)
    public static Supplier<V3Config> getScheduleExceptionTenantCRUD() {
        return () -> new V3Config(ControlScheduleExceptionTenantContext.class, null) // to be changed to
                // ControlScheduleExceptionTenantContext
                .create()
                .beforeSave(TransactionChainFactoryV3.getAddControlScheduleExceptionBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getAddControlScheduleExceptionAfterSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getAddControlScheduleExceptionBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getUpdateControlScheduleExceptionAfterSaveChain())
                .delete()
                .afterDelete(new PlanControlScheduleExceptionSlotCommand())
                .summary()
                .beforeFetch(new ControlScheduleExceptionTenantSupplementFieldSupplyCommand())
                .list()
                .beforeFetch(new ControlScheduleExceptionTenantSupplementFieldSupplyCommand())
                .build();
    }

    @Module(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME)
    public static Supplier<V3Config> getGroupRoutineCRUD() {
        return () -> new V3Config(ControlGroupRoutineContext.class, null)
                .create()
                .afterSave(new AddControlGroupRoutineSectionAndFieldCommand())
                .update()
                .afterSave(TransactionChainFactoryV3.getUpdateControlGroupRoutineChain())
                .build();
    }

    @Module(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME)
    public static Supplier<V3Config> getGroupCRUD() {
        return () -> new V3Config(ControlGroupContext.class, null)
                .create()
                .afterSave(TransactionChainFactoryV3.getAddControlGroupAfterSaveChain())
                .update()
                .afterSave(TransactionChainFactoryV3.getUpdateControlGroupAfterSaveChain())
                .summary()
                .afterFetch(new GetControlGroupCommand())
                .list()
                .beforeFetch(new ControlGroupSuplimentFieldSupplyCommand())
                .build();
    }

    @Module(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME)
    public static Supplier<V3Config> getGroupTennatCRUD() {
        return () -> new V3Config(ControlGroupTenentContext.class, null)
                .update()
                .afterSave(TransactionChainFactoryV3.getUpdateControlGroupAfterSaveChain())
                .summary()
                .afterFetch(new GetControlGroupCommand())
                .list()
                .beforeFetch(new ControlGroupSuplimentFieldSupplyCommand())
                .build();
    }

    @Module(ControlScheduleUtil.CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME)
    public static Supplier<V3Config> getScheduleTennatCRUD() {
        return () -> new V3Config(ControlScheduleTenantContext.class, null)
                .summary()
                .afterFetch(new ControlScheduleAfterFetchCommand())
                .build();
    }

    @Module(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME)
    public static Supplier<V3Config> getControlSlotCRUD() {
        return () -> new V3Config(ControlScheduleSlot.class, null)
                .update()
                .afterSave(TransactionChainFactoryV3.getUpdateOrDeleteControlGroupSlotAfterSaveChain())
                .delete()
                .beforeDelete(new FetchControlGroupSlotObjectForDelete())
                .afterDelete(TransactionChainFactoryV3.getUpdateOrDeleteControlGroupSlotAfterSaveChain())
                .build();
    }

    @Module(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE)
    public static Supplier<V3Config> getControlCommandCRUD() {
        return () -> new V3Config(ControlActionCommandContext.class, null)
                .summary()
                .beforeFetch(new ControlActionCommandSuppimentFieldSupplyCommand())
                .afterFetch(new ControlActionCommandAfterFetchCommand())
                .list()
                .beforeFetch(new ControlActionCommandSuppimentFieldSupplyCommand())
                .afterFetch(new ControlActionCommandAfterFetchCommand())
                .build();
    }

    @Module("tax")
    public static Supplier<V3Config> getTax() {
        return () -> new V3Config(TaxContext.class, null)

                .create()
                .beforeSave(new TaxValidationCommand())
                .afterSave(new InsertTaxGroupsCommand())

                .summary()
                .afterFetch(new TaxFillDetailsCommand())

                .list()
                .afterFetch(new TaxFillDetailsCommand())

                .build();
        /*
         * Using add handler for update too send with id for Updating
         * Bulk Add is Supported
         * Only single update is Supported (For Bulk update maintain old record id vs
         * new record id map)
         *
         * Tax Group Update => Sets current group as inactive and creates a new group
         *
         * Tax Individual Update => 1. Current tax is set as Inactive,
         * 2. List of All Groups with the updating tax as child will be set as inactive
         * and new Tax
         * groups will be added with new tax id
         */
    }

    @Module("serviceRequest")
    public static Supplier<V3Config> getServiceRequest() {
        return () -> new V3Config(V3ServiceRequestContext.class, new ModuleCustomFieldCount50())
                .create()
                .beforeSave(TransactionChainFactoryV3.getServiceRequestBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getServiceRequestAfterSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getServiceRequestBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getServiceRequestAfterUpdateChain())
                .afterTransaction(new AddActivitiesCommandV3())
                .delete()
                .list()
                .beforeFetch(new LoadServiceRequestLookupCommandV3())
                .showStateFlowList()
                .summary()
                .beforeFetch(new LoadServiceRequestLookupCommandV3())
                .afterFetch(new FetchEmailToModuleDataReccordCommand())
                .build();
    }

    @Module("tenantunit")
    public static Supplier<V3Config> getTenantUnit() {
        return () -> new V3Config(V3TenantUnitSpaceContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddOrUpdateSpaceLocation(), new AddSpaceCommandV3())
                .afterSave(new CreateSpaceAfterSave(), TransactionChainFactoryV3.getTenantUnitAfterSaveChain())
                .update()
                .beforeSave(new AddOrUpdateSpaceLocation(), new MarkTenantunitAsVacantCommandV3())
                .list()
                .beforeFetch(new LoadTenantUnitLookupCommandV3())
                .summary().beforeFetch(new LoadTenantUnitLookupCommandV3())
                .delete()
                .beforeDelete(new CheckForTenantBeforeDeletionCommand())
                .build();
    }

    @Module("tenantspaces")
    public static Supplier<V3Config> getTenantSpaces() {
        return () -> new V3Config(V3TenantUnitSpaceContext.class, null)
                .create()
                .update()
                .list()
                .beforeFetch(new LoadTenantSpacesLookupCommandV3())
                .summary()
                .beforeFetch(new LoadTenantSpacesLookupCommandV3())
                .build();
    }

    @Module("failurecode")
    public static Supplier<V3Config> getFailureCode() {
        return () -> new V3Config(V3FailureCodeContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .list()
                .beforeFetch(new CheckForDuplicateFailureCode())
                .summary()
                .delete()
                .build();
    }
    @Module("failureclass")
    public static Supplier<V3Config> getFailureClass() {
        return () -> new V3Config(V3FailureClassContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .beforeSave()
                .list()
                .beforeFetch(new FetchFailureClassSupplements())
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getFetchFailureClassSupplements())
                .afterFetch(new FetchFailureClassSubModules())
                .delete()
                .build();
    }

    @Module("failurecodeproblems")
    public static Supplier<V3Config> getFailureCodeProblems() {
        return () -> new V3Config(V3FailureCodeProblemsContext.class, new ModuleCustomFieldCount15())
                .list()
                .beforeFetch(new FetchFailureCodeSupplements())
                .delete()
                .beforeDelete(new CheckForAssociatedModulesBeforeDeletion())
                .build();
    }

    @Module("failurecodecauses")
    public static Supplier<V3Config> getFailureCodeCauses() {
        return () -> new V3Config(V3FailureCodeCausesContext.class, new ModuleCustomFieldCount15())
                .list()
                .beforeFetch(new FetchFailureCodeSupplements())
                .delete()
                .beforeDelete(new CheckForAssociatedModulesBeforeDeletion())
                .build();
    }

    @Module("failurecoderemedies")
    public static Supplier<V3Config> getFailureCodeRemedies() {
        return () -> new V3Config(V3FailureCodeRemediesContext.class, new ModuleCustomFieldCount15())
                .list()
                .beforeFetch(new FetchFailureCodeSupplements())
                .delete()
                .beforeDelete(new CheckForAssociatedModulesBeforeDeletion())
                .build();
    }

    @Module("insurance")
    public static Supplier<V3Config> getInsurance() {
        return () -> new V3Config(V3InsuranceContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getAssociatedVendorAndValidationBeforeSaveChain())
                .afterSave(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand())
                .update()
                .beforeSave(new ValidateDateCommandV3())
                .list()
                .beforeFetch(new LoadInsuranceLookUpCommandV3())
                .summary()
                .beforeFetch(new LoadInsuranceLookUpCommandV3())
                .build();
    }

    @Module("termsandconditions")
    public static Supplier<V3Config> getTermsAndCondition() {
        return () -> new V3Config(V3TermsAndConditionContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getTermsBeforeSaveChain())
                .update()
                .afterSave(new CheckForPublishedCommand())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getTermsAndConditionsBeforeFetchChain())
                .summary()
                .delete()
                .build();
    }

    @Module("storeRoom")
    public static Supplier<V3Config> getStoreRoom() {
        return () -> new V3Config(V3StoreRoomContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getStoreroomBeforeSaveChainV3())
                .update()
                .list()
                .beforeFetch(new LoadStoreRoomLookUpCommandV3())
                .summary()
                .beforeFetch(new LoadStoreRoomLookUpCommandV3())
                .build();
    }

    @Module("watchlist")
    public static Supplier<V3Config> getWatchList() {
        return () -> new V3Config(V3WatchListContext.class, null)
                .create()
                .beforeSave(new CheckForExisitingWatchlistRecordsCommandV3())
                .update()
                .list()
                .summary()
                .afterFetch(new GetLogsForWatchListCommandV3())
                .build();
    }

    @Module("client")
    public static Supplier<V3Config> getClient() {
        return () -> new V3Config(V3ClientContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddAddressForClientLocationCommandV3())
                .afterSave(TransactionChainFactoryV3.getAddClientsAfterSaveChain())
                .update()
                .beforeSave(new AssociateClientFromSite(), new DisassociateClientFromSiteCommand(), new UpdateAddressForClientLocationCommandV3())
                .afterSave(new AddClientUserCommandV3())
                .list()
                .beforeFetch(new LoadClientLookupCommandV3())
                .summary()
                .beforeFetch(new LoadClientLookupCommandV3())
                .delete()
                .build();
    }

    @Module("labour")
    public static Supplier<V3Config> getLabour() {
        return () -> new V3Config(LabourContextV3.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetLocationCommandV3(), new SetLocalIdCommandV3(), new SetDefaultValueForSingleLabourCraftRecord())
                .afterSave(new UpdatePeopleAsALabour())
                .list()
                .beforeFetch(new GetLabourListCommandV3())
                .afterFetch(new FetchLabourCraftAndSkillCommandV3())
                .update()
                .beforeSave(new SetLocationCommandV3())
                .delete()
                .beforeDelete(new FetchPeopleForAssociatedLabour())
                .afterDelete(new UpdateStatusForDeletedLabour())
                .summary()
                .afterFetch(new FetchLabourCraftAndSkillCommandV3())
                .build();
    }

    @Module("purchaserequest")
    public static Supplier<V3Config> getPurchaseRequest() {
        return () -> new V3Config(V3PurchaseRequestContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getAddPurchaseRequestBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getAddPurchaseRequestAfterSaveChain(), new ConstructAddCustomActivityCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getAddPurchaseRequestBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getAddPurchaseRequestAfterSaveChain(), new ConstructUpdateCustomActivityCommandV3())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchPRListChain())
                .summary()
                .beforeFetch(new LoadPurchaseRequestSummaryLookupCommandV3())
                .afterFetch(new FetchPurchaseRequestDetailsCommandV3())
                .build();
    }

    @Module("purchaseorder")
    public static Supplier<V3Config> getPurchaseOrder() {
        return () -> new V3Config(V3PurchaseOrderContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getPoBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getPoAfterSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getPoBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getUpdatePoAfterSaveChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchPOListChain())
                .summary()
                .beforeFetch(new LoadPOSummaryLookupCommandV3())
                .afterFetch(new FetchPODetailsCommandV3())
                .delete()
                .afterDelete(new DeleteReceivableByPOIdV3())
                .build();
    }

    @Module("poterms")
    public static Supplier<V3Config> getPoTerms() {
        return () -> new V3Config(V3PoAssociatedTermsContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadAssociatedTermsLookupCommandV3())
                .summary()
                .delete()
                .build();
    }

    @Module("prterms")
    public static Supplier<V3Config> getPrTerms() {
        return () -> new V3Config(PrAssociatedTermsContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadAssociatedTermsLookupCommandV3())
                .summary()
                .delete()
                .build();
    }

    @Module("quoteterms")
    public static Supplier<V3Config> getQuoteTerms() {
        return () -> new V3Config(QuotationAssociatedTermsContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadAssociatedTermsLookupCommandV3())
                .summary()
                .delete()
                .build();
    }

    @Module("transferrequest")
    public static Supplier<V3Config> getTransferRequest() {
        return () -> new V3Config(V3TransferRequestContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new TransferRequestValidationCommandV3())
                .update()
                .beforeSave(new TransferRequestValidationCommandV3())
                .afterSave(TransactionChainFactoryV3.getUpdateTransferRequestIsStagedAfterSaveChain())
                .list()
                .beforeFetch(new LoadTrListLookupCommandV3())
                .summary()
                .beforeFetch(new LoadTrSummaryLookupCommandV3())
                .afterFetch(TransactionChainFactoryV3.getUpdateLineItemsAndShipmentIdAfterFetchChain())
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.TRANSFER_REQUEST_LINE_ITEM)
    public static Supplier<V3Config> getTransferRequestLineItems() {
        return () -> new V3Config(V3TransferRequestContext.class, new ModuleCustomFieldCount30())
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.TRANSFER_REQUEST_LINE_ITEM, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.TRANSFER_REQUEST_LINE_ITEM, "toolType")
                .build();
    }
    @Module("transferrequestshipment")
    public static Supplier<V3Config> getTransferRequestShipment() {
        return () -> new V3Config(V3TransferRequestShipmentContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadTrShipmentListLookupCommandV3())
                .summary()
                .beforeFetch(new LoadTrShipmentSummaryLookupCommandV3())
                .afterFetch(new SetPendingLineItemsAndReceivablesCommandV3())
                .delete()
                .build();
    }

    @Module("transferrequestshipmentreceivables")
    public static Supplier<V3Config> getTransferRequestShipmentReceivables() {
        return () -> new V3Config(V3TransferRequestShipmentReceivablesContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ShipmentReceivablesValidationCommandV3())
                .afterSave(new UpdateQuantityReceivedInLineItemsCommandV3())
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }

    @Module("requestForQuotation")
    public static Supplier<V3Config> getRequestForQuotation() {
        return () -> new V3Config(V3RequestForQuotationContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getRfqBeforeSaveChain())
                .afterSave(new UpdateRfqIdInPrCommandV3(), new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommandV3(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_ACTIVITY))
                .update()
                .beforeSave(TransactionChainFactoryV3.getRfqBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getRfqAfterUpdateChain(), new ConstructUpdateCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommandV3(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_ACTIVITY))
                .list()
                .beforeFetch(TransactionChainFactoryV3.getRequestForQuotationBeforeFetchChain())
                .summary()
                .beforeFetch(new LoadRequestForQuotationLookupCommandV3())
                .afterFetch(TransactionChainFactoryV3.getRequestForQuotationLineItemsChainV3())
                .delete()
                .build();
    }

@Module("vendorQuotes")
    public static Supplier<V3Config> getVendorQuotes() {
        return () -> new V3Config(V3VendorQuotesContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetLocalIdCommandV3())
                .afterSave(new ConstructAddCustomActivityCommandV3())
                .update()
                .beforeSave(new VendorQuoteBeforeUpdateCommandV3())
                .afterSave(new VendorQuoteAfterUpdateCommandV3(), new ConstructUpdateCustomActivityCommandV3())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getVendorQuotesBeforeFetchChain())
                .afterFetch(new SetVendorQuotesLineItemsOnListCommandV3())
                .summary()
                .beforeFetch(new LoadVendorQuotesLookupCommandV3())
                .afterFetch(TransactionChainFactoryV3.getVendorQuotesAfterFetchChain())
                .delete()
                .build();
    }

    @Module("vendorQuotesLineItems")
    public static Supplier<V3Config> getVendorQuotesLineItems() {
        return () -> new V3Config(V3VendorQuotesLineItemsContext.class, null)
                .list()
                .beforeFetch(new LoadVendorQuoteLineItemsCommandV3())
                .build();
    }

    @Module("itemTypes")
    public static Supplier<V3Config> getItemTypes() {
        return () -> new V3Config(V3ItemTypesContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .beforeSave(new ValidateItemTypeCommandV3())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchItemTypesListChain())
                .summary()
                .beforeFetch(new LoadItemTypesLookUpCommandV3())
                .build();
    }

    @Module("item")
    public static Supplier<V3Config> getItem() {
        return () -> new V3Config(V3ItemContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetItemFieldsBeforeSaveCommand())
                .afterSave(TransactionChainFactoryV3.getAddItemChain())
                .update()
                .beforeSave(new SetItemFieldsBeforeSaveCommand())
                .afterSave(TransactionChainFactoryV3.getAddItemChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchItemListChain())
                .beforeCount(new IncludeServingSiteFilterCommandV3())
                .summary()
                .beforeFetch(new LoadItemLookUpCommandV3())
                .build();
    }

    @Module("itemTransactions")
    public static Supplier<V3Config> getItemTransactions() {
        return () -> new V3Config(V3ItemTransactionsContext.class, null)
                .create()
                .beforeSave(new SetManualItemTransactionCommandV3(), new AdjustmentItemTransactionCommandV3())
                .afterSave(new UpdateItemTransactionsCommandV3())
                .update()
                .list()
                .beforeCount(new FilterItemTransactionsCommandV3())
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchItemTransactionsChain())
                .summary()
                .build();
    }

    @Module("toolTransactions")
    public static Supplier<V3Config> getToolTransactions() {
        return () -> new V3Config(V3ToolTransactionContext.class,null)
                .create()
                .beforeSave(new SetManualToolTransactionsCommandV3(), new AdjustmentToolTransactionCommandV3())
                .afterSave(new UpdateToolTransactionsCommandV3())
                .update()
                .list()
                .beforeCount(new FilterItemTransactionsCommandV3())
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchToolTransactionsChain())
                .summary()
                .build();
    }

    @Module("toolTypes")
    public static Supplier<V3Config> getToolTypes() {
        return () -> new V3Config(V3ToolTypesContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchToolTypesListChain())
                .summary()
                .beforeFetch(new LoadToolTypesLookUpCommandV3())
                .build();
    }

    @Module("tool")
    public static Supplier<V3Config> getTool() {
        return () -> new V3Config(V3ToolContext.class, new ModuleCustomFieldCount30())
                .create()
                .afterSave(TransactionChainFactoryV3.getBulkAddToolChainV3())
                .update()
                .afterSave(new StockOrUpdateToolsCommandV3())
                .list()
                .beforeCount(new IncludeServingSiteFilterCommandV3())
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchToolListChain())
                .afterFetch(new SetToolRateCommandV3())
                .summary()
                .beforeFetch(new LoadToolLookupCommandV3())
                .afterFetch(new SetToolRateCommandV3())
                .build();
    }

    @Module("inventoryCategory")
    public static Supplier<V3Config> getInventoryCategory() {
        return () -> new V3Config(V3InventoryCategoryContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .summary()
                .build();
    }

    @Module("visitorlog")
    public static Supplier<V3Config> getVisitorLog() {
        return () -> new V3Config(VisitorLogContextV3.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getVisitorLogBeforeSaveOnCreateChain())
                .afterTransaction(TransactionChainFactoryV3.getVisitorLogAfterSaveOnCreateChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getVisitorLogBeforeSaveOnUpdateChain())
                .afterTransaction(TransactionChainFactoryV3.getVisitorLogAfterSaveOnUpdateChain())
                .list()
                .beforeFetch(new LoadVisitorLoggingLookupCommandV3())
                .showStateFlowList()
                .summary()
                .beforeFetch(ReadOnlyChainFactoryV3.getVisitorLogBeforeFetchOnSummaryChain())
                .build();
    }

    @Module("invitevisitor")
    public static Supplier<V3Config> getInviteVisitor() {
        return () -> new V3Config(InviteVisitorContextV3.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getInviteVisitorBeforeSaveOnCreateChain())
                .afterSave(TransactionChainFactoryV3.getInviteVisitorAfterSaveOnCreateBeforeTransactionChain())
                .afterTransaction(TransactionChainFactoryV3.getInviteVisitorAfterSaveOnCreateChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getInviteVisitorBeforeSaveOnUpdateChain())
                .afterTransaction(TransactionChainFactoryV3.getInviteVisitorAfterSaveOnUpdateChain())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getInviteVisitorLogBeforeFetchOnListChain())
                .showStateFlowList()
                .summary()
                .beforeFetch(ReadOnlyChainFactoryV3.getInviteVisitorBeforeFetchOnSummaryChain())
                .afterFetch(new ValidateBaseVisitDetailAndLogCommand())
                .build();
    }

    @Module("recurringinvitevisitor")
    public static Supplier<V3Config> getRecurringInviteVisitor() {
        return () -> new V3Config(RecurringInviteVisitorContextV3.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getRecurringInviteVisitorBeforeSaveOnCreateChain())
                .afterTransaction(TransactionChainFactoryV3.getRecurringInviteVisitorAfterSaveOnCreateChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getRecurringInviteVisitorBeforeSaveOnUpdateChain())
                .afterTransaction(TransactionChainFactoryV3.getInviteVisitorAfterSaveOnUpdateChain())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getInviteVisitorLogBeforeFetchOnListChain())
                .showStateFlowList()
                .summary()
                .beforeFetch(new LoadVisitorLoggingLookupCommandV3())
                .afterFetch(new GetScheduleTriggerForRecurringInviteCommandV3())
                .build();
    }

    @Module("groupinvite")
    public static Supplier<V3Config> getGroupInviteVisitor() {
        return () -> new V3Config(GroupInviteContextV3.class, null)
                .create()
                .afterTransaction(new UpdateChildInvitesAfterSaveCommand())
                .update()
                .list()
                .beforeFetch(new LoadGroupInvitesLookupCommand())
                .afterFetch(new FetchVisitorTypeForGroupInvites())
                .showStateFlowList()
                .summary()
                .beforeFetch(new LoadGroupInvitesLookupCommand())
                .afterFetch(new GetChildInvitesForGroupInviteCommand())
                .delete()
                .beforeDelete(new DeleteGroupChildInvitesCommand())
                .build();
    }

    @Module("visitorType")
    public static Supplier<V3Config> getVisitorType() {
        return () -> new V3Config(V3VisitorTypeContext.class, null)
                .create()
                .update()
                .list()
                .afterFetch(new FetchAllVisitorTypeForms())
                .summary()
                .build();
    }



    @Module("basevisit")
    public static Supplier<V3Config> getBaseVisit() {
        return () -> new V3Config(BaseVisitContextV3.class, new ModuleCustomFieldCount30())
                .summary()
                .beforeFetch(ReadOnlyChainFactoryV3.getBaseVisitBeforeFetchOnSummaryChain())
                .afterFetch(new ValidateBaseVisitDetailAndLogCommand())
                .build();
    }

    @Module("visitorlogging")
    public static Supplier<V3Config> getVisitorLogging() {
        return () -> new V3Config(V3VisitorLoggingContext.class, new ModuleCustomFieldCount30_BS2())
                .create()
                .beforeSave(TransactionChainFactoryV3.getVisitorLoggingBeforeSaveOnCreateChain())
                .afterTransaction(TransactionChainFactoryV3.getVisitorLoggingAfterSaveOnCreateChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getVisitorLoggingBeforeSaveOnUpdateChain())
                .afterTransaction(TransactionChainFactoryV3.getVisitorLoggingAfterSaveOnUpdateChain())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getVisitorLoggingBeforeFetchOnListChain())
                .showStateFlowList()
                .summary()
                .beforeFetch(new LoadVisitorLoggingLookupCommandV3())
                .afterFetch(new GetTriggerForRecurringLogCommandV3())
                .build();
    }

    @Module("visitor")
    public static Supplier<V3Config> getVisitor() {
        return () -> new V3Config(V3VisitorContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getVisitorBeforeSaveOnAddChain())
                .afterSave(TransactionChainFactoryV3.getVisitorAfterSaveOnAddChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getVisitorBeforeSaveOnAddChain())
                .list()
                .beforeFetch(new LoadVisitorLookUpCommandV3())
                .summary()
                .beforeFetch(new LoadVisitorLookUpCommandV3())
                .build();
    }

    @Module("visitcustomresponse")
    public static Supplier<V3Config> getVisitResponse() {
        return () -> new V3Config(VisitResponseContextV3.class, null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .beforeDelete(new DeleteResponseCheckCommandV3())
                .build();
    }

    @Module("vendors")
    public static Supplier<V3Config> getVendor() {
        return () -> new V3Config(V3VendorContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddOrUpdateLocationForVendorCommandV3())
                .afterSave(TransactionChainFactoryV3.getVendorsAfterSaveChain())
                .update()
                .beforeSave(new AddOrUpdateLocationForVendorCommandV3())
                .afterSave(TransactionChainFactoryV3.getVendorsAfterSaveChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchVendorsListChain())
                .afterFetch(new LookUpPrimaryFieldHandlingCommandV3())
                .showStateFlowList()
                .summary()
                .beforeFetch(new LoadVendorLookupCommandV3())
                .afterFetch(new LookUpPrimaryFieldHandlingCommandV3())
                .delete()
                .afterDelete(new PeopleUserDeletionV3())
                .build();
    }

    @Module("tenant")
    public static Supplier<V3Config> getTenant() {
        return () -> new V3Config(V3TenantContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ValidateTenantSpaceCommandV3())
                .afterSave(TransactionChainFactoryV3.getTenantAfterSaveChain())
                .update()
                .beforeSave(new ValidateTenantSpaceCommandV3())
                .afterSave(TransactionChainFactoryV3.getTenantAfterSaveChain())
                .list()
                .afterFetch(ReadOnlyChainFactoryV3.getTenantsAfterFetchOnListChain())
                .summary()
                .beforeFetch(new FillTenantsLookupCommand())
                .afterFetch(ReadOnlyChainFactoryV3.getTenantsAfterFetchOnSummaryChain())
                .delete()
                .beforeDelete(new CheckOccupyingTenantUnitsForTenantCommandV3())
                .afterDelete(new PeopleUserDeletionV3())
                .build();
    }

    @Module("workorder")
    public static Supplier<V3Config> getWorkorder() {
        return () -> new V3Config(V3WorkOrderContext.class, new WorkOrderModuleCustomFieldCount())
                .create()
                .beforeSave(TransactionChainFactoryV3.getWorkorderBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getWorkorderAfterSaveChain())
                .preCreate()
                .beforeSave(TransactionChainFactoryV3.getWorkOrderBeforeSavePreCreateChain())
                .afterSave(TransactionChainFactoryV3.getWorkOrderAfterSavePreCreateChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getWorkorderBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getWorkorderAfterUpdateChain(true))
                .list()
                .beforeFetch(TransactionChainFactoryV3.getTicketBeforeFetchChain())
                .afterFetch(TransactionChainFactoryV3.getTicketAfterFetchChain())
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getTicketBeforeFetchForSummaryChain())
                .afterFetch(ReadOnlyChainFactoryV3.getWorkorderAfterFetchOnSummaryChain())
                .delete()
                .postCreate()
                .afterSave(TransactionChainFactoryV3.getWorkOrderAfterSavePostCreateChain())
                .build();
    }

    @Module("tenantcontact")
    public static Supplier<V3Config> getTenantContact() {
        return () -> new V3Config(V3TenantContactContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getTenantContactBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getTenantContactAfterSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getTenantContactBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getTenantContactAfterSaveChain())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getTenantContactBeforeFetchChain())
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleRoleAndScopingCommand())
                .summary()
                .beforeFetch(new LoadTenantcontactLookupsCommandV3())
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleRoleAndScopingCommand())
                .delete()
                .beforeDelete(new ValidateContactsBeforeDeleteCommandV3())
                .afterDelete(new MarkRandomContactAsPrimaryCommandV3(), new DeletePeopleUserPortalAccessV3())
                .build();
    }

    @Module("vendorcontact")
    public static Supplier<V3Config> getVendorContact() {
        return () -> new V3Config(V3VendorContactContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getVendorContactBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getVendorContactAfterSaveChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.VENDOR_CONTACTS_ACTIVITY))
                .update()
                .beforeSave(TransactionChainFactoryV3.getVendorContactBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getVendorContactAfterSaveChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.VENDOR_CONTACTS_ACTIVITY))
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getVendorContactBeforeFetchChain())
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleRoleAndScopingCommand())
                .summary()
                .beforeFetch(new LoadVendorContactLookupCommandV3())
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleRoleAndScopingCommand())
                .delete()
                .beforeDelete(new ValidateContactsBeforeDeleteCommandV3())
                .afterDelete(new MarkRandomContactAsPrimaryCommandV3(), new DeletePeopleUserPortalAccessV3())
                .build();
    }

    @Module("employee")
    public static Supplier<V3Config> getEmployee() {
        return () -> new V3Config(V3EmployeeContext.class, new ModuleCustomFieldCount50())
                .create()
                .beforeSave(TransactionChainFactoryV3.getEmployeeBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getEmployeeAfterSaveChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.EMPLOYEE_ACTIVITY))
                .update()
                .beforeSave(TransactionChainFactoryV3.getEmployeeBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getUpdateEmployeeAfterUpdateChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.EMPLOYEE_ACTIVITY))
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getEmployeeBeforeFetchChain())
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleRoleAndScopingCommand())
                .summary()
                .beforeFetch(new LoadEmployeeLookupCommandV3())
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleRoleAndScopingCommand())
                .delete()
                .beforeDelete(new EmployeeDeleteValidationCommandV3())
                .afterDelete(new PeopleUserDeletionV3())
                .build();
    }

    @Module("clientcontact")
    public static Supplier<V3Config> getClientContact() {
        return () -> new V3Config(V3ClientContactContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getClientContactBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getClientContactAfterSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getClientContactBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getClientContactAfterSaveChain())
                .list()
                .beforeFetch(new LoadClientContactLookupCommandV3())
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleRoleAndScopingCommand())
                .summary()
                .beforeFetch(new LoadClientContactLookupCommandV3())
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleRoleAndScopingCommand())
                .delete()
                .beforeDelete(new ValidateContactsBeforeDeleteCommandV3())
                .afterDelete(new MarkRandomContactAsPrimaryCommandV3(), new DeleteClientContactPeopleUsersCommandV3())
                .build();
    }

    @Module("workpermit")
    public static Supplier<V3Config> getWorkPermit() {
        return () -> new V3Config(V3WorkPermitContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getWorkPermitBeforeSaveOnCreateChain())
                .afterSave(TransactionChainFactoryV3.getWorkPermitAfterSaveOnCreateChain())
                .afterTransaction(
                        new AddActivitiesCommand(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ACTIVITY))

                .update()
                .beforeSave(TransactionChainFactoryV3.getWorkPermitBeforeSaveOnUpdateChain())
                .afterSave(TransactionChainFactoryV3.getWorkPermitAfterSaveOnUpdateChain())
                .afterTransaction(
                        new AddActivitiesCommand(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ACTIVITY))

                .list()
                .beforeFetch(TransactionChainFactoryV3.getWorkPermitBeforeFetchChain())

                .summary()
                .beforeFetch(new LoadWorkPermitLookUpsCommandV3())
                .afterFetch(TransactionChainFactoryV3.getWorkPermitSummaryAfterFetchChain())
                .build();
    }

    @Module("workpermittypechecklist")
    public static Supplier<V3Config> getWorkPermitTypeChecklist() {
        return () -> new V3Config(WorkPermitTypeChecklistContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new WorkPermitTypeChecklistValidationCommand())

                .update()
                .beforeSave(new WorkPermitTypeChecklistValidationCommand())

                .list()
                .beforeFetch(new LoadWorkPermitTypeChecklistLookupsCommand())
                .build();
    }

    @Module("workpermittypechecklistcategory")
    public static Supplier<V3Config> getWorkPermitTypeChecklistCategory() {
        return () -> new V3Config(WorkPermitTypeChecklistCategoryContext.class, null)
                .create()
                .beforeSave(new WorkPermitChecklistCategoryValidationCommand())

                .update()
                .beforeSave(new WorkPermitChecklistCategoryValidationCommand())

                .list()
                .afterFetch(new WorkPermitFillChecklistForCategoryCommand())

                .build();
    }

    @Module(FacilioConstants.ContextNames.BASE_MAIL_MESSAGE)
    public static Supplier<V3Config> getCustomMailMessages() {
        return () -> new V3Config(BaseMailMessageContext.class, null)
                .create()
                .afterTransaction(new UpdateLatestMessageUIDCommandV3())
                .list()
                .summary()
                .afterFetch(new FetchBaseMailAttachmentsCommands())
                .update()
                .build();
    }

    @Module("usernotification")
    public static Supplier<V3Config> getUsernotification() {
        return () -> new V3Config(UserNotificationContext.class, null)
                .create()
                .afterSave(TransactionChainFactoryV3.getUserNotifactionBeforeSaveChain())

                .update()
                .beforeSave(TransactionChainFactoryV3.getNotificationSeenUpdateChain())

                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getUserNotificationBeforeFetchChain())
                .beforeCount(ReadOnlyChainFactoryV3.getUserNotificationBeforeFetchChain())

                .build();
    }

    @Module("audience")
    public static Supplier<V3Config> getAudience() {
        return () -> new V3Config(AudienceContext.class, null)
                .create()
                .beforeSave(new ValidateAudienceSharingCommandV3())
                .afterSave(new AddOrUpdateAudienceSharingInfoCommandV3())
                .update()
                .beforeSave(new ValidateAudienceSharingCommandV3())
                .afterSave(new AddOrUpdateAudienceSharingInfoCommandV3())
                .list()
                .beforeFetch(new LoadAudienceLookupCommandV3())
                .afterFetch(new FillAudienceSharingInfoCommandV3())
                .summary()
                .beforeFetch(new LoadAudienceLookupCommandV3())
                .afterFetch(new FillAudienceSharingInfoCommandV3())
                .delete()
                .build();
    }

    @Module("announcement")
    public static Supplier<V3Config> getAnnouncement() {
        return () -> new V3Config(AnnouncementContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateAnnouncementBeforeSaveChain())
                .afterSave(new UpdateAttachmentsParentIdCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getUpdateAnnouncementBeforeSaveChain())
                .afterTransaction(TransactionChainFactoryV3.getUpdateAnnouncementAfterSaveChain())
                .list()
                .beforeFetch(new LoadAnnouncementLookupCommandV3())
                .summary()
                .beforeFetch(new LoadAnnouncementLookupCommandV3())
                .afterFetch(new AnnouncementFillDetailsCommandV3())
                .delete()
                .afterDelete(new DeleteChildAnnouncementsCommand())
                .build();
    }

    @Module("peopleannouncement")
    public static Supplier<V3Config> getPeopleAnnouncement() {
        return () -> new V3Config(PeopleAnnouncementContext.class, null)
                .create()
                .update()
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getAnnouncementsBeforeFetchChain())
                .summary()
                .beforeFetch(ReadOnlyChainFactoryV3.getAnnouncementsBeforeFetchChain())
                .build();
    }

    @Module("newsandinformation")
    public static Supplier<V3Config> getNewsAndInformation() {
        return () -> new V3Config(NewsAndInformationContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateNewsAndInformationBeforeSaveChain())
                .afterSave(new UpdateAttachmentsParentIdCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getCreateNewsAndInformationBeforeSaveChain())
                .afterSave(new UpdateAttachmentsParentIdCommandV3())
                .list()
                .beforeFetch(new LoadNewsAndInformationLookupCommandV3())
                .afterFetch(new FillNewsRelatedModuleDataInListCommandV3())
                .summary()
                .beforeFetch(new LoadNewsAndInformationLookupCommandV3())
                .afterFetch(new FillNewsAndInformationDetailsCommandV3())
                .build();
    }

    @Module("neighbourhood")
    public static Supplier<V3Config> getNeighbourhood() {
        return () -> new V3Config(NeighbourhoodContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateNeighbourhoodBeforeSaveChain())
                .afterSave(new UpdateAttachmentsParentIdCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getCreateNeighbourhoodBeforeUpdateChain())
                .afterSave(new UpdateAttachmentsParentIdCommandV3())
                .summary()
                .beforeFetch(new NeighbourhoodFillLookupFieldsCommand())
                .afterFetch(new FillNeighbourhoodSharingInfoCommand())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getNeighbourhoodBeforeFetchChain())
                .build();
    }

    @Module("dealsandoffers")
    public static Supplier<V3Config> getDealsAndOffers() {
        return () -> new V3Config(DealsAndOffersContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateDealsBeforeSaveChain())
                .afterSave(new UpdateAttachmentsParentIdCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getCreateDealsBeforeSaveChain())
                .afterSave(new UpdateAttachmentsParentIdCommandV3())
                .summary()
                .beforeFetch(new DealsAndOffersFillLookupFields())
                .afterFetch(new FillDealsAndOffersSharingInfoCommand())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getDealsAndOffersBeforeFetchChain())
                .build();
    }

    @Module("contactdirectory")
    public static Supplier<V3Config> getContactDirectory() {
        return () -> new V3Config(ContactDirectoryContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateContactDirectoryBeforeSaveChain())
                .afterSave(new UpdateAttachmentsParentIdCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getCreateContactDirectoryBeforeSaveChain())
                .afterSave(new UpdateAttachmentsParentIdCommandV3())
                .summary()
                .beforeFetch(new ContactDirectoryFillLookupFieldsCommand())
                .afterFetch(new FillContactDirectorySharingInfoCommand())
                .list()
                .beforeFetch(new ContactDirectoryFillLookupFieldsCommand())
                .build();
    }

    @Module("admindocuments")
    public static Supplier<V3Config> getAdminDocuments() {
        return () -> new V3Config(AdminDocumentsContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateAdminDocumentsBeforeSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getCreateAdminDocumentsBeforeSaveChain())
                .list()
                .beforeFetch(new LoadAdminDocumentsLookupCommandV3())
                .summary()
                .beforeFetch(new LoadAdminDocumentsLookupCommandV3())
                .afterFetch(new FillAdminDocumentsSharingInfoCommand())
                .build();
    }

    @Module("accounttype")
    public static Supplier<V3Config> getAccountType() {
        return () -> new V3Config(AccountTypeContext.class, new ModuleCustomFieldCount30())
                .update()
                .summary()
                .build();
    }

    @Module("chartofaccount")
    public static Supplier<V3Config> getChartOfAccount() {
        return () -> new V3Config(ChartOfAccountContext.class, new ModuleCustomFieldCount30())
                .create().beforeSave(TransactionChainFactoryV3.getCreateChartOfAccountBeforeSaveChain())
                .update()
                .list()
                .beforeFetch(new LoadChartOfAccountLookupCommandV3())
                .summary()
                .beforeFetch(new LoadChartOfAccountLookupCommandV3())
                .delete()
                .beforeDelete(new ValidateDeleteChartOfAccountCommand())
                .build();
    }

    @Module("budget")
    public static Supplier<V3Config> getBudget() {
        return () -> new V3Config(BudgetContext.class, new ModuleCustomFieldCount30())
                .create().beforeSave(TransactionChainFactoryV3.getCreateBudgetBeforeSaveChain())
                .afterSave(new AddOrUpdateMonthlyBudgetAmountCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getCreateBudgetBeforeSaveChain(), new DeleteBudgetAmountCommand())
                .afterSave(new AddOrUpdateMonthlyBudgetAmountCommandV3())
                .list()
                .beforeFetch(new LoadBudgetLookupCommandV3())
                .summary()
                .beforeFetch(new LoadBudgetLookupCommandV3())
                .afterFetch(new FillBudgetDetailsCommandV3())
                .delete()
                .afterDelete(new DeleteBudgetSubModuleRecordsCommandV3())
                .build();
    }

    @Module("transaction")
    public static Supplier<V3Config> getTransaction() {
        return () -> new V3Config(V3TransactionContext.class, null)
                // new GetLogsForTransactionCommandV3() will be removed once all transactions are works fine
                .create().afterSave(new GetLogsForTransactionCommandV3(),new RollUpTransactionAmountCommand())
                .update()
                .beforeSave(new RemoveAmountFromPreviousMonthCommand())
                .afterSave(new GetLogsForTransactionCommandV3(),new RollUpTransactionAmountCommand())
                .list()
                .beforeFetch(new LoadTransactionsLookupCommandV3())
                .afterFetch(new SetDislayNameForTransactionSourceModuleName())
                .summary()
                .beforeFetch(new LoadTransactionsLookupCommandV3())
                .delete()
                .beforeDelete(new GetTransactionBeforeDeleteCommand())
                .afterDelete(new GetLogsForTransactionCommandV3(),new RollUpTransactionAmountCommand())
                .build();
    }

    @Module("facility")
    public static Supplier<V3Config> getFacility() {
        return () -> new V3Config(FacilityContext.class, new ModuleCustomFieldCount30_BS2())
                .create().beforeSave(TransactionChainFactoryV3.getFacilityBeforeSaveChain())
                .afterSave(new ScheduleSlotCreationCommand())
                .update()
                .beforeSave(new ValidateFacilityCommand())
                .afterSave(new FacilitySlotUpdateCheckCommand())
                .delete()
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getFacilityBeforeListFetchChain())
                .summary()
                .beforeFetch(new LoadFacilityLookupCommandV3())
                .afterFetch(ReadOnlyChainFactoryV3.getFacilityAfterFetchChain())
                .build();
    }

    @Module("amenity")
    public static Supplier<V3Config> getAmenity() {
        return () -> new V3Config(AmenitiesContext.class, new ModuleCustomFieldCount10())
                .create()
                .update()
                .delete()
                .list()
                .summary()
                .build();
    }

    @Module("facilitybooking")
    public static Supplier<V3Config> getFacilityBooking() {
        return () -> new V3Config(V3FacilityBookingContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateBookingBeforeSaveChain())
                .afterSave(new CreatePaymentRecordForBookingCommand())
                .update()
                .beforeSave(TransactionChainFactoryV3.getCreateBookingBeforeEditChain())
                .afterSave(TransactionChainFactoryV3.getUpdateBookingAfterEditChain())
                .delete()
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getFacilityBookingBeforeListFetchChain())
                .summary()
                .beforeFetch(new LoadFacilityBookingLookupCommand())
                .afterFetch(new FillFacilityBookingDetailsCommandV3())
                .build();
    }

    @Module("facilityWeekdayAvailability")
    public static Supplier<V3Config> getFacilityWeekDayAvailability() {
        return () -> new V3Config(WeekDayAvailability.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .delete()
                .list()
                .summary()
                .build();
    }

    @Module("facilitySpecialAvailability")
    public static Supplier<V3Config> getFacilitySpecialAvailability() {
        return () -> new V3Config(FacilitySpecialAvailabilityContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ValidateSpecialAvailabilityCommandV3())
                .afterSave(new AddSpecialAvailabilitySlotsCommandV3())
                .update()
                .delete()
                .list()
                .summary()
                .build();
    }

    @Module("slot")
    public static Supplier<V3Config> getSlot() {
        return () -> new V3Config(SlotContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .beforeSave(new ValidateSlotCommand())
                .delete()
                .list()
                .summary()
                .delete().afterDelete(new CancelBookingsForSlotsCommand())

                .build();
    }

    @Module("jobplan")
    public static Supplier<V3Config> getJobPlan() {
        return () -> new V3Config(JobPlanContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ValidationForJobPlanCategory())
                .afterSave(TransactionChainFactoryV3.getCreateJobPlanAfterSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getUpdateJobPlanBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getUpdateJobPlanAfterSaveChain())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getJobPlanBeforeListFetchChain())
                .summary()
                .beforeFetch(new FetchJobPlanLookupCommand())
                .afterFetch(TransactionChainFactoryV3.getJobPlanSummaryAfterFetchChain())
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getDeleteJobPlanBeforeChain())
                .build();
    }

    @Module("jobplansection")
    public static Supplier<V3Config> getJobPlanSection(){
        return () -> new V3Config(JobPlanTaskSectionContext.class, new ModuleCustomFieldCount30())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getJobPlanSectionBeforeListFetchChain())
                .build();
    }

    @Module("jobplantask")
    public static Supplier<V3Config> getJobPlanTask(){
        return () -> new V3Config(JobPlanTasksContext.class, new ModuleCustomFieldCount30())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getJobPlanTaskBeforeListFetchChain())
                .afterFetch(ReadOnlyChainFactoryV3.getJobPlanTaskAfterListFetchChain())               
                .build();
    }

    @Module("jobPlanTaskInputOptions")
    public static Supplier<V3Config> getJobPlanTaskInputOptions(){
        return () -> new V3Config(TaskInputOptionsContext.class, new ModuleCustomFieldCount30())
                .list()
                .beforeFetch(new AddCriteriaForJobPlanTaskInputOptionsBeforeFetchCommand())
                .build();
    }

    @Module("jobPlanSectionInputOptions")
    public static Supplier<V3Config> getJobPlanSectionInputOptions(){
        return () -> new V3Config(SectionInputOptionsContext.class, new ModuleCustomFieldCount30())
                .list()
                .beforeFetch(new AddCriteriaForJobPlanSectionInputOptionsBeforeFetchCommand())
                .build();
    }

    @Module("jobPlanItems")
    public static Supplier<V3Config> getJobPlanItems() {
        return () -> new V3Config(JobPlanItemsContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ValidateJobPlanItemsCommandV3())
                .update()
                .beforeSave(new ValidateJobPlanItemsCommandV3())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchJobPlanItemsListChain())
                .summary()
                .beforeFetch(new LoadJobPlanItemsLookupCommandV3())
                .delete()
                .beforeDelete(new ValidateJobPlanItemsBeforeDeleteCommandV3())
                .build();
    }

    @Module("jobPlanTools")
    public static Supplier<V3Config> getJobPlanTools() {
        return () -> new V3Config(JobPlanToolsContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ValidateJobPlanToolsCommandV3())
                .update()
                .beforeSave(new ValidateJobPlanToolsCommandV3())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchJobPlanToolsListChain())
                .summary()
                .beforeFetch(new LoadJobPlanToolsLookupCommandV3())
                .delete()
                .beforeDelete(new ValidateJobPlanToolsBeforeDeleteCommandV3())
                .build();
    }

    @Module("jobPlanServices")
    public static Supplier<V3Config> getJobPlanServices() {
        return () -> new V3Config(JobPlanServicesContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ValidateJobPlanServicesCommandV3())
                .update()
                .beforeSave(new ValidateJobPlanServicesCommandV3())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchJobPlanServicesListChain())
                .summary()
                .beforeFetch(new LoadJobPlanServicesCommandV3())
                .delete()
                .beforeDelete(new ValidateJobPlanServicesBeforeDeleteCommandV3())
                .build();
    }

    @Module("workOrderPlannedItems")
    public static Supplier<V3Config> getWorkOrderPlannedItems() {
        return () -> new V3Config(WorkOrderPlannedItemsContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetWorkOrderPlannedItemsCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getWoPlannedItemsBeforeUpdateChain())
                .afterSave(new UpdateWorkOrderPlannedItemsCommandV3())
                .list()
                .beforeFetch(new LoadPlannedItemsExtraFieldsCommandV3())
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_ITEMS, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_ITEMS, "storeRoom")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_ITEMS, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_ITEMS, "storeRoom")
                .afterFetch(TransactionChainFactoryV3.getReserveValidationChainV3())
                .delete()
                .beforeDelete(new ValidatePlannedItemsBeforeDeleteCommandV3())
                .build();
    }

    @Module("workOrderPlannedTools")
    public static Supplier<V3Config> getWorkOrderPlannedTools() {
        return () -> new V3Config(WorkOrderPlannedToolsContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetWorkOrderPlannedToolsCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getWoPlannedToolsBeforeUpdateChain())
                .afterSave(new UpdateWorkorderPlannedToolsCommandV3())
                .list()
                .beforeFetch(new LoadPlannedToolsExtraFieldsCommandV3())
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_TOOLS, "toolType")
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_TOOLS, "storeRoom")
                .afterFetch(new LoadPlannedToolsForActualsCommandV3())
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_TOOLS, "toolType")
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_TOOLS, "storeRoom")
                .afterFetch(TransactionChainFactoryV3.getReserveToolValidationChainV3())
                .delete()
                .build();
    }

    @Module("workOrderPlannedServices")
    public static Supplier<V3Config> getWorkOrderPlannedServices() {
        return () -> new V3Config(WorkOrderPlannedServicesContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetWorkOrderPlannedServicesCommandV3())
                .update()
                .beforeSave(new SetWorkOrderPlannedServicesCommandV3())
                .list()
                .beforeFetch(new LoadPlannedServicesExtraFieldsCommandV3())
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_SERVICES, "service")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.WO_PLANNED_SERVICES, "service")
                .delete()
                .build();
    }

    @Module("inventoryReservation")
    public static Supplier<V3Config> getInventoryReservation() {
        return () -> new V3Config(InventoryReservationContext.class, null)
                .create()
                .update()
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.INVENTORY_RESERVATION,"workOrder")
                .fetchSupplement(FacilioConstants.ContextNames.INVENTORY_RESERVATION,"itemType")
                .fetchSupplement(FacilioConstants.ContextNames.INVENTORY_RESERVATION,"toolType")
                .fetchSupplement(FacilioConstants.ContextNames.INVENTORY_RESERVATION,"storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.INVENTORY_RESERVATION, "inventoryRequest")
                .summary()
                .delete()
                .build();
    }

    @Module("workorderItem")
    public static Supplier<V3Config> getWorkOrderItems() {
        return () -> new V3Config(V3WorkorderItemContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetWorkOrderItemsCommandV3())
                .afterSave(TransactionChainFactoryV3.getAddOrUdpateWorkorderItemsChainV3())
                .update()
                .beforeSave(new SetWorkOrderItemsCommandV3())
                .afterSave(TransactionChainFactoryV3.getAddOrUdpateWorkorderItemsChainV3())
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_ITEMS, "item")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_ITEMS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_ITEMS, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_ITEMS,"inventoryReservation")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_ITEMS,"asset")
                .beforeFetch(new LoadWorkorderActualsExtraFieldsCommandV3())
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_ITEMS, "item")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_ITEMS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_ITEMS, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_ITEMS,"inventoryReservation")
                .delete()
                .afterDelete(TransactionChainFactoryV3.getAfterDeleteWorkorderItemsChainV3())
                .build();
    }

    @Module("workorderTools")
    public static Supplier<V3Config> getWorkOrderTools() {
        return () -> new V3Config(V3WorkorderToolsContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetWorkOrderToolsCommandV3())
                .afterSave(TransactionChainFactoryV3.getAddOrUdpateWorkorderToolsChainV3())
                .update()
                .beforeSave(new SetWorkOrderToolsCommandV3())
                .afterSave(TransactionChainFactoryV3.getAddOrUdpateWorkorderToolsChainV3())
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_TOOLS, "tool")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_TOOLS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_TOOLS, "toolType")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_TOOLS,"inventoryReservation")
                .beforeFetch(new LoadWorkorderActualsExtraFieldsCommandV3())
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_TOOLS, "tool")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_TOOLS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_TOOLS, "toolType")
                .fetchSupplement(FacilioConstants.ContextNames.WORKORDER_TOOLS,"inventoryReservation")
                .delete()
                .afterDelete(TransactionChainFactoryV3.getAfterDeleteWorkorderToolsChainV3())
                .build();
    }

    @Module("workorderService")
    public static Supplier<V3Config> getWorkOrderServices() {
        return () -> new V3Config(V3WorkOrderServiceContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetWorkOrderServicesCommandV3())
                .afterSave(TransactionChainFactoryV3.getAddOrUdpateWorkorderServiceChainV3())
                .update()
                .beforeSave(new SetWorkOrderServicesCommandV3())
                .afterSave(TransactionChainFactoryV3.getAddOrUdpateWorkorderServiceChainV3())
                .list()
                .beforeFetch(new LoadWorkorderActualsExtraFieldsCommandV3())
                .fetchSupplement(FacilioConstants.ContextNames.WO_SERVICE,"service")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.WO_SERVICE,"service")
                .delete()
                .afterDelete(TransactionChainFactoryV3.getAfterDeleteWorkorderServicesChainV3())
                .build();
    }

    @Module("workorderCost")
    public static Supplier<V3Config> getWorkOrderCost() {
        return () -> new V3Config(V3WorkorderCostContext.class, null)
                .create()
                .afterSave(TransactionChainFactoryV3.getAfterCreateWorkOrderCostChainV3())
                .update()
                .afterSave(TransactionChainFactoryV3.getAfterUpdateWorkOrderCostChainV3())
                .list()
                .summary()
                .delete()
                .afterDelete(TransactionChainFactoryV3.getAfterDeleteWorkOrderCostChainV3())
                .build();
    }


    @Module("indoorfloorplan")
    public static Supplier<V3Config> getIndoorFloorPlan() {
        return () -> new V3Config(V3IndoorFloorPlanContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new V3ValidateFloorPlanCommand())
                .afterSave(TransactionChainFactoryV3.addfloorplanObjectsChain())
                .update().afterSave(TransactionChainFactoryV3.AddORUpdateMarkersAndModulesCommand())
                .list().beforeFetch(new LoadFloorplanLookupCommand())
                .afterFetch(TransactionChainFactoryV3.getFloorPlanObjectsChain())
                .summary().beforeFetch(new LoadFloorplanLookupCommand())
                .afterFetch(TransactionChainFactoryV3.getFloorPlanObjectsChain())
                .build();
    }

    @Module("markertype")
    public static Supplier<V3Config> getMarkerType() {
        return () -> new V3Config(V3FloorplanMarkersContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list().afterFetch(new FetchFloorPlanMarkerCommand())
                .summary().afterFetch(new FetchFloorPlanMarkerCommand())
                .build();
    }

    @Module("floorplanmarker")
    public static Supplier<V3Config> getMarker() {
        return () -> new V3Config(V3MarkerContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .summary()
                .build();
    }

    @Module("desks")
    public static Supplier<V3Config> getDesk() {
        return () -> new V3Config(V3DeskContext.class, new ModuleCustomFieldCount30())
                .create().beforeSave(new V3ValidateSpaceCommand(), new FetchChangeSetForCustomActivityCommand())
                .afterSave(new CreateFacilityForDesksCommandV3(), new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .update().beforeSave(new FetchChangeSetForCustomActivityCommand())
                .afterSave(new CreateFacilityForDesksCommandV3(), new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .list()
                .beforeFetch(new LoadDesksLookupCommand())
                .summary()
                .beforeFetch(new LoadDesksLookupCommand())
                .build();
    }

    @Module("floorplanmarkertypes")
    public static Supplier<V3Config> getFloorPlanMarkerTypes() {
        return () -> new V3Config(V3FloorPlanMarkerTypeContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .summary()
                .build();
    }

    @Module("department")
    public static Supplier<V3Config> getDepartment() {
        return () -> new V3Config(V3DepartmentContext.class, new ModuleCustomFieldCount30())
                .create().beforeSave(new FetchChangeSetForCustomActivityCommand())
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .update().beforeSave(new FetchChangeSetForCustomActivityCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .list()
                .summary()
                .build();
    }

    @Module("moves")
    public static Supplier<V3Config> getMoves() {
        return () -> new V3Config(V3MovesContext.class, new ModuleCustomFieldCount30())
                .create().beforeSave(new FetchChangeSetForCustomActivityCommand(), new ValidateMovesCommand())
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY),
                        new UpdateEmployeeInDesksCommandV3())
                .update().beforeSave(new FetchChangeSetForCustomActivityCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY),
                        new UpdateEmployeeInDesksCommandV3())
                .list()
                .beforeFetch(new LoadMovesLookupCommand())
                .summary()
                .beforeFetch(new LoadMovesLookupCommand())
                .build();
    }

    @Module("deliveries")
    public static Supplier<V3Config> getDeliveries() {
        return () -> new V3Config(V3DeliveriesContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new FillDeliveriesDetailsCommand())
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .update()
                .beforeSave(new FillDeliveriesDetailsCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .list()
                .beforeFetch(new LoadDeliveriesLookupCommand())
                .summary()
                .beforeFetch(new LoadDeliveriesLookupCommand())
                .build();
    }

    @Module("deliveryArea")
    public static Supplier<V3Config> getDeliveryArea() {
        return () -> new V3Config(V3DeliveryAreaContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadDeliveryAreaLookupCommand())
                .summary()
                .beforeFetch(new LoadDeliveryAreaLookupCommand())
                .build();
    }

    @Module("lockers")
    public static Supplier<V3Config> getLockers() {
        return () -> new V3Config(V3LockersContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddLockersSpaceCommand(), new FetchChangeSetForCustomActivityCommand())
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .update()
                .beforeSave(new FetchChangeSetForCustomActivityCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .list()
                .beforeFetch(new LoadLockersLookupCommand())
                .summary()
                .beforeFetch(new LoadLockersLookupCommand())
                .build();
    }

    @Module("parkingstall")
    public static Supplier<V3Config> getParkingStall() {
        return () -> new V3Config(V3ParkingStallContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddParkingStallSpaceCommand(), new FetchChangeSetForCustomActivityCommand())
                .afterSave(new ConstructAddCustomActivityCommandV3(), new CreateFacilityForParkingCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .update()
                .beforeSave(new FetchChangeSetForCustomActivityCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new CreateFacilityForParkingCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .list()
                .beforeFetch(new LoadParkingStallLookupCommand())
                .summary()
                .beforeFetch(new LoadParkingStallLookupCommand())
                .build();
    }

    @Module("rooms")
    public static Supplier<V3Config> getRooms() {
        return () -> new V3Config(V3RoomsContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddRoomSpaceCommand(), new FetchChangeSetForCustomActivityCommand())
                .afterSave(new ConstructAddCustomActivityCommandV3(), new CreateFacilityForParkingCommandV3())
                .update()
                .beforeSave(new FetchChangeSetForCustomActivityCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new CreateFacilityForParkingCommandV3())
                .list()
                .beforeFetch(new LoadRoomsLookupCommand())
                .summary()
                .beforeFetch(new LoadRoomsLookupCommand())
                .build();
    }

    @Module("site")
    public static Supplier<V3Config> getSite() {
        return () -> new V3Config(V3SiteContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddOrUpdateSiteLocationCommand(), new SetSiteRelatedContextCommand())
                .afterSave(new UpdateQRValueCommandV3(),new CreateSiteAfterSave(), getSpaceReadingsChain(),
                        new InsertReadingDataMetaForNewResourceCommand(), new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommand(FacilioConstants.ContextNames.SITE_ACTIVITY))
                .update()
                .beforeSave(new AddOrUpdateSiteLocationCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommand(FacilioConstants.ContextNames.SITE_ACTIVITY))
                .delete()
                .afterDelete(new DeleteBasespaceChildrenCommandV3())
                .summary()
                .beforeFetch(new SiteFillLookupFieldsCommand())
                .afterFetch(new FetchBasespaceChildrenCountCommandV3())
                .list().beforeFetch(ReadOnlyChainFactoryV3.getFetchSiteFilterChain())
                .pickList()
                .setFourthField(FacilioConstants.ContextNames.SITE, FacilioConstants.ContextNames.DECOMMISSION)
                .beforeFetch(new DecommissionPicklistCheckCommand())
                .build();
    }

    @Module("building")
    public static Supplier<V3Config> getBuilding() {
        return () -> new V3Config(V3BuildingContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddOrUpdateBuildingLocation(), new SetBuildingRelatedContextCommand())
                .afterSave(new UpdateQRValueCommandV3(),new CreateBuildingAfterSave(), getSpaceReadingsChain(),
                        new InsertReadingDataMetaForNewResourceCommand(), new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommand(FacilioConstants.ContextNames.BUILDING_ACTIVITY))
                .update()
                .beforeSave(new AddOrUpdateBuildingLocation(), new SetBuildingRelatedContextCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommand(FacilioConstants.ContextNames.BUILDING_ACTIVITY))
                .delete()
                .afterDelete(new DeleteBasespaceChildrenCommandV3())
                .summary().beforeFetch(new BuildingFillLookupFieldsCommand())
                .afterFetch(new FetchBasespaceChildrenCountCommandV3())
                .list().beforeFetch(ReadOnlyChainFactoryV3.getFetchBuildingFilterChain())
                .pickList()
                .setFourthField(FacilioConstants.ContextNames.BUILDING, FacilioConstants.ContextNames.DECOMMISSION)
                .beforeFetch(new DecommissionPicklistCheckCommand())
                .build();
    }

    @Module("floor")
    public static Supplier<V3Config> getFloor() {
        return () -> new V3Config(V3FloorContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetFloorRelatedContextCommand())
                .afterSave(new UpdateQRValueCommandV3(),new CreateFloorAfterSave(), getSpaceReadingsChain(),
                        new InsertReadingDataMetaForNewResourceCommand(), new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommand(FacilioConstants.ContextNames.FLOOR_ACTIVITY))
                .update()
                .beforeSave(new SetFloorRelatedContextCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommand(FacilioConstants.ContextNames.FLOOR_ACTIVITY))
                .delete()
                .afterDelete(new DeleteBasespaceChildrenCommandV3())
                .summary().beforeFetch(new FloorFillLookupFieldsCommand())
                .afterFetch(new FetchBasespaceChildrenCountCommandV3())
                .list().beforeFetch(ReadOnlyChainFactoryV3.getFetchFloorFilterChain())
                .pickList()
                .setFourthField(FacilioConstants.ContextNames.FLOOR, FacilioConstants.ContextNames.DECOMMISSION)
                .beforeFetch(new DecommissionPicklistCheckCommand())
                .build();
    }

    @Module("space")
    public static Supplier<V3Config> getSpace() {
        return () -> new V3Config(V3SpaceContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddOrUpdateSpaceLocation(), new SetSpaceRelatedContextCommand(),
                        new AddSpaceCategoryExtendedModuleCommandV3())
                .afterSave(new UpdateQRValueCommandV3(),new CreateSpaceAfterSave(), getSpaceReadingsChain(),
                        new InsertReadingDataMetaForNewResourceCommand(), new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommand(FacilioConstants.ContextNames.SPACE_ACTIVITY))
                .update()
                .beforeSave(new AddOrUpdateSpaceLocation(), new SetSpaceRelatedContextCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommand(FacilioConstants.ContextNames.SPACE_ACTIVITY))
                .delete()
                .afterDelete(new DeleteBasespaceChildrenCommandV3())
                .summary().beforeFetch(new SpaceFillLookupFieldsCommand())
                .afterFetch(new FetchBasespaceChildrenCountCommandV3())
                .list().beforeFetch(TransactionChainFactoryV3.getSpaceBeforeFetchChain())
                .pickList()
                .setFourthField(FacilioConstants.ContextNames.SPACE, FacilioConstants.ContextNames.DECOMMISSION)
                .beforeFetch(new DecommissionPicklistCheckCommand())
                .build();
    }

    @Module("service")
    public static Supplier<V3Config> getService() {
        return () -> new V3Config(V3ServiceContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new UpdateStatusCommandV3())
                .update()
                .afterSave(new UpdateVendorV3(), new GetServiceVendorListCommandV3())
                .delete()
                .list()
                .summary()
                .afterFetch(new GetServiceVendorListCommandV3())
                .build();
    }

    @ModuleType(type = FacilioModule.ModuleType.TIME_LOG)
    public static Supplier<V3Config> getWorkOrderTimeLog(){
        return () -> new V3Config(TimelogContext.class,null)
                .list()
                .beforeFetch(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        long parentId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, "parentId"));
                        Condition condition = CriteriaAPI.getCondition("PARENT_ID", "parent", String.valueOf(parentId), NumberOperators.EQUALS);
                        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
                        return false;
                    }
                })
                .create()
                .beforeSave(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        Map<String,Object> bodyParam = Constants.getBodyParams(context);
                        boolean internal = true;
                        if(bodyParam != null && bodyParam.containsKey("internal")) {
                            internal = (boolean) bodyParam.get("internal");
                        }
                        if(!internal) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Create is not supported");
                        }
                        return false;
                    }
                })
                .update()
                .beforeSave(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        Map<String,Object> bodyParam = Constants.getBodyParams(context);
                        boolean internal = true;
                        if(bodyParam != null && bodyParam.containsKey("internal")) {
                            internal = (boolean) bodyParam.get("internal");
                        }
                        if(!internal) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Update is not supported");
                        }
                        return false;
                    }
                })
                .delete()
                .beforeDelete(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        Map<String,Object> bodyParam = Constants.getBodyParams(context);
                        boolean internal = true;
                        if(bodyParam != null && bodyParam.containsKey("internal")) {
                            internal = (boolean) bodyParam.get("internal");
                        }
                        if(!internal) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Delete is not supported");
                        }
                        return false;
                    }
                })
                .build();
    }

    @Module(FacilioConstants.Workflow.WORKFLOW_LOG)
    public static Supplier<V3Config> getWorkflowLog() {
        return () -> new V3Config(WorkflowLogContext.class, null)
                .list()
                .afterFetch(new LoadWorkflowLogFieldNamesCommandV3())
                .build();
    }
    
    @Module(FacilioConstants.MultiResource.NAME)
	public static Supplier<V3Config> getMultiResource() {
		return () -> new V3Config(MultiResourceContext.class, null)
							 .create()
                             .beforeSave(new ValidateBeforeSaveCommandV3())
                             .update()
                             .beforeSave(new ValidateBeforeSaveCommandV3())
                             .summary()
                             .beforeFetch(new LoadMultiResourcesLookUpFieldsCommandV3())
							 .list()
                             .beforeFetch(TransactionChainFactoryV3.getMultiResourceBeforeFetchChain())
				             .afterFetch(new SortMultiResourceDataCommandV3())
							 .delete()
							 .build();
	}

    @Module("asset")
    public static Supplier<V3Config> getAsset() {
        return () -> new V3Config(V3AssetContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AssetCategoryAdditionInExtendModuleCommand(),
                        new AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMFV3(),
                        new ValidateQrValueCommandV3(), new ValidateRotatingItemBeforeCreateCommandV3())
                .afterSave(new ConstructAddAssetActivityCommandV3(), new AddRotatingItemToolCommandV3(),
                        new AssetAfterSaveCommandV3(), FacilioChainFactory.getCategoryReadingsChain(),
                        new InsertReadingDataMetaForNewResourceCommand(), new PushDataToESCommand(),new RemoveAssetExtendedModulesFromRecordMap())
                .update()
                .beforeSave(new RotatingItemMovementToStoreCommandV3(), new ValidateRotatingItemBeforeUpdateCommandV3(),new CheckPMForAssetsCommandV3(), new AssetCategoryAdditionInExtendModuleCommand(),
                        new AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMFV3(),
                        new ValidateQrValueCommandV3())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(), new AddActivitiesCommandV3(),new RemoveAssetExtendedModulesFromRecordMap())
                .delete()
                .summary()
                .beforeFetch(new AssetSupplementsSupplyCommand())
                .afterFetch(new LoadAssetSummaryCommandV3())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getAssetBeforeFetchChain())
                .afterFetch(new AssetListFilterByReadingsCommand())
                .pickList()
                .setFourthField(FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.DECOMMISSION)
                .beforeFetch(new DecommissionPicklistCheckCommand())
                .build();
    }
    @Module("assetSpareParts")
    public static Supplier<V3Config> getAssetSpareparts() {
        return () -> new V3Config(AssetSpareParts.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getSparePartBeforeCreateChain())
                .list()
                .beforeFetch(new LoadSparePartsSupplementsCommand())
                .update()
                .beforeSave(new CheckForRotatableItemCommand())
                .delete()
                .build();
    }

    @Module("inventoryrequest")
    public static Supplier<V3Config> getInventoryRequest() {
        return () -> new V3Config(V3InventoryRequestContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getIRBeforeSaveChain())
                .afterSave(new SetStoreRoomAndReservationTypeForInventoryRequestLineItems())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.INVENTORY_REQUEST_ACTIVITY))
                .update()
                .beforeSave(TransactionChainFactoryV3.getIRBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getIRAfterSaveChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.INVENTORY_REQUEST_ACTIVITY))
                .list()
                .beforeFetch(TransactionChainFactoryV3.getIRBeforeFetchChain())
                .summary()
                .beforeFetch(new LoadIRLookupCommandV3())
                .afterFetch(new FetchInventoryRequestDetailsCommandV3())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS)
    public static Supplier<V3Config> getInventoryRequestLineItems() {
        return () -> new V3Config(V3InventoryRequestLineItemContext.class, null)
                .create()
                .beforeSave(new SetInventoryRequestLineItemsReservationTypeCommandV3())
                .afterSave(new UpdateInventoryRequestReservationStatusCommandV3())
                .update()
                .beforeSave(TransactionChainFactoryV3.getBeforeUpdateInventoryRequestLineItemsChain())
                .afterSave(TransactionChainFactoryV3.getInventoryRequestLineItemsAfterUpdateChain())
                .delete()
                .beforeDelete(new ValidateInventoryRequestLineItemBeforeDeleteCommandV3())
                .afterDelete(new InventoryRequestLineItemAfterDeleteCommandV3())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getLineItemsBeforeFetchChain())
                .afterFetch(TransactionChainFactoryV3.getAfterFetchInventoryRequestLineItemsChain())
                .fetchSupplement(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, "toolType")
                .fetchSupplement(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, "storeRoom")
                .summary()
                .build();
    }

    @Module(MLServiceUtil.ML_SERVICE_MODULE)
    public static Supplier<V3Config> getMLService() {
        return () -> new V3Config(V3MLServiceContext.class, new ModuleCustomFieldCount10())
                .create()
                .beforeSave(new MLServiceBeforeCreateValidationCommand(), new ValidateMLServiceCommand())
                .afterSave(TransactionChainFactoryV3.addMLServiceChain())
                .update()
                .beforeSave(new MLServiceBeforeUpdateCommand(), new MLServiceBeforeCreateValidationCommand())
                .afterSave(new MLServiceAfterUpdateCommand())
                .list()
                .summary()
                .delete()
                .beforeDelete(new MLServiceBeforeDeleteCommand())
                .build();
    }

    @Module("assetcategory")
    public static Supplier<V3Config> getAssetCategory() {
        return () -> new V3Config(V3AssetCategoryContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateAssetCategoryChain())
                .afterSave(new AddAssetCategoryFeatureStatusCommand())
                .list()
                .summary()
                .afterFetch(new SetAssetCategoryModuleCommandV3())
                .update()
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getDeleteAssetCategoryChain())
                .build();
    }

    @Module("assettype")
    public static Supplier<V3Config> getAssetType() {
        return () -> new V3Config(V3AssetTypeContext.class, null)
                .create()
                .list()
                .update()
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getDeleteAssetTypeChain())
                .build();
    }

    @Module("spacecategory")
    public static Supplier<V3Config> getSpaceCategory() {
        return () -> new V3Config(V3SpaceCategoryContext.class, null)
                .create()
                .list()
                .update()
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getDeleteSpaceCategoryChain())
                .build();
    }

    @Module(FacilioConstants.FaultImpact.MODULE_NAME)
    public static Supplier<V3Config> getFaultImpact() {
        return () -> new V3Config(FaultImpactContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getFaultImpactAddOrUpdateBeforeChain())
                .afterSave(TransactionChainFactoryV3.getFaultImpactAddOrUpdateAfterChain())
                .summary()
                .afterFetch(TransactionChainFactoryV3.getFaultImpactFetchChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getFaultImpactAddOrUpdateBeforeChain())
                .afterSave(TransactionChainFactoryV3.getFaultImpactAddOrUpdateAfterChain())
                .build();
    }

    @Module(FacilioConstants.FaultImpact.FAULT_IMPACT_FIELDS_MODULE_NAME)
    public static Supplier<V3Config> getFaultImpactFields() {
        return () -> new V3Config(FaultImpactNameSpaceFieldContext.class, null)
                .build();
    }

    @Module("assetdepartment")
    public static Supplier<V3Config> getAssetDepartment() {
        return () -> new V3Config(V3AssetDepartmentContext.class, null)
                .create()
                .list()
                .update()
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getDeleteAssetDepartmentChain())
                .build();
    }

    @Module("receivable")
    public static Supplier<V3Config> getReceivable() {
        return () -> new V3Config(V3ReceivableContext.class, null)
                .create()
                .beforeSave(new SetLocalIdCommandV3())
                .update()
                .list()
                .beforeFetch(TransactionChainFactoryV3.getReceivablesBeforeFetchChain())
                .summary()
                .beforeFetch(new LoadReceivableLookupCommandV3())
                .afterFetch(new SetPOLineItemCommandV3())
                .delete()
                .build();
    }

    @Module("receipts")
    public static Supplier<V3Config> getReceipts() {
        return () -> new V3Config(V3ReceiptContext.class, null)
                .create()
                .beforeSave(new SetReceiptTimeAndLocalIdCommand())
                .afterSave(TransactionChainFactoryV3.getAddOrUpdateReceiptsChain())
                .update()
                .list()
                .beforeFetch(TransactionChainFactoryV3.getReceiptsBeforeFetchListChain())
                .summary()
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM)
    public static Supplier<V3Config> getSensorRollupAlarm() {
        return () -> new V3Config(SensorRollUpAlarmContext.class, null)
                .update()
                .list()
                .beforeFetch(new LoadSupplementsForSensorAlarmCommand())
                .summary()
                .beforeFetch(new LoadSupplementsForSensorAlarmCommand())
                .afterFetch(new LoadOccurrenceForAlarmCommand())
                .delete()
                .afterDelete(new AfterDeleteAlarmCommand())
                .build();
    }


    @Module(FacilioConstants.ContextNames.NEW_READING_ALARM)
    public static Supplier<V3Config> getNewReadingAlarm() {
        return () -> new V3Config(ReadingAlarm.class, new ModuleCustomFieldCount30())
                .update()
                .afterSave(new UpdateOccurrenceCommand())
                .list()
                .beforeFetch(new LoadSupplementsForFaultsCommand())
                .afterFetch(new HandleV3AlarmListLookupCommand())
                .summary()
                .beforeFetch(new LoadSupplementsForFaultsCommand())
                .afterFetch(TransactionChainFactoryV3.getReadingAlarmAfterFetchChain())
                .delete()
                .afterDelete(new AfterDeleteAlarmCommand())
                .build();
    }

    @Module(FacilioConstants.ContextNames.READING_EVENT)
    public static Supplier<V3Config> getReadingEvent() {
        return () -> new V3Config(ReadingEventContext.class, new ModuleCustomFieldCount30())
                .build();
    }

    @Module(FacilioConstants.ContextNames.READING_ALARM_OCCURRENCE)
    public static Supplier<V3Config> getReadingAlarmOccurrence() {
        return () -> new V3Config(ReadingAlarmOccurrenceContext.class, new ModuleCustomFieldCount30())
                .list()
                .beforeFetch(new LoadSupplementsForAlarmOccurrenceCommand())
                .build();
    }

    @Module(FacilioConstants.ModuleNames.WEATHER_SERVICE)
    public static Supplier<V3Config> getWeatherService() {
        return () -> new V3Config(V3WeatherServiceContext.class, null)
                .create()
                .beforeSave(new WeatherServiceCreateValidationCommand())
                .afterSave(new AddWeatherServiceJobCommand())
                .update()
                .beforeSave(new WeatherServiceUpdateValidationCommand())
                .afterSave(new AddWeatherServiceJobCommand())
                .list()
                .summary()
                .delete()
                .beforeDelete(new DeleteWeatherServiceJobCommand())
                .build();
    }

    @Module(FacilioConstants.ModuleNames.WEATHER_STATION)
    public static Supplier<V3Config> getWeatherStation() {
        return () -> new V3Config(V3WeatherStationContext.class, null)
                .create()
                .beforeSave(new WeatherStationCreateValidationCommand())
                .afterSave(TransactionChainFactoryV3.addReadingDataMetaForReadingModule())
                .update()
                .beforeSave(new WeatherStationUpdateValidationCommand())
                .list()
                .beforeFetch(new WeatherStationLoadSupplementCommand())
                .summary()
                .beforeFetch(new WeatherStationLoadSupplementCommand())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.BMS_ALARM)
    public static Supplier<V3Config> getBmsAlarm() {
        return () -> new V3Config(BaseAlarmContext.class, new ModuleCustomFieldCount30())
                .update()
                .afterSave(new UpdateOccurrenceCommand())
                .list()
                .beforeFetch(new LoadSupplementsForBMSAlarmCommand())
                .summary()
                .beforeFetch(new LoadSupplementsForBMSAlarmCommand())
                .afterFetch(new LoadOccurrenceForAlarmCommand())
                .delete()
                .afterDelete(new AfterDeleteAlarmCommand())
                .build();
    }

    @Module(FacilioConstants.ContextNames.AGENT_ALARM)
    public static Supplier<V3Config> getAgentAlarm() {
        return () -> new V3Config(AgentAlarmContext.class, new ModuleCustomFieldCount30())
                .update()
                .afterSave(new UpdateOccurrenceCommand())
                .list()
                .beforeFetch(new LoadSupplementsForAgentAlarmCommand())
                .summary()
                .beforeFetch(new LoadSupplementsForAgentAlarmCommand())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
    public static Supplier<V3Config> getAlarmOccurrence() {
        return () -> new V3Config(AlarmOccurrenceContext.class, null)
                .list()
                .beforeFetch(new LoadSupplementsForAlarmOccurrenceCommand())
                .summary()
                .beforeFetch(new LoadSupplementsForAlarmOccurrenceCommand())
                .build();
    }

    @Module(FacilioConstants.ContextNames.BMS_EVENT)
    public static Supplier<V3Config> getBmsEvent() {
        return () -> new V3Config(BMSEventContext.class, new ModuleCustomFieldCount30())
                .list()
                .summary()
                .build();
    }

    @Module(FacilioConstants.ContextNames.BMS_ALARM_OCCURRENCE)
    public static Supplier<V3Config> getBmsAlarmOccurrence() {
        return () -> new V3Config(BMSAlarmOccurrenceContext.class, new ModuleCustomFieldCount30())
                .build();
    }

    @Module(FacilioConstants.ContextNames.BASE_EVENT)
    public static Supplier<V3Config> getBaseEvent() {
        return () -> new V3Config(BaseEventContext.class, null)
                .list()
                .beforeFetch(new LoadSupplementsForBaseEventCommand())
                .summary()
                .build();
    }

	@Module(FacilioConstants.CraftAndSKills.SKILLS)
	public static Supplier<V3Config> getCraftSkills() {
		return () -> new V3Config(SkillsContext.class, new ModuleCustomFieldCount30())
							 .list()
							 .delete()
							 .build();
	}

    @Module(FacilioConstants.CraftAndSKills.LABOUR_CRAFT)
    public static Supplier<V3Config> getLabourCrafts() {
        return () -> new V3Config(LabourCraftAndSkillContext.class, new ModuleCustomFieldCount30())
                .list()
                .beforeFetch(new LoadSupplementsForLabourCraftCommand())
                .delete()
                .build();

    }

    @Module(FacilioConstants.Routes.NAME)
    public static Supplier<V3Config> getRoute() {
        return () -> new V3Config(RoutesContext.class, null)
                .create()
                .update()
                .delete()
                .list()
                .summary()
                .build();
    }

    @Module(FacilioConstants.CraftAndSKills.CRAFT)
    public static Supplier<V3Config> getCrafts() {
        return () -> new V3Config(CraftContext.class, new ModuleCustomFieldCount30())
                .create()
                .list()
                .summary()
                .afterFetch(new FetchCraftAndSkillsCommand())
                .build();
    }

    @Module(FacilioConstants.Break.BREAK)
    public static Supplier<V3Config> getBreak() {
        return () -> new V3Config(Break.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getBreakBeforeSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getBreakBeforeUpdateChain())
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getBreakBeforeDeleteChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBreakBeforeListChain())
                .summary()
                .build();

    }

    @Module(FacilioConstants.Shift.SHIFT)
    public static Supplier<V3Config> getShift() {
        return () -> new V3Config(Shift.class, new ModuleCustomFieldCount15())
                .create()
                .beforeSave(TransactionChainFactoryV3.getShiftBeforeSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getShiftBeforeUpdateChain())
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getShiftBeforeDeleteChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getShiftBeforeListChain())
                .summary()
                .afterFetch(TransactionChainFactoryV3.getShiftAfterSummaryChain())
                .build();
    }

    @Module(FacilioConstants.ContextNames.PEOPLE)
    public static Supplier<V3Config> getPeoples() {
        return () -> new V3Config(V3PeopleContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getPeopleBeforeSaveChain())
                .afterSave(new PeopleAfterSaveCommand())
                .update()
                .beforeSave(new PeopleValidationCommandV3())
                .afterSave(TransactionChainFactoryV3.getPeopleAfterSaveChain())
                .list()
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleAfterListChain())
                .beforeFetch(new PeopleBeforeListCommand())
                .beforeCount(new PeopleBeforeListCommand())
                .summary()
                .afterFetch(ReadOnlyChainFactoryV3.getPeopleAfterFetchChain())
                .delete()
                .beforeDelete(new DeletePeopleAssociatedRecordsCommand())
                .build();
    }


    @Module(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER)
    public static Supplier<V3Config> getOutgoingMailLogger() {
        return () -> new V3Config(V3OutgoingMailLogContext.class, null)
                .list()
//                .beforeFetch(new FilterOutFailedMailLogsCommand())
                .afterFetch(new UpdateMailRecordsModuleNameCommand())
                .summary()
                .afterFetch(MailReadOnlyChainFactory.getAfterFetchMailLoggerSummaryChain())
                .build();
    }

    @Module(MailConstants.ModuleNames.OUTGOING_RECIPIENT_LOGGER)
    public static Supplier<V3Config> getOutgoingRecipientLogger() {
        return () -> new V3Config(V3OutgoingRecipientContext.class, null)
                .list()
                .beforeFetch(MailReadOnlyChainFactory.getBeforeFetchMailRecipientListChain())
                .summary()
                .beforeFetch(new OutgoingRecipientLoadSupplementsCommand())
                .build();
    }

    @Module(MailConstants.ModuleNames.OUTGOING_MAIL_ATTACHMENTS)
    public static Supplier<V3Config> getOutgoingMailAttachments() {
        return () -> new V3Config(V3OutgoingMailAttachmentContext.class, null)
                .build();
    }

    @Module(FacilioConstants.ReadingKpi.READING_KPI)
    public static Supplier<V3Config> getReadingKpi() {
        return () -> new V3Config(ReadingKPIContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.addReadingKpi())
                .afterSave(TransactionChainFactoryV3.addReadingKpiNamespace())
                .update()
                .beforeSave(new NullFieldIdAndModuleIdForDynamicKpi())
                .afterSave(TransactionChainFactoryV3.updateReadingKpiWorkflowAndNamespace())
                .list()
                .beforeFetch(new LoadSupplementsForReadingKPICommand())
                .afterFetch(TransactionChainFactoryV3.getNameSpaceAndSupplements())
                .summary()
                .beforeFetch(new LoadSupplementsForReadingKPICommand())
                .afterFetch(TransactionChainFactoryV3.getNameSpaceAndSupplements())
                .delete()
                .beforeDelete(new DeleteKpiLoggersCommand())
                .afterDelete(new ReadingKpiPostDeleteCommand())
                .build();

    }

    @Module(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE)
    public static Supplier<V3Config> getKpiLogger() {
        return () -> new V3Config(KpiLoggerContext.class, null)
                .list()
                .beforeFetch(new LoadSupplementsForKpiLogger())
                .build();
    }

    @Module(FacilioConstants.ReadingKpi.READING_KPI_LOGS_MODULE)
    public static Supplier<V3Config> getKpiLogs() {
        return () -> new V3Config(ReadingKpiLogsContext.class, null)
                .list()
                .beforeFetch(TransactionChainFactoryV3.beforeFetchKpiLogsChain())
                .build();
    }

    @Module(FacilioConstants.ReadingRules.READING_RULE_LOGS_MODULE)
    public static Supplier<V3Config> getRuleLogs() {
        return () -> new V3Config(ReadingRuleLogsContext.class, null)
                .list()
                .beforeFetch(TransactionChainFactoryV3.beforeFetchRuleLogsChain())
                .build();
    }

    @Module(FacilioConstants.ReadingRules.NEW_READING_RULE)
    public static Supplier<V3Config> getReadingRule(){
        return () -> new V3Config(NewReadingRuleContext.class,null )
                .create()
                .beforeSave(TransactionChainFactoryV3.addReadingRuleChain())
                .afterSave(TransactionChainFactoryV3.afterSaveReadingRuleChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.beforeUpdateReadingRuleChain())
                .afterSave(TransactionChainFactoryV3.updateReadingRuleChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.beforeFetchReadingRuleSummaryChain())
                .afterFetch(TransactionChainFactoryV3.fetchReadingRuleSummaryChain())
                .summary()
                .beforeFetch(TransactionChainFactoryV3.beforeFetchReadingRuleSummaryChain())
                .afterFetch(TransactionChainFactoryV3.fetchReadingRuleSummaryChain())
                .delete()
                .afterDelete(TransactionChainFactoryV3.afterDeleteReadingRuleRcaChain())
                .build();
    }

    @Module(FacilioConstants.ContextNames.SENSOR_RULE_MODULE)
    public static Supplier<V3Config> getSensorRule() {
        return () -> new V3Config(SensorRuleContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.addSensorRuleChain())
                .afterSave(TransactionChainFactoryV3.addSensorRuleTypeAndNsChain())
                .summary()
                .afterFetch(new FetchSensorRuleSummaryCommand())
                .update()
                .beforeSave(new PrepareSensorRuleForUpdateCommand())
                .afterSave(new UpdateSensorRuleCommand())
                .build();
    }

    @Module(FacilioConstants.PeopleGroup.PEOPLE_GROUP)
    public static Supplier<V3Config> getPeopleGroups() {
        return () -> new V3Config(V3PeopleGroupContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetOuIdInPeopleGroupMemberCommand())
                .list()
                .afterFetch(new FetchPeopleGroupMembersCommand())
                .update()
                .beforeSave(new SetOuIdInPeopleGroupMemberCommand())
                .delete()
                .build();
    }


    @Module(FacilioConstants.ContextNames.WORKORDER_FAILURE_CLASS_RELATIONSHIP)
    public static Supplier<V3Config> getWorkOrderFailureCodeRelationship() {
        return () -> new V3Config(WorkOrderFailureClassRelationship.class, null)
                .create()
                .list()
                .beforeFetch(new FetchSubModuleRelationSupplements())
                .summary()
                .update()
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.CUSTOM_MODULE_DATA_FAILURE_CLASS_RELATIONSHIP)
    public static Supplier<V3Config> getCustomModuleDataFailureCodeRelationship() {
        return () -> new V3Config(CustomModuleDataFailureClassRelationship.class, null)
                .create()
                .list()
                .beforeFetch(new FetchSubModuleRelationSupplements())
                .summary()
                .update()
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.CLASSIFICATION_ATTRIBUTE)
    public static Supplier<V3Config> getClassificationAttributeHandler() {
        return () -> new V3Config(ClassificationAttributeContext.class,  null)
                .list()
                .beforeFetch(ClassificationAttributeChain.getBeforeListChain())
                .create()
                .beforeSave(new BeforeSaveClassificationAttributeCommand())
                .update()
                .beforeSave(new BeforeUpdateClassificationAttributeCommand())
                .summary()
                .delete()
                .beforeDelete(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        throw new IllegalArgumentException("Delete is not supported");
                    }
                })
                .build();
    }

    @Module(FacilioConstants.ContextNames.CLASSIFICATION)
    public static Supplier<V3Config> getClassificationHandler() {
        return () -> new V3Config(ClassificationContext.class, null)
                .list()
                .beforeFetch(ClassificationChain.getBeforeListChain())
                .afterFetch(ClassificationChain.getAfterListChain())
                .fetchSupplement(FacilioConstants.ContextNames.CLASSIFICATION, "parentClassification")
                .create()
                .beforeSave(new BeforeSaveClassificationCommand())
                .afterSave(ClassificationChain.getAfterSaveChain())
                .update()
                .beforeSave(ClassificationChain.getBeforeUpdateChain())
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.CLASSIFICATION, "parentClassification")
                .afterFetch(ClassificationChain.getAfterSummaryChain())
                .delete()
                .beforeDelete(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        throw new IllegalArgumentException("Delete is not supported");
                    }
                }).build();
    }


    @Module(FacilioConstants.ContextNames.JOB_PLAN_CRAFTS)
    public static Supplier<V3Config> getJobPlanCrafts() {
        return () -> new V3Config(JobPlanCraftsContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadJobPlanCraftsLookUpCommandV3())
                .summary()
                .beforeFetch(new LoadJobPlanCraftsLookUpCommandV3())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.PRECAUTION)
    public static Supplier<V3Config> getPrecautions() {
        return () -> new V3Config(V3PrecautionContext.class, null)
                .create()
                .update()
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchPrecautionChain())
                .summary()
                .delete()
                .afterDelete(new DeleteHazardPrecautionCommand())
                .build();
    }

    @Module(FacilioConstants.ContextNames.HAZARD)
    public static Supplier<V3Config> getHazards() {
        return () -> new V3Config(V3HazardContext.class, null)
                .create()
                .update()
                .list()
                .beforeFetch(TransactionChainFactoryV3.getBeforeFetchSafetyPlanChain())
                .summary()
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.HAZARD_PRECAUTION)
    public static Supplier<V3Config> getHazardPrecautions() {
        return () -> new V3Config(V3HazardPrecautionContext.class, null)
                .create()
                .list()
                .beforeFetch(new V3LoadHazardPrecautionLookUpsCommand())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.SAFETY_PLAN)
    public static Supplier<V3Config> getSafetyPlans() {
        return () -> new V3Config(V3SafetyPlanContext.class, null)
                .create()
                .list()
                .update()
                .summary()
                .delete()
                .build();
    }


    @Module(FacilioConstants.ContextNames.SPACE_BOOKING)
    public static Supplier<V3Config> getSpaceBooking() {
        return () -> new V3Config(V3SpaceBookingContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.createSpaceBookingChain())
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .update().beforeSave(TransactionChainFactoryV3.createSpaceBookingChain())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .delete()
                .list()
                .beforeFetch(TransactionChainFactoryV3.SpaceBookingSupplementsAndExtraFieldsChain())
                .afterFetch(new ValidateBookingCancelCommand())
                .summary()
                .beforeFetch(new SpaceBookingSupplementsCommand())
                .afterFetch(new ValidateBookingCancelCommand())
                .build();
    }
    @Module(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD)
    public static Supplier<V3Config> getSafetyPlanHazard() {
        return () -> new V3Config(V3SafetyPlanHazardContext.class, null)
                .create()
                .beforeSave(new SkipAvailableHazards())
                .list()
                .beforeFetch(new FetchSafetyPlanHazardSupplements())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN)
    public static Supplier<V3Config> getWorkOrderLabourPlan() {
            return () -> new V3Config(V3WorkOrderLabourPlanContext.class, new ModuleCustomFieldCount30())
                            .create()
                            .list()
                            .beforeFetch(TransactionChainFactoryV3.getWorkorderLabourPlanBeforeFetchChain())
                            .update()
                            .delete()
                            .summary()
                            .build();
    }

    @Module(FacilioConstants.ContextNames.JOB_PLAN_LABOURS)
    public static Supplier<V3Config> getJobPlanLabour() {
        return () -> new V3Config(V3JobPlanLabourContext.class, new ModuleCustomFieldCount30())
                .create()
                .list()
                .beforeFetch(TransactionChainFactoryV3.getJobPlanLabourBeforeFetchChainV3())
                .update()
                .delete()
                .summary()
                .build();
    }
    @Module(FacilioConstants.ContextNames.WORKORDER_HAZARD)
    public static Supplier<V3Config> getWorkOrderHazard() {
        return () -> new V3Config(V3WorkorderHazardContext.class, null)
                .create()
                .afterSave(new AddPrecautionsFromWorkOrderHazardCommandV3())
                .list()
                .beforeFetch(new FetchWorkOrderHazardSupplements())
                .delete()
                .afterDelete(new DeleteWorkorderPrecaution())
                .build();
    }

    @Module(FacilioConstants.ContextNames.WO_LABOUR)
    public static Supplier<V3Config> getWorkOrderLabour() {
            return () -> new V3Config(V3WorkOrderLabourContext.class, new ModuleCustomFieldCount30())
                            .create()
                            .beforeSave(new ValidateWorkOrderLabourPlanCommandV3())
                            .afterSave(TransactionChainFactoryV3.getAddOrUdpateWorkorderLabourChainV3())
                            .list()
                            .beforeFetch(TransactionChainFactoryV3.getWorkorderLabourBeforeFetchChain())
                            .update()
                            .afterSave(TransactionChainFactoryV3.getAddOrUdpateWorkorderLabourChainV3())
                            .delete()
                            .afterDelete(TransactionChainFactoryV3.getAddOrUdpateWorkorderLabourChainV3())
                            .build();
    }
    @Module(FacilioConstants.ContextNames.WORKORDER_HAZARD_PRECAUTION)
    public static Supplier<V3Config> getWorkOrderHazardPrecaution() {
        return () -> new V3Config(V3WorkorderHazardPrecautionContext.class, null)
                .create()
                .list()
                .beforeFetch(new FetchWorkOrderHazardPrecautionSupplements())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.ASSET_HAZARD)
    public static Supplier<V3Config> getAssetHazard() {
        return () -> new V3Config(V3AssetHazardContext.class, null)
                .create()
                .list()
                .beforeFetch(new V3LoadAssetHazardLookUpsCommand())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.BASESPACE_HAZARD)
    public static Supplier<V3Config> getBaseSpaceHazard() {
        return () -> new V3Config(V3BaseSpaceHazardContext.class, null)
                .create()
                .list()
                .beforeFetch(new V3LoadBaseSpaceHazardLookUpsCommand())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.SpaceBooking.EXTERNAL_ATTENDEE)
    public static Supplier<V3Config> getExternalAttendee() {
        return () -> new V3Config(V3ExternalAttendeeContext.class, null)
                .create()
                .list()
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.WORK_ASSET)
    public static Supplier<V3Config> getWorkAsset() {
        return () -> new V3Config(V3WorkAssetContext.class, null)
                .create()
                .beforeSave(new AddSpaceAndAssetCommand())
                .list()
                .beforeFetch(new V3LoadWorkAssetLookUpsCommand())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ContextNames.ASSET_DEPRECIATION_REL)
    public static Supplier<V3Config> getAssetDepreciationRel() {
        return () -> new V3Config(V3AssetDepreciationRelContext.class,new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ValidateAssetDepreciationRelCommand(), new CheckAssetDepreciationExistingCommand())
                .afterSave(new SaveDepreciationLastCalCommand())
                .list()
                .update()
                .beforeSave(new ValidateAssetDepreciationRelCommand(),new DeleteAssetDepreciationCalCommand())
                .afterSave(new SaveDepreciationLastCalCommand())
                .build();
    }

    @Module(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY)
    public static Supplier<V3Config> getSpaceBookingPolicy() {
        return () -> new V3Config(V3SpaceBookingPolicyContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getAddPolicyChain())
                .list()
                .summary()
                .afterFetch(new FetchCriteriaObjectCommand())
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.CUSTOM_KIOSK_BUTTON)
    public static Supplier<V3Config> getCustomKioskButton() {
        return () -> new V3Config(V3CustomKioskButtonContext.class, null)
                .create()
                .update()
                .list()
                .afterFetch( new GetKioskConnectedCommand())
                .summary()
                .afterFetch( new GetKioskConnectedCommand())
                .delete()
                .beforeDelete(new ValidateBeforeButtonDeletionCommand())
                .build();
    }

    @Module(FacilioConstants.ContextNames.CUSTOM_KIOSK)
    public static Supplier<V3Config> getCustomKiosk() {
        return () -> new V3Config(V3CustomKioskContext.class, null)
                .create()
                .beforeSave(new AddCustomDeviceCommand())
                .update()
                .beforeSave(new AddCustomDeviceCommand())
                .list()
                .beforeFetch(new AddAssociateResourceCommand())
                .summary()
                .beforeFetch(new AddAssociateResourceCommand())
                .afterFetch(new GetCustomkKioskButtonCommand())
                .delete()
                .build();
    }


    @Module(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION)
    public static Supplier<V3Config> getAttendanceTransaction() {
        return () -> new V3Config(AttendanceTransaction.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getAttendanceTxnBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getAttendanceTxnAfterSaveChain())
                .afterTransaction(TransactionChainFactoryV3.getAttendanceTxnAfterTransactionChain())
                .list()
                .summary()
                .delete()
                .update()
                .beforeSave(TransactionChainFactoryV3.getAttendanceTxnBeforeSaveChain())
                .afterTransaction(TransactionChainFactoryV3.getAttendanceTxnAfterTransactionChain())
                .build();
    }

    @Module(FacilioConstants.Attendance.ATTENDANCE)
    public static Supplier<V3Config> getAttendance() {
        return () -> new V3Config(Attendance.class, null)
                .create()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.DECOMMISSION_LOG)
    public static Supplier<V3Config> getDecommissionLog(){
        return () -> new V3Config(DecommissionLogContext.class,null)
                .create()
                .list()
                .beforeFetch(new DecommissionLogLookupFieldsCommand())
                .summary()
                .update()
                .build();
    }
    
    @Module(com.facilio.backgroundactivity.factory.Constants.BACKGROUND_ACTIVITY_MODULE)
    public static Supplier<V3Config> getBackgroundActivityTransaction() {
        return () -> new V3Config(BackgroundActivity.class, null)
                .list()
                .beforeFetch(new FetchBackgroundActivitySupplementsCommand())
                .afterFetch(ReadOnlyChainFactoryV3.backgroundActivityFetchChain())
                .summary()
                .beforeFetch(new FetchBackgroundActivitySupplementsCommand())
                .afterFetch(ReadOnlyChainFactoryV3.backgroundActivityFetchChain())
                .build();
    }
    
    @Module(FacilioConstants.Workflow.WORKFLOW_VERSION_HISTORY)
    public static Supplier<V3Config> getWorkfowVersionHistory(){
        return () -> new V3Config(WorkflowVersionHistoryContext.class,null)
        		.create()
        		.beforeSave(TransactionChainFactoryV3.getWorkflowVersionHistoryBeforeSaveCommand())
                .list()
                .beforeFetch(new FillWorkflowVersionHistorySupplementsCommand())
        		.build();
    }
    
    @Module(FacilioConstants.ContextNames.BASE_SPACE)
    public static Supplier<V3Config> getBaseSpace(){
        return () -> new V3Config(V3BaseSpaceContext.class,null)
                .list()
                .pickList()
                .setFourthField(FacilioConstants.ContextNames.BASE_SPACE, FacilioConstants.ContextNames.DECOMMISSION)
                .beforeFetch(new DecommissionPicklistCheckCommand())
                .build();
    }
    @Module(FacilioConstants.ContextNames.RESOURCE)
    public static Supplier<V3Config> getResource(){
        return () -> new V3Config(V3ResourceContext.class,null)
                .list()
                .pickList()
                .setFourthField(FacilioConstants.ContextNames.RESOURCE, FacilioConstants.ContextNames.DECOMMISSION)
                .beforeFetch(new DecommissionPicklistCheckCommand())
                .build();
    }
    @Module(FacilioConstants.ContextNames.AGENT_DATA_LOGGER)
    public static Supplier<V3Config> getAgentData() {
        return () -> new V3Config(DataLogContextV3.class, null)
                .list()
                .beforeFetch(new FetchAgentDataLoggerSupplementsCommand() )
                .summary()
                .build();
    }
    @Module(FacilioConstants.ContextNames.AGENT_DATA_PROCESSING_LOGGER)
    public static Supplier<V3Config> getAgentDataSummary(){
        return () -> new V3Config(DataLogSummaryContextV3.class,null)
                .list()
                .beforeFetch(new FetchAgentDataSummarySupplementsCommand() )
                .afterFetch(new AgentLoggerSummaryAfterFetchCommand())
                .build();
    }
    @Module(FacilioConstants.Calendar.CALENDAR_MODULE_NAME)
    public static Supplier<V3Config> getCalendar(){
        return () -> new V3Config(V3CalendarContext.class,null)
                .create()
                .afterSave(TransactionChainFactoryV3.getCalendarAfterCreateChain())
                .afterTransaction(new CalendarSlotCreateCommand())
                .update()
                .afterSave(TransactionChainFactoryV3.getCalendarAfterUpdateChain())
                .afterTransaction(new CalendarSlotCreateCommand())
                .delete().markAsDeleteByPeople()
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getCalendarBeforeListChain())
                .afterFetch(TransactionChainFactoryV3.getCalendarAfterFetchChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getCalendarBeforeListChain())
                .build();
    }
    @Module(FacilioConstants.Calendar.EVENT_MODULE_NAME)
    public static Supplier<V3Config> getCalendarEvent(){
        return () -> new V3Config(V3EventContext.class,null)
                .create()
                .afterSave(TransactionChainFactoryV3.getEventAfterCreateChain())
                .update()
                .afterSave(TransactionChainFactoryV3.getEventAfterUpdateChain())
                .afterTransaction(new CalendarSlotCreateCommand())
                .delete().markAsDeleteByPeople()
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getEventBeforeFetchChain())
                .afterFetch(TransactionChainFactoryV3.getEventAfterFetchChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getEventBeforeFetchChain())
                .afterFetch(TransactionChainFactoryV3.getEventAfterFetchChain())
                .build();
    }
    @Module(FacilioConstants.Calendar.EVENT_TIME_SLOT_MODULE_NAME)
    public static Supplier<V3Config> getEventTimeSlot(){
        return () -> new V3Config(V3EventTimeSlotContext.class,null)
                .create()
                .update()
                .summary()
                .list()
                .delete().markAsDeleteByPeople()
                .build();
    }
    @Module(FacilioConstants.Calendar.CALENDAR_SLOTS_MODULE_NAME)
    public static Supplier<V3Config> getCalendarSlots(){
        return () -> new V3Config(V3CalendarSlotsContext.class,null)
                .create()
                .update()
                .delete().markAsDeleteByPeople()
                .summary()
                .list()
                .build();
    }
    @Module(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME)
    public static Supplier<V3Config> getCalendarEventMapping(){
        return () -> new V3Config(V3CalendarEventMappingContext.class,null)
                .create()
                .update()
                .delete().markAsDeleteByPeople()
                .summary()
                .list()
                .beforeFetch(TransactionChainFactoryV3.getCalendarEventMappingBeforeListChain())
                .afterFetch(TransactionChainFactoryV3.getCalendarEventMappingAfterListChain())
                .build();
    }
    @Module(FacilioConstants.Calendar.CALENDAR_TIME_SLOT_MODULE_NAME)
    public static Supplier<V3Config> getCalendarTimeSlot(){
        return () -> new V3Config(V3CalendarTimeSlotContext.class,null)
                .create()
                .update()
                .delete().markAsDeleteByPeople()
                .summary()
                .list()
                .build();
    }

    @Module("meter")
    public static Supplier<V3Config> getMeter() {
        return () -> new V3Config(V3MeterContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateMeterBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getCreateMeterAfterSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getUpdateMeterBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getUpdateMeterAfterSaveChain())
                .delete()
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getMeterSummaryBeforeFetchChain())
                .afterFetch(TransactionChainFactoryV3.getMeterSummaryAfterFetchChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getMeterListBeforeFetchChain())
                .build();
    }

    @Module("utilitytype")
    public static Supplier<V3Config> getUtilityType() {
        return () -> new V3Config(V3UtilityTypeContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateUtilityTypeBeforeSaveChain())
                .list()
                .afterFetch(TransactionChainFactoryV3.getUtilityTypeListAfterFetchChain())
                .summary()
                .afterFetch(TransactionChainFactoryV3.getUtilityTypeSummaryAfterFetchChain())
                .update()
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getUtilityTypeBeforeDeleteChain())
                .build();
    }


    @Module(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE)
    public static Supplier<V3Config> getVirtualMeterTemplate() {
        return () -> new V3Config(VirtualMeterTemplateContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getCreateVirtualMeterTemplateBeforeChain())
                .afterSave(TransactionChainFactoryV3.getCreateVirtualMeterTemplateAfterChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getUpdateVirtualMeterTemplateBeforeChain())
                .afterSave(TransactionChainFactoryV3.getUpdateVirtualMeterTemplateAfterChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getVirtualMeterTemplateListBeforeFetchChain())
                .afterFetch(TransactionChainFactoryV3.getVirtualMeterTemplateListAfterFetchChain())
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getVirtualMeterTemplateSummaryBeforeFetchChain())
                .afterFetch(TransactionChainFactoryV3.getVirtualMeterTemplateSummaryAfterFetchChain())
                .build();
    }

    @Module(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING)
    public static Supplier<V3Config> getVirtualMeterTemplateReading() {
        return () -> new V3Config(VirtualMeterTemplateReadingContext.class, null)
                .create()
                .afterSave(new PopulateNameSpaceAndFieldForVMReadingsCommand())
                .update()
                .afterSave(TransactionChainFactoryV3.getUpdateVirtualMeterTemplateReadingAfterChain())
                .summary()
                .afterFetch(new FetchNameSpaceAndFieldForVMReadingsCommand())
                .build();
    }

    @Module(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGS)
    public static Supplier<V3Config> getWorkflowRuleLogs() {
        return () -> new V3Config(WorkflowRuleLogContext.class, null)
                .list()
                .afterFetch(new GetWorkflowRuleActionLogs())
                .build();
    }

    @Module(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME)
    public static Supplier<V3Config> getControlAction(){
        return () -> new V3Config(V3ControlActionContext.class,null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getControlActionBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getControlActionAfterSaveChain())
                .afterTransaction(new CallToCommandGenerationCommand())
                .update()
                .beforeSave(TransactionChainFactoryV3.getControlActionBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getControlActionAfterUpdateChain())
                //.afterTransaction(new CallToCommandGenerationCommand())
                .delete().markAsDeleteByPeople()
                .beforeDelete(TransactionChainFactoryV3.getControlActionBeforeDeleteChain())
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getControlActionBeforeListChain())
                .afterFetch(TransactionChainFactoryV3.getControlActionAfterFetchChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getControlActionBeforeListChain())
                .build();
    }
    @Module(FacilioConstants.Control_Action.ACTION_MODULE_NAME)
    public static Supplier<V3Config> getAction(){
        return () -> new V3Config(V3ActionContext.class,null)
                .create()
                .update()
                .delete().markAsDeleteByPeople()
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getActionBeforeListChain())
                .afterFetch(TransactionChainFactoryV3.getActionAfterSummaryCommand())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getActionBeforeListChain())
                .afterFetch(TransactionChainFactoryV3.getActionAfterSummaryCommand())
                .build();
    }
    @Module(FacilioConstants.Control_Action.COMMAND_MODULE_NAME)
    public static Supplier<V3Config> getCommand(){
        return () -> new V3Config(V3CommandsContext.class,null)
                .create()
                .update()
                .delete().markAsDeleteByPeople()
                .summary()
                .afterFetch(TransactionChainFactoryV3.getCommandsAfterSummaryChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getCommandsBeforeListChain())
                .afterFetch(TransactionChainFactoryV3.getCommandsAfterListChain())
                .build();
    }
    @Module(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME)
    public static Supplier<V3Config> getControlActionTemplate() {
        return () -> new V3Config(V3ControlActionTemplateContext.class, null)
                .create()
                .beforeSave(TransactionChainFactoryV3.getControlActionTemplateBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getControlActionTemplateAfterSaveChain())
                //.afterTransaction(new CallToControlActionGenerationCommand())
                .update()
                .beforeSave(TransactionChainFactoryV3.getControlActionTemplateBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getControlActionTemplateAfterSaveChain())
                //.afterTransaction(new CallToControlActionGenerationCommand())
                .delete().markAsDeleteByPeople()
                .beforeDelete(TransactionChainFactoryV3.getControlActionTemplateBeforeDeleteChain())
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getControlActionTemplateBeforeListChain())
                .afterFetch(TransactionChainFactoryV3.getControlActionTemplateAfterFetchChain())
                .list()
                .beforeFetch(TransactionChainFactoryV3.getControlActionTemplateBeforeListChain())
                .build();
    }


    @Module(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER)
    public static Supplier<V3Config> getUtilityCustomer() {
        return () -> new V3Config(UtilityIntegrationCustomerContext.class, null)
                .create()
                .afterSave(new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ACTIVITY))
                .update()
                .afterSave(new ConstructUpdateCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ACTIVITY))
                .list()
                .summary()
                .build();
    }
    @Module(FacilioConstants.UTILITY_INTEGRATION_METER)
    public static Supplier<V3Config> getUtilityMeters() {
        return () -> new V3Config(UtilityIntegrationMeterContext.class, new ModuleCustomFieldCount30())
                .create()
                .afterSave(new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.UTILITY_INTEGRATION_METER_ACTIVITY))
                .update()
                .afterSave(new ConstructUpdateCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.UTILITY_INTEGRATION_METER_ACTIVITY))
                .list()
                .beforeFetch( new LoadUtilityIntegrationMeterExtraFieldsCommandV3())
                .summary()
                .build();
    }
    @Module(FacilioConstants.UTILITY_INTEGRATION_BILLS)
    public static Supplier<V3Config> getUtilityBills() {
        return () -> new V3Config(UtilityIntegrationBillContext.class, null)
                .create()
                .afterSave(new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new CheckAndRaiseDisputeCommand(),new AddActivitiesCommand(FacilioConstants.UTILITY_INTEGRATION_BILL_ACTIVITY))
                .update()
                .afterSave(new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.UTILITY_INTEGRATION_BILL_ACTIVITY))
                .list()
                .summary()
                .afterFetch(new FetchUtilityIntegrationDetailsCommandV3())
                .build();
    }
    @Module(FacilioConstants.UTILITY_INTEGRATION_LINE_ITEMS)
    public static Supplier<V3Config> getUtilityLineItems() {
        return () -> new V3Config(UtilityIntegrationLineItemContext.class, new ModuleCustomFieldCount30())
                .list()
                .build();
    }

    @Module(AlarmCategoryModule.MODULE_NAME)
    public static Supplier<V3Config> getAlarmCategoryModule(){
        return () -> new V3Config(AlarmDefinitionMappingContext.class,null)
                .summary()
                .fetchSupplement(AlarmCategoryModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmCategoryModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmCategoryModule.MODULE_NAME,"sysModifiedByPeople")
                .list()
                .fetchSupplement(AlarmCategoryModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmCategoryModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmCategoryModule.MODULE_NAME,"sysModifiedByPeople")
                .create()
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_CATEGORY_ACTIVITY))
                .update()
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_CATEGORY_ACTIVITY))
                .delete()
                .markAsDeleteByPeople()
                .build();
    }

    @Module(FacilioConstants.UTILITY_INTEGRATION_TIER_ITEMS)
    public static Supplier<V3Config> getUtilityTierItems() {
        return () -> new V3Config(UtilityIntegrationTierItemContext.class, new ModuleCustomFieldCount30())
                .list()
                .build();
    }

    @Module(FacilioConstants.UTILITY_INTEGRATION_SUPPLIER_LINE_ITEMS)
    public static Supplier<V3Config> getUtilitySupplierItems() {
        return () -> new V3Config(UtilityIntegrationSupplierLineItemContext.class, new ModuleCustomFieldCount30())
                .list()
                .build();
    }

    @Module(FacilioConstants.UTILITY_INTEGRATION_TOU)
    public static Supplier<V3Config> getUtilityTOU() {
        return () -> new V3Config(UtilityIntegrationTOUContext.class, new ModuleCustomFieldCount30())
                .list()
                .build();
    }
    @Module(FacilioConstants.UTILITY_INTEGRATION_POWER)
    public static Supplier<V3Config> getUtilityPower() {
        return () -> new V3Config(UtilityIntegrationPowerContext.class, new ModuleCustomFieldCount30())
                .list()
                .build();
    }
    @Module(FacilioConstants.UTILITY_INTEGRATION_DEMAND)
    public static Supplier<V3Config> getUtilityDemand() {
        return () -> new V3Config(UtilityIntegrationDemandContext.class, new ModuleCustomFieldCount30())
                .list()
                .build();
    }
    @Module(FacilioConstants.UTILITY_INTEGRATION_TARIFF)
    public static Supplier<V3Config> getUtilityTariff() {
        return () -> new V3Config(UtilityIntegrationTariffContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ValidateUtilityIntegrationTariffCommandV3())
                .update()
                .beforeSave(new ValidateUtilityIntegrationTariffCommandV3())
                .summary()
                .afterFetch(new FetchUtilityIntegrationTariffDetailsCommandV3())
                .delete()
                .list()
                .build();
    }
    @Module(FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB)
    public static Supplier<V3Config> getUtilityTariffSlab() {
        return () -> new V3Config(UtilityIntegrationTariffSlabContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new ValidateUtilityIntegrationTariffSlabCommandV3())
                .update()
                .beforeSave(new ValidateUtilityIntegrationTariffSlabCommandV3())
                .summary()
                .delete()
                .list()
                .build();
    }
    @Module(FacilioConstants.UTILITY_DISPUTE)
    public static Supplier<V3Config> getUtilityDispute() {
        return () -> new V3Config(UtilityDisputeContext.class, new ModuleCustomFieldCount30())
                .create()
                .afterSave(new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.UTILITY_DISPUTE_ACTIVITY))
                .update()
                .afterSave(new ConstructUpdateCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.UTILITY_DISPUTE_ACTIVITY))
                .list()
                .beforeFetch( new LoadUtilityDisputeExtraFieldsCommandV3())
                .fetchSupplement(FacilioConstants.UTILITY_DISPUTE, "account")
                .fetchSupplement(FacilioConstants.UTILITY_DISPUTE, "utilityBill")
                .fetchSupplement(FacilioConstants.UTILITY_DISPUTE, "resolvedBy")
                .summary()
                .fetchSupplement(FacilioConstants.UTILITY_DISPUTE, "account")
                .fetchSupplement(FacilioConstants.UTILITY_DISPUTE, "utilityBill")
                .fetchSupplement(FacilioConstants.UTILITY_DISPUTE, "resolvedBy")
                .build();
    }
    @Module(AlarmDefinitionModule.MODULE_NAME)
    public static Supplier<V3Config> getAlarmDefinitionModule(){
        return () -> new V3Config(AlarmDefinitionMappingContext.class,null)
                .summary()
                .fetchSupplement(AlarmDefinitionModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmDefinitionModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmDefinitionModule.MODULE_NAME,"sysModifiedByPeople")
                .list()
                .fetchSupplement(AlarmDefinitionModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmDefinitionModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmDefinitionModule.MODULE_NAME,"sysModifiedByPeople")
                .create()
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_DEFINITION_ACTIVITY))
                .update()
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_DEFINITION_ACTIVITY))
                .delete()
                .markAsDeleteByPeople()
                .build();
    }

    @Module(AlarmDefinitionMappingModule.MODULE_NAME)
    public static Supplier<V3Config> getAlarmDefinitionMapping(){
        return () -> new V3Config(AlarmDefinitionMappingContext.class,null)
                .list()
                .fetchSupplement(AlarmDefinitionMappingModule.MODULE_NAME,"alarmDefinition")
                .fetchSupplement(AlarmDefinitionMappingModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmDefinitionMappingModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmDefinitionMappingModule.MODULE_NAME,"sysModifiedByPeople")
                .summary()
                .fetchSupplement(AlarmDefinitionMappingModule.MODULE_NAME,"alarmDefinition")
                .fetchSupplement(AlarmDefinitionMappingModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmDefinitionMappingModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmDefinitionMappingModule.MODULE_NAME,"sysModifiedByPeople")
                .create()
                .beforeSave(new AlarmDefinitionValidationCommand(), new AlarmDefinitionMappingInvalidateCacheCommand())
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_DEFINITION_MAPPING_ACTIVITY))
                .update()
                .beforeSave(new AlarmDefinitionValidationCommand(),new AlarmDefinitionMappingInvalidateCacheCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_DEFINITION_MAPPING_ACTIVITY))
                .delete()
                .markAsDeleteByPeople()
                .afterDelete(new AlarmDefinitionMappingInvalidateCacheCommand())
                .build();
    }

    @Module(RawAlarmModule.MODULE_NAME)
    public static Supplier<V3Config> getRawAlarmModule(){
        return () -> new V3Config(RawAlarmContext.class,null)
                .list()
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"client")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"site")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"alarmType")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"alarmDefinition")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"alarmCategory")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"asset")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"controller")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"sysModifiedByPeople")
                .summary()
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"client")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"site")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"alarmType")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"alarmDefinition")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"alarmCategory")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"asset")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"controller")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(RawAlarmModule.MODULE_NAME,"sysModifiedByPeople")
                .delete()
                .markAsDeleteByPeople()
                .create()
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.RAW_ALARM_ACTIVITY))
                .update()
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.RAW_ALARM_ACTIVITY))
                .build();
    }

    @Module(AlarmTypeModule.MODULE_NAME)
    public static Supplier<V3Config> getAlarmTypeModule(){
        return () -> new V3Config(AlarmTypeContext.class,null)
                .summary()
                .fetchSupplement(AlarmTypeModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmTypeModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmTypeModule.MODULE_NAME,"sysModifiedByPeople")
                .list()
                .fetchSupplement(AlarmTypeModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmTypeModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmTypeModule.MODULE_NAME,"sysModifiedByPeople")
                .create()
                .beforeSave(new ValidateAlarmTypeCommand())
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_TYPE_ACTIVITY))
                .update()
                .beforeSave(new ValidateAlarmTypeCommand())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_TYPE_ACTIVITY))
                .delete()
                .afterDelete(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        String moduleName = Constants.getModuleName(context);
                        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
                        List<AlarmTypeContext> alarmTypes = recordMap.get(moduleName);
                        if(CollectionUtils.isNotEmpty(alarmTypes)) {
                            for(AlarmTypeContext alarmType : alarmTypes) {
                                if(alarmType.isUncategorisedAlarm()) {
                                    throw new IllegalArgumentException("Deleting undefined alarm type is not allowed");
                                }
                            }
                        }
                        return false;
                    }
                })
                .markAsDeleteByPeople()
                .build();
    }

    @Module(AlarmDefinitionTaggingModule.MODULE_NAME)
    public static Supplier<V3Config> getAlarmDefinitionTaggingModule(){
        return () -> new V3Config(AlarmDefinitionTaggingContext.class,null)
                .create()
                .beforeSave(new ValidateAlarmDefinitionTaggingModule())
                .afterSave(new AlarmDefinitionTaggingInvalidateCacheCommand(),new ConstructAddCustomActivityCommandV3(),new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_DEFINITION_TAGGING_ACTIVITY))
                .update()
                .beforeSave(new ValidateAlarmDefinitionTaggingModule())
                .afterSave(new AlarmDefinitionTaggingInvalidateCacheCommand(),new ConstructUpdateCustomActivityCommandV3(),new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_DEFINITION_TAGGING_ACTIVITY))
                .list()
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"alarmType")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"alarmDefinition")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"alarmCategory")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"controllerType")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"sysModifiedByPeople")
                .summary()
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"alarmType")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"alarmDefinition")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"alarmCategory")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"controllerType")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmDefinitionTaggingModule.MODULE_NAME,"sysModifiedByPeople")
                .delete()
                .markAsDeleteByPeople()
                .afterDelete(new AlarmDefinitionTaggingInvalidateCacheCommand())
                .build();
    }

    @Module(AlarmFilterRuleModule.MODULE_NAME)
    public static Supplier<V3Config> getAlarmFilterRuleModule(){
        return () -> new V3Config(AlarmFilterRuleContext.class,null)
                .create()
                .beforeSave(new AddSiteAndControllerCriteriaCommand())
                .afterSave(new AlarmFilterRuleInvalidateCacheCommand(),new DeleteScheduledJobsForAlarmRule(),new AddAlarmFilterRuleCriteriaCommand(),new AlarmNotReceivedForDurationJobScheduler(),
                        new ConstructAddCustomActivityCommandV3(),new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_FILTER_RULE_ACTIVITY))
                .update()
                .beforeSave(new AddSiteAndControllerCriteriaCommand())
                .afterSave(new AlarmFilterRuleInvalidateCacheCommand(),new DeleteScheduledJobsForAlarmRule(),new DeleteExistingCriteriaForFilterRuleCommand(),new AddAlarmFilterRuleCriteriaCommand(),new AlarmNotReceivedForDurationJobScheduler(),
                        new ConstructUpdateCustomActivityCommandV3(),new AddActivitiesCommandV3(AddSubModuleRelations.ALARM_FILTER_RULE_ACTIVITY))
                .list()
                .fetchSupplement(AlarmFilterRuleModule.MODULE_NAME,"alarmType")
                .fetchSupplement(AlarmFilterRuleModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmFilterRuleModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmFilterRuleModule.MODULE_NAME,"sysModifiedByPeople")
                .summary()
                .fetchSupplement(AlarmFilterRuleModule.MODULE_NAME,"alarmType")
                .fetchSupplement(AlarmFilterRuleModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmFilterRuleModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmFilterRuleModule.MODULE_NAME,"sysModifiedByPeople")
                .afterFetch(ReadOnlyChainFactoryV3.getAlarmFilterRuleSummaryAfterFetchChain())
                .delete()
                .markAsDeleteByPeople()
                .afterDelete(new AlarmFilterRuleInvalidateCacheCommand(),new DeleteScheduledJobsForAlarmRule())
                .build();
    }

    @Module(AlarmFilterRuleCriteriaModule.MODULE_NAME)
    public static Supplier<V3Config> getAlarmFilterRuleCriteriaModule(){
        return () -> new V3Config(FilterRuleCriteriaContext.class,null)
                .create()
                .update()
                .list()
                .fetchSupplement(AlarmFilterRuleCriteriaModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmFilterRuleCriteriaModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmFilterRuleCriteriaModule.MODULE_NAME,"sysModifiedByPeople")
                .fetchSupplement(AlarmFilterRuleCriteriaModule.MODULE_NAME,"alarmDefinition")
                .summary()
                .fetchSupplement(AlarmFilterRuleCriteriaModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(AlarmFilterRuleCriteriaModule.MODULE_NAME,"sysModifiedByPeople")
                .fetchSupplement(AlarmFilterRuleCriteriaModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmFilterRuleCriteriaModule.MODULE_NAME,"alarmDefinition")
                .delete()
                .markAsDeleteByPeople()
                .build();
    }

    @Module(FilteredAlarmModule.MODULE_NAME)
    public static Supplier<V3Config> getFilteredAlarmModule(){
        return () -> new V3Config(FilteredAlarmContext.class,null)
                .update()
                .afterSave(new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.FILTER_ALARM_ACTIVITY))
                .create()
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.FILTER_ALARM_ACTIVITY))
                .list()
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"site")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"client")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"controller")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"alarmCategory")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"alarmType")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"rawAlarm")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"asset")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"flaggedEventRule")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"flaggedEvent")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"sysModifiedByPeople")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"alarmFilterRule")
                .delete()
                .markAsDeleteByPeople()
                .summary()
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"site")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"client")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"controller")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"alarmCategory")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"alarmType")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"rawAlarm")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"asset")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"flaggedEventRule")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"flaggedEvent")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"sysModifiedByPeople")
                .fetchSupplement(FilteredAlarmModule.MODULE_NAME,"alarmFilterRule")
                .build();
    }

    @Module(FlaggedEventRuleModule.MODULE_NAME)
    public static Supplier<V3Config> getFlaggedEventRuleModule() {
        return () -> new V3Config(FlaggedEventRuleContext.class,null)
                .create()
                .beforeSave(new AddFlaggedEventRuleSiteAndControllerCriteriaCommand())
                .afterSave(new AddFlaggedEventClosureConfigCommand(),new DeleteAndAddRuleAttachmentsCommand(), new DeleteFlaggedEventEmailNotificationRuleCommand(),new FlaggedEventRuleInvalidateCacheCommand(),
                        new AddFlaggedEventRuleAlarmTypeCommand(), new DeleteFlaggedEventRuleJob(),
                        new FlaggedEventRuleJobScheduler(), new AddOrUpdateFlaggedEventWorkorderFieldMappingCommand(),
                        new AddOrUpdateEmailRuleForFlaggedEventCommand(), new DeleteBureauEvaluationCommand(),new AddOrUpdateBureauConfigCommand(),
                        new ConstructAddCustomActivityCommandV3(), new AddActivitiesCommandV3(AddSubModuleRelations.FLAGGED_EVENT_RULE_ACTIVITY))
                .update()
                .beforeSave(new AddFlaggedEventRuleSiteAndControllerCriteriaCommand())
                .afterSave(new AddFlaggedEventClosureConfigCommand(),new DeleteAndAddRuleAttachmentsCommand(), new DeleteFlaggedEventEmailNotificationRuleCommand(),new FlaggedEventRuleInvalidateCacheCommand(),
                        new AddFlaggedEventRuleAlarmTypeCommand(),new DeleteFlaggedEventRuleJob(),
                        new FlaggedEventRuleJobScheduler(), new AddOrUpdateFlaggedEventWorkorderFieldMappingCommand(),
                        new AddOrUpdateEmailRuleForFlaggedEventCommand(), new DeleteBureauEvaluationCommand(),new AddOrUpdateBureauConfigCommand(),
                        new ConstructUpdateCustomActivityCommandV3(), new AddActivitiesCommandV3(AddSubModuleRelations.FLAGGED_EVENT_RULE_ACTIVITY))
                .list()
                .fetchSupplement(FlaggedEventRuleModule.MODULE_NAME,"client")
                .fetchSupplement(FlaggedEventRuleModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(FlaggedEventRuleModule.MODULE_NAME,"sysModifiedByPeople")
                .summary()
                .fetchSupplement(FlaggedEventRuleModule.MODULE_NAME,"client")
                .fetchSupplement(FlaggedEventRuleModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(FlaggedEventRuleModule.MODULE_NAME,"sysModifiedByPeople")
                .afterFetch(ReadOnlyChainFactoryV3.getFlaggedEventRuleSummaryAfterFetchChain())
                .delete()
                .markAsDeleteByPeople()
                .afterDelete(new DeleteBureauEvaluationCommand(),new DeleteFlaggedEventEmailNotificationRuleCommand(),
                        new FlaggedEventRuleInvalidateCacheCommand(),new AddFlaggedEventRuleAlarmTypeCommand(),
                        new DeleteFlaggedEventRuleJob())
                .build();
    }

    @Module(FlaggedEventModule.MODULE_NAME)
    public static Supplier<V3Config> getFlaggedEventModule(){
        return () -> new V3Config(FlaggedEventContext.class,null)
                .create()
                .afterSave(new FlaggedEventBureauInformationCommand(),new BureauEvaluationOrCreateWorkorder(),new FlaggedEventClosureCommand(), new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.FLAGGED_EVENT_ACTIVITY))
                .preCreate()
                .afterSave(new FlaggedEventClosureCommand(),new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.FLAGGED_EVENT_ACTIVITY))
                .update()
                .afterSave(new FlaggedEventClosureCommand(),new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(AddSubModuleRelations.FLAGGED_EVENT_ACTIVITY))
                .postCreate()
                .afterSave(new FlaggedEventBureauInformationCommand(),
                        new BureauEvaluationOrCreateWorkorder(),
                        new FlaggedEventClosureCommand()
                )
                .afterTransaction(new ConstructAddCustomActivityCommandV3(),new AddActivitiesCommandV3(AddSubModuleRelations.FLAGGED_EVENT_ACTIVITY))
                .list()
                .afterFetch(new ComputeTimeRemainingForFlaggedEventCommand())
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"flaggedEventRule")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"flaggedEvent")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"client")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"controller")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"asset")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"sysModifiedByPeople")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"currentBureauActionDetail")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"team")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"assignedPeople")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"workorder")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"site")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"bureauCloseIssues")
                .summary()
                .afterFetch(new ComputeTimeRemainingForFlaggedEventCommand())
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"flaggedEventRule")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"flaggedEvent")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"client")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"controller")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"asset")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"sysCreatedByPeople")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"sysModifiedByPeople")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"currentBureauActionDetail")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"team")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"assignedPeople")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"workorder")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"site")
                .fetchSupplement(FlaggedEventModule.MODULE_NAME,"bureauCloseIssues")
                .delete()
                .markAsDeleteByPeople()
                .build();
    }

    @Module(FlaggedEventAlarmTypeRelModule.MODULE_NAME)
    public static Supplier<V3Config> getFlaggedEventRuleAlarmTypeRelModule(){
        return () -> new V3Config(FlaggedEventRuleAlarmTypeRel.class,null)
                .create()
                .update()
                .list()
                .fetchSupplement(FlaggedEventAlarmTypeRelModule.MODULE_NAME,"alarmType")
                .summary()
                .fetchSupplement(FlaggedEventAlarmTypeRelModule.MODULE_NAME,"alarmType")
                .delete()
                .markAsDeleteByPeople()
                .build();
    }

    @Module(FacilioConstants.ContextNames.CONTROLLER)
    public static Supplier<V3Config> getControllerModule(){
        return () -> new V3Config(Controller.class,null)
                .create()
                .update()
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.CONTROLLER,"category")
                .fetchSupplement(FacilioConstants.ContextNames.CONTROLLER,"identifiedLocation")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.CONTROLLER,"category")
                .fetchSupplement(FacilioConstants.ContextNames.CONTROLLER,"identifiedLocation")
                .delete()
                .build();
    }
    @Module(ControllerAlarmInfoModule.MODULE_NAME)
    public static Supplier<V3Config> getControllerInfoModule(){
        return () -> new V3Config(ControllerAlarmInfoContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .markAsDeleteByPeople()
                .build();
    }

    @Module(FlaggedEventBureauEvaluationModule.MODULE_NAME)
    public static Supplier<V3Config> getFlaggedEventBureauEvaluationModule(){
        return () -> new V3Config(FlaggedEventRuleBureauEvaluationContext.class,null)
                .create()
                .afterSave(new DeleteBureauInhibitReasonListCommand(), new AddOrUpdateBureauInhibitReasonListCommand())
                .update()
                .afterSave(new DeleteBureauInhibitReasonListCommand(), new AddOrUpdateBureauInhibitReasonListCommand())
                .list()
                .summary()
                .delete()
                .afterDelete(new DeleteBureauInhibitReasonListCommand())
                .build();
    }

    @Module(FlaggedEventBureauActionModule.MODULE_NAME)
    public static Supplier<V3Config> getFlaggedEventBureauActionModule(){
        return () -> new V3Config(FlaggedEventBureauActionsContext.class,null)
                .create()
                .afterSave(new AddOrUpdateBureauActionListCommand())
                .update()
                .afterSave(new AddOrUpdateBureauActionListCommand())
                .list()
                .fetchSupplement(FlaggedEventBureauActionModule.MODULE_NAME,"assignedPeople")
                .fetchSupplement(FlaggedEventBureauActionModule.MODULE_NAME,"team")
                .fetchSupplement(FlaggedEventBureauActionModule.MODULE_NAME,"troubleShootingTips")
                .summary()
                .fetchSupplement(FlaggedEventBureauActionModule.MODULE_NAME,"assignedPeople")
                .fetchSupplement(FlaggedEventBureauActionModule.MODULE_NAME,"team")
                .fetchSupplement(FlaggedEventBureauActionModule.MODULE_NAME,"troubleShootingTips")
                .afterFetch(new ReadOnlyChainFactoryV3().getFlaggedEventActionSummaryAfterFetchChain())
                .delete()
                .build();
    }
    @Module(BureauInhibitReasonListModule.MODULE_NAME)
    public static Supplier<V3Config> getBureauInhibitReasonListModule(){
        return () -> new V3Config(BureauInhibitReasonListContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }

    @Module(BureauCauseListModule.MODULE_NAME)
    public static Supplier<V3Config> getBureauCauseListModule(){
        return () -> new V3Config(BureauCauseListContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }

    @Module(BureauResolutionListModule.MODULE_NAME)
    public static Supplier<V3Config> getBureauResolutionListModule(){
        return () -> new V3Config(BureauResolutionListContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }

    @Module(BureauCloseIssueReasonOptionListModule.MODULE_NAME)
    public static Supplier<V3Config> getBureauCloseIssueReasonOptionListModule(){
        return () -> new V3Config(BureauCloseIssueReasonOptionContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }

    @Module(AddFlaggedEventClosureConfigModule.MODULE_NAME)
    public static Supplier<V3Config> getFlaggedEventClosureConfigModule(){
        return () -> new V3Config(FlaggedEventRuleClosureConfigContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module(AlarmAssetTaggingModule.MODULE_NAME)
    public static Supplier<V3Config> getAlarmAssetTaggingModule(){
        return () -> new V3Config(AlarmAssetTaggingContext.class,null)
                .create()
                .beforeSave(new ValidateAlarmAssetTaggingCommand())
                .afterSave(new InvalidateAlarmAssetTaggingCacheCommand())
                .update()
                .beforeSave(new ValidateAlarmAssetTaggingCommand())
                .afterSave(new InvalidateAlarmAssetTaggingCacheCommand())
                .list()
                .fetchSupplement(AlarmAssetTaggingModule.MODULE_NAME,"asset")
                .fetchSupplement(AlarmAssetTaggingModule.MODULE_NAME,"controller")
                .fetchSupplement(AlarmAssetTaggingModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmAssetTaggingModule.MODULE_NAME,"alarmDefinition")
                .summary()
                .fetchSupplement(AlarmAssetTaggingModule.MODULE_NAME,"asset")
                .fetchSupplement(AlarmAssetTaggingModule.MODULE_NAME,"controller")
                .fetchSupplement(AlarmAssetTaggingModule.MODULE_NAME,"client")
                .fetchSupplement(AlarmAssetTaggingModule.MODULE_NAME,"alarmDefinition")
                .delete()
                .markAsDeleteByPeople()
                .afterDelete(new InvalidateAlarmAssetTaggingCacheCommand())
                .build();
    }
    @Module("workType")
    public static Supplier<V3Config> getWorkType() {
        return () -> new V3Config(WorkTypeContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadWorkTypeLookupsCommandV3())
                .summary()
                .afterFetch(new LoadWorkTypeLineItemsCommandV3())
                .delete()
                .build();
    }
    @Module("serviceSkill")
    public static Supplier<V3Config> getServiceSkill() {
        return () -> new V3Config(ServiceSkillsContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module("serviceTask")
    public static Supplier<V3Config> getServiceTask() {
        return () -> new V3Config(ServiceTaskContext.class, new ModuleCustomFieldCount15())
                .create()
                .beforeSave(new FsmTransactionChainFactoryV3().getTaskBeforeSaveChain())
                .afterSave(new FsmTransactionChainFactoryV3().getTaskAfterSaveChain())
                .update()
                .beforeSave(new FsmTransactionChainFactoryV3().getTaskBeforeUpdateChain())
                .afterSave(new FsmTransactionChainFactoryV3().getTaskAfterUpdateChain())
                .list()
                .beforeFetch(new LoadServiceTaskLookupCommandV3())
                .summary()
                .beforeFetch(new LoadServiceTaskLookupCommandV3())
                .afterFetch(new LoadTaskPlansCommandV3())
                .delete()
                .beforeDelete(new DeleteSATaskRelCommand())
                .afterDelete(FsmTransactionChainFactoryV3.getTaskAfterDeleteChain())
                .build();
    }
    @Module("serviceOrder")
    public static Supplier<V3Config> getServiceOrder() {
        return () -> new V3Config(ServiceOrderContext.class, new ModuleCustomFieldCount15())
                .create()
                .beforeSave(FsmTransactionChainFactoryV3.getSOBeforeSaveCreateChain())
                .afterSave(FsmTransactionChainFactoryV3.afterSOCreateChain())
                .afterTransaction(new AddActivitiesCommandV3(FacilioConstants.ContextNames.SERVICE_ORDER_ACTIVITY))
                .update()
                .beforeSave(FsmTransactionChainFactoryV3.getSOBeforeUpdateChain())
                .afterSave(FsmTransactionChainFactoryV3.afterSOUpdateChain())
                .afterTransaction(new AddActivitiesCommandV3())
                .list()
                .beforeFetch(new LoadSupplementsForSOCommand())
                .summary()
                .beforeFetch(new LoadSupplementsForSOCommand())
                .afterFetch(new SetServiceTaskCommandV3())
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS)
    public static Supplier<V3Config> getServiceOrderPlannedItems() {
        return () -> new V3Config(ServiceOrderPlannedItemsContext.class, null)
                .create()
                .beforeSave(new SetServiceOrderPlannedItemsCommand())
                .afterSave(new AddOrUpdateServiceOrderCostCommand(),new SAEstimatedItemCostAfterCreateCommand())
                .update()
                .beforeSave(FsmTransactionChainFactoryV3.getSoPlannedItemsBeforeUpdateChain())
                .afterSave(FsmTransactionChainFactoryV3.getSoPlannedItemsAfterUpdateChain())
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS, "serviceTask")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS, "serviceTask")
                .delete()
                .beforeDelete(new ValidateSoPlannedItemsBeforeDeleteCommand())
                .afterDelete(new AddOrUpdateServiceOrderCostCommand(),new SAEstimatedItemCostAfterDeleteCommand())
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS)
    public static Supplier<V3Config> getServiceOrderPlannedTools() {
        return () -> new V3Config(ServiceOrderPlannedToolsContext.class, null)
                .create()
                .beforeSave(new SetServiceOrderPlannedToolsCommand())
                .afterSave(new AddOrUpdateServiceOrderCostCommand(),new SAEstimatedToolCostAfterCreateCommand())
                .update()
                .beforeSave(new SetServiceOrderPlannedToolsCommand())
                .afterSave(new AddOrUpdateServiceOrderCostCommand(),new SAEstimatedToolCostAfterUpdateCommand())
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS, "toolType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS, "serviceTask")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS, "toolType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS, "serviceTask")
                .delete()
                .beforeDelete(new ServiceOrderPlannedToolsCostBeforeDelete())
                .afterDelete(new AddOrUpdateServiceOrderCostCommand(), new SAEstimatedToolCostAfterDeleteCommand())
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES)
    public static Supplier<V3Config> getServiceOrderPlannedServices() {
        return () -> new V3Config(ServiceOrderPlannedServicesContext.class, null)
                .create()
                .beforeSave(new SetServiceOrderPlannedServicesCommand())
                .afterSave(new AddOrUpdateServiceOrderCostCommand(), new SAEstimatedServiceCostAfterCreateCommand())
                .update()
                .beforeSave(new SetServiceOrderPlannedServicesCommand())
                .afterSave(new AddOrUpdateServiceOrderCostCommand(), new SAEstimatedServiceCostAfterUpdateCommand())
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES, "service")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES, "serviceTask")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES, "service")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES, "serviceTask")
                .delete()
                .beforeDelete(new ServiceOrderPlannedServiceCostBeforeDelete())
                .afterDelete(new AddOrUpdateServiceOrderCostCommand(), new SAEstimatedServiceCostAfterDeleteCommand())
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION)
    public static Supplier<V3Config> getServiceInventoryReservation() {
        return () -> new V3Config(ServiceInventoryReservationContext.class, null)
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "serviceOrder")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "inventoryRequest")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "requestedBy")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "serviceOrderPlannedItem")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "serviceOrder")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "inventoryRequest")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "requestedBy")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "serviceOrderPlannedItem")
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS)
    public static Supplier<V3Config> getServiceOrderItems() {
        return () -> new V3Config(ServiceOrderItemsContext.class, null)
                .create()
                .beforeSave(new SetServiceOrderItemsCommand())
                .afterSave(FsmTransactionChainFactoryV3.getServiceOrderItemsAfterSaveChain())
                .update()
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, "item")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS,"rotatingAsset")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, "serviceTask")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, "item")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, "itemType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS,"rotatingAsset")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, "serviceTask")
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS)
    public static Supplier<V3Config> getServiceOrderTools() {
        return () -> new V3Config(ServiceOrderToolsContext.class, null)
                .create()
                .beforeSave(new SetServiceOrderToolsCommand())
                .afterSave(FsmTransactionChainFactoryV3.getServiceOrderToolsAfterSaveChain())
                .update()
                .beforeSave(new SetServiceOrderToolsCommand())
                .afterSave(new AddOrUpdateServiceOrderCostCommand(), new SAToolCostAfterUpdateCommand())
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, "tool")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, "toolType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, "serviceTask")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, "tool")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, "storeRoom")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, "toolType")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, "serviceTask")
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES)
    public static Supplier<V3Config> getServiceOrderServices() {
        return () -> new V3Config(ServiceOrderServiceContext.class, null)
                .create()
                .beforeSave(new SetServiceOrderServiceCommand())
                .afterSave(new AddOrUpdateServiceOrderCostCommand(),new SAServiceCostAfterCreateCommand())
                .update()
                .beforeSave(new SetServiceOrderServiceCommand())
                .afterSave(new AddOrUpdateServiceOrderCostCommand(),new SAServiceCostAfterUpdateCommand())
                .list()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES, "service")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES, "serviceTask")
                .summary()
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES, "service")
                .fetchSupplement(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES, "serviceTask")
                .delete()
                .build();
    }
    @Module(FacilioConstants.TimeOff.TIME_OFF)
    public static Supplier<V3Config> getTimeOff(){
        return () -> new V3Config(TimeOffContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .beforeSave(FsmTransactionChainFactoryV3.getTimeOffBeforeCreateChain())
                .afterSave(new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.TimeOff.TIME_OFF_ACTIVITY))
                .update()
                .afterSave(new ConstructUpdateCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.TimeOff.TIME_OFF_ACTIVITY))
                .list()
                .beforeFetch(new FetchTimeOffSupplementsCommand())
                .summary()
                .beforeFetch(new FetchTimeOffSupplementsCommand())
                .pickList()
                .delete()
                .build();
    }
    @Module(FacilioConstants.LocationHistory.LOCATION_HISTORY)
    public static Supplier<V3Config> getLocationHistory(){
        return () -> new V3Config(V3LocationHistoryContext.class,null)
                .create()
                .afterSave(new UpdatePeopleLocationHistoryCommand())
                .list()
                .beforeFetch(new FetchLocationHistorySupplements())
                .build();
    }
    @Module(FacilioConstants.Territory.TERRITORY)
    public static Supplier<V3Config> getTerritory(){
        return () -> new V3Config(TerritoryContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .beforeSave(new SetLocalIdCommandV3())
                .afterSave(new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.Territory.TERRITORY_ACTIVITY))
                .update()
                .afterSave(new ConstructUpdateCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.Territory.TERRITORY_ACTIVITY))
                .list()
                .summary()
                .pickList()
                .delete()
                .build();
    }
    @Module(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)
    public static Supplier<V3Config> getServiceAppointment(){
        return () -> new V3Config(ServiceAppointmentContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .beforeSave(FsmTransactionChainFactoryV3.getServiceAppointmentBeforeCreateChain())
                .afterSave(FsmTransactionChainFactoryV3.getServiceAppointmentAfterCreateChain())
                .afterTransaction(new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY))
                .update()
                .beforeSave(FsmTransactionChainFactoryV3.getServiceAppointmentBeforeUpdateChain())
                .afterSave(FsmTransactionChainFactoryV3.getServiceAppointmentAfterUpdateChain())
                .list()
                .beforeFetch(new FetchServiceAppointmentSupplementsCommand())
                .summary()
                .beforeFetch(new FetchServiceAppointmentSupplementsCommand())
                .pickList()
                .delete()
                .beforeDelete(FsmTransactionChainFactoryV3.getServiceAppointmentBeforeDeleteChain())
                .afterDelete(FsmTransactionChainFactoryV3.getServiceAppointmentAfterDeleteChain())
                .build();
    }
    @Module(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL)
    public static Supplier<V3Config> getPeopleSkillLevel() {
        return () -> new V3Config(PeopleSkillLevelContext.class, null)
                .create()
                .beforeSave(FsmTransactionChainFactoryV3.getPeopleSkillLevelBeforeSaveCommand())
                .list()
                .beforeFetch(new FetchPeopleSkillLevelSupplementsCommand())
                .summary()
                .beforeFetch(new FetchPeopleSkillLevelSupplementsCommand())
                .delete()
                .build();
    }
    @Module(FacilioConstants.TimeSheet.TIME_SHEET)
    public static Supplier<V3Config> getTimeSheet(){
        return () -> new V3Config(TimeSheetContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .beforeSave(FsmTransactionChainFactoryV3.getTimeSheetBeforeCreateChain())
                .afterSave(FsmTransactionChainFactoryV3.getTimeSheetAfterCreateChain())
                .afterTransaction(FsmTransactionChainFactoryV3.getTimeSheetAfterTransactionChain())
                .update()
                .beforeSave(FsmTransactionChainFactoryV3.getTimeSheetBeforeUpdateChain())
                .afterSave(FsmTransactionChainFactoryV3.getTimeSheetAfterUpdateChain())
                .afterTransaction(FsmTransactionChainFactoryV3.getTimeSheetAfterTransactionChain())
                .list()
                .beforeFetch(new FetchTimeSheetSupplementsCommand())
                .summary()
                .beforeFetch(new FetchTimeSheetSupplementsCommand())
                .pickList()
                .delete()
                .beforeDelete(new CheckRecordLockForTimeSheetCommand())
                .afterDelete(new AddTimeSheetServiceAppointmentActivity())
                .build();
    }
    @Module(FacilioConstants.Trip.TRIP)
    public static Supplier<V3Config> getTrip(){
        return () -> new V3Config(TripContext.class,null)
                .create()
                .beforeSave(FsmTransactionChainFactoryV3.getTripBeforeCreateChain())
                .afterSave(new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.Trip.TRIP_ACTIVITY))
                .update()
                .beforeSave(FsmTransactionChainFactoryV3.getTripBeforeUpdateChain())
                .afterSave(new ConstructUpdateCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.Trip.TRIP_ACTIVITY))
                .list()
                .beforeFetch(new FetchTripSupplementsCommand())
                .summary()
                .beforeFetch(new FetchTripSupplementsCommand())
                .delete()
                .beforeDelete(FsmTransactionChainFactoryV3.getTripBeforeDeleteChain())
                .afterDelete(new AddTripServiceAppointmentActivity())
                .build();
    }

    @Module(FacilioConstants.Trip.TRIP_LOCATION_HISTORY)
    public static Supplier<V3Config> getTripLocationHistory(){
        return () -> new V3Config(TripContext.class,null)
                .create()
                .afterSave(new UpdatePeopleLocationFromTripCommand())
                .update()
                .list()
                .beforeFetch(new FetchLocationHistorySupplements())
                .summary()
                .delete()
                .build();
    }

    @Module(FacilioConstants.Priority.PRIORITY)
    public static Supplier<V3Config> getPriority(){
        return () -> new V3Config(PriorityContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }

    @Module(FacilioConstants.Trip.TRIP_STATUS)
    public static Supplier<V3Config> getTripStatus(){
        return () -> new V3Config(TripStatusContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module(FacilioConstants.TimeSheet.TIME_SHEET_STATUS)
    public static Supplier<V3Config> getTimeSheetStatus(){
        return () -> new V3Config(TimeSheetStatusContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }

}