package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
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
import com.facilio.bmsconsoleV3.commands.facility.CancelBookingCommand;
import com.facilio.bmsconsoleV3.commands.facility.CreatePaymentRecordForBookingCommand;
import com.facilio.bmsconsoleV3.commands.facility.SetCanEditForBookingCommand;
import com.facilio.bmsconsoleV3.commands.facility.ValidateCancelBookingCommandV3;
import com.facilio.bmsconsoleV3.commands.facility.ValidateFacilityBookingCommandV3;
import com.facilio.bmsconsoleV3.commands.floorplan.AddDeskCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.AddFloorplanObjectsCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.AddMarkedZonesCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.AddMarkerCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.SerializeCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.UpdateDeskCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.UpdateMarkerCommand;
import com.facilio.bmsconsoleV3.commands.facility.*;
import com.facilio.bmsconsoleV3.commands.jobplan.AddJobPlanPMsInContextCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.AddJobPlanTasksCommand;
import com.facilio.bmsconsoleV3.commands.people.CheckforPeopleDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.people.UpdatePeoplePrimaryContactCommandV3;
import com.facilio.bmsconsoleV3.commands.purchaseorder.POAfterCreateOrEditV3Command;
import com.facilio.bmsconsoleV3.commands.purchaseorder.POBeforeCreateOrEditV3Command;
import com.facilio.bmsconsoleV3.commands.purchaseorder.UpdateIsPoCreatedCommand;
import com.facilio.bmsconsoleV3.commands.purchaserequest.PreFillAddPurchaseRequestCommand;
import com.facilio.bmsconsoleV3.commands.purchaserequest.PurchaseRequestTotalCostRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.InsertQuotationLineItemsAndActivitiesCommand;
import com.facilio.bmsconsoleV3.commands.quotation.QuotationValidationAndCostCalculationCommand;
import com.facilio.bmsconsoleV3.commands.quotation.ReviseQuotationCommand;
import com.facilio.bmsconsoleV3.commands.quotation.UpdateQuotationParentIdCommand;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantSpaceRelationCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantUserCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.CheckForMandatoryTenantIdCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.UpdateTenantAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.usernotification.*;
import com.facilio.bmsconsoleV3.commands.vendor.AddInsuranceVendorRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddVendorContactsCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorcontact.CheckForMandatoryVendorIdCommandV3;
import com.facilio.bmsconsoleV3.commands.vendorcontact.UpdateVendorContactAppPortalAccessCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.AddOrUpdateLocationForVisitorCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.CheckForVisitorDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.*;
import com.facilio.bmsconsoleV3.commands.visitorlogging.*;
import com.facilio.bmsconsoleV3.commands.workorder.*;
import com.facilio.bmsconsoleV3.commands.workpermit.*;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.trigger.command.*;
import org.apache.commons.chain.Context;

