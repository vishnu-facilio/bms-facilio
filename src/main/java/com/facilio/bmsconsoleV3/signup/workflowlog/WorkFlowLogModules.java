package com.facilio.bmsconsoleV3.signup.workflowlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.FormRuleAction;
import com.facilio.bmsconsole.commands.AddSubModulesSystemFieldsCommad;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.context.ScopingConfigContext;
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
import com.facilio.bmsconsoleV3.context.inspection.InspectionCategoryContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionPriorityContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.signup.AddQAndAModules;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.ScheduleInfo.FrequencyType;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;


public class WorkFlowLogModules extends SignUpData{
	
	@Override
	public void addData() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	    
	    List<FacilioModule> modules = new ArrayList<>();
	    
	    FacilioModule workFlowLogModule = constructWorkFlowLogModule();
	    modules.add(workFlowLogModule);
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
	}
	
	public FacilioModule constructWorkFlowLogModule() throws Exception {
		
		FacilioModule module = new FacilioModule(FacilioConstants.Workflow.WORKFLOW_LOG,
                "Workflow Log",
                "Workflow_Log",
                FacilioModule.ModuleType.BASE_ENTITY
                );

		List<FacilioField> fields = new ArrayList<>();
		fields.add((FacilioField) FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER));
		
		SystemEnumField logType = (SystemEnumField) FieldFactory.getDefaultField("logType", "Type", "LOG_TYPE", FieldType.SYSTEM_ENUM);
		logType.setEnumName("WorkflowLogTypes");
        fields.add(logType);
		
		fields.add((FacilioField) FieldFactory.getDefaultField("recordId", "Module RecordId", "RECORD_ID", FieldType.NUMBER));
		
		fields.add((FacilioField) FieldFactory.getDefaultField("workflowId", "Workflow Id", "WORKFLOW_ID", FieldType.NUMBER,true));
		
		SystemEnumField logStatus = (SystemEnumField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.SYSTEM_ENUM);
		logStatus.setEnumName("WorkflowLogState");
        fields.add(logStatus);
		
		fields.add((FacilioField) FieldFactory.getDefaultField("exception", "Exception", "EXCEPTION", FieldType.STRING));
		
		fields.add((FacilioField) FieldFactory.getDefaultField("logValue", "Log Value", "LOG_VALUE", FieldType.STRING));

		fields.add((FacilioField) FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.DATE_TIME));

		fields.add(FieldFactory.getDefaultField("recordModuleId","Module ID","RECORD_MODULE_ID",FieldType.NUMBER));

		fields.add(FieldFactory.getDefaultField("totalSelectCount","Total Select Count","TOTAL_SELECT_COUNT",FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("totalInsertCount","Total Insert Count","TOTAL_INSERT_COUNT",FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("totalUpdateCount","Total Update Count","TOTAL_UPDATE_COUNT",FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("totalDeleteCount","Total Delete Count","TOTAL_DELETE_COUNT",FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("totalStatementCount","Total Statement Count","TOTAL_STATEMENT_COUNT",FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("totalApiRequestCount","Total External API Request Count","TOTAL_EXTERNAL_API_REQUEST_SIZE",FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("totalApiResponseCount","Total External API Response Count","TOTAL_EXTERNAL_API_RESPONSE_SIZE",FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("totalEmailCount","Total Email Notification Count","TOTAL_EMAIL_COUNT",FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("totalPushNotficationCount","Total Push Notification Count","TOTAL_PUSH_NOTIFICATION_COUNT",FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("threadName","Thread Name","THREAD_NAME",FieldType.STRING));


		module.setFields(fields);
		return module;
	}
}