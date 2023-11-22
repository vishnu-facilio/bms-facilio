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
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.AppDomain;
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
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;
import org.json.JSONObject;

public class AddJobPlanModule extends BaseModuleConfig{
	
	public static List<String> jobPlanSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.IWMS_APP);
	
    public AddJobPlanModule(){
        setModuleName(FacilioConstants.ContextNames.JOB_PLAN);
    }


    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        String appName=FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameVsPage.put(appName, createJobPlanDefaultPage(app, module, false, true));
        return appNameVsPage;
    }
    
    @Override
    public void addData() throws Exception {
    	// TODO Auto-generated method stub
    	FacilioModule jobPlanModule = addJobPlanModule(Constants.getModBean());
    	
    	FacilioModule jobPlanSectionModule = addJobPlanSectionModule(Constants.getModBean(),jobPlanModule);
    	FacilioModule jobPlanTaskModule = addJobPlanTaskModule(Constants.getModBean(), jobPlanModule, jobPlanSectionModule);
    	FacilioModule taskModule = addTaskModuleFields(Constants.getModBean());
    	addActivityModuleForJobPlan(jobPlanModule);
    	
    	addJobPlanAttachmentsModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), jobPlanModule);
    	constructJobPlanNotesModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), jobPlanModule);
    	
    	addJobPlanTaskInputOptionsModule(Constants.getModBean(), jobPlanTaskModule);
    	
    	addJobPlanSectionInputOptionsModule(Constants.getModBean(), jobPlanSectionModule);
    	
    	addJobPlanLookupToWoModule(jobPlanModule);
        addGroupField(jobPlanModule);
        addSystemButtons();

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
        
        LookupField meterType = (LookupField) FieldFactory.getDefaultField("meterType", "Meter Type", "METER_TYPE_ID", FieldType.LOOKUP);
        meterType.setLookupModule(modBean.getModule(FacilioConstants.Meter.UTILITY_TYPE));
        fields.add(meterType);
        
        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY_ID", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);


        //SFG20 Fields for Jobplan

        NumberField taskCode = FieldFactory.getDefaultField("taskCode","Task Code","TASK_CODE",FieldType.NUMBER);
        fields.add(taskCode);


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
        
        LookupField meterType = (LookupField) FieldFactory.getDefaultField("meterType", "Meter Type", "METER_TYPE_ID", FieldType.LOOKUP);
        meterType.setLookupModule(modBean.getModule(FacilioConstants.Meter.UTILITY_TYPE));
        fields.add(meterType);
        
        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY_ID", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);

        NumberField seqNo = (NumberField) FieldFactory.getDefaultField("sequenceNumber", "Sequence Number", "SEQUENCE_NUMBER", FieldType.NUMBER);
        fields.add(seqNo);

        NumberField inputTypeField = (NumberField) FieldFactory.getDefaultField("inputType", "Input Type", "INPUT_TYPE", FieldType.NUMBER);
        fields.add(inputTypeField);

        fields.add(FieldFactory.getDefaultField("additionalInfoJsonStr", "Additional Info", "ADDITIONAL_INFO", FieldType.STRING));


        //SFG20 Fields for Jobplan

        NumberField sectionCode = FieldFactory.getDefaultField("sectionCode","Section Code","SECTION_CODE",FieldType.NUMBER);
        fields.add(sectionCode);


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

        //SFG20 Fields for Jobplan

        NumberField scheduleId = FieldFactory.getDefaultField("scheduleId","SFG Schedule Id","SFG_SCHEDULE_ID",FieldType.DECIMAL);
        fields.add(scheduleId);

        fields.add(FieldFactory.getDefaultField("sfgCode", "Code", "SFG_CODE", FieldType.STRING, false));
        fields.add(FieldFactory.getDefaultField("sfgVersion", "SFG20 Version", "SFG_VERSION", FieldType.STRING, false));
        fields.add(FieldFactory.getDefaultField("content", "Content", "CONTENT", FieldType.LARGE_TEXT, false));
        fields.add(FieldFactory.getDefaultField("notes", "Notes", "NOTES", FieldType.LARGE_TEXT, false));
        fields.add(FieldFactory.getDefaultField("sfgLegislations", "SFG Legislations", "SFG_LEGISLATIONS", FieldType.LARGE_TEXT, false));
        fields.add(FieldFactory.getDefaultField("isSfg", "IS SFG20", "IS_SFG20", FieldType.BOOLEAN, false));
        fields.add(FieldFactory.getDefaultField("unitOfMeasure", "Unit Of Measure", "UNIT_OF_MEASURE", FieldType.LARGE_TEXT, false));

        SystemEnumField sfgType = (SystemEnumField) FieldFactory.getDefaultField("sfgType", "SFG Type", "SFG_TYPE", FieldType.SYSTEM_ENUM);
        sfgType.setEnumName("SFGType");
        fields.add(sfgType);

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

        FacilioForm jobPlanModuleSFGForm = new FacilioForm();
        jobPlanModuleForm.setDisplayName("Job Plan");
        jobPlanModuleForm.setName("sfg__jobplan_web");
        jobPlanModuleForm.setModule(jobPlanModule);
        jobPlanModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        jobPlanModuleForm.setAppLinkNamesForForm(jobPlanSupportedApps);

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

    private FacilioModule addTaskModuleFields(ModuleBean modBean) throws Exception {
        FacilioModule taskModule = modBean.getModule(FacilioConstants.ContextNames.TASK);


        List<FacilioField> fields = new ArrayList<>();
        SystemEnumField PriorityType = (SystemEnumField) FieldFactory.getDefaultField("taskCriticality", "Criticality", "CRITICALITY", FieldType.SYSTEM_ENUM);
        PriorityType.setEnumName("TaskCriticality");
        fields.add(PriorityType);

        SystemEnumField FrequencyType = (SystemEnumField) FieldFactory.getDefaultField("taskFrequency", "Frequency", "FREQUENCY", FieldType.SYSTEM_ENUM);
        FrequencyType.setEnumName("TaskFrequency");
        fields.add(FrequencyType);


        SystemEnumField skillSet = (SystemEnumField) FieldFactory.getDefaultField("skillSet", "Skill Set", "SKILLSET", FieldType.SYSTEM_ENUM);
        skillSet.setEnumName("SkillSet");
        fields.add(skillSet);

        fields.add(FieldFactory.getDefaultField("sfgScheduleId", "SFG Schedule Id", "SFG_SCHEDULE_ID", FieldType.NUMBER, false));

        fields.add(FieldFactory.getDefaultField("actionContent", "Action Content", "ACTION_CONTENT", FieldType.LARGE_TEXT, false));
        fields.add(FieldFactory.getDefaultField("notes", "Notes", "NOTES", FieldType.LARGE_TEXT, false));


        taskModule.setFields(fields);
        for(FacilioField field : taskModule.getFields()) {
            field.setModule(taskModule);
            modBean.addField(field);
        }

        return taskModule;
    }


    public static List<PagesContext> createJobPlanDefaultPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        JSONObject multiresourceWidgetParam = new JSONObject();
        multiresourceWidgetParam.put("summaryWidgetName", "multiResourceWidget");
        multiresourceWidgetParam.put("module", "\"" + jobPlanModule + "\"");

        org.json.simple.JSONObject historyWidgetParam = new org.json.simple.JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.JOB_PLAN_ACTIVITY);

        List<PagesContext> jpPages = new ArrayList<>();

        Criteria sfgCriteria =new Criteria();
        sfgCriteria.addAndCondition(CriteriaAPI.getCondition("IS_SFG20","isSfg", String.valueOf(true), BooleanOperators.IS));

        jpPages.add(new PagesContext("sfgJobplanSummary", "SFG-20 Job Plan Summary","", sfgCriteria, false, false, true)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Job Plan Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSFGSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()

                .addSection("taskSections", null, null)
                .addWidget("taskSectionWidget", "Task List", PageWidget.WidgetType.JOBPLAN_TASK_SECTIONS, "flexiblewebjobplantasksectionwidget_5", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("legislations", null, null)
                .addWidget("legislationsWidget", "Legislation, Regulations and Guidance", PageWidget.WidgetType.JOBPLAN_SFG_LEGISLATIONS, "flexibleweblegislationsWidget_5", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("plan", "Plans", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("planSection", null, null)
                .addWidget("plansWidget", "Plans", PageWidget.WidgetType.PLANS, "flexiblewebJPplansWidget_5", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("notesAndInformation", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0,  historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone());


        jpPages.add(new PagesContext("defaultJobplanSummary", "Default Job Plan Summary","", null, false, true, true)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Job Plan Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()

                .addSection("taskSections", null, null)
                .addWidget("taskSectionWidget", "Task List", PageWidget.WidgetType.JOBPLAN_TASK_SECTIONS, "flexiblewebjobplantasksectionwidget_5", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("plan", "Plans", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("planSection", null, null)
                .addWidget("plansWidget", "Plans", PageWidget.WidgetType.PLANS, "flexiblewebJPplansWidget_5", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("notesAndInformation", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0,  historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone());

        return jpPages;
    }
    private static org.json.simple.JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField jobPlanCategoryField = moduleBean.getField("jobPlanCategory", moduleName);
        FacilioField jpStatusField = moduleBean.getField("jpStatus", moduleName);
        FacilioField jobPlanVersionField = moduleBean.getField("jobPlanVersion", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, jobPlanCategoryField,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, jpStatusField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, jobPlanVersionField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField,1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedByField,2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 2, 3, 1);



        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static org.json.simple.JSONObject getSFGSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField jobPlanCategoryField = moduleBean.getField("jobPlanCategory", moduleName);
        FacilioField assetCategoryField = moduleBean.getField("assetCategory", moduleName);
        FacilioField jpStatusField = moduleBean.getField("jpStatus", moduleName);
        FacilioField jobPlanVersionField = moduleBean.getField("jobPlanVersion", moduleName);
        FacilioField sfgVersionField = moduleBean.getField("sfgVersion", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField contentField = moduleBean.getField("content", moduleName);
        FacilioField notesField = moduleBean.getField("notes", moduleName);
        FacilioField scheduleIdField = moduleBean.getField("scheduleId", moduleName);
        FacilioField unitOfMeasureField = moduleBean.getField("unitOfMeasure", moduleName);
        FacilioField sfgTypeField = moduleBean.getField("sfgType", moduleName);



        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, jobPlanCategoryField,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, assetCategoryField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, jobPlanVersionField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, jpStatusField, 1, 4, 1);

        widgetGroup.setName("jobplanInformation");
        widgetGroup.setDisplayName("Job Plan Information");
        widgetGroup.setColumns(4);

        SummaryWidgetGroup sfgWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(sfgWidgetGroup, sfgVersionField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(sfgWidgetGroup, scheduleIdField,1, 2, 1);
        addSummaryFieldInWidgetGroup(sfgWidgetGroup, sfgTypeField,1, 3, 1);
        addSummaryFieldInWidgetGroup(sfgWidgetGroup, unitOfMeasureField,1, 4, 1);
        addSummaryFieldInWidgetGroup(sfgWidgetGroup, contentField,2, 1, 4);
        addSummaryFieldInWidgetGroup(sfgWidgetGroup, notesField,3, 1, 4);

        sfgWidgetGroup.setName("sfgInformation");
        sfgWidgetGroup.setDisplayName("SFG-20 Information");
        sfgWidgetGroup.setColumns(4);


        SummaryWidgetGroup systemWidgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemWidgetGroup, sysModifiedTimeField, 1, 4, 1);


        systemWidgetGroup.setName("systemInformation");
        systemWidgetGroup.setDisplayName("System Information");
        systemWidgetGroup.setColumns(4);



        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(sfgWidgetGroup);
        widgetGroupList.add(systemWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if(widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            }
            else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
    private static org.json.simple.JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        org.json.simple.JSONObject notesWidgetParam = new org.json.simple.JSONObject();
        notesWidgetParam.put("notesModuleName", "jobplannotes");
        org.json.simple.JSONObject attachmentWidgetParam = new org.json.simple.JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "jobplanattachments");
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }

    public static void addSystemButtons() throws Exception {
        SystemButtonRuleContext editButton = new SystemButtonRuleContext();
        editButton.setName("Edit");
        editButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editButton.setIdentifier("jobplanEdit");
        editButton.setPermissionRequired(true);
        editButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.JOB_PLAN, editButton);

        SystemButtonRuleContext publishJPButton = new SystemButtonRuleContext();
        publishJPButton.setName("Publish");
        publishJPButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        publishJPButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        publishJPButton.setIdentifier("jobplanPublish");
        publishJPButton.setPermissionRequired(true);
        publishJPButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.JOB_PLAN, publishJPButton);

        SystemButtonRuleContext reviseJPButton = new SystemButtonRuleContext();
        reviseJPButton.setName("Revise");
        reviseJPButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        reviseJPButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        reviseJPButton.setIdentifier("jobplanRevise");
        reviseJPButton.setPermissionRequired(true);
        reviseJPButton.setPermission("CREATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.JOB_PLAN, reviseJPButton);

        SystemButtonRuleContext cloneJPButton = new SystemButtonRuleContext();
        cloneJPButton.setName("Clone");
        cloneJPButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        cloneJPButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        cloneJPButton.setIdentifier("jobplanClone");
        cloneJPButton.setPermissionRequired(true);
        cloneJPButton.setPermission("CREATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.JOB_PLAN, cloneJPButton);

        SystemButtonRuleContext disableJPButton = new SystemButtonRuleContext();
        disableJPButton.setName("Disable");
        disableJPButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        disableJPButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        disableJPButton.setIdentifier("jobplanDisable");
        disableJPButton.setPermissionRequired(true);
        disableJPButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.JOB_PLAN, disableJPButton);

        SystemButtonRuleContext versionButton = new SystemButtonRuleContext();
        versionButton.setName("Version History");
        versionButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        versionButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        versionButton.setIdentifier("jobplanVersionHistory");
        versionButton.setPermissionRequired(true);
        versionButton.setPermission("READ");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.JOB_PLAN, versionButton);

        SystemButtonRuleContext bulkPublishButton = new SystemButtonRuleContext();
        bulkPublishButton.setName("Publish");
        bulkPublishButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        bulkPublishButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkPublishButton.setIdentifier("bulk_publish");
        bulkPublishButton.setPermissionRequired(true);
        bulkPublishButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.JOB_PLAN, bulkPublishButton);

        SystemButtonApi.addCreateButton(FacilioConstants.ContextNames.JOB_PLAN);
        SystemButtonApi.addBulkDeleteButton(FacilioConstants.ContextNames.JOB_PLAN);
        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.JOB_PLAN);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.JOB_PLAN);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",String.valueOf(JobPlanContext.JPStatus.IN_ACTIVE.getVal()),NumberOperators.EQUALS));

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        listDeleteButton.setCriteria(criteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.JOB_PLAN, listDeleteButton);

        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        listEditButton.setCriteria(criteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.JOB_PLAN, listEditButton);
    }

}
