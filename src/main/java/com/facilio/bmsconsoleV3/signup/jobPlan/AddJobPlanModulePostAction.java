package com.facilio.bmsconsoleV3.signup.jobPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
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

public class AddJobPlanModulePostAction extends BaseModuleConfig {

	public AddJobPlanModulePostAction() {
		setModuleName(FacilioConstants.ContextNames.JOB_PLAN);
	}

	@Override
	public void addForms(List<ApplicationContext> allApplications) throws Exception {
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
			addScopeSectionDisableRule(form, fieldMap, formFieldMap);
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

	@Override
	public void addWidgets() throws Exception {
		return;
	}

	private void addScopeSectionDisableRule(FacilioForm defaultForm, Map<String, FacilioField> fieldMap, Map<Long, FormField> formFieldMap) throws Exception{
		FormRuleContext singleRule = new FormRuleContext();
		singleRule.setName("Scope Section Disable");
		singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
		singleRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
		singleRule.setFormId(defaultForm.getId());
		singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus", JobPlanContext.JPStatus.PENDING_REVISION.getVal()+"",EnumOperators.IS));
		criteria.addOrCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",JobPlanContext.JPStatus.REVISED.getVal()+"",EnumOperators.IS));
		criteria.setPattern("(1 or 2)");
		singleRule.setCriteria(criteria);

		List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
		FormRuleActionContext disableAction = new FormRuleActionContext();
		disableAction.setActionType(FormActionType.DISABLE_FIELD.getVal());

		List<FormRuleActionFieldsContext> fieldList = new ArrayList<>();

		FormRuleActionFieldsContext nameField = new FormRuleActionFieldsContext();
		nameField.setFormFieldId(formFieldMap.get(fieldMap.get("name").getId()).getId());
		fieldList.add(nameField);

		FormRuleActionFieldsContext jobPlanCategoryField = new FormRuleActionFieldsContext();
		jobPlanCategoryField.setFormFieldId(formFieldMap.get(fieldMap.get("jobPlanCategory").getId()).getId());
		fieldList.add(jobPlanCategoryField);

		FormRuleActionFieldsContext spaceCategory = new FormRuleActionFieldsContext();
		spaceCategory.setFormFieldId(formFieldMap.get(fieldMap.get("spaceCategory").getId()).getId());
		fieldList.add(spaceCategory);

		FormRuleActionFieldsContext assetCategory = new FormRuleActionFieldsContext();
		assetCategory.setFormFieldId(formFieldMap.get(fieldMap.get("assetCategory").getId()).getId());
		fieldList.add(assetCategory);

		disableAction.setFormRuleActionFieldsContext(fieldList);

		actions.add(disableAction);

		singleRule.setActions(actions);

		FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		Context context = chain.getContext();

		context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);

		chain.execute();
	}
	
}
