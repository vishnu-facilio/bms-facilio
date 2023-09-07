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
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceOrderPlannedToolsModule extends BaseModuleConfig {
    public ServiceOrderPlannedToolsModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        FacilioModule serviceOrder = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule serviceTask = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule serviceAppointment = bean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioModule toolType = bean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
        FacilioModule storeRoom = bean.getModule(FacilioConstants.ContextNames.STORE_ROOM);

        if(serviceOrder!=null && serviceOrder.getModuleId()>0 && serviceAppointment!=null && serviceAppointment.getModuleId()>0 && toolType!=null && toolType.getModuleId()>0 && storeRoom!=null && storeRoom.getModuleId()>0){

            FacilioModule serviceOrderPlannedToolsModule = constructServiceOrderPlannedToolsModule(toolType,storeRoom);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderPlannedToolsModule));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
            bean.addSubModule(serviceOrder.getModuleId(), serviceOrderPlannedToolsModule.getModuleId(),0);
            bean.addSubModule(serviceTask.getModuleId(), serviceOrderPlannedToolsModule.getModuleId(),0);
            bean.addSubModule(serviceAppointment.getModuleId(), serviceOrderPlannedToolsModule.getModuleId(),0);
            createParentFields(serviceOrder,serviceTask,serviceAppointment,serviceOrderPlannedToolsModule);
        }

    }
    private FacilioModule constructServiceOrderPlannedToolsModule(FacilioModule toolTypeMod, FacilioModule storeRoomMod){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS, "Service Order Planned Tools", "Service_Order_Planned_Tools", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField toolType = FieldFactory.getDefaultField("toolType","Tool Type","TOOL_TYPE",FieldType.LOOKUP, true);
        toolType.setRequired(true);
        toolType.setLookupModule(toolTypeMod);
        fields.add(toolType);

        LookupField storeRoom = FieldFactory.getDefaultField("storeRoom","Storeroom","STOREROOM",FieldType.LOOKUP);
        storeRoom.setLookupModule(storeRoomMod);
        fields.add(storeRoom);

        EnumField unitOfMeasure = FieldFactory.getDefaultField("unitOfMeasure","Unit of Measure","UNIT_OF_MEASURE", FieldType.ENUM);
        List<String> unitOfMeasures = Arrays.asList("Each", "kg", "Hour");

        List<EnumFieldValue<Integer>> unitOfMeasureValues = unitOfMeasures.stream().map(val -> {
            int index = unitOfMeasures.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        unitOfMeasure.setValues(unitOfMeasureValues);
        fields.add(unitOfMeasure);

        fields.add(FieldFactory.getDefaultField("duration","Duration","DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("rate","Rate","RATE",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("totalCost","Total Cost","TOTAL_COST",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        module.setFields(fields);

        return module;
    }
    private void createParentFields(FacilioModule serviceOrderMod,FacilioModule serviceTaskMod,FacilioModule serviceAppointmentMod ,FacilioModule serviceOrderPlannedToolsModule)throws Exception{
        ModuleBean bean = Constants.getModBean();

        LookupField parent = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER", FieldType.LOOKUP);
        parent.setRequired(true);
        parent.setLookupModule(serviceOrderMod);
        parent.setModule(serviceOrderPlannedToolsModule);
        bean.addField(parent);

        LookupField serviceTask = FieldFactory.getDefaultField("serviceTask","Service Task","SERVICE_TASK",FieldType.LOOKUP);
        serviceTask.setLookupModule(serviceTaskMod);
        serviceTask.setModule(serviceOrderPlannedToolsModule);
        bean.addField(serviceTask);

        LookupField serviceAppointment = FieldFactory.getDefaultField("serviceAppointment","Service Appointment","SERVICE_APPOINTMENT",FieldType.LOOKUP);
        serviceAppointment.setLookupModule(serviceAppointmentMod);
        serviceAppointment.setModule(serviceOrderPlannedToolsModule);
        bean.addField(serviceAppointment);
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedToolsModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS);

        FacilioForm plannedToolsForm = new FacilioForm();
        plannedToolsForm.setDisplayName("SERVICE ORDER PLANNED TOOLS");
        plannedToolsForm.setName("default_serviceOrderPlannedTools_web");
        plannedToolsForm.setModule(plannedToolsModule);
        plannedToolsForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        plannedToolsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> plannedToolsFormFields = new ArrayList<>();
        plannedToolsFormFields.add(new FormField("toolType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tool type", FormField.Required.REQUIRED,"toolTypes", 1, 1,true));
        plannedToolsFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.DECIMAL, "Quantity", FormField.Required.REQUIRED, 2, 1));
        plannedToolsFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", 3, 1));
        plannedToolsFormFields.add(new FormField("rate", FacilioField.FieldDisplayType.DECIMAL, "Rate", FormField.Required.OPTIONAL, 4, 1));
        plannedToolsFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.DURATION, "Duration", FormField.Required.OPTIONAL, 5, 1));
        plannedToolsForm.setFields(plannedToolsFormFields);

        FormSection section = new FormSection("Default", 1, plannedToolsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        plannedToolsForm.setSections(Collections.singletonList(section));
        plannedToolsForm.setIsSystemForm(true);
        plannedToolsForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(plannedToolsForm);
    }
}
