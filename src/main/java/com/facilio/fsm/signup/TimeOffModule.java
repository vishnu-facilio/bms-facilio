package com.facilio.fsm.signup;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.context.TimeOffTypeContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TimeOffModule extends BaseModuleConfig {

    public TimeOffModule(){setModuleName(FacilioConstants.TimeOff.TIME_OFF);}

    @Override
    public void addData() throws Exception {
        addTimeOffTypeModule();
        addTimeOffModule();
        addActivityModuleForTimeOff();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.TimeOff.TIME_OFF));
    }

    public void addTimeOffTypeModule() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule timeOffTypeModule = new FacilioModule(FacilioConstants.TimeOff.TIME_OFF_TYPE,"Time Off Type","TIME_OFF_TYPE", FacilioModule.ModuleType.PICK_LIST);
        List<FacilioField> timeOffTypeFields = new ArrayList<>();

        StringField name = new StringField(timeOffTypeModule,"name","Name", FacilioField.FieldDisplayType.TEXTBOX,"NAME",FieldType.STRING,true,false,true,true);
        timeOffTypeFields.add(name);
        StringField displayName = new StringField(timeOffTypeModule,"displayName","Display Name", FacilioField.FieldDisplayType.TEXTBOX,"DISPLAY_NAME",FieldType.STRING,true,false,true,false);
        timeOffTypeFields.add(displayName);
        StringField description = new StringField(timeOffTypeModule,"description","Description", FacilioField.FieldDisplayType.TEXTAREA,"DESCRIPTION",FieldType.STRING,false,false,true,false);
        timeOffTypeFields.add(description);
        StringField color = new StringField(timeOffTypeModule,"color","Color", FacilioField.FieldDisplayType.TEXTBOX,"COLOR",FieldType.STRING,true,false,true,false);
        timeOffTypeFields.add(color);

        timeOffTypeModule.setFields(timeOffTypeFields);
        modules.add(timeOffTypeModule);


        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        InsertRecordBuilder<TimeOffTypeContext> insertRecordBuilder = new InsertRecordBuilder<TimeOffTypeContext>()
                .moduleName(FacilioConstants.TimeOff.TIME_OFF_TYPE)
                .fields(moduleBean.getAllFields(FacilioConstants.TimeOff.TIME_OFF_TYPE));

        List<TimeOffTypeContext> defaultTypes = new ArrayList<>();

        TimeOffTypeContext standBy = new TimeOffTypeContext();
        standBy.setName("standBy");
        standBy.setDisplayName("Stand By");
        standBy.setColor("#FDE49D");
        defaultTypes.add(standBy);

        TimeOffTypeContext holiday = new TimeOffTypeContext();
        holiday.setName("holiday");
        holiday.setDisplayName("Holiday");
        holiday.setColor("#F4C8E7");
        defaultTypes.add(holiday);

        TimeOffTypeContext sick = new TimeOffTypeContext();
        sick.setName("sick");
        sick.setDisplayName("Sick");
        sick.setColor("#E5F4FF");
        defaultTypes.add(sick);

        TimeOffTypeContext vacation = new TimeOffTypeContext();
        vacation.setName("vacation");
        vacation.setDisplayName("Vacation");
        vacation.setColor("#D7CCF0");
        defaultTypes.add(vacation);

        TimeOffTypeContext truckBreakdown = new TimeOffTypeContext();
        truckBreakdown.setName("truckBreakdown");
        truckBreakdown.setDisplayName("Truck Breakdown");
        truckBreakdown.setColor("#F6D1C8");
        defaultTypes.add(truckBreakdown);

        TimeOffTypeContext training = new TimeOffTypeContext();
        training.setName("training");
        training.setDisplayName("Training");
        training.setColor("#FCE0C3");
        defaultTypes.add(training);

        insertRecordBuilder.addRecords(defaultTypes);
        insertRecordBuilder.save();
    }

    public void addTimeOffModule() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule timeOffModule = new FacilioModule(FacilioConstants.TimeOff.TIME_OFF,"Time Off","Time_Off", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> timeOffFields = new ArrayList<>();
        DateField startTime = new DateField(timeOffModule,"startTime","From", FacilioField.FieldDisplayType.DATETIME,"START_TIME",FieldType.DATE_TIME,true,false,true,false);
        timeOffFields.add(startTime);

        DateField endTime = new DateField(timeOffModule,"endTime","To", FacilioField.FieldDisplayType.DATETIME,"END_TIME",FieldType.DATE_TIME,true,false,true,false);
        timeOffFields.add(endTime);

        LookupField peopleField = new LookupField(timeOffModule,"people","People", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"PEOPLE_ID",FieldType.LOOKUP,true,false,true,true,"Related People", Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE));
        timeOffFields.add(peopleField);

        LookupField typeField = new LookupField(timeOffModule,"type","Type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"TYPE",FieldType.LOOKUP,true,false,true,true,"Type",moduleBean.getModule(FacilioConstants.TimeOff.TIME_OFF_TYPE));
        timeOffFields.add(typeField);

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        timeOffFields.add(moduleStateField);

        NumberField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        timeOffFields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        timeOffFields.add(approvalStateField);

        NumberField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        timeOffFields.add(approvalFlowIdField);

        timeOffModule.setFields(timeOffFields);
        modules.add(timeOffModule);


        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule timeOffModule = modBean.getModule(FacilioConstants.TimeOff.TIME_OFF);

        FacilioForm timeOffForm =new FacilioForm();
        timeOffForm.setDisplayName("Standard");
        timeOffForm.setName("default_asset_web");
        timeOffForm.setModule(timeOffModule);
        timeOffForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        timeOffForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> timeOffFormFields = new ArrayList<>();
        timeOffFormFields.add(new FormField("startTime", FacilioField.FieldDisplayType.DATETIME, "From", FormField.Required.REQUIRED, 1, 3));
        timeOffFormFields.add(new FormField("endTime", FacilioField.FieldDisplayType.DATETIME, "To", FormField.Required.REQUIRED, 2, 3));
        timeOffFormFields.add(new FormField("people",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"People",FormField.Required.REQUIRED,3,3));
        timeOffFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Type", FormField.Required.REQUIRED,4,3));


        FormSection section = new FormSection("Default", 1, timeOffFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        timeOffForm.setSections(Collections.singletonList(section));
        timeOffForm.setIsSystemForm(true);
        timeOffForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> timeOffModuleForms = new ArrayList<>();
        timeOffModuleForms.add(timeOffForm);

        return timeOffModuleForms;
    }

    public void addActivityModuleForTimeOff() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule timeOff = modBean.getModule(FacilioConstants.TimeOff.TIME_OFF);

        FacilioModule module = new FacilioModule(FacilioConstants.TimeOff.TIME_OFF_ACTIVITY,
                "Time Off Activity",
                "Time_Off_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );


        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);

        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);

        fields.add(timefield);

        NumberField type = FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);

        LookupField doneBy = FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);

        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING);

        fields.add(info);


        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();

        modBean.addSubModule(timeOff.getModuleId(), module.getModuleId());
    }
}
