package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.enums.InventoryReservationStatus;
import com.facilio.bmsconsoleV3.enums.ReservationSource;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
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

import static com.facilio.bmsconsoleV3.util.V3InventoryUtil.rollUpReservedItem;
import static com.facilio.bmsconsoleV3.util.V3InventoryUtil.rollUpReservedTool;

public class UpdateInventoryRequestLineItemsForReservation extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestLineItemContext> inventoryRequestLineItems = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(inventoryRequestLineItems) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("reserve") && (boolean) bodyParams.get("reserve")) {
            V3WorkOrderContext workorder = new V3WorkOrderContext();
            if(bodyParams.containsKey("workorderId")){
                Long workorderId = (Long) bodyParams.get("workorderId");
                workorder.setId(workorderId);
            }
            for(V3InventoryRequestLineItemContext inventoryRequestLineItem : inventoryRequestLineItems){
                InventoryReservationContext inventoryReservation = createInventoryReservationRecord(inventoryRequestLineItem, workorder);
                if(inventoryRequestLineItem.getInventoryType() == InventoryType.ITEM.getValue()){
                    rollUpReservedItem(inventoryRequestLineItem.getItemType(), inventoryRequestLineItem.getStoreRoom(), inventoryRequestLineItem.getReservationType(), inventoryRequestLineItem.getQuantity(), inventoryReservation);
                }
                if(inventoryRequestLineItem.getInventoryType() == InventoryType.TOOL.getValue()){
                    rollUpReservedTool(inventoryRequestLineItem.getToolType(), inventoryRequestLineItem.getStoreRoom(), inventoryRequestLineItem.getReservationType(), inventoryRequestLineItem.getQuantity(), inventoryReservation);
                }
            }
        }
        return false;
    }
    private InventoryReservationContext createInventoryReservationRecord(V3InventoryRequestLineItemContext inventoryRequestLineItem, V3WorkOrderContext workorder) throws Exception {
        InventoryReservationContext reservation = new InventoryReservationContext();

        reservation.setReservationSource(ReservationSource.INVENTORY_REQUEST.getIndex());
        reservation.setReservationType(inventoryRequestLineItem.getReservationType());
        reservation.setReservationStatus(InventoryReservationStatus.NOT_ISSUED.getIndex());
        reservation.setStoreRoom(inventoryRequestLineItem.getStoreRoom());
        reservation.setWorkOrder(workorder);
        if(inventoryRequestLineItem.getInventoryType() == InventoryType.ITEM.getValue()){
            reservation.setItemType(inventoryRequestLineItem.getItemType());
        }
        if(inventoryRequestLineItem.getInventoryType() == InventoryType.TOOL.getValue()){
            reservation.setToolType(inventoryRequestLineItem.getToolType());
        }
        reservation.setReservedQuantity(inventoryRequestLineItem.getQuantity());
        reservation.setBalanceReservedQuantity(inventoryRequestLineItem.getQuantity());
        reservation.setInventoryRequestLineItem(inventoryRequestLineItem);
        reservation.setInventoryRequest(inventoryRequestLineItem.getInventoryRequestId());

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_RESERVATION);

        FacilioContext inventoryReservationContext = V3Util.createRecord(module, FacilioUtil.getAsMap(FieldUtil.getAsJSON(reservation)),null,null);
        Map<String, List> recordMap = (Map<String, List>) inventoryReservationContext.get(Constants.RECORD_MAP);

        return (InventoryReservationContext) recordMap.get(FacilioConstants.ContextNames.INVENTORY_RESERVATION).get(0);
    }
}
