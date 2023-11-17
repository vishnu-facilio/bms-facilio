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
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServiceOrderCostModule extends BaseModuleConfig {
    public ServiceOrderCostModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_COST);
    }
    @Override
    public void addData() throws Exception {
        FacilioModule serviceOrderCostModule = constructServiceOrderCostModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderCostModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    private FacilioModule constructServiceOrderCostModule() throws Exception{
        ModuleBean bean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_COST, "Work Order Cost", "Service_Order_Cost", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField serviceOrder = FieldFactory.getDefaultField("serviceOrder","Work Order","SERVICE_ORDER", FieldType.LOOKUP);
        serviceOrder.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER),"Work Order module doesn't exist."));
        fields.add(serviceOrder);
        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("cost","Cost","COST",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        SystemEnumField inventoryCostType = FieldFactory.getDefaultField("inventoryCostType","Inventory Cost Type","INVENTORY_COST_TYPE",FieldType.SYSTEM_ENUM);
        inventoryCostType.setEnumName("InventoryCostType");
        fields.add(inventoryCostType);
        SystemEnumField inventorySource = FieldFactory.getDefaultField("inventorySource","Inventory Source","INVENTORY_SOURCE",FieldType.SYSTEM_ENUM);
        inventorySource.setEnumName("InventorySource");
        fields.add(inventorySource);
        module.setFields(fields);

        return module;
    }
}