import java.util.Collections;

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

        c.addCommand(new PutOldVisitRecordsInVisitorLogContextCommandV3()); //check-in related
        c.addCommand(new AddNewVisitorWhileBaseVisitCommandV3());
        c.addCommand(new PreFillVisitorLogCommandV3()); //check-in related
        c.addCommand(new AddOrUpdateVisitorFromBaseVisitCommandV3());
        c.addCommand(new CheckForWatchListRecordBaseVisitCommandV3());

        return c;
    }

    public static FacilioChain getVisitorLogAfterSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new UpdateVisitorLogArrivedStateCommandV3()); //check-in related
        c.addCommand(new ChangeVisitorLogStateCommandV3()); //check-in related
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new AddNdaForVisitorLogModuleCommandV3()) //check-in related
                .addCommand(new GenerateQrInviteUrlForBaseVisitCommandV3())
                .addCommand(new VisitorFaceRecognitionForBaseVisitCommandV3()));

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
                .addCommand(new VisitorFaceRecognitionCommandV3())
        );
        return c;
    }

    public static FacilioChain getQuotationAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateQuotationParentIdCommand());
        c.addCommand(new InsertQuotationLineItemsAndActivitiesCommand());
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
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteSLAWorkFlowsCommand());
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_RULE, WorkflowRuleContext.RuleType.CHILD_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_REJECT_RULE));

        if (sendNotification) {
            if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 218L) {
                c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE));
            } else {
                c.addCommand(new ForkChainToInstantJobCommand()
                        .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE)));
            }
        }
        return c;
    }

    public static FacilioChain getWorkorderAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillContextAfterWorkorderAddCommandV3());
        c.addCommand(new AddWorkorderHazardsFromSafetyPlanCommandV3());
        c.addCommand(new GetRecordIdsFromRecordMapCommandV3());
        c.addCommand(new AddTicketActivityCommandV3());
        //c.addCommand(getAddTasksChainV3());
        c.addCommand(new AddPrerequisiteApproversCommandV3());
        c.addCommand(new FacilioCommand() {
            @Override
            public boolean executeCommand(Context context) throws Exception {
                context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(context.get(FacilioConstants.ContextNames.WORK_ORDER)));
                return false;
            }
        });
        c.addCommand(getWorkOrderWorkflowsChainV3(true));
        c.addCommand(new AddOrUpdateSLABreachJobCommandV3(true));
        //to be removed once all attachments are handled as sub module
        c.addCommand(new UpdateTicketAttachmentsOldParentIdCommandV3());
        c.addCommand(new AddActivitiesCommandV3());

        return c;
    }

    public static FacilioChain getWorkorderBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetRecordIdsFromRecordMapCommandV3());
        c.addCommand(new FetchOldWorkordersCommandV3());
        c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
        c.addCommand(new VerifyApprovalCommandV3());
        c.addCommand(new UpdateEventListForStateFlowCommandV3());
        c.addCommand(new UpdateWorkorderFieldsForUpdateCommandV3());
        c.addCommand(new BackwardCompatibleStateFlowUpdateCommandV3());
        c.addCommand(new ToVerifyStateFlowtransitionForStartCommandV3());


        return c;
    }

    public static FacilioChain getWorkorderAfterUpdateChain(boolean sendNotification) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillContextAfterWorkorderUpdateCommandV3());
        c.addCommand(new VerifyQrCommand());
        c.addCommand(new SendNotificationCommandV3());
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_RULE, WorkflowRuleContext.RuleType.CHILD_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_REJECT_RULE));
        c.addCommand(new AddOrUpdateSLABreachJobCommandV3(false));
        c.addCommand(new ExecuteSLACommitmentWorkflowsCommand());
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE))
                .addCommand(new ClearAlarmOnWOCloseCommand())
                .addCommand(new ExecuteTaskFailureActionCommand())
        );
        c.addCommand(new ConstructTicketNotesCommand());
        c.addCommand(TransactionChainFactory.getAddNotesChain());
        c.addCommand(new AddActivitiesCommand());

        return c;
    }


    public static FacilioChain getTenantContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CheckforPeopleDuplicationCommandV3());
        c.addCommand(new CheckForMandatoryTenantIdCommandV3());
        return c;
    }

    public static FacilioChain getTenantContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new UpdateTenantAppPortalAccessCommandV3());
        return c;
    }

    public static FacilioChain getVendorContactBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CheckforPeopleDuplicationCommandV3());
        c.addCommand(new CheckForMandatoryVendorIdCommandV3());
        return c;
    }

    public static FacilioChain getVendorContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new UpdateVendorContactAppPortalAccessCommandV3());
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
        c.addCommand(new CheckforPeopleDuplicationCommandV3());
        c.addCommand(new CheckForMandatoryClientIdCommandV3());
        return c;
    }

    public static FacilioChain getClientContactAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePeoplePrimaryContactCommandV3());
        c.addCommand(new UpdateClientAppPortalAccessCommandV3());
        return c;
    }
    
    public static FacilioChain getItemOrToolTypesAfterSaveChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand()); 

		return c;
	}
	
	public static FacilioChain getUpdateItemTypesAfterSaveChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static FacilioChain getAddClientsAfterSaveChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateClientIdInSiteCommandV3());
		c.addCommand(new AddClientUserCommandV3());
		return c;
	}
	
	public static FacilioChain getServiceRequestAfterSaveChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateAttachmentsParentIdCommandV3());
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
	
	public static FacilioChain getTenantUnitAfterUpdateChain() {
		FacilioChain c = getDefaultChain();
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
	
	public static FacilioChain getUpdateAnnouncementAfterSaveChain() {
        FacilioChain c = getDefaultChain();
	    c.addCommand(new UpdateAttachmentsParentIdCommandV3());
        c.addCommand(new PublishAnnouncementCommandV3());
        c.addCommand(new CancelParentChildAnnouncementsCommandV3());
        return c;

    }
	
	public static FacilioChain getAddPurchaseRequestAfterSaveChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new PurchaseRequestTotalCostRollUpCommandV3()); //update purchase request total cost
		return c;
	}
    public static FacilioChain getAddPurchaseRequestBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new PreFillAddPurchaseRequestCommand());
        return c;
    }

    public static FacilioChain addRecords () {
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
        c.addCommand(new SetLocalIdCommandV3());
       // c.addCommand(new CheckForSharingInfoCommandV3());
        c.addCommand(new AddOrUpdateAnnouncementSharingInfoCommandV3());
        return c;

    }
    public static FacilioChain getNotificationSeenUpdateChain () {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateSeenNotificationCommandV3());
        return c;
    }

    public static FacilioChain getUserNotifactionBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CheckUpdateMappingSeenCommand());
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
//        c.addCommand(new AddOrUpdateTriggerInclExclCommand());
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
        //c.addCommand(new ValidateCanEditBookingCommand());
        c.addCommand(new ValidateFacilityBookingCommandForEditV3());
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

    public static FacilioChain getUpdateJobPlanChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddJobPlanTasksCommand());
        chain.addCommand(new AddJobPlanPMsInContextCommand());
        return chain;
    }

    public static FacilioChain addfloorplanObjectsChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddDeskCommand());
        chain.addCommand(new AddMarkerCommand());
        chain.addCommand(new AddMarkedZonesCommand());
        return chain;
    }

    public static FacilioChain addMarkersAndModulesCommand() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddDeskCommand());
        chain.addCommand(new AddMarkerCommand());
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
    
    public static FacilioChain getAddEmailConversationThreadingFromEmailToModuleDataChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ConstructEmailConversationThreadingCommand());
        chain.addCommand(new AddEmailConversationThreadingCommand());
        return chain;
    }
    
    public static FacilioChain getAddEmailConversationThreadingAfterSaveChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SendEmailForEmailConversationThreadingCommand());
        return chain;
    }

}
