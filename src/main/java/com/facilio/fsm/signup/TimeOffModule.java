package com.facilio.fsm.signup;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
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

        List<String> types = Arrays.asList("StandBy", "Holiday","Sick","Vacation","Truck Breakdown","Training");
        List<EnumFieldValue<Integer>> typeValues = types.stream().map(val -> {
            int index = types.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        EnumField typeField = new EnumField(timeOffModule,"type","Type", FacilioField.FieldDisplayType.SELECTBOX,"TYPE",FieldType.ENUM,true,false,true,true,typeValues);
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
        timeOffFormFields.add(new FormField("type", FacilioField.FieldDisplayType.SELECTBOX, "Type", FormField.Required.REQUIRED,4,3));


        FormSection section = new FormSection("Default", 1, timeOffFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        timeOffForm.setSections(Collections.singletonList(section));
        timeOffForm.setIsSystemForm(true);
        timeOffForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> timeOffModuleForms = new ArrayList<>();
        timeOffModuleForms.add(timeOffForm);

        return timeOffModuleForms;
    }
}
