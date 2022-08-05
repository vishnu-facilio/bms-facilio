package com.facilio.bmsconsoleV3.signup.plannedmaintenance;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Arrays;
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
        addModules(plannedMaintenanceModule);

        FacilioModule plannedMaintenanceSiteModule = constructPlannedMaintenanceSiteModule(modBean, orgId, plannedMaintenanceModule);
        addModules(plannedMaintenanceSiteModule);

        /* Stage 2 */
        /* Add sites Field into plannedmaintenance module - do this only after creating plannedMaintenanceSite module */
        addSiteFieldIntoPlannedMaintenanceModule(modBean, orgId, plannedMaintenanceModule, plannedMaintenanceSiteModule);

        /* Adding SubModulesRel for plannedmaintenance & plannedMaintenanceSite */
        modBean.addSubModule(plannedMaintenanceModule.getModuleId(), plannedMaintenanceSiteModule.getModuleId());

        /* Stage 3 */
        FacilioModule pmTriggerV2Module = constructPmTriggerV2Module(modBean, orgId);
        FacilioModule pmJobPlanModule = constructPmJobPlanModule(modBean, orgId);
        // add the modules
        addModules(pmTriggerV2Module, pmJobPlanModule);

        /* Stage 4 */
        FacilioModule pmPlannerModule = constructPmPlannerModule(modBean, orgId, pmJobPlanModule, pmTriggerV2Module);
        // add the modules
        addModules(pmPlannerModule);
        /* Adding SubModulesRel for plannedmaintenance & pmPlanner */
        modBean.addSubModule(plannedMaintenanceModule.getModuleId(), pmPlannerModule.getModuleId());

        /* Stage 5 */
        FacilioModule pmResourcePlannerModule = constructPmResourcePlannerModule(modBean, orgId, pmJobPlanModule, pmPlannerModule);
        FacilioModule pmImportModule = constructPmImportModule(modBean, orgId, plannedMaintenanceModule, pmPlannerModule, pmJobPlanModule, pmTriggerV2Module);
        FacilioModule pmTasksImportModule = constructPmTasksImportModule(modBean, orgId, plannedMaintenanceModule);
        // add the modules
        addModules(pmResourcePlannerModule, pmImportModule, pmTasksImportModule);
    }

    /**
     * Helper method to add modules via using SystemModuleChain
     *
     * @param modules - List of modules to be added as System Module
     * @throws Exception
     */
    private void addModules(FacilioModule... modules) throws Exception {
        FacilioChain addModulesChain = TransactionChainFactory.addSystemModuleChain();
        addModulesChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Arrays.asList(modules));
        addModulesChain.execute();
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
        StringField nameField = getStringField(module, "name", "Name", "NAME", FacilioField.FieldDisplayType.TEXTBOX,
                true, false, true, true, orgId);
        fields.add(nameField);

        /* assetCategory Field */
        LookupField assetCategoryField = getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY),
                "assetCategory", "Asset Category", "ASSET_CATEGORY_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(assetCategoryField);

        /* spaceCategory Field */
        LookupField spaceCategoryField = getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY),
                "spaceCategory", "Space Category", "SPACE_CATEGORY_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(spaceCategoryField);

        /* baseSpace Field */
        LookupField baseSpaceField = getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.BASE_SPACE),
                "baseSpace", "Building", "BASE_SPACE_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(baseSpaceField);

        /* assignmentType Field */
        SystemEnumField assignmentTypeField = getSystemEnumField(module, "assignmentType", "Scope Category",
                "ASSIGNMENT_TYPE", "PMScopeAssigmentType", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, orgId);
        fields.add(assignmentTypeField);

        /* dueDuration Field */
        NumberField dueDurationField = getNumberField(module, "dueDuration", "Due Duration", "DUE_DURATION",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(dueDurationField);

        /* estimatedDuration Field */
        NumberField estimatedDurationField = getNumberField(module, "estimatedDuration", "Estimated Duration", "ESTIMATED_DURATION",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(estimatedDurationField);

        /* isActive Field */
        BooleanField isActiveField = getBooleanField(module, "isActive", "Is Active", "IS_ACTIVE",
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
        LookupField leftField = getLookupField(module, plannedMaintenanceModule, "left", "Planned Maintenance", "PM_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(leftField);

        /* right Field */
        LookupField rightField = getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.SITE),
                "right", "Site", "SITE_ID", null,
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
        NumberField pmIdField = getNumberField(module, "pmId", "PM ID", "PM_ID",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(pmIdField);

        /* schedule Field */
        StringField scheduleField = getStringField(module, "schedule", "Schedule", "SCHEDULE_INFO",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(scheduleField);

        /* frequency Field */
        SystemEnumField frequencyField = getSystemEnumField(module, "frequency", "Frequency", "FREQUENCY",
                "PMTriggerFrequency", FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(frequencyField);

        /* type Field */
        SystemEnumField typeField = getSystemEnumField(module, "type", "Type", "TRIGGER_TYPE",
                "PMTriggerType", FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(typeField);

        /* startTime Field */
        NumberField startTimeField = getNumberField(module, "startTime", "Start Time", "TRIGGER_START_TIME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId); // check for display name
        fields.add(startTimeField);

        /* endTime Field */
        NumberField endTimeField = getNumberField(module, "endTime", "End Time", "TRIGGER_END_TIME",
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
        NumberField pmIdField = getNumberField(module, "pmId", "PM ID", "PM_ID",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(pmIdField);

        /* isPreRequisite Field */
        BooleanField isPreRequisiteField = getBooleanField(module, "isPreRequisite", "Is Pre Requisite",
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
        StringField nameField = getStringField(module, "name", "Name", "NAME",
                FacilioField.FieldDisplayType.TEXTBOX, true, false, true, true, orgId);
        fields.add(nameField);

        /* pmId Field */
        NumberField pmIdField = getNumberField(module, "pmId", "PM ID", "PM_ID",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(pmIdField);

        /* adhocJobPlan Field */
        LookupField adhocJobPlanField = getLookupField(module, pmJobPlanModule, "adhocJobPlan", "As Hoc Task",
                "JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(adhocJobPlanField);

        /* preReqJobPlan Field */
        LookupField preReqJobPlanField = getLookupField(module, pmJobPlanModule, "preReqJobPlan", "Job Plan",
                "PREREQ_JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(preReqJobPlanField);

        /* trigger Field */
        LookupField triggerField = getLookupField(module, pmTriggerV2Module, "trigger", "Trigger",
                "TRIGGER_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(triggerField);

        /* generatedUpto Field */
        NumberField generatedUptoField = getNumberField(module, "generatedUpto", "Generated Upto", "GENERATED_UPTO",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(generatedUptoField);

        module.setFields(fields);

        return module;
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
        NumberField pmIdField = getNumberField(module, "pmId", "PM ID", "PM_ID",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(pmIdField);

        /* resource Field */
        LookupField resourceField = getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE),
                "resource", "Resource", "RESOURCE_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(resourceField);

        /* jobPlan Field */
        LookupField jobPlanField = getLookupField(module, pmJobPlanModule, "jobPlan", "Job Plan",
                "JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(jobPlanField);

        /* assignedTo Field */
        LookupField assignedToField = getLookupField(module, null, "assignedTo", "Staff",
                "ASSIGNED_TO", "users", FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(assignedToField);

        /* planner Field */
        LookupField plannerField = getLookupField(module, pmPlannerModule, "planner", "Planner",
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
        LookupField pmIdField = getLookupField(module, plannedMaintenanceModule, "pmId", "PM",
                "PM_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false,
                true, orgId);
        fields.add(pmIdField);

        /* resource planner - rpResource Field */
        LookupField rpResourceField = getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE),
                "rpResource", "Asset/Space", "RP_RESOURCE_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(rpResourceField);

        /* resource planner - assignedTo Field */
        LookupField assignedToField = getLookupField(module, null,
                "rpAssignedTo", "Asset/Space Assignee", "RP_ASSIGNED_TO", "users",
                FacilioField.FieldDisplayType.LOOKUP_POPUP, false, false, true, orgId);
        fields.add(assignedToField);

        /* resource planner - rpPlanner Field */
        LookupField rpPlannerField = getLookupField(module, pmPlannerModule,
                "rpPlanner", "Asset/Space Planner", "RP_PLANNER_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_POPUP, false, false, true, orgId);
        fields.add(rpPlannerField);

        /* pm planner - plName Field */
        StringField plNameField = getStringField(module, "plName", "Planner name", "PL_NAME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(plNameField);

        /* pm planner - plJobPlan Field */
        LookupField plJobPlanField = getLookupField(module, pmJobPlanModule, "plJobPlan", "Planner Job Plan",
                "PL_JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,
                false, true, orgId);
        fields.add(plJobPlanField);

        /* pm planner - plPreReqJobPlan Field */
        LookupField plPreReqJobPlanField = getLookupField(module, pmJobPlanModule, "plPreReqJobPlan", "Planner Prerequisite Job Plan",
                "PL_PREREQ_JOB_PLAN_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,
                false, true, orgId);
        fields.add(plPreReqJobPlanField);

        /* pm planner - plTrigger Field */
        LookupField plTriggerField = getLookupField(module, pmTriggerV2Module, "plTrigger", "Planner Trigger",
                "PL_TRIGGER_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,
                false, true, orgId);
        fields.add(plTriggerField);

        /* trigger - trFrequency Field */
        StringField trFrequencyField = getStringField(module, "trFrequency", "Trigger Frequency", "TR_FREQUENCY",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        trFrequencyField.setOrgId(orgId);
        fields.add(trFrequencyField);

        /* trigger - trTimes Field */
        StringField trTimesField = getStringField(module, "trTimes", "Trigger Times", "TR_TIMES",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(trTimesField);

        /* trigger - trDays Field */
        StringField trDaysField = getStringField(module, "trDays", "Trigger Days", "TR_DAYS",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(trDaysField);

        /* trigger - trDates Field */
        StringField trDatesField = getStringField(module, "trDates", "Trigger Dates", "TR_DATES",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(trDatesField);

        /* trigger - trWeeks Field */
        StringField trWeeksField = getStringField(module, "trWeeks", "Trigger Weeks", "TR_WEEKS",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(trWeeksField);

        /* trigger - trMonths Field */
        StringField trMonthsField = getStringField(module, "trMonths", "Trigger Months", "TR_MONTHS",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, true, orgId);
        fields.add(trMonthsField);

        /* trigger - trType Field */
        SystemEnumField trTypeField = getSystemEnumField(module, "trType", "Trigger Type", "TR_TRIGGER_TYPE",
                "PMTriggerType", FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(trTypeField);

        /* trigger - trStartTime Field */
        NumberField trStartTimeField = getNumberField(module, "trStartTime", "Trigger Start Time", "TR_TRIGGER_START_TIME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(trStartTimeField);

        /* trigger - trEndTime Field */
        NumberField trEndTimeField = getNumberField(module, "trEndTime", "Trigger End Time", "TR_TRIGGER_END_TIME",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(trEndTimeField);

        /* trigger - skip Field */
        NumberField skipField = getNumberField(module, "skip", "Trigger Skip", "TR_SKIP",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(skipField);

        /* trigger - every Field */
        NumberField everyField = getNumberField(module, "every", "Trigger Every", "TR_EVERY",
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
        LookupField pmIdField = getLookupField(module, plannedMaintenanceModule, "pmId", "PM ID",
                "PM_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,
                false, true, orgId);
        fields.add(pmIdField);

        /* sectionName Field */
        StringField sectionNameField = getStringField(module, "sectionName", "Section", "SECTION",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(sectionNameField);

        /* task Field */
        StringField taskField = getStringField(module, "task", "Task", "TASK",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(taskField);

        /* isPrerequisite Field */
        StringField isPrerequisiteField = getStringField(module, "isPrerequisite", "IS PREREQUISITE", "IS_PREREQUISITE",
                FacilioField.FieldDisplayType.TEXTBOX, false, true, true, true, orgId);
        fields.add(isPrerequisiteField);

        module.setFields(fields);
        return module;
    }


    /**
     * Helper methods declared to get the different FacilioFields.
     * viz. {@link StringField}, {@link NumberField}, {@link BooleanField}, {@link SystemEnumField}, {@link LookupField}
     */

    /**
     * Helper method to get {@link StringField}
     */
    private StringField getStringField(FacilioModule module, String name, String displayName, String columnName, FacilioField.FieldDisplayType displayType,
                                       Boolean required, Boolean disabled, Boolean isDefault, Boolean isMainField, Long orgId) {
        StringField stringField = FieldFactory.getField(name, displayName, columnName, module, FieldType.STRING);
        stringField.setDisplayType(displayType);
        stringField.setRequired(required);
        stringField.setDisabled(disabled);
        stringField.setDefault(isDefault);
        stringField.setMainField(isMainField);
        stringField.setOrgId(orgId);
        return stringField;
    }

    /**
     * Helper method to get {@link NumberField}
     */
    private NumberField getNumberField(FacilioModule module, String name, String displayName, String columnName,
                                       FacilioField.FieldDisplayType displayType, Boolean required, Boolean disabled,
                                       Boolean isDefault, Long orgId) {
        NumberField numberField = FieldFactory.getField(name, displayName, columnName, module, FieldType.NUMBER);
        numberField.setDisplayType(displayType);
        numberField.setRequired(required);
        numberField.setDisabled(disabled);
        numberField.setDefault(isDefault);
        numberField.setOrgId(orgId);
        return numberField;
    }

    /**
     * Helper method to get {@link BooleanField}
     */
    private BooleanField getBooleanField(FacilioModule module, String name, String displayName, String columnName,
                                         FacilioField.FieldDisplayType displayType, Long accessType, Boolean required,
                                         Boolean disabled, Boolean isDefault, Long orgId) {
        BooleanField booleanField = FieldFactory.getField(name, displayName, columnName, module, FieldType.BOOLEAN);
        booleanField.setDisplayType(displayType);
        booleanField.setRequired(required);
        booleanField.setDisabled(disabled);
        booleanField.setDefault(isDefault);
        booleanField.setOrgId(orgId);

        if (accessType != null) {
            booleanField.setAccessType(accessType);
        }
        return booleanField;
    }

    /**
     * Helper method to get {@link SystemEnumField}
     */
    private SystemEnumField getSystemEnumField(FacilioModule module, String name, String displayName, String columnName,
                                               String enumName, FacilioField.FieldDisplayType displayType, Boolean required,
                                               Boolean disabled, Boolean isDefault, Long orgId) {
        SystemEnumField systemEnumField = FieldFactory.getField(name, displayName, columnName, module, FieldType.SYSTEM_ENUM);
        systemEnumField.setDisplayType(displayType);
        systemEnumField.setRequired(required);
        systemEnumField.setDisabled(disabled);
        systemEnumField.setDefault(isDefault);
        systemEnumField.setOrgId(orgId);
        systemEnumField.setEnumName(enumName);
        return systemEnumField;
    }

    /**
     * Helper method to get {@link LookupField}
     */
    private LookupField getLookupField(FacilioModule fieldModule, FacilioModule lookUpModule, String name, String displayName,
                                       String columnName, String specialType, FacilioField.FieldDisplayType displayType, Boolean required,
                                       Boolean disabled, Boolean isDefault, Long orgId) {
        LookupField lookupField = FieldFactory.getField(name, displayName, columnName, fieldModule, FieldType.LOOKUP);
        lookupField.setDisplayType(displayType);
        lookupField.setRequired(required);
        lookupField.setDisabled(disabled);
        lookupField.setDefault(isDefault);
        lookupField.setOrgId(orgId);

        if (lookUpModule != null) {
            lookupField.setLookupModule(lookUpModule);
            lookupField.setLookupModuleId(lookUpModule.getModuleId());
        }

        if (specialType != null) {
            lookupField.setSpecialType(specialType);
        }
        return lookupField;
    }
}