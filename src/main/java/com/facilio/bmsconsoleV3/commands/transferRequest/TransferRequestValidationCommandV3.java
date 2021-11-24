package com.facilio.bmsconsoleV3.commands.transferRequest;

import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import nl.basjes.shaded.org.springframework.util.ObjectUtils;
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

                if (transferRequestContext != null && CollectionUtils.isNotEmpty(transferRequestContext.getSubForm().get("transferrequestlineitems"))) {
                    //Line Item Validation
                    List<Map<String, Object>> lineItems = transferRequestContext.getSubForm().get("transferrequestlineitems");

                    for(Map<String, Object> lineItem:lineItems){
                        V3TransferRequestLineItemContext lineItemContext= FieldUtil.getAsBeanFromMap(lineItem,V3TransferRequestLineItemContext.class);
                        if(lineItemContext.getInventoryType().equals(V3TransferRequestLineItemContext.InventoryType.TOOL.getIndex())){
                            if(ObjectUtils.isEmpty(lineItemContext.getQuantity()) || ObjectUtils.isEmpty(lineItemContext.getToolType())){
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Line items cannot be empty");
                            }
                        }
                        if(lineItemContext.getInventoryType().equals(V3TransferRequestLineItemContext.InventoryType.ITEM.getIndex())){
                            if(ObjectUtils.isEmpty(lineItemContext.getQuantity()) || ObjectUtils.isEmpty(lineItemContext.getItemType())){
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Line items cannot be empty");
                            }
                        }
                    }
                    // Storeroom Validation
                    if(transferRequestContext.getTransferToStore().getId()==transferRequestContext.getTransferFromStore().getId()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storerooms cannot be same");
                    }


                }


            }
        }
        return false;
    }
}
