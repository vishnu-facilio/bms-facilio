package com.facilio.bmsconsoleV3.signup.inspection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.FormRuleAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleActionFieldsContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleTriggerFieldContext;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext.TransitionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionCategoryContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionPriorityContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.signup.AddQAndAModules;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

public class AddInspectionModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        List<FacilioModule> modules = new ArrayList<>();
        
        FacilioModule insepctionCategoryModule = constructInspectionCategory();
        FacilioModule insepctionPriorityModule = constructInspectionPriority();
        modules.add(insepctionCategoryModule);
        modules.add(insepctionPriorityModule);
        
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
        addInspectionResponseRollUpToTemplate(Constants.getModBean(), inspectionResponseModule);

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
        
        addDefaultStateFlowForInspectionTemplate(inspection);
        addDefaultStateFlowForInspectionResponse(inspectionResponseModule);
        
        addDefaultFormForInspectionTemplate(inspection);
        
        addDefaultScheduleJobs();
        
        addDefaultInspectionPriorities(insepctionPriorityModule,modBean);
        addDefaultInspectionCategories(insepctionCategoryModule,modBean);
    }


    public void addDefaultInspectionCategories(FacilioModule insepctionCategoryModule,ModuleBean modBean) throws Exception {
		// TODO Auto-generated method stub
		
		List<InspectionCategoryContext> categories = new ArrayList<InspectionCategoryContext>();
		
		categories.add(new InspectionCategoryContext("Inspection", "inspection", "Visual assessment of property and assets for safety, security and proper operations"));
		categories.add(new InspectionCategoryContext("Compliance", "compliance", "Inspection to assess compliance with specific laws, regulations and guidelines"));
		categories.add(new InspectionCategoryContext("Condition Assessment", "conditionAssessment", "Inspection to assess the conditions of property and assets"));
		categories.add(new InspectionCategoryContext("Routines", "routines", "Scheduled inspections for routine assessments"));
		categories.add(new InspectionCategoryContext("Investigation", "investigation", "Inspections to identify risks and assess effectiveness of current measure"));
		categories.add(new InspectionCategoryContext("Logbook", "logbook", "Digitally log electrical, HVAC, DG readings for reporting and alerts"));
		
		InsertRecordBuilder<InspectionCategoryContext> insert = new InsertRecordBuilder<InspectionCategoryContext>()
				.moduleName(FacilioConstants.Inspection.INSPECTION_CATEGORY)
				.fields(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_CATEGORY))
				.addRecords(categories);
		
			insert.save();
	}


	public void addDefaultInspectionPriorities(FacilioModule insepctionPriorityModule,ModuleBean modBean) throws Exception {
		// TODO Auto-generated method stub
		
		
		List<InspectionPriorityContext> priorites = new ArrayList<InspectionPriorityContext>();
		
		priorites.add(new InspectionPriorityContext("High", "High", null, 1, true, "#f00"));
		priorites.add(new InspectionPriorityContext("Medium", "Medium", null, 2, true, "#fb9b00"));
		priorites.add(new InspectionPriorityContext("Low", "Low", null, 3, true, "#f0d200"));
		
		InsertRecordBuilder<InspectionPriorityContext> insert = new InsertRecordBuilder<InspectionPriorityContext>()
				.moduleName(FacilioConstants.Inspection.INSPECTION_PRIORITY)
				.fields(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_PRIORITY))
				.addRecords(priorites);
		
			insert.save();
	}


	public void addDefaultScheduleJobs() throws Exception {
		// TODO Auto-generated method stub

		FacilioTimer.schedulePeriodicJob(AccountUtil.getCurrentOrg().getId(), "ScheduleInspectionStatusChange", 50, 1800, "facilio");
		
		ScheduleInfo scheduleInfo = new ScheduleInfo();
		scheduleInfo.setTimes(Collections.singletonList("00:00"));
		scheduleInfo.setFrequencyType(FrequencyType.DAILY);
		FacilioTimer.scheduleCalendarJob((long)BaseScheduleContext.ScheduleType.INSPECTION.getIndex(), "BaseSchedulerJob", DateTimeUtil.getCurrenTime(), scheduleInfo, "facilio");
		
		JSONObject props = new JSONObject();
		props.put("saveAsV3", Boolean.TRUE);
		BmsJobUtil.addJobProps((long)BaseScheduleContext.ScheduleType.INSPECTION.getIndex(), "BaseSchedulerJob", props);
	}


	public void addDefaultFormForInspectionTemplate(FacilioModule inspection) throws Exception {
		// TODO Auto-generated method stub
		
      ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
	  FacilioForm defaultForm = new FacilioForm();
      defaultForm.setName("standard");
      defaultForm.setModule(inspection);
      defaultForm.setDisplayName("Standard");
      defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
      defaultForm.setShowInWeb(true);
      
      Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(inspection.getName()));
      
      List<FormSection> sections = new ArrayList<FormSection>();

      FormSection section = new FormSection();
      section.setName("Configuration");
      section.setSectionType(FormSection.SectionType.FIELDS);
      section.setShowLabel(true);

      List<FormField> fields = new ArrayList<>();
      
      int seq = 0;
      fields.add(new FormField(fieldMap.get("creationType").getFieldId(), "creationType", FacilioField.FieldDisplayType.SELECTBOX, "Scope", FormField.Required.REQUIRED, ++seq, 1));
      fields.add(new FormField(fieldMap.get("siteId").getFieldId(), "site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, ++seq, 1));
      fields.add(new FormField(fieldMap.get("baseSpace").getFieldId(), "baseSpace", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
      
      fields.add(new FormField(fieldMap.get("assignmentType").getFieldId(), "assignmentType", FacilioField.FieldDisplayType.SELECTBOX, "Scope Category", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
      
      fields.add(new FormField(fieldMap.get("assetCategory").getFieldId(), "assetCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset Category", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
      fields.add(new FormField(fieldMap.get("spaceCategory").getFieldId(), "spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Category", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
      
      fields.add(new FormField(fieldMap.get("resource").getFieldId(), "resource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
      
      section.setFields(fields);
      
      section.setSequenceNumber(1);
      
      sections.add(section);
      
      FormSection configSection = new FormSection();
      configSection.setName("Inspection Details");
      configSection.setSectionType(FormSection.SectionType.FIELDS);
      configSection.setShowLabel(true);
      
      List<FormField> configFields = new ArrayList<>();
      
      seq = 0;
      
      configFields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, ++seq, 1));
      configFields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seq, 1));
      configFields.add(new FormField(fieldMap.get("category").getFieldId(), "category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, ++seq, 1));
      configFields.add(new FormField(fieldMap.get("priority").getFieldId(), "priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, ++seq, 1));
      
      configFields.add(new FormField(-1, "assignment", FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", FormField.Required.OPTIONAL, ++seq, 1));

      configSection.setFields(configFields);
      
      configSection.setSequenceNumber(2);
      
      sections.add(configSection);
      
      defaultForm.setSections(sections);
      FormsAPI.createForm(defaultForm, inspection);
      
      Map<Long, FormField> formFieldMap = defaultForm.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
      
      addSingleAssetSelectRule(defaultForm, fieldMap, formFieldMap);
      addMultipleAssetSelectRule(defaultForm, fieldMap, formFieldMap);
      
      addAssetCategoryShowRule(defaultForm, fieldMap, formFieldMap);
      addAssetCategoryHideRule(defaultForm, fieldMap, formFieldMap);
      addSpaceCategoryShowRule(defaultForm, fieldMap, formFieldMap);
      addSpaceCategoryHideRule(defaultForm, fieldMap, formFieldMap);
      
      addOnLoadRule(defaultForm, fieldMap, formFieldMap);
	}
	
	private void addOnLoadRule(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		FormRuleContext singleRule = new FormRuleContext();
	      singleRule.setName("Form On Load Rule");
	      singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
	      singleRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
	      singleRule.setFormId(defaultForm.getId());
	      singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext filterAction = new FormRuleActionContext(); 
	      filterAction.setActionType(FormActionType.APPLY_FILTER.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
	      
	      JSONObject json = new JSONObject();
	      
	      json.put("show", Boolean.FALSE);
	      json.put("values", "[5,6,8]");
	      
	      actionField.setActionMeta(json.toJSONString());
	      
	      filterAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(filterAction);
	      
	      singleRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
			
		  chain.execute();
		  
	}




	private void addAssetCategoryShowRule(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		FormRuleContext singleRule = new FormRuleContext();
	      singleRule.setName("Asset Category Show");
	      singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
	      singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
	      singleRule.setFormId(defaultForm.getId());
	      singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("ASSIGNMENT_TYPE","assignmentType", PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getVal()+"", EnumOperators.IS));
	      
	      singleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
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
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("ASSIGNMENT_TYPE","assignmentType", PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getVal()+"", EnumOperators.ISN_T));
	      
	      singleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
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
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("ASSIGNMENT_TYPE","assignmentType", PreventiveMaintenance.PMAssignmentType.SPACE_CATEGORY.getVal()+"", EnumOperators.IS));
	      
	      singleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
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
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("ASSIGNMENT_TYPE","assignmentType", PreventiveMaintenance.PMAssignmentType.SPACE_CATEGORY.getVal()+"", EnumOperators.ISN_T));
	      
	      singleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
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


	public void addSingleAssetSelectRule(FacilioForm defaultForm,Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		FormRuleContext singleRule = new FormRuleContext();
	      singleRule.setName("Single Resource Show/Hide Rule");
	      singleRule.setDescription("Rule To Show/Hide single resource's related fields.");
	      singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
	      singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
	      singleRule.setFormId(defaultForm.getId());
	      singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("CREATION_TYPE","creationType", InspectionTemplateContext.CreationType.SINGLE.getVal()+"", EnumOperators.IS));
	      
	      singleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("creationType").getId()).getId());
	      singleRule.setTriggerFields(Collections.singletonList(triggerField));
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext showAction = new FormRuleActionContext(); 
	      showAction.setActionType(FormActionType.SHOW_FIELD.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("resource").getId()).getId());
	      
	      showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(showAction);
	      
	      
	      FormRuleActionContext  hideAction = new FormRuleActionContext(); 
	      hideAction.setActionType(FormActionType.HIDE_FIELD.getVal());
	      
	      List<FormRuleActionFieldsContext> hidefields = new ArrayList<FormRuleActionFieldsContext>();
	      
	      FormRuleActionFieldsContext hideField1 = new FormRuleActionFieldsContext();
	      hideField1.setFormFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
	      hidefields.add(hideField1);
	      
	      FormRuleActionFieldsContext hideField2 = new FormRuleActionFieldsContext();
	      hideField2.setFormFieldId(formFieldMap.get(fieldMap.get("baseSpace").getId()).getId());
	      hidefields.add(hideField2);
	      
	      FormRuleActionFieldsContext hideField3 = new FormRuleActionFieldsContext();
	      hideField3.setFormFieldId(formFieldMap.get(fieldMap.get("assetCategory").getId()).getId());
	      hidefields.add(hideField3);
	      
	      FormRuleActionFieldsContext hideField4 = new FormRuleActionFieldsContext();
	      hideField4.setFormFieldId(formFieldMap.get(fieldMap.get("spaceCategory").getId()).getId());
	      hidefields.add(hideField4);
	      
	      hideAction.setFormRuleActionFieldsContext(hidefields);
	      
	      actions.add(hideAction);
	      
	      singleRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
			
		  chain.execute();
		
	}
	
	public void addMultipleAssetSelectRule(FacilioForm defaultForm,Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		FormRuleContext multipleRule = new FormRuleContext();
		multipleRule.setName("Multiple Resource Show/Hide Rule");
		multipleRule.setDescription("Rule to Show/Hide Multiple Resource's related fields.");
		multipleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
	      multipleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
	      multipleRule.setFormId(defaultForm.getId());
	      multipleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("CREATION_TYPE","creationType", InspectionTemplateContext.CreationType.MULTIPLE.getVal()+"", EnumOperators.IS));
	      
	      multipleRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("creationType").getId()).getId());
	      multipleRule.setTriggerFields(Collections.singletonList(triggerField));
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext hideAction = new FormRuleActionContext(); 
	      hideAction.setActionType(FormActionType.HIDE_FIELD.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("resource").getId()).getId());
	      
	      hideAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(hideAction);
	      
	      
	      FormRuleActionContext  showAction = new FormRuleActionContext(); 
	      showAction.setActionType(FormActionType.SHOW_FIELD.getVal());
	      
	      List<FormRuleActionFieldsContext> showfields = new ArrayList<FormRuleActionFieldsContext>();
	      
	      FormRuleActionFieldsContext showField1 = new FormRuleActionFieldsContext();
	      showField1.setFormFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
	      showfields.add(showField1);
	      
	      FormRuleActionFieldsContext showField2 = new FormRuleActionFieldsContext();
	      showField2.setFormFieldId(formFieldMap.get(fieldMap.get("baseSpace").getId()).getId());
	      showfields.add(showField2);
	      
	      showAction.setFormRuleActionFieldsContext(showfields);
	      
	      actions.add(showAction);
	      
	      multipleRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,multipleRule);
			
		  chain.execute();
		
	}


	public FacilioModule constructInspectionTriggersInclExcl(ModuleBean modBean) throws Exception {

		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL,
                "Inspection Triggers Incl Excl",
                "Inspection_Trigger_Include_Exclude_Resource",
                FacilioModule.ModuleType.SUB_ENTITY
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
		
		FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING);
		fields.add(nameField);
		
		fields.add((FacilioField) FieldFactory.getDefaultField("displayName", "Display Name", "DISPLAY_NAME", FieldType.STRING,true));
		
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
        
        module.setStateFlowEnabled(Boolean.TRUE);

        List<FacilioField> fields = new ArrayList<>();
        
        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);
        
        SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("creationType", "Scope", "CREATION_TYPE", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("InspectionCreationType");
        
        fields.add(creationType);
        
        BooleanField status = (BooleanField) FieldFactory.getDefaultField("status", "Active Status", "STATUS", FieldType.BOOLEAN);
        status.setTrueVal("Active");
        status.setFalseVal("Inactive");
        fields.add(status);
        
        SystemEnumField assignmentType = (SystemEnumField) FieldFactory.getDefaultField("assignmentType", "Scope Category", "ASSIGNMENT_TYPE", FieldType.SYSTEM_ENUM);
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
        
        module.setStateFlowEnabled(Boolean.TRUE);
        
        Criteria criteria = new Criteria();
        
        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", InspectionResponseContext.Status.PRE_OPEN.getIndex()+"", EnumOperators.ISN_T));
        
        Long criteriaID = CriteriaAPI.addCriteria(criteria);
        
        module.setCriteriaId(criteriaID);

        List<FacilioField> fields = new ArrayList<>();
        
        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);
        
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP,true);
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

    public static void addInspectionResponseRollUpToTemplate(ModuleBean modBean, FacilioModule inspectionResponse) throws Exception {
        FacilioModule qandaTemplate = modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE);
        FacilioUtil.throwIllegalArgumentException(qandaTemplate == null, "Q And A Template module cannot be null. This shouldn't happen");
        FacilioField responseParentField = modBean.getField("parent", inspectionResponse.getName());
        FacilioUtil.throwIllegalArgumentException(responseParentField == null, "Parent field of Inspection response cannot be null. This shouldn't happen");
        FacilioField totalResponsesField = modBean.getField("totalResponses", qandaTemplate.getName());
        FacilioUtil.throwIllegalArgumentException(totalResponsesField == null, "totalResponses field of template cannot be null. This shouldn't happen");
        FacilioField inspectionResponseStatusField = modBean.getField("status", inspectionResponse.getName());
        FacilioUtil.throwIllegalArgumentException(inspectionResponseStatusField == null, "status field of Inspection Response cannot be null. This shouldn't happen");

        RollUpField rollUpField = AddQAndAModules.constructRollUpField("Inspection Response RollUP", inspectionResponse, responseParentField, qandaTemplate, totalResponsesField, CriteriaAPI.getCondition(inspectionResponseStatusField, String.valueOf(InspectionResponseContext.Status.PRE_OPEN.getIndex()), PickListOperators.ISN_T));
        RollUpFieldUtil.addRollUpField(Collections.singletonList(rollUpField));
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
        
        LookupField assignmentGroup = (LookupField) FieldFactory.getDefaultField("assignmentGroup", "Team", "ASSIGNMENT_GROUP", FieldType.LOOKUP);
        assignmentGroup.setSpecialType("groups");
        fields.add(assignmentGroup);
        
        return fields;
	}
    
    public void addDefaultStateFlowForInspectionResponse(FacilioModule inspectionModule) throws Exception {
    	
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
    
    public void addDefaultStateFlowForInspectionTemplate(FacilioModule inspectionModule) throws Exception {
    	
    	FacilioStatus activeStatus = getFacilioStatus(inspectionModule, "active", "Active", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus inActiveStatus = getFacilioStatus(inspectionModule, "inactive", "Inactive", StatusType.CLOSED, Boolean.FALSE);
    	
    	 StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
         stateFlowRuleContext.setName("Default Stateflow");
         stateFlowRuleContext.setModuleId(inspectionModule.getModuleId());
         stateFlowRuleContext.setModule(inspectionModule);
         stateFlowRuleContext.setActivityType(EventType.CREATE);
         stateFlowRuleContext.setExecutionOrder(1);
         stateFlowRuleContext.setStatus(true);
         stateFlowRuleContext.setDefaltStateFlow(true);
         stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
         stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
         WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);
         
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Inactivate", activeStatus, inActiveStatus,TransitionType.NORMAL,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Activate", inActiveStatus, activeStatus,TransitionType.NORMAL,null);
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
