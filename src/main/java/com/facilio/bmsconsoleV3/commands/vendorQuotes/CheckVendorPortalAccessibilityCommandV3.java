package com.facilio.bmsconsoleV3.commands.vendorQuotes;

import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

public class CheckVendorPortalAccessibilityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3VendorQuotesContext vendorQuote = (V3VendorQuotesContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.VENDOR_QUOTES, id);

        if(vendorQuote!=null && vendorQuote.getVendor()!=null){
            Long vendorId = vendorQuote.getVendor().getId();
           Boolean vendorPortalAccess = V3InventoryUtil.hasVendorPortalAccess(vendorId);
            vendorQuote.setVendorPortalAccess(vendorPortalAccess);
        }

        return false;
    }
}
