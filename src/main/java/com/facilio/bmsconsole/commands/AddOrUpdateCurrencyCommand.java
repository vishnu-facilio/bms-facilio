package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import com.facilio.util.CurrencyUtil;
import java.util.Currency;

public class AddOrUpdateCurrencyCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        CurrencyContext currencyContext = (CurrencyContext) context.get(FacilioConstants.ContextNames.CURRENCY);

        if (currencyContext != null) {
            long currencyId = currencyContext.getId();
            if (currencyId == -1) {
                String displayName = Currency.getInstance(currencyContext.getCurrencyCode()).getDisplayName();
                currencyContext.setDisplayName(displayName);
                currencyContext.setStatus(true);

                currencyId = CurrencyUtil.addCurrency(currencyContext);
                currencyContext.setId(currencyId);
            } else {
                int linesModified = CurrencyUtil.updateCurrency(currencyContext);
            }
            CurrencyUtil.addCurrencyTrend(currencyContext);
        }

        context.put(FacilioConstants.ContextNames.CURRENCY, currencyContext);
        return false;
    }
}