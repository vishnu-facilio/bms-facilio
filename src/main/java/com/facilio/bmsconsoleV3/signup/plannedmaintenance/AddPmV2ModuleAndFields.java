package com.facilio.bmsconsoleV3.signup.plannedmaintenance;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.qa.signup.AddQAndAModules;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.ScheduleInfo.FrequencyType;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * AddRevampedPmModuleAndFields adds the following Modules and its related Fields.
 * - plannedmaintenance
 * - plannedMaintenanceSite
 * - pmTriggerV2
 * - pmJobPlan
 * - pmPlanner
 * - pmResourcePlanner
 * - resourceplanner (previously called as pmImport)
 * - pmTasksImport
 */
public class AddPmV2ModuleAndFields extends SignUpData {

    private static final Logger LOGGER = LogManager.getLogger(AddPmV2ModuleAndFields.class.getName());

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        /* Stage 1 */
        FacilioModule plannedMaintenanceModule = constructPlannedMaintenanceModule(modBean, orgId);
        SignupUtil.addModules(plannedMaintenanceModule);

        FacilioModule plannedMaintenanceSiteModule = constructPlannedMaintenanceSiteModule(modBean, orgId, plannedMaintenanceModule);
        SignupUtil.addModules(plannedMaintenanceSiteModule);

        /* Stage 2 */
        /* Add sites Field into plannedmaintenance module - do this only after creating plannedMaintenanceSite module */
        addSiteFieldIntoPlannedMaintenanceModule(modBean, orgId, plannedMaintenanceModule, plannedMaintenanceSiteModule);

        /* Adding SubModulesRel for plannedmaintenance & plannedMaintenanceSite */
        modBean.addSubModule(plannedMaintenanceModule.getModuleId(), plannedMaintenanceSiteModule.getModuleId());

        /* Stage 3 */
        FacilioModule pmTriggerV2Module = constructPmTriggerV2Module(modBean, orgId);
        //FacilioModule pmJobPlanModule = constructPmJobPlanModule(modBean, orgId);
        // add the modules
        //SignupUtil.addModules(pmTriggerV2Module, pmJobPlanModule);
        SignupUtil.addModules(pmTriggerV2Module);

        /* Stage 4 */
        FacilioModule pmPlannerModule = constructPmPlannerModule(modBean, orgId, null, pmTriggerV2Module);
        // add the modules
        SignupUtil.addModules(pmPlannerModule);
        /* Adding SubModulesRel for plannedmaintenance & pmPlanner */
        modBean.addSubModule(plannedMaintenanceModule.getModuleId(), pmPlannerModule.getModuleId());

        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        if(jobPlanModule == null){
            LOGGER.error("JobPlan module not found for the org " + orgId);
            throw new Exception("JobPlan module not found for the org " + orgId);
        }
        /* Stage 5 */
        FacilioModule pmResourcePlannerModule = constructPmResourcePlannerModule(modBean, orgId, jobPlanModule, pmPlannerModule);
        FacilioModule pmImportModule = constructPmImportModule(modBean, orgId, plannedMaintenanceModule, pmPlannerModule, jobPlanModule, pmTriggerV2Module);
        FacilioModule pmTasksImportModule = constructPmTasksImportModule(modBean, orgId, plannedMaintenanceModule);
        // add the modules
        SignupUtil.addModules(pmResourcePlannerModule, pmImportModule, pmTasksImportModule);

        /* Stage 6 - Adding RollUp Fields */
        addRollUpField(modBean, pmPlannerModule, pmResourcePlannerModule,"resourceCount", "planner",
                "Resource Count of Planner");
        
        addWorkOrderFieldsForPM(modBean, orgId);
        
