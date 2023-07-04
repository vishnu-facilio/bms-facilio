package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdateReservationRecordCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3WorkorderItemContext> workOrderItems = (List<V3WorkorderItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

        if(CollectionUtils.isNotEmpty(workOrderItems)){
            for(V3WorkorderItemContext workOrderItem : workOrderItems){
                if(workOrderItem.getInventoryReservation()!=null && workOrderItem.getInventoryReservation().getId()>0){
                    Long reservationId = workOrderItem.getInventoryReservation().getId();
                    InventoryReservationContext reservation = V3RecordAPI.getRecord(FacilioConstants.ContextNames.INVENTORY_RESERVATION,reservationId,InventoryReservationContext.class);

                    Double woQuantity = workOrderItem.getQuantity();
                   if(  workOrderItem.getWorkorder()!=null && reservation.getWorkOrder()!=null &&  workOrderItem.getWorkorder().getId() != reservation.getWorkOrder().getId()){
                       throw new RESTException(ErrorCode.VALIDATION_ERROR, "Plan created in one Work Order cannot be issued to another");
                   }
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

                        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.INVENTORY_RESERVATION, reservation.getId(), FieldUtil.getAsJSON(reservation), null, null, null, null, null,null,null, null,null);

                    }
                }
            }
        }
        return false;
    }
}
