package com.facilio.apiv3;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.apiv3.sample.*;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsoleV3.commands.*;
import com.facilio.bmsconsoleV3.commands.announcement.AnnouncementFillDetailsCommandV3;
import com.facilio.bmsconsoleV3.commands.announcement.DeleteChildAnnouncementsCommand;
import com.facilio.bmsconsoleV3.commands.announcement.LoadAnnouncementLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.announcement.LoadPeopleAnnouncementLookupCommand;
import com.facilio.bmsconsoleV3.commands.budget.*;
import com.facilio.bmsconsoleV3.commands.client.AddAddressForClientLocationCommandV3;
import com.facilio.bmsconsoleV3.commands.client.LoadClientLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.client.UpdateAddressForClientLocationCommandV3;
import com.facilio.bmsconsoleV3.commands.client.UpdateClientIdInSiteCommandV3;
import com.facilio.bmsconsoleV3.commands.clientcontact.LoadClientContactLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments.FillAdminDocumentsSharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments.LoadAdminDocumentsLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory.ContactDirectoryFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory.FillContactDirectorySharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.DealsAndOffersFillLookupFields;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.FillDealsAndOffersSharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.FillNeighbourhoodSharingInfoCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.NeighbourhoodAddLocationCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.NeighbourhoodFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation.LoadNewsAndInformationLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.employee.UpdateEmployeePeopleAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.imap.UpdateLatestMessageUIDCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.AssociateVendorToInsuranceCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.LoadInsuranceLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.itemtypes.LoadItemTypesLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.itemtypes.SetItemTypesUnitCommandV3;
import com.facilio.bmsconsoleV3.commands.people.CheckforPeopleDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaserequest.*;
import com.facilio.bmsconsoleV3.commands.quotation.*;
import com.facilio.bmsconsoleV3.commands.storeroom.LoadStoreRoomLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.storeroom.UpdateServingSitesinStoreRoomCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.FillTenantsLookupCommand;
import com.facilio.bmsconsoleV3.commands.tenant.ValidateTenantSpaceCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.LoadTenantcontactLookupsCommandV3;
import com.facilio.bmsconsoleV3.commands.tooltypes.LoadToolTypesLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.tooltypes.SetToolTypesUnitCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddOrUpdateLocationForVendorCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.LoadVendorLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorcontact.LoadVendorContactLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.LoadVisitorLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.GetTriggerForRecurringLogCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.LoadVisitorLoggingLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.watchlist.CheckForExisitingWatchlistRecordsCommandV3;
import com.facilio.bmsconsoleV3.commands.watchlist.GetLogsForWatchListCommandV3;
import com.facilio.bmsconsoleV3.commands.workorder.LoadWorkorderLookupsAfterFetchcommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.*;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.context.budget.AccountTypeContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.bmsconsoleV3.context.budget.ChartOfAccountContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.bmsconsoleV3.context.tenantEngagement.*;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistCategoryContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.DefaultInit;

import java.util.function.Supplier;

@Config
public class APIv3Config {
//
//    @Module("_custom_test")
//    // we are returning a lambda so as to generate new V3Config object each
//    // and every time a request happens
//    public static Supplier<V3Config> customTest() {
//        return () -> new V3Config(CustomModuleData.class)
//                .create()
//                    .init(new DefaultInit())
//                    .beforeSave(new SampleBeforeSaveCommand())
//                    .afterSave(new SampleAfterSaveCommand())
//                    .afterTransaction(new SampleAfterTransactionCommand())
//                .summary()
//                    .afterFetch(new SampleAfterFetchCommand())
//                .list()
//                    .beforeFetch(new SampleBeforeFetchCommand())
//                    .afterFetch(new SampleAfterFetchCommand())
//                    .showStateFlowList()
//                .update()
//                    .init(new DefaultInit())
//                    .beforeSave(new SampleBeforeSaveCommand())
//                    .afterSave(new SampleAfterSaveCommand())
//                    .afterTransaction(new SampleAfterTransactionCommand())
//                .delete()
//                    .beforeDelete(new SampleBeforeDeleteCommand())
//                    .afterDelete(new SampleAfterDeleteCommand())
//                    .afterTransaction(new SampleAfterTransactionCommand())
//                .build();
//    }

