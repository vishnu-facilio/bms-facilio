package com.facilio.bmsconsoleV3.commands;


import static com.facilio.bmsconsole.commands.TransactionChainFactory.addOrDeleteFaultImpactChain;
import static com.facilio.bmsconsole.commands.TransactionChainFactory.getAddCategoryReadingChain;

import java.util.Collections;

import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.*;
import com.facilio.bmsconsoleV3.commands.reports.*;
import com.facilio.bmsconsoleV3.commands.workorder.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsole.actions.GetModuleFromReportContextCommand;
import com.facilio.bmsconsole.actions.PurchaseOrderCompleteCommand;
import com.facilio.bmsconsole.automation.command.AddOrUpdateGlobalVariableCommand;
import com.facilio.bmsconsole.automation.command.AddOrUpdateGlobalVariableGroupCommand;
import com.facilio.bmsconsole.automation.command.DeleteGlobalVariableCommand;
import com.facilio.bmsconsole.automation.command.DeleteGlobalVariableGroupCommand;
import com.facilio.bmsconsole.commands.ActivateMLServiceCommand;
import com.facilio.bmsconsole.commands.AddBulkToolStockTransactionsCommand;
import com.facilio.bmsconsole.commands.AddClientUserCommandV3;
import com.facilio.bmsconsole.commands.AddFaultImpactRelationCommand;
import com.facilio.bmsconsole.commands.AddItemCommandV3;
import com.facilio.bmsconsole.commands.AddOrUpdateItemQuantityCommandV3;
import com.facilio.bmsconsole.commands.AddOrUpdateReportCommand;
import com.facilio.bmsconsole.commands.AddPMDetailsBeforeUpdateCommand;
import com.facilio.bmsconsole.commands.AddTemplateCommand;
import com.facilio.bmsconsole.commands.BeforeSavePMPlannerCommand;
import com.facilio.bmsconsole.commands.ConstructMLModelDetails;
import com.facilio.bmsconsole.commands.ConstructReadingForMLServiceCommand;
import com.facilio.bmsconsole.commands.ConstructReportData;
import com.facilio.bmsconsole.commands.ConstructTabularReportData;
import com.facilio.bmsconsole.commands.ConstructTicketNotesCommand;
import com.facilio.bmsconsole.commands.CreateReadingAnalyticsReportCommand;
import com.facilio.bmsconsole.commands.DeleteScheduledReportsCommand;
import com.facilio.bmsconsole.commands.DuplicateDashboardForBuildingCommand;
import com.facilio.bmsconsole.commands.EnableMobileDashboardCommand;
import com.facilio.bmsconsole.commands.ExecuteAllWorkflowsCommand;
import com.facilio.bmsconsole.commands.ExecuteRollUpFieldCommand;
import com.facilio.bmsconsole.commands.ExecuteTaskFailureActionCommand;
import com.facilio.bmsconsole.commands.ExecuteWorkFlowsBusinessLogicInPostTransactionCommand;
import com.facilio.bmsconsole.commands.ExportPivotReport;
import com.facilio.bmsconsole.commands.FetchCustomBaselineData;
import com.facilio.bmsconsole.commands.FetchReadingsModuleFieldsCommand;
import com.facilio.bmsconsole.commands.ForkChainToInstantJobCommand;
import com.facilio.bmsconsole.commands.GenerateCriteriaFromFilterCommand;
import com.facilio.bmsconsole.commands.GetAddPurchasedItemCommandV3;
import com.facilio.bmsconsole.commands.GetCategoryReadingsCommand;
import com.facilio.bmsconsole.commands.GetCriteriaDataCommand;
import com.facilio.bmsconsole.commands.GetExportReportFileCommand;
import com.facilio.bmsconsole.commands.GetReadingFieldsCommand;
import com.facilio.bmsconsole.commands.GetSpaceSpecifcReadingsCommand;
import com.facilio.bmsconsole.commands.InitMLServiceCommand;
import com.facilio.bmsconsole.commands.InsertReadingDataMetaForNewResourceCommand;
import com.facilio.bmsconsole.commands.ItemTypeQuantityRollupCommandV3;
import com.facilio.bmsconsole.commands.LoadWorkOrderServiceLookUpCommand;
import com.facilio.bmsconsole.commands.LoadWorkorderItemLookUpCommand;
import com.facilio.bmsconsole.commands.LoadWorkorderToolLookupCommand;
import com.facilio.bmsconsole.commands.PMBeforeCreateCommand;
import com.facilio.bmsconsole.commands.PivotColumnFormatCommand;
import com.facilio.bmsconsole.commands.PurchaseOrderAutoCompleteCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.ScheduleV2ReportCommand;
import com.facilio.bmsconsole.commands.SetItemAndToolTypeForStoreRoomCommandV3;
import com.facilio.bmsconsole.commands.SetTableNamesCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.TriggerMLServiceJobCommand;
import com.facilio.bmsconsole.commands.UpdateServiceVendorPriceCommand;
import com.facilio.bmsconsole.commands.UpdateTransactionEventTypeCommand;
import com.facilio.bmsconsole.commands.VerifyQrCommand;
import com.facilio.bmsconsole.commands.util.AddColorPaletteCommand;
import com.facilio.bmsconsole.commands.util.DeleteColorPaletteCommand;
import com.facilio.bmsconsole.commands.util.ListColorPaletteCommand;
import com.facilio.bmsconsole.commands.util.ReportSharePermission;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsoleV3.LookUpPrimaryFieldHandlingCommandV3;
import com.facilio.bmsconsoleV3.commands.accessibleSpaces.AddAccessibleSpacesCommand;
import com.facilio.bmsconsoleV3.commands.accessibleSpaces.DeleteAccessibleSpacesCommand;
import com.facilio.bmsconsoleV3.commands.accessibleSpaces.FetchAccessibleSpacesCommand;
import com.facilio.bmsconsoleV3.commands.asset.AddRotatingItemToolCommandV3;
import com.facilio.bmsconsoleV3.commands.asset.AssetSupplementsSupplyCommand;
import com.facilio.bmsconsoleV3.commands.asset.SparePartsSelectionCommand;
import com.facilio.bmsconsoleV3.commands.assetCategory.AddAssetCategoryModuleCommandV3;
import com.facilio.bmsconsoleV3.commands.assetCategory.UpdateCategoryAssetModuleIdCommandV3;
import com.facilio.bmsconsoleV3.commands.assetCategory.ValidateAssetCategoryDeletionV3;
import com.facilio.bmsconsoleV3.commands.assetDepartment.ValidateAssetDepartmentDeletionV3;
import com.facilio.bmsconsoleV3.commands.assetType.ValidateAssetTypeDeletionV3;
import com.facilio.bmsconsoleV3.commands.autocadfileimport.AddAutoCadFileImportCommand;
import com.facilio.bmsconsoleV3.commands.autocadfileimport.AddAutoCadLayerCommand;
import com.facilio.bmsconsoleV3.commands.budget.ValidateBudgetAmountCommandV3;
import com.facilio.bmsconsoleV3.commands.budget.ValidateChartOfAccountTypeCommandV3;
import com.facilio.bmsconsoleV3.commands.budget.ValidationForScopeCommandV3;
import com.facilio.bmsconsoleV3.commands.client.UpdateClientIdInSiteCommandV3;
import com.facilio.bmsconsoleV3.commands.clientcontact.CheckForMandatoryClientIdCommandV3;
import com.facilio.bmsconsoleV3.commands.clientcontact.UpdateClientAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments.AddOrUpdateAdminDocumentsSharingCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments.ValidateContactDirectoryEmailCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory.AddOrUpdateContactDirectorySharingCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.AddOrUpdateDealsSharingInfoCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.AddOrUpdateNeighbourhoodSharingCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.NeighbourhoodAddLocationCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation.AddOrUpdateNewsSharingCommandV3;
import com.facilio.bmsconsoleV3.commands.dashboard.AddWidgetCommandV3;
import com.facilio.bmsconsoleV3.commands.dashboard.CloneDashboardCommand;
import com.facilio.bmsconsoleV3.commands.dashboard.DashboardRuleCREDCommand;
import com.facilio.bmsconsoleV3.commands.dashboard.ExecutedDashboardRuleCommand;
import com.facilio.bmsconsoleV3.commands.dashboard.GetDashboardDataCommand;
import com.facilio.bmsconsoleV3.commands.dashboard.GetDashboardWidgetsCommand;
import com.facilio.bmsconsoleV3.commands.dashboard.MoveToDashboardCommand;
import com.facilio.bmsconsoleV3.commands.dashboard.UpdateWidgetCommandV3;
import com.facilio.bmsconsoleV3.commands.dashboard.V3UpdateDashboardTabWidgetCommand;
import com.facilio.bmsconsoleV3.commands.dashboard.V3UpdateDashboardWithWidgets;
import com.facilio.bmsconsoleV3.commands.employee.AddPeopleTypeForEmployeeCommandV3;
import com.facilio.bmsconsoleV3.commands.employee.AssignDefaultShift;
import com.facilio.bmsconsoleV3.commands.employee.UpdateEmployeePeopleAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.facility.CancelBookingCommand;
import com.facilio.bmsconsoleV3.commands.facility.CreatePaymentRecordForBookingCommandOnEditV3;
import com.facilio.bmsconsoleV3.commands.facility.SetCanEditForBookingCommand;
import com.facilio.bmsconsoleV3.commands.facility.ValidateCancelBookingCommandV3;
import com.facilio.bmsconsoleV3.commands.facility.ValidateFacilityBookingCommandV3;
import com.facilio.bmsconsoleV3.commands.facility.ValidateFacilityCommand;
import com.facilio.bmsconsoleV3.commands.failureclass.FetchFailureClassSupplements;
import com.facilio.bmsconsoleV3.commands.failureclass.FetchResourceSupplements;
import com.facilio.bmsconsoleV3.commands.floorplan.AddDeskCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.AddFloorPlanLayerCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.AddMarkedZonesCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.AddMarkerCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.AddORUpdateModuleRecordCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.AddOrUpdateObjectCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.SerializeCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.UpdateDeskCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.UpdateMarkerCommand;
import com.facilio.bmsconsoleV3.commands.formrelation.AddOrUpdateFormRelationCommand;
import com.facilio.bmsconsoleV3.commands.insurance.AssociateVendorToInsuranceCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.ValidateDateCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.AddOrUpdateInventoryRequestCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.AddOrUpdateManualItemTransactionCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.AddOrUpdateManualToolTransactionsCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.CopyToToolTransactionCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.ItemTransactionRemainingQuantityRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.LoadItemTransactionEntryInputCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.PurchasedItemsQuantityRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.ToolTransactionRemainingQuantityRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.UpdateRequestedItemIssuedQuantityCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.UpdateRequestedToolIssuedQuantityCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.ValidateInventoryRequestCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.ValidateInventoryRequestUpdateCommandV3;
import com.facilio.bmsconsoleV3.commands.jobplan.AddJobPlanTasksCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.AddPlannerIdFilterCriteriaCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.DeleteJobPlanValidationCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.FillJobPlanDetailsCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.FillUpJobPlanSectionAdditionInfoObject;
import com.facilio.bmsconsoleV3.commands.jobplan.FillUpJobPlanTaskAdditionInfoObject;
import com.facilio.bmsconsoleV3.commands.jobplan.JobPlanItemsUnsavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.jobplan.JobPlanServicesUnsavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.jobplan.JobPlanToolsUnsavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.jobplan.SortJobPlanTaskSectionCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.ValidationForJobPlanCategory;
import com.facilio.bmsconsoleV3.commands.jobplan.DeleteJobPlanSubModuleRecord;
import com.facilio.bmsconsoleV3.commands.licensinginfo.AddLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.DeleteLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.FetchLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.UpdateLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.people.PeopleValidationCommandV3;
import com.facilio.bmsconsoleV3.commands.people.UpdatePeoplePrimaryContactCommandV3;
import com.facilio.bmsconsoleV3.commands.people.UpdateScopingForPeopleCommandV3;
import com.facilio.bmsconsoleV3.commands.peoplegroup.FetchPeopleGroupMembersCommand;
import com.facilio.bmsconsoleV3.commands.purchaseorder.AddOrUpdateItemTypeVendorCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.AddOrUpdateToolVendorCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.AddPurchasedItemsForBulkItemAddCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.BulkItemAdditionCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.BulkToolAdditionCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.CompletePoCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.GetPurchaseOrdersListOnInventoryTypeIdCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.POAfterCreateOrEditV3Command;
import com.facilio.bmsconsoleV3.commands.purchaseorder.POBeforeCreateOrEditV3Command;
import com.facilio.bmsconsoleV3.commands.purchaseorder.UpdateIsPoCreatedCommand;
import com.facilio.bmsconsoleV3.commands.purchaserequest.LoadPoPrListLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaserequest.PreFillAddPurchaseRequestCommand;
import com.facilio.bmsconsoleV3.commands.purchaserequest.PurchaseRequestTotalCostRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.InsertQuotationLineItemsAndActivitiesCommand;
import com.facilio.bmsconsoleV3.commands.quotation.QuotationValidationAndCostCalculationCommand;
import com.facilio.bmsconsoleV3.commands.quotation.ReviseQuotationCommand;
import com.facilio.bmsconsoleV3.commands.quotation.UpdateQuotationParentIdCommand;
import com.facilio.bmsconsoleV3.commands.quotation.UpdateQuotationTermsAndConditionCommand;
import com.facilio.bmsconsoleV3.commands.readingimportapp.AddReadingImportAppDataCommand;
import com.facilio.bmsconsoleV3.commands.readingimportapp.DeleteReadingImportDataCommand;
import com.facilio.bmsconsoleV3.commands.readingimportapp.UpdateReadingImportDataCommand;
import com.facilio.bmsconsoleV3.commands.receipts.AddOrUpdateReceiptCommandV3;
import com.facilio.bmsconsoleV3.commands.receipts.GetReceiptsListOnReceivableIdCommandV3;
import com.facilio.bmsconsoleV3.commands.receipts.LoadReceiptsListLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.receipts.PurchaseOrderLineItemQuantityRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.receipts.PurchaseOrderQuantityRecievedRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.AwardVendorsCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.ConvertPrToRfqCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.ConvertRfqToPoCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.CreatePurchaseOrdersCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.CreateVendorQuotesCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.RfqBeforeCreateOrUpdateCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.SetRequestForQuotationBooleanFieldsCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.SetRequestForQuotationLineItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.UpdateRequestForQuotationCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.UpdateRequestForQuotationLineItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.safetyplan.AddWorkorderHazardPrecautionsFromSafetyPlanCommandV3;
import com.facilio.bmsconsoleV3.commands.safetyplan.ExcludeAssociatedHazardPrecautions;
import com.facilio.bmsconsoleV3.commands.safetyplan.ExcludeAvailableWorkOrderHazardPrecautions;
import com.facilio.bmsconsoleV3.commands.servicerequest.AddActivityForServiceRequestCommandV3;
import com.facilio.bmsconsoleV3.commands.servicerequest.AddRequesterForServiceRequestCommandV3;
import com.facilio.bmsconsoleV3.commands.servicerequest.SetIsNewForServiceRequestCommandV3;
import com.facilio.bmsconsoleV3.commands.shift.AddBreakShiftRelationshipCommand;
import com.facilio.bmsconsoleV3.commands.shift.AddShiftAbsenceDetectionJobCommand;
import com.facilio.bmsconsoleV3.commands.shift.ListShiftPlannerCommand;
import com.facilio.bmsconsoleV3.commands.shift.MarkAsNonDefaultShiftCommand;
import com.facilio.bmsconsoleV3.commands.shift.RemoveBreakShiftRelationshipCommand;
import com.facilio.bmsconsoleV3.commands.shift.RemoveShiftAbsenceDetectionJobCommand;
import com.facilio.bmsconsoleV3.commands.shift.UpdateShiftPlannerCommand;
import com.facilio.bmsconsoleV3.commands.shift.ValidateBreakCommand;
import com.facilio.bmsconsoleV3.commands.shift.ValidateShiftsCommand;
import com.facilio.bmsconsoleV3.commands.shift.ValidateShiftsUsageCommand;
import com.facilio.bmsconsoleV3.commands.space.SpaceFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.spacecategory.ValidateSpaceCategoryDeletionV3;
import com.facilio.bmsconsoleV3.commands.tasks.AddJobPlanSectionInputOptions;
import com.facilio.bmsconsoleV3.commands.tasks.AddJobPlanTaskInputOptions;
import com.facilio.bmsconsoleV3.commands.tasks.AddTaskOptions;
import com.facilio.bmsconsoleV3.commands.tasks.AddTaskSectionsV3;
import com.facilio.bmsconsoleV3.commands.tasks.AddTasksCommandV3;
import com.facilio.bmsconsoleV3.commands.tasks.FetchJobPlanSectionAndTaskInputOptions;
import com.facilio.bmsconsoleV3.commands.tasks.ValidateTasksCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantSpaceRelationCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantUserCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.CheckForMandatoryTenantIdCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.UpdateTenantAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.termsandconditions.ReviseTandCCommand;
import com.facilio.bmsconsoleV3.commands.tool.AddBulkToolStockTransactionsCommandV3;
import com.facilio.bmsconsoleV3.commands.tool.ToolQuantityRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.tool.ToolTypeQuantityRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.tool.UpdateIsUnderStockedCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.SetLineItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.UpdateCurrentBalanceAfterTransferCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.UpdateCurrentBalanceCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.UpdateItemTransactionAfterTransferCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.UpdateItemTransactionCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.UpdateNewLineItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.UpdateShipmentIdCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.UpdateStatusOfShipmentCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.UpdateToolTransactionAfterTransferCommandV3;
import com.facilio.bmsconsoleV3.commands.transferRequest.UpdateToolTransactionCommandV3;
import com.facilio.bmsconsoleV3.commands.usernotification.CheckUpdateMappingSeenCommand;
import com.facilio.bmsconsoleV3.commands.usernotification.MarkAsReadUserNotificationCommand;
import com.facilio.bmsconsoleV3.commands.usernotification.SendUserNotificationCommandV3;
import com.facilio.bmsconsoleV3.commands.usernotification.UpdateSeenNotificationCommandV3;
import com.facilio.bmsconsoleV3.commands.usernotification.UserNotificationSeenCommand;
import com.facilio.bmsconsoleV3.commands.vendor.AddInsuranceVendorRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddVendorContactsCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorcontact.CheckForMandatoryVendorIdCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorcontact.UpdateVendorContactAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.AddOrUpdateLocationForVisitorCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.CheckForVisitorDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.AddNdaForVisitorLogModuleCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.AddNewVisitorWhileBaseVisitCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.AddOrUpdateScheduleInRecurringVisitorCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.AddOrUpdateVisitorFromBaseVisitCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.AddOrUpdateVisitorTypeFormCommand;
import com.facilio.bmsconsoleV3.commands.visitorlog.AddWatchListRecordCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.ChangeInviteVisitorStateCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.ChangeVisitorLogStateCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.CheckForWatchListRecordBaseVisitCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.GenerateQrInviteUrlForBaseVisitCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.LoadRecordIdForPassCodeCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.PreFillInviteVisitorCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.PreFillVisitorLogCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.PutOldVisitRecordsInInviteVisitorContextCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.PutOldVisitRecordsInVisitorLogContextCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.UpdateInviteVisitorStateInChangeSetCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.UpdateRecurringRecordIdForBaseScheduleTrigger;
import com.facilio.bmsconsoleV3.commands.visitorlog.UpdateVisitorLogArrivedStateCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.VisitResponseCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.VisitorFaceRecognitionForBaseVisitCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.AddNdaForVisitorLogCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.AddNewVisitorWhileLoggingCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.AddOrUpdateVisitorFromVisitsCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.ChangeVisitorInviteStateCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.CheckForWatchListRecordCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.GenerateQrInviteUrlCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.PutOldVisitRecordsInContextCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.UpdateVisitorInviteRelArrivedStateCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.VisitorFaceRecognitionCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.AddOrUpdateWorkorderCostCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.SetDeleteWorkorderItemCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.SetDeleteWorkorderServiceCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.SetDeleteWorkorderToolCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.UpdateReservationRecordCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.UpdateReservedQuantityCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.UpdateWorkorderTotalCostCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.WorkOrderItemUnsavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.WorkOrderReservedItemsUnsavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.WorkOrderServiceUnsavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.WorkOrderToolsUnsavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.CreateReservationCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.CreateWorkOrderPlannedInventoryCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.PlannedItemsUnSavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.PlannedServicesUnSavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.PlannedToolsUnSavedListCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.PlansCostCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.ReservationSummaryCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.ReservationValidationCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.ReserveItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.SetIsReservedCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.SetWorkOrderPlannedItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.ValidateWorkOrderPlannedItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.ComputeScheduleForWorkPermitCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.FillWorkPermitChecklistCommand;
import com.facilio.bmsconsoleV3.commands.workpermit.InsertWorkPermitActivitiesCommand;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitRecurringInfoCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.ValidationForDateCommandV3;
import com.facilio.bmsconsoleV3.context.spacebooking.AddPolicyCriteriaCommand;
import com.facilio.bmsconsoleV3.context.spacebooking.FetchPolicyCommand;
import com.facilio.bmsconsoleV3.context.spacebooking.V3ValidateSpaceBookingAvailability;
import com.facilio.bmsconsoleV3.context.spacebooking.V3ValidateSpaceBookingCommand;
import com.facilio.bmsconsoleV3.context.spacebooking.ValidatePolicyCommand;
import com.facilio.bmsconsoleV3.context.spacebooking.setSpaceBookingVariableCommand;
import com.facilio.bmsconsoleV3.plannedmaintenance.jobplan.FillTasksAndPrerequisitesCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.ns.command.AddNamespaceCommand;
import com.facilio.ns.command.AddNamespaceFieldsCommand;
import com.facilio.ns.command.SetParentIdForNamespaceCommand;
import com.facilio.qa.command.BaseSchedulerSingleInstanceCommand;
import com.facilio.readingkpi.commands.create.PrepareReadingKpiCreationCommand;
import com.facilio.readingkpi.commands.create.SetFieldAndModuleIdCommand;
import com.facilio.readingkpi.commands.list.AddNamespaceInKpiListCommand;
import com.facilio.readingkpi.commands.list.FetchMetricAndUnitCommand;
import com.facilio.readingkpi.commands.update.PrepareReadingKpiForUpdateCommand;
import com.facilio.readingkpi.commands.update.UpdateNamespaceAndFieldsCommand;
import com.facilio.readingrule.command.AddAlarmDetailsCommand;
import com.facilio.readingrule.command.AddNewReadingRuleCommand;
import com.facilio.readingrule.command.AddRuleReadingsModuleCommand;
import com.facilio.readingrule.command.DeleteNamespaceReadingRuleCommand;
import com.facilio.readingrule.command.FetchReadingRuleSummaryCommand;
import com.facilio.readingrule.command.LoadSupplementsForNewReadingRule;
import com.facilio.readingrule.command.PrepareReadingRuleForUpdateCommand;
import com.facilio.readingrule.command.ReadingRuleDependenciesCommand;
import com.facilio.readingrule.command.UpdateReadingRuleCommand;
import com.facilio.readingrule.faultimpact.command.FaultImpactAfterSaveCommand;
import com.facilio.readingrule.faultimpact.command.FaultImpactBeforeSaveCommand;
import com.facilio.readingrule.rca.command.AddRCAGroupCommand;
import com.facilio.readingrule.rca.command.AddRCAMappingCommand;
import com.facilio.readingrule.rca.command.AddRCAScoreConditionCommand;
import com.facilio.readingrule.rca.command.AddRuleRCACommand;
import com.facilio.readingrule.rca.command.DeleteReadingRuleRcaCommand;
import com.facilio.readingrule.rca.command.FetchRCAGroupCommand;
import com.facilio.readingrule.rca.command.FetchRCAMappingCommand;
import com.facilio.readingrule.rca.command.FetchRCAScoreConditionCommand;
import com.facilio.readingrule.rca.command.FetchRuleRCACommand;
import com.facilio.readingrule.rca.command.UpdateRCAGroupCommand;
import com.facilio.readingrule.rca.command.UpdateRCAMappingCommand;
import com.facilio.readingrule.rca.command.UpdateRCAScoreConditionCommand;
import com.facilio.readingrule.rca.command.UpdateRuleRCACommand;
import com.facilio.relation.command.GenerateRelationDeleteAPIDataCommand;
import com.facilio.relation.command.GenerateRelationModuleAPIDataCommand;
import com.facilio.relation.command.ValidateRelationDataCommand;
import com.facilio.trigger.command.AddOrUpdateTriggerActionAndRelCommand;
import com.facilio.trigger.command.AddOrUpdateTriggerCommand;
import com.facilio.trigger.command.DeleteTriggerCommand;
import com.facilio.trigger.command.ExecuteTriggerCommand;
import com.facilio.trigger.command.GetAllTriggersCommand;
import com.facilio.v3.commands.ConstructAddCustomActivityCommandV3;
import com.facilio.v3.commands.ConstructUpdateCustomActivityCommandV3;
import com.facilio.v3.commands.CountCommand;
import com.facilio.v3.commands.FetchChangeSetForCustomActivityCommand;
import com.facilio.workflows.command.AddWorkflowCommand;
import com.facilio.workflows.command.UpdateWorkflowCommand;