        addNightlyJob();
    }
    
    public void addNightlyJob() throws Exception {
    	
    	ScheduleInfo si = new ScheduleInfo();
    	si.setFrequencyType(FrequencyType.DAILY);
    	si.setTimes(Collections.singletonList("00:01"));
    	
    	FacilioTimer.scheduleCalendarJob(AccountUtil.getCurrentOrg().getId(), "PMV2NightlyScheduler", DateTimeUtil.getCurrenTime(), si, "facilio");

        ScheduleInfo scheduleInfo2 = new ScheduleInfo();
        scheduleInfo2.setTimes(Collections.singletonList("09:00"));
        scheduleInfo2.setFrequencyType(FrequencyType.DAILY);
        FacilioTimer.scheduleCalendarJob((long) BaseScheduleContext.ScheduleType.PM.getIndex(), "pmV2MonitoringToolJob", DateTimeUtil.getCurrenTime(), scheduleInfo2, "facilio");
	}

	private void addWorkOrderFieldsForPM(ModuleBean modBean,  long orgId) throws Exception {
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        if(module != null) {
        	
            NumberField pmPlannerField = SignupUtil.getNumberField(module, "pmPlanner", "PM Planner",
                    "PM_PLANNER_ID", FacilioField.FieldDisplayType.NUMBER, false, false, true, orgId);
            modBean.addField(pmPlannerField);
            
            NumberField pmV2Field = SignupUtil.getNumberField(module, "pmV2", "Planned Maintenance V2",
                    "PM_V2_ID", FacilioField.FieldDisplayType.NUMBER, false, false, true, orgId);
            modBean.addField(pmV2Field);
            
            NumberField pmResourcePlannerField = SignupUtil.getNumberField(module, "pmResourcePlanner", "PM Resource Planner",
                    "PM_RP_ID", FacilioField.FieldDisplayType.NUMBER, false, false, true, orgId);
            modBean.addField(pmResourcePlannerField);
            
            NumberField pmTriggerV2Field = SignupUtil.getNumberField(module, "pmTriggerV2", "PM Trigger V2",
                    "PM_V2_TRIGGER_ID", FacilioField.FieldDisplayType.NUMBER, false, false, true, orgId);
            modBean.addField(pmTriggerV2Field);
            
        }
        else {
            FacilioUtil.throwIllegalArgumentException(true, "WorkOrder module cannot be null while adding fields for PlannedMaintenance module.");
        }
    }

    /**
     * Construct plannedmaintenance Module
     */
    private FacilioModule constructPlannedMaintenanceModule(ModuleBean moduleBean, long orgId) throws Exception {

        FacilioModule module = new FacilioModule("plannedmaintenance", "Planned Maintenance",
                "PM_V2", FacilioModule.ModuleType.BASE_ENTITY, moduleBean.getModule("workorder"),
                true);
        module.setHideFromParents(true);
        module.setOrgId(orgId);
        module.setDescription("Schedule planned maintenance work orders to mitigate unexpected failures.");

        /**
         * Construct fields plannedmaintenance Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* name Field */
        StringField nameField = SignupUtil.getStringField(module, "name", "Name", "NAME", FacilioField.FieldDisplayType.TEXTBOX,
                true, false, true, true, orgId);
        fields.add(nameField);

        /* assetCategory Field */
        LookupField assetCategoryField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY),
                "assetCategory", "Asset Category", "ASSET_CATEGORY_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(assetCategoryField);

        /* spaceCategory Field */
        LookupField spaceCategoryField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY),
                "spaceCategory", "Space Category", "SPACE_CATEGORY_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(spaceCategoryField);

        /* baseSpace Field */
        LookupField baseSpaceField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.BASE_SPACE),
                "baseSpace", "Building", "BASE_SPACE_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(baseSpaceField);

        /* assignmentType Field */
        SystemEnumField assignmentTypeField = SignupUtil.getSystemEnumField(module, "assignmentType", "Scope Category",
                "ASSIGNMENT_TYPE", "PMScopeAssigmentType", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, orgId);
        fields.add(assignmentTypeField);

        /* dueDuration Field */
        NumberField dueDurationField = SignupUtil.getNumberField(module, "dueDuration", "Due Duration", "DUE_DURATION",
                FacilioField.FieldDisplayType.DURATION, false, false, true, orgId);
        fields.add(dueDurationField);

        /* estimatedDuration Field */
        NumberField estimatedDurationField = SignupUtil.getNumberField(module, "estimatedDuration", "Estimated Duration", "ESTIMATED_DURATION",
                FacilioField.FieldDisplayType.DURATION, false, false, true, orgId);
        fields.add(estimatedDurationField);
        
        
        NumberField leadTime = SignupUtil.getNumberField(module, "leadTime", "Lead Time", "LEAD_TIME",FacilioField.FieldDisplayType.DURATION, false, false, true, orgId);
        fields.add(leadTime);

        /* isActive Field */
