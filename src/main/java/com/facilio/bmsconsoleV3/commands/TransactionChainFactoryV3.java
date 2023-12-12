package com.facilio.bmsconsoleV3.commands;

import static com.facilio.bmsconsole.commands.TransactionChainFactory.addOrDeleteFaultImpactChain;
import static com.facilio.bmsconsole.commands.TransactionChainFactory.getAddCategoryReadingChain;

import java.util.Collections;

import com.facilio.alarms.sensor.commands.*;
import com.facilio.bmsconsole.activity.InActivateControlActionTemplateCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsoleV3.commands.Calendar.*;
import com.facilio.bmsconsoleV3.commands.asset.*;
import com.facilio.bmsconsoleV3.commands.attendance.*;
import com.facilio.bmsconsoleV3.commands.client.GetOldClienContactRecordMap;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.*;
import com.facilio.bmsconsoleV3.commands.controlActions.*;
import com.facilio.bmsconsoleV3.commands.dashboard.*;
import com.facilio.bmsconsoleV3.commands.decommission.*;
import com.facilio.bmsconsoleV3.commands.employee.GetOldEmployeeRecordMap;
import com.facilio.bmsconsoleV3.commands.invoice.*;
import com.facilio.bmsconsoleV3.commands.item.*;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.lineitems.LoadExtraFieldsCommandV3;
import com.facilio.bmsconsoleV3.commands.item.FilterItemTransactionsCommandV3;
import com.facilio.bmsconsoleV3.commands.item.IncludeServingSiteFilterCommandV3;
import com.facilio.bmsconsoleV3.commands.item.LoadItemLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.itemtypes.LoadItemTypesExtraFields;
import com.facilio.bmsconsoleV3.commands.itemtypes.LoadItemTypesLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.jobPlanInventory.*;
import com.facilio.bmsconsoleV3.commands.jobplan.*;
import com.facilio.bmsconsoleV3.commands.meter.*;
import com.facilio.bmsconsoleV3.commands.people.*;
import com.facilio.bmsconsoleV3.commands.pivot.GetPivotModulesListCommand;
import com.facilio.bmsconsoleV3.commands.plannedmaintenance.*;
import com.facilio.bmsconsoleV3.commands.purchaseorder.*;
import com.facilio.bmsconsoleV3.commands.purchaserequest.*;
import com.facilio.bmsconsoleV3.commands.quotation.*;
import com.facilio.bmsconsoleV3.commands.receivable.LoadReceivableLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.receivable.LoadReceivablesExtraFields;
import com.facilio.bmsconsoleV3.commands.servicerequest.*;
import com.facilio.bmsconsoleV3.commands.shift.*;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.*;
import com.facilio.bmsconsoleV3.commands.reports.*;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.*;
import com.facilio.bmsconsoleV3.commands.site.FetchSiteWithoutScoping;
import com.facilio.bmsconsoleV3.commands.storeroom.SetLocationObjectFromSiteV3;
import com.facilio.bmsconsoleV3.commands.storeroom.UpdateServingSitesinStoreRoomCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.GetOldTenantContactRecordMap;
import com.facilio.bmsconsoleV3.commands.termsandconditions.LoadTermsAndConditionsExtraFieldsCommandV3;
import com.facilio.bmsconsoleV3.commands.termsandconditions.LoadTermsLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.tool.*;
import com.facilio.bmsconsoleV3.commands.tooltypes.LoadToolTypesExtraFields;
import com.facilio.bmsconsoleV3.commands.tooltypes.LoadToolTypesLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.userScoping.*;
import com.facilio.bmsconsoleV3.commands.utilityType.*;
import com.facilio.bmsconsoleV3.commands.vendor.LoadVendorLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.LoadVendorsExtraFieldsCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorQuotes.CheckVendorPortalAccessibilityCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorQuotes.LoadVendorQuotesExtraFieldsCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorQuotes.LoadVendorQuotesLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorQuotes.SetVendorQuotesLineItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorcontact.GetOldVendorContactRecordMap;
import com.facilio.bmsconsoleV3.commands.visitorlog.*;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.*;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.*;
import com.facilio.bmsconsoleV3.commands.workorder.*;
import com.facilio.bmsconsoleV3.commands.workorder.multi_import.AddWorkOrderCommandV3Import;
import com.facilio.bmsconsoleV3.commands.workpermit.*;
import com.facilio.bmsconsoleV3.context.LoadMultiResourceExtraFieldsCommandV3;
import com.facilio.bmsconsoleV3.context.spacebooking.*;
import com.facilio.bmsconsoleV3.plannedmaintenance.ValidatePmResourcePlannerResource;
import com.facilio.assetcatergoryfeature.commands.FetchAssetCategoryLevelStatusCommand;
import com.facilio.assetcatergoryfeature.commands.UpdateAssetCategoryLevelStatusCommand;
import com.facilio.faults.LoadOccurrenceForAlarmCommand;
import com.facilio.faults.newreadingalarm.HandleV3AlarmListLookupCommand;
import com.facilio.connected.commands.AddExecutorCommand;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.ns.command.*;
import com.facilio.plannedmaintenance.FetchPlannerDetails;
import com.facilio.plannedmaintenance.PreCreateWorkOrderRecord;
import com.facilio.permission.commands.AddOrUpdatePermissionSet;
import com.facilio.permission.commands.DeletePermissionSetCommand;
import com.facilio.permission.commands.FetchPermissionSetCommand;
import com.facilio.permission.commands.UpdatePermissionsForPermissionSetCommand;

import com.facilio.readingkpi.commands.create.SetFieldAndModuleCommand;
import com.facilio.readingrule.command.*;

import com.facilio.utility.commands.*;

