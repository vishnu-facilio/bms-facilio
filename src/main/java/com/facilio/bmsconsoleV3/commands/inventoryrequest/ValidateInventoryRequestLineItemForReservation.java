package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsoleV3.util.V3InventoryUtil.*;

public class ValidateInventoryRequestLineItemForReservation extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestLineItemContext> inventoryRequestLineItems = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if (CollectionUtils.isNotEmpty(inventoryRequestLineItems)) {
            for(V3InventoryRequestLineItemContext inventoryRequestLineItem : inventoryRequestLineItems) {
                if(inventoryRequestLineItem.getIsReserved()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot Update Reserved Item");
                }
                if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("reserve") && (boolean) bodyParams.get("reserve")) {
                    ReservationType reservationType = ReservationType.valueOf(inventoryRequestLineItem.getReservationType());
                    validateQuantityAndReservationType(inventoryRequestLineItem.getQuantity(), reservationType);
                    if(inventoryRequestLineItem.getInventoryType() == InventoryType.ITEM.getValue()){
                        validateReservationForItem(inventoryRequestLineItem.getQuantity(),reservationType , inventoryRequestLineItem.getItemType(), inventoryRequestLineItem.getStoreRoom());
                    }
                    if(inventoryRequestLineItem.getInventoryType() == InventoryType.TOOL.getValue()){
                        validateReservationForTool(inventoryRequestLineItem.getQuantity(), reservationType, inventoryRequestLineItem.getToolType(), inventoryRequestLineItem.getStoreRoom());
                    }
                }
            }
        }
        return false;
    }
}
