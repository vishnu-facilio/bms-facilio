package com.facilio.bmsconsoleV3.commands.quotation;


import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;


public class HandleQuoteSettingCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(QuotationFillDetailsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        QuotationSettingContext quotationSetting = QuotationAPI.fetchQuotationSetting();

        if(quotationSetting != null) {
            context.put(FacilioConstants.ContextNames.QUOTATIONSETTING, quotationSetting);

        }

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<QuotationContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (QuotationContext quotation : list) {
                if(quotationSetting != null) {
                    quotation.setQuotationSetting((quotationSetting));
                    quotation.setShowMarkupValue(QuotationAPI.showMarkupValue(context));
                    if(quotationSetting.getMarkupdisplayMode() == 1) {
                        if(quotationSetting.getGlobalMarkupValue() != null) {
                            quotation.setMarkup(quotationSetting.getGlobalMarkupValue());
                        }
                        quotation.setIsGlobalMarkup(true);
                    }
                }
            }
            }


        return false;
    }
}
