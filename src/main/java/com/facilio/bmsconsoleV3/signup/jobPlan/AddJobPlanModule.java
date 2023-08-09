package com.facilio.bmsconsoleV3.signup.jobPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
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
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;

public class AddJobPlanModule extends BaseModuleConfig{
	
	public static List<String> jobPlanSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP, FacilioConstants.ApplicationLinkNames.IWMS_APP);
	
    public AddJobPlanModule(){
        setModuleName(FacilioConstants.ContextNames.JOB_PLAN);
    }
    
    @Override
    public void addData() throws Exception {
    	// TODO Auto-generated method stub
    	FacilioModule jobPlanModule = addJobPlanModule(Constants.getModBean());
    	
    	FacilioModule jobPlanSectionModule = addJobPlanSectionModule(Constants.getModBean(),jobPlanModule);
    	FacilioModule jobPlanTaskModule = addJobPlanTaskModule(Constants.getModBean(), jobPlanModule, jobPlanSectionModule);
    	
    	addActivityModuleForJobPlan(jobPlanModule);
    	
    	addJobPlanAttachmentsModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), jobPlanModule);
    	constructJobPlanNotesModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), jobPlanModule);
    	
    	addJobPlanTaskInputOptionsModule(Constants.getModBean(), jobPlanTaskModule);
    	
    	addJobPlanSectionInputOptionsModule(Constants.getModBean(), jobPlanSectionModule);
    	
    	addJobPlanLookupToWoModule(jobPlanModule);
        addGroupField(jobPlanModule);

    }
    
    private void addJobPlanLookupToWoModule(FacilioModule jobPlanModule) throws Exception {
    	
    	LookupField jobPlanField = (LookupField) FieldFactory.getField("jobPlan", "Job Plan", "JOB_PLAN_ID", Constants.getModBean().getModule(FacilioConstants.ContextNames.WORK_ORDER), FieldType.LOOKUP);
    	jobPlanField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
    	jobPlanField.setLookupModule(jobPlanModule);
        jobPlanField.setDefault(true);
        Constants.getModBean().addField(jobPlanField);
    }
    
    private FacilioModule addJobPlanTaskInputOptionsModule(ModuleBean modBean, FacilioModule jobPlanTaskModule) throws Exception {

        FacilioModule taskInputOptionsModule = ModuleFactory.getJobPlanTaskInputOptionsModule();

        List<FacilioField> fields = new ArrayList<>(FieldFactory.getJobPlanTaskInputOptionsFields().values());

        taskInputOptionsModule.setFields(fields);
        
        SignupUtil.addModules(taskInputOptionsModule);
        modBean.addSubModule(jobPlanTaskModule.getModuleId(), taskInputOptionsModule.getModuleId());

        return taskInputOptionsModule;
    }

    private FacilioModule addJobPlanSectionInputOptionsModule(ModuleBean modBean, FacilioModule jobPlanSectionModule) throws Exception {

        FacilioModule sectionInputOptionsModule = ModuleFactory.getJobPlanSectionInputOptionsModule();
        List<FacilioField> fields = new ArrayList<>(FieldFactory.getJobPlanSectionInputOptionsFields().values());
        sectionInputOptionsModule.setFields(fields);
        
        SignupUtil.addModules(sectionInputOptionsModule);
        modBean.addSubModule(jobPlanSectionModule.getModuleId(), sectionInputOptionsModule.getModuleId());

        return sectionInputOptionsModule;
    }
    
    
    private FacilioModule constructJobPlanNotesModule(ModuleBean modBean, long orgId, FacilioModule jobPlanModule) throws Exception {
        FacilioModule jobPlanNotesModule = new FacilioModule("jobplannotes", "JobPlan Notes",
                "JobPlan_Notes", FacilioModule.ModuleType.NOTES, null,
                null);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField createdTimeField = new FacilioField(jobPlanNotesModule, "createdTime", "Created Time",
                FacilioField.FieldDisplayType.DATETIME, "CREATED_TIME", FieldType.DATE_TIME,
                true, false, true, false);
        fields.add(createdTimeField);

        LookupField createdByField = SignupUtil.getLookupField(jobPlanNotesModule, null, "createdBy",
                "Created By", "CREATED_BY", "users", FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(createdByField);

        NumberField parentIdField = SignupUtil.getNumberField(jobPlanNotesModule,
                "parentId", "Parent", "PARENT_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(parentIdField);

        StringField titleField = SignupUtil.getStringField(jobPlanNotesModule,
                "title", "Title",  "TITLE", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, false,orgId);
        fields.add(titleField);

        StringField bodyField = SignupUtil.getStringField(jobPlanNotesModule,
                "body", "Body", "BODY", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false,orgId);
        fields.add(bodyField);

        StringField bodyHtmlField = SignupUtil.getStringField(jobPlanNotesModule,
                "bodyHTML", "Body HTML", "BODY_HTML", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false,orgId);
        fields.add(bodyHtmlField);

        LookupField parentNote = SignupUtil.getLookupField(jobPlanNotesModule, jobPlanNotesModule, "parentNote", "Parent Note",
                "PARENT_NOTE", null, FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(parentNote);


        jobPlanNotesModule.setFields(fields);
        
        SignupUtil.addModules(jobPlanNotesModule);
        
        modBean.addSubModule(jobPlanModule.getModuleId(), jobPlanNotesModule.getModuleId());

        return jobPlanNotesModule;
    }

    private FacilioModule addJobPlanAttachmentsModule(ModuleBean modBean, long orgId, FacilioModule jobPlanModule) throws Exception {
        FacilioModule jobPlanAttachmentsModule = new FacilioModule("jobplanattachments", "JobPlan Attachments", 
                "JobPlan_Attachments", FacilioModule.ModuleType.ATTACHMENTS, null,
                null);

        List<FacilioField> fields = new ArrayList<>();

        NumberField fieldIdField = SignupUtil.getNumberField(jobPlanAttachmentsModule, "fileId", "File ID",
                "FILE_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fieldIdField.setMainField(true);
        fields.add(fieldIdField);

        NumberField parentIdField = SignupUtil.getNumberField(jobPlanAttachmentsModule,
                "parentId", "Parent", "PARENT_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(parentIdField);

        FacilioField createdTimeField = SignupUtil.getNumberField(jobPlanAttachmentsModule,
                "createdTime", "Created Time","CREATED_TIME",
                FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(createdTimeField);

        FacilioField attachmentTypeField = SignupUtil.getNumberField(jobPlanAttachmentsModule,
                "type", "Type", "ATTACHMENT_TYPE",
                FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(attachmentTypeField);


        jobPlanAttachmentsModule.setFields(fields);
        
        SignupUtil.addModules(jobPlanAttachmentsModule);
        
        modBean.addSubModule(jobPlanModule.getModuleId(), jobPlanAttachmentsModule.getModuleId());

        return jobPlanAttachmentsModule;
    }
    
    public void addActivityModuleForJobPlan(FacilioModule jobPlanModule) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.JOB_PLAN_ACTIVITY,
                "Job Plan Activity",
                "JobPlanActivity",
                FacilioModule.ModuleType.ACTIVITY
                );

				
		List<FacilioField> fields = new ArrayList<>();
		
		NumberField parentId = (NumberField) FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
		fields.add(parentId);
		
		FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);
		
		fields.add(timefield);
		
		NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
		fields.add(type);
		
		LookupField doneBy = (LookupField) FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP,FieldDisplayType.LOOKUP_POPUP);
		doneBy.setSpecialType("users");
		fields.add(doneBy);
		
		FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING,FieldDisplayType.TEXTAREA);
		
		fields.add(info);
		
		module.setFields(fields);	
        
        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();
		
        modBean.addSubModule(jobPlanModule.getModuleId(), module.getModuleId());
	}
    
    private FacilioModule addJobPlanTaskModule(ModuleBean modBean, FacilioModule jobPlanModule, FacilioModule jobPlanSectionModule) throws Exception {
    	
    	FacilioModule taskModule = modBean.getModule(FacilioConstants.ContextNames.TASK);
    	
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.JOB_PLAN_TASK,
                                                "Job Plan Tasks",
                                                "JobPlan_Task",
                                                FacilioModule.ModuleType.SUB_ENTITY,
                                                taskModule
                                                );
        
        List<FacilioField> fields = new ArrayList<>();
        
        
        LookupField jobPlan = (LookupField) FieldFactory.getDefaultField("jobPlan", "Job Plan", "JOB_PLAN_ID", FieldType.LOOKUP);
        jobPlan.setLookupModule(jobPlanModule);
        fields.add(jobPlan);
        
        LookupField jobPlanSection = (LookupField) FieldFactory.getDefaultField("taskSection", "Task Section", "TASK_SECTION_ID", FieldType.LOOKUP,true);
        jobPlanSection.setLookupModule(jobPlanSectionModule);
        fields.add(jobPlanSection);
        
        
        SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("jobPlanTaskCategory", "Job Plan Task Category", "CATEGORY", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("MultiResourceAssignmentType");
        fields.add(creationType);
        
        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY_ID", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);
        
        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY_ID", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);
        
        
        module.setFields(fields);
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.execute();
        
        modBean.addSubModule(jobPlanModule.getModuleId(), module.getModuleId());
        
        modBean.addSubModule(jobPlanSectionModule.getModuleId(), module.getModuleId());
        
        return module;
    }
    
    private FacilioModule addJobPlanSectionModule(ModuleBean modBean, FacilioModule jobPlanModule) throws Exception {
    	
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.JOB_PLAN_SECTION,
                                                "Job Plan Section",
                                                "JobPlan_Task_Section",
                                                FacilioModule.ModuleType.SUB_ENTITY
                                                );
        
        List<FacilioField> fields = new ArrayList<>();
        
        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        
        LookupField jobPlan = (LookupField) FieldFactory.getDefaultField("jobPlan", "Job Plan", "JOB_PLAN_ID", FieldType.LOOKUP);
        jobPlan.setLookupModule(jobPlanModule);
        fields.add(jobPlan);
        
        SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("jobPlanSectionCategory", "Job Plan Section Category", "CATEGORY", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("MultiResourceAssignmentType");
        fields.add(creationType);
        
        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY_ID", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);
        
        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY_ID", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);
        
        NumberField seqNo = (NumberField) FieldFactory.getDefaultField("sequenceNumber", "Sequence Number", "SEQUENCE_NUMBER", FieldType.NUMBER);
        fields.add(seqNo);
        
        NumberField inputTypeField = (NumberField) FieldFactory.getDefaultField("inputType", "Input Type", "INPUT_TYPE", FieldType.NUMBER);
        fields.add(inputTypeField);
        
        fields.add(FieldFactory.getDefaultField("additionalInfoJsonStr", "Additional Info", "ADDITIONAL_INFO", FieldType.STRING));

        module.setFields(fields);
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.execute();
        
        modBean.addSubModule(jobPlanModule.getModuleId(), module.getModuleId());
        
        return module;
    }
    
    private FacilioModule addJobPlanModule(ModuleBean modBean) throws Exception {
    	
    	List<FacilioModule> jobplanModules = new ArrayList<>();
    	
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.JOB_PLAN,
                                                "Job Plan",
                                                "JobPlan",
                                                FacilioModule.ModuleType.BASE_ENTITY
                                                );

        module.setDescription("Create and manage templates that defines maintenance tasks, required planned inventory, labor to ensure safe, efficient, and effective execution of maintenance activities for planned work orders.");

        List<FacilioField> fields = new ArrayList<>();
        
        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        
        SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("jobPlanCategory", "Category", "CATEGORY", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("JPScopeAssignmentType");
        fields.add(creationType);
        
        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY_ID", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);
        
        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY_ID", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);
        
//        BooleanField isActive = (BooleanField) FieldFactory.getDefaultField("isActive", "Is Active", "IS_ACTIVE", FieldType.BOOLEAN);
//        fields.add(isActive);
//        
//        BooleanField isDisabled = (BooleanField) FieldFactory.getDefaultField("isDisabled", "Is Disabled", "IS_DISABLED", FieldType.BOOLEAN);
//        fields.add(isDisabled);
        
        SystemEnumField jpStatus = (SystemEnumField) FieldFactory.getDefaultField("jpStatus", "Job Plan Status", "JP_STATUS", FieldType.SYSTEM_ENUM);
        jpStatus.setEnumName("JobPlanStatus");
        
        fields.add(jpStatus);

        NumberField version = FieldFactory.getDefaultField("jobPlanVersion","Version","VERSION",FieldType.DECIMAL);
        fields.add(version);
        
        module.setFields(fields);
        
        jobplanModules.add(module);
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, jobplanModules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        
        return module;
    }
    private void addGroupField(FacilioModule jobPlanModule) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        LookupField group = FieldFactory.getDefaultField("group", "Group", "GROUP_ID", FieldType.LOOKUP);
        group.setLookupModule(jobPlanModule);
        fields.add(group);
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, jobPlanModule.getName());
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> jobPlan = new ArrayList<FacilioView>();
        jobPlan.add(getAllJobPlanView().setOrder(order++));
        jobPlan.add(getActiveJobPlanView().setOrder(order++));
        jobPlan.add(getInActiveJobPlanView().setOrder(order++));
        jobPlan.add(getDisabledJobPlanView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.JOB_PLAN);
        groupDetails.put("views", jobPlan);
        groupDetails.put("appLinkNames", AddJobPlanModule.jobPlanSupportedApps);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllJobPlanView() {
        Criteria criteria = getAllJobPlanCriteria();

        FacilioModule module = ModuleFactory.getJobPlanModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Job Plans");
        allView.setCriteria(criteria);
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(AddJobPlanModule.jobPlanSupportedApps);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }

    private static FacilioView getActiveJobPlanView() {
        Criteria criteria = getActiveJobPlanCriteria();
        FacilioModule jobPlanModule = ModuleFactory.getJobPlanModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(jobPlanModule);
        FacilioView allView = new FacilioView();
        allView.setName("active");
        allView.setDisplayName("Active");
        allView.setCriteria(criteria);
        allView.setAppLinkNames(AddJobPlanModule.jobPlanSupportedApps);
        return allView;
    }

    private static FacilioView getInActiveJobPlanView() {
        Criteria criteria = getInActiveJobPlanCriteria();
        FacilioModule jobPlanModule = ModuleFactory.getJobPlanModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(jobPlanModule);
        FacilioView allView = new FacilioView();
        allView.setName("inactive");
        allView.setDisplayName("Inactive");
        allView.setCriteria(criteria);
        allView.setAppLinkNames(AddJobPlanModule.jobPlanSupportedApps);
        return allView;
    }

    private static FacilioView getDisabledJobPlanView() {
        Criteria criteria = getDisabledJobPlanCriteria();
        FacilioModule jobPlanModule = ModuleFactory.getJobPlanModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(jobPlanModule);
        FacilioView allView = new FacilioView();
        allView.setName("disabled");
        allView.setDisplayName("Disabled");
        allView.setCriteria(criteria);
        allView.setAppLinkNames(AddJobPlanModule.jobPlanSupportedApps);
        return allView;
    }

    public static Criteria getActiveJobPlanCriteria() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",String.valueOf(JobPlanContext.JPStatus.ACTIVE.getVal()),NumberOperators.EQUALS));
        criteria.setPattern("(1)");
        return criteria;
    }

    public static Criteria getInActiveJobPlanCriteria() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",String.valueOf(JobPlanContext.JPStatus.IN_ACTIVE.getVal()),NumberOperators.EQUALS));
        criteria.setPattern("(1)");
        return criteria;
    }
    public static Criteria getDisabledJobPlanCriteria(){
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",String.valueOf(JobPlanContext.JPStatus.DISABLED.getVal()),NumberOperators.EQUALS));
        criteria.setPattern("(1)");
        return criteria;
    }
    public static Criteria getAllJobPlanCriteria(){
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",String.valueOf(JobPlanContext.JPStatus.ACTIVE.getVal()),NumberOperators.EQUALS));
        criteria.addOrCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",String.valueOf(JobPlanContext.JPStatus.IN_ACTIVE.getVal()),NumberOperators.EQUALS));
        criteria.setPattern("(1 or 2)");
        return criteria;
    }

    public static Condition getJobPlanBooleanCondition(String fieldName, String columnName, String conditionValue) {
        FacilioModule module = ModuleFactory.getJobPlanModule();
        FacilioField field = new FacilioField();
        field.setName(fieldName);
        field.setColumnName(columnName);
        field.setDataType(FieldType.BOOLEAN);
        field.setModule(module);

        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperator(BooleanOperators.IS);
        condition.setValue(conditionValue);

        return condition;
    }

    
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        
        FacilioForm jobPlanModuleForm = new FacilioForm();
        jobPlanModuleForm.setDisplayName("Job Plan");
        jobPlanModuleForm.setName("default_jobplan_web");
        jobPlanModuleForm.setModule(jobPlanModule);
        jobPlanModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        jobPlanModuleForm.setAppLinkNamesForForm(jobPlanSupportedApps);

        List<FormField> jobPlanModuleFormDefaultFields = new ArrayList<>();
        jobPlanModuleFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        jobPlanModuleFormDefaultFields.add(new FormField("jobPlanCategory", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.REQUIRED, 2, 1));
        FormField assetCat = new FormField("assetCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset Category", FormField.Required.OPTIONAL, "assetcategory", 3, 1);
        assetCat.setHideField(Boolean.TRUE);
        assetCat.setRequired(Boolean.TRUE);
        jobPlanModuleFormDefaultFields.add(assetCat);
        FormField spaceCat = new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Category", FormField.Required.OPTIONAL, "spacecategory", 4, 1);
        spaceCat.setHideField(Boolean.TRUE);
        spaceCat.setRequired(Boolean.TRUE);
        jobPlanModuleFormDefaultFields.add(spaceCat);

        FormField jpStatus = new FormField("jpStatus", FieldDisplayType.SELECTBOX,"Job Plan Status", FormField.Required.OPTIONAL,5,1);
        jpStatus.setHideField(Boolean.TRUE);
        jpStatus.setIsDisabled(Boolean.TRUE);
        jobPlanModuleFormDefaultFields.add(jpStatus);

        FormField version = new FormField("jobPlanVersion",FieldDisplayType.DECIMAL,"Version",FormField.Required.OPTIONAL,6,1);
        version.setHideField(Boolean.TRUE);
        version.setIsDisabled(Boolean.TRUE);
        jobPlanModuleFormDefaultFields.add(version);

        List<FormField> taskFields = new ArrayList<>();
        taskFields.add(new FormField("jobplansection", FacilioField.FieldDisplayType.JP_TASK, "Tasks", FormField.Required.REQUIRED, 5, 1));

        List<FormField> jobPlanModuleFormFields = new ArrayList<>();
        jobPlanModuleFormFields.addAll(jobPlanModuleFormDefaultFields);
        jobPlanModuleFormFields.addAll(taskFields);

        jobPlanModuleForm.setFields(jobPlanModuleFormFields);

        FormSection defaultSection = new FormSection("Scope", 1, jobPlanModuleFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection taskSection = new FormSection("TASKS", 2, taskFields, true);
        taskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(taskSection);

        jobPlanModuleForm.setSections(sections);
        jobPlanModuleForm.setIsSystemForm(true);
        jobPlanModuleForm.setType(FacilioForm.Type.FORM);
        
        return Collections.singletonList(jobPlanModuleForm);

        //FormsAPI.createForm(jobPlanModuleForm, jobPlanModule);
        
        //addFormRulesForJP(jobPlanModuleForm, jobPlanModule);
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("assetCategory");
        fieldNames.add("spaceCategory");
        fieldNames.add("jobPlanCategory");
        fieldNames.add("jpStatus");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
