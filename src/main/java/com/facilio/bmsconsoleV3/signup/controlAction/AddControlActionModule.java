package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.joda.time.DateTimeField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AddControlActionModule extends SignUpData {

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        FacilioModule controlActionModule = constructControlActionModule(modBean,orgId);
        FacilioModule controlActionFirstLevelApproval = constructControlActionFirstLevelApprovalModule(modBean,orgId,controlActionModule);
        FacilioModule controlActionSecondLevelApproval = constructControlActionSecondLevelApprovalModule(modBean,orgId,controlActionModule);
        addApprovalFieldToControlActionModule(modBean,orgId,controlActionModule,controlActionFirstLevelApproval,controlActionSecondLevelApproval);
        modBean.addSubModule(controlActionModule.getModuleId(),controlActionFirstLevelApproval.getModuleId());
        modBean.addSubModule(controlActionModule.getModuleId(),controlActionSecondLevelApproval.getModuleId());
        FacilioModule actionModule = constructActionModule(modBean,orgId,controlActionModule);
        FacilioModule commandModule = constructCommandModule(modBean,orgId,controlActionModule,actionModule);
        constructControlActionActivityModule(controlActionModule,modBean);
        constructControlActionNotesModule(modBean,orgId,controlActionModule);
    }
    private FacilioModule constructControlActionModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule controlActionModule = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,"Control Action","Control_Actions",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        controlActionModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        StringField name = SignupUtil.getStringField(controlActionModule,"name","Name","NAME", FacilioField.FieldDisplayType.TEXTBOX,
                true,false,true,true,orgId);
        fields.add(name);

        StringField desc = SignupUtil.getStringField(controlActionModule,"description","Description","DESCRIPTION", FacilioField.FieldDisplayType.TEXTBOX,
                false,false,false,false,orgId);
        fields.add(desc);

        SystemEnumField sourceType = SignupUtil.getSystemEnumField(controlActionModule,"controlActionSourceType","Source Type","SOURCE_TYPE","ControlActionSourceTypeEnum",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,false,orgId);
        fields.add(sourceType);

        SystemEnumField calendarType = SignupUtil.getSystemEnumField(controlActionModule,"controlActionType","Type","CONTROL_ACTION_TYPE","ControlActionTypeEnum",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,false,orgId);
        fields.add(calendarType);

        LookupField assetCategoryField = SignupUtil.getLookupField(controlActionModule, moduleBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY),
                "assetCategory", "Asset Category", "ASSET_CATEGORY_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, true, false, true, orgId);
        fields.add(assetCategoryField);

        FacilioField  scheduledActionDateTime = FieldFactory.getDefaultField("scheduledActionDateTime","Scheduled Action Date Time","SCHEDULED_ACTION_DATE_TIME", FieldType.DATE_TIME);
        fields.add(scheduledActionDateTime);

        FacilioField revertActionDateTime = FieldFactory.getDefaultField("revertActionDateTime","Revert Action Date Time","REVERT_ACTION_DATE_TIME",FieldType.DATE_TIME);
        fields.add(revertActionDateTime);

        NumberField siteCriteriaId = SignupUtil.getNumberField(controlActionModule,"siteCriteriaId","Site Criteria Id","SITE_CRITERIA_ID",
                FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(siteCriteriaId);

        NumberField assetCriteriaId = SignupUtil.getNumberField(controlActionModule,"assetCriteriaId","Asset Criteria Id","ASSET_CRITERIA_ID",
                FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(assetCriteriaId);


        NumberField controllerCriteriaId = SignupUtil.getNumberField(controlActionModule,"controllerCriteriaId","Controller Criteria Id","CONTROLLER_CRITERIA_ID",
                FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(controllerCriteriaId);

        LookupField approvalState = SignupUtil.getLookupField(controlActionModule,moduleBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS),"approvalStatus","Approval Status",
                "APPROVAL_STATE",null, FacilioField.FieldDisplayType.SELECTBOX,false,false,false,orgId);
        fields.add(approvalState);

        NumberField approvalFlowId = SignupUtil.getNumberField(controlActionModule,"approvalFlowId","Approval Flow Id","APPROVAL_FLOW_ID",
                FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(approvalFlowId);

        SystemEnumField controlActionStatus = SignupUtil.getSystemEnumField(controlActionModule,"controlActionStatus","Status","STATUS","ControlActionStatus",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,false,orgId);
        fields.add(controlActionStatus);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        controlActionModule.setFields(fields);
        modules.add(controlActionModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return controlActionModule;
    }
    private FacilioModule constructActionModule(ModuleBean moduleBean, long orgId, FacilioModule controlActionModule) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule actionsModule = new FacilioModule(FacilioConstants.Control_Action.ACTION_MODULE_NAME,"Action","Actions",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        controlActionModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        LookupField controlAction = SignupUtil.getLookupField(actionsModule,controlActionModule,"controlAction","Control Action","CONTROL_ACTION_ID",null,
                FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(controlAction);

        SystemEnumField actionVariableType = SignupUtil.getSystemEnumField(actionsModule,"actionVariableType","Variable Type","VARIABLE_TYPE","ActionVariableTypeEnum",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,false,orgId);
        fields.add(actionVariableType);

        NumberField readingFieldId = SignupUtil.getNumberField(actionsModule,"readingFieldId","Reading","READING_FIELD_ID",
                FacilioField.FieldDisplayType.SELECTBOX, true,false,false,orgId);
        fields.add(readingFieldId);

        NumberField readingFieldDataType = SignupUtil.getNumberField(actionsModule,"readingFieldDataType","Data Type","READING_FIELD_DATE_TYPE",
                FacilioField.FieldDisplayType.TEXTBOX,true,true,false,orgId);
        fields.add(readingFieldDataType);

        SystemEnumField scheduledActionOperatorType = SignupUtil.getSystemEnumField(actionsModule,"scheduledActionOperatorType","Operator","SCHEDULE_ACTION_OPERATOR_TYPE","ActionOperatorTypeEnum",
                FacilioField.FieldDisplayType.SELECTBOX,true,false,false,orgId);
        fields.add(scheduledActionOperatorType);

        StringField scheduleActionValue = SignupUtil.getStringField(actionsModule,"scheduleActionValue","Schedule Action Value","SCHEDULE_ACTION_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                true,false,true,true,orgId);
        fields.add(scheduleActionValue);

        SystemEnumField revertActionOperatorType = SignupUtil.getSystemEnumField(actionsModule,"revertActionOperatorType","Operator","REVERT_ACTION_OPERATOR_TYPE","ActionOperatorTypeEnum",
                FacilioField.FieldDisplayType.SELECTBOX,true,false,false,orgId);
        fields.add(revertActionOperatorType);

        StringField revertActionValue = SignupUtil.getStringField(actionsModule,"revertActionValue","Revert Action Value","REVERT_ACTION_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                true,false,true,true,orgId);
        fields.add(revertActionValue);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        actionsModule.setFields(fields);
        modules.add(actionsModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return actionsModule;
    }
    private FacilioModule constructControlActionFirstLevelApprovalModule(ModuleBean moduleBean,long orgId,FacilioModule controlAction) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule controlActionFirstLevelApprovalModule = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME,"First Level Approval",
                "Control_Action_First_Level_Approval", FacilioModule.ModuleType.SUB_ENTITY,true);
        controlActionFirstLevelApprovalModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();
        LookupField leftField = SignupUtil.getLookupField(controlActionFirstLevelApprovalModule, controlAction, "left", "Control Action", "LEFT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(leftField);

        LookupField rightField = SignupUtil.getLookupField(controlActionFirstLevelApprovalModule, moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),
                "right", "Approver", "RIGHT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(rightField);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        controlActionFirstLevelApprovalModule.setFields(fields);

        modules.add(controlActionFirstLevelApprovalModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return controlActionFirstLevelApprovalModule;
    }
    private FacilioModule constructControlActionSecondLevelApprovalModule(ModuleBean moduleBean,long orgId,FacilioModule controlAction) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule controlActionSecondLevelApprovalModule = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_SECOND_LEVEL_APPROVAL_MODULE_NAME,"Second Level Approval",
                "Control_Action_Second_Level_Approval", FacilioModule.ModuleType.SUB_ENTITY,true);
        controlActionSecondLevelApprovalModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();
        LookupField leftField = SignupUtil.getLookupField(controlActionSecondLevelApprovalModule, controlAction, "left", "Control Action", "LEFT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(leftField);

        LookupField rightField = SignupUtil.getLookupField(controlActionSecondLevelApprovalModule, moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),
                "right", "Approver", "RIGHT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(rightField);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        controlActionSecondLevelApprovalModule.setFields(fields);

        modules.add(controlActionSecondLevelApprovalModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return controlActionSecondLevelApprovalModule;
    }
    private void addApprovalFieldToControlActionModule(ModuleBean moduleBean, long orgId, FacilioModule controlAction,
                                                          FacilioModule controlActionFirstLevelApprovalModule, FacilioModule controlActionSecondLevelApprovalModule) throws Exception {
        /* sites Field */
        MultiLookupField  firstLevelApproval = FieldFactory.getDefaultField("firstLevelApproval", null, null, FieldType.MULTI_LOOKUP);
        firstLevelApproval.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        firstLevelApproval.setRequired(false);
        firstLevelApproval.setDisabled(false);
        firstLevelApproval.setDefault(false);
        firstLevelApproval.setMainField(false);
        firstLevelApproval.setOrgId(orgId);
        firstLevelApproval.setModule(controlAction);
        firstLevelApproval.setLookupModuleId(controlActionFirstLevelApprovalModule.getModuleId());
        firstLevelApproval.setRelModuleId(controlActionFirstLevelApprovalModule.getModuleId());
        firstLevelApproval.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        moduleBean.addField(firstLevelApproval);

        MultiLookupField  secondLevelApproval = FieldFactory.getDefaultField("secondLevelApproval", null, null, FieldType.MULTI_LOOKUP);
        secondLevelApproval.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        secondLevelApproval.setRequired(false);
        secondLevelApproval.setDisabled(false);
        secondLevelApproval.setDefault(false);
        secondLevelApproval.setMainField(false);
        secondLevelApproval.setOrgId(orgId);
        secondLevelApproval.setModule(controlAction);
        secondLevelApproval.setLookupModuleId(controlActionSecondLevelApprovalModule.getModuleId());
        secondLevelApproval.setRelModuleId(controlActionSecondLevelApprovalModule.getModuleId());
        secondLevelApproval.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        moduleBean.addField(secondLevelApproval);


    }

    private FacilioModule constructCommandModule(ModuleBean moduleBean,long orgId, FacilioModule controlActionModule, FacilioModule actionModule) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule commandModule = new FacilioModule(FacilioConstants.Control_Action.COMMAND_MODULE_NAME,"Command","Commands",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        controlActionModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        LookupField controlAction = SignupUtil.getLookupField(commandModule,controlActionModule,"controlAction","Control Action","CONTROL_ACTION_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,false,orgId);
        fields.add(controlAction);

        LookupField action = SignupUtil.getLookupField(commandModule,actionModule,"action","Action","ACTION_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,false,orgId);
        fields.add(action);

        LookupField site = SignupUtil.getLookupField(commandModule,moduleBean.getModule(FacilioConstants.ContextNames.SITE),"site","Site","SITE_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,false,orgId);
        fields.add(site);

        LookupField asset = SignupUtil.getLookupField(commandModule,moduleBean.getModule(FacilioConstants.ContextNames.ASSET),"asset","Asset","ASSET_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,false,orgId);
        fields.add(asset);

        LookupField controller = SignupUtil.getLookupField(commandModule,moduleBean.getModule(FacilioConstants.ContextNames.CONTROLLER),"controller","Controller","CONTROLLER_ID",
                null, FacilioField.FieldDisplayType.TEXTBOX,true,true,false,orgId);
        fields.add(controller);

        NumberField fieldId = SignupUtil.getNumberField(commandModule,"fieldId","Field","FIELD_ID", FacilioField.FieldDisplayType.TEXTBOX,
                false,false,false,orgId);
        fields.add(fieldId);

        FacilioField actionTime = FieldFactory.getDefaultField("actionTime","Action Time","ACTION_TIME",FieldType.DATE_TIME);
        fields.add(actionTime);

        StringField setValue = SignupUtil.getStringField(commandModule,"setValue","Set Value","SET_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                false,false,false,false,orgId);
        fields.add(setValue);

        StringField afterValue = SignupUtil.getStringField(commandModule,"afterValue","After Value","AFTER_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                false,false,false,false,orgId);
        fields.add(afterValue);

        StringField previousValue = SignupUtil.getStringField(commandModule,"previousValue","Previous Value","PREVIOUS_VALUE", FacilioField.FieldDisplayType.TEXTBOX,
                false,false,false,false,orgId);
        fields.add(previousValue);

        SystemEnumField controlActionCommandStatus = SignupUtil.getSystemEnumField(commandModule,"controlActionCommandStatus","Status","STATUS","ControlActionCommandStatus",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,false,orgId);
        fields.add(controlActionCommandStatus);

        StringField errorMsg = SignupUtil.getStringField(commandModule,"errorMsg","Error Msg","ERROR_MSG",
                FacilioField.FieldDisplayType.TEXTAREA,false,false,false,false,orgId);
        fields.add(errorMsg);

        FacilioField previousValueCapturedTime = FieldFactory.getDefaultField("previousValueCapturedTime","Previous Value Captured Time","PREVIOUS_VALUE_CAPTURED_TIME",FieldType.DATE_TIME);
        fields.add(previousValueCapturedTime);

        SystemEnumField commandActionType = SignupUtil.getSystemEnumField(commandModule,"commandActionType","Action Type","ACTION_TYPE","CommandActionType",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,false,orgId);
        fields.add(commandActionType);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));


        commandModule.setFields(fields);
        modules.add(commandModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return commandModule;
    }
    private void constructControlActionActivityModule(FacilioModule controlActionModule, ModuleBean moduleBean) throws Exception{
        FacilioModule module = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_ACTIVITY_MODULE_NAME,
                "Control Action Activity",
                "Control_Action_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );
        List<FacilioField> fields = new ArrayList<>();
        NumberField parentId = (NumberField) FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);
        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);
        fields.add(timefield);
        NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);
        LookupField doneBy = (LookupField) FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_POPUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);
        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA);
        fields.add(info);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));


        module.setFields(fields);
        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();
        moduleBean.addSubModule(controlActionModule.getModuleId(), module.getModuleId());
    }
    private FacilioModule constructControlActionNotesModule(ModuleBean modBean, long orgId, FacilioModule controlActionModule) throws Exception {
        FacilioModule notesModule = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_NOTES_MODULE_NAME, "Control Action Notes",
                "Control_Action_Notes", FacilioModule.ModuleType.NOTES, null,
                null);

        List<FacilioField> fields = new ArrayList<>();

        NumberField parentIdField = SignupUtil.getNumberField(notesModule,
                "parentId", "Parent", "PARENT_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(parentIdField);

        StringField titleField = SignupUtil.getStringField(notesModule,
                "title", "Title",  "TITLE", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, false,orgId);
        fields.add(titleField);

        StringField bodyField = SignupUtil.getStringField(notesModule,
                "body", "Body", "BODY", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false,orgId);
        fields.add(bodyField);

        StringField bodyHtmlField = SignupUtil.getStringField(notesModule,
                "bodyHTML", "Body HTML", "BODY_HTML", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false,orgId);
        fields.add(bodyHtmlField);

        LookupField parentNote = SignupUtil.getLookupField(notesModule, notesModule, "parentNote", "Parent Note",
                "PARENT_NOTE", null, FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(parentNote);

        BooleanField notifyRequester = SignupUtil.getBooleanField(notesModule,"notifyRequester","Notify Requester","NOTIFY_REQUESTER",
                FacilioField.FieldDisplayType.DECISION_BOX,null,false,false,false,orgId);
        fields.add(notifyRequester);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);


        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));


        notesModule.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(notesModule));
        addModuleChain1.execute();

        modBean.addSubModule(controlActionModule.getModuleId(), notesModule.getModuleId());

        return notesModule;
    }
}
