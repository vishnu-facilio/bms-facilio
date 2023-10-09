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
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServiceInventoryReservationModule extends BaseModuleConfig {
    public ServiceInventoryReservationModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule plannedItems = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS);

        FacilioModule serviceInventoryReservationModule = constructServiceInventoryReservationModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceInventoryReservationModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        bean.addSubModule(plannedItems.getModuleId(), serviceInventoryReservationModule.getModuleId(),0);
        createPlannedItemField(serviceInventoryReservationModule);

        addReservationFieldInItemTransactions();
    }
    private void  addReservationFieldInItemTransactions() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTransactions = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);

        LookupField reservation = FieldFactory.getDefaultField("serviceInventoryReservation","Service Inventory Reservation","SERVICE_INV_RESERVATION",FieldType.LOOKUP);
        reservation.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION),"Service Inventory Reservation module doesn't exist."));
        reservation.setModule(itemTransactions);

        modBean.addField(reservation);
    }
    private FacilioModule constructServiceInventoryReservationModule() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, "Service Inventory Reservation", "Service_Inventory_Reservation", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        SystemEnumField reservationSource = FieldFactory.getDefaultField("reservationSource","Reservation Source","RESERVATION_SOURCE",FieldType.SYSTEM_ENUM);
        reservationSource.setEnumName("ReservationSource");
        fields.add(reservationSource);

        SystemEnumField reservationType = FieldFactory.getDefaultField("reservationType","Reservation Type","RESERVATION_TYPE",FieldType.SYSTEM_ENUM);
        reservationType.setEnumName("ReservationType");
        fields.add(reservationType);

        SystemEnumField reservationStatus = FieldFactory.getDefaultField("reservationStatus","Reservation Status","RESERVATION_STATUS",FieldType.SYSTEM_ENUM);
        reservationStatus.setEnumName("InventoryReservationStatus");
        fields.add(reservationStatus);


        LookupField serviceOrder = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER",FieldType.LOOKUP);
        serviceOrder.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER),"Service Order module doesn't exist."));
        fields.add(serviceOrder);

        LookupField storeRoom = FieldFactory.getDefaultField("storeRoom","Storeroom","STOREROOM",FieldType.LOOKUP);
        storeRoom.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.STORE_ROOM),"Storeroom module doesn't exist."));
        fields.add(storeRoom);

        LookupField inventoryRequest = FieldFactory.getDefaultField("inventoryRequest","Inventory Request","INVENTORY_REQUEST",FieldType.LOOKUP);
        inventoryRequest.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST),"Inventory Request module doesn't exist."));
        fields.add(inventoryRequest);

        LookupField requestedBy = FieldFactory.getDefaultField("requestedBy","Request By","REQUESTED_BY",FieldType.LOOKUP);
        requestedBy.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exist."));
        fields.add(requestedBy);

        LookupField itemType = FieldFactory.getDefaultField("itemType","Item Type","ITEM_TYPE",FieldType.LOOKUP);
        itemType.setMainField(true);
        itemType.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.ITEM_TYPES),"Item type module doesn't exist."));
        fields.add(itemType);

        fields.add(FieldFactory.getDefaultField("reservedQuantity","Reserved Quantity","RESERVED_QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("issuedQuantity","Issued Quantity","ISSUED_QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("balanceReservedQuantity","Balance Reserved Quantity","BALANCE_RESERVED_QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));

        LookupField inventoryRequestLineItem = FieldFactory.getDefaultField("inventoryRequestLineItem","Inventory Request LineItem","INV_REQ_LINE_ITEM_ID",FieldType.LOOKUP);
        inventoryRequestLineItem.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS),"Inventory Request LineItem module doesn't exist."));
        fields.add(inventoryRequestLineItem);

        module.setFields(fields);

        return module;
    }
    private void createPlannedItemField(FacilioModule serviceInventoryReservation)throws Exception{
        ModuleBean bean = Constants.getModBean();

        LookupField serviceOrderPlannedItem = FieldFactory.getDefaultField("serviceOrderPlannedItem","Service Order Planned Item","SO_PLANNED_ITEM_ID",FieldType.LOOKUP);
        serviceOrderPlannedItem.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS),"Service Order Planned Item module doesn't exist."));
        serviceOrderPlannedItem.setModule(serviceInventoryReservation);
        bean.addField(serviceOrderPlannedItem);
    }
}
