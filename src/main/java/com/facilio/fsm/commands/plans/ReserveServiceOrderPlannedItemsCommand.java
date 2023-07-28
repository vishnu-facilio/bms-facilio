package com.facilio.fsm.commands.plans;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.enums.InventoryReservationStatus;
import com.facilio.bmsconsoleV3.enums.ReservationSource;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceInventoryReservationContext;
import com.facilio.fsm.context.ServiceOrderPlannedItemsContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsoleV3.util.V3InventoryUtil.rollUpReservedItemForSoPlans;

public class ReserveServiceOrderPlannedItemsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(serviceOrderPlannedItems) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("reserve") && (boolean) bodyParams.get("reserve")){
            for(ServiceOrderPlannedItemsContext serviceOrderPlannedItem : serviceOrderPlannedItems){
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION);
                ServiceInventoryReservationContext reservation = getReservationRecord(serviceOrderPlannedItem);
                V3Util.createRecord(module, FacilioUtil.getAsMap(FieldUtil.getAsJSON(reservation)),null,null);
                rollUpReservedItemForSoPlans(serviceOrderPlannedItem.getItemType(), serviceOrderPlannedItem.getStoreRoom(), serviceOrderPlannedItem.getReservationType(), serviceOrderPlannedItem.getQuantity());
            }
        }
        return false;
    }
    public ServiceInventoryReservationContext getReservationRecord(ServiceOrderPlannedItemsContext serviceOrderPlannedItem){
        ServiceInventoryReservationContext reservation = new ServiceInventoryReservationContext();
        reservation.setReservationSource(ReservationSource.SO_PLANS.getIndex());
        reservation.setReservationType(serviceOrderPlannedItem.getReservationType());
        reservation.setReservationStatus(InventoryReservationStatus.NOT_ISSUED.getIndex());
        reservation.setStoreRoom(serviceOrderPlannedItem.getStoreRoom());
        reservation.setServiceOrder(serviceOrderPlannedItem.getServiceOrder());
        reservation.setItemType(serviceOrderPlannedItem.getItemType());
        reservation.setReservedQuantity(serviceOrderPlannedItem.getQuantity());
        reservation.setBalanceReservedQuantity(serviceOrderPlannedItem.getQuantity());
        reservation.setServiceOrderPlannedItem(serviceOrderPlannedItem);

        return reservation;
    }
}
