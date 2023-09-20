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

public class ServicePlanItemsModule extends BaseModuleConfig {
    public ServicePlanItemsModule(){
        setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ITEMS);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePlanModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
        FacilioModule serviceTaskTemplateModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE);

        FacilioModule servicePlanItemsModule = constructServicePlanItemsModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(servicePlanItemsModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        modBean.addSubModule(servicePlanModule.getModuleId(), servicePlanItemsModule.getModuleId(),2);
        modBean.addSubModule(serviceTaskTemplateModule.getModuleId(), servicePlanItemsModule.getModuleId(),2);

        addParentFields(servicePlanModule,serviceTaskTemplateModule,servicePlanItemsModule);
    }
    private FacilioModule constructServicePlanItemsModule()throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ITEMS, "Service Plan Items", "Service_Plan_Items", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField itemType = FieldFactory.getDefaultField("itemType","Item Type","ITEM_TYPE",FieldType.LOOKUP);
        itemType.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES),"Item Type module doesn't exist."));
        fields.add(itemType);
        LookupField storeRoom = FieldFactory.getDefaultField("storeRoom","Storeroom","STOREROOM",FieldType.LOOKUP);
        storeRoom.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM),"Storeroom module doesn't exist."));
        fields.add(storeRoom);
        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        module.setFields(fields);
        return module;
    }
    private void addParentFields(FacilioModule servicePlanModule,FacilioModule serviceTaskTemplateModule,FacilioModule servicePlanItemsModule)throws Exception{
        ModuleBean modBean = Constants.getModBean();

        LookupField serviceTaskTemplate = FieldFactory.getDefaultField("serviceTaskTemplate","Service Task Template","SERVICE_TASK_TEMPLATE", FieldType.LOOKUP);
        serviceTaskTemplate.setLookupModule(serviceTaskTemplateModule);
        serviceTaskTemplate.setModule(servicePlanItemsModule);
        modBean.addField(serviceTaskTemplate);

        LookupField servicePlan = FieldFactory.getDefaultField("servicePlan","Job Plan","SERVICE_PLAN", FieldType.LOOKUP);
        servicePlan.setRequired(true);
        servicePlan.setLookupModule(servicePlanModule);
        servicePlan.setModule(servicePlanItemsModule);
        modBean.addField(servicePlan);
    }
}
