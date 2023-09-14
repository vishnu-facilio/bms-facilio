package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class FetchQuotationSetting extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        QuotationSettingContext quotationSettingDATA = QuotationAPI.fetchQuotationSetting();
        if (quotationSettingDATA != null) {
            context.put(FacilioConstants.ContextNames.QUOTATIONSETTING, quotationSettingDATA);
            quotationSettingDATA.setShowMarkupValue(QuotationAPI.showMarkupValue(context, null));
            context.put(FacilioConstants.ContextNames.QUOTATIONSETTING, quotationSettingDATA);
        }

        return false;
    }
}
