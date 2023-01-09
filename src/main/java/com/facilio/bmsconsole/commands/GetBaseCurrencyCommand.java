package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.CurrencyUtil;
import org.apache.commons.chain.Context;

public class GetBaseCurrencyCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();
        if (baseCurrency != null) {
            baseCurrency.setBaseCurrency(true);
        }

        context.put(FacilioConstants.ContextNames.CURRENCY, baseCurrency);
        return false;
    }
}
