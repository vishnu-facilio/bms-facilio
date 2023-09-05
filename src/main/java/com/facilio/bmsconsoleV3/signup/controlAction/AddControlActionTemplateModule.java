package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@Log4j
public class AddControlActionTemplateModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        FacilioModule controlActionTemplate = constructControlActionTemplateModule(modBean,orgId);
        addControlActionTemplateToControlAction(modBean,controlActionTemplate,orgId);
        constructControlActionTemplateActivityModule(controlActionTemplate,modBean);
        addNightlyJob();
    }
    public FacilioModule constructControlActionTemplateModule(ModuleBean moduleBean,long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule controlActionTemplate = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME, "Control Action Template",
                "Control_Action_Templates", FacilioModule.ModuleType.BASE_ENTITY, moduleBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME),
                true);
        controlActionTemplate.setHideFromParents(true);
        controlActionTemplate.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();
        StringField subject = SignupUtil.getStringField(controlActionTemplate,"subject","Name","SUBJECT", FacilioField.FieldDisplayType.TEXTBOX,
                true,false,true,true,orgId);
        fields.add(subject);
        FacilioModule calendarModule = moduleBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        if(calendarModule == null){
            LOGGER.error("calendar Module Not Found for Org - #"+orgId);
            throw new IllegalArgumentException("Calendar Module Not Found for OrgId - #"+orgId);
        }
        LookupField calendar = SignupUtil.getLookupField(controlActionTemplate,calendarModule,"calendar","Calendar","CALENDAR_ID",null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE,false,false,false,orgId);
        fields.add(calendar);
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

            LookupField controlActionTemplateField = SignupUtil.getLookupField(module, controlActionTemplate, "controlActionTemplate","control Action Template",
                    "CONTROL_ACTION_TEMPLATES_ID", null,FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false,false,true,orgId);
            modBean.addField(controlActionTemplateField);
        }
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
        module.setFields(fields);
        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();
        moduleBean.addSubModule(controlActionTemplateModule.getModuleId(), module.getModuleId());
    }
}