public class TransactionChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getWorkPermitAfterSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new InsertWorkPermitActivitiesCommand());
        c.addCommand(new RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3());

        return c;
    }

    public static FacilioChain getWorkPermitBeforeSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new ComputeScheduleForWorkPermitCommandV3());
        c.addCommand(new ValidationForDateCommandV3());

        return c;
    }

    public static FacilioChain getWorkPermitBeforeSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new ValidationForDateCommandV3());

        return c;
    }

    public static FacilioChain getWorkPermitAfterSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new InsertWorkPermitActivitiesCommand());
        c.addCommand(new LoadWorkPermitLookUpsCommandV3());
        c.addCommand(new RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3());

        return c;
    }

    public static FacilioChain getWorkPermitSummaryAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadWorkPermitRecurringInfoCommandV3());
        c.addCommand(new FillWorkPermitChecklistCommand());
        return c;
    }

    public static FacilioChain getVisitorLoggingBeforeSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new PutOldVisitRecordsInContextCommandV3());
        c.addCommand(new AddNewVisitorWhileLoggingCommandV3());
        c.addCommand(new AddOrUpdateVisitorFromVisitsCommandV3());
        c.addCommand(new CheckForWatchListRecordCommandV3());

        return c;
    }

    public static FacilioChain getVisitorLoggingAfterSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new UpdateVisitorInviteRelArrivedStateCommandV3());
        c.addCommand(new ChangeVisitorInviteStateCommandV3());
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new AddNdaForVisitorLogCommandV3())
                .addCommand(new GenerateQrInviteUrlCommandV3())
                .addCommand(new VisitorFaceRecognitionCommandV3()));

        return c;
    }

    public static FacilioChain getVisitorLoggingBeforeSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new PutOldVisitRecordsInContextCommandV3());
        c.addCommand(new AddOrUpdateVisitorFromVisitsCommandV3());
        return c;
    }

    public static FacilioChain getVisitorLoggingAfterSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ChangeVisitorInviteStateCommandV3());
        return c;
    }

    public static FacilioChain getVisitorLogBeforeSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new PutOldVisitRecordsInVisitorLogContextCommandV3()); // check-in related
        c.addCommand(new AddNewVisitorWhileBaseVisitCommandV3());
        c.addCommand(new PreFillVisitorLogCommandV3()); // check-in related
        c.addCommand(new AddOrUpdateVisitorFromBaseVisitCommandV3());
        c.addCommand(new CheckForWatchListRecordBaseVisitCommandV3());

        return c;
    }

    public static FacilioChain getVisitorLogAfterSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new UpdateVisitorLogArrivedStateCommandV3()); // check-in related
        c.addCommand(new ChangeVisitorLogStateCommandV3()); // check-in related
        c.addCommand(new GenerateQrInviteUrlForBaseVisitCommandV3());
        c.addCommand(new UpdateVisitorLogArrivedStateCommandV3()); // check-in related
        c.addCommand(new ChangeVisitorLogStateCommandV3()); // check-in related
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new AddNdaForVisitorLogModuleCommandV3()) // check-in related
                .addCommand(new GenerateQrInviteUrlForBaseVisitCommandV3())
                .addCommand(new VisitorFaceRecognitionForBaseVisitCommandV3()));
        c.addCommand(new VisitResponseCommandV3());

        return c;
    }

    public static FacilioChain getVisitorLogBeforeSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new LoadRecordIdForPassCodeCommandV3());
        c.addCommand(new PutOldVisitRecordsInVisitorLogContextCommandV3());
        c.addCommand(new AddOrUpdateVisitorFromBaseVisitCommandV3());
        return c;
    }

    public static FacilioChain getVisitorLogAfterSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ChangeVisitorLogStateCommandV3());
        c.addCommand(new AddWatchListRecordCommandV3());
        return c;
    }

    public static FacilioChain getInviteVisitorBeforeSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new PutOldVisitRecordsInInviteVisitorContextCommandV3());
        c.addCommand(new AddNewVisitorWhileBaseVisitCommandV3());
        c.addCommand(new PreFillInviteVisitorCommandV3());
        c.addCommand(new AddOrUpdateVisitorFromBaseVisitCommandV3());
        c.addCommand(new CheckForWatchListRecordBaseVisitCommandV3());

        return c;
    }

    public static FacilioChain getInviteVisitorAfterSaveOnCreateBeforeTransactionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateQrInviteUrlForBaseVisitCommandV3());
        c.addCommand(new VisitorFaceRecognitionForBaseVisitCommandV3());
        return c;
    }

    public static FacilioChain getInviteVisitorAfterSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new UpdateInviteVisitorStateInChangeSetCommandV3());
        c.addCommand(new ChangeInviteVisitorStateCommandV3());
        return c;
    }

    public static FacilioChain getInviteVisitorBeforeSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new PutOldVisitRecordsInInviteVisitorContextCommandV3());
        c.addCommand(new AddOrUpdateVisitorFromBaseVisitCommandV3());

        return c;
    }

    public static FacilioChain getInviteVisitorAfterSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ChangeInviteVisitorStateCommandV3());
        return c;
    }

    public static FacilioChain getRecurringInviteVisitorBeforeSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new PutOldVisitRecordsInInviteVisitorContextCommandV3());
        c.addCommand(new AddNewVisitorWhileBaseVisitCommandV3());
        c.addCommand(new PreFillInviteVisitorCommandV3());
        c.addCommand(new AddOrUpdateVisitorFromBaseVisitCommandV3());
        c.addCommand(new CheckForWatchListRecordBaseVisitCommandV3());
        c.addCommand(new AddOrUpdateScheduleInRecurringVisitorCommandV3());
        return c;
    }

    public static FacilioChain getRecurringInviteVisitorAfterSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new UpdateInviteVisitorStateInChangeSetCommandV3());
        c.addCommand(new ChangeInviteVisitorStateCommandV3());
        c.addCommand(new UpdateRecurringRecordIdForBaseScheduleTrigger());
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new GenerateQrInviteUrlForBaseVisitCommandV3())
                .addCommand(new VisitorFaceRecognitionForBaseVisitCommandV3()));
        return c;
    }

    public static FacilioChain getRecurringInviteVisitorBeforeSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new PutOldVisitRecordsInInviteVisitorContextCommandV3());
        c.addCommand(new AddOrUpdateVisitorFromBaseVisitCommandV3());
        c.addCommand(new AddOrUpdateScheduleInRecurringVisitorCommandV3());

        return c;
    }

    public static FacilioChain getVisitorBeforeSaveOnAddChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new AddOrUpdateLocationForVisitorCommandV3());
        c.addCommand(new CheckForVisitorDuplicationCommandV3());
        return c;
    }

    public static FacilioChain getVisitorAfterSaveOnAddChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new VisitorFaceRecognitionCommandV3()));
        return c;
    }

    public static FacilioChain getQuotationAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateQuotationParentIdCommand());
        c.addCommand(new InsertQuotationLineItemsAndActivitiesCommand());
        c.addCommand(new UpdateQuotationTermsAndConditionCommand());
        return c;
    }

    public static FacilioChain getQuotationAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new InsertQuotationLineItemsAndActivitiesCommand());
        return c;
    }

    public static FacilioChain getQuotationBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReviseQuotationCommand());
        c.addCommand(new QuotationValidationAndCostCalculationCommand());
        return c;
    }

    public static FacilioChain getTermsBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReviseTandCCommand());
        return c;
    }

    public static FacilioChain getVendorsAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddVendorContactsCommandV3());
        c.addCommand(new AddInsuranceVendorRollupCommandV3());
        return c;
    }

    public static FacilioChain getTenantAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddTenantUserCommandV3());
        c.addCommand(new AddTenantSpaceRelationCommandV3());
        c.addCommand(new AddTenantToTenantUnitCommandV3());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.TENANT_ACTIVITY));
        return c;
    }

    public static FacilioChain getWorkorderBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddPortalRequestsDetailsCommandV3());
        c.addCommand(new PMSettingsCommandV3());
        c.addCommand(new AddRequesterCommandV3());
        c.addCommand(new WorkOrderPreAdditionHandlingCommandV3());
        c.addCommand(new ValidateWorkOrderFieldsCommandV3());
        c.addCommand(new UpdateEventListForStateFlowCommandV3());
        c.addCommand(new AddWorkOrderCommandV3());
        c.addCommand(new AddFailureClassFromResource());

        return c;
    }

    public static FacilioChain getWorkOrderBeforeSavePreCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalModuleIdCommand());
        c.addCommand(new SetWorkOrderSourceType());
        c.addCommand(new ValidateWorkOrderFieldsPreCreateChainCommandV3());
        c.addCommand(new AddFailureClassFromResource());
        // Attachment Command has to be added after its fixes are done
        return c;
    }

    public static FacilioChain getWorkOrderAfterSavePreCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateTasksCommandV3());
        c.addCommand(new FillTasksAndPrerequisitesCommand());
        c.addCommand(new AddTaskSectionsV3());
        c.addCommand(new AddTasksCommandV3());
        c.addCommand(new AddTaskOptions());
        return c;
    }

    public static FacilioChain getWorkOrderAfterSavePostCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new FetchWorkorderRecordAndValidateForPostCreate());
        c.addCommand(new FillContextAfterAddingWorkOrderPostCreateChainCommandV3());
        c.addCommand(new GetRecordIdsFromRecordMapCommandV3());
        c.addCommand(new AddPrerequisiteApproversCommandV3());
        c.addCommand(new UpdateJobStatusForWorkOrderCommand());
        c.addCommand(new AddActivityForWoPostCreateCommand());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.WORKORDER_ACTIVITY));
        return c;
    }

    public static FacilioChain getWorkOrderWorkflowsChainV3(boolean sendNotification) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_CUSTOM_CHANGE));
        c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SATISFACTION_SURVEY_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SURVEY_ACTION_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_RULE,
                WorkflowRuleContext.RuleType.CHILD_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_APPROVAL_RULE,
                WorkflowRuleContext.RuleType.REQUEST_REJECT_RULE));

        if (sendNotification) {
            if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 218L) {
                c.addCommand(
                        new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE,
                                WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE));
            } else {
                c.addCommand(new ForkChainToInstantJobCommand()
                        .addCommand(new ExecuteAllWorkflowsCommand(
                                WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE,
                                WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE)));
            }
        }
        return c;
    }

    public static FacilioChain getWorkorderAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateTasksCommandV3());
        c.addCommand(new FillTasksAndPrerequisitesCommand());
        c.addCommand(new AddTaskSectionsV3());
        c.addCommand(new AddTasksCommandV3());
        c.addCommand(new AddTaskOptions());
        c.addCommand(new FillContextAfterWorkorderAddCommandV3());
        c.addCommand(new AddWorkorderHazardsFromSafetyPlanCommandV3());
        c.addCommand(new AddWorkorderHazardPrecautionsFromSafetyPlanCommandV3());
        c.addCommand(new GetRecordIdsFromRecordMapCommandV3());
        c.addCommand(new AddTicketActivityCommandV3());
        // c.addCommand(getAddTasksChainV3());
        c.addCommand(new AddPrerequisiteApproversCommandV3());
        c.addCommand(new FacilioCommand() {
            @Override
            public boolean executeCommand(Context context) throws Exception {
                context.put(FacilioConstants.ContextNames.RECORD_LIST,
                        Collections.singletonList(context.get(FacilioConstants.ContextNames.WORK_ORDER)));
                return false;
            }
        });
        c.addCommand(getWorkOrderWorkflowsChainV3(true));
        c.addCommand(new AddOrUpdateMultiResourceForWorkorderCommandV3());
        // to be removed once all attachments are handled as sub module
        c.addCommand(new UpdateTicketAttachmentsOldParentIdCommandV3());
        c.addCommand(new UpdateAttachmentCountCommand());
        c.addCommand(new AddActivitiesCommandV3());
        //planned inventory
        c.addCommand(getPlannedInventoryChainV3());
        return c;
    }

    public static FacilioChain getPlannedInventoryChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateWorkOrderPlannedInventoryCommandV3());
        return c;
    }

    public static FacilioChain getWorkorderBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetRecordIdsFromRecordMapCommandV3());
        c.addCommand(new FetchOldWorkordersCommandV3());
        c.addCommand(new ValidateWOForUpdate());
        // c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
        // c.addCommand(new VerifyApprovalCommandV3());
        c.addCommand(new UpdateEventListForStateFlowCommandV3());
        c.addCommand(new UpdateWorkorderFieldsForUpdateCommandV3());
        c.addCommand(new FillTasksAndPrerequisitesCommand());
        c.addCommand(new BackwardCompatibleStateFlowUpdateCommandV3());
        c.addCommand(new ToVerifyStateFlowtransitionForStartCommandV3());
        c.addCommand(new AddFailureClassFromResource());

        return c;
    }

    public static FacilioChain getWorkorderAfterUpdateChain(boolean sendNotification) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillContextAfterWorkorderUpdateCommandV3());
        c.addCommand(new VerifyQrCommand());
        c.addCommand(new SendNotificationCommandV3());
