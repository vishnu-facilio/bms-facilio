package com.facilio.remotemonitoring.signup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.impl.PageBuilderConfigUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.DateField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.operations.Bool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddSubModuleRelations extends SignUpData {
    public static final String ALARM_TYPE_ACTIVITY = "alarmTypeActivity";
    public static final String ALARM_CATEGORY_ACTIVITY = "alarmCategoryActivity";
    public static final String ALARM_DEFINITION_ACTIVITY = "alarmDefinitionActivity";
    public static final String ALARM_DEFINITION_MAPPING_ACTIVITY = "alarmDefinitionMappingActivity";
    public static final String ALARM_DEFINITION_TAGGING_ACTIVITY = "alarmDefinitionTaggingActivity";
    public static final String ALARM_FILTER_RULE_ACTIVITY = "alarmFilterRuleActivity";
    public static final String RAW_ALARM_ACTIVITY = "rawAlarmActivity";
    public static final String FILTER_ALARM_ACTIVITY = "filterAlarmActivity";
    public static final String FLAGGED_EVENT_ACTIVITY = "flaggedEventActivity";
    public static final String FLAGGED_EVENT_RULE_ACTIVITY = "flaggedEventRuleActivity";
    public static final String ALARM_ASSET_TAGGING_ACTIVITY = "alarmAssetTaggingActivity";


    @Override
    public void addData() throws Exception {
        addAlarmTypeActivity();
        addAlarmCategoryActivity();
        addAlarmDefinitionActivity();
        addAlarmDefinitionMappingActivity();
        addAlarmDefinitionTaggingActivity();
        addRawAlarmActivityModule();
        addFilteredAlarmActivityModule();
        addFilterRuleActivityModule();
        addFlaggedEventActivityModule();
        addFlaggedEventRuleActivityModule();
        addAlarmAssetTaggingActivityModule();

        PageBuilderConfigUtil.updatePageBuilderModuleSettings(AccountUtil.getCurrentOrg().getId(), new String[] {
                AlarmTypeModule.MODULE_NAME,
                AlarmDefinitionModule.MODULE_NAME,
                AlarmCategoryModule.MODULE_NAME,
                AlarmDefinitionTaggingModule.MODULE_NAME,
                AlarmDefinitionMappingModule.MODULE_NAME,
                FacilioConstants.ContextNames.CONTROLLER,
                FacilioConstants.ContextNames.CLIENT,
                FacilioConstants.ContextNames.CLIENT_CONTACT,
                AlarmFilterRuleModule.MODULE_NAME,
                FilteredAlarmModule.MODULE_NAME,
                FlaggedEventRuleModule.MODULE_NAME,
                FlaggedEventModule.MODULE_NAME,
                AlarmAssetTaggingModule.MODULE_NAME
        });

        addSystemButtonsForFlaggedEvent();
    }

    private void addAlarmDefinitionTaggingActivity() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(ALARM_DEFINITION_TAGGING_ACTIVITY,
                "Alarm Type Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(AlarmDefinitionTaggingModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }

    private void addAlarmDefinitionMappingActivity() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(ALARM_DEFINITION_MAPPING_ACTIVITY,
                "Alarm Type Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(AlarmDefinitionMappingModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }
    private void addAlarmDefinitionActivity() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(ALARM_DEFINITION_ACTIVITY,
                "Alarm Type Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(AlarmDefinitionModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }

    private void addAlarmCategoryActivity() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(ALARM_CATEGORY_ACTIVITY,
                "Alarm Type Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(AlarmCategoryModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }
    private void addAlarmTypeActivity() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(ALARM_TYPE_ACTIVITY,
                "Alarm Type Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(AlarmTypeModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);

    }
    private void addRawAlarmActivityModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(RAW_ALARM_ACTIVITY,
                "Raw Alarm Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(RawAlarmModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }

    private void addFilterRuleActivityModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(ALARM_FILTER_RULE_ACTIVITY,
                "Filter Rule Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(AlarmFilterRuleModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }

    private void addFilteredAlarmActivityModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(FILTER_ALARM_ACTIVITY,
                "Filtered Alarm Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(FilteredAlarmModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }

    private void addFlaggedEventActivityModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(FLAGGED_EVENT_ACTIVITY,
                "Flagged Event Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(FlaggedEventModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }


    private void addFlaggedEventRuleActivityModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(FLAGGED_EVENT_RULE_ACTIVITY,
                "Flagged Event Rule Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(FlaggedEventRuleModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }


    private void addAlarmAssetTaggingActivityModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(ALARM_ASSET_TAGGING_ACTIVITY,
                "Alarm Asset Tagging Activity",
                "Custom_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        FacilioModule fetchedModule = modBean.getModule(AlarmAssetTaggingModule.MODULE_NAME);

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

        modBean.addSubModule(fetchedModule.getModuleId(), module.getModuleId());
        SignupUtil.addNotesAndAttachmentModule(fetchedModule);
    }

    public static void addSystemButtonsForFlaggedEvent() throws Exception {
        createWorkorderFlaggedEventButton();
        takeCustodyFlaggedEventButton();
        inhibitFlaggedEventButton();
        passToNextBureauButton();
        takeActionSystemButton();
        assignToSystemButton();
        suspendAlarmButton();
    }

    public static void createWorkorderFlaggedEventButton()  throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        //Flagged Event Create Workorder Button
        SystemButtonRuleContext createWorkorderSystemButton = new SystemButtonRuleContext();
        createWorkorderSystemButton.setPermissionRequired(true);
        createWorkorderSystemButton.setPermission("READ");
        createWorkorderSystemButton.setIdentifier("flagged_event_create_workorder");
        Criteria criteria = new Criteria();
        createWorkorderSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        createWorkorderSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        createWorkorderSystemButton.setName("Create Workorder");

        Criteria lookupCriteria = new Criteria();
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("assignedPeople",FlaggedEventBureauActionModule.MODULE_NAME),(String) null, PeopleOperator.CURRENT_USER));
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("eventStatus",FlaggedEventBureauActionModule.MODULE_NAME),"UNDER_CUSTODY,ACTION_TAKEN,INHIBIT", StringSystemEnumOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("currentBureauActionDetail",FlaggedEventModule.MODULE_NAME), lookupCriteria, LookupOperator.LOOKUP));

        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("status",FlaggedEventModule.MODULE_NAME),
                FlaggedEventContext.FlaggedEventStatus.OPEN.getIndex() + "," +
                        FlaggedEventContext.FlaggedEventStatus.SUSPENDED.getIndex() , StringSystemEnumOperators.IS));


        Criteria timeoutCriteria = new Criteria();
        timeoutCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("team",FlaggedEventModule.MODULE_NAME), "", TeamOperator.CURRENT_PEOPLE_IN_TEAM));
        timeoutCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("status",FlaggedEventModule.MODULE_NAME),"TIMEOUT", StringSystemEnumOperators.IS));
        Criteria ruleLookupCriteriaForTimeout = new Criteria();
        ruleLookupCriteriaForTimeout.addAndCondition(CriteriaAPI.getCondition(modBean.getField("autoCreateWorkOrder", FlaggedEventRuleModule.MODULE_NAME),"false", BooleanOperators.IS));
        timeoutCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME,FlaggedEventModule.MODULE_NAME), ruleLookupCriteriaForTimeout, LookupOperator.LOOKUP));
        criteria.orCriteria(timeoutCriteria);

        Criteria ruleLookupCriteria = new Criteria();
        ruleLookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("createWorkorder",FlaggedEventRuleModule.MODULE_NAME),"true", BooleanOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME,FlaggedEventModule.MODULE_NAME), ruleLookupCriteria, LookupOperator.LOOKUP));


        createWorkorderSystemButton.setCriteria(criteria);
        SystemButtonApi.addSystemButton(FlaggedEventModule.MODULE_NAME,createWorkorderSystemButton);
    }


    public static void takeCustodyFlaggedEventButton()  throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SystemButtonRuleContext systemButton = new SystemButtonRuleContext();
        systemButton.setPermissionRequired(true);
        systemButton.setPermission("READ");
        systemButton.setIdentifier("flagged_event_take_custody");
        systemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        systemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        systemButton.setName("Take Custody");


