package com.facilio.bmsconsoleV3.signup.inspection;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext.TransitionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

import java.util.ArrayList;
import java.util.List;

public class AddInspectionModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        List<FacilioModule> modules = new ArrayList<>();
        
        modules.add(constructInspectionCategory());
        modules.add(constructInspectionPriority());
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        
        
        modules = new ArrayList<>();
        
        FacilioModule inspection = constructInspection(modBean);
        modules.add(inspection);
        FacilioModule inspectionResponseModule = constructInspectionResponse(modBean, inspection);
        modules.add(inspectionResponseModule);
        
        
        addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        
        List<FacilioModule> modules1 = new ArrayList<>();
        
        modules1.add(constructInspectionTriggers(modBean, inspection));
        
        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules1);
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();
        
        modules1 = new ArrayList<>();
        
        modules1.add(constructInspectionTriggersInclExcl(modBean));
        
        addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules1);
        addModuleChain1.execute();
        
        addDefaultStateFlow(inspectionResponseModule);
    }


	public FacilioModule constructInspectionTriggersInclExcl(ModuleBean modBean) throws Exception {

		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL,
                "Inspection Triggers Incl Excl",
                "Inspection_Trigger_Include_Exclude_Resource",
                FacilioModule.ModuleType.SUB_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();
        
        LookupField template = (LookupField) FieldFactory.getDefaultField("inspectionTemplate", "Inspection Template", "INSPECTION_ID", FieldType.LOOKUP);
        template.setLookupModule(modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE));
        fields.add(template);
        
        LookupField trigger = (LookupField) FieldFactory.getDefaultField("inspectionTrigger", "Inspection Trigger", "TRIGGER_ID", FieldType.LOOKUP);
        trigger.setLookupModule(modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER));
        fields.add(trigger);
        
        LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Resource", "RESOURCE_ID", FieldType.LOOKUP);
        resource.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        fields.add(resource);
        
        BooleanField isIncl = (BooleanField) FieldFactory.getDefaultField("isInclude", "Is Include", "IS_INCLUDE", FieldType.BOOLEAN);
        fields.add(isIncl);
        
        module.setFields(fields);
        
        return module;
	}


	public FacilioModule constructInspectionPriority() {
		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_PRIORITY,
                "Inspection Priority",
                "Inspection_Priority",
                FacilioModule.ModuleType.BASE_ENTITY
                );

		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("priority", "Priority", "PRIORITY", FieldType.STRING, true);
		fields.add(nameField);
		
		fields.add((FacilioField) FieldFactory.getDefaultField("displayName", "Display Name", "DISPLAY_NAME", FieldType.STRING));
		
		fields.add((FacilioField) FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));
		
		fields.add((FacilioField) FieldFactory.getDefaultField("colour", "Colour", "COLOUR", FieldType.STRING));
		
		fields.add((NumberField) FieldFactory.getDefaultField("sequenceNumber", "Sequence", "SEQUENCE_NUMBER", FieldType.NUMBER));
		
		fields.add((BooleanField) FieldFactory.getDefaultField("isDefault", "Is Default", "ISDEFAULT", FieldType.BOOLEAN));
		
		module.setFields(fields);
		return module;
	}


	public FacilioModule constructInspectionCategory() throws Exception {
		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_CATEGORY,
                "Inspection Category",
                "Inspection_Category",
                FacilioModule.ModuleType.BASE_ENTITY
                );

		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
		fields.add(nameField);
		
		fields.add((FacilioField) FieldFactory.getDefaultField("displayName", "Display Name", "DISPLAY_NAME", FieldType.STRING));
		
		fields.add((FacilioField) FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));
		
		module.setFields(fields);
		return module;
	}


	private FacilioModule constructInspection(ModuleBean modBean) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE,
                                                "Inspection Templates",
                                                "Inspection_Templates",
                                                FacilioModule.ModuleType.Q_AND_A,
                                                modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE)
                                                );

        List<FacilioField> fields = new ArrayList<>();
        
        LookupField siteField = (LookupField) FieldFactory.getDefaultField("site", "Site", "SITE_ID", FieldType.LOOKUP);
        siteField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        fields.add(siteField);
        
        SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("creationType", "Creation Type", "CREATION_TYPE", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("InspectionCreationType");
        
        fields.add(creationType);
        
        SystemEnumField assignmentType = (SystemEnumField) FieldFactory.getDefaultField("assignmentType", "Assigment Type", "ASSIGNMENT_TYPE", FieldType.SYSTEM_ENUM);
        assignmentType.setEnumName("MultiResourceAssignmentType");
        
        fields.add(assignmentType);
        
        LookupField baseSpace = (LookupField) FieldFactory.getDefaultField("baseSpace", "Base Space", "BASE_SPACE", FieldType.LOOKUP);
        baseSpace.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE));
        fields.add(baseSpace);
        
        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);
        
        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);
        
        fields.addAll(getInspectionCommonFieldList(modBean));
        
        module.setFields(fields);
        return module;
    }
	
	private FacilioModule constructInspectionTriggers(ModuleBean modBean, FacilioModule inspection) throws Exception {
		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_TRIGGER,
                "Inspection Triggers",
                "Inspection_Triggers",
                FacilioModule.ModuleType.SUB_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();
        
        FacilioField nameField = FieldFactory.getNameField(module);
        fields.add(nameField);
        
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(inspection);
        fields.add(parentField);
        
        NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Trigger Type", "TRIGGER_TYPE", FieldType.NUMBER);
        fields.add(type);
        
        NumberField scheduleID = (NumberField) FieldFactory.getDefaultField("scheduleId", "Schedule", "SCHEDULE_ID", FieldType.NUMBER);
        fields.add(scheduleID);

        module.setFields(fields);
        return module;
	}

    private FacilioModule constructInspectionResponse (ModuleBean modBean, FacilioModule inspection) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_RESPONSE,
                "Inspections",
                "Inspection_Responses",
                FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                modBean.getModule(FacilioConstants.QAndA.RESPONSE)
        );

        List<FacilioField> fields = new ArrayList<>();
        LookupField siteField = (LookupField) FieldFactory.getDefaultField("site", "Site", "SITE_ID", FieldType.LOOKUP);
        siteField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        fields.add(siteField);
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(inspection);
        fields.add(parentField);
        
        FacilioField createdTime = (FacilioField) FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);
        
        
        
        FacilioField scheduledWorkStart = (FacilioField) FieldFactory.getDefaultField("scheduledWorkStart", "Scheduled Work Start", "SCHEDULED_WORK_START", FieldType.DATE_TIME);
        fields.add(scheduledWorkStart);
        
        FacilioField scheduledWorkEnd = (FacilioField) FieldFactory.getDefaultField("scheduledWorkEnd", "Scheduled Work End", "SCHDULED_WORK_END", FieldType.DATE_TIME);
        fields.add(scheduledWorkEnd);
        
        FacilioField actualWorkStart = (FacilioField) FieldFactory.getDefaultField("actualWorkStart", "Actual Work Start", "ACTUAL_WORK_START", FieldType.DATE_TIME);
        fields.add(actualWorkStart);
        
        FacilioField actualWorkEnd = (FacilioField) FieldFactory.getDefaultField("actualWorkEnd", "Actual Work End", "ACTUAL_WORK_END", FieldType.DATE_TIME);
        fields.add(actualWorkEnd);
        
        FacilioField resumedWorkStart = (FacilioField) FieldFactory.getDefaultField("resumedWorkStart", "Resumed Work Start", "RESUMED_WORK_START", FieldType.DATE_TIME);
        fields.add(resumedWorkStart);
        
        FacilioField  actualWorkDuration = (FacilioField) FieldFactory.getDefaultField("actualWorkDuration", "Actual Work Duration", "ACTUAL_WORK_DURATION", FieldType.NUMBER);
        fields.add(actualWorkDuration);
        
        LookupField moduleStateField = (LookupField) FieldFactory.getField("moduleState", "Status", "MODULE_STATE", module, FieldType.LOOKUP);
		moduleStateField.setDefault(true);
		moduleStateField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
		moduleStateField.setLookupModule(modBean.getModule("ticketstatus"));
		fields.add(moduleStateField);

		FacilioField stateFlowIdField = FieldFactory.getField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", module, FieldType.NUMBER);
		stateFlowIdField.setDefault(true);
		stateFlowIdField.setDisplayType(FieldDisplayType.NUMBER);
		fields.add(stateFlowIdField);

		LookupField approvalStateField = (LookupField) FieldFactory.getField("approvalStatus", "Approval Status", "APPROVAL_STATE", module, FieldType.LOOKUP);
		approvalStateField.setDefault(true);
		approvalStateField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
		approvalStateField.setLookupModule(modBean.getModule("ticketstatus"));
		fields.add(approvalStateField);

		FacilioField approvalFlowIdField = FieldFactory.getField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", module, FieldType.NUMBER);
		approvalFlowIdField.setDefault(true);
		approvalFlowIdField.setDisplayType(FieldDisplayType.NUMBER);
		fields.add(approvalFlowIdField);
        
        SystemEnumField status = (SystemEnumField) FieldFactory.getDefaultField("status", "Response Status", "STATUS", FieldType.SYSTEM_ENUM);
        status.setEnumName("InspectionResponseStatus");
        fields.add(status);
        
        SystemEnumField sourceType = (SystemEnumField) FieldFactory.getDefaultField("sourceType", "Source", "SOURCE_TYPE", FieldType.SYSTEM_ENUM);
        sourceType.setEnumName("InspectionResponseSourceType");
        fields.add(sourceType);
        
        fields.addAll(getInspectionCommonFieldList(modBean));

        module.setFields(fields);
        return module;
    }

    public List<FacilioField> getInspectionCommonFieldList(ModuleBean modBean) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		
        LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Resource", "RESOURCE", FieldType.LOOKUP);
        resource.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        fields.add(resource);
        
        LookupField vendor = (LookupField) FieldFactory.getDefaultField("vendor", "Vendor", "VENDOR", FieldType.LOOKUP);
        vendor.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
        fields.add(vendor);
        
        LookupField tenant = (LookupField) FieldFactory.getDefaultField("tenant", "Tenant", "TENANT", FieldType.LOOKUP);
        tenant.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TENANT));
        fields.add(tenant);
        
        LookupField category = (LookupField) FieldFactory.getDefaultField("category", "Category", "CATEGORY", FieldType.LOOKUP);
        category.setLookupModule(modBean.getModule(FacilioConstants.Inspection.INSPECTION_CATEGORY));
        fields.add(category);
        
        LookupField priority = (LookupField) FieldFactory.getDefaultField("priority", "Priority", "PRIORITY", FieldType.LOOKUP);
        priority.setLookupModule(modBean.getModule(FacilioConstants.Inspection.INSPECTION_PRIORITY));
        fields.add(priority);
        
        LookupField assignedTo = (LookupField) FieldFactory.getDefaultField("assignedTo", "Assigned To", "ASSIGNED_TO", FieldType.LOOKUP);
        assignedTo.setSpecialType("users");
        fields.add(assignedTo);
        
        LookupField assignmentGroup = (LookupField) FieldFactory.getDefaultField("assignmentGroup", "Assignment Group", "ASSIGNMENT_GROUP", FieldType.LOOKUP);
        assignmentGroup.setSpecialType("groups");
        fields.add(assignmentGroup);
        
        return fields;
	}
    
    public void addDefaultStateFlow(FacilioModule inspectionModule) throws Exception {
    	
    	FacilioStatus createdStatus = getFacilioStatus(inspectionModule, "created", "Created", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus assignedStatus = getFacilioStatus(inspectionModule, "assigned", "Assigned", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus wipStatus = getFacilioStatus(inspectionModule, "workInProgress", "Work in Progress", StatusType.OPEN, Boolean.TRUE);
    	FacilioStatus onHoldStatus = getFacilioStatus(inspectionModule, "onHold", "On Hold", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus resolvedStatus = getFacilioStatus(inspectionModule, "resolved", "Resolved", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus closed = getFacilioStatus(inspectionModule, "closed", "Closed", StatusType.CLOSED, Boolean.FALSE);
    	
    	 StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
         stateFlowRuleContext.setName("Default Stateflow");
         stateFlowRuleContext.setModuleId(inspectionModule.getModuleId());
         stateFlowRuleContext.setModule(inspectionModule);
         stateFlowRuleContext.setActivityType(EventType.CREATE);
         stateFlowRuleContext.setExecutionOrder(1);
         stateFlowRuleContext.setStatus(true);
         stateFlowRuleContext.setDefaltStateFlow(true);
         stateFlowRuleContext.setDefaultStateId(createdStatus.getId());
         stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
         WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);
         
         Criteria assignmentCriteria = new Criteria();
         assignmentCriteria.addAndCondition(CriteriaAPI.getCondition("ASSIGNED_TO", "assignedTo", null, CommonOperators.IS_NOT_EMPTY));
         
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Assign", createdStatus, assignedStatus,TransitionType.CONDITIONED,assignmentCriteria);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Start Work", assignedStatus, wipStatus,TransitionType.NORMAL,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Pause", wipStatus, onHoldStatus,TransitionType.NORMAL,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Resume", onHoldStatus, wipStatus,TransitionType.NORMAL,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Resolve", wipStatus, resolvedStatus,TransitionType.NORMAL,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Close", resolvedStatus, closed,TransitionType.NORMAL,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Re-Open", closed, assignedStatus,TransitionType.NORMAL,null);
         
    }
    
    
    private StateflowTransitionContext addStateflowTransitionContext(FacilioModule module,StateFlowRuleContext parentStateFlow,String name,FacilioStatus fromStatus,FacilioStatus toStatus,AbstractStateTransitionRuleContext.TransitionType transitionType,Criteria criteria) throws Exception {
    	
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
        
        
        return stateFlowTransitionContext;
    }
    
    private FacilioStatus getFacilioStatus(FacilioModule module,String status,String displayName,StatusType status1,Boolean timerEnabled) throws Exception {
    	
    	FacilioStatus statusObj = new FacilioStatus();
    	statusObj.setStatus(status);
    	statusObj.setDisplayName(displayName);
    	statusObj.setTypeCode(status1.getIntVal());
    	statusObj.setTimerEnabled(timerEnabled);
        TicketAPI.addStatus(statusObj, module);
        
        return statusObj;
    }
}
