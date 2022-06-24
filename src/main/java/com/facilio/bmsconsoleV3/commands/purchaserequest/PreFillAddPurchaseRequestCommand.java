package com.facilio.bmsconsoleV3.commands.purchaserequest;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class PreFillAddPurchaseRequestCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PurchaseRequestContext> purchaseRequestContexts = recordMap.get(moduleName);
        if(purchaseRequestContexts != null && CollectionUtils.isNotEmpty(purchaseRequestContexts)) {
            for(V3PurchaseRequestContext purchaseRequestContext : purchaseRequestContexts) 
            {
				QuotationAPI.lineItemsCostCalculations(purchaseRequestContext, purchaseRequestContext.getLineItems());

    			// setting current user to requestedBy
    			if(purchaseRequestContext.getRequestedBy() == null) {
    		 	  purchaseRequestContext.setRequestedBy(AccountUtil.getCurrentUser());
    			}
    			
    			if(purchaseRequestContext.getRequestedTime() == null) {
					purchaseRequestContext.setRequestedTime(System.currentTimeMillis());
				}

    			purchaseRequestContext.setShipToAddress(LocationAPI.getPoPrLocation(purchaseRequestContext.getStoreRoom(), purchaseRequestContext.getShipToAddress(), "SHIP_TO_Location", true, true));
                purchaseRequestContext.setBillToAddress(LocationAPI.getPoPrLocation(purchaseRequestContext.getVendor(), purchaseRequestContext.getBillToAddress(), "BILL_TO_Location", false, true));

            }
        }
		return false;	
	}

}
	