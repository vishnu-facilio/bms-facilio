package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;

import com.facilio.bmsconsoleV3.commands.asset.AssetSupplementsSupplyCommand;
import com.facilio.bmsconsoleV3.commands.assetDepartment.ValidateAssetDepartmentDeletionV3;
import com.facilio.bmsconsoleV3.commands.assetType.ValidateAssetTypeDeletionV3;
import com.facilio.bmsconsoleV3.commands.dashboard.*;
import com.facilio.bmsconsoleV3.commands.floorplan.*;
import com.facilio.bmsconsoleV3.commands.jobplan.FillUpJobPlanSectionAdditionInfoObject;
import com.facilio.bmsconsoleV3.commands.jobplan.FillUpJobPlanTaskAdditionInfoObject;
import com.facilio.bmsconsoleV3.commands.jobplan.ValidationForJobPlanCategory;
import com.facilio.bmsconsoleV3.commands.jobplan.AddPlannerIdFilterCriteriaCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.AddLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.DeleteLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.FetchLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.licensinginfo.UpdateLicensingInfoCommand;
import com.facilio.bmsconsoleV3.commands.people.UpdateScopingForPeopleCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.*;
import com.facilio.bmsconsoleV3.commands.purchaserequest.LoadPoPrListLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.readingimportapp.AddReadingImportAppDataCommand;
import com.facilio.bmsconsoleV3.commands.readingimportapp.DeleteReadingImportDataCommand;
import com.facilio.bmsconsoleV3.commands.readingimportapp.UpdateReadingImportDataCommand;
import com.facilio.bmsconsoleV3.commands.space.SpaceFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.spacecategory.ValidateSpaceCategoryDeletionV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.AutoAwardingPriceCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.CreatePurchaseOrdersCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.CreateVendorQuotesCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.SetRequestForQuotationLineItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.servicerequest.AddActivityForServiceRequestCommandV3;
import com.facilio.bmsconsoleV3.commands.servicerequest.AddRequesterForServiceRequestCommandV3;
import com.facilio.bmsconsoleV3.commands.servicerequest.SetIsNewForServiceRequestCommandV3;
import com.facilio.bmsconsoleV3.commands.requestForQuotation.*;
import com.facilio.bmsconsoleV3.commands.tasks.*;
import com.facilio.bmsconsoleV3.commands.workorder.*;
import com.facilio.bmsconsoleV3.commands.tool.AddBulkToolStockTransactionsCommandV3;
import com.facilio.bmsconsoleV3.commands.tool.ToolQuantityRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.tool.ToolTypeQuantityRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.tool.UpdateIsUnderStockedCommandV3;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsoleV3.commands.receipts.*;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.command.*;
import com.facilio.bmsconsoleV3.plannedmaintenance.jobplan.FillTasksAndPrerequisitesCommand;
import com.facilio.v3.commands.*;
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
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.commands.util.AddColorPaletteCommand;
import com.facilio.bmsconsole.commands.util.DeleteColorPaletteCommand;
import com.facilio.bmsconsole.commands.util.ListColorPaletteCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsoleV3.commands.accessibleSpaces.AddAccessibleSpacesCommand;
import com.facilio.bmsconsoleV3.commands.accessibleSpaces.DeleteAccessibleSpacesCommand;
import com.facilio.bmsconsoleV3.commands.accessibleSpaces.FetchAccessibleSpacesCommand;
import com.facilio.bmsconsoleV3.commands.assetCategory.AddAssetCategoryModuleCommandV3;
import com.facilio.bmsconsoleV3.commands.assetCategory.UpdateCategoryAssetModuleIdCommandV3;
import com.facilio.bmsconsoleV3.commands.assetCategory.ValidateAssetCategoryDeletionV3;
import com.facilio.bmsconsoleV3.commands.assetDepartment.ValidateAssetDepartmentDeletionV3;
import com.facilio.bmsconsoleV3.commands.assetType.ValidateAssetTypeDeletionV3;
import com.facilio.bmsconsoleV3.commands.budget.ValidateBudgetAmountCommandV3;
import com.facilio.bmsconsoleV3.commands.budget.ValidateChartOfAccountTypeCommandV3;
import com.facilio.bmsconsoleV3.commands.client.UpdateClientIdInSiteCommandV3;
import com.facilio.bmsconsoleV3.commands.clientcontact.CheckForMandatoryClientIdCommandV3;
import com.facilio.bmsconsoleV3.commands.clientcontact.UpdateClientAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments.AddOrUpdateAdminDocumentsSharingCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.*;
import com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory.AddOrUpdateContactDirectorySharingCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.AddOrUpdateDealsSharingInfoCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.AddOrUpdateNeighbourhoodSharingCommandV3;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.NeighbourhoodAddLocationCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation.AddOrUpdateNewsSharingCommandV3;
import com.facilio.bmsconsoleV3.commands.employee.AddPeopleTypeForEmployeeCommandV3;
import com.facilio.bmsconsoleV3.commands.employee.UpdateEmployeePeopleAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.facility.*;
import com.facilio.bmsconsoleV3.commands.insurance.AssociateVendorToInsuranceCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.ValidateDateCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.*;
import com.facilio.bmsconsoleV3.commands.jobplan.AddJobPlanTasksCommand;
import com.facilio.bmsconsoleV3.commands.people.CheckforPeopleDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.people.UpdatePeoplePrimaryContactCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaserequest.PreFillAddPurchaseRequestCommand;
import com.facilio.bmsconsoleV3.commands.purchaserequest.PurchaseRequestTotalCostRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.*;
import com.facilio.bmsconsoleV3.commands.reports.*;
import com.facilio.bmsconsoleV3.commands.tasks.AddTaskSectionsV3;
import com.facilio.bmsconsoleV3.commands.tasks.AddTasksCommandV3;
import com.facilio.bmsconsoleV3.commands.tasks.ValidateTasksCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantSpaceRelationCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantUserCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.CheckForMandatoryTenantIdCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.UpdateTenantAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.termsandconditions.ReviseTandCCommand;
import com.facilio.bmsconsoleV3.commands.transferRequest.*;
import com.facilio.bmsconsoleV3.commands.usernotification.*;
import com.facilio.bmsconsoleV3.commands.vendor.AddInsuranceVendorRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddVendorContactsCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorcontact.CheckForMandatoryVendorIdCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorcontact.UpdateVendorContactAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.AddOrUpdateLocationForVisitorCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.CheckForVisitorDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.*;
import com.facilio.bmsconsoleV3.commands.visitorlogging.*;
import com.facilio.bmsconsoleV3.commands.workpermit.*;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.faultimpact.command.FaultImpactAfterSaveCommand;
import com.facilio.readingrule.faultimpact.command.FaultImpactBeforeSaveCommand;
import com.facilio.relation.command.GenerateRelationDeleteAPIDataCommand;
import com.facilio.relation.command.GenerateRelationModuleAPIDataCommand;
import com.facilio.relation.command.ValidateRelationDataCommand;
import com.facilio.trigger.command.*;

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

        return c;
    }

    public static FacilioChain getWorkOrderWorkflowsChainV3(boolean sendNotification) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_CUSTOM_CHANGE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SATISFACTION_SURVEY_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SURVEY_ACTION_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteSLAWorkFlowsCommand());
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
        c.addCommand(new AddOrUpdateSLABreachJobCommandV3(true));
        // to be removed once all attachments are handled as sub module
        c.addCommand(new UpdateTicketAttachmentsOldParentIdCommandV3());
        c.addCommand(new AddActivitiesCommandV3());

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

        return c;
    }

    public static FacilioChain getWorkorderAfterUpdateChain(boolean sendNotification) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillContextAfterWorkorderUpdateCommandV3());
        c.addCommand(new VerifyQrCommand());
        c.addCommand(new SendNotificationCommandV3());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SATISFACTION_SURVEY_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SURVEY_ACTION_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_RULE,
                WorkflowRuleContext.RuleType.CHILD_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_APPROVAL_RULE,
                WorkflowRuleContext.RuleType.REQUEST_REJECT_RULE));
        c.addCommand(new AddOrUpdateSLABreachJobCommandV3(false));
        c.addCommand(new ExecuteSLACommitmentWorkflowsCommand());
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(
                        new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE,
                                WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE))
                .addCommand(new ClearAlarmOnWOClosureCommand())
                .addCommand(new ExecuteTaskFailureActionCommand()));
        c.addCommand(new ConstructTicketNotesCommand());
        c.addCommand(TransactionChainFactory.getAddNotesChain());
        c.addCommand(new AddActivitiesCommand());

        return c;
    }

    public static FacilioChain getTenantContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new CheckforPeopleDuplicationCommandV3());
        c.addCommand(new CheckForMandatoryTenantIdCommandV3());
        return c;
    }

    public static FacilioChain getTenantContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new UpdateTenantAppPortalAccessCommandV3());
        c.addCommand(new UpdateScopingForPeopleCommandV3());
        return c;
    }

    public static FacilioChain getVendorContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new CheckforPeopleDuplicationCommandV3());
        c.addCommand(new CheckForMandatoryVendorIdCommandV3());
        return c;
    }

    public static FacilioChain getVendorContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new UpdateVendorContactAppPortalAccessCommandV3());
        c.addCommand(new UpdateScopingForPeopleCommandV3());
        return c;
    }

    public static FacilioChain getEmployeeBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CheckforPeopleDuplicationCommandV3());
        c.addCommand(new AddPeopleTypeForEmployeeCommandV3());
        return c;
    }

    public static FacilioChain getClientContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new CheckforPeopleDuplicationCommandV3());
        c.addCommand(new CheckForMandatoryClientIdCommandV3());
        return c;
    }

    public static FacilioChain getClientContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new UpdateClientAppPortalAccessCommandV3());
        c.addCommand(new UpdateScopingForPeopleCommandV3());
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
        c.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                .addCommand(new ExecuteAllWorkflowsCommand()));
        return c;

    }

    public static FacilioChain getAddPurchaseRequestAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PurchaseRequestTotalCostRollUpCommandV3()); // update purchase request total cost
        c.addCommand(new UpdateTransactionEventTypeV3Command());
        c.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                .addCommand(new ExecuteAllWorkflowsCommand(RuleType.TRANSACTION_RULE)));
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
        c.addCommand(new AddOrUpdateAnnouncementSharingInfoCommandV3());
        return c;

    }

    public static FacilioChain getCreateAnnouncementBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CloneAnnouncementCommandV3());
        c.addCommand(new SetLocalIdCommandV3());
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
        c.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                .addCommand(new ExecuteAllWorkflowsCommand(RuleType.TRANSACTION_RULE)));
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

    public static FacilioChain getUpdateJobPlanBeforeChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        // added this command to prefill/remove properties in JobPlanSection's additionInfo object
        chain.addCommand(new FillUpJobPlanSectionAdditionInfoObject());
        chain.addCommand(new ValidationForJobPlanCategory());
        return chain;
    }
    public static FacilioChain getUpdateJobPlanChain() {
        FacilioChain chain = getDefaultChain();
        // added this command to prefill/remove properties in JobPlanTask's additionInfo object
        chain.addCommand(new FillUpJobPlanTaskAdditionInfoObject());
        chain.addCommand(new AddJobPlanTasksCommand());
       // chain.addCommand(new AddJobPlanPMsInContextCommand());
        chain.addCommand(new ConstructUpdateCustomActivityCommandV3());
        chain.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.JOB_PLAN_ACTIVITY));

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
    public static FacilioChain getDeleteAssetDepartmentChain(){
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
                .addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE,RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE))
        );
        c.addCommand(getUpdateToolTypeQuantityRollupChainV3());
        return c;
    }
    public static FacilioChain getUpdateToolTypeQuantityRollupChainV3() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ToolTypeQuantityRollupCommandV3());
        return c;
    }
    public static FacilioChain getUpdateIsUnderStockedChainV3(){
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
        c.addCommand(new AutoAwardingPriceCommandV3());
        return c;
    }
    public static FacilioChain getRfqBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
       // c.addCommand(new CreateRfqFromPrCommandV3());
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new RfqBeforeCreateOrUpdateCommandV3());
        return c;
    }
    public static FacilioChain getConvertPrToRfqChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ConvertPrToRfqCommandV3());
        return c;
    }
    public static FacilioChain getConvertRfqToPoChain(){
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
    public static FacilioChain getCloneDashboardChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CloneDashboardCommand());
        return c;
    }
    public static FacilioChain getMoveToDashboardChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new MoveToDashboardCommand());
        return c;
    }

    public static FacilioChain getReadingImportAppChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddReadingImportAppDataCommand());
        return c;
    }

    public static FacilioChain updateReadingImportChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateReadingImportDataCommand());
        return c;
    }
    public static FacilioChain deleteReadingImportChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteReadingImportDataCommand());
        return c;
    }

    public static FacilioChain getCreateJobPlanChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddJobPlanTasksCommand());
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

    public static FacilioChain getDashboardDataChain(){
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

    public static FacilioChain getExecuteNow() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PMExecuteNowContextCommand());
        return c;
    }

    public static FacilioChain getPublishPM() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MarkPMAsActiveCommand());
        c.addCommand(new PublishPMCommand());
        return c;
    }

    public static FacilioChain getDeactivatePM() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MarkPMAsDeactivatedCommand());
        return c;
    }

    public static FacilioChain getAddColorPaletteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddColorPaletteCommand());
        return c;
    }
    public  static FacilioChain getDeleteColorPaletteChain(){
        FacilioChain c=getDefaultChain();
        c.addCommand(new DeleteColorPaletteCommand());
        return c;
    }

    public static FacilioChain getListColorPaletteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ListColorPaletteCommand());
        return c;
    }
    

    public static FacilioChain addVisitsAndInvitesForms() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddOrUpdateVisitorTypeFormCommand());
        return chain;
    }
}
