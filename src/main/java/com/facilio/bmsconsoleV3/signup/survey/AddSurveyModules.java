package com.facilio.bmsconsoleV3.signup.survey;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleActionFieldsContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleTriggerFieldContext;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext.TransitionType;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.signup.AddQAndAModules;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.ScheduleInfo.FrequencyType;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

public class AddSurveyModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        List<FacilioModule> modules = new ArrayList<>();
        
        FacilioModule survey = constructSurvey(modBean);
        modules.add(survey);
        FacilioModule surveyResponseModule = constructSurveyResponse(modBean, survey);
        modules.add(surveyResponseModule);
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        
//        addScoping(survey);
        
        SignupUtil.addNotesAndAttachmentModule(surveyResponseModule);
        
        addSurveyResponseRollUpToTemplate(Constants.getModBean(), surveyResponseModule);
        
        addDefaultStateFlowForSurveyTemplate(survey);
        addDefaultStateFlowForSurveyResponse(surveyResponseModule);

        addActivityModuleForSurveyResponse(surveyResponseModule);
		addWorkOrderRollupFields(modBean);
		addSurveyResponseRollUpToWorkOrder(modBean, surveyResponseModule);
    }
    
//    public void addScoping(FacilioModule module) throws Exception {
//		
//		long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
//        
//        ScopingConfigContext scoping = new ScopingConfigContext();
//        Criteria criteria = new Criteria();
//        
//        Condition condition = CriteriaAPI.getCondition(module.getName()+".sites", "com.facilio.modules.ContainsSiteValueGenerator", ScopeOperator.SCOPING_IS);
//        condition.setColumnName("sites");  			// setting dummy column Name
//        condition.setModuleName(module.getName());
//        
//        Condition condition1 = CriteriaAPI.getCondition(module.getName()+".sites", null, CommonOperators.IS_EMPTY);
//        condition1.setColumnName("sites");  		// setting dummy column Name
//        condition1.setModuleName(module.getName());
//        
//        criteria.addAndCondition(condition);
//        criteria.addOrCondition(condition1);
//        
//        scoping.setScopingId(applicationScopingId);
//        scoping.setModuleId(module.getModuleId());
//        scoping.setCriteria(criteria);
//        
//        ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
//	}


	public void addActivityModuleForSurveyResponse(FacilioModule surveyResponseModule) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = new FacilioModule(FacilioConstants.Survey.SURVEY_RESPONSE_ACTIVITY,
                "Survey Response Activity",
                "Q_And_A_Response_Activity",
                FacilioModule.ModuleType.ACTIVITY
                );

				
		List<FacilioField> fields = new ArrayList<>();
		
		NumberField parentId = (NumberField) FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
		fields.add(parentId);
		
		FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);
		
		fields.add(timefield);
		
		NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
		fields.add(type);
		
		LookupField doneBy = (LookupField) FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
		doneBy.setSpecialType("users");
		fields.add(doneBy);
		
		FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING);
		
		fields.add(info);
		
		
		module.setFields(fields);	
        
        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();
		
        modBean.addSubModule(surveyResponseModule.getModuleId(), module.getModuleId());
	}


	private FacilioModule constructSurvey(ModuleBean modBean) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Survey.SURVEY_TEMPLATE,
                                                "Survey Templates",
                                                "Survey_Templates",
                                                FacilioModule.ModuleType.Q_AND_A,
                                                modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE),
												true
                                                );
        
        module.setStateFlowEnabled(Boolean.TRUE);

        List<FacilioField> fields = new ArrayList<>();
        
        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);

        
        SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("creationType", "Scope", "CREATION_TYPE", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("SurveyCreationType");
        
        fields.add(creationType);
        
        BooleanField status = (BooleanField) FieldFactory.getDefaultField("status", "Active Status", "STATUS", FieldType.BOOLEAN);
        status.setTrueVal("Active");
        status.setFalseVal("Inactive");
        fields.add(status);
        
        SystemEnumField assignmentType = (SystemEnumField) FieldFactory.getDefaultField("assignmentType", "Scope Category", "ASSIGNMENT_TYPE", FieldType.SYSTEM_ENUM);
        assignmentType.setEnumName("MultiResourceAssignmentType");
        
        fields.add(assignmentType);
        
        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);
        
        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);
        
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
		
		MultiLookupField multiLookupSiteField = (MultiLookupField) FieldFactory.getDefaultField("sites", "Sites", null, FieldType.MULTI_LOOKUP);
        multiLookupSiteField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupSiteField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        
        MultiLookupField multiLookupBuildingField = (MultiLookupField) FieldFactory.getDefaultField("buildings", "Buildings", null, FieldType.MULTI_LOOKUP);
        multiLookupBuildingField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupBuildingField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.BUILDING));
        
        
        fields.add(multiLookupSiteField);
        
        fields.add(multiLookupBuildingField);
        
        fields.addAll(getSurveyCommonFieldList(modBean));
        
        module.setFields(fields);
        return module;
    }

    private FacilioModule constructSurveyResponse (ModuleBean modBean, FacilioModule survey) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Survey.SURVEY_RESPONSE,
                "Surveys",
                "Survey_Responses",
                FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                modBean.getModule(FacilioConstants.QAndA.RESPONSE),
				true
        );
        
        module.setStateFlowEnabled(Boolean.TRUE);
        
        Criteria criteria = new Criteria();
        
        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", SurveyResponseContext.Status.PRE_OPEN.getIndex()+"", EnumOperators.ISN_T));
        
        Long criteriaID = CriteriaAPI.addCriteria(criteria);
        
        module.setCriteriaId(criteriaID);

        List<FacilioField> fields = new ArrayList<>();
        
        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);

		NumberField ruleField = (NumberField) FieldFactory.getDefaultField("ruleId", "Rule Id", "RULE_ID", FieldType.NUMBER);
		fields.add(ruleField);

        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(survey);
        fields.add(parentField);
        
        FacilioField createdTime = (FacilioField) FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);

		LookupField workOrderField = ( LookupField ) FieldFactory.getDefaultField ("workOrderId", "WorkOrder", "WORKORDER_ID", FieldType.LOOKUP, false);
		workOrderField.setLookupModule (modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		fields.add (workOrderField);

		LookupField serviceRequestField = ( LookupField ) FieldFactory.getDefaultField ("serviceRequestId", "Service Request", "SERVICE_REQUEST_ID", FieldType.LOOKUP, false);
		serviceRequestField.setLookupModule (modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
		fields.add (serviceRequestField);
        
        FacilioField scheduledWorkStart = (FacilioField) FieldFactory.getDefaultField("scheduledWorkStart", "Scheduled Start", "SCHEDULED_WORK_START", FieldType.DATE_TIME);
        fields.add(scheduledWorkStart);
        
        FacilioField scheduledWorkEnd = (FacilioField) FieldFactory.getDefaultField("scheduledWorkEnd", "Scheduled End", "SCHDULED_WORK_END", FieldType.DATE_TIME);
        fields.add(scheduledWorkEnd);
        
        FacilioField actualWorkStart = (FacilioField) FieldFactory.getDefaultField("actualWorkStart", "Actual Start", "ACTUAL_WORK_START", FieldType.DATE_TIME);
        fields.add(actualWorkStart);
        
        FacilioField actualWorkEnd = (FacilioField) FieldFactory.getDefaultField("actualWorkEnd", "Actual End", "ACTUAL_WORK_END", FieldType.DATE_TIME);
        fields.add(actualWorkEnd);
        
        FacilioField resumedWorkStart = (FacilioField) FieldFactory.getDefaultField("resumedWorkStart", "Resumed Work Start", "RESUMED_WORK_START", FieldType.DATE_TIME);
        fields.add(resumedWorkStart);
        
        FacilioField  actualWorkDuration = (FacilioField) FieldFactory.getDefaultField("actualWorkDuration", "Actual Duration", "ACTUAL_WORK_DURATION", FieldType.NUMBER);
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
        status.setEnumName("SurveyResponseStatus");
        fields.add(status);
        
        SystemEnumField sourceType = (SystemEnumField) FieldFactory.getDefaultField("sourceType", "Source", "SOURCE_TYPE", FieldType.SYSTEM_ENUM);
        sourceType.setEnumName("SurveyResponseSourceType");
        fields.add(sourceType);
        
        fields.addAll(getSurveyCommonFieldList(modBean));

        module.setFields(fields);
        return module;
    }

    public static void addSurveyResponseRollUpToTemplate(ModuleBean modBean, FacilioModule surveyResponse) throws Exception {
        FacilioModule qandaTemplate = modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE);
        FacilioUtil.throwIllegalArgumentException(qandaTemplate == null, "Q And A Template module cannot be null. This shouldn't happen");
        FacilioField responseParentField = modBean.getField("parent", surveyResponse.getName());
        FacilioUtil.throwIllegalArgumentException(responseParentField == null, "Parent field of Survey response cannot be null. This shouldn't happen");
        FacilioField totalResponsesField = modBean.getField("totalResponses", qandaTemplate.getName());
        FacilioUtil.throwIllegalArgumentException(totalResponsesField == null, "totalResponses field of template cannot be null. This shouldn't happen");
        FacilioField surveyResponseStatusField = modBean.getField("status", surveyResponse.getName());
        FacilioUtil.throwIllegalArgumentException(surveyResponseStatusField == null, "status field of Survey Response cannot be null. This shouldn't happen");

        RollUpField rollUpField = AddQAndAModules.constructRollUpField("Survey Response RollUP", surveyResponse, responseParentField, qandaTemplate, totalResponsesField, CriteriaAPI.getCondition(surveyResponseStatusField, String.valueOf(SurveyResponseContext.Status.PRE_OPEN.getIndex()), PickListOperators.ISN_T));
        RollUpFieldUtil.addRollUpField(Collections.singletonList(rollUpField));
    }

    public List<FacilioField> getSurveyCommonFieldList(ModuleBean modBean) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		
        LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Space/Asset", "RESOURCE", FieldType.LOOKUP);
        resource.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        fields.add(resource);
        
        LookupField assignedTo = (LookupField) FieldFactory.getDefaultField("assignedTo", "Assigned To", "ASSIGNED_TO", FieldType.LOOKUP);
        assignedTo.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(assignedTo);
        
        return fields;
	}
    
    public void addDefaultStateFlowForSurveyResponse(FacilioModule surveyModule) throws Exception {
    	
    	FacilioStatus createdStatus = getFacilioStatus(surveyModule, "created", "Created", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus assignedStatus = getFacilioStatus(surveyModule, "assigned", "Assigned", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus wipStatus = getFacilioStatus(surveyModule, "inProgress", "In Progress", StatusType.OPEN, Boolean.TRUE);
    	FacilioStatus resolvedStatus = getFacilioStatus(surveyModule, "completed", "Completed", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus closed = getFacilioStatus(surveyModule, "closed", "Closed", StatusType.CLOSED, Boolean.FALSE);
    	
    	 StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
         stateFlowRuleContext.setName("Default Stateflow");
         stateFlowRuleContext.setModuleId(surveyModule.getModuleId());
         stateFlowRuleContext.setModule(surveyModule);
         stateFlowRuleContext.setActivityType(EventType.CREATE);
         stateFlowRuleContext.setExecutionOrder(1);
         stateFlowRuleContext.setStatus(true);
         stateFlowRuleContext.setDefaltStateFlow(true);
         stateFlowRuleContext.setDefaultStateId(createdStatus.getId());
         stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
         WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);
         
         Criteria assignmentCriteria = new Criteria();
         assignmentCriteria.addAndCondition(CriteriaAPI.getCondition("ASSIGNED_TO", "assignedTo", null, CommonOperators.IS_NOT_EMPTY));
         
         Criteria wipCtriteria = new Criteria();
         wipCtriteria.addAndCondition(CriteriaAPI.getCondition("RESPONSE_STATUS", "responseStatus", ResponseContext.ResponseStatus.PARTIALLY_ANSWERED.getIndex()+"", EnumOperators.IS));
         
         
         Criteria completionCtriteria = new Criteria();
         completionCtriteria.addAndCondition(CriteriaAPI.getCondition("RESPONSE_STATUS", "responseStatus", ResponseContext.ResponseStatus.COMPLETED.getIndex()+"", EnumOperators.IS));
         
         ActionContext startTimeaction = getUpdateActualStartTimeField();
         ActionContext endTimeaction = getUpdateActualEndTimeField();
         
         addStateflowTransitionContext(surveyModule, stateFlowRuleContext, "Assign", createdStatus, assignedStatus,TransitionType.CONDITIONED,assignmentCriteria,null);
         addStateflowTransitionContext(surveyModule, stateFlowRuleContext, "Start Survey", assignedStatus, wipStatus,TransitionType.CONDITIONED,wipCtriteria,null);
         addStateflowTransitionContext(surveyModule, stateFlowRuleContext, "End Survey", wipStatus, resolvedStatus,TransitionType.CONDITIONED,completionCtriteria,null);
         addStateflowTransitionContext(surveyModule, stateFlowRuleContext, "Close", resolvedStatus, closed,TransitionType.NORMAL,null,null);
         
    }
    
    private ActionContext getUpdateActualStartTimeField() {
		// TODO Auto-generated method stub
    	ActionContext action = new ActionContext();
    	
    	action.setActionType(ActionType.FIELD_CHANGE);
    	
    	JSONObject templateJson = new JSONObject();
    	JSONObject fieldUpdateObject = new JSONObject();
    	
    	fieldUpdateObject.put("columnName", "Survey_Responses.ACTUAL_WORK_START");
    	fieldUpdateObject.put("field", "actualWorkStart");
    	fieldUpdateObject.put("isSpacePicker", false);
    	fieldUpdateObject.put("value", 0);
    	
    	templateJson.put("fieldMatcher", FacilioUtil.getSingleTonJsonArray(fieldUpdateObject));
    	
    	action.setTemplateJson(templateJson);
    	
    	JSONTemplate template = new JSONTemplate();
    	
    	template.setContent("{\"actualWorkStart\":0}");
    	template.setName("actualWorkStart Update");
    	template.setType(Type.JSON);
    	
    	action.setTemplate(template);
    	
		return action;
	}

	private ActionContext getUpdateActualEndTimeField() {
		// TODO Auto-generated method stub
		ActionContext action = new ActionContext();
    	
    	action.setActionType(ActionType.FIELD_CHANGE);
    	
    	JSONObject templateJson = new JSONObject();
    	JSONObject fieldUpdateObject = new JSONObject();
    	
    	fieldUpdateObject.put("columnName", "Survey_Responses.ACTUAL_WORK_END");
    	fieldUpdateObject.put("field", "actualWorkEnd");
    	fieldUpdateObject.put("isSpacePicker", false);
    	fieldUpdateObject.put("value", 0);
    	
    	templateJson.put("fieldMatcher", FacilioUtil.getSingleTonJsonArray(fieldUpdateObject));
    	
    	action.setTemplateJson(templateJson);
    	
    	JSONTemplate template = new JSONTemplate();
    	
    	template.setContent("{\"actualWorkEnd\":0}");
    	template.setName("actualWorkEnd Update");
    	template.setType(Type.JSON);
    	
    	action.setTemplate(template);
    	
		return action;
	}

	public void addDefaultStateFlowForSurveyTemplate(FacilioModule surveyModule) throws Exception {
    	
    	FacilioStatus activeStatus = getFacilioStatus(surveyModule, "active", "Active", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus inActiveStatus = getFacilioStatus(surveyModule, "inactive", "Inactive", StatusType.CLOSED, Boolean.FALSE);
    	
    	 StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
         stateFlowRuleContext.setName("Default Stateflow");
         stateFlowRuleContext.setModuleId(surveyModule.getModuleId());
         stateFlowRuleContext.setModule(surveyModule);
         stateFlowRuleContext.setActivityType(EventType.CREATE);
         stateFlowRuleContext.setExecutionOrder(1);
         stateFlowRuleContext.setStatus(true);
         stateFlowRuleContext.setDefaltStateFlow(true);
         stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
         stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
         WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);
         
         addStateflowTransitionContext(surveyModule, stateFlowRuleContext, "Deactivate", activeStatus, inActiveStatus,TransitionType.NORMAL,null,null);
         addStateflowTransitionContext(surveyModule, stateFlowRuleContext, "Activate", inActiveStatus, activeStatus,TransitionType.NORMAL,null,null);
    }

    private StateflowTransitionContext addStateflowTransitionContext(FacilioModule module,StateFlowRuleContext parentStateFlow,String name,FacilioStatus fromStatus,FacilioStatus toStatus,AbstractStateTransitionRuleContext.TransitionType transitionType,Criteria criteria,List<ActionContext> actions) throws Exception {
    	
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
    
    private FacilioStatus getFacilioStatus(FacilioModule module,String status,String displayName,StatusType status1,Boolean timerEnabled) throws Exception {
    	
    	FacilioStatus statusObj = new FacilioStatus();
    	statusObj.setStatus(status);
    	statusObj.setDisplayName(displayName);
    	statusObj.setTypeCode(status1.getIntVal());
    	statusObj.setTimerEnabled(timerEnabled);
        TicketAPI.addStatus(statusObj, module);
        
        return statusObj;
    }

	private void addWorkOrderRollupFields(ModuleBean modBean) throws Exception{

		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getDefaultField("fullScore", "Full Score", "FULL_SCORE", FieldType.DECIMAL));
		fields.add(FieldFactory.getDefaultField("totalScore", "Total Score", "TOTAL_SCORE", FieldType.DECIMAL));

		FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, workOrderModule.getName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
		chain.execute();

	}

	private void addSurveyResponseRollUpToWorkOrder(ModuleBean modBean, FacilioModule surveyResponseModule) throws Exception{

		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

		FacilioField surveyParentIdField = modBean.getField("workOrderId", surveyResponseModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyParentIdField == null, "Parent field of WorkOrder Survey Response cannot be null. This shouldn't happen");

		FacilioField surveyFullScoreField = modBean.getField("fullScore", surveyResponseModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyFullScoreField == null, "Full score  field of SurveyResponse cannot be null. This shouldn't happen");

		FacilioField surveyTotalScoreField = modBean.getField("totalScore", surveyResponseModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyFullScoreField == null, "Total score  field of SurveyResponse cannot be null. This shouldn't happen");

		FacilioField workOrderFullScoreField = modBean.getField("fullScore", workOrderModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyFullScoreField == null, "Full score  field of SurveyResponse cannot be null. This shouldn't happen");

		FacilioField workOrderTotalScoreField = modBean.getField("totalScore", workOrderModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyFullScoreField == null, "Total score  field of SurveyResponse cannot be null. This shouldn't happen");

		List<RollUpField> rollUpFields = new ArrayList<>();
		rollUpFields.add(constructRollUpField("WorkOrder survey full score RollUp",surveyResponseModule , surveyParentIdField,workOrderModule, workOrderFullScoreField, surveyFullScoreField));
		rollUpFields.add(constructRollUpField("WorkOrder survey Total score RollUp", surveyResponseModule, surveyParentIdField,workOrderModule , workOrderTotalScoreField, surveyTotalScoreField));
		RollUpFieldUtil.addRollUpField(rollUpFields);
	}
	private static RollUpField constructRollUpField(String desc, FacilioModule childModule, FacilioField childLookUpField, FacilioModule parentModule, FacilioField parentRollUpField, FacilioField aggregateField){

		RollUpField rollUp = new RollUpField();
		rollUp.setDescription(desc);
		rollUp.setAggregateFunctionId(BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
		rollUp.setChildModuleId(childModule.getModuleId());
		rollUp.setChildFieldId(childLookUpField.getFieldId());
		rollUp.setParentModuleId(parentModule.getModuleId());
		rollUp.setParentRollUpFieldId(parentRollUpField.getFieldId());
		rollUp.setAggregateFieldId(aggregateField.getFieldId());
		rollUp.setIsSystemRollUpField(true);

		return rollUp;
	}
}
