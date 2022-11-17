package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class SetInventoryRequestBeforeSaveCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestContext> inventoryRequests = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);

        if (CollectionUtils.isNotEmpty(inventoryRequests)) {
            for (V3InventoryRequestContext inventoryRequest : inventoryRequests) {
                // setting by default issue on inventory request transaction type
                inventoryRequest.setTransactionType(V3InventoryRequestContext.TransactionType.ISSUE.getIndex());
                if(inventoryRequest.getInventoryRequestReservationStatus() == null) {
                    inventoryRequest.setInventoryRequestReservationStatus(V3InventoryRequestContext.InventoryRequestReservationStatus.PENDING.getIndex());
                }
                if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("setIRFullyReserved") && (boolean) bodyParams.get("setIRFullyReserved")){
                    inventoryRequest.setInventoryRequestReservationStatus(V3InventoryRequestContext.InventoryRequestReservationStatus.FULLY_RESERVED.getIndex());
                }
                if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("setIRPartiallyReserved") && (boolean) bodyParams.get("setIRPartiallyReserved")){
                    inventoryRequest.setInventoryRequestReservationStatus(V3InventoryRequestContext.InventoryRequestReservationStatus.PARTIALLY_RESERVED.getIndex());
                }
            }
        }
        return false;
    }
}
