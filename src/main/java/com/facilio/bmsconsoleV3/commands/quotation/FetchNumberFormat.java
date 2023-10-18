package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsoleV3.context.quotation.NumberFormatContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class FetchNumberFormat extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        NumberFormatContext numberFormatData = QuotationAPI.fetchNumberFormat();
        if (numberFormatData != null) {
            context.put(FacilioConstants.ContextNames.NUMBER_FORMAT, numberFormatData);
        }

        return false;
    }
}