//        FacilioField groupField = modBean.getField("group",FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
//        Criteria groupMemberCriteria = new Criteria();
//        FacilioField peopleField = modBean.getField("people",FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
//        groupMemberCriteria.addAndCondition(CriteriaAPI.getCondition(peopleField,(String) null, PeopleOperator.CURRENT_USER));
//        Criteria crit = new Criteria();
//        crit.addAndCondition(CriteriaAPI.getCondition(groupField,groupMemberCriteria, RelatedModuleOperator.RELATED));
        Criteria parentCriteria = new Criteria();
//        parentCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("team", FlaggedEventModule.MODULE_NAME),crit, LookupOperator.LOOKUP));
        parentCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("status",FlaggedEventModule.MODULE_NAME), FlaggedEventContext.FlaggedEventStatus.OPEN.getIndex(), StringSystemEnumOperators.IS));
        Criteria criteria = new Criteria();
        Criteria lookupCriteria = new Criteria();
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("eventStatus",FlaggedEventBureauActionModule.MODULE_NAME), StringUtils.join(Arrays.asList(FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN.getIndex(),FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.NOT_STARTED.getIndex()),","), StringSystemEnumOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("currentBureauActionDetail",FlaggedEventModule.MODULE_NAME), lookupCriteria, LookupOperator.LOOKUP));
        parentCriteria.andCriteria(criteria);

        Criteria timeoutCriteria = new Criteria();
        timeoutCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("status",FlaggedEventModule.MODULE_NAME),"TIMEOUT", StringSystemEnumOperators.IS));
        Criteria ruleLookupCriteriaForTimeout = new Criteria();
        ruleLookupCriteriaForTimeout.addAndCondition(CriteriaAPI.getCondition(modBean.getField("createWorkorder",FlaggedEventRuleModule.MODULE_NAME),"true", BooleanOperators.IS));
        ruleLookupCriteriaForTimeout.addAndCondition(CriteriaAPI.getCondition(modBean.getField("autoCreateWorkOrder", FlaggedEventRuleModule.MODULE_NAME),"false", BooleanOperators.IS));
        timeoutCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME,FlaggedEventModule.MODULE_NAME), ruleLookupCriteriaForTimeout, LookupOperator.LOOKUP));
        parentCriteria.orCriteria(timeoutCriteria);


        parentCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("team",FlaggedEventModule.MODULE_NAME), "", TeamOperator.CURRENT_PEOPLE_IN_TEAM));

        systemButton.setCriteria(parentCriteria);

        SystemButtonApi.addSystemButton(FlaggedEventModule.MODULE_NAME,systemButton);
    }

    public static void inhibitFlaggedEventButton()  throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SystemButtonRuleContext systemButton = new SystemButtonRuleContext();
        systemButton.setPermissionRequired(true);
        systemButton.setPermission("READ");
        systemButton.setIdentifier("inhibit_flagged_event_button");
        systemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        systemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        systemButton.setName("Snooze");

        Criteria criteria = new Criteria();
        Criteria lookupCriteria = new Criteria();
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("assignedPeople",FlaggedEventBureauActionModule.MODULE_NAME),(String) null, PeopleOperator.CURRENT_USER));
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("eventStatus",FlaggedEventBureauActionModule.MODULE_NAME),"UNDER_CUSTODY", StringSystemEnumOperators.IS));
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("isSLABreached",FlaggedEventBureauActionModule.MODULE_NAME), "false", BooleanOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("currentBureauActionDetail",FlaggedEventModule.MODULE_NAME), lookupCriteria, LookupOperator.LOOKUP));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("status", FlaggedEventModule.MODULE_NAME), FlaggedEventContext.FlaggedEventStatus.TIMEOUT.getIndex(), StringSystemEnumOperators.ISN_T));

        systemButton.setCriteria(criteria);

        SystemButtonApi.addSystemButton(FlaggedEventModule.MODULE_NAME,systemButton);
    }

    private static void passToNextBureauButton()  throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SystemButtonRuleContext systemButton = new SystemButtonRuleContext();
        systemButton.setPermissionRequired(true);
        systemButton.setPermission("READ");
        systemButton.setIdentifier("pass_to_next_bureau_flagged_event_button");
        systemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        systemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        systemButton.setName("Forward");

        Criteria criteria = new Criteria();
        Criteria lookupCriteria = new Criteria();
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("assignedPeople",FlaggedEventBureauActionModule.MODULE_NAME),(String) null, PeopleOperator.CURRENT_USER));
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("eventStatus",FlaggedEventBureauActionModule.MODULE_NAME),"UNDER_CUSTODY", StringSystemEnumOperators.IS));
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("isFinalTeam",FlaggedEventBureauActionModule.MODULE_NAME),"false", BooleanOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("currentBureauActionDetail",FlaggedEventModule.MODULE_NAME), lookupCriteria, LookupOperator.LOOKUP));

        systemButton.setCriteria(criteria);

        SystemButtonApi.addSystemButton(FlaggedEventModule.MODULE_NAME,systemButton);
    }

    public static void takeActionSystemButton()  throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SystemButtonRuleContext systemButton = new SystemButtonRuleContext();
        systemButton.setPermissionRequired(true);
        systemButton.setPermission("READ");
        systemButton.setIdentifier("flagged_event_take_action");
        systemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        systemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        systemButton.setName("Take Action");

        Criteria criteria = new Criteria();
        Criteria lookupCriteria = new Criteria();
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("assignedPeople",FlaggedEventBureauActionModule.MODULE_NAME),(String) null, PeopleOperator.CURRENT_USER));
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("eventStatus",FlaggedEventBureauActionModule.MODULE_NAME),"UNDER_CUSTODY, INHIBIT", StringSystemEnumOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("currentBureauActionDetail",FlaggedEventModule.MODULE_NAME), lookupCriteria, LookupOperator.LOOKUP));

        systemButton.setCriteria(criteria);

        SystemButtonApi.addSystemButton(FlaggedEventModule.MODULE_NAME,systemButton);
    }

    public static void assignToSystemButton()  throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SystemButtonRuleContext systemButton = new SystemButtonRuleContext();
        systemButton.setPermissionRequired(true);
        systemButton.setPermission("READ");
        systemButton.setIdentifier("flagged_event_assign_to");
        systemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        systemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        systemButton.setName("Assign");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("currentBureauActionDetail",FlaggedEventModule.MODULE_NAME),"",CommonOperators.IS_NOT_EMPTY));
        Criteria lookupCriteria = new Criteria();
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("eventStatus",FlaggedEventBureauActionModule.MODULE_NAME),
                FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN.getIndex() + "," +
                FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.UNDER_CUSTODY.getIndex() + "," +
                        FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.INHIBIT.getIndex()
                , StringSystemEnumOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("currentBureauActionDetail",FlaggedEventModule.MODULE_NAME), lookupCriteria, LookupOperator.LOOKUP));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("team",FlaggedEventModule.MODULE_NAME), "", TeamOperator.CURRENT_PEOPLE_IN_TEAM));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("status", FlaggedEventModule.MODULE_NAME), FlaggedEventContext.FlaggedEventStatus.SUSPENDED.getIndex(), StringSystemEnumOperators.ISN_T));

        Criteria timeoutCriteria = new Criteria();
        timeoutCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("team",FlaggedEventModule.MODULE_NAME), "", TeamOperator.CURRENT_PEOPLE_IN_TEAM));
        timeoutCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("status",FlaggedEventModule.MODULE_NAME),"TIMEOUT", StringSystemEnumOperators.IS));
        Criteria ruleLookupCriteriaForTimeout = new Criteria();
        ruleLookupCriteriaForTimeout.addAndCondition(CriteriaAPI.getCondition(modBean.getField("createWorkorder",FlaggedEventRuleModule.MODULE_NAME),"true", BooleanOperators.IS));
        ruleLookupCriteriaForTimeout.addAndCondition(CriteriaAPI.getCondition(modBean.getField("autoCreateWorkOrder", FlaggedEventRuleModule.MODULE_NAME),"false", BooleanOperators.IS));
        timeoutCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME,FlaggedEventModule.MODULE_NAME), ruleLookupCriteriaForTimeout, LookupOperator.LOOKUP));
        criteria.orCriteria(timeoutCriteria);

        systemButton.setCriteria(criteria);

        SystemButtonApi.addSystemButton(FlaggedEventModule.MODULE_NAME,systemButton);
    }


    public static void suspendAlarmButton()  throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SystemButtonRuleContext systemButton = new SystemButtonRuleContext();
        systemButton.setPermissionRequired(true);
        systemButton.setPermission("READ");
        systemButton.setIdentifier("flagged_event_suspend_alarm");
        systemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        systemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        systemButton.setName("Suspend Alarm");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("currentBureauActionDetail",FlaggedEventModule.MODULE_NAME),"",CommonOperators.IS_NOT_EMPTY));
        Criteria lookupCriteria = new Criteria();
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("eventStatus",FlaggedEventBureauActionModule.MODULE_NAME), FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.UNDER_CUSTODY.getIndex(), StringSystemEnumOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("currentBureauActionDetail",FlaggedEventModule.MODULE_NAME), lookupCriteria, LookupOperator.LOOKUP));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("team",FlaggedEventModule.MODULE_NAME), "", TeamOperator.CURRENT_PEOPLE_IN_TEAM));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("asset", FlaggedEventModule.MODULE_NAME), "", CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("status", FlaggedEventModule.MODULE_NAME), FlaggedEventContext.FlaggedEventStatus.SUSPENDED.getIndex(), StringSystemEnumOperators.ISN_T));

        systemButton.setCriteria(criteria);

        SystemButtonApi.addSystemButton(FlaggedEventModule.MODULE_NAME,systemButton);
    }
}