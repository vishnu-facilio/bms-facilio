package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.flowengine.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.utility.context.UtilityDisputeContext;
import org.joda.time.DateTimeField;

import java.util.*;

public class AddControlActionModule extends SignUpData {

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        FacilioModule controlActionModule = constructControlActionModule(modBean,orgId);
        FacilioModule controlActionFirstLevelApproval = constructControlActionFirstLevelApprovalModule(modBean,orgId,controlActionModule);
        FacilioModule controlActionSecondLevelApproval = constructControlActionSecondLevelApprovalModule(modBean,orgId,controlActionModule);
        addApprovalFieldToControlActionModule(modBean,orgId,controlActionModule,controlActionFirstLevelApproval,controlActionSecondLevelApproval);
        modBean.addSubModule(controlActionModule.getModuleId(),controlActionFirstLevelApproval.getModuleId());
        modBean.addSubModule(controlActionModule.getModuleId(),controlActionSecondLevelApproval.getModuleId());
        //addDefaultStateFlowForControlActionModule(controlActionModule);
        FacilioModule actionModule = constructActionModule(modBean,orgId,controlActionModule);
        FacilioModule commandModule = constructCommandModule(modBean,orgId,controlActionModule,actionModule);
        constructControlActionActivityModule(controlActionModule,modBean);
        constructControlActionAttachmentModule(controlActionModule,modBean);
        constructCommandActivityModule(commandModule,modBean);
        addSystemButtons();
    }
    private FacilioModule constructControlActionModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule controlActionModule = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,"Control Action","Control_Actions",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        controlActionModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        StringField name = SignupUtil.getStringField(controlActionModule,"name","Name","NAME", FacilioField.FieldDisplayType.TEXTBOX,
                true,false,true,true,orgId);
        fields.add(name);

        StringField desc = SignupUtil.getStringField(controlActionModule,"description","Description","DESCRIPTION", FacilioField.FieldDisplayType.TEXTBOX,
                false,false,true,false,orgId);
        fields.add(desc);

        SystemEnumField sourceType = SignupUtil.getSystemEnumField(controlActionModule,"controlActionSourceType","Source Type","SOURCE_TYPE","ControlActionSourceTypeEnum",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(sourceType);

        SystemEnumField calendarType = SignupUtil.getSystemEnumField(controlActionModule,"controlActionType","Type","CONTROL_ACTION_TYPE","ControlActionTypeEnum",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(calendarType);

        LookupField assetCategoryField = SignupUtil.getLookupField(controlActionModule, moduleBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY),
                "assetCategory", "Asset Category", "ASSET_CATEGORY_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, true, false, true, orgId);
        fields.add(assetCategoryField);

        FacilioField  scheduledActionDateTime = FieldFactory.getDefaultField("scheduledActionDateTime","Scheduled Action Date Time","SCHEDULED_ACTION_DATE_TIME", FieldType.DATE_TIME);
        fields.add(scheduledActionDateTime);

        FacilioField revertActionDateTime = FieldFactory.getDefaultField("revertActionDateTime","Revert Action Date Time","REVERT_ACTION_DATE_TIME",FieldType.DATE_TIME);
        fields.add(revertActionDateTime);

        NumberField siteCriteriaId = SignupUtil.getNumberField(controlActionModule,"siteCriteriaId","Site Criteria Id","SITE_CRITERIA_ID",
                FacilioField.FieldDisplayType.TEXTBOX,false,false,true,orgId);
        fields.add(siteCriteriaId);

        NumberField assetCriteriaId = SignupUtil.getNumberField(controlActionModule,"assetCriteriaId","Asset Criteria Id","ASSET_CRITERIA_ID",
                FacilioField.FieldDisplayType.TEXTBOX,false,false,true,orgId);
        fields.add(assetCriteriaId);


        NumberField controllerCriteriaId = SignupUtil.getNumberField(controlActionModule,"controllerCriteriaId","Controller Criteria Id","CONTROLLER_CRITERIA_ID",
                FacilioField.FieldDisplayType.TEXTBOX,false,false,true,orgId);
        fields.add(controllerCriteriaId);

        LookupField approvalState = SignupUtil.getLookupField(controlActionModule,moduleBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS),"approvalStatus","Approval Status",
                "APPROVAL_STATE",null, FacilioField.FieldDisplayType.SELECTBOX,false,false,true,orgId);
        fields.add(approvalState);

        NumberField approvalFlowId = SignupUtil.getNumberField(controlActionModule,"approvalFlowId","Approval Flow Id","APPROVAL_FLOW_ID",
                FacilioField.FieldDisplayType.TEXTBOX,false,false,true,orgId);
        fields.add(approvalFlowId);

        SystemEnumField controlActionStatus = SignupUtil.getSystemEnumField(controlActionModule,"controlActionStatus","Status","STATUS","ControlActionStatus",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(controlActionStatus);

        SystemEnumField scheduleActionStatus = SignupUtil.getSystemEnumField(controlActionModule,"scheduleActionStatus","Schedule Action Status","SCHEDULE_ACTION_STATUS","ControlActionStatus",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(scheduleActionStatus);

        SystemEnumField revertActionStatus = SignupUtil.getSystemEnumField(controlActionModule,"revertActionStatus","Revert Action Status","REVERT_ACTION_STATUS","ControlActionStatus",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(revertActionStatus);

        SystemEnumField controlActionExecutionType = SignupUtil.getSystemEnumField(controlActionModule,"controlActionExecutionType","Execution Type","CONTROL_ACTION_EXECUTION_TYPE",
                "ControlActionExecutionType", FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(controlActionExecutionType);

//        LookupField moduleStateField = (LookupField) FieldFactory.getField("moduleState", "Control Action Status", "MODULE_STATE", controlActionModule, FieldType.LOOKUP);
//        moduleStateField.setDefault(true);
//        moduleStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
//        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
//        fields.add(moduleStateField);
//
//        FacilioField stateFlowIdField = FieldFactory.getField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", controlActionModule, FieldType.NUMBER);
//        stateFlowIdField.setDefault(true);
//        stateFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
//        fields.add(stateFlowIdField);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        controlActionModule.setFields(fields);
        modules.add(controlActionModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return controlActionModule;
    }
    private void addDefaultStateFlowForControlActionModule(FacilioModule controlActionModule) throws Exception {
        FacilioStatus unPublished = getFacilioStatus(controlActionModule, "unPublished", "Un Published", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus published = getFacilioStatus(controlActionModule, "Published", "Published", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus waitingForFirstLevelApproval = getFacilioStatus(controlActionModule, "1stLevelPending", "Waiting For First level Approval", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus firstLevelApproved = getFacilioStatus(controlActionModule, "1stLevelApproved", "First Level Approved", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus waitingForSecondLevelApproval = getFacilioStatus(controlActionModule, "2ndLevelPending", "Waiting For Second Level Approval", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus secondLevelApproved = getFacilioStatus(controlActionModule, "2ndLevelApproved", "Second Level Approved", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus commandGenerated = getFacilioStatus(controlActionModule, "commandGenerated", "CommandGenerated", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus scheduleActionScheduled = getFacilioStatus(controlActionModule, "schActionScheduled", "Schedule Action Scheduled", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus scheduleActionInProgress = getFacilioStatus(controlActionModule, "schActionInProgress", "Schedule Action In Progress", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus scheduleActionSuccess = getFacilioStatus(controlActionModule, "schActionSuccess", "Schedule Action Success", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus scheduleActionFailed = getFacilioStatus(controlActionModule, "schActionFailed", "Schedule Action Failed", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus scheduleActionCompletedWithError = getFacilioStatus(controlActionModule, "schActPartial", "Scheduled Action Completed with Error", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus revertActionScheduled = getFacilioStatus(controlActionModule, "revActionScheduled", "Revert Action Scheduled", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus revertActionInProgress = getFacilioStatus(controlActionModule, "revActionInProgress", "Revert Action In Progress", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus revertActionSuccess = getFacilioStatus(controlActionModule, "revActionSuccess", "Revert Action Success", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus revertActionFailed = getFacilioStatus(controlActionModule, "revActionFailed", "Revert Action Failed", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus revertActionCompletedWithError = getFacilioStatus(controlActionModule, "revActionPartial", "Revert Action Completed with Error", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus rejected = getFacilioStatus(controlActionModule, "rejected", "Rejected", FacilioStatus.StatusType.OPEN, Boolean.FALSE);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(controlActionModule.getModuleId());
        stateFlowRuleContext.setModule(controlActionModule);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(unPublished.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

        Criteria unPublishedCriteria = new Criteria();
        unPublishedCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.UNPUBLISHED.getIndex()), EnumOperators.IS));

        Criteria publishedCriteria = new Criteria();
        publishedCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.PUBLISHED.getIndex()), EnumOperators.IS));

        Criteria waitingForFirstLevelApprovalCriteria = new Criteria();
        waitingForFirstLevelApprovalCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.WAITING_FOR_FIRST_LEVEL_APPROVAL.getIndex()), EnumOperators.IS));

        Criteria firstLevelApprovedCriteria = new Criteria();
        firstLevelApprovedCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.FIRST_LEVEL_APPROVED.getIndex()), EnumOperators.IS));

        Criteria waitingForSecondLevelApprovalCriteria = new Criteria();
        waitingForSecondLevelApprovalCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.WAITING_FOR_SECOND_LEVEL_APPROVAL.getIndex()), EnumOperators.IS));

        Criteria secondLevelApprovedCriteria = new Criteria();
        secondLevelApprovedCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.SECOND_LEVEL_APPROVED.getIndex()), EnumOperators.IS));

        Criteria commandGeneratedCriteria = new Criteria();
        commandGeneratedCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.COMMAND_GENERATED.getIndex()), EnumOperators.IS));

        Criteria scheduleActionScheduledCriteria = new Criteria();
        scheduleActionScheduledCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SCHEDULED.getIndex()), EnumOperators.IS));

        Criteria scheduleActionInProgressCriteria = new Criteria();
        scheduleActionInProgressCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_IN_PROGRESS.getIndex()), EnumOperators.IS));

        Criteria scheduleActionSuccessCriteria = new Criteria();
        scheduleActionSuccessCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SUCCESS.getIndex()), EnumOperators.IS));

        Criteria scheduleActionFailedCriteria = new Criteria();
        scheduleActionFailedCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_FAILED.getIndex()), EnumOperators.IS));

        Criteria scheduleActionCompletedWithErrorCriteria = new Criteria();
        scheduleActionCompletedWithErrorCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_COMPLETED_WITH_ERROR.getIndex()), EnumOperators.IS));

        Criteria revertActionScheduledCriteria = new Criteria();
        revertActionScheduledCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.REVERT_ACTION_SCHEDULED.getIndex()), EnumOperators.IS));

        Criteria revertActionInProgressCriteria = new Criteria();
        revertActionInProgressCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.REVERT_ACTION_IN_PROGRESS.getIndex()), EnumOperators.IS));

        Criteria revertActionSuccessCriteria = new Criteria();
        revertActionSuccessCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.REVERT_ACTION_SUCCESS.getIndex()), EnumOperators.IS));

        Criteria revertActionFailedCriteria = new Criteria();
        revertActionFailedCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.REVERT_ACTION_FAILED.getIndex()), EnumOperators.IS));

        Criteria revertActionCompletedWithErrorCriteria = new Criteria();
        revertActionCompletedWithErrorCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.REVERT_ACTION_COMPLETED_WITH_ERROR.getIndex()), EnumOperators.IS));

        Criteria rejectedCriteria = new Criteria();
        rejectedCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "controlActionStatus", String.valueOf(V3ControlActionContext.ControlActionStatus.REJECTED.getIndex()), EnumOperators.IS));

        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Publish", unPublished, published, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,publishedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "First Level Approval Pending", published, waitingForFirstLevelApproval, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,waitingForFirstLevelApprovalCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "First level Approved", waitingForFirstLevelApproval, firstLevelApproved, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,firstLevelApprovedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Second Level Approval Pending", firstLevelApproved, waitingForSecondLevelApproval, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,waitingForSecondLevelApprovalCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Second Level Approved", waitingForSecondLevelApproval, secondLevelApproved, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,secondLevelApprovedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Command Generation", secondLevelApproved, commandGenerated, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,commandGeneratedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Scheduling Schedule Action", commandGenerated, scheduleActionScheduled, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,scheduleActionScheduledCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Schedule Action In Progress", scheduleActionScheduled, scheduleActionInProgress, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,scheduleActionInProgressCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Schedule Action Success", scheduleActionInProgress, scheduleActionSuccess, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,scheduleActionSuccessCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Schedule Action Failed", scheduleActionInProgress, scheduleActionFailed, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,scheduleActionFailedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Schedule Action With Error", scheduleActionInProgress, scheduleActionCompletedWithError, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,scheduleActionCompletedWithErrorCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Scheduling Revert Action", scheduleActionSuccess, revertActionScheduled, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,revertActionScheduledCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Scheduling Revert Action", scheduleActionFailed, revertActionScheduled, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,revertActionScheduledCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Scheduling Revert Action", scheduleActionCompletedWithError, revertActionScheduled, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,revertActionScheduledCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Revert Action In progress", revertActionScheduled, revertActionInProgress, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,revertActionInProgressCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Revert Action Success", revertActionInProgress, revertActionSuccess, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,revertActionSuccessCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Revert Action Failed", revertActionInProgress, revertActionFailed, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,revertActionFailedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Revert Action With Error", revertActionInProgress, revertActionCompletedWithError, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,revertActionCompletedWithErrorCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Reject", waitingForFirstLevelApproval, rejected, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,rejectedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Reject", waitingForSecondLevelApproval, rejected, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,rejectedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Un Publish", rejected, unPublished, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,unPublishedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Un Publish", published, unPublished, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,unPublishedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Command Generation", published, commandGenerated, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,commandGeneratedCriteria,null);
        addStateflowTransitionContext(controlActionModule, stateFlowRuleContext, "Command Generation", firstLevelApproved, commandGenerated, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,commandGeneratedCriteria,null);

    }
    private static void addSystemButtons() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SystemButtonRuleContext unPublishControlAction = new SystemButtonRuleContext();
        unPublishControlAction.setName("Un Publish");
        unPublishControlAction.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        unPublishControlAction.setIdentifier("unPublish");
        unPublishControlAction.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria unPublishCriteria = new Criteria();
        unPublishCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("controlActionStatus"),String.valueOf(V3ControlActionContext.ControlActionStatus.PUBLISHED.getIndex()), EnumOperators.IS));
        unPublishCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("controlActionStatus"),String.valueOf(V3ControlActionContext.ControlActionStatus.REJECTED.getIndex()), EnumOperators.IS));
        unPublishControlAction.setCriteria(unPublishCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,unPublishControlAction);

        SystemButtonRuleContext publishControlAction = new SystemButtonRuleContext();
        publishControlAction.setName("Publish");
        publishControlAction.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        publishControlAction.setIdentifier("publish");
        publishControlAction.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria publishCriteria = new Criteria();
        publishCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("controlActionStatus"),String.valueOf(V3ControlActionContext.ControlActionStatus.UNPUBLISHED.getIndex()), EnumOperators.IS));
        publishControlAction.setCriteria(publishCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,publishControlAction);

        SystemButtonRuleContext editRecord = new SystemButtonRuleContext();
        editRecord.setName("Edit");
        editRecord.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editRecord.setIdentifier("edit");
        editRecord.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        editRecord.setPermission(AccountConstants.ModulePermission.UPDATE.name());
