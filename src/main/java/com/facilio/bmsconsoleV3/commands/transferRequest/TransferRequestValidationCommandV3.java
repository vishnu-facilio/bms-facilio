package com.facilio.bmsconsoleV3.commands.transferRequest;

import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class TransferRequestValidationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequestContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(transferRequestContexts)) {
            for (V3TransferRequestContext transferRequestContext : transferRequestContexts) {
                // Storeroom Validation
                if (transferRequestContext.getTransferToStore().getId() == transferRequestContext.getTransferFromStore().getId()) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storerooms cannot be same");
                }
                // LineItems Validation
                if(transferRequestContext.getId()<= 0 && CollectionUtils.isEmpty(transferRequestContext.getTransferrequestlineitems())){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Line items cannot be empty");
                }
                //rotating item validation
                if(CollectionUtils.isNotEmpty(transferRequestContext.getTransferrequestlineitems())){
                    List<V3TransferRequestLineItemContext> lineItems = transferRequestContext.getTransferrequestlineitems();
                    for (V3TransferRequestLineItemContext lineItem : lineItems) {
                        if (lineItem.getInventoryType().equals(V3TransferRequestLineItemContext.InventoryType.TOOL.getIndex())) {
                            Long id = lineItem.getToolType().getId();
                            if (isRotating(id, 2)) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating tools cannot be transferred");
                            }
                        }
                        if (lineItem.getInventoryType().equals(V3TransferRequestLineItemContext.InventoryType.ITEM.getIndex())) {
                            Long id = lineItem.getItemType().getId();
                            if (isRotating(id, 1)) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating items cannot be transferred");
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private Boolean isRotating(Long id, int inventoryType) throws Exception {
        if (inventoryType == 1) {
            ItemTypesContext record = ItemsApi.getItemTypes(id);
            return record.isRotating();
        } else{
            ToolTypesContext record = ToolsApi.getToolTypes(id);
            return record.isRotating();
        }
    }
}
