package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import lombok.extern.log4j.Log4j;

import java.util.*;

@Log4j
public class AddControlActionTemplateModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        FacilioModule controlActionTemplate = constructControlActionTemplateModule(modBean,orgId);
        addControlActionTemplateToControlAction(modBean,controlActionTemplate,orgId);
        constructControlActionTemplateActivityModule(controlActionTemplate,modBean);
        constructControlActionTemplateAttachmentModule(controlActionTemplate,modBean);
        addNightlyJob();
        addSystemButtons();
    }
    public FacilioModule constructControlActionTemplateModule(ModuleBean moduleBean,long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule controlActionTemplate = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME, "Control Action Template",
                "Control_Action_Templates", FacilioModule.ModuleType.BASE_ENTITY, moduleBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME),
                true);
        controlActionTemplate.setHideFromParents(true);
        controlActionTemplate.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();
        StringField subject = SignupUtil.getStringField(controlActionTemplate,"subject","Control Action Template Name","SUBJECT", FacilioField.FieldDisplayType.TEXTBOX,
                true,false,true,true,orgId);
        fields.add(subject);
        FacilioModule calendarModule = moduleBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        if(calendarModule == null){
            LOGGER.error("calendar Module Not Found for Org - #"+orgId);
            throw new IllegalArgumentException("Calendar Module Not Found for OrgId - #"+orgId);
        }
        LookupField calendar = SignupUtil.getLookupField(controlActionTemplate,calendarModule,"calendar","Calendar","CALENDAR_ID",null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE,false,false,true,orgId);
        fields.add(calendar);

        SystemEnumField controlActionTemplateStatus = SignupUtil.getSystemEnumField(controlActionTemplate,"controlActionTemplateStatus","Template Status","CONTROL_ACTION_TEMPLATE_STATUS","ControlActionTemplateStatus",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(controlActionTemplateStatus);

        SystemEnumField controlActionTemplateType = SignupUtil.getSystemEnumField(controlActionTemplate,"controlActionTemplateType","Template Type","CONTROL_ACTION_TEMPLATE_TYPE","ControlActionTemplateType",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(controlActionTemplateType);

        BooleanField isEnableRevert = SignupUtil.getBooleanField(controlActionTemplate,"isEnableRevert","Enable Revert",
                "IS_ENABLED_REVERT", FacilioField.FieldDisplayType.DECISION_BOX,null,false,false,true,orgId);
        fields.add(isEnableRevert);

        controlActionTemplate.setFields(fields);
        modules.add(controlActionTemplate);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return controlActionTemplate;

    }
    private void addControlActionTemplateToControlAction(ModuleBean modBean,FacilioModule controlActionTemplate,long orgId) throws Exception {
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        if(module != null) {

            LookupField controlActionTemplateField = SignupUtil.getLookupField(module, controlActionTemplate, "controlActionTemplate","Control Action Template",
                    "CONTROL_ACTION_TEMPLATES_ID", null,FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,false,true,orgId);
            modBean.addField(controlActionTemplateField);
        }
    }
    public static void addSystemButtons() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SystemButtonRuleContext activateControlActionTemplate = new SystemButtonRuleContext();
        activateControlActionTemplate.setName("Publish");
        activateControlActionTemplate.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        activateControlActionTemplate.setIdentifier("publish");
        activateControlActionTemplate.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria  activationCriteria = new Criteria();
        activationCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("controlActionTemplateStatus"),String.valueOf(V3ControlActionTemplateContext.ControlActionTemplateStatus.IN_ACTIVE.getIndex()), EnumOperators.IS));
        activateControlActionTemplate.setCriteria(activationCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME,activateControlActionTemplate);

        SystemButtonRuleContext  inactivateControlActionTemplate = new SystemButtonRuleContext();
        inactivateControlActionTemplate.setName("Unpublish");
        inactivateControlActionTemplate.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        inactivateControlActionTemplate.setIdentifier("unPublish");
        inactivateControlActionTemplate.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria unPublishCriteria = new Criteria();
        unPublishCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("controlActionTemplateStatus"),String.valueOf(V3ControlActionTemplateContext.ControlActionTemplateStatus.ACTIVE.getIndex()), EnumOperators.IS));
        inactivateControlActionTemplate.setCriteria(unPublishCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME,inactivateControlActionTemplate);

        SystemButtonRuleContext editRecord = new SystemButtonRuleContext();
        editRecord.setName("Edit");
        editRecord.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editRecord.setIdentifier("edit");
        editRecord.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME,editRecord);
    }
    public void addNightlyJob() throws Exception {

        ScheduleInfo si = new ScheduleInfo();
        si.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        si.setTimes(Collections.singletonList("00:01"));

        FacilioTimer.scheduleCalendarJob(AccountUtil.getCurrentOrg().getId(), "controlActionTemplateNightlyJob", DateTimeUtil.getCurrenTime(), si, "facilio");
    }
    private void constructControlActionTemplateActivityModule(FacilioModule controlActionTemplateModule, ModuleBean moduleBean) throws Exception{
        FacilioModule module = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_ACTIVITY_MODULE_NAME,
                "Control Action Template Activity",
                "Control_Action_Template_Activity",
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
        moduleBean.addSubModule(controlActionTemplateModule.getModuleId(), module.getModuleId());
    }
    private FacilioModule constructControlActionTemplateAttachmentModule(FacilioModule controlActionTemplateModule, ModuleBean moduleBean) throws Exception{
        FacilioModule module = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_ATTACHMENT_MODULE_NAME,
                "Control Action Template Attachments", "Control_Action_Template_Attachments",
                FacilioModule.ModuleType.ATTACHMENTS);

        List<FacilioField> fields = new ArrayList<>();

        NumberField fileId = new NumberField(module, "fileId", "File ID", FacilioField.FieldDisplayType.NUMBER, "FILE_ID", FieldType.NUMBER, true, false, true, true);
        fields.add(fileId);

        NumberField parentId = new NumberField(module, "parentId", "Parent", FacilioField.FieldDisplayType.NUMBER, "PARENT_CONTROL_ACTION_TEMPLATE", FieldType.NUMBER, true, false, true, null);
        fields.add(parentId);

        NumberField createdTime = new NumberField(module, "createdTime", "Created Time", FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.NUMBER, true, false, true, null);
        fields.add(createdTime);

        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();

        moduleBean.addSubModule(controlActionTemplateModule.getModuleId(), module.getModuleId());
        return module;
    }
}
