package com.facilio.bmsconsoleV3.signup.serviceRequestModules;

import org.json.simple.JSONObject;

import com.facilio.accounts.sso.SSOUtil;
import com.facilio.bmsconsole.actions.CustomButtonAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext.PositionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.ModuleFactory;
import com.facilio.security.requestvalidator.config.Config;
import com.facilio.time.DateTimeUtil;

public class AddServiceRequestModules extends SignUpData{

	@Override
	public void addData() throws Exception {
		long activityType=EventType.CUSTOM_BUTTON.getValue();
		CustomButtonAction customButton=new CustomButtonAction();
		customButton.setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);
		CustomButtonRuleContext buttonRule=new CustomButtonRuleContext();
		buttonRule.setName("CONVERT TO WORKORDER");
		buttonRule.setPositionType(PositionType.SUMMARY.getIndex());
		buttonRule.setActivityType(EventType.CUSTOM_BUTTON);
		buttonRule.setButtonType(CustomButtonRuleContext.ButtonType.SHOW_WIDGET.getIndex());
		JSONObject config=new JSONObject();
		config.put("actionType", "Redirect URL");
		config.put("url",SSOUtil.getCurrentAppURL()+"/app/wo/create?serviceRequest=${serviceRequest.id:-}&subject=${serviceRequest.subject:-}&siteId=${serviceRequest.siteId:-}&description=${serviceRequest.description:-}");
		buttonRule.setConfig(config);
		buttonRule.setCreatedTime(DateTimeUtil.getCurrenTime());
		WorkflowEventContext workFlowEvent=new WorkflowEventContext();
		workFlowEvent.setActivityType(EventType.CUSTOM_BUTTON);
		FacilioModule module=new FacilioModule();
		module.setCreatedTime(DateTimeUtil.getCurrenTime());
		module.setCustom(false);
		module.setDataInterval(-1);
		module.setDisplayName("Service Requests");
		module.setModifiedTime(DateTimeUtil.getCurrenTime());//*
		module.setName(ContextNames.SERVICE_REQUEST);
		module.setShowAsView(false);
		module.setSourceBundle(0);
		module.setStateFlowEnabled(true);
		module.setTableName(ModuleFactory.getServiceRequestModule().getTableName());
		module.setTrashEnabled(true);
		module.setType(ModuleType.BASE_ENTITY.getValue());
		module.setType(ModuleType.BASE_ENTITY);		
		workFlowEvent.setModule(module);
		workFlowEvent.setModuleName(ModuleFactory.getServiceRequestModule().getName());
		buttonRule.setEvent(workFlowEvent);
		buttonRule.setLatestVersion(true);
		buttonRule.setModifiedTime(DateTimeUtil.getCurrenTime());
		buttonRule.setModule(module);
		buttonRule.setModuleName(ModuleFactory.getServiceRequestModule().getName());
		buttonRule.setName("CONVERT TO WORKORDER");
		buttonRule.setOnSuccess(false);
		buttonRule.setParentId(0);
		buttonRule.setPositionType(1);
		buttonRule.setReportBreakdown(false);
		buttonRule.setRuleType(RuleType.CUSTOM_BUTTON);
		buttonRule.setShouldFormInterfaceApply(false);
		buttonRule.setStatus(true);
		customButton.setRule(buttonRule);
		
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateCustomButtonChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, customButton.getModuleName());
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, buttonRule);

        chain.execute();   
		
	}
	

}
