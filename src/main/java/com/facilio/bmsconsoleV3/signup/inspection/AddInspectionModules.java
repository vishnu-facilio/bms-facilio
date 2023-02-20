package com.facilio.bmsconsoleV3.signup.inspection;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
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
import com.facilio.bmsconsoleV3.context.inspection.InspectionCategoryContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionPriorityContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
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
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.ScheduleInfo.FrequencyType;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

public class AddInspectionModules extends BaseModuleConfig {

	public AddInspectionModules() throws Exception {
		setModuleName(FacilioConstants.Inspection.INSPECTION_RESPONSE);
	}

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        List<FacilioModule> modules = new ArrayList<>();
        
        FacilioModule inspectionCategoryModule = constructInspectionCategory();
        FacilioModule inspectionPriorityModule = constructInspectionPriority();
        modules.add(inspectionCategoryModule);
        modules.add(inspectionPriorityModule);
        
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
//        if(!SignupUtil.maintenanceAppSignup()) {
//			addScoping(inspection);
//		}
//		long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
//		addScopingMaintenanceApp(inspection,applicationScopingId);

        SignupUtil.addNotesAndAttachmentModule(inspectionResponseModule);
        
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
        if(!SignupUtil.maintenanceAppSignup()) {
			addDefaultFormForInspectionTemplate(inspection);
		}
		addDefaultFormForInspectionTemplateMaintenanceApp(inspection);
        addDefaultScheduleJobs();
        
