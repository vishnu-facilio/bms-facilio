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
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServiceOrderServicesModule  extends BaseModuleConfig {
    public ServiceOrderServicesModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        FacilioModule serviceOrder = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule serviceTask = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule serviceAppointment = bean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioModule service = bean.getModule(FacilioConstants.ContextNames.SERVICE);

        if(serviceOrder!=null && serviceOrder.getModuleId()>0 && serviceAppointment!=null && serviceAppointment.getModuleId()>0 && serviceTask!=null && serviceTask.getModuleId()>0 && service!=null && service.getModuleId()>0){

            FacilioModule serviceOrderServicesModule = constructServiceOrderServicesModule(service);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderServicesModule));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
            bean.addSubModule(serviceOrder.getModuleId(), serviceOrderServicesModule.getModuleId(),0);
            bean.addSubModule(serviceTask.getModuleId(), serviceOrderServicesModule.getModuleId(),0);
            bean.addSubModule(serviceAppointment.getModuleId(), serviceOrderServicesModule.getModuleId(),0);

            createParentFields(serviceOrder,serviceTask,serviceAppointment,serviceOrderServicesModule);
        }

    }
    private FacilioModule constructServiceOrderServicesModule(FacilioModule serviceMod){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES, "Work Order Services", "Service_Order_Services", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField service = FieldFactory.getDefaultField("service","Service","SERVICE",FieldType.LOOKUP,true);
        service.setLookupModule(serviceMod);
        fields.add(service);

        fields.add(FieldFactory.getDefaultField("duration","Duration","DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("unitPrice","Unit Price","UNIT_PRICE",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("totalCost","Total Cost","TOTAL_COST",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("startTime","Start Time","START_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));
        fields.add(FieldFactory.getDefaultField("endTime","End Time","END_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));

        module.setFields(fields);

        return module;
    }
    private void createParentFields(FacilioModule serviceOrderMod,FacilioModule serviceTaskMod,FacilioModule serviceAppointmentMod,FacilioModule serviceOrderServicesModule)throws Exception{
        ModuleBean bean = Constants.getModBean();

        LookupField parent = FieldFactory.getDefaultField("serviceOrder","Work Order","SERVICE_ORDER", FieldType.LOOKUP);
        parent.setRequired(true);
        parent.setLookupModule(serviceOrderMod);
        parent.setModule(serviceOrderServicesModule);
        bean.addField(parent);

        LookupField serviceTask = FieldFactory.getDefaultField("serviceTask","Task","SERVICE_TASK",FieldType.LOOKUP);
        serviceTask.setLookupModule(serviceTaskMod);
        serviceTask.setModule(serviceOrderServicesModule);
        bean.addField(serviceTask);

        LookupField serviceAppointment = FieldFactory.getDefaultField("serviceAppointment","Appointment","SERVICE_APPOINTMENT",FieldType.LOOKUP);
        serviceAppointment.setLookupModule(serviceAppointmentMod);
        serviceAppointment.setModule(serviceOrderServicesModule);
        bean.addField(serviceAppointment);
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderServiceModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES);

        FacilioForm serviceOrderServiceModuleForm = new FacilioForm();
        serviceOrderServiceModuleForm.setDisplayName("New Work Order Service");
        serviceOrderServiceModuleForm.setName("default_serviceOrderService_web");
        serviceOrderServiceModuleForm.setModule(serviceOrderServiceModule);
        serviceOrderServiceModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        serviceOrderServiceModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        int seqNum = 0;
        List<FormField> serviceOrderServiceModuleFormFields = new ArrayList<>();
        serviceOrderServiceModuleFormFields.add(new FormField("service", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Service", FormField.Required.REQUIRED, "Service", ++seqNum, 1,true));
        serviceOrderServiceModuleFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.DECIMAL, "Quantity", FormField.Required.REQUIRED, ++seqNum, 1));
        serviceOrderServiceModuleFormFields.add(new FormField("startTime", FacilioField.FieldDisplayType.DATETIME,"Start Time", FormField.Required.OPTIONAL, ++seqNum,1));
        serviceOrderServiceModuleFormFields.add(new FormField("endTime", FacilioField.FieldDisplayType.DATETIME,"End Time", FormField.Required.OPTIONAL, ++seqNum,1));
        serviceOrderServiceModuleFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.DURATION,"Duration", FormField.Required.OPTIONAL,"duration", ++seqNum,1));
        serviceOrderServiceModuleFormFields.add(new FormField("serviceTask", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Task", FormField.Required.OPTIONAL,"serviceTask", ++seqNum, 1,false));

        FormSection serviceOrderServiceModuleFormSection = new FormSection("Default", 1, serviceOrderServiceModuleFormFields, false);
        serviceOrderServiceModuleFormSection.setSectionType(FormSection.SectionType.FIELDS);
        serviceOrderServiceModuleForm.setSections(Collections.singletonList(serviceOrderServiceModuleFormSection));
        serviceOrderServiceModuleForm.setIsSystemForm(true);
        serviceOrderServiceModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(serviceOrderServiceModuleForm);

    }
}
