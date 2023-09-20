package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServicePlanServicesModule extends BaseModuleConfig {
    public ServicePlanServicesModule(){
        setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_SERVICES);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePlanModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
        FacilioModule serviceTaskTemplateModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE);

        FacilioModule servicePlanServicesModule = constructServicePlanServicesModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(servicePlanServicesModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        modBean.addSubModule(servicePlanModule.getModuleId(), servicePlanServicesModule.getModuleId(),2);
        modBean.addSubModule(serviceTaskTemplateModule.getModuleId(), servicePlanServicesModule.getModuleId(),2);

        addParentFields(servicePlanModule,serviceTaskTemplateModule,servicePlanServicesModule);
    }
    private FacilioModule constructServicePlanServicesModule()throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_SERVICES, "Service Plan Services", "Service_Plan_Services", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField service = FieldFactory.getDefaultField("service","Service","SERVICE", FieldType.LOOKUP);
        service.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.SERVICE),"Service module doesn't exist."));
        fields.add(service);
        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("duration","Duration","DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        module.setFields(fields);
        return module;
    }
    private void addParentFields(FacilioModule servicePlanModule,FacilioModule serviceTaskTemplateModule,FacilioModule servicePlanServicesModule)throws Exception{
        ModuleBean modBean = Constants.getModBean();

        LookupField serviceTaskTemplate = FieldFactory.getDefaultField("serviceTaskTemplate","Service Task Template","SERVICE_TASK_TEMPLATE", FieldType.LOOKUP);
        serviceTaskTemplate.setLookupModule(serviceTaskTemplateModule);
        serviceTaskTemplate.setModule(servicePlanServicesModule);
        modBean.addField(serviceTaskTemplate);

        LookupField servicePlan = FieldFactory.getDefaultField("servicePlan","Job Plan","SERVICE_PLAN", FieldType.LOOKUP);
        servicePlan.setRequired(true);
        servicePlan.setLookupModule(servicePlanModule);
        servicePlan.setModule(servicePlanServicesModule);
        modBean.addField(servicePlan);
    }
}
