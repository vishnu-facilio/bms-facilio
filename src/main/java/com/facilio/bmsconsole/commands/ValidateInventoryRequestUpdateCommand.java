package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class ValidateInventoryRequestUpdateCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {

        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_APPROVALS)) {
            return false;
        }

        InventoryRequestContext inventoryRequestContext = (InventoryRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
        if(inventoryRequestContext != null && inventoryRequestContext.getId() > 0) {
            InventoryRequestContext inv = (InventoryRequestContext) RecordAPI.getRecord(FacilioConstants.ContextNames.INVENTORY_REQUEST, inventoryRequestContext.getId());
            //inventory request should not updated after approval state.
            if (inv.getApprovalFlowId() <= 0) {
                throw new IllegalArgumentException("The Inventory request cannot be updated");
            } else {
                context.put(FacilioConstants.ContextNames.SKIP_APPROVAL_CHECK, true);
            }
        }
        return false;
    }
}
