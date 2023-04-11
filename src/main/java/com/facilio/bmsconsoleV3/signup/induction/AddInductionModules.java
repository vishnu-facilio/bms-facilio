package com.facilio.bmsconsoleV3.signup.induction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

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
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext.TransitionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.signup.AddQAndAModules;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

public class AddInductionModules extends BaseModuleConfig {

    public AddInductionModules() throws Exception {
        setModuleName(FacilioConstants.Induction.INDUCTION_RESPONSE);
    }

	@Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        List<FacilioModule> modules = new ArrayList<>();
        
        FacilioModule induction = constructInduction(modBean);
        modules.add(induction);
        FacilioModule InductionResponseModule = constructInductionResponse(modBean, induction);
        modules.add(InductionResponseModule);
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
//        if(!SignupUtil.maintenanceAppSignup()) {
//            addScoping(induction);
//        }
//        long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
//        addScopingForMaintenanceApp(induction,applicationScopingId);

        addInductionResponseRollUpToTemplate(Constants.getModBean(), InductionResponseModule);
        
        
        SignupUtil.addNotesAndAttachmentModule(InductionResponseModule);
        
//        addMultiEnumSiteLookupField(modBean, induction);

        List<FacilioModule> modules1 = new ArrayList<>();
        
        modules1.add(constructInductionTriggers(modBean, induction));
        
        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules1);
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();
        
        modules1 = new ArrayList<>();
        
        modules1.add(constructInductionTriggersInclExcl(modBean));
        
        addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules1);
        addModuleChain1.execute();
        
        addDefaultStateFlowForInductionTemplate(induction);
        addDefaultStateFlowForInductionResponse(InductionResponseModule);
        if(!SignupUtil.maintenanceAppSignup()) {
            addDefaultFormForInductionTemplate(induction);
        }
        addDefaultFormForInductionTemplateMaintenanceApp(induction);
        addActivityModuleForInductionResponse(InductionResponseModule);
        
