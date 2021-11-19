package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class ValidateInventoryRequestUpdateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestContext> inventoryRequestContexts = recordMap.get(moduleName);

        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_APPROVALS)) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(inventoryRequestContexts)) {
            for (V3InventoryRequestContext inventoryRequestContext : inventoryRequestContexts) {
                if (inventoryRequestContext != null && inventoryRequestContext.getId() > 0) {
                    Map<String, Object> bodyParams = Constants.getBodyParams(context);
                    if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("issue")) {
                        inventoryRequestContext.setIsIssued(true);
                    } else {
                        //inventory request should not updated after approval state.
                        V3InventoryRequestContext inv = (V3InventoryRequestContext) RecordAPI.getRecord(FacilioConstants.ContextNames.INVENTORY_REQUEST, inventoryRequestContext.getId());
                        if (inv.getApprovalFlowId() <= 0) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Inventory request cannot be updated");
                        } else {
                            context.put(FacilioConstants.ContextNames.SKIP_APPROVAL_CHECK, true);
                        }
                    }
                }
            }
        }

        return false;
    }
}
