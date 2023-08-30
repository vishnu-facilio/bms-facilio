package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServiceOrderItemsModule extends BaseModuleConfig {
    public ServiceOrderItemsModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        FacilioModule serviceOrder = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule serviceTask = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule itemType = bean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
        FacilioModule item = bean.getModule(FacilioConstants.ContextNames.ITEM);
        FacilioModule storeRoom = bean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
        FacilioModule asset = bean.getModule(FacilioConstants.ContextNames.ASSET);

        if(serviceOrder!=null && serviceOrder.getModuleId()>0 && serviceTask!=null && serviceTask.getModuleId()>0 && itemType!=null && itemType.getModuleId()>0 && storeRoom!=null && storeRoom.getModuleId()>0 && item!=null && item.getModuleId()>0 && asset!=null && asset.getModuleId()>0){

            FacilioModule serviceOrderItemsModule = constructServiceOrderItemsModule(itemType,storeRoom,item,asset);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderItemsModule));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
            bean.addSubModule(serviceOrder.getModuleId(), serviceOrderItemsModule.getModuleId(),0);
            bean.addSubModule(serviceTask.getModuleId(), serviceOrderItemsModule.getModuleId(),0);
            createParentFields(serviceOrder,serviceTask,serviceOrderItemsModule);
        }
        addServiceOrderItemFieldInItemTransactions();
    }
    private void addServiceOrderItemFieldInItemTransactions() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTransactions = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);

        LookupField serviceOrderItem = FieldFactory.getDefaultField("serviceOrderItem","Service Order Items","SERVICE_ORDER_ITEM",FieldType.LOOKUP);
        serviceOrderItem.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS),"Service Order Items module doesn't exist."));
        serviceOrderItem.setModule(itemTransactions);

        modBean.addField(serviceOrderItem);
    }
    private FacilioModule constructServiceOrderItemsModule(FacilioModule itemTypeMod, FacilioModule storeRoomMod,FacilioModule itemMod, FacilioModule assetMod){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, "Service Order Items", "Service_Order_Items", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField itemType = FieldFactory.getDefaultField("itemType","Item Type","ITEM_TYPE",FieldType.LOOKUP);
        itemType.setLookupModule(itemTypeMod);
        fields.add(itemType);

        LookupField item = FieldFactory.getDefaultField("item","Item","ITEM",FieldType.LOOKUP,true);
        item.setRequired(true);
        item.setLookupModule(itemMod);
        fields.add(item);

        LookupField storeRoom = FieldFactory.getDefaultField("storeRoom","Storeroom","STOREROOM",FieldType.LOOKUP);
        storeRoom.setLookupModule(storeRoomMod);
        fields.add(storeRoom);

        LookupField rotatingAsset = FieldFactory.getDefaultField("rotatingAsset","Rotating Asset","ROTATING_ASSET",FieldType.LOOKUP);
        rotatingAsset.setLookupModule(assetMod);
        fields.add(rotatingAsset);

        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("unitPrice","Unit Price","UNIT_PRICE",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("totalCost","Total Cost","TOTAL_COST",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));

        module.setFields(fields);

        return module;
    }
    private void createParentFields(FacilioModule serviceOrderMod,FacilioModule serviceTaskMod,FacilioModule serviceOrderItemsModule)throws Exception{
        ModuleBean bean = Constants.getModBean();

        LookupField parent = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER", FieldType.LOOKUP);
        parent.setRequired(true);
        parent.setLookupModule(serviceOrderMod);
        parent.setModule(serviceOrderItemsModule);
        bean.addField(parent);

        LookupField serviceTask = FieldFactory.getDefaultField("serviceTask","Service Task","SERVICE_TASK",FieldType.LOOKUP);
        serviceTask.setLookupModule(serviceTaskMod);
        serviceTask.setModule(serviceOrderItemsModule);
        bean.addField(serviceTask);
    }
}