//        addDefaultScheduleJobs();
    }

    public void addScopingForMaintenanceApp(FacilioModule module,long applicationScopingId) throws Exception {
        ScopingConfigContext scoping = new ScopingConfigContext();
        Criteria criteria = new Criteria();
        Condition condition = CriteriaAPI.getCondition(module.getName()+".sites", "com.facilio.modules.ContainsSiteValueGenerator", ScopeOperator.SCOPING_IS);
        condition.setColumnName("sites");  			// setting dummy column Name
        condition.setModuleName(module.getName());
        Condition condition1 = CriteriaAPI.getCondition(module.getName()+".siteApplyTo", Boolean.TRUE.toString(), BooleanOperators.IS);
        condition1.setColumnName("SITE_APPLY_TO");
        condition1.setModuleName(module.getName());
        criteria.addAndCondition(condition);
        criteria.addOrCondition(condition1);
        scoping.setScopingId(applicationScopingId);
        scoping.setModuleId(module.getModuleId());
        scoping.setCriteria(criteria);
        ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
    }

    public void addDefaultFormForInductionTemplateMaintenanceApp(FacilioModule Induction) throws Exception {
        // TODO Auto-generated method stub
        ApplicationContext maintenance = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioForm defaultForm = new FacilioForm();
        defaultForm.setName("standard_maintenance");
        defaultForm.setModule(Induction);
        defaultForm.setDisplayName("Standard");
        defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultForm.setShowInWeb(true);
        defaultForm.setAppId(maintenance.getId());
        defaultForm.setIsSystemForm(true);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(Induction.getName()));
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection configSection = new FormSection();
        configSection.setName("Induction Details");
        configSection.setSectionType(FormSection.SectionType.FIELDS);
        configSection.setShowLabel(true);
        List<FormField> configFields = new ArrayList<>();
        int seq = 0;
        FormField field = new FormField(fieldMap.get("siteApplyTo").getFieldId(), "siteApplyTo", FacilioField.FieldDisplayType.RADIO, "Apply To", FormField.Required.REQUIRED, ++seq, 1);
        field.setValue(Boolean.TRUE.toString());
        configFields.add(field);
        configFields.add(new FormField(fieldMap.get("sites").getFieldId(), "sites", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Sites", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
        configFields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, ++seq, 1));
        configFields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seq, 1));
        configSection.setFields(configFields);
        configSection.setSequenceNumber(1);
        sections.add(configSection);
        defaultForm.setSections(sections);
        defaultForm.setType(FacilioForm.Type.FORM);
        defaultForm.setIsSystemForm(true);
        defaultForm.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        FormsAPI.createForm(defaultForm, Induction);
        Map<Long, FormField> formFieldMap = defaultForm.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
        addApplyToSiteRule(defaultForm, fieldMap, formFieldMap);
    }
	public void addScoping(FacilioModule module) throws Exception {
		
		long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        
        ScopingConfigContext scoping = new ScopingConfigContext();
        Criteria criteria = new Criteria();
        
        Condition condition = CriteriaAPI.getCondition(module.getName()+".sites", "com.facilio.modules.ContainsSiteValueGenerator", ScopeOperator.SCOPING_IS);
        condition.setColumnName("sites");  			// setting dummy column Name
        condition.setModuleName(module.getName());
        
        Condition condition1 = CriteriaAPI.getCondition(module.getName()+".siteApplyTo", Boolean.TRUE.toString(), BooleanOperators.IS);
        condition1.setColumnName("SITE_APPLY_TO"); 
        condition1.setModuleName(module.getName());
        
        criteria.addAndCondition(condition);
        criteria.addOrCondition(condition1);
        
        scoping.setScopingId(applicationScopingId);
        scoping.setModuleId(module.getModuleId());
        scoping.setCriteria(criteria);
        
        ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
	}

	public void addActivityModuleForInductionResponse(FacilioModule inductionResponseModule) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = new FacilioModule(FacilioConstants.Induction.INDUCTION_RESPONSE_ACTIVITY,
                "Induction Response Activity",
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
        
        modBean.addSubModule(inductionResponseModule.getModuleId(), module.getModuleId());
	}

	public void addDefaultStateFlowForInductionTemplate(FacilioModule inductionModule) throws Exception {
    	
    	FacilioStatus activeStatus = getFacilioStatus(inductionModule, "active", "Active", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus inActiveStatus = getFacilioStatus(inductionModule, "inactive", "Inactive", StatusType.CLOSED, Boolean.FALSE);
    	
    	 StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
         stateFlowRuleContext.setName("Default Stateflow");
         stateFlowRuleContext.setModuleId(inductionModule.getModuleId());
         stateFlowRuleContext.setModule(inductionModule);
         stateFlowRuleContext.setActivityType(EventType.CREATE);
         stateFlowRuleContext.setExecutionOrder(1);
         stateFlowRuleContext.setStatus(true);
         stateFlowRuleContext.setDefaltStateFlow(true);
         stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
         stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
         WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);
         
         addStateflowTransitionContext(inductionModule, stateFlowRuleContext, "Deactivate", activeStatus, inActiveStatus,TransitionType.NORMAL,null,null);
         addStateflowTransitionContext(inductionModule, stateFlowRuleContext, "Activate", inActiveStatus, activeStatus,TransitionType.NORMAL,null,null);
    }

