package com.facilio.bmsconsoleV3.signup.plannedmaintenance;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        FacilioModule pmJobPlanModule = constructPmJobPlanModule(modBean, orgId);
        // add the modules
        SignupUtil.addModules(pmTriggerV2Module, pmJobPlanModule);

        /* Stage 4 */
        FacilioModule pmPlannerModule = constructPmPlannerModule(modBean, orgId, pmJobPlanModule, pmTriggerV2Module);
        // add the modules
        SignupUtil.addModules(pmPlannerModule);
        /* Adding SubModulesRel for plannedmaintenance & pmPlanner */
        modBean.addSubModule(plannedMaintenanceModule.getModuleId(), pmPlannerModule.getModuleId());

        /* Stage 5 */
        FacilioModule pmResourcePlannerModule = constructPmResourcePlannerModule(modBean, orgId, pmJobPlanModule, pmPlannerModule);
        FacilioModule pmImportModule = constructPmImportModule(modBean, orgId, plannedMaintenanceModule, pmPlannerModule, pmJobPlanModule, pmTriggerV2Module);
        FacilioModule pmTasksImportModule = constructPmTasksImportModule(modBean, orgId, plannedMaintenanceModule);
        // add the modules
        SignupUtil.addModules(pmResourcePlannerModule, pmImportModule, pmTasksImportModule);
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
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(dueDurationField);

        /* estimatedDuration Field */
        NumberField estimatedDurationField = SignupUtil.getNumberField(module, "estimatedDuration", "Estimated Duration", "ESTIMATED_DURATION",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(estimatedDurationField);

        /* isActive Field */
        BooleanField isActiveField = SignupUtil.getBooleanField(module, "isActive", "Is Active", "IS_ACTIVE",
                FacilioField.FieldDisplayType.DECISION_BOX, 63L, false, false, true, orgId);
        fields.add(isActiveField);

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
        MultiLookupField sitesField = FieldFactory.getDefaultField("sites", null, null, FieldType.MULTI_LOOKUP);
        sitesField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
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
        NumberField startTimeField = SignupUtil.getNumberField(module, "startTime", "Start Time", "TRIGGER_START_TIME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId); // check for display name
        fields.add(startTimeField);

        /* endTime Field */
        NumberField endTimeField = SignupUtil.getNumberField(module, "endTime", "End Time", "TRIGGER_END_TIME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
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

        /* adhocJobPlan Field */
        LookupField adhocJobPlanField = SignupUtil.getLookupField(module, pmJobPlanModule, "adhocJobPlan", "As Hoc Task",
                "JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(adhocJobPlanField);

        /* preReqJobPlan Field */
        LookupField preReqJobPlanField = SignupUtil.getLookupField(module, pmJobPlanModule, "preReqJobPlan", "Job Plan",
                "PREREQ_JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(preReqJobPlanField);

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
    private FacilioModule constructPmResourcePlannerModule(ModuleBean moduleBean, long orgId, FacilioModule pmJobPlanModule, FacilioModule pmPlannerModule) throws Exception {
        FacilioModule module = new FacilioModule("pmResourcePlanner", "Resource Planner",
                "PM_V2_Resource_Planner", FacilioModule.ModuleType.BASE_ENTITY, true);
        module.setOrgId(orgId);

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
        LookupField jobPlanField = SignupUtil.getLookupField(module, pmJobPlanModule, "jobPlan", "Job Plan",
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
                                                  FacilioModule pmPlannerModule, FacilioModule pmJobPlanModule,
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
        LookupField plJobPlanField = SignupUtil.getLookupField(module, pmJobPlanModule, "plJobPlan", "Planner Job Plan",
                "PL_JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,
                false, true, orgId);
        fields.add(plJobPlanField);

        /* pm planner - plPreReqJobPlan Field */
        LookupField plPreReqJobPlanField = SignupUtil.getLookupField(module, pmJobPlanModule, "plPreReqJobPlan", "Planner Prerequisite Job Plan",
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
        /* pmId Field */
        LookupField pmIdField = SignupUtil.getLookupField(module, plannedMaintenanceModule, "pmId", "PM ID",
                "PM_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,
                false, true, orgId);
        fields.add(pmIdField);

        /* sectionName Field */
        StringField sectionNameField = SignupUtil.getStringField(module, "sectionName", "Section", "SECTION",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(sectionNameField);

        /* task Field */
        StringField taskField = SignupUtil.getStringField(module, "task", "Task", "TASK",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(taskField);

        /* isPrerequisite Field */
        StringField isPrerequisiteField = SignupUtil.getStringField(module, "isPrerequisite", "IS PREREQUISITE", "IS_PREREQUISITE",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(isPrerequisiteField);

        module.setFields(fields);
        return module;
    }
}