//        editRecord.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,editRecord);

    }

    private FacilioStatus getFacilioStatus(FacilioModule module, String status, String displayName, FacilioStatus.StatusType status1, Boolean timerEnabled) throws Exception {

        FacilioStatus statusObj = new FacilioStatus();
        statusObj.setStatus(status);
        statusObj.setDisplayName(displayName);
        statusObj.setTypeCode(status1.getIntVal());
        statusObj.setTimerEnabled(timerEnabled);
        TicketAPI.addStatus(statusObj, module);

        return statusObj;
    }
    private StateflowTransitionContext addStateflowTransitionContext(FacilioModule module, StateFlowRuleContext parentStateFlow, String name, FacilioStatus fromStatus, FacilioStatus toStatus, AbstractStateTransitionRuleContext.TransitionType transitionType, Criteria criteria, List<ActionContext> actions) throws Exception {

        StateflowTransitionContext stateFlowTransitionContext = new StateflowTransitionContext();
        stateFlowTransitionContext.setName(name);
        stateFlowTransitionContext.setModule(module);
        stateFlowTransitionContext.setModuleId(module.getModuleId());
        stateFlowTransitionContext.setActivityType(EventType.STATE_TRANSITION);
        stateFlowTransitionContext.setExecutionOrder(1);
        stateFlowTransitionContext.setButtonType(1);
        stateFlowTransitionContext.setFromStateId(fromStatus.getId());
        stateFlowTransitionContext.setToStateId(toStatus.getId());
        stateFlowTransitionContext.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        stateFlowTransitionContext.setType(transitionType);
        stateFlowTransitionContext.setStateFlowId(parentStateFlow.getId());
        stateFlowTransitionContext.setCriteria(criteria);

        WorkflowRuleAPI.addWorkflowRule(stateFlowTransitionContext);

        if (actions != null && !actions.isEmpty()) {
            actions = ActionAPI.addActions(actions, stateFlowTransitionContext);
            if(stateFlowTransitionContext != null) {
                ActionAPI.addWorkflowRuleActionRel(stateFlowTransitionContext.getId(), actions);
                stateFlowTransitionContext.setActions(actions);
            }
        }

        return stateFlowTransitionContext;
    }
    private FacilioModule constructActionModule(ModuleBean moduleBean, long orgId, FacilioModule controlActionModule) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule actionsModule = new FacilioModule(FacilioConstants.Control_Action.ACTION_MODULE_NAME,"Action","Actions",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        controlActionModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        LookupField controlAction = SignupUtil.getLookupField(actionsModule,controlActionModule,"controlAction","Control Action","CONTROL_ACTION_ID",null,
                FacilioField.FieldDisplayType.TEXTBOX,false,false,true,orgId);
        fields.add(controlAction);

        SystemEnumField actionVariableType = SignupUtil.getSystemEnumField(actionsModule,"actionVariableType","Variable Type","VARIABLE_TYPE","ActionVariableTypeEnum",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(actionVariableType);

        NumberField readingFieldId = SignupUtil.getNumberField(actionsModule,"readingFieldId","Reading","READING_FIELD_ID",
                FacilioField.FieldDisplayType.SELECTBOX, true,false,true,orgId);
        fields.add(readingFieldId);

        NumberField readingFieldDataType = SignupUtil.getNumberField(actionsModule,"readingFieldDataType","Data Type","READING_FIELD_DATE_TYPE",
                FacilioField.FieldDisplayType.TEXTBOX,true,true,true,orgId);
        fields.add(readingFieldDataType);

        SystemEnumField scheduledActionOperatorType = SignupUtil.getSystemEnumField(actionsModule,"scheduledActionOperatorType","Schedule Action Operator","SCHEDULE_ACTION_OPERATOR_TYPE","ActionOperatorTypeEnum",
                FacilioField.FieldDisplayType.SELECTBOX,true,false,true,orgId);
        fields.add(scheduledActionOperatorType);

        StringField scheduleActionValue = SignupUtil.getStringField(actionsModule,"scheduleActionValue","Schedule Action Value","SCHEDULE_ACTION_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                true,false,true,true,orgId);
        fields.add(scheduleActionValue);

        SystemEnumField revertActionOperatorType = SignupUtil.getSystemEnumField(actionsModule,"revertActionOperatorType","Revert Action Operator","REVERT_ACTION_OPERATOR_TYPE","ActionOperatorTypeEnum",
                FacilioField.FieldDisplayType.SELECTBOX,true,false,true,orgId);
        fields.add(revertActionOperatorType);

        StringField revertActionValue = SignupUtil.getStringField(actionsModule,"revertActionValue","Revert Action Value","REVERT_ACTION_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                true,false,true,true,orgId);
        fields.add(revertActionValue);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        actionsModule.setFields(fields);
        modules.add(actionsModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return actionsModule;
    }
    private FacilioModule constructControlActionFirstLevelApprovalModule(ModuleBean moduleBean,long orgId,FacilioModule controlAction) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule controlActionFirstLevelApprovalModule = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME,"First Level Approval",
                "Control_Action_First_Level_Approval", FacilioModule.ModuleType.SUB_ENTITY,true);
        controlActionFirstLevelApprovalModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();
        LookupField leftField = SignupUtil.getLookupField(controlActionFirstLevelApprovalModule, controlAction, "left", "Control Action", "LEFT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(leftField);

        LookupField rightField = SignupUtil.getLookupField(controlActionFirstLevelApprovalModule, moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),
                "right", "Approver", "RIGHT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(rightField);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        controlActionFirstLevelApprovalModule.setFields(fields);

        modules.add(controlActionFirstLevelApprovalModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return controlActionFirstLevelApprovalModule;
    }
    private FacilioModule constructControlActionSecondLevelApprovalModule(ModuleBean moduleBean,long orgId,FacilioModule controlAction) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule controlActionSecondLevelApprovalModule = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_SECOND_LEVEL_APPROVAL_MODULE_NAME,"Second Level Approval",
                "Control_Action_Second_Level_Approval", FacilioModule.ModuleType.SUB_ENTITY,true);
        controlActionSecondLevelApprovalModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();
        LookupField leftField = SignupUtil.getLookupField(controlActionSecondLevelApprovalModule, controlAction, "left", "Control Action", "LEFT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(leftField);

        LookupField rightField = SignupUtil.getLookupField(controlActionSecondLevelApprovalModule, moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),
                "right", "Approver", "RIGHT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(rightField);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        controlActionSecondLevelApprovalModule.setFields(fields);

        modules.add(controlActionSecondLevelApprovalModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return controlActionSecondLevelApprovalModule;
    }
    private void addApprovalFieldToControlActionModule(ModuleBean moduleBean, long orgId, FacilioModule controlAction,
                                                          FacilioModule controlActionFirstLevelApprovalModule, FacilioModule controlActionSecondLevelApprovalModule) throws Exception {
        /* sites Field */
        MultiLookupField  firstLevelApproval = FieldFactory.getDefaultField("firstLevelApproval", null, null, FieldType.MULTI_LOOKUP);
        firstLevelApproval.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        firstLevelApproval.setRequired(false);
        firstLevelApproval.setDisabled(false);
        firstLevelApproval.setDefault(true);
        firstLevelApproval.setMainField(false);
        firstLevelApproval.setOrgId(orgId);
        firstLevelApproval.setModule(controlAction);
        firstLevelApproval.setLookupModuleId(controlActionFirstLevelApprovalModule.getModuleId());
        firstLevelApproval.setRelModuleId(controlActionFirstLevelApprovalModule.getModuleId());
        firstLevelApproval.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        moduleBean.addField(firstLevelApproval);

        MultiLookupField  secondLevelApproval = FieldFactory.getDefaultField("secondLevelApproval", null, null, FieldType.MULTI_LOOKUP);
        secondLevelApproval.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        secondLevelApproval.setRequired(false);
        secondLevelApproval.setDisabled(false);
        secondLevelApproval.setDefault(true);
        secondLevelApproval.setMainField(false);
        secondLevelApproval.setOrgId(orgId);
        secondLevelApproval.setModule(controlAction);
        secondLevelApproval.setLookupModuleId(controlActionSecondLevelApprovalModule.getModuleId());
        secondLevelApproval.setRelModuleId(controlActionSecondLevelApprovalModule.getModuleId());
        secondLevelApproval.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        moduleBean.addField(secondLevelApproval);


    }

    private FacilioModule constructCommandModule(ModuleBean moduleBean,long orgId, FacilioModule controlActionModule, FacilioModule actionModule) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule commandModule = new FacilioModule(FacilioConstants.Control_Action.COMMAND_MODULE_NAME,"Command","Commands",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        controlActionModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        StringField name = SignupUtil.getStringField(commandModule,"name","Name","NAME", FacilioField.FieldDisplayType.TEXTBOX,
                true,false,true,true,orgId);
        fields.add(name);

        LookupField controlAction = SignupUtil.getLookupField(commandModule,controlActionModule,"controlAction","Control Action","CONTROL_ACTION_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,true,orgId);
        fields.add(controlAction);

        LookupField action = SignupUtil.getLookupField(commandModule,actionModule,"action","Action","ACTION_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,true,orgId);
        fields.add(action);

        LookupField site = SignupUtil.getLookupField(commandModule,moduleBean.getModule(FacilioConstants.ContextNames.SITE),"site","Site","SITE_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,true,orgId);
        fields.add(site);

        LookupField asset = SignupUtil.getLookupField(commandModule,moduleBean.getModule(FacilioConstants.ContextNames.ASSET),"asset","Asset","ASSET_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,true,orgId);
        fields.add(asset);

        LookupField controller = SignupUtil.getLookupField(commandModule,moduleBean.getModule(FacilioConstants.ContextNames.CONTROLLER),"controller","Controller","CONTROLLER_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,true,orgId);
        fields.add(controller);

        NumberField fieldId = SignupUtil.getNumberField(commandModule,"fieldId","Field","FIELD_ID", FacilioField.FieldDisplayType.TEXTBOX,
                true,true,true,orgId);
        fields.add(fieldId);

        FacilioField actionTime = FieldFactory.getDefaultField("actionTime","Action Time","ACTION_TIME",FieldType.DATE_TIME);
        fields.add(actionTime);

        StringField setValue = SignupUtil.getStringField(commandModule,"setValue","Set Value","SET_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                false,false,true,false,orgId);
        fields.add(setValue);

        StringField afterValue = SignupUtil.getStringField(commandModule,"afterValue","After Value","AFTER_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                false,false,true,false,orgId);
        fields.add(afterValue);

        StringField previousValue = SignupUtil.getStringField(commandModule,"previousValue","Previous Value","PREVIOUS_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                false,false,true,false,orgId);
        fields.add(previousValue);

        SystemEnumField controlActionCommandStatus = SignupUtil.getSystemEnumField(commandModule,"controlActionCommandStatus","Status","STATUS","ControlActionCommandStatus",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(controlActionCommandStatus);

        StringField errorMsg = SignupUtil.getStringField(commandModule,"errorMsg","Error Msg","ERROR_MSG",
                FacilioField.FieldDisplayType.TEXTAREA,false,false,true,false,orgId);
        fields.add(errorMsg);

        FacilioField previousValueCapturedTime = FieldFactory.getDefaultField("previousValueCapturedTime","Previous Value Captured Time","PREVIOUS_VALUE_CAPTURED_TIME",FieldType.DATE_TIME);
        fields.add(previousValueCapturedTime);

        SystemEnumField commandActionType = SignupUtil.getSystemEnumField(commandModule,"commandActionType","Action Type","ACTION_TYPE","CommandActionType",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(commandActionType);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));


        commandModule.setFields(fields);
        modules.add(commandModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return commandModule;
    }
    private void constructControlActionActivityModule(FacilioModule controlActionModule, ModuleBean moduleBean) throws Exception{
        FacilioModule module = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_ACTIVITY_MODULE_NAME,
                "Control Action Activity",
                "Control_Action_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        List<FacilioField> fields = new ArrayList<>();
        NumberField parentId = (NumberField) FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);
        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);
        fields.add(timefield);
        NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);
        LookupField doneBy = (LookupField) FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_POPUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);
        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA);
        fields.add(info);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));


        module.setFields(fields);
        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();
        moduleBean.addSubModule(controlActionModule.getModuleId(), module.getModuleId());
    }
    private FacilioModule constructControlActionAttachmentModule(FacilioModule controlActionModule, ModuleBean moduleBean) throws Exception{
        FacilioModule module = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_ATTACHMENT_MODULE_NAME,
                "Control Action Attachments", "Control_Action_Attachments",
                FacilioModule.ModuleType.ATTACHMENTS);

        List<FacilioField> fields = new ArrayList<>();

        NumberField fileId = new NumberField(module, "fileId", "File ID", FacilioField.FieldDisplayType.NUMBER, "FILE_ID", FieldType.NUMBER, true, false, true, true);
        fields.add(fileId);

        NumberField parentId = new NumberField(module, "parentId", "Parent", FacilioField.FieldDisplayType.NUMBER, "PARENT_CONTROL_ACTION", FieldType.NUMBER, true, false, true, null);
        fields.add(parentId);

        NumberField createdTime = new NumberField(module, "createdTime", "Created Time", FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.NUMBER, true, false, true, null);
        fields.add(createdTime);

        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();

        moduleBean.addSubModule(controlActionModule.getModuleId(), module.getModuleId());

        return module;
    }
    private void constructCommandActivityModule(FacilioModule commandModule, ModuleBean moduleBean) throws Exception{
        FacilioModule module = new FacilioModule(FacilioConstants.Control_Action.COMMAND_ACTIVITY_MODULE_NAME,
                "Command Activity",
                "Command_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        List<FacilioField> fields = new ArrayList<>();
        NumberField parentId = (NumberField) FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);
        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);
        fields.add(timefield);
        NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);
        LookupField doneBy = (LookupField) FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_POPUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);
        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA);
        fields.add(info);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));


        module.setFields(fields);
        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();
        moduleBean.addSubModule(commandModule.getModuleId(), module.getModuleId());
    }
}
