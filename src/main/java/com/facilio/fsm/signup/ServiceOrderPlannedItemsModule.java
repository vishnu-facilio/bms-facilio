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

public class ServiceOrderPlannedItemsModule extends BaseModuleConfig {
    public ServiceOrderPlannedItemsModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        FacilioModule serviceOrder = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule serviceTask = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule itemType = bean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
        FacilioModule storeRoom = bean.getModule(FacilioConstants.ContextNames.STORE_ROOM);

        if(serviceOrder!=null && serviceOrder.getModuleId()>0 && itemType!=null && itemType.getModuleId()>0 && storeRoom!=null && storeRoom.getModuleId()>0){

            FacilioModule serviceOrderPlannedItemsModule = constructServiceOrderPlannedItemsModule(serviceOrder,serviceTask,itemType,storeRoom);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderPlannedItemsModule));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.getContext().put(FacilioConstants.Module.USE_PEOPLE_LOOKUP, true);
            addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, serviceOrder.getName());
            addModuleChain.execute();
            bean.addSubModule(serviceOrder.getModuleId(), serviceOrderPlannedItemsModule.getModuleId());
        }

    }
    private FacilioModule constructServiceOrderPlannedItemsModule(FacilioModule serviceOrderMod,FacilioModule serviceTaskMod,FacilioModule itemTypeMod, FacilioModule storeRoomMod){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS, "Service Order Planned Items", "Service_Order_Planned_Items", FacilioModule.ModuleType.SUB_ENTITY);

        List<FacilioField> fields = new ArrayList<>();

        LookupField parent = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER",FieldType.LOOKUP);
        parent.setRequired(true);
        parent.setLookupModule(serviceOrderMod);
        fields.add(parent);

        LookupField serviceTask = FieldFactory.getDefaultField("serviceTask","Service Task","SERVICE_TASK",FieldType.LOOKUP);
        serviceTask.setLookupModule(serviceTaskMod);
        fields.add(serviceTask);

        LookupField itemType = FieldFactory.getDefaultField("itemType","Item Type","ITEM_TYPE",FieldType.LOOKUP, true);
        itemType.setRequired(true);
        itemType.setLookupModule(itemTypeMod);
        fields.add(itemType);

        LookupField storeRoom = FieldFactory.getDefaultField("storeRoom","Storeroom","STOREROOM",FieldType.LOOKUP);
        storeRoom.setLookupModule(storeRoomMod);
        fields.add(storeRoom);

        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("unitPrice","Unit Price","UNIT_PRICE",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("totalCost","Total Cost","TOTAL_COST",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("isReserved","Is Reserved","IS_RESERVED",FieldType.BOOLEAN));

        SystemEnumField reservationType = FieldFactory.getDefaultField("reservationType","Reservation Type","RESERVATION_TYPE",FieldType.SYSTEM_ENUM);
        reservationType.setEnumName("ReservationType");
        fields.add(reservationType);
        module.setFields(fields);

        return module;
    }
}
