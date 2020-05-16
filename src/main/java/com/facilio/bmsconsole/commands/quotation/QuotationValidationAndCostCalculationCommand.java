package com.facilio.bmsconsole.commands.quotation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.quotation.QuotationContext;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class QuotationValidationAndCostCalculationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<QuotationContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (QuotationContext quotation : list) {
                QuotationAPI.calculateQuotationCost(quotation);
            }
        }

        return false;
    }
}
