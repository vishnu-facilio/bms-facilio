package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
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

        if(serviceOrder!=null && serviceOrder.getModuleId()>0 && itemType!=null && itemType.getModuleId()>0 && storeRoom!=null && storeRoom.getModuleId()>0 && serviceTask!=null && serviceTask.getModuleId()>0){

            FacilioModule serviceOrderPlannedItemsModule = constructServiceOrderPlannedItemsModule(itemType,storeRoom);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderPlannedItemsModule));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
            bean.addSubModule(serviceOrder.getModuleId(), serviceOrderPlannedItemsModule.getModuleId(),0);
            bean.addSubModule(serviceTask.getModuleId(), serviceOrderPlannedItemsModule.getModuleId(),0);
            createParentFields(serviceOrder,serviceTask,serviceOrderPlannedItemsModule);
        }

    }
    private FacilioModule constructServiceOrderPlannedItemsModule(FacilioModule itemTypeMod, FacilioModule storeRoomMod){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS, "Service Order Planned Items", "Service_Order_Planned_Items", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

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

        EnumField unitOfMeasure = FieldFactory.getDefaultField("unitOfMeasure","Unit of Measure","UNIT_OF_MEASURE", FieldType.ENUM);
        List<String> unitOfMeasures = Arrays.asList("Each", "kg", "Hour");

        List<EnumFieldValue<Integer>> unitOfMeasureValues = unitOfMeasures.stream().map(val -> {
            int index = unitOfMeasures.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        unitOfMeasure.setValues(unitOfMeasureValues);
        fields.add(unitOfMeasure);

        SystemEnumField reservationType = FieldFactory.getDefaultField("reservationType","Reservation Type","RESERVATION_TYPE",FieldType.SYSTEM_ENUM);
        reservationType.setEnumName("ReservationType");
        fields.add(reservationType);
        module.setFields(fields);

        return module;
    }
    private void createParentFields(FacilioModule serviceOrderMod,FacilioModule serviceTaskMod,FacilioModule serviceOrderPlannedItemsModule)throws Exception{
        ModuleBean bean = Constants.getModBean();

        LookupField parent = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER", FieldType.LOOKUP);
        parent.setRequired(true);
        parent.setLookupModule(serviceOrderMod);
        parent.setModule(serviceOrderPlannedItemsModule);
        bean.addField(parent);

        LookupField serviceTask = FieldFactory.getDefaultField("serviceTask","Service Task","SERVICE_TASK",FieldType.LOOKUP);
        serviceTask.setLookupModule(serviceTaskMod);
        serviceTask.setModule(serviceOrderPlannedItemsModule);
        bean.addField(serviceTask);
    }
}
