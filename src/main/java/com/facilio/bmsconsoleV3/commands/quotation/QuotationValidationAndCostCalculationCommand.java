package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class QuotationValidationAndCostCalculationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<QuotationContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (QuotationContext quotation : list) {
                QuotationAPI.calculateQuotationCost(quotation);
                if(quotation.getRevisionNumber() == null){
                    quotation.setRevisionNumber(0L);
                }
                if (quotation.getBillToAddress() != null) {
                    LocationContext billToAddressLocation = quotation.getBillToAddress();
                    QuotationAPI.addLocation(quotation, billToAddressLocation);
                }
                if (quotation.getShipToAddress() != null) {
                    LocationContext shipToAddressLocation = quotation.getShipToAddress();
                    QuotationAPI.addLocation(quotation, shipToAddressLocation);
                }
                if (QuotationAPI.lookupValueIsNotEmpty(quotation.getTenant()) && quotation.getCustomerType() <= 0) {
                    // TEMP remove this once customer type is configured in form
                    quotation.setCustomerType(QuotationContext.CustomerType.TENANT.getIndex());
                }
                QuotationAPI.validateForWorkorder(quotation);
            }
        }
        
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);

        return false;
    }
}