//		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SATISFACTION_SURVEY_RULE));
//		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SURVEY_ACTION_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_RULE,
                WorkflowRuleContext.RuleType.CHILD_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_APPROVAL_RULE,
                WorkflowRuleContext.RuleType.REQUEST_REJECT_RULE));
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(
                        new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE,
                                WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE))
                .addCommand(new ClearAlarmOnWOClosureCommand())
                .addCommand(new ExecuteTaskFailureActionCommand()));
        c.addCommand(new ConstructTicketNotesCommand());
        c.addCommand(TransactionChainFactory.getAddNotesChain());
        c.addCommand(new AddOrUpdateMultiResourceForWorkorderCommandV3());
        c.addCommand(new AddActivitiesCommand());
        c.addCommand(new AddWorkorderHazardsFromSafetyPlanCommandV3());
        c.addCommand(new AddWorkorderHazardPrecautionsFromSafetyPlanCommandV3());
        c.addCommand(new UpdateAttachmentCountCommand());
        c.addCommand(new UpdateCloseAllFromBulkActionCommandV3());
        return c;
    }

    public static FacilioChain getTenantContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new CheckForMandatoryTenantIdCommandV3());
        return c;
    }

    public static FacilioChain getTenantContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new UpdateTenantAppPortalAccessCommandV3());
        c.addCommand(new UpdateScopingForPeopleCommandV3());
        c.addCommand(new AssignDefaultShift());
        return c;
    }

    public static FacilioChain getVendorContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new CheckForMandatoryVendorIdCommandV3());
        return c;
    }

    public static FacilioChain getVendorContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new UpdateVendorContactAppPortalAccessCommandV3());
        c.addCommand(new UpdateScopingForPeopleCommandV3());
        c.addCommand(new AssignDefaultShift());
        return c;
    }

    public static FacilioChain getEmployeeBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new AddPeopleTypeForEmployeeCommandV3());
        return c;
    }

    public static FacilioChain getEmployeeAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateEmployeePeopleAppPortalAccessCommandV3());
        c.addCommand(new AssignDefaultShift());
        return c;
    }

    public static FacilioChain getClientContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new CheckForMandatoryClientIdCommandV3());
        return c;
    }

    public static FacilioChain getClientContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new UpdateClientAppPortalAccessCommandV3());
        c.addCommand(new UpdateScopingForPeopleCommandV3());
        c.addCommand(new AssignDefaultShift());
        return c;
    }

    public static FacilioChain getAddClientsAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateClientIdInSiteCommandV3());
        c.addCommand(new AddClientUserCommandV3());
        return c;
    }

    public static FacilioChain getServiceRequestBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddRequesterForServiceRequestCommandV3());
        c.addCommand(new SetIsNewForServiceRequestCommandV3());
        return c;
    }

    public static FacilioChain getServiceRequestAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        // c.addCommand(new UpdateAttachmentsParentIdCommandV3());
        c.addCommand(new AddActivityForServiceRequestCommandV3());
        c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
        return c;
    }

    public static FacilioChain getTenantUnitAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateHelperFieldsCommandV3());
        c.addCommand(getSpaceReadingsChain());
        c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
        c.addCommand(new SetSpaceRecordForRollUpFieldCommandV3());
        c.addCommand(new ExecuteRollUpFieldCommand());
        return c;
    }

    public static FacilioChain getSpaceReadingsChain() {
        FacilioChain c = FacilioChain.getNonTransactionChain();
        c.addCommand(new GetSpaceSpecifcReadingsCommand());
        c.addCommand(new GetCategoryReadingsCommand());
        c.addCommand(new GetReadingFieldsCommand());
        return c;
    }

    public static FacilioChain getSpaceBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SpaceFillLookupFieldsCommand());
        c.addCommand(new AddPlannerIdFilterCriteriaCommand());
        return c;
    }

    public static FacilioChain getUpdateAnnouncementAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateAttachmentsParentIdCommandV3());
        c.addCommand(new PublishAnnouncementCommandV3());
        c.addCommand(new CancelParentChildAnnouncementsCommandV3());
        return c;

    }

    public static FacilioChain getAddPurchaseRequestAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PurchaseRequestTotalCostRollUpCommandV3()); // update purchase request total cost
        c.addCommand(new UpdateTransactionEventTypeV3Command());
        return c;
    }

    public static FacilioChain getAddPurchaseRequestBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new PreFillAddPurchaseRequestCommand());

        return c;
    }

    public static FacilioChain addRecords() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddRecordsCommandV3());
        return c;
    }

    public static FacilioChain getUpdateAnnouncementBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidatePublishAnnouncementActionCommandV3());
        c.addCommand(new ValidateCancelAnnouncementActionCommandV3());
        c.addCommand(new SetAnnouncementPhotoIdCommand());
        c.addCommand(new AddOrUpdateAnnouncementSharingInfoCommandV3());
        return c;

    }

    public static FacilioChain getCreateAnnouncementBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CloneAnnouncementCommandV3());
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new SetAnnouncementPhotoIdCommand());
        // c.addCommand(new CheckForSharingInfoCommandV3());
        c.addCommand(new AddOrUpdateAnnouncementSharingInfoCommandV3());
        return c;

    }

    public static FacilioChain getNotificationSeenUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateSeenNotificationCommandV3());
        return c;
    }

    public static FacilioChain getUserNotifactionBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CheckUpdateMappingSeenCommand());
        c.addCommand(new SetMessageTopicCommand());
        c.addCommand(new SendUserNotificationCommandV3());
        return c;
    }

    public static FacilioChain getUserNotificationSeenUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UserNotificationSeenCommand());
        return c;
    }

    public static FacilioChain getMarkAllAsReadUserNotification() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MarkAsReadUserNotificationCommand());
        return c;
    }

    public static FacilioChain getCreateNeighbourhoodBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new NeighbourhoodAddLocationCommand());
        c.addCommand(new AddOrUpdateNeighbourhoodSharingCommandV3());

        return c;

    }

    public static FacilioChain getCreateNeighbourhoodBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new NeighbourhoodAddLocationCommand());
        c.addCommand(new AddOrUpdateNeighbourhoodSharingCommandV3());

        return c;

    }

    public static FacilioChain getCreateDealsBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new AddOrUpdateDealsSharingInfoCommandV3());

        return c;

    }

    public static FacilioChain getCreateBudgetBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new ValidateBudgetAmountCommandV3());
        c.addCommand(new ValidationForScopeCommandV3());
        return c;

    }

    public static FacilioChain getCreateChartOfAccountBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new ValidateChartOfAccountTypeCommandV3());
        return c;

    }

    public static FacilioChain getCreateNewsAndInformationBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new AddOrUpdateNewsSharingCommandV3());
        return c;
    }

    public static FacilioChain getCreateContactDirectoryBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateContactDirectoryEmailCommand());
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new AddOrUpdateContactDirectorySharingCommandV3());
        // c.addCommand(new AddPeopleIfNotExistsCommandV3());
        return c;
    }

    public static FacilioChain getCreateAdminDocumentsBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new AddOrUpdateAdminDocumentsSharingCommandV3());
        return c;
    }

    public static FacilioChain getUpdateEmployeeAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateEmployeePeopleAppPortalAccessCommandV3());
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        return c;
    }

    public static FacilioChain getTriggerAddOrUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateTriggerCommand());
        // c.addCommand(new AddOrUpdateTriggerInclExclCommand());
        c.addCommand(new AddOrUpdateTriggerActionAndRelCommand());
        return c;
    }

    public static FacilioChain getAllTriggers() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetAllTriggersCommand());
        return chain;
    }

    public static FacilioChain getTriggerDeleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteTriggerCommand());
        return c;
    }

    public static FacilioChain rearrangeTriggerActionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RearrangeTriggerActionCommand());
        return c;
    }

    public static FacilioChain getTriggerExecuteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecuteTriggerCommand());
        return c;
    }

    public static FacilioChain getPoBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new POBeforeCreateOrEditV3Command());
        return c;
    }

    public static FacilioChain getPoAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateIsPoCreatedCommand());
        c.addCommand(new POAfterCreateOrEditV3Command());
        c.addCommand(new UpdateTransactionEventTypeV3Command());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.PURCHASE_ORDER_ACTIVITY));
        return c;
    }

    public static FacilioChain getCreateBookingBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new SetCanEditForBookingCommand());
        c.addCommand(new ValidateFacilityBookingCommandV3());
        return c;
    }

    public static FacilioChain getCreateBookingBeforeEditChain() {
        FacilioChain c = getDefaultChain();
        // c.addCommand(new ValidateCanEditBookingCommand());
        // c.addCommand(new ValidateFacilityBookingCommandForEditV3());
        c.addCommand(new ValidateCancelBookingCommandV3());
        return c;
    }

    public static FacilioChain getUpdateBookingAfterEditChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CancelBookingCommand());
        c.addCommand(new CreatePaymentRecordForBookingCommandOnEditV3());
        return c;
    }

    public static FacilioChain getAddControlScheduleBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddControlScheduleBeforeSaveCommand());
        return c;
    }

    public static FacilioChain getAddControlScheduleAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteAndAddControlScheduleExceptionCommand());
        c.addCommand(new PlanControlScheduleSlotCommand());
        return c;
    }

    public static FacilioChain getDeleteAndAddControlScheduleExceptionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteAndAddControlScheduleExceptionCommand());
        return c;
    }

    public static FacilioChain getUpdateControlScheduleBeforeSaveCommandChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateControlScheduleBeforeSaveCommand());
        return c;
    }

    public static FacilioChain getUpdateControlScheduleAfterSaveCommandChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteAndAddControlScheduleExceptionCommand());
        c.addCommand(new PlanControlScheduleSlotCommand());
        return c;
    }

    public static FacilioChain getAddControlScheduleExceptionBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ControlScheduleExceptionBeforeSaveCommand());
        c.addCommand(new ControlScheduleExceptionValidateCommand());
        return c;
    }

    public static FacilioChain getAddControlScheduleExceptionAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ControlScheduleExceptionAfterSaveCommand());
        c.addCommand(new PlanControlScheduleExceptionSlotCommand());
        return c;
    }

    public static FacilioChain getUpdateControlScheduleExceptionAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PlanControlScheduleExceptionSlotCommand());
        return c;
    }

    public static FacilioChain getUpdateControlGroupRoutineChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteControlGroupRoutineSectionAndFieldCommand());
        c.addCommand(new AddControlGroupRoutineSectionAndFieldCommand());
        return c;
    }

    public static FacilioChain getAddControlGroupAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddControlGroupV2Command());
        c.addCommand(new PlanControlGroupSlotsCommand());
        return c;
    }

    public static FacilioChain getPlanControlGroupSlotChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PlanControlGroupSlotsCommand());
        return c;
    }

    public static FacilioChain deleteControlGroupSlotChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteControlGroupSlotsCommand());
        return c;
    }

    public static FacilioChain getUpdateControlGroupAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateControlGroupV2Command());
        c.addCommand(new UpdateControlGroupRelatedV2Command());
        c.addCommand(new PlanControlGroupSlotsCommand());
        return c;
    }

    public static FacilioChain getUpdateOrDeleteControlGroupSlotAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteExceptionsForOneTimeSlotCommand());
        c.addCommand(new FetchCurrentDaySlotsCommand());
        c.addCommand(new PlanControlGroupFinalSlots());
        c.addCommand(new PlanControlGroupCommands());
        return c;
    }

    public static FacilioChain planControlGroupSlotsAndRoutines() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PlanControlGroupSlots());
        c.addCommand(new PlanControlGroupFinalSlots());
        c.addCommand(new PlanControlGroupCommands());
        return c;
    }

    public static FacilioChain getChangeStatusOfTriggerChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ChangeStatusOfTriggerCommand());
        chain.addCommand(new AddOrUpdateTriggerCommand());
        return chain;
    }

    public static FacilioChain getRelationDataAddBeforeSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateRelationDataCommand());
        return chain;
    }

    public static FacilioChain getRelationDataBeforeDeleteChain() {
        FacilioChain chain = getDefaultChain();
//        chain.addCommand(new ValidateRelationParamCommand());
        return chain;
    }

    public static FacilioChain getRelationAPIDataChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GenerateRelationModuleAPIDataCommand());
        return chain;
    }

    public static FacilioChain getRelationDeleteAPIDataChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GenerateRelationDeleteAPIDataCommand());
        return chain;
    }

    public static FacilioChain getDataCountChain(FacilioModule module) {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new CountCommand(module));
        return chain;
    }

    public static FacilioChain getControlGroupPublishChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ControlSchedulePublishCommand());
        chain.addCommand(new ControlGroupPublishCommand());
        return chain;
    }

    public static FacilioChain getControlGroupUnPublishChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ControlGroupUnPublishCommand());
        return chain;
    }

    public static FacilioChain controlGroupResetTenantChanges() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetParentcontrolGroupCommand());
        chain.addCommand(new ResetControlScheduleAndExceptionsCommand());
        chain.addCommand(new ResetControlGroupCommand());
        return chain;
    }

    public static FacilioChain getControlCommandExecutionCreateScheduleChain() {
        // TODO Auto-generated method stub
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetCommandsAndScheduleForExecutionCommand());
        return chain;
    }

    public static FacilioChain getUpdateJobPlanBeforeSaveChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        // added this command to prefill/remove properties in JobPlanSection's additionInfo object
        chain.addCommand(new FillUpJobPlanSectionAdditionInfoObject());
        chain.addCommand(new ValidationForJobPlanCategory());
        return chain;
    }

    public static FacilioChain getUpdateJobPlanAfterSaveChain() {
        FacilioChain chain = getDefaultChain();
        // added this command to prefill/remove properties in JobPlanTask's additionInfo object
        chain.addCommand(new FillUpJobPlanTaskAdditionInfoObject());
        chain.addCommand(new AddJobPlanSectionInputOptions());
        chain.addCommand(new AddJobPlanTasksCommand());
        chain.addCommand(new AddJobPlanTaskInputOptions());
        // chain.addCommand(new AddJobPlanPMsInContextCommand());
        chain.addCommand(new ConstructUpdateCustomActivityCommandV3());
        chain.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.JOB_PLAN_ACTIVITY));
        chain.addCommand(new DeleteJobPlanSubModuleRecord());

        return chain;
    }

    public static FacilioChain getJobPlanSummaryAfterFetchChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FillJobPlanDetailsCommand());
        chain.addCommand(new SortJobPlanTaskSectionCommand());
        chain.addCommand(new FetchJobPlanSectionAndTaskInputOptions());
        return chain;
    }

    public static FacilioChain getDeleteJobPlanBeforeChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new DeleteJobPlanValidationCommand());
        return chain;
    }

    public static FacilioChain addfloorplanObjectsChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddDeskCommand());
        chain.addCommand(new AddMarkerCommand());
        chain.addCommand(new AddMarkedZonesCommand());
        chain.addCommand(new AddFloorPlanLayerCommand());
        return chain;
    }

    public static FacilioChain addMarkersAndModulesCommand() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddDeskCommand());
        chain.addCommand(new AddMarkerCommand());
        return chain;
    }

    public static FacilioChain AddORUpdateMarkersAndModulesCommand() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddORUpdateModuleRecordCommand());
        chain.addCommand(new AddOrUpdateObjectCommand());
        return chain;
    }

    public static FacilioChain updateMarkersAndModulesCommand() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new UpdateDeskCommand());
        chain.addCommand(new UpdateMarkerCommand());
        return chain;
    }

    public static FacilioChain getFloorPlanObjectsChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SerializeCommand());
        return chain;
    }

    public static FacilioChain getEmailConversationThreadingBeforeListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddSuplimentsForEmailBaseMessageCommand());
        chain.addCommand(new AddFetchCriteriaForEmailConversationThreadingCommand());
        return chain;
    }

    public static FacilioChain getAddEmailConversationThreadingAfterSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SendEmailForEmailConversationThreadingCommand());
        chain.addCommand(new AddActivityInRelatedModuleForEmailConversationThreadingCommand());
        chain.addCommand(new ExecuteWorkflowInRelatedModuleForEmailConversationThreadingCommand());
        chain.addCommand(new SetModeInRelatedModuleForEmailConversationThreadingCommand());
        return chain;
    }

    public static FacilioChain getAddEmailConversationThreadingBeforeSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddSuplimentsForEmailBaseMessageCommand());
        chain.addCommand(new AddDefaultFieldsForEmailThreadingCommand());
        chain.addCommand(new AddEmailsToPeopleCommandV3());
        return chain;
    }

    public static FacilioChain getAddEmailToModuleDataBeforeSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddSuplimentsForEmailBaseMessageCommand());
        return chain;
    }

    public static FacilioChain getEmailFromAddressBeforeSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new EmailFromAddressValidateCommand());
        chain.addCommand(new EmailFromAddressAddDefaultValuesCommand());
        return chain;
    }

    public static FacilioChain getReSendVerificationEmailChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ReSendVerificationEmailCommand());
        return chain;
    }

    public static FacilioChain getFromEmailForEmailThreadingReplyChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FromEmailForEmailThreadingReplyCommand());
        return chain;
    }

    public static FacilioChain getEmailFromAddressAfterSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SendVerifcationEmailForFromAddressCommand());
        return chain;
    }

    public static Command getEmailConversationThreadingAfterListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddAttachmentsForEmailConversationThreadingCommand());
        chain.addCommand(new SetUserAndPeopleForEmailConversationThreadingCommand());
        return chain;
    }

    public static FacilioChain addOrUpdateGlobalVariableGroupChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddOrUpdateGlobalVariableGroupCommand());
        return chain;
    }

    public static FacilioChain deleteGlobalVariableGroupChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new DeleteGlobalVariableGroupCommand());
        return chain;
    }

    public static FacilioChain addOrUpdateGlobalVariableChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddOrUpdateGlobalVariableCommand());
        return chain;
    }

    public static FacilioChain deleteGlobalVariableChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new DeleteGlobalVariableCommand());
        return chain;
    }

    public static Command getServiceRequestAfterUpdateChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddCommentForServiceRequestCommand());
        chain.addCommand(new FillActivityforServiceRequestCommand());
        return chain;
    }

    public static Command getServiceRequestBeforeUpdateChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FillOldServiceRequestCommand());
        return chain;
    }

    public static Command getFacilityBeforeSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SetLocalIdCommandV3());
        chain.addCommand(new ValidateFacilityCommand());
        return chain;
    }

    public static FacilioChain getAssociatedVendorAndValidationBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AssociateVendorToInsuranceCommandV3());
        c.addCommand(new ValidateDateCommandV3());
        return c;
    }

    public static FacilioChain getIRBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new ValidateInventoryRequestUpdateCommandV3());
        c.addCommand(new ValidateInventoryRequestCommandV3());
        return c;
    }

    public static FacilioChain getUpdateItemQuantityRollupChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateItemQuantityCommandV3());
        c.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                .addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE,
                        RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE))

        );
        c.addCommand(getUpdateItemTypeQuantityRollupChain());
        return c;
    }

    public static FacilioChain getUpdateItemTypeQuantityRollupChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ItemTypeQuantityRollupCommandV3());
        return c;
    }

    public static FacilioChain getAddOrUpdateToolStockTransactionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(SetTableNamesCommand.getForToolTranaction());
        c.addCommand(new AddOrUpdateToolStockTransactionsCommandV3());
        c.addCommand(getUpdatetoolQuantityRollupChainV3());
        return c;
    }

    public static FacilioChain getUpdatetoolQuantityRollupChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ToolQuantityRollUpCommandV3());
        c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE));
        c.addCommand(getUpdateToolTypeQuantityRollupChain());
        return c;
    }

    public static FacilioChain getUpdateToolTypeQuantityRollupChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ToolTypeQuantityRollupCommandV3());
        return c;
    }

    public static FacilioChain getUpdateTransferRequestIsStagedAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateNewLineItemsCommandV3());
        c.addCommand(new UpdateCurrentBalanceCommandV3());
        c.addCommand(new UpdateItemTransactionCommandV3());
        c.addCommand(new UpdateToolTransactionCommandV3());
        c.addCommand(TransactionChainFactoryV3.getUpdateTransferRequestIsCompletedAfterSaveChain());
        return c;
    }

    public static FacilioChain getUpdateTransferRequestIsCompletedAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateStatusOfShipmentCommandV3());
        c.addCommand(new UpdateCurrentBalanceAfterTransferCommandV3());
        c.addCommand(new UpdateItemTransactionAfterTransferCommandV3());
        c.addCommand(new UpdateToolTransactionAfterTransferCommandV3());

        return c;
    }

    public static FacilioChain getUpdateLineItemsAndShipmentIdAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLineItemsCommandV3());
        c.addCommand(new UpdateShipmentIdCommandV3());
        return c;
    }

    public static FacilioChain getAddAccessibleSpaceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddAccessibleSpacesCommand());
        return c;
    }

    public static FacilioChain getDeleteAccessibleSpaceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteAccessibleSpacesCommand());
        return c;
    }

    public static FacilioChain getFetchAccessibleSpaceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchAccessibleSpacesCommand());
        return c;
    }

    public static FacilioChain getIssueInventoryRequestChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateInventoryRequestCommandV3());
        c.addCommand(new LoadItemTransactionEntryInputCommandV3());
        c.addCommand(TransactionChainFactoryV3.getAddOrUpdateItemTransactionsChainV3());
        c.addCommand(new CopyToToolTransactionCommandV3());
        c.addCommand(TransactionChainFactoryV3.getAddOrUdpateToolTransactionsChainV3());

        return c;
    }

    public static FacilioChain getItemTransactionsAfterSaveChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(getItemTransactionRemainingQuantityRollupChainV3());
        c.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(new UpdateRequestedItemIssuedQuantityCommandV3());

        return c;
    }

    public static FacilioChain getAdjustmentItemTransactionsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        return c;
    }

    public static FacilioChain getToolTransactionsAfterSaveChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(getToolTransactionRemainingQuantityRollupChainV3());
        c.addCommand(getUpdatetoolQuantityRollupChainV3());
        c.addCommand(new UpdateRequestedToolIssuedQuantityCommandV3());
        return c;
    }

    public static FacilioChain getAddOrUpdateItemTransactionsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateManualItemTransactionCommandV3());
        c.addCommand(getItemTransactionRemainingQuantityRollupChainV3());
        c.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(new UpdateRequestedItemIssuedQuantityCommandV3());

        return c;
    }

    public static FacilioChain getItemTransactionRemainingQuantityRollupChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ItemTransactionRemainingQuantityRollupCommandV3());
        return c;
    }

    public static FacilioChain getAddOrUdpateToolTransactionsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateManualToolTransactionsCommandV3());
        c.addCommand(getToolTransactionRemainingQuantityRollupChainV3());
        c.addCommand(getUpdatetoolQuantityRollupChain());
        c.addCommand(new UpdateRequestedToolIssuedQuantityCommandV3());

        return c;
    }

    public static FacilioChain getToolTransactionRemainingQuantityRollupChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ToolTransactionRemainingQuantityRollupCommandV3());
        return c;
    }

    public static FacilioChain getTicketBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTicketLookupsCommand());
        c.addCommand(new SkipModuleCriteriaForUpcomingViewCommand());
        return c;
    }

    public static FacilioChain getTicketBeforeFetchForSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTicketLookupsCommand());
        c.addCommand(new SkipModuleCriteriaForSummaryCommand());
        return c;
    }

    public static FacilioChain getUpdatePoAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new POAfterCreateOrEditV3Command());
        c.addCommand(new CompletePoCommandV3());
        c.addCommand(new UpdateTransactionEventTypeV3Command());
        c.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                .addCommand(new ExecuteAllWorkflowsCommand(RuleType.TRANSACTION_RULE)));
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.PURCHASE_ORDER_ACTIVITY));
        return c;
    }

    public static FacilioChain getPurchaseOrderCompleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PurchaseOrderCompleteCommand());
        c.addCommand(getAddOrUpdateItemTypeVendorChain());
        c.addCommand(getAddOrUpdateToolTypeVendorChain());
        c.addCommand(getBulkAddToolChain());
        c.addCommand(getAddBulkItemChain());
        c.addCommand(new UpdateServiceVendorPriceCommand());
        return c;
    }

    public static FacilioChain getAddOrUpdateItemTypeVendorChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateItemTypeVendorCommandV3());
        return c;
    }

    public static FacilioChain getAddOrUpdateToolTypeVendorChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateToolVendorCommandV3());
        return c;
    }

    public static FacilioChain getAddBulkItemChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new BulkItemAdditionCommandV3());
        c.addCommand(getAddBulkPurchasedItemChain());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
        return c;
    }
    public static FacilioChain getSparePartBeforeCreateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExistingSparePartSelectionCommandV3());
        c.addCommand(new CheckForDuplicateSparePartCommandV3());
        c.addCommand(new CheckForRotatableItemCommand());
        return c;
    }

    public static FacilioChain getBulkAddToolChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new BulkToolAdditionCommandV3());
        c.addCommand(new AddBulkToolStockTransactionsCommand());
        c.addCommand(getUpdatetoolQuantityRollupChain());
        c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
        return c;
    }

    public static FacilioChain getAddBulkPurchasedItemChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(SetTableNamesCommand.getForPurchasedItem());
        c.addCommand(new AddPurchasedItemsForBulkItemAddCommandV3());
        c.addCommand(getAddOrUpdateItemStockTransactionChainV3());
        return c;
    }

    public static FacilioChain getSetItemAndToolTypeForStoreRoomChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetItemAndToolTypeForStoreRoomCommandV3());
        return c;
    }

    public static FacilioChain getCreateOrUpdateReportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructReportData());
        c.addCommand(new AddOrUpdateReportCommand());
        return c;
    }

    public static FacilioChain getreportShareChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReportSharePermission());
        return c;
    }

    public static FacilioChain getCreateOrUpdateAnalyticsReportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateReadingAnalyticsReportCommand());
        c.addCommand(new AddOrUpdateReportCommand());
        return c;
    }

    public static FacilioChain getCreateOrUpdatePivotReportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructTabularReportData());
        c.addCommand(ReadOnlyChainFactory.constructAndFetchTabularReportDataChain());
        c.addCommand(new AddOrUpdateReportCommand());
        return c;
    }

    public static FacilioChain getDeleteReportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructReportDeleteCommand());
        return c;
    }

    public static FacilioChain getReportContextChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructReportDetailsCommand());
        return c;
    }

    public static FacilioChain getDashboardDateFilterChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructReportDetailsCommand());
        return c;
    }

    public static FacilioChain getExecuteReportChain(String filters, boolean needCriteriaData) {
        FacilioChain c = getDefaultChain();
        if (filters != null) {
            c.addCommand(new GenerateCriteriaFromFilterCommand());
        }
        c.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
        c.addCommand(new GetModuleFromReportContextCommand());
        if (needCriteriaData) {
            c.addCommand(new GetCriteriaDataCommand());
        }
        return c;
    }

    public static FacilioChain getExecutePivotReportChain(String filters) {
        FacilioChain c = getDefaultChain();
        if (filters != null) {
            c.addCommand(new GenerateCriteriaFromFilterCommand());
        }
        c.addCommand(new ConstructTabularReportData());
        c.addCommand(ReadOnlyChainFactory.constructAndFetchTabularReportDataChain());
        c.addCommand(new PivotColumnFormatCommand());
        return c;
    }

    public static FacilioChain getReadingDataChain(long alarmId, String fields, boolean newFormat,
                                                   boolean isOnlyReportDataChain) {
        FacilioChain c = getDefaultChain();
        if (alarmId > 0 && fields == null) {
            c.addCommand(new GetDataPointFromAlarmCommand());
        }
        if (newFormat) {
            c.addCommand(new ConstructLiveFilterCommandToExport());
            if (!isOnlyReportDataChain) {
                c.addCommand(new CreateReadingAnalyticsReportCommand());
            }
            c.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
        } else {
            if (!isOnlyReportDataChain) {
                c.addCommand(new CreateReadingAnalyticsReportCommand());
            }
            c.addCommand(ReadOnlyChainFactory.fetchReportDataChain());
            if (!isOnlyReportDataChain) {
                c.addCommand(new FetchCustomBaselineData());
            }
        }
        return c;
    }

    public static FacilioChain getReadingAlarmDataChain(boolean newFormat) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetDataPointFromAlarmCommand());
        if (newFormat) {
            c.addCommand(new CreateReadingAnalyticsReportCommand());
            c.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
        } else {
            c.addCommand(new CreateReadingAnalyticsReportCommand());
            c.addCommand(ReadOnlyChainFactory.fetchReportDataChain());
            c.addCommand(new FetchCustomBaselineData());
        }
        return c;
    }

    public static FacilioChain getFoldersListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetReportFoldersCommand());
        return c;
    }

    public static FacilioChain addMLServiceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ForkChainToInstantJobCommand()
                // .addCommand(new ValidateMLServiceCommand())
                .addCommand(new ConstructMLModelDetails())
                .addCommand(new ConstructReadingForMLServiceCommand())
                .addCommand(new InitMLServiceCommand())
                .addCommand(new ActivateMLServiceCommand())
                .addCommand(new TriggerMLServiceJobCommand()));
        return c;
    }

    public static FacilioChain updateMLServiceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new InitMLServiceCommand())
                .addCommand(new TriggerMLServiceJobCommand()));
        return c;
    }

    public static FacilioChain getAllReportsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetAllReportsCommand());
        return c;
    }

    public static FacilioChain getReportFieldsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetReportFieldsCommand());
        return c;
    }

    public static FacilioChain getSubModulesListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetSubModulesListCommand());
        return c;
    }

    public static FacilioChain getCreateReportFolderChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetCreateFolderCommand());
        return c;
    }

    public static FacilioChain getMoveReportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetMoveReportCommand());
        return c;
    }

    public static FacilioChain getFolderPermissionUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetFolderPermissionUpdateCommand());
        return c;
    }

    public static FacilioChain getExportReportFileChain(boolean isAnalyticsReport, boolean isFilterNeeded) {
        FacilioChain c = getDefaultChain();
        if (isFilterNeeded) {
            c.addCommand(new ConstructLiveFilterCommandToExport());
        }
        c.addCommand(isAnalyticsReport ? ReadOnlyChainFactory.newFetchReadingReportChain()
                : ReadOnlyChainFactory.newFetchReportDataChain());
        c.addCommand(new GetExportReportFileCommand());
        return c;
    }

    public static FacilioChain getExportPivotReportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructTabularReportData());
        c.addCommand(ReadOnlyChainFactory.constructAndFetchTabularReportDataChain());
        c.addCommand(new ExportPivotReport());
        return c;
    }

    public static FacilioChain getScheduledReportChain(boolean isUpdate) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddTemplateCommand());
        if (isUpdate) {
            c.addCommand(new DeleteScheduledReportsCommand(true));
        }
        c.addCommand(new ScheduleV2ReportCommand());
        return c;
    }

    public static FacilioChain getAssetBeforeFetchChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AssetSupplementsSupplyCommand());
        chain.addCommand(new AddPlannerIdFilterCriteriaCommand());
        return chain;
    }

    public static FacilioChain getCreateAssetCategoryChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddAssetCategoryModuleCommandV3());
        chain.addCommand(TransactionChainFactory.commonAddModuleChain());
        chain.addCommand(new UpdateCategoryAssetModuleIdCommandV3());
        return chain;
    }

    public static FacilioChain getDeleteAssetCategoryChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateAssetCategoryDeletionV3());
        return chain;
    }

    public static FacilioChain getDeleteAssetDepartmentChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateAssetDepartmentDeletionV3());
        return chain;
    }

    public static FacilioChain getDeleteSpaceCategoryChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateSpaceCategoryDeletionV3());
        return chain;
    }

    public static FacilioChain getFaultImpactAddOrUpdateBeforeChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FaultImpactBeforeSaveCommand());
        return chain;
    }

    public static FacilioChain getFaultImpactAddOrUpdateAfterChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FaultImpactAfterSaveCommand());
        return chain;
    }

    public static FacilioChain getFaultImpactFetchChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FaultImpactAfterFetchCommand());
        return chain;
    }

    public static FacilioChain getBeforeFetchPOListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadPoPrListLookupCommandV3());
        chain.addCommand(new GetPurchaseOrdersListOnInventoryTypeIdCommandV3());
        return chain;
    }

    public static FacilioChain getReceiptsBeforeFetchListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadReceiptsListLookupCommandV3());
        chain.addCommand(new GetReceiptsListOnReceivableIdCommandV3());
        return chain;
    }

    public static FacilioChain getReportModuleListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetReportModuleListCommand());
        return chain;
    }

    public static FacilioChain getBulkAddToolChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddBulkToolStockTransactionsCommandV3());
        c.addCommand(getUpdatetoolQuantityRollupChainV3());
        c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
        return c;
    }

    public static FacilioChain getUpdatetoolQuantityRollupChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ToolQuantityRollUpCommandV3());
        c.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                .addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE, RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE))
        );
        c.addCommand(getUpdateToolTypeQuantityRollupChainV3());
        return c;
    }

    public static FacilioChain getUpdateToolTypeQuantityRollupChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ToolTypeQuantityRollupCommandV3());
        return c;
    }

    public static FacilioChain getUpdateIsUnderStockedChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateIsUnderStockedCommandV3());
        c.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                .addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE))
        );
        return c;
    }

    public static FacilioChain getAddItemChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddItemCommandV3());
        c.addCommand(getAddPurchasedItemChain());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
        return c;
    }

    public static FacilioChain getAddPurchasedItemChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetAddPurchasedItemCommandV3());
        c.addCommand(getAddOrUpdateItemStockTransactionChainV3());
        return c;
    }

    public static FacilioChain getAddOrUpdateItemStockTransactionChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(SetTableNamesCommand.getForItemTransactions());
        c.addCommand(new AddOrUpdateItemStockTransactionsCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        return c;
    }

    public static FacilioChain getCreateVendorQuotesChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateVendorQuotesCommandV3());
        return c;
    }

    public static FacilioChain getCreatePurchaseOrdersChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreatePurchaseOrdersCommandV3());
        return c;
    }

    public static FacilioChain getAwardVendorsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AwardVendorsCommandV3());
        return c;
    }

    public static FacilioChain getRequestForQuotationLineItemsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetRequestForQuotationLineItemsCommandV3());
//        c.addCommand(new AutoAwardingPriceCommandV3());
        return c;
    }

    public static FacilioChain getRfqBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        // c.addCommand(new CreateRfqFromPrCommandV3());
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new RfqBeforeCreateOrUpdateCommandV3());
        return c;
    }

    public static FacilioChain getConvertPrToRfqChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConvertPrToRfqCommandV3());
        return c;
    }

    public static FacilioChain getConvertRfqToPoChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConvertRfqToPoCommandV3());
        return c;
    }

    public static FacilioChain getRfqBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new RfqBeforeCreateOrUpdateCommandV3());
        c.addCommand(new SetRequestForQuotationBooleanFieldsCommandV3());
        return c;
    }

    public static FacilioChain getRfqAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateRequestForQuotationCommandV3());
        c.addCommand(new UpdateRequestForQuotationLineItemsCommandV3());
        return c;
    }

    public static FacilioChain getPurchaseOrderLineItemQuantityRecievedRollUpChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new PurchaseOrderLineItemQuantityRollUpCommandV3());
        return chain;
    }

    public static FacilioChain getPurchaseOrderQuantityRecievedRollUpChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new PurchaseOrderQuantityRecievedRollUpCommandV3());
        return chain;
    }

    public static FacilioChain getAddOrUpdateReceiptsChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddOrUpdateReceiptCommandV3());
        chain.addCommand(getPurchaseOrderLineItemQuantityRecievedRollUpChain());
        chain.addCommand(getPurchaseOrderQuantityRecievedRollUpChain());
        chain.addCommand(getPurchaseOrderAutoCompleteChainV3());
        return chain;
    }

    public static FacilioChain getPurchaseOrderAutoCompleteChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new PurchaseOrderAutoCompleteCommand());
        chain.addCommand(getAddOrUpdateItemTypeVendorChain());
        chain.addCommand(getAddOrUpdateToolTypeVendorChain());
        chain.addCommand(getBulkAddToolChain());
        chain.addCommand(getAddBulkItemChain());
        chain.addCommand(new UpdateServiceVendorPriceCommand());
        return chain;
    }

    public static FacilioChain getAddLicensingInfoChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddLicensingInfoCommand());
        return c;
    }


    public static FacilioChain getUpdateLicensingInfoChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateLicensingInfoCommand());
        return c;
    }


    public static FacilioChain getFetchLicensingInfoChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchLicensingInfoCommand());
        return c;
    }


    public static FacilioChain getDeleteLicensingInfoChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteLicensingInfoCommand());
        return c;
    }

    public static FacilioChain getCloneDashboardChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CloneDashboardCommand());
        return c;
    }
    public static FacilioChain getCloneReportChain(){
        FacilioChain c =getDefaultChain();
        c.addCommand(new GetReportCloneCommand());
        return c;
    }
    public static FacilioChain getMoveToDashboardChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new MoveToDashboardCommand());
        return c;
    }

    public static FacilioChain getReadingImportAppChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddReadingImportAppDataCommand());
        return c;
    }

    public static FacilioChain updateReadingImportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateReadingImportDataCommand());
        return c;
    }

    public static FacilioChain deleteReadingImportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteReadingImportDataCommand());
        return c;
    }
    public static FacilioChain getAutocadImportAppChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddAutoCadFileImportCommand());
        c.addCommand(new AddAutoCadLayerCommand());
        return c;

    }

    public static FacilioChain getCreateJobPlanAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddJobPlanSectionInputOptions());
        c.addCommand(new AddJobPlanTasksCommand());
        c.addCommand(new AddJobPlanTaskInputOptions());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.JOB_PLAN_ACTIVITY));
        return c;
    }

    public static FacilioChain addReadingDataMetaForReadingModule() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FetchReadingsModuleFieldsCommand());
        chain.addCommand(new InsertReadingDataMetaForNewResourceCommand());
        return chain;
    }

    public static FacilioChain getUpdateDashboardChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DuplicateDashboardForBuildingCommand());
        c.addCommand(new V3UpdateDashboardWithWidgets());
        c.addCommand(new EnableMobileDashboardCommand());

        return c;
    }

    public static FacilioChain getDashboardDataChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetDashboardDataCommand());
        return c;
    }

    public static FacilioChain getAddWidgetChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddWidgetCommandV3());
        return c;
    }

    public static FacilioChain getUpdateWidgetsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateWidgetCommandV3());
        return c;
    }

    public static FacilioChain getUpdateDashboardTabChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new V3UpdateDashboardTabWidgetCommand());
        return c;
    }

    public static FacilioChain getDeleteAssetTypeChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateAssetTypeDeletionV3());
        return chain;
    }

    public static FacilioChain getCREDDashboardRuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DashboardRuleCREDCommand());
        return c;
    }

    public static FacilioChain getAddColorPaletteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddColorPaletteCommand());
        return c;
    }

    public static FacilioChain getDeleteColorPaletteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteColorPaletteCommand());
        return c;
    }

    public static FacilioChain getListColorPaletteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ListColorPaletteCommand());
        return c;
    }

    public static FacilioChain getExecuteNow() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PMExecuteNowContextCommand());
        return c;
    }

    public static FacilioChain getPublishPM() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidatePublishCommand());
        c.addCommand(new MarkPMAsActiveCommand());
        c.addCommand(new PublishPMCommand());
        return c;
    }

    public static FacilioChain getDeactivatePM() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MarkPMAsDeactivatedCommand());
        return c;
    }

    public static FacilioChain getTriggerFrequencyListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetAvailableTriggerFrequencyForPM());
        return c;
    }

    public static FacilioChain addVisitsAndInvitesForms() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddOrUpdateVisitorTypeFormCommand());
        return chain;
    }

    public static FacilioChain getShiftBeforeSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateShiftsCommand());
        chain.addCommand(new MarkAsNonDefaultShiftCommand());
        return chain;
    }

    public static FacilioChain getShiftAfterSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddShiftAbsenceDetectionJobCommand());
        return chain;
    }

    public static FacilioChain getShiftBeforeDeleteChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateShiftsUsageCommand());
        return chain;
    }

    public static FacilioChain getShiftAfterDeleteChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new RemoveShiftAbsenceDetectionJobCommand());
        return chain;
    }

    public static FacilioChain getShiftBeforeUpdateChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateShiftsCommand());
        chain.addCommand(new MarkAsNonDefaultShiftCommand());
        return chain;
    }

    public static Command getShiftAfterUpdateChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new RemoveShiftAbsenceDetectionJobCommand());
        chain.addCommand(new AddShiftAbsenceDetectionJobCommand());
        return chain;
    }

    public static FacilioChain getListMyApps() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ListMyAppsCommandV3());
        return c;
    }

    public static FacilioChain getListMyAppsForUser() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ListMyAppsForUserCommandV3());
        return c;
    }

    public static FacilioChain setDefaultAppForUser() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetDefaultAppForUserCommandV3());
        return c;
    }

    public static FacilioChain getBreakAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddBreakShiftRelationshipCommand());
        return c;
    }

    public static FacilioChain getBreakBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateBreakCommand());
        return c;
    }

    public static FacilioChain getBreakBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateBreakCommand());
        return c;
    }

    public static FacilioChain getBreakAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RemoveBreakShiftRelationshipCommand());
        c.addCommand(new AddBreakShiftRelationshipCommand());
        return c;
    }

    public static FacilioChain getBreakBeforeDeleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RemoveBreakShiftRelationshipCommand());
        return c;
    }

    public static FacilioChain addReadingKpi() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new PrepareReadingKpiCreationCommand());
        chain.addCommand(getAddCategoryReadingChain());
        chain.addCommand(new SetFieldAndModuleIdCommand());
        return chain;
    }

    public static FacilioChain addReadingKpiNamespace() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SetParentIdForNamespaceCommand());
        chain.addCommand(addNamespaceAndFieldsChain());
        return chain;
    }

    public static FacilioChain getNameSpaceAndSupplements() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FetchMetricAndUnitCommand());
        chain.addCommand(new AddNamespaceInKpiListCommand());
        return chain;
    }

    public static FacilioChain updateReadingKpiWorkflowAndNamespace() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new PrepareReadingKpiForUpdateCommand());
        chain.addCommand(new UpdateNamespaceAndFieldsCommand());
        chain.addCommand(new UpdateWorkflowCommand());
        return chain;
    }


    public static FacilioChain getFetchFailureClassSupplements() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchResourceSupplements());
        c.addCommand(new FetchFailureClassSupplements());
        return c;
    }

    public static FacilioChain getBeforeFetchSafetyPlanChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExcludeAssociatedHazardPrecautions());
//        c.addCommand(new ExcludeAvailableWorkOrderHazards());
        return c;
    }

    public static FacilioChain getBeforeFetchPrecautionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExcludeAvailableWorkOrderHazardPrecautions());
        c.addCommand(new ExcludeAssociatedHazardPrecautions());
        return c;
    }

    public static FacilioChain getReserveItemsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReserveItemsCommandV3());
        c.addCommand(new CreateReservationCommandV3());
        return c;
    }

    public static FacilioChain getCostChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PlansCostCommandV3());
        return c;
    }
    public static FacilioChain getPlannedItemsUnSavedListChainV3(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new PlannedItemsUnSavedListCommandV3());
        c.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return c;
    }
    public static FacilioChain getPlannedToolsUnSavedListChainV3(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new PlannedToolsUnSavedListCommandV3());
        c.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return c;
    }

    public static FacilioChain getPlannedServicesUnSavedListChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PlannedServicesUnSavedListCommandV3());
        c.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return c;
    }
    public static FacilioChain getUnsavedWorkOrderItemsListChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new WorkOrderItemUnsavedListCommandV3());
        chain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return chain;
    }
    public static FacilioChain getUnsavedReservedWorkOrderItemsListChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new WorkOrderReservedItemsUnsavedListCommandV3());
        chain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return chain;
    }
    public static FacilioChain getUnsavedWorkOrderToolsListChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new WorkOrderToolsUnsavedListCommandV3());
        chain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return chain;
    }
    public static FacilioChain getUnsavedWorkOrderServiceListChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new WorkOrderServiceUnsavedListCommandV3());
        chain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return chain;
    }
    public static FacilioChain getUnsavedJobPlanItemsListChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new JobPlanItemsUnsavedListCommandV3());
        chain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return chain;
    }
    public static FacilioChain getUnsavedSparePartsSelectionChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SparePartsSelectionCommand());
        return chain;
    }
    public static FacilioChain getUnsavedJobPlanToolsListChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new JobPlanToolsUnsavedListCommandV3());
        chain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return chain;
    }
    public static FacilioChain getUnsavedJobPlanServicesListChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new JobPlanServicesUnsavedListCommandV3());
        chain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return chain;
    }
    //    public static FacilioChain getUnReserveItemsChainV3(){
