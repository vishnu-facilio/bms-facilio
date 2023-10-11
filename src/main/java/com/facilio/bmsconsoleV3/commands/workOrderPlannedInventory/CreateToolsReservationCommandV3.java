package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
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

import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsoleV3.util.V3InventoryUtil.rollUpReservedTool;

public class CreateToolsReservationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WorkOrderPlannedToolsContext> workOrderPlannedTools = (List<WorkOrderPlannedToolsContext>) context.get(FacilioConstants.ContextNames.WO_PLANNED_TOOLS);

        if(CollectionUtils.isNotEmpty(workOrderPlannedTools)){
            for(WorkOrderPlannedToolsContext workOrderPlannedTool : workOrderPlannedTools){
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_RESERVATION);
                InventoryReservationContext reservation = getReservationRecord(workOrderPlannedTool);
                FacilioContext inventoryReservationContext = V3Util.createRecord(module, FacilioUtil.getAsMap(FieldUtil.getAsJSON(reservation)),null,null);
                Map<String, List> recordMap = (Map<String, List>) inventoryReservationContext.get(Constants.RECORD_MAP);

                InventoryReservationContext inventoryReservation = (InventoryReservationContext) recordMap.get(FacilioConstants.ContextNames.INVENTORY_RESERVATION).get(0);

                rollUpReservedTool(workOrderPlannedTool.getToolType(), workOrderPlannedTool.getStoreRoom(), workOrderPlannedTool.getReservationType(), workOrderPlannedTool.getQuantity(), inventoryReservation);
            }
        }
        return false;
    }
    public InventoryReservationContext getReservationRecord(WorkOrderPlannedToolsContext workOrderPlannedTool){
        InventoryReservationContext reservation = new InventoryReservationContext();
        reservation.setReservationSource(ReservationSource.WO_PLANS.getIndex());
        reservation.setReservationType(workOrderPlannedTool.getReservationType());
        reservation.setReservationStatus(InventoryReservationStatus.NOT_ISSUED.getIndex());
        reservation.setStoreRoom(workOrderPlannedTool.getStoreRoom());
        reservation.setWorkOrder(workOrderPlannedTool.getWorkOrder());
        reservation.setToolType(workOrderPlannedTool.getToolType());
        reservation.setReservedQuantity(workOrderPlannedTool.getQuantity());
        reservation.setBalanceReservedQuantity(workOrderPlannedTool.getQuantity());
        reservation.setWorkOrderPlannedTool(workOrderPlannedTool);

        return reservation;
    }
}