        addDefaultInspectionPriorities(inspectionPriorityModule,modBean);
        addDefaultInspectionCategories(inspectionCategoryModule,modBean);
        
        
        addActivityModuleForInspectionResponse(inspectionResponseModule);
	}

	public void addScopingMaintenanceApp(FacilioModule module,long applicationScopingId) throws Exception {
		ScopingConfigContext scoping = new ScopingConfigContext();
		Criteria criteria = new Criteria();
		Condition condition = CriteriaAPI.getCondition(module.getName()+".siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
		condition.setColumnName(Constants.getModBean().getField("siteId", module.getName()).getCompleteColumnName());
		condition.setModuleName(module.getName());
		Condition condition1 = CriteriaAPI.getCondition(module.getName()+".sites", "com.facilio.modules.ContainsSiteValueGenerator", ScopeOperator.SCOPING_IS);
		condition1.setColumnName("sites");  			// setting dummy column Name
		condition1.setModuleName(module.getName());
		criteria.addAndCondition(condition);
		criteria.addOrCondition(condition1);
		scoping.setScopingId(applicationScopingId);
		scoping.setModuleId(module.getModuleId());
		scoping.setCriteria(criteria);
		ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
	}

	public void addScoping(FacilioModule module) throws Exception {
		
		long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        
        ScopingConfigContext scoping = new ScopingConfigContext();
        Criteria criteria = new Criteria();
        
        Condition condition = CriteriaAPI.getCondition(module.getName()+".siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
        condition.setColumnName(Constants.getModBean().getField("siteId", module.getName()).getCompleteColumnName());
        condition.setModuleName(module.getName());
        
        Condition condition1 = CriteriaAPI.getCondition(module.getName()+".sites", "com.facilio.modules.ContainsSiteValueGenerator", ScopeOperator.SCOPING_IS);
        condition1.setColumnName("sites");  			// setting dummy column Name
        condition1.setModuleName(module.getName());
        
        criteria.addAndCondition(condition);
        criteria.addOrCondition(condition1);
        
        scoping.setScopingId(applicationScopingId);
        scoping.setModuleId(module.getModuleId());
        scoping.setCriteria(criteria);
        
        ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
	}


	public void addActivityModuleForInspectionResponse(FacilioModule inspectionResponseModule) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_RESPONSE_ACTIVITY,
                "Inspection Response Activity",
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
		
        modBean.addSubModule(inspectionResponseModule.getModuleId(), module.getModuleId());
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
		props.put("saveAsV3PreCreate", Boolean.TRUE);
		BmsJobUtil.addJobProps((long)BaseScheduleContext.ScheduleType.INSPECTION.getIndex(), "BaseSchedulerJob", props);

		ScheduleInfo scheduleInfo2 = new ScheduleInfo();
		scheduleInfo2.setTimes(Collections.singletonList("08:00"));
		scheduleInfo2.setFrequencyType(FrequencyType.DAILY);
		FacilioTimer.scheduleCalendarJob((long)BaseScheduleContext.ScheduleType.INSPECTION.getIndex(), "inspectionMonitoringToolJob", DateTimeUtil.getCurrenTime(), scheduleInfo2, "facilio");
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
	  defaultForm.setIsSystemForm(true);
      
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
      fields.add(new FormField(fieldMap.get("sites").getFieldId(), "sites", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Sites", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
      fields.add(new FormField(fieldMap.get("buildings").getFieldId(), "buildings", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Buildings", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
      
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
	  defaultForm.setType(FacilioForm.Type.FORM);
	  defaultForm.setIsSystemForm(true);
      FormsAPI.createForm(defaultForm, inspection);
      
      Map<Long, FormField> formFieldMap = defaultForm.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
      
      addSingleAssetSelectRule(defaultForm, fieldMap, formFieldMap);
      addMultipleAssetSelectRule(defaultForm, fieldMap, formFieldMap);
      
      addBuildingFilteringWithSiteRule(defaultForm, fieldMap, formFieldMap);
      
      addAssetCategoryShowRule(defaultForm, fieldMap, formFieldMap);
      addAssetCategoryHideRule(defaultForm, fieldMap, formFieldMap);
      addSpaceCategoryShowRule(defaultForm, fieldMap, formFieldMap);
      addSpaceCategoryHideRule(defaultForm, fieldMap, formFieldMap);
      
      addOnLoadRule(defaultForm, fieldMap, formFieldMap);
	}
	
	private void addBuildingFilteringWithSiteRule(FacilioForm defaultForm, Map<String, FacilioField> fieldMap, Map<Long, FormField> formFieldMap) throws Exception {
		// TODO Auto-generated method stub
		
		  FormRuleContext singleRule = new FormRuleContext();
	      singleRule.setName("Set Building Filter");
	      singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
	      singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
	      singleRule.setFormId(defaultForm.getId());
	      singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("sites").getId()).getId());
	      singleRule.setTriggerFields(Collections.singletonList(triggerField));
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext filterAction = new FormRuleActionContext(); 
	      filterAction.setActionType(FormActionType.APPLY_FILTER.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("buildings").getId()).getId());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("SITE_ID","site", "${inspectionTemplate.sites.id}", NumberOperators.EQUALS));
	      
	      actionField.setCriteria(criteria);
	      
	      filterAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(filterAction);
	      
	      singleRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
			
		  chain.execute();
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
	      
	      List<FormRuleActionFieldsContext> actionFields = new ArrayList<FormRuleActionFieldsContext>();
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
	      
	      JSONObject json = new JSONObject();
	      
	      json.put("show", Boolean.FALSE);
	      json.put("values", "[5,6,8]");
	      
	      actionField.setActionMeta(json.toJSONString());
	      
	      actionFields.add(actionField);
	      
	      filterAction.setFormRuleActionFieldsContext(actionFields);
	      
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

	public void addDefaultFormForInspectionTemplateMaintenanceApp(FacilioModule inspection) throws Exception {
		// TODO Auto-generated method stub
		ApplicationContext maintenance = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioForm defaultForm = new FacilioForm();
		defaultForm.setName("standard_maintenance");
		defaultForm.setModule(inspection);
		defaultForm.setDisplayName("Standard");
		defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		defaultForm.setShowInWeb(true);
		defaultForm.setAppId(maintenance.getId());
		defaultForm.setIsSystemForm(true);
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
		fields.add(new FormField(fieldMap.get("sites").getFieldId(), "sites", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Sites", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
		fields.add(new FormField(fieldMap.get("buildings").getFieldId(), "buildings", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Buildings", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
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
		defaultForm.setType(FacilioForm.Type.FORM);
		defaultForm.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
		defaultForm.setIsSystemForm(true);
		FormsAPI.createForm(defaultForm, inspection);
		Map<Long, FormField> formFieldMap = defaultForm.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
		addSingleAssetSelectRule(defaultForm, fieldMap, formFieldMap);
		addMultipleAssetSelectRule(defaultForm, fieldMap, formFieldMap);
		addBuildingFilteringWithSiteRule(defaultForm, fieldMap, formFieldMap);
		addAssetCategoryShowRule(defaultForm, fieldMap, formFieldMap);
		addAssetCategoryHideRule(defaultForm, fieldMap, formFieldMap);
		addSpaceCategoryShowRule(defaultForm, fieldMap, formFieldMap);
		addSpaceCategoryHideRule(defaultForm, fieldMap, formFieldMap);
		addOnLoadRule(defaultForm, fieldMap, formFieldMap);
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
	      
	      List<FormRuleActionFieldsContext> showFields = new ArrayList<FormRuleActionFieldsContext>();
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("resource").getId()).getId());
	      
	      showFields.add(actionField);
	      
	      FormRuleActionFieldsContext actionField1 = new FormRuleActionFieldsContext();	
	      
	      actionField1.setFormFieldId(formFieldMap.get(fieldMap.get("siteId").getId()).getId());
	      
	      showFields.add(actionField1);

	      showAction.setFormRuleActionFieldsContext(showFields);
	      
	      actions.add(showAction);
	      
	      
	      FormRuleActionContext  hideAction = new FormRuleActionContext(); 
	      hideAction.setActionType(FormActionType.HIDE_FIELD.getVal());
	      
	      List<FormRuleActionFieldsContext> hidefields = new ArrayList<FormRuleActionFieldsContext>();
	      
	      FormRuleActionFieldsContext hideField1 = new FormRuleActionFieldsContext();
	      hideField1.setFormFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
	      hidefields.add(hideField1);
	      
	      FormRuleActionFieldsContext hideField2 = new FormRuleActionFieldsContext();
	      hideField2.setFormFieldId(formFieldMap.get(fieldMap.get("buildings").getId()).getId());
          hidefields.add(hideField2);

	      FormRuleActionFieldsContext hideField3 = new FormRuleActionFieldsContext();
	      hideField3.setFormFieldId(formFieldMap.get(fieldMap.get("assetCategory").getId()).getId());
	      hidefields.add(hideField3);
	      
	      FormRuleActionFieldsContext hideField4 = new FormRuleActionFieldsContext();
	      hideField4.setFormFieldId(formFieldMap.get(fieldMap.get("spaceCategory").getId()).getId());
	      hidefields.add(hideField4);
	      
	      FormRuleActionFieldsContext hideField5 = new FormRuleActionFieldsContext();
	      hideField5.setFormFieldId(formFieldMap.get(fieldMap.get("sites").getId()).getId());
	      hidefields.add(hideField5);
	      
	      hideAction.setFormRuleActionFieldsContext(hidefields);
	      
	      actions.add(hideAction);

		  FormRuleActionContext mandatoryAction = new FormRuleActionContext();
		  mandatoryAction.setActionType(FormActionType.SET_MANDATORY.getVal());

		 List<FormRuleActionFieldsContext> mandatoryFields = new ArrayList<FormRuleActionFieldsContext>();

		 FormRuleActionFieldsContext mandatoryField = new FormRuleActionFieldsContext();

		 mandatoryField.setFormFieldId(formFieldMap.get(fieldMap.get("resource").getId()).getId());

		 mandatoryFields.add(mandatoryField);

		 FormRuleActionFieldsContext mandatoryField1 = new FormRuleActionFieldsContext();

		 mandatoryField1.setFormFieldId(formFieldMap.get(fieldMap.get("siteId").getId()).getId());

		 mandatoryFields.add(mandatoryField1);

		 mandatoryAction.setFormRuleActionFieldsContext(mandatoryFields);

		 actions.add(mandatoryAction);

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
	      
	      List<FormRuleActionFieldsContext> hideFields = new ArrayList<FormRuleActionFieldsContext>();
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("resource").getId()).getId());
	      
	      hideFields.add(actionField);
	      
	      FormRuleActionFieldsContext actionField1 = new FormRuleActionFieldsContext();	
	      
	      actionField1.setFormFieldId(formFieldMap.get(fieldMap.get("siteId").getId()).getId());
	      
	      hideFields.add(actionField1);
	      
	      hideAction.setFormRuleActionFieldsContext(hideFields);
	      
	      actions.add(hideAction);
	      
	      
	      FormRuleActionContext  showAction = new FormRuleActionContext(); 
	      showAction.setActionType(FormActionType.SHOW_FIELD.getVal());
	      
	      List<FormRuleActionFieldsContext> showfields = new ArrayList<FormRuleActionFieldsContext>();
	      
	      FormRuleActionFieldsContext showField1 = new FormRuleActionFieldsContext();
	      showField1.setFormFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
	      showfields.add(showField1);
	      
	      FormRuleActionFieldsContext showField2 = new FormRuleActionFieldsContext();
	      showField2.setFormFieldId(formFieldMap.get(fieldMap.get("buildings").getId()).getId());
	      showfields.add(showField2);
	      
	      FormRuleActionFieldsContext showField3 = new FormRuleActionFieldsContext();
	      showField3.setFormFieldId(formFieldMap.get(fieldMap.get("sites").getId()).getId());
	      showfields.add(showField3);
	      
	      showAction.setFormRuleActionFieldsContext(showfields);
	      
	      actions.add(showAction);

		FormRuleActionContext  mandatoryactions = new FormRuleActionContext();
		mandatoryactions.setActionType(FormActionType.SET_MANDATORY.getVal());

		List<FormRuleActionFieldsContext> mandatoryfields = new ArrayList<FormRuleActionFieldsContext>();

		FormRuleActionFieldsContext mandatoryField1 = new FormRuleActionFieldsContext();
		mandatoryField1.setFormFieldId(formFieldMap.get(fieldMap.get("sites").getId()).getId());
		mandatoryfields.add(mandatoryField1);


		FormRuleActionFieldsContext mandatoryField2 = new FormRuleActionFieldsContext();
		mandatoryField2.setFormFieldId(formFieldMap.get(fieldMap.get("assignmentType").getId()).getId());
		mandatoryfields.add(mandatoryField2);

		FormRuleActionFieldsContext mandatoryField3 = new FormRuleActionFieldsContext();

		mandatoryField3.setFormFieldId(formFieldMap.get(fieldMap.get("spaceCategory").getId()).getId());

		mandatoryfields.add(mandatoryField3);

		FormRuleActionFieldsContext mandatoryField4 = new FormRuleActionFieldsContext();

		mandatoryField4.setFormFieldId(formFieldMap.get(fieldMap.get("assetCategory").getId()).getId());

		mandatoryfields.add(mandatoryField4);

		mandatoryactions.setFormRuleActionFieldsContext(mandatoryfields);

		actions.add(mandatoryactions);

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
		
		FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("categoryName", "Name", "NAME", FieldType.STRING);
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
                                                modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE),
												true
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
		multiLookupSiteField.setDisplayType(FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        multiLookupSiteField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupSiteField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        
        MultiLookupField multiLookupBuildingField = (MultiLookupField) FieldFactory.getDefaultField("buildings", "Buildings", null, FieldType.MULTI_LOOKUP);
        multiLookupBuildingField.setDisplayType(FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        multiLookupBuildingField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupBuildingField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.BUILDING));
        
        
        fields.add(multiLookupSiteField);
        
        fields.add(multiLookupBuildingField);
        
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
                modBean.getModule(FacilioConstants.QAndA.RESPONSE),
				true
        );
        
        module.setStateFlowEnabled(Boolean.TRUE);
        
        Criteria criteria = new Criteria();
        
        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", InspectionResponseContext.Status.PRE_OPEN.getIndex()+"", EnumOperators.ISN_T));
        
        Long criteriaID = CriteriaAPI.addCriteria(criteria);
        
        module.setCriteriaId(criteriaID);

        List<FacilioField> fields = new ArrayList<>();
        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);
        
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(inspection);
        fields.add(parentField);
        
        FacilioField createdTime = (FacilioField) FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);

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
        
        NumberField  actualWorkDuration = (NumberField) FieldFactory.getDefaultField("actualWorkDuration", "Actual Duration", "ACTUAL_WORK_DURATION", FieldType.NUMBER);
        actualWorkDuration.setMetric(Metric.DURATION.getMetricId());
        actualWorkDuration.setUnitId(Unit.SEC.getUnitId());
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

		FacilioField slaPolicyId = FieldFactory.getField("slaPolicyId", "SLA Policy Id", "SLA_POLICY_ID", module, FieldType.NUMBER);
		slaPolicyId.setDefault(true);
		slaPolicyId.setDisplayType(FieldDisplayType.NUMBER);
		fields.add(slaPolicyId);
        
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
		
        LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Space/Asset", "RESOURCE", FieldType.LOOKUP);
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
    	FacilioStatus wipStatus = getFacilioStatus(inspectionModule, "inProgress", "In Progress", StatusType.OPEN, Boolean.TRUE);
    	FacilioStatus resolvedStatus = getFacilioStatus(inspectionModule, "completed", "Completed", StatusType.OPEN, Boolean.FALSE);
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
         
         Criteria wipCtriteria = new Criteria();
         wipCtriteria.addAndCondition(CriteriaAPI.getCondition("RESPONSE_STATUS", "responseStatus", ResponseContext.ResponseStatus.PARTIALLY_ANSWERED.getIndex()+"", EnumOperators.IS));
         
         
         Criteria completionCtriteria = new Criteria();
         completionCtriteria.addAndCondition(CriteriaAPI.getCondition("RESPONSE_STATUS", "responseStatus", ResponseContext.ResponseStatus.COMPLETED.getIndex()+"", EnumOperators.IS));
         
         ActionContext startTimeaction = getUpdateActualStartTimeField();
         ActionContext endTimeaction = getUpdateActualEndTimeField();
         
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Assign", createdStatus, assignedStatus,TransitionType.CONDITIONED,assignmentCriteria,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Start Inspection", assignedStatus, wipStatus,TransitionType.CONDITIONED,wipCtriteria,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "End Inspection", wipStatus, resolvedStatus,TransitionType.CONDITIONED,completionCtriteria,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Close", resolvedStatus, closed,TransitionType.NORMAL,null,null);
		 addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Complete Inspection", assignedStatus,resolvedStatus,TransitionType.CONDITIONED,completionCtriteria,null);
    }
    
    private ActionContext getUpdateActualStartTimeField() {
		// TODO Auto-generated method stub
    	ActionContext action = new ActionContext();
    	
    	action.setActionType(ActionType.FIELD_CHANGE);
    	
    	JSONObject templateJson = new JSONObject();
    	JSONObject fieldUpdateObject = new JSONObject();
    	
    	fieldUpdateObject.put("columnName", "Inspection_Responses.ACTUAL_WORK_START");
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
    	
    	fieldUpdateObject.put("columnName", "Inspection_Responses.ACTUAL_WORK_END");
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
         
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Deactivate", activeStatus, inActiveStatus,TransitionType.NORMAL,null,null);
         addStateflowTransitionContext(inspectionModule, stateFlowRuleContext, "Activate", inActiveStatus, activeStatus,TransitionType.NORMAL,null,null);
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


	@Override
	public List<GlimpseContext> getModuleGlimpse() throws Exception{

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE));
		List<String> fieldNames = new ArrayList<>();
		fieldNames.add("resource");
		fieldNames.add("name");
		fieldNames.add("status");
		fieldNames.add("totalScore");
		fieldNames.add("assignedTo");

		GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

		List<GlimpseContext> glimpseList = new ArrayList<>();
		glimpseList.add(glimpse);

		return glimpseList;

	}


