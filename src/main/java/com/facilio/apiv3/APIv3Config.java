package com.facilio.apiv3;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.LookUpPrimaryFieldHandlingCommandV3;
import com.facilio.bmsconsoleV3.commands.*;
import com.facilio.bmsconsoleV3.commands.Audience.ValidateAudienceSharingCommandV3;
import com.facilio.bmsconsoleV3.commands.asset.*;
import com.facilio.bmsconsoleV3.commands.basespace.DeleteBasespaceChildrenCommandV3;
import com.facilio.bmsconsoleV3.commands.basespace.FetchBasespaceChildrenCountCommandV3;
import com.facilio.bmsconsoleV3.commands.budget.*;
import com.facilio.bmsconsoleV3.commands.building.AddOrUpdateBuildingLocation;
import com.facilio.bmsconsoleV3.commands.building.BuildingFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.building.CreateBuildingAfterSave;
import com.facilio.bmsconsoleV3.commands.building.SetBuildingRelatedContextCommand;
import com.facilio.bmsconsoleV3.commands.client.*;
import com.facilio.bmsconsoleV3.commands.client.DisassociateClientFromSiteCommand;
import com.facilio.bmsconsoleV3.commands.clientcontact.DeleteClientContactPeopleUsersCommandV3;
import com.facilio.bmsconsoleV3.commands.clientcontact.LoadClientContactLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.FillAudienceSharingInfoCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.LoadAudienceLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments.FillAdminDocumentsSharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments.LoadAdminDocumentsLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.AnnouncementFillDetailsCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.DeleteChildAnnouncementsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.LoadAnnouncementLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.LoadPeopleAnnouncementLookupCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory.ContactDirectoryFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory.FillContactDirectorySharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.DealsAndOffersFillLookupFields;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.FillDealsAndOffersSharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.FillNeighbourhoodSharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.NeighbourhoodFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation.FillNewsAndInformationDetailsCommandV3;