import com.facilio.trigger.command.*;
import com.facilio.v3.commands.*;
import com.facilio.remotemonitoring.commands.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsole.actions.GetModuleFromReportContextCommand;
import com.facilio.bmsconsole.actions.PurchaseOrderCompleteCommand;
import com.facilio.bmsconsole.automation.command.AddOrUpdateGlobalVariableCommand;
import com.facilio.bmsconsole.automation.command.AddOrUpdateGlobalVariableGroupCommand;
import com.facilio.bmsconsole.automation.command.DeleteGlobalVariableCommand;
import com.facilio.bmsconsole.automation.command.DeleteGlobalVariableGroupCommand;
import com.facilio.bmsconsole.commands.util.AddColorPaletteCommand;
import com.facilio.bmsconsole.commands.util.DeleteColorPaletteCommand;
import com.facilio.bmsconsole.commands.util.ListColorPaletteCommand;
import com.facilio.bmsconsole.commands.util.ReportSharePermission;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
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
import com.facilio.bmsconsoleV3.commands.licensinginfo.AddLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.DeleteLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.FetchLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.UpdateLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.peoplegroup.FetchPeopleGroupMembersCommand;
import com.facilio.bmsconsoleV3.commands.readingimportapp.AddReadingImportAppDataCommand;
import com.facilio.bmsconsoleV3.commands.readingimportapp.DeleteReadingImportDataCommand;
import com.facilio.bmsconsoleV3.commands.readingimportapp.UpdateReadingImportDataCommand;
import com.facilio.bmsconsoleV3.commands.receipts.AddOrUpdateReceiptCommandV3;
import com.facilio.bmsconsoleV3.commands.receipts.GetReceiptsListOnReceivableIdCommandV3;
import com.facilio.bmsconsoleV3.commands.receipts.LoadReceiptsListLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.receipts.PurchaseOrderLineItemQuantityRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.receipts.PurchaseOrderQuantityRecievedRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.safetyplan.AddWorkorderHazardPrecautionsFromSafetyPlanCommandV3;
import com.facilio.bmsconsoleV3.commands.safetyplan.ExcludeAssociatedHazardPrecautions;
import com.facilio.bmsconsoleV3.commands.safetyplan.ExcludeAvailableWorkOrderHazardPrecautions;
import com.facilio.bmsconsoleV3.commands.shift.ListShiftPlannerCommand;
import com.facilio.bmsconsoleV3.commands.shift.MarkAsNonDefaultShiftCommand;
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
import com.facilio.bmsconsoleV3.commands.termsandconditions.ReviseTandCCommand;
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
import com.facilio.bmsconsoleV3.commands.visitor.AddOrUpdateLocationForVisitorCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.CheckForVisitorDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.AddNdaForVisitorLogCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.AddNewVisitorWhileLoggingCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.AddOrUpdateVisitorFromVisitsCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.ChangeVisitorInviteStateCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.CheckForWatchListRecordCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.GenerateQrInviteUrlCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.PutOldVisitRecordsInContextCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.UpdateVisitorInviteRelArrivedStateCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.VisitorFaceRecognitionCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.CreateReservationCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.CreateWorkOrderPlannedInventoryCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.PlansCostCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.ReservationSummaryCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory.ReservationValidationCommandV3;
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
import com.facilio.readingkpi.commands.list.AddNamespaceInKpiListCommand;
import com.facilio.readingkpi.commands.list.FetchMetricAndUnitCommand;
import com.facilio.readingkpi.commands.update.PrepareReadingKpiForUpdateCommand;
import com.facilio.readingkpi.commands.update.UpdateNamespaceAndFieldsCommand;
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
import com.facilio.workflows.command.AddWorkflowCommand;
import com.facilio.workflows.command.UpdateWorkflowCommand;
import org.apache.commons.lang3.BooleanUtils;

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

        c.addCommand(new ValidationForWorkPermitDateCommandV3());

        return c;
    }

    public static FacilioChain getWorkPermitAfterSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new InsertWorkPermitActivitiesCommand());
        c.addCommand(new LoadWorkPermitLookUpsCommandV3());
        c.addCommand(new RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3());

        return c;
    }

    public static FacilioChain getWorkPermitBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadWorkPermitLookUpsCommandV3());
        c.addCommand(new LoadWorkPermitExtraFieldsCommandV3());
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
        c.addCommand(new ValidateInviteCommandV3());

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
        c.addCommand(new ValidateInviteCommandV3());

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

    public static FacilioChain getBaseSpaceChildrenChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetSiteChildrenCommand());
        return chain;
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
        c.addCommand(new HandleQuoteSettingCommand());
        c.addCommand(new QuotationValidationAndCostCalculationCommand());
        return c;
    }

    public static FacilioChain getQuotationBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new HandleQuoteSettingCommand());
        c.addCommand(new QuotationValidationAndCostCalculationCommand());
        return c;
    }


    public static FacilioChain getInvoiceBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReviseInvoiceCommand());
        c.addCommand(new HandleInvoiceSettingCommand());
        c.addCommand(new InvoiceValidationAndCostCalculationCommand());
        return c;
    }

    public static FacilioChain getInvoiceBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new HandleInvoiceSettingCommand());
        c.addCommand(new InvoiceValidationAndCostCalculationCommand());
        return c;
    }
    public static FacilioChain getInvoiceAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateInvoiceParentIdCommand());
        c.addCommand(new InsertInvoiceLineItemsAndActivitiesCommand());
        c.addCommand(new UpdateInvoiceTermsAndConditionCommand());
        c.addCommand(new AddInvoiceGroupCommand());
        return c;
    }

    public static FacilioChain getInvoiceAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new InsertInvoiceLineItemsAndActivitiesCommand());
        c.addCommand(new UpdateInvoiceStatusFieldsCommand());
        return c;
    }

    public static FacilioChain getTermsBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new ReviseTandCCommand());
        return c;
    }

    public static FacilioChain getTermsAndConditionsBeforeFetchChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadTermsLookupCommandV3());
        chain.addCommand(new LoadTermsAndConditionsExtraFieldsCommandV3());
        return chain;
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
        c.addCommand(new TrimWorkOrderDescriptionCommandV3());
        c.addCommand(new ValidateJobPlanAssociationCommand());
        return c;
    }

    public static FacilioChain getWorkOrderBeforeCreateImportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddWorkOrderCommandV3Import());
        c.addCommand(new SetLocalIdCommandV3());
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
        // c.addCommand(new V3AddActionForTaskCommand());
        c.addCommand(new AddTaskSectionsV3());
        c.addCommand(new AddTasksCommandV3());
        c.addCommand(new AddTaskOptions());
        c.addCommand(getPlannedInventoryChainV3());
        return c;
    }

    public static FacilioChain getWorkOrderAfterSavePostCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new FetchWorkorderRecordAndValidateForPostCreate());
        c.addCommand(new FillContextAfterAddingWorkOrderPostCreateChainCommandV3());
        c.addCommand(new GetRecordIdsFromRecordMapCommandV3());
        c.addCommand(new AddPrerequisiteApproversCommandV3());
        c.addCommand(new UpdateJobStatusForWorkOrderCommand());
        c.addCommand(new AddOrUpdateMultiResourceForWorkorderCommandV3());
        c.addCommand(new AddActivityForWoPostCreateCommand());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.WORKORDER_ACTIVITY));
        return c;
    }

    public static FacilioChain getWorkOrderWorkflowsChainV3(boolean sendNotification) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_CUSTOM_CHANGE));
        c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SURVEY_ACTION_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_RULE,
                WorkflowRuleContext.RuleType.CHILD_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_APPROVAL_RULE,
                WorkflowRuleContext.RuleType.REQUEST_REJECT_RULE));

        if (sendNotification) {
            c.addCommand(
                    new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE,
                            WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE));
        }
        return c;
    }

    public static FacilioChain getWorkorderAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateTasksCommandV3());
        c.addCommand(new FillTasksAndPrerequisitesCommand());
        // c.addCommand(new V3AddActionForTaskCommand());
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
        // planned inventory
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
        c.addCommand(new AddFailureClassFromResource());
        c.addCommand(new TrimWorkOrderDescriptionCommandV3());
        c.addCommand(new ValidateJobPlanAssociationCommand());
        return c;
    }

    public static FacilioChain getWorkorderAfterUpdateChain(boolean sendNotification) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillContextAfterWorkorderUpdateCommandV3());
        c.addCommand(new SendNotificationCommandV3());
        // c.addCommand(new
        // ExecuteAllWorkflowsCommand(RuleType.SATISFACTION_SURVEY_RULE));
        // c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SURVEY_ACTION_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_RULE,
                WorkflowRuleContext.RuleType.CHILD_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_APPROVAL_RULE,
                WorkflowRuleContext.RuleType.REQUEST_REJECT_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE,
                WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE));
        c.addCommand(new V3ExecuteTaskFailureActionCommand());
        c.addCommand(new ConstructTicketNotesCommand());
        c.addCommand(TransactionChainFactory.getAddNotesChain());
        c.addCommand(new AddOrUpdateMultiResourceForWorkorderCommandV3());
        c.addCommand(new WorkOrderNotesDestructureCommand());
        c.addCommand(new AddActivitiesCommand());
        c.addCommand(new AddWorkorderHazardsFromSafetyPlanCommandV3());
        c.addCommand(new AddWorkorderHazardPrecautionsFromSafetyPlanCommandV3());
        c.addCommand(new UpdateAttachmentCountCommand());
        c.addCommand(new UpdateCloseAllFromBulkActionCommandV3());
        c.addCommand(new FlaggedEventWorkorderCloseCommand());
        return c;
    }

    public static FacilioChain getTenantContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new CheckForMandatoryTenantIdCommandV3());
        return c;
    }

    public static FacilioChain getTenantContactBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new GetOldTenantContactRecordMap());
        return c;
    }

    public static FacilioChain getTenantContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new AddOrUpdatePortalUserCommandV3());
        c.addCommand(new AddOrUpdateScopingAndPermissionCommandV3());
        return c;
    }

    public static FacilioChain getVendorContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new CheckForMandatoryVendorIdCommandV3());
        return c;
    }

    public static FacilioChain getVendorContactBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new GetOldVendorContactRecordMap());
        return c;
    }

    public static FacilioChain getVendorContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new AddOrUpdatePortalUserCommandV3());
        c.addCommand(new AddOrUpdateScopingAndPermissionCommandV3());
        c.addCommand(new AssignDefaultShift());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        return c;
    }

    public static FacilioChain getEmployeeBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new AddPeopleTypeForEmployeeCommandV3());
        return c;
    }

    public static FacilioChain getEmployeeBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new GetOldEmployeeRecordMap());
        return c;
    }

    public static FacilioChain getEmployeeAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdatePortalUserCommandV3());
        c.addCommand(new AssignDefaultShift());
        c.addCommand(new AddOrUpdateScopingAndPermissionCommandV3());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        return c;
    }

    public static FacilioChain getPeopleBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetPeopleTypeCommand());
        c.addCommand(new PeopleValidationCommandV3());
        return c;
    }

    public static FacilioChain getPeopleAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PeopleAfterSaveCommand());
        c.addCommand(new UserUpdateCommand());
        return c;
    }

    public static FacilioChain getClientContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new CheckForMandatoryClientIdCommandV3());
        return c;
    }

    public static FacilioChain getClientContactBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PeopleValidationCommandV3());
        c.addCommand(new GetOldClienContactRecordMap());
        return c;
    }

    public static FacilioChain getClientContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new AddOrUpdatePortalUserCommandV3());
        c.addCommand(new AddOrUpdateScopingAndPermissionCommandV3());
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
        c.addCommand(new SetAppIdForServiceRequestCommandV3());
        c.addCommand(new setSourceTypeCommandV3());
        return c;
    }

    public static FacilioChain getServiceRequestAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        // c.addCommand(new UpdateAttachmentsParentIdCommandV3());
        c.addCommand(new AddActivityForServiceRequestCommandV3());
        c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
        return c;
    }

    public static FacilioChain getCommentSharingOptionsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetCommentSharingOptionsCommand());
        return c;
    }

    public static FacilioChain getCommentSharingPreferencesUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateCommentPreferencesChain());
        return c;
    }

    public static FacilioChain getClearCommentSharingPreferencesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new deleteCommentPreferencesChain());
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
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        return c;
    }

    public static FacilioChain getInitTriggerAddOrUpdateChain(){
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new InitTriggerAddOrUpdateCommand());
        return chain;
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

    public static FacilioChain getTriggerListChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetAllTriggerCommand());
        return c;
    }
    public static FacilioChain getTriggerDeleteChainV2(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new InitDeleteTriggerCommand());
        return chain;
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
        // chain.addCommand(new ValidateRelationParamCommand());
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
        // added this command to prefill/remove properties in JobPlanSection's
        // additionInfo object
        chain.addCommand(new FillUpJobPlanSectionAdditionInfoObject());
        chain.addCommand(new ValidationForJobPlanCategory());
        return chain;
    }

    public static FacilioChain getUpdateJobPlanAfterSaveChain() {
        FacilioChain chain = getDefaultChain();
        // added this command to prefill/remove properties in JobPlanTask's additionInfo
        // object
        chain.addCommand(new FillUpJobPlanTaskAdditionInfoObject());
        chain.addCommand(new AddJobPlanSectionInputOptions());
        chain.addCommand(new AddJobPlanTasksCommand());
        chain.addCommand(new AddJobPlanTaskInputOptions());
        // chain.addCommand(new AddJobPlanPMsInContextCommand());
        chain.addCommand(new AddJobPlanGroupCommand());
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
        c.addCommand(new SetInventoryRequestBeforeSaveCommandV3());
        c.addCommand(new ValidateInventoryRequestCommandV3());
        return c;
    }

    public static FacilioChain getIRBeforeFetchChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadIRLookupCommandV3());
        chain.addCommand(new LoadInventoryRequestExtraFieldsCommandV3());
        return chain;
    }

    public static FacilioChain getIRAfterSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SetStoreRoomAndReservationTypeForInventoryRequestLineItems());
        return chain;
    }

    public static FacilioChain getLineItemsBeforeFetchChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadExtraFieldsCommandV3());
        return chain;
    }

    public static FacilioChain getInventoryRequestLineItemsAfterUpdateChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new UpdateInventoryRequestLineItemsForReservation());
        chain.addCommand(new UpdateInventoryRequestReservationStatusOnLineItemUpdateCommandV3());
        return chain;
    }

    public static FacilioChain getUpdateItemQuantityRollupChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateItemQuantityCommandV3());
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

    public static FacilioChain getBeforeUpdateInventoryRequestLineItemsChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateInventoryRequestLineItemForReservation());
        chain.addCommand(new SetReservedForInventoryRequestLineItems());
        chain.addCommand(new SetInventoryRequestLineItemsReservationTypeCommandV3());
        return chain;
    }

    public static FacilioChain getAfterFetchInventoryRequestLineItemsChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetReservedInventoryRequestLineItems());
        return chain;
    }

    public static FacilioChain getIssueInventoryRequestChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateInventoryRequestCommandV3());
        c.addCommand(new LoadItemTransactionEntryInputCommandV3());
        c.addCommand(TransactionChainFactoryV3.getAddOrUpdateItemTransactionsChainV3());
        c.addCommand(new CopyToToolTransactionCommandV3());
        c.addCommand(TransactionChainFactoryV3.getAddOrUpdateToolTransactionsChainV3());

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
        c.addCommand(new ComputeWeightedAverageCostCommand());
        c.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        return c;
    }

    public static FacilioChain getAdjustmentToolTransactionsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PurchasedToolsQuantityRollupCommandV3());
        c.addCommand(getUpdatetoolQuantityRollupChainV3());
        return c;
    }

    public static FacilioChain getToolTransactionsAfterSaveChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(getToolTransactionRemainingQuantityRollupChainV3());
        c.addCommand(new PurchasedToolsQuantityRollupCommandV3());
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

    public static FacilioChain getAddOrUpdateToolTransactionsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateManualToolTransactionsCommandV3());
        c.addCommand(getToolTransactionRemainingQuantityRollupChainV3());
        c.addCommand((new PurchasedToolsQuantityRollupCommandV3()));
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
        c.addCommand(new LoadWorkOrderExtraFieldsCommand());
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
        c.addCommand(new ComputeWeightedAverageCostCommand());
        c.addCommand(getAddBulkPurchasedItemChain());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
        return c;
    }

    public static FacilioChain getSparePartBeforeCreateChain() {
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

    public static FacilioChain getAddOrUpdateModuleReportChain(){
        FacilioChain c = getDefaultChain();
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

    public static FacilioChain getFoldersListChainNew() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetReportFoldersNewCommand());
        return c;
    }

    public static FacilioChain getReportsListViewChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetReportListViewCommand());
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
        chain.addCommand(new AddAssetRequiredFieldsCommand());
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
        chain.addCommand(new LoadPurchaseOrderExtraFields());
        return chain;
    }

    public static FacilioChain getBeforeFetchPRListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadPoPrListLookupCommandV3());
        chain.addCommand(new LoadPurchaseRequestExtraFields());
        return chain;
    }

    public static FacilioChain getBeforeFetchItemTypesListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadItemTypesLookUpCommandV3());
        chain.addCommand(new LoadItemTypesExtraFields());
        return chain;
    }

    public static FacilioChain getBeforeFetchJobPlanItemsListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadJobPlanItemsLookupCommandV3());
        c.addCommand(new LoadJobPlanItemsExtraFieldsCommandV3());
        return c;
    }

    public static FacilioChain getBeforeFetchJobPlanToolsListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadJobPlanToolsLookupCommandV3());
        c.addCommand(new LoadJobPlanToolsExtraFieldsCommandV3());
        return c;
    }

    public static FacilioChain getBeforeFetchJobPlanServicesListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadJobPlanServicesCommandV3());
        c.addCommand(new LoadJobPlanServicesExtraFieldsCommandV3());
        return c;
    }

    public static FacilioChain getBeforeFetchVendorsListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadVendorLookupCommandV3());
        chain.addCommand(new LoadVendorsExtraFieldsCommandV3());
        return chain;
    }

    public static FacilioChain getBeforeFetchToolTypesListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadToolTypesLookUpCommandV3());
        chain.addCommand(new LoadToolTypesExtraFields());
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
        c.addCommand(new addPurchasedToolCommandV3());
        c.addCommand(new AddBulkToolStockTransactionsCommandV3());
        c.addCommand(getUpdatetoolQuantityRollupChainV3());
        c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
        return c;
    }

    public static FacilioChain getUpdatetoolQuantityRollupChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ToolQuantityRollUpCommandV3());
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
                .addCommand(
                        new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE)));
        return c;
    }

    public static FacilioChain getAddItemChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetItemsCommand());
        c.addCommand(new ComputeWeightedAverageCostCommand());
        c.addCommand(new AddItemCommandV3());
        c.addCommand(getAddPurchasedItemChain());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
        return c;
    }

    public static FacilioChain getBeforeFetchItemListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new IncludeServingSiteFilterCommandV3());
        chain.addCommand(new LoadItemLookUpCommandV3());
        return chain;
    }

    public static FacilioChain getBeforeFetchItemTransactionsChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FilterItemTransactionsCommandV3());
        chain.addCommand(new LoadItemTransactionsLookupCommandV3());
        return chain;
    }

    public static FacilioChain getBeforeFetchToolTransactionsChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FilterItemTransactionsCommandV3());
        chain.addCommand(new LoadToolTransactionsLookupCommandV3());
        return chain;
    }

    public static FacilioChain getBeforeFetchToolListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new IncludeServingSiteFilterCommandV3());
        chain.addCommand(new LoadToolLookupCommandV3());
        return chain;
    }

    public static FacilioChain getAddPurchasedItemChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetAddPurchasedItemCommandV3());
        c.addCommand(getAddOrUpdateItemStockTransactionChainV3());
        return c;
    }

    public static FacilioChain getAddOrUpdateItemStockTransactionChainV3() {
        FacilioChain c = getDefaultChain();
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
        c.addCommand(new UpdateAwardedStatusCommandV3());
        return c;
    }

    public static FacilioChain getRequestForQuotationLineItemsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetRequestForQuotationLineItemsCommandV3());
        // c.addCommand(new AutoAwardingPriceCommandV3());
        return c;
    }

    public static FacilioChain getRequestForQuotationBeforeFetchChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadRequestForQuotationExtraFieldsCommandV3());
        chain.addCommand(new LoadRequestForQuotationLookupCommandV3());
        return chain;
    }

    public static FacilioChain getRfqBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        // c.addCommand(new CreateRfqFromPrCommandV3());
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new RfqBeforeCreateOrUpdateCommandV3());
        return c;
    }

    public static FacilioChain getReceivablesBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadReceivablesExtraFields());
        c.addCommand(new LoadReceivableLookupCommandV3());
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

    public static FacilioChain getConvertVendorQuoteToPoChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConvertVendorQuoteToPoCommandV3());
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

    public static FacilioChain getVendorQuotesBeforeFetchChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new LoadVendorQuotesExtraFieldsCommandV3());
        chain.addCommand(new LoadVendorQuotesLookupCommandV3());
        return chain;
    }

    public static FacilioChain getVendorQuotesAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetVendorQuotesLineItemsCommandV3());
        c.addCommand(new CheckVendorPortalAccessibilityCommandV3());
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
    public static FacilioChain getCloneDashboardTabChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CloneDashboardTabCommand());
        return c;
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
    public static FacilioChain getNewCloneDashboardChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CloneDashboardNewCommand());
        return c;
    }
    public static FacilioChain getDashboardListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetDashboardFolderCommand());
        c.addCommand(new GetPublishDashboardCommand());
        c.addCommand(new GetDashboardListCommand());
        c.addCommand(new GetDashboardSharingCommand());
        c.addCommand(new GetDashboardListResponseCommand());
        return c;
    }
    public static FacilioChain getMobileDashboardListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetDashboardFolderCommand());
        c.addCommand(new GetMobileDashboardListCommand());
        c.addCommand(new GetDashboardSharingCommand());
        c.addCommand(new GetDashboardListResponseCommand());
        return c;
    }

    public static FacilioChain getCloneReportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetReportCloneCommand());
        return c;
    }

    public static FacilioChain getMoveToDashboardChain() {
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

    public static FacilioChain getAutocadImportAppChain() {
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
        c.addCommand(new AddJobPlanGroupCommand());
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

    public static FacilioChain getUpdateDashboardChainV3(boolean newFlow) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DuplicateDashboardForBuildingCommand());
        c.addCommand(new V3UpdateDashboardWithWidgets());
        c.addCommand(new EnableMobileDashboardCommand());
        if (newFlow) {
            c.addCommand(new GetDashboardThumbnailCommand());
        }
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
    public static FacilioChain getAddOrUpdateDashboardAndTabChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateDashboardAndTabCommand());
        return c;
    }
    public static FacilioChain addOrUpdateWidgetChain(){
        FacilioChain c =  getDefaultChain();
        c.addCommand(new AddOrUpdateWidgetCommand());
        return c;
    }
    public static FacilioChain getDashboardTabListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetDashboardTabListCommand());
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
        c.addCommand(new DeletePPMPreOpenWorkorders());
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

    public static Command getShiftBeforeListChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddShiftSupplementsCommand());
        return chain;
    }

    public static FacilioChain getShiftBeforeSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateShiftsCommand());
        chain.addCommand(new AssignShiftActivityModuleCommand());
        chain.addCommand(new MarkAsNonDefaultShiftCommand());
        return chain;
    }

    public static FacilioChain getShiftBeforeDeleteChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateShiftsUsageCommand());
        return chain;
    }

    public static FacilioChain getShiftBeforeUpdateChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateShiftsCommand());
        chain.addCommand(new AssignShiftActivityModuleCommand());
        return chain;
    }

    public static Command getShiftAfterSummaryChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddComputedPropertiesForShiftCommand());
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

    public static Command getBreakBeforeListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddBreakSupplementsCommand());
        return c;
    }

    public static FacilioChain getBreakBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateBreakCommand());
        c.addCommand(new MarkBreakAsManualCommand());
        return c;
    }

    public static FacilioChain getBreakBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateBreakCommand());
        c.addCommand(new MarkBreakAsManualCommand());
        return c;
    }

    public static FacilioChain getBreakBeforeDeleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateBreakUsageCommand());
        return c;
    }

    public static FacilioChain addReadingKpi() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new PrepareReadingKpiCreationCommand());
        chain.addCommand(addConnectedReadingModulesCommand());
        chain.addCommand(new SetFieldAndModuleCommand());
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
        chain.addCommand(new UpdateNamespaceCommand());
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
        // c.addCommand(new ExcludeAvailableWorkOrderHazards());
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
        c.addCommand(new CreateReservationCommandV3());
        return c;
    }

    public static FacilioChain getReserveToolsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateToolsReservationCommandV3());
        return c;
    }

    public static FacilioChain getCostChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PlansCostCommandV3());
        return c;
    }

    public static FacilioChain getRotatingAssetUsagesChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetPmAndInspectionForRotatingAssetCommandV3());
        return c;
    }

    public static FacilioChain getWorkorderLabourPlanBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenericFetchLookUpFieldsCommandV3());
        c.addCommand(new LoadWOLabourPlanExtraFieldsCommandV3());
        return c;

    }

    public static FacilioChain getWorkorderLabourBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenericFetchLookUpFieldsCommandV3());
        c.addCommand(new LoadWOLabourExtraFieldsCommandV3());
        return c;

    }

    public static FacilioChain getJobPlanLabourBeforeFetchChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenericFetchLookUpFieldsCommandV3());
        c.addCommand(new LoadJobPlanLabourExtraFieldsCommandV3());
        return c;

    }

    public static FacilioChain getMultiResourceBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadMultiResourcesLookUpFieldsCommandV3());
        c.addCommand(new LoadMultiResourceExtraFieldsCommandV3());
        return c;

    }

    public static FacilioChain getWorkOrderItemChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetWorkOrderItemCommandV3());
        return chain;
    }

    public static FacilioChain getWorkorderItemFromReservationChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetWorkOrderItemFromReservationCommandV3());
        return chain;
    }

    public static FacilioChain getWorkorderToolFromReservationChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetWorkOrderToolFromReservationCommandV3());
        return chain;
    }

    public static FacilioChain getWorkOrderItemFromIssuedItemChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetWorkOrderItemFromIssuedItemCommandV3());
        return chain;
    }

    public static FacilioChain getWorkOrderToolFromIssuedToolChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetWorkOrderToolFromIssuedToolCommandV3());
        return chain;
    }

    public static FacilioChain getPlannedToolForActualsChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetPlannedToolForActualsCommandV3());
        return chain;
    }

    public static FacilioChain getPlannedServiceForActualsChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetPlannedServiceForActualsCommandV3());
        return chain;
    }

    public static FacilioChain getWorkOrderToolChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetWorkOrderToolCommandV3());
        return chain;
    }

    public static FacilioChain getWorkOrderServiceChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetWorkOrderServiceCommandV3());
        return chain;
    }

    public static FacilioChain getUnsavedSparePartsSelectionChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SparePartsSelectionCommand());
        return chain;
    }

    public static FacilioChain getJobPlanLabourChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new JobPlanPlannedLabourCommandV3());
        return c;
    }

    // public static FacilioChain getUnReserveItemsChainV3(){
    // FacilioChain c = getDefaultChain();
    // c.addCommand(new UnReserveItemsCommandV3());
    // return c;
    // }

    public static FacilioChain getWoPlannedItemsBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateWorkOrderPlannedItemsCommandV3());
        c.addCommand(new SetIsReservedCommandV3());
        c.addCommand(new SetWorkOrderPlannedItemsCommandV3());
        return c;
    }

    public static FacilioChain getWoPlannedToolsBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateWorkOrderPlannedToolsCommandV3());
        c.addCommand(new SetIsReservedForPlannedToolsCommandV3());
        c.addCommand(new SetWorkOrderPlannedToolsCommandV3());
        return c;
    }

    public static FacilioChain getItemReservationChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateReservationRecordForWOItemsCommandV3());
        c.addCommand(new UpdateReservedQuantityForItemsCommandV3());
        return c;
    }

    public static FacilioChain getToolReservationChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateReservationRecordForWOToolsCommandV3());
        c.addCommand(new UpdateReservedQuantityForToolsCommandV3());
        return c;
    }

    public static FacilioChain getAddOrUdpateWorkorderItemsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateTransactionEventTypeCommand());
        c.addCommand(getItemReservationChainV3());
        // c.addCommand(new LoadWorkorderItemLookUpCommand());
        c.addCommand(new CreateWeightedAverageChildTransactionsCommand());
        c.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(new ItemTransactionRemainingQuantityRollupCommandV3());
        c.addCommand(new AddActivitiesCommand());
        c.addCommand(new AddOrUpdateWorkorderCostCommandV3());
        c.addCommand(new UpdateWorkorderTotalCostCommandV3());
        return c;
    }

    public static FacilioChain getAddOrUdpateWorkorderLabourChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddorUpdateWoActualsCostforLabourCommand());
        c.addCommand(new UpdateWorkorderTotalCostLabourCommandV3());
        return c;
    }


    public static FacilioChain getAddOrUdpateWorkorderToolsChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateTransactionEventTypeCommand());
        c.addCommand(getToolReservationChainV3());
        c.addCommand(new PurchasedToolsQuantityRollupCommandV3());
        c.addCommand(new LoadWorkorderToolLookupCommand());
        c.addCommand(getUpdatetoolQuantityRollupChain());
        c.addCommand(new ToolTransactionRemainingQuantityRollupCommandV3());
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
        chain.addCommand(new PurchasedToolsQuantityRollupCommandV3());
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

    public static FacilioChain getAfterCreateWorkOrderCostChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(getFillParentIdAndUpdateWorkOrderCostChainV3());
        return chain;
    }

    public static FacilioChain getAfterUpdateWorkOrderCostChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(getFillParentIdAndUpdateWorkOrderCostChainV3());
        return chain;
    }

    public static FacilioChain getAfterDeleteWorkOrderCostChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(getFillParentIdAndUpdateWorkOrderCostChainV3());
        return chain;
    }

    public static FacilioChain getFillParentIdAndUpdateWorkOrderCostChainV3() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FacilioCommand() {
            @Override
            public boolean executeCommand(Context context) throws Exception {
                // adding boolean flag to add multiple costs in
                // UpdateWorkorderTotalCostCommandV3()
                context.put(FacilioConstants.ContextNames.IS_WORKORDER_COST_CHAIN, true);
                return false;
            }
        });
        chain.addCommand(new FillParentIdForCalculationOfWorkOrderCostV3());
        chain.addCommand(new UpdateWorkorderTotalCostCommandV3());
        return chain;
    }

    public static FacilioChain getReserveValidationChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReservationValidationCommandV3());
        return c;
    }

    public static FacilioChain getReserveToolValidationChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ToolReservationValidationCommandV3());
        return c;
    }

    public static FacilioChain getReserveChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReservationSummaryCommandV3());
        return c;
    }

    public static FacilioChain getToolsReserveChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ToolsReservationSummaryCommandV3());
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

    public static FacilioChain getNewDashboardRuleExecuteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecutedNewDashboardRuleCommand());
        return c;
    }

    public static FacilioChain getWorkorderActualsLabourChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new WorkOrderActualsLabourCommandV3());
        return c;
    }

    public static FacilioChain getWorkorderPlannedLabourChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new WorkOrderPlannedLabourCommandV3());
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

    public static FacilioChain getPeopleFromRecordFieldsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getPeopleFromRecordFieldsCommand());
        return c;
    }

    public static FacilioChain fetchAppListForPeopleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPeopleApplicationListCommand());
        return c;
    }

    public static FacilioChain addAppAccessForPeopleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddAppPortalAccessForPeopleCommand());
        return c;
    }

    public static FacilioChain convertPeopleTypeChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new convertPeopleTypeCommand());
        return c;
    }

    public static FacilioChain updateAppAccessChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateAppAccessCommand());
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

    public static FacilioChain getExportShiftPlannerChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetShiftExportFlagsCommand());
        c.addCommand(new GetPageEmployeesCommand());
        c.addCommand(new ExportShiftPlannerCommand());
        return c;
    }

    public static FacilioChain getShiftPlannerCalendarChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetPageEmployeesCommand());
        c.addCommand(new ComputeShiftPlannerRangeCommand());
        c.addCommand(new ComposeShiftCalendarCommand());
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


    public static FacilioChain addReadingRuleChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReadingRuleDependenciesCommand());
        c.addCommand(addRuleReadingsModuleChain());
        c.addCommand(new AddNewReadingRuleCommand());
        return c;
    }

    public static FacilioChain addRuleReadingsModuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddRuleReadingsModuleCommand());
        c.addCommand(addConnectedReadingModulesCommand());
        return c;
    }

    private static FacilioChain addConnectedReadingModulesCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddReadingCategoryCommand());
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

    private static FacilioChain addNsAndNsFieldsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddNamespaceCommand());
        c.addCommand(new AddNamespaceFieldsCommand());
        return c;
    }

    public static FacilioChain addNamespaceAndFieldsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddExecutorCommand());
        c.addCommand(addNsAndNsFieldsChain());
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

    public static FacilioChain getReadingAlarmAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadOccurrenceForAlarmCommand());
        c.addCommand(new HandleV3AlarmListLookupCommand());
        return c;
    }

    public static FacilioChain beforeFetchRuleLogsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadSupplementsForRuleLogsCommand());
        c.addCommand(new LoadSupplementsForConnectedRuleLogs());
        return c;
    }

    public static FacilioChain beforeFetchKpiLogsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadSupplementsForKpiLogsCommand());
        c.addCommand(new LoadSupplementsForConnectedRuleLogs());
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

    public static FacilioChain beforeUpdateReadingRuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PrepareReadingRuleForUpdateCommand());
        return c;
    }

    public static FacilioChain updateReadingRuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(addOrDeleteFaultImpactChain());
        c.addCommand(new UpdateNamespaceCommand());
        c.addCommand(new UpdateReadingRuleCommand());
        c.addCommand(new UpdateWorkflowCommand());
        c.addCommand(updateReadingRuleRcaChain());
        return c;
    }

    public static FacilioChain afterDeleteReadingRuleRcaChain() {
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

    public static FacilioChain addOrUpdateFormRelationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateFormRelationCommand());
        return c;
    }

    public static FacilioChain moveWoInQueueForPreOpenToOpenChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPMV2WorkordersToMoveInQueueForPreOpenToOpen());
        c.addCommand(new ScheduleWorkordersToMoveInQueueForPreOpenToOpen());
        return c;
    }

    public static FacilioChain getPMPlannerBeforeUpdateCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateTriggerTypeForPMPlannerCommand());
        c.addCommand(new BeforeSavePMPlannerCommand());
        return c;
    }

    public static FacilioChain BaseSchedulerSingleInstanceJobChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new BaseSchedulerSingleInstanceCommand());
        return c;
    }

    public static FacilioChain PMV2NightlyJobChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateAndGetAllPMPlannersCommand());
        c.addCommand(new RunExecuterBaseForPMPlannersCommand());
        return c;
    }

    public static FacilioChain PMV2BeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RemoveDuplicateSites());
        c.addCommand(new PMBeforeCreateCommand());
        c.addCommand(new AddPMDetailsBeforeUpdateCommand());
        c.addCommand(new UpdateResourcePlannerOnPMSitesUpdateCommand());
        return c;
    }

    public static FacilioChain PMPlannerAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateTimelineViewCalenderTypeCommand());
        c.addCommand(new FetchPPMDetailsFromPlannerCommand());
        c.addCommand(new DeletePPMPreOpenWorkorders());
        c.addCommand(new MarkPMAsDeactivatedCommand());
        return c;
    }

    public static FacilioChain PPMAfterPatchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PMAfterPatchCommand());
        c.addCommand(new MarkPMAsDeactivatedCommand());
        c.addCommand(new DeletePPMPreOpenWorkorders());
        return c;
    }

    public static FacilioChain PMPlannerAfterDeleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeletePMPlannerPreOpenWorkOrders());
        c.addCommand(new DeletePlannerTriggerCommand());
        return c;
    }

    public static FacilioChain PMV2TriggerBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructTriggerNameCommand());
        return c;
    }

    public static FacilioChain PMV2TriggerBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructTriggerNameCommand());
        return c;
    }
    public static FacilioChain SpaceBookingSupplementsAndExtraFieldsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SpaceBookingExtraFieldsCommand());
        c.addCommand(new SpaceBookingSupplementsCommand());
        return c;
    }

    public static FacilioChain getWorkflowExportChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new WorkflowExportCommand());
        return c;
    }

    public static FacilioChain PMV2BeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RemoveDuplicateSites());
        c.addCommand(new PMBeforeCreateCommand());
        c.addCommand(new AddPMDetailsBeforeCreateCommand());
        return c;
    }

    public static FacilioChain updateResourceDecommissionChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new ValidateDecommisionCommand());
        c.addCommand(new FetchDependentResourceDataCommand());
        c.addCommand(new UpdateResourceDecommissionCommand());
        c.addCommand(new DecommissionWmsCommand());
        return c;
    }

    public static FacilioChain addDecommissionLog() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddDecommissionLogCommand());
        return c;
    }

    public static FacilioChain addOrUpdateUserScopingChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateUserScopingCommand());
        return c;
    }

    public static FacilioChain deleteUserScopingChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteUserScopingCommand());
        return c;
    }

    public static FacilioChain addOrUpdateUserScopingConfigChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateUserScopingConfigCommand());
        return c;
    }

    public static FacilioChain updateUserScopingStatusChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateUserScopingStatusCommand());
        return c;
    }

    public static FacilioChain getAttendanceTransitionListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchAttendanceTransitionCommand());
        return c;
    }

    public static FacilioChain getAttendanceTransactionListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchAttendanceTransactionCommand());
        return c;
    }

    public static Command getAttendanceTxnBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateAttendanceTransactionCommand());
        return c;
    }

    public static Command getAttendanceTxnAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddPlaceholderAttendanceCommand());
        return c;
    }

    public static FacilioChain getAttendanceListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ListAttendanceCommand());
        return c;
    }

    public static Command getAttendanceTxnAfterTransactionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateAttendance());
        c.addCommand(new UpdatePeopleAvailabilityStatusCommand());
        c.addCommand(new SilentNotificationForAttendanceTransactionCommand());
        return c;
    }

    public static FacilioChain getShiftStatesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetShiftStatesCommand());
        return c;
    }

    public static FacilioChain getAttendanceSettingsUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetAttendanceSettingsUpdateCommand());
        return c;
    }

    public static FacilioChain getAttendanceSettingsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetAttendanceSettingsCommand());
        return c;
    }

    public static FacilioChain preCreateWorkOrderAfterPPMPublish() {
        FacilioChain c = FacilioChain.getTransactionChain(1800000); // in ms
        c.addCommand(new FetchPlannerDetails());
        c.addCommand(new PreCreateWorkOrderRecord());
        return c;
    }

    public static FacilioChain reviseJobPlan() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchJobPlanDetails());
        c.addCommand(new AddJobPlanGroupCommand());
        c.addCommand(new JobPlanContextCloningCommand());
        c.addCommand(new SetJobPlanStatusCommand());
        c.addCommand(new CreateJobPlanAfterCloning());
        c.addCommand(new CloneJobPlanAdditionRecords());
        return c;
    }

    public static FacilioChain reviseInvoice() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchInvoiceDetailsCommand());
        c.addCommand(new AddInvoiceGroupCommand());
        c.addCommand(new InvoiceContextCloneCommand());
        c.addCommand(new CreateInvoiceAfterCloningCommand());
        c.addCommand(new ReviseStatusInvoiceDetailsCommand());
        c.addCommand(new AddActivitiesCommandV3());

        return c;
    }

    public static FacilioChain cloneInvoiceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchInvoiceDetailsCommand());
        c.addCommand(new InvoiceContextCloneCommand());
        c.addCommand(new CreateInvoiceAfterCloningCommand());
        c.addCommand(new AddInvoiceCloneActivityType());
        c.addCommand(new AddActivitiesCommandV3());
        return c;
    }

    public static FacilioChain convertInvoiceTypeChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchInvoiceDetailsCommand());
        c.addCommand(new InvoiceContextCloneCommand());
        c.addCommand(new InvoiceConversionTypeCommand());
        c.addCommand(new GetInvoiceDefaultFormCommand());
        return c;
    }

    public static FacilioChain convertPOtoInvoiceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConvertPOtoInvoiceCommand());
        c.addCommand(new GetInvoiceDefaultFormCommand());
        return c;
    }

    public static FacilioChain convertQuoteToInvoiceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConvertQuoteToInvoiceCommand());
        c.addCommand(new GetInvoiceDefaultFormCommand());
        return c;
    }

    public static FacilioChain convertWorkOrderToInvoiceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConvertWorkOrderToInvoiceCommand());
        c.addCommand(new GetInvoiceDefaultFormCommand());
        return c;
    }



    public static FacilioChain associateTermsInvoiceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AssociateInvoiceTermsCommand());
        return c;
    }
    public static FacilioChain manageTermsInvoiceChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DissociateTermsCommand());
        c.addCommand(new AssociateInvoiceTermsCommand());
        return c;
    }

    public static FacilioChain getSendInvoiceMailChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SendInvoiceEmailCommand());
        c.addCommand(new AddActivitiesCommandV3());
        return c;
    }
    public static FacilioChain getPublishJobPlanChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchJobPlanDetails());
        c.addCommand(new AddJobPlanGroupCommand());
        c.addCommand(new SwapJobPlanDetailsCommand());
        c.addCommand(new PublishJobPlanCommand());
        c.addCommand(new UnPublishAndRepublishPPM());
        return c;
    }

    public static FacilioChain fetchJobPlanVersionHistoryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchJobPlanDetails());
        c.addCommand(new FetchVersionHistoryCommand());
        return c;
    }

    public static FacilioChain fetchInvoiceVersionHistoryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchInvoiceDetailsCommand());
        c.addCommand(new FetchInvoiceVersionHistoryCommand());
        return c;
    }


    public static FacilioChain getJobPlanCloneChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchJobPlanDetails());
        c.addCommand(new JobPlanContextCloningCommand());
        c.addCommand(new SetJobPlanStatusCommand());
        c.addCommand(new CreateJobPlanAfterCloning());
        c.addCommand(new CloneJobPlanAdditionRecords());
        return c;
    }

    public static FacilioChain addOrUpdatePermissionSetChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdatePermissionSet());
        c.addCommand(new FetchPermissionSetCommand());
        return c;
    }

    public static FacilioChain deletePermissionSetChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeletePermissionSetCommand());
        return c;
    }

    public static FacilioChain updatePermissionsForPermissionSetChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePermissionsForPermissionSetCommand());
        return c;
    }

    public static FacilioChain getStoreroomBeforeSaveChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocationObjectFromSiteV3());
        c.addCommand(new UpdateServingSitesinStoreRoomCommandV3());
        return c;
    }

    //	should be removed after old app migration
    public static FacilioChain getOldAppMigrationChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new UpdateLayoutCommand());
        chain.addCommand(new AddMobileLayoutTabsCommand());
        chain.addCommand(new AddWebLayoutTabsCommand());
        chain.addCommand(new AddSetupLayoutTabsCommand());
        chain.addCommand(new AddRelatedApplicationsForMigratingAppCommand());
        chain.addCommand(new RelateMainAppWithMaintenanceCommand());
        return chain;
    }

    public static FacilioChain getPmV2ResourcePlannerAfterCreateChain(){

        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPMDetailsFromPmResourcePlanner());
        c.addCommand(new DeletePPMPreOpenWorkorders());
        c.addCommand(new MarkPMAsDeactivatedCommand());
        return c;
    }

    public static FacilioChain getPmV2PlannerAfterCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddTimelineViewForPMPlannerCommand());
        c.addCommand(new FetchPPMDetailsFromPlannerCommand());
        c.addCommand(new DeletePPMPreOpenWorkorders());
        c.addCommand(new MarkPMAsDeactivatedCommand());
        return c;
    }

    public static FacilioChain getReportListAsOptionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetReportsAsOptionCommand());
        return c;
    }

    public static FacilioChain getReporsForDownloadChain(boolean isNotImageOrPDF, boolean isPivot, boolean isModuleReport) {
        FacilioChain c = getDefaultChain();
        if (isPivot) {
            c.addCommand(TransactionChainFactory.getExportpivotReportFileChain());
        } else if (isModuleReport) {
            c.addCommand(TransactionChainFactory.getExportModuleReportFileChain(isNotImageOrPDF));
        } else {
            c.addCommand(TransactionChainFactory.getExportReportFileChain());
        }
        return c;
    }

    public static FacilioChain getSendReportsAsMailChain() throws Exception {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SendMultipleReportAsMailCommand());
        return c;
    }

    public static FacilioChain getPmV2ResourcePlannerBeforeSaveCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PMResourcePlannerBeforeSaveCommand());
        c.addCommand(new ValidatePmResourcePlannerResource());
        c.addCommand(new ValidateDuplicateResourcesInsidePlannerCommand());
        c.addCommand(new ValidateRotatingAssetPMResource());
        return c;
    }

    public static FacilioChain addSensorRuleNsChain(){

        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructNsFieldsForSensorCommand());
        c.addCommand(new SetParentIdForNamespaceCommand());
        c.addCommand(addNamespaceAndFieldsChain());
        return c;
    }

    public static FacilioChain addSensorRuleTypeAndNsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddSensorRuleTypeCommand());
        c.addCommand(new AddSensorAlarmDetailsCommand());
        c.addCommand(addSensorRuleNsChain());
        return c;
    }

    public static FacilioChain addSensorRuleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SensorRulePreCreationCommand());
        c.addCommand(addSensorReadingsChain());
        c.addCommand(new SetRecordModuleAndFieldIdCommand());
        return c;
    }

    public static FacilioChain addSensorReadingsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddSensorReadingsCommand());
        c.addCommand(getAddCategoryReadingChain());
        return c;
    }
    public static FacilioChain getPivotModulesList() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetPivotModulesListCommand());
        return c;
    }


    public  static FacilioChain fetchConnectedCategoryStatusChain() {

        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FetchAssetCategoryLevelStatusCommand());
        return chain;
    }

    public static FacilioChain createUtilityAccountChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateUtilityAccountCommand());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ACTIVITY));
        return c;
    }

    public static FacilioChain getUtilityDataForReferralChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new UtilityDataForReferralCommand());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ACTIVITY));
        return c;
    }

    public static FacilioChain getActivateMeterChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ActivateMetersCommand());
        return c;
    }

    public static FacilioChain scheduleOngoingMonitoringChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ScheduleOngoingMonitoringCommand());
        return c;
    }

    public static FacilioChain getUtilityBillsChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new UtilityBillsCommand());
        return c;
    }

    public static FacilioChain getUtilityDisputeChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new UtilityDisputeResolveCommand());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.UTILITY_DISPUTE_ACTIVITY));
        return c;
    }

    public static FacilioChain getAssociateMeterChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new AssociateMeterCommand());
        return c;

    }

    public static FacilioChain getWorkflowVersionHistoryBeforeSaveCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new WorkflowVersionHistoryFillVersionCommand());
        return c;
    }

    public static FacilioChain updateConnectedCategoryStatusChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new UpdateAssetCategoryLevelStatusCommand());
        return chain;
    }

    public static FacilioChain fetchSiteDetailsWithoutScopingCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchSiteWithoutScoping());
        return c;
    }

    public static FacilioChain getCreateMeterBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillDefaultValuesForMetersCommand());
        c.addCommand(new UtilityTypeAdditionInExtendModuleCommand());
        c.addCommand(new ValidateMeterQrValueCommand());
        c.addCommand(new AssignMeterActivityModuleCommand());
        return c;
    }

    public static FacilioChain getCreateMeterAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MeterAfterSaveCommand());
        c.addCommand(new GetUtilityTypeReadingsCommand());
        c.addCommand(new GetReadingFieldsCommand());
        c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
        c.addCommand(new RemoveMeterExtendedModulesFromRecordMap());
        return c;
    }

    public static FacilioChain getUpdateMeterBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UtilityTypeAdditionInExtendModuleCommand());
        c.addCommand(new ValidateMeterQrValueCommand());
        c.addCommand(new AssignMeterActivityModuleCommand());
        return c;
    }

    public static FacilioChain getUpdateMeterAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RemoveMeterExtendedModulesFromRecordMap());
        return c;
    }

    public static FacilioChain getMeterSummaryBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MeterSupplementsSupplyCommand());
        return c;
    }

    public static FacilioChain getMeterSummaryAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadMeterDetailsCommand());
        return c;
    }

    public static FacilioChain getMeterListBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MeterSupplementsSupplyCommand());
        return c;
    }

    public static FacilioChain getCreateUtilityTypeBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddUtilityTypeModuleCommand());
        c.addCommand(TransactionChainFactory.commonAddModuleChain());
        c.addCommand(new UpdateUtilityTypeMeterModuleIdCommand());
        c.addCommand(new AddRelationshipForCustomUtilityTypeCommand());
        return c;
    }

    public static FacilioChain getUtilityTypeBeforeDeleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateUtilityTypeDeletion());
        return c;
    }

    public static FacilioChain getUtilityTypeSummaryAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetUtilityTypeModuleCommand());
        return c;
    }

    public static FacilioChain getUtilityTypeListAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetUtilityTypeModuleCommand());
        return c;
    }

    public static FacilioChain getCreateVirtualMeterTemplateBeforeChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillDefaultValuesAndValidateForVMTemplates());
        //c.addCommand(new AddRelationshipWithParentModuleCommand());
        c.addCommand(new AssignVMTemplateActivityModuleCommand());
        return c;
    }

    public static FacilioChain getCreateVirtualMeterTemplateAfterChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddVirtualMeterTemplateReadingCommand());
        return c;
    }

    public static FacilioChain getPublishVirtualMeterTemplateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MarkVMAsPublishedCommand());
        return c;
    }

    public static FacilioChain getRunHistoryVirtualMeterTemplateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RunHistoryForVMTemplateReadings());
        return c;
    }

    public static FacilioChain getGenerateVirtualMeterChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddWMSMessageForVirtualMeterPopulationCommand());
        return c;
    }

    public static FacilioChain fetchVMGeneratedResourcesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchVMGeneratedResourcesCommand());
        return c;
    }

    public static FacilioChain getVirtualMeterTemplateSummaryBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new VMTemplateSupplementsSupplyCommand());
        return c;
    }

    public static FacilioChain getVirtualMeterTemplateSummaryAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillVirtualMeterTemplateDetailsCommand());
        return c;
    }

    public static FacilioChain getVirtualMeterTemplateListBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new VMTemplateSupplementsSupplyCommand());
        return c;
    }

    public static FacilioChain getVirtualMeterTemplateListAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillVirtualMeterTemplateDetailsCommand());
        return c;
    }

    public static FacilioChain getUpdateVirtualMeterTemplateBeforeChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AssignVMTemplateActivityModuleCommand());
        return c;
    }

    public static FacilioChain getUpdateVirtualMeterTemplateAfterChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateVMTemplateReadingCommand());
        return c;
    }
    public static FacilioChain getUpdateVirtualMeterTemplateReadingAfterChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateNameSpaceForVMReadingsCommand());
        c.addCommand(new UpdateNamespaceCommand());
        c.addCommand(new UpdateWorkflowCommand());
        return c;
    }
    public static FacilioChain fetchExistingRelationsChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchExistingRelationsCommand());
        return c;
    }

    public static FacilioChain fetchMeterRelationsChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchMeterRelationsCommand());
        return c;
    }

    public static FacilioChain getMeterMonthlyConsumptionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetMeterMonthlyConsumptionCommand());
        return c;
    }
    public static FacilioChain getMeterYearlyConsumptionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetMeterYearlyConsumptionCommand());
        return c;
    }
    public static FacilioChain getMeterPeakChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetMeterPeakCommand());
        return c;
    }

    public static FacilioChain getSFG20ConnectCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SFG20ConnectCommand());
        c.addCommand(new SFGJobPlanComparisonCommand());
        c.addCommand(new SFGtoFacilioJobplanCommand());
        return c;
    }

    public static FacilioChain getSFG20ConnectTestCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddSFG20SyncHistoryCommand());
        c.addCommand(new AddSFGBackgroundActivityCommand());
        c.addCommand(new SFG20ConnectCommand());
        c.addCommand(new SFGJobPlanComparisonCommand());
        c.addCommand(new SFGtoFacilioJobplanCommand());
        return c;
    }

    public static FacilioChain getSFG20SyncCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddSFG20SyncHistoryCommand());
        c.addCommand(new AddSFGBackgroundActivityCommand());
        c.addCommand(new SFG20SyncJobCommand());
        return c;
    }

    public static FacilioChain getSFG20SettingAddOrUpdateCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SFG20SettingAddOrUpdateCommand());
        return c;
    }


    public static FacilioChain generateRawAlarms(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateRawAlarmsCommand());
        return c;
    }

    public static FacilioChain getCalendarAfterCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MakeEventTimeAsCalendarTimeSlotCommand());
        c.addCommand(new AssociateEventAndTimeSlotForCalendarCommand());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.Calendar.CALENDAR_ACTIVITY_MODULE));
        return c;
    }

    public static FacilioChain getCalendarAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MakeEventTimeAsCalendarTimeSlotCommand());
        c.addCommand(new AssociateEventAndTimeSlotForCalendarCommand());
        c.addCommand(new DropCalendarTimeSlotCommand());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.Calendar.CALENDAR_ACTIVITY_MODULE));
        return c;
    }

    public static FacilioChain getEventAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddEventTimeSlotCommand());
        c.addCommand(new DropAssociatedCalendarSlotRecordCommand());
        c.addCommand(new CreateCalendarTimeSlotCommand());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.Calendar.EVENT_ACTIVITY_MODULE));
        return c;
    }

    public static FacilioChain getEventAfterCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddEventTimeSlotCommand());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.Calendar.EVENT_ACTIVITY_MODULE));
        return c;
    }

    public static FacilioChain getCalendarAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillEventAndTimeSlotForCalendarCommand());
        return c;
    }

    public static FacilioChain getEventAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillTimeSlotForEventCommand());
        c.addCommand(new SetSpecialCaseMilliSecondsCommand());
        return c;
    }

    public static FacilioChain getCalendarBeforeListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchSupplementsForCalendarCommand());
        return c;
    }

    public static FacilioChain getControlActionBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateActionsOfControlActionCommand());
        c.addCommand(new AddCriteriaForControlActionCommand());
        c.addCommand(new SetControlActionInitialStatusCommand());
        return c;
    }

    public static FacilioChain getControlActionAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddActionCommand());
        c.addCommand(new setInitialApprovalStatusForControlActionCommand());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME));
        return c;
    }

    public static FacilioChain getControlActionBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateActionsOfControlActionCommand());
        c.addCommand(new UnPublishControlActionOnEditCommand());
        c.addCommand(new AddCriteriaForControlActionCommand());
        c.addCommand(new AddActionCommand());
        c.addCommand(new StatusUpdateOnApprovalProcessCommand());
        return c;
    }

    public static FacilioChain getCommandGenerationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateControlActionCommandsCommand());
        c.addCommand(new CreateOneTimeJobForCommandExecutionCommand());
        return c;
    }

    public static FacilioChain getControlActionGenerationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateControlActionFromTemplateCommand());
        return c;
    }

    public static FacilioChain getControlActionAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetActionAndCriteriaToControlActionCommand());
        return c;
    }

    public static FacilioChain getControlActionTemplateAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddActionsForControlActionTemplateCommand());
        c.addCommand(new DropControlActionsOfControlActionTemplateCommand());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_ACTIVITY_MODULE_NAME));
        return c;
    }

    public static FacilioChain getControlActionBeforeDeleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DropActionsAndCommandsOfControlActionCommand());
        return c;
    }

    public static FacilioChain getControlActionTemplateBeforeDeleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DropControlActionsOfControlActionTemplateCommand());
        return c;
    }


    public static FacilioChain getActionAfterSummaryCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchReadingFieldDetailsCommand());
        return c;
    }

    public static FacilioChain getCommandsBeforeListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchSupplementsForControlActionCommandsCommand());
        return c;
    }
    public static FacilioChain getCommandsAfterListChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillReadingFieldDetailsForCommandsCommand());
        return c;
    }
    public static FacilioChain getCommandsAfterSummaryChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillReadingFieldDetailsForCommandsCommand());
        return c;
    }
    public static FacilioChain getControlActionBeforeListChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchSupplementsForControlActionCommand());
        return c;
    }
    public static FacilioChain getControlActionAfterUpdateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME));
        return c;
    }
    public static FacilioChain getControlActionTemplateBeforeListChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchSupplementsForControlActionTemplateCommand());
        return c;
    }
    public static FacilioChain getControlActionTemplateAfterFetchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillActionsAndCriteriaForControlActionTemplateCommand());
        return c;
    }
    public static FacilioChain getControlActionTemplateBeforeSaveChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateActionsOfControlActionTemplateCommand());
        c.addCommand(new AddCriteriaFromControlActionTemplateCommand());
        c.addCommand(new SetControlActionExecutionTypeCommand());
        c.addCommand(new MakeControlActionTemplateStatusAsInActiveCommand());
        return c;
    }
    public static FacilioChain getEventBeforeFetchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchSupplementsForCalendarEventCommand());
        return c;
    }
    public static FacilioChain getCalendarEventMappingBeforeListChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchSupplementsForCalendarEventMappingCommand());
        return c;
    }
    public static FacilioChain getCalendarEventMappingAfterListChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillTimeSlotForCalendarEventMappingContext());
        return c;
    }
    public static FacilioChain getPublishControlActionChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new PublishControlActionCommand());
        return chain;
    }
    public static FacilioChain getUnPublishControlActionChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new UnPublishControlActionCommand());
        return chain;
    }
    public static FacilioChain getActivateControlActionTemplateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ActivateControlActionTemplateCommand());
        return c;
    }
    public static FacilioChain getInActivateControlActionTemplateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new InActivateControlActionTemplateCommand());
        return c;
    }
    public static FacilioChain getActionBeforeListChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchSupplementsForActionCommand());
        return c;
    }

    public static FacilioChain addQuotationSetting(){
        FacilioChain c=getDefaultChain();
        c.addCommand(new addQuotationSettingCommand());
        return c;
    }

    public static FacilioChain updateQuotationSetting() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new updateQuotationSettingCommand());
        return c;
    }

    public static FacilioChain getDeleteQuoteSettingChain() {

        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteQuoteSettingCommand());
        return c;
    }

    public static FacilioChain addNumberformat() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new addNumberFormatCommand());
        return c;
    }

    public static FacilioChain updateNumberFormat() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new updateNumberFormatCommand());
        return c;
    }

    public static FacilioChain deleteNumberFormat() {

        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteNumberFormatCommand());
        return c;
    }

    public static FacilioChain addOrUpdateInvoiceSettingChain(){
        FacilioChain c=getDefaultChain();
        c.addCommand(new AddOrUpdateInvoiceSettingCommand());
        return c;
    }

    public static  FacilioChain getDeleteInvoiceSettingChain() {

        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteInvoiceSettingCommand());
        return c;
    }

    public static FacilioChain createFlaggedEventWorkorder(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateFlaggedEventWorkorderCommand());
        return c;
    }

    public static FacilioChain flaggedEventTakeCustody(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FlaggedEventTakeCustodyCommand());
        return c;
    }

    public static FacilioChain passToNextBureau(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new PassToNextBureauCommand());
        return c;
    }

    public static FacilioChain inhibit(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new InhibitFlaggedEventCommand());
        return c;
    }

    public static FacilioChain closeFlaggedEvent(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CloseFlaggedEventCommand());
        return c;
    }

    public static FacilioChain isAllAlarmClosed(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CloseFlaggedEventCommand());
        return c;
    }
    public static FacilioChain removeDummyAccountChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new removeDummyAccountCommand());
        return c;
    }
    public static FacilioChain getEventBeforeSaveChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateEventDetailsCommand());
        return c;
    }
    public static FacilioChain getControlActionCancelChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CancelControlActionCommand());
        return c;
    }
}
