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

public class ServiceOrderPlannedServicesModule extends BaseModuleConfig {
    public ServiceOrderPlannedServicesModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        FacilioModule serviceOrder = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule serviceTask = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule service = bean.getModule(FacilioConstants.ContextNames.SERVICE);

        if(serviceOrder!=null && serviceOrder.getModuleId()>0 && service!=null && service.getModuleId()>0){

            FacilioModule serviceOrderPlannedServicesModule = constructServiceOrderPlannedServicesModule(service);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderPlannedServicesModule));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
            bean.addSubModule(serviceOrder.getModuleId(), serviceOrderPlannedServicesModule.getModuleId(),0);
            bean.addSubModule(serviceTask.getModuleId(), serviceOrderPlannedServicesModule.getModuleId(),0);
            createParentFields(serviceOrder,serviceTask,serviceOrderPlannedServicesModule);
        }

    }
    private FacilioModule constructServiceOrderPlannedServicesModule(FacilioModule serviceMod){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES, "Service Order Planned Services", "Service_Order_Planned_Services", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField service = FieldFactory.getDefaultField("service","Service","SERVICE",FieldType.LOOKUP, true);
        service.setRequired(true);
        service.setLookupModule(serviceMod);
        fields.add(service);

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
        fields.add(FieldFactory.getDefaultField("unitPrice","Unit Price","UNIT_PRICE",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("totalCost","Total Cost","TOTAL_COST",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        module.setFields(fields);

        return module;
    }
    private void createParentFields(FacilioModule serviceOrderMod,FacilioModule serviceTaskMod,FacilioModule serviceOrderPlannedServicesModule)throws Exception{
        ModuleBean bean = Constants.getModBean();

        LookupField parent = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER", FieldType.LOOKUP);
        parent.setRequired(true);
        parent.setLookupModule(serviceOrderMod);
        parent.setModule(serviceOrderPlannedServicesModule);
        bean.addField(parent);

        LookupField serviceTask = FieldFactory.getDefaultField("serviceTask","Service Task","SERVICE_TASK",FieldType.LOOKUP);
        serviceTask.setLookupModule(serviceTaskMod);
        serviceTask.setModule(serviceOrderPlannedServicesModule);
        bean.addField(serviceTask);
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedServicesModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES);

        FacilioForm plannedServicesForm = new FacilioForm();
        plannedServicesForm.setDisplayName("SERVICE ORDER PLANNED SERVICES");
        plannedServicesForm.setName("default_serviceOrderPlannedServices_web");
        plannedServicesForm.setModule(plannedServicesModule);
        plannedServicesForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        plannedServicesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> plannedServicesFormFields = new ArrayList<>();

        plannedServicesFormFields.add(new FormField("service", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Service", FormField.Required.REQUIRED,"service", 1, 1,true));
        plannedServicesFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.DECIMAL, "Quantity", FormField.Required.REQUIRED, 2, 1));
        plannedServicesFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.DECIMAL, "Unit Price", FormField.Required.OPTIONAL, 3, 1));
        plannedServicesFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.DURATION, "Duration", FormField.Required.OPTIONAL, 4, 1));

        plannedServicesForm.setFields(plannedServicesFormFields);

        FormSection section = new FormSection("Default", 1, plannedServicesFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        plannedServicesForm.setSections(Collections.singletonList(section));
        plannedServicesForm.setIsSystemForm(true);
        plannedServicesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(plannedServicesForm);

    }
}