import com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation.FillNewsRelatedModuleDataInListCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation.LoadNewsAndInformationLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.employee.LoadEmployeeLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.employee.UpdateEmployeePeopleAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.facility.*;
import com.facilio.bmsconsoleV3.commands.floor.CreateFloorAfterSave;
import com.facilio.bmsconsoleV3.commands.floor.FloorFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.floor.SetFloorRelatedContextCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.*;
import com.facilio.bmsconsoleV3.commands.imap.UpdateLatestMessageUIDCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.LoadInsuranceLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.ValidateDateCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.*;
import com.facilio.bmsconsoleV3.commands.item.LoadItemLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.itemtypes.LoadItemTypesLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.jobplan.AddJobPlanTasksCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.FetchJobPlanLookupCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.FillJobPlanDetailsCommand;
import com.facilio.bmsconsoleV3.commands.labour.GetLabourListCommandV3;
import com.facilio.bmsconsoleV3.commands.labour.SetLocationCommandV3;
import com.facilio.bmsconsoleV3.commands.moves.UpdateEmployeeInDesksCommandV3;
import com.facilio.bmsconsoleV3.commands.moves.ValidateMovesCommand;
import com.facilio.bmsconsoleV3.commands.people.CheckforPeopleDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.people.MarkRandomContactAsPrimaryCommandV3;
import com.facilio.bmsconsoleV3.commands.people.ValidateContactsBeforeDeleteCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.*;
import com.facilio.bmsconsoleV3.commands.purchaserequest.FetchPurchaseRequestDetailsCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaserequest.LoadPoPrListLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaserequest.LoadPurchaseRequestSummaryLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.*;
import com.facilio.bmsconsoleV3.commands.receipts.SetReceiptTimeAndLocalIdCommand;
import com.facilio.bmsconsoleV3.commands.receivable.LoadReceivableLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.receivable.SetPOLineItemCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.*;
import com.facilio.bmsconsoleV3.commands.service.GetServiceVendorListCommandV3;
import com.facilio.bmsconsoleV3.commands.service.UpdateStatusCommandV3;
import com.facilio.bmsconsoleV3.commands.service.UpdateVendorV3;
import com.facilio.bmsconsoleV3.commands.servicerequest.AddRequesterForServiceRequestCommandV3;
import com.facilio.bmsconsoleV3.commands.servicerequest.LoadServiceRequestLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.site.AddOrUpdateSiteLocationCommand;
import com.facilio.bmsconsoleV3.commands.site.CreateSiteAfterSave;
import com.facilio.bmsconsoleV3.commands.site.SetSiteRelatedContextCommand;
import com.facilio.bmsconsoleV3.commands.site.SiteFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.space.*;
import com.facilio.bmsconsoleV3.commands.storeroom.LoadStoreRoomLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.storeroom.SetLocationObjectFromSiteV3;
import com.facilio.bmsconsoleV3.commands.storeroom.UpdateServingSitesinStoreRoomCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.FillTenantsLookupCommand;
import com.facilio.bmsconsoleV3.commands.tenant.ValidateTenantSpaceCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.LoadTenantcontactLookupsCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantspaces.LoadTenantSpacesLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantunit.AddSpaceCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantunit.CheckForTenantBeforeDeletionCommand;
import com.facilio.bmsconsoleV3.commands.tenantunit.LoadTenantUnitLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.termsandconditions.CheckForPublishedCommand;
import com.facilio.bmsconsoleV3.commands.termsandconditions.LoadTermsLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.tool.LoadToolLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.tool.StockOrUpdateToolsCommandV3;
import com.facilio.bmsconsoleV3.commands.tooltypes.LoadToolTypesLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.*;
import com.facilio.bmsconsoleV3.commands.vendor.AddOrUpdateLocationForVendorCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.LoadVendorLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorQuotes.LoadVendorQuoteLineItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorQuotes.LoadVendorQuotesLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorQuotes.SetVendorQuotesLineItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorcontact.LoadVendorContactLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.LoadVisitorLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.GetChildInvitesForGroupInviteCommand;
import com.facilio.bmsconsoleV3.commands.visitorlog.GetScheduleTriggerForRecurringInviteCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.UpdateChildInvitesAfterSaveCommand;
import com.facilio.bmsconsoleV3.commands.visitorlog.ValidateBaseVisitDetailAndLogCommand;
import com.facilio.bmsconsoleV3.commands.visitorlogging.GetTriggerForRecurringLogCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.LoadVisitorLoggingLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.watchlist.CheckForExisitingWatchlistRecordsCommandV3;
import com.facilio.bmsconsoleV3.commands.watchlist.GetLogsForWatchListCommandV3;
import com.facilio.bmsconsoleV3.commands.workorder.LoadWorkorderLookupsAfterFetchcommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.*;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetDepartmentContext;
import com.facilio.bmsconsoleV3.context.budget.AccountTypeContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.bmsconsoleV3.context.budget.ChartOfAccountContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.*;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.bmsconsoleV3.context.floorplan.*;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
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
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistCategoryContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistContext;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount10;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount30;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount30_BS2;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount50;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.*;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.elasticsearch.command.PushDataToESCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.faultimpact.FaultImpactNameSpaceFieldContext;
import com.facilio.relation.context.RelationDataContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.annotation.ModuleType;
import com.facilio.v3.commands.ConstructAddCustomActivityCommandV3;
import com.facilio.v3.commands.ConstructUpdateCustomActivityCommandV3;
import com.facilio.v3.commands.FetchChangeSetForCustomActivityCommand;
import com.facilio.v3.context.Constants;
import com.facilio.workflowlog.context.WorkflowLogContext;

