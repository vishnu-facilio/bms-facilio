package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.CurrencyUtil;
import org.apache.commons.chain.Context;

public class UpdateCurrencyStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        CurrencyContext currencyContext = (CurrencyContext) context.get(FacilioConstants.ContextNames.CURRENCY);

        long currencyId = currencyContext.getId();
        CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();

        if (baseCurrency != null && baseCurrency.getId() == currencyId) {
            throw new IllegalArgumentException("BaseCurrency cannot be edited");
        }
        int count = CurrencyUtil.updateCurrency(currencyContext);

        return false;
    }
}
