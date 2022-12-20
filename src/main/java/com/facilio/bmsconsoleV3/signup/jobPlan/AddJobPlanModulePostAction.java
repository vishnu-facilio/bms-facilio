package com.facilio.bmsconsoleV3.signup.jobPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleActionFieldsContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleTriggerFieldContext;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class AddJobPlanModulePostAction extends SignUpData {

	@Override
	public void addData() throws Exception {
		// TODO Auto-generated method stub
		
		FacilioModule jobPlanModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.JOB_PLAN);
		
		addFormRulesForJP(jobPlanModule);
	}

	
	public void addFormRulesForJP(FacilioModule jobPlanModule) throws Exception {
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(jobPlanModule.getName()));
    	
    	
    	Map<String, FacilioForm> forms = FormsAPI.getFormsFromDB(jobPlanModule.getName(), AddJobPlanModule.jobPlanSupportedApps);
    	
    	for(FacilioForm form : forms.values()) {
    		
    		Map<Long, FormField> formFieldMap = form.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
        	
        	addAssetCategoryShowRule(form, fieldMap, formFieldMap);
            addAssetCategoryHideRule(form, fieldMap, formFieldMap);
            addSpaceCategoryShowRule(form, fieldMap, formFieldMap);
            addSpaceCategoryHideRule(form, fieldMap, formFieldMap);
    	}
    	
    }
    
    private void addAssetCategoryShowRule(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		  FormRuleContext singleRule = new FormRuleContext();
	      singleRule.setName("Asset Category Show");
	      singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
	      singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
	      singleRule.setFormId(defaultForm.getId());
	      singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("CATEGORY","jobPlanCategory", PlannedMaintenance.PMScopeAssigmentType.ASSETCATEGORY.getVal()+"", EnumOperators.IS));
	      
	      singleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("jobPlanCategory").getId()).getId());
	      singleRule.setTriggerFields(Collections.singletonList(triggerField));
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext showAction = new FormRuleActionContext(); 
	      showAction.setActionType(FormActionType.SHOW_FIELD.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("assetCategory").getId()).getId());
	      
	      showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(showAction);
	      
	      singleRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
			
		  chain.execute();
	}
    
    private void addAssetCategoryHideRule(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		FormRuleContext singleRule = new FormRuleContext();
	      singleRule.setName("Asset Category Hide");
	      singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
	      singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
	      singleRule.setFormId(defaultForm.getId());
	      singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("CATEGORY","jobPlanCategory", PlannedMaintenance.PMScopeAssigmentType.ASSETCATEGORY.getVal()+"", EnumOperators.ISN_T));
	      
	      singleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("jobPlanCategory").getId()).getId());
	      singleRule.setTriggerFields(Collections.singletonList(triggerField));
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext showAction = new FormRuleActionContext(); 
	      showAction.setActionType(FormActionType.HIDE_FIELD.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("assetCategory").getId()).getId());
	      
	      showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(showAction);
	      
	      singleRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
			
		  chain.execute();
	}
	
	private void addSpaceCategoryShowRule(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		FormRuleContext singleRule = new FormRuleContext();
	      singleRule.setName("Space Category Show");
	      singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
	      singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
	      singleRule.setFormId(defaultForm.getId());
	      singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("CATEGORY","jobPlanCategory", PlannedMaintenance.PMScopeAssigmentType.SPACECATEGORY.getVal()+"", EnumOperators.IS));
	      
	      singleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("jobPlanCategory").getId()).getId());
	      singleRule.setTriggerFields(Collections.singletonList(triggerField));
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext showAction = new FormRuleActionContext(); 
	      showAction.setActionType(FormActionType.SHOW_FIELD.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("spaceCategory").getId()).getId());
	      
	      showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(showAction);
	      
	      singleRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
			
		  chain.execute();
	}
	
	private void addSpaceCategoryHideRule(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		FormRuleContext singleRule = new FormRuleContext();
	      singleRule.setName("Space Category Hide");
	      singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
	      singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
	      singleRule.setFormId(defaultForm.getId());
	      singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("CATEGORY","jobPlanCategory", PlannedMaintenance.PMScopeAssigmentType.SPACECATEGORY.getVal()+"", EnumOperators.ISN_T));
	      
	      singleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("jobPlanCategory").getId()).getId());
	      singleRule.setTriggerFields(Collections.singletonList(triggerField));
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext showAction = new FormRuleActionContext(); 
	      showAction.setActionType(FormActionType.HIDE_FIELD.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("spaceCategory").getId()).getId());
	      
	      showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(showAction);
	      
	      singleRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
			
		  chain.execute();
	}
	
}