//        FacilioChain c = getDefaultChain();
//        c.addCommand(new UnReserveItemsCommandV3());
//        return c;
//    }

    public static FacilioChain getWoPlannedItemsBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateWorkOrderPlannedItemsCommandV3());
        c.addCommand(new SetIsReservedCommandV3());
        c.addCommand(new SetWorkOrderPlannedItemsCommandV3());
        return c;
    }

    public static FacilioChain getAddOrUdpateWorkorderItemsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateTransactionEventTypeCommand());
        c.addCommand(getInventoryReservationChainV3());
        c.addCommand(new LoadWorkorderItemLookUpCommand());
        c.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(new ItemTransactionRemainingQuantityRollupCommandV3());
        c.addCommand(new AddActivitiesCommand());
        c.addCommand(new AddOrUpdateWorkorderCostCommandV3());
        c.addCommand(new UpdateWorkorderTotalCostCommandV3());
        return c;
    }

    public static FacilioChain getInventoryReservationChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateReservedQuantityCommandV3());
        c.addCommand(new UpdateReservationRecordCommandV3());
        return c;
    }

    public static FacilioChain getAddOrUdpateWorkorderToolsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateTransactionEventTypeCommand());
        c.addCommand(new LoadWorkorderToolLookupCommand());
        c.addCommand(getUpdatetoolQuantityRollupChain());
        c.addCommand(new AddActivitiesCommand());
        c.addCommand(new AddOrUpdateWorkorderCostCommandV3());
        c.addCommand(new UpdateWorkorderTotalCostCommandV3());
        return c;
    }

    public static FacilioChain getAddOrUdpateWorkorderServiceChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateTransactionEventTypeCommand());
        c.addCommand(new LoadWorkOrderServiceLookUpCommand());
        c.addCommand(new AddOrUpdateWorkorderCostCommandV3());
        c.addCommand(new UpdateWorkorderTotalCostCommandV3());
        return c;
    }

    public static FacilioChain getAfterDeleteWorkorderItemsChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SetDeleteWorkorderItemCommandV3());
        chain.addCommand(new ItemTransactionRemainingQuantityRollupCommandV3());
        chain.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        chain.addCommand(getUpdateItemQuantityRollupChain());
        chain.addCommand(new AddOrUpdateWorkorderCostCommandV3());
        chain.addCommand(new UpdateWorkorderTotalCostCommandV3());
        return chain;
    }

    public static FacilioChain getAfterDeleteWorkorderToolsChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SetDeleteWorkorderToolCommandV3());
        chain.addCommand(new ToolTransactionRemainingQuantityRollupCommandV3());
        chain.addCommand(getUpdatetoolQuantityRollupChain());
        chain.addCommand(new AddOrUpdateWorkorderCostCommandV3());
        chain.addCommand(new UpdateWorkorderTotalCostCommandV3());
        return chain;
    }

    public static FacilioChain getAfterDeleteWorkorderServicesChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SetDeleteWorkorderServiceCommandV3());
        chain.addCommand(new AddOrUpdateWorkorderCostCommandV3());
        chain.addCommand(new UpdateWorkorderTotalCostCommandV3());
        return chain;
    }
    public static FacilioChain getReserveValidationChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReservationValidationCommandV3());
        return c;
    }

    public static FacilioChain getReserveChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReservationSummaryCommandV3());
        return c;
    }

    public static FacilioChain getPeopleGroupAndMembersChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPeopleGroupMembersCommand());

        return c;
    }

    public static FacilioChain getDashboardRuleExecuteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecutedDashboardRuleCommand());
        return c;
    }

    public static FacilioChain getDashboardWidgetsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetDashboardWidgetsCommand());
        return c;
    }

    public static FacilioChain addOrUpdateScopeVariable() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateScopeVariable());
        c.addCommand(new FetchScopeVariableCommand());
        return c;
    }

    public static FacilioChain fetchActivePeopleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchInviteAcceptedUsersCommand());
        return c;
    }

    public static FacilioChain createSpaceBookingChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchChangeSetForCustomActivityCommand());
        c.addCommand(new setSpaceBookingVariableCommand());
        c.addCommand(new V3ValidateSpaceBookingAvailability());
        c.addCommand(new V3ValidateSpaceBookingCommand());
        c.addCommand(new FetchPolicyCommand());
        c.addCommand(new ValidatePolicyCommand());
        return c;
    }

    public static FacilioChain getShiftPlannerListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ListShiftPlannerCommand());
        return c;
    }

    public static FacilioChain getShiftPlannerUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateShiftPlannerCommand());
        return c;
    }

    public static Command getTicketAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddTicketLookups());
        return c;
    }

    public static FacilioChain setSwitchStatus() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetSwitchVariableStatusCommand());
        return c;
    }

    public static FacilioChain setGlobalScopeVariableStatus() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetGlobalScopeVariableStatusCommand());
        return c;
    }

    public static FacilioChain deleteGlobalScopeVariable() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteGlobalScopeVariableCommand());
        return c;
    }

    public static FacilioChain addReadingRuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReadingRuleDependenciesCommand());
        c.addCommand(addRuleReadingsModuleChain());
        c.addCommand(new AddNewReadingRuleCommand());
        return c;
    }

    public static FacilioChain addRuleReadingsModuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddRuleReadingsModuleCommand());
        c.addCommand(getAddCategoryReadingChain());
        return c;
    }

    public static FacilioChain afterSaveReadingRuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddAlarmDetailsCommand());
        c.addCommand(new SetParentIdForNamespaceCommand());
        c.addCommand(addNamespaceAndFieldsChain());
        c.addCommand(addRCARuleChain());
        c.addCommand(new AddFaultImpactRelationCommand());
        return c;
    }

    public static FacilioChain addNamespaceAndFieldsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddWorkflowCommand());
        c.addCommand(new AddNamespaceCommand());
        c.addCommand(new AddNamespaceFieldsCommand());
        return c;
    }

    public static FacilioChain addRCARuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddRuleRCACommand());
        c.addCommand(new AddRCAMappingCommand());
        c.addCommand(new AddRCAGroupCommand());
        c.addCommand(new AddRCAScoreConditionCommand());
        return c;
    }

    private static FacilioChain updateReadingRuleRcaChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateRuleRCACommand());
        c.addCommand(new UpdateRCAMappingCommand());
        c.addCommand(new UpdateRCAGroupCommand());
        c.addCommand(new UpdateRCAScoreConditionCommand());
        return c;
    }

    public static FacilioChain fetchRCARuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchRuleRCACommand());
        c.addCommand(new FetchRCAMappingCommand());
        c.addCommand(new FetchRCAGroupCommand());
        c.addCommand(new FetchRCAScoreConditionCommand());
        return c;
    }

    public static FacilioChain beforeFetchReadingRuleSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadSupplementsForNewReadingRule());
        return c;
    }

    public static FacilioChain fetchReadingRuleSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchReadingRuleSummaryCommand());
        c.addCommand(fetchRCARuleChain());
        return c;
    }

    public static FacilioChain updateReadingRuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PrepareReadingRuleForUpdateCommand());
        c.addCommand(addOrDeleteFaultImpactChain());
        c.addCommand(new UpdateReadingRuleCommand());
        c.addCommand(new UpdateWorkflowCommand());
        c.addCommand(updateReadingRuleRcaChain());
        return c;
    }

    public static FacilioChain afterDeleteReadingRuleRcaChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteNamespaceReadingRuleCommand());
        c.addCommand(new DeleteReadingRuleRcaCommand());
        return c;
    }

    public static FacilioChain getTagAssetASRotatingChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddRotatingItemToolCommandV3());
        return c;
    }

    public static FacilioChain getAddPolicyChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddPolicyCriteriaCommand());
        return c;
    }
    public static FacilioChain addOrUpdateFormRelationChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateFormRelationCommand());
        return c;
    }

    public static FacilioChain moveWoInQueueForPreOpenToOpenChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPMV2WorkordersToMoveInQueueForPreOpenToOpen());
        c.addCommand(new ScheduleWorkordersToMoveInQueueForPreOpenToOpen());
        return c;
    }
    
    public static FacilioChain getPMPlannerBeforeUpdateCommand(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateTriggerTypeForPMPlannerCommand());
        c.addCommand(new BeforeSavePMPlannerCommand());
        return c;
    }
    public static FacilioChain BaseSchedulerSingleInstanceJobChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new BaseSchedulerSingleInstanceCommand());
        return c;
    }
    
    public static FacilioChain PMV2NightlyJobChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateAndGetAllPMPlannersCommand());
        c.addCommand(new RunExecuterBaseForPMPlannersCommand());
        return c;
    }
    
    public static FacilioChain PMV2BeforeUpdateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new PMBeforeCreateCommand());
        c.addCommand(new AddPMDetailsBeforeUpdateCommand());
        c.addCommand(new UpdateResourcePlannerOnPMSitesUpdateCommand());
        return c;
    }
}