import org.apache.commons.chain.Context;

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
                .beforeSave(new QuotationValidationAndCostCalculationCommand())
                .afterSave(TransactionChainFactoryV3.getQuotationAfterUpdateChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTE_ACTIVITY))

                .summary()
                .beforeFetch(new QuotationFillLookupFields())
                .afterFetch(new QuotationFillDetailsCommand())

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
                .beforeFetch(new LoadTermsLookupCommandV3())
                .summary()
                .delete()
                .build();
    }

    @Module("storeRoom")
    public static Supplier<V3Config> getStoreRoom() {
        return () -> new V3Config(V3StoreRoomContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetLocationObjectFromSiteV3())
                .afterSave(new UpdateServingSitesinStoreRoomCommandV3())
                .update()
                .afterSave(new UpdateServingSitesinStoreRoomCommandV3())
                .list()
                .beforeFetch(new LoadStoreRoomLookUpCommandV3())
                .summary()
                .beforeFetch(new LoadStoreRoomLookUpCommandV3())
                .afterFetch(new AddStoreRoomDetailsCommandV3())
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
                .beforeSave(new AssociateClientFromSite(),new DisassociateClientFromSiteCommand(), new UpdateAddressForClientLocationCommandV3())
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
                .beforeSave(new SetLocationCommandV3(),new SetLocalIdCommandV3())
                .list()
                .beforeFetch(new GetLabourListCommandV3())
                .update()
                .beforeSave(new SetLocationCommandV3())
                .delete()
                .build();
    }

    @Module("purchaserequest")
    public static Supplier<V3Config> getPurchaseRequest() {
        return () -> new V3Config(V3PurchaseRequestContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getAddPurchaseRequestBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getAddPurchaseRequestAfterSaveChain(), new ConstructAddCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommandV3(FacilioConstants.ContextNames.PURCHASE_REQUEST_ACTIVITY))
                .update()
                .beforeSave(TransactionChainFactoryV3.getAddPurchaseRequestBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getAddPurchaseRequestAfterSaveChain(), new ConstructUpdateCustomActivityCommandV3())
                .afterTransaction(new AddActivitiesCommandV3(FacilioConstants.ContextNames.PURCHASE_REQUEST_ACTIVITY))
                .list()
                .beforeFetch(new LoadPoPrListLookupCommandV3())
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
                .beforeFetch(new LoadRequestForQuotationLookupCommandV3())
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
                .update()
                .list()
                .beforeFetch(new LoadVendorQuotesLookupCommandV3())
                .summary()
                .beforeFetch(new LoadVendorQuotesLookupCommandV3())
                .afterFetch(new SetVendorQuotesLineItemsCommandV3())
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
                .list()
                .beforeFetch(new LoadItemTypesLookUpCommandV3())
                .summary()
                .beforeFetch(new LoadItemTypesLookUpCommandV3())
                .build();
    }

    @Module("item")
    public static Supplier<V3Config> getItem() {
        return () -> new V3Config(V3ItemContext.class, new ModuleCustomFieldCount30())
                .create()
                .afterSave(TransactionChainFactoryV3.getAddItemChain())
                .update()
                .afterSave(TransactionChainFactoryV3.getAddItemChain())
                .list()
                .beforeFetch(new LoadItemLookUpCommandV3())
                .summary()
                .beforeFetch(new LoadItemLookUpCommandV3())
                .build();
    }

    @Module("toolTypes")
    public static Supplier<V3Config> getToolTypes() {
        return () -> new V3Config(V3ToolTypesContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadToolTypesLookUpCommandV3())
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
                .beforeFetch(new LoadToolLookupCommandV3())
                .summary()
                .beforeFetch(new LoadToolLookupCommandV3())
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
                .showStateFlowList()
                .summary()
                .afterFetch(new GetChildInvitesForGroupInviteCommand())
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
        return () -> new V3Config(V3VisitorContext.class, null)
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
                .beforeFetch(new LoadVendorLookupCommandV3())
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
        return () -> new V3Config(V3WorkOrderContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getWorkorderBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getWorkorderAfterSaveChain())
                .update()
                .beforeSave(TransactionChainFactoryV3.getWorkorderBeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.getWorkorderAfterUpdateChain(true))
                .list()
                .beforeFetch(TransactionChainFactoryV3.getTicketBeforeFetchChain())
                .afterFetch(new LoadWorkorderLookupsAfterFetchcommandV3())
                .summary()
                .beforeFetch(TransactionChainFactoryV3.getTicketBeforeFetchForSummaryChain())
                .afterFetch(ReadOnlyChainFactoryV3.getWorkorderAfterFetchOnSummaryChain())
                .build();
    }

    @Module("tenantcontact")
    public static Supplier<V3Config> getTenantContact() {
        return () -> new V3Config(V3TenantContactContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getTenantContactBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getTenantContactAfterSaveChain())
                .update()
                .beforeSave(new CheckforPeopleDuplicationCommandV3())
                .afterSave(TransactionChainFactoryV3.getTenantContactAfterSaveChain())
                .list()
                .beforeFetch(new LoadTenantcontactLookupsCommandV3())
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
                .update()
                .beforeSave(new CheckforPeopleDuplicationCommandV3())
                .afterSave(TransactionChainFactoryV3.getVendorContactAfterSaveChain())
                .list()
                .beforeFetch(new LoadVendorContactLookupCommandV3())
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
                .afterSave(new UpdateEmployeePeopleAppPortalAccessCommandV3())
                .update()
                .beforeSave(new CheckforPeopleDuplicationCommandV3())
                .afterSave(TransactionChainFactoryV3.getUpdateEmployeeAfterUpdateChain())
                .list()
                .beforeFetch(new LoadEmployeeLookupCommandV3())
                .afterFetch(new FetchRolesForPeopleCommandV3())
                .summary()
                .beforeFetch(new LoadEmployeeLookupCommandV3())
                .afterFetch(new FetchRolesForPeopleCommandV3())
                .delete()
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
                .beforeSave(new CheckforPeopleDuplicationCommandV3())
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
                .afterSave(TransactionChainFactoryV3.getWorkPermitAfterSaveOnUpdateChain())
                .afterTransaction(
                        new AddActivitiesCommand(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ACTIVITY))

                .list()
                .beforeFetch(new LoadWorkPermitLookUpsCommandV3())

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
                .beforeFetch(new LoadPeopleAnnouncementLookupCommand())
                .summary()
                .beforeFetch(new LoadPeopleAnnouncementLookupCommand())
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
                .beforeFetch(new NeighbourhoodFillLookupFieldsCommand())
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
                .beforeFetch(new DealsAndOffersFillLookupFields())
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
                .build();
    }

    @Module("budget")
    public static Supplier<V3Config> getBudget() {
        return () -> new V3Config(BudgetContext.class, new ModuleCustomFieldCount30())
                .create().beforeSave(TransactionChainFactoryV3.getCreateBudgetBeforeSaveChain())
                .afterSave(new AddOrUpdateMonthlyBudgetAmountCommandV3())
                .update()
                .beforeSave(new ValidateBudgetAmountCommandV3())
                .afterSave(new AddOrUpdateMonthlyBudgetAmountCommandV3())
                .list()
                .beforeFetch(new LoadBudgetLookupCommandV3())
                .summary()
                .beforeFetch(new LoadBudgetLookupCommandV3())
                .afterFetch(new FillBudgetDetailsCommandV3())
                .build();
    }

    @Module("transaction")
    public static Supplier<V3Config> getTransaction() {
        return () -> new V3Config(V3TransactionContext.class, null)
                .create().afterSave(new RollUpTransactionAmountCommand())
                .update()
                .afterSave(new RollUpTransactionAmountCommand())
                .delete()
                .afterDelete(new RollUpTransactionAmountCommand())
                .build();
    }

    @Module("facility")
    public static Supplier<V3Config> getFacility() {
        return () -> new V3Config(FacilityContext.class, new ModuleCustomFieldCount30_BS2())
                .create().beforeSave(TransactionChainFactoryV3.getFacilityBeforeSaveChain())
                .afterSave(new ScheduleSlotCreationCommand())
                .update()
                .beforeSave(new ValidateFacilityCommand())
                .delete()
                .list()
                .beforeFetch(new LoadFacilityLookupCommandV3())
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
                .beforeFetch(new LoadFacilityBookingLookupCommand())
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
                .afterSave(TransactionChainFactoryV3.getCreateJobPlanChain())
                .update()
                .afterSave(TransactionChainFactoryV3.getUpdateJobPlanChain())
                .delete()
                .list()
                .beforeFetch(new FetchJobPlanLookupCommand())
                .summary()
                .beforeFetch(new FetchJobPlanLookupCommand())
                .afterFetch(new FillJobPlanDetailsCommand())
                .delete()

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

    @Module("site")
    public static Supplier<V3Config> getSite() {
        return () -> new V3Config(V3SiteContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddOrUpdateSiteLocationCommand(), new SetSiteRelatedContextCommand())
                .afterSave(new CreateSiteAfterSave(), getSpaceReadingsChain(),
                        new InsertReadingDataMetaForNewResourceCommand(), new ConstructAddCustomActivityCommandV3(),
                        new AddActivitiesCommand(FacilioConstants.ContextNames.SITE_ACTIVITY),
                        new AddWeatherStationCommand())
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
                .build();
    }

    @Module("building")
    public static Supplier<V3Config> getBuilding() {
        return () -> new V3Config(V3BuildingContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddOrUpdateBuildingLocation(), new SetBuildingRelatedContextCommand())
                .afterSave(new CreateBuildingAfterSave(), getSpaceReadingsChain(),
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
                .build();
    }

    @Module("floor")
    public static Supplier<V3Config> getFloor() {
        return () -> new V3Config(V3FloorContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new SetFloorRelatedContextCommand())
                .afterSave(new CreateFloorAfterSave(), getSpaceReadingsChain(),
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
                .list().beforeFetch(new FloorFillLookupFieldsCommand())
                .build();
    }

    @Module("space")
    public static Supplier<V3Config> getSpace() {
        return () -> new V3Config(V3SpaceContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AddOrUpdateSpaceLocation(), new SetSpaceRelatedContextCommand(),
                        new AddSpaceCategoryExtendedModuleCommandV3())
                .afterSave(new CreateSpaceAfterSave(), getSpaceReadingsChain(),
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
                .list().beforeFetch(new SpaceFillLookupFieldsCommand())
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

    @Module("workorderTimeLog")
    public static Supplier<V3Config> getWorkOrderTimeLog() {
        return () -> new V3Config(TimelogContext.class, null)
                .build();
    }

    @Module(FacilioConstants.Workflow.WORKFLOW_LOG)
    public static Supplier<V3Config> getWorkflowLog() {
        return () -> new V3Config(WorkflowLogContext.class, null)
                .build();
    }

    @Module("asset")
    public static Supplier<V3Config> getAsset() {
        return () -> new V3Config(V3AssetContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(new AssetCategoryAdditionInExtendModuleCommand(),
                        new AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMFV3(),
                        new ValidateQrValueCommandV3())
                .afterSave(new ConstructAddAssetActivityCommandV3(), new AddRotatingItemToolCommandV3(),
                        new AssetAfterSaveCommandV3(), FacilioChainFactory.getCategoryReadingsChain(),
                        new InsertReadingDataMetaForNewResourceCommand(), new PushDataToESCommand())
                .update()
                .beforeSave(new CheckPMForAssetsCommandV3(), new AssetCategoryAdditionInExtendModuleCommand(),
                        new AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMFV3(),
                        new ValidateQrValueCommandV3())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(), new AddActivitiesCommandV3())
                .delete()
                .summary()
                .beforeFetch(new AssetSupplementsSupplyCommand())
                .afterFetch(new LoadAssetSummaryCommandV3())
                .list()
                .beforeFetch(new AssetSupplementsSupplyCommand())
                .afterFetch(new AssetListFilterByReadingsCommand())
                .build();
    }

    @Module("inventoryrequest")
    public static Supplier<V3Config> getInventoryRequest() {
        return () -> new V3Config(V3InventoryRequestContext.class, new ModuleCustomFieldCount30())
                .create()
                .beforeSave(TransactionChainFactoryV3.getIRBeforeSaveChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.INVENTORY_REQUEST_ACTIVITY))
                .update()
                .beforeSave(TransactionChainFactoryV3.getIRBeforeSaveChain())
                .afterSave(new IssueInvRequestCommandV3())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.INVENTORY_REQUEST_ACTIVITY))
                .list()
                .beforeFetch(new LoadIRLookupCommandV3())
                .summary()
                .beforeFetch(new LoadIRLookupCommandV3())
                .afterFetch(new FetchInventoryRequestDetailsCommandV3())
                .delete()
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
                .list()
                .update()
                .delete()
                .beforeDelete(TransactionChainFactoryV3.getDeleteAssetCategoryChain())
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
    public static Supplier<V3Config> getAssetDepartment(){
        return () -> new V3Config(V3AssetDepartmentContext.class,null)
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
                .update()
                .list()
                .beforeFetch(new LoadReceivableLookupCommandV3())
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


    @Module("newreadingalarm")
    public static Supplier<V3Config> getNewReadingAlarm() {
        return () -> new V3Config(ReadingAlarm.class, null)
                .update()
                .afterSave(new UpdateOccurrenceCommand())
                .list()
                .beforeFetch(new LoadResourceLookUpCommand())
                .afterFetch(new HandleV3AlarmListLookupCommand())
                .summary()
                .beforeFetch(new LoadResourceLookUpCommand())
                .delete()
                .build();
    }

    @Module(FacilioConstants.ModuleNames.WEATHER_SERVICE)
    public static Supplier<V3Config> getWeatherService() {
        return () -> new V3Config(V3WeatherServiceContext.class, null)
                .create()
                .beforeSave(new ValidateWeatherServiceCommand())
                .afterSave(new AddWeatherServiceJobCommand())
                .update()
                .beforeSave(new ValidateWeatherServiceCommand())
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
                .beforeSave(new ValidateWeatherStationCommand())
                .afterSave(TransactionChainFactoryV3.addReadingDataMetaForReadingModule())
                .update()
                .beforeSave(new ValidateWeatherStationCommand())
                .list()
                .summary()
                .delete()
                .build();
    }
}
