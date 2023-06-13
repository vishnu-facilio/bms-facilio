package com.facilio.bmsconsoleV3.commands.vendorQuotes;

import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class SetVendorQuotesLineItemsOnListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("getWithLineItems")) {
            List<V3VendorQuotesContext> vendorQuotes = Constants.getRecordList((FacilioContext) context);
            for (V3VendorQuotesContext vendorQuote : vendorQuotes) {
                List<V3VendorQuotesLineItemsContext> lineItems = V3InventoryUtil.getVendorQuoteLineItems(vendorQuote);
                vendorQuote.setVendorQuotesLineItems(lineItems);
            }
        }
        return false;
    }




}