    @Module("assetdepreciation")
    public static Supplier<V3Config> getAssetDepreciation() {
        return () -> new V3Config(AssetDepreciationContext.class)
                .create()
                    .beforeSave(new ValidateAssetDepreciationCommand())
                .summary()
                    .afterFetch(new AssetDepreciationFetchAssetDetailsCommand())
                .build();
    }

    @Module("quote")
    public static Supplier<V3Config> getQuotation() {
        return () -> new V3Config(QuotationContext.class)

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

    @Module("tax")
    public static Supplier<V3Config> getTax() {
        return () -> new V3Config(TaxContext.class)

                .create()
                .beforeSave(new TaxValidationCommand())
                .afterSave(new InsertTaxGroupsCommand())

                .summary()
                .afterFetch(new TaxFillDetailsCommand())

                .list()
                .afterFetch(new TaxFillDetailsCommand())

                .build();
        /*
            Using add handler for update too send with id for Updating
            Bulk Add is Supported
            Only single update is Supported (For Bulk update maintain old record id vs new record id map)

            Tax Group Update => Sets current group as inactive and creates a new group

            Tax Individual Update => 1. Current tax is set as Inactive,
                                     2. List of All Groups with the updating tax as child will be set as inactive and new Tax
                                        groups will be added with new tax id
         */
    }

    @Module("insurance")
    public static Supplier<V3Config> getInsurance() {
        return () -> new V3Config(V3InsuranceContext.class)
                .create()
                    .beforeSave(new AssociateVendorToInsuranceCommandV3())
                    .afterSave(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand())
                .update()
                .list()
                    .beforeFetch(new LoadInsuranceLookUpCommandV3())
                .summary()
                    .beforeFetch(new LoadInsuranceLookUpCommandV3())
                .build();
    }
    
    @Module("storeRoom")
    public static Supplier<V3Config> getStoreRoom() {
        return () -> new V3Config(V3StoreRoomContext.class)
                .create()
                    .afterSave(new UpdateServingSitesinStoreRoomCommandV3())
                .update()
                	.afterSave(new UpdateServingSitesinStoreRoomCommandV3())
                .list()
                    .beforeFetch(new LoadStoreRoomLookUpCommandV3())
                .summary()
                    .beforeFetch(new LoadStoreRoomLookUpCommandV3())
                .build();
    }
    
    @Module("watchlist")
    public static Supplier<V3Config> getWatchList() {
        return () -> new V3Config(V3WatchListContext.class)
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
        return () -> new V3Config(V3ClientContext.class)
                .create()
            		.beforeSave(new AddAddressForClientLocationCommandV3())
                    .afterSave(TransactionChainFactoryV3.getAddClientsAfterSaveChain())
                .update()
                	.beforeSave(new UpdateAddressForClientLocationCommandV3())
                	.afterSave(new UpdateClientIdInSiteCommandV3())
                .list()
                    .beforeFetch(new LoadClientLookupCommandV3())
                .summary()
                    .beforeFetch(new LoadClientLookupCommandV3())
                .build();
    }
    
    @Module("purchaserequest")
    public static Supplier<V3Config> getPurchaseRequest() {
        return () -> new V3Config(V3PurchaseRequestContext.class)
                .create()
            		.beforeSave(new PreFillAddPurchaseRequestCommand())
                    .afterSave(TransactionChainFactoryV3.getAddPurchaseRequestAfterSaveChain())
                .update()
                	.beforeSave(new PreFillUpdatePurchaseRequestCommand())
                	.afterSave(new ForkChainToInstantJobCommand()
        					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)))
                .list()
                    .beforeFetch(new LoadPurchaseRequestListLookupCommandV3())
                .summary()
                    .beforeFetch(new LoadPurchaseRequestSummaryLookupCommandV3())
                    .afterFetch(new FetchPurchaseRequestDetailsCommandV3())
                .build();
    }
    
    @Module("itemTypes")
    public static Supplier<V3Config> getItemTypes() {
        return () -> new V3Config(V3ItemTypesContext.class)
                .create()
                	.afterSave(TransactionChainFactoryV3.getItemOrToolTypesAfterSaveChain())
                .update()
            		.afterSave(TransactionChainFactoryV3.getUpdateItemTypesAfterSaveChain())
                .list()
                	.beforeFetch(new LoadItemTypesLookUpCommandV3())
                    .afterFetch(new SetItemTypesUnitCommandV3())
                .summary()
            		.beforeFetch(new LoadItemTypesLookUpCommandV3())
                .build();
    }
    
    @Module("itemTypesStatus")
    public static Supplier<V3Config> getItemTypesStatus() {
        return () -> new V3Config(V3ItemTypesStatusContext.class)
                .create()
        			.afterSave(new ExecuteAllWorkflowsCommand())
                .update()
                .list()
                .summary()
                .build();
    }
    
    @Module("itemTypesCategory")
    public static Supplier<V3Config> getItemTypesCategory() {
        return () -> new V3Config(V3ItemTypesCategoryContext.class)
                .create()
        			.afterSave(new ExecuteAllWorkflowsCommand())
                .update()
                .list()
                .summary()
                .build();
    }
    
    @Module("toolTypes")
    public static Supplier<V3Config> getToolTypes() {
        return () -> new V3Config(V3ToolTypesContext.class)
                .create()
            		.afterSave(TransactionChainFactoryV3.getItemOrToolTypesAfterSaveChain())
                .update()
            		.afterSave(TransactionChainFactoryV3.getItemOrToolTypesAfterSaveChain())
                .list()
                	.beforeFetch(new LoadToolTypesLookUpCommandV3())
                    .afterFetch(new SetToolTypesUnitCommandV3())
                .summary()
            		.beforeFetch(new LoadToolTypesLookUpCommandV3())
                .build();
    }
    
    @Module("toolTypesStatus")
    public static Supplier<V3Config> getToolTypesStatus() {
        return () -> new V3Config(V3ToolTypesStatusContext.class)
                .create()
                	.afterSave(new ExecuteAllWorkflowsCommand())
                .update()
                .list()
                .summary()
                .build();
    }
    
    @Module("toolTypesCategory")
    public static Supplier<V3Config> getToolTypesCategory() {
        return () -> new V3Config(V3ToolTypesCategoryContext.class)
                .create()
            		.afterSave(new ExecuteAllWorkflowsCommand())
                .update()
                .list()
                .summary()
                .build();
    }

    @Module("visitorlogging")
    public static Supplier<V3Config> getVisitorLogging() {
        return () -> new V3Config(V3VisitorLoggingContext.class)
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
        return () -> new V3Config(V3VisitorContext.class)
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

    @Module("vendors")
    public static Supplier<V3Config> getVendor() {
        return () -> new V3Config(V3VendorContext.class)
                .create()
                  .beforeSave(new AddOrUpdateLocationForVendorCommandV3())
                  .afterSave(TransactionChainFactoryV3.getVendorsAfterSaveChain())
                .update()
                  .beforeSave(new AddOrUpdateLocationForVendorCommandV3())
                  .afterSave(TransactionChainFactoryV3.getVendorsAfterSaveChain())
                .list()
                  .beforeFetch(new LoadVendorLookupCommandV3())
                  .showStateFlowList()
                .summary()
                  .beforeFetch(new LoadVendorLookupCommandV3())
                .build();
    }

    @Module("tenant")
    public static Supplier<V3Config> getTenant() {
        return () -> new V3Config(V3TenantContext.class)
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
                .build();
    }

    @Module("workorder")
    public static Supplier<V3Config> getWorkorder() {
        return () -> new V3Config(V3WorkOrderContext.class)
                .create()
                  .beforeSave(TransactionChainFactoryV3.getWorkorderBeforeSaveChain())
                  .afterSave(TransactionChainFactoryV3.getWorkorderAfterSaveChain())
                .update()
                  .beforeSave(TransactionChainFactoryV3.getWorkorderBeforeUpdateChain())
                  .afterSave(TransactionChainFactoryV3.getWorkorderAfterUpdateChain(true))
                .list()
                  .afterFetch(new LoadWorkorderLookupsAfterFetchcommandV3())
                  .showStateFlowList()
                .summary()
                  .afterFetch(ReadOnlyChainFactoryV3.getWorkorderAfterFetchOnSummaryChain())
                .build();
    }

    @Module("tenantcontact")
    public static Supplier<V3Config> getTenantContact() {
        return () -> new V3Config(V3TenantContactContext.class)
                .create()
                    .beforeSave(TransactionChainFactoryV3.getTenantContactBeforeSaveChain())
                    .afterSave(TransactionChainFactoryV3.getTenantContactAfterSaveChain())
                .update()
                    .beforeSave(new CheckforPeopleDuplicationCommandV3())
		            .afterSave(TransactionChainFactoryV3.getTenantContactAfterSaveChain())
                .list()
                    .beforeFetch(new LoadTenantcontactLookupsCommandV3())
                .summary()
                    .beforeFetch(new LoadTenantcontactLookupsCommandV3())
                .build();
    }

    @Module("vendorcontact")
    public static Supplier<V3Config> getVendorContact() {
        return () -> new V3Config(V3VendorContactContext.class)
                .create()
                    .beforeSave(TransactionChainFactoryV3.getVendorContactBeforeSaveChain())
                    .afterSave(TransactionChainFactoryV3.getVendorContactAfterSaveChain())
                .update()
                    .beforeSave(new CheckforPeopleDuplicationCommandV3())
                    .afterSave(TransactionChainFactoryV3.getVendorContactAfterSaveChain())
                .list()
                    .beforeFetch(new LoadVendorContactLookupCommandV3())
                .summary()
                    .beforeFetch(new LoadVendorContactLookupCommandV3())
                .build();
    }

    @Module("employee")
    public static Supplier<V3Config> getEmployee() {
        return () -> new V3Config(V3EmployeeContext.class)
                .create()
                    .beforeSave(TransactionChainFactoryV3.getEmployeeBeforeSaveChain())
                    .afterSave(new UpdateEmployeePeopleAppPortalAccessCommandV3())
                .update()
                    .beforeSave(new CheckforPeopleDuplicationCommandV3())
                    .afterSave(new UpdateEmployeePeopleAppPortalAccessCommandV3())
                .list()
                .summary()
                .build();
    }

    @Module("clientcontact")
    public static Supplier<V3Config> getClientContact() {
        return () -> new V3Config(V3ClientContactContext.class)
                .create()
                    .beforeSave(TransactionChainFactoryV3.getClientContactBeforeSaveChain())
                    .afterSave(TransactionChainFactoryV3.getClientContactAfterSaveChain())
                .update()
                    .beforeSave(new CheckforPeopleDuplicationCommandV3())
                    .afterSave(TransactionChainFactoryV3.getClientContactAfterSaveChain())
                .list()
                    .beforeFetch(new LoadClientContactLookupCommandV3())
                .summary()
                    .beforeFetch(new LoadClientContactLookupCommandV3())
                .build();
    }
    @Module("workpermit")
    public static Supplier<V3Config> getWorkPermit() {
        return () -> new V3Config(V3WorkPermitContext.class)
                .create()
                .beforeSave(TransactionChainFactoryV3.getWorkPermitBeforeSaveOnCreateChain())
                .afterSave(TransactionChainFactoryV3.getWorkPermitAfterSaveOnCreateChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ACTIVITY))

                .update()
                .afterSave(TransactionChainFactoryV3.getWorkPermitAfterSaveOnUpdateChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ACTIVITY))

                .list()
                .beforeFetch(new LoadWorkPermitLookUpsCommandV3())

                .summary()
                .beforeFetch(new LoadWorkPermitLookUpsCommandV3())
                .afterFetch(TransactionChainFactoryV3.getWorkPermitSummaryAfterFetchChain())
                .build();
    }
    @Module("workpermittypechecklist")
    public static Supplier<V3Config> getWorkPermitTypeChecklist() {
        return () -> new V3Config(WorkPermitTypeChecklistContext.class)
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
        return () -> new V3Config(WorkPermitTypeChecklistCategoryContext.class)
                .create()
                .beforeSave(new WorkPermitChecklistCategoryValidationCommand())

                .update()
                .beforeSave(new WorkPermitChecklistCategoryValidationCommand())

                .list()
                .afterFetch(new WorkPermitFillChecklistForCategoryCommand())


                .build();
    }
    @Module("customMailMessages")
    public static Supplier<V3Config> getCustomMailMessages() {
        return () -> new V3Config(V3MailMessageContext.class)
                .create()
                .afterTransaction(new UpdateLatestMessageUIDCommandV3())
                .update()
                .build();
    }
    @Module("usernotification")
    public static Supplier<V3Config> getUsernotification() {
        return () -> new V3Config(UserNotificationContext.class)
                .create()
                    .afterSave(TransactionChainFactoryV3.getUserNotifactionBeforeSaveChain())
                .update()
                    .beforeSave(TransactionChainFactoryV3.getNotificationSeenUpdateChain())

                .list()
                   .beforeFetch(ReadOnlyChainFactoryV3.getUserNotificationBeforeFetchChain())
                .build();
    }

    @Module("announcement")
    public static Supplier<V3Config> getAnnouncement() {
        return () -> new V3Config(AnnouncementContext.class)
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
        return () -> new V3Config(PeopleAnnouncementContext.class)
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
        return () -> new V3Config(NewsAndInformationContext.class)
                .create().beforeSave(new SetLocalIdCommandV3()).afterSave(new UpdateAttachmentsParentIdCommandV3())
                .update().afterSave(new UpdateAttachmentsParentIdCommandV3())
                .list()
                    .beforeFetch(new LoadNewsAndInformationLookupCommandV3())
                .summary()
                    .beforeFetch(new LoadNewsAndInformationLookupCommandV3())
                .build();
    }

    @Module("neighbourhood")
    public static Supplier<V3Config> getNeighbourhood() {
        return () -> new V3Config(NeighbourhoodContext.class)
                .create().beforeSave(TransactionChainFactoryV3.getCreateNeighbourhoodBeforeSaveChain()).afterSave(new UpdateAttachmentsParentIdCommandV3())
                .update().beforeSave(new NeighbourhoodAddLocationCommand()).afterSave(new UpdateAttachmentsParentIdCommandV3())
                .summary().beforeFetch(new NeighbourhoodFillLookupFieldsCommand()).afterFetch(new FillNeighbourhoodSharingInfoCommand())
                .list().beforeFetch(new NeighbourhoodFillLookupFieldsCommand())
                .build();
    }

    @Module("dealsandoffers")
    public static Supplier<V3Config> getDealsAndOffers() {
        return () -> new V3Config(DealsAndOffersContext.class)
                .create().beforeSave(new SetLocalIdCommandV3()).afterSave(new UpdateAttachmentsParentIdCommandV3())
                .update().afterSave(new UpdateAttachmentsParentIdCommandV3())
                .summary().beforeFetch(new DealsAndOffersFillLookupFields()).afterFetch(new FillDealsAndOffersSharingInfoCommand())
                .list().beforeFetch(new DealsAndOffersFillLookupFields())
                .build();
    }

    @Module("contactdirectory")
    public static Supplier<V3Config> getContactDirectory() {
        return () -> new V3Config(ContactDirectoryContext.class)
                .create().beforeSave(new SetLocalIdCommandV3()).afterSave(new UpdateAttachmentsParentIdCommandV3())
                .update().afterSave(new UpdateAttachmentsParentIdCommandV3())
                .summary().beforeFetch(new ContactDirectoryFillLookupFieldsCommand()).afterFetch(new FillContactDirectorySharingInfoCommand())
                .list().beforeFetch(new ContactDirectoryFillLookupFieldsCommand())
                .build();
    }

    @Module("admindocuments")
    public static Supplier<V3Config> getAdminDocuments() {
        return () -> new V3Config(AdminDocumentsContext.class)
                .create().beforeSave(new SetLocalIdCommandV3())
                .update()
                .list()
                    .beforeFetch(new LoadAdminDocumentsLookupCommandV3())
                .summary()
                    .beforeFetch(new LoadAdminDocumentsLookupCommandV3())
                    .afterFetch(new FillAdminDocumentsSharingInfoCommand())
                .build();
    }

    @Module("accounttype")
    public static Supplier<V3Config> getAccountType() {
        return () -> new V3Config(AccountTypeContext.class)
                .update()
                .summary()
                .build();
    }

    @Module("chartofaccount")
    public static Supplier<V3Config> getChartOfAccount() {
        return () -> new V3Config(ChartOfAccountContext.class)
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
        return () -> new V3Config(BudgetContext.class)
                .create().
                        beforeSave(TransactionChainFactoryV3.getCreateBudgetBeforeSaveChain())
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
        return () -> new V3Config(V3TransactionContext.class)
                .create().
                 afterSave(new RollUpTransactionAmountCommand())
                .update()
                .afterSave(new RollUpTransactionAmountCommand())
                .delete()
                .afterDelete(new RollUpTransactionAmountCommand())
                .build();
    }


}