//        BooleanField isActiveField = SignupUtil.getBooleanField(module, "isActive", "Is Active", "IS_ACTIVE",
//                FacilioField.FieldDisplayType.DECISION_BOX, 63L, false, false, true, orgId);
//        fields.add(isActiveField);
        
        
        SystemEnumField pmStatus = (SystemEnumField) FieldFactory.getDefaultField("pmStatus", "PM Status", "PM_STATUS", FieldType.SYSTEM_ENUM);
        pmStatus.setEnumName("PlannedMaintenanceStatus");
        
        fields.add(pmStatus);

        /* SystemModifiedByField */
        LookupField modifiedByField = (LookupField) FieldFactory.getSystemField("sysModifiedBy", module);
        fields.add(modifiedByField);

        module.setFields(fields);
        return module;
    }


    /**
     * Construct plannedMaintenanceSite Module
     */
    private FacilioModule constructPlannedMaintenanceSiteModule(ModuleBean moduleBean, long orgId, FacilioModule plannedMaintenanceModule) throws Exception {

        FacilioModule module = new FacilioModule("plannedMaintenanceSite", "Planned Maintenance Sites",
                "PM_V2_Sites", FacilioModule.ModuleType.SUB_ENTITY, true);
        module.setOrgId(orgId);

        /**
         * Adding fields plannedMaintenanceSite Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* left Field */
        LookupField leftField = SignupUtil.getLookupField(module, plannedMaintenanceModule, "left", "Planned Maintenance", "LEFT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(leftField);

        /* right Field */
        LookupField rightField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.SITE),
                "right", "Site", "RIGHT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(rightField);
        module.setFields(fields);

        return module;
    }


    /**
     * Helper Function to add Site Field into plannedmaintenance module as MULTI_LOOKUP field
     *
     * @param moduleBean
     * @param orgId
     * @param plannedMaintenanceModule
     * @param plannedMaintenanceSiteModule
     * @throws Exception
     */
    private void addSiteFieldIntoPlannedMaintenanceModule(ModuleBean moduleBean, long orgId, FacilioModule plannedMaintenanceModule,
                                                          FacilioModule plannedMaintenanceSiteModule) throws Exception {
        /* sites Field */
        MultiLookupField sitesField = FieldFactory.getDefaultField("sites", "Sites", null, FieldType.MULTI_LOOKUP);
        sitesField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        sitesField.setRequired(true);
        sitesField.setDisabled(false);
        sitesField.setDefault(true);
        sitesField.setMainField(false);
        sitesField.setOrgId(orgId);
        sitesField.setModule(plannedMaintenanceModule);
        sitesField.setLookupModuleId(plannedMaintenanceSiteModule.getModuleId());
        sitesField.setRelModuleId(plannedMaintenanceSiteModule.getModuleId());
        sitesField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        moduleBean.addField(sitesField);
    }

    /**
     * Construct pmTriggerV2 Module
     */
    private FacilioModule constructPmTriggerV2Module(ModuleBean moduleBean, long orgId) throws Exception {
        FacilioModule module = new FacilioModule("pmTriggerV2", "Trigger", "PM_V2_Trigger",
                FacilioModule.ModuleType.BASE_ENTITY, true);
        module.setOrgId(orgId);

        /**
         * Adding fields pmTriggerV2 Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* Name Field */
        StringField nameField = SignupUtil.getStringField(module, "name", "Name", "NAME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(nameField);

        /* pmId Field */
        NumberField pmIdField = SignupUtil.getNumberField(module, "pmId", "PM ID", "PM_ID",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(pmIdField);

        /* schedule Field */
        StringField scheduleField = SignupUtil.getStringField(module, "schedule", "Schedule", "SCHEDULE_INFO",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(scheduleField);

        /* frequency Field */
        SystemEnumField frequencyField = SignupUtil.getSystemEnumField(module, "frequency", "Frequency", "FREQUENCY",
                "PMTriggerFrequency", FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(frequencyField);

        /* type Field */
        SystemEnumField typeField = SignupUtil.getSystemEnumField(module, "type", "Type", "TRIGGER_TYPE",
                "PMTriggerType", FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(typeField);

        /* startTime Field */
        DateField startTimeField = SignupUtil.getDateField(module, "startTime", "Start Time", "TRIGGER_START_TIME",
                FacilioField.FieldDisplayType.DATE, false, false, true, orgId);
        fields.add(startTimeField);

        /* endTime Field */
        DateField endTimeField = SignupUtil.getDateField(module, "endTime", "End Time", "TRIGGER_END_TIME",
                FacilioField.FieldDisplayType.DATE, false, false, true, orgId);
        fields.add(endTimeField);

        module.setFields(fields);
        return module;
    }

    /**
     * Construct pmJobPlan Module
     */
    private FacilioModule constructPmJobPlanModule(ModuleBean moduleBean, long orgId) throws Exception {

        FacilioModule module = new FacilioModule("pmJobPlan", "Job Plan", "PM_Job_Plan",
                FacilioModule.ModuleType.BASE_ENTITY, moduleBean.getModule(FacilioConstants.ContextNames.JOB_PLAN), true);
        module.setOrgId(orgId);

        /**
         * Adding fields pmJobPlan Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* pmId Field */
        NumberField pmIdField = SignupUtil.getNumberField(module, "pmId", "PM ID", "PM_ID",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(pmIdField);

        /* isPreRequisite Field */
        BooleanField isPreRequisiteField = SignupUtil.getBooleanField(module, "isPreRequisite", "Is Pre Requisite",
                "IS_PREREQUISITE", FacilioField.FieldDisplayType.DECISION_BOX, null, false, false,
                true, orgId);
        fields.add(isPreRequisiteField);

        module.setFields(fields);

        return module;
    }

    /**
     * Construct pmPlanner Module
     */
    private FacilioModule constructPmPlannerModule(ModuleBean moduleBean, long orgId, FacilioModule pmJobPlanModule, FacilioModule pmTriggerV2Module) throws Exception {

        FacilioModule module = new FacilioModule("pmPlanner", "Planner", "PM_Planner",
                FacilioModule.ModuleType.BASE_ENTITY, true);
        module.setOrgId(orgId);

        /**
         * Adding fields pmPlanner Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* name Field */
        StringField nameField = SignupUtil.getStringField(module, "name", "Name", "NAME",
                FacilioField.FieldDisplayType.TEXTBOX, true, false, true, true, orgId);
        fields.add(nameField);

        /* pmId Field */
        NumberField pmIdField = SignupUtil.getNumberField(module, "pmId", "PM ID", "PM_ID",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(pmIdField);

//        pmJobPlanModule will be null now.
//        /* adhocJobPlan Field */
//        LookupField adhocJobPlanField = SignupUtil.getLookupField(module, pmJobPlanModule, "adhocJobPlan", "As Hoc Task",
//                "JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
//                false, false, true, orgId);
//        fields.add(adhocJobPlanField);
//
//        /* preReqJobPlan Field */
//        LookupField preReqJobPlanField = SignupUtil.getLookupField(module, pmJobPlanModule, "preReqJobPlan", "Job Plan",
//                "PREREQ_JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
//                false, false, true, orgId);
//        fields.add(preReqJobPlanField);

        /* trigger Field */
        LookupField triggerField = SignupUtil.getLookupField(module, pmTriggerV2Module, "trigger", "Trigger",
                "TRIGGER_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(triggerField);

        /* generatedUpto Field */
        NumberField generatedUptoField = SignupUtil.getNumberField(module, "generatedUpto", "Generated Upto", "GENERATED_UPTO",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(generatedUptoField);

        NumberField resourceCountField = SignupUtil.getNumberField(module, "resourceCount", "Resource Count", "RP_COUNT",
                FacilioField.FieldDisplayType.NUMBER, false, false, true, orgId);
        fields.add(resourceCountField);

        NumberField resourceTimelineViewIdField = SignupUtil.getNumberField(module, "resourceTimelineViewId",
                "Space/Asset Timeline View", "RESOURCE_TIMELINE_VIEW_ID",
                FacilioField.FieldDisplayType.NUMBER,false,false,true, orgId);
        fields.add(resourceTimelineViewIdField);

        NumberField staffTimelineViewIdField = SignupUtil.getNumberField(module, "staffTimelineViewId",
                "Staff Timeline View", "STAFF_TIMELINE_VIEW_ID",
                FacilioField.FieldDisplayType.NUMBER,false,false,true, orgId);
        fields.add(staffTimelineViewIdField);

        module.setFields(fields);

        return module;
    }


    public static RollUpField constructRollUpField(String desc, FacilioModule childModule, FacilioField childLookupField, FacilioModule parentModule, FacilioField parentRollupField, Condition condition) throws Exception {
        RollUpField rollUp = new RollUpField();
        rollUp.setDescription(desc);
        rollUp.setAggregateFunctionId(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue());
        rollUp.setChildModuleId(childModule.getModuleId());
        rollUp.setChildFieldId(childLookupField.getFieldId());
        rollUp.setParentModuleId(parentModule.getModuleId());
        rollUp.setParentRollUpFieldId(parentRollupField.getFieldId());
        rollUp.setIsSystemRollUpField(true);
        if (condition != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(condition);
            rollUp.setChildCriteriaId(CriteriaAPI.addCriteria(criteria));
        }
        return rollUp;
    }


    /**
     * Construct pmResourcePlanner Module
     */
    private FacilioModule constructPmResourcePlannerModule(ModuleBean moduleBean, long orgId, FacilioModule jobPlanModule, FacilioModule pmPlannerModule) throws Exception {
        FacilioModule module = new FacilioModule("pmResourcePlanner", "Resource Planner",
                "PM_V2_Resource_Planner", FacilioModule.ModuleType.BASE_ENTITY, true);
        module.setOrgId(orgId);
        module.setTrashEnabled(false);

        /**
         * Adding fields pmPlanner Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* pmId Field */
        NumberField pmIdField = SignupUtil.getNumberField(module, "pmId", "PM ID", "PM_ID",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(pmIdField);

        /* resource Field */
        LookupField resourceField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE),
                "resource", "Resource", "RESOURCE_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId, true);
        fields.add(resourceField);

        /* jobPlan Field */
        LookupField jobPlanField = SignupUtil.getLookupField(module, jobPlanModule, "jobPlan", "Job Plan",
                "JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(jobPlanField);

        /* assignedTo Field */
        LookupField assignedToField = SignupUtil.getLookupField(module, null, "assignedTo", "Staff",
                "ASSIGNED_TO", "users", FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(assignedToField);

        /* planner Field */
        LookupField plannerField = SignupUtil.getLookupField(module, pmPlannerModule, "planner", "Planner",
                "PLANNER_ID", null, FacilioField.FieldDisplayType.LOOKUP_POPUP, false,
                false, true, orgId);
        fields.add(plannerField);

        module.setFields(fields);
        return module;
    }

    /**
     * Construct pmImport Module
     */
    private FacilioModule constructPmImportModule(ModuleBean moduleBean, long orgId, FacilioModule plannedMaintenanceModule,
                                                  FacilioModule pmPlannerModule, FacilioModule jobPlanModule,
                                                  FacilioModule pmTriggerV2Module) throws Exception {
        FacilioModule module = new FacilioModule("resourceplanner", "Resource Planner", "PM_Import",
                FacilioModule.ModuleType.BASE_ENTITY, true);
        module.setOrgId(orgId);

        /**
         * Adding fields pmImport/resourceplanner Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* pmId Field */
        LookupField pmIdField = SignupUtil.getLookupField(module, plannedMaintenanceModule, "pmId", "PM",
                "PM_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false,
                true, orgId);
        fields.add(pmIdField);

        /* resource planner - rpResource Field */
        LookupField rpResourceField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE),
                "rpResource", "Asset/Space", "RP_RESOURCE_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(rpResourceField);

        /* resource planner - assignedTo Field */
        LookupField assignedToField = SignupUtil.getLookupField(module, null,
                "rpAssignedTo", "Asset/Space Assignee", "RP_ASSIGNED_TO", "users",
                FacilioField.FieldDisplayType.LOOKUP_POPUP, false, false, true, orgId);
        fields.add(assignedToField);

        /* resource planner - rpPlanner Field */
        LookupField rpPlannerField = SignupUtil.getLookupField(module, pmPlannerModule,
                "rpPlanner", "Asset/Space Planner", "RP_PLANNER_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_POPUP, false, false, true, orgId);
        fields.add(rpPlannerField);

        /* pm planner - plName Field */
        StringField plNameField = SignupUtil.getStringField(module, "plName", "Planner name", "PL_NAME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(plNameField);

        /* pm planner - plJobPlan Field */
        LookupField plJobPlanField = SignupUtil.getLookupField(module, jobPlanModule, "plJobPlan", "Planner Job Plan",
                "PL_JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,
                false, true, orgId);
        fields.add(plJobPlanField);

        /* pm planner - plPreReqJobPlan Field */
        LookupField plPreReqJobPlanField = SignupUtil.getLookupField(module, jobPlanModule, "plPreReqJobPlan", "Planner Prerequisite Job Plan",
                "PL_PREREQ_JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,
                false, true, orgId);
        fields.add(plPreReqJobPlanField);

        /* pm planner - plTrigger Field */
        LookupField plTriggerField = SignupUtil.getLookupField(module, pmTriggerV2Module, "plTrigger", "Planner Trigger",
                "PL_TRIGGER_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,
                false, true, orgId);
        fields.add(plTriggerField);

        /* trigger - trFrequency Field */
        StringField trFrequencyField = SignupUtil.getStringField(module, "trFrequency", "Trigger Frequency", "TR_FREQUENCY",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        trFrequencyField.setOrgId(orgId);
        fields.add(trFrequencyField);

        /* trigger - trTimes Field */
        StringField trTimesField = SignupUtil.getStringField(module, "trTimes", "Trigger Times", "TR_TIMES",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(trTimesField);

        /* trigger - trDays Field */
        StringField trDaysField = SignupUtil.getStringField(module, "trDays", "Trigger Days", "TR_DAYS",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(trDaysField);

        /* trigger - trDates Field */
        StringField trDatesField = SignupUtil.getStringField(module, "trDates", "Trigger Dates", "TR_DATES",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(trDatesField);

        /* trigger - trWeeks Field */
        StringField trWeeksField = SignupUtil.getStringField(module, "trWeeks", "Trigger Weeks", "TR_WEEKS",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(trWeeksField);

        /* trigger - trMonths Field */
        StringField trMonthsField = SignupUtil.getStringField(module, "trMonths", "Trigger Months", "TR_MONTHS",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(trMonthsField);

        /* trigger - trType Field */
        SystemEnumField trTypeField = SignupUtil.getSystemEnumField(module, "trType", "Trigger Type", "TR_TRIGGER_TYPE",
                "PMTriggerType", FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(trTypeField);

        /* trigger - trStartTime Field */
        NumberField trStartTimeField = SignupUtil.getNumberField(module, "trStartTime", "Trigger Start Time", "TR_TRIGGER_START_TIME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(trStartTimeField);

        /* trigger - trEndTime Field */
        NumberField trEndTimeField = SignupUtil.getNumberField(module, "trEndTime", "Trigger End Time", "TR_TRIGGER_END_TIME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(trEndTimeField);

        /* trigger - skip Field */
        NumberField skipField = SignupUtil.getNumberField(module, "skip", "Trigger Skip", "TR_SKIP",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(skipField);

        /* trigger - every Field */
        NumberField everyField = SignupUtil.getNumberField(module, "every", "Trigger Every", "TR_EVERY",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(everyField);

        module.setFields(fields);
        return module;
    }

    /**
     * Construct pmTasksImport Module
     */
    private FacilioModule constructPmTasksImportModule(ModuleBean moduleBean, long orgId, FacilioModule plannedMaintenanceModule) throws Exception {
        FacilioModule module = new FacilioModule("pmTasksImport", "PM Tasks Import", "PM_Tasks_Import",
                FacilioModule.ModuleType.BASE_ENTITY, true);
        module.setOrgId(orgId);

        /**
         * Adding fields pmImport Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* name Field */
        StringField nameField = SignupUtil.getStringField(module, "name", "Name", "NAME",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(nameField);

        /* category Field */
        SystemEnumField assignmentTypeField = SignupUtil.getSystemEnumField(module, "category", "Scope Category",
                "CATEGORY", "PMScopeAssigmentType", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, orgId);
        fields.add(assignmentTypeField);

        /* assetCategory Field */
        LookupField assetCategoryField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY),
                "assetCategory", "Asset Category", "ASSET_CATEGORY", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(assetCategoryField);

        /* spaceCategory Field */
        LookupField spaceCategoryField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY),
                "spaceCategory", "Space Category", "SPACE_CATEGORY", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(spaceCategoryField);


        /* sectionName Field */
        StringField sectionNameField = SignupUtil.getStringField(module, "sectionName", "Section", "SECTION",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(sectionNameField);

        /* category Field */
        SystemEnumField sectionScopeField = SignupUtil.getSystemEnumField(module, "sectionScope", "Section Scope",
                "SECTION_SCOPE", "MultiResourceAssignmentType", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, orgId);
        fields.add(sectionScopeField);

        /* assetCategory Field */
        LookupField sectionAssetCategoryField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY),
                "sectionAssetCategory", "Section Asset Category", "SECTION_ASSET_CATEGORY", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(sectionAssetCategoryField);

        /* spaceCategory Field */
        LookupField sectionSpaceCategoryField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY),
                "sectionSpaceCategory", "Section Space Category", "SECTION_SPACE_CATEGORY", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(sectionSpaceCategoryField);

        /* task Field */
        StringField taskField = SignupUtil.getStringField(module, "task", "Task", "TASK",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(taskField);

        /* task description Field */
        StringField taskDescriptionField = SignupUtil.getStringField(module, "taskDescription", "Task Description", "TASK_DESCRIPTION",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(taskDescriptionField);

        /* task scope Field */
        SystemEnumField taskScopeField = SignupUtil.getSystemEnumField(module, "taskScope", "Task Scope",
                "TASK_SCOPE", "MultiResourceAssignmentType", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, orgId);
        fields.add(taskScopeField);

        /* task assetCategory Field */
        LookupField taskAssetCategoryField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY),
                "taskAssetCategory", "Task Asset Category", "TASK_ASSET_CATEGORY", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(taskAssetCategoryField);

        /* task spaceCategory Field */
        LookupField taskSpaceCategoryField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY),
                "taskSpaceCategory", "Task Space Category", "TASK_SPACE_CATEGORY", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(taskSpaceCategoryField);

        /* input type Field */
        StringField inputTypeField = SignupUtil.getStringField(module, "inputType", "Input Type", "INPUT_TYPE",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(inputTypeField);

        /* default value Field */
        StringField defaultValueField = SignupUtil.getStringField(module, "defaultValue", "Default Value", "DEFAULT_VALUE",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(defaultValueField);

        /* options Field */
        StringField optionsField = SignupUtil.getStringField(module, "options", "Options", "OPTIONS",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(optionsField);

        NumberField sectionSequence = SignupUtil.getNumberField(module, "sectionSequence", "Section Sequence",
                "SECTION_SEQUENCE", FacilioField.FieldDisplayType.NUMBER, true, false, true, orgId);
        fields.add(sectionSequence);

        NumberField taskSequence = SignupUtil.getNumberField(module, "taskSequence", "Task Sequence",
                "TASK_SEQUENCE", FacilioField.FieldDisplayType.NUMBER, true, false, true, orgId);
        fields.add(taskSequence);

        module.setFields(fields);
        return module;
    }

    /**
     * Helper function to add RollUp field.
     *
     * @param modBean
     * @param parentModule
     * @param childModule
     * @param rollUpFieldName
     * @param childFieldName
     * @throws Exception
     */
    private void addRollUpField(ModuleBean modBean, FacilioModule parentModule, FacilioModule childModule,
                                String rollUpFieldName, String childFieldName, String description) throws Exception {

        List<FacilioField> parentModuleFields = modBean.getAllFields(parentModule.getName());
        Map<String, FacilioField> parentModuleFieldsMap = FieldFactory.getAsMap(parentModuleFields);

        List<FacilioField> childModuleFields = modBean.getAllFields(childModule.getName());
        Map<String, FacilioField> childModuleFieldsMap = FieldFactory.getAsMap(childModuleFields);

        // Later constructRollUpField can be moved to some Util class
        RollUpField rollUpField = AddQAndAModules.constructRollUpField(description, childModule,
                childModuleFieldsMap.get(childFieldName), parentModule,
                parentModuleFieldsMap.get(rollUpFieldName), null);
        RollUpFieldUtil.addRollUpField(Collections.singletonList(rollUpField));
    }
}