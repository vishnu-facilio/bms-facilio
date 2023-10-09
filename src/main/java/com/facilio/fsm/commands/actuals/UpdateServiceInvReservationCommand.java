package com.facilio.fsm.commands.actuals;

import com.facilio.bmsconsoleV3.enums.InventoryReservationStatus;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceInventoryReservationContext;
import com.facilio.fsm.context.ServiceOrderItemsContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import static com.facilio.bmsconsoleV3.util.V3InventoryUtil.updateReservedQuantity;

public class UpdateServiceInvReservationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ServiceOrderItemsContext> serviceOrderItems = (List<ServiceOrderItemsContext>) context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS);

        if(CollectionUtils.isNotEmpty(serviceOrderItems)){
            for(ServiceOrderItemsContext serviceOrderItem : serviceOrderItems){
                if(serviceOrderItem.getServiceInventoryReservation()!=null && serviceOrderItem.getServiceInventoryReservation().getId()>0){
                    Long reservationId = serviceOrderItem.getServiceInventoryReservation().getId();
                    ServiceInventoryReservationContext reservation = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION,reservationId,ServiceInventoryReservationContext.class);

                    Double soQuantity = serviceOrderItem.getQuantity();
                    if(  serviceOrderItem.getServiceOrder()!=null && reservation.getServiceOrder()!=null &&  serviceOrderItem.getServiceOrder().getId() != reservation.getServiceOrder().getId()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Plan created in one Service Order cannot be issued to another");
                    }
                    if(reservation.getReservationStatusEnum().equals(InventoryReservationStatus.ISSUED)){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Selected reserved item is fully issued");
                    }
                    if(soQuantity > reservation.getBalanceReservedQuantity()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity is greater than balance reserved quantity");
                    }
                    else{
                        Double balanceReservedQuantity = reservation.getBalanceReservedQuantity();
                        balanceReservedQuantity -= soQuantity;
                        Double issuedQuantity = reservation.getIssuedQuantity()!=null ? reservation.getIssuedQuantity(): 0;
                        issuedQuantity += soQuantity;

                        if(balanceReservedQuantity>0){
                            reservation.setReservationStatus(InventoryReservationStatus.PARTIALLY_ISSUED.getIndex());
                        }
                        else if(balanceReservedQuantity==0){
                            reservation.setReservationStatus(InventoryReservationStatus.ISSUED.getIndex());
                        }
                        reservation.setIssuedQuantity(issuedQuantity);
                        reservation.setBalanceReservedQuantity(balanceReservedQuantity);

                        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, reservation.getId(), FieldUtil.getAsJSON(reservation), null, null, null, null, null,null,null, null,null);

                    }
                    updateReservedQuantity(serviceOrderItem.getItem().getId(),serviceOrderItem.getQuantity());
                }
            }
        }
        return false;
    }
}
