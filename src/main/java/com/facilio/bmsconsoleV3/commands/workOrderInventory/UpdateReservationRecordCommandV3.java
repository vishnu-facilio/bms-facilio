package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class UpdateReservationRecordCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3WorkorderItemContext> workOrderItems = (List<V3WorkorderItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

        if(CollectionUtils.isNotEmpty(workOrderItems)){
            for(V3WorkorderItemContext workOrderItem : workOrderItems){
                if(workOrderItem.getWorkOrderPlannedItem()!=null && workOrderItem.getWorkOrderPlannedItem().getId()>0){
                    Long workOrderPlannedItemId = workOrderItem.getWorkOrderPlannedItem().getId();
                    Double woQuantity = workOrderItem.getQuantity();
                    InventoryReservationContext reservation = getReservationRecord(workOrderPlannedItemId);
                    if(reservation.getReservationStatusEnum().equals(InventoryReservationContext.InventoryReservationStatus.ISSUED)){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Selected reserved item is fully issued");
                    }
                    if(woQuantity > reservation.getBalanceReservedQuantity()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity is greater than balance reserved quantity");
                    }
                    else{
                        Double balanceReservedQuantity = reservation.getBalanceReservedQuantity();
                        balanceReservedQuantity -= woQuantity;
                        Double issuedQuantity = reservation.getIssuedQuantity()!=null ? reservation.getIssuedQuantity(): 0;
                        issuedQuantity += woQuantity;

                        if(balanceReservedQuantity>0){
                            reservation.setReservationStatus(InventoryReservationContext.InventoryReservationStatus.PARTIALLY_ISSUED.getIndex());
                        }
                        else if(balanceReservedQuantity==0){
                            reservation.setReservationStatus(InventoryReservationContext.InventoryReservationStatus.ISSUED.getIndex());
                        }
                        reservation.setIssuedQuantity(issuedQuantity);
                        reservation.setBalanceReservedQuantity(balanceReservedQuantity);

                        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.INVENTORY_RESERVATION, reservation.getId(), FieldUtil.getAsJSON(reservation), null, null, null, null, null,null,null);

                    }
                }
            }
        }
        return false;
    }
    public InventoryReservationContext getReservationRecord(Long workOrderPlannedItemId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("WO_PLANNED_ITEM_ID", "workOrderPlannedItem", String.valueOf(workOrderPlannedItemId), NumberOperators.EQUALS));
        List<InventoryReservationContext> reservations = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.INVENTORY_RESERVATION, null, InventoryReservationContext.class, criteria, null);

        return reservations.get(0);
    }
}
