package com.facilio.bmsconsoleV3.commands.vendorQuotes;

import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class SetVendorQuotesLineItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3VendorQuotesContext vendorQuote = (V3VendorQuotesContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.VENDOR_QUOTES, id);
        if(!FacilioUtil.isEmptyOrNull(vendorQuote)) {
            //set pending line items
            List<V3VendorQuotesLineItemsContext> lineItems = V3InventoryUtil.getVendorQuoteLineItems(vendorQuote);
            vendorQuote.setVendorQuotesLineItems(lineItems);
        }
        return false;
    }




}