/*	@Override
    public List<FacilioForm> getModuleForms() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule inspectionResponseModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);

		FacilioForm inspectionForm = new FacilioForm();
		inspectionForm.setName("default_"+FacilioConstants.Inspection.INSPECTION_RESPONSE+"_web");
		inspectionForm.setModule(inspectionResponseModule);
		inspectionForm.setDisplayName("Standard");
		inspectionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
		inspectionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		inspectionForm.setShowInWeb(true);

		List<FormField> inspectionFormFields = new ArrayList<>();
		int i = 1;
		inspectionFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.OPTIONAL, i++, 1));
		inspectionFormFields.add(new FormField("parent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parent", FormField.Required.OPTIONAL, i++, 1));
		inspectionFormFields.add(new FormField("createdTime", FacilioField.FieldDisplayType.DATETIME, "Created Time", FormField.Required.REQUIRED,  "building",i++, 2));
		inspectionFormFields.add(new FormField("scheduledWorkStart", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start", FormField.Required.OPTIONAL, "site",i++, 3));
		inspectionFormFields.add(new FormField("scheduledWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Scheduled End", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("actualWorkStart", FacilioField.FieldDisplayType.DATETIME, "Actual Start", FormField.Required.OPTIONAL, i++, 3));
		inspectionFormFields.add(new FormField("actualWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Actual End", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("actualWorkDuration", FacilioField.FieldDisplayType.DATETIME, "Actual Duration", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("status", FacilioField.FieldDisplayType.DECISION_BOX, "Response Status", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("sourceType", FacilioField.FieldDisplayType.DECISION_BOX, "Source", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space/Asset", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("assignedTo", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Assigned To", FormField.Required.OPTIONAL, i++, 2));
		inspectionFormFields.add(new FormField("assignmentGroup", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Team", FormField.Required.OPTIONAL, i++, 2));
		inspectionForm.setFields(inspectionFormFields);

		FormSection section = new FormSection("Default", 1, inspectionFormFields, false);
		section.setSectionType(FormSection.SectionType.FIELDS);
		inspectionForm.setSections(Collections.singletonList(section));

		return Collections.singletonList(inspectionForm);
	}
*/
}
