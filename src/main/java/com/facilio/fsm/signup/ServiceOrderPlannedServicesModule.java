package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
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

            FacilioModule serviceOrderPlannedServicesModule = constructServiceOrderPlannedServicesModule(serviceOrder,serviceTask,service);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderPlannedServicesModule));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, serviceOrder.getName());
            addModuleChain.execute();
            bean.addSubModule(serviceOrder.getModuleId(), serviceOrderPlannedServicesModule.getModuleId());
        }

    }
    private FacilioModule constructServiceOrderPlannedServicesModule(FacilioModule serviceOrderMod,FacilioModule serviceTaskMod,FacilioModule serviceMod){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES, "Service Order Planned Services", "Service_Order_Planned_Services", FacilioModule.ModuleType.SUB_ENTITY);

        List<FacilioField> fields = new ArrayList<>();

        LookupField parent = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER", FieldType.LOOKUP);
        parent.setRequired(true);
        parent.setLookupModule(serviceOrderMod);
        fields.add(parent);

        LookupField serviceTask = FieldFactory.getDefaultField("serviceTask","Service Task","SERVICE_TASK",FieldType.LOOKUP);
        serviceTask.setLookupModule(serviceTaskMod);
        fields.add(serviceTask);

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
}