//	public void addDefaultScheduleJobs() throws Exception {
//		// TODO Auto-generated method stub
//
//		FacilioTimer.schedulePeriodicJob(AccountUtil.getCurrentOrg().getId(), "ScheduleInductionStatusChange", 50, 1800, "facilio");
//		
//		ScheduleInfo scheduleInfo = new ScheduleInfo();
//		scheduleInfo.setTimes(Collections.singletonList("00:00"));
//		scheduleInfo.setFrequencyType(FrequencyType.DAILY);
//		FacilioTimer.scheduleCalendarJob((long)BaseScheduleContext.ScheduleType.INDUCTION.getIndex(), "BaseSchedulerJob", DateTimeUtil.getCurrenTime(), scheduleInfo, "facilio");
//		
//		JSONObject props = new JSONObject();
//		props.put("saveAsV3", Boolean.TRUE);
//		BmsJobUtil.addJobProps((long)BaseScheduleContext.ScheduleType.INDUCTION.getIndex(), "BaseSchedulerJob", props);
//	}


	public void addDefaultFormForInductionTemplate(FacilioModule Induction) throws Exception {
		// TODO Auto-generated method stub
		
      ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
	  FacilioForm defaultForm = new FacilioForm();
      defaultForm.setName("standard");
      defaultForm.setModule(Induction);
      defaultForm.setDisplayName("Standard");
      defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
      defaultForm.setShowInWeb(true);
      defaultForm.setIsSystemForm(true);
      
      Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(Induction.getName()));
      
      List<FormSection> sections = new ArrayList<FormSection>();

      FormSection configSection = new FormSection();
      configSection.setName("Induction Details");
      configSection.setSectionType(FormSection.SectionType.FIELDS);
      configSection.setShowLabel(true);
      
      List<FormField> configFields = new ArrayList<>();
      
      int seq = 0;
      
      FormField field = new FormField(fieldMap.get("siteApplyTo").getFieldId(), "siteApplyTo", FacilioField.FieldDisplayType.RADIO, "Apply To", FormField.Required.REQUIRED, ++seq, 1);
      field.setValue(Boolean.TRUE.toString());
      configFields.add(field);
      configFields.add(new FormField(fieldMap.get("sites").getFieldId(), "sites", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Sites", FormField.Required.OPTIONAL, ++seq, 1,Boolean.TRUE));
      configFields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, ++seq, 1));
      configFields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seq, 1));

      configSection.setFields(configFields);
      
      configSection.setSequenceNumber(1);
      
      sections.add(configSection);
      
      defaultForm.setSections(sections);
      defaultForm.setType(FacilioForm.Type.FORM);
      defaultForm.setIsSystemForm(true);
      defaultForm.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
      FormsAPI.createForm(defaultForm, Induction);
      
      Map<Long, FormField> formFieldMap = defaultForm.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
      
      addApplyToSiteRule(defaultForm, fieldMap, formFieldMap);
	}
	
	public void addApplyToSiteRule(FacilioForm defaultForm,Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		addShowSitesFieldRule(defaultForm, fieldMap, formFieldMap);
		addHideSitesFieldRule(defaultForm, fieldMap, formFieldMap);
	}
	
	public void addHideSitesFieldRule(FacilioForm defaultForm,Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		  FormRuleContext hideRule = new FormRuleContext();
		  hideRule.setName("Multi Site Hide Rule");
		  hideRule.setDescription("Rule To Hide Multi Site field.");
		  hideRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
		  hideRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
		  hideRule.setFormId(defaultForm.getId());
		  hideRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("SITE_APPLY_TO","siteApplyTo", Boolean.TRUE.toString()+"", BooleanOperators.IS));
	      
	      hideRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("siteApplyTo").getId()).getId());
	      hideRule.setTriggerFields(Collections.singletonList(triggerField));
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext showAction = new FormRuleActionContext(); 
	      showAction.setActionType(FormActionType.HIDE_FIELD.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("sites").getId()).getId());
	      
	      showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(showAction);
	      
	      hideRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,hideRule);
			
		  chain.execute();
		
	}
	
	public void addShowSitesFieldRule(FacilioForm defaultForm,Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
		
		  FormRuleContext showRule = new FormRuleContext();
		  showRule.setName("Multi Site Show Rule");
		  showRule.setDescription("Rule To Show Multi Site field.");
		  showRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
		  showRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
		  showRule.setFormId(defaultForm.getId());
		  showRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
	      
	      Criteria criteria = new Criteria();
	      
	      criteria.addAndCondition(CriteriaAPI.getCondition("SITE_APPLY_TO","siteApplyTo", Boolean.FALSE.toString()+"", BooleanOperators.IS));
	      
	      showRule.setCriteria(criteria);
	      
	      FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
	      triggerField.setFieldId(formFieldMap.get(fieldMap.get("siteApplyTo").getId()).getId());
	      showRule.setTriggerFields(Collections.singletonList(triggerField));
	      
	      List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
	      
	      FormRuleActionContext showAction = new FormRuleActionContext(); 
	      showAction.setActionType(FormActionType.SHOW_FIELD.getVal());
	      
	      FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
	      
	      actionField.setFormFieldId(formFieldMap.get(fieldMap.get("sites").getId()).getId());
	      
	      showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
	      
	      actions.add(showAction);
	      
	      showRule.setActions(actions);
	      
	      FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
		  Context context = chain.getContext();
			
		  context.put(FormRuleAPI.FORM_RULE_CONTEXT,showRule);
			
		  chain.execute();
		
	}
	


	public FacilioModule constructInductionTriggersInclExcl(ModuleBean modBean) throws Exception {

		
		FacilioModule module = new FacilioModule(FacilioConstants.Induction.INDUCTION_TRIGGER_INCL_EXCL,
                "Induction Triggers Incl Excl",
                "Induction_Trigger_Include_Exclude_Resource",
                FacilioModule.ModuleType.SUB_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();
        
        LookupField template = (LookupField) FieldFactory.getDefaultField("inductionTemplate", "Induction Template", "INDUCTION_ID", FieldType.LOOKUP);
        template.setLookupModule(modBean.getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE));
        fields.add(template);
        
        LookupField trigger = (LookupField) FieldFactory.getDefaultField("inductionTrigger", "Induction Trigger", "TRIGGER_ID", FieldType.LOOKUP);
        trigger.setLookupModule(modBean.getModule(FacilioConstants.Induction.INDUCTION_TRIGGER));
        fields.add(trigger);
        
        LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Resource", "RESOURCE_ID", FieldType.LOOKUP);
        resource.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        fields.add(resource);
        
        BooleanField isIncl = (BooleanField) FieldFactory.getDefaultField("isInclude", "Is Include", "IS_INCLUDE", FieldType.BOOLEAN);
        fields.add(isIncl);
        
        module.setFields(fields);
        
        return module;
	}


	private FacilioModule constructInduction(ModuleBean modBean) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Induction.INDUCTION_TEMPLATE,
                                                "Induction Templates",
                                                "Induction_Templates",
                                                FacilioModule.ModuleType.Q_AND_A,
                                                modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE),
                                                true
                                                );
        
        module.setStateFlowEnabled(Boolean.TRUE);

        List<FacilioField> fields = new ArrayList<>();
        
        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);
        
        SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("creationType", "Scope", "CREATION_TYPE", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("InductionCreationType");
        
        fields.add(creationType);
        
        BooleanField status = (BooleanField) FieldFactory.getDefaultField("status", "Active Status", "STATUS", FieldType.BOOLEAN);
        status.setTrueVal("Active");
        status.setFalseVal("Inactive");
        fields.add(status);
        
        BooleanField siteApplyTo = (BooleanField) FieldFactory.getDefaultField("siteApplyTo", "Apply To", "SITE_APPLY_TO", FieldType.BOOLEAN);
        siteApplyTo.setTrueVal("All Sites");
        siteApplyTo.setFalseVal("Specific Sites");
        fields.add(siteApplyTo);
        
        MultiLookupField multiLookupSiteField = (MultiLookupField) FieldFactory.getDefaultField("sites", "Sites", null, FieldType.MULTI_LOOKUP);
        multiLookupSiteField.setDisplayType(FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        multiLookupSiteField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupSiteField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));

        fields.add(multiLookupSiteField);
        
        SystemEnumField assignmentType = (SystemEnumField) FieldFactory.getDefaultField("assignmentType", "Scope Category", "ASSIGNMENT_TYPE", FieldType.SYSTEM_ENUM);
        assignmentType.setEnumName("MultiResourceAssignmentType");
        
        fields.add(assignmentType);
        
        LookupField baseSpace = (LookupField) FieldFactory.getDefaultField("baseSpace", "Building", "BASE_SPACE", FieldType.LOOKUP);
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
        
        fields.addAll(getInductionCommonFieldList(modBean));
        
        module.setFields(fields);
        return module;
    }
	
	private FacilioModule constructInductionTriggers(ModuleBean modBean, FacilioModule Induction) throws Exception {
		
		FacilioModule module = new FacilioModule(FacilioConstants.Induction.INDUCTION_TRIGGER,
                "Induction Triggers",
                "Induction_Triggers",
                FacilioModule.ModuleType.SUB_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();
        
        FacilioField nameField = FieldFactory.getNameField(module);
        fields.add(nameField);
        
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(Induction);
        fields.add(parentField);
        
        NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Trigger Type", "TRIGGER_TYPE", FieldType.NUMBER);
        fields.add(type);
        
        NumberField scheduleID = (NumberField) FieldFactory.getDefaultField("scheduleId", "Schedule", "SCHEDULE_ID", FieldType.NUMBER);
        fields.add(scheduleID);

        module.setFields(fields);
        return module;
	}

    private FacilioModule constructInductionResponse (ModuleBean modBean, FacilioModule Induction) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Induction.INDUCTION_RESPONSE,
                "Inductions",
                "Induction_Responses",
                FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                modBean.getModule(FacilioConstants.QAndA.RESPONSE),
                true
        );
        
        module.setStateFlowEnabled(Boolean.TRUE);
        module.setDescription("Induction is a process of introducing and familiarizing new employees with the organization.");

        Criteria criteria = new Criteria();
        
        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", InductionResponseContext.Status.PRE_OPEN.getIndex()+"", EnumOperators.ISN_T));
        
        Long criteriaID = CriteriaAPI.addCriteria(criteria);
        
        module.setCriteriaId(criteriaID);

        List<FacilioField> fields = new ArrayList<>();
        
        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);
        
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(Induction);
        fields.add(parentField);
        
        FacilioField createdTime = (FacilioField) FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);
        
        LookupField vendor = (LookupField) FieldFactory.getDefaultField("vendor", "Vendor", "VENDOR_ID", FieldType.LOOKUP);
        vendor.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
        fields.add(vendor);
        
        FacilioField scheduledWorkStart = (FacilioField) FieldFactory.getDefaultField("scheduledWorkStart", "Scheduled Start", "SCHEDULED_WORK_START", FieldType.DATE_TIME);
        fields.add(scheduledWorkStart);
        
        FacilioField scheduledWorkEnd = (FacilioField) FieldFactory.getDefaultField("scheduledWorkEnd", "Scheduled End", "SCHDULED_WORK_END", FieldType.DATE_TIME);
        fields.add(scheduledWorkEnd);
        
        FacilioField actualWorkStart = (FacilioField) FieldFactory.getDefaultField("actualWorkStart", "Actual Start", "ACTUAL_WORK_START", FieldType.DATE_TIME);
        fields.add(actualWorkStart);
        
        FacilioField actualWorkEnd = (FacilioField) FieldFactory.getDefaultField("actualWorkEnd", "Actual End", "ACTUAL_WORK_END", FieldType.DATE_TIME);
        fields.add(actualWorkEnd);
        
        FacilioField  actualWorkDuration = (FacilioField) FieldFactory.getDefaultField("actualWorkDuration", "Actual Duration", "ACTUAL_WORK_DURATION", FieldType.NUMBER);
        fields.add(actualWorkDuration);
        
        FacilioField resumedWorkStart = (FacilioField) FieldFactory.getDefaultField("resumedWorkStart", "Resumed Work Start", "RESUMED_WORK_START", FieldType.DATE_TIME);
        fields.add(resumedWorkStart);
        
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
        status.setEnumName("InductionResponseStatus");
        fields.add(status);
        
        SystemEnumField sourceType = (SystemEnumField) FieldFactory.getDefaultField("sourceType", "Source", "SOURCE_TYPE", FieldType.SYSTEM_ENUM);
        sourceType.setEnumName("InductionResponseSourceType");
        fields.add(sourceType);
        
        fields.addAll(getInductionCommonFieldList(modBean));

        module.setFields(fields);
        return module;
    }

    public static void addInductionResponseRollUpToTemplate(ModuleBean modBean, FacilioModule InductionResponse) throws Exception {
        FacilioModule qandaTemplate = modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE);
        FacilioUtil.throwIllegalArgumentException(qandaTemplate == null, "Q And A Template module cannot be null. This shouldn't happen");
        FacilioField responseParentField = modBean.getField("parent", InductionResponse.getName());
        FacilioUtil.throwIllegalArgumentException(responseParentField == null, "Parent field of Induction response cannot be null. This shouldn't happen");
        FacilioField totalResponsesField = modBean.getField("totalResponses", qandaTemplate.getName());
        FacilioUtil.throwIllegalArgumentException(totalResponsesField == null, "totalResponses field of template cannot be null. This shouldn't happen");
        FacilioField InductionResponseStatusField = modBean.getField("status", InductionResponse.getName());
        FacilioUtil.throwIllegalArgumentException(InductionResponseStatusField == null, "status field of Induction Response cannot be null. This shouldn't happen");

        RollUpField rollUpField = AddQAndAModules.constructRollUpField("Induction Response RollUP", InductionResponse, responseParentField, qandaTemplate, totalResponsesField, CriteriaAPI.getCondition(InductionResponseStatusField, String.valueOf(InductionResponseContext.Status.PRE_OPEN.getIndex()), PickListOperators.ISN_T));
        RollUpFieldUtil.addRollUpField(Collections.singletonList(rollUpField));
    }

    public List<FacilioField> getInductionCommonFieldList(ModuleBean modBean) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		
        LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Space/Asset", "RESOURCE", FieldType.LOOKUP);
        resource.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        fields.add(resource);
        
        LookupField assignedTo = (LookupField) FieldFactory.getDefaultField("people", "Assigned To", "ASSIGNED_TO", FieldType.LOOKUP);
        assignedTo.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(assignedTo);
        
        return fields;
	}
    
    public void addDefaultStateFlowForInductionResponse(FacilioModule inductionModule) throws Exception {
    	
    	FacilioStatus createdStatus = getFacilioStatus(inductionModule, "created", "Created", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus wipStatus = getFacilioStatus(inductionModule, "inProgress", "In Progress", StatusType.OPEN, Boolean.TRUE);
    	FacilioStatus resolvedStatus = getFacilioStatus(inductionModule, "completed", "Completed", StatusType.OPEN, Boolean.FALSE);
    	FacilioStatus closed = getFacilioStatus(inductionModule, "closed", "Closed", StatusType.CLOSED, Boolean.FALSE);
    	
    	 StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
         stateFlowRuleContext.setName("Default Stateflow");
         stateFlowRuleContext.setModuleId(inductionModule.getModuleId());
         stateFlowRuleContext.setModule(inductionModule);
         stateFlowRuleContext.setActivityType(EventType.CREATE);
         stateFlowRuleContext.setExecutionOrder(1);
         stateFlowRuleContext.setStatus(true);
         stateFlowRuleContext.setDefaltStateFlow(true);
         stateFlowRuleContext.setDefaultStateId(createdStatus.getId());
         stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
         WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);
         
         Criteria wipCtriteria = new Criteria();
         wipCtriteria.addAndCondition(CriteriaAPI.getCondition("RESPONSE_STATUS", "responseStatus", ResponseContext.ResponseStatus.PARTIALLY_ANSWERED.getIndex()+"", EnumOperators.IS));
         
         
         Criteria completionCtriteria = new Criteria();
         completionCtriteria.addAndCondition(CriteriaAPI.getCondition("RESPONSE_STATUS", "responseStatus", ResponseContext.ResponseStatus.COMPLETED.getIndex()+"", EnumOperators.IS));
         
         ActionContext startTimeaction = getUpdateActualStartTimeField();
         ActionContext endTimeaction = getUpdateActualEndTimeField();
         
         addStateflowTransitionContext(inductionModule, stateFlowRuleContext, "Start Induction", createdStatus, wipStatus,TransitionType.CONDITIONED,wipCtriteria,null);
         addStateflowTransitionContext(inductionModule, stateFlowRuleContext, "End Induction", wipStatus, resolvedStatus,TransitionType.CONDITIONED,completionCtriteria,null);
         addStateflowTransitionContext(inductionModule, stateFlowRuleContext, "Close", resolvedStatus, closed,TransitionType.NORMAL,null,null);
         
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
    
    private ActionContext getUpdateActualStartTimeField() {
		// TODO Auto-generated method stub
    	ActionContext action = new ActionContext();
    	
    	action.setActionType(ActionType.FIELD_CHANGE);
    	
    	JSONObject templateJson = new JSONObject();
    	JSONObject fieldUpdateObject = new JSONObject();
    	
    	fieldUpdateObject.put("columnName", "Induction_Responses.ACTUAL_WORK_START");
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
    	
    	fieldUpdateObject.put("columnName", "Induction_Responses.ACTUAL_WORK_END");
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

/*    @Override
    public List<FacilioForm> getModuleForms() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule  inductionResponseModule = modBean.getModule(FacilioConstants.Induction.INDUCTION_RESPONSE);


        FacilioForm inductionForm = new FacilioForm();
        inductionForm.setName("default_" + FacilioConstants.Induction.INDUCTION_RESPONSE + "_web");
        inductionForm.setModule(inductionResponseModule);
        inductionForm.setDisplayName("Standard");
        inductionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        inductionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        inductionForm.setShowInWeb(true);

        List<FormField> inductionFormFields = new ArrayList<>();
        int i = 1;
        inductionFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.OPTIONAL, i++, 1));
        inductionFormFields.add(new FormField("parent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parent", FormField.Required.OPTIONAL, i++, 1));
        inductionFormFields.add(new FormField("createdTime", FacilioField.FieldDisplayType.DATETIME, "Created Time", FormField.Required.REQUIRED, "building", i++, 2));
        inductionFormFields.add(new FormField("scheduledWorkStart", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start", FormField.Required.OPTIONAL, "site", i++, 3));
        inductionFormFields.add(new FormField("scheduledWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Scheduled End", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("actualWorkStart", FacilioField.FieldDisplayType.DATETIME, "Actual Start", FormField.Required.OPTIONAL, i++, 3));
        inductionFormFields.add(new FormField("actualWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Actual End", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("actualWorkDuration", FacilioField.FieldDisplayType.DATETIME, "Actual Duration", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("status", FacilioField.FieldDisplayType.DECISION_BOX, "Response Status", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("sourceType", FacilioField.FieldDisplayType.DECISION_BOX, "Source", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space/Asset", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("people", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Assigned To", FormField.Required.OPTIONAL, i++, 2));
        inductionForm.setFields(inductionFormFields);

        FormSection section = new FormSection("Default", 1, inductionFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        inductionForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(inductionForm);
    }
*/
}
